package ch.ethz.syslab.telesto.client;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import ch.ethz.syslab.telesto.client.exception.ProcessingException;
import ch.ethz.syslab.telesto.client.network.ClientConnection;
import ch.ethz.syslab.telesto.common.config.CONFIG;
import ch.ethz.syslab.telesto.common.model.Client;
import ch.ethz.syslab.telesto.common.model.ClientMode;
import ch.ethz.syslab.telesto.common.model.Message;
import ch.ethz.syslab.telesto.common.model.Queue;
import ch.ethz.syslab.telesto.common.model.ReadMode;
import ch.ethz.syslab.telesto.common.protocol.CreateQueuePacket;
import ch.ethz.syslab.telesto.common.protocol.CreateQueueResponsePacket;
import ch.ethz.syslab.telesto.common.protocol.DeleteQueuePacket;
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
import ch.ethz.syslab.telesto.common.protocol.IdentifyClientPacket;
import ch.ethz.syslab.telesto.common.protocol.IdentifyClientResponsePacket;
import ch.ethz.syslab.telesto.common.protocol.Packet;
import ch.ethz.syslab.telesto.common.protocol.PingPacket;
import ch.ethz.syslab.telesto.common.protocol.PutMessagePacket;
import ch.ethz.syslab.telesto.common.protocol.ReadMessagePacket;
import ch.ethz.syslab.telesto.common.protocol.ReadMessageResponsePacket;
import ch.ethz.syslab.telesto.common.protocol.RegisterClientPacket;
import ch.ethz.syslab.telesto.common.protocol.RegisterClientResponsePacket;
import ch.ethz.syslab.telesto.common.util.ErrorType;

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
        Packet packet = new RegisterClientPacket(name, mode.getByteValue());
        RegisterClientResponsePacket response = (RegisterClientResponsePacket) connection.sendPacket(packet);
        return new Client(response.clientId, name, mode);
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
        Packet packet = new IdentifyClientPacket(clientId);
        IdentifyClientResponsePacket response = (IdentifyClientResponsePacket) connection.sendPacket(packet);
        return new Client(clientId, response.name, ClientMode.fromByteValue(response.mode));
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
        Packet packet = new CreateQueuePacket(queueName);
        CreateQueueResponsePacket response = (CreateQueueResponsePacket) connection.sendPacket(packet);
        return new Queue(response.queueId, queueName);
    }

    /**
     * remove a queue including all its messages from the system
     * 
     * @param queueId
     *            the id of the queue to delete
     * @throws ProcessingException
     */
    public void deleteQueue(int queueId) throws ProcessingException {
        Packet packet = new DeleteQueuePacket(queueId);
        connection.sendPacket(packet);
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
        Packet packet = new GetQueueIdPacket(queueName);
        GetQueueIdResponsePacket response = (GetQueueIdResponsePacket) connection.sendPacket(packet);
        return new Queue(response.queueId, queueName);
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
        Packet packet = new GetQueueNamePacket(queueId);
        GetQueueNameResponsePacket response = (GetQueueNameResponsePacket) connection.sendPacket(packet);
        return new Queue(queueId, response.name);
    }

    /**
     * retrieve a list of all queues in the system
     * 
     * @return list of all queues in the system
     * @throws ProcessingException
     */
    public List<Queue> getQueues() throws ProcessingException {
        Packet packet = new GetQueuesPacket();
        GetQueuesResponsePacket response = (GetQueuesResponsePacket) connection.sendPacket(packet);
        return Arrays.asList(response.queues);
    }

    /**
     * Retrieve all queues where messages for this client are waiting
     * 
     * @return a list of queues that contain messages for the client
     * @throws ProcessingException
     */
    public List<Queue> getActiveQueues() throws ProcessingException {
        Packet packet = new GetActiveQueuesPacket();
        GetActiveQueuesResponsePacket response = (GetActiveQueuesResponsePacket) connection.sendPacket(packet);
        return Arrays.asList(response.queues);
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
        Packet packet = new GetMessagesPacket(queueId);
        GetMessagesResponsePacket response = (GetMessagesResponsePacket) connection.sendPacket(packet);
        return Arrays.asList(response.messages);
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
        Packet packet = new PutMessagePacket(message, new int[0]);
        connection.sendPacket(packet);
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
        int[] additionalQueues = new int[queueIds.length - 1];
        System.arraycopy(queueIds, 1, additionalQueues, 0, queueIds.length - 1);
        Packet packet = new PutMessagePacket(message, additionalQueues);
        connection.sendPacket(packet);
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
        Packet packet = new PutMessagePacket(message, new int[0]);
        return retryUntilMessageAvailable(packet);
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
        Packet packet = new ReadMessagePacket(queueId, 0, ReadMode.TIME.getByteValue());
        ReadMessageResponsePacket response = (ReadMessageResponsePacket) connection.sendPacket(packet);
        return response.message;
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
        Packet packet = new ReadMessagePacket(queueId, 0, mode.getByteValue());
        ReadMessageResponsePacket response = (ReadMessageResponsePacket) connection.sendPacket(packet);
        return response.message;
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
        Packet packet = new ReadMessagePacket(queueId, sender, ReadMode.TIME.getByteValue());
        ReadMessageResponsePacket response = (ReadMessageResponsePacket) connection.sendPacket(packet);
        return response.message;
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
        Packet packet = new ReadMessagePacket(queueId, sender, mode.getByteValue());
        ReadMessageResponsePacket response = (ReadMessageResponsePacket) connection.sendPacket(packet);
        return response.message;
    }

    private Message retryUntilMessageAvailable(Packet packet) throws ProcessingException {
        ReadMessageResponsePacket response;
        while (true) {
            try {
                response = (ReadMessageResponsePacket) connection.sendPacket(packet);
                break;
            } catch (ProcessingException e) {
                if (e.type == ErrorType.NO_MESSAGES_RETRIEVED) {
                    try {
                        Thread.sleep(CONFIG.CLI_RETRY_DELAY);
                    } catch (InterruptedException e1) {
                        // Ignore
                    }
                    continue;
                }
                throw e;
            }
        }
        return response.message;
    }

}
