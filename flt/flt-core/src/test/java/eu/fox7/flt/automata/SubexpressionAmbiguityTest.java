/**
 * Created on Sep 3, 2009
 * Modified on $Date: 2009-11-12 21:44:56 $
 */
package eu.fox7.flt.automata;

import eu.fox7.flt.automata.FeatureNotSupportedException;
import eu.fox7.flt.regex.Glushkov;
import eu.fox7.flt.regex.Regex;
import eu.fox7.flt.regex.UnknownOperatorException;
import eu.fox7.util.tree.Node;
import eu.fox7.util.tree.SExpressionParseException;
import eu.fox7.util.tree.Tree;
import eu.fox7.util.tree.selectors.PositionalNodeSelector;

import java.util.HashSet;
import java.util.Set;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author lucg5005
 * @version $Revision: 1.1 $
 *
 */
public class SubexpressionAmbiguityTest extends TestCase {

    public static Test suite() {
        return new TestSuite(SubexpressionAmbiguityTest.class);
    }

    public void testFirstSets() {
		final String regexStr = "(+ (| (. (? (b)) (a)) (. (a) (| (b) (c)))))";
		Regex regex = new Regex();
		try {
			Tree tree = regex.getTree(regexStr);
			checkFirstSet(tree, new int[] {}, new String[] {"a", "b"});
			checkFirstSet(tree, new int[] {0, 0}, new String[] {"a", "b"});
			checkFirstSet(tree, new int[] {0, 1}, new String[] {"a"});
			checkFirstSet(tree, new int[] {0, 0, 0}, new String[] {"b"});
		} catch (SExpressionParseException e) {
			e.printStackTrace();
			fail("unexpected exception");
		} catch (FeatureNotSupportedException e) {
			e.printStackTrace();
			fail("unexpected exception");
		} catch (UnknownOperatorException e) {
			e.printStackTrace();
			fail("unexpected exception");
		}
    }

    protected void checkFirstSet(Tree tree, int[] positions, String[] symbols)
            throws FeatureNotSupportedException, UnknownOperatorException {
    	Glushkov glushkov = new Glushkov();
    	PositionalNodeSelector selector = new PositionalNodeSelector();
    	Node node = selector.select(tree, positions);
		Set<String> firstSet = glushkov.first(node);
		Set<String> expectedSet = new HashSet<String>();
		for (String symbol : symbols)
			expectedSet.add(symbol);
		assertEquals("expression first set", expectedSet, firstSet);
    }
}
