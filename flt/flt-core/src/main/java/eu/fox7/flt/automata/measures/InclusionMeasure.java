/**
 * Created on Nov 9, 2009
 * Modified on $Date: 2009-11-10 14:01:29 $
 */
package eu.fox7.flt.automata.measures;

import eu.fox7.flt.automata.impl.sparse.State;
import eu.fox7.flt.automata.impl.sparse.StateNFA;
import eu.fox7.flt.automata.impl.sparse.SupportNFA;
import eu.fox7.flt.automata.impl.sparse.Symbol;
import eu.fox7.flt.automata.impl.sparse.Transition;

/**
 * @author lucg5005
 * @version $Revision: 1.3 $
 *
 */
public class InclusionMeasure implements RelativeLanguageMeasure<Double> {

	/* (non-Javadoc)
	 * @see eu.fox7.flt.automata.measures.RelativeLanguageMeasure#compute(eu.fox7.flt.automata.impl.sparse.StateNFA, eu.fox7.flt.automata.impl.sparse.StateNFA)
	 */
	@Override
	public Double compute(StateNFA nfa1, StateNFA nfa2) {
        double cost = 0.0;
        double total = 0.0;
        for (Transition transition : nfa1.getTransitionMap().getTransitions()) {
        	Symbol symbol = transition.getSymbol();
        	State fromState = transition.getFromState();
			State toState = transition.getToState();
			int support = ((SupportNFA) nfa1).getSupport(symbol, fromState, toState);
			total += support;
			if (!nfa2.hasTransition(symbol.toString(),
			                        nfa1.getStateValue(fromState),
			                        nfa1.getStateValue(toState)))
				cost += support;
        }
        for (State finalState : nfa1.getFinalStates()) {
        	int support = ((SupportNFA) nfa1).getFinalStateSupport(finalState);
        	total += support;
        	String finalStateValue = nfa1.getStateValue(finalState);
        	if (!nfa2.hasState(finalStateValue) ||
        			!nfa2.isFinalState(finalStateValue))
        		cost += support;
        }
        if (total > 0.0)
            return cost/total;
        else
            return 0.0;
	}

}
