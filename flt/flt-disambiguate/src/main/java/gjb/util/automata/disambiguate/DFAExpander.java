package gjb.util.automata.disambiguate;

import gjb.flt.automata.converters.Simplifier;
import gjb.flt.automata.impl.sparse.SparseNFA;
import gjb.flt.automata.NoSuchStateException;
import gjb.flt.automata.NotDFAException;
import gjb.flt.automata.impl.sparse.State;
import gjb.flt.automata.impl.sparse.Symbol;
import gjb.flt.automata.impl.sparse.Transition;
import gjb.util.RandomSelector;

import gjb.flt.regex.Glushkov;
import gjb.flt.automata.NotDFAException;
import gjb.flt.automata.impl.sparse.TransitionMap;
import gjb.flt.regex.infer.rwr.impl.GraphAutomatonFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DFAExpander {

	private static final int MAX_SUBSET_SIZE = 4;
	
	public SparseNFA expandDFA(SparseNFA dfa){		
		Map<String,Set<String>> correspondingStates = computeCorrespondingStates(dfa);
		
		Set<String> candidates = new HashSet<String>();
		for(String state : dfa.getStateValues()){
			Set<Transition> incomingTransitions = dfa.getIncomingTransitions(dfa.getState(state));
			if(incomingTransitions.size() > 1){
				candidates.add(state);
			}
		}
		RandomSelector<String> random = new RandomSelector<String>();
		if(!candidates.isEmpty()){
			String state = random.selectOneFrom(candidates);
			SparseNFA newDFA = expandDFAOne(dfa, state, correspondingStates);
			//DeterministicExpressionExploreTest.writeDot("newDFA.dot", newDFA);
			Simplifier.simplify(newDFA);
			//DeterministicExpressionExploreTest.writeDot("newDFASimple.dot", newDFA);
			if(newDFA.getNumberOfStates() == dfa.getNumberOfStates() + 1)
				return newDFA;
		}
		
		return null;
	}
	
	public static SparseNFA constructGlushkovAutomaton(SparseNFA dfa){
		SparseNFA glushkov = new SparseNFA();

		Simplifier.simplify(dfa);

		for(String state : dfa.getStateValues()){
			Set<String> symbols = extractSymbols(dfa.getIncomingTransitions(dfa.getState(state)));
			Set<Transition> outgoingTransitions = dfa.getOutgoingTransitions(dfa.getState(state));
			for(String symbol : symbols){
				if(dfa.isFinalState(state))
					glushkov.addFinalState(symbol + "_" + state.replace('_', '$'));
				for(Transition transition : outgoingTransitions){
					String transitionSymbol = transition.getSymbol().toString();
					glushkov.addTransition(transitionSymbol, symbol + "_" + state.replace('_', '$'), transitionSymbol + "_" + dfa.getStateValue(transition.getToState()).replace('_', '$'));
				}
			}
		}

		State newInitialObject = new State();
		glushkov.addState(newInitialObject.toString());
		String newInitial = glushkov.getStateValue(glushkov.getState(newInitialObject.toString()));
		glushkov.setInitialState(newInitial);
		if(dfa.isFinalState(dfa.getInitialState()))
			glushkov.addFinalState(newInitial);

		for(Transition transition : dfa.getOutgoingTransitions(dfa.getInitialState())){
			glushkov.addTransition(transition.getSymbol().toString(), newInitial, transition.getSymbol().toString() + "_" + dfa.getStateValue(transition.getToState()).replace('_', '$'));
		}

		return glushkov;
	}
	
	protected String extractOriginalLabel(String label){
		int i;
		for(i = 0; i < label.length() && label.charAt(i) != '#'; i++)
			;
		return label.substring(0,i);
	}
	
	protected String extractSymbol(String label) {
		int i;
		for(i = 0; i < label.length() && label.charAt(i) != '_'; i++)
			;
		return label.substring(0,i);
	}
	
	protected Map<String,Set<String>> computeCorrespondingStates(SparseNFA dfa){
		Map<String,Set<String>> correspondingStates = new HashMap<String, Set<String>>();
		
		for(String state : dfa.getStateValues()){
			if(!dfa.isInitialState(state)){
				String originalState = extractOriginalLabel(state);
				if(!correspondingStates.containsKey(originalState))
					correspondingStates.put(originalState, new HashSet<String>());
				correspondingStates.get(originalState).add(state);
			}
		}
		return correspondingStates;
	}

	private static Set<String> computePreSet(SparseNFA dfa, String state) {
		Set<Transition> incomingTransitions = dfa.getIncomingTransitions(dfa.getState(state));
		Set<String> preSet = new HashSet<String>();

		for(Transition transition : incomingTransitions){
			preSet.add(dfa.getStateValue(transition.getFromState()));
		}
		preSet.remove(state);
		return preSet;
	}
	
	/**
	 * Expands the given dfa with one state, by duplicating the given state. Warning: it 
	 * assumes that the states are labeled a_id1#id2, where a_id1 is the label of the
	 * corresponding label of the original automaton
	 * @param dfa
	 * @param state
	 * @return
	 */
	protected SparseNFA expandDFAOne(SparseNFA dfa, String state, Map<String,Set<String>> correspondingStates){
		String originalState = extractOriginalLabel(state);
		
		SparseNFA newDFA = new SparseNFA(dfa);
		try {
			newDFA.removeState(newDFA.getState(state));
		} catch (NoSuchStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		int index = dfa.getNumberOfStates() + 1;
		String newState1 = originalState + "#" + index;
		newDFA.addState(newState1);

		String newState2 = originalState + "#" + (index + 1);
		newDFA.addState(newState2);

		if(dfa.isFinalState(state)){
			newDFA.addFinalState(newState1);
			newDFA.addFinalState(newState2);
		}
		
		String symbol = extractSymbol(originalState);
		
		Set<String> preSet = computePreSet(dfa, state);
		RandomSelector<String> random = new RandomSelector<String>();

		Set<String> subset = random.selectSubsetFrom(preSet, (int) ((Math.random() * (preSet.size() - 1)) + 1));

		for(String fromState : preSet){
			if(subset.contains(fromState))
				newDFA.addTransition(symbol, fromState, newState1);
			else
				newDFA.addTransition(symbol, fromState, newState2);
		}
		
		Set<String> statesCorrespondingtoOriginalState = correspondingStates.get(originalState);
		statesCorrespondingtoOriginalState.remove(state);
		statesCorrespondingtoOriginalState.add(newState1);
		statesCorrespondingtoOriginalState.add(newState2);
		
		for(Transition transition : dfa.getOutgoingTransitions(dfa.getState(state))){
			String outState1 = dfa.getStateValue(transition.getToState());
			String originalOutState = extractOriginalLabel(outState1);
			if(outState1.equals(state))
				outState1 = newState1;
			RandomSelector<String> selector = new RandomSelector<String>();
			String outState2 = selector.selectOneFrom(correspondingStates.get(originalOutState));
			
			if(Math.random() > 0.5){
				newDFA.addTransition(transition.getSymbol().toString(), newState1, outState1);
				newDFA.addTransition(transition.getSymbol().toString(), newState2, outState2);
			} 
			else{
				newDFA.addTransition(transition.getSymbol().toString(), newState1, outState2);
				newDFA.addTransition(transition.getSymbol().toString(), newState2, outState1);
			}
		}
		
		return newDFA;
	}
	
	
	
	// NOTHING BELOW THIS POINT IS GUARANTEED TO BE CORRECT!
	
	
	
	
	
	public static Set<SparseNFA> expandDFAs(Set<SparseNFA> dfas, boolean removeIsomorphic){
		return expandDFAs(dfas, removeIsomorphic ,false, -1);
	}

	

	/**
	 * @param incomingTransitions
	 * @return
	 */
	private static Set<String> extractSymbols(Set<Transition> transitions) {
		Set<String> symbols = new HashSet<String>();

		for(Transition transition : transitions)
			symbols.add(transition.getSymbol().toString());

		return symbols;

	}

	protected static SparseNFA expandDFASingle(SparseNFA dfa, boolean removeIsomorphic, IsomorphismTree tree){
		/*Map<String,String> candidates = new HashMap<String,String>();
		for(String state : dfa.getStateValues()){
			Set<Transition> incomingTransitions = dfa.getIncomingTransitions(dfa.getState(state));
			if(incomingTransitions.size() > 1){
				String symbol = gjb.util.Collections.getOne(incomingTransitions).getSymbol().toString();
				candidates.put(state, symbol);
			}
		}
		if(candidates.isEmpty())
			return null;
		else{
			RandomSelector<String> random = new RandomSelector<String>();
			String state = random.selectOneFrom(candidates.keySet());
			return expandDFASingle(dfa, state, candidates.get(state), removeIsomorphic, tree);
		}*/
		Set<SparseNFA> set = expandDFAAll(dfa, removeIsomorphic, tree);
		if(set.isEmpty())
			return null;
		else{
			RandomSelector<SparseNFA> random = new RandomSelector<SparseNFA>();
			return random.selectOneFrom(set);
		}
	}


	public static Set<SparseNFA> expandDFAAll(SparseNFA dfa, boolean removeIsomorphic,IsomorphismTree tree){
		Set<SparseNFA> result = new HashSet<SparseNFA>();

		Simplifier.simplify(dfa);

		if(!isGlushkov(dfa))
			return null;

		for(String state : dfa.getStateValues()){
			Set<Transition> incomingTransitions = dfa.getIncomingTransitions(dfa.getState(state));
			if(incomingTransitions.size() > 1){
				String symbol = gjb.util.Collections.getOne(incomingTransitions).getSymbol().toString();
				result.addAll(expandDFAAll(dfa,state,symbol,removeIsomorphic,tree));
			}
			if(removeIsomorphic && tree.satisfied())
				return result;
		}

		return result;
	}

	public static boolean isGlushkov(SparseNFA dfa) {

		for(String state : dfa.getStateValues()){
			Set<Transition> incomingTransitions = dfa.getIncomingTransitions(dfa.getState(state));
			if(!incomingTransitions.isEmpty()){
				Symbol symbol = ((Transition)gjb.util.Collections.getOne(incomingTransitions)).getSymbol();
				for(Transition transition : incomingTransitions){
					if(!symbol.equals(transition.getSymbol()))
						return false;				
				}
			}		
		}
		return true;
	}

	
	
	/**
	 * @param state
	 * @return
	 */


	/*protected static NFA expandDFASingle(NFA dfa, String state, String symbol, boolean removeIsomorphic,IsomorphismTree tree) {
		Set<NFA> result = new HashSet<NFA>();
		NFA newDFA = new NFA(dfa);

		newDFA.removeState(newDFA.getState(state));

		String newState1 = symbol + "_" + (new State()).toString();
		newDFA.addState(newState1);

		String newState2 = symbol + "_" + (new State()).toString();
		newDFA.addState(newState2);

		if(dfa.isFinalState(state)){
			newDFA.addFinalState(newState1);
			newDFA.addFinalState(newState2);
		}

		boolean selfLoop = dfa.hasTransition(symbol, state, state);

		setOutgoingStates(dfa, state, newDFA, newState1, newState2);

		Set<String> preSet = computePreSet(dfa, state);
		RandomSelector<String> random = new RandomSelector<String>();

		Set<String> subset = random.selectSubsetFrom(preSet, (int) ((Math.random() * (preSet.size() - 1)) + 1));

		for(String fromState : preSet){
			if(subset.contains(fromState))
				newDFA.addTransition(symbol, fromState, newState1);
			else
				newDFA.addTransition(symbol, fromState, newState2);
		}

		if(!selfLoop){
			return newDFA;
		}
		else{
			switch((int) (Math.random() * 4)){
			case 0:
				newDFA.addTransition(symbol, newState1, newState1);
				newDFA.addTransition(symbol, newState2, newState2);
				return newDFA;
			case 1:
				newDFA.addTransition(symbol, newState1, newState2);
				newDFA.addTransition(symbol, newState2, newState1);
				return newDFA;
			case 2:
				newDFA.addTransition(symbol, newState1, newState2);
				newDFA.addTransition(symbol, newState2, newState2);
				return newDFA;
			case 3 :
				newDFA.addTransition(symbol, newState1, newState1);
			newDFA.addTransition(symbol, newState2, newState1);
			return newDFA;
			}
		}
		
		return dfa;
	}*/


	private static Set<SparseNFA> expandDFAAll(SparseNFA dfa, String state, String symbol, boolean removeIsomorphic,IsomorphismTree tree) {
		Set<SparseNFA> result = new HashSet<SparseNFA>();
		SparseNFA newDFA = new SparseNFA(dfa);

		try {
			newDFA.removeState(newDFA.getState(state));
		} catch (NoSuchStateException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		String newState1 = symbol + "_" + (new State()).toString();
		newDFA.addState(newState1);

		String newState2 = symbol + "_" + (new State()).toString();
		newDFA.addState(newState2);

		if(dfa.isFinalState(state)){
			newDFA.addFinalState(newState1);
			newDFA.addFinalState(newState2);
		}

		boolean selfLoop = dfa.hasTransition(symbol, state, state);

		setOutgoingStates(dfa, state, newDFA, newState1, newState2);

		Set<String> preSet = computePreSet(dfa, state);

		Set<HashSet<String>> subsets;
		if(preSet.size() <= MAX_SUBSET_SIZE * 2)
			subsets = powerSet(preSet, preSet.size() / 2);
		else
			subsets = powerSet(preSet, MAX_SUBSET_SIZE);
		//System.out.println(subsets.size());

		for(HashSet<String> subset : subsets){
			if(subset.size() > 0 && subset.size() <= preSet.size() / 2){
				SparseNFA newerDFA = new SparseNFA(newDFA);
				for(String fromState : preSet){
					if(subset.contains(fromState))
						newerDFA.addTransition(symbol, fromState, newState1);
					else
						newerDFA.addTransition(symbol, fromState, newState2);
				}
				if(!selfLoop){
					if(!removeIsomorphic)
						result.add(newerDFA);
					else{
						try {
							if(tree.addNFA(newerDFA))
								result.add(newerDFA);
						} catch (NotDFAException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				else {
					addSelfLoop(symbol, removeIsomorphic, tree, result, newState1, newState2,
							newerDFA);
				}
			}
			if(removeIsomorphic && tree.satisfied())
				return result;
		}

		return result;
	}



	private static void setOutgoingStates(SparseNFA dfa, String state, SparseNFA newDFA,
			String newState1, String newState2) {
		Set<Transition> outgoingTransitions = dfa.getOutgoingTransitions(dfa.getState(state));

		for(Transition transition : outgoingTransitions){
			String toState = dfa.getStateValue(transition.getToState());
			if(toState != state){
				newDFA.addTransition(transition.getSymbol().toString(),newState1,toState);
				newDFA.addTransition(transition.getSymbol().toString(),newState2,toState);
			}
		}
	}

	private static void addSelfLoop(String symbol, boolean removeIsomorphic,IsomorphismTree tree,
			Set<SparseNFA> result, String newState1, String newState2, SparseNFA newerDFA) {

		SparseNFA newerDFA1 = new SparseNFA(newerDFA);
		newerDFA1.addTransition(symbol, newState1, newState1);
		newerDFA1.addTransition(symbol, newState2, newState2);
		if(!removeIsomorphic)
			result.add(newerDFA1);
		else{
			try {
				if(tree.addNFA(newerDFA1))
					result.add(newerDFA1);
			} catch (NotDFAException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		//result.add(newerDFA1);

		newerDFA1 = new SparseNFA(newerDFA);
		newerDFA1.addTransition(symbol, newState1, newState2);
		newerDFA1.addTransition(symbol, newState2, newState1);
		if(!removeIsomorphic)
			result.add(newerDFA1);
		else{
			try {
				if(tree.addNFA(newerDFA1))
					result.add(newerDFA1);
			} catch (NotDFAException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		newerDFA1 = new SparseNFA(newerDFA);
		newerDFA1.addTransition(symbol, newState1, newState2);
		newerDFA1.addTransition(symbol, newState2, newState2);
		if(!removeIsomorphic)
			result.add(newerDFA1);
		else{
			try {
				if(tree.addNFA(newerDFA1))
					result.add(newerDFA1);
			} catch (NotDFAException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		newerDFA1 = new SparseNFA(newerDFA);
		newerDFA1.addTransition(symbol, newState1, newState1);
		newerDFA1.addTransition(symbol, newState2, newState1);
		if(!removeIsomorphic)
			result.add(newerDFA1);
		else{
			try {
				if(tree.addNFA(newerDFA1))
					result.add(newerDFA1);
			} catch (NotDFAException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static Set<HashSet<String>> powerSet(Set<String> set, int subsetSize) {
		Set<String> newSet = new HashSet<String>();
		newSet.addAll(set);
		return powerSetInduction(newSet,subsetSize);
	}

	private static Set<HashSet<String>> powerSetInduction(Set<String> preSet, int subsetSize) {
		if(preSet.isEmpty()){
			Set<HashSet<String>>result = new HashSet<HashSet<String>>();
			result.add(new HashSet<String>());
			return result;
		}
		else{
			String current = gjb.util.Collections.takeOne(preSet);
			Set<HashSet<String>> subSets = powerSetInduction(preSet,subsetSize);
			Set<HashSet<String>> result = new HashSet<HashSet<String>>();
			result.addAll(subSets);
			for(HashSet<String> subset : subSets){
				if(subset.size() < subsetSize){
					HashSet<String> newSubset = new HashSet<String>();
					newSubset.add(current);
					newSubset.addAll(subset);
					result.add(newSubset);
				}
			}
			return result;
		}
	}

	/**
	 * @param as
	 * @param approximateSetSize
	 * @return
	 */
	public static Set<SparseNFA> expandDFAs(Set<SparseNFA> dfas,int approximateSetSize) {
		return expandDFAs(dfas, true, true, approximateSetSize);
	}

	/**
	 * @param as
	 * @param b
	 * @param approximateSetSize
	 * @return
	 */
	private static Set<SparseNFA> expandDFAs(Set<SparseNFA> dfas, boolean removeIsomorphic, boolean bound, int sizeBound) {
		IsomorphismTree tree = null;
		if(removeIsomorphic){
			if(bound)
				tree = new IsomorphismTree(sizeBound);
			else
				tree = new IsomorphismTree();
		}

		Set<SparseNFA> result = new HashSet<SparseNFA>();

		if(bound && dfas.size() >= (9.0/10)*sizeBound){
			for(SparseNFA dfa : dfas){
				SparseNFA expansion = expandDFASingle(dfa,removeIsomorphic,tree);
				if(expansion != null)
					result.add(expansion);
			}
			return result;
		}

		boolean addingSingleDFAs = false;
		int remaining = dfas.size();
		for(SparseNFA dfa : dfas){
			remaining--;
			if(!addingSingleDFAs){
				result.addAll(expandDFAAll(dfa,removeIsomorphic,tree));
				if(bound && tree.satisfied()){
					RandomSelector<SparseNFA> random = new RandomSelector<SparseNFA>();
					result = random.selectSubsetFrom(result, sizeBound - remaining);
				}
			}
			else{
				SparseNFA expansion = expandDFASingle(dfa,removeIsomorphic,tree);
				if(expansion != null)
					result.add(expansion);
			}
		}

		return result;
		// TODO Auto-generated 

	}
}
