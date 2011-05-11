/*
 * Created on Feb 16, 2006
 * Modified on $Date: 2009-11-10 14:01:29 $
 */
package gjb.flt.automata.misc;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import gjb.flt.automata.factories.sparse.KLAFactory;
import gjb.flt.automata.impl.sparse.State;
import gjb.flt.automata.impl.sparse.StateNFA;
import gjb.flt.automata.impl.sparse.Transition;
import gjb.util.AbstractEquivalenceRelation;

/**
 * @author gjb
 * @version $Revision: 1.2 $
 * 
 */
public class PrefixStringRelation extends AbstractEquivalenceRelation<State> {

    protected int k = 1;
    protected StateNFA nfa;

    public PrefixStringRelation(StateNFA nfa, int k) {
        this.k = k;
        this.nfa = nfa;
    }

    @Override
    public boolean areEquivalent(State state1, State state2) {
        return convert(getPrefixStrings(state1)).equals(convert(getPrefixStrings(state2)));
    }

    public Set<String[]> getPrefixStrings(State endState) {
        Set<String[]> stringSet = new HashSet<String[]>();
        int level = 0;
        Map<State,Set<String[]>> oldStateMap = new HashMap<State,Set<String[]>>();
        oldStateMap.put(endState, new HashSet<String[]>());
        oldStateMap.get(endState).add(new String[0]);
        while (level < getK()) {
            level++;
            Map<State,Set<String[]>> newStateMap = new HashMap<State,Set<String[]>>();
            for (State state : oldStateMap.keySet()) {
                if (nfa.isInitialState(state)) {
                    if (!newStateMap.containsKey(state)) {
                        newStateMap.put(state, new HashSet<String[]>());
                    }
                    for (String[] suffix : oldStateMap.get(state)) {
                        String[] string = prepend(suffix,
                                                  KLAFactory.DEFAULT_PADDING_CHAR);
                        newStateMap.get(state).add(string);
                    }
                    
                }
                for (Transition transition : nfa.getIncomingTransitions(state)) {
                    State fromState = transition.getFromState();
                    if (!newStateMap.containsKey(fromState))
                        newStateMap.put(fromState, new HashSet<String[]>());
                    for (String[] suffix : oldStateMap.get(state)) {
                        String[] string = prepend(suffix,
                                                  transition.getSymbol().toString());
                        newStateMap.get(fromState).add(string);
                    }
                }
            }
            oldStateMap = newStateMap;
        }
        for (Set<String[]> strings : oldStateMap.values()) {
            stringSet.addAll(strings);
        }
        return stringSet;
    }

    protected static String[] prepend(String[] suffix, String first) {
        String[] string = new String[suffix.length + 1];
        string[0] = first;
        for (int i = 0; i < suffix.length; i++)
            string[i + 1] = suffix[i];
        return string;
    }

    public static Set<String> convert(Set<String[]> stringArraySet) {
        Set<String> stringSet = new HashSet<String>();
        for (String[] string : stringArraySet) {
            stringSet.add(StringUtils.join(string, " "));
        }
        return stringSet;
    }

    public int getK() {
        return k;
    }

}
