package eu.fox7.bonxai.typeautomaton.operations;

import java.util.Collection;
import java.util.LinkedList;

import eu.fox7.bonxai.typeautomaton.TypeAutomaton;
import eu.fox7.flt.automata.impl.sparse.Symbol;
import eu.fox7.schematoolkit.common.CountingPattern;
import eu.fox7.schematoolkit.common.ElementRef;
import eu.fox7.schematoolkit.common.GroupReference;
import eu.fox7.schematoolkit.common.Particle;
import eu.fox7.schematoolkit.common.ParticleContainer;
import eu.fox7.schematoolkit.common.QualifiedName;
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
