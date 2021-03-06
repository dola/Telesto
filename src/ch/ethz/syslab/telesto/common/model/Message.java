package ch.ethz.syslab.telesto.common.model;

import java.sql.Timestamp;

import ch.ethz.syslab.telesto.server.db.result.DatabaseResultEntry;

public class Message implements DatabaseResultEntry {

    public int id;
    public int queueId;
    public int senderId;
    public int receiverId;
    public int context;
    public byte priority;
    public Timestamp timeOfArrival;
    public String message;

    public Message() {

    }

    public Message(int queueId, byte priority, String message) {
        this(0, queueId, 0, 0, 0, priority, null, message);
    }

    public Message(int queueId, int receiverId, byte priority, String message) {
        this(0, queueId, 0, receiverId, 0, priority, null, message);
    }

    /**
     * Create a response message that takes care of setting the same queueId, context and priority and setting the right
     * receiver.
     * 
     * @param request
     *            Message to create a response for
     * @param message
     *            content of the new message
     */
    public Message(Message request, String message) {
        this(0, request.queueId, 0, request.senderId, request.context, request.priority, null, message);
    }

    public Message(int id, int queueId, int senderId, int receiverId, int context, byte priority, Timestamp timeOfArrival, String message) {
        this.id = id;
        this.queueId = queueId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.context = context;
        this.priority = priority;
        this.timeOfArrival = timeOfArrival;
        this.message = message;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Message) {
            return ((Message) other).id == id;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return id;
    }
}
