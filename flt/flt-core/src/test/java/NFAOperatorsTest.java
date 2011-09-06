import eu.fox7.flt.automata.NotDFAException;
import eu.fox7.flt.automata.converters.Simplifier;
import eu.fox7.flt.automata.factories.sparse.Determinizer;
import eu.fox7.flt.automata.factories.sparse.GlushkovFactory;
import eu.fox7.flt.automata.factories.sparse.ProductNFAFactory;
import eu.fox7.flt.automata.factories.sparse.ThompsonBuilder;
import eu.fox7.flt.automata.factories.sparse.ThompsonFactory;
import eu.fox7.flt.automata.impl.sparse.SparseNFA;
import eu.fox7.flt.automata.impl.sparse.State;
import eu.fox7.flt.automata.impl.sparse.Symbol;
import eu.fox7.flt.automata.matchers.NFAMatcher;
import eu.fox7.flt.automata.measures.EmptynessTest;
import eu.fox7.flt.automata.measures.EquivalenceTest;
import eu.fox7.util.tree.Node;
import eu.fox7.util.tree.Tree;

import java.io.StringReader;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class NFAOperatorsTest extends TestCase {
  
	protected SparseNFA nfa1, nfa2, nfa3, nfaA, nfaB, nfaC,
		nfaConcatenation, nfaStar, nfaPlus, nfaQ, nfaUnion,
		nfaIntersectionAB, nfaIntersectionBC, nfaIntersectionABC,
		nfaIntersection12;
	protected String treeRep1 = "(? (b))", treeRep2 = "(* (a))";

	public NFAOperatorsTest(String name) {
		super(name);
	}

	public static Test suite() {
		return new TestSuite(NFAOperatorsTest.class);
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
		nfaConcatenation = ThompsonBuilder.concatenate(nfas);
		nfaStar = ThompsonBuilder.zeroOrMore(nfa1);
		nfaPlus = ThompsonBuilder.oneOrMore(nfa1);
		nfaQ = ThompsonBuilder.zeroOrOne(nfa1);
		nfaUnion = ThompsonBuilder.union(nfas);
		nfaA = new SparseNFA();
		nfaA.addTransition("a", "q0", "q0");
		nfaA.addTransition("epsilon", "q0", "q1");
		nfaA.addTransition("b", "q1", "q1");
		nfaA.addTransition("epsilon", "q1", "q2");
		nfaA.addTransition("c", "q2", "q2");
		nfaA.setInitialState("q0");
		nfaA.setFinalState("q2");
		nfaB = new SparseNFA();
		nfaB.addTransition("b", "q0", "q0");
		nfaB.addTransition("epsilon", "q0", "q1");
		nfaB.addTransition("a", "q1", "q1");
		nfaB.addTransition("epsilon", "q1", "q2");
		nfaB.addTransition("c", "q2", "q2");
		nfaB.setInitialState("q0");
		nfaB.setFinalState("q2");
		nfaC = new SparseNFA();
		nfaC.addTransition("c", "q0", "q0");
		nfaC.addTransition("epsilon", "q0", "q1");
		nfaC.addTransition("b", "q1", "q1");
		nfaC.addTransition("epsilon", "q1", "q2");
		nfaC.addTransition("a", "q2", "q2");
		nfaC.setInitialState("q0");
		nfaC.setFinalState("q2");
		SparseNFA[] nfasABC = {nfaA, nfaB, nfaC};
		nfaIntersectionABC = ProductNFAFactory.intersection(nfasABC);
		SparseNFA[] nfasAB = {nfaA, nfaB};
		nfaIntersectionAB = ProductNFAFactory.intersection(nfasAB);
		SparseNFA nfasBC[] = {nfaB, nfaC};
		nfaIntersectionBC = ProductNFAFactory.intersection(nfasBC);
		try {
			Tree tree = new Tree(new StringReader(treeRep1));
			SparseNFA tmpNFA1 = tree2NFA(tree.getRoot());
			tree = new Tree(new StringReader(treeRep2));
			SparseNFA tmpNFA2 = tree2NFA(tree.getRoot());
			SparseNFA[] nfas12 = {tmpNFA1, tmpNFA2};
			nfaIntersection12 = ProductNFAFactory.intersection(nfas12);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("deprecation")
    public void test_ConcatenatedAlphabet() {
		Map<String,Symbol> alphabet = nfaConcatenation.getAlphabet();
		assertTrue(alphabet.containsKey("a"));
		assertTrue(alphabet.containsKey("b"));
		assertTrue(alphabet.containsKey("c"));
		assertTrue(alphabet.containsKey("d"));
		assertTrue(alphabet.containsKey("e"));
		assertTrue(alphabet.containsKey("f"));
		assertEquals(6, alphabet.size());
	}

    public void test_ConcatenatedStates() {
		assertEquals(9, nfaConcatenation.getNumberOfStates());
	}

	public void test_ConcatenatedInitialState() {
		assertEquals(nfa1.getInitialState(), nfaConcatenation.getInitialState());
	}

	public void test_ConcatenatedTransitions() {
		State state, state1, state2;
		Set<State> result;
		state = nfaConcatenation.getInitialState();
		result = nfaConcatenation.getNextStates(Symbol.create("a"), state);
		state = (State) extractSingleton(result);
		result = nfaConcatenation.getNextStates(Symbol.create("b"), state);
		state1 = (State) extractSingleton(result);
		result = nfaConcatenation.getNextStates(Symbol.create("c"), state);
		state2 = (State) extractSingleton(result);
		assertTrue(nfa1.getFinalStates().contains(state1));
		assertTrue(nfa1.getFinalStates().contains(state2));
		assertEquals(2, nfa1.getFinalStates().size());
		result = nfaConcatenation.getNextStates(Symbol.getEpsilon(), state1);
		state1 = (State) extractSingleton(result);
		result = nfaConcatenation.getNextStates(Symbol.getEpsilon(), state2);
		state2 = (State) extractSingleton(result);
		assertEquals(state1, state2);
		assertEquals(nfa2.getInitialState(), state1);
		result = nfaConcatenation.getNextStates(Symbol.create("d"), state1);
		state = (State) extractSingleton(result);
		assertTrue(nfa2.getFinalStates().contains(state));
		assertEquals(1, nfa2.getFinalStates().size());
		result = nfaConcatenation.getNextStates(Symbol.getEpsilon(), state);
		state = (State) extractSingleton(result);
		assertEquals(state, nfa3.getInitialState());
		result = nfaConcatenation.getNextStates(Symbol.create("e"), state);
		state1 = (State) extractSingleton(result);
		result = nfaConcatenation.getNextStates(Symbol.create("f"), state);
		state2 = (State) extractSingleton(result);
		assertTrue(nfa3.getFinalStates().contains(state1));
		assertTrue(nfa3.getFinalStates().contains(state2));
		assertEquals(2, nfa3.getFinalStates().size());
		assertTrue(nfaConcatenation.getFinalStates().contains(state1));
		assertTrue(nfaConcatenation.getFinalStates().contains(state2));
	}

	public void test_ConcatenatedFinalStates() {
		assertTrue(nfaConcatenation.getFinalStates().containsAll(nfa3.getFinalStates()));
		assertTrue(nfa3.getFinalStates().containsAll(nfaConcatenation.getFinalStates()));
		assertEquals(2, nfaConcatenation.getFinalStates().size());
	}

	public void test_ConcatenatedRun() {
		NFAMatcher matcher = new NFAMatcher(nfaConcatenation);
		String[] input1 = {"a", "b", "d", "e"};
		assertTrue(matcher.matches(input1));
		String[] input2 = {"a", "c", "d", "e"};
		assertTrue(matcher.matches(input2));
		String[] input3 = {"a", "b", "d", "f"};
		assertTrue(matcher.matches(input3));
		String[] input4 = {"a", "c", "d", "f"};
		assertTrue(matcher.matches(input4));
		String[] input5 = {"a", "d", "f"};
		assertTrue(!matcher.matches(input5));
		String[] input6 = {"a", "b", "c", "d"};
		assertTrue(!matcher.matches(input6));
		String[] input7 = {"a", "q", "c", "d"};
		assertTrue(!matcher.matches(input7));
		String[] input8 = {};
		assertTrue(!matcher.matches(input8));
	}

	public void test_ZeroOrOneRun() {
		NFAMatcher matcher = new NFAMatcher(nfaQ);
		String[] input1 = {"a", "b", "a", "c"};
		assertTrue(!matcher.matches(input1));
		String[] input2 = {"a", "c", "a", "b"};
		assertTrue(!matcher.matches(input2));
		String[] input3 = {"a", "b", "a", "b"};
		assertTrue(!matcher.matches(input3));
		String[] input4 = {"a", "c", "a", "c"};
		assertTrue(!matcher.matches(input4));
		String[] input5 = {"a", "c"};
		assertTrue(matcher.matches(input5));
		String[] input6 = {"a", "b"};
		assertTrue(matcher.matches(input6));
		String[] input7 = {"a", "q", "c"};
		assertTrue(!matcher.matches(input7));
		String[] input8 = {"a", "a"};
		assertTrue(!matcher.matches(input8));
		String[] input9 = {"a", "c", "a", "b", "a", "b", "a", "b", "a", "b"};
		assertTrue(!matcher.matches(input9));
		String[] input10 = {"a", "c", "a", "b", "a", "b", "c", "b", "a", "b"};
		assertTrue(!matcher.matches(input10));
		String[] input11 = {};
		assertTrue(matcher.matches(input11));
	}

    public void test_ZeroOrOneRun2() throws Exception {
        ThompsonFactory regex = new ThompsonFactory();
        SparseNFA nfa = regex.create("(? (. (+ (b)) (a)))");
		NFAMatcher matcher = new NFAMatcher(nfa);
        String[] input1 = {"b", "b", "a"};
        assertTrue(matcher.matches(input1));
        String[] input2 = {"b", "b"};
        assertTrue(!matcher.matches(input2));
    }

	public void test_ZeroOrMoreRun() {
		NFAMatcher matcher = new NFAMatcher(nfaStar);
		String[] input1 = {"a", "b", "a", "c"};
		assertTrue(matcher.matches(input1));
		String[] input2 = {"a", "c", "a", "b"};
		assertTrue(matcher.matches(input2));
		String[] input3 = {"a", "b", "a", "b"};
		assertTrue(matcher.matches(input3));
		String[] input4 = {"a", "c", "a", "c"};
		assertTrue(matcher.matches(input4));
		String[] input5 = {"a", "c"};
		assertTrue(matcher.matches(input5));
		String[] input6 = {"a", "b", "c"};
		assertTrue(!matcher.matches(input6));
		String[] input7 = {"a", "q", "c"};
		assertTrue(!matcher.matches(input7));
		String[] input8 = {"a", "a"};
		assertTrue(!matcher.matches(input8));
		String[] input9 = {"a", "c", "a", "b", "a", "b", "a", "b", "a", "b"};
		assertTrue(matcher.matches(input9));
		String[] input10 = {"a", "c", "a", "b", "a", "b", "c", "b", "a", "b"};
		assertTrue(!matcher.matches(input10));
		String[] input11 = {};
		assertTrue(matcher.matches(input11));
	}

	public void test_OneOrMoreRun() {
		NFAMatcher matcher = new NFAMatcher(nfaPlus);
		String[] input1 = {"a", "b", "a", "c"};
		assertTrue(matcher.matches(input1));
		String[] input2 = {"a", "c", "a", "b"};
		assertTrue(matcher.matches(input2));
		String[] input3 = {"a", "b", "a", "b"};
		assertTrue(matcher.matches(input3));
		String[] input4 = {"a", "c", "a", "c"};
		assertTrue(matcher.matches(input4));
		String[] input5 = {"a", "c"};
		assertTrue(matcher.matches(input5));
		String[] input6 = {"a", "b", "c"};
		assertTrue(!matcher.matches(input6));
		String[] input7 = {"a", "q", "c"};
		assertTrue(!matcher.matches(input7));
		String[] input8 = {"a", "a"};
		assertTrue(!matcher.matches(input8));
		String[] input9 = {"a", "c", "a", "b", "a", "b", "a", "b", "a", "b"};
		assertTrue(matcher.matches(input9));
		String[] input10 = {"a", "c", "a", "b", "a", "b", "c", "b", "a", "b"};
		assertTrue(!matcher.matches(input10));
		String[] input11 = {};
		assertTrue(!matcher.matches(input11));
	}

	public void test_UnionRun() {
		NFAMatcher matcher = new NFAMatcher(nfaUnion);
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

	public void test_intersectionAB() {
		NFAMatcher matcher = new NFAMatcher(nfaA);
		String[] input1 = {"a", "a", "a", "b", "b", "c", "c", "c"};
		assertTrue(matcher.matches(input1));
		matcher = new NFAMatcher(nfaB);
		String[] input2 = {"b", "b", "a", "a", "a", "c", "c", "c"};
		assertTrue(matcher.matches(input2));
		matcher = new NFAMatcher(nfaIntersectionAB);
		assertTrue(!matcher.matches(input1));
		assertTrue(!matcher.matches(input2));
		String[] input3 = {"a", "a", "a", "c", "c"};
		assertTrue(matcher.matches(input3));
		String[] input4 = {"b", "b", "b", "c", "c"};
		assertTrue(matcher.matches(input4));
		String[] input5 = {"a", "a", "a"};
		assertTrue(matcher.matches(input5));
		String[] input6 = {"b", "b", "b"};
		assertTrue(matcher.matches(input6));
		String[] input7 = {"c", "c", "c"};
		assertTrue(matcher.matches(input7));
		String[] input8 = {"c", "c", "c", "a", "a"};
		assertTrue(!matcher.matches(input8));
		String[] input9 = {"c", "c", "c", "b", "b"};
		assertTrue(!matcher.matches(input9));
		String[] input10 = {"b", "b", "a", "a", "a"};
		assertTrue(!matcher.matches(input10));
		String[] input11 = {};
		assertTrue(matcher.matches(input11));
	}

	public void test_intersectionBC() {
		NFAMatcher matcher = new NFAMatcher(nfaIntersectionBC);
		String[] input1 = {"c", "c", "c", "b", "b", "a", "a", "a"};
		assertTrue(!matcher.matches(input1));
		String[] input2 = {"b", "b", "a", "a", "a", "c", "c", "c"};
		assertTrue(!matcher.matches(input2));
		String[] input3 = {"a", "a", "a", "c", "c"};
		assertTrue(!matcher.matches(input3));
		String[] input4 = {"b", "b", "b", "c", "c"};
		assertTrue(!matcher.matches(input4));
		String[] input5 = {"a", "a", "a"};
		assertTrue(matcher.matches(input5));
		String[] input6 = {"b", "b", "b"};
		assertTrue(matcher.matches(input6));
		String[] input7 = {"c", "c", "c"};
		assertTrue(matcher.matches(input7));
		String[] input8 = {"c", "c", "c", "a", "a"};
		assertTrue(!matcher.matches(input8));
		String[] input9 = {"c", "c", "c", "b", "b"};
		assertTrue(!matcher.matches(input9));
		String[] input10 = {"b", "b", "a", "a", "a"};
		assertTrue(matcher.matches(input10));
		String[] input11 = {};
		assertTrue(matcher.matches(input11));
	}

	public void test_intersectionABC() {
		NFAMatcher matcher  = new NFAMatcher(nfaIntersectionABC);
		String[] input1 = {"c", "c", "c", "b", "b", "a", "a", "a"};
		assertTrue(!matcher.matches(input1));
		String[] input2 = {"b", "b", "a", "a", "a", "c", "c", "c"};
		assertTrue(!matcher.matches(input2));
		String[] input3 = {"a", "a", "a", "c", "c"};
		assertTrue(!matcher.matches(input3));
		String[] input4 = {"b", "b", "b", "c", "c"};
		assertTrue(!matcher.matches(input4));
		String[] input5 = {"a", "a", "a"};
		assertTrue(matcher.matches(input5));
		String[] input6 = {"b", "b", "b"};
		assertTrue(matcher.matches(input6));
		String[] input7 = {"c", "c", "c"};
		assertTrue(matcher.matches(input7));
		String[] input8 = {"c", "c", "c", "a", "a"};
		assertTrue(!matcher.matches(input8));
		String[] input9 = {"c", "c", "c", "b", "b"};
		assertTrue(!matcher.matches(input9));
		String[] input10 = {"b", "b", "a", "a", "a"};
		assertTrue(!matcher.matches(input10));
		String[] input11 = {};
		assertTrue(matcher.matches(input11));
	}

	public void test_emptyStringIntersection() throws Exception {
		NFAMatcher matcher = new NFAMatcher(nfaIntersection12);
		assertTrue(nfaIntersection12 != null);
		String[] input1 = {};
   		assertTrue(matcher.matches(input1));
		String[] input2 = {"a"};
		assertTrue(!matcher.matches(input2));
		String[] input3 = {"b"};
		assertTrue(!matcher.matches(input3));
	}

	public void test_identicalIntersection() throws Exception {
		Tree tree = new Tree(new StringReader("(| (a) (b))"));
		SparseNFA tmpNFA1 = tree2NFA(tree.getRoot());
		tree = new Tree(new StringReader("(| (a) (b))"));
		SparseNFA tmpNFA2 = tree2NFA(tree.getRoot());
		SparseNFA[] nfas = {tmpNFA1, tmpNFA2};
		SparseNFA nfa = ProductNFAFactory.intersection(nfas);
		NFAMatcher matcher = new NFAMatcher(nfa);
		String[] input1 = {"a"};
		assertTrue(matcher.matches(input1));
		String[] input2 = {"b"};
		assertTrue(matcher.matches(input2));
		String[] input3 = {};
		assertTrue(!matcher.matches(input3));
		String[] input4 = {"a", "b"};
		assertTrue(!matcher.matches(input4));
		String[] input5 = {"c"};
		assertTrue(!matcher.matches(input5));
	}

	public void test_emptyIntersection() throws Exception {
		Tree tree = new Tree(new StringReader("(| (a) (b))"));
		SparseNFA tmpNFA1 = tree2NFA(tree.getRoot());
		tree = new Tree(new StringReader("(, (| (a) (b)) (a))"));
		SparseNFA tmpNFA2 = tree2NFA(tree.getRoot());
		SparseNFA[] nfas = {tmpNFA1, tmpNFA2};
		SparseNFA nfa = ProductNFAFactory.intersection(nfas);
		NFAMatcher matcher = new NFAMatcher(nfa);
		String[] input1 = {"a"};
		assertTrue(!matcher.matches(input1));
		String[] input2 = {"b"};
		assertTrue(!matcher.matches(input2));
		String[] input3 = {};
		assertTrue(!matcher.matches(input3));
		String[] input4 = {"a", "b"};
		assertTrue(!matcher.matches(input4));
		String[] input5 = {"a", "a"};
		assertTrue(!matcher.matches(input5));
		String[] input6 = {"c"};
		assertTrue(!matcher.matches(input6));
		EmptynessTest emptyTest = new EmptynessTest();
		assertTrue(emptyTest.test(nfa));
	}

	@SuppressWarnings("deprecation")
    public void test_intersectionLabels() throws Exception {
	    SparseNFA nfa1 = new SparseNFA();
	    nfa1.addTransition("a", "q0", "q1");
	    nfa1.addTransition("b", "q1", "q1");
	    nfa1.addTransition("a", "q1", "q2");
	    nfa1.addTransition("a", "q2", "q2");
	    nfa1.setInitialState("q0");
	    nfa1.addFinalState("q0");
	    nfa1.setFinalState("q2");
	    SparseNFA nfa2 = new SparseNFA();
	    nfa2.addTransition("a", "q0", "q0");
	    nfa2.addTransition("b", "q0", "q1");
	    nfa2.addTransition("a", "q1", "q2");
	    nfa2.addTransition("b", "q2", "q2");
	    nfa2.setInitialState("q0");
	    nfa2.setFinalState("q2");
	    SparseNFA[] nfas = {nfa1, nfa2};
	    SparseNFA nfa = ProductNFAFactory.intersection(nfas);
	    Simplifier.simplify(nfa);
	    assertEquals(4, nfa.getStateMap().size());
	    for (Iterator<String> it = nfa.getStateValues().iterator(); it.hasNext(); ) {
	        String stateValue = it.next();
	        assertTrue(stateValue.equals("[q0, q0]") ||
	                stateValue.equals("[q1, q0]") ||
	                stateValue.equals("[q1, q1]") ||
	                stateValue.equals("[q2, q2]"));
	    }
	}
	
	@SuppressWarnings("deprecation")
    public void test_complete() throws Exception {
	    SparseNFA nfa = new SparseNFA();
	    nfa.addTransition("a", "q0", "q1");
	    nfa.addTransition("b", "q1", "q2");
	    nfa.setInitialState("q0");
	    nfa.setFinalState("q2");
	    SparseNFA nfaC = ThompsonBuilder.complete(nfa);
	    assertEquals(4, nfaC.getStateMap().size());
	    assertTrue(nfaC.isInitialState("q0"));
	    assertTrue(nfaC.isFinalState("q2"));
	    assertTrue(nfaC.hasState("q_sink"));
	}
	
	public void test_complement1() throws Exception {
		Tree tree = new Tree(new StringReader("(* (, (a) (a)))"));
		SparseNFA nfa = tree2NFA(tree.getRoot());
		NFAMatcher matcher = new NFAMatcher(nfa);
		String[] s0 = {};
		String[] s1 = {"a"};
		String[] s2 = {"a", "a"};
		String[] s3 = {"a", "a", "a"};
		String[] s4 = {"a", "a", "a", "a"};
		String[] s5 = {"a", "a", "a", "a", "a"};
		String[] s6 = {"a", "a", "a", "a", "a", "a"};
		assertTrue(matcher.matches(s0));
		assertTrue(!matcher.matches(s1));
		assertTrue(matcher.matches(s2));
		assertTrue(!matcher.matches(s3));
		assertTrue(matcher.matches(s4));
		assertTrue(!matcher.matches(s5));
		assertTrue(matcher.matches(s6));
		SparseNFA nfaC = ThompsonBuilder.complement(nfa);
		assertTrue(matcher.matches(s0));
		assertTrue(!matcher.matches(s1));
		assertTrue(matcher.matches(s2));
		assertTrue(!matcher.matches(s3));
		assertTrue(matcher.matches(s4));
		assertTrue(!matcher.matches(s5));
		assertTrue(matcher.matches(s6));
		matcher = new NFAMatcher(nfaC);
		assertTrue(!matcher.matches(s0));
		assertTrue(matcher.matches(s1));
		assertTrue(!matcher.matches(s2));
		assertTrue(matcher.matches(s3));
		assertTrue(!matcher.matches(s4));
		assertTrue(matcher.matches(s5));
		assertTrue(!matcher.matches(s6));
	}

	public void test_complement2() throws Exception {
		SparseNFA nfa = ThompsonBuilder.emptyNFA();
		String[] s0 = {};
		String[] s1 = {"a"};
		String[] s2 = {"a", "b"};
		String[] s3 = {"b", "a", "c"};
		String[] s4 = {"b", "d"};
		NFAMatcher matcher = new NFAMatcher(nfa);
		assertTrue(!matcher.matches(s0));
		assertTrue(!matcher.matches(s1));
		assertTrue(!matcher.matches(s2));
		assertTrue(!matcher.matches(s3));
		assertTrue(!matcher.matches(s4));
		Set<String> symbols = new HashSet<String>();
		symbols.add("a");
		symbols.add("b");
		symbols.add("c");
		SparseNFA nfaC = ThompsonBuilder.complement(nfa, symbols);
		assertTrue(!matcher.matches(s0));
		assertTrue(!matcher.matches(s1));
		assertTrue(!matcher.matches(s2));
		assertTrue(!matcher.matches(s3));
		assertTrue(!matcher.matches(s4));
		matcher = new NFAMatcher(nfaC);
		assertTrue(matcher.matches(s0));
		assertTrue(matcher.matches(s1));
		assertTrue(matcher.matches(s2));
		assertTrue(matcher.matches(s3));
		assertTrue(!matcher.matches(s4));
	}

	public void test_complement3() throws Exception {
		SparseNFA nfa = new SparseNFA();
		nfa.addTransition("a", "q0", "q1");
		nfa.addTransition("b", "q1", "q2");
		nfa.setInitialState("q0");
		nfa.setFinalState("q2");
		String[] s0 = {};
		String[] s1 = {"a"};
		String[] s2 = {"a", "b"};
		String[] s3 = {"b"};
		String[] s4 = {"a", "b", "a"};
		String[] s5 = {"c", "b", "a"};
		NFAMatcher matcher = new NFAMatcher(nfa);
		assertTrue(!matcher.matches(s0));
		assertTrue(!matcher.matches(s1));
		assertTrue(matcher.matches(s2));
		assertTrue(!matcher.matches(s3));
		assertTrue(!matcher.matches(s4));
		assertTrue(!matcher.matches(s5));
		SparseNFA nfaC = ThompsonBuilder.complement(nfa);
		/* check whether original automaton is unaltered */
		assertTrue(!matcher.matches(s0));
		assertTrue(!matcher.matches(s1));
		assertTrue(matcher.matches(s2));
		assertTrue(!matcher.matches(s3));
		assertTrue(!matcher.matches(s4));
		assertTrue(!matcher.matches(s5));
		matcher = new NFAMatcher(nfaC);
		assertTrue(matcher.matches(s0));
		assertTrue(matcher.matches(s1));
		assertTrue(!matcher.matches(s2));
		assertTrue(matcher.matches(s3));
		assertTrue(matcher.matches(s4));
		assertTrue(!matcher.matches(s5));
	}

	public void test_complement4() throws Exception {
		SparseNFA nfa = new SparseNFA();
		nfa.addTransition("a", "q0", "q1");
		nfa.addTransition("b", "q1", "q2");
		nfa.setInitialState("q0");
		nfa.setFinalState("q2");
		String[] s0 = {};
		String[] s1 = {"a"};
		String[] s2 = {"a", "b"};
		String[] s3 = {"b"};
		String[] s4 = {"a", "b", "a"};
		String[] s5 = {"c", "b", "a"};
		String[] s6 = {"c"};
		String[] s7 = {"a", "c"};
		NFAMatcher matcher = new NFAMatcher(nfa);
		assertTrue(!matcher.matches(s0));
		assertTrue(!matcher.matches(s1));
		assertTrue(matcher.matches(s2));
		assertTrue(!matcher.matches(s3));
		assertTrue(!matcher.matches(s4));
		assertTrue(!matcher.matches(s5));
		assertTrue(!matcher.matches(s6));
		assertTrue(!matcher.matches(s7));
		Set<String> symbols = new HashSet<String>();
		symbols.add("a");
		symbols.add("b");
		symbols.add("c");
		SparseNFA nfaC = ThompsonBuilder.complement(nfa);
		matcher = new NFAMatcher(nfaC);
		assertTrue(matcher.matches(s0));
		assertTrue(matcher.matches(s1));
		assertTrue(!matcher.matches(s2));
		assertTrue(matcher.matches(s3));
		assertTrue(matcher.matches(s4));
		assertTrue(!matcher.matches(s5));
		assertTrue(!matcher.matches(s6));
		assertTrue(!matcher.matches(s7));
		SparseNFA nfaC2 = ThompsonBuilder.complement(nfa, symbols);
		matcher = new NFAMatcher(nfaC2);
		assertTrue(matcher.matches(s0));
		assertTrue(matcher.matches(s1));
		assertTrue(!matcher.matches(s2));
		assertTrue(matcher.matches(s3));
		assertTrue(matcher.matches(s4));
		assertTrue(matcher.matches(s5));
		assertTrue(matcher.matches(s6));
		assertTrue(matcher.matches(s7));
	}

	public void test_anyCharNFA() throws Exception {
		String[] chars = {"a", "b", "c"};
		SparseNFA nfa = ThompsonBuilder.anyCharNFA(chars);
		NFAMatcher matcher = new NFAMatcher(nfa);
		String[] s0 = {};
		String[] s1 = {"a"};
		String[] s2 = {"b"};
		String[] s3 = {"c"};
		String[] s4 = {"d"};
		String[] s5 = {"a", "c"};
		assertTrue(!matcher.matches(s0));
		assertTrue(matcher.matches(s1));
		assertTrue(matcher.matches(s2));
		assertTrue(matcher.matches(s3));
		assertTrue(!matcher.matches(s4));
		assertTrue(!matcher.matches(s5));
	}

    public void test_equivalentNFAs() throws Exception {
        String regexStr = "(+ (| (a) (. (b) (c)) (. (a) (d))))";
        ThompsonFactory regex = new ThompsonFactory();
        GlushkovFactory glushkov = new GlushkovFactory();
        SparseNFA nfas[] = {
                Determinizer.dfa(regex.create(regexStr)),
                Determinizer.dfa(glushkov.create(regexStr))
        };
        assertTrue("equivalent", EquivalenceTest.areEquivalent(nfas));
    }

    public void test_equivalentNFAs2() throws Exception {
        SparseNFA nfa1 = new SparseNFA();
        nfa1.addTransition("a", "q_I", "a");
        nfa1.addTransition("c", "q_I", "c");
        nfa1.addTransition("a", "a", "a");
        nfa1.addTransition("b", "a", "b");
        nfa1.addTransition("b", "b", "b");
        nfa1.addTransition("c", "b", "c");
        nfa1.setInitialState("q_I");
        nfa1.addFinalState("c");
        
        SparseNFA nfa2 = new SparseNFA();
        nfa2.addTransition("a", "q_I", "#a");
        nfa2.addTransition("c", "q_I", "#c");
        nfa2.addTransition("a", "#a", "aa");
        nfa2.addTransition("b", "#a", "ab");
        nfa2.addTransition("a", "aa", "aa");
        nfa2.addTransition("b", "aa", "ab");
        nfa2.addTransition("b", "ab", "bb");
        nfa2.addTransition("b", "bb", "bb");
        nfa2.addTransition("c", "ab", "bc");
        nfa2.addTransition("c", "bb", "bc");
        nfa2.setInitialState("q_I");
        nfa2.addFinalState("#c");
        nfa2.addFinalState("bc");

        assertTrue("equiv", EquivalenceTest.areEquivalent(new SparseNFA[] {nfa1, nfa2}));
    }

    public void test_notEquivalentNFAs() throws Exception {
        String regexStr1 = "(+ (| (a) (. (b) (? (c))) (. (a) (d))))";
        String regexStr2 = "(+ (| (a) (. (b) (c)) (. (a) (d))))";
        ThompsonFactory regex = new ThompsonFactory();
        GlushkovFactory glushkov = new GlushkovFactory();
        SparseNFA nfas[] = {
                Determinizer.dfa(regex.create(regexStr1)),
                Determinizer.dfa(glushkov.create(regexStr2))
        };
        assertTrue("not equivalent", !EquivalenceTest.areEquivalent(nfas));
    }

    public void test_equivalentNFAsException() throws Exception {
        String regexStr = "(+ (| (a) (. (b) (c)) (. (a) (d))))";
        ThompsonFactory regex = new ThompsonFactory();
        GlushkovFactory glushkov = new GlushkovFactory();
        SparseNFA nfas[] = {
                Determinizer.dfa(regex.create(regexStr)),
                glushkov.create(regexStr)
        };
        try {
            EquivalenceTest.areEquivalent(nfas);
            fail("nondeterministic NFA");
        } catch (NotDFAException e) {}
    }

    public void test_nonEquivalentNFAs2() throws Exception {
        SparseNFA nfa1 = new SparseNFA();
        nfa1.addTransition("a", "q0", "21");
        nfa1.addTransition("b", "q0", "q2");
        nfa1.addTransition("d", "q1", "q3");
        nfa1.addTransition("b", "q2", "q2");
        nfa1.addTransition("c", "q2", "q3");
        nfa1.addTransition("d", "q3", "q1");
        nfa1.addTransition("e", "q3", "q4");
        nfa1.setInitialState("q0");
        nfa1.addFinalState("q4");
        SparseNFA nfa2 = new SparseNFA();
        nfa2.addTransition("d", "q1", "q3");
        nfa2.addTransition("d", "q3", "q1");
        nfa2.addTransition("e", "q3", "q4");
        nfa2.setInitialState("q1");
        nfa2.addFinalState("q4");
        assertTrue("bug", !EquivalenceTest.areEquivalent(new SparseNFA[] {nfa1, nfa2}));
    }

    public void test_nonEquivalentNFAs3() throws Exception {
        SparseNFA nfa1 = new SparseNFA();
        nfa1.addTransition("td", "tr", "td");
        nfa1.setInitialState("tr");
        nfa1.addFinalState("td");
        SparseNFA nfa2 = new SparseNFA();
        nfa2.addTransition("td", "tr", "td");
        nfa2.addTransition("p", "td", "p");
        nfa2.addTransition("br", "p", "br#1");
        nfa2.addTransition("h1", "td", "h1");
        nfa2.addTransition("br", "h1", "br#2");
        nfa2.addTransition("br", "td", "br#3");
        nfa2.addTransition("ul", "td", "ul");
        nfa2.addTransition("li", "ul", "li");
        nfa2.addTransition("br", "li", "br#4");
        nfa2.addTransition("form", "td", "form");
        nfa2.addTransition("div", "form", "div");
        nfa2.addTransition("input", "div", "input");
        nfa2.setInitialState("tr");
        nfa2.addFinalState("td");
        nfa2.addFinalState("br#1");
        nfa2.addFinalState("br#2");
        nfa2.addFinalState("br#3");
        nfa2.addFinalState("br#4");
        nfa2.addFinalState("input");
        assertTrue("bug?", !EquivalenceTest.areEquivalent(new SparseNFA[] {nfa1, nfa2}));
        assertTrue("bug?", !EquivalenceTest.areEquivalent(new SparseNFA[] {nfa2, nfa1}));
    }

	protected static State extractSingleton(Set<State> set) {
		if (set.size() == 1) {
			State[] array = set.toArray(new State[0]);
			return array[0];
		} else {
			return null;
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
