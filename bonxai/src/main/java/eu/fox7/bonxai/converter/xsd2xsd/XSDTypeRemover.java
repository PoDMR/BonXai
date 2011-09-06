package eu.fox7.bonxai.converter.xsd2xsd;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import eu.fox7.bonxai.common.AllPattern;
import eu.fox7.bonxai.common.AnyPattern;
import eu.fox7.bonxai.common.ChoicePattern;
import eu.fox7.bonxai.common.CountingPattern;
import eu.fox7.bonxai.common.GroupRef;
import eu.fox7.bonxai.common.Particle;
import eu.fox7.bonxai.common.SequencePattern;
import eu.fox7.bonxai.common.SymbolTableFoundation;
import eu.fox7.bonxai.common.SymbolTableRef;
import eu.fox7.bonxai.xsd.ComplexContentType;
import eu.fox7.bonxai.xsd.ComplexType;
import eu.fox7.bonxai.xsd.Element;
import eu.fox7.bonxai.xsd.ElementRef;
import eu.fox7.bonxai.xsd.SimpleContentType;
import eu.fox7.bonxai.xsd.SimpleType;
import eu.fox7.bonxai.xsd.Type;
import eu.fox7.bonxai.xsd.XSDSchema;

public class XSDTypeRemover {

	private class EmptyPattern extends Particle {}
	
	public void removeTypes(XSDSchema schema, Set<Type> types) {
		SymbolTableFoundation<Type> typeSymbolTable = schema.getTypeSymbolTable();
//		Set<String> typenames = typeSymbolTable.getAllKeys();
		Set<SymbolTableRef<Type>> typeRefs = new HashSet<SymbolTableRef<Type>>();
		for (SymbolTableRef<Type> typeRef: typeSymbolTable.getReferences()) {
//			SymbolTableRef<Type> typeRef = typeSymbolTable.getReference(typename);
			Type type = typeRef.getReference();
			if (types.contains(type))
				typeRefs.add(typeRef);
			else
				removeTypes(type, types);
		}
		SymbolTableFoundation<Element> elementSymbolTable = schema.getElementSymbolTable();
		for (SymbolTableRef<Element> elementRef: elementSymbolTable.getReferences())
			schema.removeElement(elementRef);
		
		for (SymbolTableRef<Type> typeRef: typeRefs)
			schema.removeType(typeRef);

//		schema.getGroups();
//		schema.getGroupSymbolTable();
	}
	
	private void removeTypes(Type type, Set<Type> types) {
		if (type instanceof SimpleType)
			removeTypes((SimpleType) type, types);
		else if (type instanceof ComplexType)
			removeTypes((ComplexType) type, types);
		else
			throw new RuntimeException("Unkown type");
	}
	
	private void removeTypes(SimpleType type, Set<Type> types) {
	}
	
	private void removeTypes(ComplexType type, Set<Type> types) {
		if ((type.getContent() == null) || (type.getContent() instanceof SimpleContentType)) {
			return;
		} else {
			ComplexContentType cct = (ComplexContentType) type.getContent();
			Particle particle = cct.getParticle();
			Particle newParticle = removeTypes(particle, types);
			cct.setParticle(newParticle);
		}
	}

	private Particle removeTypes(Particle particle, Set<Type> types) {
		if (particle instanceof Element)
			return removeTypes((Element) particle, types);
		else if (particle instanceof ElementRef)
			return removeTypes((ElementRef) particle, types);
		else if (particle instanceof AnyPattern)
			return removeTypes((AnyPattern) particle, types);
		else if (particle instanceof AllPattern)
			return removeTypes((AllPattern) particle, types);
		else if (particle instanceof CountingPattern)
			return removeTypes((CountingPattern) particle, types);
		else if (particle instanceof ChoicePattern)
			return removeTypes((ChoicePattern) particle, types);
		else if (particle instanceof SequencePattern)
			return removeTypes((SequencePattern) particle, types);
		else if (particle instanceof GroupRef)
			return removeTypes((GroupRef) particle, types);
		else
			throw new RuntimeException("Unknown particle");
	}
	
	private Particle removeTypes(Element element, Set<Type> types) {
		if (types.contains(element.getType()))
			return null;
		else
			return element; 
	}
	
	private Particle removeTypes(ElementRef elementRef, Set<Type> types) {
		if (types.contains(elementRef.getElement().getType())) 
			return null;
		else
			return elementRef;
	}

	private Particle removeTypes(AnyPattern anyPattern, Set<Type> types) {
		return anyPattern;
	}
	
	private Particle removeTypes(AllPattern allPattern, Set<Type> types) {
		boolean epsilon = false;
		boolean empty = true;
		LinkedList<Particle> particles = allPattern.getParticles();
		for (Iterator<Particle> it = particles.iterator(); it.hasNext();) {
			Particle child = it.next();
			Particle newChild = removeTypes(child, types);
			if (newChild instanceof EmptyPattern) {
				epsilon = true;
				it.remove();
			} else if (newChild == null) {
				it.remove();
			} else {
				empty = false;
			}
		}
		
		allPattern.setParticles(particles);
		
		if (empty)
			if (epsilon)
				return new EmptyPattern();
			else 
				return null;
		else
			return allPattern;
	}

	private Particle removeTypes(CountingPattern countingPattern, Set<Type> types) {
		Particle newChild = removeTypes(countingPattern.getParticles().getFirst(), types);
		if ((newChild instanceof EmptyPattern) || (newChild == null && countingPattern.getMin()==0)) {
			return new EmptyPattern();
		} else if (newChild == null) {
			return null;
		} else {
			return countingPattern;
		}
	}

	private Particle removeTypes(ChoicePattern choicePattern, Set<Type> types) {
		boolean epsilon = false;
		boolean empty = true;
		LinkedList<Particle> particles = choicePattern.getParticles();
		for (ListIterator<Particle> it = particles.listIterator(); it.hasNext();) {
			Particle child = it.next();
			Particle newChild = removeTypes(child, types);
			if (newChild instanceof EmptyPattern) {
				epsilon = true;
				it.remove();
			} else if (newChild == null) {
				it.remove();
			} else {
				empty = false;
				it.set(newChild);
			}
		}
		
		choicePattern.setParticles(particles);
		
		if (empty)
			if (epsilon)
				return new EmptyPattern();
			else 
				return null;
		else
			return choicePattern;
	}

	private Particle removeTypes(SequencePattern sequencePattern, Set<Type> types) {
		boolean epsilon = true;
		
		LinkedList<Particle> particles = sequencePattern.getParticles();
		for (ListIterator<Particle> it = particles.listIterator(); it.hasNext();) {
			Particle child = it.next();
			Particle newChild = removeTypes(child, types);
			if (newChild instanceof EmptyPattern) {
				it.remove();
			} else if (newChild == null) {
				return null;
			} else {
				epsilon = false;
				it.set(newChild);
			}
		}
		
		sequencePattern.setParticles(particles);
		
		if (epsilon)
			return new EmptyPattern();
		else
			return sequencePattern;
	}
	
	private Particle removeTypes(GroupRef groupRef, Set<Type> types) {
		groupRef.getGroup().getParticleContainer();
		return null;
	}
}
