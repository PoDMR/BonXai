package eu.fox7.schematoolkit.dtd.common.exceptions;

/**
 * Notation is not declared but used.
 * @author Lars Schmidt
 */
public class NotationNotDeclaredException extends DTDException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NotationNotDeclaredException(String attributeName, String notationName) {
        super("A notation is not declared but used in attribute:" + "\"" + attributeName + "\"" + " notation: \"" + notationName + "\"");
    }

}
