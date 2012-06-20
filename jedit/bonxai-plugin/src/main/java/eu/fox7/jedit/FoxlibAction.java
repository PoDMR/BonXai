package eu.fox7.jedit;

import java.io.File;
import java.io.IOException;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.buffer.JEditBuffer;
import org.gjt.sp.jedit.io.VFSFile;

import eu.fox7.schematoolkit.SchemaHandler;
import eu.fox7.schematoolkit.SchemaLanguage;
import eu.fox7.schematoolkit.SchemaToolkitException;

public abstract class FoxlibAction {
	public void handleBrowserAction(View view, VFSFile[] files, String actionName) {}
	public void handleBufferAction(View view, String actionName) {}
	
	public abstract String[] getActions();
	
	protected SchemaLanguage getSchemaLanguage(Buffer buffer) {
		SchemaLanguage schemaLanguage = null;
		String language = buffer.getStringProperty("eu.fox7.schemalanguage");
		if (language != null)
		  schemaLanguage = SchemaLanguage.valueOf(language);
		else {
			String path = buffer.getPath();
			schemaLanguage = getSchemaLanguageFromPath(path);
		}
		return schemaLanguage;
	}

	private SchemaLanguage getSchemaLanguageFromPath(String path) {
		String extension = path.substring(path.lastIndexOf('.')+1);
		SchemaLanguage schemaLanguage = null;
		if (extension.equals("bonxai")) schemaLanguage = SchemaLanguage.BONXAI; else
		if (extension.equals("xsd")) schemaLanguage = SchemaLanguage.XMLSCHEMA; else
		if (extension.equals("rng")) schemaLanguage = SchemaLanguage.RELAXNG; else
		if (extension.equals("xml") || extension.equals("dtd")) schemaLanguage = SchemaLanguage.DTD;
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
	
	protected void openSchemaInNewBuffer(SchemaHandler schemaHandler, View view) throws IOException, SchemaToolkitException {
		JEditBuffer buffer = jEdit.newFile(view);
		// USE AWT-Thread?
		buffer.insert(0, schemaHandler.getSchemaString());
		buffer.setStringProperty("eu.fox7.schemalanguage", schemaHandler.getSchemaLanguage().name());
	    view.getTextArea().goToBufferStart(false);
	    view.getBuffer().setMode();
	}
	
	protected SchemaHandler parseFile(VFSFile file) throws IOException, SchemaToolkitException {
		String path = file.getPath();
		SchemaLanguage schemaLanguage = this.getSchemaLanguageFromPath(path);
		SchemaHandler schemaHandler = schemaLanguage.getSchemaHandler();
		schemaHandler.loadSchema(new File(file.getPath()));
		return schemaHandler;
	}


	
}
