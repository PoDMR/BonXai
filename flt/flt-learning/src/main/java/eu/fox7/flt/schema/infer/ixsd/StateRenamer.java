/*
 * Created on Nov 1, 2006
 * Modified on $Date: 2009-11-09 13:12:26 $
 */
package eu.fox7.flt.schema.infer.ixsd;

import java.util.Set;

import eu.fox7.flt.automata.factories.sparse.StateSetRenamer;
import eu.fox7.flt.automata.impl.sparse.AnnotatedStateNFA;
import eu.fox7.flt.automata.impl.sparse.State;
import eu.fox7.flt.treeautomata.factories.SupportContextAutomatonFactory;
import eu.fox7.flt.treeautomata.impl.ContentAutomaton;


/**
 * @author eu.fox7
 * @version $Revision: 1.1 $
 * 
 */
public class StateRenamer implements StateSetRenamer<ContentAutomaton,Integer> {

    protected SupportContextAutomatonFactory factory;

    public StateRenamer(SupportContextAutomatonFactory factory) {
        this.factory = factory;
    }

    /* (non-Javadoc)
     * @see eu.fox7.util.automata.StateSetRenamer#rename(eu.fox7.util.automata.AnnotatedNFA, java.util.Set)
     */
    public String rename(AnnotatedStateNFA<ContentAutomaton, Integer> nfa,
                         Set<State> stateSet) {
        return null;
    }

    /* (non-Javadoc)
     * @see eu.fox7.util.automata.StateSetRenamer#rename(eu.fox7.util.automata.AnnotatedNFA, eu.fox7.util.automata.State, eu.fox7.util.automata.State)
     */
    public String rename(AnnotatedStateNFA<ContentAutomaton,Integer> nfa,
                         State state1, State state2) {
        String stateValue = nfa.getStateValue(state1);
        String symbolValue = stateValue.substring(0, stateValue.indexOf(factory.getSeparatorChar()));
        return  factory.getNewStateName(symbolValue);
    }

}
