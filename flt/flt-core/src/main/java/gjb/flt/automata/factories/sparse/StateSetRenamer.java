/*
 * Created on Dec 28, 2005
 * Modified on $Date: 2009-10-27 14:14:01 $
 */
package gjb.flt.automata.factories.sparse;

import gjb.flt.automata.impl.sparse.AnnotatedStateNFA;
import gjb.flt.automata.impl.sparse.State;

import java.util.Set;

/**
 * @author gjb
 * @version $Revision: 1.1 $
 * 
 */
public interface StateSetRenamer<StateT,TransitionT> {

    public String rename(AnnotatedStateNFA<StateT,TransitionT> nfa,
                         Set<State> stateSet);
    public String rename(AnnotatedStateNFA<StateT,TransitionT> nfa,
                         State state1, State state2);

}
