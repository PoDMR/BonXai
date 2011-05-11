/**
 * Created on Jun 16, 2009
 * Modified on $Date: 2009-11-12 21:44:56 $
 */
package gjb.flt.automata.factories;

import gjb.flt.automata.FeatureNotSupportedException;
import gjb.flt.automata.NotDFAException;
import gjb.flt.automata.factories.sparse.GlushkovFactory;
import gjb.flt.automata.factories.sparse.SOAFactory;
import gjb.flt.automata.impl.sparse.SparseNFA;
import gjb.flt.automata.measures.EquivalenceTest;
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
public class SOABuilderTest  extends TestCase {

	protected GlushkovFactory glushkovFactory;

    public static Test suite() {
        return new TestSuite(SOABuilderTest.class);
    }

    public void setUp() {
    	glushkovFactory = new GlushkovFactory();
    }

    public void test_expr01() {
		final String regexStr = "(. (? (a)) (* (| (b) (c))))";
		final String[][] sample = {
				{},
				{"a"},
				{"a", "b", "b", "c"},
				{"a", "c", "c", "b"},
				{"b", "c", "b"},
				{"c", "b", "c"}
		};
		SOAFactory builder = new SOAFactory();
		for (String[] example : sample) {
			for (String symbolValue : example)
				builder.addSymbol(symbolValue);
			builder.addEnd();
		}
		SparseNFA nfa = builder.getAutomaton();
		try {
			SparseNFA targetNFA = glushkovFactory.create(regexStr);
			assertTrue("equiv", EquivalenceTest.areEquivalent(new SparseNFA[] {targetNFA, nfa}));
		} catch (SExpressionParseException e) {
			e.printStackTrace();
			fail("unexpected exception");
		} catch (NotDFAException e) {
			e.printStackTrace();
			fail("unexpected exception");
		} catch (UnknownOperatorException e) {
			e.printStackTrace();
			fail("unexpected exception");
		} catch (FeatureNotSupportedException e) {
			e.printStackTrace();
			fail("unexpected exception");
		}
	}

	public void test_expr02() {
		final String regexStr = "(EPSILON)";
		final String[][] sample = {
				{}
		};
		SOAFactory builder = new SOAFactory();
		for (String[] example : sample) {
			for (String symbolValue : example)
				builder.addSymbol(symbolValue);
			builder.addEnd();
		}
		SparseNFA nfa = builder.getAutomaton();
		try {
			SparseNFA targetNFA = glushkovFactory.create(regexStr);
			assertTrue("equiv", EquivalenceTest.areEquivalent(new SparseNFA[] {targetNFA, nfa}));
		} catch (SExpressionParseException e) {
			e.printStackTrace();
			fail("unexpected exception");
		} catch (NotDFAException e) {
			e.printStackTrace();
			fail("unexpected exception");
		} catch (UnknownOperatorException e) {
			e.printStackTrace();
			fail("unexpected exception");
		} catch (FeatureNotSupportedException e) {
			e.printStackTrace();
			fail("unexpected exception");
		}
	}
	
	public void test_expr03() {
		final String regexStr = "(EMPTY)";
		final String[][] sample = {
		};
		SOAFactory builder = new SOAFactory();
		for (String[] example : sample) {
			for (String symbolValue : example)
				builder.addSymbol(symbolValue);
			builder.addEnd();
		}
		SparseNFA nfa = builder.getAutomaton();
		try {
			SparseNFA targetNFA = glushkovFactory.create(regexStr);
			assertTrue("equiv", EquivalenceTest.areEquivalent(new SparseNFA[] {targetNFA, nfa}));
		} catch (SExpressionParseException e) {
			e.printStackTrace();
			fail("unexpected exception");
		} catch (NotDFAException e) {
			e.printStackTrace();
			fail("unexpected exception");
		} catch (UnknownOperatorException e) {
			e.printStackTrace();
			fail("unexpected exception");
		} catch (FeatureNotSupportedException e) {
			e.printStackTrace();
			fail("unexpected exception");
		}
	}
	
	public void test_expr04() {
		final String regexStr = "(+ (a))";
		final String[][] sample = {
				{"a", "a"}
		};
		SOAFactory builder = new SOAFactory();
		for (String[] example : sample) {
			for (String symbolValue : example)
				builder.addSymbol(symbolValue);
			builder.addEnd();
		}
		SparseNFA nfa = builder.getAutomaton();
		try {
			SparseNFA targetNFA = glushkovFactory.create(regexStr);
			assertTrue("equiv", EquivalenceTest.areEquivalent(new SparseNFA[] {targetNFA, nfa}));
		} catch (SExpressionParseException e) {
			e.printStackTrace();
			fail("unexpected exception");
		} catch (NotDFAException e) {
			e.printStackTrace();
			fail("unexpected exception");
		} catch (UnknownOperatorException e) {
			e.printStackTrace();
			fail("unexpected exception");
		} catch (FeatureNotSupportedException e) {
			e.printStackTrace();
			fail("unexpected exception");
		}
	}
	
}
