package ch.ethz.syslab.telesto.client;

import java.util.HashMap;
import java.util.Map;

import ch.ethz.syslab.telesto.client.exception.ProcessingException;
import ch.ethz.syslab.telesto.client.test.ClientTest;
import ch.ethz.syslab.telesto.client.test.ClientTestExecutor;
import ch.ethz.syslab.telesto.common.model.ClientMode;
import ch.ethz.syslab.telesto.common.util.Log;
import ch.ethz.syslab.telesto.common.util.ShutdownLogManager;
import ch.ethz.syslab.telesto.profile.BenchmarkLog;

public class Main {
    private static Log LOGGER = new Log(Main.class);
    private static BenchmarkLog log;
    private static boolean running = true;

    /**
     * Command line args:
     * <ul>
     * <li>name or id
     * <li>test (one of [ONE_WAY, REQUEST_RESPONSE])
     * 
     * 
     */
    // e.g. use: args = new String[]{"name","client1","test",""}
    public static final void main(String[] args) throws ProcessingException {
        if (args.length <= 3) {
            // must specify name or id and test
            System.err.println("No client id or name provided. Usage: client (name <name>|id <id>) test <mode>");
            System.exit(1);
        }

        // parse parameters
        Map<String, String> arguments = mapArgs(args);
        if (!arguments.containsKey("test")) {
            System.err.println("No parameter test supplied");
            System.exit(1);
        }

        ClientTestExecutor c;

        String logName = arguments.containsKey("id") ? "client-" + arguments.get("id") : "client-" + arguments.get("name");
        log = new BenchmarkLog(logName);
        if (arguments.containsKey("id")) {
            // identify by id
            c = new ClientTestExecutor(Integer.parseInt(arguments.get("id")), log);
        } else if (arguments.containsKey("name")) {
            // create new client with name
            c = new ClientTestExecutor(arguments.get("name"), ClientMode.FULL, log);
        } else {
            System.err.println("No parameter test supplied");
            return;
        }

        String testId = arguments.get("test");
        ClientTest t = ClientTest.getByString(testId);
        if (t != null) {
            Runtime.getRuntime().addShutdownHook(new ShutdownHook());
            runTest(c, t);
        } else {
            System.err.println("Test not found by id " + testId);
            System.exit(0);
        }
    }

    private static void runTest(ClientTestExecutor c, ClientTest t) throws ProcessingException {

        // Run Test
        try {
            c.runTest(t.getTestClass().newInstance());
        } catch (InstantiationException | IllegalAccessException e) {
            LOGGER.severe("Could not create Test instance %s", t.getTestClass().getName(), e);
        }
    }

    private static Map<String, String> mapArgs(String[] args) {
        Map<String, String> m = new HashMap<>();
        for (int i = 0; i < args.length; i += 2) {
            if (i + 1 == args.length) {
                LOGGER.warning("Argument length invalid. Ignoring last argument %s", args[i]);
                break;
            }
            m.put(args[i], args[i + 1]);
        }
        return m;
    }

    private static class ShutdownHook extends Thread {
        @Override
        public void run() {
            running = false;
            log.closeFile();
            ShutdownLogManager.resetFinally();
        }
    }
}
