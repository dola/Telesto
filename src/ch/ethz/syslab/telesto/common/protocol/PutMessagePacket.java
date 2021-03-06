

package ch.ethz.syslab.telesto.common.protocol;

import java.nio.ByteBuffer;

import ch.ethz.syslab.telesto.common.model.Message;
import ch.ethz.syslab.telesto.common.protocol.handler.PacketProcessingException;
import ch.ethz.syslab.telesto.common.protocol.handler.ProtocolHandler;



/* 
 * Do not edit this file! 
 * 
 * Edit the template at tools/protocol/telesto/templates/packet.java instead.
 */
public class PutMessagePacket extends Packet {
    public Message message;
    public int[] additionalQueueIds;

    public PutMessagePacket() {
    }

    public PutMessagePacket(Message message, int[] additionalQueueIds) {
        this.message = message;
        this.additionalQueueIds = additionalQueueIds;
    }
    
    public PutMessagePacket(int packetId, Message message, int[] additionalQueueIds) {
        this.packetId = packetId;
        this.message = message;
        this.additionalQueueIds = additionalQueueIds;
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
        buffer.putInt(additionalQueueIds.length);
        for (int i = 0; i < additionalQueueIds.length; i++) {
            buffer.putInt(additionalQueueIds[i]);
        }
        buffer.putShort(lengthIndex, (short) (buffer.position() - lengthIndex - 2));
    }

    @Override
    public void parse(ByteBuffer buffer) {
        packetId = buffer.getInt();
        message = getMessage(buffer);
        additionalQueueIds = new int[buffer.getInt()];
        for (int i = 0; i < additionalQueueIds.length; i++) {
            additionalQueueIds[i] = buffer.getInt();
        }
    }

    @Override
    public PutMessagePacket newInstance() {
        return new PutMessagePacket();
    }
    
    public Packet getHandled(ProtocolHandler handler) throws PacketProcessingException {
        return handler.handle((PutMessagePacket) this);
    }

    public String toString() {
        return "PutMessagePacket";
    }
}