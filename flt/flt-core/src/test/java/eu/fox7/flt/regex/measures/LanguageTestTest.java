/**
 * Created on Mar 26, 2009
 * Modified on $Date: 2009-11-12 21:44:56 $
 */
package eu.fox7.flt.regex.measures;

import eu.fox7.flt.automata.FeatureNotSupportedException;
import eu.fox7.flt.automata.NFAException;
import eu.fox7.flt.regex.RegexException;
import eu.fox7.flt.regex.UnknownOperatorException;
import eu.fox7.flt.regex.measures.AmbiguityTest;
import eu.fox7.flt.regex.measures.ContainsEpsilonTest;
import eu.fox7.flt.regex.measures.LanguageTest;
import eu.fox7.util.tree.SExpressionParseException;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author lucg5005
 * @version $Revision: 1.1 $
 *
 */
public class LanguageTestTest extends TestCase {

    public static Test suite() {
        return new TestSuite(LanguageTestTest.class);
    }

	public void testAmbiguity() {
		final String[] nonAmbExprStrs = {
				"(EPSILON)",
				"(EMPTY)",
				"(. (a) (a) (a))",
				"(| (a) (b) (c))"
		};
		final String[] ambExprStrs = {
				"(. (? (a)) (a))",
				"(| (a) (. (a) (b)))"
		};
		LanguageTest test = new AmbiguityTest();
		try {
			for (String regexStr : nonAmbExprStrs)
				assertFalse(regexStr, test.test(regexStr));
			for (String regexStr : ambExprStrs)
				assertTrue(regexStr, test.test(regexStr));
		} catch (SExpressionParseException e) {
			e.printStackTrace();
			fail("unexpected exception");
		} catch (UnknownOperatorException e) {
			e.printStackTrace();
			fail("unexpected exception");
		} catch (FeatureNotSupportedException e) {
			e.printStackTrace();
			fail("unexpected exception");
		} catch (NFAException e) {
			e.printStackTrace();
			fail("unexpected exception");
        } catch (RegexException e) {
			e.printStackTrace();
			fail("unexpected exception");
        }
	}

	public void testContainsEpsilon() {
		final String[] nonEpsilonExprs = {
				"(EMPTY)",
				"(a)",
				"(+ (a))",
				"({1,4} (a))",
				"(. (? (a)) (b))",
				"(| (a) (+ (b)))",
				"(# (a) (. (b) (c)) (| (d) (e)))"
		};
		final String[] epsilonExprs = {
				"(EPSILON)",
				"(? (a))",
				"(* (. (a) (b)))",
				"({0,4} (. (a) (b)))",
				"({1,7} (. (? (a)) (? (b))))",
				"(. (? (a)) (? (b)))",
				"(| (a) (? (b)))",
				"(| (. (? (a)) (b)) (. (* (c)) (? (d))))",
				"(# (* (a)) (? (b)) ({0,3} (c)))"
		};
		LanguageTest tester = new ContainsEpsilonTest();
		try {
			for (String regexStr : nonEpsilonExprs)
				assertFalse(regexStr, tester.test(regexStr));
			for (String regexStr : epsilonExprs)
				assertTrue(regexStr, tester.test(regexStr));
		} catch (SExpressionParseException e) {
			e.printStackTrace();
			fail("unexpected exception");
		} catch (UnknownOperatorException e) {
			e.printStackTrace();
			fail("unexpected exception");
		} catch (FeatureNotSupportedException e) {
			e.printStackTrace();
			fail("unexpected exception");
		} catch (NFAException e) {
			e.printStackTrace();
			fail("unexpected exception");
        } catch (RegexException e) {
			e.printStackTrace();
			fail("unexpected exception");
        }
	}

}
