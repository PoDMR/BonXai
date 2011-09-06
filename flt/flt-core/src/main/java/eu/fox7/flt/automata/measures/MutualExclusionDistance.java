/**
 * Created on Nov 9, 2009
 * Modified on $Date: 2009-11-09 13:39:00 $
 */
package eu.fox7.flt.automata.measures;

import eu.fox7.flt.automata.impl.sparse.StateNFA;

/**
 * @author lucg5005
 * @version $Revision: 1.1 $
 *
 */
public class MutualExclusionDistance implements RelativeLanguageMeasure<Double> {

    protected RelativeLanguageMeasure<Double> measure = new InclusionMeasure();

	/* (non-Javadoc)
	 * @see eu.fox7.flt.automata.measures.RelativeLanguageMeasure#compute(eu.fox7.flt.automata.impl.sparse.StateNFA, eu.fox7.flt.automata.impl.sparse.StateNFA)
	 */
	@Override
	public Double compute(StateNFA nfa1, StateNFA nfa2) {
        return measure.compute(nfa1, nfa2) + measure.compute(nfa2, nfa1);
	}

}
