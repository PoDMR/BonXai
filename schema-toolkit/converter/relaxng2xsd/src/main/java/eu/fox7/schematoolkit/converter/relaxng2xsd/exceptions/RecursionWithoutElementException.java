package eu.fox7.schematoolkit.converter.relaxng2xsd.exceptions;

/**
 * There is a recursive structure without an element, this is not valid in RELAX NG.
 * @author Lars Schmidt
 */
public class RecursionWithoutElementException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RecursionWithoutElementException() {
        super("There is a recursive structure without an element, this is not valid in RELAX NG.");
    }

}
