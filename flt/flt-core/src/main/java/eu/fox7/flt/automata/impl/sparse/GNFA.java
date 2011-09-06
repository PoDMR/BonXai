/**
 * Created on Nov 12, 2009
 * Modified on $Date: 2009-11-12 22:57:38 $
 */
package eu.fox7.flt.automata.impl.sparse;

import eu.fox7.flt.regex.Regex;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author lucg5005
 * @version $Revision: 1.2 $
 * 
 * Note that a sparse implementation should be considered for any
 * computationally intensive application.
 */
public class GNFA {

	protected State initialState = new State();
	protected State finalState = new State();
	protected Set<State> internalStates = new HashSet<State>();
	protected Map<StatePair,Regex> transitions = new HashMap<StatePair,Regex>();

	public GNFA() {
		super();
	}

	public int getNumberOfStates() {
        return 2 + internalStates.size();
    }

	public State getInitialState() {
		return initialState;
	}

	public State getFinalState() {
		return finalState;
	}

	public Set<State> getInternalStates() {
		return internalStates;
	}

	public void removeState(State state) {
		Set<StatePair> toRemove = new HashSet<StatePair>();
		for (StatePair statePair : transitions.keySet())
			if (statePair.getFirst() == state || statePair.getSecond() == state)
				toRemove.add(statePair);
		for (StatePair statePair : toRemove)
			transitions.remove(statePair);
		internalStates.remove(state);
	}

	public Regex getRegex(State fromState, State toState) {
		return transitions.get(new StatePair(fromState, toState));
	}

	public boolean hasTransition(State fromState, State toState) {
		return transitions.containsKey(new StatePair(fromState, toState));
	}

	public void setRegex(State fromState, State toState, Regex regex) {
		if (fromState != getInitialState() && fromState != getFinalState())
			internalStates.add(fromState);
		if (toState != getInitialState() && toState != getFinalState())
			internalStates.add(toState);
		transitions.put(new StatePair(fromState, toState), regex);
	}

	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		str.append("initial state: ").append(getInitialState().toString()).append("\n");
		str.append("final state: ").append(getFinalState().toString()).append("\n");
		for (StatePair statePair : transitions.keySet()) {
	        str.append(statePair.getFirst().toString()).append(" - ");
	        str.append(transitions.get(statePair).toString());
	        str.append(" -> ").append(statePair.getSecond()).append("\n");
        }
		return str.toString();
	}

}
