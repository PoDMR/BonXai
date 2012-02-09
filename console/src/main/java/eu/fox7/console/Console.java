package eu.fox7.console;


import java.io.IOException;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.List;
import java.util.ArrayList;

import jline.Completor;
import jline.ConsoleReader;
import jline.FileNameCompletor;
import jline.MultiCompletor;
import jline.NullCompletor;
import jline.SimpleCompletor;



import eu.fox7.schematoolkit.Schema;
import eu.fox7.schematoolkit.SchemaHandler;
import eu.fox7.schematoolkit.SchemaLanguage;
import eu.fox7.console.commands.*;


public class Console {

	private ConsoleReader consoleReader = null;
	private static final String DEFAULT_PROMPT = "> ";
	private boolean running;
	
	private String workingDirectory;
	
	private SimpleCompletor schemaCompletor = new SimpleCompletor(new String[]{});
	private SimpleCompletor settingNameCompletor = null;
	
	private SortedMap<String,SchemaHandler> schemas = new TreeMap<String,SchemaHandler>();
	
	private SortedMap<String,Command> commands = new TreeMap<String,Command>();

	private List<Completor> completors = new ArrayList<Completor>();
	private Completor filenameCompletor = new FileNameCompletor();
	private Completor languageCompletor;
	
	private class HelpCmd extends Command {
		private static final String command = "help";
		@Override
		public String getCommand() {
			return command;
		}

		@Override
		public Completor getCompletor() {
			return commandCompletor();
		}

		@Override
		public void parseCommand(String[] commandLine) throws Exception {
			System.out.println("Try tab.");
		}		
	}
	
	private class ListCmd extends Command{
		private static final String command = "list";
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
			for(Entry<String,SchemaHandler> entry: this.console.schemas.entrySet()) {
				System.out.println(entry.getKey()+": "+entry.getValue().getSchemaLanguage());
			}
			
		}
		
	}
	
	public Console() {
		try {
			consoleReader = new ConsoleReader();
		} catch (IOException e) {
			e.printStackTrace();
		}
						
		
		languageCompletor = new SimpleCompletor(new String[] {
			SchemaLanguage.BONXAI.name(), 
			SchemaLanguage.DTD.name(), 
			SchemaLanguage.RELAXNG.name(), 
			SchemaLanguage.XMLSCHEMA.name() 
		} );
//		completors.add(new ArgumentCompletor(new Completor[] {new SimpleCompletor("addXML"), fileNameCompletor } ));
//		completors.add(new ArgumentCompletor(new Completor[] {new SimpleCompletor("convert"), schemaNumberCompletor, new SimpleCompletor(new String[]{"DTD", "XSD", "RelaxNG", "BonXai"}), nullCompletor}));
//		completors.add(new ArgumentCompletor(new Completor[] {new SimpleCompletor("difference"), schemaNumberCompletor, schemaNumberCompletor, nullCompletor}));
//		completors.add(new SimpleCompletor("exit"));
//		completors.add(new ArgumentCompletor(new Completor[] {new SimpleCompletor("fixEDC"), schemaNumberCompletor, nullCompletor}));
//		completors.add(new ArgumentCompletor(new Completor[] {new SimpleCompletor("fixUPA"), schemaNumberCompletor, nullCompletor}));
//		completors.add(new SimpleCompletor("help"));
//		completors.add(new ArgumentCompletor(new Completor[] {new SimpleCompletor("intersect"), schemaNumberCompletor}));
//		completors.add(new ArgumentCompletor(new Completor[] { new SimpleCompletor("load"), fileNameCompletor } ));
//		completors.add(new ArgumentCompletor(new Completor[] { new SimpleCompletor("lernXSD"), nullCompletor } ));
//		completors.add(new ArgumentCompletor(new Completor[] { new SimpleCompletor("learnPreProcessed"), fileNameCompletor, new SimpleCompletor(new String[]{"1", "2", "3", "4", "5"}), nullCompletor } ));
//		completors.add(new SimpleCompletor("list"));
//		completors.add(new ArgumentCompletor(new Completor[] {new SimpleCompletor("print"), schemaNumberCompletor, nullCompletor}));
//		completors.add(new ArgumentCompletor(new Completor[] {new SimpleCompletor("printContextAutomaton"), schemaNumberCompletor, nullCompletor}));
//		completors.add(new ArgumentCompletor(new Completor[] {new SimpleCompletor("printRootElements"), schemaNumberCompletor, nullCompletor}));
//		completors.add(new ArgumentCompletor(new Completor[] {new SimpleCompletor("remove"), schemaNumberCompletor, nullCompletor}));
//		completors.add(new ArgumentCompletor(new Completor[] {new SimpleCompletor("removeUnreachableTypes"), schemaNumberCompletor, nullCompletor}));
//		completors.add(new ArgumentCompletor(new Completor[] {new SimpleCompletor("removeEmptyTypes"), schemaNumberCompletor, nullCompletor}));
//		completors.add(new ArgumentCompletor(new Completor[] {new SimpleCompletor("resetSchemaLearner"), nullCompletor}));
//		completors.add(new ArgumentCompletor(new Completor[] {new SimpleCompletor("resolveComplexTypeInheritance"), schemaNumberCompletor, nullCompletor}));
//		completors.add(new ArgumentCompletor(new Completor[] {new SimpleCompletor("resolveElementInheritance"), schemaNumberCompletor, nullCompletor}));
//		completors.add(new ArgumentCompletor(new Completor[] {new SimpleCompletor("run"), fileNameCompletor } ));
//		completors.add(new ArgumentCompletor(new Completor[] {new SimpleCompletor("set"), settingNameCompletor, nullCompletor}));
//		completors.add(new SimpleCompletor("showPreferences"));
//		completors.add(new ArgumentCompletor(new Completor[] {new SimpleCompletor("unfoldCollection"), schemaNumberCompletor, nullCompletor}));
//		completors.add(new ArgumentCompletor(new Completor[] {new SimpleCompletor("union"), schemaNumberCompletor}));
//		completors.add(new ArgumentCompletor(new Completor[] {new SimpleCompletor("write"), schemaNumberCompletor, fileNameCompletor, nullCompletor}));

		consoleReader.setDefaultPrompt(DEFAULT_PROMPT);
		
		workingDirectory = System.getProperty("user.dir");
	}
	
	public void addCommand(Command command) {
		this.commands.put(command.getCommand(), command);
		command.registerConsole(this);
		this.completors.add(command.getCompletor());
	}
	
	public void work() {
		consoleReader.addCompletor(new MultiCompletor(completors));
		running = true;

		while (running) {
			try {
				String commandline = readInput();
				parseCommand(commandline);
				
				schemaCompletor.setCandidateStrings(this.schemas.keySet().toArray(new String[0]));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void parseCommand(String commandline) throws Exception {
		if (commandline.equals("")) 
			return; 
		String[] parameters = commandline.split(" ");
		parseCommand(parameters);
	}
	
	public void parseCommand(String[] parameters) throws Exception {
		try {
			Command command = commands.get(parameters[0]);
			if (command == null)
				System.out.println("No such command: " + parameters[0]);
			else
				command.parseCommand(parameters);
		} catch (ArrayIndexOutOfBoundsException e) {
//			e.printStackTrace();
			System.out.println("incomplete command");
		} catch (NoSuchSchemaException e) {
			System.out.println(e.getMessage());
		}
	}		



	public SchemaHandler getSchemaHandler(String schemaName) throws NoSuchSchemaException {
		SchemaHandler schemaHandler = schemas.get(schemaName);
		if (schemaHandler == null)
			throw new NoSuchSchemaException(schemaName);
		return schemaHandler;
	}
	
	public Completor getSchemaCompletor() {
		return this.schemaCompletor;
	}
	
	private String readInput() {
		try {
			return consoleReader.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println(SchemaLanguage.class.getCanonicalName());
		Console console = new Console();
		console.addCommand(console.new HelpCmd());
		console.addCommand(new WriteCmd());
		console.addCommand(new LoadCmd());
		console.addCommand(new PrintCmd());
		console.addCommand(new ConvertCmd());
		console.addCommand(new ExitCmd());
		console.addCommand(console.new ListCmd());
		console.work();
//		if (args.length == 0) {
//		} else {
//			console.cmdRun(args[0]);
//		}
	}

	public void addSchema(String schemaName, SchemaHandler schemaHandler) {
		this.schemas.put(schemaName, schemaHandler);
	}

	public Completor getFilenameCompletor() {
		return this.filenameCompletor ;
	}

	public Completor getLanguageCompletor() {
		return this.languageCompletor;
	}
}
