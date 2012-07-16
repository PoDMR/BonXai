package eu.fox7.jedit.action;

import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.buffer.JEditBuffer;

import eu.fox7.jedit.BonXaiPlugin;
import eu.fox7.jedit.FoxlibAction;
import eu.fox7.jedit.HighlightManager;
import eu.fox7.jedit.JEditBonxaiManager;
import eu.fox7.schematoolkit.bonxai.BonxaiManager;

public class DebugPrinter extends FoxlibAction {
	private JEditBuffer debugBuffer;
	
	@Override
	public void handleBufferAction(View view, String actionName) {
		if (debugBuffer==null) {
			debugBuffer = jEdit.newFile(view);
		}
		JEditBonxaiManager bonxaiManager = BonXaiPlugin.getBonxaiManager();
		HighlightManager highlightManager = BonXaiPlugin.getHighlightManager();
		if (actionName.equals("printSchemaMap"))
			print(bonxaiManager.getSchemaMap());
		else if (actionName.equals("printHighlights"))
			print(highlightManager.getHighlights());
		else if (actionName.equals("printLinks"))
			print(highlightManager.getLinks());
//		else if (actionName.equals("printTextElements"))
			//print (highlightManager.getTextElements());
	}

	private static final String[] ACTIONS = new String[]{"printSchemaMap","printHighlights","printLinks","printTextElements"};
	
	@Override
	public String[] getActions() {
		return ACTIONS;
	}
	
	private void print(Object o) {
		int offset = debugBuffer.getLength();
		debugBuffer.insert(offset, o.toString());
	}
	
	

}
