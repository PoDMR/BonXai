package gjb.util.automata.disambiguate;

import gjb.flt.automata.converters.AlphabetCleaner;
import gjb.flt.automata.impl.sparse.SparseNFA;
import gjb.flt.automata.NotDFAException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class NaiveIsomorphism implements Isomorphism {

	public Set<SparseNFA> removeIsomporhicDFAs(Set<SparseNFA> DFAs) {
		Set<SparseNFA> result = new HashSet<SparseNFA>();
		
		for(SparseNFA dfa : DFAs){
			boolean notIsomorphic = true;
			for(SparseNFA resultDFA : result){
				notIsomorphic = !areIsomorphic(dfa, resultDFA);
				if(!notIsomorphic)
					break;
			}
			if(notIsomorphic)
				result.add(dfa);
		}
		
		return result;
	}

	public boolean areIsomorphic(SparseNFA dfa1, SparseNFA dfa2){
		new AlphabetCleaner().clean(dfa1);
		new AlphabetCleaner().clean(dfa2);
	
		if(!(NaiveIsomorphism.areEqual(dfa1.getAlphabet().keySet(),dfa2.getAlphabet().keySet())))
			return false;
	
		Map<String,String> bijection = new HashMap<String,String>();
		Map<String,String> toDo = new HashMap<String,String>();
	
		toDo.put(dfa1.getStateValue(dfa1.getInitialState()), dfa2.getStateValue(dfa2.getInitialState()));
		bijection.put(dfa1.getStateValue(dfa1.getInitialState()), dfa2.getStateValue(dfa2.getInitialState()));
	
		while(!toDo.isEmpty()){
			String state1 = gjb.util.Collections.getOne(toDo.keySet());
			String state2 = toDo.get(state1);
			toDo.remove(state1);
	
			if(dfa1.isFinalState(dfa1.getState(state1)) != dfa2.isFinalState(dfa2.getState(state2)))
				return false;
	
			for(String symbol : dfa1.getAlphabet().keySet()){
				Set<String> toStates1 = dfa1.getNextStateValues(symbol, state1);
				Set<String> toStates2 = dfa2.getNextStateValues(symbol, state2);
	
				if(!(toStates1.size() == toStates2.size()) || toStates1.size() > 1)
					return false;
				else if(toStates1.size() == 1){
					String toState1 = gjb.util.Collections.extractSingleton(toStates1);
					String toState2 = gjb.util.Collections.extractSingleton(toStates2);
					if(bijection.containsKey(toState1) && !(bijection.get(toState1) == toState2))
						return false;
					else if(!bijection.containsKey(toState1)){
						bijection.put(toState1, toState2);
						toDo.put(toState1, toState2);
					}
	
	
				}
			}
		}
		return true;
	}
	
	public static boolean areEqual(Set<String> set1, Set<String> set2) {
		//System.out.println(set1 + " " + set2);
		return set1.containsAll(set2) && set2.containsAll(set1);
	}

	/* (non-Javadoc)
	 * @see gjb.util.automata.disambiguate.Isomorphism#isContainedIn(gjb.flt.automata.impl.sparse.SparseNFA, java.util.Set)
	 */
	public boolean isContainedIn(SparseNFA dfa, Set<SparseNFA> DFAs) throws NotDFAException {
		for(SparseNFA dfaSuper : DFAs)
			if(areIsomorphic(dfa,dfaSuper))
				return true;
		
		return false;
	}

	/* (non-Javadoc)
	 * @see gjb.util.automata.disambiguate.Isomorphism#isContainedIn(java.util.Set, java.util.Set)
	 */
	public boolean isContainedIn(Set<SparseNFA> subset, Set<SparseNFA> superset)
			throws NotDFAException {
		for(SparseNFA dfa : subset){
			if(!isContainedIn(dfa,superset))
				return false;
		}
		return true;
	}
	
	public boolean areIsomorphicallyEqual(Set<SparseNFA> set1, Set<SparseNFA> set2) throws NotDFAException {
		return isContainedIn(set1, set2) && isContainedIn(set2, set1);
	}

}
