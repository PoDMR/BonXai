package eu.fox7.schematoolkit.converter.relaxng2xsd.exceptions;

/**
 * There is an unqualified name in the following structure.
 * @author Lars Schmidt
 */
public class OnlyFullQualifiedNamesAllowedException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public OnlyFullQualifiedNamesAllowedException(String info) {
        super("There is an unqualified name in the following structure: \"" + info + "\"");
    }

}
