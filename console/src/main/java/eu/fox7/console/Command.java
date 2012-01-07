package eu.fox7.console;

import java.io.File;

import eu.fox7.schematoolkit.Schema;
import eu.fox7.schematoolkit.SchemaHandler;
import eu.fox7.schematoolkit.SchemaLanguage;
import jline.Completor;
import jline.SimpleCompletor;

public abstract class Command implements Comparable<Command>{
	protected Console console;
	
	private static int schemaNumber = 0;
	
	public abstract String getCommand();
	public abstract Completor getCompletor();
	public abstract void parseCommand(String[] parameters) throws Exception;
	
	public String getHelp() {
		return "No help available for command " + getCommand() + ".";
	}
	
	public String getUsage() {
		return getCommand()  + " <arguments>";
	}
	
	public void registerConsole(Console console) {
		this.console = console;
	}
	
	protected SchemaHandler getSchemaHandler(String schemaName) throws NoSuchSchemaException {
		return this.console.getSchemaHandler(schemaName);
	}
	
	protected Schema getSchema(String schemaName) throws NoSuchSchemaException {
		return this.getSchemaHandler(schemaName).getSchema();
	}
	
	protected File getFile(String filename) {
		return new File(filename);
	}
	
	protected Completor schemaCompletor() {
		return console.getSchemaCompletor(); 
	}
	
	protected Completor commandCompletor() {
		return new SimpleCompletor(getCommand());
	}
	
	protected String getNewSchemaName() {
		return "schema" + Integer.toString(schemaNumber++);
	}
	
	protected SchemaLanguage getLanguage(String language) {
		return SchemaLanguage.valueOf(language);
	}
	
	public int compareTo(Command command) {
		return getCommand().compareTo(command.getCommand());
	}
	
	public boolean equals(Object command) {
		if (command instanceof Command) 
			return getCommand().equals(((Command) command).getCommand());
		else
			return false;
	}
	
	public int hashCode() {
		return getCommand().hashCode();
	}
}
