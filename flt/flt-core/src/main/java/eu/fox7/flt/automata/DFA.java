/**
 * Created on Oct 27, 2009
 * Modified on $Date: 2009-10-27 14:14:00 $
 */
package eu.fox7.flt.automata;

/**
 * @author lucg5005
 * @version $Revision: 1.1 $
 *
 */
public interface DFA extends NFA {

	public String getNextStateValue(String symbolValue, String fromValue)
            throws NotDFAException;

}
