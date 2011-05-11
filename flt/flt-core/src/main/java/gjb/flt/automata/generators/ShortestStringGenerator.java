/**
 * Created on Oct 10, 2009
 * Modified on $Date: 2009-11-12 09:10:15 $
 */
package gjb.flt.automata.generators;

import gjb.flt.automata.NotDFAException;
import gjb.flt.automata.factories.sparse.Determinizer;
import gjb.flt.automata.impl.sparse.State;
import gjb.flt.automata.impl.sparse.StateNFA;
import gjb.flt.automata.impl.sparse.Transition;
import gjb.util.CarthesianProduct;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author lucg5005
 * @version $Revision: 1.3 $
 *
 */
public class ShortestStringGenerator {

	protected StateNFA dfa;

	public ShortestStringGenerator(StateNFA nfa) {
		this.dfa = Determinizer.dfa(nfa);
	}

	public List<List<State>> shortestStatePaths() throws NotDFAException {
        if (!dfa.isDFA())
            throw new NotDFAException();
        Map<State,Set<State>> previousStates = new HashMap<State,Set<State>>();
        Map<State,Integer> distances = new HashMap<State,Integer>();
        Set<State> workList = new HashSet<State>();
        int distance = 0;
        boolean finalStateReached = false;
        previousStates.put(dfa.getInitialState(), null);
        distances.put(dfa.getInitialState(), distance);
        if (dfa.isFinalState(dfa.getInitialState()))
            finalStateReached = true;
        workList.add(dfa.getInitialState());
        while (!finalStateReached && workList.size() > 0) {
            distance++;
            Set<State> newWorkList = new HashSet<State>();
            for (State workListState : workList) {
                Set<State> nextStates = dfa.getNextStates(workListState);
                for (State nextState : nextStates) {
                    if (!distances.containsKey(nextState)) {
                        if (!previousStates.containsKey(nextState))
                            previousStates.put(nextState, new HashSet<State>());
                        previousStates.get(nextState).add(workListState);
                        newWorkList.add(nextState);
                    }
                }
            }
            for (State state : newWorkList) {
                distances.put(state, distance);
                if (dfa.isFinalState(state))
                    finalStateReached = true;
            }
            workList = newWorkList;
        }
        List<List<State>> paths = new LinkedList<List<State>>();
        for (State state : workList)
            if (dfa.isFinalState(state)) {
                List<State> path = new LinkedList<State>();
                path.add(state);
                paths.add(path);
            }
        boolean done = false || !finalStateReached;
        while (!done) {
            List<List<State>> newPaths = new LinkedList<List<State>>();
            for (List<State> path : paths) {
                Set<State> previous = previousStates.get(path.get(0));
                if (previous != null)
                    for (State state : previous) {
                        LinkedList<State> newPath = new LinkedList<State>(path);
                        newPath.addFirst(state);
                        newPaths.add(newPath);
                    }
                else
                    done = true;
            }
            if (!done)
                paths = newPaths;
        }
        return paths;
    }

    public List<List<String>> shortestPaths() {
        List<List<State>> paths = null;
        try {
            paths = shortestStatePaths();
        } catch (NotDFAException e) {
            throw new Error("Unexpected NotDFAException thrown");
        }
        List<List<String>> results = new LinkedList<List<String>>();
        for (List<State> path : paths) {
            results.addAll(shortestStrings(path));
        }
        return results;
    }

    public int shortestAcceptedStringLength() {
        List<List<String>> paths = shortestPaths();
        if (paths.size() > 0)
            return paths.iterator().next().size();
        else
            return -1;
    }

    @SuppressWarnings("unchecked")
    public List<List<String>> shortestStrings(List<State> stateSequence) {
        List<Set<String>> result = new LinkedList<Set<String>>();
        for (int i = 1; i < stateSequence.size(); i++)
            result.add(transitionSymbols(stateSequence.get(i - 1),
                                         stateSequence.get(i)));
        CarthesianProduct product = new CarthesianProduct(result);
        return product.all();
    }

    protected Set<String> transitionSymbols(State fromState, State toState) {
    	Set<String> symbols = new HashSet<String>();
    	for (Transition transition : dfa.getOutgoingTransitions(fromState))
    		if (dfa.hasState(transition.getToState()) &&
    				transition.getToState().equals(toState))
    			symbols.add(transition.getSymbol().toString());
    	return symbols;
    }

}
