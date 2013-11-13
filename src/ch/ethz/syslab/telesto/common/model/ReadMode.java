package ch.ethz.syslab.telesto.common.model;

public enum ReadMode {
    PRIORITY(1),
    TIME(2);

    private byte byteValue;
    private static ReadMode[] modes = new ReadMode[127];

    private ReadMode(int b) {
        byteValue = (byte) b;
    }

    public byte getByteValue() {
        return byteValue;
    }

    public static ReadMode fromByteValue(int b) {
        return modes[b];
    }

    static {
        for (ReadMode mode : values()) {
            modes[mode.byteValue] = mode;
        }
    }
}
