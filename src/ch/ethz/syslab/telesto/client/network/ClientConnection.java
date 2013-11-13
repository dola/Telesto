package ch.ethz.syslab.telesto.client.network;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.concurrent.atomic.AtomicInteger;

import ch.ethz.syslab.telesto.client.exception.ProcessingException;
import ch.ethz.syslab.telesto.client.profile.ClientStopwatch;
import ch.ethz.syslab.telesto.common.config.CONFIG;
import ch.ethz.syslab.telesto.common.network.Connection;
import ch.ethz.syslab.telesto.common.protocol.ErrorPacket;
import ch.ethz.syslab.telesto.common.protocol.Packet;
import ch.ethz.syslab.telesto.common.util.ErrorType;
import ch.ethz.syslab.telesto.common.util.Log;
import ch.ethz.syslab.telesto.profile.BenchmarkLog;
import ch.ethz.syslab.telesto.profile.Stopwatch.Phase;

public class ClientConnection extends Connection {
    private static Log LOGGER = new Log(ClientConnection.class);

    private ClientStopwatch stopwatch;
    private AtomicInteger packetId = new AtomicInteger();

    public ClientConnection(BenchmarkLog log) throws IOException {
        super(SocketChannel.open(new InetSocketAddress(CONFIG.CLI_MW_HOST, CONFIG.MW_PORT)));
        socket.configureBlocking(true);
        stopwatch = new ClientStopwatch(log);
    }

    private Packet receivePacket() throws ProcessingException {
        Packet packet = null;
        while (packet == null && connected) {
            try {
                readFromChannel();
            } catch (IOException e) {
                throw new ProcessingException(ErrorType.IO_ERROR, e);
            }
            packet = readPacket();
        }
        stopwatch.enterPhase(Phase.WAITING);
        if (packet == null) {
            LOGGER.info("Disconnected");
        } else {
            LOGGER.fine("Received packet %s", packet);
        }

        if (packet instanceof ErrorPacket) {
            throw new ProcessingException(((ErrorPacket) packet).errorType, ((ErrorPacket) packet).details);
        }

        return packet;
    }

    public Packet sendPacket(Packet packet) throws ProcessingException {
        packet.packetId = packetId.incrementAndGet();
        stopwatch.setLastPacket(packet);
        stopwatch.enterPhase(Phase.DATABASE);
        try {
            send(packet);
        } catch (IOException e) {
            throw new ProcessingException(ErrorType.IO_ERROR, e);
        }
        return receivePacket();
    }
}
