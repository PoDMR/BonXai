package eu.fox7.schematoolkit.converter.relaxng2xsd.exceptions;

/**
 * A generated type is null
 * @author Lars Schmidt
 */
public class TypeIsNullException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TypeIsNullException(String type, String info) {
        super("The following generated type is null: \"" + type + "\" (" + info + ")");
    }

}
