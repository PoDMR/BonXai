package eu.fox7.bonxai.converter.xsd2xsd;


import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import eu.fox7.bonxai.common.SymbolTableFoundation;
import eu.fox7.bonxai.common.SymbolTableRef;
import eu.fox7.bonxai.typeautomaton.TypeAutomaton;
import eu.fox7.bonxai.typeautomaton.factories.XSDTypeAutomatonFactory;
import eu.fox7.bonxai.xsd.Type;
import eu.fox7.bonxai.xsd.XSDSchema;
import eu.fox7.flt.automata.impl.sparse.State;

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
