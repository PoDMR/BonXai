package eu.fox7.bonxai.typeautomaton;

import java.util.Map;

import eu.fox7.bonxai.common.SymbolAlreadyRegisteredException;
import eu.fox7.bonxai.common.SymbolTable;
import eu.fox7.bonxai.common.SymbolTableFoundation;
import eu.fox7.bonxai.common.SymbolTableRef;
import eu.fox7.bonxai.xsd.Type;
import eu.fox7.flt.automata.impl.sparse.AnnotatedSparseNFA;
import eu.fox7.flt.automata.impl.sparse.State;
import eu.fox7.flt.automata.impl.sparse.StateNFA;
import eu.fox7.util.Pair;

public class AnnotatedNFATypeAutomaton extends
		AnnotatedSparseNFA<Pair<SymbolTableRef<Type>, ElementProperties>, Object> implements TypeAutomaton {
	
	private SymbolTableFoundation<Type> symbolTable = new SymbolTable<Type>();
	
	public AnnotatedNFATypeAutomaton() {
		super();
	}
	
	public AnnotatedNFATypeAutomaton(StateNFA nfa, Map<State,State> stateMap) {
		super(nfa, stateMap);
	}

	public SymbolTableRef<Type> getType(State state) {
		if (this.hasAnnotation(state))
			return this.getAnnotation(state).getFirst();
		else
			return null;
	}

	public void createTypeRef(State state, String typename) throws SymbolAlreadyRegisteredException {
		this.setType(state, this.symbolTable.getReference(typename));
	}
	
	public void setType(State state, SymbolTableRef<Type> type) {
		ElementProperties elementProperties = null;
		if (this.hasAnnotation(state)) 
			elementProperties = this.getAnnotation(state).getSecond();
		this.annotate(state, new Pair<SymbolTableRef<Type>, ElementProperties>(type, elementProperties));
	}

	public void updateType(String typename, Type type) {
		this.symbolTable.updateOrCreateReference(typename, type);
	}
	
	public void setElementProperties(State state, ElementProperties elementProperties) {
		SymbolTableRef<Type> type = null;
		if (this.hasAnnotation(state))
			type = this.getAnnotation(state).getFirst();
		this.annotate(state, new Pair<SymbolTableRef<Type>, ElementProperties>(type, elementProperties));
	}

	public ElementProperties getElementProperties(State state) {
		if (this.hasAnnotation(state)) {
			return this.getAnnotation(state).getSecond();
		} else {
			return null;
		}
	}

	@Override
	public SymbolTableFoundation<Type> getTypeSymbolTable() {
		return this.symbolTable;
	}
}
