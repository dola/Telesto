

package ch.ethz.syslab.telesto.common.protocol;

import java.nio.ByteBuffer;

import ch.ethz.syslab.telesto.common.protocol.handler.PacketProcessingException;
import ch.ethz.syslab.telesto.common.protocol.handler.ProtocolHandler;



/* 
 * Do not edit this file! 
 * 
 * Edit the template at tools/protocol/telesto/templates/packet.java instead.
 */
public class ReadMessagePacket extends Packet {
    public int queueId;
    public int senderId;
    public byte mode;

    public ReadMessagePacket() {
    }

    public ReadMessagePacket(int queueId, int senderId, byte mode) {
        this.queueId = queueId;
        this.senderId = senderId;
        this.mode = mode;
    }
    
    public ReadMessagePacket(int packetId, int queueId, int senderId, byte mode) {
        this.packetId = packetId;
        this.queueId = queueId;
        this.senderId = senderId;
        this.mode = mode;
    }

    @Override
    public byte methodId() {
        return 51;
    }

    @Override
    public void emit(ByteBuffer buffer) {
        int lengthIndex = buffer.position();
        buffer.position(lengthIndex + 2);
        buffer.put(methodId());
        buffer.putInt(packetId);
        buffer.putInt(queueId);
        buffer.putInt(senderId);
        buffer.put(mode);
        buffer.putShort(lengthIndex, (short) (buffer.position() - lengthIndex - 2));
    }

    @Override
    public void parse(ByteBuffer buffer) {
        packetId = buffer.getInt();
        queueId = buffer.getInt();
        senderId = buffer.getInt();
        mode = buffer.get();
    }

    @Override
    public ReadMessagePacket newInstance() {
        return new ReadMessagePacket();
    }
    
    public Packet getHandled(ProtocolHandler handler) throws PacketProcessingException {
        return handler.handle((ReadMessagePacket) this);
    }

    public String toString() {
        return "ReadMessagePacket";
    }
}