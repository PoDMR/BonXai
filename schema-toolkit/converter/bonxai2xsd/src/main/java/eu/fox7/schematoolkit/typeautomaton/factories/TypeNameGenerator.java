package eu.fox7.schematoolkit.typeautomaton.factories;

import eu.fox7.schematoolkit.bonxai.om.Expression;
import eu.fox7.schematoolkit.common.QualifiedName;

public interface TypeNameGenerator {
	public QualifiedName generateTypeName(Expression ex);
}
