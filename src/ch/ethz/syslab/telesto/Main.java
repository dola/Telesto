package ch.ethz.syslab.telesto;

import java.io.IOException;

import ch.ethz.syslab.telesto.client.exception.ProcessingException;

public class Main {

    /**
     * Main entry point. First argument on command line should be either "MW" or "CL" to indicate whether a middleware
     * or a client instance should be started.
     * 
     * The remaining arguments are forwarded to the respective Main class.
     * 
     * @param args
     * @throws ProcessingException
     * @throws IOException
     */
    public static void main(String[] args) throws ProcessingException, IOException {

        if (args.length == 0) {
            System.err.println("Need to specify instance to start (MW, CL)");
            System.exit(1);
        }

        String i = args[0];
        String[] remainingArgs = new String[args.length - 1];
        System.arraycopy(args, 1, remainingArgs, 0, remainingArgs.length);

        if (args[0].equals("MW")) {
            // start middleware
            ch.ethz.syslab.telesto.server.Main.main(remainingArgs);
        } else if (args[0].equals("CL")) {
            // start client
            ch.ethz.syslab.telesto.client.Main.main(remainingArgs);
        }

    }

}
