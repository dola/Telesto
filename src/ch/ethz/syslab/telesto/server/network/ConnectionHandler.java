package ch.ethz.syslab.telesto.server.network;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;

import ch.ethz.syslab.telesto.common.config.CONFIG;
import ch.ethz.syslab.telesto.common.network.Connection;
import ch.ethz.syslab.telesto.common.util.Log;
import ch.ethz.syslab.telesto.server.db.Database;

public class ConnectionHandler extends Thread {
    public static final void main(String[] args) throws IOException {
        new ConnectionHandler(new InetSocketAddress(CONFIG.MW_HOST, CONFIG.MW_PORT), CONFIG.MW_WORKER_POOL_SIZE).start();
    }

    private static Log LOGGER = new Log(ConnectionHandler.class);

    private Database database = new Database();
    private ServerSocketChannel socket;
    private DataHandler[] workers;
    private Selector selector = Selector.open();
    private ArrayBlockingQueue<Connection> clientQueue = new ArrayBlockingQueue<Connection>(100);

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

        // Setting up database
        database.initialize();
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

    public int getHandledPacketCount() {
        int sum = 0;
        for (DataHandler worker : workers) {
            sum += worker.getHandledPacketCount();
        }
        return sum;
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
        Connection connection = (Connection) key.attachment();
        int bytesRead;

        bytesRead = connection.readFromChannel();
        if (bytesRead > 0) {
            LOGGER.fine("Read %s bytes from; %s", bytesRead, channel.getRemoteAddress());
            clientQueue.add(connection);
        } else if (bytesRead < 0) {
            LOGGER.info("Disconnected %s", channel.getRemoteAddress());
            connection.disconnect();
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
        ServerConnection connection = new ServerConnection(channel, database);
        connection.setSelectionKey(channel.register(selector, SelectionKey.OP_READ, connection));
    }
}
