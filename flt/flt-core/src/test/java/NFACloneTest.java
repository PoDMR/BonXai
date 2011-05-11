import gjb.flt.automata.factories.sparse.ThompsonBuilder;
import gjb.flt.automata.impl.sparse.SparseNFA;
import gjb.flt.automata.impl.sparse.Symbol;
import gjb.flt.automata.matchers.NFAMatcher;

import java.util.Map;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class NFACloneTest extends TestCase {
  
	protected SparseNFA nfa1, nfa2, nfa3, nfa;

	public NFACloneTest(String name) {
		super(name);
	}

	public static Test suite() {
		return new TestSuite(NFACloneTest.class);
	}

	protected void setUp() throws Exception {
	    super.setUp();
		nfa1 = new SparseNFA();
		nfa1.addTransition("a", "q0", "q1");
		nfa1.addTransition("b", "q1", "q2");
		nfa1.addTransition("c", "q1", "q3");
		nfa1.setInitialState("q0");
		nfa1.setFinalState("q2");
		nfa1.addFinalState("q3");
		nfa2 = new SparseNFA();
		nfa2.addTransition("d", "q0", "q1");
		nfa2.setInitialState("q0");
		nfa2.setFinalState("q1");
		nfa3 = new SparseNFA();
		nfa3.addTransition("e", "q0", "q1");
		nfa3.addTransition("f", "q0", "q2");
		nfa3.setInitialState("q0");
		nfa3.setFinalState("q1");
		nfa3.addFinalState("q2");
		SparseNFA[] nfas = {nfa1, nfa2, nfa3};
		SparseNFA nfaUnion = ThompsonBuilder.union(nfas);
		nfa = new SparseNFA(nfaUnion);
	}

	@SuppressWarnings("deprecation")
    public void test_CloneAlphabet() {
		Map<String,Symbol> alphabet = nfa.getAlphabet();
		assertTrue(alphabet.containsKey("a"));
		assertTrue(alphabet.containsKey("b"));
		assertTrue(alphabet.containsKey("c"));
		assertTrue(alphabet.containsKey("d"));
		assertTrue(alphabet.containsKey("e"));
		assertTrue(alphabet.containsKey("f"));
		assertEquals(6, alphabet.size());
	}

	public void test_CloneRun() {
		NFAMatcher matcher = new NFAMatcher(nfa);
		String[] input1 = {"a", "b"};
		assertTrue(matcher.matches(input1));
		String[] input2 = {"a", "c"};
		assertTrue(matcher.matches(input2));
		String[] input3 = {"d"};
		assertTrue(matcher.matches(input3));
		String[] input4 = {"f"};
		assertTrue(matcher.matches(input4));
		String[] input5 = {"e"};
		assertTrue(matcher.matches(input5));
		String[] input6 = {"a", "b", "c"};
		assertTrue(!matcher.matches(input6));
		String[] input7 = {"a", "q", "c"};
		assertTrue(!matcher.matches(input7));
		String[] input8 = {"a", "a"};
		assertTrue(!matcher.matches(input8));
		String[] input9 = {"a", "c", "a", "b", "a", "b", "a", "b", "a", "b"};
		assertTrue(!matcher.matches(input9));
		String[] input10 = {"a", "c", "a", "b", "a", "b", "c", "b", "a", "b"};
		assertTrue(!matcher.matches(input10));
		String[] input11 = {};
		assertTrue(!matcher.matches(input11));
	}

}
