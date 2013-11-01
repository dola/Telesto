package ch.ethz.syslab.telesto.server.model;

import java.nio.ByteBuffer;

import ch.ethz.syslab.telesto.server.controller.IPacketHandler;

public class Client {
    public static final int BUFFER_SIZE = 32768;
    public ByteBuffer writeBuffer;
    public ByteBuffer readBuffer;
    public IPacketHandler packetHandler;
    private boolean gettingHandled = false;
    private boolean active = true;
    public int id;

    public Client() {
        writeBuffer = ByteBuffer.allocateDirect(BUFFER_SIZE);
        readBuffer = writeBuffer.duplicate();
    }

    public synchronized boolean acquire() {
        if (gettingHandled || !active) {
            return false;
        } else {
            gettingHandled = true;
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
        return String.format("Client(%d)", id);
    }
}
