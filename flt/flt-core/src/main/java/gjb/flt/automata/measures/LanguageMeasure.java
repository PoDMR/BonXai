/*
 * Created on Jul 9, 2007
 * Modified on $Date: 2009-10-27 14:14:01 $
 */
package gjb.flt.automata.measures;

import gjb.flt.automata.impl.sparse.StateNFA;

public interface LanguageMeasure<T> {

    public T compute(StateNFA nfa);

}