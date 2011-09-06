/**
 * Created on Oct 13, 2009
 * Modified on $Date: 2009-10-27 14:14:01 $
 */
package eu.fox7.flt.automata.impl.sparse;

import eu.fox7.flt.automata.AnnotatedNFA;
import eu.fox7.flt.automata.NoSuchStateException;
import eu.fox7.flt.automata.NoSuchTransitionException;

/**
 * @author lucg5005
 * @version $Revision: 1.1 $
 *
 */
public interface AnnotatedStateNFA<StateT, TransitionT>
        extends AnnotatedNFA<StateT, TransitionT>, StateNFA {

	public void annotate(State state, StateT annotation)
	        throws NoSuchStateException;

	public StateT getAnnotation(State state);

	public boolean hasAnnotation(State state);

	public void annotate(Symbol symbol, State fromState,
	                     State toState, TransitionT annotation)
            throws NoSuchTransitionException;

	public TransitionT getAnnotation(Symbol symbol, State fromState,
	                                 State toState);

	public boolean hasAnnotation(Symbol symbol, State fromState,
	                             State toState);

	public void annotate(Transition transition, TransitionT annotation)
            throws NoSuchTransitionException;

	public TransitionT getAnnotation(Transition transition);

	public boolean hasAnnotation(Transition transition);

}
