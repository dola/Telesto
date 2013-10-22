# -*- coding: utf-8 -*-

from telesto.parsing import *


protocol = Protocol()


# Add an id for tracking replies to every message type
Message.message_id = Int()


with protocol.version(0):
    class Ping(Message):
        method_id = 0x01

    class Complex(Message):
        method_id = 0x7f
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
