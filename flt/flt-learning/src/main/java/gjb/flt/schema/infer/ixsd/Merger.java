/*
 * Created on Nov 2, 2006
 * Modified on $Date: 2009-11-10 14:01:49 $
 */
package gjb.flt.schema.infer.ixsd;

import gjb.flt.automata.factories.sparse.StateMerger;
import gjb.flt.automata.impl.sparse.SparseNFA;
import gjb.flt.automata.impl.sparse.State;
import gjb.flt.automata.impl.sparse.Symbol;
import gjb.flt.treeautomata.factories.SupportContextAutomatonFactory;
import gjb.flt.treeautomata.impl.ContentAutomaton;
import gjb.flt.treeautomata.impl.ContextAutomaton;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author gjb
 * @version $Revision: 1.3 $
 * 
 */
public class Merger {

    protected SupportContextAutomatonFactory factory;
    protected ContextEquivalenceRelation equiv;
    protected ContentEquivalenceRelation contentEquiv;
    protected StateMerger<ContentAutomaton,Integer> stateMerger;
	protected ContextAutomaton contextAutomaton;

    public Merger(SupportContextAutomatonFactory factory,
                  ContextAutomaton automaton) {
    	this.factory = factory;
    	this.contextAutomaton = automaton;
        this.stateMerger = new StateMerger<ContentAutomaton,Integer>();
        this.stateMerger.setAnnotationMerger(new ContextMerger());
        this.stateMerger.setRenamer(new StateRenamer(factory));
    }

    public void merge() {
        if (this.contentEquiv == null)
            this.equiv = new ContextEquivalenceRelation(getAutomaton());
        else
            this.equiv = new ContextEquivalenceRelation(getAutomaton(), getContentEquivalenceRelation());
        List<Set<State>> stateSetList = null;
        while (!(stateSetList = findMergeCandidates()).isEmpty())
            merge(stateSetList);
    }
    
    protected void merge(List<Set<State>> stateSetList) {
        for (Set<State> stateSet : stateSetList)
            mergeStateSet(stateSet);
        Set<State> stateSet = null;
        while (!(stateSet = findStateCandidates()).isEmpty())
            mergeStateSet(stateSet);
    }

    protected State mergeStateSet(Set<State> stateSet) {
        ContextAutomaton nfa = getAutomaton();
        State state1 = gjb.util.Collections.takeOne(stateSet);
        assert state1 != null;
        while (!stateSet.isEmpty()) {
            Map<State,State> stateMap = new HashMap<State,State>();
            State state2 = gjb.util.Collections.takeOne(stateSet);
            assert state2 != null;
            State newState = stateMerger.addMergeState(nfa, state1, state2);
            stateMap.put(state1, newState);
            stateMap.put(state2, newState);
            stateMerger.mergeStates(nfa, stateMap);
            state1 = newState;
        }
        return state1;
    }

    protected List<Set<State>> findMergeCandidates() {
        SparseNFA nfa = getAutomaton();
        Map<State,Set<State>> setsMap = new HashMap<State,Set<State>>();
        Set<State> done = new HashSet<State>();
        done.add(nfa.getInitialState());
        for (State state1 : nfa.getStates()) {
            if (done.contains(state1)) continue;
            done.add(state1);
            for (State state2 : nfa.getStates()) {
                if (done.contains(state2)) continue;
                if (equiv.areEquivalent(state1, state2)) {
                    if (setsMap.containsKey(state1)) {
                        Set<State> set = setsMap.get(state1);
                        if (setsMap.containsKey(state2)) {
                            set.addAll(setsMap.get(state2));
                            for (State state : setsMap.get(state2))
                                setsMap.put(state, set);
                        } else {
                            set.add(state2);
                            setsMap.put(state2, set);
                        }
                    } else if (setsMap.containsKey(state2)) {
                        Set<State> set = setsMap.get(state2);
                        set.add(state1);
                        setsMap.put(state1, set);
                    } else {
                        Set<State> equivalentStates = new HashSet<State>();
                        equivalentStates.add(state1);
                        equivalentStates.add(state2);
                        setsMap.put(state1, equivalentStates);
                        setsMap.put(state2, equivalentStates);
                    }
                }
            }
        }
        done.clear();
        List<Set<State>> candidates = new LinkedList<Set<State>>();
        for (State state : setsMap.keySet())
            if (!done.contains(state)) {
                Set<State> stateSet = setsMap.get(state);
                done.addAll(stateSet);
                if (stateSet.size() > 1)
                    candidates.add(stateSet);
            }
        return candidates;
    }

    protected Set<State> findStateCandidates() {
        Set<State> states = new HashSet<State>();
        ContextAutomaton nfa = getAutomaton();
        for (State fromState : nfa.getStates()) {
            for (Symbol symbol : nfa.getSymbols()) {
                Set<State> toStates = nfa.getNextStates(symbol, fromState);
                if (toStates.size() > 1) {
                    states.addAll(toStates);
                    return states;
                }
            }
        }
        return states;
    }

    public ContextAutomaton getAutomaton() {
    	return this.contextAutomaton;
    }

    public ContentEquivalenceRelation getContentEquivalenceRelation() {
        return contentEquiv;
    }

    public void setContentEquivalenceRelation(ContentEquivalenceRelation contentEquiv) {
        this.contentEquiv = contentEquiv;
    }

}
