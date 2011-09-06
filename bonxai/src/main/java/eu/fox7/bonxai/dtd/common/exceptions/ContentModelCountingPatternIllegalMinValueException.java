package eu.fox7.bonxai.dtd.common.exceptions;

/**
 * The minOccurs value of a countingPattern is not allowed
 * @author Lars Schmidt
 */
public class ContentModelCountingPatternIllegalMinValueException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ContentModelCountingPatternIllegalMinValueException(String value) {
        super("The minOccurs value of a countingPattern is not allowed in a DTD element content model: " + value);
    }

}
