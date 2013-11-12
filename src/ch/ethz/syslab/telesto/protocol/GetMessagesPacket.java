

package ch.ethz.syslab.telesto.protocol;

import java.nio.ByteBuffer;



/* 
 * Do not edit this file! 
 * 
 * Edit the template at tools/protocol/telesto/templates/packet.java instead.
 */
public class GetMessagesPacket extends Packet {
    public int queueId;

    public GetMessagesPacket() {
    }

    public GetMessagesPacket(int queueId) {
        this.queueId = queueId;
    }
    
    public GetMessagesPacket(int packetId, int queueId) {
        this.packetId = packetId;
        this.queueId = queueId;
    }

    @Override
    public byte methodId() {
        return 45;
    }

    @Override
    public void emit(ByteBuffer buffer) {
        int lengthIndex = buffer.position();
        buffer.position(lengthIndex + 2);
        buffer.put(methodId());
        buffer.putInt(packetId);
        buffer.putInt(queueId);
        buffer.putShort(lengthIndex, (short) (buffer.position() - lengthIndex - 2));
    }

    @Override
    public void parse(ByteBuffer buffer) {
        packetId = buffer.getInt();
        queueId = buffer.getInt();
    }

    @Override
    public GetMessagesPacket newInstance() {
        return new GetMessagesPacket();
    }
    
    public String toString() {
        return "GetMessagesPacket";
    }
}