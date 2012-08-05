package eu.fox7.bonxai.converter.xsd2dtd;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import eu.fox7.flt.automata.NotDFAException;
import eu.fox7.flt.automata.converters.Minimizer;
import eu.fox7.flt.automata.converters.NFAMinimizer;
import eu.fox7.flt.automata.factories.sparse.ProductDFAFactory;
import eu.fox7.flt.automata.impl.sparse.ModifiableStateDFA;
import eu.fox7.flt.automata.impl.sparse.State;
import eu.fox7.flt.automata.impl.sparse.Symbol;
import eu.fox7.flt.automata.impl.sparse.Transition;
import eu.fox7.flt.treeautomata.impl.ContentAutomaton;
import eu.fox7.schematoolkit.common.AttributeParticle;
import eu.fox7.schematoolkit.common.QualifiedName;
import eu.fox7.schematoolkit.typeautomaton.TypeAutomaton;
import eu.fox7.schematoolkit.typeautomaton.operations.StateWrapper;
import eu.fox7.schematoolkit.typeautomaton.operations.TypeAutomatonUnion;
import eu.fox7.schematoolkit.xsd.om.ComplexContentType;
import eu.fox7.schematoolkit.xsd.om.ComplexType;
import eu.fox7.schematoolkit.xsd.om.SimpleType;
import eu.fox7.schematoolkit.xsd.om.Type;
import eu.fox7.schematoolkit.xsd.om.TypeReference;
import eu.fox7.treeautomata.converter.ContentAutomaton2TypeConverter;
import eu.fox7.treeautomata.converter.Type2ContentAutomatonConverter;

public class TypeAutomatonDTDizer {
	private TypeAutomaton typeAutomaton;
	private TypeAutomaton result;
	private Map<Symbol,Set<State>> nameMap;
	private Map<Symbol,State> stateMap;
	
	public void simplify(TypeAutomaton typeAutomaton) {
		this.typeAutomaton = typeAutomaton;
		Collection<Transition> transitions = typeAutomaton.getTransitionMap().getTransitions();
		for (Transition transition: transitions) {
			Set<State> stateSet = nameMap.get(transition.getSymbol());
			if (stateSet == null) {
				stateSet = new HashSet<State>();
				nameMap.put(transition.getSymbol(), stateSet);
			} 				
			stateSet.add(transition.getToState());
		}
		
		for (Symbol symbol: nameMap.keySet()) {
			result.addSymbol(symbol);
			State state = new State();
			result.addState(state);
			result.setTypeName(state, QualifiedName.getQualifiedNameFromFQN(symbol.toString()));
			stateMap.put(symbol, state);
		}
		
		for (Symbol symbol: nameMap.keySet()) {
			Set<State> states = nameMap.get(symbol);
			State state = stateMap.get(symbol);
			uniteStates(state, states);
		}
	}
	
	
	private State uniteStates(State resultState, Set<State> states) {
		System.err.println("Unite states " + states);
		
		Set<Type> types = new HashSet<Type>();
		for (State state: states) {
			Type type = typeAutomaton.getType(state);
			types.add(type);
		}

		Set<Symbol> usedSymbols = new HashSet<Symbol>();
		Set<ComplexType> complexTypes = new HashSet<ComplexType>(); 
		Collection<AttributeParticle> attributeParticles = new HashSet<AttributeParticle>(); 
		
		boolean complex = false;
		boolean complexContent = false;
		boolean simple = false;
		
		for (Type type: types) {
			if (type instanceof ComplexType) {
				complex = true;
				ComplexType complexType = (ComplexType) type;
				attributeParticles.addAll(complexType.getAttributes());
				if (complexType.getContent() instanceof ComplexContentType) {
					complexContent = true;
					complexTypes.add(complexType);
					ComplexContentType content = (ComplexContentType) complexType.getContent();
					usedSymbols.addAll(getUsedSymbols(content.getParticle()));
					if (complexType.getMixed())
						simple = true;
				} else
					simple = true;
			} else if (type instanceof SimpleType)
				simple = true;
			else if (type instanceof TypeReference)
				if (((TypeReference) type).isXSDSimpleType())
					simple = true;
		}

		for (Symbol symbol: usedSymbols) {
			this.result.addSymbol(symbol);
			State targetState = stateMap.get(symbol);

			this.result.addTransition(symbol, resultState, targetState);
		}

		Type type=null;
		
		if (complexContent) {
			type = uniteComplexTypes(complexTypes, resultState);
			((ComplexType) type).setMixed(simple);
		} else if (complex) {
			
		} else if (simple) {
			uniteSimpleTypes(types);
		}
		
		type.setName(name);
		
		if (type != null)
			result.setType(resultState, type);
	}

	private void uniteSimpleTypes(Set<Type> types) {
		// TODO Auto-generated method stub
	}

	private Type uniteComplexTypes(Set<ComplexType> complexTypes, State resultState) {
		System.err.println("Unite complex types: " + complexTypes);
		Type2ContentAutomatonConverter converter = new Type2ContentAutomatonConverter();
		Vector<ContentAutomaton> automata = new Vector<ContentAutomaton>(complexTypes.size());
		for (ComplexType type: complexTypes) {
			ContentAutomaton automaton = converter.convertType(type);
			automata.add(automaton);
		}
		
		ModifiableStateDFA productAutomaton;
		try {
			productAutomaton = ProductDFAFactory.union(automata.toArray(new ContentAutomaton[0]));
			Minimizer minimizer = new NFAMinimizer();
			minimizer.minimize(productAutomaton);
		} catch (NotDFAException e) {
			throw new RuntimeException(e);
		}
		
		ContentAutomaton2TypeConverter backConverter = new ContentAutomaton2TypeConverter(result); 
		Type resultType = backConverter.convertContentAutomaton(productAutomaton, result.getTypeName(resultState), resultState);
		
		return resultType;
	}

}
