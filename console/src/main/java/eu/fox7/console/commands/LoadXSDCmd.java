package eu.fox7.console.commands;

import java.io.File;

import eu.fox7.schematoolkit.SchemaHandler;
import eu.fox7.schematoolkit.xsd.XSDSchemaHandler;
import eu.fox7.schematoolkit.xsd.om.XSDSchema;
import eu.fox7.schematoolkit.xsd.parser.XSDParser;

public class LoadXSDCmd extends AbstractLoadCmd {
	private static final String command = "load-xsd";

	@Override
	public String getCommand() {
		return command;
	}

	@Override
	public XSDSchemaHandler getNewSchemaHandler() {
		return new XSDSchemaHandler();
	}
}
