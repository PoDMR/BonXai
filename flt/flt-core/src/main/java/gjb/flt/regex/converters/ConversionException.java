/**
 * Created on Jun 17, 2009
 * Modified on $Date: 2009-10-27 14:14:01 $
 */
package gjb.flt.regex.converters;

/**
 * @author lucg5005
 * @version $Revision: 1.1 $
 *
 */
public class ConversionException extends RuntimeException {

	private static final long serialVersionUID = 7453053154584753171L;
	protected String msg;
	protected Exception exception;

	public ConversionException(String msg) {
		this.msg = msg;
	}

	public ConversionException(String msg, Exception e) {
		this(msg);
		this.exception = e;
	}

	@Override
	public String getMessage() {
		return msg;
	}

	@Override
	public Exception getCause() {
		return exception;
	}

}
