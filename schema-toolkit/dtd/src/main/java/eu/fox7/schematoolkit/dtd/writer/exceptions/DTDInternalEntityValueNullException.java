package eu.fox7.schematoolkit.dtd.writer.exceptions;

/**
 * There is no value defined in a DTD internal entity
 * @author Lars Schmidt
 */
public class DTDInternalEntityValueNullException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DTDInternalEntityValueNullException(String name) {
        super("There is value defined in the following DTD internal entity: " + name);
    }

}
