package eu.fox7.bonxai.dtd.writer.exceptions;

/**
 * There is an empty content model for an element
 * @author Lars Schmidt
 */
public class DTDElementContentModelEmptyException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DTDElementContentModelEmptyException(String name) {
        super("There is an empty content model for the following element: " + name);
    }

}
