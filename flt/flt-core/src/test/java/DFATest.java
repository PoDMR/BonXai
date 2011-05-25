import gjb.flt.automata.factories.sparse.CFGApproximationFactory;
import gjb.flt.automata.factories.sparse.Determinizer;
import gjb.flt.automata.impl.sparse.SparseNFA;
import gjb.flt.automata.impl.sparse.Symbol;
import gjb.flt.automata.matchers.NFAMatcher;
import gjb.flt.grammar.CFG;

import java.io.StringReader;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class DFATest extends TestCase {
  
	protected CFG cfg;
	protected String grammar = "A -> a A b | c";

	static public void main(String args[]) {
		junit.textui.TestRunner.run(suite());
	}

	public DFATest(String name) {
		super(name);
	}

	public static Test suite() {
		return new TestSuite(DFATest.class);
	}

	protected void setUp() throws Exception {
	    super.setUp();
		StringReader reader = new StringReader(grammar);
		cfg = new CFG(reader);
		reader.close();
	}

	public void test_DFA1() {
		CFGApproximationFactory cfgApprox = new CFGApproximationFactory(cfg, 4);
		SparseNFA nfa = cfgApprox.nfa();
		NFAMatcher nfaMatcher = new NFAMatcher(nfa);
		SparseNFA dfa = Determinizer.dfa(nfa);
		NFAMatcher dfaMatcher = new NFAMatcher(dfa);
		String[] input1 = {"c"};
		assertTrue(nfaMatcher.matches(input1) && dfaMatcher.matches(input1));
		String[] input2 = {"a", "c", "b"};
		assertTrue(nfaMatcher.matches(input2) && dfaMatcher.matches(input2));
		String[] input3 = {"a", "a", "c", "b", "b"};
		assertTrue(nfaMatcher.matches(input3) && dfaMatcher.matches(input3));
		String[] input4 = {"a", "a", "a", "c", "b", "b", "b"};
		assertTrue(nfaMatcher.matches(input4) && dfaMatcher.matches(input4));
		String[] input5 = {"a", "c"};
		assertTrue(!nfaMatcher.matches(input5) && !dfaMatcher.matches(input5));
		String[] input6 = {"a", "a", "c", "b"};
		assertTrue(!nfaMatcher.matches(input6) && !dfaMatcher.matches(input6));
		String[] input7 = {"a", "c", "b", "b"};
		assertTrue(!nfaMatcher.matches(input7) && !dfaMatcher.matches(input7));
		String[] input8 = {"a", "a", "c", "b", "b", "b"};
		assertTrue(!nfaMatcher.matches(input8) && !dfaMatcher.matches(input8));
		String[] input9 = {"a", "a", "a", "c", "b", "b"};
		assertTrue(!nfaMatcher.matches(input9) && !dfaMatcher.matches(input9));
		String[] input10 = {"a", "a", "a", "c", "b", "b", "b", "b"};
		assertTrue(nfaMatcher.matches(input10) && dfaMatcher.matches(input10));
		String[] input11 = {"a", "a", "a", "a", "c", "b", "b", "b"};
		assertTrue(nfaMatcher.matches(input11) && dfaMatcher.matches(input11));
	}

	public void test_DFA2() {
	    SparseNFA nfa = new SparseNFA();
	    nfa.addTransition(Symbol.getEpsilon().toString(), "q1", "q2");
	    nfa.addTransition("a", "q1", "q3");
	    nfa.addTransition("b", "q3", "q2");
	    nfa.addTransition(Symbol.getEpsilon().toString(), "q3", "q4");
	    nfa.addTransition("b", "q3", "q5");
	    nfa.addTransition("a", "q4", "q4");
	    nfa.addTransition("a", "q4", "q6");
	    nfa.addTransition("b", "q5", "q5");
	    nfa.addTransition("b", "q5", "q6");
	    nfa.setInitialState("q1");
	    nfa.setFinalState("q2");
	    nfa.addFinalState("q6");
	    SparseNFA dfa = Determinizer.dfa(nfa);
	    assertEquals("DFA number of states", 6, dfa.getNumberOfStates());
	    assertEquals("DFA final state number", 4, dfa.getNumberOfFinalStates());
	    assertTrue(dfa.hasState("[q1, q2]"));
	    assertTrue(dfa.hasState("[q3, q4]"));
	    assertTrue(dfa.hasState("[q4, q6]"));
	    assertTrue(dfa.hasState("[q2, q5]"));
	    assertTrue(dfa.hasState("[q5, q6]"));
	    assertTrue(dfa.getFinalStates().contains(dfa.getState("[q1, q2]")));
	    assertTrue(dfa.getFinalStates().contains(dfa.getState("[q4, q6]")));
	    assertTrue(dfa.getFinalStates().contains(dfa.getState("[q2, q5]")));
	    assertTrue(dfa.getFinalStates().contains(dfa.getState("[q5, q6]")));
	    assertEquals("DFA initial state", "[q1, q2]", dfa.getStateValue(dfa.getInitialState()));
	}
	
}
