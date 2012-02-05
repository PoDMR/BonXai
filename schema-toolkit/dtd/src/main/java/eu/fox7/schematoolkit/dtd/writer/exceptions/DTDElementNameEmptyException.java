package eu.fox7.schematoolkit.dtd.writer.exceptions;

/**
 * There is no name defined for an element in the current DTD
 * @author Lars Schmidt
 */
public class DTDElementNameEmptyException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DTDElementNameEmptyException(String name) {
        super("There is no name defined for an element in the current DTD");
    }

}
