package eu.fox7.console.commands;

import jline.Completor;
import eu.fox7.console.Command;

public class ExitCmd extends Command {

	@Override
	public String getCommand() {
		return "exit";
	}

	@Override
	public Completor getCompletor() {
		return commandCompletor();
	}

	@Override
	public void parseCommand(String[] parameters) throws Exception {
		System.exit(0);
	}

}
