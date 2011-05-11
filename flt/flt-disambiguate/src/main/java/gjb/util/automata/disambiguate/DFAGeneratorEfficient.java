/**
 * 
 */
package gjb.util.automata.disambiguate;

import gjb.flt.automata.impl.sparse.SparseNFA;
import gjb.flt.automata.impl.sparse.State;
import gjb.flt.automata.impl.sparse.Transition;
import gjb.util.Pair;
import gjb.util.RandomSelector;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

/**
 * @author woutergelade
 *
 */
public class DFAGeneratorEfficient extends DFAGeneratorNaive {

	private final int ENUMERATE = 0;
	private final int SEMIRANDOM = 1;
	private final int RANDOM = 2;

	private int absoluteUpperBound;

	private int totalGenerated;
	private boolean randomSearch;
	private int randomThreshold;
	private int status;

	private Set<Map<String,Integer>> previousStateCounts;
	private int previousSize;

	public DFAGeneratorEfficient() {
		randomSearch = false;
		absoluteUpperBound = 100000;
	}

	public DFAGeneratorEfficient(int threshold){
		randomSearch = true;
		randomThreshold = threshold;
		absoluteUpperBound = threshold;
		status = ENUMERATE;
	}

	public void setAbsoluteUpperbound(int bound){
		absoluteUpperBound = bound;
	}

	public Set<Map<String,Integer>> allOneStateCount(SparseNFA minimalDFA){
		Map<String,Integer> one = new HashMap<String, Integer>();
		for(String state : minimalDFA.getStateValues())
			if(!minimalDFA.isInitialState(state))
				one.put(state, new Integer(1));
		Set<Map<String,Integer>> result = new HashSet<Map<String,Integer>>();
		result.add(one);
		return result;
	}

	public Set<Map<String,Integer>> generatePossibleNextOnes(){
		Set<Map<String,Integer>> newSet = new HashSet<Map<String,Integer>>();
		for(Map<String,Integer> oldStateCount : previousStateCounts){
			for(String state : oldStateCount.keySet()){
				Map<String,Integer> newStateCount = new HashMap<String, Integer>(oldStateCount);
				newStateCount.put(state, new Integer(oldStateCount.get(state).intValue() + 1));
				newSet.add(newStateCount);
			}
		}
		return newSet;
	}

	public boolean isGoodStateCount(SparseNFA minimalDFA, Map<String,Integer> stateCount){
		Map<String,Integer> fullStateCount = new HashMap<String,Integer>(stateCount);
		fullStateCount.put(minimalDFA.getStateValue(minimalDFA.getInitialState()), new Integer(1));

		for(String state : stateCount.keySet()){
			Set<State> preSet = minimalDFA.getPreviousStates(minimalDFA.getState(state));
			int preSetSum = 0;
			for(State fromState : preSet)
				preSetSum += fullStateCount.get(minimalDFA.getStateValue(fromState)).intValue();
			if(preSetSum < stateCount.get(state).intValue())
				return false;
		}

		return true;
	}

	public Set<Map<String,Integer>> removeBadStateCounts(SparseNFA minimalDFA, Set<Map<String,Integer>> stateCountSet){
		Set<Map<String,Integer>> newSet = new HashSet<Map<String,Integer>>();
		for(Map<String,Integer> stateCount : stateCountSet)
			if(isGoodStateCount(minimalDFA,stateCount))
				newSet.add(stateCount);
		return newSet;
	}

	/* (non-Javadoc)
	 * @see gjb.util.automata.disambiguate.DFAGenerator#generateDFAs(gjb.flt.automata.impl.sparse.SparseNFA, int)
	 */
	public Set<SparseNFA> generateDFAs(SparseNFA minimalDFA, int size) {
		totalGenerated = 0;
		Set<SparseNFA> result = new HashSet<SparseNFA>();

		if(minimalDFA.getNumberOfStates() > size)
			return null;
		else if(minimalDFA.getNumberOfStates() == size){
			result.add(new SparseNFA(minimalDFA));
			previousSize = size;
			previousStateCounts = allOneStateCount(minimalDFA);
			return result;
		}
		else{
			RandomSelector<Map<String,Integer>> selector = new RandomSelector<Map<String, Integer>>();
			Set<String> stateSet = new HashSet<String>(minimalDFA.getStateValues());
			stateSet.remove(minimalDFA.getStateValue(minimalDFA.getInitialState()));
			String[] states = stateSet.toArray(new String[stateSet.size()]);
			if(!randomSearch || status == ENUMERATE){
				Set<Map<String,Integer>> stateCountMap;
				
				if(previousSize == size - 1)
					stateCountMap = generatePossibleNextOnes();
				else
					stateCountMap = generateStateCount(states,size-1);
				


				stateCountMap = removeBadStateCounts(minimalDFA, stateCountMap);
				if(stateCountMap.size() > absoluteUpperBound / 2){
					stateCountMap = selector.selectSubsetFrom(stateCountMap, absoluteUpperBound / 2);
				}


				if(randomSearch && stateCountMap.size() > randomThreshold){
					status = RANDOM;
					return generateDFAs(minimalDFA,size);
				}

				//Set<NFA> randomResultSet = new HashSet<NFA>();

				Set<Map<String,Integer>> stateCountMapCopy = new HashSet<Map<String,Integer>>(stateCountMap);
				while(!stateCountMapCopy.isEmpty()){
					Map<String,Integer> stateCount = selector.selectOneFrom(stateCountMapCopy);
					stateCountMapCopy.remove(stateCount);
					result.addAll(generateDFAsWithStates(minimalDFA, stateCount));
					if(totalGenerated >= absoluteUpperBound)
						break;

					/*for(int i = 0; i < 5; i++){
						NFA random = generateOneDFAWithStates(minimalDFA, stateCount, false);
						if(random != null)
							randomResultSet.add(random);
						random = generateOneDFAWithStates(minimalDFA, stateCount, true);
						if(random == null)
							System.err.println("Stupid greedy function");
					}*/

				}

				/*NaiveIsomorphism iso = new NaiveIsomorphism();
				try {
					if(totalGenerated < absoluteUpperBound && !iso.isContainedIn(randomResultSet, result))
						System.err.println("Generated bad DFAs");
				} catch (NotDFAException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/


				if(randomSearch && totalGenerated >= randomThreshold){
					status = SEMIRANDOM;
					return generateDFAs(minimalDFA,size);
				}
				else {
					previousSize = size;
					previousStateCounts = stateCountMap;
					return result;
				}
			}
			else if(status == SEMIRANDOM){
				Set<Map<String,Integer>> stateCountMap;
				
				
				if(previousSize == size - 1)
					stateCountMap = generatePossibleNextOnes();
				else
					stateCountMap = generateStateCount(states,size-1);

				stateCountMap = removeBadStateCounts(minimalDFA, stateCountMap);
				if(randomSearch && stateCountMap.size() > randomThreshold){
					status = RANDOM;
					return generateDFAs(minimalDFA,size);
				}
				else{
					int counter = 0;
					if(stateCountMap.isEmpty())
						return result;
					while(counter < randomThreshold && result.size() < randomThreshold){
						for(Map<String,Integer> stateCount : stateCountMap){
							SparseNFA nfa = generateOneDFAWithStates(minimalDFA, stateCount, false);
							if(nfa != null){
								result.add(nfa);
							}
							counter++;
							if(result.size() >= randomThreshold)
								break;
						}
					}
					if(result.size() < randomThreshold){
						RandomSelector<Map<String,Integer>> random = new RandomSelector<Map<String,Integer>>();
						Map<String, Integer> stateCount = random.selectOneFrom(stateCountMap);
						for(counter = result.size(); counter < randomThreshold; counter++){
							SparseNFA nfa = generateOneDFAWithStates(minimalDFA, stateCount, true);
							if(nfa != null){
								result.add(nfa);
							}
							else
								System.err.println("Didn't generate an NFA in the greedy fashion??");
						}
					}
					
					previousSize = size;
					previousStateCounts = stateCountMap;
					return result;
				}
			}
			else{
				int counter = 0;
				Set<Map<String,Integer>> newStateCounts = new HashSet<Map<String,Integer>>();
				while(counter < 10 * randomThreshold && result.size() < randomThreshold){
					Map<String,Integer> stateCount = generateOneStateCount(stateSet, size-1);
					if(isGoodStateCount(minimalDFA, stateCount)){
						newStateCounts.add(stateCount);
						SparseNFA nfa = generateOneDFAWithStates(minimalDFA, stateCount, false);
						if(nfa != null){
							result.add(nfa);
						}
					}
					counter++;
				}
				counter = 0;
				if(result.size() < randomThreshold){
					while(counter < randomThreshold && result.size() < randomThreshold){
						Map<String,Integer> stateCount = extendOneByOne();
						if(isGoodStateCount(minimalDFA, stateCount)){
							newStateCounts.add(stateCount);
							SparseNFA nfa = generateOneDFAWithStates(minimalDFA, stateCount, true);
							if(nfa != null){
								result.add(nfa);
							}
						}
						counter++;
					}
				}
				previousSize = size;
				previousStateCounts = newStateCounts;
				return result;
			}
		}
	}

	/**
	 * @param 
	 * @return
	 */
	private Map<String, Integer> extendOneByOne() {
		RandomSelector<Map<String,Integer>> selector = new RandomSelector<Map<String,Integer>>();
		Map<String,Integer> newStateCount = new HashMap<String, Integer>(selector.selectOneFrom(previousStateCounts));

		RandomSelector<String> stateSelector = new RandomSelector<String>();
		String state = stateSelector.selectOneFrom(newStateCount.keySet());
		newStateCount.put(state, new Integer(newStateCount.get(state).intValue() + 1));

		return newStateCount;
	}

	public Set<SparseNFA> generateDisjointDFAs(SparseNFA minimalDFA, int size) {
		return generateDFAs(minimalDFA,size);
	}

	protected Set<SparseNFA> generateDFAsWithStates(SparseNFA minimalDFA,Map<String,Integer> stateCount){
		String stringRep = "";
		Map<String,Integer> stateIndex = new HashMap<String, Integer>();
		for(String state : stateCount.keySet())
			stateIndex.put(state, new Integer(0));

		// list contains pairs ('minimal state','new state')
		LinkedList<Pair<String,String>> toDo = new LinkedList<Pair<String,String>>();

		SparseNFA newDFA = new SparseNFA();
		String getInitialState = "q_I";
		newDFA.setInitialState(getInitialState);
		if(minimalDFA.isFinalState(minimalDFA.getInitialState()))
			newDFA.addFinalState(getInitialState);

		for(String state : stateCount.keySet()){
			for(int count = 1; count <= stateCount.get(state).intValue(); count++){
				String newState = makeLabel(state, "" + count);
				newDFA.addState(newState);
				if(minimalDFA.isFinalState(state))
					newDFA.addFinalState(newState);
			}
			for(String symbol : minimalDFA.getAlphabet().keySet()){
				if(minimalDFA.hasTransition(symbol, minimalDFA.getStateValue(minimalDFA.getInitialState()), state)){
					newDFA.addTransition(symbol, getInitialState, makeLabel(state, "1"));
					stringRep += writeTransition(getInitialState,makeLabel(state, "1"));
					toDo.addLast(new Pair<String, String>(state,makeLabel(state,"1")));
					stateIndex.put(state, new Integer(stateIndex.get(state).intValue() + 1));
				}
			}
		}

		Map<String, Integer> remainingOpportunities = initializeRemainingOpportunities(minimalDFA, stateCount);

		/*for(String state : stateCount.keySet())
			if(remainingOpportunities.get(state).intValue() < stateCount.get(state).intValue() - stateIndex.get(state).intValue())
				return new HashSet<NFA>();*/

		String[] getAlphabet = (String[]) minimalDFA.getAlphabet().keySet().toArray(new String[minimalDFA.getAlphabet().keySet().size()]);		

		return generateDFAsRecursively(minimalDFA,newDFA,stringRep,toDo,getAlphabet,0,stateCount,stateIndex,remainingOpportunities, 0);
	}

	protected SparseNFA generateOneDFAWithStates(SparseNFA minimalDFA,Map<String,Integer> stateCount, boolean greedy){
		String stringRep = "";
		Map<String,Integer> stateIndex = new HashMap<String, Integer>();
		for(String state : stateCount.keySet())
			stateIndex.put(state, new Integer(0));

		// list contains pairs ('minimal state','new state')
		LinkedList<Pair<String,String>> toDo = new LinkedList<Pair<String,String>>();

		SparseNFA newDFA = new SparseNFA();
		String getInitialState = "q_I";
		newDFA.setInitialState(getInitialState);
		if(minimalDFA.isFinalState(minimalDFA.getInitialState()))
			newDFA.addFinalState(getInitialState);

		for(String state : stateCount.keySet()){
			for(int count = 1; count <= stateCount.get(state).intValue(); count++){
				String newState = makeLabel(state, "" + count);
				newDFA.addState(newState);
				if(minimalDFA.isFinalState(state))
					newDFA.addFinalState(newState);
			}
			for(String symbol : minimalDFA.getAlphabet().keySet()){
				if(minimalDFA.hasTransition(symbol, minimalDFA.getStateValue(minimalDFA.getInitialState()), state)){
					newDFA.addTransition(symbol, getInitialState, makeLabel(state, "1"));
					stringRep += writeTransition(getInitialState,makeLabel(state, "1"));
					toDo.addLast(new Pair<String, String>(state,makeLabel(state,"1")));
					stateIndex.put(state, new Integer(stateIndex.get(state).intValue() + 1));
				}
			}
		}

		Map<String, Integer> remainingOpportunities = initializeRemainingOpportunities(minimalDFA, stateCount);

		/*for(String state : stateCount.keySet())
			if(remainingOpportunities.get(state).intValue() < stateCount.get(state).intValue() - stateIndex.get(state).intValue())
				return new HashSet<NFA>();*/

		String[] getAlphabet = (String[]) minimalDFA.getAlphabet().keySet().toArray(new String[minimalDFA.getAlphabet().keySet().size()]);		

		return generateOneDFARecursively(minimalDFA,newDFA,stringRep,toDo,getAlphabet,0,stateCount,stateIndex,remainingOpportunities, 0,greedy);
	}

	/**
	 * @param minimalDFA
	 * @return
	 */
	private Map<String, Integer> initializeOpportunitiesOnQueue(SparseNFA minimalDFA) {
		Map<String, Integer> opportunitiesOnQueue = new HashMap<String, Integer>();
		for(String state : minimalDFA.getStateValues())
			opportunitiesOnQueue.put(state, new Integer(0));
		return opportunitiesOnQueue;
	}

	/**
	 * @param getInitialState
	 * @param makeLabel
	 * @return
	 */
	private String writeTransition(String fromState, String toState) {
		return ".." + fromState + "->" + toState;
	}

	/*	protected NFA generateOneDFAWithStates(NFA minimalDFA,Map<String,Integer> stateCount){
		Map<String,Integer> stateIndex = new HashMap<String, Integer>();
		for(String state : stateCount.keySet())
			stateIndex.put(state, new Integer(0));

		// list contains pairs ('minimal state','new state')
		LinkedList<Pair<String,String>> toDo = new LinkedList<Pair<String,String>>();

		NFA newDFA = new NFA();
		String getInitialState = "q_I";
		newDFA.setInitialState(getInitialState);
		if(minimalDFA.isFinalState(minimalDFA.getInitialState()))
			newDFA.addFinalState(getInitialState);

		for(String state : stateCount.keySet()){
			for(int count = 1; count <= stateCount.get(state).intValue(); count++){
				String newState = makeLabel(state, "" + count);
				newDFA.addState(newState);
				if(minimalDFA.isFinalState(state))
					newDFA.addFinalState(newState);
			}
			for(String symbol : minimalDFA.getAlphabet().keySet()){
				if(minimalDFA.hasTransition(symbol, minimalDFA.getStateValue(minimalDFA.getInitialState()), state)){
					newDFA.addTransition(symbol, getInitialState, makeLabel(state, "1"));
					toDo.addLast(new Pair<String, String>(state,makeLabel(state,"1")));
					stateIndex.put(state, new Integer(stateIndex.get(state).intValue() + 1));
				}
			}
		}

		Map<String, Integer> remainingOpportunities = initializeRemainingOpportunities(minimalDFA, stateCount);

		/*for(String state : stateCount.keySet())
			if(remainingOpportunities.get(state).intValue() < stateCount.get(state).intValue() - stateIndex.get(state).intValue())
				return new HashSet<NFA>();

		String[] getAlphabet = (String[]) minimalDFA.getAlphabet().keySet().toArray(new String[minimalDFA.getAlphabet().keySet().size()]);		

		return generateOneDFARecursively(minimalDFA,newDFA,toDo,getAlphabet,0,stateCount,stateIndex,remainingOpportunities, new HashSet<String>());
	}*/

	private Map<String, Integer> initializeRemainingOpportunities(
			SparseNFA minimalDFA, Map<String, Integer> stateCount) {
		Map<String,Integer> remainingOpportunities = new HashMap<String, Integer>();
		for(String state : stateCount.keySet()){
			/*if(minimalDFA.getNextStates(minimalDFA.getState(state)).contains(minimalDFA.getState(state)))
				remainingOpportunities.put(state, new Integer(-1));
			else*/
			remainingOpportunities.put(state, new Integer(0));
		}
		for(Transition transition : minimalDFA.getTransitionMap().getTransitions()){
			if(transition.getFromState() != minimalDFA.getInitialState()){
				String toState = minimalDFA.getStateValue(transition.getToState());
				int newValue = remainingOpportunities.get(toState).intValue() + stateCount.get(minimalDFA.getStateValue(transition.getFromState()));
				remainingOpportunities.put(toState, new Integer(newValue));
			}
		}
		return remainingOpportunities;
	}

	protected long numberOfStateCounts(int total, int places){
		if(places == 1 && total > 0)
			return 1;
		else if(places == 1)
			return 0;
		else{
			int result = 0;
			for(int i = 1; i + places - 1 <= total; i++)
				result += numberOfStateCounts(total - i, places - 1);
			return result;
		}
	}

	/*private NFA generateOneDFARecursively(NFA minimalDFA, NFA newDFA,
			LinkedList<Pair<String, String>> toDo, String[] getAlphabet, int getAlphabetNr,
			Map<String, Integer> stateCount, Map<String, Integer> stateIndex,
			Map<String, Integer> remainingOpportunities, Set<String> done) {


		if(toDo.isEmpty()){
			int originalSize = newDFA.getNumberOfStates();
			newDFA.simplify();
			if(newDFA.getNumberOfStates() == originalSize){
				if(done.size() != originalSize - 1)
					System.out.println("Didn't do all states in random generation");
				return newDFA;
			}
			else
				return null;
		}
		else{
			String fromState = toDo.getFirst().getFirst();
			String fromStateNew = toDo.getFirst().getSecond();
			done.add(fromState);
			int newAlphabetNr = computeAlphabetNr(getAlphabet, getAlphabetNr);
			if(newAlphabetNr == 0)
				toDo.removeFirst();

			Set<State> nextStates = minimalDFA.getNextStates(minimalDFA.getAlphabet().get(getAlphabet[getAlphabetNr]),minimalDFA.getState(fromState));

			if(nextStates.isEmpty())
				return generateOneDFARecursively(minimalDFA,newDFA,toDo,getAlphabet,newAlphabetNr,stateCount,stateIndex,remainingOpportunities,done);
			else {
				String toState = minimalDFA.getStateValue(gjb.util.Collections.getOne(nextStates));

				boolean toOldStateAllowed = toOldStateAllowed(stateCount,
						stateIndex, remainingOpportunities, fromState, toState);

				boolean toNewStateAllowed = stateIndex.get(toState).intValue() < stateCount.get(toState).intValue();


				if(toNewStateAllowed && (!toOldStateAllowed || Math.random() > 0.5)){

					return addTransitionToNewState(minimalDFA, newDFA, toDo,
							getAlphabet, getAlphabetNr, stateCount, stateIndex,
							remainingOpportunities, fromStateNew,
							newAlphabetNr, toState,done);
				} else{
					int position = (int) (Math.random() * stateIndex.get(toState).intValue()) + 1;
					return addTransitionToOldState(minimalDFA, newDFA,
							toDo, getAlphabet, getAlphabetNr, stateCount,
							stateIndex, remainingOpportunities,
							fromStateNew, newAlphabetNr, toState, position,done);					
				} 
			}
		}
	}*/

	private void updateOpportunitiesOnQueue(Map<String,Integer> opportunitesOnQueue, String state, SparseNFA minimalDFA){
		for(String symbol : minimalDFA.getAlphabet().keySet()){
			if(!minimalDFA.getNextStates(minimalDFA.getAlphabet().get(symbol), minimalDFA.getState(state)).isEmpty()){
				String toState = minimalDFA.getStateValue(gjb.util.Collections.getOne(minimalDFA.getNextStates(minimalDFA.getState(state))));
				for(String reachableState : reachableStatesFrom(toState, minimalDFA))
					opportunitesOnQueue.put(reachableState, new Integer(opportunitesOnQueue.get(reachableState).intValue() + 1));
			}
		}


		/*for(State toState : minimalDFA.getNextStates(minimalDFA.getState(state))){
			String toStateValue = minimalDFA.getStateValue(toState);
			opportunitesOnQueue.put(toStateValue, new Integer(opportunitesOnQueue.get(toStateValue).intValue() + 1));
		}*/
	}

	/**
	 * @param toState
	 * @param minimalDFA
	 * @return
	 */
	private Set<String> reachableStatesFrom(String fromState, SparseNFA minimalDFA) {
		Set<String> reachable = new HashSet<String>();
		Stack<String> toDo = new Stack<String>();

		reachable.add(fromState);
		toDo.add(fromState);

		while(!toDo.isEmpty()){
			String current = toDo.pop();
			for(State toState : minimalDFA.getNextStates(minimalDFA.getState(current))){
				String toStateValue = minimalDFA.getStateValue(toState);
				if(!reachable.contains(toStateValue)){
					reachable.add(toStateValue);
					toDo.push(toStateValue);
				}
			}
		}

		return reachable;
	}

	private boolean toOldStateAllowed(Map<String, Integer> stateCount,
			Map<String, Integer> stateIndex,
			Map<String, Integer> remainingOpportunities, String fromState,
			String toState, SparseNFA minimalDFA) {
		/*if(opportunitiesOnQueue.get(toState).intValue() == 0)
			System.err.println("Oeps");*/
		if(stateIndex.get(toState).intValue() == stateCount.get(toState).intValue())
			return true;
		else if(remainingOpportunities.get(toState).intValue() <= stateCount.get(toState).intValue() - stateIndex.get(toState).intValue())
			return false;
		else 
			return true;

		/*			if(remainingOpportunities.get(toState).intValue() <= stateCount.get(toState).intValue() - stateIndex.get(toState).intValue())
			return false;
		else
			return opportunitiesOnQueue.get(toState) > 1;*/



		/*	if(!minimalDFA.getNextStates(minimalDFA.getState(toState)).contains(minimalDFA.getState(toState))){
			return remainingOpportunities.get(toState).intValue() > stateCount.get(toState).intValue() - stateIndex.get(toState).intValue();
		}
		else {
			return remainingOpportunities.get(toState).intValue() > stateCount.get(toState).intValue() - stateIndex.get(toState).intValue() + 1;
		}	*/
	}

	/**
	 * @param toState
	 * @param minimalDFA
	 * @return
	 */
	/*private boolean hasSelfLoop(String toState, NFA minimalDFA) {
		return minimalDFA.getNextStates(minimalDFA.getState(toState)).contains(minimalDFA.getState(toState));
	}*/

	/*private NFA addTransitionToOldState(NFA minimalDFA, NFA newDFA,
			LinkedList<Pair<String, String>> toDo, String[] getAlphabet,
			int getAlphabetNr, Map<String, Integer> stateCount,
			Map<String, Integer> stateIndex,
			Map<String, Integer> remainingOpportunities, String fromStateNew,
			int newAlphabetNr, String toState, int position, Set<String> done) {
		updateRemoveOpportunitiesOnQueue(toState, remainingOpportunities);
		String toStateNew = makeLabel(toState, "" + position);
		newDFA.addTransition(getAlphabet[getAlphabetNr], fromStateNew, toStateNew);
		Set<String> doneNew = new HashSet<String>(done);
		return generateOneDFARecursively(minimalDFA,newDFA,toDo,getAlphabet,newAlphabetNr,stateCount,stateIndex,remainingOpportunities,doneNew);
	}

	private NFA addTransitionToNewState(NFA minimalDFA, NFA newDFA,
			LinkedList<Pair<String, String>> toDo, String[] getAlphabet,
			int getAlphabetNr, Map<String, Integer> stateCount,
			Map<String, Integer> stateIndex,
			Map<String, Integer> remainingOpportunities, String fromStateNew,
			int newAlphabetNr, String toState, Set<String> done) {
		updateRemoveOpportunitiesOnQueue(toState, remainingOpportunities);
		stateIndex.put(toState, new Integer(stateIndex.get(toState).intValue() + 1));
		String toStateNew = makeLabel(toState, "" + stateIndex.get(toState).intValue());
		newDFA.addTransition(getAlphabet[getAlphabetNr], fromStateNew, toStateNew);
		toDo.addLast(new Pair<String,String>(toState,toStateNew));
		Set<String> doneNew = new HashSet<String>(done);
		return generateOneDFARecursively(minimalDFA,newDFA,toDo,getAlphabet,newAlphabetNr,stateCount,stateIndex,remainingOpportunities,doneNew);
	}*/



	/**
	 * @param minimalDFA
	 * @param newDFA
	 * @param toDo.g
	 * @param getAlphabet
	 * @param instanceNr
	 * @param stateCount
	 * @param stateIndex
	 * @param remainingOpportunities
	 * @return
	 */
	private Set<SparseNFA> generateDFAsRecursively(SparseNFA minimalDFA, SparseNFA newDFA, String stringRep, 
			LinkedList<Pair<String, String>> toDo, String[] getAlphabet, int getAlphabetNr,
			Map<String, Integer> stateCount, Map<String, Integer> stateIndex,
			Map<String, Integer> remainingOpportunities, int numberDone) {
		if(totalGenerated >= absoluteUpperBound)
			return new HashSet<SparseNFA>();

		Set<SparseNFA> result = new HashSet<SparseNFA>();
		if(toDo.isEmpty()){
			if(numberDone == newDFA.getNumberOfStates() - 1){
				result.add(newDFA);
				totalGenerated++;
			}


			/*int originalSize = newDFA.getNumberOfStates();
			//DeterministicExpressionExploreTest.writeDot("before.dot", newDFA);
			newDFA.simplify();
			if(newDFA.getNumberOfStates() == originalSize && (totalGenerated < absoluteUpperBound)){
				result.add(newDFA);
				totalGenerated++;
				if(done.size() != originalSize - 1)
					System.out.println("Didn't do all states!");
			}*/
			/*else {
				DeterministicExpressionExploreTest.writeDot("notGood.dot", newDFA);
				DeterministicExpressionExploreTest.writeDot("originalOne.dot", minimalDFA);
				System.out.println(stringRep);
				System.out.println("Error : constructed a simplifiable automaton (this if can be almost completely be removed if this is never printed)");

			}*/
			return result;
		}
		else{
			String fromState = toDo.getFirst().getFirst();
			String fromStateNew = toDo.getFirst().getSecond();
			if(getAlphabetNr == 0){
				numberDone++;
			}

			/*if(getAlphabetNr == 0 && !opportunitiesOnQueue.equals(computeOpportunitiesOnQueue(toDo, minimalDFA))){
				Set<State> nextStates = minimalDFA.getNextStates(minimalDFA.getAlphabet().get(getAlphabet[getAlphabetNr]),minimalDFA.getState(fromState));
				String toState = minimalDFA.getStateValue(gjb.util.Collections.getOne(nextStates));
				System.out.println("Handling" + fromStateNew + " to " + toState);
			System.out.println(stringRep);
			System.out.println(toDo);
			System.out.println("StateCount" + stateCount);
			System.out.println("StateIndex" + stateIndex);
			System.out.println("RemaingingOpportunites" + remainingOpportunities);
			System.out.println("OpportunitiesOnQueue" + opportunitiesOnQueue);
			System.out.println(computeOpportunitiesOnQueue(toDo, minimalDFA));
			System.out.println(minimalDFA);
			}*/

			int newAlphabetNr = computeAlphabetNr(getAlphabet, getAlphabetNr);
			if(newAlphabetNr == 0){
				toDo.removeFirst();
			}

			Set<State> nextStates = minimalDFA.getNextStates(minimalDFA.getAlphabet().get(getAlphabet[getAlphabetNr]),minimalDFA.getState(fromState));

			if(nextStates.isEmpty())
				return generateDFAsRecursively(minimalDFA,newDFA,stringRep,toDo,getAlphabet,newAlphabetNr,stateCount,stateIndex,remainingOpportunities,numberDone);
			else {
				String toState = minimalDFA.getStateValue(gjb.util.Collections.getOne(nextStates));
				boolean toNewStateAllowed = stateIndex.get(toState).intValue() < stateCount.get(toState).intValue();
				boolean toOldStateAllowed = toOldStateAllowed(stateCount,
						stateIndex, remainingOpportunities, fromState, toState, minimalDFA);

				/*System.out.println("Handling" + fromStateNew + " to " + toState);
				System.out.println(stringRep);
				System.out.println(toDo);
				System.out.println("StateCount" + stateCount);
				System.out.println("StateIndex" + stateIndex);
				System.out.println("RemaingingOpportunites" + remainingOpportunities);
				System.out.println("OpportunitiesOnQueue" + opportunitiesOnQueue);*/


				if(toOldStateAllowed){
					for(int position = 1; position <= stateIndex.get(toState).intValue(); position++){
						SparseNFA newerDFA = new SparseNFA(newDFA);
						Map<String,Integer> remainingOpportunitiesNew = new HashMap<String, Integer>(remainingOpportunities);
						remainingOpportunitiesNew.put(toState,new Integer(remainingOpportunitiesNew.get(toState).intValue() - 1));
						Map<String,Integer> stateIndexNew = new HashMap<String, Integer>(stateIndex);
						LinkedList<Pair<String,String>> toDoNew = new LinkedList<Pair<String,String>>(toDo);
						String toStateNew = makeLabel(toState, "" + position);
						newerDFA.addTransition(getAlphabet[getAlphabetNr], fromStateNew, toStateNew);
						String newStringRep = stringRep + writeTransition(fromStateNew, toStateNew);
						Set<SparseNFA> recursiveResult = generateDFAsRecursively(minimalDFA,newerDFA,newStringRep,toDoNew,getAlphabet,newAlphabetNr,stateCount,stateIndexNew,remainingOpportunitiesNew,numberDone);
						if(recursiveResult.isEmpty())
							break;
						result.addAll(recursiveResult);
					}
				}
				if(toNewStateAllowed){
					remainingOpportunities.put(toState,new Integer(remainingOpportunities.get(toState).intValue() - 1));
					stateIndex.put(toState, new Integer(stateIndex.get(toState).intValue() + 1));
					String toStateNew = makeLabel(toState, "" + stateIndex.get(toState).intValue());
					newDFA.addTransition(getAlphabet[getAlphabetNr], fromStateNew, toStateNew);
					stringRep += writeTransition(fromStateNew, toStateNew);
					toDo.addLast(new Pair<String,String>(toState,toStateNew));
					result.addAll(generateDFAsRecursively(minimalDFA,newDFA,stringRep,toDo,getAlphabet,newAlphabetNr,stateCount,stateIndex,remainingOpportunities,numberDone));
				}
				return result;
			}
		}
	}

	private SparseNFA generateOneDFARecursively(SparseNFA minimalDFA, SparseNFA newDFA, String stringRep, 
			LinkedList<Pair<String, String>> toDo, String[] getAlphabet, int getAlphabetNr,
			Map<String, Integer> stateCount, Map<String, Integer> stateIndex,
			Map<String, Integer> remainingOpportunities, int numberDone, boolean greedy) {
		if(totalGenerated >= absoluteUpperBound)
			return null;

		Set<SparseNFA> result = new HashSet<SparseNFA>();
		if(toDo.isEmpty()){
			if(numberDone == newDFA.getNumberOfStates() - 1){
				totalGenerated++;
				return newDFA;
			}
			else
				return null;
		}
		else{
			String fromState = toDo.getFirst().getFirst();
			String fromStateNew = toDo.getFirst().getSecond();
			if(getAlphabetNr == 0){
				numberDone++;
			}

			int newAlphabetNr = computeAlphabetNr(getAlphabet, getAlphabetNr);
			if(newAlphabetNr == 0){
				toDo.removeFirst();
			}

			Set<State> nextStates = minimalDFA.getNextStates(minimalDFA.getAlphabet().get(getAlphabet[getAlphabetNr]),minimalDFA.getState(fromState));

			if(nextStates.isEmpty())
				return generateOneDFARecursively(minimalDFA,newDFA,stringRep,toDo,getAlphabet,newAlphabetNr,stateCount,stateIndex,remainingOpportunities,numberDone,greedy);
			else {
				String toState = minimalDFA.getStateValue(gjb.util.Collections.getOne(nextStates));
				boolean toNewStateAllowed = stateIndex.get(toState).intValue() < stateCount.get(toState).intValue();
				boolean toOldStateAllowed = toOldStateAllowed(stateCount,
						stateIndex, remainingOpportunities, fromState, toState, minimalDFA);

				if(toOldStateAllowed && (!toNewStateAllowed || (!greedy && Math.random() > 0.6))){
					int position = (int) (Math.random() * stateIndex.get(toState).intValue()) + 1;
					remainingOpportunities.put(toState,new Integer(remainingOpportunities.get(toState).intValue() - 1));
					String toStateNew = makeLabel(toState, "" + position);
					newDFA.addTransition(getAlphabet[getAlphabetNr], fromStateNew, toStateNew);
					stringRep = stringRep + writeTransition(fromStateNew, toStateNew);
					return generateOneDFARecursively(minimalDFA,newDFA,stringRep,toDo,getAlphabet,newAlphabetNr,stateCount,stateIndex,remainingOpportunities,numberDone,greedy);				
				}
				else if(toNewStateAllowed){
					remainingOpportunities.put(toState,new Integer(remainingOpportunities.get(toState).intValue() - 1));
					stateIndex.put(toState, new Integer(stateIndex.get(toState).intValue() + 1));
					String toStateNew = makeLabel(toState, "" + stateIndex.get(toState).intValue());
					newDFA.addTransition(getAlphabet[getAlphabetNr], fromStateNew, toStateNew);
					stringRep += writeTransition(fromStateNew, toStateNew);
					toDo.addLast(new Pair<String,String>(toState,toStateNew));
					return generateOneDFARecursively(minimalDFA,newDFA,stringRep,toDo,getAlphabet,newAlphabetNr,stateCount,stateIndex,remainingOpportunities,numberDone,greedy);
				}
				else
					return null;
			}
		}
	}

	/*private void updateRemoveOpportunitiesOnQueue(String toState,
			Map<String, Integer> newOpportunitiesOnQueue) {
		newOpportunitiesOnQueue.put(toState,new Integer(newOpportunitiesOnQueue.get(toState).intValue() - 1));
	}*/

	/*private void updateRemoveOpportunitiesOnQueue(String state,
			Map<String, Integer> opportunitiesOnQueue, NFA minimalDFA) {
		for(String toState : reachableStatesFrom(state, minimalDFA))
			opportunitiesOnQueue.put(toState, new Integer(opportunitiesOnQueue.get(toState) - 1));

		//newOpportunitiesOnQueue.put(toState,new Integer(newOpportunitiesOnQueue.get(toState).intValue() - 1));
	}*/

	/*private Map<String,Integer> computeOpportunitiesOnQueue(LinkedList<Pair<String,String>> toDo, NFA minimalDFA){
		Map<String,Integer> opportunities = initializeOpportunitiesOnQueue(minimalDFA);
		for(Pair<String,String> pair : toDo){
			updateOpportunitiesOnQueue(opportunities, pair.getFirst(), minimalDFA);
		}
		return opportunities;
	}*/

	/*private NFA generateOneDFARecursively(NFA minimalDFA, NFA newDFA,
			LinkedList<Pair<String, String>> toDo, String[] getAlphabet, int getAlphabetNr,
			Map<String, Integer> stateCount, Map<String, Integer> stateIndex,
			Map<String, Integer> remainingOpportunities, Set<String> done) {
		if(toDo.isEmpty()){
			int originalSize = newDFA.getNumberOfStates();
			//DeterministicExpressionExploreTest.writeDot("before.dot", newDFA);
			newDFA.simplify();
			if(newDFA.getNumberOfStates() == originalSize && (totalGenerated < absoluteUpperBound)){
				if(done.size() != originalSize - 1)
					System.out.println("Didn't do all states!");
				return newDFA;

			}
			else
				return null;
		}
		else{
			String fromState = toDo.getFirst().getFirst();
			String fromStateNew = toDo.getFirst().getSecond();
			done.add(fromStateNew);
			int newAlphabetNr = computeAlphabetNr(getAlphabet, getAlphabetNr);
			if(newAlphabetNr == 0)
				toDo.removeFirst();

			Set<State> nextStates = minimalDFA.getNextStates(minimalDFA.getAlphabet().get(getAlphabet[getAlphabetNr]),minimalDFA.getState(fromState));

			if(nextStates.isEmpty())
				return generateOneDFARecursively(minimalDFA,newDFA,toDo,getAlphabet,newAlphabetNr,stateCount,stateIndex,remainingOpportunities,done);
			else {
				String toState = minimalDFA.getStateValue(gjb.util.Collections.getOne(nextStates));
				boolean toNewStateAllowed = stateIndex.get(toState).intValue() < stateCount.get(toState).intValue();

				//toOldStateAllowed(stateCount,
				//	stateIndex, remainingOpportunities, fromState, toState, minimalDFA);


				if (toNewStateAllowed) {
					stateIndex.put(toState, new Integer(stateIndex.get(toState).intValue() + 1));
					String toStateNew = makeLabel(toState, "" + stateIndex.get(toState).intValue());
					newDFA.addTransition(getAlphabet[getAlphabetNr], fromStateNew, toStateNew);
					toDo.addLast(new Pair<String,String>(toState,toStateNew));
					Set<String> doneNew = new HashSet<String>(done);
					return generateOneDFARecursively(minimalDFA,newDFA,toDo,getAlphabet,newAlphabetNr,stateCount,stateIndex,remainingOpportunities,doneNew);
				}
				else {
					int position = (int)(Math.random() * stateIndex.get(toState).intValue()) + 1;
					NFA newerDFA = new NFA(newDFA);
					Map<String,Integer> remainingOpportunitiesNew = new HashMap<String, Integer>(remainingOpportunities);		
					Map<String,Integer> stateIndexNew = new HashMap<String, Integer>(stateIndex);
					LinkedList<Pair<String,String>> toDoNew = new LinkedList<Pair<String,String>>(toDo);
					String toStateNew = makeLabel(toState, "" + position);
					newerDFA.addTransition(getAlphabet[getAlphabetNr], fromStateNew, toStateNew);
					Set<String> doneNew = new HashSet<String>(done);
					return generateOneDFARecursively(minimalDFA,newerDFA,toDoNew,getAlphabet,newAlphabetNr,stateCount,stateIndexNew,remainingOpportunitiesNew,doneNew);

				}

			}
		}
	}*/


}
