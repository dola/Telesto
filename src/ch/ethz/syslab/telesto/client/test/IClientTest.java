package ch.ethz.syslab.telesto.client.test;

import ch.ethz.syslab.telesto.client.TelestoClient;
import ch.ethz.syslab.telesto.client.exception.ProcessingException;
import ch.ethz.syslab.telesto.common.model.Client;

public interface IClientTest {
    void executeTest(TelestoClient c, Client self) throws ProcessingException;

    void shutdown();
}
