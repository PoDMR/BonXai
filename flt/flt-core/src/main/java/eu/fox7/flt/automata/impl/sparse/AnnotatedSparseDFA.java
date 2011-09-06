package eu.fox7.flt.automata.impl.sparse;

import eu.fox7.flt.automata.NotDFAException;

import java.util.Map;

public class AnnotatedSparseDFA<StateT, TransitionT> extends AnnotatedSparseNFA<StateT, TransitionT> {
	private State sink = null;
	
	public AnnotatedSparseDFA() {
		super();
		this.intendedDFA = true;
	}

	public AnnotatedSparseDFA(StateDFA dfa) {
		super(dfa);
		this.intendedDFA = true;
	}
	
    public State getNextState(Symbol symbol, State fromState) {
    	try {
			return getTransitionMap().nextState(symbol, fromState);
		} catch (NotDFAException e) {
			return this.getSink();
		}
    }
	
	private State getSink() {
		if (sink == null) {
			this.addState("sink");
			sink = this.getState("sink");
		}
		
		return sink;
	}
}
