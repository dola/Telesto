# -*- coding: utf-8 -*-

from telesto.parsing import *


protocol = Protocol()


# Add an id for tracking replies to every message type
Message.message_id = Int()


with protocol.version(0):
    # Global Messages
    class Ping(ClientMessage):
        IS_LOGIN_MESSAGE = True
        method_id = 0x01

    class Pong(ServerMessage):
        method_id = 0x02

    class Success(ServerMessage):
        method_id = 0x03

    class Error(ServerMessage):
        method_id = 0x05
        error_type = TelestoErrorType()
        details = String()

    # Handshake
    class RegisterClient(ClientMessage):
        IS_LOGIN_MESSAGE = True
        IS_REGULAR_MESSAGE = False
        method_id = 0x11
        client_name = String()
        mode = Byte()

    class RegisterClientResponse(ServerMessage):
        method_id = 0x12
        client_id = Int()

    class IdentifyClient(ClientMessage):
        IS_LOGIN_MESSAGE = True
        IS_REGULAR_MESSAGE = False
        method_id = 0x13
        client_id = Int()

    class IdentifyClientResponse(ServerMessage):
        method_id = 0x14
        mode = Byte()
        name = String()

    class DeleteClient(ClientMessage):
        method_id = 0x15

    # Queues
    class CreateQueue(ClientMessage):
        method_id = 0x21
        name = String()

    class CreateQueueResponse(ServerMessage):
        method_id = 0x22
        queue_id = Int()

    class DeleteQueue(ClientMessage):
        method_id = 0x23
        queue_id = Int()

    class GetQueueId(ClientMessage):
        method_id = 0x25
        name = String()

    class GetQueueIdResponse(ServerMessage):
        method_id = 0x26
        queue_id = Int()

    class GetQueueName(ClientMessage):
        method_id = 0x27
        queue_id = Int()

    class GetQueueNameResponse(ServerMessage):
        method_id = 0x28
        name = String()

    class GetQueues(ClientMessage):
        method_id = 0x29

    class GetQueuesResponse(ServerMessage):
        method_id = 0x2a
        queues = List(TelestoQueue())

    class GetActiveQueues(ClientMessage):
        method_id = 0x2b

    class GetActiveQueuesResponse(ServerMessage):
        method_id = 0x2c
        queues = List(TelestoQueue())

    class GetMessages(ClientMessage):
        method_id = 0x2d
        queue_id = Int()

    class GetMessagesResponse(ServerMessage):
        method_id = 0x2e
        messages = List(TelestoMessage())

    # Messages
    class PutMessage(ClientMessage):
        method_id = 0x31
        message = TelestoMessage()
        additional_queue_ids = List(Int())

    class ReadMessage(ClientMessage):
        method_id = 0x33
        queue_id = Int()
        sender_id = Int()
        mode = Byte()

    class ReadMessageResponse(ServerMessage):
        method_id = 0x34
        message = TelestoMessage()

    class ReadResponse(ClientMessage):
        method_id = 0x35
        queue_id = Int()
        context = Int()

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

    class MessageTest(Message):
        method_id = 0x72
        message = TelestoMessage()

    class QueueTest(Message):
        method_id = 0x73
        queue = TelestoQueue()


if __name__ == "__main__":
    print protocol
