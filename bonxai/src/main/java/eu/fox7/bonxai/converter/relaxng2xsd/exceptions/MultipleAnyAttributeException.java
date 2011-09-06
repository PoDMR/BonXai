package eu.fox7.bonxai.converter.relaxng2xsd.exceptions;

/**
 * There are multiple anyAttributes within the same type.
 * @author Lars Schmidt
 */
public class MultipleAnyAttributeException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MultipleAnyAttributeException() {
        super("There are multiple anyAttributes within the same type");
    }

}
