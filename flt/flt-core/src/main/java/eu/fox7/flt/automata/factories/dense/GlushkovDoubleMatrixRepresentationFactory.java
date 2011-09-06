/**
 * Created on Sep 22, 2009
 * Modified on $Date: 2009-11-10 14:01:29 $
 */
package eu.fox7.flt.automata.factories.dense;

import eu.fox7.flt.automata.impl.dense.GlushkovMatrixRepresentation;
import eu.fox7.flt.automata.impl.sparse.State;
import eu.fox7.flt.automata.impl.sparse.StateNFA;
import eu.fox7.flt.automata.impl.sparse.Transition;

import java.util.Map;

/**
 * @author lucg5005
 * @version $Revision: 1.2 $
 *
 */
public class GlushkovDoubleMatrixRepresentationFactory {

	protected DoubleMatrixRepresentationFactory factory = new DoubleMatrixRepresentationFactory();
	
	public GlushkovMatrixRepresentation<Double> create(StateNFA nfa) {
		GlushkovMatrixRepresentation<Double> m = new GlushkovMatrixRepresentation<Double>();
		init(m, nfa);
		return m;
	}

	protected Map<State,Integer> init(GlushkovMatrixRepresentation<Double> m,
	                                  StateNFA nfa) {
		Map<State,Integer> stateNumbers = factory.init(m, nfa);
		this.computeMaps(m, nfa, stateNumbers);
		return stateNumbers;
	}

	protected void computeMaps(GlushkovMatrixRepresentation<Double> m,
	                           StateNFA nfa,
	                           Map<State,Integer> stateNumbers) {
        for (Transition transition : nfa.getTransitionMap().getTransitions()) {
            String symbol = transition.getSymbol().toString();
            Integer stateNumber = stateNumbers.get(transition.getToState());
			m.addIndex(stateNumber, symbol);
            m.addSymbol(symbol, stateNumber);
        }
	}

}
