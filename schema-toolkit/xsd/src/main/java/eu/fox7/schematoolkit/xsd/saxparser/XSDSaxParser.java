package eu.fox7.schematoolkit.xsd.saxparser;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

import eu.fox7.schematoolkit.Schema;
import eu.fox7.schematoolkit.SchemaToolkitException;
import eu.fox7.schematoolkit.xsd.om.XSDSchema;

public class XSDSaxParser {
	private XSDParserHandler xsdParserHandler = new XSDParserHandler();
	
	public XSDSchema parse(File file) throws IOException, SchemaToolkitException {
	    // creates and returns new instance of SAX-implementation:
	    SAXParserFactory factory = SAXParserFactory.newInstance();
	    factory.setNamespaceAware(true);
	    try {
			// create SAX-parser...
			SAXParser parser = factory.newSAXParser();
			parser.parse(file, xsdParserHandler);
		} catch (ParserConfigurationException e) {
			throw new SchemaToolkitException(e);
		} catch (SAXException e) {
			throw new SchemaToolkitException(e);
		}

		return xsdParserHandler.getSchema();
	}

	public Schema parse(InputStream stream) throws IOException, SchemaToolkitException {
	    // creates and returns new instance of SAX-implementation:
	    SAXParserFactory factory = SAXParserFactory.newInstance();
	    factory.setNamespaceAware(true);
	    try {
			// create SAX-parser...
			SAXParser parser = factory.newSAXParser();
			parser.parse(stream, xsdParserHandler);
		} catch (ParserConfigurationException e) {
			throw new SchemaToolkitException(e);
		} catch (SAXException e) {
			throw new SchemaToolkitException(e);
		}

		return xsdParserHandler.getSchema();
	}
	
}
