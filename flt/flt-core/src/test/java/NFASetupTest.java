import eu.fox7.flt.automata.NoSuchStateException;
import eu.fox7.flt.automata.converters.Simplifier;
import eu.fox7.flt.automata.factories.sparse.ThompsonBuilder;
import eu.fox7.flt.automata.impl.sparse.SparseNFA;
import eu.fox7.flt.automata.impl.sparse.State;
import eu.fox7.flt.automata.impl.sparse.Symbol;
import eu.fox7.flt.automata.impl.sparse.Transition;
import eu.fox7.flt.automata.matchers.NFAMatcher;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class NFASetupTest extends TestCase {
  
	protected SparseNFA nfa, emptyNFA, epsilonNFA, symbolNFA, sigmaStarNFA,
		removalNFA;

	static public void main(String args[]) {
		junit.textui.TestRunner.run(suite());
	}

	public NFASetupTest(String name) {
		super(name);
	}

	public static Test suite() {
		return new TestSuite(NFASetupTest.class);
	}

	protected void setUp() throws Exception {
	    super.setUp();
		nfa = new SparseNFA();
		nfa.addTransition("a", "q0", "q1");
		nfa.addTransition("b", "q1", "q2");
		nfa.addTransition("c", "q1", "q1");
		nfa.addTransition("a", "q1", "q0");
		nfa.addTransition("b", "q2", "q2");
		nfa.addTransition("c", "q2", "q1");
		nfa.setInitialState("q0");
		nfa.setFinalState("q0");
		emptyNFA = ThompsonBuilder.emptyNFA();
		epsilonNFA = ThompsonBuilder.epsilonNFA();
		symbolNFA = ThompsonBuilder.symbolNFA("a");
		String[] alphabet = {"a", "b", "c"};
		sigmaStarNFA = ThompsonBuilder.sigmaStarNFA(alphabet);
		removalNFA = new SparseNFA();
		removalNFA.addTransition("a", "q1", "q2");
		removalNFA.addTransition("b", "q1", "q4");
		removalNFA.addTransition("b", "q2", "q2");
		removalNFA.addTransition("d", "q2", "q3");
		removalNFA.addTransition("c", "q2", "q4");
		removalNFA.addTransition("e", "q3", "q3");
		removalNFA.addTransition("c", "q3", "q4");
		removalNFA.addTransition("a", "q3", "q1");
		removalNFA.addTransition("a", "q4", "q2");
		removalNFA.setInitialState("q1");
		removalNFA.setFinalState("q3");
	}

	@SuppressWarnings("deprecation")
    public void test_Alphabet() {
		Map<String,Symbol> alphabet = nfa.getAlphabet();
		assertTrue(alphabet.containsKey("a"));
		assertTrue(alphabet.containsKey("b"));
		assertTrue(alphabet.containsKey("c"));
		assertEquals(3, alphabet.size());
	}

	@SuppressWarnings("deprecation")
    public void test_States() {
		Map<String,State> states = nfa.getStateMap();
		assertTrue(states.containsKey("q0"));
		assertTrue(states.containsKey("q1"));
		assertTrue(states.containsKey("q2"));
		assertEquals(3, states.size());
		State q2 = nfa.getState("q2");
		String q2Value = nfa.getStateValue(q2);
		assertEquals("q2", q2Value);
	}

	@SuppressWarnings("deprecation")
    public void test_InitialState() {
		Map<String,State> states = nfa.getStateMap();
		State initialState = nfa.getInitialState();
		assertEquals(initialState, states.get("q0"));
	}

    public void test_StateRemoval1() {
		State removalState = removalNFA.getState("q1");
		assertEquals(4, removalNFA.getNumberOfStates());
		try {
	        removalNFA.removeState(removalState);
        } catch (NoSuchStateException e) {
        	fail("unexpected exception");
        }
		assertEquals(null, removalNFA.getInitialState());
		assertEquals(3, removalNFA.getNumberOfStates());
		Set<State> set = removalNFA.getNextStates(removalNFA.getState("q2"));
		assertEquals(3, set.size());
		assertTrue(set.contains(removalNFA.getState("q2")));
		assertTrue(set.contains(removalNFA.getState("q3")));
		assertTrue(set.contains(removalNFA.getState("q4")));
		set = removalNFA.getNextStates(removalNFA.getState("q3"));
		assertEquals(2, set.size());
		assertTrue(set.contains(removalNFA.getState("q3")));
		assertTrue(set.contains(removalNFA.getState("q4")));
		set = removalNFA.getNextStates(removalNFA.getState("q4"));
		assertEquals(1, set.size());
		assertTrue(set.contains(removalNFA.getState("q2")));
		assertEquals(1, removalNFA.getFinalStates().size());
	}

    public void test_StateRemoval2() {
		State removalState = removalNFA.getState("q2");
		assertEquals(4, removalNFA.getNumberOfStates());
		try {
	        removalNFA.removeState(removalState);
        } catch (NoSuchStateException e) {
        	fail("unexpected exception");
        }
		assertTrue(null != removalNFA.getInitialState());
		assertEquals(3, removalNFA.getNumberOfStates());
		Set<State> set = removalNFA.getNextStates(removalNFA.getState("q1"));
		assertEquals(1, set.size());
		assertTrue(set.contains(removalNFA.getState("q4")));
		set = removalNFA.getNextStates(removalNFA.getState("q3"));
		assertEquals(3, set.size());
		assertTrue(set.contains(removalNFA.getState("q1")));
		assertTrue(set.contains(removalNFA.getState("q3")));
		assertTrue(set.contains(removalNFA.getState("q4")));
		set = removalNFA.getNextStates(removalNFA.getState("q4"));
		assertEquals(0, set.size());
		assertEquals(1, removalNFA.getFinalStates().size());
	}

    public void test_StateRemoval3() {
		State removalState = removalNFA.getState("q3");
		assertEquals(4, removalNFA.getNumberOfStates());
		try {
	        removalNFA.removeState(removalState);
        } catch (NoSuchStateException e) {
        	fail("unexpected exception");
        }
		assertTrue(null != removalNFA.getInitialState());
		assertEquals(3, removalNFA.getNumberOfStates());
		Set<State> set = removalNFA.getNextStates(removalNFA.getState("q1"));
		assertEquals(2, set.size());
		assertTrue(set.contains(removalNFA.getState("q2")));
		assertTrue(set.contains(removalNFA.getState("q4")));
		set = removalNFA.getNextStates(removalNFA.getState("q2"));
		assertEquals(2, set.size());
		assertTrue(set.contains(removalNFA.getState("q2")));
		assertTrue(set.contains(removalNFA.getState("q4")));
		set = removalNFA.getNextStates(removalNFA.getState("q4"));
		assertEquals(1, set.size());
		assertTrue(set.contains(removalNFA.getState("q2")));
		assertEquals(0, removalNFA.getFinalStates().size());
	}

    public void test_StateRemoval4() {
		State removalState = removalNFA.getState("q4");
		assertEquals(4, removalNFA.getNumberOfStates());
		try {
	        removalNFA.removeState(removalState);
        } catch (NoSuchStateException e) {
        	fail("unexpected exception");
        }
		assertTrue(null != removalNFA.getInitialState());
		assertEquals(3, removalNFA.getNumberOfStates());
		Set<State> set = removalNFA.getNextStates(removalNFA.getState("q1"));
		assertEquals(1, set.size());
		assertTrue(set.contains(removalNFA.getState("q2")));
		set = removalNFA.getNextStates(removalNFA.getState("q2"));
		assertEquals(2, set.size());
		assertTrue(set.contains(removalNFA.getState("q2")));
		assertTrue(set.contains(removalNFA.getState("q3")));
		set = removalNFA.getNextStates(removalNFA.getState("q3"));
		assertEquals(2, set.size());
		assertTrue(set.contains(removalNFA.getState("q1")));
		assertTrue(set.contains(removalNFA.getState("q3")));
		assertEquals(1, removalNFA.getFinalStates().size());
	}

	@SuppressWarnings("deprecation")
    public void test_Transitions() {
		Map<String,Symbol> alphabet = nfa.getAlphabet();
		Map<String,State> states = nfa.getStateMap();
		Set<State> set = new HashSet<State>();
		set.add(states.get("q0"));
		assertEquals(1, nfa.getNextStates(alphabet.get("a"),
		                                  states.get("q1")).size());
		assertTrue(set.containsAll(nfa.getNextStates(alphabet.get("a"),
		                                             states.get("q1"))));
		set.clear();
		set.add(states.get("q1"));
		assertEquals(1, nfa.getNextStates(alphabet.get("a"),
		                                  states.get("q0")).size());
		assertTrue(set.containsAll(nfa.getNextStates(alphabet.get("a"),
		                                             states.get("q0"))));
		assertTrue(set.containsAll(nfa.getNextStates(alphabet.get("c"),
		                                             states.get("q2"))));
		assertTrue(set.containsAll(nfa.getNextStates(alphabet.get("c"),
		                                             states.get("q1"))));
		set.clear();
		set.add(states.get("q2"));
		assertTrue(set.containsAll(nfa.getNextStates(alphabet.get("b"),
		                                             states.get("q1"))));
		assertTrue(set.containsAll(nfa.getNextStates(alphabet.get("b"),
		                                             states.get("q2"))));
		set.clear();
	}

	@SuppressWarnings("deprecation")
    public void test_NextStates() {
		Map<String,State> states = nfa.getStateMap();
		State q0 = (State) states.get("q0");
		State q1 = (State) states.get("q1");
		State q2 = (State) states.get("q2");
		Set<State> q0Set = nfa.getNextStates(q0);
		assertTrue(q0Set.contains(q1));
		assertEquals(1, q0Set.size());
		Set<State> q1Set = nfa.getNextStates(q1);
		assertTrue(q1Set.contains(q0));
		assertTrue(q1Set.contains(q1));
		assertTrue(q1Set.contains(q2));
		assertEquals(3, q1Set.size());
		Set<State> q2Set = nfa.getNextStates(q2);
		assertTrue(q2Set.contains(q1));
		assertTrue(q2Set.contains(q2));
		assertEquals(2, q2Set.size());
	}

	@SuppressWarnings("deprecation")
    public void test_NextStatesEpsilon() {
		SparseNFA nfa = new SparseNFA();
		nfa.addTransition(Symbol.getEpsilon().toString(), "q0", "q1");
		nfa.addTransition("a", "q0", "q2");
		nfa.addTransition("b", "q1", "q2");
		nfa.addTransition(Symbol.getEpsilon().toString(), "q2", "q1");
		nfa.addTransition(Symbol.getEpsilon().toString(), "q1", "q1");
		nfa.addTransition("c", "q1", "q3");
		Map<String,State> states = nfa.getStateMap();
		State q0 = (State) states.get("q0");
		State q1 = (State) states.get("q1");
		State q2 = (State) states.get("q2");
		State q3 = (State) states.get("q3");
		Set<State> q0Set = nfa.getNextStates(q0);
		assertTrue(q0Set.contains(q1));
		assertTrue(q0Set.contains(q2));
		assertEquals(2, q0Set.size());
		Set<State> q1Set = nfa.getNextStates(q1);
		assertTrue(q1Set.contains(q1));
		assertTrue(q1Set.contains(q2));
		assertTrue(q1Set.contains(q3));
		assertEquals(3, q1Set.size());
		Set<State> q2Set = nfa.getNextStates(q2);
		assertTrue(q2Set.contains(q1));
		assertEquals(1, q2Set.size());
		Set<State> q3Set = nfa.getNextStates(q3);
		assertEquals(0, q3Set.size());
	}

	public void test_PreviousStates() {
		State q1 = removalNFA.getState("q1");
		State q2 = removalNFA.getState("q2");
		State q3 = removalNFA.getState("q3");
		State q4 = removalNFA.getState("q4");
		Set<State> states = removalNFA.getPreviousStates(q1);
		assertEquals(1, states.size());
		assertTrue(states.contains(q3));
		states = removalNFA.getPreviousStates(q2);
		assertEquals(3, states.size());
		assertTrue(states.contains(q1) &&
				   states.contains(q2) &&
				   states.contains(q4));
		states = removalNFA.getPreviousStates(q3);
		assertEquals(2, states.size());
		assertTrue(states.contains(q2) &&
				   states.contains(q3));
		states = removalNFA.getPreviousStates(q4);
		assertEquals(3, states.size());
		assertTrue(states.contains(q1) &&
				   states.contains(q2) &&
				   states.contains(q3));
	}

    public void test_incomingTransitions() {
        SparseNFA nfa = new SparseNFA();
        nfa.addTransition("a", "q0", "q1");
        nfa.addTransition("a", "q0", "q2");
        nfa.addTransition("b", "q0", "q1");
        nfa.addTransition("c", "q1", "q3");
        nfa.addTransition("d", "q2", "q3");
        nfa.addTransition("d", "q2", "q2");
        nfa.setInitialState("q0");
        nfa.addFinalState("q3");
        Set<Transition> transitions = nfa.getIncomingTransitions(nfa.getState("q0"));
        assertEquals("nr transitions to q0",
                     0, transitions.size());
        transitions = nfa.getIncomingTransitions(nfa.getState("q1"));
        assertEquals("nr transitions to q1",
                     2, transitions.size());
        assertTrue("q0, a -> q1",
                   transitions.contains(new Transition(Symbol.create("a"),
                                                       nfa.getState("q0"),
                                                       nfa.getState("q1"))));
        assertTrue("q0, b -> q1",
                   transitions.contains(new Transition(Symbol.create("b"),
                                                       nfa.getState("q0"),
                                                       nfa.getState("q1"))));
        assertTrue("q0, c -!-> q1",
                   !transitions.contains(new Transition(Symbol.create("c"),
                                                        nfa.getState("q0"),
                                                        nfa.getState("q1"))));
        transitions = nfa.getIncomingTransitions(nfa.getState("q2"));
        assertEquals("nr transitions to q2",
                     2, transitions.size());
        assertTrue("q0, a -> q2",
                   transitions.contains(new Transition(Symbol.create("a"),
                                                       nfa.getState("q0"),
                                                       nfa.getState("q2"))));
        assertTrue("q2, d -> q2",
                   transitions.contains(new Transition(Symbol.create("d"),
                                                       nfa.getState("q2"),
                                                       nfa.getState("q2"))));
        assertTrue("q2, c -!-> q3",
                   !transitions.contains(new Transition(Symbol.create("c"),
                                                        nfa.getState("q2"),
                                                        nfa.getState("q3"))));
        transitions = nfa.getIncomingTransitions(nfa.getState("q3"));
        assertEquals("nr transitions to q3",
                     2, transitions.size());
        assertTrue("q1, c -> q3",
                   transitions.contains(new Transition(Symbol.create("c"),
                                                       nfa.getState("q1"),
                                                       nfa.getState("q3"))));
        assertTrue("q2, d -> q3",
                   transitions.contains(new Transition(Symbol.create("d"),
                                                       nfa.getState("q2"),
                                                       nfa.getState("q3"))));
        assertTrue("q0, c -!-> q3",
                   !transitions.contains(new Transition(Symbol.create("c"),
                                                        nfa.getState("q0"),
                                                        nfa.getState("q3"))));
    }

    public void test_outgoingTransitions() {
        SparseNFA nfa = new SparseNFA();
        nfa.addTransition("a", "q0", "q1");
        nfa.addTransition("a", "q0", "q2");
        nfa.addTransition("b", "q0", "q1");
        nfa.addTransition("c", "q1", "q3");
        nfa.addTransition("d", "q2", "q3");
        nfa.addTransition("d", "q2", "q2");
        nfa.setInitialState("q0");
        nfa.addFinalState("q3");
        Set<Transition> transitions = nfa.getOutgoingTransitions(nfa.getState("q0"));
        assertEquals("nr transitions from q0",
                     3, transitions.size());
        assertTrue("q0, a -> q1",
                   transitions.contains(new Transition(Symbol.create("a"),
                                                       nfa.getState("q0"),
                                                       nfa.getState("q1"))));
        assertTrue("q0, b -> q1",
                   transitions.contains(new Transition(Symbol.create("b"),
                                                       nfa.getState("q0"),
                                                       nfa.getState("q1"))));
        assertTrue("q0, a -> q2",
                   transitions.contains(new Transition(Symbol.create("a"),
                                                       nfa.getState("q0"),
                                                       nfa.getState("q2"))));
        assertTrue("q0, c -!-> q1",
                   !transitions.contains(new Transition(Symbol.create("c"),
                                                        nfa.getState("q0"),
                                                        nfa.getState("q1"))));
        transitions = nfa.getOutgoingTransitions(nfa.getState("q1"));
        assertEquals("nr transitions from q1",
                     1, transitions.size());
        assertTrue("q1, c -> q3",
                   transitions.contains(new Transition(Symbol.create("c"),
                                                       nfa.getState("q1"),
                                                       nfa.getState("q3"))));
        transitions = nfa.getOutgoingTransitions(nfa.getState("q2"));
        assertEquals("nr transitions from q2",
                     2, transitions.size());
        assertTrue("q2, d -> q2",
                   transitions.contains(new Transition(Symbol.create("d"),
                                                       nfa.getState("q2"),
                                                       nfa.getState("q2"))));
        assertTrue("q2, d -> q3",
                   transitions.contains(new Transition(Symbol.create("d"),
                                                       nfa.getState("q2"),
                                                       nfa.getState("q3"))));
        assertTrue("q2, c -!-> q3",
                   !transitions.contains(new Transition(Symbol.create("c"),
                                                        nfa.getState("q2"),
                                                        nfa.getState("q3"))));
        transitions = nfa.getOutgoingTransitions(nfa.getState("q3"));
        assertEquals("nr transitions from q3",
                     0, transitions.size());
    }

    public void test_SourceAndSink() {
		SparseNFA nfa = new SparseNFA();
		nfa.addTransition("a", "q1", "q2");
		nfa.addTransition("b", "q1", "q3");
		nfa.addTransition("c", "q4", "q5");
		nfa.addTransition("d", "q4", "q2");
		nfa.addTransition("h", "q5", "q5");
		nfa.addTransition("f", "q2", "q3");
		nfa.addTransition("e", "q3", "q5");
		nfa.addTransition("g", "q3", "q6");
		nfa.addTransition("i", "q3", "q7");
		nfa.addTransition("k", "q7", "q3");
		nfa.addTransition("l", "q8", "q8");
		nfa.addTransition("j", "q8", "q6");
		nfa.addTransition("m", "q9", "q5");
		nfa.addTransition("n", "q8", "q10");
		nfa.setInitialState("q1");
		nfa.setFinalState("q6");
		nfa.addFinalState("q7");
		nfa.addFinalState("q9");
		State[] states = new State[11];
		for (int i = 1; i <= 10; i++) {
			states[i] = nfa.getState("q" + i);
		}
		assertTrue(!nfa.isSourceState(states[1]));
		assertTrue(!nfa.isSourceState(states[2]));
		assertTrue(!nfa.isSourceState(states[3]));
		assertTrue(nfa.isSourceState(states[4]));
		assertTrue(!nfa.isSourceState(states[5]));
		assertTrue(!nfa.isSourceState(states[6]));
		assertTrue(!nfa.isSourceState(states[7]));
		assertTrue(nfa.isSourceState(states[8]));
		assertTrue(nfa.isSourceState(states[9]));
		assertTrue(nfa.isSourceState(states[10]));
		assertTrue(!nfa.isSinkState(states[1]));
		assertTrue(!nfa.isSinkState(states[2]));
		assertTrue(!nfa.isSinkState(states[3]));
		assertTrue(!nfa.isSinkState(states[4]));
		assertTrue(nfa.isSinkState(states[5]));
		assertTrue(!nfa.isSinkState(states[6]));
		assertTrue(!nfa.isSinkState(states[7]));
		assertTrue(!nfa.isSinkState(states[8]));
		assertTrue(!nfa.isSinkState(states[9]));
		assertTrue(nfa.isSinkState(states[10]));
	}

	
    public void test_Simplify1() {
		SparseNFA nfa = new SparseNFA();
		nfa.addTransition("a", "q1", "q2");
		nfa.addTransition("b", "q1", "q3");
		nfa.addTransition("c", "q4", "q5");
		nfa.addTransition("d", "q4", "q2");
		nfa.addTransition("h", "q5", "q5");
		nfa.addTransition("f", "q2", "q3");
		nfa.addTransition("e", "q3", "q5");
		nfa.addTransition("g", "q3", "q6");
		nfa.addTransition("i", "q3", "q7");
		nfa.addTransition("k", "q7", "q3");
		nfa.addTransition("l", "q8", "q8");
		nfa.addTransition("j", "q8", "q6");
		nfa.addTransition("m", "q9", "q5");
		nfa.addTransition("n", "q8", "q10");
		nfa.setInitialState("q1");
		nfa.setFinalState("q6");
		nfa.addFinalState("q7");
		nfa.addFinalState("q9");
		State[] states = new State[11];
		for (int i = 1; i <= 10; i++) {
			states[i] = nfa.getState("q" + i);
		}
		Simplifier.simplify(nfa);
		assertEquals(5, nfa.getNumberOfStates());
		assertTrue(nfa.hasState("q1") &&
				   nfa.hasState("q2") &&
				   nfa.hasState("q3") &&
				   nfa.hasState("q6") &&
				   nfa.hasState("q7"));
		Set<State> set = nfa.getNextStates(nfa.getState("q1"));
		assertEquals(2, set.size());
		assertTrue(set.contains(nfa.getState("q2")) &&
				   set.contains(nfa.getState("q3")));
		set = nfa.getPreviousStates(nfa.getState("q1"));
		assertEquals(0, set.size());
		set = nfa.getNextStates(nfa.getState("q2"));
		assertEquals(1, set.size());
		assertTrue(set.contains(nfa.getState("q3")));
		set = nfa.getPreviousStates(nfa.getState("q2"));
		assertEquals(1, set.size());
		assertTrue(set.contains(nfa.getState("q1")));
		set = nfa.getNextStates(nfa.getState("q3"));
		assertEquals(2, set.size());
		assertTrue(set.contains(nfa.getState("q6")) &&
				   set.contains(nfa.getState("q7")));
		set = nfa.getPreviousStates(nfa.getState("q3"));
		assertEquals(3, set.size());
		assertTrue(set.contains(nfa.getState("q1")) &&
				   set.contains(nfa.getState("q2")) &&
				   set.contains(nfa.getState("q7")));
		set = nfa.getNextStates(nfa.getState("q6"));
		assertEquals(0, set.size());
		set = nfa.getPreviousStates(nfa.getState("q6"));
		assertEquals(1, set.size());
		assertTrue(set.contains(nfa.getState("q3")));
		set = nfa.getNextStates(nfa.getState("q7"));
		assertEquals(1, set.size());
		assertTrue(set.contains(nfa.getState("q3")));
		set = nfa.getPreviousStates(nfa.getState("q7"));
		assertEquals(1, set.size());
		assertTrue(set.contains(nfa.getState("q3")));
		assertEquals(6, nfa.getTransitionMap().getTransitions().size());
	}

    public void test_simplify2() {
	    SparseNFA nfa = new SparseNFA();
	    nfa.addTransition("a", "q0", "q1");
	    nfa.addTransition("b", "q0", "q2");
	    nfa.addTransition("a", "q2", "q1");
	    nfa.addTransition("b", "q1", "q2");
	    nfa.setInitialState("q0");
	    nfa.setFinalState("q0");
	    Simplifier.simplify(nfa);
	    assertEquals("number of states", 1, nfa.getNumberOfStates());
	    assertEquals("initial state", "q0", nfa.getStateValue(nfa.getInitialState()));
	    assertEquals("no transitions", 0, nfa.getNextStates(nfa.getInitialState()).size());
	}
	
	@SuppressWarnings("deprecation")
    public void test_FinalStates() {
		Set<State> finalStates = nfa.getFinalStates();
		Map<String,State> states = nfa.getStateMap();
		assertTrue(finalStates.contains(states.get("q0")));
		assertEquals(1, finalStates.size());
	}

	public void test_EmptyNFA() {
		String[] input1 = {};
		NFAMatcher matcher = new NFAMatcher(emptyNFA);
		assertTrue(!matcher.matches(input1));
		String[] input2 = {"a"};
		assertTrue(!matcher.matches(input2));
	}

	public void test_EpsilonNFA() {
		String[] input1 = {};
		NFAMatcher matcher = new NFAMatcher(epsilonNFA);
		assertTrue(matcher.matches(input1));
		String[] input2 = {"a"};
		assertTrue(!matcher.matches(input2));
	}

	public void test_SymbolNFA() {
		String[] input1 = {};
		NFAMatcher matcher = new NFAMatcher(symbolNFA);
		assertTrue(!matcher.matches(input1));
		String[] input2 = {"a"};
		assertTrue(matcher.matches(input2));
		String[] input3 = {"b"};
		assertTrue(!matcher.matches(input3));
	}

	public void test_SigmaStarNFA() {
		NFAMatcher matcher = new NFAMatcher(sigmaStarNFA);
		String[] input1 = {};
		assertTrue(matcher.matches(input1));
		String[] input2 = {"a"};
		assertTrue(matcher.matches(input2));
		String[] input3 = {"a", "c", "b", "a", "c"};
		assertTrue(matcher.matches(input3));
		String[] input4 = {"a", "d", "c"};
		assertTrue(!matcher.matches(input4));
	}

    public void test_hasTransition() {
        SparseNFA nfa = new SparseNFA();
        nfa.addTransition("a", "q0", "q1");
        nfa.addTransition("b", "q0", "q2");
        nfa.addTransition("a", "q1", "q2");
        nfa.addTransition("b", "q2", "q2");
        nfa.addTransition("c", "q2", "q1");
        nfa.setInitialState("q0");
        nfa.setFinalState("q2");
        assertTrue("transition exists", nfa.hasTransition("b", "q2", "q2"));
        assertTrue("transition does not exist",
                   !nfa.hasTransition("b", "q2", "q1"));
    }

}
