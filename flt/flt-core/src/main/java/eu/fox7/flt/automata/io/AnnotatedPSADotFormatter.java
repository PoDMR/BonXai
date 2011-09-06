/*
 * Created on Jan 31, 2006
 * Modified on $Date: 2009-10-27 14:14:01 $
 */
package eu.fox7.flt.automata.io;

import eu.fox7.flt.automata.impl.sparse.AnnotatedStateNFA;
import eu.fox7.flt.automata.impl.sparse.StateNFA;
import eu.fox7.flt.automata.impl.sparse.Transition;

/**
 * @author eu.fox7
 * @version $Revision: 1.1 $
 * 
 */
public class AnnotatedPSADotFormatter extends DefaultDotFormatter {

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
