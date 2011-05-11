/*
 * Created on Feb 3, 2006
 * Modified on $Date: 2009-10-27 14:14:01 $
 */
package gjb.flt.automata.converters;

import gjb.flt.automata.AnnotatedNFA;
import gjb.flt.automata.impl.sparse.State;

import java.util.Map;
import java.util.Set;

/**
 * @author gjb
 * @version $Revision: 1.1 $
 * 
 */
public interface AnnotationConverter<StateT,TransitionT> {

    public void convert(AnnotatedNFA<StateT, TransitionT> oldNFA,
                        AnnotatedNFA<StateT, TransitionT> newNFA,
                        Map<State,Set<State>> stateMap);

}
