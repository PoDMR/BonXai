package eu.fox7.jedit.textelement;

import java.awt.Color;
import java.util.Collection;
import java.util.LinkedList;

import org.gjt.sp.jedit.buffer.JEditBuffer;

import eu.fox7.jedit.HighlightManager;
import eu.fox7.jedit.JEditBonxaiManager;
import eu.fox7.jedit.Highlight;
import eu.fox7.jedit.Location;
import eu.fox7.schematoolkit.bonxai.om.Expression;

public class BonxaiExpression extends AbstractBonxaiElement {
	public BonxaiExpression(JEditBuffer buffer, Expression expression) {
		super(buffer, expression.getAncestorPatternLocation());
    }
	
	@Override
	public void fireAction(JEditBonxaiManager bonxaiManager, HighlightManager highlightManager) {
		highlightManager.removeHighlight(Highlight.XMLELEMENT);
		highlightManager.removeHighlight(Highlight.XSDTYPE);
		highlightManager.highlightLinks(this, Linktype.EXPRESSION2TYPE, null, Highlight.XSDTYPE);
		highlightManager.highlightLinks(this, Linktype.EXPRESSION2ELEMENT, Color.YELLOW, Highlight.XMLELEMENT);
		highlightManager.addHighlight(this, Color.YELLOW, Highlight.XMLELEMENT);
	}
}
