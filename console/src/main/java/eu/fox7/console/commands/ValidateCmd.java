package eu.fox7.console.commands;

import java.util.LinkedList;
import java.util.List;

import jline.ArgumentCompletor;
import jline.Completor;
import eu.fox7.console.Command;
import eu.fox7.schematoolkit.SchemaHandler;
import eu.fox7.schematoolkit.XMLValidator;

public class ValidateCmd extends Command {
	private static final String COMMAND = "validate";

	@Override
	public String getCommand() {
		return COMMAND;
	}

	@Override
	public Completor getCompletor() {
		List<Completor> completors = new LinkedList<Completor>();
		completors.add(commandCompletor());
		completors.add(schemaCompletor());
		completors.add(filenameCompletor());
		completors.add(nullCompletor());
		return new ArgumentCompletor(completors);
	}

	@Override
	public void parseCommand(String[] parameters) throws Exception {
		SchemaHandler schemaHandler = this.getSchemaHandler(parameters[1]);
		XMLValidator validator = schemaHandler.getValidator();
		boolean valid = validator.validate(parameters[2]);
		System.out.println("XMLFile " + parameters[2] + " is " + (valid?"valid":"invalid")+".");
	}

}
