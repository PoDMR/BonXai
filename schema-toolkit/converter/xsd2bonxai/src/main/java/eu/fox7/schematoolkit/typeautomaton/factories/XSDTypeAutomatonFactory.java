package eu.fox7.schematoolkit.typeautomaton.factories;


import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import eu.fox7.bonxai.typeautomaton.AnnotatedNFATypeAutomaton;
import eu.fox7.bonxai.typeautomaton.TypeAutomaton;
import eu.fox7.flt.automata.impl.sparse.State;
import eu.fox7.flt.automata.impl.sparse.Symbol;
import eu.fox7.schematoolkit.common.CountingPattern;
import eu.fox7.schematoolkit.common.GroupReference;
import eu.fox7.schematoolkit.common.Particle;
import eu.fox7.schematoolkit.common.ParticleContainer;
import eu.fox7.schematoolkit.common.QualifiedName;
import eu.fox7.schematoolkit.xsd.om.ComplexContentType;
import eu.fox7.schematoolkit.xsd.om.ComplexType;
import eu.fox7.schematoolkit.xsd.om.Content;
import eu.fox7.schematoolkit.xsd.om.Element;
import eu.fox7.schematoolkit.xsd.om.Group;
import eu.fox7.schematoolkit.xsd.om.Type;
import eu.fox7.schematoolkit.xsd.om.XSDSchema;

public class XSDTypeAutomatonFactory {
    /**
     * Should states for simple types be added?
     */
    private boolean addSimpleTypes;
    
    /**
     * TypeAutomaton
     */
    private TypeAutomaton typeAutomaton;
    
    /** 
     * Maps types to states of the type automaton
     */
    private Map<QualifiedName, State> typeMap;
    
	private Queue<State> workingQueue;
	
	private XSDSchema schema;

	public XSDTypeAutomatonFactory(boolean addSimpleTypes) {
		this.addSimpleTypes = addSimpleTypes;
	}

	
	public XSDTypeAutomatonFactory() {
		this.addSimpleTypes = false;
	}

	public TypeAutomaton createTypeAutomaton(XSDSchema schema) {
    	this.typeAutomaton = new AnnotatedNFATypeAutomaton();
    	this.typeMap = new HashMap<QualifiedName,State>();
    	this.workingQueue = new LinkedList<State>();
    	this.schema = schema;
    	
    	State initial = new State();
    	typeAutomaton.addState(initial);
    	typeAutomaton.setInitialState(initial);
    	
    	Collection<Element> rootElements =  schema.getElements();
    	
    	for (Element rootElement: rootElements) {
    		addChild(initial, rootElement);
    	}
    	
		while (! workingQueue.isEmpty()) {
			State state = workingQueue.remove();
			Type type = typeAutomaton.getType(state);
			Set<Element> childs = getChilds(type);
			for (Element child: childs) {
				addChild(state, child);
			}
		}
		return typeAutomaton;
    }
	

    private Set<Element> getChilds(Type type) {
		if (type instanceof ComplexType) {
			ComplexType complexType = (ComplexType) type;
			Content content = complexType.getContent();
			if (content instanceof ComplexContentType) {
				ComplexContentType complexContentType = (ComplexContentType) content;
				Particle particle = complexContentType.getParticle();
				return getChilds(particle);
			}
		}
		return new HashSet<Element>();
	}

	private Set<Element> getChilds(Particle particle) {
		Set<Element> childs = new HashSet<Element>();
		if (particle instanceof Element) {
			Element element = (Element) particle;
			Type type = schema.getType(element.getTypeName());
			if (this.addSimpleTypes || (type instanceof ComplexType)) {
				childs.add((Element) particle);				
			}
		} else if (particle instanceof ParticleContainer) {
			ParticleContainer particleContainer = (ParticleContainer) particle;
			for (Particle childParticle: particleContainer.getParticles()) {
				childs.addAll(getChilds(childParticle));
			}
		} else if (particle instanceof CountingPattern) {
			CountingPattern countingPattern = (CountingPattern) particle;
			childs.addAll(getChilds(countingPattern.getParticle()));
		} else if (particle instanceof GroupReference) {
			GroupReference groupReference = (GroupReference) particle;
			Group group = schema.getGroup(groupReference);
			childs.addAll(getChilds(group.getParticle()));
		} else
			throw new RuntimeException("Unkown Particle of class " + particle.getClass());
		return childs;
	}

	private void addChild(State state, Element child) {
    	String name = child.getName().getFullyQualifiedName();
		Symbol symbol = Symbol.create(name);
    	typeAutomaton.addSymbol(symbol);
    	QualifiedName typename = child.getTypeName();
    	Type type = schema.getType(typename);
    	State newState;
    	
    	if (typeMap.containsKey(typename)) {
    		newState = typeMap.get(typename);
    	} else {
    		newState = new State();
    		typeAutomaton.addState(newState);
    		if (type!= null) 
    			typeAutomaton.setType(newState, type);
    		else
    			typeAutomaton.setTypeName(newState, typename);
    		typeAutomaton.setElementProperties(newState, child.getProperties());
    		typeMap.put(typename, newState);
    		workingQueue.add(newState);
    	}
    	
    	typeAutomaton.addTransition(symbol, state, newState);
    }
}
