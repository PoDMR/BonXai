package eu.fox7.bonxai.converter.bonxai2xsd;


import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import eu.fox7.bonxai.typeautomaton.TypeAutomaton;
import eu.fox7.bonxai.typeautomaton.factories.BonxaiTypeAutomatonConstruction;
import eu.fox7.bonxai.typeautomaton.factories.TrivialTypeNameGenerator;
import eu.fox7.bonxai.typeautomaton.factories.TypeNameGenerator;
import eu.fox7.flt.automata.NotDFAException;
import eu.fox7.flt.automata.impl.sparse.State;
import eu.fox7.flt.automata.impl.sparse.Symbol;
import eu.fox7.schematoolkit.bonxai.om.Bonxai;
import eu.fox7.schematoolkit.bonxai.om.BonxaiAbstractGroup;
import eu.fox7.schematoolkit.common.IdentifiedNamespace;
import eu.fox7.schematoolkit.common.QualifiedName;
import eu.fox7.schematoolkit.xsd.om.Element;
import eu.fox7.schematoolkit.xsd.om.Type;
import eu.fox7.schematoolkit.xsd.om.XSDSchema;
import eu.fox7.schematoolkit.xsd.om.XSDSchema.Qualification;

public class NewBonxai2XSDConverter {
	public static XSDSchema convert(Bonxai bonxai) {
		XSDSchema xsdSchema = new XSDSchema();
		
		List<IdentifiedNamespace> namespaces = bonxai.getNamespaces();
		for (IdentifiedNamespace ns: namespaces)
			xsdSchema.addIdentifiedNamespace(ns);
		
		xsdSchema.setElementFormDefault(Qualification.qualified);
		xsdSchema.setTargetNamespace(bonxai.getDefaultNamespace());
		
		//convert groups
		for (BonxaiAbstractGroup group: bonxai.getGroups()) {
		//TODO: Convert groups	
		}		

		BonxaiTypeAutomatonConstruction btac = new BonxaiTypeAutomatonConstruction();
		TypeNameGenerator tng = new TrivialTypeNameGenerator(bonxai.getDefaultNamespace());
		TypeAutomaton typeAutomaton = btac.constructTypeAutomaton(bonxai, tng);

		System.err.println(typeAutomaton);
		
		//add root elements
		List<QualifiedName> rootElements = bonxai.getRootElementNames();
		for (QualifiedName rootElement: rootElements) {
			Element element = new Element(rootElement);
			State state;
			try {
				state = typeAutomaton.getNextState(Symbol.create(rootElement.getFullyQualifiedName()), typeAutomaton.getInitialState());
			} catch (NotDFAException e) {
				throw new RuntimeException(e);
			}
			element.setTypeName(typeAutomaton.getTypeName(state));
			xsdSchema.addElement(element);
		}
		
		
		Collection<Type> typeList = typeAutomaton.getTypes();;
		
		
		//convert type automaton to xsd
		for (Type type: typeList)
			xsdSchema.addType(type);
	
		//TODO: convert simple types
		
		return xsdSchema;
	}
}
