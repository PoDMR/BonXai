package eu.fox7.schematoolkit.dtd;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;

import org.xml.sax.SAXException;

import eu.fox7.schematoolkit.SchemaHandler;
import eu.fox7.schematoolkit.SchemaToolkitException;
import eu.fox7.schematoolkit.dtd.om.DocumentTypeDefinition;
import eu.fox7.schematoolkit.dtd.parser.DTDSAXParser;
import eu.fox7.schematoolkit.dtd.writer.DTDWriter;

public class DTDSchemaHandler extends SchemaHandler {

	public DTDSchemaHandler() {
	}
	
	public DTDSchemaHandler(DocumentTypeDefinition dtd) {
		this.schema = dtd;
	}
	
	@Override
	public void parseSchema(InputStream stream) throws IOException,
			SchemaToolkitException {
		DTDSAXParser parser = new DTDSAXParser();
		try {
			this.schema = parser.parseDTDOnly(stream);
		} catch (SAXException e) {
			throw new SchemaToolkitException(e);
		}
		

	}

	@Override
	public void writeSchema(Writer writer) throws IOException,
			SchemaToolkitException {
		DTDWriter dtdWriter = new DTDWriter((DocumentTypeDefinition) schema);
		String dtd = dtdWriter.getExternalSubsetString();
		writer.append(dtd);
	}

}
