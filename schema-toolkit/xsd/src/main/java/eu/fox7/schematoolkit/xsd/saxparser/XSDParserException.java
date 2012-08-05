package eu.fox7.schematoolkit.xsd.saxparser;

import org.xml.sax.SAXException;

import eu.fox7.schematoolkit.SchemaToolkitException;

public class XSDParserException extends SAXException {
	
	public XSDParserException(Exception e) {
		super(e);
	}

}
