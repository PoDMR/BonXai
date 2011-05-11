/*
 * Created on Sep 13, 2004
 *
 *  */
package gjb.flt.automata.io;

import gjb.flt.automata.impl.sparse.StateNFA;

/**
 * Interface to be implemented by any class that writes an NFA to a
 * Writer.
 * 
 * @author gjb
 * @version 1.0
 */
public interface NFAWriter {

    /**
     * implementations of this method are responsable to write the NFA
     * passed as a parameter to the Writer that has been supplied via
     * a constructor
     * @param nfa NFA to be written
     * @throws NFAWriteException Exception that encapsulates the original
     * Exceptions that occur during write operations
     */
    public void write(StateNFA nfa) throws NFAWriteException;
    
}
