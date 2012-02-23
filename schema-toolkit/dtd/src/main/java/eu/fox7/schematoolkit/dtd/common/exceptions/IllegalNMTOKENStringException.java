package eu.fox7.schematoolkit.dtd.common.exceptions;

/**
 * The testet string is not conform to the NMTOKEN type of XML
 * @author Lars Schmidt
 */
public class IllegalNMTOKENStringException extends DTDException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public IllegalNMTOKENStringException(String place, String string) {
        super("The testet string is not conform to the NMTOKEN type of XML in: " + "\"" + place + "\"" + " String: \"" + string + "\"");
    }
}
