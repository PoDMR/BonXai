/**
 * Created on Oct 9, 2009
 * Modified on $Date: 2009-10-27 14:14:01 $
 */
package eu.fox7.flt.automata.converters;

import eu.fox7.flt.automata.NoSuchStateException;
import eu.fox7.flt.automata.impl.sparse.ModifiableStateNFA;
import eu.fox7.flt.automata.impl.sparse.State;

import java.util.HashSet;
import java.util.Set;

/**
 * @author lucg5005
 * @version $Revision: 1.1 $
 *
 */
public class Simplifier {

    public static void simplify(ModifiableStateNFA nfa) {
        Set<State> removeStates = new HashSet<State>();
        Set<State> reachables = nfa.reachableStates();
        Set<State> terminatings = nfa.getTerminatingStates();
        for (State state : nfa.getStates())
            if (!(reachables.contains(state) &&
                    terminatings.contains(state)))
                removeStates.add(state);
        for (State state : removeStates)
	        try {
	            nfa.removeState(state);
            } catch (NoSuchStateException e) {
            	throw new RuntimeException("state '" + state.toString() + "' does not exist");
            }
    }

}
