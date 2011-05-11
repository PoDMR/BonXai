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
public class InsufficientDataException extends InappropriateTestException {

    private static final long serialVersionUID = -5794755517532376926L;

    public InsufficientDataException(String message) {
    	super(message);
    }

    public InsufficientDataException(Throwable throwable) {
    	super(throwable);
    }

}
