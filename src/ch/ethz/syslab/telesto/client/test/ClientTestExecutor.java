package ch.ethz.syslab.telesto.client.test;

import ch.ethz.syslab.telesto.client.TelestoClient;
import ch.ethz.syslab.telesto.client.exception.ProcessingException;
import ch.ethz.syslab.telesto.common.model.Client;
import ch.ethz.syslab.telesto.common.model.ClientMode;
import ch.ethz.syslab.telesto.profile.BenchmarkLog;

public class ClientTestExecutor {
    TelestoClient client;
    Client self;

    IClientTest test;

    public ClientTestExecutor(String name, ClientMode mode, BenchmarkLog log) throws ProcessingException {
        client = new TelestoClient(log);
        self = client.connect(name, ClientMode.FULL);
    }

    public ClientTestExecutor(int id, BenchmarkLog log) throws ProcessingException {
        client = new TelestoClient(log);
        self = client.connect(id);
    }

    public void runTest(IClientTest test) throws ProcessingException {
        this.test = test;
        test.executeTest(client, self);
    }

    public void shutdown() {
        if (test != null) {
            test.shutdown();
        }
    }

}
