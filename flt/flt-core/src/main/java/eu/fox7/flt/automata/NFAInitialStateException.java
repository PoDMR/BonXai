package eu.fox7.flt.automata;

/**
 * Exception to be thrown when no or multiple initial states are
 * found in the  NFA
 * @author eu.fox7
 * @version $Revision: 1.1 $
 */
public class NFAInitialStateException extends NFAException {

    private static final long serialVersionUID = 1L;
    /**
     * number of initial states
     */
	protected int number;

	/**
	 * Constructor taking the number of initial states as parameter
	 * @param number the number of initial states
	 */
	public NFAInitialStateException(int number) {
		super();
		this.number = number;
	}

	/**
	 * @return the message for this Exception
	 */
	@Override
	public String getMessage() {
		return "expected one initial state, found " + number;
	}

}
