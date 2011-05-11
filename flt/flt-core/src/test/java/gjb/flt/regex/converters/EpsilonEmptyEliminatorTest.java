/**
 * Created on Apr 16, 2009
 * Modified on $Date: 2009-11-12 21:44:56 $
 */
package gjb.flt.regex.converters;

import gjb.flt.regex.Regex;
import gjb.flt.regex.UnknownOperatorException;
import gjb.flt.regex.converters.EpsilonEmptyEliminator;
import gjb.flt.regex.converters.Normalizer;
import gjb.util.tree.SExpressionParseException;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author lucg5005
 * @version $Revision: 1.1 $
 *
 */
public class EpsilonEmptyEliminatorTest extends TestCase {

	public static Test suite() {
		return new TestSuite(EpsilonEmptyEliminatorTest.class);
	}

	public void testExprs() {
		final String[] regexStrs = {
				"(EMPTY)",
				"(EPSILON)",
				"(a)",
				"(| (a) (EPSILON))",
				"(| (EPSILON) (a))",
				"(| (EMPTY) (a))",
				"(| (EPSILON) (EMPTY))",
				"(| (a) (. (b) (c)))",
				"(. (EPSILON) (a) (b))",
				"(. (a) (EPSILON))",
				"(. (EPSILON) (EMPTY))",
				"(. (a) (b) (EMPTY))",
				"(. (? (EMPTY)) (a))",
				"(. (| (EMPTY) (EMPTY) (EMPTY)) (d))",
				"(. (EPSILON) (EPSILON) (EPSILON))",
				"(| (? (a)) (EPSILON))",
				"(. (a) (? (EMPTY)))",
				"(. (a) (* (EMPTY)))",
				"(. (a) (+ (EMPTY)))",
				"(. (a) ({1,3} (EMPTY)))",
				"(. (a) ({0,3} (EMPTY)))",
		};
		final String[] targetRegexStrs = {
				"(EMPTY)",
				"(EPSILON)",
				"(a)",
				"(? (a))",
				"(? (a))",
				"(a)",
				"(EPSILON)",
				"(| (a) (. (b) (c)))",
				"(. (a) (b))",
				"(a)",
				"(EMPTY)",
				"(EMPTY)",
				"(a)",
				"(EMPTY)",
				"(EPSILON)",
				"(? (a))",
				"(a)",
				"(a)",
				"(EMPTY)",
				"(EMPTY)",
				"(a)",
		};
		assertEquals("test setup", regexStrs.length, targetRegexStrs.length);
		Normalizer converter = new EpsilonEmptyEliminator();
		try {
			for (int i = 0; i < regexStrs.length; i++) {
				Regex regex = converter.normalize(new Regex(regexStrs[i]));
				assertEquals(regexStrs[i], targetRegexStrs[i], regex.toString());
			}
		} catch (SExpressionParseException e) {
			e.printStackTrace();
			fail("unexpected exception");
		} catch (UnknownOperatorException e) {
			e.printStackTrace();
			fail("unexpected exception");
		}
	}

}
