package ch.ethz.syslab.telesto.server.db.result;

public class ClientRow implements DatabaseResultEntry {

    private int clientId;
    private String clientName;
    private byte operationMode;

    public ClientRow(int clientId, String clientName, byte operationMode) {
        super();
        this.clientId = clientId;
        this.clientName = clientName;
        this.operationMode = operationMode;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public byte getOperationMode() {
        return operationMode;
    }

    public void setOperationMode(byte operationMode) {
        this.operationMode = operationMode;
    }
}
