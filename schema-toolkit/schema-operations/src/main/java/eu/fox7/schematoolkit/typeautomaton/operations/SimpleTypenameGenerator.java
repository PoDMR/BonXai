/**
 * Copyright 2009-2012 TU Dortmund
 *
 * This file is part of FoXLib.
 *
 * FoXLib is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FoXLib is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with BonXai.  If not, see <http://www.gnu.org/licenses/>.
 */

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
