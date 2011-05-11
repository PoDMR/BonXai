/*
 * Created on May 13, 2007
 * Modified on $Date: 2009-10-27 14:14:00 $
 */
package gjb.flt.automata.factories.sparse;

import gjb.flt.automata.impl.sparse.State;
import gjb.flt.automata.impl.sparse.StateNFA;

/**
 * @author gjb
 * @version $Revision: 1.1 $
 * 
 */
public interface StateCondition {

    public void setNFAs(StateNFA nfa1, StateNFA nfa2);
    public boolean satisfy(State state1, State state2);

}
