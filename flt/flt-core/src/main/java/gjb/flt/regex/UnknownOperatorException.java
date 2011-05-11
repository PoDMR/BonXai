package gjb.flt.regex;

public class UnknownOperatorException extends RegexException {

    private static final long serialVersionUID = 5456796139005899176L;
	protected String operator, regex;

	public UnknownOperatorException(String operator) {
		super();
		this.operator = operator;
	}

	public UnknownOperatorException(String operator, String regex) {
		this(operator);
		this.regex = regex;
	}

	public String operator() {
		return operator;
	}		

	@Override
	public String getMessage() {
		StringBuffer str = new StringBuffer();
		str.append("unknown operator '").append(operator()).append("'");
		if (regex != null) {
			str.append(" in regular expression '").append(regex).append("'");
		}
		return str.toString();
	}

}
