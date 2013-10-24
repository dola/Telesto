

package ch.ethz.syslab.telesto.protocol;

import java.nio.ByteBuffer;

import ch.ethz.syslab.telesto.protocol.model.*;



/* 
 * Do not edit this file! 
 * 
 * Edit the template at tools/protocol/telesto/templates/packet.java instead.
 */
public class GetActiveQueuesResponsePacket extends Packet {

    public GetActiveQueuesResponsePacket() {
    }
    
    public GetActiveQueuesResponsePacket(int packetId) {
        this.packetId = packetId;
    }

    @Override
    public byte methodId() {
        return 44;
    }

    @Override
    public void emit(ByteBuffer buffer) {
        int lengthIndex = buffer.position();
        buffer.position(lengthIndex + 2);
        buffer.put(methodId());
        buffer.putInt(packetId);
        buffer.putShort(lengthIndex, (short) (buffer.position() - lengthIndex - 2));
    }

    @Override
    public void parse(ByteBuffer buffer) {
        packetId = buffer.getInt();
    }

    @Override
    public GetActiveQueuesResponsePacket newInstance() {
        return new GetActiveQueuesResponsePacket();
    }
    
    public String toString() {
        return "GetActiveQueuesResponsePacket";
    }
}