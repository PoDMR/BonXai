/**
 * Created on Oct 27, 2009
 * Modified on $Date: 2009-10-27 09:30:13 $
 */
package eu.fox7.flt.regex.infer;

import eu.fox7.flt.FLTException;

/**
 * @author lucg5005
 * @version $Revision: 1.1 $
 *
 */
public class InferenceException extends FLTException {

    private static final long serialVersionUID = 1760716643709622893L;

	public InferenceException(String msg, Exception e) {
		super(msg, e);
	}

	public InferenceException(String msg) {
	    super(msg);
    }

	public InferenceException() {
		super();
	}

}
