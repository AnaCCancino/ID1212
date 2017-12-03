package server.model;

/**
 * Thrown when a user tries to access and it is not authorized.
 */
public class PersonException extends Exception {

    /**
     * Create a new instance thrown because of the specified reason.
     *
     * @param reason Why the exception was thrown.
     */
    public PersonException(String reason){
        super(reason);
    }

    /**
     * Create a new instance thrown because of the specified reason and exception.
     *
     * @param reason Why the exception was thrown.
     * @param rootCause The exception that caused this exception to be thrown.
     */
    public PersonException(String reason, Throwable rootCause){
        super(reason,rootCause);
    }
}
