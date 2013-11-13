package ch.ethz.syslab.telesto.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import ch.ethz.syslab.telesto.client.exception.ProcessingException;
import ch.ethz.syslab.telesto.client.network.ClientConnection;
import ch.ethz.syslab.telesto.common.config.CONFIG;
import ch.ethz.syslab.telesto.common.protocol.PingPacket;
import ch.ethz.syslab.telesto.common.protocol.PongPacket;
import ch.ethz.syslab.telesto.profile.MockBenchmarkLog;

public class ClientConnectionTest {

    private ClientConnection connection;

    @Before
    public void setup() throws IOException {
        connection = new ClientConnection(new MockBenchmarkLog());
    }

    @Test
    public void testPacketId() throws ProcessingException {
        assertEquals(1, connection.sendPacket(new PingPacket()).packetId);
        assertEquals(2, connection.sendPacket(new PingPacket()).packetId);
    }

    @Test
    public void testPacketMethod() throws ProcessingException {
        assertTrue(connection.sendPacket(new PingPacket()) instanceof PongPacket);
    }

    @Test
    public void testBufferRewind() throws ProcessingException {
        for (int i = 1; i < CONFIG.CLI_WRITE_BUFFER_SIZE / 5; i++) {
            assertEquals(i, connection.sendPacket(new PingPacket()).packetId);
        }
    }

}
