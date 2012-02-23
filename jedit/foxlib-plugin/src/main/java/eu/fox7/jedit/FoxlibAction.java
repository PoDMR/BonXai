package eu.fox7.jedit;

import java.io.IOException;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.io.VFSFile;

import eu.fox7.schematoolkit.SchemaHandler;
import eu.fox7.schematoolkit.SchemaLanguage;
import eu.fox7.schematoolkit.SchemaToolkitException;

public abstract class FoxlibAction {
	public void handleBrowserAction(View view, VFSFile[] files, String actionName) {}
	public void handleBufferAction(View view, String actionName) {}
	
	public abstract String getActionName();
	
	protected SchemaLanguage getSchemaLanguage(Buffer buffer) {
		SchemaLanguage schemaLanguage = null;
		String language = buffer.getStringProperty("eu.fox7.schemalanguage");
		if (language != null)
		  schemaLanguage = SchemaLanguage.valueOf(language);
		else {
			String path = buffer.getPath();
			String extension = path.substring(path.indexOf('.')+1);
			if (extension.equals("bonxai")) schemaLanguage = SchemaLanguage.BONXAI; else
			if (extension.equals("xsd")) schemaLanguage = SchemaLanguage.XMLSCHEMA; else
			if (extension.equals("rng")) schemaLanguage = SchemaLanguage.RELAXNG; else
			if (extension.equals("xml") || extension.equals("dtd")) schemaLanguage = SchemaLanguage.DTD;
		}
		return schemaLanguage;
	}

	protected SchemaHandler getSchemaHandler(View view) {
		Buffer buffer = view.getBuffer();
		SchemaHandler handler = getSchemaLanguage(buffer).getSchemaHandler();
		try {
			handler.parseSchema(view.getTextArea().getText());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return handler;
	}
	
}
