/*
 * Created on Aug 3, 2004
 *
 */
package gjb.flt.automata;

/**
 * Exeption thrown when a state with the same value as an existing
 * one is added to the NFA.
 * 
 * @author gjb
 * @version $Revision: 1.1 $
 */
public class NFAStateAlreadyExistsException extends NFAException {

    private static final long serialVersionUID = 1L;
    /**
     * state value that is added multiple times
     */
	protected String stateValue;

	/**
	 * Constructor that has the state value String as a parameter
	 * @param stateValue String representing the State's value
	 */
	public NFAStateAlreadyExistsException(String stateValue) {
		super();
		this.stateValue = stateValue;
	}


	/**
	 * @return message documenting the Exception
	 */
	@Override
	public String getMessage() {
		return "a state with value '" + stateValue + "' is already in the NFA";
	}

}
