/*
 * Created on Jan 27, 2006
 * Modified on $Date: 2009-11-12 09:10:15 $
 */
package eu.fox7.flt.automata.factories.sparse;

import eu.fox7.flt.automata.NotDFAException;
import eu.fox7.flt.automata.impl.sparse.SparseNFA;
import eu.fox7.flt.automata.impl.sparse.State;
import eu.fox7.flt.automata.impl.sparse.Symbol;
import eu.fox7.flt.regex.Glushkov;

/**
 * @author eu.fox7
 * @version $Revision: 1.4 $
 * 
 */
public class PSAFactory extends AbstractPSAFactory<SparseNFA> {

    public PSAFactory() {
        super();
        nfa = new SparseNFA();
        nfa.setInitialState(Glushkov.INITIAL_STATE);
    }

    public PSAFactory newInstance() {
    	return new PSAFactory();
    }

    public void add(String[] example) {
        State fromState = nfa.getInitialState();
        for (int i = 0; i < example.length; i++) {
            String symbolValue = example[i];
            Symbol symbol = Symbol.create(symbolValue);
            String fromValue = nfa.getStateValue(fromState);
            try {
                if (nfa.getNextStates(symbol, fromState).isEmpty()) {
                    String toValue = example[i];
                    if (!stateCounter.containsKey(toValue)) {
                        stateCounter.put(toValue, 0);
                    }
                    stateCounter.put(toValue, stateCounter.get(toValue) + 1);
                    toValue += STATE_DECORATOR + stateCounter.get(toValue);
                    nfa.addTransition(symbolValue, fromValue, toValue);
                }
                fromState = nfa.getNextState(symbol, fromState);
            } catch (NotDFAException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
        nfa.addFinalState(fromState);
    }

    public static String strip(String str) {
        int pos = str.lastIndexOf(STATE_DECORATOR);
        if (pos >= 0)
            return str.substring(0, pos);
        else
            return str;
    }

}
