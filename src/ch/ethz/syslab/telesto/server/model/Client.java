package ch.ethz.syslab.telesto.server.model;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class Client {
    public SocketChannel channel;
    public ByteBuffer buffer;

    public Client(SocketChannel channel) {
        this.channel = channel;
        buffer = ByteBuffer.allocate(32768);
    }

}
