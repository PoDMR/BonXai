/**
 * Created on Oct 27, 2009
 * Modified on $Date: 2009-11-09 11:50:32 $
 */
package gjb.flt;

/**
 * @author lucg5005
 * @version $Revision: 1.1 $
 *
 */
public class FLTException extends Exception {

    private static final long serialVersionUID = -2525327856828964002L;
	protected Exception exception;
	protected String msg;

	public FLTException(String msg, Exception e) {
    	this(msg);
    	this.exception = e;
    }

	public FLTException(String msg) {
		this();
		this.msg = msg;
	}

	public FLTException() {
	    super();
    }

	@Override
	public String getMessage() {
		return msg;
	}

	@Override
	public Throwable getCause() {
		return exception;
	}

}
