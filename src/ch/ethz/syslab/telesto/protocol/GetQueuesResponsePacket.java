

package ch.ethz.syslab.telesto.protocol;

import java.nio.ByteBuffer;

import ch.ethz.syslab.telesto.protocol.model.*;



/* 
 * Do not edit this file! 
 * 
 * Edit the template at tools/protocol/telesto/templates/packet.java instead.
 */
public class GetQueuesResponsePacket extends Packet {
    public int[] queues;

    public GetQueuesResponsePacket() {
    }
    
    public GetQueuesResponsePacket(int packetId, int[] queues) {
        this.packetId = packetId;
        this.queues = queues;
    }

    @Override
    public byte methodId() {
        return 42;
    }

    @Override
    public void emit(ByteBuffer buffer) {
        int lengthIndex = buffer.position();
        buffer.position(lengthIndex + 2);
        buffer.put(methodId());
        buffer.putInt(packetId);
        buffer.putInt(queues.length);
        for (int i = 0; i < queues.length; i++) {
            buffer.putInt(queues[i]);
        }
        buffer.putShort(lengthIndex, (short) (buffer.position() - lengthIndex - 2));
    }

    @Override
    public void parse(ByteBuffer buffer) {
        packetId = buffer.getInt();
        queues = new int[buffer.getInt()];
        for (int i = 0; i < queues.length; i++) {
            queues[i] = buffer.getInt();
        }
    }

    @Override
    public GetQueuesResponsePacket newInstance() {
        return new GetQueuesResponsePacket();
    }
    
    public String toString() {
        return "GetQueuesResponsePacket";
    }
}