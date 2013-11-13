package ch.ethz.syslab.telesto.common.config;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import ch.ethz.syslab.telesto.common.util.Log;

public final class CONFIG {
    private static Log LOGGER = new Log(CONFIG.class);

    private static Properties prop = new SortedProperties();
    private static final String DEFAULT_PROPERTIES_PATH = "default-config.properties";
    private static final String PROPERTIES_PATH = "config.properties";

    static {
        try {
            LOGGER.config("trying to load properties file at %s", PROPERTIES_PATH);
            prop.load(new FileInputStream(PROPERTIES_PATH));
        } catch (FileNotFoundException e) {
            try {
                LOGGER.config("Loading default values for CONFIG");
                loadDefaultValues();
            } catch (IOException e1) {
                LOGGER.severe("Failed to read default config file", e1);
            }
        } catch (IOException e) {
            LOGGER.severe("Failed to read config file", e);
        }
    }

    private static void loadDefaultValues() throws IOException {
        // load default values from resource
        InputStream in = CONFIG.class.getResourceAsStream(DEFAULT_PROPERTIES_PATH);
        prop.load(in);
        in.close();

        // write default values to config file
        FileOutputStream out = new FileOutputStream(PROPERTIES_PATH);
        prop.store(out, null);
        out.close();
    }

    private CONFIG() {
        // don't allow instantiation
        throw new UnsupportedOperationException();
    }

    /* DB CONFIG */
    public static final String DB_POOL_NAME = prop.getProperty("dbPoolName");
    public static final String DB_SERVER_NAME = prop.getProperty("dbServerName");
    public static final int DB_PORT_NUMBER = Integer.parseInt(prop.getProperty("dbPortNumber"));
    public static final String DB_NAME = prop.getProperty("dbName");
    public static final String DB_USER = prop.getProperty("dbUser");
    public static final String DB_PASSWORD = prop.getProperty("dbPassword");
    public static final int DB_MAX_CONNECTIONS = Integer.parseInt(prop.getProperty("dbMaxConnections"));

    /* MIDDLEWARE CONFIG */
    public static final int MW_WORKER_POOL_SIZE = Integer.parseInt(prop.getProperty("mwWorkerPoolSize"));
    public static final int MW_PORT = Integer.parseInt(prop.getProperty("mwPort"));
    public static final String MW_HOST = prop.getProperty("mwHost");
    public static final int MW_READ_BUFFER_SIZE = Integer.parseInt(prop.getProperty("mwReadBufferSize"));

    /* CLIENT CONFIG */
    public static final int CLI_WRITE_BUFFER_SIZE = Integer.parseInt(prop.getProperty("cliWriteBufferSize"));
    public static final String CLI_MW_HOST = prop.getProperty("cliMwHost");
    public static final int CLI_RETRY_DELAY = Integer.parseInt(prop.getProperty("cliRetryDelay"));
    public static final int CLI_ONE_WAY_COUNT = Integer.parseInt(prop.getProperty("cliOneWayCount"));
    public static final int CLI_REQUEST_RESPONSE_COUNT = Integer.parseInt(prop.getProperty("cliRequestResponseCount"));

}
