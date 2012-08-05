package eu.fox7.jedit.textelement;

import org.gjt.sp.jedit.buffer.JEditBuffer;

import eu.fox7.schematoolkit.bonxai.om.Locatable;

public class BonxaiElement extends AbstractBonxaiElement {

	public BonxaiElement(JEditBuffer buffer, Locatable element) {
		super(buffer, element.getLocation());
	}
}
