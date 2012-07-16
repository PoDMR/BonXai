package eu.fox7.treeautomata.converter;

import java.util.Map;

import eu.fox7.flt.automata.impl.sparse.AnnotatedSparseNFA;
import eu.fox7.flt.automata.impl.sparse.State;
import eu.fox7.flt.automata.impl.sparse.StateNFA;

public class ExtendedContextAutomaton extends AnnotatedSparseNFA<StateNFA,Object> {
	public ExtendedContextAutomaton() {
		super();
	}

	public ExtendedContextAutomaton(StateNFA nfa, Map<State, State> stateRemap) {
		super(nfa, stateRemap);
	}

	public ExtendedContextAutomaton(StateNFA nfa) {
		super(nfa);
	}

	public enum StateType {
		CONTENT, ELEMENTREF, TYPEREF
	}
	
	public StateType getStateType(State state) {
		return null;
	}

	public void setContentAutomaton(State state, StateNFA contentAutomaton) {
		this.annotate(state, contentAutomaton);
	}
}
