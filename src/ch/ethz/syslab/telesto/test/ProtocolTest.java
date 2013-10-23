package ch.ethz.syslab.telesto.test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.nio.ByteBuffer;

import org.junit.Test;

import ch.ethz.syslab.telesto.protocol.ComplexTestPacket;
import ch.ethz.syslab.telesto.protocol.Packet;
import ch.ethz.syslab.telesto.protocol.Packet.UnknownMethodException;
import ch.ethz.syslab.telesto.protocol.PingPacket;

public class ProtocolTest {

    @Test
    public void emitPingPacket() {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        new PingPacket(0).emit(buffer);
        buffer.flip();
        assertEquals(5, buffer.getShort());
        assertEquals(1, buffer.get());
        assertEquals(0, buffer.getInt());
    }

    @Test
    public void parsePingPacket() throws UnknownMethodException {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        buffer.put((byte) 1);
        buffer.putInt(0);
        buffer.flip();
        Packet packet = Packet.create(buffer);
        assertTrue(packet instanceof PingPacket);
        assertEquals(0, packet.messageId);
    }

    @Test
    public void emitComplexPacket() {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        new ComplexTestPacket(1, (byte) 2, true, (short) 3, 4, 5, 6, 7, "string").emit(buffer);
        buffer.flip();
        assertEquals(41, buffer.getShort());
        assertEquals(0x71, buffer.get());
        assertEquals(1, buffer.getInt());
        assertEquals(2, buffer.get());
        assertEquals(1, buffer.get());
        assertEquals(3, buffer.getShort());
        assertEquals(4, buffer.getInt());
        assertEquals(5, buffer.getLong());
        assertEquals(6, buffer.getFloat(), 0);
        assertEquals(7, buffer.getDouble(), 0);
        assertEquals(6, buffer.getShort());
        byte[] stringBytes = new byte[6];
        buffer.get(stringBytes);
        assertArrayEquals("string".getBytes(Packet.CHARSET), stringBytes);
    }

    @Test
    public void messageIds() {
        for (int i = 0; i < Packet.packets.length; i++) {
            if (Packet.packets[i] != null) {
                assertEquals(i, Packet.packets[i].methodId());
            }
        }
    }
}
