/*
 * Created on Nov 1, 2006
 * Modified on $Date: 2009-11-09 13:12:26 $
 */
package gjb.flt.schema.infer.ixsd;

import java.util.Set;

import gjb.flt.automata.factories.sparse.StateSetRenamer;
import gjb.flt.automata.impl.sparse.AnnotatedStateNFA;
import gjb.flt.automata.impl.sparse.State;
import gjb.flt.treeautomata.factories.SupportContextAutomatonFactory;
import gjb.flt.treeautomata.impl.ContentAutomaton;

/**
 * @author gjb
 * @version $Revision: 1.1 $
 * 
 */
public class StateRenamer implements StateSetRenamer<ContentAutomaton,Integer> {

    protected SupportContextAutomatonFactory factory;

    public StateRenamer(SupportContextAutomatonFactory factory) {
        this.factory = factory;
    }

    /* (non-Javadoc)
     * @see gjb.util.automata.StateSetRenamer#rename(gjb.util.automata.AnnotatedNFA, java.util.Set)
     */
    public String rename(AnnotatedStateNFA<ContentAutomaton, Integer> nfa,
                         Set<State> stateSet) {
        return null;
    }

    /* (non-Javadoc)
     * @see gjb.util.automata.StateSetRenamer#rename(gjb.util.automata.AnnotatedNFA, gjb.util.automata.State, gjb.util.automata.State)
     */
    public String rename(AnnotatedStateNFA<ContentAutomaton,Integer> nfa,
                         State state1, State state2) {
        String stateValue = nfa.getStateValue(state1);
        String symbolValue = stateValue.substring(0, stateValue.indexOf(factory.getSeparatorChar()));
        return  factory.getNewStateName(symbolValue);
    }

}
