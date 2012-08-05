package eu.fox7.schematoolkit.typeautomaton.operations;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import eu.fox7.flt.automata.NotDFAException;
import eu.fox7.flt.automata.converters.Minimizer;
import eu.fox7.flt.automata.converters.NFAMinimizer;
import eu.fox7.flt.automata.factories.sparse.ProductDFAFactory;
import eu.fox7.flt.automata.impl.sparse.ModifiableStateDFA;
import eu.fox7.flt.automata.impl.sparse.State;
import eu.fox7.flt.automata.impl.sparse.StateNFA;
import eu.fox7.flt.automata.impl.sparse.Symbol;
import eu.fox7.flt.treeautomata.impl.ContentAutomaton;
import eu.fox7.schematoolkit.common.AttributeParticle;
import eu.fox7.schematoolkit.common.QualifiedName;
import eu.fox7.schematoolkit.typeautomaton.AnnotatedNFATypeAutomaton;
import eu.fox7.schematoolkit.typeautomaton.TypeAutomaton;
import eu.fox7.schematoolkit.typeautomaton.operations.Operation;
import eu.fox7.schematoolkit.xsd.om.ComplexContentType;
import eu.fox7.schematoolkit.xsd.om.ComplexType;
import eu.fox7.schematoolkit.xsd.om.SimpleType;
import eu.fox7.schematoolkit.xsd.om.Type;
import eu.fox7.schematoolkit.xsd.om.TypeReference;
import eu.fox7.treeautomata.converter.ContentAutomaton2TypeConverter;
import eu.fox7.treeautomata.converter.Type2ContentAutomatonConverter;
//import eu.fox7.upafixer.UPAFixer;
//import eu.fox7.upafixer.impl.BKWUPAFixer;

public class TypeAutomatonUnion extends Operation {

	private TypeAutomaton result;
	private HashMap<Set<StateWrapper>,State> stateMap = new HashMap<Set<StateWrapper>,State>(); 
	private TypenameGenerator typenameGenerator = new SimpleTypenameGenerator();
	private String namespace;
//	private boolean fixUPA = true;
	
	public TypeAutomatonUnion() {
	}
	
//	public TypeAutomatonUnion(boolean fixUPA) {
//		this.fixUPA = fixUPA;
//	}
//	
//	public void setFixUPA(boolean fixUPA) {
//		this.fixUPA = fixUPA;
//	}
	
	@Override
	public TypeAutomaton apply(Collection<TypeAutomaton> input) {
		Set<StateWrapper> states = new HashSet<StateWrapper>();
		Set<Symbol> rootSymbols = new HashSet<Symbol>();
		for (TypeAutomaton automaton: input) {
			states.add(new StateWrapper(automaton, automaton.getInitialState()));
			rootSymbols.addAll(automaton.getRootSymbols());
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
				if (targetState!=null)
					targetStates.add(targetState);
			}
			
			State targetState = this.uniteStates(targetStates);
			this.result.addTransition(symbol, initial, targetState);
		}
		
//		if (fixUPA) {
//			UPAFixer upaFixer = new BKWUPAFixer();
//			upaFixer.fixUPA(result);
//		}
		
		return result;
	}
	
	private State uniteStates(Set<StateWrapper> states) {
		System.err.println("Unite states " + states);
		if (stateMap.containsKey(states))
			return stateMap.get(states);
		
		State resultState = new State();
		this.result.addState(resultState);
		stateMap.put(states, resultState);
		
		Set<Type> types = new HashSet<Type>();
		for (StateWrapper state: states) {
			Type type = state.getType();
			System.err.println(type);
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
			
			State targetState = this.uniteStates(targetStates);
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
		
		if (type != null)
			result.setType(typename, type);

		return resultState;
	}

	private void uniteSimpleTypes(Set<Type> types) {
		// TODO Auto-generated method stub
	}

	private Type uniteComplexTypes(Set<ComplexType> complexTypes, State resultState) {
		System.err.println("Unite complex types: " + complexTypes);
		Type2ContentAutomatonConverter converter = new Type2ContentAutomatonConverter();
		Vector<StateNFA> automata = new Vector<StateNFA>(complexTypes.size());
		for (ComplexType type: complexTypes) {
			StateNFA automaton = converter.convertType(type);
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
