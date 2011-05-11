/**
 * Created on Oct 6, 2009
 * Modified on $Date: 2009-11-10 14:01:29 $
 */
package gjb.flt.automata.measures;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import gjb.flt.automata.FeatureNotSupportedException;
import gjb.flt.automata.impl.sparse.State;
import gjb.flt.automata.impl.sparse.StateNFA;
import gjb.flt.automata.impl.sparse.Symbol;
import gjb.flt.automata.impl.sparse.Transition;
import gjb.flt.automata.matchers.NFAMatcher;

/**
 * @author lucg5005
 * @version $Revision: 1.2 $
 *
 */
public class SampleSupport extends NFAMatcher {

    protected Map<Transition,Integer> supportMap = new HashMap<Transition,Integer>();
    protected Map<Transition,Integer> visitMap = new HashMap<Transition,Integer>();
    protected Set<Transition> updatedSet = new HashSet<Transition>();
    protected int totalSupport = 0;
    protected int totalVisits = 0;

    public SampleSupport(StateNFA nfa) {
	    super(nfa);
    }

    public void compute(String[][] sample) {
    	for (String[] example : sample)
    		matches(example);
    }

	/**
     * method to reset the support count
     */
    public void reset() {
        supportMap.clear();
        updatedSet.clear();
        totalSupport = 0;
        visitMap.clear();
        totalVisits = 0;
    }

	/**
     * method that returns the support for a given Transition, i.e., the
     * number of strings for which the transition was traversed; note that
     * this is <em>not</em> the total number of times a transition is traversed
     * @param transition Transition
     * @return int support of the Transition
     */
    public int getSupport(Transition transition) {
        if (supportMap.containsKey(transition))
            return supportMap.get(transition);
        else
            return 0;
    }

    public int getTotalSupport() {
        return totalSupport;
    }

    public int getVisits(Transition transition) {
        if (visitMap.containsKey(transition))
            return visitMap.get(transition);
        else
            return 0;
    }

    public int getTotalVisits() {
        return totalVisits;
    }

    @Override
    public void initRun() {
        if (nfa.hasEpsilonTransitions())
            throw new RuntimeException(new FeatureNotSupportedException("only for epsilon-free NFAs"));
        super.initRun();
        updatedSet.clear();
        totalSupport++;
    }

	@Override
    protected boolean step(Symbol symbol) {
        SortedSet<State> newCurrentStates = new TreeSet<State>(stateValueComparator);
        for (State state : currentStates) {
            Set<State> result = nfa.getNextStates(symbol, state);
            if (result != null) {
                newCurrentStates.addAll(result);
                for (State toState : result) {
                    Transition transition = new Transition(symbol, state, toState);
                    if (!updatedSet.contains(transition)) {
                        updatedSet.add(transition);
                        if (!supportMap.containsKey(transition))
                            supportMap.put(transition, 0);
                        supportMap.put(transition, 1 + supportMap.get(transition));
                    }
                    if (!visitMap.containsKey(transition))
                        visitMap.put(transition, 0);
                    visitMap.put(transition, 1 + visitMap.get(transition));
                    totalVisits++;
                }
            }
        }
        if (this.currentStates.isEmpty()) {
            return false;
        }
        this.currentStates = newCurrentStates;
        return true;
    }

}
