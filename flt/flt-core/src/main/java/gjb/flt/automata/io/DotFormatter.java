/*
 * Created on Jan 31, 2006
 * Modified on $Date: 2009-10-27 14:14:01 $
 */
package gjb.flt.automata.io;

import gjb.flt.automata.impl.sparse.State;
import gjb.flt.automata.impl.sparse.StateNFA;
import gjb.flt.automata.impl.sparse.Transition;

/**
 * @author gjb
 * @version $Revision: 1.1 $
 * 
 */
public interface DotFormatter {

    public String stateToDot(StateNFA nfa, State state);
    public String transitionToDot(StateNFA nfa, Transition transition);

}
