package eu.fox7.schematoolkit.dtd.writer.exceptions;

import eu.fox7.schematoolkit.dtd.common.exceptions.DTDException;

/**
 * There is no root element defined in the current DTD
 * @author Lars Schmidt
 */
public class DTDNoRootElementDefinedException extends DTDException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DTDNoRootElementDefinedException(String name) {
        super("There is no root element defined in the current DTD");
    }

}
