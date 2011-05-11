/**
 * Created on Jun 16, 2009
 * Modified on $Date: 2009-10-27 14:14:00 $
 */
package gjb.flt.automata.factories.sparse;

import gjb.flt.automata.ModifiableNFA;

/**
 * @author lucg5005
 * @version $Revision: 1.1 $
 *
 */
public interface AutomatonFactory<NFAType extends ModifiableNFA> {

	public NFAType getAutomaton();

}
