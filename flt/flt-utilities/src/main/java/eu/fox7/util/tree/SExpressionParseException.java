package eu.fox7.util.tree;

/**
 * Class <code>SExpressionParseException</code> is thrown whenever a
 * syntax error in an S-Expression is encountered during parsing.  If
 * necessary, in can encapsulate I/O excpetions.
 *
 * @author <a href="mailto:eu.fox7@luc.ac.be">Geert Jan Bex</a>
 * @version 1.0
 */
public class SExpressionParseException extends TreeException {

    private static final long serialVersionUID = 1L;
    protected String parseStr;
	protected Exception exception;

	/**
	 * Creates a new <code>SExpressionParseException</code> instance.
	 *
	 * @param str a <code>String</code> value specifying the nature of
	 * the exception
	 */
	public SExpressionParseException(String str) {
		super();
		parseStr = str;
	}

	/**
	 * Creates a new <code>SExpressionParseException</code> instance.
	 *
	 * @param exception an <code>Exception</code> value to be
	 * encapsulated
	 */
	public SExpressionParseException(Exception exception) {
		super();
		this.exception = exception;
	}

	/**
	 * <code>exception</code> method returns the encapsulated
	 * exception.
	 *
	 * @return an <code>Exception</code> value
	 */
	public Exception exception() {
		return exception;
	}

	/**
	 * <code>getMessage</code> method returns the exception message.
	 *
	 * @return a <code>String</code> value, the exception message
	 */
	public String getMessage() {
		if (exception == null) {
			return "syntax error in Lisp tree definition: '" + parseStr + "'";
		} else {
			return "original exception: " + exception().getMessage();
		}
	}

}
