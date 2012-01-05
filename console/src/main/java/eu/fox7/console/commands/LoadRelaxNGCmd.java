package eu.fox7.console.commands;

import eu.fox7.schematoolkit.SchemaHandler;
import eu.fox7.schematoolkit.relaxng.RelaxNGSchemaHandler;

public class LoadRelaxNGCmd extends AbstractLoadCmd {
	@Override
	public SchemaHandler getNewSchemaHandler() {
		return new RelaxNGSchemaHandler();
	}

	@Override
	public String getCommand() {
		return "load-relaxng";
	}

}
