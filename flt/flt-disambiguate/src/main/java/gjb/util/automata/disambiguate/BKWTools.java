package gjb.util.automata.disambiguate;

import gjb.flt.automata.FeatureNotSupportedException;
import gjb.flt.automata.NoSuchStateException;
import gjb.flt.regex.Glushkov;
import gjb.flt.automata.converters.NFAMinimizer;
import gjb.flt.automata.converters.Simplifier;
import gjb.flt.automata.factories.sparse.Determinizer;
import gjb.flt.automata.impl.sparse.SparseNFA;
import gjb.flt.automata.NoSuchTransitionException;
import gjb.flt.automata.impl.sparse.State;
import gjb.flt.automata.impl.sparse.Transition;
import gjb.flt.regex.Regex;
import gjb.flt.regex.UnknownOperatorException;
import gjb.flt.regex.generators.LanguageGenerator;
import gjb.util.tree.Node;
import gjb.util.tree.SExpressionParseException;
import gjb.util.tree.Tree;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.apache.commons.collections15.BidiMap;


public class BKWTools {

	/*static void makeSymbolConsistent(NFA nfa, String symbol,
			String toState) {
		for(String state : finalStateValues(nfa))
			if(nfa.getNextStateValues(symbol, state).isEmpty())
				nfa.addTransition(symbol, state, toState);
	}*/

	public static void contractStates(SparseNFA nfa, Set<String> contract){
	}

	public static Set<HashSet<String>> computeContractibleStates(SparseNFA nfa){
		Set<HashSet<String>> contract = new HashSet<HashSet<String>>();

		for(String symbol : nfa.getAlphabet().keySet()){
			HashSet<String> reachableStates = new HashSet<String>();
			boolean canContract = true;
			for(String state : finalStateValues(nfa)){
				Set<String> nextStates = nfa.getNextStateValues(symbol, state);
				if(nextStates.isEmpty())
					canContract = false;
				else{
					String toState = gjb.util.Collections.extractSingleton(nextStates);
					reachableStates.add(toState);
				}
				if(canContract)
					contract.add(reachableStates);
			}
		}

		return contract;
	}

	public static Set<String> computeCandidateSConsistentSymbols(SparseNFA nfa) {
		Set<String> candidates = new HashSet<String>();
		Set<String> getFinalStates = finalStateValues(nfa);

		for(String state : getFinalStates){
			Set<Transition> outgoingTransitions = nfa.getOutgoingTransitions(nfa.getState(state));
			for(Transition transition : outgoingTransitions)
				candidates.add(transition.getSymbol().toString());
		}
		
		return candidates;

	}
	
	static Set<String> finalStateValues(SparseNFA nfa) {
		Set<String> finalStateValues = new HashSet<String>();

		for(State state : nfa.getFinalStates())
			finalStateValues.add(nfa.getStateValue(state));

		return finalStateValues;
	}

	/*private static void removeStateClean(NFA nfa, String state){
		nfa.removeState(state);

		if(nfa.getInitialState() != null){
		for(String s : nfa.getStateValues()){
			if(!reach(nfa,nfa.getStateValue(nfa.getInitialState()),s))
				nfa.removeState(s);
		}
		}
	}*/

	static Map<String, String> outgoingConnections(SparseNFA nfa,
			Set<String> states) {
		Map<String,String> mapOutgoingConnections = new HashMap<String,String>();
		Set<String> newFinalStates = new HashSet<String>();

		for(String state : states){
			Set<String> connectedStates = new HashSet<String>();
			for(String symbol : nfa.getAlphabet().keySet())
				connectedStates.addAll(nfa.getNextStateValues(symbol, state));
			if(!gjb.util.Collections.intersect(states, connectedStates).equals(connectedStates))
				newFinalStates.add(state);
		}

		if(newFinalStates.isEmpty()){
			return null;
		}
		else{
			String finalState = gjb.util.Collections.getOne(newFinalStates);
			for(String symbol : nfa.getAlphabet().keySet()){
				Set<String> nextStates = nfa.getNextStateValues(symbol, finalState);
				if(nextStates.size() == 1){
					String toState = gjb.util.Collections.extractSingleton(nextStates);
					if(!states.contains(toState)){
						mapOutgoingConnections.put(symbol, toState);
					}
				}
			}
		}

		return mapOutgoingConnections;
	}

	static SparseNFA createOrbitAutomaton(SparseNFA nfa, Set<String> scc, String getInitialState) {
		if(scc.isEmpty())
			return null;
		else{
			/*String newInitialState = nfa.getStateValue(nfa.getInitialState());
			if(!scc.contains(nfa.getStateValue(nfa.getInitialState()))){
				for(String state : scc){
					if(!scc.containsAll(stateValues(nfa,nfa.getPreviousStates(nfa.getState(state)))))
						newInitialState = state;
				}
			}*/
			Set<String> newFinalStates = new HashSet<String>();
			for(String state : scc){
				Set<String> connectedStates = new HashSet<String>();
				for(String symbol : nfa.getAlphabet().keySet())
					connectedStates.addAll(nfa.getNextStateValues(symbol, state));
				if(!gjb.util.Collections.intersect(scc, connectedStates).equals(connectedStates))
					newFinalStates.add(state);
			}

			SparseNFA orbit = new SparseNFA(nfa);
			Set<String> states = nfa.getStateValues();
			for(String state : states)
				if(!scc.contains(state))
					try {
						orbit.removeState(state);
					} catch (NoSuchStateException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

			if(getInitialState != null)
				orbit.setInitialState(getInitialState);
			else
				orbit.setInitialState(gjb.util.Collections.getOne(scc));

			for(String finalState : newFinalStates)
				orbit.addFinalState(finalState);

			Simplifier.simplify(orbit);
			return orbit;
		}
	}

	/*private static void orbitAutomaton(NFA nfa,
			Set<String> states) {
		Set<String> newFinalStates = new HashSet<String>();

		for(String state : states){
			Set<String> connectedStates = new HashSet<String>();
			for(String symbol : nfa.getAlphabet().keySet())
				connectedStates.addAll(nfa.getNextStateValues(symbol, state));
			if(!gjb.util.Collections.intersect(states, connectedStates).equals(connectedStates))
				newFinalStates.add(state);
		}

		Set<String> oldStates = new HashSet<String>(nfa.getStateValues());

		for(String state : oldStates)
			if(!states.contains(state))
				nfa.removeState(state);		

		for(String finalState : newFinalStates)
			nfa.addFinalState(finalState);
	}*/

	static Set<String> getScc(Set<HashSet<String>> sccs,
			String state) {
		for(Set<String> scc : sccs){
			if(scc.contains(state))
				return scc;
		}	
		return null;
	}




	public static Node createDisjunctionX(Set<Node> expressions){
		if(expressions.size() == 0)
			return null;
		else if(expressions.size() == 1)
			return gjb.util.Collections.extractSingleton(expressions);
		else{
			boolean first = true;
			Iterator<Node> expressionIterator = expressions.iterator();
			Node root = new Node(Regex.UNION_OPERATOR);
			Node lastDisjunction = root;
			while (expressionIterator.hasNext()) {
				Node expression = expressionIterator.next();
				if (!expressionIterator.hasNext())
					lastDisjunction.addChild(expression);
				else {
					if(!first){
						Node disjunction = new Node(Regex.UNION_OPERATOR);
						lastDisjunction.addChild(disjunction);
						lastDisjunction = disjunction;
					}
					lastDisjunction.addChild(expression);
					first = false;
				}
			}
			return root;
		}
	}
	


	static Node concatenate(String symbol, Tree re){
		Node root = new Node(Regex.CONCAT_OPERATOR);
		root.addChild(new Node(symbol));
		root.addChild(re.getRoot());
		return root;
	}

	static void setInitialStateClean(SparseNFA nfa, String state) {
		nfa.setInitialState(state);
		Simplifier.simplify(nfa);
	}

	public static boolean isUnambiguous(String regexString) throws SExpressionParseException, UnknownOperatorException, FeatureNotSupportedException{
//		Glushkov g = new Glushkov();
		SparseNFA nfa = new SparseNFA(new LanguageGenerator(regexString).getNFA());
		return isUnambiguous(nfa);
	}

	public static boolean isUnambiguous(SparseNFA nfa){
		SparseNFA dfa = new SparseNFA(nfa);
		dfa = Determinizer.dfa(dfa);
		new NFAMinimizer().minimize(dfa);
		//cleanNFA(dfa);

		return isUnambiguousInduction(dfa);
	}

	private static boolean isUnambiguousInduction(SparseNFA nfa){
		Simplifier.simplify(nfa);
		//Modification
		//Set<HashSet<String>> sccs = stronglyConnectedComponents(nfa);
		StronglyConnectedComponents sccCompute = new LinearStronglyConnectedComponents();
		Set<HashSet<String>> sccs = sccCompute.computeStronglyConnectedComponents(nfa,nfa.getStateValue(nfa.getInitialState()));

		Set<String> sConsistent = sConsistentSymbols(nfa);
		//removeFinalTransitions(nfa,sConsistent);
		if(isTrivial(nfa))
			return true;
		else if(sccs.size() == 1 && sConsistent.isEmpty())
			return false;
		else{
			removeFinalTransitions(nfa,sConsistent);
			//Modification
			//sccs = completeStronglyConnectedComponents(nfa);
			sccs = sccCompute.computeAllStronglyConnectedComponents(nfa);
			if(!hasOrbitProperty(nfa,sccs))
				return false;
			else{
				for(HashSet<String> scc : sccs){
					SparseNFA orbitAutomaton = createOrbitAutomaton(nfa,scc, null);
					if(!isUnambiguousInduction(orbitAutomaton))
						return false;
				}
				return true;
			}
		}
	}

	/*
	 * public static boolean isUnambiguousInduction(NFA nfa){		
		if(isTrivial(nfa))
			return true;
		else{

			Set<HashSet<String>> sccs = stronglyConnectedComponents(nfa);
			if(sccs.size() == 1){
				Set<String> sConsistent = sConsistentSymbols(nfa);
				if(sConsistent.isEmpty())
					return false;
				else{
					removeFinalTransitions(nfa,sConsistent);
					return(isUnambiguousInduction(nfa));
				}
			}
			else{
				if(!hasOrbitProperty(nfa,sccs))
					return false;
				else{
					for(HashSet<String> scc : sccs){
						NFA orbitAutomaton = createOrbitAutomaton(nfa,scc);
						if(!isUnambiguousInduction(orbitAutomaton))
							return false;
					}
					return true;
				}
			}
		}
	}
	 */

	static boolean hasOrbitProperty(SparseNFA nfa, Set<HashSet<String>> sccs) {
		Set<String> getAlphabet = nfa.getAlphabet().keySet();
		for(HashSet<String> scc : sccs){
			Set<String> gates = computeGates(nfa, scc);

			if (!gates.isEmpty()) {
				String firstState = gjb.util.Collections.getOne(gates);
				boolean areFinal = nfa.isFinalState(firstState);
				Map<String, String> toStates = new HashMap<String, String>();
				for (String symbol : getAlphabet) {
					if (!nfa.getNextStateValues(symbol, firstState).isEmpty()){
						String toState = gjb.util.Collections
						.extractSingleton(nfa.getNextStateValues(symbol,
								firstState));
						if(!scc.contains(toState))
							toStates.put(symbol, toState);
					}
				}

				for (String currentState : gates) {
					if (!areFinal == nfa.isFinalState(currentState))
						return false;
					for (String symbol : getAlphabet) {
						Set<String> nextStates = nfa.getNextStateValues(symbol,
								currentState);
						if (nextStates.isEmpty()
								&& toStates.containsKey(symbol))
							return false;
						else if (!nextStates.isEmpty()){
							String toState = gjb.util.Collections.extractSingleton(nextStates);
							if(scc.contains(toState) && toStates.containsKey(symbol))
								return false;
							else if(!scc.contains(toState) && (!toStates.containsKey(symbol) || !toStates.get(symbol).equals(toState)))
								return false;
						}
					}
				}
			}
		}	
		return true;
	}

	static Set<String> computeGates(SparseNFA nfa, HashSet<String> scc) {
		Set<String> gates = new HashSet<String>();
		for(String state : scc){
			Set<String> nextStates = stateValues(nfa,nfa.getNextStates(nfa.getState(state)));
			if(!scc.containsAll(nextStates) || nfa.isFinalState(state))
				gates.add(state);
		}
		return gates;
	}

	static void removeFinalTransitions(SparseNFA nfa, Set<String> consistent) {
		Set<State> getFinalStatesState = nfa.getFinalStates();
		Set<String> getFinalStates = new HashSet<String>();
		BidiMap<String,State> stateMap = nfa.getStateMap();

		for(State s : getFinalStatesState)
			getFinalStates.add(stateMap.getKey(s));

		for(String state : getFinalStates){
			for(String symbol : consistent){
				try {
					nfa.removeTransition(nfa.getAlphabet().get(symbol), nfa.getState(state), nfa.getState(gjb.util.Collections.extractSingleton(nfa.getNextStateValues(symbol, state))));
				} catch (NoSuchTransitionException e) {
					e.printStackTrace();
				}
			}
		}

	}

	public static boolean isTrivial(SparseNFA nfa){
		Simplifier.simplify(nfa);
		return nfa.getNumberOfTransitions() == 0;
	}

	public static Set<String> sConsistentSymbols(SparseNFA nfa){
		Set<State> getFinalStatesState = nfa.getFinalStates();
		Set<String> getFinalStates = new HashSet<String>();
		BidiMap<String,State> stateMap = nfa.getStateMap();
		Set<String> getAlphabet = nfa.getAlphabet().keySet();
		Set<String> sConsistent = new HashSet<String>();
		String targetState;
		String finalState;
		boolean mistakeFound = false;

		for(State s : getFinalStatesState)
			getFinalStates.add(stateMap.getKey(s));

		finalState = gjb.util.Collections.getOne(getFinalStates);
		if(!getFinalStates.isEmpty()){
			for(String symbol : getAlphabet){
				mistakeFound = false;
				if(!nfa.getNextStateValues(symbol,finalState).isEmpty()){
					targetState = gjb.util.Collections.extractSingleton(nfa.getNextStateValues(symbol,finalState));
					for(String state : getFinalStates){
						mistakeFound = mistakeFound || nfa.getNextStateValues(symbol,state).isEmpty() || !targetState.equals(gjb.util.Collections.extractSingleton(nfa.getNextStateValues(symbol,state)));
					}
					if(!mistakeFound)
						sConsistent.add(symbol);
				}
			}
		}  			

		return sConsistent;
	}


	public static Set<HashSet<String>> stronglyConnectedComponents(SparseNFA nfa){
		//cleanNFA(nfa);
		Set<HashSet<String>> scc = new HashSet<HashSet<String>>();
		Set<String> done  = new HashSet<String>();
		Set<String> states = reachableStates(nfa, nfa.getStateValue(nfa.getInitialState()));
		gjb.util.Collections.intersect(states, backwardReachableStates(nfa,finalStateValues(nfa)));

		for(String state : states){
			if(!done.contains(state)){
				HashSet<String> currentSCC = new HashSet<String>();
				currentSCC.add(state);
				done.add(state);
				for(String state2 : states){
					if(reach(nfa,state,state2) && reach(nfa,state2,state)){
						currentSCC.add(state2);
						done.add(state2);
					}
				}
				scc.add(currentSCC);
			}
		}		

		return scc;
	}

	/*public static void throwNoOpportunityFoundException(String message,NFA nfa) throws NoOpportunityFoundException{
		gjb.flt.regex.infer.rwr.impl.GraphAutomatonFactory factory = new gjb.flt.regex.infer.rwr.impl.GraphAutomatonFactory();
		throw new NoOpportunityFoundException(message,factory.create(nfa));
	}*/

	private static Set<HashSet<String>> completeStronglyConnectedComponents(
			SparseNFA nfa) {
		Set<HashSet<String>> scc = new HashSet<HashSet<String>>();
		Set<String> done  = new HashSet<String>();
		Set<String> states = nfa.getStateValues();

		for(String state : states){
			if(!done.contains(state)){
				HashSet<String> currentSCC = new HashSet<String>();
				currentSCC.add(state);
				done.add(state);
				for(String state2 : states){
					if(reach(nfa,state,state2) && reach(nfa,state2,state)){
						currentSCC.add(state2);
						done.add(state2);
					}
				}
				scc.add(currentSCC);
			}
		}		

		return scc;
	}


	public static Set<String> reachableStates(SparseNFA nfa, String state) {
		Set<String> reach = new HashSet<String>();		
		Stack<String> toDo = new Stack<String>();
		toDo.add(state);

		while (!toDo.isEmpty()) {
			String currentState = toDo.pop();
			reach.add(currentState);
			Set<String> connected = stateValues(nfa,nfa.getNextStates(nfa.getState(currentState)));
			connected.removeAll(reach);
			toDo.addAll(connected);
		}
		return reach;
	}

	private static Set<String> backwardReachableStates(SparseNFA nfa, Set<String> states) {
		Set<String> reach = new HashSet<String>();	
		Set<String> toDo = new HashSet<String>();
		toDo.addAll(states);

		while (!toDo.isEmpty()) {
			String currentState = gjb.util.Collections.takeOne(toDo);
			reach.add(currentState);
			Set<String> connected = stateValues(nfa,nfa.getPreviousStates(nfa.getState(currentState)));
			connected.removeAll(reach);
			toDo.addAll(connected);
		}

		return reach;
	}


	private static Set<String> stateValues(SparseNFA nfa, Set<State> states) {
		Set<String> stateValues = new HashSet<String>();

		for(State state : states)
			stateValues.add(nfa.getStateValue(state));

		return stateValues;
	}


	/*	private static void cleanNFA(NFA nfa) {
		String initial = nfa.getStateValue(nfa.getInitialState());
		Set<String> states = reachableStates(nfa,initial);
		states = gjb.util.Collections.intersect(states, backwardReachableStates(nfa,stateValues(nfa,nfa.getFinalStates())));
		Set<String> oldStates = new HashSet<String>(nfa.getStateValues());

		for(String state : oldStates){
			if(!states.contains(state))
				nfa.removeState(state);
		}
	}*/


	static boolean reach(SparseNFA nfa, String fromState, String toState) {
		boolean reach = false;
		Set<String> toDo = new HashSet<String>();
		Set<String> done = new HashSet<String>();
		toDo.add(fromState);

		if(fromState.equals(toState))
			return true;

		while (!toDo.isEmpty() && !reach) {
			String state = gjb.util.Collections.takeOne(toDo);
			done.add(state);
			Set<String> connected = connectedStates(nfa,state);
			if(connected.contains(toState))
				reach = true;
			connected.removeAll(done);
			toDo.addAll(connected);
		}
		return reach;
	}

	private static Set<String> connectedStates(SparseNFA nfa, String state) {
		Set<String> connected = new HashSet<String>();
		Set<String> getAlphabet = nfa.getAlphabet().keySet();

		for(String symbol : getAlphabet)
			connected.addAll(nfa.getNextStateValues(symbol, state));

		return connected;
	}

	/**
	 * @param precedingSymbols
	 * @param result
	 * @return
	 */
	static Node concatenate(Tree precedingSymbols, Tree result) {
		Node root = new Node(Regex.CONCAT_OPERATOR);
		root.addChild(precedingSymbols.getRoot());
		root.addChild(result.getRoot());
		return root;
	}

	/**
	 * @param hashSet
	 * @return
	 */
	static Tree createDisjunction(Set<String> symbols) {
		Set<Node> nodes = new HashSet<Node>();
		for(String symbol : symbols){
			Node node = new Node(symbol);
			nodes.add(node);
		}
		Tree tree = new Tree();
		tree.setRoot(createDisjunctionX(nodes));
		return tree;
	}

}
