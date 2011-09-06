/*
 * Created on Dec 20, 2005
 * Modified on $Date: 2009-10-27 14:14:00 $
 */
package eu.fox7.flt.automata;

import eu.fox7.flt.automata.impl.sparse.State;
import eu.fox7.flt.automata.impl.sparse.Symbol;
import eu.fox7.flt.automata.impl.sparse.Transition;

/**
 * @author eu.fox7
 * @version $Revision: 1.1 $
 * 
 */
public class NoSuchTransitionException extends NFAException {

    private static final long serialVersionUID = 6631019047289150439L;
	protected Symbol symbol;
	protected State fromState;
	protected State toState;
	protected String symbolValue;
	protected String fromStateValue;
	protected String toStateValue;
	protected boolean stringRepresentation;
    protected Transition transition;

    public NoSuchTransitionException(Transition transition) {
        this.transition = transition;
    }

	public NoSuchTransitionException(Symbol symbol, State fromState,
	                                 State toState) {
		super();
		this.symbol = symbol;
		this.fromState = fromState;
		this.toState = toState;
		stringRepresentation = false;
	}

	public NoSuchTransitionException(String symbol, String fromState,
	                                 String toState) {
		super();
		this.symbolValue = symbol;
		this.fromStateValue = fromState;
		this.toStateValue = toState;
		stringRepresentation = true;
	}

	@Override
	public String getMessage() {
		if (transition != null) {
	        return "no transition '" + transition.toString() + "' defined";
		} else if (stringRepresentation) {
			return "transition from state '" + fromStateValue +
				"' to state '" + toStateValue + "' with symbol '" +
				symbolValue + "' is not defined";
		} else {
			return "transition from state '" + fromState.toString() +
				"' to state '" + toState.toString() + "' with symbol '" +
				symbol.toString() + "' is not defined";
		}
	}

}
