package ch.ethz.syslab.telesto.protocol;

import java.nio.ByteBuffer;

/* 
 * Do not edit this file!
 * 
 * Edit the template at tools/protocol/telesto/templates/packet.java instead
 */
public class SuccessPacket extends Packet {

    public SuccessPacket() {
    }
    
    public SuccessPacket(int messageId) {
        this.messageId = messageId;
    }

    @Override
    public byte methodId() {
        return 0x03;
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
    public SuccessPacket newInstance() {
        return new SuccessPacket();
    }
    
    public String toString() {
        return "SuccessPacket";
    }
}
