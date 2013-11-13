package ch.ethz.syslab.telesto.client.test;

import ch.ethz.syslab.telesto.client.TelestoClient;
import ch.ethz.syslab.telesto.client.exception.ProcessingException;
import ch.ethz.syslab.telesto.common.model.Client;
import ch.ethz.syslab.telesto.common.model.Message;
import ch.ethz.syslab.telesto.common.model.Queue;

public class ServerServiceClientTest implements IClientTest {
    String queueName = "serviceQueue";
    Queue queue;

    @Override
    public void executeTest(TelestoClient c, Client self) throws ProcessingException {
        queue = c.getQueueByName(queueName);

        int msgServed = 0;
        while (true) {
            // retrieve request
            Message request = c.retrieveMessage(queue.id);

            // process request
            String message = String.format("%s-%s:%s", request.message, ++msgServed, self.id);

            // send response
            Message response = new Message(request, message);
            c.putMessage(response);
        }
    }

}
