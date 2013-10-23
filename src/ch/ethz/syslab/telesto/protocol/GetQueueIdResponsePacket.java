package ch.ethz.syslab.telesto.protocol;

import java.nio.ByteBuffer;

/* 
 * Do not edit this file!
 * 
 * Edit the template at tools/protocol/telesto/templates/packet.java instead
 */
public class GetQueueIdResponsePacket extends Packet {
    public int queueId;

    public GetQueueIdResponsePacket() {
    }
    
    public GetQueueIdResponsePacket(int messageId, int queueId) {
        this.messageId = messageId;
        this.queueId = queueId;
    }

    @Override
    public byte methodId() {
        return 0x26;
    }

    @Override
    public void emit(ByteBuffer buffer) {
        int lengthIndex = buffer.position();
        buffer.position(lengthIndex + 2);
        buffer.put(methodId());
        buffer.putInt(messageId);
        buffer.putInt(queueId);
        buffer.putShort(lengthIndex, (short) (buffer.position() - lengthIndex - 2));
    }

    @Override
    public void parse(ByteBuffer buffer) {
        messageId = buffer.getInt();
        queueId = buffer.getInt();
    }

    @Override
    public GetQueueIdResponsePacket newInstance() {
        return new GetQueueIdResponsePacket();
    }
    
    public String toString() {
        return "GetQueueIdResponsePacket";
    }
}
