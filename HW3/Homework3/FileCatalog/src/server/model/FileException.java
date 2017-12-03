package server.model;

/**
 * Thrown when create, read or delete of an account fails.
 */
public class FileException extends Exception {

    /**
     * Create a new instance thrown because of the specified reason.
     *
     * @param reason Why the exception was thrown.
     */
    public FileException(String reason) {
        super(reason);
    }

    /**
     * Create a new instance thrown because of the specified reason and exception.
     *
     * @param reason Why the exception was thrown.
     * @param rootCause The exception that caused this exception to be thrown.
     */
    public FileException(String reason, Throwable rootCause) {
        super(reason, rootCause);
    }
}
