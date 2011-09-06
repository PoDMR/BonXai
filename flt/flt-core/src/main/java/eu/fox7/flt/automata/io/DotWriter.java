/*
 * Created on Sep 13, 2004
 *
 */
package eu.fox7.flt.automata.io;

import eu.fox7.flt.automata.impl.sparse.State;
import eu.fox7.flt.automata.impl.sparse.StateNFA;
import eu.fox7.flt.automata.impl.sparse.Transition;

import java.io.IOException;
import java.io.Writer;

/**
 * Class that implements the NFAWriter interface.  It writes the
 * graph representation of the NFA as a string that can be used by
 * AT&amp;T's Graphviz dot program.
 * 
 * @author eu.fox7
 * @version 1.0
 */
public class DotWriter implements NFAWriter {

    /**
     * Writer object to perform the write operation to
     */
    protected Writer writer;
    
    /**
     * Constructor that sets the Writer object to be written to
     * @param writer Writer to be written to
     */
    public DotWriter(Writer writer) {
        this.writer = writer;
    }
    
    /**
     * method to write the dot file representing the NFA graph structure
     * to the Writer
     * @param nfa NFA to be written
     * @throws NFAWriteException Exception that embeds the original Exception
     */
    public void write(StateNFA nfa) throws NFAWriteException {
        write(nfa, new DefaultDotFormatter());
    }

    public void write(StateNFA nfa, DotFormatter formatter)
            throws NFAWriteException {
        StringBuffer str = new StringBuffer();
        str.append("digraph g\n{\n\n").append("  rankdir=\"LR\"\n\n");
        str.append("  /* list of nodes */\n");
        str.append("  qStart [shape=\"plaintext\", label=\"\"];\n");
        for (State state : nfa.getStates()) {
            str.append(formatter.stateToDot(nfa, state));
            str.append("\n");
        }
        str.append("\n\n");
        str.append("  /* list of edges */\n");
        str.append("  qStart -> ").append(nfa.getInitialState().toString());
        str.append(";\n");
        for (Transition transition : nfa.getTransitionMap().getTransitions()) {
            str.append(formatter.transitionToDot(nfa, transition));
            str.append("\n");
        }
        str.append("}\n");
        try {
            writer.write(str.toString());
        } catch (IOException e) {
            throw new NFAWriteException("write operation failed", e);
        }
    }
}
