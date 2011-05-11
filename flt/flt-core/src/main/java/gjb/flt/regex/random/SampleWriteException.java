/*
 * Created on May 30, 2007
 * Modified on $Date: 2009-10-27 14:14:01 $
 */
package gjb.flt.regex.random;

/**
 * @author gjb
 * @version $Revision: 1.1 $
 * 
 */
public class SampleWriteException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * exception message
     */
    protected String message;
    
    /**
     * original exception
     */
    protected Exception exception;
    
    /**
     * Constructor for the exception
     * @param message string to indicate the origine of the Exception
     * @param exception original Exception that triggered this one
     */
    public SampleWriteException(String message, Exception exception) {
        this.message = message;
        this.exception = exception;
    }
    
    /**
     * getMessage returns the appropriate error message
     * @return the error message string
     */
    public String getMessage() {
        return("Exception: " + this.message);
    }

    /**
     * getException returns the original exception
     * @return the original exception
     */
    public Exception getException() {
        return this.exception;
    }

}
