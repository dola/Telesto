package ch.ethz.syslab.telesto.protocol;

import java.nio.ByteBuffer;

/* 
 * Do not edit this file!
 * 
 * Edit the template at tools/protocol/telesto/templates/packet.java instead
 */
public class PongPacket extends Packet {

    public PongPacket() {
    }
    
    public PongPacket(int messageId) {
        this.messageId = messageId;
    }

    @Override
    public byte methodId() {
        return 0x02;
    }

    @Override
    public void emit(ByteBuffer buffer) {
        int lengthIndex = buffer.position();
        buffer.position(lengthIndex + 2);
        buffer.put(methodId());
        buffer.putInt(messageId);
        buffer.putShort(lengthIndex, (short) (buffer.position() - lengthIndex - 2));
    }

    @Override
    public void parse(ByteBuffer buffer) {
        messageId = buffer.getInt();
    }

    @Override
    public PongPacket newInstance() {
        return new PongPacket();
    }
    
    public String toString() {
        return "PongPacket";
    }
}
