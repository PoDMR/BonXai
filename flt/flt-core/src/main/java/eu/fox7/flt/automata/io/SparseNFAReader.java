/*
 * Created on Nov 13, 2006
 * Modified on $Date: 2009-10-27 14:14:01 $
 */
package eu.fox7.flt.automata.io;

import eu.fox7.flt.automata.impl.sparse.SparseNFA;

/**
 * @author eu.fox7
 * @version $Revision: 1.1 $
 * 
 */
public interface SparseNFAReader {

    /**
     * implementations of this method are responsable to read the NFA
     * representation and return the corresponding NFA
     * @return NFA
     * @throws NFAWriteException Exception that encapsulates the original
     * Exceptions that occur during read operations
     */
    public SparseNFA read() throws NFAReadException;

}
