/*
 * Created on Nov 13, 2006
 * Modified on $Date: 2009-10-27 14:14:01 $
 */
package gjb.flt.automata.io;

import gjb.flt.automata.impl.sparse.SparseNFA;

/**
 * @author gjb
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
