import gjb.flt.automata.factories.sparse.CFGApproximationFactory;
import gjb.flt.automata.impl.sparse.SparseNFA;
import gjb.flt.automata.matchers.NFAMatcher;
import gjb.flt.grammar.CFG;

import java.io.StringReader;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class CFGApproximationTest extends TestCase {
  
	protected CFG cfg1, cfg2, cfg3;
	protected String grammar1 =
        "A -> a B b | c A\n" +
        "B -> d A e | f";
	protected String grammar2 = "A -> a A b | c";

	public CFGApproximationTest(String name) {
		super(name);
	}

	public static Test suite() {
		return new TestSuite(CFGApproximationTest.class);
	}

	protected void setUp() throws Exception {
	    super.setUp();
		StringReader reader = new StringReader(grammar1);
		cfg1 = new CFG(reader);
		reader.close();
		reader = new StringReader(grammar2);
		cfg2 = new CFG(reader);
		reader.close();
	}

	public void test_CFGApproximationNFA() throws Exception {
		CFGApproximationFactory cfgApprox = new CFGApproximationFactory(cfg1);
		SparseNFA nfa = cfgApprox.nfa();
		NFAMatcher matcher = new NFAMatcher(nfa);
		String[] input1 = {"a", "f", "b"};
		assertTrue(matcher.matches(input1));
		String[] input2 = {"a", "d", "e", "b"};
		assertTrue(!matcher.matches(input2));
		String[] input3 = {"a", "d", "c", "c", "c", "a", "f", "b", "e", "b"};
		assertTrue(matcher.matches(input3));
		String[] input4 = {"a", "d", "a", "f", "b", "e", "b"};
		assertTrue(matcher.matches(input4));
		String[] input5 = {"a", "d", "a", "d", "a", "d", "c", "c",
						   "e", "b", "e", "b", "e", "b"};
		assertTrue(!matcher.matches(input5));
		String[] input6 = {"a", "d", "a", "d", "a", "d", "a", "c", "c",
						   "b", "e", "b", "e", "b", "e", "b"};
		assertTrue(!matcher.matches(input6));
		String[] input7 = {"a", "d", "a", "d", "a", "d", "c", "c",
						   "e", "b", "e", "b"};
		assertTrue(!matcher.matches(input7));
		String[] input8 = {"c", "a", "f", "b"};
		assertTrue(matcher.matches(input8));
	}

	public void test_CFGApproximationNFA_2() throws Exception {
		CFGApproximationFactory cfgApprox = new CFGApproximationFactory(cfg1, 2);
		SparseNFA nfa = cfgApprox.nfa();
		NFAMatcher matcher = new NFAMatcher(nfa);
		String[] input1 = {"a", "f", "b"};
		assertTrue(matcher.matches(input1));
		String[] input2 = {"a", "d", "e", "b"};
		assertTrue(!matcher.matches(input2));
		String[] input3 = {"a", "d", "c", "c", "c", "a", "f", "b", "e", "b"};
		assertTrue(matcher.matches(input3));
		String[] input4 = {"a", "d", "a", "f", "b", "e", "b"};
		assertTrue(matcher.matches(input4));
		String[] input5 = {"a", "d", "a", "d", "a", "d", "c", "c",
						   "e", "b", "e", "b", "e", "b"};
		assertTrue(!matcher.matches(input5));
		String[] input6 = {"a", "d", "a", "d", "a", "d", "a", "c", "c",
						   "b", "e", "b", "e", "b", "e", "b"};
		assertTrue(!matcher.matches(input6));
		String[] input7 = {"a", "d", "a", "d", "a", "d", "c", "c",
						   "e", "b", "e", "b"};
		assertTrue(!matcher.matches(input7));
		String[] input8 = {"c", "a", "f", "b"};
		assertTrue(matcher.matches(input8));
	}

 	public void test_CFGApproximationNFA_3() throws Exception {
		CFGApproximationFactory cfgApprox = new CFGApproximationFactory(cfg1, 3);
 		SparseNFA nfa = cfgApprox.nfa();
		NFAMatcher matcher = new NFAMatcher(nfa);
		String[] input1 = {"a", "f", "b"};
		assertTrue(matcher.matches(input1));
		String[] input2 = {"a", "d", "e", "b"};
		assertTrue(!matcher.matches(input2));
		String[] input3 = {"a", "d", "c", "c", "c", "a", "f", "b", "e", "b"};
		assertTrue(matcher.matches(input3));
		String[] input4 = {"a", "d", "a", "f", "b", "e", "b"};
		assertTrue(matcher.matches(input4));
		String[] input5 = {"a", "d", "a", "d", "a", "d", "c", "c",
						   "e", "b", "e", "b", "e", "b"};
		assertTrue(!matcher.matches(input5));
		String[] input6 = {"a", "d", "a", "d", "a", "d", "a", "c", "c",
						   "b", "e", "b", "e", "b", "e", "b"};
		assertTrue(!matcher.matches(input6));
		String[] input7 = {"a", "d", "a", "d", "a", "d", "c", "c",
						   "e", "b", "e", "b"};
		assertTrue(!matcher.matches(input7));
		String[] input8 = {"c", "a", "f", "b"};
		assertTrue(matcher.matches(input8));
	}

	public void test_CFGApproximationAnBn_1() {
		CFGApproximationFactory cfgApprox = new CFGApproximationFactory(cfg2, 1);
		SparseNFA nfa = cfgApprox.nfa();
		NFAMatcher matcher = new NFAMatcher(nfa);
		String[] input1 = {"c"};
		assertTrue(matcher.matches(input1));
		String[] input2 = {"a", "c", "b"};
		assertTrue(matcher.matches(input2));
		String[] input3 = {"a", "a", "c", "b", "b"};
		assertTrue(matcher.matches(input3));
		String[] input4 = {"a", "a", "a", "c", "b", "b", "b"};
		assertTrue(matcher.matches(input4));
		String[] input5 = {"a", "c"};
		assertTrue(matcher.matches(input5));
		String[] input5a = {"c", "b"};
		assertTrue(matcher.matches(input5a));
		String[] input6 = {"a", "a", "c", "b"};
		assertTrue(matcher.matches(input6));
		String[] input7 = {"a", "c", "b", "b"};
		assertTrue(matcher.matches(input7));
		String[] input8 = {"a", "a", "c", "b", "b", "b"};
		assertTrue(matcher.matches(input8));
		String[] input9 = {"a", "a", "a", "c", "b", "b"};
		assertTrue(matcher.matches(input9));
		String[] input10 = {"a", "a", "a", "c", "b", "b", "b", "b"};
		assertTrue(matcher.matches(input10));
		String[] input11 = {"a", "a", "a", "a", "c", "b", "b", "b"};
		assertTrue(matcher.matches(input11));
	}

	public void test_CFGApproximationAnBn_2() {
		CFGApproximationFactory cfgApprox = new CFGApproximationFactory(cfg2, 2);
		SparseNFA nfa = cfgApprox.nfa();
		NFAMatcher matcher = new NFAMatcher(nfa);
		String[] input1 = {"c"};
		assertTrue(matcher.matches(input1));
		String[] input2 = {"a", "c", "b"};
		assertTrue(matcher.matches(input2));
		String[] input3 = {"a", "a", "c", "b", "b"};
		assertTrue(matcher.matches(input3));
		String[] input4 = {"a", "a", "a", "c", "b", "b", "b"};
		assertTrue(matcher.matches(input4));
		String[] input5 = {"a", "c"};
		assertTrue(!matcher.matches(input5));
		String[] input6 = {"a", "a", "c", "b"};
		assertTrue(matcher.matches(input6));
		String[] input7 = {"a", "c", "b", "b"};
		assertTrue(matcher.matches(input7));
		String[] input8 = {"a", "a", "c", "b", "b", "b"};
		assertTrue(matcher.matches(input8));
		String[] input9 = {"a", "a", "a", "c", "b", "b"};
		assertTrue(matcher.matches(input9));
		String[] input10 = {"a", "a", "a", "c", "b", "b", "b", "b"};
		assertTrue(matcher.matches(input10));
		String[] input11 = {"a", "a", "a", "a", "c", "b", "b", "b"};
		assertTrue(matcher.matches(input11));
	}

	public void test_CFGApproximationAnBn_3() {
		CFGApproximationFactory cfgApprox = new CFGApproximationFactory(cfg2, 3);
		SparseNFA nfa = cfgApprox.nfa();
		NFAMatcher matcher = new NFAMatcher(nfa);
		String[] input1 = {"c"};
		assertTrue(matcher.matches(input1));
		String[] input2 = {"a", "c", "b"};
		assertTrue(matcher.matches(input2));
		String[] input3 = {"a", "a", "c", "b", "b"};
		assertTrue(matcher.matches(input3));
		String[] input4 = {"a", "a", "a", "c", "b", "b", "b"};
		assertTrue(matcher.matches(input4));
		String[] input5 = {"a", "c"};
		assertTrue(!matcher.matches(input5));
		String[] input6 = {"a", "a", "c", "b"};
		assertTrue(!matcher.matches(input6));
		String[] input7 = {"a", "c", "b", "b"};
		assertTrue(!matcher.matches(input7));
		String[] input8 = {"a", "a", "c", "b", "b", "b"};
		assertTrue(matcher.matches(input8));
		String[] input9 = {"a", "a", "a", "c", "b", "b"};
		assertTrue(matcher.matches(input9));
		String[] input10 = {"a", "a", "a", "c", "b", "b", "b", "b"};
		assertTrue(matcher.matches(input10));
		String[] input11 = {"a", "a", "a", "a", "c", "b", "b", "b"};
		assertTrue(matcher.matches(input11));
	}

	public void test_CFGApproximationAnBn_4() {
		CFGApproximationFactory cfgApprox = new CFGApproximationFactory(cfg2, 4);
		SparseNFA nfa = cfgApprox.nfa();
		NFAMatcher matcher = new NFAMatcher(nfa);
		String[] input1 = {"c"};
		assertTrue(matcher.matches(input1));
		String[] input2 = {"a", "c", "b"};
		assertTrue(matcher.matches(input2));
		String[] input3 = {"a", "a", "c", "b", "b"};
		assertTrue(matcher.matches(input3));
		String[] input4 = {"a", "a", "a", "c", "b", "b", "b"};
		assertTrue(matcher.matches(input4));
		String[] input5 = {"a", "c"};
		assertTrue(!matcher.matches(input5));
		String[] input6 = {"a", "a", "c", "b"};
		assertTrue(!matcher.matches(input6));
		String[] input7 = {"a", "c", "b", "b"};
		assertTrue(!matcher.matches(input7));
		String[] input8 = {"a", "a", "c", "b", "b", "b"};
		assertTrue(!matcher.matches(input8));
		String[] input9 = {"a", "a", "a", "c", "b", "b"};
		assertTrue(!matcher.matches(input9));
		String[] input10 = {"a", "a", "a", "c", "b", "b", "b", "b"};
		assertTrue(matcher.matches(input10));
		String[] input11 = {"a", "a", "a", "a", "c", "b", "b", "b"};
		assertTrue(matcher.matches(input11));
	}

}
