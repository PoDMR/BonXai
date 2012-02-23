package eu.fox7.schematoolkit.exceptions;

public class ConversionFailedException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ConversionFailedException() {
	}

	public ConversionFailedException(String message) {
		super(message);
	}

	public ConversionFailedException(Throwable cause) {
		super(cause);
	}

	public ConversionFailedException(String message, Throwable cause) {
		super(message, cause);
	}

}
