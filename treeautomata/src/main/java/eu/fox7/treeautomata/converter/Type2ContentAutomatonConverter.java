package eu.fox7.treeautomata.converter;


import java.util.Vector;

import eu.fox7.flt.automata.FeatureNotSupportedException;
import eu.fox7.flt.automata.factories.sparse.GlushkovFactory;
import eu.fox7.flt.regex.Regex;
import eu.fox7.flt.regex.UnknownOperatorException;
import eu.fox7.flt.regex.factories.RegexFactory;
import eu.fox7.flt.treeautomata.impl.ContentAutomaton;
import eu.fox7.schematoolkit.common.AllPattern;
import eu.fox7.schematoolkit.common.AnyPattern;
import eu.fox7.schematoolkit.common.ChoicePattern;
import eu.fox7.schematoolkit.common.CountingPattern;
import eu.fox7.schematoolkit.common.EmptyPattern;
import eu.fox7.schematoolkit.common.GroupReference;
import eu.fox7.schematoolkit.common.Particle;
import eu.fox7.schematoolkit.common.ParticleContainer;
import eu.fox7.schematoolkit.common.SequencePattern;
import eu.fox7.schematoolkit.xsd.om.ComplexContentType;
import eu.fox7.schematoolkit.xsd.om.ComplexType;
import eu.fox7.schematoolkit.xsd.om.Content;
import eu.fox7.schematoolkit.xsd.om.Element;
import eu.fox7.schematoolkit.xsd.om.SimpleContentType;
import eu.fox7.schematoolkit.xsd.om.SimpleType;
import eu.fox7.schematoolkit.xsd.om.Type;

public class Type2ContentAutomatonConverter {
	public ContentAutomaton convertType(Type type) {
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
			Regex regex = this.convertParticle(countingPattern.getParticle());
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
			String name = element.getName().getFullyQualifiedName();
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
			throw new RuntimeException("Unknown Particle");
		}
	}

}
