package eu.fox7.treeautomata.converter;

import org.apache.commons.collections15.BidiMap;

import eu.fox7.flt.automata.impl.sparse.State;
import eu.fox7.flt.automata.impl.sparse.StateNFA;
import eu.fox7.schematoolkit.bonxai.om.ChildPattern;
import eu.fox7.schematoolkit.bonxai.om.ElementPattern;
import eu.fox7.schematoolkit.common.EmptyPattern;
import eu.fox7.schematoolkit.common.Particle;
import eu.fox7.schematoolkit.common.Element;
import eu.fox7.schematoolkit.common.QualifiedName;

public class ChildPattern2ContentAutomatonConverter {
	private Particle2ContentAutomatonConverter particleConverter = new Particle2ContentAutomatonConverter();
	
	public void setGroupResolver(Particle2ContentAutomatonConverter.GroupResolver groupResolver) {
		this.particleConverter.setGroupResolver(groupResolver);
	}
	
	public StateNFA convertChildPattern(ChildPattern childPattern) {
		StateNFA contentAutomaton = null;
		Particle particle = null;
		ElementPattern elementPattern = childPattern.getElementPattern();
		if (elementPattern!=null)
			particle = elementPattern.getRegexp();
		if (particle == null)
			particle = new EmptyPattern();
		contentAutomaton = particleConverter.convertParticle(particle);
		return contentAutomaton;
	}
	
	public BidiMap<Particle,State> getElementStateMap() {
		return particleConverter.getElementStateMap();
	}

	public boolean verifyChildPattern(ChildPattern childPattern, StateNFA contentAutomaton) {
		Particle particle = null;
		ElementPattern elementPattern = childPattern.getElementPattern();
		if (elementPattern!=null)
			particle = elementPattern.getRegexp();
		if (particle == null)
			particle = new EmptyPattern();
		return particleConverter.verifyParticle(particle, contentAutomaton);
	}
}
