/**
 * Created on Oct 20, 2009
 * Modified on $Date: 2009-10-27 14:14:01 $
 */
package eu.fox7.flt.regex.matchers;

import java.util.Properties;

import eu.fox7.flt.automata.factories.sparse.ThompsonFactory;
import eu.fox7.flt.automata.matchers.NFAMatcher;
import eu.fox7.flt.regex.NoRegularExpressionDefinedException;
import eu.fox7.flt.regex.Regex;
import eu.fox7.flt.regex.UnknownOperatorException;
import eu.fox7.util.tree.SExpressionParseException;


/**
 * @author lucg5005
 * @version $Revision: 1.1 $
 *
 */
public class Matcher {

	protected Regex regex;
	protected String regexStr;

	public Matcher(String regexStr)
	        throws SExpressionParseException, UnknownOperatorException {
		this(regexStr, null);
	}

	public Matcher(String regexStr, Properties properties)
	        throws SExpressionParseException, UnknownOperatorException {
		this.regexStr = regexStr;
		this.regex = new Regex(regexStr, properties);
	}

	public Matcher(Regex regex) {
		this.regex = regex;
		this.regexStr = regex.toString();
	}

	public String getRegexStr() {
		return regexStr;
	}

	public Regex getRegex() {
		return regex;
	}

	/**
	 * method to check whether the given string of symbols is matched by the regular
	 * expression
	 * @param str String[] with the string representations of the string's symbols
	 * @return boolean true if the given string matches the regular expression, false
	 *         otherwise
	 * @throws NoRegularExpressionDefinedException thrown if the Regex was not created
	 * for a specific regular expression
	 * @throws UnknownOperatorException 
	 */
	public boolean matches(String[] str)
	        throws NoRegularExpressionDefinedException, UnknownOperatorException {
	    if (getRegex().getTree() == null)
	        throw new NoRegularExpressionDefinedException();
	    ThompsonFactory factory = new ThompsonFactory();
	    NFAMatcher matcher = new NFAMatcher(factory.create(getRegex().getTree()));
	    return matcher.matches(str);
	}

	@Override
	public String toString() {
		return "matcher for " + getRegexStr();
	}

}
