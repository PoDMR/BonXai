package eu.fox7.console.commands;

import java.util.LinkedList;
import java.util.List;

import jline.ArgumentCompletor;
import jline.Completor;
import eu.fox7.console.Command;
import eu.fox7.schematoolkit.Schema;
import eu.fox7.schematoolkit.SchemaHandler;
import eu.fox7.schematoolkit.SchemaOperation;

public class UnionCmd extends Command {
	private static final String COMMAND = "union";
	private static final String XSDUNION = "eu.fox7.schematoolkit.xsd.operations.XSDUnion";
	
	@Override
	public String getCommand() {
		return COMMAND;
	}

	@Override
	public Completor getCompletor() {
		List<Completor> completors = new LinkedList<Completor>();
		completors.add(commandCompletor());
		completors.add(schemaCompletor());
		return new ArgumentCompletor(completors);
	}

	@Override
	public void parseCommand(String[] parameters) throws Exception {
		Class<SchemaOperation> unionClass = (Class<SchemaOperation>) this.getClass().getClassLoader().loadClass(XSDUNION);
		SchemaOperation union = unionClass.newInstance();
		
		for (int i=1; i<parameters.length; i++) {
			Schema schema = this.getSchema(parameters[i]);
			union.addSchema(schema);
		}
		
		SchemaHandler schemaHandler = union.apply();
		
		this.console.addSchema(getNewSchemaName(), schemaHandler);

	}

}
