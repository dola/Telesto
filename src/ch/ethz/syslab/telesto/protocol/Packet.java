package ch.ethz.syslab.telesto.protocol;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

public abstract class Packet {
    public static final Charset CHARSET = Charset.forName("UTF-8");
    public static Packet[] packets = new Packet[Byte.MAX_VALUE + 1];

    public int messageId;

    public abstract void emit(ByteBuffer buffer);

    public abstract void parse(ByteBuffer buffer);

    public abstract Packet newInstance();

    public abstract byte methodId();

    public static Packet create(ByteBuffer buffer) throws UnknownMethodException {
        int method = buffer.get();

        if (method > Byte.MAX_VALUE || packets[method] == null) {
            throw new UnknownMethodException(method);
        }

        Packet packet = packets[method].newInstance();
        packet.parse(buffer);
        return packet;
    }

    protected static String getString(ByteBuffer buffer) {
        byte[] bytes = new byte[buffer.getShort()];
        buffer.get(bytes);
        return new String(bytes);
    }

    protected static void putString(ByteBuffer buffer, String value) {
        byte[] bytes = value.getBytes(CHARSET);
        buffer.putShort((short) bytes.length);
        buffer.put(bytes);
    }

    protected static boolean getBoolean(ByteBuffer buffer) {
        return buffer.get() == 1;
    }

    protected static void putBoolean(ByteBuffer buffer, boolean value) {
        buffer.put((byte) (value ? 1 : 0));
    }

    public static class UnknownMethodException extends Exception {
        private static final long serialVersionUID = 1L;

        UnknownMethodException(int method) {
            super("Unknown packet method: " + method);
        }
    }

    static {
        packets[1] = new PingPacket();
        packets[127] = new ComplexPacket();
    }
}
