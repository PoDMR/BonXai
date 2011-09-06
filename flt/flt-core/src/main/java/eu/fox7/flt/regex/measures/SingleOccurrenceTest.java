/*
 * Created on Aug 5, 2008
 * Modified on $Date: 2009-10-27 14:14:01 $
 */
package eu.fox7.flt.regex.measures;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import eu.fox7.flt.regex.Regex;
import eu.fox7.util.tree.Node;
import eu.fox7.util.tree.SExpressionParseException;
import eu.fox7.util.tree.Tree;


/**
 * @author eu.fox7
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
