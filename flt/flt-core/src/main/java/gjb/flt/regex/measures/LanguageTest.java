/*
 * Created on Aug 5, 2008
 * Modified on $Date: 2009-10-27 14:14:01 $
 */
package gjb.flt.regex.measures;

import gjb.flt.automata.NFAException;
import gjb.flt.regex.RegexException;
import gjb.util.tree.SExpressionParseException;

/**
 * @author gjb
 * @version $Revision: 1.1 $
 * 
 */
public interface LanguageTest {

    public boolean test(String regexStr)
            throws NFAException, RegexException, SExpressionParseException;

}
