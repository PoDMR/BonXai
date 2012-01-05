package eu.fox7.console.commands;

import eu.fox7.schematoolkit.SchemaHandler;

public class LoadBonxaiCmd extends AbstractLoadCmd {
	private static final String command = "load-bonxai";

	@Override
	public String getCommand() {
		return command;
	}

	@Override
	public SchemaHandler getNewSchemaHandler() {
		return null;
	}

}
