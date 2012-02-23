package eu.fox7.schematoolkit.dtd.common.exceptions;

/**
 * The values of a countingPattern is not allowed
 * @author Lars Schmidt
 */
public class ContentModelCountingPatternNotAllowedDTDValueException extends DTDException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ContentModelCountingPatternNotAllowedDTDValueException(String values) {
        super("The values of a countingPattern is not allowed in a DTD element content model: " + values);
    }

}
