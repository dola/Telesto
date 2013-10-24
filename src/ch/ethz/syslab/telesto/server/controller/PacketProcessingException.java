package ch.ethz.syslab.telesto.server.controller;

public class PacketProcessingException extends Exception {

    private static final long serialVersionUID = 1L;

    public PacketProcessingException(String message) {
        super(message);
    }

    public PacketProcessingException(Throwable e) {
        super(e);
    }

    public PacketProcessingException(String message, Throwable e) {
        super(message, e);
    }
}
