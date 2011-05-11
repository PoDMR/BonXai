/**
 * Created on Apr 3, 2009
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
public interface Normalizer {

	public Regex normalize(Regex regex);
	public String normalize(String regexStr)
	    throws SExpressionParseException, UnknownOperatorException;

}
