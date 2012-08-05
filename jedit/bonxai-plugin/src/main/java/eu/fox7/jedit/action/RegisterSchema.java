package eu.fox7.jedit.action;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.buffer.JEditBuffer;

import eu.fox7.jedit.BonXaiPlugin;
import eu.fox7.jedit.FoxlibAction;
import eu.fox7.jedit.HighlightManager;
import eu.fox7.jedit.JEditBonxaiManager;
import eu.fox7.jedit.textelement.BonxaiExpression;
import eu.fox7.jedit.textelement.XSDElement;
import eu.fox7.jedit.textelement.XSDType;
import eu.fox7.schematoolkit.SchemaHandler;
import eu.fox7.schematoolkit.SchemaLanguage;
import eu.fox7.schematoolkit.bonxai.om.Bonxai;
import eu.fox7.schematoolkit.bonxai.om.Expression;
import eu.fox7.schematoolkit.xsd.om.Element;
import eu.fox7.schematoolkit.xsd.om.Type;
import eu.fox7.schematoolkit.xsd.om.XSDSchema;

public class RegisterSchema extends FoxlibAction {
	private static final String[] ACTIONS = new String[]{"registerSchema"};
	
	@Override
	public String[] getActions() {
		return ACTIONS;
	}
	
	@Override
	public void handleBufferAction(View view, String actionName) {
		this.registerSchema(view.getBuffer());
	}
	
	public void registerSchema(Buffer buffer) {
		JEditBonxaiManager bonxaiManager = BonXaiPlugin.getBonxaiManager();
		HighlightManager highlightManager = BonXaiPlugin.getHighlightManager();
		SchemaHandler schemaHandler = this.getSchemaHandler(buffer);
		if (schemaHandler==null)
			return;
		if (schemaHandler.getSchemaLanguage().equals(SchemaLanguage.BONXAI)) {
			Bonxai schema = (Bonxai) schemaHandler.getSchema();
			bonxaiManager.addSchema(schema, buffer);
		} else if (schemaHandler.getSchemaLanguage().equals(SchemaLanguage.XMLSCHEMA)) {
			XSDSchema schema = (XSDSchema) schemaHandler.getSchema();
			bonxaiManager.addSchema(schema, buffer);
			
		}
		
	}


}
