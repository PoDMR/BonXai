package eu.fox7.console.commands;

import jline.Completor;
import eu.fox7.console.Command;
import eu.fox7.schematoolkit.Schema;
import eu.fox7.schematoolkit.SchemaConverter;
import eu.fox7.schematoolkit.SchemaHandler;
import eu.fox7.schematoolkit.SchemaLanguage;


public class ConvertCmd extends Command {
	private static final String command = "convert";
	
	@Override
	public String getCommand() {
		return command;
	}

	@Override
	public Completor getCompletor() {
		return commandCompletor();
	}

	@Override
	public void parseCommand(String[] parameters) throws Exception {
		SchemaHandler sourceHandler = this.getSchemaHandler(parameters[1]);
		SchemaLanguage sourceLanguage = sourceHandler.getSchema().getSchemaLanguage();
		SchemaLanguage targetLanguage = this.getLanguage(parameters[2]);
		SchemaConverter converter = sourceLanguage.getConverter(targetLanguage);
		SchemaHandler targetHandler = converter.convert(sourceHandler);
		
		String schemaName;
		if (parameters.length>3)
			schemaName = parameters[3];
		else
			schemaName = this.getNewSchemaName();
		
		this.console.addSchema(schemaName, targetHandler);
	}

}
