/**
 * Created on Jun 17, 2009
 * Modified on $Date: 2009-10-27 14:14:01 $
 */
package eu.fox7.flt.regex.converters;

import eu.fox7.flt.regex.Regex;
import eu.fox7.flt.regex.UnknownOperatorException;
import eu.fox7.util.tree.SExpressionParseException;

/**
 * @author lucg5005
 * @version $Revision: 1.1 $
 *
 */
public class PrefixConverter implements Converter {

	/* (non-Javadoc)
	 * @see eu.fox7.util.regex.converters.Converter#convert(eu.fox7.util.regex.Regex)
	 */
	public String convert(Regex regex) {
		return regex.toString();
	}

	/* (non-Javadoc)
	 * @see eu.fox7.util.regex.converters.Converter#convert(java.lang.String)
	 */
	public String convert(String regexStr) throws SExpressionParseException,
			UnknownOperatorException {
    	Regex regex = new Regex(regexStr);
    	return convert(regex);
	}

}
