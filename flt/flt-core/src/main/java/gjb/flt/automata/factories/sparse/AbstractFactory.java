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
public abstract class AbstractFactory<NFAType extends ModifiableNFA>
        implements AutomatonFactory<NFAType> {

	protected NFAType nfa;

	public NFAType getAutomaton() {
		return nfa;
	}

}
