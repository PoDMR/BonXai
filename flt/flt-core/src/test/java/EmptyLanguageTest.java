import gjb.flt.automata.factories.sparse.ProductNFAFactory;
import gjb.flt.automata.factories.sparse.ThompsonBuilder;
import gjb.flt.automata.generators.ShortestStringGenerator;
import gjb.flt.automata.impl.sparse.SparseNFA;
import gjb.flt.automata.matchers.NFAMatcher;
import gjb.flt.automata.measures.EmptynessTest;
import gjb.flt.automata.measures.NonTrivialEmptynessTest;
import gjb.util.tree.Node;
import gjb.util.tree.Tree;

import java.io.StringReader;
import java.util.Iterator;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class EmptyLanguageTest extends TestCase {
  
	protected SparseNFA nfa1, nfa2, nfa3, nfa4, nfa5, nfa6, nfa7, nfa8, nfa9, nfa10;
	protected String treeRep1 = "(, (| (, (a) (* (c)) (c)) (, (b) (* (e)) (f) (* (f)) (h))) (* (f)))",
		treeRep2 = "(? (a))",
		treeRep3 = "(* (b))",
		treeRep4 = "(+ (b))",
		treeRep5 = "(? (c))";

	static public void main(String args[]) {
		junit.textui.TestRunner.run(suite());
	}

	public EmptyLanguageTest(String name) {
		super(name);
	}

	public static Test suite() {
		return new TestSuite(EmptyLanguageTest.class);
	}

	protected void setUp() throws Exception {
	    super.setUp();
		Tree tree = new Tree(new StringReader(treeRep1));
		nfa1 = tree2NFA(tree.getRoot());
		nfa2 = new SparseNFA();
		nfa2.addTransition("a", "q0", "q1");
		nfa2.addTransition("b", "q1", "q2");
		nfa2.addTransition("c", "q1", "q3");
		nfa2.addTransition("c", "q3", "q3");
		nfa2.addTransition("d", "q0", "q3");
		nfa2.addTransition("e", "q2", "q2");
		nfa2.setInitialState("q0");
		nfa3 = ThompsonBuilder.symbolNFA("a");
		nfa4 = ThompsonBuilder.emptyNFA();
		tree = new Tree(new StringReader(treeRep2));
		nfa5 = tree2NFA(tree.getRoot());
		tree = new Tree(new StringReader(treeRep3));
		nfa6 = tree2NFA(tree.getRoot());
		SparseNFA[] nfas = {nfa5, nfa6};
		nfa7 = ProductNFAFactory.intersection(nfas);
		tree = new Tree(new StringReader(treeRep4));
		nfa8 = tree2NFA(tree.getRoot());
		tree = new Tree(new StringReader(treeRep5));
		nfa9 = tree2NFA(tree.getRoot());
		SparseNFA[] nfas2 = {nfa8, nfa9};
		nfa10 = ProductNFAFactory.intersection(nfas2);
	}

	public void test_isEmpty() throws Exception {
		EmptynessTest emptyTest = new EmptynessTest();
		assertTrue(!emptyTest.test(nfa1));
		assertTrue(emptyTest.test(nfa2));
		assertTrue(!emptyTest.test(nfa3));
		assertTrue(emptyTest.test(nfa4));
		assertTrue(!emptyTest.test(nfa5));
		assertTrue(!emptyTest.test(nfa6));
		assertTrue(!emptyTest.test(nfa7));
		assertTrue(!emptyTest.test(nfa8));
		assertTrue(!emptyTest.test(nfa9));
		assertTrue(emptyTest.test(nfa10));
	}

	public void test_isNonTrivialEmpty() throws Exception {
		NonTrivialEmptynessTest nteTest = new NonTrivialEmptynessTest();
		assertTrue(!nteTest.test(nfa1));
		assertTrue(nteTest.test(nfa2));
		assertTrue(!nteTest.test(nfa3));
		assertTrue(nteTest.test(nfa4));
		assertTrue(!nteTest.test(nfa5));
		assertTrue(!nteTest.test(nfa6));
		assertTrue(nteTest.test(nfa7));
		assertTrue(!nteTest.test(nfa8));
		assertTrue(!nteTest.test(nfa9));
		assertTrue(nteTest.test(nfa10));
	}

	public void test_acceptsEmptyString() throws Exception {
	    SparseNFA nfa = new SparseNFA();
	    nfa.addTransition("a", "q0", "q0");
	    nfa.addTransition("b", "q0", "q0");
	    nfa.setInitialState("q0");
	    nfa.setFinalState("q0");
	    NFAMatcher matcher = new NFAMatcher(nfa);
	    assertTrue(matcher.matches(new String[0]));
	}

	public void test_shortestPaths1() throws Exception {
		SparseNFA nfa = new SparseNFA();
		nfa.addTransition("a", "q1", "q2");
		nfa.addTransition("b", "q1", "q4");
		nfa.addTransition("b", "q2", "q2");
		nfa.addTransition("c", "q2", "q3");
		nfa.addTransition("f", "q3", "q3");
		nfa.addTransition("d", "q4", "q3");
		nfa.addTransition("a", "q3", "q7");
		nfa.addTransition("e", "q1", "q5");
		nfa.addTransition("f", "q5", "q4");
		nfa.addTransition("c", "q4", "q6");
		nfa.addTransition("d", "q6", "q5");
		nfa.setInitialState("q1");
   		nfa.setFinalState("q7");
   		ShortestStringGenerator g = new ShortestStringGenerator(nfa);
		List<List<String>> paths = g.shortestPaths();
		assertEquals(2, paths.size());
		for (Iterator<List<String>> it = paths.iterator(); it.hasNext(); ) {
			List<String> path = it.next();
			assertTrue(concatList(path, "-").equals("a-c-a") ||
					   concatList(path, "-").equals("b-d-a"));
			assertEquals(3, path.size());
		}
	}

	public void test_shortestPaths2() throws Exception {
		SparseNFA nfa = new SparseNFA();
		nfa.addTransition("a", "q1", "q2");
		nfa.addTransition("b", "q2", "q2");
		nfa.addTransition("c", "q2", "q3");
		nfa.addTransition("f", "q3", "q3");
		nfa.addTransition("d", "q4", "q3");
		nfa.addTransition("a", "q3", "q7");
		nfa.addTransition("e", "q1", "q5");
		nfa.addTransition("f", "q5", "q4");
		nfa.addTransition("c", "q4", "q6");
		nfa.addTransition("d", "q6", "q5");
		nfa.setInitialState("q1");
   		nfa.setFinalState("q7");
   		ShortestStringGenerator g = new ShortestStringGenerator(nfa);
		List<List<String>> paths = g.shortestPaths();
		assertEquals(1, paths.size());
		for (Iterator<List<String>> it = paths.iterator(); it.hasNext(); ) {
			List<String> path = it.next();
			assertTrue(concatList(path, "-").equals("a-c-a"));
			assertEquals(3, path.size());
		}
	}

	public void test_shortestPaths3() throws Exception {
		SparseNFA nfa = new SparseNFA();
		nfa.addTransition("a", "q1", "q2");
		nfa.addTransition("b", "q1", "q4");
		nfa.addTransition("b", "q2", "q2");
		nfa.addTransition("e", "q2", "q8");
		nfa.addTransition("f", "q8", "q8");
		nfa.addTransition("c", "q2", "q3");
		nfa.addTransition("f", "q3", "q3");
		nfa.addTransition("d", "q4", "q3");
		nfa.addTransition("a", "q3", "q7");
		nfa.addTransition("e", "q1", "q5");
		nfa.addTransition("f", "q5", "q4");
		nfa.addTransition("c", "q4", "q6");
		nfa.addTransition("d", "q6", "q5");
		nfa.setInitialState("q1");
   		nfa.setFinalState("q7");
		nfa.addFinalState("q8");
   		ShortestStringGenerator g = new ShortestStringGenerator(nfa);
		List<List<String>> paths = g.shortestPaths();
		assertEquals(1, paths.size());
		for (Iterator<List<String>> it = paths.iterator(); it.hasNext(); ) {
			List<String> path = it.next();
			assertTrue(concatList(path, "-").equals("a-e"));
			assertEquals(2, path.size());
		}
	}

	public void test_shortestPaths4() throws Exception {
		SparseNFA nfa = new SparseNFA();
		nfa.addTransition("a", "q1", "q2");
		nfa.addTransition("b", "q1", "q4");
		nfa.addTransition("b", "q2", "q2");
		nfa.addTransition("e", "q6", "q8");
		nfa.addTransition("f", "q8", "q8");
		nfa.addTransition("c", "q2", "q3");
		nfa.addTransition("f", "q3", "q3");
		nfa.addTransition("d", "q4", "q3");
		nfa.addTransition("a", "q3", "q7");
		nfa.addTransition("e", "q1", "q5");
		nfa.addTransition("f", "q5", "q4");
		nfa.addTransition("c", "q4", "q6");
		nfa.addTransition("d", "q6", "q5");
		nfa.setInitialState("q1");
   		nfa.setFinalState("q7");
		nfa.addFinalState("q8");
   		ShortestStringGenerator g = new ShortestStringGenerator(nfa);
		List<List<String>> paths = g.shortestPaths();
		assertEquals(3, paths.size());
		for (Iterator<List<String>> it = paths.iterator(); it.hasNext(); ) {
			List<String> path = it.next();
			assertTrue(concatList(path, "-").equals("a-c-a") ||
					   concatList(path, "-").equals("b-d-a") ||
					   concatList(path, "-").equals("b-c-e"));
			assertEquals(3, path.size());
		}
	}

	public void test_shortestPaths5() throws Exception {
		SparseNFA nfa = new SparseNFA();
		nfa.addTransition("a", "q1", "q2");
		nfa.addTransition("b", "q1", "q4");
		nfa.addTransition("b", "q2", "q2");
		nfa.addTransition("e", "q6", "q9");
		nfa.addTransition("f", "q9", "q8");
		nfa.addTransition("f", "q8", "q8");
		nfa.addTransition("c", "q2", "q3");
		nfa.addTransition("f", "q3", "q3");
		nfa.addTransition("d", "q4", "q3");
		nfa.addTransition("a", "q3", "q7");
		nfa.addTransition("e", "q1", "q5");
		nfa.addTransition("f", "q5", "q4");
		nfa.addTransition("c", "q4", "q6");
		nfa.addTransition("d", "q6", "q5");
		nfa.setInitialState("q1");
   		nfa.setFinalState("q7");
		nfa.addFinalState("q8");
   		ShortestStringGenerator g = new ShortestStringGenerator(nfa);
		List<List<String>> paths = g.shortestPaths();
		assertEquals(2, paths.size());
		for (Iterator<List<String>> it = paths.iterator(); it.hasNext(); ) {
			List<String> path = it.next();
			assertTrue(concatList(path, "-").equals("a-c-a") ||
					   concatList(path, "-").equals("b-d-a"));
			assertEquals(3, path.size());
		}
	}

	public void test_shortestPathsEmptyLanguage() throws Exception {
		SparseNFA nfa = new SparseNFA();
		nfa.addTransition("a", "q1", "q2");
		nfa.addTransition("b", "q1", "q4");
		nfa.addTransition("b", "q2", "q2");
		nfa.addTransition("e", "q6", "q9");
		nfa.addTransition("f", "q9", "q8");
		nfa.addTransition("f", "q8", "q8");
		nfa.addTransition("c", "q2", "q3");
		nfa.addTransition("f", "q3", "q3");
		nfa.addTransition("d", "q4", "q3");
		nfa.addTransition("a", "q3", "q7");
		nfa.addTransition("e", "q1", "q5");
		nfa.addTransition("f", "q5", "q4");
		nfa.addTransition("c", "q4", "q6");
		nfa.addTransition("d", "q6", "q5");
		nfa.setInitialState("q1");
   		ShortestStringGenerator g = new ShortestStringGenerator(nfa);
		List<List<String>> paths = g.shortestPaths();
		assertEquals(0, paths.size());
        assertEquals(-1, g.shortestAcceptedStringLength());
	}

	public void test_shortestPaths7() throws Exception {
		SparseNFA nfa = new SparseNFA();
		nfa.addTransition("a", "q1", "q1");
		nfa.addTransition("a", "q1", "q2");
		nfa.addTransition("b", "q1", "q2");
		nfa.addTransition("c", "q2", "q2");
		nfa.addTransition("c", "q2", "q1");
		nfa.addTransition("d", "q2", "q1");
		nfa.addTransition("c", "q2", "q3");
		nfa.addTransition("d", "q2", "q3");
		nfa.addTransition("e", "q3", "q2");
		nfa.addTransition("e", "q3", "q4");
		nfa.setInitialState("q1");
   		nfa.setFinalState("q4");
   		ShortestStringGenerator g = new ShortestStringGenerator(nfa);
		List<List<String>> paths = g.shortestPaths();
		assertEquals(4, paths.size());
		for (Iterator<List<String>> it = paths.iterator(); it.hasNext(); ) {
			List<String> path = it.next();
			assertTrue(concatList(path, "-").equals("a-c-e") ||
					   concatList(path, "-").equals("b-c-e") ||
					   concatList(path, "-").equals("a-d-e") ||
					   concatList(path, "-").equals("b-d-e"));
			assertEquals(3, path.size());
		}
	}

	public void test_shortestPaths8() throws Exception {
		SparseNFA nfa = new SparseNFA();
		nfa.addTransition("a", "q1", "q2");
		nfa.addTransition("b", "q1", "q3");
		nfa.addTransition("c", "q2", "q4");
		nfa.addTransition("d", "q3", "q4");
		nfa.addTransition("e", "q4", "q5");
		nfa.addTransition("f", "q4", "q6");
		nfa.addTransition("g", "q5", "q7");
		nfa.addTransition("h", "q6", "q7");
		nfa.addTransition("i", "q7", "q8");
		nfa.setInitialState("q1");
   		nfa.setFinalState("q8");
   		ShortestStringGenerator g = new ShortestStringGenerator(nfa);
		List<List<String>> paths = g.shortestPaths();
		assertEquals(4, paths.size());
		for (Iterator<List<String>> it = paths.iterator(); it.hasNext(); ) {
			List<String> path = it.next();
			assertTrue(concatList(path, "-").equals("a-c-e-g-i") ||
					   concatList(path, "-").equals("a-c-f-h-i") ||
					   concatList(path, "-").equals("b-d-e-g-i") ||
					   concatList(path, "-").equals("b-d-f-h-i"));
			assertEquals(5, path.size());
		}
        assertEquals(5, g.shortestAcceptedStringLength());
	}

	public void test_shortestPathsEmptyString() throws Exception {
		SparseNFA nfa = new SparseNFA();
		nfa.addTransition("a", "q1", "q2");
		nfa.addTransition("b", "q1", "q4");
		nfa.addTransition("b", "q2", "q2");
		nfa.addTransition("e", "q6", "q9");
		nfa.addTransition("f", "q9", "q8");
		nfa.addTransition("f", "q8", "q8");
		nfa.addTransition("c", "q2", "q3");
		nfa.addTransition("f", "q3", "q3");
		nfa.addTransition("d", "q4", "q3");
		nfa.addTransition("a", "q3", "q7");
		nfa.addTransition("e", "q1", "q5");
		nfa.addTransition("f", "q5", "q4");
		nfa.addTransition("c", "q4", "q6");
		nfa.addTransition("d", "q6", "q5");
		nfa.setInitialState("q1");
   		nfa.setFinalState("q7");
		nfa.addFinalState("q8");
		nfa.addFinalState("q1");
   		ShortestStringGenerator g = new ShortestStringGenerator(nfa);
		List<List<String>> paths = g.shortestPaths();
		assertEquals(1, paths.size());
		for (Iterator<List<String>> it = paths.iterator(); it.hasNext(); ) {
			List<String> path = it.next();
			assertTrue(concatList(path, "-").equals(""));
			assertEquals(0, path.size());
		}
        assertEquals(0, g.shortestAcceptedStringLength());
	}

	protected static String concatList(List<String> list, String sep) {
		if (list.size() == 0) {
			return "";
		} else {
			StringBuffer str = new StringBuffer();
			str.append(list.get(0));
			for (int i = 1; i < list.size(); i++) {
				str.append(sep).append(list.get(i).toString());
			}
			return str.toString();
		}
	}

	protected static SparseNFA tree2NFA(Node node) throws Exception {
		String o = node.key();
		if (node.hasChildren()) {
			SparseNFA[] nfas = new SparseNFA[node.getNumberOfChildren()];
			for (int i = 0; i < node.getNumberOfChildren(); i++) {
				nfas[i] = tree2NFA(node.child(i));
			}
			if (o.equals(",")) {
				return ThompsonBuilder.concatenate(nfas);
			} else if (o.equals("|")) {
				return ThompsonBuilder.union(nfas);
			} else if (o.equals("+")) {
				return ThompsonBuilder.oneOrMore(nfas[0]);
			} else if (o.equals("*")) {
				return ThompsonBuilder.zeroOrMore(nfas[0]);
			} else if (o.equals("?")) {
				return ThompsonBuilder.zeroOrOne(nfas[0]);
			} else {
				throw new Exception("unknown operator");
			}
		} else {
			return ThompsonBuilder.symbolNFA(o);
		}
	}

}
