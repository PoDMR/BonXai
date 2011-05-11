/**
 * Created on Oct 19, 2009
 * Modified on $Date: 2009-10-27 14:14:01 $
 */
package gjb.flt.regex;

import gjb.flt.FLTException;


/**
 * @author lucg5005
 * @version $Revision: 1.1 $
 *
 */
public class RegexException extends FLTException {

    private static final long serialVersionUID = -8742818537244319596L;

	public RegexException(String msg, Exception e) {
		super(msg, e);
	}

	public RegexException(String msg) {
		super(msg);
	}

	public RegexException() {
		super();
	}

}
