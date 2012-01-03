package eu.fox7.schematoolkit.xsd;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;

import eu.fox7.bonxai.xsd.XSDSchema;
import eu.fox7.bonxai.xsd.parser.XSDParser;
import eu.fox7.bonxai.xsd.writer.XSDWriter;
import eu.fox7.schematoolkit.SchemaHandler;

public class XSDSchemaHandler extends SchemaHandler {
	public XSDSchemaHandler() {}
	public XSDSchemaHandler(XSDSchema schema) {
		this.schema = schema;
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
