package eu.fox7.bonxai.dtd.common.exceptions;

/**
 * The content model of an element with \"mixed\" content contains more than one of the same name
 * @author Lars Schmidt
 */
public class ContentModelIllegalMixedDuplicateElementException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ContentModelIllegalMixedDuplicateElementException(String message) {
        super("The content model of an element with \"mixed\" content contains more than one of the same name: " + message);
    }

}
