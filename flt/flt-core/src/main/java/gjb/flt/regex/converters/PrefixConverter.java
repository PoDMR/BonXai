/**
 * Created on Jun 17, 2009
 * Modified on $Date: 2009-10-27 14:14:01 $
 */
package gjb.flt.regex.converters;

import gjb.flt.regex.Regex;
import gjb.flt.regex.UnknownOperatorException;
import gjb.util.tree.SExpressionParseException;

/**
 * @author lucg5005
 * @version $Revision: 1.1 $
 *
 */
public class PrefixConverter implements Converter {

	/* (non-Javadoc)
	 * @see gjb.util.regex.converters.Converter#convert(gjb.util.regex.Regex)
	 */
	public String convert(Regex regex) {
		return regex.toString();
	}

	/* (non-Javadoc)
	 * @see gjb.util.regex.converters.Converter#convert(java.lang.String)
	 */
	public String convert(String regexStr) throws SExpressionParseException,
			UnknownOperatorException {
    	Regex regex = new Regex(regexStr);
    	return convert(regex);
	}

}
