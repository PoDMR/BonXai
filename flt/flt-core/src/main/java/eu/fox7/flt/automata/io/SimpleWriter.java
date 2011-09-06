/*
 * Created on Nov 13, 2006
 * Modified on $Date: 2009-11-10 14:01:29 $
 */
package eu.fox7.flt.automata.io;

import eu.fox7.flt.automata.impl.sparse.State;
import eu.fox7.flt.automata.impl.sparse.StateNFA;
import eu.fox7.flt.automata.impl.sparse.Transition;

import java.io.IOException;
import java.io.Writer;

/**
 * @author eu.fox7
 * @version $Revision: 1.2 $
 * 
 */
public class SimpleWriter implements NFAWriter {

    public static final String INIT_STATE = "init-state: ";
    public static final String LHS_SEP = ", ";
    public static final String SEP = " -> ";
    public static final String FINAL_STATE = "final-state: ";
    protected Writer writer;

    public SimpleWriter(Writer writer) {
        this.writer = writer;
    }

    /* (non-Javadoc)
     * @see eu.fox7.util.automata.NFAWriter#write(eu.fox7.util.automata.NFA)
     */
    public void write(StateNFA nfa) throws NFAWriteException {
        try {
            writer.append(INIT_STATE).append(nfa.getStateValue(nfa.getInitialState())).append("\n");
            for (Transition transition : nfa.getTransitionMap().getTransitions()) {
                writer.append(nfa.getStateValue(transition.getFromState())).append(LHS_SEP);
                writer.append(transition.getSymbol().toString()).append(SEP);
                writer.append(nfa.getStateValue(transition.getToState())).append("\n");
            }
            for (State finalState : nfa.getFinalStates())
                writer.append(FINAL_STATE).append(nfa.getStateValue(finalState)).append("\n");
        } catch (IOException e) {
            throw new NFAWriteException("IO exception", e);
        }
    }

}
