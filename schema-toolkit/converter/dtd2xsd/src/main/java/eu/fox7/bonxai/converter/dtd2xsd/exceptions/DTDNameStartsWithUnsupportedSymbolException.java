package eu.fox7.bonxai.converter.dtd2xsd.exceptions;

/**
 * This exception will be thrown if there is a DTD name that starts with an
 * unsupported symbol
 * @author Lars Schmidt
 */
public class DTDNameStartsWithUnsupportedSymbolException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DTDNameStartsWithUnsupportedSymbolException(String dtdName) {
        super("There is a DTD name that starts with an unsupported symbol: \"" + dtdName + "\"");
    }

}
