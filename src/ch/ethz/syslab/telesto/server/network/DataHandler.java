package ch.ethz.syslab.telesto.server.network;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;

import ch.ethz.syslab.telesto.common.network.Connection;
import ch.ethz.syslab.telesto.common.protocol.ErrorPacket;
import ch.ethz.syslab.telesto.common.protocol.Packet;
import ch.ethz.syslab.telesto.common.protocol.handler.PacketProcessingException;
import ch.ethz.syslab.telesto.common.util.ErrorType;
import ch.ethz.syslab.telesto.common.util.Log;

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
            Packet packet = connection.readPacket();
            if (packet != null) {
                LOGGER.info("Received packet %s (%d)", packet, id);
                if (connection.dataRemaining()) {
                    clientQueue.add(connection);
                }
                Packet response;
                try {
                    response = connection.handle(packet);
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
                response.packetId = packet.packetId;
                try {
                    connection.send(response);
                } catch (IOException e) {
                    LOGGER.warning(e, "Failed to send %s to %s", response, connection);
                    connection.disconnect();
                }
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
        }
        return client;
    }
}
