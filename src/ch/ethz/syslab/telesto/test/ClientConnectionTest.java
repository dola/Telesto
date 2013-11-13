package ch.ethz.syslab.telesto.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import ch.ethz.syslab.telesto.client.exception.ProcessingException;
import ch.ethz.syslab.telesto.client.network.ClientConnection;
import ch.ethz.syslab.telesto.common.protocol.PingPacket;
import ch.ethz.syslab.telesto.common.protocol.PongPacket;

public class ClientConnectionTest {

    private ClientConnection connection;

    @Before
    public void setup() throws IOException {
        connection = new ClientConnection();
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

}
