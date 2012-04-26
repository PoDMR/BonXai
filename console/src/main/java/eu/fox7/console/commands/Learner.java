package eu.fox7.console.commands;

import java.io.File;

import jline.ArgumentCompletor;
import jline.Completor;
import jline.MultiCompletor;
import jline.SimpleCompletor;
import eu.fox7.console.Command;
import eu.fox7.console.CommandCollection;
import eu.fox7.schematoolkit.Schema;
import eu.fox7.schematoolkit.SchemaLearner;

public class Learner extends CommandCollection {
	private SchemaLearner schemaLearner;
		
	private static final String LEARNXSD = "learnXSD";
	private static final String LEARNBONXAI = "learnBonxai";
	private static final String CLEAR = "clearLearner";
	private static final String ADDXML = "addXML";

	private static final String DEFAULT_SCHEMA_LEARNER = "eu.fox7.learning.impl.SchemaLearner";
	
	@SuppressWarnings("unchecked")
	public Learner() {
		try {
			Class<SchemaLearner> slc = (Class<SchemaLearner>) this.getClass().getClassLoader().loadClass(DEFAULT_SCHEMA_LEARNER); 
			schemaLearner = slc.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public String[] getCommands() {
		return new String[]{LEARNXSD, LEARNBONXAI, ADDXML, CLEAR};
	}

	@Override
	public Completor getCompletor() {
		SimpleCompletor sc = new SimpleCompletor(new String[]{LEARNXSD, LEARNBONXAI, ADDXML});
		ArgumentCompletor ac = new ArgumentCompletor(new Completor[]{new SimpleCompletor(ADDXML), this.filenameCompletor()});
		return new MultiCompletor(new Completor[]{sc, ac});
	}

	@Override
	public void parseCommand(String[] parameters) throws Exception {
		Schema schema = null;
		
		if (parameters[0].equals(ADDXML))
			for (int i=1; i<parameters.length; i++)
				schemaLearner.addXML(new File(parameters[i]));
		else if (parameters[0].equals(CLEAR))
			schemaLearner.clear();
		else if (parameters[0].equals(LEARNBONXAI))
			schema = schemaLearner.learnBonxai();
		else if (parameters[0].equals(LEARNXSD))
			schema = schemaLearner.learnXSD();
		
		
		if (parameters[0].equals(LEARNBONXAI) || parameters[0].equals(LEARNXSD)) {
			String schemaName;
			if (parameters.length>1)
				schemaName = parameters[1];
			else
				schemaName = this.getNewSchemaName();
			this.console.addSchema(schemaName, schema.getSchemaHandler());
		}
	}

}
