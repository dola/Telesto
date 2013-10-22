package ch.ethz.syslab.telesto.server.network;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;

import ch.ethz.syslab.telesto.server.model.Client;
import ch.ethz.syslab.telesto.util.Log;

public class ConnectionHandler extends Thread {
    public static final void main(String[] args) throws IOException {
        new ConnectionHandler(new InetSocketAddress("localhost", 8889), 8).start();
    }

    private static Log LOGGER = new Log(ConnectionHandler.class);

    private ServerSocketChannel socket;
    private DataHandler[] workers;
    private Selector selector = Selector.open();
    private ArrayBlockingQueue<Client> clientQueue = new ArrayBlockingQueue<Client>(100);

    ConnectionHandler(InetSocketAddress address, int workerCount) throws IOException {
        LOGGER.config("Listening on %s:%d", address.getHostName(), address.getPort());

        // Setting up data workers
        workers = new DataHandler[workerCount];
        for (int i = 0; i < workerCount; i++) {
            workers[i] = new DataHandler(clientQueue, i);
        }

        // Setting up selector
        socket = ServerSocketChannel.open().bind(address);
        socket.configureBlocking(false);
        socket.register(selector, SelectionKey.OP_ACCEPT);
    }

    @Override
    public void start() {
        super.start();
        for (DataHandler worker : workers) {
            worker.start();
        }
    }

    @Override
    public void run() {
        try {
            eventLoop();
        } catch (IOException e) {
            LOGGER.severe(e, "Error while selecting active channels");
        }
    }

    private void eventLoop() throws IOException {
        while (true) {
            int availableChannels = selector.select();
            Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
            LOGGER.finest("Selected channels: %d", availableChannels);

            while (keyIterator.hasNext()) {
                SelectionKey key = keyIterator.next();
                if (key.isReadable()) {
                    read(key);
                } else if (key.isAcceptable()) {
                    accept(key);
                }
                keyIterator.remove();
            }
        }
    }

    private void read(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        Client client = (Client) key.attachment();
        int bytesRead;

        // This prevents read operations while the buffer is being rewound
        synchronized (client.writeBuffer) {
            bytesRead = channel.read(client.writeBuffer);
        }
        if (bytesRead > 0) {
            LOGGER.fine("Read %s bytes from; %s", bytesRead, channel.getRemoteAddress());
            clientQueue.add(client);
        } else if (bytesRead < 0) {
            LOGGER.info("Disconnected %s", channel.getRemoteAddress());
            key.cancel();
        }
    }

    private void accept(SelectionKey key) throws IOException {
        SocketChannel channel = ((ServerSocketChannel) key.channel()).accept();
        if (channel == null) {
            LOGGER.warning("Accepted channel is null");
            return;
        }
        LOGGER.info("Accepted new connection from %s", channel.getRemoteAddress());
        channel.configureBlocking(false);
        channel.register(selector, SelectionKey.OP_READ, new Client());
    }
}
