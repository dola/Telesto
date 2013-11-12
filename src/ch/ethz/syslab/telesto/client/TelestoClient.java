package ch.ethz.syslab.telesto.client;

import java.util.List;

import ch.ethz.syslab.telesto.client.exception.ProcessingException;
import ch.ethz.syslab.telesto.model.Client;
import ch.ethz.syslab.telesto.model.ClientMode;
import ch.ethz.syslab.telesto.model.Message;
import ch.ethz.syslab.telesto.model.Queue;
import ch.ethz.syslab.telesto.model.ReadMode;
import ch.ethz.syslab.telesto.protocol.CreateQueuePacket;
import ch.ethz.syslab.telesto.protocol.DeleteQueuePacket;
import ch.ethz.syslab.telesto.protocol.GetActiveQueuesPacket;
import ch.ethz.syslab.telesto.protocol.GetMessagesPacket;
import ch.ethz.syslab.telesto.protocol.GetQueueIdPacket;
import ch.ethz.syslab.telesto.protocol.GetQueueNamePacket;
import ch.ethz.syslab.telesto.protocol.GetQueuesPacket;
import ch.ethz.syslab.telesto.protocol.IdentifyClientPacket;
import ch.ethz.syslab.telesto.protocol.Packet;
import ch.ethz.syslab.telesto.protocol.PutMessagePacket;
import ch.ethz.syslab.telesto.protocol.ReadMessagePacket;
import ch.ethz.syslab.telesto.protocol.RegisterClientPacket;

public class TelestoClient implements ITelestoClient {

    public TelestoClient() {
    }

    @Override
    public Client connect(String name, ClientMode mode) throws ProcessingException {
        Packet p = new RegisterClientPacket(name, mode.getByteValue());
        // send
        return null;
    }

    @Override
    public Client connect(int clientId) throws ProcessingException {
        Packet p = new IdentifyClientPacket(clientId);

        return null;
    }

    @Override
    public Queue createQueue(String queueName) throws ProcessingException {
        Packet p = new CreateQueuePacket(queueName);
        return null;
    }

    @Override
    public void deleteQueue(int queueId) throws ProcessingException {
        Packet p = new DeleteQueuePacket(queueId);

    }

    @Override
    public Queue getQueueByName(String queueName) throws ProcessingException {
        Packet p = new GetQueueIdPacket(queueName);

        return null;
    }

    @Override
    public Queue getQueueById(int queueId) throws ProcessingException {
        Packet p = new GetQueueNamePacket(queueId);
        return null;
    }

    @Override
    public List<Queue> getQueues() throws ProcessingException {
        Packet p = new GetQueuesPacket();
        return null;
    }

    @Override
    public List<Queue> getActiveQueues() throws ProcessingException {
        Packet p = new GetActiveQueuesPacket();
        return null;
    }

    @Override
    public List<Message> readMessages(int queueId) throws ProcessingException {
        Packet p = new GetMessagesPacket(queueId);
        return null;
    }

    @Override
    public void putMessage(Message message) throws ProcessingException {
        Packet p = new PutMessagePacket(message);

    }

    @Override
    public void putMessage(Message message, int[] queueIds) throws ProcessingException {
        // TODO: multiqueue support
    }

    @Override
    public Message sendRequestResponseMessage(Message message) throws ProcessingException {
        return null;
    }

    @Override
    public Message retrieveMessage(int queueId) throws ProcessingException {
        // Packet p = new ReadMessagePacket();
        return null;
    }

    @Override
    public Message retrieveMessage(int queueId, ReadMode mode) throws ProcessingException {
        // Packet p = new ReadMessagePacket();
        return null;
    }

    @Override
    public Message retrieveMessage(int queueId, int sender) throws ProcessingException {
        // Packet p = new ReadMessagePacket(queueId);
        return null;
    }

    @Override
    public Message retrieveMessage(int queueId, int sender, ReadMode mode) throws ProcessingException {
        Packet p = new ReadMessagePacket(queueId, sender, mode.getByteValue());
        return null;
    }

}
