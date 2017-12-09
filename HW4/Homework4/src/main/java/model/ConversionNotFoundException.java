package model;

/**
 * Thrown when the conversion is not found.
 */
public class ConversionNotFoundException extends Exception {
    /**
     * Creates an instance representing the specified exception situation.
     * 
     * @param msg A description of the exception.
     */
    public ConversionNotFoundException(String msg) {
        super(msg);
    }

}
