

package ch.ethz.syslab.telesto.protocol;

import java.nio.ByteBuffer;



/* 
 * Do not edit this file! 
 * 
 * Edit the template at tools/protocol/telesto/templates/packet.java instead.
 */
public class CreateQueuePacket extends Packet {
    public String name;

    public CreateQueuePacket() {
    }

    public CreateQueuePacket(String name) {
        this.name = name;
    }
    
    public CreateQueuePacket(int packetId, String name) {
        this.packetId = packetId;
        this.name = name;
    }

    @Override
    public byte methodId() {
        return 33;
    }

    @Override
    public void emit(ByteBuffer buffer) {
        int lengthIndex = buffer.position();
        buffer.position(lengthIndex + 2);
        buffer.put(methodId());
        buffer.putInt(packetId);
        putString(buffer, name);
        buffer.putShort(lengthIndex, (short) (buffer.position() - lengthIndex - 2));
    }

    @Override
    public void parse(ByteBuffer buffer) {
        packetId = buffer.getInt();
        name = getString(buffer);
    }

    @Override
    public CreateQueuePacket newInstance() {
        return new CreateQueuePacket();
    }
    
    public String toString() {
        return "CreateQueuePacket";
    }
}