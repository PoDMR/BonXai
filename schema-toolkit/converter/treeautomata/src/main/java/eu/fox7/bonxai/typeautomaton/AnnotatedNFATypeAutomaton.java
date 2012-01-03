package eu.fox7.bonxai.typeautomaton;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import eu.fox7.bonxai.common.QualifiedName;
import eu.fox7.bonxai.common.ElementProperties;

import eu.fox7.bonxai.xsd.Type;
import eu.fox7.flt.automata.impl.sparse.SparseNFA;
import eu.fox7.flt.automata.impl.sparse.State;
import eu.fox7.flt.automata.impl.sparse.StateNFA;

public class AnnotatedNFATypeAutomaton extends
		SparseNFA implements TypeAutomaton {
	
	private HashMap<QualifiedName,Type> types = new HashMap<QualifiedName,Type>();
	private HashMap<State,QualifiedName> typeNames = new HashMap<State,QualifiedName>();
	private HashMap<State,ElementProperties> elementProperties = new HashMap<State,ElementProperties>();
	
	public AnnotatedNFATypeAutomaton() {
		super();
	}
	
	public AnnotatedNFATypeAutomaton(StateNFA nfa, Map<State,State> stateMap) {
		super(nfa, stateMap);
	}

	@Override
	public Type getType(State state) {
		return types.get(typeNames.get(state));
	}

	@Override
	public QualifiedName getTypeName(State state) {
		return typeNames.get(state);
	}

	@Override
	public void setType(State state, Type type) {
		types.put(type.getName(), type);
		typeNames.put(state, type.getName());
	}

	@Override
	public void setType(QualifiedName typeName, Type type) {
		types.put(typeName, type);
	}

	@Override
	public void setTypeName(State state, QualifiedName typeName) {
		typeNames.put(state, typeName);
	}

	@Override
	public void setElementProperties(State state,
			ElementProperties elementProperties) {
		this.elementProperties.put(state, elementProperties);
	}

	@Override
	public ElementProperties getElementProperties(State state) {
		return this.elementProperties.get(state);
	}

	@Override
	public Collection<Type> getTypes() {
		return this.types.values();
	}
	

	
}
