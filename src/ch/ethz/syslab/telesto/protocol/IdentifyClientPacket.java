

package ch.ethz.syslab.telesto.protocol;

import java.nio.ByteBuffer;

import ch.ethz.syslab.telesto.protocol.model.*;



/* 
 * Do not edit this file! 
 * 
 * Edit the template at tools/protocol/telesto/templates/packet.java instead.
 */
public class IdentifyClientPacket extends Packet {
    public int clientId;

    public IdentifyClientPacket() {
    }
    
    public IdentifyClientPacket(int packetId, int clientId) {
        this.packetId = packetId;
        this.clientId = clientId;
    }

    @Override
    public byte methodId() {
        return 19;
    }

    @Override
    public void emit(ByteBuffer buffer) {
        int lengthIndex = buffer.position();
        buffer.position(lengthIndex + 2);
        buffer.put(methodId());
        buffer.putInt(packetId);
        buffer.putInt(clientId);
        buffer.putShort(lengthIndex, (short) (buffer.position() - lengthIndex - 2));
    }

    @Override
    public void parse(ByteBuffer buffer) {
        packetId = buffer.getInt();
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