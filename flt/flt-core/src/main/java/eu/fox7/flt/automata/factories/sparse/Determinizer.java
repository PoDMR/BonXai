/**
 * Created on Oct 7, 2009
 * Modified on $Date: 2009-10-27 14:14:01 $
 */
package eu.fox7.flt.automata.factories.sparse;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.Stack;
import java.util.TreeSet;

import eu.fox7.flt.automata.impl.sparse.SparseNFA;
import eu.fox7.flt.automata.impl.sparse.State;
import eu.fox7.flt.automata.impl.sparse.StateNFA;
import eu.fox7.flt.automata.impl.sparse.Symbol;
import eu.fox7.flt.automata.matchers.NFAMatcher;


/**
 * @author lucg5005
 * @version $Revision: 1.1 $
 *
 */
public class Determinizer {

    /**
     * method that constructs a new DFA from the NFA
     * 
     * @return a DFA constructed from the NFA object
     */
    public static SparseNFA dfa(StateNFA nfa) {
        if (nfa.isDFA())
            return new SparseNFA(nfa);
        SparseNFA dfa = new SparseNFA();
        NFAMatcher matcher = new NFAMatcher(nfa);
        dfa.addSymbols(nfa.getSymbols());
        Stack<String> toDo = new Stack<String>();
        Map<String,SortedSet<State>> dfaSets = new HashMap<String,SortedSet<State>>();
        SortedSet<State> epsilonExtension = matcher.doEpsilonMoves(nfa.getInitialState());
        String initialDFAStateValue = encodeSet(nfa, epsilonExtension);
        dfa.setInitialState(initialDFAStateValue);
        dfaSets.put(initialDFAStateValue, epsilonExtension);
        toDo.push(initialDFAStateValue);
        while (!toDo.empty()) {
            String dfaStateValue = toDo.pop();
            SortedSet<State> stateSet = dfaSets.get(dfaStateValue);
            for (Symbol symbol : nfa.getSymbols()) {
                SortedSet<State> toStates = new TreeSet<State>(matcher.getStateValueComparator());
                for (State fromState : stateSet) {
                    Set<State> nextStates = nfa.getNextStates(symbol, fromState);
                    if (nextStates != null)
                        toStates.addAll(nextStates);
                }
                epsilonExtension = matcher.doEpsilonMoves(toStates);
                String newDFAStateValue;
                if (!epsilonExtension.isEmpty()) {
                	newDFAStateValue = encodeSet(nfa, epsilonExtension);


                	if (!dfaSets.containsKey(newDFAStateValue)) {
                		toDo.push(newDFAStateValue);
                		dfaSets.put(newDFAStateValue, epsilonExtension);
                	}
                	dfa.addTransition(symbol.toString(), dfaStateValue,
                			newDFAStateValue);
                }
            }
        }
        for (State finalState : nfa.getFinalStates())
            for (SortedSet<State> stateSet : dfaSets.values())
                if (stateSet.contains(finalState))
                  dfa.addFinalState(encodeSet(nfa, stateSet));
        return dfa;
    }

    /**
     * Helper function that encodes sets of states for the computation of a DFA
     * into a string representation that reflects the original NFA's state names
     * 
     * @param stateSet
     *            set to encode using the user specified state names
     * @param nfa
     *            original NFA the states in the set originate from
     */
    protected static String encodeSet(StateNFA nfa, SortedSet<State> stateSet) {
        SortedSet<String> valueSet = new TreeSet<String>();
        for (State state : stateSet)
            valueSet.add(nfa.getStateValue(state));
        return valueSet.toString();
    }

}
