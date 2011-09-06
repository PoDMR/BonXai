/*
 * Created on Oct 22, 2007
 * Modified on $Date: 2009-10-27 14:14:01 $
 */
package eu.fox7.flt.automata.measures;

import eu.fox7.flt.automata.impl.sparse.StateNFA;

/**
 * @author eu.fox7
 * @version $Revision: 1.1 $
 * 
 */
public interface SampleMeasure {

    public double compute(StateNFA nfa, String[][] sample);

}
