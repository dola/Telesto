

package ch.ethz.syslab.telesto.common.protocol;

import java.nio.ByteBuffer;

import ch.ethz.syslab.telesto.common.protocol.handler.PacketProcessingException;
import ch.ethz.syslab.telesto.common.protocol.handler.ProtocolHandler;



/* 
 * Do not edit this file! 
 * 
 * Edit the template at tools/protocol/telesto/templates/packet.java instead.
 */
public class DeleteQueuePacket extends Packet {
    public int queueId;

    public DeleteQueuePacket() {
    }

    public DeleteQueuePacket(int queueId) {
        this.queueId = queueId;
    }
    
    public DeleteQueuePacket(int packetId, int queueId) {
        this.packetId = packetId;
        this.queueId = queueId;
    }

    @Override
    public byte methodId() {
        return 35;
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
    public DeleteQueuePacket newInstance() {
        return new DeleteQueuePacket();
    }
    
    public Packet getHandled(ProtocolHandler handler) throws PacketProcessingException {
        return handler.handle((DeleteQueuePacket) this);
    }

    public String toString() {
        return "DeleteQueuePacket";
    }
}