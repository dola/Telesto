package ch.ethz.syslab.telesto.protocol;

import java.nio.ByteBuffer;

public class ComplexPacket extends Packet {
    public byte byteField;
    public boolean booleanField;
    public short shortField;
    public int intField;
    public long longField;
    public float floatField;
    public double doubleField;
    public String stringField;

    public ComplexPacket() {
    }
    
    public ComplexPacket(int messageId, byte byteField, boolean booleanField, short shortField, int intField, long longField, float floatField, double doubleField, String stringField) {
        this.messageId = messageId;
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
        return 0x7f;
    }

    @Override
    public void emit(ByteBuffer buffer) {
        int lengthIndex = buffer.position();
        buffer.position(lengthIndex + 2);
        buffer.put(methodId());
        buffer.putInt(messageId);
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
        messageId = buffer.getInt();
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
    public ComplexPacket newInstance() {
        return new ComplexPacket();
    }
    
    public String toString() {
        return "ComplexPacket";
    }
}
