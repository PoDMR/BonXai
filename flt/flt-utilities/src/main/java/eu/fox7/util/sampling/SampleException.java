/*
 * Created on Jan 27, 2006
 * Modified on $Date: 2009-10-29 13:34:16 $
 */
package eu.fox7.util.sampling;

/**
 * @author eu.fox7
 * @version $Revision: 1.2 $
 * 
 */
public class SampleException extends Exception {

    private static final long serialVersionUID = -4437390135106106641L;
	protected String msg;
    protected Exception exception;

    public SampleException(Exception e) {
        this.exception = e;
    }

    public SampleException(String msg) {
	    this.msg = msg;
    }

	public Exception getException() {
        return exception;
    }

	@Override
	public String getMessage() {
		return msg;
	}

}
