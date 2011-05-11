package gjb.flt.automata.impl.sparse;

import gjb.flt.automata.NoSuchTransitionException;
import gjb.flt.automata.NotDFAException;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.LinkedList;

/**
 * @version $Revision: 1.3 $ 
 * @author gjb
 *
 */
public class TransitionMap {

	protected Map<Symbol,Map<State,Set<State>>> rules;
	protected boolean dfa = true;
    protected boolean intendedDFA = false;

	public TransitionMap() {
		this.rules = new HashMap<Symbol,Map<State,Set<State>>>();
	}

	public TransitionMap(TransitionMap map, Map<State,State> stateRemap)
            throws NotDFAException {
		this();
		for (Symbol symbol : map.rules.keySet()) {
			Map<State,Set<State>> fromStateMap = map.rules.get(symbol);
			for (State fromState : fromStateMap.keySet())
				for (State toState : fromStateMap.get(fromState))
					add(symbol, stateRemap.get(fromState), stateRemap.get(toState));
		}
		dfa = map.isDeterministic();
	}

	public static TransitionMap merge(TransitionMap... maps) {
		TransitionMap map = new TransitionMap();
		for (int i = 0; i < maps.length; i++) {
			for (Symbol symbol : maps[i].rules.keySet()) {
				Map<State,Set<State>> fromStateMap = maps[i].rules.get(symbol);
				if (!map.rules.containsKey(symbol))
					map.rules.put(symbol, new HashMap<State,Set<State>>());
				Map<State,Set<State>> newFromStateMap = map.rules.get(symbol);
				for (State fromState : fromStateMap.keySet()) {
					if (!newFromStateMap.containsKey(fromState))
						newFromStateMap.put(fromState, new HashSet<State>());
					Set<State> newToStateSet = newFromStateMap.get(fromState);
					newToStateSet.addAll(fromStateMap.get(fromState));
					if (map.dfa && newToStateSet.size() > 1) {
						map.dfa = false;
					}
				}
			}
		}
		if (map.dfa && map.rules.containsKey(Symbol.getEpsilon()))
			map.dfa = false;
		return map;
	}

	protected boolean isIntendedDFA() {
        return intendedDFA;
    }

    protected void setIntendedDFA(boolean intendedDFA) {
        this.intendedDFA = intendedDFA;
    }

    protected void add(Symbol symbol, State fromState, State toState)
            throws NotDFAException {
        if (symbol.equals(Symbol.getEpsilon())) {
            dfa = false;
            if (isIntendedDFA())
                throw new NotDFAException(new Transition(symbol, fromState, toState));
        }
		if (!rules.containsKey(symbol))
			rules.put(symbol, new HashMap<State,Set<State>>());
		Map<State,Set<State>> stateMap = rules.get(symbol);
		if (!stateMap.containsKey(fromState))
			stateMap.put(fromState, new HashSet<State>());
		Set<State> set = stateMap.get(fromState);
		set.add(toState);
        if (set.size() > 1) {
            dfa = false;
            if (isIntendedDFA()) {
                set.remove(toState);
                throw new NotDFAException(new Transition(symbol, fromState, toState));
            }
        }
	}

	protected void remove(State removeState) {
		List<Symbol> removeSymbols = new LinkedList<Symbol>();
		for (Symbol symbol : symbols()) {
			Map<State,Set<State>> stateMap = rules.get(symbol);
			stateMap.remove(removeState);
			List<State> removeFromStates = new LinkedList<State>();
			for (State fromState : stateMap.keySet()) {
				Set<State> set = stateMap.get(fromState);
				set.remove(removeState);
				if (set.size() == 0)
					removeFromStates.add(fromState);
			}
			for (State state : removeFromStates)
				stateMap.remove(state);
			if (stateMap.size() == 0)
				removeSymbols.add(symbol);
		}
		for (Symbol symbol : removeSymbols)
			rules.remove(symbol);
        checkDeterminism();
	}

	protected void remove(Symbol symbol, State fromState, State toState)
		    throws NoSuchTransitionException {
		if (!rules.containsKey(symbol))
			throw new NoSuchTransitionException(symbol, fromState, toState);
		Map<State,Set<State>> stateMap = rules.get(symbol);
		if (!stateMap.containsKey(fromState))
			throw new NoSuchTransitionException(symbol, fromState, toState);
		Set<State> set = stateMap.get(fromState);
		if (!set.contains(toState))
			throw new NoSuchTransitionException(symbol, fromState, toState);
		if (set.size() > 1)
			set.remove(toState);
		else if (stateMap.size() > 1)
			stateMap.remove(fromState);
		else
			rules.remove(symbol);
        checkDeterminism();
	}

	protected boolean isDeterministic() {
		return dfa;
	}

	public boolean checkDeterminism() {
		if (dfa && rules.containsKey(Symbol.getEpsilon())) {
			dfa = false;
			return dfa;
		}
		for (Symbol symbol : rules.keySet()) {
			Map<State,Set<State>> fromStateMap = rules.get(symbol);
			for (State fromState : fromStateMap.keySet())
				if (fromStateMap.get(fromState).size() > 1) {
					dfa = false;
					return dfa;
				}
		}
        dfa = true;
        return dfa;
	}

	protected boolean hasTransitionsWith(Symbol symbol) {
		return rules.containsKey(symbol);
	}

    public boolean hasTransitionWith(Symbol symbol, State fromState) {
        return rules.containsKey(symbol) &&
               rules.get(symbol).containsKey(fromState) &&
               !rules.get(symbol).get(fromState).isEmpty();
    }

    protected Set<State> fromStatesWith(Symbol symbol) {
		Set<State> states = new HashSet<State>();
		if (this.hasTransitionsWith(symbol))
			states.addAll(rules.get(symbol).keySet());
		return states;
	}

	protected Set<State> toStatesWith(Symbol symbol) {
	    Set<State> states = new HashSet<State>();
	    if (this.hasTransitionsWith(symbol)) {
	        Map<State,Set<State>> map = rules.get(symbol);
	        for (State fromState : map.keySet())
                states.addAll(map.get(fromState));
	    }
	    return states;
	}
	
	protected Set<Symbol> symbols() {
		return Collections.unmodifiableSet(rules.keySet());
	}

	protected Set<State> nextStates(Symbol symbol, State fromState) {
		Map<State,Set<State>> stateMap = rules.get(symbol);
		Set<State> toStateSet = new HashSet<State>();
		if (stateMap != null) {
			Set<State> set = stateMap.get(fromState);
			if (set != null)
				toStateSet.addAll(set);
		}
		return Collections.unmodifiableSet(toStateSet);
	}

    protected boolean contains(Symbol symbol, State fromState, State toState) {
        if (!rules.containsKey(symbol)) return false;
        Map<State,Set<State>> stateMap = rules.get(symbol);
        if (!stateMap.containsKey(fromState)) return false;
        if (!stateMap.get(fromState).contains(toState)) return false;
        return true;
    }

    public boolean contains(Transition transition) {
        return contains(transition.getSymbol(),
                        transition.getFromState(),
                        transition.getToState());
    }
	/**
	 * method that returns the set of States that can be reached from the given
	 * state within a single step 
	 * @param fromState
	 *             State to start from
	 * @return Set of single-step reacheable states
	 */
	protected Set<State> nextStates(State fromState) {
		Set<State> states = new HashSet<State>();
		for (Symbol symbol : symbols())
			states.addAll(nextStates(symbol, fromState));
		return states;
	}

	protected Set<State> previousStates(Symbol symbol, State toState) {
		Map<State,Set<State>> stateMap = rules.get(symbol);
		Set<State> fromStateSet = new HashSet<State>();
		if (stateMap != null)
			for (State fromState : stateMap.keySet())
				if (stateMap.get(fromState).contains(toState))
					fromStateSet.add(fromState);
		return Collections.unmodifiableSet(fromStateSet);
	}

	protected Set<State> previousStates(State toState) {
		Set<State> states = new HashSet<State>();
		for (Symbol symbol : symbols())
			states.addAll(previousStates(symbol, toState));
		return states;
	}

    protected Set<Transition> incomingTransitions(State toState) {
        Set<Transition> transitionSet = new HashSet<Transition>();
        for (Symbol symbol : symbols())
            for (State fromState : previousStates(symbol, toState))
                transitionSet.add(new Transition(symbol, fromState, toState));
        return transitionSet;
    }

    protected Set<Transition> outgoingTransitions(State fromState) {
        Set<Transition> transitionSet = new HashSet<Transition>();
        for (Symbol symbol : symbols())
            for (State toState : nextStates(symbol, fromState))
                transitionSet.add(new Transition(symbol, fromState, toState));
        return transitionSet;
    }

    protected State nextState(Symbol symbol, State fromState)
		    throws NotDFAException {
		Map<State,Set<State>> stateMap = rules.get(symbol);
		if (stateMap != null) {
			Set<State> toStateSet = stateMap.get(fromState);
			if (toStateSet != null) {
				if (toStateSet.size() == 1) {
					return toStateSet.iterator().next();
				} else {
					throw new NotDFAException();
				}
			}
		}
		return null;
	}

	protected int numberOfChoices(State state) {
	    return nextStates(state).size();
	}

	/**
	 * method that returns the Set of all States reacheable from the specified
	 * state through one or more epsilon transitions
	 * @param state
	 *            State to start from
	 * @return Set of States reacheable by epsilon transitions
	 */
	protected Set<State> epsilonReacheableStates(State startState) {
	    Set<State> reacheable = new HashSet<State>();
	    reacheable.add(startState);
	    Set<State> current = new HashSet<State>();
	    current.add(startState);
	    Set<State> done = new HashSet<State>();
	    while (!current.isEmpty()) {
	        Set<State> newCurrent = new HashSet<State>();
	        for (State state : current) {
                done.add(state);
                Set<State> states = nextStates(Symbol.getEpsilon(), state);
                reacheable.addAll(states);
                newCurrent.addAll(states);
            }
	        newCurrent.removeAll(done);
	        current = newCurrent;
	    }
	    return reacheable;
	}

	/**
	 * method that returns the Set of all States reacheable from the specified
	 * states through one or more epsilon transitions
	 * @param states
	 *            Set of States to start from
	 * @return  Set of States reacheable by epsilon transitions
	 */
	public Set<State> getEpsilonReacheableStates(Set<State> states) {
	    Set<State> reacheable = new HashSet<State>();
	    for (State state : states)
            reacheable.addAll(epsilonReacheableStates(state));
	    return reacheable;
	}

	public Set<Transition> getTransitions() {
    	Set<Transition> set = new HashSet<Transition>();
    	for (Symbol symbol : rules.keySet()) {
    		Map<State,Set<State>> stateMap = rules.get(symbol);
    		for (State fromState : stateMap.keySet()) {
    			Set<State> stateSet = stateMap.get(fromState);
    			for (State toState : stateSet)
    				set.add(new Transition(symbol, fromState, toState));
    		}
    	}
    	return Collections.unmodifiableSet(set);
    }

	@Override
    public String toString() {
		return rules.toString();
	}

}
