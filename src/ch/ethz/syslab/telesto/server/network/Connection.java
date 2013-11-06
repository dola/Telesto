package ch.ethz.syslab.telesto.server.network;

import ch.ethz.syslab.telesto.model.Client;
import ch.ethz.syslab.telesto.server.config.CONFIG;
import ch.ethz.syslab.telesto.server.controller.IPacketHandler;
import ch.ethz.syslab.telesto.server.controller.PacketHandler;

public class Connection {
    public DoubleBuffer doubleReadBuffer = new DoubleBuffer(CONFIG.MW_READ_BUFFER_SIZE);;
    public DoubleBuffer doubleWriteBuffer = new DoubleBuffer(CONFIG.MW_READ_BUFFER_SIZE);;
    public IPacketHandler packetHandler = new PacketHandler(null);
    private boolean gettingHandled = false;
    private boolean active = true;
    public Client client;

    public Connection() {
    }

    public void cleanup() {
        doubleReadBuffer.cleanup();
        doubleWriteBuffer.cleanup();
    }

    public synchronized boolean acquire() {
        if (gettingHandled || !active) {
            return false;
        } else {
            gettingHandled = true;
            doubleReadBuffer.prepare();
            doubleWriteBuffer.prepare();
            return true;
        }
    }

    public void release() {
        gettingHandled = false;
    }

    public void disconnect() {
        active = false;
    }

    @Override
    public String toString() {
        return String.format("Connectionn(%d)", client.id);
    }
}
