package eu.fox7.jedit.textelement;

import org.gjt.sp.jedit.buffer.JEditBuffer;

import eu.fox7.schematoolkit.xmlvalidator.XMLParser;

public class XMLElement extends AbstractXMLElement {
	
	public XMLElement(JEditBuffer buffer, XMLParser.ParsedElement element) {
		super(buffer, element);
		this.setTooltip(element.getErrorString());
	}
}
