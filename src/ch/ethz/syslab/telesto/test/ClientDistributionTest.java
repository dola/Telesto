package ch.ethz.syslab.telesto.test;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ch.ethz.syslab.telesto.client.test.OneWayClientTest;
import ch.ethz.syslab.telesto.common.config.CONFIG;

public class ClientDistributionTest {

    private static final int RUNS = 10000;

    @Test
    public void testGenerateRandomOneWayRecipient() {
        // has to always be between 1..CONFIG.CLI_COUNT/5*3 and never ownId
        int minAllowed = 1;
        int maxAllowed = CONFIG.CLI_ONE_WAY_COUNT;

        int maxFound = 0;
        int minFound = maxAllowed;

        int ownId = CONFIG.CLI_ONE_WAY_COUNT / 5;
        OneWayClientTest t = new OneWayClientTest();
        for (int i = 0; i < RUNS; i++) {
            int c = t.generateRecipientId(ownId);
            assertNotEquals(ownId, c);
            assertTrue(c >= minAllowed);
            assertTrue(c <= maxAllowed);
            maxFound = Math.max(maxFound, c);
            minFound = Math.min(minFound, c);
        }
        System.out.println("Max value: " + maxFound + " (allowed: " + maxAllowed + ")");
        System.out.println("Min value: " + minFound + " (allowed: " + minAllowed + ")");
    }
}
