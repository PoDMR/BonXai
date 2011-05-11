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
public class FLTRuntimeException extends RuntimeException {

    private static final long serialVersionUID = -777950789808965783L;
	protected Exception exception;
	protected String msg;

	public FLTRuntimeException(String msg, Exception e) {
    	this(msg);
    	this.exception = e;
    }

	public FLTRuntimeException(String msg) {
		this();
		this.msg = msg;
	}

	public FLTRuntimeException() {
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
