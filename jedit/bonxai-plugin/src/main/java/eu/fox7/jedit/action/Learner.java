package eu.fox7.jedit.action;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;

import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.io.VFSFile;

import eu.fox7.jedit.FoxlibAction;
import eu.fox7.schematoolkit.Schema;
import eu.fox7.schematoolkit.SchemaLearner;
import eu.fox7.schematoolkit.SchemaToolkitException;

public class Learner extends FoxlibAction {
	private static final String DEFAULT_SCHEMA_LEARNER = "eu.fox7.learning.impl.SchemaLearner";
	private static final String ADDXML = "addXML";
	private static final String LEARN_XMLSCHEMA = "learnXMLSCHEMA";
	private static final String LEARN_BONXAI = "learnBONXAI";
	private static final String CLEAR = "clearLearner";
	private static final String[] ACTIONS = new String[]{ADDXML, LEARN_XMLSCHEMA, LEARN_BONXAI, CLEAR};
	
	private SchemaLearner schemaLearner;
	
	@SuppressWarnings("unchecked")
	public Learner() {
		try {
			Class<SchemaLearner> learnerClass = (Class<SchemaLearner>) this.getClass().getClassLoader().loadClass(DEFAULT_SCHEMA_LEARNER);
			schemaLearner = learnerClass.newInstance();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public String[] getActions() {
		return (schemaLearner==null)?new String[]{}:ACTIONS;
	}

	/* (non-Javadoc)
	 * @see eu.fox7.jedit.FoxlibAction#handleBrowserAction(org.gjt.sp.jedit.View, org.gjt.sp.jedit.io.VFSFile[], java.lang.String)
	 */
	@Override
	public void handleBrowserAction(View view, VFSFile[] files,
			String actionName) {
		try {
			System.err.println("BrowserAction: " + actionName + " Files: " + files);
			for (VFSFile file: files) {
				System.err.println("Adding XML file: " + file.getPath());
				schemaLearner.addXML(new File(file.getPath()));
			}
			if (actionName.equals(ADDXML)) {
			} else if (actionName.equals(LEARN_XMLSCHEMA)) {
				Schema schema = schemaLearner.learnXSD();
				schemaLearner.clear();
				this.openSchemaInNewBuffer(schema.getSchemaHandler(), view);
			} else if (actionName.equals(LEARN_BONXAI)) {
				Schema schema = schemaLearner.learnBonxai();
				schemaLearner.clear();
				this.openSchemaInNewBuffer(schema.getSchemaHandler(), view);
			} else 
				throw new RuntimeException("Unsupported action: "+ actionName);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (SchemaToolkitException e) {
			throw new RuntimeException(e);
		}
	}

	/* (non-Javadoc)
	 * @see eu.fox7.jedit.FoxlibAction#handleBufferAction(org.gjt.sp.jedit.View, java.lang.String)
	 */
	@Override
	public void handleBufferAction(View view, String actionName) {
		System.err.println("BufferAction: " + actionName);
		try {
			if (actionName.equals(ADDXML)) {
				schemaLearner.addXML(new StringReader(view.getTextArea().getText()));
			} else if (actionName.equals(CLEAR)) {
				schemaLearner.clear();
			} else if (actionName.equals(LEARN_XMLSCHEMA)) {
				Schema schema = schemaLearner.learnXSD();
				this.openSchemaInNewBuffer(schema.getSchemaHandler(), view);
			} else if (actionName.equals(LEARN_BONXAI)) {
				Schema schema = schemaLearner.learnBonxai();
				this.openSchemaInNewBuffer(schema.getSchemaHandler(), view);
			} else
				throw new RuntimeException("Unsupported action: "+ actionName);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (SchemaToolkitException e) {
			throw new RuntimeException(e);
		}
	}
	
	

}
