/*
 * Created on Jun 22, 2005
 * Modified on $Date: 2009-10-26 18:37:39 $
 */
package gjb.util.tree;

/**
 * @author gjb
 * @version $Revision: 1.1 $
 * 
 */
public class NodeTransformException extends Exception {

    private static final long serialVersionUID = 1L;
    protected String message;
	protected Exception exception;

    public NodeTransformException(String message, Exception e) {
        this.message = message;
        this.exception = e;
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
