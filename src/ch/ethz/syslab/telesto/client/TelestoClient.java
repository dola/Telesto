package ch.ethz.syslab.telesto.client;

import java.io.IOException;
import java.util.List;

import ch.ethz.syslab.telesto.client.exception.ProcessingException;
import ch.ethz.syslab.telesto.client.network.ClientConnection;
import ch.ethz.syslab.telesto.common.model.Client;
import ch.ethz.syslab.telesto.common.model.ClientMode;
import ch.ethz.syslab.telesto.common.model.Message;
import ch.ethz.syslab.telesto.common.model.Queue;
import ch.ethz.syslab.telesto.common.model.ReadMode;
import ch.ethz.syslab.telesto.common.protocol.CreateQueuePacket;
import ch.ethz.syslab.telesto.common.protocol.DeleteQueuePacket;
import ch.ethz.syslab.telesto.common.protocol.GetActiveQueuesPacket;
import ch.ethz.syslab.telesto.common.protocol.GetMessagesPacket;
import ch.ethz.syslab.telesto.common.protocol.GetQueueIdPacket;
import ch.ethz.syslab.telesto.common.protocol.GetQueueNamePacket;
import ch.ethz.syslab.telesto.common.protocol.GetQueuesPacket;
import ch.ethz.syslab.telesto.common.protocol.IdentifyClientPacket;
import ch.ethz.syslab.telesto.common.protocol.Packet;
import ch.ethz.syslab.telesto.common.protocol.PingPacket;
import ch.ethz.syslab.telesto.common.protocol.PutMessagePacket;
import ch.ethz.syslab.telesto.common.protocol.ReadMessagePacket;
import ch.ethz.syslab.telesto.common.protocol.RegisterClientPacket;

public class TelestoClient {
    ClientConnection connection;

    public TelestoClient() throws IOException {
        connection = new ClientConnection();
    }

    /**
     * Bounce an empty packet off the middleware to measure latency.
     * 
     * @return the elapsed time in nanoseconds
     * 
     * @throws ProcessingException
     */
    public long ping() throws ProcessingException {
        long start = System.nanoTime();
        connection.sendPacket(new PingPacket());
        return System.nanoTime() - start;
    }

    /**
     * Registers the client with the specified {@link ClientMode} and name and returns the complete {@link Client}
     * instance
     * 
     * @param name
     *            the name of the new client
     * @param mode
     *            the client mode to use
     * @return the {@link Client} object representing the new client
     * @throws ProcessingException
     */
    public Client connect(String name, ClientMode mode) throws ProcessingException {
        Packet p = new RegisterClientPacket(name, mode.getByteValue());
        // send
        return null;
    }

    /**
     * Connect using the given client id. The returned Client instance contains the {@link ClientMode} of the identified
     * client which has to be considered for future API calls in order to avoid raising errors.
     * 
     * @param queueId
     *            client id to identify the client
     * @return the {@link Client} object representing the new client
     * @throws ProcessingException
     */
    public Client connect(int clientId) throws ProcessingException {
        Packet p = new IdentifyClientPacket(clientId);

        return null;
    }

    /**
     * creates a new queue with the given name. The name must be unique.
     * 
     * @param queueName
     *            a unique name for the queue to be generated
     * @return the representing {@link Queue} instance
     * @throws ProcessingException
     */
    public Queue createQueue(String queueName) throws ProcessingException {
        Packet p = new CreateQueuePacket(queueName);
        return null;
    }

    /**
     * remove a queue including all its messages from the system
     * 
     * @param queueId
     *            the id of the queue to delete
     * @throws ProcessingException
     */
    public void deleteQueue(int queueId) throws ProcessingException {
        Packet p = new DeleteQueuePacket(queueId);

    }

    /**
     * retrieve the {@link Queue} by its name
     * 
     * @param queueName
     *            the name of the queue
     * @return a full {@link Queue} object representing the requested queue
     * @throws ProcessingException
     */
    public Queue getQueueByName(String queueName) throws ProcessingException {
        Packet p = new GetQueueIdPacket(queueName);

        return null;
    }

    /**
     * retrieve the {@link Queue} by its id
     * 
     * @param queueId
     *            the id of the queue
     * @return a full {@link Queue} object representing the requested queue
     * @throws ProcessingException
     */
    public Queue getQueueById(int queueId) throws ProcessingException {
        Packet p = new GetQueueNamePacket(queueId);
        return null;
    }

    /**
     * retrieve a list of all queues in the system
     * 
     * @return list of all queues in the system
     * @throws ProcessingException
     */
    public List<Queue> getQueues() throws ProcessingException {
        Packet p = new GetQueuesPacket();
        return null;
    }

    /**
     * Retrieve all queues where messages for this client are waiting
     * 
     * @return a list of queues that contain messages for the client
     * @throws ProcessingException
     */
    public List<Queue> getActiveQueues() throws ProcessingException {
        Packet p = new GetActiveQueuesPacket();
        return null;
    }

    /**
     * Query for all messages in a queue without removing any of them.
     * 
     * Note that there is no guarantee that a message is still in a queue upon subsequent retrieving operations.
     * 
     * @param queueId
     *            the queue to read from
     * @return a list of all messages for the client in the specified queue
     * @throws ProcessingException
     */
    public List<Message> readMessages(int queueId) throws ProcessingException {
        Packet p = new GetMessagesPacket(queueId);
        return null;
    }

    /**
     * Put a single message into the queue specified in the given {@link Message} instance.
     * 
     * This method considers the following fields of the Message instance:
     * <ul>
     * <li>{@link Message#queueId}
     * <li>{@link Message#receiverId}
     * <li>{@link Message#priority}
     * <li>{@link Message#message}
     * </ul>
     * 
     * @param message
     *            the message to insert
     * @throws ProcessingException
     * @see {@link #putMessage(Message, int[])}
     * @see {@link #sendRequestResponseMessage(Message)}
     */
    public void putMessage(Message message) throws ProcessingException {
        Packet p = new PutMessagePacket(message);

    }

    /**
     * Put a message into multiple queues. When this method is used, the queue specified in {@link Message#queueId} will
     * be ignored.
     * 
     * This method considers the following fields of the Message instance:
     * <ul>
     * <li>{@link Message#receiverId}
     * <li>{@link Message#priority}
     * <li>{@link Message#message}
     * </ul>
     * 
     * @param message
     *            the message to be inserted
     * @param queueId
     *            an array of queue ids specifying the queues to insert to
     * @throws ProcessingException
     * @see {@link #putMessage(Message)}
     * @see {@link #sendRequestResponseMessage(Message)}
     */
    public void putMessage(Message message, int[] queueIds) throws ProcessingException {
        // TODO: multiqueue support
    }

    /**
     * send a Message that waits for a response. This method is blocking until the response arrives.
     * 
     * @param message
     *            the message to be sent
     * @return The response Message
     * @throws ProcessingException
     * @see {@link #putMessage(Message)}
     * @see {@link #putMessage(Message, int[])}
     */
    public Message sendRequestResponseMessage(Message message) throws ProcessingException {
        return null;
    }

    /**
     * retrieve (and remove) a single Message from the specified queue by priority
     * 
     * @param queueId
     *            the queue to read from
     * @return the {@link Message} retrieved
     * @throws ProcessingException
     * @see {@link #retrieveMessage(int, int)}
     * @see {@link #retrieveMessage(int, ReadMode)}
     * @see {@link #retrieveMessage(int, int, ReadMode)}
     */
    public Message retrieveMessage(int queueId) throws ProcessingException {
        // Packet p = new ReadMessagePacket();
        return null;
    }

    /**
     * retrieve (and remove) a single Message from the specified queue using the given {@link ReadMode}
     * 
     * @param queueId
     *            the queue to read from
     * @param mode
     *            the {@link ReadMode} to be used
     * @return the {@link Message} retrieved
     * @throws ProcessingException
     * @see {@link #retrieveMessage(int)}
     * @see {@link #retrieveMessage(int, int)}
     * @see {@link #retrieveMessage(int, int, ReadMode)}
     */
    public Message retrieveMessage(int queueId, ReadMode mode) throws ProcessingException {
        // Packet p = new ReadMessagePacket();
        return null;
    }

    /**
     * retrieve (and remove) a single Message from the specified queue that was sent by the specified sender
     * 
     * @param queueId
     *            the queue to read from
     * @param sender
     *            the sender of the message
     * @return the {@link Message} retrieved
     * @throws ProcessingException
     * @see {@link #retrieveMessage(int)}
     * @see {@link #retrieveMessage(int, ReadMode)}
     * @see {@link #retrieveMessage(int, int, ReadMode)}
     */
    public Message retrieveMessage(int queueId, int sender) throws ProcessingException {
        // Packet p = new ReadMessagePacket(queueId);
        return null;
    }

    /**
     * retrieve (and remove) a single Message from the specified queue that was sent by the specified sender using the
     * given {@link ReadMode}
     * 
     * @param queueId
     *            the queue to read from
     * @param sender
     *            the sender of the message
     * @param mode
     *            the {@link ReadMode} to be used
     * @return the {@link Message} retrieved
     * @throws ProcessingException
     * @see {@link #retrieveMessage(int)}
     * @see {@link #retrieveMessage(int, int)}
     * @see {@link #retrieveMessage(int, ReadMode)}
     */
    public Message retrieveMessage(int queueId, int sender, ReadMode mode) throws ProcessingException {
        Packet p = new ReadMessagePacket(queueId, sender, mode.getByteValue());
        return null;
    }

}
