

package ch.ethz.syslab.telesto.common.protocol;

import java.nio.ByteBuffer;

import ch.ethz.syslab.telesto.common.protocol.handler.PacketProcessingException;
import ch.ethz.syslab.telesto.common.protocol.handler.ProtocolHandler;



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
    
    public Packet getHandled(ProtocolHandler handler) throws PacketProcessingException {
        return handler.handle((GetMessagesPacket) this);
    }

    public String toString() {
        return "GetMessagesPacket";
    }
}