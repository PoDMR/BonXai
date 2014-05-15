package eu.fox7.treeautomata.converter;

import java.util.Collection;

import eu.fox7.flt.automata.NotDFAException;
import eu.fox7.flt.automata.impl.sparse.AnnotatedSparseNFA;
import eu.fox7.flt.automata.impl.sparse.SparseNFA;
import eu.fox7.flt.automata.impl.sparse.State;
import eu.fox7.flt.automata.impl.sparse.StateNFA;
import eu.fox7.flt.automata.impl.sparse.Symbol;
import eu.fox7.schematoolkit.common.DefaultNamespace;
import eu.fox7.schematoolkit.common.IdentifiedNamespace;
import eu.fox7.schematoolkit.common.QualifiedName;
import eu.fox7.schematoolkit.typeautomaton.TypeAutomaton;
import eu.fox7.schematoolkit.xsd.om.Element;
import eu.fox7.schematoolkit.xsd.om.Type;
import eu.fox7.schematoolkit.xsd.om.XSDSchema;

public class ContextAutomaton2XSDConverter {
	public XSDSchema convert(AnnotatedSparseNFA<? extends StateNFA, ?> contextAutomaton) {
		XSDSchema xsdSchema = new XSDSchema();
		
		ContextAutomatonTypeAutomatonFactory taFactory = new ContextAutomatonTypeAutomatonFactory();
		System.err.println("Context Automaton: " + contextAutomaton);
		TypeAutomaton typeAutomaton = taFactory.convertContextAutomaton(contextAutomaton);

		System.err.println("TypeAutomaton: " + typeAutomaton);
		
		Collection<QualifiedName> rootElements = typeAutomaton.getRootElements();

		DefaultNamespace targetNamespace = new DefaultNamespace(rootElements.iterator().next().getNamespaceURI());
		
		xsdSchema.setDefaultNamespace(targetNamespace);
		xsdSchema.setTargetNamespace(targetNamespace);
		xsdSchema.addIdentifiedNamespace(new IdentifiedNamespace("xs", XSDSchema.XMLSCHEMA_NAMESPACE));

		for (QualifiedName rootElement: rootElements) {
			Element element = new Element(rootElement);
			State state;
			try {
				state = typeAutomaton.getNextState(Symbol.create(rootElement.getFullyQualifiedName()), typeAutomaton.getInitialState());
				if (state == null && rootElement.getNamespaceURI().equals(""))
					state = typeAutomaton.getNextState(Symbol.create(rootElement.getName()), typeAutomaton.getInitialState());
			} catch (NotDFAException e) {
				throw new RuntimeException(e);
			}
			Type type = typeAutomaton.getType(state);
			if (type != null)
				element.setTypeName(typeAutomaton.getType(state).getName());
			xsdSchema.addElement(element);
		}
		
		for (Type type: typeAutomaton.getTypes())
			xsdSchema.addType(type);
	
		return xsdSchema;
	}
}
