package ch.ethz.syslab.telesto.client.test;

import java.util.Random;

import ch.ethz.syslab.telesto.client.TelestoClient;
import ch.ethz.syslab.telesto.client.exception.ProcessingException;
import ch.ethz.syslab.telesto.common.config.CONFIG;
import ch.ethz.syslab.telesto.common.model.Client;
import ch.ethz.syslab.telesto.common.model.Message;
import ch.ethz.syslab.telesto.common.model.Queue;

/**
 * Test procedure:
 * <ol>
 * <li>select random other client
 * <li>send a message with a counter to the selected client
 * <li>wait for a message for self
 * <li>increase counter
 * <li>forward message to new random client
 * <li>repeat endlessly
 * </ol>
 */
public class OneWayClientTest extends AbstractClientTest {
    private final static String queueName = "oneWayQueue";
    private Random r = new Random();

    @Override
    public void executeTest(TelestoClient c, Client self) throws ProcessingException {

        Queue q = c.getQueueByName(queueName);

        Message r = new Message(q.id, (byte) 1, "0");

        while (running) {
            r.receiverId = generateRecipientId(self.id);
            c.putMessage(r);
            r = c.retrieveMessage(q.id);
            r.message = String.valueOf(Integer.parseInt(r.message) + 1);
        }
    }

    public int generateRecipientId(int ownId) {
        int max = CONFIG.CLI_ONE_WAY_COUNT;
        // range for this type: 0..n

        int i = r.nextInt(max - 1) + 2;
        if (i == ownId) {
            i = 1;
        }
        return i;
    }
}
