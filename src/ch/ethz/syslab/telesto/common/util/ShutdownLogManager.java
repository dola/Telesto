package ch.ethz.syslab.telesto.common.util;

import java.util.logging.LogManager;

public class ShutdownLogManager extends LogManager {
    static ShutdownLogManager instance;

    public ShutdownLogManager() {
        instance = this;
    }

    @Override
    public void reset() {
        /* don't reset yet. */
    }

    private void superReset() {
        super.reset();
    }

    public static void resetFinally() {
        if (instance != null) {
            instance.superReset();
        }
    }
}
