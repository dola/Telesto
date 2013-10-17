package ch.ethz.syslab.telesto.server.network;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;

import ch.ethz.syslab.telesto.server.model.Client;
import ch.ethz.syslab.telesto.util.Log;

public class DataHandler extends Thread {

    private MessageHandler[] workers;
    private Selector selector;
    private ConcurrentLinkedQueue<SocketChannel> connectionQueue;
    private ArrayBlockingQueue<Client> clientQueue;

    public DataHandler(int workerCount) throws IOException {
        selector = Selector.open();
        connectionQueue = new ConcurrentLinkedQueue<SocketChannel>();
        clientQueue = new ArrayBlockingQueue<Client>(100);
        workers = new MessageHandler[workerCount];
        for (int i = 0; i < workerCount; i++) {
            workers[i] = new MessageHandler(clientQueue);
        }
    }

    @Override
    public void start() {
        super.start();
        for (MessageHandler worker : workers) {
            worker.start();
        }
    }

    public void register(SocketChannel channel) throws IOException {
        channel.configureBlocking(false);
        connectionQueue.add(channel);
        selector.wakeup();
    }

    @Override
    public void run() {
        while (true) {
            try {
                select();
            } catch (IOException e) {
                Log.severe(e, "Error while selecting.");
            }
        }
    }

    private void select() throws IOException {
        int changes = selector.select();
        if (changes == 0) {
            while (!connectionQueue.isEmpty()) {
                SocketChannel channel = connectionQueue.poll();
                channel.register(selector, SelectionKey.OP_READ, new Client(channel));
            }
        } else {
            for (SelectionKey key : selector.selectedKeys()) {
                if (key.isReadable()) {
                    Client client = (Client) key.attachment();
                    int count = client.channel.read(client.buffer);
                    Log.fine("Read %d bytes from channel %s", count, client.channel);
                    clientQueue.add(client);
                }
            }
        }
    }

}
