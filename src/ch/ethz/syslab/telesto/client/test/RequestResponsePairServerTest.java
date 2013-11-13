package ch.ethz.syslab.telesto.client.test;

import ch.ethz.syslab.telesto.client.TelestoClient;
import ch.ethz.syslab.telesto.client.exception.ProcessingException;
import ch.ethz.syslab.telesto.common.model.Client;
import ch.ethz.syslab.telesto.common.model.Message;
import ch.ethz.syslab.telesto.common.model.Queue;

/**
 * Retrieve messages from a queue and send back a response.
 * 
 */
public class RequestResponsePairServerTest implements IClientTest {

    String queueName = "requestResponsePairQueue";
    Queue queue;

    @Override
    public void executeTest(TelestoClient c, Client self) throws ProcessingException {
        queue = c.getQueueByName(queueName);

        // send score around. Client always increments first part, server second one
        while (true) {
            // retrieve request
            Message request = c.retrieveMessage(queue.id);

            // process request
            String[] parts = request.message.split("-");
            parts[1] = String.valueOf(Integer.parseInt(parts[1]) + 1);

            // send response
            Message response = new Message(request, parts[0] + "-" + parts[1]);
            c.putMessage(response);
        }
    }

}
