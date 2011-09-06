/*
 * Created on Aug 5, 2008
 * Modified on $Date: 2009-10-27 14:14:01 $
 */
package eu.fox7.flt.regex.measures;

import eu.fox7.flt.automata.NFAException;
import eu.fox7.flt.regex.RegexException;
import eu.fox7.util.tree.SExpressionParseException;

/**
 * @author eu.fox7
 * @version $Revision: 1.1 $
 * 
 */
public interface LanguageTest {

    public boolean test(String regexStr)
            throws NFAException, RegexException, SExpressionParseException;

}
