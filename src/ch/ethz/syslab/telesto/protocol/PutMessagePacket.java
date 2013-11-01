

package ch.ethz.syslab.telesto.protocol;

import java.nio.ByteBuffer;

import ch.ethz.syslab.telesto.model.Message;


/* 
 * Do not edit this file! 
 * 
 * Edit the template at tools/protocol/telesto/templates/packet.java instead.
 */
public class PutMessagePacket extends Packet {
    public Message message;

    public PutMessagePacket() {
    }

    public PutMessagePacket(Message message) {
        this.message = message;
    }
    
    public PutMessagePacket(int packetId, Message message) {
        this.packetId = packetId;
        this.message = message;
    }

    @Override
    public byte methodId() {
        return 49;
    }

    @Override
    public void emit(ByteBuffer buffer) {
        int lengthIndex = buffer.position();
        buffer.position(lengthIndex + 2);
        buffer.put(methodId());
        buffer.putInt(packetId);
        putMessage(buffer, message);
        buffer.putShort(lengthIndex, (short) (buffer.position() - lengthIndex - 2));
    }

    @Override
    public void parse(ByteBuffer buffer) {
        packetId = buffer.getInt();
        message = getMessage(buffer);
    }

    @Override
    public PutMessagePacket newInstance() {
        return new PutMessagePacket();
    }
    
    public String toString() {
        return "PutMessagePacket";
    }
}