package ch.ethz.syslab.telesto.client;

import ch.ethz.syslab.telesto.client.exception.ProcessingException;
import ch.ethz.syslab.telesto.common.util.ShutdownLogManager;
import ch.ethz.syslab.telesto.profile.BenchmarkLog;

public class PingMain {
    private static BenchmarkLog log;
    private static boolean running = true;

    public static void main(String[] args) throws ProcessingException {
        Runtime.getRuntime().addShutdownHook(new ShutdownHook());

        log = new BenchmarkLog("client");
        TelestoClient c = new TelestoClient(log);

        while (running) {
            c.ping();
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
