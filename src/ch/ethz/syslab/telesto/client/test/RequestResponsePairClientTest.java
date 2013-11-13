package ch.ethz.syslab.telesto.client.test;

import ch.ethz.syslab.telesto.client.TelestoClient;
import ch.ethz.syslab.telesto.client.exception.ProcessingException;
import ch.ethz.syslab.telesto.common.model.Client;
import ch.ethz.syslab.telesto.common.model.Message;
import ch.ethz.syslab.telesto.common.model.Queue;

/**
 * Send message to a queue for a particular other client (id+1) that has a serving role and responses to the message.
 * 
 */
public class RequestResponsePairClientTest implements IClientTest {

    String queueName = "requestResponsePairQueue";
    Queue queue;

    @Override
    public void executeTest(TelestoClient c, Client self) throws ProcessingException {
        queue = c.getQueueByName(queueName);

        // send score around. Client always increments first part, server second one
        Message request = new Message(queue.id, self.id + 1, (byte) 1, "1-0");
        while (true) {
            Message response = c.sendRequestResponseMessage(request);
            String[] parts = response.message.split("-");
            parts[0] = String.valueOf(Integer.parseInt(parts[0]) + 1);
            request.message = parts[0] + "-" + parts[1];
        }
    }
}
