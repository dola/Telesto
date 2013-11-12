package ch.ethz.syslab.telesto.test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.nio.ByteBuffer;
import java.sql.Timestamp;

import org.junit.Test;

import ch.ethz.syslab.telesto.common.model.Message;
import ch.ethz.syslab.telesto.common.model.Queue;
import ch.ethz.syslab.telesto.common.protocol.ComplexTestPacket;
import ch.ethz.syslab.telesto.common.protocol.MessageTestPacket;
import ch.ethz.syslab.telesto.common.protocol.Packet;
import ch.ethz.syslab.telesto.common.protocol.PingPacket;
import ch.ethz.syslab.telesto.common.protocol.QueueTestPacket;
import ch.ethz.syslab.telesto.common.protocol.Packet.UnknownMethodException;

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
        assertEquals(0, packet.packetId);
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
    public void messagePacket() throws UnknownMethodException {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        Message message = new Message(1, 2, 3, 4, 5, (byte) 6, new Timestamp(7), "8");
        new MessageTestPacket(0, message).emit(buffer);
        buffer.flip();
        buffer.getShort();
        MessageTestPacket packet = (MessageTestPacket) Packet.create(buffer);
        assertEquals(0, packet.packetId);
        assertEquals(message, packet.message);
        assertEquals(message.queueId, packet.message.queueId);
        assertEquals(message.senderId, packet.message.senderId);
        assertEquals(message.receiverId, packet.message.receiverId);
        assertEquals(message.priority, packet.message.priority);
        assertEquals(message.timeOfArrival, packet.message.timeOfArrival);
        assertEquals(message.message, packet.message.message);
    }

    @Test
    public void queuePacket() throws UnknownMethodException {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        Queue queue = new Queue(1, "Hello Queue");
        new QueueTestPacket(0, queue).emit(buffer);
        buffer.flip();
        buffer.getShort();
        QueueTestPacket packet = (QueueTestPacket) Packet.create(buffer);
        assertEquals(0, packet.packetId);
        assertEquals(queue, packet.queue);
        assertEquals(queue.name, packet.queue.name);
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
