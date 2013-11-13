package ch.ethz.syslab.telesto.common.util;

public enum ErrorType {
    INTERNAL_ERROR,
    UNEXPECTED_PACKET,
    IO_ERROR,
    UNIQUE_CONSTRAINT,
    QUEUE_NAME_NOT_UNIQUE,
    CLIENT_NAME_NOT_UNIQUE,
    QUEUE_NOT_EXISTING;
}
