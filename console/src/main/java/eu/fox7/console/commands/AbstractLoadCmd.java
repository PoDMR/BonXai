package eu.fox7.console.commands;

import jline.Completor;
import eu.fox7.console.Command;
import eu.fox7.schematoolkit.SchemaHandler;

public abstract class AbstractLoadCmd extends Command {
	@Override
	public Completor getCompletor() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public abstract SchemaHandler getNewSchemaHandler();

	@Override
	public void parseCommand(String[] parameters) throws Exception {
		String schemaName;
		if (parameters.length == 3)
			schemaName = parameters[2];
		else
			schemaName = getNewSchemaName();
		
		SchemaHandler schemaHandler = this.getNewSchemaHandler();
		schemaHandler.loadSchema(getFile(parameters[1]));
		
		this.console.addSchema(schemaName, schemaHandler);
	}

}
