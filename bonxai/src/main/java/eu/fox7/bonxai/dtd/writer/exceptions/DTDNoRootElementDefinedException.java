package eu.fox7.bonxai.dtd.writer.exceptions;

/**
 * There is no root element defined in the current DTD
 * @author Lars Schmidt
 */
public class DTDNoRootElementDefinedException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DTDNoRootElementDefinedException(String name) {
        super("There is no root element defined in the current DTD");
    }

}
