package ch.ethz.syslab.telesto.util;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Log {
    private Logger logger;
    private String name;

    public Log(Class<?> cls) {
        name = cls.getName();
        logger = Logger.getLogger(name);
    }

    public void finest(String format, Object... params) {
        log(Level.FINEST, format, params);
    }

    public void finer(String format, Object... params) {
        log(Level.FINER, format, params);
    }

    public void fine(String format, Object... params) {
        log(Level.FINE, format, params);
    }

    public void config(String format, Object... params) {
        log(Level.CONFIG, format, params);
    }

    public void info(String format, Object... params) {
        log(Level.INFO, format, params);
    }

    public void warning(String format, Object... params) {
        log(Level.WARNING, format, params);
    }

    public void warning(Throwable exception, String format, Object... params) {
        log(Level.WARNING, exception, format, params);
    }

    public void severe(String format, Object... params) {
        log(Level.SEVERE, format, params);
    }

    public void severe(Throwable exception, String format, Object... params) {
        log(Level.SEVERE, exception, format, params);
    }

    public void log(Level level, String format, Object... params) {
        logger.log(level, String.format(format, params));
    }

    public void log(Level level, Throwable exception, String format, Object... params) {
        logger.log(level, String.format(format, params), exception);
    }
}
