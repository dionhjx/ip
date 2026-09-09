package chudgpt.exception;

/** Exceptions that are caused when using ChudGPT */
public class ChudException extends Exception{
    /**
     * Creates a ChudException
     *
     * @param message the error message
     */
    public ChudException(String message) {
        super(message);
    }
}
