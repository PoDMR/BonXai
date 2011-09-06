package eu.fox7.treeautomata.converter;

import java.util.Set;
import eu.fox7.bonxai.common.DefaultNamespace;
import eu.fox7.bonxai.common.NamespaceList;
import eu.fox7.bonxai.common.SymbolTable;
import eu.fox7.bonxai.common.SymbolTableFoundation;
import eu.fox7.bonxai.common.SymbolTableRef;
import eu.fox7.bonxai.typeautomaton.TypeAutomaton;
import eu.fox7.bonxai.xsd.Element;
import eu.fox7.bonxai.xsd.Type;
import eu.fox7.bonxai.xsd.XSDSchema;
import eu.fox7.flt.automata.NotDFAException;
import eu.fox7.flt.automata.impl.sparse.State;
import eu.fox7.flt.automata.impl.sparse.Symbol;
import eu.fox7.flt.treeautomata.impl.ContextAutomaton;

public class ContextAutomaton2XSDConverter {
	public XSDSchema convert(ContextAutomaton contextAutomaton) {
		XSDSchema xsdSchema = new XSDSchema();
		
		SymbolTableFoundation<eu.fox7.bonxai.xsd.Element> elementSymbolTable = xsdSchema.getElementSymbolTable();

		String targetNamespace = "";
		
		NamespaceList namespaceList = new NamespaceList(new DefaultNamespace(targetNamespace));
		
		xsdSchema.setNamespaceList(namespaceList);
		
		ContextAutomatonTypeAutomatonFactory taFactory = new ContextAutomatonTypeAutomatonFactory();
		TypeAutomaton typeAutomaton = taFactory.convertContextAutomaton(contextAutomaton);

		Set<String> rootElements = typeAutomaton.getSymbolValuesFrom(typeAutomaton.getInitialStateValue());
		
		for (String rootElement: rootElements) {
			Element element = new Element("{}"+rootElement);
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
		SymbolTableFoundation<Type> typeSymbolTable = typeAutomaton.getTypeSymbolTable();
		
		for (SymbolTableRef<Type> typeRef: typeSymbolTable.getReferences())
			if (! typeRef.getReference().isAnonymous())
				xsdSchema.addType(typeRef);
	
		return xsdSchema;
	}
}
