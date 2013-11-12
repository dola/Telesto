package ch.ethz.syslab.telesto.client;

import java.util.List;

import ch.ethz.syslab.telesto.client.exception.ProcessingException;
import ch.ethz.syslab.telesto.model.Message;
import ch.ethz.syslab.telesto.model.Queue;
import ch.ethz.syslab.telesto.model.ReadMode;

public interface ITelestoClient {
    /**
     * first registers the client to get new id
     * 
     * @return the new unique client id
     * @throws ProcessingException
     */
    int connect() throws ProcessingException;

    /**
     * Connect using the given client id
     * 
     * @param id
     *            client id to identify the client
     * @throws ProcessingException
     */
    void connect(int id) throws ProcessingException;

    /**
     * creates a new queue with the given name. The name must be unique.
     * 
     * @param name
     *            a unique name for the queue to be generated
     * @return the representing {@link Queue} instance
     * @throws ProcessingException
     */
    Queue createQueue(String name) throws ProcessingException;

    /**
     * remove a queue including all its messages from the system
     * 
     * @param id
     *            the id of the queue to delete
     * @throws ProcessingException
     */
    void deleteQueue(int id) throws ProcessingException;

    /**
     * retrieve the {@link Queue} by its name
     * 
     * @param name
     *            the name of the queue
     * @return a full {@link Queue} object representing the requested queue
     * @throws ProcessingException
     */
    Queue getQueueByName(String name) throws ProcessingException;

    /**
     * retrieve the {@link Queue} by its id
     * 
     * @param id
     *            the id of the queue
     * @return a full {@link Queue} object representing the requested queue
     * @throws ProcessingException
     */
    Queue getQueueById(int id) throws ProcessingException;

    /**
     * retrieve a list of all queues in the system
     * 
     * @return list of all queues in the system
     * @throws ProcessingException
     */
    List<Queue> getQueues() throws ProcessingException;

    /**
     * Retrieve all queues where messages for this client are waiting
     * 
     * @return a list of queues that contain messages for the client
     * @throws ProcessingException
     */
    List<Queue> getActiveQueues() throws ProcessingException;

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
    List<Message> readMessages(int queueId) throws ProcessingException;

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
    void putMessage(Message message) throws ProcessingException;

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
    void putMessage(Message message, int[] queueId) throws ProcessingException;

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
    Message sendRequestResponseMessage(Message message) throws ProcessingException;

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
    Message retrieveMessage(int queueId) throws ProcessingException;

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
    Message retrieveMessage(int queueId, ReadMode mode) throws ProcessingException;

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
    Message retrieveMessage(int queueId, int sender) throws ProcessingException;

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
    Message retrieveMessage(int queueId, int sender, ReadMode mode) throws ProcessingException;

}
