/*
 * Created on Jun 9, 2006
 * Modified on $Date: 2009-11-10 14:01:29 $
 */
package eu.fox7.flt.automata.impl.sparse;


import java.util.HashMap;
import java.util.Map;

/**
 * @author eu.fox7
 * @version $Revision: 1.2 $
 * 
 */
public class PFA extends SparseNFA {

    protected Map<Transition,Double> probabilityMap = new HashMap<Transition,Double>();

    public PFA() {
        super();
    }
    public PFA(StateNFA nfa) {
        super(nfa);
    }

    public PFA(StateNFA nfa, Map<State,State> stateRemap) {
        super(nfa, stateRemap);
    }

    public double getTransitionProbability(Transition transition) {
        if (probabilityMap.containsKey(transition))
            return probabilityMap.get(transition);
        else
            return 0.0;
    }

    public void setTransitionProbability(Transition transition,
                                         double probability) {
        if (getTransitionMap().getTransitions().contains(transition))
            probabilityMap.put(transition, probability);
        else
            throw new RuntimeException("Transition " + transition.toString() + " does not exist");
    }

}
