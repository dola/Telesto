package ch.ethz.syslab.telesto.client.network;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.concurrent.atomic.AtomicInteger;

import ch.ethz.syslab.telesto.client.exception.ProcessingException;
import ch.ethz.syslab.telesto.common.config.CONFIG;
import ch.ethz.syslab.telesto.common.network.Connection;
import ch.ethz.syslab.telesto.common.protocol.Packet;
import ch.ethz.syslab.telesto.common.util.ErrorType;
import ch.ethz.syslab.telesto.common.util.Log;

public class ClientConnection extends Connection {
    private static Log LOGGER = new Log(ClientConnection.class);

    private AtomicInteger packetId = new AtomicInteger();

    public ClientConnection() throws IOException {
        super(SocketChannel.open(new InetSocketAddress(CONFIG.CLI_MW_HOST, CONFIG.MW_PORT)));
        socket.configureBlocking(true);
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
        if (packet == null) {
            LOGGER.info("Disconnected");
        } else {
            LOGGER.fine("Received packet %s", packet);
        }
        return packet;
    }

    public Packet sendPacket(Packet packet) throws ProcessingException {
        packet.packetId = packetId.incrementAndGet();
        try {
            send(packet);
        } catch (IOException e) {
            throw new ProcessingException(ErrorType.IO_ERROR, e);
        }
        return receivePacket();
    }
}
