package gjb.flt.automata.impl.sparse;


import java.util.Map;

public class InsertionPoint extends Transition {

	protected String nonTerminal;
	protected String[] production;
	protected int insertionLocation;

	public InsertionPoint(String nonTerminal, String[] prod,
						  int insertionLocation, Symbol symbol,
						  State fromState, State toState) {
		super(symbol, fromState, toState);
		this.nonTerminal = nonTerminal;
		this.production = new String[prod.length];
		for (int i = 0; i < prod.length; i++) {
			this.production[i] = prod[i];
		}
		this.insertionLocation = insertionLocation;
	}

	public InsertionPoint(InsertionPoint point, Map<State,State> stateMap) {
		super(point.getSymbol(), stateMap.get(point.getFromState()),
			  stateMap.get(point.getToState()));
		this.nonTerminal = point.nonTerminal();
		String[] prod = point.production();
		this.production = new String[prod.length];
		for (int i = 0; i < prod.length; i++) {
			this.production[i] = prod[i];
		}
		this.insertionLocation = point.insertionLocation();
	}

	public String nonTerminal() {
		return nonTerminal;
	}

	public String[] production() {
	    String[] result = new String[production.length];
	    for (int i = 0; i < production.length; i++) {
	        result[i] = production[i];
        }
		return result;
	}

	public int insertionLocation() {
		return insertionLocation;
	}

	public String toString() {
		StringBuffer str = new StringBuffer();
		str.append(nonTerminal).append(" ->");
		int i;
		for (i = 0; i < insertionLocation; i++) {
			str.append(" ").append(production[i]);
		}
		str.append(" [").append(production[i]).append("]");
		for (i = insertionLocation+1; i < production.length; i++) {
			str.append(" ").append(production[i]);
		}
		return str.toString();
	}

}
