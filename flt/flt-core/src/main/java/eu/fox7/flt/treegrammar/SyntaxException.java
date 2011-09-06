/*
 * Created on Jun 24, 2005
 * Modified on $Date: 2009-10-29 13:35:07 $
 */
package eu.fox7.flt.treegrammar;

/**
 * <p> Exception representing a syntax error in the textual representation of
 * generators, distributions or grammar rules.  Chiefl used to encapsulate the
 * actual Exception raised. </p>
 * @author eu.fox7
 * @version $Revision: 1.1 $
 * 
 */
public class SyntaxException extends Exception {

    /**
     * for serialization
     */
    private static final long serialVersionUID = 1L;
    protected String message;
    protected Exception exception;

    /**
     * Constructor with a message
     * @param message String
     */
    public SyntaxException(String message) {
        this.message = message;
    }

    /**
     * Constructor with a message and an Exception
     * @param message
     * @param exception
     */
    public SyntaxException(String message, Exception exception) {
        this(message);
        this.exception = exception;
    }

    /**
     * Method to retrieve the exception's message
     * @return String message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Method to retrieve the Exception that is encapsulated
     * @return Exception
     */
    public Exception getException() {
        return exception;
    }

}
