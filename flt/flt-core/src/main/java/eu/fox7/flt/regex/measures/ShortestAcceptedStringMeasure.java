/**
 * Created on Oct 20, 2009
 * Modified on $Date: 2009-10-27 14:14:01 $
 */
package eu.fox7.flt.regex.measures;

import java.util.Properties;

import eu.fox7.flt.automata.factories.sparse.ThompsonFactory;
import eu.fox7.flt.automata.generators.ShortestStringGenerator;
import eu.fox7.flt.regex.NoRegularExpressionDefinedException;
import eu.fox7.flt.regex.Regex;
import eu.fox7.flt.regex.UnknownOperatorException;
import eu.fox7.util.tree.SExpressionParseException;


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
