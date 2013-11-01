package ch.ethz.syslab.telesto.model;

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
}
