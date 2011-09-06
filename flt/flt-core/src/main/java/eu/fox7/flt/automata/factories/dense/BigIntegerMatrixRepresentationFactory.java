/**
 * Created on Sep 21, 2009
 * Modified on $Date: 2009-11-10 14:01:29 $
 */
package eu.fox7.flt.automata.factories.dense;

import eu.fox7.flt.automata.impl.dense.MatrixRepresentation;
import eu.fox7.flt.automata.impl.sparse.State;
import eu.fox7.flt.automata.impl.sparse.StateNFA;
import eu.fox7.flt.automata.impl.sparse.Transition;
import eu.fox7.math.BigIntegerMatrix;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lucg5005
 * @version $Revision: 1.2 $
 *
 */
public class BigIntegerMatrixRepresentationFactory {

	public MatrixRepresentation<BigInteger> create(StateNFA nfa) {
		MatrixRepresentation<BigInteger> m = new MatrixRepresentation<BigInteger>();
		init(m, nfa);
		return m;
	}

	protected Map<State, Integer> init(MatrixRepresentation<BigInteger> m,
	                                   StateNFA nfa) {
		Map<State,Integer> stateIndices = computeStateMap(nfa);
		m.setZeroValue(BigInteger.ZERO);
		m.setOneValue(BigInteger.ONE);
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

    protected void computeInitialStateMatrix(MatrixRepresentation<BigInteger> m,
                                             StateNFA nfa,
                                             Map<State,Integer> stateMap) {
        m.setX0(new BigIntegerMatrix(nfa.getNumberOfStates() - 1, 1));
        for (Transition transition : nfa.getOutgoingTransitions(nfa.getInitialState())) {
            State toState = transition.getToState();
            m.setInitialValue(stateMap.get(toState), BigInteger.ONE);
        }
    }

    protected void computeTransitionMatrix(MatrixRepresentation<BigInteger> m,
                                           StateNFA nfa,
                                           Map<State,Integer> stateMap) {
        m.setP(new BigIntegerMatrix(nfa.getNumberOfStates() - 1, nfa.getNumberOfStates() - 1));
        for (Transition transition : nfa.getTransitionMap().getTransitions()) {
            State fromState = transition.getFromState();
            if (fromState != nfa.getInitialState()) {
                State toState = transition.getToState();
                m.setTransitionValue(stateMap.get(toState), stateMap.get(fromState),
                                     BigInteger.ONE);
            }
        }
    }
    
    protected void computeFinalStateMatrix(MatrixRepresentation<BigInteger> m,
                                           StateNFA nfa,
                                           Map<State,Integer> stateMap) {
        m.setAcceptsEmptyString(nfa.isFinalState(nfa.getInitialState()));
        m.setXf(new BigIntegerMatrix(1, nfa.getNumberOfStates() - 1));
        for (State state : nfa.getStates())
            if (state != nfa.getInitialState() && nfa.isFinalState(state))
                m.setFinalValue(stateMap.get(state), BigInteger.ONE);
    }

}
