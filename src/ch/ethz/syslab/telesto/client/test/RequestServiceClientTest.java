package ch.ethz.syslab.telesto.client.test;

import ch.ethz.syslab.telesto.client.TelestoClient;
import ch.ethz.syslab.telesto.client.exception.ProcessingException;
import ch.ethz.syslab.telesto.common.model.Client;
import ch.ethz.syslab.telesto.common.model.Message;
import ch.ethz.syslab.telesto.common.model.Queue;

public class RequestServiceClientTest implements IClientTest {

    String queueName = "serviceQueue";
    Queue queue;

    @Override
    public void executeTest(TelestoClient c, Client self) throws ProcessingException {
        queue = c.getQueueByName(queueName);

        int msgSent = 0;
        // send own id around
        Message request = new Message(queue.id, (byte) 1, String.format("%s:%s", ++msgSent, self.id));
        while (true) {
            Message response = c.sendRequestResponseMessage(request);
            request.message = String.format("%s:%s", ++msgSent, self.id);
        }
    }

}
