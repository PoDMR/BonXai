/**
 * Created on Oct 13, 2009
 * Modified on $Date: 2009-10-27 14:14:01 $
 */
package eu.fox7.flt.automata.impl.sparse;


import java.util.Comparator;

/**
 * @author lucg5005
 * @version $Revision: 1.1 $
 *
 */
public class StateValueComparator implements Comparator<State> {

    protected StateNFA nfa;
    
    public StateValueComparator(StateNFA nfa) {
        this.nfa = nfa;
    }
    
    public int compare(State state1, State state2) throws ClassCastException {
        return nfa.getStateValue(state1).compareTo(nfa.getStateValue(state2));
    }
    
}
