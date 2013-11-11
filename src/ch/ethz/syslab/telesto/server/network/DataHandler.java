package ch.ethz.syslab.telesto.server.network;

import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.util.concurrent.ArrayBlockingQueue;

import ch.ethz.syslab.telesto.protocol.ErrorPacket;
import ch.ethz.syslab.telesto.protocol.Packet;
import ch.ethz.syslab.telesto.protocol.Packet.UnknownMethodException;
import ch.ethz.syslab.telesto.server.controller.PacketProcessingException;
import ch.ethz.syslab.telesto.util.ErrorType;
import ch.ethz.syslab.telesto.util.Log;

public class DataHandler extends Thread {

    static private Log LOGGER = new Log(DataHandler.class);

    private ArrayBlockingQueue<Connection> clientQueue;
    private int id;

    public DataHandler(ArrayBlockingQueue<Connection> clientQueue, int id) {
        this.clientQueue = clientQueue;
        this.id = id;
    }

    @Override
    public void run() {
        while (true) {
            Connection connection = nextClient();
            Packet packet = parseNextPacket(connection);
            connection.release(); // TODO: we probably want to acquire/release the double buffer instead of the
                                  // connection
            if (packet != null) {
                LOGGER.info("Received packet %s (%d)", packet, id);
                connection.doubleReadBuffer.cleanup();
                if (connection.doubleReadBuffer.dataRemaining()) {
                    clientQueue.add(connection);
                }
                Packet response;
                try {
                    response = connection.packetHandler.handle(packet);
                } catch (PacketProcessingException e) {
                    LOGGER.info("Error while processing packet '%s': %s", packet, e.getMessage());
                    response = new ErrorPacket(e.type, e.getMessage());
                } catch (Exception e) {
                    LOGGER.info("Unexpected error while processing packet '%s': %s", packet, e.getMessage());
                    response = new ErrorPacket(ErrorType.INTERNAL_ERROR, e.getMessage());
                }
                if (response == null) {
                    LOGGER.info("Packet handler for '%s' returned null", packet);
                    response = new ErrorPacket(ErrorType.INTERNAL_ERROR, "No response from packet handler");
                }
                try {
                    send(connection, response);
                } catch (IOException e) {
                    LOGGER.warning(e, "Failed to send %s to %s", response, connection);
                    connection.disconnect();
                }
                connection.cleanup();
            }
        }
    }

    private Connection nextClient() {
        Connection client = null;
        while (client == null) {
            try {
                client = clientQueue.take();
            } catch (InterruptedException e) {
                LOGGER.info("Interrupt in message handler loop");
                continue;
            }
            if (!client.acquire()) {
                LOGGER.info("Client %s is already beeing handled by another worker. (%d)", client, id);
                client = null;
            }
        }
        return client;
    }

    private Packet parseNextPacket(Connection client) {
        DoubleBuffer buffer = client.doubleReadBuffer;
        buffer.prepare();

        int bytesAvailable = buffer.bytesAvailable();
        if (bytesAvailable < 7) {
            LOGGER.fine("Received incomplete packet data (%d bytes)", bytesAvailable);
            return null;
        }

        client.doubleReadBuffer.limit(2);
        int packetSize = buffer.readBuffer.getShort();

        if (bytesAvailable < packetSize + 2) {
            LOGGER.fine("Received incomplete packet data (%d bytes)", bytesAvailable);
            buffer.readBuffer.reset();
            return null;
        }

        LOGGER.fine("Received packet data (%d bytes)", packetSize);
        client.doubleReadBuffer.limit(packetSize + 2);

        Packet packet;
        try {
            packet = Packet.create(buffer.readBuffer);
        } catch (UnknownMethodException e) {
            LOGGER.warning(e.getMessage());
            client.disconnect();
            return null;
        } catch (BufferUnderflowException e) {
            LOGGER.warning("Not enough data in buffer to read packet. Size must be wrong.");
            client.disconnect();
            return null;
        }

        if (buffer.bytesRead() - 2 != packetSize) {
            LOGGER.warning("Packet length (%d) did not match header (%d).", buffer.bytesRead() - 2, packetSize);
            client.disconnect();
            return null;
        }

        return packet;
    }

    private void send(Connection connection, Packet response) throws IOException {
        response.emit(connection.doubleWriteBuffer.writeBuffer);
        connection.socket.write(connection.doubleWriteBuffer.readBuffer);
    }
}
