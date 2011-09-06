/*
 * Created on Sep 13, 2004
 *
 */
package eu.fox7.flt.automata.io;

import eu.fox7.flt.automata.NFAException;

/**
 * Exception thrown if something goes wrong when performing a write
 * operation with an implementation of the NFAWriter interface.  It embeds
 * the Exception originally raised as well as a message text.
 * 
 * @author eu.fox7
 * @version $Revision: 1.1 $
 */
public class NFAWriteException extends NFAException {

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
    public NFAWriteException(String message, Exception exception) {
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
