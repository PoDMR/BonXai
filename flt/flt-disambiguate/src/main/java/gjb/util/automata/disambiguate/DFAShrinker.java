/**
 * 
 */
package gjb.util.automata.disambiguate;

import gjb.flt.automata.NoSuchStateException;
import gjb.flt.automata.impl.sparse.SparseNFA;
import gjb.flt.automata.impl.sparse.State;
import gjb.flt.automata.impl.sparse.Transition;
import gjb.flt.regex.infer.rwr.impl.Automaton;
import gjb.flt.regex.infer.rwr.impl.GraphAutomatonFactory;
import gjb.flt.regex.infer.rwr.measures.LanguageSizeMeasure;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author woutergelade
 *
 */
public class DFAShrinker {

	private static final int INFINITY = 100000;
	private  int UPPERBOUND = 20;
	private static LanguageSizeMeasure l;

	public DFAShrinker(int getNumberOfStates) {
		l = new LanguageSizeMeasure();
		l.setMaxLength((getNumberOfStates * 2) + 1);
	}

	/**
	 * 
	 */
	public DFAShrinker() {
		l = null;
	}

	public void setUpperbound(int upperbound){
		this.UPPERBOUND = upperbound;
	}
	
	public SparseNFA getBestShrinked(SparseNFA dfa){
		Set<SparseNFA> result = shrink(dfa);

		if(result.isEmpty())
			return null;

		SparseNFA first = gjb.util.Collections.takeOne(result);
		gjb.flt.regex.infer.rwr.impl.GraphAutomatonFactory factory = new gjb.flt.regex.infer.rwr.impl.GraphAutomatonFactory();
		double languageSize = 1 - l.compute(factory.create(first));
		SparseNFA optimal = first;
		for(SparseNFA nfa : result){
			double size = 1 - l.compute(factory.create(nfa));
			if(size < languageSize){
				optimal = nfa;
				languageSize = size;
			}
		}
		return optimal;
	}

	public Set<SparseNFA> shrink(Set<SparseNFA> dfas){
		Set<SparseNFA> result = new HashSet<SparseNFA>();
		for(SparseNFA dfa : dfas){
			result.addAll(shrink(dfa));
		}
		return result;
	}
	
	/**
	 * Compute all DFAs which can be computed from the input dfa
	 * by merging two states (with identical labels)
	 * @param dfa
	 * @return
	 */
	private Set<SparseNFA> shrink(SparseNFA dfa){
		int generated = 0;
		Set<SparseNFA> result = new HashSet<SparseNFA>();
		Map<String,String> incomingSymbol = new HashMap<String,String>();

		for(String state : dfa.getStateValues()){
			Set<Transition> incomingTransitions = dfa.getIncomingTransitions(dfa.getState(state));
			if(!incomingTransitions.isEmpty()){
				String symbol = gjb.util.Collections.getOne(incomingTransitions).getSymbol().toString();
				incomingSymbol.put(state, symbol);
			}
		}

		for(String state1 : incomingSymbol.keySet()){
			for(String state2 : incomingSymbol.keySet()){
				if(state1.compareTo(state2) < 0 && incomingSymbol.get(state1).equals(incomingSymbol.get(state2))){
					generated++;
					HashSet<String> states = new HashSet<String>();
					SparseNFA newDFA = new SparseNFA(dfa);
					states.add(state1);
					states.add(state2);
					mergeStates(newDFA,states, true);
					result.add(newDFA);
				}
				if(generated >= UPPERBOUND)
					break;
			}
			if(generated >= UPPERBOUND)
				break;
		}
		return result;
	}

	public void mergeStates(SparseNFA dfa, HashSet<String> states, boolean labelWithSymbol){
		if(states.size() > 1){
			Set<HashSet<String>> merge = new HashSet<HashSet<String>>();

			merge.add(states);
			mergeStates(dfa,merge,labelWithSymbol);
		}
	}

	/**
	 * Merges state1 and state2 into one state and proceeds recursively, if necessary.
	 * @param dfa
	 * @param labelWithSymbol TODO
	 * @param state1
	 * @param state2
	 */
	public void mergeStates(SparseNFA dfa, Set<HashSet<String>> merge, boolean labelWithSymbol){
		Map<String,String> newName = new HashMap<String,String>();

		while(!merge.isEmpty()){
			Set<String> currentSet = new HashSet<String>();
			for(String state : gjb.util.Collections.takeOne(merge)){
				while(newName.containsKey(state))
					state = newName.get(state);
				currentSet.add(state);
			}
			
			
			if(currentSet.size() > 1){
				String newState;
				if(labelWithSymbol)
					newState = computeNewStateLabel(dfa, currentSet);
				else
					newState = (new State()).toString();				

				
				for(String state : currentSet)
					newName.put(state, newState);

				mergeStatesWithoutRecursion(dfa, currentSet, newState);			
				
				for(String sym : dfa.getAlphabet().keySet()){
					Set<State> nextStates = dfa.getNextStates(dfa.getAlphabet().get(sym), dfa.getState(newState));
					if(nextStates.size() > 1){
						addToMergeSet(dfa,nextStates,merge);
					}
				}
			}
		}
	}

	private void mergeStatesWithoutRecursion(SparseNFA dfa, Set<String> currentSet,
			String newState) {
		dfa.addState(newState);
		for(String state : currentSet){
			Set<Transition> incomingTransitions = dfa.getIncomingTransitions(dfa.getState(state));
			for(Transition transition : incomingTransitions){
				if(currentSet.contains(dfa.getStateValue(transition.getFromState())))
					dfa.addTransition(transition.getSymbol().toString(), newState, newState);
				else
					dfa.addTransition(transition.getSymbol().toString(), dfa.getStateValue(transition.getFromState()), newState);
			}

			if(dfa.isFinalState(dfa.getState(state)))
				dfa.addFinalState(newState);

			if(dfa.isInitialState(state))
				dfa.setInitialState(newState);

			Set<Transition> outgoingTransitions = dfa.getOutgoingTransitions(dfa.getState(state));
			for(Transition transition : outgoingTransitions){
				if(currentSet.contains(dfa.getStateValue(transition.getToState())))
					dfa.addTransition(transition.getSymbol().toString(), newState, newState);
				else
					dfa.addTransition(transition.getSymbol().toString(), newState, dfa.getStateValue(transition.getToState()));
			}
		}
		
		for(String state : currentSet)
			try {
				dfa.removeState(state);
			} catch (NoSuchStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

	/**
	 * @param nextStates
	 * @param merge
	 */
	private static void addToMergeSet(SparseNFA dfa, Set<State> nextStates,
			Set<HashSet<String>> merge) {
		HashSet<String> next = new HashSet<String>();
		for(State state : nextStates)
			next.add(dfa.getStateValue(state));
		merge.add(next);
	}

	/**
	 * @param dfa
	 * @param current
	 * @return
	 */
	private static String computeNewStateLabel(SparseNFA dfa, Set<String> set) {
		String label = (new State()).toString();

		for(String state : set){
			Set<Transition> incomingTransitions = dfa.getIncomingTransitions(dfa.getState(state));
			if(!incomingTransitions.isEmpty()){
				Transition transition = gjb.util.Collections.getOne(incomingTransitions);
				return transition.getSymbol().toString() + "_" + label;
			}
		}

		return label;
	}

	public void mergeStates(SparseNFA dfa, HashSet<String> states, boolean labelWithSymbol, SparseNFA globalNFA){
		if(states.size() > 1){
			Set<HashSet<String>> merge = new HashSet<HashSet<String>>();

			merge.add(states);
			mergeStates(dfa,merge,labelWithSymbol, globalNFA);
		}
	}

	/**
	 * Merges state1 and state2 into one state and proceeds recursively, if necessary.
	 * @param dfa
	 * @param labelWithSymbol TODO
	 * @param state1
	 * @param state2
	 */
	public void mergeStates(SparseNFA dfa, Set<HashSet<String>> merge, boolean labelWithSymbol, SparseNFA globalNFA){
		Map<String,String> newName = new HashMap<String,String>();

		while(!merge.isEmpty()){
			Set<String> currentSet = new HashSet<String>();
			for(String state : gjb.util.Collections.takeOne(merge)){
				while(newName.containsKey(state))
					state = newName.get(state);
				currentSet.add(state);
			}
			
			
			if(currentSet.size() > 1){
				String newState;
				if(labelWithSymbol)
					newState = computeNewStateLabel(dfa, currentSet);
				else
					newState = (new State()).toString();				

				
				for(String state : currentSet)
					newName.put(state, newState);

				mergeStatesWithoutRecursion(dfa, currentSet, newState);
				mergeStatesWithoutRecursion(globalNFA, currentSet, newState);		
				
				for(String sym : dfa.getAlphabet().keySet()){
					Set<State> nextStates = dfa.getNextStates(dfa.getAlphabet().get(sym), dfa.getState(newState));
					if(nextStates.size() > 1){
						addToMergeSet(dfa,nextStates,merge);
					}
				}
			}
		}
	}

}
