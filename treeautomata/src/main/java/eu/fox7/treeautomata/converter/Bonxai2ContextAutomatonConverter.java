package eu.fox7.treeautomata.converter;

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
import eu.fox7.flt.automata.impl.sparse.StateNFA;
import eu.fox7.flt.automata.impl.sparse.Symbol;
import eu.fox7.schematoolkit.bonxai.om.Bonxai;
import eu.fox7.schematoolkit.bonxai.om.ChildPattern;
import eu.fox7.schematoolkit.common.Element;
import eu.fox7.schematoolkit.bonxai.om.Expression;
import eu.fox7.schematoolkit.common.QualifiedName;
import eu.fox7.schematoolkit.typeautomaton.factories.AncestorPatternConverter;
import eu.fox7.schematoolkit.typeautomaton.factories.ChildSymbolExtractor;

public class Bonxai2ContextAutomatonConverter {
	private ExtendedContextAutomaton eca;
	
	private Queue<State> workingQueue;
	/**
	 * Bidirectional mapping between states of the type automaton and the product automaton
	 */
	private BidiMap<State,State> stateMap;
	
	/**
	 * @return the stateExpressionMap
	 */
	public Map<State, Expression> getStateExpressionMap() {
		return stateExpressionMap;
	}

	/**
	 * @return the expressionStateMap
	 */
	public Map<Expression, Set<State>> getExpressionStateMap() {
		return expressionStateMap;
	}


	/** 
	 * Mapping between states and expressions
	 */
	private Map<State, Expression> stateExpressionMap;
	
	/**
	 * Mapping between expression and states
	 */
	private Map<Expression, Set<State>> expressionStateMap;
	
	/**
	 * Mpping between bonxai elements and horizontal states
	 */
	private BidiMap<Element,State> elementStateMap;

	/**
	 * productAutomaton representing all ancestor patterns.
	 * final states are annotated with the matching ancestor pattern
	 */
	private ModifiableAnnotatedStateNFA<Expression,?> productDFA;
	
	
	private ChildPattern2ContentAutomatonConverter childPatternConverter;
	
	public ExtendedContextAutomaton convert(Bonxai bonxai) {
		eca = new ExtendedContextAutomaton();

		stateMap = new DualHashBidiMap<State,State>();
		expressionStateMap = new HashMap<Expression, Set<State>>();
		stateExpressionMap = new HashMap<State, Expression>();
		elementStateMap = new DualHashBidiMap<Element,State>();
		
		this.childPatternConverter = new ChildPattern2ContentAutomatonConverter();

//		expressions = new Vector<Expression>(bonxai.getExpressions());
//		List<QualifiedName> rootElements = bonxai.getRootElementNames();
		workingQueue = new LinkedList<State>();

		
		
		// construct product automaton
		productDFA = AncestorPatternConverter.convertAncestorExpressions(bonxai);
		
//		System.err.println("Product automaton: " + productDFA);
		

		for (Symbol symbol: productDFA.getSymbols())
			eca.addSymbol(symbol);
		
		//create initial state for TypeAutomaton
		State initialState = new State();
		eca.addState(initialState);
		eca.setInitialState(initialState);
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

//		//add ElementProperties
//		for (State state: eca.getStates()) {
//			if (! eca.isInitialState(state)) {
//				State productState = stateMap.get(state);
//				if (productState!=null) {
//					Expression matchingExpression = productDFA.getAnnotation(productState);
//					ChildPattern childPattern = matchingExpression.getChildPattern();
//
//					ElementProperties elementProperties = new ElementProperties();
//					ElementPattern elementPattern = childPattern.getElementPattern();
//					if (elementPattern != null && elementPattern.getBonxaiType()!=null) {
//						elementProperties.setDefaultValue(elementPattern.getBonxaiType().getDefaultValue());
//						elementProperties.setFixedValue(elementPattern.getBonxaiType().getFixedValue());
//						elementProperties.setNillable(elementPattern.isMissing());
//					
//						eca.setElementProperties(state, elementProperties);
//					}
//				} else {
//					System.err.println("No product state for state "+state.toString());
//				}
//			}
//		}
		
		
		//add type information
		for (State state: eca.getStates()) {
			if (! eca.isInitialState(state)) {
				State productState = stateMap.get(state);
				if (productState!=null) {
					Expression matchingExpression = productDFA.getAnnotation(productState);
					ChildPattern childPattern = matchingExpression.getChildPattern();
					StateNFA contentAutomaton = childPatternConverter.convertChildPattern(childPattern);
					eca.setContentAutomaton(state, contentAutomaton);
//					this.elementStateMap.putAll(childPatternConverter.getElementStateMap());
				} else {
					System.err.println("No product state for state "+state.toString());
				}
			}
		}
		

		
		return eca;
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
			eca.addState(newState);
//			Expression expression = productDFA.getAnnotation(newProductState);
//			AncestorPattern ap = expression.getAncestorPattern();
//			ChildPattern cp = expression.getChildPattern();
//			QualifiedName typename;
//			if (cp.getElementPattern().getBonxaiType()!=null)
//				typename = cp.getElementPattern().getBonxaiType().getTypename();
//			else
//				typename = typeNameGenerator.generateTypeName(ap);
//			typeAutomaton.setTypeName(newState, typename);
			stateMap.put(newState, newProductState);
			workingQueue.add(newState);
		} else {
			newState = stateMap.getKey(newProductState);
		}
		eca.addTransition(symbol, state, newState);
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
}
