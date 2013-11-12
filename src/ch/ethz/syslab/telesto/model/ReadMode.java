package ch.ethz.syslab.telesto.model;

public enum ReadMode {
    PRIORITY(1),
    TIME(2);

    private byte byteValue;

    private ReadMode(int b) {
        byteValue = (byte) b;
    }

    public byte getByteValue() {
        return byteValue;
    }
}
