package ch.ethz.syslab.telesto.common.model;

import ch.ethz.syslab.telesto.server.db.result.DatabaseResultEntry;

public class Client implements DatabaseResultEntry {

    public int id;
    public String name;
    public ClientMode mode;

    public Client() {

    }

    public Client(int id, String name, ClientMode mode) {
        this.id = id;
        this.name = name;
        this.mode = mode;
    }
}
