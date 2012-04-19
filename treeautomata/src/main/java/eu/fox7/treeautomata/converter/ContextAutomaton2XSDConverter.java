package eu.fox7.treeautomata.converter;

import java.util.Collection;
import eu.fox7.bonxai.typeautomaton.TypeAutomaton;
import eu.fox7.flt.automata.NotDFAException;
import eu.fox7.flt.automata.impl.sparse.State;
import eu.fox7.flt.automata.impl.sparse.Symbol;
import eu.fox7.flt.treeautomata.impl.ContextAutomaton;
import eu.fox7.schematoolkit.common.DefaultNamespace;
import eu.fox7.schematoolkit.common.QualifiedName;
import eu.fox7.schematoolkit.xsd.om.Element;
import eu.fox7.schematoolkit.xsd.om.Type;
import eu.fox7.schematoolkit.xsd.om.XSDSchema;

public class ContextAutomaton2XSDConverter {
	public XSDSchema convert(ContextAutomaton contextAutomaton) {
		XSDSchema xsdSchema = new XSDSchema();
		
		@SuppressWarnings("deprecation")
		ContextAutomatonTypeAutomatonFactory taFactory = new ContextAutomatonTypeAutomatonFactory(xsdSchema.getNamespaceList());
		TypeAutomaton typeAutomaton = taFactory.convertContextAutomaton(contextAutomaton);

		Collection<QualifiedName> rootElements = typeAutomaton.getRootElements();

		DefaultNamespace targetNamespace = new DefaultNamespace(rootElements.iterator().next().getNamespaceURI());
		
		xsdSchema.setDefaultNamespace(targetNamespace);
		xsdSchema.setTargetNamespace(targetNamespace);

		for (QualifiedName rootElement: rootElements) {
			Element element = new Element(rootElement);
			State state;
			try {
				state = typeAutomaton.getNextState(Symbol.create(rootElement.getFullyQualifiedName()), typeAutomaton.getInitialState());
			} catch (NotDFAException e) {
				throw new RuntimeException(e);
			}
			element.setTypeName(typeAutomaton.getType(state).getName());
			xsdSchema.addElement(element);
		}
		
		for (Type type: typeAutomaton.getTypes())
			xsdSchema.addType(type);
	
		return xsdSchema;
	}
}
