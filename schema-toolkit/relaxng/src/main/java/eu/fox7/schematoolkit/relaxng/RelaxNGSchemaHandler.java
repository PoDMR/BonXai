package eu.fox7.schematoolkit.relaxng;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;

import org.xml.sax.SAXException;

import eu.fox7.schematoolkit.SchemaHandler;
import eu.fox7.schematoolkit.SchemaToolkitException;
import eu.fox7.schematoolkit.relaxng.om.RelaxNGSchema;
import eu.fox7.schematoolkit.relaxng.parser.RNGParser;
import eu.fox7.schematoolkit.relaxng.writer.RNGWriter;

public class RelaxNGSchemaHandler extends SchemaHandler {
	public RelaxNGSchemaHandler() {}
	
	public RelaxNGSchemaHandler(RelaxNGSchema relaxNGSchema) {
		super(relaxNGSchema);
	}

	@Override
	public void parseSchema(InputStream stream) throws IOException, SchemaToolkitException {
		try {
			RNGParser parser = new RNGParser(stream, true);
			this.schema = parser.getRNGSchema();
		} catch (SAXException e) {
			throw new SchemaToolkitException(e);
		}
	}

	@Override
	public void writeSchema(Writer writer) throws SchemaToolkitException {
		try {
			RNGWriter rngWriter = new RNGWriter((RelaxNGSchema) this.schema);
			rngWriter.writeRNG(writer);
		} catch (Exception e) {
			throw new SchemaToolkitException(e);
		}
	}

}
