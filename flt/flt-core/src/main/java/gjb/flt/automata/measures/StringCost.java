/**
 * Created on Oct 9, 2009
 * Modified on $Date: 2009-11-10 14:01:29 $
 */
package gjb.flt.automata.measures;

import gjb.flt.automata.impl.sparse.State;
import gjb.flt.automata.impl.sparse.StateNFA;
import gjb.flt.automata.impl.sparse.Symbol;
import gjb.flt.automata.matchers.NFAMatcher;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * @author lucg5005
 * @version $Revision: 1.2 $
 *
 */
public class StringCost {

    public static final int INFINITY = Integer.MAX_VALUE;
    public static final int DEFAULT_STATE_COST = 1;
    public static final int DEFAULT_TRANSITION_COST = 2;
    public static final int DEFAULT_FINAL_STATE_COST = 1;
    protected StateNFA nfa;
    protected NFAMatcher matcher;

    public StringCost(StateNFA nfa) {
    	this.nfa = nfa;
    	this.matcher = new NFAMatcher(nfa);
    }

    /**
     * Method that computes the cost of the NFA
     * @return int NFA's cost
     */
    public int getNFACost() {
        return getNFACost(DEFAULT_STATE_COST, DEFAULT_TRANSITION_COST,
                          DEFAULT_FINAL_STATE_COST);
    }

    /**
     * Method that computes the cost of the NFA
     * @param stateCost
     *            int the cost of a State
     * @param transitionCost
     *            int the cost of a Transition
     * @param finalStateCost
     *            int the cost of a final state
     * @return int the total cost of the NFA encoding
     */
    public int getNFACost(int stateCost, int transitionCost, int finalStateCost) {
        return stateCost*nfa.getNumberOfStates() +
                transitionCost*nfa.getNumberOfTransitions() +
                finalStateCost*nfa.getNumberOfFinalStates();
    }

    /**
     * method that initializes an NFA for a cost run; it calsl initRun() to prepare
     * the NFA and assigns 0 to the costs of the initial states
     * @param costMap
     *            Map that associates each State with its cost
     */
    protected void initCostRun(Map<State,Integer> costMap) {
        matcher.initRun();
        for (State state : matcher.getCurrentStates())
            costMap.put(state, 0);
    }

    public int compute(String[] input) {
        Symbol[] symbols = new Symbol[input.length];
        for (int i = 0; i < input.length; i++) {
            if (!nfa.hasSymbol(input[i])) {
                return INFINITY;
            } else {
                symbols[i] = Symbol.create(input[i]);
            }
        }
        return this.costRun(symbols);
    }

    protected int costRun(Symbol[] input) {
        Map<State,Integer> costMap = new HashMap<State,Integer>();
        initCostRun(costMap);
        for (int i = 0; i < input.length; i++) {
            if (!costStep(input[i], costMap)) {
                return INFINITY;
            }
        }
        if (matcher.isInFinalState()) {
            Set<State> minStates = new HashSet<State>();
            int minCost = INFINITY;
            for (State state : matcher.getCurrentStates()) {
                if (costMap.containsKey(state) && nfa.isFinalState(state)) {
                    int cost = costMap.get(state);
                    if (cost < minCost) {
                        minCost = cost;
                        minStates.clear();
                        minStates.add(state);
                    } else if (cost == minCost)
                        minStates.add(state);
                }
            }
            int finalCost = Integer.MAX_VALUE;
            for (State state : minStates) {
                int extraCost = computeCost(nfa.getNumberOfChoicesFrom(state));
                if (finalCost > minCost + extraCost)
                    finalCost = minCost + extraCost;
            }
            return finalCost;
        } else {
            return INFINITY;
        }
    }

    /**
     * method that performs a single step and computes the associated costs in
     * the run of the NFA given the symbol as input
     * 
     * @param symbol
     *            symbol the step is to be performed for
     * @param costMap
     *            Map that holds the costs for states
     * @return true if the step could be performed, false otherwise
     */
    protected boolean costStep(Symbol symbol, Map<State,Integer> costMap) {
        SortedSet<State> newCurrentStates = new TreeSet<State>(matcher.getStateValueComparator());
        for (State state : matcher.getCurrentStates()) {
            int nrChoices = nfa.getNumberOfChoicesFrom(state);
            int cost = costMap.get(state);
            if (nrChoices > 1) {
                cost += computeCost(nrChoices);
            }
            Set<State> result = nfa.getNextStates(symbol, state);
            if (result != null) {
                newCurrentStates.addAll(result);
                for (State newState : result) {
                    if (costMap.containsKey(newState)) {
                        int oldCost = costMap.get(newState);
                        if (cost > oldCost) {
                            costMap.put(newState, cost);
                        }
                    } else {
                        costMap.put(newState, cost);
                    }
                }
            }
        }
        if (matcher.getCurrentStates().isEmpty()) {
            return false;
        }
        matcher.setCurrentStates(newCurrentStates);
        this.doCostEpsilonMoves(costMap);
        return true;
    }

    public static int computeCost(int nrChoices) {
        return (int) Math.ceil(Math.log(nrChoices)/Math.log(2));
    }

    protected void doCostEpsilonMoves(Map<State,Integer> costMap) {
        boolean addedNew = true;
        while (addedNew) {
            addedNew = false;
            SortedSet<State> newCurrentStates = new TreeSet<State>(matcher.getStateValueComparator());
            newCurrentStates.addAll(matcher.getCurrentStates());
            for (State state : matcher.getCurrentStates()) {
                int nrChoices = nfa.getNumberOfChoicesFrom(state);
                int currentCost = costMap.get(state);
                int cost = 1 + (int) Math.floor(Math.log(nrChoices)/Math.log(2.0))
                             + currentCost;
                Set<State> result = nfa.getNextStates(Symbol.getEpsilon(),
                        state);
                if (result != null) {
                    if (!newCurrentStates.containsAll(result)) {
                        newCurrentStates.addAll(result);
                        addedNew = true;
                        for (State newState : result) {
                            if (costMap.containsKey(newState)) {
                                int oldCost = costMap.get(newState);
                                if (cost > oldCost) {
                                    costMap.put(newState, cost);
                                }
                            } else {
                                costMap.put(newState, cost);
                            }
                        }
                    }
                }
            }
            matcher.setCurrentStates(newCurrentStates);
        }
    }

}
