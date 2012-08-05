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
		return (childPattern.getElementPattern() == null)?new HashSet<Symbol>():getSymbols(childPattern.getElementPattern().getRegexp(), bonxai);
	}
	
	private static Set<Symbol> getSymbols(Particle particle, Bonxai bonxai) {
		Set<Symbol> symbols = new HashSet<Symbol>();
		if (particle instanceof Element) {
			Element element = (Element) particle;
			if (element.getType()==null) {
				Symbol symbol = Symbol.create(element.getName().getFullyQualifiedName());
				symbols.add(symbol);
			}
		} else if (particle instanceof CountingPattern) {
			symbols = getSymbols(((CountingPattern) particle).getParticle(), bonxai);
		} else if (particle instanceof ParticleContainer) {
			ParticleContainer particleContainer = (ParticleContainer) particle;
			for (Particle childParticle: particleContainer.getParticles()) {
				symbols.addAll(getSymbols(childParticle, bonxai));
			}
		} else if (particle instanceof AnyPattern) {
			//nothing to do here. 
		} else if (particle instanceof GroupReference) {
			GroupReference groupRef = (GroupReference) particle;
			QualifiedName groupName = groupRef.getName();
			BonxaiGroup group = bonxai.getElementGroup(groupName);
			symbols = getSymbols(group.getParticle(), bonxai);
		} // just ignore other Particles. We just need the child symbols here. An error may be thrown during the actual conversion .
		return symbols;
	}


}
