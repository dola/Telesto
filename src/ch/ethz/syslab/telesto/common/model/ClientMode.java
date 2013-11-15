package ch.ethz.syslab.telesto.common.model;

/**
 * The mode in which the client operates.
 * 
 */
public enum ClientMode {
    /**
     * All permissions
     */
    FULL(1),
    /**
     * Not allowed to put messages in queues
     */
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
