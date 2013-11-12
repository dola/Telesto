package ch.ethz.syslab.telesto.client.network;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

import ch.ethz.syslab.telesto.common.config.CONFIG;
import ch.ethz.syslab.telesto.common.network.Connection;
import ch.ethz.syslab.telesto.common.protocol.Packet;
import ch.ethz.syslab.telesto.common.util.Log;

public class ClientConnection extends Connection {
    private static Log LOGGER = new Log(ClientConnection.class);

    public ClientConnection() throws IOException {
        super(SocketChannel.open(new InetSocketAddress(CONFIG.CLI_MW_HOST, CONFIG.MW_PORT)));
        socket.configureBlocking(true);
    }

    public Packet receivePacket() throws IOException {
        Packet packet = null;
        while (packet == null && connected) {
            readFromChannel();
            packet = readPacket();
        }
        if (packet == null) {
            LOGGER.info("Disconnected");
        } else {
            LOGGER.fine("Received packet %s", packet);
        }
        return packet;
    }
}
