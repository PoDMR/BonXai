/**
 * Created on Oct 20, 2009
 * Modified on $Date: 2009-10-27 14:14:01 $
 */
package gjb.flt.regex.measures;

import java.util.Properties;

import gjb.flt.automata.factories.sparse.ThompsonFactory;
import gjb.flt.automata.generators.ShortestStringGenerator;
import gjb.flt.regex.NoRegularExpressionDefinedException;
import gjb.flt.regex.Regex;
import gjb.flt.regex.UnknownOperatorException;
import gjb.util.tree.SExpressionParseException;

/**
 * @author lucg5005
 * @version $Revision: 1.1 $
 *
 */
public class ShortestAcceptedStringMeasure {

	protected Regex regex;

	public ShortestAcceptedStringMeasure(String regexStr)
	        throws SExpressionParseException, UnknownOperatorException {
		this(regexStr, null);
	}
    public ShortestAcceptedStringMeasure(String regexStr, Properties properties)
            throws SExpressionParseException, UnknownOperatorException {
    	this.regex = new Regex(regexStr, properties);
    }

    /**
     * method to calculated the length of the shortest string accepted by the
     * regular expression specified
     * @return int length of the shortest accepted string
     * @throws NoRegularExpressionDefinedException thrown if the Regex was not
     *         created for a specific regular expression
     * @throws UnknownOperatorException 
     */
    public int shortestAcceptedStringLength()
            throws NoRegularExpressionDefinedException, UnknownOperatorException {
        if (regex.getTree() == null)
            throw new NoRegularExpressionDefinedException();
        ThompsonFactory factory = new ThompsonFactory();
        ShortestStringGenerator g = new ShortestStringGenerator(factory.create(regex.getTree()));
        return g.shortestAcceptedStringLength();
    }
    
}
