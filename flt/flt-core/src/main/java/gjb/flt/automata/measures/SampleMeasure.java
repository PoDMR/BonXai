/*
 * Created on Oct 22, 2007
 * Modified on $Date: 2009-10-27 14:14:01 $
 */
package gjb.flt.automata.measures;

import gjb.flt.automata.impl.sparse.StateNFA;

/**
 * @author gjb
 * @version $Revision: 1.1 $
 * 
 */
public interface SampleMeasure {

    public double compute(StateNFA nfa, String[][] sample);

}
