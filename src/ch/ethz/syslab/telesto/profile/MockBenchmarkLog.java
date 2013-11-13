package ch.ethz.syslab.telesto.profile;

public class MockBenchmarkLog extends BenchmarkLog {

    public MockBenchmarkLog() {
        super(null);
    }

    public static void setExecutionId(String id) {
    }

    @Override
    protected void openLogFile() {
    }

    @Override
    public void addEntry(Object... entries) {
    }

    @Override
    public void addTimedEntry(Object... entries) {
    }

    @Override
    public void closeFile() {
    }

}
