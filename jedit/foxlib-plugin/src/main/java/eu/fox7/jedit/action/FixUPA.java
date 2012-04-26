package eu.fox7.jedit.action;

import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.buffer.JEditBuffer;
import org.gjt.sp.jedit.io.VFSFile;
import eu.fox7.jedit.FoxlibAction;
import eu.fox7.schematoolkit.SchemaHandler;
import eu.fox7.schematoolkit.SchemaToolkitException;
import eu.fox7.schematoolkit.UPAFixer;
import eu.fox7.upafixer.impl.BKWUPAFixer;

public class FixUPA extends FoxlibAction {
	private static String ACTIONNAME = "fixupa";
	
	@Override
	public String[] getActions() {
		return new String[]{ACTIONNAME};
	}

	@Override
	public void handleBrowserAction(View view, VFSFile[] files,
			String actionName) {
		
	}

	@Override
	public void handleBufferAction(View view, String actionName) {
		SchemaHandler handler = getSchemaHandler(view);
		UPAFixer upaFixer = new BKWUPAFixer();
		try {
			upaFixer.fixUPA(handler.getSchema());
		} catch (SchemaToolkitException e) {
			throw new RuntimeException(e);
		}
		writeSchema(view, handler);
	}

	private void writeSchema(View view, SchemaHandler handler) {
		try {
			JEditBuffer buffer = view.getBuffer();
			// USE AWT-Thread?
			buffer.remove(0, buffer.getLength());
			buffer.insert(0, handler.getSchemaString());
			buffer.setStringProperty("eu.fox7.schemalanguage", handler.getSchemaLanguage().name());
			view.getTextArea().goToBufferStart(false);
			view.getBuffer().setMode();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		}
	}

	
}
