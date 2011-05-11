/*
 * Created on Feb 7, 2006
 * Modified on $Date: 2009-10-27 14:14:01 $
 */
package gjb.flt.automata.factories.sparse;

import gjb.flt.automata.ModifiableNFA;

import java.util.HashMap;
import java.util.Map;

/**
 * @author gjb
 * @version $Revision: 1.1 $
 * 
 */
public abstract class AbstractPSAFactory<NFAType extends ModifiableNFA>
        extends AbstractIncrementalNFAFactory<NFAType> {

    public static final String STATE_DECORATOR = "#";
    protected Map<String,Integer> stateCounter = new HashMap<String,Integer>();

}
