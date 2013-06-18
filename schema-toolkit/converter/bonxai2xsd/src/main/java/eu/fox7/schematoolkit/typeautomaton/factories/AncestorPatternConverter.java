/**
 * Copyright 2009-2012 TU Dortmund
 *
 * This file is part of FoXLib.
 *
 * FoXLib is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FoXLib is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with BonXai.  If not, see <http://www.gnu.org/licenses/>.
 */

package eu.fox7.schematoolkit.typeautomaton.factories;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import eu.fox7.flt.automata.NoSuchSymbolException;
import eu.fox7.flt.automata.NoSuchTransitionException;
import eu.fox7.flt.automata.NotDFAException;
import eu.fox7.flt.automata.factories.sparse.Determinizer;
import eu.fox7.flt.automata.factories.sparse.ProductDFAFactory;
import eu.fox7.flt.automata.impl.sparse.AnnotatedSparseNFA;
import eu.fox7.flt.automata.impl.sparse.ModifiableAnnotatedStateNFA;
import eu.fox7.flt.automata.impl.sparse.ModifiableStateNFA;
import eu.fox7.flt.automata.impl.sparse.SparseNFA;
import eu.fox7.flt.automata.impl.sparse.State;
import eu.fox7.flt.automata.impl.sparse.Symbol;
import eu.fox7.flt.automata.impl.sparse.Transition;
import eu.fox7.schematoolkit.bonxai.om.AncestorPattern;
import eu.fox7.schematoolkit.bonxai.om.AncestorPatternElement;
import eu.fox7.schematoolkit.bonxai.om.AncestorPatternWildcard;
import eu.fox7.schematoolkit.bonxai.om.Bonxai;
import eu.fox7.schematoolkit.bonxai.om.CardinalityParticle;
import eu.fox7.schematoolkit.bonxai.om.DoubleSlashPrefixedContainer;
import eu.fox7.schematoolkit.bonxai.om.Expression;
import eu.fox7.schematoolkit.bonxai.om.OrExpression;
import eu.fox7.schematoolkit.bonxai.om.SequenceExpression;
import eu.fox7.schematoolkit.common.QualifiedName;

public class AncestorPatternConverter {
//	private class ProductEquivalenceCondition implements StateCondition {
//    protected AnnotatedStateNFA<Integer, Object> nfa1, nfa2;
//    protected Set<Symbol> alphabet;
//
//    @SuppressWarnings("unchecked")
//	public void setNFAs(StateNFA nfa1, StateNFA nfa2) {
//        this.nfa1 = (AnnotatedStateNFA<Integer,Object>) nfa1;
//        this.nfa2 = (AnnotatedStateNFA<Integer,Object>) nfa2;
//        this.alphabet = new HashSet<Symbol>();
//        this.alphabet.addAll(nfa1.getSymbols());
//        this.alphabet.addAll(nfa2.getSymbols());
//    }
//
//    public boolean satisfy(State fromState1, State fromState2) {
//        if ((nfa1.isFinalState(fromState1) && !nfa2.isFinalState(fromState2)) ||
//                (!nfa1.isFinalState(fromState1) && nfa2.isFinalState(fromState2)))
//            return false;
//        if ((nfa1.isFinalState(fromState1) && nfa2.isFinalState(fromState2)) &&
//        		(! nfa1.getAnnotation(fromState1).equals(nfa2.getAnnotation(fromState2))))
//        	return false;
//        
//        for (Symbol symbol : alphabet) {
//            try {
//                State toState1 = ((StateDFA) nfa1).getNextState(symbol, fromState1);
//                State toState2 = ((StateDFA) nfa2).getNextState(symbol, fromState2);
//                if ((toState1 == null && toState2 != null) ||
//                        (toState1 != null && toState2 == null))
//                    return false;
//            } catch (NotDFAException e) {
//                throw new RuntimeException(e);
//            }
//        }
//        return true;
//    }
//
//}
//
//private class ProductEquivalenceRelation extends NerodeEquivalenceRelation {
//	public ProductEquivalenceRelation(StateNFA nfa) {
//        this.nfa = (StateDFA) nfa;
//		this.simulator = new Simulator(new ProductEquivalenceCondition());
//    }
//}
//
//private class ProductMinimizer extends AbstractMinimizer {
//    protected void initRelation(ModifiableStateNFA nfa) {
//        relation = new ProductEquivalenceRelation(nfa);
//    }
//}
	
	public static final Symbol WILDCARD = Symbol.create("wildcard");
	
	public static ModifiableAnnotatedStateNFA<Expression, ?> convertAncestorExpressions(Bonxai bonxai) {
		Vector<Expression> expressions = new Vector<Expression>(bonxai.getExpressions());
		List<QualifiedName> rootElements = bonxai.getRootElementNames();
		
		Set<Symbol> rootSymbols = new HashSet<Symbol>();
		for (QualifiedName rootElement: rootElements) {
			rootSymbols.add(Symbol.create(rootElement.getFullyQualifiedName()));
		}
		
		Set<Symbol> rightHandSymbols = new HashSet<Symbol>();
		rightHandSymbols.addAll(rootSymbols);
		
		// for each rule convert the ancestor expression and extract the alphabet symbols
		SparseNFA[] nfas = new SparseNFA[expressions.size()];
		Iterator<Expression> it = expressions.iterator();
		int i=0;
		while(it.hasNext()) {
			Expression expression = it.next();
			nfas[i] = AncestorPatternConverter.convertAncestorExpression(expression);
			rightHandSymbols.addAll(ChildSymbolExtractor.getChildSymbols(expression.getChildPattern(), bonxai));
			++i;
		}

		System.err.println("UsedSymbols: " + rightHandSymbols);
		// remove wildcards
		removeWildcards(rightHandSymbols, nfas);

		ModifiableAnnotatedStateNFA<Expression, ?> productDFA = constructProductAutomaton(expressions, nfas);

		// minimize product automaton
//		ProductMinimizer minimizer = new ProductMinimizer();
//		minimizer.initRelation(productDFA);
//		minimizer.minimize(productDFA);
//
//		System.err.println("Product automaton after minimization: " + productDFA);

		return productDFA;
		
	}
	
	public static SparseNFA convertAncestorExpression(Expression ex) {
		SparseNFA nfa = new SparseNFA();
		State initialState =  new State();
		State finalState =  new State();
		nfa.addState(initialState);
		nfa.addState(finalState);
		nfa.setInitialState(initialState);
		nfa.setFinalState(finalState);
		
		AncestorPattern ancestorPatternParticle = ex.getAncestorPattern();
		convertAncestorPatternParticle(ancestorPatternParticle, nfa, initialState, finalState);
		
		System.err.println("Expression: "+ ex.getAncestorPattern());
		System.err.println("NFA: "+ nfa);
		return nfa;
	}
	
	private static void convertAncestorPatternParticle(AncestorPattern ancestorPatternParticle, ModifiableStateNFA nfa, State initialState, State finalState) {
		if (ancestorPatternParticle instanceof CardinalityParticle) {
			CardinalityParticle cardinalityParticle = (CardinalityParticle) ancestorPatternParticle;
			AncestorPattern childParticle = cardinalityParticle.getChild();
			if (cardinalityParticle.getMin().equals(1) && cardinalityParticle.getMax().equals(1)) { // just one occurence
				convertAncestorPatternParticle(childParticle, nfa, initialState,finalState);
			} else if (cardinalityParticle.getMin().equals(1) && cardinalityParticle.getMax()==null) { // +
				State state = new State();
				nfa.addState(state);
				nfa.addTransition(Symbol.getEpsilon(),  state, finalState);// adding an epsilon transition from pre final to final state
				nfa.addTransition(Symbol.getEpsilon(),  state, initialState); //adding an epsilon transition from pre final to initial state
				convertAncestorPatternParticle(childParticle, nfa,initialState, state);//
			} else if (cardinalityParticle.getMin().equals(0) && cardinalityParticle.getMax()==null) { // *
				State state = new State();
				nfa.addState(state);
				nfa.addTransition(Symbol.getEpsilon(),  state, finalState);// adding an epsilon transition from pre final to final state
				nfa.addTransition(Symbol.getEpsilon(),  state, initialState); //adding an epsilon transition from pre final to initial state
				nfa.addTransition(Symbol.getEpsilon(),   initialState,finalState); //adding an epsilon transition from initial to final  state
				convertAncestorPatternParticle(childParticle, nfa,initialState, state);//
			} else if (cardinalityParticle.getMin().equals(0) && cardinalityParticle.getMax().equals(1)) { // ?
				State state = new State();
				nfa.addState(state);
				nfa.addTransition(Symbol.getEpsilon(), initialState,finalState); //adding an epsilon transition from initial to final  state
				nfa.addTransition(Symbol.getEpsilon(),  state, finalState); //adding an epsilon transition from pre final to final state
				convertAncestorPatternParticle(childParticle, nfa,initialState, state);
			} else {
				throw new RuntimeException("Invalid cardinalities in cardinality particle.");
			}
		} else if (ancestorPatternParticle instanceof OrExpression) {
			OrExpression orExpression = (OrExpression) ancestorPatternParticle;
			List<AncestorPattern> childParticles = orExpression.getChildren();
			for (AncestorPattern childParticle: childParticles) {
				State state = new State();
				nfa.addState(state);
				nfa.addTransition(Symbol.getEpsilon(), initialState, state);
				convertAncestorPatternParticle(childParticle, nfa, state, finalState);
			}
		} else if (ancestorPatternParticle instanceof SequenceExpression) {  // concatenation (a_1/a_2/.../a_n)
			SequenceExpression sequenceExpression = (SequenceExpression) ancestorPatternParticle;
			List<AncestorPattern> childParticles = sequenceExpression.getChildren();
			State state1 = initialState;
			for (AncestorPattern childParticle: childParticles) {
				State state2 = new State();
				nfa.addState(state2);
				convertAncestorPatternParticle(childParticle, nfa, state1, state2);
				state1 = state2;
			}
			nfa.addTransition(Symbol.getEpsilon(), state1, finalState);
		} else if (ancestorPatternParticle instanceof AncestorPatternElement) { // single label
			AncestorPatternElement ancestorPatternElement =  (AncestorPatternElement) ancestorPatternParticle;
			Symbol symbol = Symbol.create(ancestorPatternElement.getName().getFullyQualifiedName());
			nfa.addSymbol(symbol);
			nfa.addTransition(symbol, initialState, finalState);
		} else if (ancestorPatternParticle instanceof DoubleSlashPrefixedContainer) { // expression prefixed with // (use wildcard as Symbol for transition)
			DoubleSlashPrefixedContainer doubleSlashPrefixedContainer =  (DoubleSlashPrefixedContainer) ancestorPatternParticle;
			nfa.addTransition(WILDCARD, initialState, initialState);
			nfa.addSymbol(WILDCARD);
			AncestorPattern childParticle = doubleSlashPrefixedContainer.getChild();
			convertAncestorPatternParticle(childParticle, nfa, initialState, finalState);
		} else if (ancestorPatternParticle instanceof AncestorPatternWildcard) { // wildcard (*)
			nfa.addTransition(WILDCARD, initialState, finalState);
		} else {
			throw new RuntimeException("Cannot convert AncestorPattern of class "+ancestorPatternParticle.getClass());
		}
	}
	
	
	private static void removeWildcards(Set<Symbol> usedSymbols, SparseNFA...nfas) {
		for (SparseNFA nfa: nfas) {
			Set<Transition> transitions = nfa.getTransitionMap().getTransitions();
			
			if (nfa.hasSymbol(WILDCARD)) {
				nfa.addSymbols(usedSymbols);
				for (Transition transition: transitions) 
					if (transition.getSymbol() == WILDCARD ) {
						try {
							nfa.removeTransition(transition.getSymbol(), transition.getFromState(), transition.getToState());
						} catch (NoSuchTransitionException e) {
							throw new RuntimeException(e);
						}
						for (Symbol symbol: usedSymbols) 
							nfa.addTransition(symbol, transition.getFromState(), transition.getToState());
					}
				try {
					nfa.removeSymbol(WILDCARD);
				} catch (NoSuchSymbolException e) {
					throw new RuntimeException("No such symbol: "+WILDCARD);
				}
			}
		}
	}
	
	
	private static ModifiableAnnotatedStateNFA<Expression, ?> constructProductAutomaton(Vector<Expression> expressions, SparseNFA...nfas) {
		AnnotatedSparseNFA<Expression,?> dfa = new AnnotatedSparseNFA<Expression,Object>();
		SparseNFA[] dfas = new SparseNFA[nfas.length];

		for (int i=0; i<nfas.length; i++) {
			dfas[i] = Determinizer.dfa(nfas[i]);
			System.err.println("DFA "+i+": "+dfas[i]);
		}
		
		try {
			ProductDFAFactory.product(dfa, dfas);
		} catch (NotDFAException e) {
			throw new RuntimeException(e);
		}

//		System.err.println("productDFA: "+dfa);

		
		for (State state: dfa.getStates()) {
			Vector<String> stateNames = new Vector<String>();
			String stateName = dfa.getStateValue(state);
			int brackets=0;
			int start=1;
			int end=0;
			for (int i=0; i< stateName.length(); i++) {
//				System.err.println("Pos: "+i+" Char: "+stateName.charAt(i)+" Brackets: "+brackets);
				switch (stateName.charAt(i)) {
				case '[': brackets++; break;
				case ']': brackets--; if (brackets==0) {
					end=i;
//					System.err.println("Start: "+start+" End: "+end);
					stateNames.add(stateName.substring(start, end));
				} break;
				case ',': if (brackets==1) {
					end=i;
//					System.err.println("Start: "+start+" End: "+end);
					stateNames.add(stateName.substring(start, end));
				} break;
				case ' ': if (brackets==1) start=i+1;
				}
			}

//			System.err.println("stateName: "+stateName);
//			System.err.println("stateNames: "+stateNames);

			if (stateNames.size() != nfas.length) {
				throw new RuntimeException("Number of states unequal number of automata in product construction");
			}
				
			boolean isFinal=false;
			int j=nfas.length - 1;
			while ((!isFinal) && (j>=0)) {
				if (dfas[j].isFinalState(stateNames.get(j))) {
					isFinal=true;
					dfa.addFinalState(state);
					dfa.annotate(state, expressions.get(j));
				}
				--j;
			}
		}
		return dfa;
	}


}
