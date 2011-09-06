package eu.fox7.bonxai.typeautomaton.factories;

import gjb.flt.automata.impl.sparse.State;
import gjb.flt.automata.impl.sparse.Symbol;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import eu.fox7.bonxai.common.Particle;
import eu.fox7.bonxai.common.ParticleContainer;
import eu.fox7.bonxai.typeautomaton.AnnotatedNFATypeAutomaton;
import eu.fox7.bonxai.typeautomaton.TypeAutomaton;
import eu.fox7.bonxai.xsd.ComplexContentType;
import eu.fox7.bonxai.xsd.ComplexType;
import eu.fox7.bonxai.xsd.Content;
import eu.fox7.bonxai.xsd.Element;
import eu.fox7.bonxai.xsd.Type;
import eu.fox7.bonxai.xsd.XSDSchema;

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
    private Map<Type, State> typeMap;
    
	private Queue<State> workingQueue;

	public XSDTypeAutomatonFactory(boolean addSimpleTypes) {
		this.addSimpleTypes = addSimpleTypes;
	}

	
	public XSDTypeAutomatonFactory() {
		this.addSimpleTypes = false;
	}

	public TypeAutomaton createTypeAutomaton(XSDSchema schema) {
    	this.typeAutomaton = new AnnotatedNFATypeAutomaton();
    	this.typeMap = new HashMap<Type,State>();
    	this.workingQueue = new LinkedList<State>();
    	
    	State initial = new State();
    	typeAutomaton.addState(initial);
    	typeAutomaton.setInitialState(initial);
    	
    	Collection<Element> rootElements =  schema.getElements();
    	
    	for (Element rootElement: rootElements) {
    		addChild(initial, rootElement);
    	}
    	
		while (! workingQueue.isEmpty()) {
			State state = workingQueue.remove();
			Type type = typeAutomaton.getType(state).getReference();
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

    // only return elements of complex type right now.
	private Set<Element> getChilds(Particle particle) {
		Set<Element> childs = new HashSet<Element>();
		if (particle instanceof Element) {
			Element element = (Element) particle;
			if (this.addSimpleTypes || (element.getType() instanceof ComplexType)) {
				childs.add((Element) particle);
				
			}
		} else if (particle instanceof ParticleContainer) {
			ParticleContainer particleContainer = (ParticleContainer) particle;
			for (Particle childParticle: particleContainer.getParticles()) {
				childs.addAll(getChilds(childParticle));
			}
		}
		return childs;
	}

	private void addChild(State state, Element child) {
    	String name = child.getName();
		Symbol symbol = Symbol.create(name);
    	typeAutomaton.addSymbol(symbol);
    	Type type = child.getType();
    	State newState;
    	
    	if (typeMap.containsKey(type)) {
    		newState = typeMap.get(type);
    	} else {
    		newState = new State();
    		typeAutomaton.addState(newState);
    		typeAutomaton.setType(newState, child.getTypeReference());
    		typeAutomaton.setElementProperties(newState, child.getProperties());
    		typeMap.put(type, newState);
    		workingQueue.add(newState);
    	}
    	
    	typeAutomaton.addTransition(symbol, state, newState);
    }
}
