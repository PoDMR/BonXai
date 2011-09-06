package eu.fox7.bonxai.dtd.writer.exceptions;

/**
 * There is no name defined for an internal entity in the current DTD
 * @author Lars Schmidt
 */
public class DTDInternalEntityEmptyNameException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DTDInternalEntityEmptyNameException(String name) {
        super("There is no name defined for an internal entity in the current DTD");
    }

}
