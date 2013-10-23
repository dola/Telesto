package ch.ethz.syslab.telesto.protocol;

import java.nio.ByteBuffer;

/* 
 * Do not edit this file!
 * 
 * Edit the template at tools/protocol/telesto/templates/packet.java instead
 */
public class IdentifyClientPacket extends Packet {
    public int clientId;

    public IdentifyClientPacket() {
    }
    
    public IdentifyClientPacket(int messageId, int clientId) {
        this.messageId = messageId;
        this.clientId = clientId;
    }

    @Override
    public byte methodId() {
        return 0x13;
    }

    @Override
    public void emit(ByteBuffer buffer) {
        int lengthIndex = buffer.position();
        buffer.position(lengthIndex + 2);
        buffer.put(methodId());
        buffer.putInt(messageId);
        buffer.putInt(clientId);
        buffer.putShort(lengthIndex, (short) (buffer.position() - lengthIndex - 2));
    }

    @Override
    public void parse(ByteBuffer buffer) {
        messageId = buffer.getInt();
        clientId = buffer.getInt();
    }

    @Override
    public IdentifyClientPacket newInstance() {
        return new IdentifyClientPacket();
    }
    
    public String toString() {
        return "IdentifyClientPacket";
    }
}
