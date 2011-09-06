/*
 * Created on Jun 24, 2007
 * Modified on $Date: 2009-10-27 14:14:01 $
 */
package eu.fox7.flt.automata.converters;

import eu.fox7.flt.automata.impl.sparse.ModifiableStateNFA;

public interface Minimizer {

    public void minimize(ModifiableStateNFA nfa);

}