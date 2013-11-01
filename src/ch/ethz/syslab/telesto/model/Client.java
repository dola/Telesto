package ch.ethz.syslab.telesto.model;

import ch.ethz.syslab.telesto.server.db.result.DatabaseResultEntry;

public class Client implements DatabaseResultEntry {

    public int clientId;
    public String clientName;
    public byte operationMode;

    public Client() {

    }

    public Client(int clientId, String clientName, byte operationMode) {
        this.clientId = clientId;
        this.clientName = clientName;
        this.operationMode = operationMode;
    }
}
