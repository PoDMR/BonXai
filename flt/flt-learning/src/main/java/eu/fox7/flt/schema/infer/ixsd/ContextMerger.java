/*
 * Created on Nov 1, 2006
 * Modified on $Date: 2009-11-10 14:01:49 $
 */
package eu.fox7.flt.schema.infer.ixsd;

import eu.fox7.flt.FLTRuntimeException;
import eu.fox7.flt.automata.NoSuchStateException;
import eu.fox7.flt.automata.NoSuchTransitionException;
import eu.fox7.flt.automata.factories.sparse.AnnotationMerger;
import eu.fox7.flt.automata.impl.sparse.AnnotatedStateNFA;
import eu.fox7.flt.automata.impl.sparse.State;
import eu.fox7.flt.automata.impl.sparse.Transition;
import eu.fox7.flt.treeautomata.impl.ContentAutomaton;

import java.util.Map;
import java.util.Set;

/**
 * @author eu.fox7
 * @version $Revision: 1.2 $
 * 
 */
public class ContextMerger implements AnnotationMerger<ContentAutomaton, Integer> {

    public ContentAutomaton mergeStateAnnotations(ContentAutomaton model1,
                                                  ContentAutomaton model2) {
    	for (String stateValue : model1.getStateValues()) {
    		if (!model2.hasState(stateValue))
    			model2.addState(stateValue);
    		if (model1.isFinalState(stateValue)) {
    			if (!model2.isFinalState(stateValue))
    				model2.addFinalState(stateValue);
    			State state = model1.getState(stateValue);
    			try {
	                model2.annotate(stateValue, model1.getFinalStateSupport(state));
                } catch (NoSuchStateException e) {
	                e.printStackTrace();
	                throw new FLTRuntimeException("this can't happend", e);
                }
    		}
    	}
    	for (Transition transition : model1.getTransitionMap().getTransitions()) {
    		String symbolValue = transition.getSymbol().toString();
    		String fromValue = model1.getStateValue(transition.getFromState());
    		String toValue = model1.getStateValue(transition.getToState());
    		if (!model2.hasTransition(symbolValue, fromValue, toValue))
    			model2.addTransition(symbolValue, fromValue, toValue);
    		int support1 = model1.getSupport(symbolValue, fromValue, toValue);
    		int support2 = 0;
    		if (model2.hasAnnotation(symbolValue, fromValue, toValue))
    			support2 = model2.getSupport(symbolValue, fromValue, toValue);
    		try {
	            model2.annotate(symbolValue, fromValue, toValue, support1 + support2);
            } catch (NoSuchTransitionException e) {
                e.printStackTrace();
                throw new FLTRuntimeException("this can't happend", e);
            }
    	}
        return model2;
    }

    public ContentAutomaton mergeStateAnnotation(State state1, ContentAutomaton model1,
                                                 State state2, ContentAutomaton model2) {
        return mergeStateAnnotations(model1, model2);
    }

    public Integer mergeTransitionAnnotations(Integer transAnn1, Integer transAnn2) {
        return (transAnn1 == null ? 0 : transAnn1) +
               (transAnn2 == null ? 0 : transAnn2);
    }

    public Integer mergeTransitionAnnotations(Transition trans1, Integer transAnn1,
                                              Transition trans2, Integer transAnn2) {
        return mergeTransitionAnnotations(transAnn1, transAnn2);
    }

	@Override
    public void merge(AnnotatedStateNFA<ContentAutomaton, Integer> oldNFA,
                      AnnotatedStateNFA<ContentAutomaton, Integer> newNFA,
                      Map<State, Set<State>> stateMap) {}

}
