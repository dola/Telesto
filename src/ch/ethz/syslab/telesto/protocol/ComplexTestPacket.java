

package ch.ethz.syslab.telesto.protocol;

import java.nio.ByteBuffer;

import ch.ethz.syslab.telesto.protocol.model.*;



/* 
 * Do not edit this file! 
 * 
 * Edit the template at tools/protocol/telesto/templates/packet.java instead.
 */
public class ComplexTestPacket extends Packet {
    public byte byteField;
    public boolean booleanField;
    public short shortField;
    public int intField;
    public long longField;
    public float floatField;
    public double doubleField;
    public String stringField;

    public ComplexTestPacket() {
    }
    
    public ComplexTestPacket(int packetId, byte byteField, boolean booleanField, short shortField, int intField, long longField, float floatField, double doubleField, String stringField) {
        this.packetId = packetId;
        this.byteField = byteField;
        this.booleanField = booleanField;
        this.shortField = shortField;
        this.intField = intField;
        this.longField = longField;
        this.floatField = floatField;
        this.doubleField = doubleField;
        this.stringField = stringField;
    }

    @Override
    public byte methodId() {
        return 113;
    }

    @Override
    public void emit(ByteBuffer buffer) {
        int lengthIndex = buffer.position();
        buffer.position(lengthIndex + 2);
        buffer.put(methodId());
        buffer.putInt(packetId);
        buffer.put(byteField);
        putBoolean(buffer, booleanField);
        buffer.putShort(shortField);
        buffer.putInt(intField);
        buffer.putLong(longField);
        buffer.putFloat(floatField);
        buffer.putDouble(doubleField);
        putString(buffer, stringField);
        buffer.putShort(lengthIndex, (short) (buffer.position() - lengthIndex - 2));
    }

    @Override
    public void parse(ByteBuffer buffer) {
        packetId = buffer.getInt();
        byteField = buffer.get();
        booleanField = getBoolean(buffer);
        shortField = buffer.getShort();
        intField = buffer.getInt();
        longField = buffer.getLong();
        floatField = buffer.getFloat();
        doubleField = buffer.getDouble();
        stringField = getString(buffer);
    }

    @Override
    public ComplexTestPacket newInstance() {
        return new ComplexTestPacket();
    }
    
    public String toString() {
        return "ComplexTestPacket";
    }
}