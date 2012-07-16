package eu.fox7.jedit.action;

import java.awt.Color;
import java.util.Collection;

import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.buffer.JEditBuffer;

import eu.fox7.flt.automata.impl.sparse.State;
import eu.fox7.jedit.BonXaiPlugin;
import eu.fox7.jedit.FoxlibAction;
import eu.fox7.jedit.Highlight;
import eu.fox7.jedit.HighlightManager;
import eu.fox7.jedit.JEditBonxaiManager;
import eu.fox7.jedit.textelement.BonxaiExpression;
import eu.fox7.jedit.textelement.Linktype;
import eu.fox7.jedit.textelement.XMLElement;
import eu.fox7.jedit.textelement.XSDType;
import eu.fox7.schematoolkit.SchemaToolkitException;
import eu.fox7.schematoolkit.xmlvalidator.AbstractXMLValidator;
import eu.fox7.schematoolkit.xmlvalidator.XMLParser;

public class ValidateXML extends FoxlibAction {
	private static final String[] ACTIONS = new String[]{"validate"};
	
	@Override
	public String[] getActions() {
		return ACTIONS;
	}
	
	@Override
	public void handleBufferAction(View view, String actionName) {
		this.validateBuffer(view.getBuffer());
	}

		
		
	public void validateBuffer(JEditBuffer buffer) {
		JEditBonxaiManager bonxaiManager = BonXaiPlugin.getBonxaiManager();
		HighlightManager highlightManager = BonXaiPlugin.getHighlightManager();
		AbstractXMLValidator validator = bonxaiManager.getXMLValidator();
		try {
			validator.validate(buffer.getText());
			Collection<XMLParser.ParsedElement> elements = validator.getElements();
			highlightManager.removeBuffer(buffer);
			bonxaiManager.addXMLBuffer(buffer);
			for (XMLParser.ParsedElement element: elements) {
				System.err.println(element.toString());
				XMLElement xmlElement = new XMLElement(buffer, element);
				highlightManager.addTextElement(xmlElement);
				Color color = null;
				int key=0;
				switch (element.getStatus()) {
				case ERROR: color = Color.RED; key = Highlight.XMLERROR; break;
				case INVALID: color = Color.ORANGE; key = Highlight.XMLERROR; break;
				case UNCONSTRAINED: color = Color.BLUE; key = Highlight.XMLERROR; break;
				case EXTERNAL: color = Color.GREEN; key = Highlight.XMLERROR; break;
				}
				
				if (color != null)
					highlightManager.addHighlight(xmlElement, color, key);
				
				State vertical = element.getVertical();
				BonxaiExpression expression = bonxaiManager.getExpression(vertical);
				if (expression!=null) {
					highlightManager.addLink(expression, xmlElement, Linktype.EXPRESSION2ELEMENT);
					highlightManager.addLink(xmlElement, expression, Linktype.ELEMENT2EXPRESSION);
				}
				XSDType type = bonxaiManager.getType(vertical);
				if (type!=null) {
					highlightManager.addLink(type, xmlElement, Linktype.TYPE2ELEMENT);
					highlightManager.addLink(xmlElement, type, Linktype.ELEMENT2TYPE);
				}
			}
		} catch (SchemaToolkitException e) {
			throw new RuntimeException(e);
		}
	}
}
