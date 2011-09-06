/**
 * Created on Nov 10, 2009
 * Modified on $Date: 2009-11-10 14:01:29 $
 */
package eu.fox7.flt.automata.measures;

import eu.fox7.flt.automata.impl.sparse.StateNFA;
import eu.fox7.flt.automata.impl.sparse.SupportNFA;
import eu.fox7.flt.automata.impl.sparse.Transition;

/**
 * @author lucg5005
 * @version $Revision: 1.2 $
 *
  * SOAWeightedEditDistance is implemented for SOAs only.  For performance reasons, this is
 * not tested at runtime.  In case of doubt, use eu.fox7.flt.automata.measures.SOATest
 * before computing the edit distance.  Note that in addition, the automata should
 * be descendants of SupportNFA.
*/
public class SOAWeightedEditDistance extends SOAEditDistance {

	@Override
    protected double computeTransitionCost(StateNFA nfa1, StateNFA nfa2,
                                           double editCost) {
    	double cost = 0.0;
        for (Transition transition1 : nfa1.getTransitionMap().getTransitions()) {
    		String symbolValue = transition1.getSymbol().toString();
    		String fromValue = nfa1.getStateValue(transition1.getFromState());
    		String toValue = nfa1.getStateValue(transition1.getToState());
    		if (!nfa2.hasTransition(symbolValue, fromValue, toValue)) {
                cost += editCost*computeWeight(nfa1, nfa2, transition1);
            }
    	}
        return cost;
    }

	protected double computeWeight(StateNFA nfa1, StateNFA nfa2,
	                               Transition transition1) {
		SupportNFA s1 = (SupportNFA) nfa1;
		SupportNFA s2 = (SupportNFA) nfa2;
		String symbolValue = transition1.getSymbol().toString();
		String fromValue = nfa1.getStateValue(transition1.getFromState());
		String toValue = nfa1.getStateValue(transition1.getToState());
	    return Math.abs(s1.getSupport(symbolValue, fromValue, toValue) -
	                    s2.getSupport(symbolValue, fromValue, toValue));
    }

}
