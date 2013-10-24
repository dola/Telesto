package ch.ethz.syslab.telesto.server.db.result;

public class QueueRow implements DatabaseResultEntry {

    private int queueId;
    private String queueName;

    public QueueRow(int queueId, String queueName) {
        super();
        this.queueId = queueId;
        this.queueName = queueName;
    }

    public int getQueueId() {
        return queueId;
    }

    public void setQueueId(int queueId) {
        this.queueId = queueId;
    }

    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

}
