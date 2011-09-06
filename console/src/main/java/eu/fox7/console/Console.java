package eu.fox7.console;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import java.util.Collection;
import java.util.Vector;
import java.util.List;
import java.util.ArrayList;

import jline.ArgumentCompletor;
import jline.Completor;
import jline.ConsoleReader;
import jline.FileNameCompletor;
import jline.MultiCompletor;
import jline.NullCompletor;
import jline.SimpleCompletor;



import eu.fox7.bonxai.Schema;
import eu.fox7.bonxai.SchemaType;
import eu.fox7.bonxai.bonxai.Bonxai;
import eu.fox7.bonxai.converter.ConversionFailedException;
import eu.fox7.bonxai.tools.PreferencesManager;
import eu.fox7.bonxai.xsd.XSDSchema;
import eu.fox7.flt.schema.infer.ixsd.XsdLearner;
import eu.fox7.flt.treeautomata.impl.ContextAutomaton;
import eu.fox7.learning.impl.SchemaLearner;
import eu.fox7.treeautomata.converter.ContextAutomaton2XSDConverter;
import eu.fox7.treeautomata.converter.XSD2ContextAutomatonConverter;
import eu.fox7.upafixer.UPAFixer;
import eu.fox7.upafixer.impl.BKWUPAFixer;


public class Console {

	private ConsoleReader consoleReader = null;
	private static final String DEFAULT_PROMPT = "> ";
	private boolean running;
	
	private String workingDirectory;
	
	private SimpleCompletor schemaNumberCompletor = null;
	private SimpleCompletor settingNameCompletor = null;
	
	private Vector<Schema> schemas;
	
	
	public Console() {
		try {
			consoleReader = new ConsoleReader();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		schemas = new Vector<Schema>();
				
		List<Completor> completors = new ArrayList<Completor>();
		
		FileNameCompletor fileNameCompletor = new FileNameCompletor();
		NullCompletor nullCompletor = new NullCompletor();
		schemaNumberCompletor = new SimpleCompletor(new String[]{});
		settingNameCompletor = new SimpleCompletor(PreferencesManager.getPreferencesManager().getSettingNames());
		
		completors.add(new ArgumentCompletor(new Completor[] {new SimpleCompletor("addXML"), fileNameCompletor } ));
		completors.add(new ArgumentCompletor(new Completor[] {new SimpleCompletor("convert"), schemaNumberCompletor, new SimpleCompletor(new String[]{"DTD", "XSD", "RelaxNG", "BonXai"}), nullCompletor}));
		completors.add(new ArgumentCompletor(new Completor[] {new SimpleCompletor("difference"), schemaNumberCompletor, schemaNumberCompletor, nullCompletor}));
		completors.add(new SimpleCompletor("exit"));
		completors.add(new ArgumentCompletor(new Completor[] {new SimpleCompletor("fixEDC"), schemaNumberCompletor, nullCompletor}));
		completors.add(new ArgumentCompletor(new Completor[] {new SimpleCompletor("fixUPA"), schemaNumberCompletor, nullCompletor}));
		completors.add(new SimpleCompletor("help"));
		completors.add(new ArgumentCompletor(new Completor[] {new SimpleCompletor("intersect"), schemaNumberCompletor}));
		completors.add(new ArgumentCompletor(new Completor[] { new SimpleCompletor("load"), fileNameCompletor } ));
		completors.add(new ArgumentCompletor(new Completor[] { new SimpleCompletor("lernXSD"), nullCompletor } ));
		completors.add(new ArgumentCompletor(new Completor[] { new SimpleCompletor("learnPreProcessed"), fileNameCompletor, new SimpleCompletor(new String[]{"1", "2", "3", "4", "5"}), nullCompletor } ));
		completors.add(new SimpleCompletor("list"));
		completors.add(new ArgumentCompletor(new Completor[] {new SimpleCompletor("print"), schemaNumberCompletor, nullCompletor}));
		completors.add(new ArgumentCompletor(new Completor[] {new SimpleCompletor("printContextAutomaton"), schemaNumberCompletor, nullCompletor}));
		completors.add(new ArgumentCompletor(new Completor[] {new SimpleCompletor("printRootElements"), schemaNumberCompletor, nullCompletor}));
		completors.add(new ArgumentCompletor(new Completor[] {new SimpleCompletor("remove"), schemaNumberCompletor, nullCompletor}));
		completors.add(new ArgumentCompletor(new Completor[] {new SimpleCompletor("removeUnreachableTypes"), schemaNumberCompletor, nullCompletor}));
		completors.add(new ArgumentCompletor(new Completor[] {new SimpleCompletor("removeEmptyTypes"), schemaNumberCompletor, nullCompletor}));
		completors.add(new ArgumentCompletor(new Completor[] {new SimpleCompletor("resetSchemaLearner"), nullCompletor}));
		completors.add(new ArgumentCompletor(new Completor[] {new SimpleCompletor("resolveComplexTypeInheritance"), schemaNumberCompletor, nullCompletor}));
		completors.add(new ArgumentCompletor(new Completor[] {new SimpleCompletor("resolveElementInheritance"), schemaNumberCompletor, nullCompletor}));
		completors.add(new ArgumentCompletor(new Completor[] {new SimpleCompletor("run"), fileNameCompletor } ));
		completors.add(new ArgumentCompletor(new Completor[] {new SimpleCompletor("set"), settingNameCompletor, nullCompletor}));
		completors.add(new SimpleCompletor("showPreferences"));
		completors.add(new ArgumentCompletor(new Completor[] {new SimpleCompletor("unfoldCollection"), schemaNumberCompletor, nullCompletor}));
		completors.add(new ArgumentCompletor(new Completor[] {new SimpleCompletor("union"), schemaNumberCompletor}));
		completors.add(new ArgumentCompletor(new Completor[] {new SimpleCompletor("write"), schemaNumberCompletor, fileNameCompletor, nullCompletor}));
			
		consoleReader.setDefaultPrompt(DEFAULT_PROMPT);
		
		consoleReader.addCompletor(new MultiCompletor(completors));
		
		workingDirectory = System.getProperty("user.dir");
	}
	
	public void work() {
		running = true;

		while (running) {
			try {
				parseCommand(readInput());

				String[] numbers = new String[schemas.size()];
				for (int i=0; i<schemas.size(); ++i)
					numbers[i]=""+i;
			
				schemaNumberCompletor.setCandidateStrings(numbers);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private void parseCommand(String command) {
		String[] parameters = command.split(" ");
		
		try {
			if (command.equals("")) return;
		
			if (parameters[0].equals("help")) {
				cmdHelp();
			} else if (parameters[0].equals("exit")) {
				cmdExit();
			} else if (parameters[0].equals("showPreferences")) {
				cmdShowPreferences();
			} else if (parameters[0].equals("set")) {
				cmdSet(parameters[1],parameters[2]);
			} else if (parameters[0].equals("load")) {
				cmdLoad(parameters);
			} else if (parameters[0].equals("run")) {
				cmdRun(parameters[1]);
//			} else if (parameters[0].equals("cd")) {
//				cmdCD(parameters[1]);
//			} else if (parameters[0].equals("pwd")) {
//				cmdPWD();
			} else if (parameters[0].equals("resolveElementInheritance")) {
				cmdResolveElementInheritance(getSchema(parameters[1]));
			} else if (parameters[0].equals("resolveComplexTypeInheritance")) {
				cmdResolveComplexTypeInheritance(getSchema(parameters[1]));
			} else if (parameters[0].equals("unfoldCollection")) {
				cmdUnfoldCollection(getSchema(parameters[1]));
			} else if (parameters[0].equals("write")) {
				cmdWrite(getSchema(parameters[1]), parameters[2]);
			} else if (parameters[0].equals("list")) {
				cmdList();
			} else if (parameters[0].equals("print")) {
				cmdPrint(getSchema(parameters[1]));
			} else if (parameters[0].equals("remove")) {
				cmdRemove(parameters[1]);
			} else if (parameters[0].equals("convert")) {
				cmdConvert(getSchema(parameters[1]), parameters[2]);
			} else if (parameters[0].equals("union")) {
				cmdUnion(parameters);
			} else if (parameters[0].equals("intersect")) {
				cmdIntersect(parameters);
			} else if (parameters[0].equals("difference")) {
				cmdDifference(getSchema(parameters[1]), getSchema(parameters[2]));
			} else if (parameters[0].equals("fixEDC")) {
				cmdFixEDC(getSchema(parameters[1]));
			} else if (parameters[0].equals("fixUPA")) {
				cmdFixUPA(getSchema(parameters[1]));
			} else if (parameters[0].equals("removeUnreachableTypes")) {
				cmdRemoveUnreachableTypes(getSchema(parameters[1]));
			} else if (parameters[0].equals("removeEmptyTypes")) {
				cmdRemoveEmptyTypes(getSchema(parameters[1]));
			} else if (parameters[0].equals("learnPreProcessed")) {
				cmdLearnPreProcessed(parameters[1],Integer.parseInt(parameters[2]));
			} else if (parameters[0].equals("printContextAutomaton")) {
				cmdPrintContextAutomaton(getSchema(parameters[1]));
			} else if (parameters[0].equals("learnXSD")) {
				cmdLearnXSD();
			} else if (parameters[0].equals("addXML")) {
				cmdAddXML(parameters[1]);
			} else if (parameters[0].equals("resetSchemaLearner")) {
				cmdResetSchemaLearner();
			} else {
				System.out.println("unknown command");
			}
		} catch (ArrayIndexOutOfBoundsException e) {
//			e.printStackTrace();
			System.out.println("incomplete command");
		} catch (NoSuchSchemaException e) {
			System.out.println(e.getMessage());
		}
		
	}
	
	private void cmdResetSchemaLearner() {
		this.schemaLearner.clear();
	}

	private SchemaLearner schemaLearner = new SchemaLearner();
	
	
	private void cmdAddXML(String filename) {
		try {
			this.schemaLearner.addXML(new File(filename));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void cmdLearnXSD() {
		this.schemas.add(this.schemaLearner.learnXSD());
	}

	private void cmdPrintContextAutomaton(Schema schema) {
		XSD2ContextAutomatonConverter factory = new XSD2ContextAutomatonConverter(true); 
		ContextAutomaton contextAutomaton = factory.convertXSD(schema.getXSD());
		System.out.println(contextAutomaton);
	}

	private void cmdLearnPreProcessed(String filename, int contextSize) {
		try {
			File file = new File(filename);
			XsdLearner xsdLearner = new XsdLearner();
			ContextAutomaton contextAutomaton = xsdLearner.learn(file, contextSize);
			ContextAutomaton2XSDConverter converter = new ContextAutomaton2XSDConverter();
			XSDSchema xsdSchema = converter.convert(contextAutomaton);
			Schema schema = new Schema(xsdSchema);
			schema.setComment("Learned from "+filename);
			schemas.add(schema);
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	private void cmdFixEDC(Schema schema) {
		try {
			Schema newSchema = schema.fixEDC(workingDirectory);
			schemas.add(newSchema);
		} catch (ConversionFailedException e) {
			e.printStackTrace();
		}
	}
	
	private void cmdFixUPA(Schema schema) {
		UPAFixer upaFixer = new BKWUPAFixer();
		upaFixer.fixUPA(schema.getXSD());
	}
	
	private void cmdRemoveUnreachableTypes(Schema schema) {
		try {
			schema.removeUnreachableTypes();
		} catch (ConversionFailedException e) {
			e.printStackTrace();
		}
	}

	private void cmdRemoveEmptyTypes(Schema schema) {
		try {
			schema.removeEmptyTypes();
		} catch (ConversionFailedException e) {
			e.printStackTrace();
		}
	}

	private void cmdHelp() {
		System.out.println("Just try TAB for now :)");
	}
	
	private void cmdExit() {
		running = false;
	}
	
	private void cmdRun(String filename) {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
			String cmd = reader.readLine();
			while (cmd != null) {
				parseCommand(cmd);
				cmd = reader.readLine();
			}
		} catch (FileNotFoundException e) {
			System.out.println("The file " + filename + " does not exist.");
		} catch (IOException e) {
			System.out.println("Error reading commands.");
			e.printStackTrace();
		}
	}
	
	private void cmdLoad(String[] parameters) {
		for (int i=1; i<parameters.length; ++i) {
			File file = new File(parameters[i]);
			Schema schema = new Schema();
			try {
				schema.loadSchema(file);
			} catch (ConversionFailedException e) {
				e.printStackTrace();
			}
			if (schema.getType() != SchemaType.NONE) {
				schemas.add(schema);
			}
		}
	}
	
	private void cmdUnfoldCollection(Schema schema) {
		Collection<Schema> schemas = schema.getCollection();
		this.schemas.addAll(schemas);
	}

	private void cmdResolveElementInheritance(Schema schema) {
		try {
			schema.resolveElementInheritance();
		} catch (ConversionFailedException e) {
			e.printStackTrace();
		}
	}

	private void cmdResolveComplexTypeInheritance(Schema schema) {
		try {
			Schema newSchema = schema.resolveComplexTypeInheritance(workingDirectory);
			schemas.add(newSchema);
		} catch (ConversionFailedException e) {
			e.printStackTrace();
		}
	}

	private void cmdWrite(Schema schema, String filename) {
		File file = new File(filename);
		try {
			schema.writeSchema(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void cmdRemove(String schemaNo) throws NoSuchSchemaException {
		int schemaNumber=0;
		
		try {
			schemaNumber = Integer.parseInt(schemaNo);
			schemas.remove(schemaNumber);
		} catch (IndexOutOfBoundsException e) {
			throw new NoSuchSchemaException("There is no schmema " + schemaNumber);
		} catch (NumberFormatException e) {
			throw new NoSuchSchemaException("Not a (schema-)number: " + schemaNo);
		}
	}
	
	private void cmdShowPreferences() {
		PreferencesManager.getPreferencesManager().showSettings(System.out);
	}
	
	private void cmdSet(String name, String value) {
		PreferencesManager.getPreferencesManager().setSetting(name, value);
	}

//	private void cmdPWD() {
//		System.out.println(workingDirectory);
//	}
//	
//	private void cmdCD(String dir) {
//		File newDir = new File(dir).getAbsoluteFile();
//		if (newDir.isDirectory()) {
//			workingDirectory = newDir.getPath();
//		} else {
//			System.out.println("Not a directory: " + newDir.getPath());
//		}
//	}

	private void cmdList() {
		for (int i=0; i<schemas.size(); i++) {
			Schema schema = schemas.get(i);
			System.out.println("" + i + ": " + schema.getType() + " " + schema.getComment());
		}
	}
	
	private void cmdPrint(Schema schema) {
		System.out.println(schema.getSchemaString());
	}
	
	private void cmdConvert(Schema schema, String targetType) {
		try {
			targetType = targetType.toLowerCase();
			Schema newSchema = new Schema();

			if (targetType.equals("xsd")) {
				newSchema = schema.convert(SchemaType.XSD);
			} else if (targetType.equals("bonxai")) {
				newSchema = schema.convert(SchemaType.BONXAI);
			} else if (targetType.equals("relaxng")) {
				newSchema = schema.convert(SchemaType.RELAXNG);
			} else if (targetType.equals("dtd")) {
				newSchema = schema.convert(SchemaType.DTD);
			}

			if (newSchema.getType() == SchemaType.NONE) {
				System.out.println("Conversion failed!!!");
			} else {
				schemas.add(newSchema);
			}
		} catch (ConversionFailedException e) {
			System.out.println("ConversionFailed !");
			e.printStackTrace();
		}
	}
	
	private void cmdUnion(String[] parameters) throws NoSuchSchemaException {
		Vector<Schema> schemas = new Vector<Schema>(parameters.length - 1);
		for (int i=1; i<parameters.length; ++i) {
			schemas.add(getSchema(parameters[i]));
		}
	
		try {
			Schema newSchema = Schema.union(schemas, workingDirectory);
			this.schemas.add(newSchema);
		} catch (ConversionFailedException e) {
			System.out.println("ConversionFailed !");
			e.printStackTrace();
		}
	}

	private void cmdIntersect(String[] parameters) throws NoSuchSchemaException {
		Vector<Schema> schemas = new Vector<Schema>(parameters.length - 1);
		for (int i=1; i<parameters.length; ++i) {
			schemas.add(getSchema(parameters[i]));
		}

		try {
			Schema newSchema = Schema.intersect(schemas, workingDirectory);
			this.schemas.add(newSchema);
		} catch (ConversionFailedException e) {
			System.out.println("ConversionFailed !");
			e.printStackTrace();
		}
	}
	
	private void cmdDifference(Schema schema1, Schema schema2) {
		try {
			Schema newSchema = schema1.substract(schema2, workingDirectory);
			schemas.add(newSchema);
		} catch (ConversionFailedException e) {
			System.out.println("ConversionFailed !");
			e.printStackTrace();
		}
	}

	private Schema getSchema(String schemaNo) throws NoSuchSchemaException {
		int schemaNumber=0;
		try {
			schemaNumber = Integer.parseInt(schemaNo);
			return schemas.get(schemaNumber);
		} catch (IndexOutOfBoundsException e) {
			throw new NoSuchSchemaException("There is no schmema " + schemaNumber);
		} catch (NumberFormatException e) {
			throw new NoSuchSchemaException("Not a (schema-)number: " + schemaNo);
		}
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
		Console console = new Console();
		if (args.length == 0) {
			console.work();
		} else {
			console.cmdRun(args[0]);
		}
	}
}
