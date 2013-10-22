package ch.ethz.syslab.telesto.server.network;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.concurrent.ArrayBlockingQueue;

import ch.ethz.syslab.telesto.protocol.Packet;
import ch.ethz.syslab.telesto.protocol.Packet.UnknownMethodException;
import ch.ethz.syslab.telesto.server.model.Client;
import ch.ethz.syslab.telesto.util.Log;

public class DataHandler extends Thread {

    static private Log LOGGER = new Log(DataHandler.class);

    private ArrayBlockingQueue<Client> clientQueue;
    private int id;

    public DataHandler(ArrayBlockingQueue<Client> clientQueue, int id) {
        this.clientQueue = clientQueue;
        this.id = id;
    }

    @Override
    public void run() {
        while (true) {
            Client client = nextClient();
            Packet packet = parseNextPacket(client);
            client.release();
            if (packet != null) {
                LOGGER.info("Received packet %s (%d)", packet, id);
                rewindBuffer(client);
                if (dataRemaining(client)) {
                    clientQueue.add(client);
                }
                // TODO: Do something with received packet
            }
        }
    }

    private boolean dataRemaining(Client client) {
        return client.writeBuffer.position() != client.readBuffer.position();
    }

    private Client nextClient() {
        Client client = null;
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

    private Packet parseNextPacket(Client client) {
        ByteBuffer readBuffer = client.readBuffer;
        int position = readBuffer.mark().position();
        int bytesAvailable = client.writeBuffer.position() - position;

        if (bytesAvailable < 7) {
            LOGGER.fine("Received incomplete packet (%d bytes)", bytesAvailable);
            return null;
        }

        client.readBuffer.limit(position + 2);
        int packetSize = readBuffer.getShort();

        if (bytesAvailable < packetSize + 2) {
            LOGGER.fine("Received incomplete packet (%d bytes)", bytesAvailable);
            readBuffer.reset();
            return null;
        }

        LOGGER.fine("Received packet (%d bytes)", packetSize);
        client.readBuffer.limit(position + packetSize + 2);

        Packet packet;
        try {
            packet = Packet.create(readBuffer);
        } catch (UnknownMethodException e) {
            LOGGER.warning(e.getMessage());
            client.disconnect();
            return null;
        } catch (BufferUnderflowException e) {
            LOGGER.warning("Not enough data in buffer to read packet. Size must be wrong.");
            client.disconnect();
            return null;
        }

        if (readBuffer.position() - position - 2 != packetSize) {
            LOGGER.warning("Packet length (%d) did not match header (%d).", readBuffer.position() - position - 2, packetSize);
            client.disconnect();
            return null;
        }

        return packet;
    }

    private void rewindBuffer(Client client) {
        if (client.writeBuffer.position() * 4 > Client.BUFFER_SIZE * 3 && client.readBuffer.position() * 4 > Client.BUFFER_SIZE) {
            LOGGER.fine("Rewinding buffer for %s", client);
            synchronized (client.writeBuffer) {
                client.readBuffer.limit(client.writeBuffer.position());
                client.readBuffer.compact();
                client.writeBuffer.position(client.readBuffer.position());
                client.readBuffer.flip();
            }
        }
    }
}
