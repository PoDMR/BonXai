package eu.fox7.schematoolkit.bonxai;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;

import eu.fox7.schematoolkit.SchemaHandler;
import eu.fox7.schematoolkit.bonxai.om.Bonxai;
import eu.fox7.schematoolkit.bonxai.parser.BonxaiParser;
import eu.fox7.schematoolkit.bonxai.parser.CompactSyntaxParser;
import eu.fox7.schematoolkit.bonxai.parser.ParseException;
import eu.fox7.schematoolkit.bonxai.writer.CompactSyntaxWriter;

public class BonxaiSchemaHandler extends SchemaHandler {
	public BonxaiSchemaHandler() {}
	
	public BonxaiSchemaHandler(Bonxai bonxai) {
		super(bonxai);
	}
	
	@Override
	public void parseSchema(InputStream stream) throws IOException, ParseException {
		BonxaiParser parser = new CompactSyntaxParser();
		Reader reader = new InputStreamReader(stream);
		this.schema = parser.parse(reader);
	}

	@Override
	public void writeSchema(Writer writer) throws IOException {
		CompactSyntaxWriter csw = new CompactSyntaxWriter();
		csw.write((Bonxai) schema, writer);
	}

}
