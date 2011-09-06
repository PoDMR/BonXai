/**
 * Created on Oct 13, 2009
 * Modified on $Date: 2009-10-27 14:14:01 $
 */
package eu.fox7.flt.automata.misc;

import eu.fox7.flt.automata.impl.sparse.State;
import eu.fox7.flt.automata.impl.sparse.StateNFA;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lucg5005
 * @version $Revision: 1.1 $
 *
 */
public class StateRemapper {

    public static Map<State,State> stateRemapping(StateNFA nfa) {
        Map<State,State> map = new HashMap<State,State>();
        for (State state : nfa.getStates()) {
            State newState = new State();
            map.put(state, newState);
        }
        return map;
    }

}
