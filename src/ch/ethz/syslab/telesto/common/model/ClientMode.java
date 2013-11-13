package ch.ethz.syslab.telesto.common.model;

public enum ClientMode {
    FULL(1),
    READ_ONLY(2);

    private byte byteValue;
    private static ClientMode[] modes = new ClientMode[127];

    private ClientMode(int b) {
        byteValue = (byte) b;
    }

    public byte getByteValue() {
        return byteValue;
    }

    public static ClientMode fromByteValue(int b) {
        return modes[b];
    }

    static {
        for (ClientMode mode : values()) {
            modes[mode.byteValue] = mode;
        }
    }
}
