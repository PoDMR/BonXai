package eu.fox7.console.commands;

import java.io.File;

import jline.Completor;
import eu.fox7.console.Command;
import eu.fox7.schematoolkit.SchemaHandler;
import eu.fox7.schematoolkit.SchemaLanguage;

public class LoadCmd extends Command {
	private static final String command = "load";

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

		schemaHandler.loadSchema(getFile(parameters[1]));
		
		this.console.addSchema(schemaName, schemaHandler);
	}
}
