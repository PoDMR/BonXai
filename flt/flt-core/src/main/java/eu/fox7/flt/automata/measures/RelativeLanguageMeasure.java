/*
 * Created on Oct 17, 2007
 * Modified on $Date: 2009-10-27 14:14:01 $
 */
package eu.fox7.flt.automata.measures;

import eu.fox7.flt.automata.impl.sparse.StateNFA;

/**
 * @author eu.fox7
 * @version $Revision: 1.1 $
 * 
 */
public interface RelativeLanguageMeasure<T> {

    public T compute(StateNFA nfa1, StateNFA nfa2);

}
