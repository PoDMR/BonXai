import gjb.flt.automata.converters.Minimizer;
import gjb.flt.automata.converters.NFAMinimizer;
import gjb.flt.automata.factories.sparse.CFGApproximationFactory;
import gjb.flt.automata.factories.sparse.Determinizer;
import gjb.flt.automata.impl.sparse.SparseNFA;
import gjb.flt.automata.impl.sparse.StateDFA;
import gjb.flt.grammar.CFG;
import gjb.flt.regex.NoRegularExpressionDefinedException;
import gjb.flt.regex.Regex;
import gjb.flt.regex.UnknownOperatorException;
import gjb.flt.regex.factories.StateEliminationFactory;
import gjb.flt.regex.matchers.Matcher;

import java.io.StringReader;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class MinimizeTest extends TestCase {
  
	protected CFG cfg;
	protected String grammar = "A -> a A b | c";

	static public void main(String args[]) {
		junit.textui.TestRunner.run(suite());
	}

	public MinimizeTest(String name) {
		super(name);
	}

	public static Test suite() {
		return new TestSuite(MinimizeTest.class);
	}

	protected void setUp() throws Exception {
	    super.setUp();
		StringReader reader = new StringReader(grammar);
		cfg = new CFG(reader);
		reader.close();
	}

	public void test_Minimize_2() {
		CFGApproximationFactory cfgApprox = new CFGApproximationFactory(cfg, 2);
		SparseNFA nfa = cfgApprox.nfa();
		SparseNFA minimal = Determinizer.dfa(nfa);
		Minimizer minimizer = new NFAMinimizer();
		minimizer.minimize(minimal);
		minimizer.minimize(minimal);
		StateDFA dfa = Determinizer.dfa(minimal);
		StateEliminationFactory factory = new StateEliminationFactory();
		Regex regex = factory.create(dfa, false);
		Matcher matcher = new Matcher(regex);
		try {
	        assertTrue(matcher.matches(new String[] {"c"}));
	        assertTrue(matcher.matches(new String[] {"a", "c", "b"}));
	        assertTrue(matcher.matches(new String[] {"a", "c", "b", "b", "b", "b"}));
	        assertTrue(matcher.matches(new String[] {"a", "a", "a", "a", "c", "b"}));
	        assertTrue(matcher.matches(new String[] {"a", "a", "a", "a", "a", "c", "b", "b", "b"}));
	        assertTrue(!matcher.matches(new String[] {"a", "c"}));
	        assertTrue(!matcher.matches(new String[] {"c", "b"}));
	        assertTrue(!matcher.matches(new String[] {"a", "a", "c", "b", "a"}));
	        assertTrue(!matcher.matches(new String[] {"c", "a"}));
	        assertTrue(!matcher.matches(new String[] {"a", "a", "b", "b"}));
        } catch (NoRegularExpressionDefinedException e) {
	        e.printStackTrace();
	        fail("unexpected exception");
        } catch (UnknownOperatorException e) {
	        e.printStackTrace();
	        fail("unexpected exception");
        }
	}


	public void test_Minimize_3() {
		CFGApproximationFactory cfgApprox = new CFGApproximationFactory(cfg, 3);
		SparseNFA nfa = cfgApprox.nfa();
		SparseNFA minimal = Determinizer.dfa(nfa);
		Minimizer minimizer = new NFAMinimizer();
		minimizer.minimize(minimal);
		StateEliminationFactory factory = new StateEliminationFactory();
		Regex regex = factory.create(minimal, false);
		Matcher matcher = new Matcher(regex);
        try {
	        assertTrue(matcher.matches(new String[] {"c"}));
	        assertTrue(matcher.matches(new String[] {"a", "c", "b"}));
	        assertTrue(matcher.matches(new String[] {"a", "a", "c", "b", "b"}));
	        assertTrue(!matcher.matches(new String[] {"a", "c", "b", "b", "b", "b"}));
	        assertTrue(!matcher.matches(new String[] {"a", "a", "a", "a", "c", "b"}));
	        assertTrue(matcher.matches(new String[] {"a", "a", "c", "b", "b", "b"}));
	        assertTrue(matcher.matches(new String[] {"a", "a", "a", "c", "b", "b"}));
	        assertTrue(matcher.matches(new String[] {"a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "c", "b", "b"}));
	        assertTrue(matcher.matches(new String[] {"a", "a", "c", "b", "b", "b", "b", "b", "b", "b", "b", "b", "b"}));
	        assertTrue(matcher.matches(new String[] {"a", "a", "a", "a", "a", "c", "b", "b", "b"}));
	        assertTrue(!matcher.matches(new String[] {"a", "c"}));
	        assertTrue(!matcher.matches(new String[] {"c", "b"}));
	        assertTrue(!matcher.matches(new String[] {"a", "a", "c", "b", "a"}));
	        assertTrue(!matcher.matches(new String[] {"c", "a"}));
	        assertTrue(!matcher.matches(new String[] {"a", "a", "b", "b"}));
        } catch (NoRegularExpressionDefinedException e) {
	        e.printStackTrace();
	        fail("unexpected exception");
        } catch (UnknownOperatorException e) {
	        e.printStackTrace();
	        fail("unexpected exception");
        }
	}

}
