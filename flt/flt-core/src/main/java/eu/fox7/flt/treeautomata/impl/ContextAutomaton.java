/**
 * Created on Oct 28, 2009
 * Modified on $Date: 2009-10-28 15:44:07 $
 */
package eu.fox7.flt.treeautomata.impl;

import java.util.Map;

import eu.fox7.flt.automata.impl.sparse.AnnotatedSparseNFA;
import eu.fox7.flt.automata.impl.sparse.State;
import eu.fox7.flt.automata.impl.sparse.StateNFA;
import eu.fox7.flt.automata.impl.sparse.Symbol;


/**
 * @author lucg5005
 * @version $Revision: 1.2 $
 *
 */
public class ContextAutomaton extends
        AnnotatedSparseNFA<ContentAutomaton,Integer> {

	public ContextAutomaton() {
		super();
	}
	
	public ContextAutomaton(StateNFA nfa,
			Map<State, State> stateMap) {
		super(nfa, stateMap);
	}

	public ContentAutomaton getContentAutomaton(State state) {
		return this.getAnnotation(state);
	}

	public int getSupport(Symbol symbol, State fromState, State toState) {
		if (hasAnnotation(symbol, fromState, toState))
			return getAnnotation(symbol, fromState, toState);
		else
			return 0;
	}

	public int getSupport(String symbolValue, String fromValue, String toValue) {
		return getSupport(Symbol.create(symbolValue), getState(fromValue), getState(toValue));
	}

	public boolean isTerminationState(State state) {
		return getTerminalStateSupport(state) > 0;
	}

	public boolean isTerminationState(String stateValue) {
		return isTerminationState(getState(stateValue));
	}

	public int getTerminalStateSupport(State state) {
		return getAnnotation(state).getEmptyStringSupport();
	}

	public int getTerminalStateSupport(String stateValue) {
		return getTerminalStateSupport(getState(stateValue));
	}

	public int getTotalSupport() {
		int totalSupport = 0;
		for (State state : getStates())
			totalSupport += getAnnotation(state).getTotalSupport();
		return totalSupport;
	}

}
