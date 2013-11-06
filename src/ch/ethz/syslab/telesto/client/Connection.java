package ch.ethz.syslab.telesto.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import ch.ethz.syslab.telesto.protocol.Packet;
import ch.ethz.syslab.telesto.protocol.PingPacket;
import ch.ethz.syslab.telesto.server.config.CONFIG;

public class Connection {
    public static final void main(String[] args) throws IOException {
        Connection connection = new Connection(new InetSocketAddress("localhost", CONFIG.MW_PORT));
        Packet packet = new PingPacket(1);
        connection.send(packet);
    }

    private SocketChannel channel;
    private ByteBuffer buffer = ByteBuffer.allocateDirect(CONFIG.CLI_WRITE_BUFFER_SIZE);

    Connection(InetSocketAddress address) throws IOException {
        channel = SocketChannel.open(address);
    }

    void send(Packet packet) throws IOException {
        packet.emit(buffer);
        buffer.flip();
        while (buffer.hasRemaining()) {
            channel.write(buffer);
        }
        buffer.compact();
    }
}
