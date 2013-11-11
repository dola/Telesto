package ch.ethz.syslab.telesto.protocol;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.sql.Timestamp;

import ch.ethz.syslab.telesto.model.*;

/* 
 * Do not edit this file! 
 * 
 * Edit the template at tools/protocol/telesto/templates/superclass.java instead.
 */
public abstract class Packet {
    public static final Charset CHARSET = Charset.forName("UTF-8");
    public static Packet[] packets = new Packet[Byte.MAX_VALUE + 1];

    public int packetId;

    public abstract void emit(ByteBuffer buffer);

    public abstract void parse(ByteBuffer buffer);

    public abstract Packet newInstance();
    
    public abstract byte methodId();
    
    public static Packet create(ByteBuffer buffer) throws UnknownMethodException {
        int method = buffer.get();
        
        if (method > Byte.MAX_VALUE || packets[method] == null) {
            throw new UnknownMethodException(method);
        }
        
        Packet packet = packets[method].newInstance();
        packet.parse(buffer);
        return packet;
    }

    protected static String getString(ByteBuffer buffer) {
        byte[] bytes = new byte[buffer.getShort()];
        buffer.get(bytes);
        return new String(bytes);
    }
    
    protected static void putString(ByteBuffer buffer, String value) {
        byte[] bytes = value.getBytes(CHARSET);
        buffer.putShort((short) bytes.length);
        buffer.put(bytes);
    }

    protected static boolean getBoolean(ByteBuffer buffer) {
        return buffer.get() == 1;
    }
    
    protected static void putBoolean(ByteBuffer buffer, boolean value) {
        buffer.put((byte)(value ? 1 : 0));
    }

    protected static Message getMessage(ByteBuffer buffer) {
        return new Message(buffer.getInt(),
                           buffer.getInt(),
                           buffer.getInt(),
                           buffer.getInt(),
                           buffer.getInt(),
                           buffer.get(),
                           new Timestamp(buffer.getLong()),
                           getString(buffer));
    }

    protected static void putMessage(ByteBuffer buffer, Message message) {
        buffer.putInt(message.id);
        buffer.putInt(message.queueId);
        buffer.putInt(message.senderId);
        buffer.putInt(message.receiverId);
        buffer.putInt(message.context);
        buffer.put(message.priority);
        buffer.putLong(message.timeOfArrival.getTime());
        putString(buffer, message.message);
    }

    protected static Queue getQueue(ByteBuffer buffer) {
        return new Queue(buffer.getInt(),
                         getString(buffer));
    }

    protected static void putQueue(ByteBuffer buffer, Queue queue) {
        buffer.putInt(queue.id);
        putString(buffer, queue.name);
    }

    public static class UnknownMethodException extends Exception {
        private static final long serialVersionUID = 1L;

        UnknownMethodException(int method) {
            super("Unknown packet method: " + method);
        }
    }

    static {
       packets[1] = new PingPacket();
       packets[2] = new PongPacket();
       packets[3] = new SuccessPacket();
       packets[5] = new ErrorPacket();
       packets[17] = new RegisterClientPacket();
       packets[18] = new RegisterClientResponsePacket();
       packets[19] = new IdentifyClientPacket();
       packets[20] = new IdentifyClientResponsePacket();
       packets[33] = new CreateQueuePacket();
       packets[34] = new CreateQueueResponsePacket();
       packets[35] = new DeleteQueuePacket();
       packets[37] = new GetQueueIdPacket();
       packets[38] = new GetQueueIdResponsePacket();
       packets[39] = new GetQueueNamePacket();
       packets[40] = new GetQueueNameResponsePacket();
       packets[41] = new GetQueuesPacket();
       packets[42] = new GetQueuesResponsePacket();
       packets[43] = new GetActiveQueuesPacket();
       packets[44] = new GetActiveQueuesResponsePacket();
       packets[45] = new GetMessagesPacket();
       packets[46] = new GetMessagesResponsePacket();
       packets[49] = new PutMessagePacket();
       packets[51] = new ReadMessagePacket();
       packets[52] = new ReadMessageResponsePacket();
       packets[113] = new ComplexTestPacket();
       packets[114] = new MessageTestPacket();
       packets[115] = new QueueTestPacket();}
}