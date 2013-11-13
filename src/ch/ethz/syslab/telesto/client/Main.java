package ch.ethz.syslab.telesto.client;

import java.io.IOException;

import ch.ethz.syslab.telesto.client.exception.ProcessingException;
import ch.ethz.syslab.telesto.common.protocol.Packet.UnknownMethodException;
import ch.ethz.syslab.telesto.common.util.ShutdownLogManager;
import ch.ethz.syslab.telesto.profile.BenchmarkLog;

public class Main {
    private static boolean running = true;
    private static BenchmarkLog log;

    public static final void main(String[] args) throws IOException, UnknownMethodException, ProcessingException {
        log = new BenchmarkLog("client");
        TelestoClient client = new TelestoClient(log);

        Runtime.getRuntime().addShutdownHook(new ShutdownHook());

        while (running) {
            client.ping();
        }

        log.closeFile();
    }

    private static class ShutdownHook extends Thread {
        @Override
        public void run() {
            running = false;
            ShutdownLogManager.resetFinally();
        }
    }
}
