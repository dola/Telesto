# -*- coding: utf-8 -*-

import re
import struct
import inspect
import collections

from telesto import logger


class Protocol(dict):
    """Collection of multiple protocol versions"""
    def version(self, version):
        return ProtocolVersion(version, self)

    def __getitem__(self, version):
        assert isinstance(version, int)
        while version not in self and version > 0:
            version -= 1
        return super(Protocol, self).__getitem__(version)

    def _register_version(self, protocol_version):
        self[protocol_version.version] = protocol_version

    def __str__(self):
        return "\n\n".join(str(version) for version in self.itervalues())


class ProtocolVersion(list):
    def __init__(self, version, protocol=None):
        self.version = version
        super(ProtocolVersion, self).__init__([None] * 256)
        self.protocol = protocol

    def parse_message(self, stream, side):
        method_id = Byte.parse(stream)
        logger.debug("%s trying to parse message type %x" % (side, method_id))
        message = self[method_id]
        if message is None:
            raise self.UnsupportedPacketException(method_id)
        if not message._accept_from(side):
            raise self.WrongDirectionException(message, side)
        return message(stream, side)

    def __enter__(self):
        pass

    def __exit__(self, *args):
        """Captures all defined messages"""
        potential_messages = inspect.currentframe().f_back.f_locals
        for message in potential_messages.itervalues():
            if (inspect.isclass(message) and
                    issubclass(message, Message) and
                    message not in (Message, ServerMessage, ClientMessage)):
                message._do_magic()
                self[message.method_id] = message
        if self.protocol is not None:
            self.protocol._register_version(self)

    def __str__(self):
        return "\n".join((
            "Protocol version %s" % self.version,
            "-------------------",
            "\n\n".join(msg._str() for msg in self if msg)
        ))

    class UnsupportedPacketException(Exception):
        def __init__(self, method_id):
            super(ProtocolVersion.UnsupportedPacketException, self).__init__(
                "Unsupported packet id 0x%x" % method_id
            )
            self.method_id = method_id

    class WrongDirectionException(Exception):
        def __init__(self, message, side):
            correct_side = "server" if side == "client" else "client"
            super(ProtocolVersion.WrongDirectionException, self).__init__(
                "Received %s-only packet 0x%02x %s from %s" %
                (correct_side, message.method_id, message._name, side)
            )
            self.message = message
            self.side = side


class Message(object):
    _NAME_PATTERN = re.compile("(.)([A-Z])")
    method_id = None

    def __init__(self, stream=None, side=None, **kwargs):
        self._side = side
        if stream is not None and kwargs:
            raise TypeError("Unexpected argument combination")
        for name, field in self._fields.iteritems():
            if stream is not None:
                setattr(self, name, field.parse(stream, self))
            else:
                setattr(self, name, kwargs.get(name))
        if stream is not None:
            self._raw_bytes = stream.packet_finished()

    def emit(self):
        for name, field in self._fields.iteritems():
            field.prepare(getattr(self, name), self)
        return (struct.pack(">B", self.packet_id) +
                "".join(field.emit(getattr(self, name), self)
                        for name, field in self._fields.iteritems()))

    def __str__(self):
        if self._fields:
            fields = "\n".join(
                "  %s (%s): %s" %
                (name, field, field.format(getattr(self, name)))
                for name, field in self._fields.iteritems()
            )
        else:
            fields = "  -- empty --"
        if self._side == "client":
            direction = "Client -> Server "
        elif self._side == "server":
            direction = "Server -> Client "
        else:
            direction = ""
        return "\n".join((
            "%s0x%02x %s" % (direction, self.packet_id, self._name),
            fields
        ))

    @classmethod
    def _accept_from(cls, side):
        return True

    @classmethod
    def contains_type(cls, field_type):
        for field in cls._fields.itervalues():
            if field.contains_type(field_type):
                return True
        return False

    @classmethod
    def _do_magic(cls):
        cls._name = cls._NAME_PATTERN.sub(
            lambda g: "%s %s" % (g.group(1), g.group(2)), cls.__name__
        )
        cls._fields = collections.OrderedDict(packet_id=Int())
        cls._fields.update(sorted(
            ((name, field) for name, field in cls.__dict__.iteritems()
             if isinstance(field, MessageField)),
            key=lambda i: i[1]._order_id
        ))
        for name, field in cls._fields.iteritems():
            field.name = name

    @classmethod
    def _str(cls):
        if len(cls._fields) >= 2:
            fields = "\n".join(
                "  %s (%s)" % (name, field)
                for name, field in cls._fields.iteritems()
                if name != "packet_id"
            )
        else:
            fields = "  -- empty --"
        return "\n".join((
            "0x%02x %s" % (cls.method_id, cls._name),
            fields
        ))


class ClientMessage(Message):
    """Message sent from client to server"""
    @classmethod
    def _accept_from(cls, side):
        return side == "client"


class ServerMessage(Message):
    """Message sent from server to client"""
    @classmethod
    def _accept_from(cls, side):
        return side == "server"


class MessageField(object):
    _NEXT_ID = 1
    java_type = "Object";

    def __init__(self):
        self._order_id = MessageField._NEXT_ID
        MessageField._NEXT_ID += 1

    @property
    def java_name(self):
        return re.sub("_(\w)", lambda m: m.group(1).upper(), self.name)

    @classmethod
    def parse(cls, stream, message):
        return None

    @classmethod
    def prepare(cls, value, message):
        """Used to set stray length fields"""
        pass

    @classmethod
    def emit(self, value, message):
        return ""

    def format(self, value):
        return str(value)

    @classmethod
    def _parse_subfield(cls, field, stream, message):
        if isinstance(field, MessageField):
            return field.parse(stream, message)
        elif isinstance(field, basestring):
            return getattr(message, field)
        elif isinstance(field, dict):
            return collections.OrderedDict(
                (key, cls._parse_subfield(subfield, stream, message))
                for key, subfield in field.iteritems()
            )
        else:
            raise NotImplementedError

    @classmethod
    def _emit_subfield(cls, field, value, message):
        if isinstance(field, MessageField):
            return field.emit(value, message)
        elif isinstance(field, basestring):
            return ""
        elif isinstance(field, dict):
            return "".join(
                cls._emit_subfield(subfield, value[name], message)
                for name, subfield in field.iteritems()
            )
        else:
            raise NotImplementedError

    @classmethod
    def _set_subfield(cls, field, value, message):
        if isinstance(field, basestring):
            setattr(message, field, value)

    def contains_type(self, field_type):
        return self.__class__.__name__ == field_type

    def __str__(self):
        return self.__class__.__name__


def simple_type_field(name, format):
    format = ">" + format
    length = struct.calcsize(format)

    class SimpleType(MessageField):
        java_type = name.lower()

        @classmethod
        def parse(cls, stream, message=None):
            return struct.unpack(format, stream.read(length))[0]

        @classmethod
        def emit(cls, value, message=None):
            return struct.pack(format, value)

    SimpleType.__name__ = name
    return SimpleType


class List(MessageField):
    def __init__(self, field):
        self._size = Int()
        self._field = field
        super(List, self).__init__()

    @property
    def name(self):
        return self._name

    @name.setter
    def name(self, name):
        self._name = name
        self._field.name = name

    @property
    def java_type(self):
        return "{}[]".format(self._field.java_type)

    def parse(self, stream, message=None):
        return [
            self._parse_subfield(self._field, stream, message)
            for i in range(self._parse_subfield(self._size, stream, message))
        ]

    def emit(self, value, message=None):
        return (self._emit_subfield(self._size, len(value), message) +
                "".join(self._emit_subfield(self._field, entry, message)
                        for entry in value))

    def contains_type(self, field_type):
        return (super(List, self).contains_type(field_type) or
                self._field.contains_type(field_type))


Byte = simple_type_field("Byte", "b")
Short = simple_type_field("Short", "h")
Int = simple_type_field("Int", "i")
Float = simple_type_field("Float", "f")
Double = simple_type_field("Double", "d")
Long = simple_type_field("Long", "q")


class Bool(Byte):
    java_type = "boolean"
    @classmethod
    def parse(cls, stream, message):
        return super(Bool, cls).parse(stream) == 1


class String(MessageField):
    java_type = "String"
    @classmethod
    def parse(cls, stream, message=None):
        raise NotImplementedError

    @classmethod
    def emit(cls, value, message=None):
        raise NotImplementedError

    def format(self, value):
        return value.encode("utf8")


class TelestoMessage(MessageField):
    java_type = "Message"
    @classmethod
    def parse(cls, stream, message=None):
        raise NotImplementedError

    @classmethod
    def emit(cls, value, message=None):
        raise NotImplementedError

    def format(self, value):
        raise NotImplementedError


class TelestoQueue(MessageField):
    java_type = "Queue"
    @classmethod
    def parse(cls, stream, message=None):
        raise NotImplementedError

    @classmethod
    def emit(cls, value, message=None):
        raise NotImplementedError

    def format(self, value):
        raise NotImplementedError


class TelestoErrorType(MessageField):
    java_type = "ErrorType"
    @classmethod
    def parse(cls, stream, message=None):
        raise NotImplementedError

    @classmethod
    def emit(cls, value, message=None):
        raise NotImplementedError

    def format(self, value):
        raise NotImplementedError
