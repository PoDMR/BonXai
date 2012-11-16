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
import java.util.LinkedList;

import eu.fox7.flt.automata.impl.sparse.Symbol;
import eu.fox7.schematoolkit.common.CountingPattern;
import eu.fox7.schematoolkit.common.ElementRef;
import eu.fox7.schematoolkit.common.GroupReference;
import eu.fox7.schematoolkit.common.Particle;
import eu.fox7.schematoolkit.common.ParticleContainer;
import eu.fox7.schematoolkit.common.QualifiedName;
import eu.fox7.schematoolkit.typeautomaton.TypeAutomaton;
import eu.fox7.schematoolkit.xsd.om.Element;

public abstract class Operation implements UnaryOperation, BinaryOperation {

	@Override
	public TypeAutomaton apply(TypeAutomaton left, TypeAutomaton right) {
		LinkedList<TypeAutomaton> list = new LinkedList<TypeAutomaton>();
		list.add(left);
		list.add(right);
		return this.apply(list);
	}

	@Override
	public TypeAutomaton apply(TypeAutomaton source) {
		LinkedList<TypeAutomaton> list = new LinkedList<TypeAutomaton>();
		list.add(source);
		return this.apply(list);
	}
	
	public abstract TypeAutomaton apply(Collection<TypeAutomaton> input);

	protected Collection<Symbol> getUsedSymbols(Particle particle) {
		Collection<Symbol> usedSymbols = new LinkedList<Symbol>();
		if (particle instanceof Element) {
			Element element = (Element) particle;
			usedSymbols.add(Symbol.create(element.getName().getFullyQualifiedName()));
		} else if (particle instanceof CountingPattern) {
			CountingPattern countingPattern = (CountingPattern) particle;
			usedSymbols = getUsedSymbols(countingPattern.getParticle());
		} else if (particle instanceof ParticleContainer) {
			ParticleContainer particleContainer = (ParticleContainer) particle;
			for (Particle childParticle: particleContainer.getParticles())
				usedSymbols.addAll(getUsedSymbols(childParticle));
		} else if (particle instanceof GroupReference) {
			GroupReference groupReference = (GroupReference) particle;
			QualifiedName groupName = groupReference.getName();
			throw new RuntimeException("GroupReference not supported.");
			//			particle = automaton.getGroup(groupName);
		} else if (particle instanceof ElementRef) {
			
		}

		return usedSymbols;
	}

}
