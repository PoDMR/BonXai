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

import eu.fox7.flt.automata.impl.sparse.Symbol;
import eu.fox7.schematoolkit.bonxai.om.Bonxai;
import eu.fox7.schematoolkit.bonxai.om.BonxaiGroup;
import eu.fox7.schematoolkit.bonxai.om.ChildPattern;
import eu.fox7.schematoolkit.bonxai.om.Element;
import eu.fox7.schematoolkit.common.AnyPattern;
import eu.fox7.schematoolkit.common.CountingPattern;
import eu.fox7.schematoolkit.common.GroupReference;
import eu.fox7.schematoolkit.common.Particle;
import eu.fox7.schematoolkit.common.ParticleContainer;
import eu.fox7.schematoolkit.common.QualifiedName;

public class ChildSymbolExtractor {
	public static Set<Symbol> getChildSymbols(ChildPattern childPattern, Bonxai bonxai) {
		return getChildSymbols(childPattern, bonxai, false);
	}

	public static Set<Symbol> getChildSymbolsWithBonxaiType(ChildPattern childPattern, Bonxai bonxai) {
		return getChildSymbols(childPattern, bonxai, false);
	}
	
	private static Set<Symbol> getChildSymbols(ChildPattern childPattern, Bonxai bonxai, boolean bonxaiTypes) {
		return (childPattern.getElementPattern() == null)?new HashSet<Symbol>():getSymbols(childPattern.getElementPattern().getRegexp(), bonxai, bonxaiTypes);
	}

	private static Set<Symbol> getSymbols(Particle particle, Bonxai bonxai, boolean bonxaiTypes) {
		Set<Symbol> symbols = new HashSet<Symbol>();
		if (particle instanceof Element) {
			Element element = (Element) particle;
			if ((element.getType()!=null) == bonxaiTypes) {
				Symbol symbol = Symbol.create(element.getName().getFullyQualifiedName());
				symbols.add(symbol);
			}
		} else if (particle instanceof CountingPattern) {
			symbols = getSymbols(((CountingPattern) particle).getParticle(), bonxai, bonxaiTypes);
		} else if (particle instanceof ParticleContainer) {
			ParticleContainer particleContainer = (ParticleContainer) particle;
			for (Particle childParticle: particleContainer.getParticles()) {
				symbols.addAll(getSymbols(childParticle, bonxai, bonxaiTypes));
			}
		} else if (particle instanceof AnyPattern) {
			//nothing to do here. 
		} else if (particle instanceof GroupReference) {
			GroupReference groupRef = (GroupReference) particle;
			QualifiedName groupName = groupRef.getName();
			BonxaiGroup group = bonxai.getElementGroup(groupName);
			symbols = getSymbols(group.getParticle(), bonxai, bonxaiTypes);
		} // just ignore other Particles. We just need the child symbols here. An error may be thrown during the actual conversion .
		return symbols;
	}


}
