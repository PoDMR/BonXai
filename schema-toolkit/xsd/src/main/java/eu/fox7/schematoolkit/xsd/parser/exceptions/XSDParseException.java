package eu.fox7.schematoolkit.xsd.parser.exceptions;

import eu.fox7.schematoolkit.SchemaToolkitException;

public class XSDParseException extends SchemaToolkitException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public XSDParseException() {
		super();
	}

	public XSDParseException(Exception cause) {
		super(cause);
	}

	public XSDParseException(String string) {
		super(string);
	}

}
