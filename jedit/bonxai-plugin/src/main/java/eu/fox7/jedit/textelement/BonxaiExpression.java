package eu.fox7.jedit.textelement;

import org.gjt.sp.jedit.buffer.JEditBuffer;

import eu.fox7.schematoolkit.bonxai.om.Expression;

public class BonxaiExpression extends AbstractBonxaiElement {
	public BonxaiExpression(JEditBuffer buffer, Expression expression) {
		super(buffer, expression.getAncestorPatternLocation());
    }
}
