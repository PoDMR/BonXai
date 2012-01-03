package eu.fox7.schematoolkit.exceptions;

public class ConversionFailedException extends Exception {

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
