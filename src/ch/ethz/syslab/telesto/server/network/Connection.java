package ch.ethz.syslab.telesto.server.network;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import ch.ethz.syslab.telesto.model.Client;
import ch.ethz.syslab.telesto.server.config.CONFIG;
import ch.ethz.syslab.telesto.server.controller.IPacketHandler;
import ch.ethz.syslab.telesto.server.controller.PacketHandler;
import ch.ethz.syslab.telesto.util.Log;

public class Connection {
    private static Log LOGGER = new Log(Connection.class);

    public DoubleBuffer doubleReadBuffer = new DoubleBuffer(CONFIG.MW_READ_BUFFER_SIZE);;
    public DoubleBuffer doubleWriteBuffer = new DoubleBuffer(CONFIG.MW_READ_BUFFER_SIZE);;
    public IPacketHandler packetHandler = new PacketHandler(null);
    private boolean gettingHandled = false;
    private boolean active = true;
    public Client client;
    public SocketChannel socket;
    public SelectionKey selectionKey;

    public Connection(SocketChannel socket) {
        this.socket = socket;
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
        selectionKey.cancel();
        try {
            socket.close();
        } catch (IOException e) {
            LOGGER.warning(e, "Failed to close socket for %s", this);
        }
    }

    @Override
    public String toString() {
        return String.format("Connectionn(%d)", client.id);
    }
}
