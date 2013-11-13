package ch.ethz.syslab.telesto.server.network;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;

import ch.ethz.syslab.telesto.common.network.Connection;
import ch.ethz.syslab.telesto.common.util.Log;
import ch.ethz.syslab.telesto.profile.BenchmarkLog;
import ch.ethz.syslab.telesto.server.db.Database;

public class ConnectionHandler extends Thread {

    private static Log LOGGER = new Log(ConnectionHandler.class);

    private Database database = new Database();
    private ServerSocketChannel socket;
    private DataHandler[] workers;
    private Selector selector = Selector.open();
    private ArrayBlockingQueue<Connection> clientQueue = new ArrayBlockingQueue<Connection>(100);
    private boolean running = true;

    public ConnectionHandler(InetSocketAddress address, int workerCount, BenchmarkLog log) throws IOException {
        // Setting up data workers
        workers = new DataHandler[workerCount];
        for (int i = 0; i < workerCount; i++) {
            workers[i] = new DataHandler(clientQueue, i, log);
        }

        // Setting up selector
        socket = ServerSocketChannel.open().bind(address);
        socket.configureBlocking(false);
        socket.register(selector, SelectionKey.OP_ACCEPT);
        LOGGER.config("Listening on %s:%d", address.getHostName(), address.getPort());

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
            e.printStackTrace();
            LOGGER.severe(e, "Error while selecting active channels");
        }
    }

    public void shutdown() {
        LOGGER.info("Shutting down IO loop...");
        running = false;
        LOGGER.info("Waiting for client queue to be emptied...");
        while (!clientQueue.isEmpty()) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                // Ignore
            }
        }
        LOGGER.info("Terminating workers...");
        for (DataHandler worker : workers) {
            worker.shutdown();
        }
        LOGGER.info("Closing connections...");
        for (SelectionKey key : selector.keys()) {
            try {
                key.channel().close();
            } catch (IOException e) {
                // Ignore
            }
        }
    }

    private void eventLoop() throws IOException {
        while (running) {
            int availableChannels = selector.select();
            Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
            LOGGER.finest("Selected channels: %d", availableChannels);

            while (keyIterator.hasNext()) {
                SelectionKey key = keyIterator.next();
                if (key.isReadable()) {
                    try {
                        read(key);
                    } catch (IOException e) {
                        key.cancel();
                        LOGGER.info("Client disonnected: %s", key.attachment());
                    }
                } else if (key.isAcceptable()) {
                    try {
                        accept(key);
                    } catch (IOException e) {
                        key.cancel();
                        LOGGER.info("Error while accepting connection: %s", e);
                    }
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
