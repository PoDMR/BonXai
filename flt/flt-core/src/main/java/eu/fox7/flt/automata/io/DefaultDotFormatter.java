/*
 * Created on Jan 31, 2006
 * Modified on $Date: 2009-11-10 14:01:29 $
 */
package eu.fox7.flt.automata.io;

import eu.fox7.flt.automata.impl.sparse.State;
import eu.fox7.flt.automata.impl.sparse.StateNFA;
import eu.fox7.flt.automata.impl.sparse.Symbol;
import eu.fox7.flt.automata.impl.sparse.Transition;

/**
 * @author eu.fox7
 * @version $Revision: 1.2 $
 * 
 */
public class DefaultDotFormatter implements DotFormatter {

    protected static final String INDENT = "  ";
    protected static final String ATTR_OPEN = " [";
    protected static final String ATTR_CLOSE = "]";
    protected static final String ATTR_SEP = ", ";
    protected static final String LINE_END = ";";
    protected static final String STATE_SHAPE = "ellipse";
    protected static final String SYMBOL_FONT = "Symbol";
    protected static final String EPSILON_SYMBOL = "&epsilon;";
    protected static final String EDGE_SEP = " -> ";

    public String stateToDot(StateNFA nfa, State state) {
        StringBuffer str = new StringBuffer(INDENT);
        str.append(state.toString());
        str.append(ATTR_OPEN);
        computeStateAttributes(str, nfa, state);
        str.append(ATTR_CLOSE);
        str.append(LINE_END);
        return str.toString();
    }

    protected void computeStateAttributes(StringBuffer str, StateNFA nfa, State state) {
        str.append(createLabel(nfa.getStateValue(state)));
        str.append(ATTR_SEP);
        str.append(createAttr("shape", STATE_SHAPE));
        if (nfa.getFinalStates().contains(state)) {
            str.append(ATTR_SEP);
            str.append(createAttr("peripheries", "2"));
        }
    }

    public String transitionToDot(StateNFA nfa, Transition transition) {
        StringBuffer str = new StringBuffer(INDENT);
        str.append(createEdge(transition));
        str.append(ATTR_OPEN);
        computeTransitionAttribute(str, nfa, transition);
        str.append(ATTR_CLOSE);
        str.append(LINE_END);
        return str.toString();
    }

    protected void computeTransitionAttribute(StringBuffer str,
                                              StateNFA nfa,
                                              Transition transition) {
        if (Symbol.getEpsilon().equals(transition.getSymbol())) {
            str.append(createLabel(EPSILON_SYMBOL));
        } else {
            str.append(createLabel(transition.getSymbol().toString()));
        }
    }

    protected static String createAttr(String key, String value) {
        return key + "=\"" + value + "\"";
    }

    protected static String createLabel(String value) {
        return createAttr("label", value);
    }

    protected static String createEdge(Transition transition) {
        return transition.getFromState().toString() +
            EDGE_SEP +
            transition.getToState().toString();
    }

}
