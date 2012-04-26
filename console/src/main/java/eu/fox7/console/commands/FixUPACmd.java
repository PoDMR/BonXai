package eu.fox7.console.commands;

import java.util.LinkedList;
import java.util.List;

import jline.ArgumentCompletor;
import jline.Completor;
import eu.fox7.console.Command;
import eu.fox7.schematoolkit.Schema;
import eu.fox7.schematoolkit.UPAFixer;

public class FixUPACmd extends Command {
	private static final String COMMAND="fixUPA";
	private static final String UPAFIXER="eu.fox7.upafixer.impl.BKWUPAFixer";
	
	@Override
	public String getCommand() {
		return COMMAND;
	}

	@Override
	public Completor getCompletor() {
		List<Completor> completors = new LinkedList<Completor>();
		completors.add(commandCompletor());
		completors.add(schemaCompletor());
		completors.add(nullCompletor());
		return new ArgumentCompletor(completors);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void parseCommand(String[] parameters) throws Exception {
		Schema schema = this.getSchema(parameters[1]);
		Class upafixerClass = this.getClass().getClassLoader().loadClass(UPAFIXER);
		UPAFixer upaFixer = (UPAFixer) upafixerClass.newInstance();
		upaFixer.fixUPA(schema);
	}

}
