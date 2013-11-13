package ch.ethz.syslab.telesto.client.exception;

import ch.ethz.syslab.telesto.common.util.ErrorType;

public class ProcessingException extends Exception {
    private static final long serialVersionUID = 1L;

    public ErrorType type = ErrorType.INTERNAL_ERROR;

    public ProcessingException(String message) {
        super(message);
    }

    public ProcessingException(ErrorType type, String message) {
        super(message);
        this.type = type;
    }

    public ProcessingException(Throwable e) {
        super(e);
    }

    public ProcessingException(ErrorType type, Throwable e) {
        super(e);
        this.type = type;
    }

    public ProcessingException(String message, Throwable e) {
        super(message, e);
    }

    public ProcessingException(ErrorType type, String message, Throwable e) {
        super(message, e);
        this.type = type;
    }

    public ErrorType getErrorType() {
        return type;
    }
}
