package ch.ethz.syslab.telesto.common.model;

import ch.ethz.syslab.telesto.server.db.result.DatabaseResultEntry;

public class Client implements DatabaseResultEntry {

    public int id;
    public String name;
    public byte operationMode;

    public Client() {

    }

    public Client(int clientId, String clientName, byte operationMode) {
        id = clientId;
        name = clientName;
        this.operationMode = operationMode;
    }
}
