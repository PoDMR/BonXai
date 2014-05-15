/**
 * Created on Oct 28, 2009
 * Modified on $Date: 2009-10-28 15:38:40 $
 */
package eu.fox7.flt.automata.impl.sparse;

/**
 * @author lucg5005
 * @version $Revision: 1.2 $
 *
 */
public class SupportNFA extends AnnotatedSparseNFA<Integer, Integer> {

	public SupportNFA(SparseNFA nfa) {
		super(nfa);
	}

	public SupportNFA() {
	}

	public int getTotalSupport() {
		int totalSupport = 0;
		for (State finalState : getFinalStates())
			totalSupport += getAnnotation(finalState);
		return totalSupport;
	}

	public int getSupport(Symbol symbol, State fromState, State toState) {
		if (hasAnnotation(symbol, fromState, toState))
			return getAnnotation(symbol, fromState, toState);
		else
			return 0;
	}

	public int getSupport(String symbolValue, String fromValue, String toValue) {
		return getSupport(Symbol.create(symbolValue),
		                  getState(fromValue),
		                  getState(toValue));
	}

	public int getEmptyStringSupport() {
		if (hasAnnotation(getInitialState()))
			return getAnnotation(getInitialState());
		else
			return 0;
	}

	public int getFinalStateSupport(State state) {
		if (hasAnnotation(state))
			return getAnnotation(state);
		else
			return 0;
	}

}
