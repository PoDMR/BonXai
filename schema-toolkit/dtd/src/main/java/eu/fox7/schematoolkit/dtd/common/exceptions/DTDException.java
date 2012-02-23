package eu.fox7.schematoolkit.dtd.common.exceptions;

import eu.fox7.schematoolkit.SchemaToolkitException;

public abstract class DTDException extends SchemaToolkitException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DTDException() {
	}

	public DTDException(String message) {
		super(message);
	}

	public DTDException(Throwable cause) {
		super(cause);
	}

	public DTDException(String message, Throwable cause) {
		super(message, cause);
	}

}
