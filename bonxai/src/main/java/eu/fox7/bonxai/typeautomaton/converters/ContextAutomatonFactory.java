package eu.fox7.bonxai.typeautomaton.converters;

import gjb.flt.automata.FeatureNotSupportedException;
import gjb.flt.automata.factories.sparse.GlushkovFactory;
import gjb.flt.automata.impl.sparse.State;
import gjb.flt.automata.misc.StateRemapper;
import gjb.flt.regex.Regex;
import gjb.flt.regex.UnknownOperatorException;
import gjb.flt.regex.factories.RegexFactory;
import gjb.flt.treeautomata.impl.ContentAutomaton;
import gjb.flt.treeautomata.impl.ContextAutomaton;
import java.util.Map;
import java.util.Vector;
import java.util.Map.Entry;

import eu.fox7.bonxai.common.AllPattern;
import eu.fox7.bonxai.common.AnyPattern;
import eu.fox7.bonxai.common.ChoicePattern;
import eu.fox7.bonxai.common.CountingPattern;
import eu.fox7.bonxai.common.EmptyPattern;
import eu.fox7.bonxai.common.Particle;
import eu.fox7.bonxai.common.ParticleContainer;
import eu.fox7.bonxai.common.SequencePattern;
import eu.fox7.bonxai.typeautomaton.TypeAutomaton;
import eu.fox7.bonxai.typeautomaton.factories.XSDTypeAutomatonFactory;
import eu.fox7.bonxai.xsd.ComplexContentType;
import eu.fox7.bonxai.xsd.ComplexType;
import eu.fox7.bonxai.xsd.Content;
import eu.fox7.bonxai.xsd.Element;
import eu.fox7.bonxai.xsd.SimpleContentType;
import eu.fox7.bonxai.xsd.SimpleType;
import eu.fox7.bonxai.xsd.Type;
import eu.fox7.bonxai.xsd.XSDSchema;

public class ContextAutomatonFactory {
	private boolean addSimpleTypes = false; 
	
	public ContextAutomatonFactory() {
		
	}

	public ContextAutomatonFactory(boolean addSimpleTypes) {
		this.addSimpleTypes = addSimpleTypes;
	}

	public ContextAutomaton convertXSD(XSDSchema xsd) {
		XSDTypeAutomatonFactory factory = new XSDTypeAutomatonFactory(this.addSimpleTypes);
		TypeAutomaton typeAutomaton = factory.createTypeAutomaton(xsd);
		return this.convertTypeAutomaton(typeAutomaton);
	}

	public ContextAutomaton convertTypeAutomaton(TypeAutomaton typeAutomaton) {
		Map<State,State> stateMap = StateRemapper.stateRemapping(typeAutomaton);
		ContextAutomaton contextAutomaton = new ContextAutomaton(typeAutomaton,stateMap);
		
		for (Entry<State,State> entry: stateMap.entrySet()) {
			State origState = entry.getKey();
			State newState = entry.getValue();
			if (! typeAutomaton.isInitialState(origState)) {
				Type type = typeAutomaton.getType(origState).getReference();
				ContentAutomaton contentAutomaton = this.convertType(type);
				contextAutomaton.annotate(newState, contentAutomaton);
			}			
		}
		return contextAutomaton;
	}
	
	private ContentAutomaton convertType(Type type) {
		Regex regex = new RegexFactory().createEpsilon();
		if (type instanceof SimpleType) {
			// nothing todo here
		} else if (type instanceof ComplexType) {
			ComplexType complexType = (ComplexType) type;
			Content content = complexType.getContent();
			if (content instanceof SimpleContentType) {
				//nothing todo here	
			} else if (content instanceof ComplexContentType) {
				ComplexContentType complexContent = (ComplexContentType) content;
				Particle particle = complexContent.getParticle();
				regex = convertParticle(particle);
			} else if (content == null) {
				// nothing todo here
			} else {
				throw new RuntimeException("Unknown content type in complex type.");
			}
		}
		ContentAutomaton contentAutomaton = convertRegex(regex);
		return contentAutomaton;
	}

	private ContentAutomaton convertRegex(Regex regex) {
		GlushkovFactory factory = new GlushkovFactory();
		ContentAutomaton nfa = new ContentAutomaton();
		try {
			return (ContentAutomaton) factory.create(nfa,regex.getTree());
		} catch (UnknownOperatorException e) {
			throw new RuntimeException(e);
		} catch (FeatureNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}

	private Regex convertParticle(Particle particle) {
		RegexFactory factory = new RegexFactory();
		if (particle instanceof CountingPattern) {
			CountingPattern countingPattern = (CountingPattern) particle;
			Regex regex = this.convertParticle(countingPattern.getParticles().getFirst());
			return factory.createMultiplicity(regex, countingPattern.getMin(), (countingPattern.getMax()==null?RegexFactory.INFINITY:countingPattern.getMax()));
		} else if (particle instanceof ParticleContainer) {
			ParticleContainer container = (ParticleContainer) particle;
			Vector<Regex> childs = new Vector<Regex>();
			for (Particle childParticle: container.getParticles()) {
				childs.add(this.convertParticle(childParticle));
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
			String name = element.getName();
			return factory.createSymbol(name);
		} else if (particle instanceof EmptyPattern) {
			return factory.createEpsilon();
		} else if (particle instanceof AnyPattern) {
			return factory.createEpsilon();
		} else if (particle == null) {
			return factory.createEpsilon();
		} else {
			throw new RuntimeException("Unknown Particle");
		}
	}

}