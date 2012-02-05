package eu.fox7.schematoolkit.dtd.common.exceptions;

/**
 * The testet string is not conform to the NAME type of XML
 * @author Lars Schmidt
 */
public class IllegalNAMEStringException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public IllegalNAMEStringException(String place, String string) {
        super("The testet string is not conform to the NAME type of XML in: " + "\"" + place + "\"" + " String: \"" + string + "\"");
    }
}
