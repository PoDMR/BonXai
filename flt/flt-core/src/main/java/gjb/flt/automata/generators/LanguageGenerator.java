/**
 * Created on Oct 7, 2009
 * Modified on $Date: 2009-11-12 22:18:04 $
 */
package gjb.flt.automata.generators;

import gjb.flt.automata.FeatureNotSupportedException;
import gjb.flt.automata.factories.sparse.GlushkovFactory;
import gjb.flt.automata.impl.sparse.State;
import gjb.flt.automata.impl.sparse.StateNFA;
import gjb.flt.automata.impl.sparse.Symbol;
import gjb.flt.automata.impl.sparse.Transition;
import gjb.flt.automata.matchers.NFAMatcher;
import gjb.flt.regex.UnknownOperatorException;
import gjb.util.tree.Node;
import gjb.util.tree.SExpressionParseException;
import gjb.util.tree.Tree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.lang.math.RandomUtils;

/**
 * @author lucg5005
 * @version $Revision: 1.4 $
 *
 */
public class LanguageGenerator {

	protected StateNFA nfa;
	protected NFAMatcher matcher;
    /**
     * Map that associates states with the leaves in the computation tree
     */
    protected Map<State,Set<Node>> leafNodes = new HashMap<State,Set<Node>>();
    /**
     * Map that associates each state with its outgoing transitions
     */
    protected Map<State,List<Transition>> stateTransitionMap;

    public LanguageGenerator(String regexStr)
            throws SExpressionParseException, UnknownOperatorException,
                   FeatureNotSupportedException {
    	this(regexStr, null);
    }

    public LanguageGenerator(String regexStr, Properties properties)
            throws SExpressionParseException, UnknownOperatorException,
                   FeatureNotSupportedException {
    	this((new GlushkovFactory(properties)).create(regexStr));
    }

    public LanguageGenerator(StateNFA nfa) {
		this.nfa = nfa;
		this.matcher = new NFAMatcher(nfa);
	}

	/**
     * method that prepares the automaton for a run, it initializes the required
     * data structures
     * @return Tree that will represent all paths through the automaton
     */
    protected Tree initGeneratingRun() {
        matcher.initRun();
        Tree pathTree = new Tree();
        pathTree.setRoot(new Node(""));
        for (State state : matcher.getCurrentStates()) {
            Node child = new Node("");
            child.setValue(state);
            pathTree.getRoot().addChild(child);
            Set<Node> set = new HashSet<Node>();
            set.add(child);
            leafNodes.put(state, set);
        }
        return pathTree;
    }

    /**
     * method that returns an Iterator over all accepted symbol strings upto the
     * length specified; symbol strings are encoded as List objects
     * @param length
     *            int that specifies the length of the longest accepted string
     * @return Iterator over the accepted strings encoded as Lists
     */
    public Iterator<List<String>> generatingRun(int length) {
        Tree pathTree = initGeneratingRun();
        for (int i = 0; i < length; i++) {
            if (!generatingStep()) {
                break;
            }
        }
        return examples(pathTree);
    }

    /**
     * method that returns the size of the language accepted by the NFA upto the
     * length specified
     * @param length
     *            int maximum length of the strings to consider
     * @return int number of strings in the language with lengths smaller than or
     *         equal to length
     */
    public int languageSize(int length) {
        Tree pathTree = initGeneratingRun();
        for (int i = 0; i < length; i++) {
            if (!generatingStep()) {
                break;
            }
        }
        return computeExampleList(pathTree).size();
    }

    /**
     * method that performs a single step in the run of the automaton; each call
     * to this method generates one level in the computation tree
     * @return boolean true if the set of current states is not empty, false otherwise
     */
    protected boolean generatingStep() {
        SortedSet<State> newCurrentStates = new TreeSet<State>(matcher.getStateValueComparator());
        Map<State,Set<Node>> newLeafNodes = new HashMap<State,Set<Node>>();
        for (State state : matcher.getCurrentStates()) {
            Set<Node> nodeSet = leafNodes.get(state);
            for (Node node : nodeSet) {
                for (String symbolValue : nfa.getSymbolValues()) {
                    Symbol symbol = Symbol.create(symbolValue);
                    Set<State> result = nfa.getTransitionMap().getEpsilonReacheableStates(nfa.getNextStates(symbol, state));
                    if (result != null) {
                        newCurrentStates.addAll(result);
                        for (State newState : result) {
                            Node newNode = new Node(symbolValue);
                            newNode.setValue(newState);
                            if (!newLeafNodes.containsKey(newState)) {
                                newLeafNodes.put(newState, new HashSet<Node>());
                            }
                            Set<Node> newNodeSet = newLeafNodes.get(newState);
                            newNodeSet.add(newNode);
                            node.addChild(newNode);
                        }
                    }
                }
            }
        }
        if (matcher.getCurrentStates().isEmpty()) {
            return false;
        }
        matcher.setCurrentStates(newCurrentStates);
        leafNodes = newLeafNodes;
        return true;
    }

    /**
     * method that computes the examples from the computation tree specified
     * @param pathTree
     *            Tree that encodes a run of the NFA
     * @return Iterator over the accepted strings encoded as Lists
     */
    protected Iterator<List<String>> examples(Tree pathTree) {
        List<List<String>> list = computeExampleList(pathTree);
        return list.iterator();
    }

    /**
     * method that computes the List of examples upto the specified length
     * @param pathTree
     *            Tree representing the NFA run
     * @return List of strings (Lists of symbol strings)
     */
    protected List<List<String>> computeExampleList(Tree pathTree) {
        List<List<String>> list = new LinkedList<List<String>>();
        for (Iterator<Node> it = pathTree.preOrderIterator(); it.hasNext();) {
            Node node = it.next();
            if (nfa.isFinalState((State) node.getValue())) {
                list.add(computeRootPath(node));
            }
        }
        return list;
    }
 
    /**
     * method that computes the symbol string needed to get to the specified node
     * in the computation tree
     * @param node
     *            Node in a computation tree
     * @return List of symbol values to reach the Node
     */
    protected static List<String> computeRootPath(Node node) {
        List<String> rootPath = new LinkedList<String>();
        while (!node.getKey().equals("")) {
            rootPath.add(0, node.getKey());
            node = node.getParent();
        }
        return rootPath;
    }

    /**
     * method that prepares the NFA for generating random examples and random runs,
     * more specifically, it initializes the stateTransitionMap that is computed once.
     */
    protected void initializeStateMap() {
        stateTransitionMap = new HashMap<State,List<Transition>>();
        for (Transition transition : nfa.getTransitionMap().getTransitions()) {
            if (!stateTransitionMap.containsKey(transition.getFromState())) {
                stateTransitionMap.put(transition.getFromState(), new ArrayList<Transition>());
            }
            List<Transition> list = stateTransitionMap.get(transition.getFromState());
            list.add(transition);
        }
    }

    /**
     * method that generates a random example with the stop probability specified; each
     * time a final state is reached, the probability that the generating run
     * terminates is equal to the former;  the result will be null if the run didn't
     * stop and the final state has no outgoing transitions.  In each state, an
     * outgoing transition will be selected with uniform probability.
     * @param stopProbability
     *            double between 0 and 1 that represents the probability the run
     *            will stop when a final state is reached
     * @return List symbols the random example is composed of
     */
    public List<String> generateRandomExample(double stopProbability) {
        return generateRandomRun(stopProbability).example;
    }

    public ExampleRun generateRandomRun(double stopProbability) {
        List<String> symbols = new LinkedList<String>();
        if (stateTransitionMap == null) {
            initializeStateMap();
        }
        State currentState = nfa.getInitialState();
        int numberToStop = 0;
        for (;;) {
            if (nfa.isFinalState(currentState)) {
                numberToStop++;
                if (Math.random() < stopProbability)
                    break;
            }
            List<Transition> transitionList = stateTransitionMap.get(currentState);
            if (transitionList == null || transitionList.isEmpty()) {
                if (nfa.isFinalState(currentState))
                    break;
                else
                    return new ExampleRun(null, numberToStop);
            }
            int choice = RandomUtils.nextInt(transitionList.size());
            Transition selectedTransition = transitionList.get(choice);
            symbols.add(selectedTransition.getSymbol().toString());
            currentState = selectedTransition.getToState();
        }
        return new ExampleRun(symbols, numberToStop);
    }

    public Iterator<List<String>> getCharacteristicStringIterator() {
        return new CharacteristicStringIterator();
    }

    static public class ExampleRun {

        /**
         * example generated during the run
         */
        protected List<String> example;
        /**
         * number of times a final state was reached (the one in which the process
         * stopped included
         */
        protected int numberToStop;

        /**
         * constructor for an ExampleRun that holds the generated example as a list
         * of symbols as well as the number of times a final state has been reached
         * during the run, the last inclusive. 
         * @param example
         *            List of symbols the generated example is composed of
         * @param numberToStop
         *            number of times a final state was reached during the run
         */
        protected ExampleRun(List<String> example, int numberToStop) {
            this.example = example;
            this.numberToStop = numberToStop;
        }

        /**
         * method that returns an Iterator over the example's symbol values
         * @return Iterator of the Strings the example is composed of
         */
        public Iterator<String> getExampleIterator() {
            return example.iterator();
        }

        /**
         * method that returns the number of times a final state was reached during
         * the run
         * @return int that represents the number of times a final state was reached
         *         during the run, the one the run stopped in inclusive
         */
        public int getNumberToStop() {
            return numberToStop;
        }

    }

    public class CharacteristicStringIterator implements Iterator<List<String>> {

        protected LinkedList<Transition> transitionsToDo = new LinkedList<Transition>();
        protected Map<State,List<Transition>> inPaths = new HashMap<State,List<Transition>>();
        protected Map<State,List<Transition>> outPaths = new HashMap<State,List<Transition>>();
        protected LinkedList<State> statesToDo = new LinkedList<State>();
        protected Set<State> statesDone = new HashSet<State>();
        protected boolean hasReturnedEmpty = false;
        protected final String[] emptyString = {};
        protected ArrayList<Cycle> cycles = new ArrayList<Cycle>();
        protected int cycleIndex = 0;
        protected boolean traverseCyclesTwice = false;

        public CharacteristicStringIterator() {
            transitionsToDo.addAll(nfa.getTransitionMap().getTransitions());
            statesToDo.add(nfa.getInitialState());
            inPaths.put(nfa.getInitialState(), new ArrayList<Transition>());
            for (State finalState : nfa.getFinalStates())
                outPaths.put(finalState, new ArrayList<Transition>());
        }

        public CharacteristicStringIterator(boolean traverseCyclesTwice) {
            this();
            this.traverseCyclesTwice = traverseCyclesTwice;
        }

        public List<Cycle> getCycles() {
            return cycles;
        }

        public void remove() {}

        public boolean hasNext() {
            return !transitionsToDo.isEmpty() ||
                (!hasReturnedEmpty && nfa.isFinalState(nfa.getInitialState())) ||
                (traverseCyclesTwice && cycleIndex < cycles.size());
        }

        public List<String> next() {
            if (!hasReturnedEmpty && nfa.isFinalState(nfa.getInitialState())) {
                hasReturnedEmpty = true;
                return new LinkedList<String>();
            } else {
                hasReturnedEmpty = true;
            }
            List<String> tokens = findNextString();
            if (tokens != null) {
                return tokens;
            } else if (traverseCyclesTwice && cycleIndex < cycles.size()) {
                Cycle cycle = cycles.get(cycleIndex++);
                State startState = cycle.getStartState();
                tokens = computePathSymbols(inPaths.get(startState));
                tokens.addAll(cycle.getTokens());
                tokens.addAll(computePathSymbols(outPaths.get(startState)));
                return tokens;
            } else if (!hasNext()){
                throw new NoSuchElementException();
            } else {
                throw new RuntimeException("abnormal termination condition in iterator");
            }
        }
        
        protected List<String> findNextString() {
            while (!statesToDo.isEmpty()) {
                State fromState = statesToDo.get(0);
                for (Transition transition : nfa.getOutgoingTransitions(fromState)) {
                    if (!transitionsToDo.contains(transition))
                        continue;
                    if (transition.getFromState() == transition.getToState())
                        // this is a back-edge, skip for now
                        continue;
                    State toState = transition.getToState();
                    if (inPaths.containsKey(toState)) {
                        continue;
                    }
                    // this is a tree-edge or a cross-edge
                    List<Transition> path = new LinkedList<Transition>(inPaths.get(fromState));
                    path.add(transition);
                    inPaths.put(toState, path);
                    transitionsToDo.remove(transition);
                    if (!statesToDo.contains(toState) && !statesDone.contains(toState))
                        statesToDo.addLast(toState);
                    if (outPaths.containsKey(toState)) {
                        List<Transition> inPath = inPaths.get(toState);
                        List<Transition> outPath = new ArrayList<Transition>(outPaths.get(toState));
                        for (int i = inPath.size() - 1; i >= 0; i--) {
                            Transition t = inPath.get(i);
                            State fState = t.getFromState();
                            outPath = new ArrayList<Transition>(outPath);
                            outPath.add(0, t);
                            if (!outPaths.containsKey(fState)) {
                                outPaths.put(fState, outPath);
                            }
                        }
                        return computePathSymbols(toState);
                    }
                }
                statesToDo.removeFirst();
                statesDone.add(fromState);
            }
            // now only back-edges should be left
            if (!transitionsToDo.isEmpty()) {
                Transition transition = null;
                for (Transition t : transitionsToDo) {
                    if (inPaths.containsKey(t.getFromState()) &&
                            outPaths.containsKey(t.getToState())) {
                        transition = t;
                        break;
                    }
                }
                if (transition != null) {
                    transitionsToDo.remove(transition);
                    List<Transition> inPath = new ArrayList<Transition>(inPaths.get(transition.getFromState()));
                    if (transition.getFromState() == transition.getToState())
                        inPath.add(transition);
                    List<String> tokens = computePathSymbols(inPath);
                    List<Transition> outPath = new ArrayList<Transition>(outPaths.get(transition.getToState()));
                    if (transition.getFromState() != transition.getToState())
                        outPath.add(0, transition);
                    tokens.addAll(computePathSymbols(outPath));
                    if (!outPaths.containsKey(transition.getFromState())) {
                        outPaths.put(transition.getFromState(), outPath);
                    }
                    for (int i = inPath.size() - 1; i >= 0; i--) {
                        Transition t = inPath.get(i);
                        State fState = t.getFromState();
                        outPath = new ArrayList<Transition>(outPath);
                        if (t.getToState() != t.getFromState())
                            outPath.add(0, t);
                        if (!outPaths.containsKey(fState)) {
                            outPaths.put(fState, outPath);
                        }
                    }
                    if (transition.getFromState() == transition.getToState()) {
                        cycles.add(new Cycle(transition.getToState(), transition));
                    } else if (occursInPath(transition.getToState(),
                                            inPaths.get(transition.getFromState())) ||
                                            occursInPath(transition.getFromState(),
                                                         outPaths.get(transition.getToState()))) {
                        cycles.add(computeCycle(transition));
                    }
                    return tokens;
                }
            }
            return null;
        }

        protected List<String> computePathSymbols(State state) {
            List<String> tokens = computePathSymbols(inPaths.get(state));
            tokens.addAll(computePathSymbols(outPaths.get(state)));
            return tokens;
        }

        protected List<String> computePathSymbols(List<Transition> transitions) {
            List<String> tokens = new LinkedList<String>();
            for (int i = 0; i < transitions.size(); i++) {
                Transition transition = transitions.get(i);
                if (transition.getSymbol() != Symbol.getEpsilon())
                    tokens.add(transition.getSymbol().toString());
            }
            return tokens;
        }

        protected boolean occursInPath(State state, List<Transition> transitions) {
            for (Transition t : transitions)
                if (t.getFromState() == state || t.getToState() == state)
                    return true;
            return false;
        }

        protected Cycle computeCycle(Transition transition) {
            List<Transition> inPath = inPaths.get(transition.getFromState());
            State startState = null;
            LinkedList<Transition> cyclePath = new LinkedList<Transition>();
            cyclePath.add(transition);
            for (int i = inPath.size() - 1; i >= 0; i--) {
                Transition t = inPath.get(i);
                if (!occursInPath(transition.getFromState(),
                                  outPaths.get(t.getToState()))) {
                    startState = t.getToState();
                    break;
                }
                cyclePath.addFirst(t);
            }
            if (startState == null) return null;
            List<Transition> outPath = outPaths.get(transition.getToState());
            for (int i = 0; i < outPath.size(); i++) {
                Transition t = outPath.get(i);
                if (t.getFromState() == startState)
                    break;
                cyclePath.add(t);
            }
            return new Cycle(startState, cyclePath);
        }

        @Override
        public String toString() {
            StringBuffer str = new StringBuffer();
            for (State state : nfa.getStates()) {
                if (inPaths.containsKey(state))
                    str.append(pathToString(inPaths.get(state)));
                else
                    str.append("[?]");
                str.append(" ").append(nfa.getStateValue(state)).append(" ");
                if (outPaths.containsKey(state))
                    str.append(pathToString(outPaths.get(state)));
                else
                    str.append("[?]");
                str.append("\n");
            }
            return str.toString();
        }

        protected String pathToString(List<Transition> path) {
            StringBuffer str = new StringBuffer();
            str.append("[");
            for (Iterator<Transition> transIt = path.iterator(); transIt.hasNext(); ) {
                Transition transition = transIt.next();
                str.append("(");
                str.append(nfa.getStateValue(transition.getFromState())).append(", ");
                str.append(transition.getSymbol().toString()).append(" -> ");
                str.append(nfa.getStateValue(transition.getToState()));
                str.append(")");
                if (transIt.hasNext())
                    str.append(", ");
            }
            str.append("]");
            return str.toString();
        }

    }

    public class Cycle {

        protected State startState;
        protected List<Transition> path;
        protected List<String> tokens;

        public Cycle(State startState, List<Transition> transitions) {
            this.startState = startState;
            this.path = transitions;
            this.tokens = new LinkedList<String>();
            for (Transition t : path)
                tokens.add(t.getSymbol().toString());
        }

        public Cycle(State startState, Transition transition) {
            this.startState = startState;
            this.path = new LinkedList<Transition>();
            path.add(transition);
            this.tokens = new LinkedList<String>();
            for (Transition t : path)
                tokens.add(t.getSymbol().toString());
        }

        public State getStartState() {
            return startState;
        }

        public List<String> getTokens() {
            return tokens;
        }

        @Override
        public String toString() {
            return nfa.getStateValue(getStartState()) + ": " + getTokens().toString();
        }

    }

}
