package ch.ethz.syslab.telesto.profile;

public class Stopwatch {
    protected Object[] measurements = new Object[Phase.values().length];
    private Phase currentPhase;
    private long lastNanoTime;

    private BenchmarkLog log;

    public Stopwatch(BenchmarkLog log) {
        this.log = log;
    }

    public synchronized long enterPhase(Phase phase) {
        long time = System.nanoTime();
        long timePassed = time - lastNanoTime;
        if (currentPhase != null) {
            measurements[currentPhase.ordinal()] = timePassed;
            if (phase == Phase.WAITING) {
                flush();
            }
        }
        lastNanoTime = time;
        currentPhase = phase;
        return timePassed;
    }

    protected void flush() {
        log.addTimedEntry(measurements);
        measurements = new Object[Phase.values().length];
    }

    public static long mean(long[]... data) {
        long sum = 0;
        long n = 0;
        for (long[] row : data) {
            for (long point : row) {
                sum += point;
            }
            n += row.length;
        }
        return n == 0 ? 0 : sum / n;
    }

    public static double standardDeviation(double mean, long[]... data) {
        double variance = 0;
        long n = 0;
        for (long[] row : data) {
            for (long point : row) {
                variance += Math.pow(mean - point, 2);
            }
            n += row.length;
        }
        return n <= 1 ? 0 : Math.sqrt(variance / (n - 1));
    }

    public static int count(long[]... data) {
        int n = 0;
        for (long[] row : data) {
            n += row.length;
        }
        return n;
    }

    public static enum Phase {
        WAITING,
        PARSING,
        DATABASE,
        RESPONSE
    }
}
