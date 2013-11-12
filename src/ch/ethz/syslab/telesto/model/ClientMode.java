package ch.ethz.syslab.telesto.model;

public enum ClientMode {
    FULL(1),
    READ_ONLY(2);

    private byte byteValue;

    private ClientMode(int b) {
        byteValue = (byte) b;
    }

    public byte getByteValue() {
        return byteValue;
    }
}
