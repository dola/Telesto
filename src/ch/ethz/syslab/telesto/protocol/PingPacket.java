package ch.ethz.syslab.telesto.protocol;

import java.nio.ByteBuffer;

public class PingPacket extends Packet {

    public PingPacket() {
    }
    
    public PingPacket(int messageId) {
        this.messageId = messageId;
    }

    @Override
    public byte methodId() {
        return 0x01;
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
    public PingPacket newInstance() {
        return new PingPacket();
    }
    
    public String toString() {
        return "PingPacket";
    }
}
