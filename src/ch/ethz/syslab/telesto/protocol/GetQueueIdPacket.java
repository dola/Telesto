package ch.ethz.syslab.telesto.protocol;

import java.nio.ByteBuffer;

/* 
 * Do not edit this file!
 * 
 * Edit the template at tools/protocol/telesto/templates/packet.java instead
 */
public class GetQueueIdPacket extends Packet {
    public String name;

    public GetQueueIdPacket() {
    }
    
    public GetQueueIdPacket(int messageId, String name) {
        this.messageId = messageId;
        this.name = name;
    }

    @Override
    public byte methodId() {
        return 0x25;
    }

    @Override
    public void emit(ByteBuffer buffer) {
        int lengthIndex = buffer.position();
        buffer.position(lengthIndex + 2);
        buffer.put(methodId());
        buffer.putInt(messageId);
        putString(buffer, name);
        buffer.putShort(lengthIndex, (short) (buffer.position() - lengthIndex - 2));
    }

    @Override
    public void parse(ByteBuffer buffer) {
        messageId = buffer.getInt();
        name = getString(buffer);
    }

    @Override
    public GetQueueIdPacket newInstance() {
        return new GetQueueIdPacket();
    }
    
    public String toString() {
        return "GetQueueIdPacket";
    }
}
