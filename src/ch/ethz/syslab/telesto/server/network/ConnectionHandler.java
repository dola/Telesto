package ch.ethz.syslab.telesto.server.network;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import ch.ethz.syslab.telesto.util.Log;

public class ConnectionHandler extends Thread {
    public static final void main(String[] args) throws IOException {
        new ConnectionHandler(new InetSocketAddress("localhost", 8889), 1).start();
    }

    private ServerSocketChannel socket;
    private DataHandler dataHandler;

    ConnectionHandler(InetSocketAddress address, int workerCount) throws IOException {
        Log.config("Listening on %s:%d", address.getHostName(), address.getPort());
        socket = ServerSocketChannel.open().bind(address);
        dataHandler = new DataHandler(workerCount);
    }

    @Override
    public void start() {
        super.start();
        dataHandler.start();
    }

    @Override
    public void run() {
        while (true) {
            try {
                accept();
            } catch (IOException e) {
                Log.warning(e, "Error while accepting incoming connection");
            }
        }
    }

    private void accept() throws IOException {
        SocketChannel channel = socket.accept();
        Log.info("Accepted connection from %s", channel.getLocalAddress());
        dataHandler.register(channel);
        /*
         * SelectionKey key = channel.register(selector, SelectionKey.OP_READ,
         * new Client(channel)); selector.wakeup();
         * 
         * selector.select(); Log.info("%s", key.channel()); Log.info("%s",
         * key.isReadable()); ByteBuffer buffer = ByteBuffer.allocate(3);
         * channel.read(buffer); buffer.flip(); while (buffer.hasRemaining()) {
         * System.out.print(buffer.get()); // read 1 byte at a time }
         */
    }
}
