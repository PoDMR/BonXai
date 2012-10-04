package eu.fox7.schematoolkit.converter.bonxai2xsd;


import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import eu.fox7.flt.automata.NotDFAException;
import eu.fox7.flt.automata.impl.sparse.State;
import eu.fox7.flt.automata.impl.sparse.Symbol;
import eu.fox7.schematoolkit.AbstractSchemaConverter;
import eu.fox7.schematoolkit.Schema;
import eu.fox7.schematoolkit.bonxai.om.Bonxai;
import eu.fox7.schematoolkit.bonxai.om.Expression;
import eu.fox7.schematoolkit.common.IdentifiedNamespace;
import eu.fox7.schematoolkit.common.QualifiedName;
import eu.fox7.schematoolkit.exceptions.ConversionFailedException;
import eu.fox7.schematoolkit.typeautomaton.TypeAutomaton;
import eu.fox7.schematoolkit.typeautomaton.factories.AdvancedTypeNameGenerator;
import eu.fox7.schematoolkit.typeautomaton.factories.BonxaiTypeAutomatonConstruction;
import eu.fox7.schematoolkit.typeautomaton.factories.TrivialTypeNameGenerator;
import eu.fox7.schematoolkit.typeautomaton.factories.TypeNameGenerator;
import eu.fox7.schematoolkit.xsd.om.Element;
import eu.fox7.schematoolkit.xsd.om.Type;
import eu.fox7.schematoolkit.xsd.om.XSDSchema;
import eu.fox7.schematoolkit.xsd.om.XSDSchema.Qualification;

public class Bonxai2XSDConverter extends AbstractSchemaConverter {
	private Map<Expression, Set<State>> expressionStateMap;
	private Map<State, Expression> stateExpressionMap;
	private Map<State, Type> stateTypeMap;
	private TypeAutomaton typeAutomaton;
	
	
	public XSDSchema convert(Schema schema) throws ConversionFailedException {
		if (!(schema instanceof Bonxai))
			throw new ConversionFailedException("Can only convert BonXai schemas.");
		
		Bonxai bonxai = (Bonxai) schema;
		XSDSchema xsdSchema = new XSDSchema();
		
		List<IdentifiedNamespace> namespaces = bonxai.getNamespaces();
		for (IdentifiedNamespace ns: namespaces)
			xsdSchema.addIdentifiedNamespace(ns);
		
		xsdSchema.setElementFormDefault(Qualification.qualified);
		xsdSchema.setTargetNamespace(bonxai.getTargetNamespace());
		xsdSchema.setDefaultNamespace(bonxai.getTargetNamespace());
		
		BonxaiTypeAutomatonConstruction btac = new BonxaiTypeAutomatonConstruction();
		TypeNameGenerator tng = new AdvancedTypeNameGenerator(bonxai.getTargetNamespace());
		typeAutomaton = btac.constructTypeAutomaton(bonxai, tng, false);
		btac.convertGroups(xsdSchema);
		expressionStateMap = btac.getExpressionStateMap();

		//System.err.println(typeAutomaton);
		
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
		
		
		
		
		Collection<Type> typeList = typeAutomaton.getTypes();
		
		//convert type automaton to xsd
		for (Type type: typeList)
			xsdSchema.addType(type);
	
		//TODO: convert simple types
		
		return xsdSchema;
	}

	/**
	 * @return the expressionStateMap
	 */
	public Map<Expression, Set<State>> getExpressionStateMap() {
		return expressionStateMap;
	}

	/**
	 * @return the stateExpressionMap
	 */
	public Map<State, Expression> getStateExpressionMap() {
		return stateExpressionMap;
	}

	/**
	 * @return the stateTypeMap
	 */
	public Map<State, Type> getStateTypeMap() {
		return stateTypeMap;
	}

	/**
	 * @return the typeAutomaton
	 */
	public TypeAutomaton getTypeAutomaton() {
		return typeAutomaton;
	}
}
