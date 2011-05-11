/**
 * Created on Oct 27, 2009
 * Modified on $Date: 2009-10-27 14:14:01 $
 */
package gjb.flt.automata.impl.sparse;

import gjb.flt.automata.NotDFAException;


/**
 * @author lucg5005
 * @version $Revision: 1.1 $
 *
 */
public interface StateDFA extends StateNFA {

    public State getNextState(Symbol symbol, State fromState)
            throws NotDFAException;

}
