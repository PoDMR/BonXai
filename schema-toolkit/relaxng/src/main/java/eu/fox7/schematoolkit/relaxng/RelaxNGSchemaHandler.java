package eu.fox7.schematoolkit.relaxng;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;

import org.xml.sax.SAXException;

import eu.fox7.schematoolkit.SchemaHandler;
import eu.fox7.schematoolkit.relaxng.om.RelaxNGSchema;
import eu.fox7.schematoolkit.relaxng.parser.RNGParser;
import eu.fox7.schematoolkit.relaxng.writer.RNGWriter;

public class RelaxNGSchemaHandler extends SchemaHandler {
	@Override
	public void parseSchema(InputStream stream) throws IOException, SAXException {
		RNGParser parser = new RNGParser(stream, true);
		this.schema = parser.getRNGSchema();
	}

	@Override
	public void writeSchema(Writer writer) throws Exception {
		RNGWriter rngWriter = new RNGWriter((RelaxNGSchema) this.schema);
		rngWriter.writeRNG(writer);
	}

}
