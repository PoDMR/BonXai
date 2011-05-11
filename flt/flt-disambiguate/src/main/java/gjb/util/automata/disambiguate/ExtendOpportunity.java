/**
 * 
 */
package gjb.util.automata.disambiguate;

/**
 * @author woutergelade
 *
 */
public class ExtendOpportunity {
	public static final int TRANSITION = 0;
	public static final int FINAL_STATE = 1;
	
	private int type;
	private String state;
	private String fromState;
	private String toState;
	private String symbol;
	
	public ExtendOpportunity(String state){
		type = FINAL_STATE;
		this.state = state;
	}
	
	public ExtendOpportunity(String fromState, String toState, String symbol){
		type = TRANSITION;
		this.fromState = fromState;
		this.toState = toState;
		this.symbol = symbol;
	}
	
	public String toString() {
		if(type == FINAL_STATE)
			return "make " + state + " final";
		else
			return "add (" + fromState + "," + symbol + "," + toState + ")";
	}
	public boolean equals(Object opportunity) {
		ExtendOpportunity op = (ExtendOpportunity) opportunity;
		if(type == FINAL_STATE)
			return op.concernsFinal() && state.equals(op.getState());
		else
			return op.concernsTransition() && symbol.equals(op.getSymbol()) && fromState.equals(op.getFromState()) && toState.equals(op.getToState());
	}
	
	public boolean concernsTransition() {
		return type == TRANSITION;
	}
	
	public boolean concernsFinal() {
		return type == FINAL_STATE;
	}
	
	private int getType() {
		return type;
	}
	public String getState() {
		return state;
	}
	public String getFromState() {
		return fromState;
	}
	public String getToState() {
		return toState;
	}
	public String getSymbol() {
		return symbol;
	}
	
	
}
