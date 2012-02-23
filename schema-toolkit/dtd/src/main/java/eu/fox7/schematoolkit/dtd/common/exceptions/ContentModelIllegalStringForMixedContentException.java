package eu.fox7.schematoolkit.dtd.common.exceptions;

/**
 * The specific string is not allowed in a DTD element content model
 * @author Lars Schmidt
 */
public class ContentModelIllegalStringForMixedContentException extends DTDException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ContentModelIllegalStringForMixedContentException(String value) {
        super("The following string is not allowed in a DTD element content model: " + value);
    }
}
