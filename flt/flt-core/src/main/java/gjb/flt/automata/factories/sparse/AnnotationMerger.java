/*
 * Created on Feb 3, 2006
 * Modified on $Date: 2009-10-27 14:14:01 $
 */
package gjb.flt.automata.factories.sparse;

import gjb.flt.automata.impl.sparse.AnnotatedStateNFA;
import gjb.flt.automata.impl.sparse.State;
import gjb.flt.automata.impl.sparse.Transition;

import java.util.Map;
import java.util.Set;

/**
 * @author gjb
 * @version $Revision: 1.1 $
 * 
 */
public interface AnnotationMerger<StateT,TransitionT> {

    public void merge(AnnotatedStateNFA<StateT,TransitionT> oldNFA,
                      AnnotatedStateNFA<StateT,TransitionT> newNFA,
                      Map<State,Set<State>> stateMap);
    public StateT mergeStateAnnotations(StateT stateAnn1,
                                        StateT stateAnn2);
    public StateT mergeStateAnnotation(State state1, StateT stateAnn1,
                                       State state2, StateT stateAnn2);
    public TransitionT mergeTransitionAnnotations(TransitionT transAnn1,
                                                  TransitionT transAnn2);
    public TransitionT mergeTransitionAnnotations(Transition trans1,
                                                  TransitionT transAnn1,
                                                  Transition trans2,
                                                  TransitionT transAnn2);

}
