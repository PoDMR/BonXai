package eu.fox7.console.commands;

import java.util.LinkedList;
import java.util.List;

import jline.ArgumentCompletor;
import jline.Completor;
import eu.fox7.console.Command;
import eu.fox7.schematoolkit.Schema;
import eu.fox7.schematoolkit.SchemaHandler;
import eu.fox7.schematoolkit.SchemaOperation;

public class IntersectionCmd extends Command {
	private static final String COMMAND = "intersect";
	private static final String XSDINTERSECTION = "eu.fox7.schematoolkit.xsd.operations.XSDIntersection";
	
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
		Class<SchemaOperation> intersectionClass = (Class<SchemaOperation>) this.getClass().getClassLoader().loadClass(XSDINTERSECTION);
		SchemaOperation intersection = intersectionClass.newInstance();
		
		for (int i=1; i<parameters.length; i++) {
			Schema schema = this.getSchema(parameters[i]);
			intersection.addSchema(schema);
		}
		
		SchemaHandler schemaHandler = intersection.apply();
		
		this.console.addSchema(getNewSchemaName(), schemaHandler);

	}
}
