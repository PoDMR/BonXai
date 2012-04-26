package eu.fox7.schematoolkit.typeautomaton.operations;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import eu.fox7.schematoolkit.common.Namespace;
import eu.fox7.schematoolkit.common.QualifiedName;

public class SimpleTypenameGenerator implements TypenameGenerator {
	Set<QualifiedName> names = new HashSet<QualifiedName>(); 

	@Override
	public QualifiedName generateTypename(String namespace, Collection<QualifiedName> names) {
		QualifiedName qname;
		if ((names==null) || (names.size()==0) || (names.iterator().next()==null))
				qname = new QualifiedName(namespace, "type");
		else
			qname = names.iterator().next();
		String name = qname.getName();
		int i = 1;
		while (this.names.contains(qname)) {
			qname = new QualifiedName(namespace, name + i);
			++i;
		}
		this.names.add(qname);
		return qname;
	}

}
