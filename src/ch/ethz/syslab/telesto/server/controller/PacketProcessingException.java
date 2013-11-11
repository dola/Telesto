package ch.ethz.syslab.telesto.server.controller;

import ch.ethz.syslab.telesto.util.ErrorType;

public class PacketProcessingException extends Exception {

    private static final long serialVersionUID = 1L;
    public ErrorType type = ErrorType.INTERNAL_ERROR;

    public PacketProcessingException(String message) {
        super(message);
    }

    public PacketProcessingException(ErrorType type, String message) {
        super(message);
        this.type = type;
    }

    public PacketProcessingException(Throwable e) {
        super(e);
    }

    public PacketProcessingException(ErrorType type, Throwable e) {
        super(e);
        this.type = type;
    }

    public PacketProcessingException(String message, Throwable e) {
        super(message, e);
    }

    public PacketProcessingException(ErrorType type, String message, Throwable e) {
        super(message, e);
        this.type = type;
    }
}