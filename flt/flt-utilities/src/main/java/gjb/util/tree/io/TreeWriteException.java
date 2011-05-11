package gjb.util.tree.io;

import gjb.util.tree.TreeException;

/**
 * Class <code>TreeWriteException</code> is intended to encapsulate
 * any exceptions that are thrown by implementations of the
 * <code>gjb.util.tree.TreeWriter</code> interface.  Since any
 * exception can be thrown this encapsulation is mandatory for
 * generality.
 *
 * @author <a href="mailto:gjb@luc.ac.be">Geert Jan Bex</a>
 * @version 1.0
 */
public class TreeWriteException extends TreeException {

    private static final long serialVersionUID = 1L;
    protected String message;
	protected Exception exception;

	/**
	 * TreeWriteException constructor
	 * @param message the message to be associated with this exception
	 * @param exception the original exception that raised this exception
	 */
	public TreeWriteException(String message, Exception exception) {
		this.message = message;
		this.exception = exception;
	}

	/**
	 * getMessage returns the appropriate error message
	 * @return the error message string
	 */
	public String getMessage() {
		return("Error: " + this.message);
	}

	/**
	 * getException returns the original exception
	 * @return the original exception
	 */
	public Exception getException() {
		return this.exception;
	}

}
