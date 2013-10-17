package ch.ethz.syslab.telesto.util;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Log {
    private static Logger logger = Logger.getGlobal();

    private Log() {
        throw new UnsupportedOperationException();
    }

    public static void finest(String format, Object... params) {
        log(Level.FINEST, format, params);
    }

    public static void finer(String format, Object... params) {
        log(Level.FINER, format, params);
    }

    public static void fine(String format, Object... params) {
        log(Level.FINE, format, params);
    }

    public static void config(String format, Object... params) {
        log(Level.CONFIG, format, params);
    }

    public static void info(String format, Object... params) {
        log(Level.INFO, format, params);
    }

    public static void warning(String format, Object... params) {
        log(Level.WARNING, format, params);
    }

    public static void warning(Throwable exception, String format, Object... params) {
        log(Level.WARNING, exception, format, params);
    }

    public static void severe(String format, Object... params) {
        log(Level.SEVERE, format, params);
    }

    public static void severe(Throwable exception, String format, Object... params) {
        log(Level.SEVERE, exception, format, params);
    }

    public static void log(Level level, String format, Object... params) {
        System.out.println(level.toString() + ": " + String.format(format, params));
        logger.log(level, String.format(format, params));
    }

    public static void log(Level level, Throwable exception, String format, Object... params) {
        logger.log(level, String.format(format, params), exception);
    }
}
