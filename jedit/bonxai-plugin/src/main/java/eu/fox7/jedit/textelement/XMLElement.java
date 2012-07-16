package eu.fox7.jedit.textelement;

import java.awt.Color;

import org.gjt.sp.jedit.buffer.JEditBuffer;

import eu.fox7.jedit.Highlight;
import eu.fox7.jedit.HighlightManager;
import eu.fox7.jedit.JEditBonxaiManager;
import eu.fox7.schematoolkit.xmlvalidator.XMLParser;

public class XMLElement extends AbstractXMLElement {
	
	public XMLElement(JEditBuffer buffer, XMLParser.ParsedElement element) {
		super(buffer, element);
		this.setTooltip(element.getErrorString());
	}

	@Override
	public void fireAction(JEditBonxaiManager bonxaiManager, HighlightManager highlightManager) {
		highlightManager.removeHighlight(Highlight.XMLELEMENT);
		highlightManager.addHighlight(this, Color.YELLOW, Highlight.XMLELEMENT);
		highlightManager.highlightLinks(this, Linktype.ELEMENT2TYPE, Color.YELLOW, Highlight.XMLELEMENT);
		highlightManager.highlightLinks(this, Linktype.ELEMENT2EXPRESSION, Color.YELLOW, Highlight.XMLELEMENT);
	}
}
