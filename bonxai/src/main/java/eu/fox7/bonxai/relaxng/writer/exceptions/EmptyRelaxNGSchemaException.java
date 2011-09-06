package eu.fox7.bonxai.relaxng.writer.exceptions;

/**
 * Exception: The given Relax NG XSDSchema is empty or has no root pattern.
 * @author Lars Schmidt
 */
public class EmptyRelaxNGSchemaException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public EmptyRelaxNGSchemaException() {
        super ("The given Relax NG XSDSchema is empty or has no root pattern.");
    }

}
