package ch.ethz.syslab.telesto.common.model;

import ch.ethz.syslab.telesto.server.db.result.DatabaseResultEntry;

public class Queue implements DatabaseResultEntry {

    public int id;
    public String name;

    public Queue(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Queue) {
            return ((Queue) other).id == id;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return id;
    }
}
