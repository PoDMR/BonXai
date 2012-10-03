package eu.fox7.schematoolkit.xsd;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;

import eu.fox7.schematoolkit.SchemaHandler;
import eu.fox7.schematoolkit.SchemaToolkitException;
import eu.fox7.schematoolkit.xsd.om.XSDSchema;
import eu.fox7.schematoolkit.xsd.parser.XSDParser;
import eu.fox7.schematoolkit.xsd.saxparser.XSDSaxParser;
import eu.fox7.schematoolkit.xsd.writer.XSDWriter;

public class XSDSchemaHandler extends SchemaHandler {
	private static boolean useSaxParser = false;
	
	public XSDSchemaHandler() {}
	public XSDSchemaHandler(XSDSchema schema) {
		super(schema);
	}
	
	@Override
	public void parseSchema(InputStream stream) throws IOException, SchemaToolkitException {
		if (useSaxParser) {
			XSDSaxParser parser = new XSDSaxParser();
			schema = parser.parse(stream);
		} else {
			XSDParser parser = new XSDParser(false, false);
			schema = parser.parse(stream);
		}
	}

	@Override
	public void writeSchema(Writer writer) throws IOException, SchemaToolkitException {
		XSDWriter xsdWriter = new XSDWriter((XSDSchema) schema);
		xsdWriter.writeXSD(writer);
	}
	
	public static void useSaxParser(boolean useSaxParser) {
		XSDSchemaHandler.useSaxParser = useSaxParser;		
	}

}
