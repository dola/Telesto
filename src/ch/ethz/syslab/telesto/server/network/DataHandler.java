package ch.ethz.syslab.telesto.server.network;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;

import ch.ethz.syslab.telesto.common.network.Connection;
import ch.ethz.syslab.telesto.common.protocol.ErrorPacket;
import ch.ethz.syslab.telesto.common.protocol.Packet;
import ch.ethz.syslab.telesto.common.protocol.handler.PacketProcessingException;
import ch.ethz.syslab.telesto.common.util.ErrorType;
import ch.ethz.syslab.telesto.common.util.Log;
import ch.ethz.syslab.telesto.profile.BenchmarkLog;
import ch.ethz.syslab.telesto.profile.Stopwatch;
import ch.ethz.syslab.telesto.profile.Stopwatch.Phase;

public class DataHandler extends Thread {

    static private Log LOGGER = new Log(DataHandler.class);

    private ArrayBlockingQueue<Connection> clientQueue;
    private int id;
    private boolean running = true;
    private Stopwatch stopwatch;

    public DataHandler(ArrayBlockingQueue<Connection> clientQueue, int id, BenchmarkLog log) {
        this.clientQueue = clientQueue;
        this.id = id;
        stopwatch = new Stopwatch(log);
    }

    @Override
    public void run() {
        while (running) {
            stopwatch.enterPhase(Phase.WAITING);
            Connection connection = nextClient();
            stopwatch.enterPhase(Phase.PARSING);
            Packet packet = connection.readPacket();
            if (packet != null) {
                LOGGER.info("Received packet %s (%d)", packet, id);
                if (connection.dataRemaining()) {
                    clientQueue.add(connection);
                }
                Packet response;
                stopwatch.enterPhase(Phase.DATABASE);
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
                stopwatch.enterPhase(Phase.RESPONSE);
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

    public void shutdown() {
        running = false;
    }
}
