/**
 * 
 */
package eu.fox7.util.automata.disambiguate;

import eu.fox7.flt.automata.NotDFAException;
import eu.fox7.flt.automata.converters.Simplifier;
import eu.fox7.flt.automata.impl.sparse.SparseNFA;
import eu.fox7.flt.automata.impl.sparse.State;
import eu.fox7.util.Pair;
import eu.fox7.util.RandomSelector;

//import eu.fox7.util.regex.random.RegexGenerator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author woutergelade
 *
 */
public class DFAGeneratorNaive {

	/* (non-Javadoc)
	 * @see eu.fox7.util.automata.disambiguate.DFAGenerator#generateDFAs(eu.fox7.flt.automata.impl.sparse.SparseNFA, int)
	 */
	public Set<SparseNFA> generateDFAs(SparseNFA minimalDFA, int size) {
		Set<SparseNFA> result = new HashSet<SparseNFA>();

		if(minimalDFA.getNumberOfStates() > size)
			return null;
		else if(minimalDFA.getNumberOfStates() == size){
			result.add(new SparseNFA(minimalDFA));
			return result;
		}
		else{
			Set<String> stateSet = new HashSet(minimalDFA.getStateValues());
			stateSet.remove(minimalDFA.getStateValue(minimalDFA.getInitialState()));
			String[] states = stateSet.toArray(new String[stateSet.size()]);
			Set<Map<String,Integer>> stateCountMap = generateStateCount(states,size - 1);

			for(Map<String,Integer> stateCount : stateCountMap)
				result.addAll(generateDFAsWithStates(minimalDFA, stateCount));

			return result;
		}
	}

	/**
	 * @param states
	 * @param size
	 * @return
	 */
	protected Set<Map<String, Integer>> generateStateCount(String[] states,
			int size) {
		return generateStateCountRecursively(new HashMap<String,Integer>(),states,0,size);
	}

	protected Map<String, Integer> generateOneStateCount(Set<String> states, int size){
		if(size < states.size())
			return null;
		else{
			Map<String, Integer> stateCount = new HashMap<String, Integer>();
			Set<String> toDo = new HashSet<String>();
			toDo.addAll(states);
			while(!toDo.isEmpty()){
				RandomSelector<String> random = new RandomSelector<String>();
				String state = random.selectOneFrom(toDo);
				toDo.remove(state);
				int count;
				if(toDo.isEmpty())
					count = size;
				else
					count = (int)(Math.random() * (size - toDo.size())) + 1;
				size -= count;
				stateCount.put(state, new Integer(count));
			}
			return stateCount;
		}
	}
	
	/**
	 * @param states
	 * @param i
	 * @param size
	 * @return
	 */
	private Set<Map<String, Integer>> generateStateCountRecursively(Map<String,Integer> currentMap,
			String[] states, int position, int valueLeft) {
		Set<Map<String,Integer>> result = new HashSet<Map<String,Integer>>();
		if(position == states.length - 1){
			currentMap.put(states[position], valueLeft);
			result.add(currentMap);
		}
		else{
			for(int number = 1; valueLeft - number >= states.length - position - 1; number++){
				Map<String,Integer> newMap = new HashMap<String, Integer>(currentMap);
				newMap.put(states[position], new Integer(number));
				result.addAll(generateStateCountRecursively(newMap,states,position+1,valueLeft-number));
			}
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see eu.fox7.util.automata.disambiguate.DFAGenerator#generateDisjointDFAs(eu.fox7.flt.automata.impl.sparse.SparseNFA, int)
	 */
	public Set<SparseNFA> generateDisjointDFAs(SparseNFA minimalDFA, int size) {
		Isomorphism linear = new LinearIsomorphism();
		Set<SparseNFA> result;
		try {
			result = linear.removeIsomporhicDFAs(generateDFAs(minimalDFA,size));
			return result;
		} catch (NotDFAException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		return null;
	}

	protected Set<SparseNFA> generateDFAsWithStates(SparseNFA minimalDFA, Map<String,Integer> stateCount){
		Set<SparseNFA> result = new HashSet<SparseNFA>();

		SparseNFA newDFA = new SparseNFA();
		String getInitialState = "q_I";
		newDFA.setInitialState(getInitialState);
		if(minimalDFA.isFinalState(minimalDFA.getInitialState()))
			newDFA.addFinalState(getInitialState);

		for(String state : stateCount.keySet()){
			for(int count = 1; count <= stateCount.get(state).intValue(); count++){
				String newState = makeLabel(state,""+count);
				newDFA.addState(newState);
				if(minimalDFA.isFinalState(state))
					newDFA.addFinalState(newState);
			}
			for(String symbol : minimalDFA.getAlphabet().keySet()){
				if(minimalDFA.hasTransition(symbol, minimalDFA.getStateValue(minimalDFA.getInitialState()), state))
					newDFA.addTransition(symbol, getInitialState, makeLabel(state,"1"));
			}
		}

		String[] getAlphabet = (String[]) minimalDFA.getAlphabet().keySet().toArray(new String[minimalDFA.getAlphabet().keySet().size()]);		
		Set<String> stateSet = new HashSet<String>(minimalDFA.getStateValues());
		stateSet.remove(minimalDFA.getStateValue(minimalDFA.getInitialState()));
		String[] states = (String[]) stateSet.toArray(new String[stateSet.size()]);

		result = generateDFAsRecursively(minimalDFA, newDFA, states, 0, 1, getAlphabet, 0,stateCount);

		return result;
	}

	/**
	 * @param newDFA
	 * @param states
	 * @param i
	 * @param j
	 * @param getAlphabet
	 * @param k
	 * @return
	 */
	private Set<SparseNFA> generateDFAsRecursively(SparseNFA minimalDFA, SparseNFA newDFA, String[] states,
			int stateNr, int instanceNr, String[] getAlphabet, int getAlphabetNr, Map<String,Integer> stateCount) {
		Set<SparseNFA> result = new HashSet<SparseNFA>();

		// System.out.println(stateNr + " " + instanceNr + " " + getAlphabetNr);

		if(stateNr >= states.length){
			int originalSize = newDFA.getNumberOfStates();
			//DeterministicExpressionExploreTest.writeDot("test.dot", newDFA);
			Simplifier.simplify(newDFA);
			//DeterministicExpressionExploreTest.writeDot("test.dot", newDFA);
			if(newDFA.getNumberOfStates() == originalSize)
				result.add(newDFA);
			return result;
		}
		else{
			int nextAlphabetNr = computeAlphabetNr(getAlphabet, getAlphabetNr);			
			int nextInstanceNr = computeInstanceNr(states, stateNr, instanceNr,stateCount, nextAlphabetNr);		
			int nextStateNr = computeStateNr(stateNr, nextAlphabetNr, nextInstanceNr);

			Set<State> nextStates = minimalDFA.getNextStates(minimalDFA.getAlphabet().get(getAlphabet[getAlphabetNr]),minimalDFA.getState(states[stateNr]));

			if(nextStates.isEmpty())
				return generateDFAsRecursively(minimalDFA, newDFA, states,
						nextStateNr, nextInstanceNr, getAlphabet, nextAlphabetNr, stateCount);
			else {
				String toState = minimalDFA.getStateValue(eu.fox7.util.Collections.getOne(nextStates));
				for(int count = 1; count <= stateCount.get(toState).intValue();count++){
					SparseNFA newerDFA = new SparseNFA(newDFA);
					newerDFA.addTransition(getAlphabet[getAlphabetNr], makeLabel(states[stateNr], ""+instanceNr), makeLabel(toState, "" + count));
					result.addAll(generateDFAsRecursively(minimalDFA, newerDFA, states,
							nextStateNr, nextInstanceNr, getAlphabet, nextAlphabetNr, stateCount));
				}
				return result;
			}
		}
	}

	protected int computeStateNr(int stateNr, int nextAlphabetNr,
			int nextInstanceNr) {
		int nextStateNr;
		if(nextAlphabetNr > 0 || nextInstanceNr > 1)
			nextStateNr = stateNr;
		else
			nextStateNr = stateNr + 1;
		return nextStateNr;
	}

	protected int computeInstanceNr(String[] states, int stateNr,int instanceNr,
			Map<String, Integer> stateCount, int nextAlphabetNr) {
		int nextInstanceNr;
		if(nextAlphabetNr > 0)
			nextInstanceNr = instanceNr;
		else if(instanceNr < stateCount.get(states[stateNr]).intValue())
			nextInstanceNr = instanceNr + 1;
		else
			nextInstanceNr = 1;
		return nextInstanceNr;
	}

	protected int computeAlphabetNr(String[] getAlphabet, int getAlphabetNr) {
		int nextAlphabetNr;
		if(getAlphabetNr < getAlphabet.length - 1)
			nextAlphabetNr = getAlphabetNr + 1;
		else
			nextAlphabetNr = 0;
		return nextAlphabetNr;
	}
	
	protected String makeLabel(String id1, String id2){
		return id1 + "#" + id2;
	}



}
