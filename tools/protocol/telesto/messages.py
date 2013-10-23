# -*- coding: utf-8 -*-

from telesto.parsing import *


protocol = Protocol()


# Add an id for tracking replies to every message type
Message.message_id = Int()


with protocol.version(0):
    # Global Messages
    class Ping(Message):
        method_id = 0x01

    class Pong(Message):
        method_id = 0x02

    class Success(Message):
        method_id = 0x03

    class Error(Message):
        method_id = 0x05
        error_code = Byte()
        details = String()

    # Handshake
    class RegisterClient(Message):
        method_id = 0x11
        mode = Byte()

    class RegisterClientResponse(Message):
        method_id = 0x12
        client_id = Int()

    class IdentifyClient(Message):
        method_id = 0x13
        client_id = Int()

    # Queues
    class CreateQueue(Message):
        method_id = 0x21
        name = String()

    class CreateQueueResponse(Message):
        method_id = 0x22
        queue_id = Int()

    class DeleteQueue(Message):
        method_id = 0x23
        queue_id = Int()

    class GetQueueId(Message):
        method_id = 0x25
        name = String()

    class GetQueueIdResponse(Message):
        method_id = 0x26
        queue_id = Int()

    class GetQueueName(Message):
        method_id = 0x27
        queue_id = Int()

    class GetQueueNameResponse(Message):
        method_id = 0x28
        name = String()

    class GetQueues(Message):
        method_id = 0x29

    class GetQueuesResponse(Message):
        method_id = 0x2a
        queues = List(Int())

    class GetActiveQueues(Message):
        method_id = 0x2b

    class GetActiveQueuesResponse(Message):
        method_id = 0x2c
        # TODO

    class GetMessages(Message):
        method_id = 0x2d

    class GetMessagesResponse(Message):
        method_id = 0x2e
        messages = List(TelestoMessage())

    # Messages
    class PutMessage(Message):
        method_id = 0x31
        message = TelestoMessage()

    class ReadMessage(Message):
        method_id = 0x33
        queue_id = Int()
        sender_id = Int()
        mode = Byte()

    class ReadMessageResponse(Message):
        method_id = 0x34
        message = TelestoMessage()

    # Test
    class ComplexTest(Message):
        method_id = 0x71
        byte_field = Byte()
        boolean_field = Bool()
        short_field = Short()
        int_field = Int()
        long_field = Long()
        float_field = Float()
        double_field = Double()
        string_field = String()


if __name__ == "__main__":
    print protocol
