package ch.ethz.syslab.telesto.server.network;

import java.nio.ByteBuffer;
import java.util.concurrent.ArrayBlockingQueue;

import ch.ethz.syslab.telesto.server.model.Client;
import ch.ethz.syslab.telesto.util.Log;

public class MessageHandler extends Thread {

    private ArrayBlockingQueue<Client> clientQueue;

    public MessageHandler(ArrayBlockingQueue<Client> clientQueue) {
        this.clientQueue = clientQueue;
    }

    @Override
    public void run() {
        while (true) {
            Client client;
            try {
                client = clientQueue.take();
            } catch (InterruptedException e) {
                Log.info("Interrupt in message handler loop");
                return;
            }
            ByteBuffer buffer = client.buffer.duplicate();
            buffer.flip();
            Log.info("Buffer content %x%x%x", buffer.get(), buffer.get(), buffer.get());
            client.buffer.compact();
        }
    }

}
