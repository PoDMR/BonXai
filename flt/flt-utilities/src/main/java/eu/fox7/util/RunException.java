/**
 * Created on Sep 24, 2009
 * Modified on $Date: 2009-10-26 18:37:39 $
 */
package eu.fox7.util;

/**
 * @author lucg5005
 * @version $Revision: 1.1 $
 *
 */
public class RunException extends RuntimeException {

    private static final long serialVersionUID = -3439695592996196479L;
	protected Exception exception;

	public Exception getException() {
    	return exception;
    }

	public RunException(Exception e) {
	    this.exception = e;
    }

}
