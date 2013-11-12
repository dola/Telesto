package ch.ethz.syslab.telesto.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import ch.ethz.syslab.telesto.common.protocol.Packet;
import ch.ethz.syslab.telesto.common.protocol.PingPacket;
import ch.ethz.syslab.telesto.common.protocol.Packet.UnknownMethodException;
import ch.ethz.syslab.telesto.server.config.CONFIG;

public class Connection {
    public static final void main(String[] args) throws IOException, UnknownMethodException {
        Connection connection = new Connection(new InetSocketAddress("localhost", CONFIG.MW_PORT));
        Packet packet = new PingPacket(1);
        connection.send(packet);
        System.out.println(connection.receive());
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

    Packet receive() throws IOException, UnknownMethodException {
        channel.read(buffer);
        buffer.flip();
        buffer.getShort();
        Packet packet = Packet.create(buffer);
        buffer.compact();
        return packet;
    }
}
