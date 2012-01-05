package eu.fox7.console.commands;

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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void parseCommand(String[] parameters) throws Exception {
		SchemaHandler schemaHandler = getSchemaHandler(parameters[1]);
		schemaHandler.writeSchema(getFile(parameters[2]));
	}
}
