package eu.fox7.schematoolkit.typeautomaton.operations;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import eu.fox7.bonxai.typeautomaton.AnnotatedNFATypeAutomaton;
import eu.fox7.bonxai.typeautomaton.TypeAutomaton;
import eu.fox7.bonxai.typeautomaton.operations.Operation;
import eu.fox7.flt.automata.NotDFAException;
import eu.fox7.flt.automata.converters.Minimizer;
import eu.fox7.flt.automata.converters.NFAMinimizer;
import eu.fox7.flt.automata.factories.sparse.ProductDFAFactory;
import eu.fox7.flt.automata.factories.sparse.ProductNFAFactory;
import eu.fox7.flt.automata.impl.sparse.ModifiableStateDFA;
import eu.fox7.flt.automata.impl.sparse.State;
import eu.fox7.flt.automata.impl.sparse.Symbol;
import eu.fox7.flt.treeautomata.impl.ContentAutomaton;
import eu.fox7.schematoolkit.common.AttributeParticle;
import eu.fox7.schematoolkit.common.QualifiedName;
import eu.fox7.schematoolkit.xsd.om.ComplexContentType;
import eu.fox7.schematoolkit.xsd.om.ComplexType;
import eu.fox7.schematoolkit.xsd.om.SimpleType;
import eu.fox7.schematoolkit.xsd.om.Type;
import eu.fox7.treeautomata.converter.ContentAutomaton2TypeConverter;
import eu.fox7.treeautomata.converter.Type2ContentAutomatonConverter;

public class TypeAutomatonIntersection extends Operation {

	private TypeAutomaton result;
	private HashMap<Set<StateWrapper>,State> stateMap = new HashMap<Set<StateWrapper>,State>(); 
	private TypenameGenerator typenameGenerator = new SimpleTypenameGenerator();
	private String namespace;
	
	@Override
	public TypeAutomaton apply(Collection<TypeAutomaton> input) {
		Set<StateWrapper> states = new HashSet<StateWrapper>();
		Set<Symbol> rootSymbols = null;
		for (TypeAutomaton automaton: input) {
			states.add(new StateWrapper(automaton, automaton.getInitialState()));
			if (rootSymbols == null) 
				rootSymbols = new HashSet<Symbol>(automaton.getRootSymbols());
			else
				rootSymbols.retainAll(automaton.getRootSymbols());
		}

		this.result = new AnnotatedNFATypeAutomaton();
		State initial = new State();
		this.result.setInitialState(initial);
		
		for (Symbol symbol: rootSymbols) {
			namespace = QualifiedName.getNamespaceFromFQN(symbol.toString());
			this.result.addSymbol(symbol);
			Set<StateWrapper> targetStates = new HashSet<StateWrapper>();
			for (StateWrapper state: states) {
				StateWrapper targetState = state.getNextState(symbol);
				targetStates.add(targetState);
			}
			
			State targetState = this.intersectStates(targetStates);
			this.result.addTransition(symbol, initial, targetState);
		}
		return result;
	}

	private State intersectStates(Set<StateWrapper> states) {
		if (stateMap.containsKey(states))
			return stateMap.get(states);
		
		State resultState = new State();
		this.result.addState(resultState);
		stateMap.put(states, resultState);
		
		Set<Type> types = new HashSet<Type>();
		for (StateWrapper state: states)
			types.add(state.getType());

		Set<Symbol> usedSymbols = null;
		Collection<AttributeParticle> attributeParticles = new HashSet<AttributeParticle>(); 
		
		boolean complex = true;
		boolean complexContent = true;
		boolean simple = true;

		for (Type type: types) {
			if (type instanceof SimpleType) {
				complex = false;
				complexContent = false;
			} else if (type instanceof ComplexType) {
				ComplexType complexType = (ComplexType) type;
				if (complexType.getContent() instanceof ComplexContentType) {
					if (!complexType.getMixed())
						simple = false;
					ComplexContentType content = (ComplexContentType) complexType.getContent();
					if (usedSymbols == null) 
						usedSymbols = new HashSet<Symbol>(getUsedSymbols(content.getParticle()));
					else
						usedSymbols.retainAll(getUsedSymbols(content.getParticle()));
				} else
					complexContent = false;
			}
		}

		Set<QualifiedName> names = new HashSet<QualifiedName>();
		for (Type type: types)
			if (type != null && type.getName() != null) {
				names.add(type.getName());
			}
		
		QualifiedName typename = typenameGenerator.generateTypename(namespace, names);
		
		this.result.setTypeName(resultState, typename);

		
		for (Symbol symbol: usedSymbols) {
			this.result.addSymbol(symbol);
			Set<StateWrapper> targetStates = new HashSet<StateWrapper>();
			for (StateWrapper state: states) {
				StateWrapper targetState = state.getNextState(symbol);
				if (targetState!=null)
					targetStates.add(targetState);
			}
			
			State targetState = this.intersectStates(targetStates);
			this.result.addTransition(symbol, resultState, targetState);
		}

		Type type=null;
		
		if (complexContent) {
			type = intersectComplexTypes(types, resultState);
			((ComplexType) type).setMixed(simple);
		} 
		
		if (complex) {
			// intersectAttributes();
		} else if (simple) {
			// intersectSimpleTypes(types);
		}
		
		if (type != null)
			result.setType(typename, type);

		return resultState;
	}

	private Type intersectComplexTypes(Set<Type> complexTypes, State resultState) {
		System.err.println("Unite complex types: " + complexTypes);
		Type2ContentAutomatonConverter converter = new Type2ContentAutomatonConverter();
		Vector<ContentAutomaton> automata = new Vector<ContentAutomaton>(complexTypes.size());
		for (Type type: complexTypes) {
			ContentAutomaton automaton = converter.convertType(type);
			automata.add(automaton);
		}
		
		ModifiableStateDFA productAutomaton;
		productAutomaton = ProductNFAFactory.intersection(automata.toArray(new ContentAutomaton[0]));
		Minimizer minimizer = new NFAMinimizer();
		minimizer.minimize(productAutomaton);

		
		ContentAutomaton2TypeConverter backConverter = new ContentAutomaton2TypeConverter(result); 
		Type resultType = backConverter.convertContentAutomaton(productAutomaton, result.getTypeName(resultState), resultState);
		
		return resultType;
	}

	
}
