package eu.fox7.schematoolkit.typeautomaton.factories;


import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import org.apache.commons.collections15.BidiMap;
import org.apache.commons.collections15.bidimap.DualHashBidiMap;

import eu.fox7.flt.automata.NotDFAException;
import eu.fox7.flt.automata.impl.sparse.ModifiableAnnotatedStateNFA;
import eu.fox7.flt.automata.impl.sparse.State;
import eu.fox7.flt.automata.impl.sparse.StateDFA;
import eu.fox7.flt.automata.impl.sparse.Symbol;
import eu.fox7.schematoolkit.bonxai.om.AncestorPattern;
import eu.fox7.schematoolkit.bonxai.om.Bonxai;
import eu.fox7.schematoolkit.bonxai.om.ChildPattern;
import eu.fox7.schematoolkit.bonxai.om.ElementPattern;
import eu.fox7.schematoolkit.bonxai.om.Expression;
import eu.fox7.schematoolkit.common.ElementProperties;
import eu.fox7.schematoolkit.common.QualifiedName;
import eu.fox7.schematoolkit.converter.bonxai2xsd.ChildPatternConverter;
import eu.fox7.schematoolkit.typeautomaton.AnnotatedNFATypeAutomaton;
import eu.fox7.schematoolkit.typeautomaton.TypeAutomaton;
import eu.fox7.schematoolkit.xsd.om.Type;
import eu.fox7.schematoolkit.xsd.om.XSDSchema;

public class BonxaiTypeAutomatonConstruction {
	/**
	 * @return the typeAutomaton from the last conversion
	 */
	public TypeAutomaton getTypeAutomaton() {
		return typeAutomaton;
	}

	/**
	 * @return the stateExpressionMap from the last conversion
	 */
	public Map<State, Expression> getStateExpressionMap() {
		return stateExpressionMap;
	}

	/**
	 * @return the expressionStateMap from the last conversion
	 */
	public Map<Expression, Set<State>> getExpressionStateMap() {
		return expressionStateMap;
	}



	/**
	 * Returned type automaton
	 */
	private TypeAutomaton typeAutomaton;
	
	/**
	 * Bidirectional mapping between states of the type automaton and the product automaton
	 */
	private BidiMap<State,State> stateMap;
	
	/** 
	 * Mapping between states and expressions
	 */
	private Map<State, Expression> stateExpressionMap;
	
	/**
	 * Mapping between expression and states
	 */
	private Map<Expression, Set<State>> expressionStateMap;
	
	/**
	 * productAutomaton representing all ancestor patterns.
	 * final states are annotated with the matching ancestor pattern
	 */
	private ModifiableAnnotatedStateNFA<Expression,?> productDFA;
	
	/**
	 * ChildPatternConverter
	 */
	private ChildPatternConverter childPatternConverter;
	
	private Queue<State> workingQueue;
	private Bonxai bonxai;
//	private Vector<Expression> expressions;
	
	private TypeNameGenerator typeNameGenerator;

	public BonxaiTypeAutomatonConstruction() {
	}
	
	public TypeAutomaton constructTypeAutomaton(Bonxai bonxai, TypeNameGenerator typeNameGenerator, boolean eliminateGroups) {
		this.bonxai = bonxai;
		this.typeNameGenerator = typeNameGenerator;
		typeAutomaton = new AnnotatedNFATypeAutomaton();
		stateMap = new DualHashBidiMap<State,State>();
		expressionStateMap = new HashMap<Expression, Set<State>>();
		stateExpressionMap = new HashMap<State, Expression>();
		
		this.childPatternConverter = new ChildPatternConverter(typeAutomaton, bonxai.getTargetNamespace(), eliminateGroups);

//		expressions = new Vector<Expression>(bonxai.getExpressions());
//		List<QualifiedName> rootElements = bonxai.getRootElementNames();
		workingQueue = new LinkedList<State>();

		
		
		// construct product automaton
		productDFA = AncestorPatternConverter.convertAncestorExpressions(bonxai);
		
		System.err.println("Product automaton: " + productDFA);
		

		for (Symbol symbol: productDFA.getSymbols())
			typeAutomaton.addSymbol(symbol);
		
		//create initial state for TypeAutomaton
		State initialState = new State();
		typeAutomaton.addState(initialState);
		typeAutomaton.setInitialState(initialState);
		stateMap.put(initialState, productDFA.getInitialState());

		//insert root elements
		for (QualifiedName rootElement: bonxai.getRootElementNames()) {
			insertChild(initialState, productDFA.getInitialState(), Symbol.create(rootElement.getFullyQualifiedName()));
		}
		
		// build type automaton until working queue is empty
		while (! workingQueue.isEmpty()) {
			State state = workingQueue.remove();
			State productState = stateMap.get(state);
			if (productState!=null) {
				Expression matchingExpression = productDFA.getAnnotation(productState);
				
				this.mapStateToExpression(state, matchingExpression);
			
				Set<Symbol> children = ChildSymbolExtractor.getChildSymbols(matchingExpression.getChildPattern(), bonxai);
				for (Symbol symbol: children) {
					insertChild(state, productState, symbol);
				}
			} else {
				System.err.println("No product state for state "+state.toString());
			}
		}

		//add ElementProperties
		for (State state: typeAutomaton.getStates()) {
			if (! typeAutomaton.isInitialState(state)) {
				State productState = stateMap.get(state);
				if (productState!=null) {
					Expression matchingExpression = productDFA.getAnnotation(productState);
					ChildPattern childPattern = matchingExpression.getChildPattern();

					ElementProperties elementProperties = new ElementProperties();
					ElementPattern elementPattern = childPattern.getElementPattern();
					if (elementPattern != null && elementPattern.getBonxaiType()!=null) {
						elementProperties.setDefaultValue(elementPattern.getBonxaiType().getDefaultValue());
						elementProperties.setFixedValue(elementPattern.getBonxaiType().getFixedValue());
						elementProperties.setNillable(elementPattern.isMissing());
					
						typeAutomaton.setElementProperties(state, elementProperties);
					}
				} else {
					System.err.println("No product state for state "+state.toString());
				}
			}
		}
		
		
		//add type information
		for (State state: typeAutomaton.getStates()) {
			if (! typeAutomaton.isInitialState(state)) {
				State productState = stateMap.get(state);
				if (productState!=null) {
					Expression matchingExpression = productDFA.getAnnotation(productState);
					ChildPattern childPattern = matchingExpression.getChildPattern();
					QualifiedName typename = typeAutomaton.getTypeName(state);
					Type type = childPatternConverter.convertChildPattern(childPattern, typename, state);
					if (type != null) 
						typeAutomaton.setType(typename, type);
				} else {
					System.err.println("No product state for state "+state.toString());
				}
			}
		}
		return typeAutomaton;
	}
	
	private void mapStateToExpression(State state, Expression expression) {
		this.stateExpressionMap.put(state, expression);
		Set<State> states = this.expressionStateMap.get(expression);
		if (states == null) {
			states = new HashSet<State>();
			this.expressionStateMap.put(expression, states);
		}
		states.add(state);
	}

	private void insertChild(State state, State productState, Symbol symbol) {
		State newState;
		State newProductState;
		try {
			newProductState = ((StateDFA) productDFA).getNextState(symbol, productState);
//			if (newProductState == null) {
//				throw new RuntimeException("No product state found.");
//			}
		} catch (NotDFAException e) {
			throw new RuntimeException(e);
		}
		
		if (! stateMap.containsValue(newProductState)) {
			newState = new State();
			typeAutomaton.addState(newState);
			Expression expression = productDFA.getAnnotation(newProductState);
			AncestorPattern ap = expression.getAncestorPattern();
			ChildPattern cp = expression.getChildPattern();
			QualifiedName typename;
			if (cp.getElementPattern().getBonxaiType()!=null)
				typename = cp.getElementPattern().getBonxaiType().getTypename();
			else
				typename = typeNameGenerator.generateTypeName(ap);
			typeAutomaton.setTypeName(newState, typename);
			stateMap.put(newState, newProductState);
			workingQueue.add(newState);
		} else {
			newState = stateMap.getKey(newProductState);
		}
		typeAutomaton.addTransition(symbol, state, newState);
	}

	
	public void convertGroups(XSDSchema xsdSchema) {
		this.childPatternConverter.convertGroups(bonxai, xsdSchema);
	}

}
