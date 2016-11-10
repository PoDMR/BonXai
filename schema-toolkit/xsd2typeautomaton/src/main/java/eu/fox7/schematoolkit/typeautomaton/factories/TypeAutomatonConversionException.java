package eu.fox7.schematoolkit.typeautomaton.factories;

import eu.fox7.schematoolkit.exceptions.ConversionFailedException;

public class TypeAutomatonConversionException extends ConversionFailedException {

	private static final long serialVersionUID = 1L;

	public TypeAutomatonConversionException() {
	}

	public TypeAutomatonConversionException(String message) {
		super(message);
	}

	public TypeAutomatonConversionException(Throwable cause) {
		super(cause);
	}

	public TypeAutomatonConversionException(String message, Throwable cause) {
		super(message, cause);
	}

}
