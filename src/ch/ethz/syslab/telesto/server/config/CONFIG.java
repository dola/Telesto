package ch.ethz.syslab.telesto.server.config;

public final class CONFIG {

    private CONFIG() {
        // don't allow instantiation
        throw new UnsupportedOperationException();
    }

    /* DB CONFIG */
    public static String DB_POOL_NAME = "localhost";
    public static String DB_SERVER_NAME = "localhost";
    public static final int DB_PORT_NUMBER = 5432;
    public static String DB_NAME = "telesto";
    public static String DB_USER = "telesto";
    public static String DB_PASSWORD = "blubbi";
    public static int DB_MAX_CONNECTIONS = 10;

    /* MIDDLEWARE CONFIG */
    public static int MW_WORKER_POOL_SIZE = 10;
}
