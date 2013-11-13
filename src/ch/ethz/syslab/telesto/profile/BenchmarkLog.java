package ch.ethz.syslab.telesto.profile;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import ch.ethz.syslab.telesto.common.util.Log;
import ch.ethz.syslab.telesto.common.util.StringUtil;

/**
 * A simple implementation of a log for benchmarking tasks that uses a {@link BufferedWriter}. All entries written must
 * not contain any tabular characters <code>\t</code> other than those intended for value separation or the resulting
 * log file may be corrupted for reading and analysis.
 * 
 * <p>
 * Note that this implementation does not throw any exception but directly logs them to its logger. This ensures that no
 * {@link IOException}s have to be handled by any users of this service.
 * 
 */
public class BenchmarkLog {
    private static Log LOGGER = new Log(BenchmarkLog.class);

    private static final String DELIMITER = "\t";

    private static final String EXTENSION = ".log";
    private static String FOLDER = "log";
    private String name;

    private File logFile;
    private BufferedWriter logWriter;

    public BenchmarkLog(String name) {
        this.name = name;
        openLogFile();
    }

    /**
     * Set ExecutionId for the current execution that is used as the folder name for all logFiles written by instances
     * of {@link BenchmarkLog}. This does not throw an exception if the name is not valid. Only further instantiation of
     * this class lead to the expected {@link IOException}s.
     * 
     * @param id
     *            A valid foldername for the current system
     */
    public static void setExecutionId(String id) {
        FOLDER = id;
    }

    /**
     * creates the logFile and opens it for writing. Also ensures that the directory hierarchy is present and generates
     * it otherwise.
     */
    protected void openLogFile() {
        logFile = new File(FOLDER, name + EXTENSION);
        if (logFile.exists()) {
            LOGGER.warning("Log File %s already exists at location %s!", name, logFile.getAbsolutePath());
        }
        logFile.getParentFile().mkdirs();
        try {
            logFile.createNewFile();
            logWriter = new BufferedWriter(new FileWriter(logFile));
        } catch (IOException e) {
            LOGGER.severe(e, "Failed to create log file %s", logFile);
        }
    }

    /**
     * Adds the given entry to a new line of the log file.
     * 
     * @param entry
     */
    public void addEntry(Object... entries) {
        try {
            logWriter.write(StringUtil.joinString(DELIMITER, entries) + "\n");
        } catch (IOException e) {
            LOGGER.severe(e, "Failed to write to log file %s", logFile);
        }
    }

    /**
     * Adds the given entry prepended by the current system timestamp to a new line of the log file.
     * 
     * @param entries
     */
    public void addTimedEntry(Object... entries) {
        long time = System.currentTimeMillis();
        if (entries != null && entries.length > 0) {
            entries[0] = StringUtil.joinString(DELIMITER, time, entries[0]);
            addEntry(entries);
        } else {
            addEntry(time);
        }
    }

    /**
     * close the underlying log file. Internally uses {@link Writer#close()}.
     * 
     * @throws IOException
     */
    public void closeFile() {
        try {
            logWriter.close();
        } catch (IOException e) {
            LOGGER.severe(e, "Failed to close log file %s", logFile);
        }
    }

    /**
     * Ensure that the logWriter is closed and all content is written to the file.
     */
    @Override
    protected void finalize() throws Throwable {
        closeFile();
    }

}
