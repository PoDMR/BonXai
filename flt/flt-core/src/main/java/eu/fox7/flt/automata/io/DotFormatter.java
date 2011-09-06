/*
 * Created on Jan 31, 2006
 * Modified on $Date: 2009-10-27 14:14:01 $
 */
package eu.fox7.flt.automata.io;

import eu.fox7.flt.automata.impl.sparse.State;
import eu.fox7.flt.automata.impl.sparse.StateNFA;
import eu.fox7.flt.automata.impl.sparse.Transition;

/**
 * @author eu.fox7
 * @version $Revision: 1.1 $
 * 
 */
public interface DotFormatter {

    public String stateToDot(StateNFA nfa, State state);
    public String transitionToDot(StateNFA nfa, Transition transition);

}
