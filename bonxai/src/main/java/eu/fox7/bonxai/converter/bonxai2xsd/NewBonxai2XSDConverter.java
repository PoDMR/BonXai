package eu.fox7.bonxai.converter.bonxai2xsd;

import gjb.flt.automata.NotDFAException;
import gjb.flt.automata.impl.sparse.State;
import gjb.flt.automata.impl.sparse.Symbol;
import gjb.flt.automata.impl.sparse.Transition;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import eu.fox7.bonxai.bonxai.Bonxai;
import eu.fox7.bonxai.bonxai.Declaration;
import eu.fox7.bonxai.bonxai.GrammarList;
import eu.fox7.bonxai.common.NamespaceList;
import eu.fox7.bonxai.common.SymbolTable;
import eu.fox7.bonxai.common.SymbolTableFoundation;
import eu.fox7.bonxai.common.SymbolTableRef;
import eu.fox7.bonxai.typeautomaton.TypeAutomaton;
import eu.fox7.bonxai.typeautomaton.factories.BonxaiTypeAutomatonConstruction;
import eu.fox7.bonxai.xsd.AttributeGroup;
import eu.fox7.bonxai.xsd.Element;
import eu.fox7.bonxai.xsd.Type;
import eu.fox7.bonxai.xsd.XSDSchema;
import eu.fox7.bonxai.xsd.XSDSchema.Qualification;

public class NewBonxai2XSDConverter {
	public static Map<String,XSDSchema> convert(Bonxai bonxai) {
		Map<String,XSDSchema> schemaMap = new HashMap<String,XSDSchema>();
		XSDSchema xsdSchema = new XSDSchema();
		
		SymbolTableFoundation<AttributeGroup> attributeGroupSymbolTable = xsdSchema.getAttributeGroupSymbolTable();
		SymbolTableFoundation<eu.fox7.bonxai.xsd.Element> elementSymbolTable = xsdSchema.getElementSymbolTable();
		SymbolTableFoundation<Type> typeSymbolTable = xsdSchema.getTypeSymbolTable();
		SymbolTableFoundation<eu.fox7.bonxai.xsd.Group> groupSymbolTable = xsdSchema.getGroupSymbolTable();

		Declaration declaration = bonxai.getDeclaration();
		// TODO: handle unqualified
		NamespaceList namespaceList = declaration.getNamespaceList();
		String targetNamespace = namespaceList.getDefaultNamespace().getUri();
		GrammarList grammarList = bonxai.getGrammarList();
		
		xsdSchema.setNamespaceList(namespaceList);
		
		xsdSchema.setElementFormDefault(Qualification.qualified);
		
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
