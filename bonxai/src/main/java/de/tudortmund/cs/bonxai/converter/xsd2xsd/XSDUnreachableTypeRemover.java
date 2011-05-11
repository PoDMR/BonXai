package de.tudortmund.cs.bonxai.converter.xsd2xsd;

import gjb.flt.automata.impl.sparse.State;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import de.tudortmund.cs.bonxai.common.SymbolTableFoundation;
import de.tudortmund.cs.bonxai.common.SymbolTableRef;
import de.tudortmund.cs.bonxai.typeautomaton.TypeAutomaton;
import de.tudortmund.cs.bonxai.typeautomaton.factories.XSDTypeAutomatonFactory;
import de.tudortmund.cs.bonxai.xsd.Type;
import de.tudortmund.cs.bonxai.xsd.XSDSchema;

public class XSDUnreachableTypeRemover {
	public static void removeUnreachableTypes(XSDSchema schema) {
		XSDTypeAutomatonFactory factory = new XSDTypeAutomatonFactory();
		TypeAutomaton typeAutomaton =factory.createTypeAutomaton(schema);
		Collection<State> states =typeAutomaton.getStates();
		Set<String> usedTypes = new HashSet<String>();
		for (State state: states) {
			SymbolTableRef<Type> type = typeAutomaton.getType(state);
			if (type != null) {
				usedTypes.add(typeAutomaton.getType(state).getKey());
			}
		}
		
		SymbolTableFoundation<Type> typeSymbolTable = schema.getTypeSymbolTable();
		Set<String> unusedTypes = new HashSet<String>(typeSymbolTable.getAllKeys());
		unusedTypes.removeAll(usedTypes);
		for (String unusedType: unusedTypes) {
			schema.removeType(typeSymbolTable.getReference(unusedType));
			typeSymbolTable.removeReference(unusedType);
		}
		
		
	}
}
