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

package eu.fox7.schematoolkit.typeautomaton.factories;

import java.util.HashSet;
import java.util.Set;

import eu.fox7.schematoolkit.bonxai.om.AncestorPattern;
import eu.fox7.schematoolkit.bonxai.om.AncestorPatternElement;
import eu.fox7.schematoolkit.bonxai.om.DoubleSlashPrefixedContainer;
import eu.fox7.schematoolkit.bonxai.om.ElementPattern;
import eu.fox7.schematoolkit.bonxai.om.Expression;
import eu.fox7.schematoolkit.bonxai.om.SequenceExpression;
import eu.fox7.schematoolkit.common.Namespace;
import eu.fox7.schematoolkit.common.QualifiedName;

public class AdvancedTypeNameGenerator implements TypeNameGenerator {
	private Set<String> usedNames = new HashSet<String>();
	private Namespace namespace;
	
	
	@Override
	public QualifiedName generateTypeName(Expression ex) {
		String chosenName = ex.getAnnotation("typename");
		if (chosenName == null)
			chosenName = chooseName(ex.getAncestorPattern());
		
		String typename = chosenName;
		int i=1;
		while (usedNames.contains(typename))
			typename = chosenName + (++i);
		
		usedNames.add(typename);
		return new QualifiedName(namespace, typename);
	}
	
	private String chooseName(AncestorPattern ancestorPattern) {
		if (ancestorPattern instanceof AncestorPatternElement)
			return ((AncestorPatternElement) ancestorPattern).getName().getName();
		else if (ancestorPattern instanceof DoubleSlashPrefixedContainer)
			return chooseName(((DoubleSlashPrefixedContainer) ancestorPattern).getChild());
		else if (ancestorPattern instanceof SequenceExpression) {
			int size = ((SequenceExpression) ancestorPattern).getChildren().size();
			return chooseName(((SequenceExpression) ancestorPattern).getChildren().get(size-1));
		} else 
			return "Type";
	}

	public AdvancedTypeNameGenerator(Namespace targetNamespace) {
		this.namespace = targetNamespace;
	}

}
