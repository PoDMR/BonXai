package eu.fox7.bonxai.converter;

public class ConversionFailedException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ConversionFailedException() {
	}

	public ConversionFailedException(String arg0) {
		super(arg0);
	}

	public ConversionFailedException(Throwable arg0) {
		super(arg0);
	}

	public ConversionFailedException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

}
