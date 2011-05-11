package de.tudortmund.cs.bonxai.converter.bonxai2xsd;

import gjb.flt.automata.NotDFAException;
import gjb.flt.automata.impl.sparse.State;
import gjb.flt.automata.impl.sparse.Symbol;
import gjb.flt.automata.impl.sparse.Transition;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import de.tudortmund.cs.bonxai.bonxai.Bonxai;
import de.tudortmund.cs.bonxai.bonxai.Declaration;
import de.tudortmund.cs.bonxai.bonxai.GrammarList;
import de.tudortmund.cs.bonxai.common.NamespaceList;
import de.tudortmund.cs.bonxai.common.SymbolTable;
import de.tudortmund.cs.bonxai.common.SymbolTableFoundation;
import de.tudortmund.cs.bonxai.common.SymbolTableRef;
import de.tudortmund.cs.bonxai.typeautomaton.TypeAutomaton;
import de.tudortmund.cs.bonxai.typeautomaton.factories.BonxaiTypeAutomatonConstruction;
import de.tudortmund.cs.bonxai.xsd.AttributeGroup;
import de.tudortmund.cs.bonxai.xsd.Element;
import de.tudortmund.cs.bonxai.xsd.Type;
import de.tudortmund.cs.bonxai.xsd.XSDSchema;

public class NewBonxai2XSDConverter {
	public static Map<String,XSDSchema> convert(Bonxai bonxai) {
		Map<String,XSDSchema> schemaMap = new HashMap<String,XSDSchema>();
		XSDSchema xsdSchema = new XSDSchema();
		
		SymbolTableFoundation<AttributeGroup> attributeGroupSymbolTable = xsdSchema.getAttributeGroupSymbolTable();
		SymbolTableFoundation<de.tudortmund.cs.bonxai.xsd.Element> elementSymbolTable = xsdSchema.getElementSymbolTable();
		SymbolTableFoundation<Type> typeSymbolTable = xsdSchema.getTypeSymbolTable();
		SymbolTableFoundation<de.tudortmund.cs.bonxai.xsd.Group> groupSymbolTable = xsdSchema.getGroupSymbolTable();

		Declaration declaration = bonxai.getDeclaration();
		NamespaceList namespaceList = declaration.getNamespaceList();
		String targetNamespace = namespaceList.getDefaultNamespace().getUri();
		GrammarList grammarList = bonxai.getGrammarList();
		
		xsdSchema.setNamespaceList(namespaceList);
		
		//convert groups
		//TODO
		
		//convert attributeGroups
		//TODO
		
		

		BonxaiTypeAutomatonConstruction btac = new BonxaiTypeAutomatonConstruction(targetNamespace, attributeGroupSymbolTable, elementSymbolTable, groupSymbolTable, typeSymbolTable);
		TypeAutomaton typeAutomaton = btac.constructTypeAutomaton(grammarList);

		//add root elements
		Vector<String> rootElements = grammarList.getRootElementNames();
		for (String rootElement: rootElements) {
			Element element = new Element(rootElement);
			State state;
			try {
				state = typeAutomaton.getNextState(Symbol.create(rootElement), typeAutomaton.getInitialState());
			} catch (NotDFAException e) {
				throw new RuntimeException(e);
			}
			element.setType(typeAutomaton.getType(state));
			elementSymbolTable.updateOrCreateReference(rootElement, element);
			xsdSchema.addElement(elementSymbolTable.getReference(rootElement));
		}
		
		//convert TypeAutomaton to XSD
		
		for (SymbolTableRef<Type> typeRef: typeSymbolTable.getReferences())
			if (! typeRef.getReference().isAnonymous())
				xsdSchema.addType(typeRef);
	
		schemaMap.put(xsdSchema.getTargetNamespace(), xsdSchema);
		
		return schemaMap;
	}
}
