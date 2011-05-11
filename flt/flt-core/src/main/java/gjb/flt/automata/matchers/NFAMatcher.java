/**
 * Created on Oct 6, 2009
 * Modified on $Date: 2009-11-10 14:01:29 $
 */
package gjb.flt.automata.matchers;

import gjb.flt.automata.impl.sparse.State;
import gjb.flt.automata.impl.sparse.StateNFA;
import gjb.flt.automata.impl.sparse.StateValueComparator;
import gjb.flt.automata.impl.sparse.Symbol;

import java.util.Collections;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * @author lucg5005
 * @version $Revision: 1.2 $
 *
 */
public class NFAMatcher {

	protected StateNFA nfa;
    protected SortedSet<State> currentStates;
    /**
     * this comparator allows to order the states according to their value
     */
    protected StateValueComparator stateValueComparator;

    public NFAMatcher(StateNFA nfa) {
    	this.nfa = nfa;
    	this.stateValueComparator = new StateValueComparator(nfa);
    }

    public StateValueComparator getStateValueComparator() {
    	return this.stateValueComparator;
    }

    public boolean matches(String[] input) {
        Symbol[] symbols = new Symbol[input.length];
        for (int i = 0; i < input.length; i++)
            if (!nfa.hasSymbol(input[i]))
                return false;
            else
                symbols[i] = Symbol.create(input[i]);
        return matches(symbols);
    }

	/**
     * method that initializes an NFA for a run; it sets the NFA's currentStates
     * to the initialState and the states reachable from it by epsilon
     * transitions
     */
    public void initRun() {
        this.currentStates = new TreeSet<State>(stateValueComparator);
        this.currentStates.add(nfa.getInitialState());
        this.doEpsilonMoves();
    }

    protected void doEpsilonMoves() {
        boolean addedNew = true;
        while (addedNew) {
            addedNew = false;
            SortedSet<State> newCurrentStates = new TreeSet<State>(stateValueComparator);
            newCurrentStates.addAll(this.currentStates);
            for (State state : this.currentStates) {
                Set<State> result = nfa.getNextStates(Symbol.getEpsilon(), state);
                if (result != null) {
                    if (!newCurrentStates.containsAll(result)) {
                        newCurrentStates.addAll(result);
                        addedNew = true;
                    }
                }
            }
            this.currentStates = newCurrentStates;
        }
    }

    public SortedSet<State> doEpsilonMoves(State q) {
        SortedSet<State> states = this.currentStates;
        this.currentStates = new TreeSet<State>(stateValueComparator);
        this.currentStates.add(q);
        this.doEpsilonMoves();
        SortedSet<State> result = this.currentStates;
        this.currentStates = states;
        return result;
    }

    public SortedSet<State> doEpsilonMoves(Set<State> set) {
        SortedSet<State> result = new TreeSet<State>(stateValueComparator);
        for (State state : set)
            result.addAll(doEpsilonMoves(state));
        return result;
    }

    /**
     * method that performs a single step in the run of the NFA given the symbol
     * String as input
     * 
     * @param symbolValue
     *            symbol String the step is to be performed for
     * @return true if the step could be performed, false otherwise
     */
    protected boolean step(String symbolValue) {
        Symbol symbol = Symbol.create(symbolValue);
        return step(symbol);
    }

    /**
     * method that performs a single step in the run of the NFA given the symbol
     * as input
     * 
     * @param symbol
     *            symbol the step is to be performed for
     * @return true if the step could be performed, false otherwise
     */
    protected boolean step(Symbol symbol) {
        SortedSet<State> newCurrentStates = new TreeSet<State>(stateValueComparator);
        for (State state : this.currentStates) {
            Set<State> result = nfa.getNextStates(symbol, state);
            if (result != null)
                newCurrentStates.addAll(result);
        }
        if (this.currentStates.isEmpty())
            return false;
        this.currentStates = newCurrentStates;
        this.doEpsilonMoves();
        return true;
    }

    protected boolean matches(Symbol[] input) {
        initRun();
        for (int i = 0; i < input.length; i++)
            if (!step(input[i]))
                return isInFinalState();
        return isInFinalState();
    }

    public boolean isInFinalState() {
        for (State state : currentStates)
            if (nfa.getFinalStates().contains(state))
                return true;
        return false;
    }

	public SortedSet<State> getCurrentStates() {
	    return Collections.unmodifiableSortedSet(currentStates);
    }

	public void setCurrentStates(SortedSet<State> newCurrentStates) {
		this.currentStates = newCurrentStates;
    }

}
