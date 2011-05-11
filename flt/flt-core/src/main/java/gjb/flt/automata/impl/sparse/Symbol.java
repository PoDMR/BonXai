package gjb.flt.automata.impl.sparse;

import java.util.HashMap;
import java.util.Map;

public class Symbol {

	protected final static Map<String,Symbol> symbols = new HashMap<String,Symbol>();
	static Symbol epsilon = new Symbol("epsilon");
	protected String value;

	private Symbol(String value) {
		this.value = value;
	}

	public static Symbol create(String value) {
		if (!symbols.containsKey(value)) {
			Symbol symbol = new Symbol(value);
			symbols.put(value, symbol);
		}
		return symbols.get(value);
	}

	public static void setEpsilon(String value) {
		symbols.remove(Symbol.getEpsilon().toString());
		Symbol.epsilon = Symbol.create(value);
	}

	public static Symbol getEpsilon() {
		return Symbol.epsilon;
	}

	@Override
	public String toString() {
		return this.value;
	}

}
