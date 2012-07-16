package eu.fox7.jedit.textelement;

import java.awt.Color;

import org.gjt.sp.jedit.buffer.JEditBuffer;

import eu.fox7.jedit.Highlight;
import eu.fox7.jedit.HighlightManager;
import eu.fox7.jedit.JEditBonxaiManager;
import eu.fox7.schematoolkit.xsd.om.Type;

public class XSDType extends AbstractXMLElement {
	public XSDType(JEditBuffer buffer, Type type) {
		super(buffer, type);
	}

	@Override
	public void fireAction(JEditBonxaiManager bonxaiManager,
			HighlightManager highlightManager) {
		highlightManager.removeHighlight(Highlight.XMLELEMENT);
		highlightManager.highlightLinks(this, Linktype.TYPE2ELEMENT, Color.YELLOW, Highlight.XMLELEMENT);
		highlightManager.addHighlight(this, Color.YELLOW, Highlight.XMLELEMENT);
	}

	
}
