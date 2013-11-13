package ch.ethz.syslab.telesto.common.config;

public final class CONFIG {

    private CONFIG() {
        // don't allow instantiation
        throw new UnsupportedOperationException();
    }

    /* DB CONFIG */
    public static final String DB_POOL_NAME = "localhost";
    public static final String DB_SERVER_NAME = "localhost";
    public static final int DB_PORT_NUMBER = 5432;
    public static final String DB_NAME = "telesto";
    public static final String DB_USER = "telesto";
    public static final String DB_PASSWORD = "blubbi";
    public static final int DB_MAX_CONNECTIONS = 10;

    /* MIDDLEWARE CONFIG */
    public static final int MW_WORKER_POOL_SIZE = 10;
    public static final int MW_PORT = 9001;
    public static final String MW_HOST = "0.0.0.0";
    public static final int MW_READ_BUFFER_SIZE = 32768;

    /* CLIENT CONFIG */
    public static final int CLI_WRITE_BUFFER_SIZE = 32768;
    public static final String CLI_MW_HOST = "localhost";
    public static final int CLI_RETRY_DELAY = 50;

}
