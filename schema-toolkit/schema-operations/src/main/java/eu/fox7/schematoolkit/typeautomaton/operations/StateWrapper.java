package eu.fox7.schematoolkit.typeautomaton.operations;

import eu.fox7.bonxai.typeautomaton.TypeAutomaton;
import eu.fox7.flt.automata.NotDFAException;
import eu.fox7.flt.automata.impl.sparse.State;
import eu.fox7.flt.automata.impl.sparse.Symbol;
import eu.fox7.schematoolkit.xsd.om.Type;

public class StateWrapper {
	TypeAutomaton automaton;
	State state;

	public StateWrapper(TypeAutomaton automaton, State state) {
		this.automaton = automaton;
		this.state = state;
	}

	public Type getType() {
		return automaton.getType(state);
	}

	public StateWrapper getNextState(Symbol symbol) {
		try {
			return new StateWrapper(this.automaton,this.automaton.getNextState(symbol, this.state));
		} catch (NotDFAException e) {
			throw new RuntimeException(e);
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((automaton == null) ? 0 : automaton.hashCode());
		result = prime * result + ((state == null) ? 0 : state.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		StateWrapper other = (StateWrapper) obj;
		if (automaton == null) {
			if (other.automaton != null)
				return false;
		} else if (!automaton.equals(other.automaton))
			return false;
		if (state == null) {
			if (other.state != null)
				return false;
		} else if (!state.equals(other.state))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return (state==null)?null:state.toString();
	}
	
}
