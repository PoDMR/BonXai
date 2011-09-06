/**
 * Created on Sep 22, 2009
 * Modified on $Date: 2009-11-10 14:01:29 $
 */
package eu.fox7.flt.automata.factories.dense;

import eu.fox7.flt.automata.impl.dense.MatrixRepresentation;
import eu.fox7.flt.automata.impl.sparse.State;
import eu.fox7.flt.automata.impl.sparse.StateNFA;
import eu.fox7.flt.automata.impl.sparse.Transition;
import eu.fox7.math.DoubleMatrix;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lucg5005
 * @version $Revision: 1.2 $
 *
 */
public class DoubleMatrixRepresentationFactory {

	public MatrixRepresentation<Double> create(StateNFA nfa) {
		MatrixRepresentation<Double> m = new MatrixRepresentation<Double>();
		init(m, nfa);
		return m;
	}

	protected Map<State, Integer> init(MatrixRepresentation<Double> m,
	                                   StateNFA nfa) {
		Map<State,Integer> stateIndices = computeStateMap(nfa);
		m.setZeroValue(Double.valueOf(0.0));
		m.setOneValue(Double.valueOf(1.0));
        computeInitialStateMatrix(m, nfa, stateIndices);
        computeTransitionMatrix(m, nfa, stateIndices);
        computeFinalStateMatrix(m, nfa, stateIndices);
        return stateIndices;
	}

	protected Map<State,Integer> computeStateMap(StateNFA nfa) {
        Map<State,Integer> stateMap = new HashMap<State,Integer>();
        int number = 0;
        for (State state : nfa.getStates())
            if (state != nfa.getInitialState())
                stateMap.put(state, number++);
        return stateMap;
    }

    protected void computeInitialStateMatrix(MatrixRepresentation<Double> m,
                                             StateNFA nfa,
                                             Map<State,Integer> stateMap) {
        m.setX0(new DoubleMatrix(nfa.getNumberOfStates() - 1, 1));
        for (Transition transition : nfa.getOutgoingTransitions(nfa.getInitialState())) {
            State toState = transition.getToState();
            m.setInitialValue(stateMap.get(toState), Double.valueOf(1.0));
        }
    }

    protected void computeTransitionMatrix(MatrixRepresentation<Double> m,
                                           StateNFA nfa,
                                           Map<State,Integer> stateMap) {
        m.setP(new DoubleMatrix(nfa.getNumberOfStates() - 1, nfa.getNumberOfStates() - 1));
        for (Transition transition : nfa.getTransitionMap().getTransitions()) {
            State fromState = transition.getFromState();
            if (fromState != nfa.getInitialState()) {
                State toState = transition.getToState();
                m.setTransitionValue(stateMap.get(toState), stateMap.get(fromState),
                                     Double.valueOf(1.0));
            }
        }
    }
    
    protected void computeFinalStateMatrix(MatrixRepresentation<Double> m,
                                           StateNFA nfa,
                                           Map<State,Integer> stateMap) {
        m.setAcceptsEmptyString(nfa.isFinalState(nfa.getInitialState()));
        m.setXf(new DoubleMatrix(1, nfa.getNumberOfStates() - 1));
        for (State state : nfa.getStates())
            if (state != nfa.getInitialState() && nfa.isFinalState(state))
                m.setFinalValue(stateMap.get(state), Double.valueOf(1.0));
    }

}
