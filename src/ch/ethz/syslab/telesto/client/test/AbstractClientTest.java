package ch.ethz.syslab.telesto.client.test;

public abstract class AbstractClientTest implements IClientTest {
    protected boolean running = true;

    @Override
    public void shutdown() {
        running = false;
    }
}
