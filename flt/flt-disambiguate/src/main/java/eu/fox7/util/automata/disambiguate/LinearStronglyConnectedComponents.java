/**
 * 
 */
package eu.fox7.util.automata.disambiguate;

import eu.fox7.flt.automata.NFA;
import eu.fox7.flt.automata.impl.sparse.SparseNFA;
import eu.fox7.flt.automata.impl.sparse.State;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;


/**
 * @author woutergelade
 *
 */
public class LinearStronglyConnectedComponents implements StronglyConnectedComponents {
	private Set<HashSet<String>> result;
	private int index;
	private Stack<String> stack;
	private Map<String,Integer> stateIndex;
	private Map<String,Integer> stateLowLink;
	private Set<String> onStack;
	
	public Set<HashSet<String>> computeAllStronglyConnectedComponents(NFA nfa) {
		Set<String> toDo = new HashSet<String>();
		toDo.addAll(nfa.getStateValues());
		Set<HashSet<String>> result = new HashSet<HashSet<String>>();
		
		while(!toDo.isEmpty()){
			String currentState = eu.fox7.util.Collections.getOne(toDo);
			Set<HashSet<String>> newResult = computeStronglyConnectedComponents(nfa,currentState);
			for(HashSet<String> scc : newResult)
				toDo.removeAll(scc);
			result.addAll(newResult);
		}
		
		return result;
	}
	
	public Set<HashSet<String>> computeStronglyConnectedComponents(NFA nfa, String getInitialState){
		
		result = new HashSet<HashSet<String>>();
		index = 0;
		stack = new Stack();
		onStack = new HashSet<String>();
		stateIndex = new HashMap<String,Integer>();
		stateLowLink = new HashMap<String,Integer>();
		//onStack = new HashSet<String>();
		
		computeSCCRecursively(nfa,getInitialState);
		return result;
	}


	/**
	 * @param nfa
	 * @param getInitialState
	 */
	private void computeSCCRecursively(NFA nfa, String currentState) {
		SparseNFA snfa = (SparseNFA)nfa;
		stateIndex.put(currentState, new Integer(index));
		stateLowLink.put(currentState, index);
		index++;
		
		stack.push(currentState);
		onStack.add(currentState);
		Set<State> next =  snfa.getNextStates(snfa.getState(currentState));
		Set<String> nextStates = new HashSet<String>();
		for(State state : next)
			nextStates.add(snfa.getStateValue(state));
		
		for(String nextState : nextStates){
			if(!stateIndex.containsKey(nextState)){
				computeSCCRecursively(snfa,nextState);
				stateLowLink.put(currentState, Math.min(stateLowLink.get(currentState).intValue(), stateLowLink.get(nextState).intValue()));
			}
			else if(onStack.contains(nextState))
				stateLowLink.put(currentState, Math.min(stateLowLink.get(currentState).intValue(), stateLowLink.get(nextState).intValue()));
		}
		
		if(stateIndex.get(currentState).intValue() == stateLowLink.get(currentState).intValue()){
			HashSet<String> scc = new HashSet<String>();
			scc.add(currentState);
			while(!currentState.equals(stack.peek())){
				onStack.remove(stack.peek());
				scc.add(stack.pop());
			}
			onStack.remove(stack.peek());
			stack.pop();
			result.add(scc);
		}	
	}
}
