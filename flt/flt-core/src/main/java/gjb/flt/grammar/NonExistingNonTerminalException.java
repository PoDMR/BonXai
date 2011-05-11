package gjb.flt.grammar;

public class NonExistingNonTerminalException extends Exception {

    private static final long serialVersionUID = 1L;
    protected String symbol;

	public NonExistingNonTerminalException(String symbol) {
		super();
		this.symbol = symbol;
	}

	public String getMessage() {
		return "not a non-terminal symbol of the grammar: '" + symbol + "'";
	}

}
