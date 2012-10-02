package eu.fox7.schematoolkit.typeautomaton;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;

import eu.fox7.schematoolkit.SchemaHandler;
import eu.fox7.schematoolkit.SchemaToolkitException;

public class TypeAutomatonSchemaHandler extends SchemaHandler {

	public TypeAutomatonSchemaHandler(
			TypeAutomaton typeAutomaton) {
		super(typeAutomaton);
	}

	@Override
	public void parseSchema(InputStream stream) throws IOException,
			SchemaToolkitException {
		throw new SchemaToolkitException("TypeAutomata cannot be parsed.");
	}

	@Override
	public void writeSchema(Writer writer) throws IOException,
			SchemaToolkitException {
		throw new SchemaToolkitException("TypeAutomata cannot be written.");
	}

}
