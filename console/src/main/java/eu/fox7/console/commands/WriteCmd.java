package eu.fox7.console.commands;

import java.util.LinkedList;
import java.util.List;

import jline.ArgumentCompletor;
import jline.Completor;
import eu.fox7.console.Command;
import eu.fox7.schematoolkit.SchemaHandler;

public class WriteCmd extends Command {
	private static final String command = "write";
	@Override
	public String getCommand() {
		return command;
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
	public String getUsage() {
		return getCommand()+" <schemaname> <filename>";
	}

	@Override
	public void parseCommand(String[] parameters) throws Exception {
		SchemaHandler schemaHandler = getSchemaHandler(parameters[1]);
		schemaHandler.writeSchema(getFile(parameters[2]));
	}
}
