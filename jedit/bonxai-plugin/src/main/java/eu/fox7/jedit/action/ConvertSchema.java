package eu.fox7.jedit.action;

import java.io.IOException;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.io.VFSFile;

import eu.fox7.jedit.BonXaiPlugin;
import eu.fox7.jedit.FoxlibAction;
import eu.fox7.schematoolkit.SchemaConverter;
import eu.fox7.schematoolkit.SchemaHandler;
import eu.fox7.schematoolkit.SchemaLanguage;
import eu.fox7.schematoolkit.SchemaToolkitException;
import eu.fox7.schematoolkit.exceptions.ConversionFailedException;

public class ConvertSchema extends FoxlibAction {
	private static final String[] ACTIONS = new String[] {"convertSchema"};
	
	@Override
	public String[] getActions() {
		return ACTIONS;
	}

	@Override
	public void handleBufferAction(View view, String actionName) {
		BonXaiPlugin.getBonxaiManager().convertSchema(view.getBuffer(), view);
	}
}
