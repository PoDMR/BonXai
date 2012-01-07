package eu.fox7.schematoolkit.xsd;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;

import eu.fox7.schematoolkit.SchemaHandler;
import eu.fox7.schematoolkit.xsd.om.XSDSchema;
import eu.fox7.schematoolkit.xsd.parser.XSDParser;
import eu.fox7.schematoolkit.xsd.writer.XSDWriter;

public class XSDSchemaHandler extends SchemaHandler {
	public XSDSchemaHandler() {}
	public XSDSchemaHandler(XSDSchema schema) {
		super(schema);
	}
	
	@Override
	public void parseSchema(InputStream stream) throws IOException, Exception {
		XSDParser parser = new XSDParser(false, false);
		schema = parser.parse(stream);
	}

	@Override
	public void writeSchema(Writer writer) throws IOException, Exception {
		XSDWriter xsdWriter = new XSDWriter((XSDSchema) schema);
		xsdWriter.writeXSD(writer);
	}

}
