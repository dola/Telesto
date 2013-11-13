package ch.ethz.syslab.telesto.server;

import java.io.IOException;
import java.net.InetSocketAddress;

import ch.ethz.syslab.telesto.common.config.CONFIG;
import ch.ethz.syslab.telesto.common.util.Log;
import ch.ethz.syslab.telesto.server.network.ConnectionHandler;

public class Main {

    private static Log LOGGER = new Log(Main.class);

    private static ConnectionHandler ch;

    public static final void main(String[] args) throws IOException {
        LOGGER.info("Middleware starting...");
        ch = new ConnectionHandler(new InetSocketAddress(CONFIG.MW_HOST, CONFIG.MW_PORT), CONFIG.MW_WORKER_POOL_SIZE);
        ch.start();
        LOGGER.info("Middleware ready");

        // adding shutdown hook
        Runtime.getRuntime().addShutdownHook(new ShutdownHook());
    }

    private static class ShutdownHook extends Thread {
        @Override
        public void run() {
        }
    }
}
