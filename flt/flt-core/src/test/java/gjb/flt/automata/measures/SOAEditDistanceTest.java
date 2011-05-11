/**
 * Created on Nov 10, 2009
 * Modified on $Date: 2009-11-12 21:44:56 $
 */
package gjb.flt.automata.measures;

import gjb.flt.automata.FeatureNotSupportedException;
import gjb.flt.automata.factories.sparse.GlushkovFactory;
import gjb.flt.automata.impl.sparse.StateNFA;
import gjb.flt.automata.measures.RelativeLanguageMeasure;
import gjb.flt.automata.measures.SOAEditDistance;
import gjb.flt.regex.UnknownOperatorException;
import gjb.util.tree.SExpressionParseException;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author lucg5005
 * @version $Revision: 1.1 $
 *
 */
public class SOAEditDistanceTest extends TestCase {

	protected GlushkovFactory glushkov = new GlushkovFactory();

    public static Test suite() {
        return new TestSuite(SOAEditDistanceTest.class);
    }

	public void testIdentical() {
		final String regexStr = "(. (+ (a)) (| (b) (? (c))) (d) (| (e) (f0)))";
		final double expected = 0.0 + 0.0 + 0.0;
		testMeasure(regexStr, regexStr, expected);
	}

	public void testMissingTransitions() {
		final String regexStr1 = "(. (a) (? (b)) (| (c) (d)))";
		final String regexStr2 = "(. (a) (b) (| (c) (d)))";
		final double expected = 0.0 + 2.0 + 0.0;
		testMeasure(regexStr1, regexStr2, expected);
	}

	public void testMissingStatesTransitions1() {
		final String regexStr1 = "(. (a) (? (b)) (| (c) (d)))";
		final String regexStr2 = "(. (a) (b) (c))";
		final double expected = 1.0 + 3.0 + 1.0;
		testMeasure(regexStr1, regexStr2, expected);
	}

	public void testMissingStatesTransitions2() {
		final String regexStr1 = "(. (a) (b) (d))";
		final String regexStr2 = "(. (a) (c) (d))";
		final double expected = 2.0 + 4.0 + 0.0;
		testMeasure(regexStr1, regexStr2, expected);
	}
	
	public void testEmpty() {
		final String regexStr1 = "(. (a) (? (b)) (| (c) (d)))";
		final String regexStr2 = "(EMPTY)";
		final double expected = 4.0 + 6.0 + 2.0;
		testMeasure(regexStr1, regexStr2, expected);
	}

	public void testEpsilon() {
		final String regexStr1 = "(. (a) (? (b)) (| (c) (d)))";
		final String regexStr2 = "(EPSILON)";
		final double expected = 4.0 + 6.0 + 3.0;
		testMeasure(regexStr1, regexStr2, expected);
	}

	public void testEmptyEpsilon() {
		final String regexStr1 = "(EMPTY)";
		final String regexStr2 = "(EPSILON)";
		final double expected = 0.0 + 0.0 + 1.0;
		testMeasure(regexStr1, regexStr2, expected);
	}

	protected void testMeasure(String regexStr1, String regexStr2, double expected) {
        try {
            StateNFA nfa1 = glushkov.create(regexStr1);
            StateNFA nfa2 = glushkov.create(regexStr2);
            RelativeLanguageMeasure<Double> measure = new SOAEditDistance();
    		assertEquals("identical", expected, measure.compute(nfa1, nfa2), 0.01);
        } catch (UnknownOperatorException e) {
            e.printStackTrace();
            fail("unexpected exception");
        } catch (FeatureNotSupportedException e) {
            e.printStackTrace();
            fail("unexpected exception");
        } catch (SExpressionParseException e) {
            e.printStackTrace();
            fail("unexpected exception");
        }
    }

}
