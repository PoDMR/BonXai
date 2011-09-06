package eu.fox7.flt.automata;

import eu.fox7.flt.automata.impl.sparse.Transition;

/**
 * Exception that is thrown when an operation expects an DFA but the
 * caller provides an NFA as a parameter.
 * 
 * @author eu.fox7
 * @version $Revision: 1.1 $
 */
public class NotDFAException extends NFAException {

    private static final long serialVersionUID = 1L;
    protected String msg = "this is not a DFA, non-deterministic transitions are present";
    protected Transition transition;

    public NotDFAException() {
		super();
	}

    public NotDFAException(String msg) {
        this.msg = msg;
    }

    public NotDFAException(Transition transition) {
        this.transition = transition;
    }

    /**
	 * @return String documenting the Exception
	 */
	public String getMessage() {
        return msg;
	}

    public Transition getTransition() {
        return transition;
    }

}
