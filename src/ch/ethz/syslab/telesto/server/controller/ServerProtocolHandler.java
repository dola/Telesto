package ch.ethz.syslab.telesto.server.controller;

import java.util.Arrays;
import java.util.List;

import ch.ethz.syslab.telesto.common.model.Client;
import ch.ethz.syslab.telesto.common.model.ClientMode;
import ch.ethz.syslab.telesto.common.model.Message;
import ch.ethz.syslab.telesto.common.model.Queue;
import ch.ethz.syslab.telesto.common.model.ReadMode;
import ch.ethz.syslab.telesto.common.protocol.CreateQueuePacket;
import ch.ethz.syslab.telesto.common.protocol.CreateQueueResponsePacket;
import ch.ethz.syslab.telesto.common.protocol.DeleteClientPacket;
import ch.ethz.syslab.telesto.common.protocol.DeleteQueuePacket;
import ch.ethz.syslab.telesto.common.protocol.ErrorPacket;
import ch.ethz.syslab.telesto.common.protocol.GetActiveQueuesPacket;
import ch.ethz.syslab.telesto.common.protocol.GetActiveQueuesResponsePacket;
import ch.ethz.syslab.telesto.common.protocol.GetMessagesPacket;
import ch.ethz.syslab.telesto.common.protocol.GetMessagesResponsePacket;
import ch.ethz.syslab.telesto.common.protocol.GetQueueIdPacket;
import ch.ethz.syslab.telesto.common.protocol.GetQueueIdResponsePacket;
import ch.ethz.syslab.telesto.common.protocol.GetQueueNamePacket;
import ch.ethz.syslab.telesto.common.protocol.GetQueueNameResponsePacket;
import ch.ethz.syslab.telesto.common.protocol.GetQueuesPacket;
import ch.ethz.syslab.telesto.common.protocol.GetQueuesResponsePacket;
import ch.ethz.syslab.telesto.common.protocol.Packet;
import ch.ethz.syslab.telesto.common.protocol.PingPacket;
import ch.ethz.syslab.telesto.common.protocol.PongPacket;
import ch.ethz.syslab.telesto.common.protocol.PutMessagePacket;
import ch.ethz.syslab.telesto.common.protocol.ReadMessagePacket;
import ch.ethz.syslab.telesto.common.protocol.ReadMessageResponsePacket;
import ch.ethz.syslab.telesto.common.protocol.ReadResponsePacket;
import ch.ethz.syslab.telesto.common.protocol.SuccessPacket;
import ch.ethz.syslab.telesto.common.protocol.handler.IServerProtocolHandler;
import ch.ethz.syslab.telesto.common.protocol.handler.PacketProcessingException;
import ch.ethz.syslab.telesto.common.protocol.handler.ProtocolHandler;
import ch.ethz.syslab.telesto.common.util.ErrorType;
import ch.ethz.syslab.telesto.common.util.NumberUtil;
import ch.ethz.syslab.telesto.server.db.Database;
import ch.ethz.syslab.telesto.server.db.procedure.ClientProcedure;
import ch.ethz.syslab.telesto.server.db.procedure.MessageProcedure;
import ch.ethz.syslab.telesto.server.db.procedure.QueueProcedure;

public class ServerProtocolHandler extends ProtocolHandler implements IServerProtocolHandler {

    private Database db;
    private Client client;

    public ServerProtocolHandler(Database database, Client client) {
        db = database;
        this.client = client;
    }

    @Override
    public Packet handle(PingPacket packet) throws PacketProcessingException {
        return new PongPacket();
    }

    @Override
    public Packet handle(DeleteClientPacket packet) throws PacketProcessingException {
        int clientId = db.callSimpleProcedure(ClientProcedure.DELETE_CLIENT, client.id);
        if (clientId != 0) {
            return new SuccessPacket();
        }

        return new ErrorPacket(ErrorType.CLIENT_NOT_EXISTING, "Could not delete non-existent Client");
    }

    @Override
    public Packet handle(CreateQueuePacket packet) throws PacketProcessingException {
        List<Queue> queues = db.callQueueProcedure(QueueProcedure.CREATE_QUEUE, packet.name);
        if (queues.size() == 1) {
            return new CreateQueueResponsePacket(queues.get(0).id);
        }

        return new ErrorPacket(ErrorType.INTERNAL_ERROR, "Failed to create new Queue");
    }

    @Override
    public Packet handle(DeleteQueuePacket packet) throws PacketProcessingException {
        int queueId = db.callSimpleProcedure(QueueProcedure.DELETE_QUEUE, packet.queueId);
        if (queueId != 0) {
            return new SuccessPacket();
        }

        return new ErrorPacket(ErrorType.QUEUE_NOT_EXISTING, "Failed to delete non-existing Queue");
    }

    @Override
    public Packet handle(GetQueueIdPacket packet) throws PacketProcessingException {
        List<Queue> queues = db.callQueueProcedure(QueueProcedure.GET_QUEUE_ID, packet.name);
        if (queues.size() == 1) {
            return new GetQueueIdResponsePacket(queues.get(0).id);
        }

        return new ErrorPacket(ErrorType.QUEUE_NOT_EXISTING, "Failed to get id of non-existing Queue");
    }

    @Override
    public Packet handle(GetQueueNamePacket packet) throws PacketProcessingException {
        List<Queue> queues = db.callQueueProcedure(QueueProcedure.GET_QUEUE_NAME, packet.queueId);
        if (queues.size() == 1) {
            return new GetQueueNameResponsePacket(queues.get(0).name);
        }

        return new ErrorPacket(ErrorType.QUEUE_NOT_EXISTING, "Failed to get name of non-existing Queue");
    }

    @Override
    public Packet handle(GetQueuesPacket packet) throws PacketProcessingException {
        List<Queue> queues = db.callQueueProcedure(QueueProcedure.LIST_QUEUES);
        if (!queues.isEmpty()) {
            return new GetQueuesResponsePacket(queues.toArray(new Queue[queues.size()]));
        }

        return new ErrorPacket(ErrorType.NO_QUEUES_EXISTING, "No Queues are currently existing");
    }

    @Override
    public Packet handle(GetActiveQueuesPacket packet) throws PacketProcessingException {
        List<Queue> queues = db.callQueueProcedure(QueueProcedure.GET_ACTIVE_QUEUES, client.id);

        if (!queues.isEmpty()) {
            return new GetActiveQueuesResponsePacket(queues.toArray(new Queue[queues.size()]));
        }

        return new ErrorPacket(ErrorType.NO_ACTIVE_QUEUES_EXISTING, "No Queues with messages for Client are currently existing");
    }

    @Override
    public Packet handle(GetMessagesPacket packet) throws PacketProcessingException {
        List<Message> messages = db.callMessageProcedure(MessageProcedure.GET_MESSAGES_FROM_QUEUE);

        if (messages.size() > 0) {
            return new GetMessagesResponsePacket(messages.toArray(new Message[messages.size()]));
        }

        return new ErrorPacket(ErrorType.NO_MESSAGES_IN_QUEUE, "No Messages were found in the specified Queue");
    }

    @Override
    public Packet handle(PutMessagePacket packet) throws PacketProcessingException {
        requireClientMode(ClientMode.FULL); // not accessible for READ_ONLY clients

        Message msg = packet.message;
        Integer receiverId = NumberUtil.set0ToNull(msg.receiverId);
        Integer context = NumberUtil.set0ToNull(msg.context);

        if (packet.additionalQueueIds == null || packet.additionalQueueIds.length == 0) {
            // insert into single queue

            int queueId = db.callSimpleProcedure(MessageProcedure.PUT_MESSAGE, msg.queueId, client.id, receiverId, context, msg.priority,
                    msg.message);
            if (queueId != msg.queueId) {
                return new SuccessPacket();
            }
            return new ErrorPacket(ErrorType.QUEUE_NOT_EXISTING, "Message was not inserted because Queue does not exist");

        } else {
            // insert into multiple queues

            int[] queueIds = new int[packet.additionalQueueIds.length + 1];
            queueIds[0] = msg.queueId;
            System.arraycopy(packet.additionalQueueIds, 0, queueIds, 1, packet.additionalQueueIds.length);

            List<Integer> insertedQueueIds = db.callIntegerListProcedure(MessageProcedure.PUT_MESSAGES, queueIds, client.id, receiverId, context,
                    msg.priority, msg.message);

            if (insertedQueueIds.size() == queueIds.length) {
                return new SuccessPacket();
            } else {
                return new ErrorPacket(ErrorType.QUEUE_NOT_EXISTING, "Message was not inserted because some Queues do not exist. Tried: "
                        + Arrays.toString(queueIds) + "; Inserted: " + insertedQueueIds.toString());
            }
        }

    }

    @Override
    public Packet handle(ReadMessagePacket packet) throws PacketProcessingException {

        List<Message> messages;

        Integer queueId = NumberUtil.set0ToNull(packet.queueId);
        Integer senderId = NumberUtil.set0ToNull(packet.senderId);
        Integer receiverId = client.id;

        switch (ReadMode.fromByteValue(packet.mode)) {
            case TIME:
                messages = db.callMessageProcedure(MessageProcedure.READ_MESSAGE_BY_TIMESTAMP, queueId, senderId, receiverId);
                break;
            case PRIORITY:
            default:
                messages = db.callMessageProcedure(MessageProcedure.READ_MESSAGE_BY_PRIORITY, queueId, senderId, receiverId);
                break;
        }

        if (!messages.isEmpty()) {
            return new ReadMessageResponsePacket(messages.get(0));
        }

        return new ErrorPacket(ErrorType.NO_MESSAGES_RETRIEVED, "No corresponding message to the query found");
    }

    @Override
    public Packet handle(ReadResponsePacket packet) throws PacketProcessingException {

        List<Message> messages;

        int queueId = packet.queueId;
        int receiverId = client.id;
        int context = packet.context;

        if (queueId == 0 || context == 0) {
            throw new PacketProcessingException(ErrorType.REQUIRED_PARAMETER_MISSING, "QueueId and Context are required parameters to read responses");
        }

        messages = db.callMessageProcedure(MessageProcedure.READ_RESPONSE_MESSAGE, queueId, receiverId, context);

        if (!messages.isEmpty()) {
            return new ReadMessageResponsePacket(messages.get(0));
        }

        return new ErrorPacket(ErrorType.NO_MESSAGES_RETRIEVED, "No corresponding message to the query found");
    }

    protected void requireClientMode(ClientMode... modes) throws PacketProcessingException {
        if (!Arrays.asList(modes).contains(client.mode)) {
            throw new PacketProcessingException(ErrorType.CLIENT_MODE_PERMISSION_VIOLATION, "To use this method the client mode must be one of "
                    + Arrays.toString(modes));
        }
    }

    protected void requiredIntFields(int... values) throws PacketProcessingException {
        for (int v : values) {
            if (v == 0) {
                throw new PacketProcessingException(ErrorType.REQUIRED_PARAMETER_MISSING, "A required parameter is missing");
            }
        }
    }
}
