

package ch.ethz.syslab.telesto.protocol;

import java.nio.ByteBuffer;



/* 
 * Do not edit this file! 
 * 
 * Edit the template at tools/protocol/telesto/templates/packet.java instead.
 */
public class RegisterClientResponsePacket extends Packet {
    public int clientId;

    public RegisterClientResponsePacket() {
    }
    
    public RegisterClientResponsePacket(int packetId, int clientId) {
        this.packetId = packetId;
        this.clientId = clientId;
    }

    @Override
    public byte methodId() {
        return 18;
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
    public RegisterClientResponsePacket newInstance() {
        return new RegisterClientResponsePacket();
    }
    
    public String toString() {
        return "RegisterClientResponsePacket";
    }
}