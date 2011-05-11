/*
 * Created on Feb 26, 2009
 * Modified on $Date: 2009-10-26 18:37:39 $
 */
package gjb.math;

/**
 * @author gjb
 * @version $Revision: 1.1 $
 * 
 */
public class InappropriateTestException extends Exception {

	private static final long serialVersionUID = -3297144735664530611L;
	protected String message;
	protected Throwable throwable;

    public InappropriateTestException(String message) {
    	this.message = message;
    }

    public InappropriateTestException(Throwable throwable) {
    	this.throwable = throwable;
    }

    @Override
    public String getMessage() {
    	return message;
    }

    @Override
    public Throwable getCause() {
    	return throwable;
    }

}
