/*
 * Created on Mar 16, 2007
 * Modified on $Date: 2009-10-27 14:14:01 $
 */
package eu.fox7.flt.automata.io;

import eu.fox7.flt.automata.impl.sparse.State;
import eu.fox7.flt.automata.impl.sparse.StateNFA;
import eu.fox7.flt.automata.impl.sparse.Transition;

import java.util.Set;

/**
 * @author eu.fox7
 * @version $Revision: 1.1 $
 * 
 */
public class MarkedDotFormatter extends DefaultDotFormatter {

    public static final String DEFAULT_MARK_COLOR = "red";
    protected static final String COLOR_ATTR = "color";
    protected static final String FONT_COLOR_ATTR = "fontcolor";
    protected String markColor = DEFAULT_MARK_COLOR;
    protected Set<State> markedStates;
    protected Set<Transition> markedTransitions;

    public MarkedDotFormatter(Set<State> markedStates,
                              Set<Transition> markedTransitions) {
        this.markedStates = markedStates;
        this.markedTransitions = markedTransitions;
    }

    public String getMarkColor() {
        return markColor;
    }

    public void setMarkColor(String markColor) {
        this.markColor = markColor;
    }

    @Override
    protected void computeStateAttributes(StringBuffer str, StateNFA nfa, State state) {
        super.computeStateAttributes(str, nfa, state);
        if (markedStates.contains(state)) {
            str.append(ATTR_SEP);
            str.append(createAttr(FONT_COLOR_ATTR, getMarkColor()));
            str.append(ATTR_SEP);
            str.append(createAttr(COLOR_ATTR, getMarkColor()));
        }
    }

    @Override
    protected void computeTransitionAttribute(StringBuffer str, StateNFA nfa,
                                              Transition transition) {
        super.computeTransitionAttribute(str, nfa, transition);
        if (markedTransitions.contains(transition)) {
            str.append(ATTR_SEP);
            str.append(createAttr(FONT_COLOR_ATTR, getMarkColor()));
            str.append(ATTR_SEP);
            str.append(createAttr(COLOR_ATTR, getMarkColor()));
        }
    }

}
