/*
 * Created on Oct 9, 2006
 * Modified on $Date: 2009-11-12 09:10:15 $
 */
package eu.fox7.flt.automata.factories.sparse;


import eu.fox7.flt.automata.NoSuchTransitionException;
import eu.fox7.flt.automata.NotDFAException;
import eu.fox7.flt.automata.impl.sparse.AnnotatedSparseNFA;
import eu.fox7.flt.automata.impl.sparse.AnnotatedStateNFA;
import eu.fox7.flt.automata.impl.sparse.State;
import eu.fox7.flt.automata.impl.sparse.StateNFA;
import eu.fox7.flt.automata.impl.sparse.Symbol;
import eu.fox7.flt.automata.impl.sparse.Transition;
import eu.fox7.flt.automata.io.DefaultDotFormatter;
import eu.fox7.flt.automata.io.DotFormatter;
import eu.fox7.flt.regex.Glushkov;

import java.util.HashMap;
import java.util.Map;

/**
 * @author eu.fox7
 * @version $Revision: 1.3 $
 * 
 */
public class PrefixTreeFactory extends AbstractIncrementalNFAFactory<AnnotatedSparseNFA<Integer,Integer>> {

    public static final String DEFAULT_SEPARATION_CHAR = "_";
    protected String separatorChar = DEFAULT_SEPARATION_CHAR;
    protected Map<String,Integer> stateCounter = new HashMap<String,Integer>();

    public PrefixTreeFactory() {
        nfa = new AnnotatedSparseNFA<Integer,Integer>();
        nfa.setInitialState(Glushkov.INITIAL_STATE);
    }

    public PrefixTreeFactory newInstance() {
    	PrefixTreeFactory newFactory = new PrefixTreeFactory();
    	newFactory.setSeparatorChar(getSeparatorChar());
    	return newFactory;
    }

    @Override
    public void add(String[] example) {
        String fromValue = nfa.getStateValue(nfa.getInitialState());
        for (int i = 0; i < example.length; i++) {
            String symbolValue = example[i];
            Symbol symbol = Symbol.create(symbolValue);
            try {
                String toValue = null;
                if (!nfa.getTransitionMap().hasTransitionWith(symbol, nfa.getState(fromValue))) {
                    toValue = getNewStateName(symbolValue);
                    nfa.addTransition(symbolValue, fromValue, toValue);
                    nfa.annotate(symbolValue, fromValue, toValue, 0);
                } else {
                    try {
                        State toState = nfa.getNextState(symbol, nfa.getState(fromValue));
                        toValue = nfa.getStateValue(toState);
                    } catch (NotDFAException e) {
                        throw new RuntimeException(e);
                    }
                }
                nfa.annotate(symbolValue, fromValue, toValue,
                             nfa.getAnnotation(symbolValue, fromValue, toValue) + 1);
                fromValue = toValue;
            } catch (NoSuchTransitionException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
        nfa.addFinalState(fromValue);
    }

    protected String getNewStateName(String stateValue) {
        if (!stateCounter.containsKey(stateValue))
            stateCounter.put(stateValue, 1);
        int index = stateCounter.get(stateValue);
        stateCounter.put(stateValue, index + 1);
        return stateValue + getSeparatorChar() + index;
    }

    public DotFormatter getDotFormatter() {
        return new AnnotatedPrefixTreeDotFormatter();
    }

    protected static String strip(String name, String sep) {
        int index = name.lastIndexOf(sep);
        if (index > 0)
            return name.substring(0, index);
        else
            return name;
    }

    public String stripStateValue(String name) {
        return strip(name, getSeparatorChar());
    }

    public String getSeparatorChar() {
        return separatorChar;
    }

    public void setSeparatorChar(String separatorChar) {
        this.separatorChar = separatorChar;
    }

    protected class AnnotatedPrefixTreeDotFormatter extends DefaultDotFormatter {

    	@Override
        public String stateToDot(StateNFA nfa, State state) {
            StringBuffer str = new StringBuffer(INDENT);
            str.append(state.toString());
            str.append(ATTR_OPEN);
            str.append(createLabel(nfa.getStateValue(state)));
            str.append(ATTR_SEP);
            str.append(createAttr("shape", STATE_SHAPE));
            if (nfa.getFinalStates().contains(state)) {
                str.append(ATTR_SEP);
                str.append(createAttr("peripheries", "2"));
            }
            str.append(ATTR_CLOSE);
            str.append(LINE_END);
            return str.toString();
        }

    	@Override
        public String transitionToDot(StateNFA nfa, Transition transition) {
            StringBuffer str = new StringBuffer(INDENT);
            str.append(createEdge(transition));
            str.append(ATTR_OPEN);
            str.append(createLabel(((AnnotatedStateNFA<?, ?>) nfa).getAnnotation(transition).toString()));
            str.append(ATTR_CLOSE);
            str.append(LINE_END);
            return str.toString();
        }

    }

}
