package eu.fox7.treeautomata.converter;

import java.util.Set;
import gjb.flt.automata.NotDFAException;
import gjb.flt.automata.impl.sparse.State;
import gjb.flt.automata.impl.sparse.Symbol;
import gjb.flt.treeautomata.impl.ContextAutomaton;
import de.tudortmund.cs.bonxai.common.DefaultNamespace;
import de.tudortmund.cs.bonxai.common.NamespaceList;
import de.tudortmund.cs.bonxai.common.SymbolTable;
import de.tudortmund.cs.bonxai.common.SymbolTableFoundation;
import de.tudortmund.cs.bonxai.common.SymbolTableRef;
import de.tudortmund.cs.bonxai.typeautomaton.TypeAutomaton;
import de.tudortmund.cs.bonxai.xsd.Element;
import de.tudortmund.cs.bonxai.xsd.Type;
import de.tudortmund.cs.bonxai.xsd.XSDSchema;

public class ContextAutomaton2XSDConverter {
	public XSDSchema convert(ContextAutomaton contextAutomaton) {
		XSDSchema xsdSchema = new XSDSchema();
		
		SymbolTableFoundation<de.tudortmund.cs.bonxai.xsd.Element> elementSymbolTable = xsdSchema.getElementSymbolTable();

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
