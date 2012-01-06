package eu.fox7.bonxai.typeautomaton.factories;

import eu.fox7.schematoolkit.bonxai.om.AncestorPattern;
import eu.fox7.schematoolkit.common.QualifiedName;

public interface TypeNameGenerator {
	public QualifiedName generateTypeName(AncestorPattern ap);
}
