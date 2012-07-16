package eu.fox7.jedit;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import org.gjt.sp.jedit.OperatingSystem;
import org.gjt.sp.jedit.buffer.JEditBuffer;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.textarea.TextArea;

import eu.fox7.jedit.textelement.TextElement;

public class MyMouseAdapter extends MouseAdapter {
	private TextArea textArea;
	private HighlightManager highlightManager;

	public MyMouseAdapter(HighlightManager highlightManager,
			JEditTextArea textArea) {
		this.textArea = textArea;
		this.highlightManager = highlightManager;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		boolean control = (OperatingSystem.isMacOS() && e.isMetaDown())
		  || (!OperatingSystem.isMacOS() && e.isControlDown());
		
		if (control) {		
			int offset = textArea.xyToOffset(e.getX(), e.getY());
			JEditBuffer buffer = textArea.getBuffer();

			TextElement textElement = highlightManager.getClickTarget(buffer, offset);
			if (textElement!=null)
				textElement.fireAction();
		}
	}


}
