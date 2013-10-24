package ch.ethz.syslab.telesto.server.db.result;

import java.sql.Timestamp;

public class MessageRow implements DatabaseResultEntry {

    private int messageId;
    private int queueId;
    private int senderId;
    private int receiverId;
    private int context;
    private byte priority;
    private Timestamp timeOfArrival;
    private String message;

    public MessageRow(int messageId, int queueId, int senderId, int receiverId, int context, byte priority, Timestamp timeOfArrival, String message) {
        super();
        this.messageId = messageId;
        this.queueId = queueId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.context = context;
        this.priority = priority;
        this.timeOfArrival = timeOfArrival;
        this.message = message;
    }

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public int getQueueId() {
        return queueId;
    }

    public void setQueueId(int queueId) {
        this.queueId = queueId;
    }

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public int getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(int receiverId) {
        this.receiverId = receiverId;
    }

    public int getContext() {
        return context;
    }

    public void setContext(int context) {
        this.context = context;
    }

    public byte getPriority() {
        return priority;
    }

    public void setPriority(byte priority) {
        this.priority = priority;
    }

    public Timestamp getTimeOfArrival() {
        return timeOfArrival;
    }

    public void setTimeOfArrival(Timestamp timeOfArrival) {
        this.timeOfArrival = timeOfArrival;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
