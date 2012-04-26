package eu.fox7.schematoolkit.typeautomaton.operations;

import java.util.Collection;

import eu.fox7.schematoolkit.common.QualifiedName;

public interface TypenameGenerator {
	public QualifiedName generateTypename(String namespace, Collection<QualifiedName> names);
}
