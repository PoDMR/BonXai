package de.tudortmund.cs.bonxai.typeautomaton;

import gjb.flt.automata.impl.sparse.ModifiableStateNFA;
import gjb.flt.automata.impl.sparse.State;
import gjb.flt.automata.impl.sparse.StateDFA;
import de.tudortmund.cs.bonxai.common.SymbolAlreadyRegisteredException;
import de.tudortmund.cs.bonxai.common.SymbolTable;
import de.tudortmund.cs.bonxai.common.SymbolTableFoundation;
import de.tudortmund.cs.bonxai.common.SymbolTableRef;
import de.tudortmund.cs.bonxai.xsd.Type;

public interface TypeAutomaton extends ModifiableStateNFA, StateDFA {
	public SymbolTableRef<Type> getType(State state);
	public void setType(State state, SymbolTableRef<Type> type);

	public void setElementProperties(State state, ElementProperties elementProperties);
	public ElementProperties getElementProperties(State state);
	public void createTypeRef(State state, String typename) throws SymbolAlreadyRegisteredException;
	public void updateType(String typename, Type type);
	public SymbolTableFoundation<Type> getTypeSymbolTable();
}