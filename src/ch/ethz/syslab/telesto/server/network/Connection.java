package ch.ethz.syslab.telesto.server.network;

import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import ch.ethz.syslab.telesto.model.Client;
import ch.ethz.syslab.telesto.protocol.Packet;
import ch.ethz.syslab.telesto.protocol.Packet.UnknownMethodException;
import ch.ethz.syslab.telesto.protocol.handler.PacketProcessingException;
import ch.ethz.syslab.telesto.protocol.handler.ProtocolHandler;
import ch.ethz.syslab.telesto.server.config.CONFIG;
import ch.ethz.syslab.telesto.server.controller.ServerAuthenticationProtocolHandler;
import ch.ethz.syslab.telesto.util.Log;

public class Connection {
    private static Log LOGGER = new Log(Connection.class);

    private DoubleBuffer receivingBuffer = new DoubleBuffer(CONFIG.MW_READ_BUFFER_SIZE);;
    private DoubleBuffer sendingBuffer = new DoubleBuffer(CONFIG.MW_READ_BUFFER_SIZE);;
    private ProtocolHandler packetHandler = new ServerAuthenticationProtocolHandler(null);
    private boolean connected = true;
    private SocketChannel socket;
    public SelectionKey selectionKey;
    public Client client;

    public Connection(SocketChannel socket) {
        this.socket = socket;
    }

    public Packet readPacket() {
        if (!connected) {
            LOGGER.warning("Trying to read packet from disconnected client: %s", this);
            return null;
        }

        if (!receivingBuffer.acquire()) {
            LOGGER.info("Client is being handled by another thread: %s", this);
            return null;
        }

        receivingBuffer.prepare();

        int bytesAvailable = receivingBuffer.bytesAvailable();
        if (bytesAvailable < 7) {
            LOGGER.fine("Received incomplete packet data (%d bytes)", bytesAvailable);
            receivingBuffer.release();
            return null;
        }

        receivingBuffer.limit(2);
        int packetSize = receivingBuffer.readView.getShort();

        if (bytesAvailable < packetSize + 2) {
            LOGGER.fine("Received incomplete packet data (%d bytes)", bytesAvailable);
            receivingBuffer.readView.reset();
            receivingBuffer.release();
            return null;
        }

        LOGGER.fine("Received packet data (%d bytes)", packetSize);
        receivingBuffer.limit(packetSize + 2);

        Packet packet;
        try {
            packet = Packet.create(receivingBuffer.readView);
        } catch (UnknownMethodException e) {
            LOGGER.warning(e.getMessage());
            disconnect();
            return null;
        } catch (BufferUnderflowException e) {
            LOGGER.warning("Not enough data in buffer to read packet. Size must be wrong.");
            disconnect();
            return null;
        }

        if (receivingBuffer.bytesRead() - 2 != packetSize) {
            LOGGER.warning("Packet length (%d) did not match header (%d).", receivingBuffer.bytesRead() - 2, packetSize);
            disconnect();
            return null;
        }

        receivingBuffer.cleanup();
        receivingBuffer.release();

        return packet;
    }

    public boolean dataRemaining() {
        return receivingBuffer.dataRemaining();
    }

    public void disconnect() {
        connected = false;
        selectionKey.cancel();
        try {
            socket.close();
        } catch (IOException e) {
            LOGGER.warning(e, "Failed to close socket for %s", this);
        }
    }

    public Packet handle(Packet packet) throws PacketProcessingException {
        return packet.getHandled(packetHandler);
    }

    public void send(Packet response) throws IOException {
        response.emit(sendingBuffer.writeView);
        socket.write(sendingBuffer.readView);
    }

    public int readFromChannel() throws IOException {
        synchronized (receivingBuffer) {
            return socket.read(receivingBuffer.writeView);
        }
    }

    @Override
    public String toString() {
        String id;
        if (client == null) {
            try {
                id = socket.getRemoteAddress().toString();
            } catch (IOException e) {
                id = "disconnected";
            }
        } else {
            id = Integer.toString(client.id);
        }
        return String.format("Connection(%s)", id);
    }
}
