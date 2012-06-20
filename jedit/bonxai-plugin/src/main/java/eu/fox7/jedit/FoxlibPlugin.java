package eu.fox7.jedit;

import java.util.HashMap;
import java.util.Map;

import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.io.VFSFile;

import eu.fox7.jedit.action.ConvertSchema;
import eu.fox7.jedit.action.FixUPA;
import eu.fox7.jedit.action.Learner;

public class FoxlibPlugin extends EditPlugin {
	private Map<String, FoxlibAction> actions = new HashMap<String, FoxlibAction>();
	
	public void handleBrowserAction(View view, VFSFile[] files, String actionName) {
		FoxlibAction action = this.actions.get(actionName);
		if (action != null)
			action.handleBrowserAction(view, files, actionName);
	}

	public void handleBufferAction(View view, String actionName) {
		FoxlibAction action = this.actions.get(actionName);
		if (action != null)
			action.handleBufferAction(view, actionName);
	}
	
	public void addAction(FoxlibAction action) {
		for (String actionName: action.getActions())
			this.actions.put(actionName, action);
	}
	
	public void start() {
		this.addAction(new FixUPA());
		this.addAction(new ConvertSchema());
		this.addAction(new Learner());
	}
}
