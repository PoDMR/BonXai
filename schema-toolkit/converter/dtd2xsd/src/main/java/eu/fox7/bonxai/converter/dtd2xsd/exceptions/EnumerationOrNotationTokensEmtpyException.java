package eu.fox7.bonxai.converter.dtd2xsd.exceptions;

import eu.fox7.schematoolkit.exceptions.ConversionFailedException;

/**
 * There are no values in an enumeration type of DTD
 * @author Lars Schmidt
 */
public class EnumerationOrNotationTokensEmtpyException extends ConversionFailedException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public EnumerationOrNotationTokensEmtpyException(String attributeName, String typeName) {
        super("There are no values in the following enumeration type of DTD: \"" + attributeName + "\" with the following type: \"" + typeName + "\"");
    }

}
