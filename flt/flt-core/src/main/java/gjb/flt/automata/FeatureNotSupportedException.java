package gjb.flt.automata;

public class FeatureNotSupportedException extends NFAException {

    private static final long serialVersionUID = 1L;
    protected String message;

	public FeatureNotSupportedException(String message) {
		super();
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

}
