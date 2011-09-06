package eu.fox7.flt.automata.impl.sparse;

import eu.fox7.flt.automata.NotDFAException;

import java.util.Map;

public class SparseDFA extends SparseNFA implements StateDFA {

	private State sink = null;
	
	public SparseDFA() {
		super();
		this.intendedDFA = true;
	}

	public SparseDFA(StateNFA dfa, Map<State, State> stateRemap) {
		super(dfa, stateRemap);
		this.intendedDFA = true;
	}

	public SparseDFA(StateNFA dfa) {
		super(dfa);
		this.intendedDFA = true;
	}
	
    public State getNextState(Symbol symbol, State fromState) throws NotDFAException {
			State state = getTransitionMap().nextState(symbol, fromState);
		return (state == null)?this.getSink():state;
    }
	
	private State getSink() {
		if (sink == null) {
			this.addState("sink");
			sink = this.getState("sink");
		}
		
		return sink;
	}

}
