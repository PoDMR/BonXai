/**
 * Created on Nov 9, 2009
 * Modified on $Date: 2009-11-10 14:01:29 $
 */
package gjb.flt.automata.measures;

import java.util.HashMap;
import java.util.Map;

import gjb.flt.automata.impl.sparse.State;
import gjb.flt.automata.impl.sparse.StateNFA;
import gjb.flt.automata.impl.sparse.Symbol;
import gjb.flt.automata.impl.sparse.Transition;

/**
 * @author lucg5005
 * @version $Revision: 1.2 $
 *
 */
public class SOATest {

	public boolean test(StateNFA nfa) {
		Map<Symbol,State> map = new HashMap<Symbol,State>();
		for (Transition transition : nfa.getTransitionMap().getTransitions()) {
			Symbol symbol = transition.getSymbol();
			State toState = transition.getToState();
			if (!map.containsKey(symbol))
				map.put(symbol, toState);
			else if (!map.get(symbol).equals(toState))
				return false;
		}
		return true;
	}

}
