package eu.fox7.treeautomata.converter;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.collections15.BidiMap;
import org.apache.commons.collections15.bidimap.DualHashBidiMap;

import eu.fox7.flt.automata.FeatureNotSupportedException;
import eu.fox7.flt.automata.factories.sparse.GlushkovFactory;
import eu.fox7.flt.automata.impl.sparse.SparseNFA;
import eu.fox7.flt.automata.impl.sparse.State;
import eu.fox7.flt.automata.impl.sparse.StateNFA;
import eu.fox7.flt.automata.impl.sparse.Transition;
import eu.fox7.flt.regex.Glushkov;
import eu.fox7.flt.regex.Regex;
import eu.fox7.flt.regex.UnknownOperatorException;
import eu.fox7.flt.regex.factories.RegexFactory;
import eu.fox7.flt.treeautomata.impl.ContentAutomaton;
import eu.fox7.schematoolkit.common.AllPattern;
import eu.fox7.schematoolkit.common.AnyPattern;
import eu.fox7.schematoolkit.common.ChoicePattern;
import eu.fox7.schematoolkit.common.CountingPattern;
import eu.fox7.schematoolkit.common.Element;
import eu.fox7.schematoolkit.common.ElementRef;
import eu.fox7.schematoolkit.common.EmptyPattern;
import eu.fox7.schematoolkit.common.GroupReference;
import eu.fox7.schematoolkit.common.Particle;
import eu.fox7.schematoolkit.common.ParticleContainer;
import eu.fox7.schematoolkit.common.SequencePattern;

public class Particle2ContentAutomatonConverter {
	private int count;
	private Map<String,Particle> stringElementMap;
	private BidiMap<Particle,State> elementStateMap;
	
	private boolean namespaceAware = true;
	
	public void setNamespaceAware(boolean namespaceAware) {
		this.namespaceAware = namespaceAware;
	}
	
	public SparseNFA convertParticle(Particle particle) {
		count = 0;
		this.stringElementMap = new HashMap<String,Particle>();
		this.elementStateMap = new DualHashBidiMap<Particle,State>();
		Regex regex = convertParticleRecursive(particle);
		ContentAutomaton contentAutomaton = convertRegex(regex);
		for(Entry<String,Particle> entry: stringElementMap.entrySet())
			this.elementStateMap.put(entry.getValue(), contentAutomaton.getState(entry.getKey()));
		return contentAutomaton;
	}
	
	public boolean verifyParticle(Particle particle, StateNFA oldContentAutomaton) {
		boolean correct = true;
		count = 0;
		this.stringElementMap = new HashMap<String,Particle>();
		this.elementStateMap = new DualHashBidiMap<Particle,State>();
		Regex regex = convertParticleRecursive(particle);
		ContentAutomaton newContentAutomaton = convertRegex(regex);
		
		for (State oldState: oldContentAutomaton.getStates()) {
			String stateName = oldContentAutomaton.getStateValue(oldState);
			State newState = newContentAutomaton.getState(stateName);
			if (newState == null)
				correct = false;
			else {
				Particle element = this.stringElementMap.get(stateName);
				this.elementStateMap.put(element, oldState);
				for (Transition transition: oldContentAutomaton.getOutgoingTransitions(oldState)) {
					String targetState = oldContentAutomaton.getStateValue(transition.getToState());
					Set<String> targetStates = newContentAutomaton.getNextStateValues(transition.getSymbol().toString(), stateName);
					if (! targetStates.contains(targetState))
						correct = false;
				}
				if (oldContentAutomaton.getNumberOfChoicesFrom(oldState) != newContentAutomaton.getNumberOfChoicesFrom(newState))
					correct = false;
			}
		}
		return correct;
	}
	

	private ContentAutomaton convertRegex(Regex regex) {
		GlushkovFactory factory = new GlushkovFactory();
		ContentAutomaton nfa = new ContentAutomaton();
		try {
			return (ContentAutomaton) factory.create(nfa,regex.getTree(), true);
		} catch (UnknownOperatorException e) {
			throw new RuntimeException(e);
		} catch (FeatureNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @return the elementStateMap
	 */
	public BidiMap<Particle, State> getElementStateMap() {
		return elementStateMap;
	}

	private Regex convertParticleRecursive(Particle particle) {
		RegexFactory factory = new RegexFactory();
		if (particle instanceof CountingPattern) {
			CountingPattern countingPattern = (CountingPattern) particle;
			Regex regex = this.convertParticleRecursive(countingPattern.getParticle());
			return factory.createMultiplicity(regex, countingPattern.getMin(), (countingPattern.getMax()==null?RegexFactory.INFINITY:countingPattern.getMax()));
		} else if (particle instanceof ParticleContainer) {
			ParticleContainer container = (ParticleContainer) particle;
			Vector<Regex> childs = new Vector<Regex>();
			for (Particle childParticle: container.getParticles()) {
				childs.add(this.convertParticleRecursive(childParticle));
			}
			
			if (particle instanceof SequencePattern) {
				return factory.createConcatenation(childs.toArray(new Regex[0]));
			} else if (particle instanceof ChoicePattern) {
				return factory.createUnion(childs.toArray(new Regex[0]));
			} else if (particle instanceof AllPattern) {
				return factory.createInterleave(childs.toArray(new Regex[0]));
			}else {
				throw new RuntimeException("Unknown particle container");
			}
		} else if (particle instanceof Element) {
			Element element = (Element) particle;
			String name = element.getName().getFullyQualifiedName() + Glushkov.MARK_SEP + (count++);
			stringElementMap.put(name, element);
			return factory.createSymbol(name);
		} else if (particle instanceof ElementRef) {
			ElementRef elementRef = (ElementRef) particle;
			String name = elementRef.getElementName().getFullyQualifiedName() + Glushkov.MARK_SEP + (count++);
			stringElementMap.put(name, elementRef);
			return factory.createSymbol(name);
		} else if (particle instanceof EmptyPattern) {
			return factory.createEpsilon();
		} else if (particle instanceof AnyPattern) {
			return factory.createEpsilon();
		} else if (particle == null) {
			return factory.createEpsilon();
		} else if (particle instanceof GroupReference) {
			throw new RuntimeException("Groups are not supported yet.");
		} else {
			throw new RuntimeException("Unknown Particle of class " + particle.getClass().getCanonicalName());
		}
	}
}
