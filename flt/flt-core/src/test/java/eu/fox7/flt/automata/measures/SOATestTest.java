/**
 * Created on Nov 9, 2009
 * Modified on $Date: 2009-11-12 21:44:56 $
 */
package eu.fox7.flt.automata.measures;

import eu.fox7.flt.automata.FeatureNotSupportedException;
import eu.fox7.flt.automata.factories.sparse.GlushkovFactory;
import eu.fox7.flt.automata.impl.sparse.StateNFA;
import eu.fox7.flt.automata.measures.SOATest;
import eu.fox7.flt.regex.UnknownOperatorException;
import eu.fox7.util.tree.SExpressionParseException;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author lucg5005
 * @version $Revision: 1.1 $
 *
 */
public class SOATestTest extends TestCase {

	protected SOATest soaTest = new SOATest();
	protected GlushkovFactory glushkov = new GlushkovFactory();

    public static Test suite() {
        return new TestSuite(SOATestTest.class);
    }

	public void testExpressions() {
		final String[] regexStr = {
				"(EMPTY)",
				"(EPSILON)",
				"(a)",
				"(. (? (a)) (b) (+ (| (c) (d))))",
				"(| (a) (. (b) (a)))",
				"(. (a) (a))"
		};
		final boolean[] expected = {
				true,
				true,
				true,
				true,
				false,
				false
		};
		try {
			for (int i = 0; i < regexStr.length; i++) {
				StateNFA nfa = glushkov.create(regexStr[i]);
				assertEquals(regexStr[i], expected[i], soaTest.test(nfa));
			}
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
