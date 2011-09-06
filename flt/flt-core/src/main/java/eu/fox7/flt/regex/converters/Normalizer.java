/**
 * Created on Apr 3, 2009
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
public interface Normalizer {

	public Regex normalize(Regex regex);
	public String normalize(String regexStr)
	    throws SExpressionParseException, UnknownOperatorException;

}
