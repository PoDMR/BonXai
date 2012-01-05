package eu.fox7.console.commands;

import java.io.File;

import jline.Completor;
import eu.fox7.console.Command;

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
		
		if (extension.equals("xsd")) {
			parameters[0] = "load-xsd";
		} else if (extension.equals("dtd") || extension.equals("xml")) {
			parameters[0] = "load-dtd";
		} else if (extension.equals("rng")) {
			parameters[0] = "load-relaxng";
		} else if (extension.equals("bonxai")) {
			parameters[0] = "load-bonxai";
		} else {
			System.out.println("Unknown filetype " + extension);
			return;
		}

		this.console.parseCommand(parameters);
	}
}
