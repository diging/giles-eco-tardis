package edu.asu.diging.tardis.core.exception;

public class InnogenScriptRunnerException extends Exception {

    private static final long serialVersionUID = 2353695957253007231L;
    
    public InnogenScriptRunnerException() {
        super();
    }

    public InnogenScriptRunnerException(String message, Throwable cause,
            boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public InnogenScriptRunnerException(String message, Throwable cause) {
        super(message, cause);
    }

    public InnogenScriptRunnerException(String message) {
        super(message);
    }

    public InnogenScriptRunnerException(Throwable cause) {
        super(cause);
    }
}
