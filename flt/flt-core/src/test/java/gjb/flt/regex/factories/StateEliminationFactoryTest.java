/**
 * Created on Nov 12, 2009
 * Modified on $Date: 2009-11-12 23:38:28 $
 */
package gjb.flt.regex.factories;

import gjb.flt.automata.FeatureNotSupportedException;
import gjb.flt.automata.NotDFAException;
import gjb.flt.automata.factories.sparse.Determinizer;
import gjb.flt.automata.factories.sparse.GlushkovFactory;
import gjb.flt.automata.impl.sparse.ModifiableStateNFA;
import gjb.flt.automata.impl.sparse.SparseNFA;
import gjb.flt.automata.impl.sparse.StateDFA;
import gjb.flt.automata.measures.EquivalenceTest;
import gjb.flt.regex.Regex;
import gjb.flt.regex.UnknownOperatorException;
import gjb.util.tree.SExpressionParseException;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author lucg5005
 * @version $Revision: 1.3 $
 *
 */
public class StateEliminationFactoryTest extends TestCase {

    public static Test suite() {
        return new TestSuite(StateEliminationFactoryTest.class);
    }

	public void testEmpty() {
		final String targetExpr = "(+ (a))";
		ModifiableStateNFA origDFA = new SparseNFA();
		origDFA.setInitialState("q1");
		origDFA.addTransition("a", "q1", "q2");
		origDFA.addTransition("a", "q2", "q2");
		origDFA.setFinalState("q2");
		StateEliminationFactory factory = new StateEliminationFactory();
		Regex regex = factory.create((StateDFA) origDFA, false);
		GlushkovFactory glushkov = new GlushkovFactory();
		try {
			SparseNFA dfa = Determinizer.dfa(glushkov.create(regex.toString()));
			assertTrue("equiv", EquivalenceTest.areEquivalent(origDFA,
			                                                  dfa));
			assertTrue("equiv", EquivalenceTest.areEquivalent(glushkov.create(targetExpr),
			                                                  origDFA));
		} catch (NotDFAException e) {
			e.printStackTrace();
			fail("unexpected exception");
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
	
	public void testEpsilon() {
		final String targetExpr = "(EPSILON)";
		ModifiableStateNFA origDFA = new SparseNFA();
		origDFA.setInitialState("q1");
		origDFA.setFinalState("q1");
		StateEliminationFactory factory = new StateEliminationFactory();
		Regex regex = factory.create((StateDFA) origDFA, false);
		GlushkovFactory glushkov = new GlushkovFactory();
		try {
			SparseNFA dfa = Determinizer.dfa(glushkov.create(regex.toString()));
			assertTrue("equiv", EquivalenceTest.areEquivalent(origDFA,
			                                                  dfa));
			assertTrue("equiv", EquivalenceTest.areEquivalent(glushkov.create(targetExpr),
			                                                  origDFA));
		} catch (NotDFAException e) {
			e.printStackTrace();
			fail("unexpected exception");
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
	
	public void testDFA1() {
    	final String targetExpr = "(. (| (a) (b)) (* (c)) (| (e) (. (d) (* (e)) (? (f)))))";
    	ModifiableStateNFA origDFA = new SparseNFA();
    	origDFA.setInitialState("q1");
    	origDFA.addTransition("a", "q1", "q2");
    	origDFA.addTransition("b", "q1", "q2");
    	origDFA.addTransition("c", "q2", "q2");
    	origDFA.addTransition("d", "q2", "q3");
    	origDFA.addTransition("e", "q2", "q4");
    	origDFA.addTransition("e", "q3", "q3");
    	origDFA.addTransition("f", "q3", "q4");
    	origDFA.addFinalState("q3");
    	origDFA.addFinalState("q4");
    	StateEliminationFactory factory = new StateEliminationFactory();
    	Regex regex = factory.create((StateDFA) origDFA, false);
    	GlushkovFactory glushkov = new GlushkovFactory();
    	try {
    		SparseNFA dfa = Determinizer.dfa(glushkov.create(regex.toString()));
    		assertTrue("equiv", EquivalenceTest.areEquivalent(origDFA,
    		                                                  dfa));
    		assertTrue("equiv", EquivalenceTest.areEquivalent(glushkov.create(targetExpr),
    		                                                  origDFA));
        } catch (NotDFAException e) {
            e.printStackTrace();
            fail("unexpected exception");
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

	public void testDFA2() {
		final String targetExpr = "(EPSILON)";
		ModifiableStateNFA origDFA = new SparseNFA();
		origDFA.setInitialState("q1");
		origDFA.setFinalState("q1");
		StateEliminationFactory factory = new StateEliminationFactory();
		Regex regex = factory.create((StateDFA) origDFA, false);
		GlushkovFactory glushkov = new GlushkovFactory();
		try {
			SparseNFA dfa = Determinizer.dfa(glushkov.create(regex.toString()));
			assertTrue("equiv", EquivalenceTest.areEquivalent(origDFA,
			                                                  dfa));
			assertTrue("equiv", EquivalenceTest.areEquivalent(glushkov.create(targetExpr),
			                                                  origDFA));
		} catch (NotDFAException e) {
			e.printStackTrace();
			fail("unexpected exception");
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
