/*
 * Created on Jan 2, 2006
 * Modified on $Date: 2009-11-12 21:44:56 $
 */
package eu.fox7.flt.automata;

import eu.fox7.flt.automata.factories.sparse.StateSetRenamer;
import eu.fox7.flt.automata.impl.sparse.AnnotatedStateNFA;
import eu.fox7.flt.automata.impl.sparse.State;
import eu.fox7.flt.regex.Glushkov;

import java.util.Set;

/**
 * @author eu.fox7
 * @version $Revision: 1.1 $
 * 
 */
public class GlushkovRenamer<StateT,TransT> implements StateSetRenamer<StateT,TransT> {

    public String rename(AnnotatedStateNFA<StateT,TransT> nfa, Set<State> stateSet) {
        State state = stateSet.iterator().next();
        String stateValue = nfa.getStateValue(state);
        if (nfa.isInitialState(state))
            return stateValue;
        else
            return Glushkov.unmark(stateValue);
    }

    public String rename(AnnotatedStateNFA<StateT,TransT> nfa,
                         State state1, State state2) {
        return null;
    }

}
