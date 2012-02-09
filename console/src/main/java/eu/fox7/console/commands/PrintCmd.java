package eu.fox7.console.commands;

import java.util.LinkedList;
import java.util.List;

import jline.ArgumentCompletor;
import jline.Completor;
import eu.fox7.console.Command;
import eu.fox7.schematoolkit.SchemaHandler;

public class PrintCmd extends Command {
	private static final String command = "print";
	@Override
	public String getCommand() {
		return command;
	}

	@Override
	public Completor getCompletor() {
		List<Completor> completors = new LinkedList<Completor>();
		completors.add(commandCompletor());
		completors.add(schemaCompletor());
		completors.add(nullCompletor());
		return new ArgumentCompletor(completors);
	}

	@Override
	public String getUsage() {
		return getCommand()+" <schemaname>";
	}

	@Override
	public void parseCommand(String[] parameters) throws Exception {
		SchemaHandler schemaHandler = getSchemaHandler(parameters[1]);
		String schemaString = schemaHandler.getSchemaString();
		System.out.println(schemaString);
	}

}
