package eu.fox7.jedit;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.EditBus.EBHandler;
import org.gjt.sp.jedit.buffer.BufferListener;
import org.gjt.sp.jedit.buffer.JEditBuffer;
import org.gjt.sp.jedit.io.VFSFile;
import org.gjt.sp.jedit.msg.BufferUpdate;
import org.gjt.sp.jedit.msg.EditPaneUpdate;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.textarea.TextAreaPainter;
import org.gjt.sp.jedit.visitors.JEditVisitorAdapter;

import eu.fox7.jedit.action.ConvertSchema;
import eu.fox7.jedit.action.DebugPrinter;
import eu.fox7.jedit.action.Learner;
import eu.fox7.jedit.action.RegisterSchema;
import eu.fox7.jedit.action.ValidateXML;
import eu.fox7.schematoolkit.xsd.XSDSchemaHandler;
import gatchan.highlight.color.FlexColorPainter;

public class BonXaiPlugin extends EditPlugin {
	private int layer = TextAreaPainter.HIGHEST_LAYER;
	private static final JEditBonxaiManager bonxaiManager = new JEditBonxaiManager();
	private static final HighlightManager highlightManager = new HighlightManager();
	private Map<String, FoxlibAction> actions = new HashMap<String, FoxlibAction>();

	public static JEditBonxaiManager getBonxaiManager() {
		return bonxaiManager;
	}

	public void handleBrowserAction(View view, VFSFile[] files, String actionName) {
		FoxlibAction action = this.actions.get(actionName);
		if (action != null)
			action.handleBrowserAction(view, files, actionName);
		else 
			throw new RuntimeException("No such action: " + actionName);
	}

	public void handleBufferAction(View view, String actionName) {
		FoxlibAction action = this.actions.get(actionName);
		if (action != null)
			action.handleBufferAction(view, actionName);
		else 
			throw new RuntimeException("No such action: " + actionName);
	}
	
	public void addAction(FoxlibAction action) {
		for (String actionName: action.getActions())
			this.actions.put(actionName, action);
	}
	
	public void parseBuffer(JEditBuffer buffer) {
	}
	
	
	//{{{ start() method
	/**
	 * Initialize the plugin. When starting this plugin will add an Highlighter on each text area
	 */
	@Override
	public void start() {
		XSDSchemaHandler.useSaxParser(true);

		this.addAction(new ConvertSchema());
		this.addAction(new RegisterSchema());
		this.addAction(new ValidateXML());
		this.addAction(new Learner());
		this.addAction(new DebugPrinter());
		jEdit.visit(new TextAreaInitializer());
//		jEdit.visit(new ViewInitializer());
		EditBus.addToBus(this);
	} //}}}

	//{{{ stop() method
	/**
	 * uninitialize the plugin. we will remove the Highlighter on each text area
	 */
	@Override
	public void stop()
	{
		EditBus.removeFromBus(this);
		jEdit.resetProperty("plugin.gatchan.highlight.HighlightPlugin.activate");

//		Buffer[] buffers = jEdit.getBuffers();
//		for (int i = 0; i < buffers.length; i++)
//		{
//			buffers[i].unsetProperty(Highlight.HIGHLIGHTS_BUFFER_PROPS);
//		}

		jEdit.visit(new TextAreaUninitializer());
//		jEdit.visit(new ViewUninitializer());
	} //}}}

	
	@EBHandler
	public void handleBufferUpdate(BufferUpdate bufferUpdate) {
		Buffer buffer = bufferUpdate.getBuffer();
		Object what = bufferUpdate.getWhat();
		
		if (what == BufferUpdate.LOADED) {
			new RegisterSchema().registerSchema(buffer);
			if (buffer.getPath().endsWith(".xml")) {
				try {
					new ValidateXML().validateBuffer(buffer);
				} catch (Exception e) {
				}
			}
			highlightManager.addBuffer(buffer);
		} else if (what == BufferUpdate.CLOSING) {
			bonxaiManager.removeBuffer(buffer);
			highlightManager.removeBuffer(buffer);
		}
	}
	
	//{{{ handleEditPaneMessage() method
	@EBHandler
	public void handleEditPaneUpdate(EditPaneUpdate editPaneUpdate)
	{
		JEditTextArea textArea = editPaneUpdate.getEditPane().getTextArea();
		Object what = editPaneUpdate.getWhat();

		if (what == EditPaneUpdate.CREATED)
		{
			initTextArea(textArea);
		}
		else if (what == EditPaneUpdate.DESTROYED)
		{
			uninitTextArea(textArea);
		}
	} //}}}
	
	//{{{ uninitTextArea() method
	/**
	 * Remove the highlighter from a text area.
	 *
	 * @param textArea the textarea from wich we will remove the highlighter
	 * @see #stop()
	 * @see #handleEditPaneUpdate(org.gjt.sp.jedit.msg.EditPaneUpdate) 
	 */
	private static void uninitTextArea(JEditTextArea textArea)
	{
		highlightManager.removeTextArea(textArea);
		TextAreaPainter painter = textArea.getPainter();
		Highlighter highlighter = (Highlighter) textArea.getClientProperty(Highlighter.class);
		if (highlighter != null)
		{
			painter.removeExtension(highlighter);
			textArea.putClientProperty(Highlighter.class, null);
		}
		FlexColorPainter flexColorPainter = (FlexColorPainter) textArea.getClientProperty(FlexColorPainter.class);
		if (flexColorPainter != null)
		{
			painter.removeExtension(flexColorPainter);
			textArea.putClientProperty(FlexColorPainter.class, null);
		}
//		removeHighlightOverview(textArea);
//		textArea.removeCaretListener(highlightManager);
	} //}}}

	//{{{ initTextArea() method
	/**
	 * Initialize the textarea with a highlight painter.
	 *
	 * @param textArea the textarea to initialize
	 */
	private void initTextArea(JEditTextArea textArea)
	{
		Highlighter highlighter = new Highlighter(textArea);
		TextAreaPainter painter = textArea.getPainter();
		painter.addExtension(layer, highlighter);
		textArea.putClientProperty(Highlighter.class, highlighter);
//		textArea.addCaretListener(highlightManager);
		FlexColorPainter flexColorPainter = new FlexColorPainter(textArea);
		textArea.putClientProperty(FlexColorPainter.class, flexColorPainter);
		painter.addExtension(layer-1, flexColorPainter);
		MyMouseAdapter mouseAdapter = new MyMouseAdapter(highlightManager, textArea);
		textArea.getPainter().addMouseListener(mouseAdapter);
		highlightManager.addTextArea(textArea);

		textArea.revalidate();
	} //}}}
	
	//{{{ TextAreaInitializer class
	private class TextAreaInitializer extends JEditVisitorAdapter
	{
		public void visit(JEditTextArea textArea)
		{
			initTextArea(textArea);
		}
	} //}}}

	//{{{ TextAreaUninitializer class
	private class TextAreaUninitializer extends JEditVisitorAdapter
	{
		public void visit(JEditTextArea textArea)
		{
			uninitTextArea(textArea);
		}
	} //}}}

	public static HighlightManager getHighlightManager() {
		return highlightManager;
	}
	
}
