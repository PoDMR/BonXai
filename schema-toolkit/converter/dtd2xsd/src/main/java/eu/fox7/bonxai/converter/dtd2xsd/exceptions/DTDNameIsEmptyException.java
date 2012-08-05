package eu.fox7.bonxai.converter.dtd2xsd.exceptions;

import eu.fox7.schematoolkit.exceptions.ConversionFailedException;

/**
 * This exception will be thrown if there is an empty DTD name string
 * @author Lars Schmidt
 */
public class DTDNameIsEmptyException extends ConversionFailedException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DTDNameIsEmptyException() {
        super("There is an empty DTD name!");
    }

}
