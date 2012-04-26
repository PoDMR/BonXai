package eu.fox7.console.commands;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import jline.ArgumentCompletor;
import jline.Completor;
import eu.fox7.console.Command;
import eu.fox7.schematoolkit.SchemaHandler;
import eu.fox7.schematoolkit.SchemaLanguage;
import eu.fox7.schematoolkit.SchemaToolkitException;

public class LoadCmd extends Command {
	private static final String command = "load";

	@Override
	public String getCommand() {
		return command;
	}

	@Override
	public Completor getCompletor() {
		List<Completor> completors = new LinkedList<Completor>();
		completors.add(commandCompletor());
		completors.add(filenameCompletor());
		completors.add(nullCompletor());
		return new ArgumentCompletor(completors);
	}

	@Override
	public String getUsage() {
		return getCommand()+" <filename> [<schemaname>]";
	}

	@Override
	public void parseCommand(String[] parameters) throws Exception {
		File file = getFile(parameters[1]);
		String filename = file.getPath();
		String[] nameComponents = filename.split("\\.");
		String extension = "";
		if (nameComponents.length>0) { 
			extension = nameComponents[nameComponents.length - 1];
		}

		String schemaName;
		if (parameters.length == 3)
			schemaName = parameters[2];
		else
			schemaName = getNewSchemaName();

		SchemaLanguage language;
		
		if (extension.equals("xsd")) {
			language = SchemaLanguage.XMLSCHEMA;
		} else if (extension.equals("dtd") || extension.equals("xml")) {
			language = SchemaLanguage.DTD;
		} else if (extension.equals("rng")) {
			language = SchemaLanguage.RELAXNG;
		} else if (extension.equals("bonxai")) {
			language = SchemaLanguage.BONXAI;
		} else {
			System.out.println("Unknown filetype " + extension);
			return;
		}
		
		SchemaHandler schemaHandler = language.getSchemaHandler();

		if (schemaHandler == null)
			throw new SchemaToolkitException("SchemaHandler for " + language + " not found.");
		
		schemaHandler.loadSchema(getFile(parameters[1]));
		
		this.console.addSchema(schemaName, schemaHandler);
	}
}
