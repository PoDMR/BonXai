/*
 * Created on Aug 5, 2008
 * Modified on $Date: 2009-10-27 14:14:01 $
 */
package gjb.flt.regex.measures;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import gjb.flt.regex.Regex;
import gjb.util.tree.Node;
import gjb.util.tree.SExpressionParseException;
import gjb.util.tree.Tree;

/**
 * @author gjb
 * @version $Revision: 1.1 $
 * 
 */
public class SingleOccurrenceTest implements LanguageTest {

    public boolean test(String regexStr) throws SExpressionParseException {
        Regex regex = new Regex();
        Tree tree = regex.getTree(regexStr);
        Set<String> symbols = new HashSet<String>();
        for (Iterator<Node> it = tree.leaves(); it.hasNext(); ) {
            String symbol = it.next().getKey();
            if (symbols.contains(symbol))
                return false;
            else
                symbols.add(symbol);
        }
        return true;
    }

}
