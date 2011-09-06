/**
 * Created on Nov 9, 2009
 * Modified on $Date: 2009-11-12 20:55:58 $
 */
package eu.fox7.flt.automata.measures;

import eu.fox7.flt.automata.impl.sparse.StateNFA;
import eu.fox7.flt.automata.impl.sparse.Transition;

/**
 * @author lucg5005
 * @version $Revision: 1.3 $
 *
 * SOAEditDistance is implemented for SOAs only.  For performance reasons, this is
 * not tested at runtime.  In case of doubt, use eu.fox7.flt.automata.measures.SOATest
 * before computing the edit distance.
 */
public class SOAEditDistance implements RelativeLanguageMeasure<Double> {

	public final double ADD_STATE = 1.0;
	public final double DELETE_STATE = ADD_STATE;
	public final double ADD_TRANSITION = 1.0;
	public final double DELETE_TRANSITION = ADD_TRANSITION;
	public final double ADD_FINAL_STATE = ADD_TRANSITION;
	public final double DELETE_FINAL_STATE = ADD_FINAL_STATE;
    protected double addTransitionCost = ADD_TRANSITION;
    protected double deleteTransitionCost = DELETE_TRANSITION;
    protected double addStateCost = ADD_STATE;
    protected double deleteStateCost = DELETE_STATE;
    protected double addFinalStateCost = ADD_FINAL_STATE;
    protected double deleteFinalState = DELETE_FINAL_STATE;

    /* (non-Javadoc)
	 * @see eu.fox7.flt.automata.measures.RelativeLanguageMeasure#compute(eu.fox7.flt.automata.impl.sparse.StateNFA, eu.fox7.flt.automata.impl.sparse.StateNFA)
	 */
	@Override
	public Double compute(StateNFA nfa1, StateNFA nfa2) {
		double cost = 0.0;
		cost += computeStateCost(nfa1, nfa2, getDeleteStateCost());
		cost += computeStateCost(nfa2, nfa1, getAddStateCost());
		cost += computeTransitionCost(nfa1, nfa2, getDeleteTransitionCost());
		cost += computeTransitionCost(nfa2, nfa1, getAddTransitionCost());
		cost += computeFinalStateCost(nfa1, nfa2, getDeleteFinalState());
		cost += computeFinalStateCost(nfa2, nfa1, getAddFinalStateCost());
		return cost;
	}

	protected double computeStateCost(StateNFA nfa1, StateNFA nfa2,
	                                  double editCost) {
		double cost = 0.0;
	    for (String stateValue1 : nfa1.getStateValues())
			if (!nfa2.hasState(stateValue1))
				cost += editCost;
	    return cost;
    }

	protected double computeFinalStateCost(StateNFA nfa1, StateNFA nfa2,
	                                       double editCost) {
		double cost = 0.0;
		for (String stateValue1 : nfa1.getFinalStateValues())
			if (!nfa2.isFinalState(stateValue1))
				cost += editCost;
		return cost;
	}
	
    protected double computeTransitionCost(StateNFA nfa1, StateNFA nfa2,
                                           double editCost) {
    	double cost = 0.0;
        for (Transition transition1 : nfa1.getTransitionMap().getTransitions()) {
    		String symbolValue = transition1.getSymbol().toString();
    		String fromValue = nfa1.getStateValue(transition1.getFromState());
    		String toValue = nfa1.getStateValue(transition1.getToState());
    		if (!nfa2.hasTransition(symbolValue, fromValue, toValue)) {
                cost += editCost;
            }
    	}
        return cost;
    }

	public double getAddTransitionCost() {
        return addTransitionCost;
    }

    public void setAddTransitionCost(double addTransitionCost) {
        this.addTransitionCost = addTransitionCost;
    }

    public double getDeleteTransitionCost() {
        return deleteTransitionCost;
    }

	public void setDeleteTransitionCost(double deleteTransitionCost) {
        this.deleteTransitionCost = deleteTransitionCost;
    }

	public double getAddFinalStateCost() {
    	return addFinalStateCost;
    }

	public void setAddFinalStateCost(double addFinalStateCost) {
    	this.addFinalStateCost = addFinalStateCost;
    }

	public double getDeleteFinalState() {
    	return deleteFinalState;
    }

	public void setDeleteFinalState(double deleteFinalState) {
    	this.deleteFinalState = deleteFinalState;
    }

	public double getAddStateCost() {
        return addStateCost;
    }

    public void setAddStateCost(double addStateCost) {
        this.addStateCost = addStateCost;
    }

    public double getDeleteStateCost() {
        return deleteStateCost;
    }

    public void setDeleteStateCost(double deleteStateCost) {
        this.deleteStateCost = deleteStateCost;
    }

    @Override
    public String toString() {
    	StringBuilder str = new StringBuilder();
    	str.append("add transition cost: ").append(getAddTransitionCost()).append("\n");
    	str.append("delete transition cost: ").append(getDeleteTransitionCost()).append("\n");
    	str.append("add state cost: ").append(getAddStateCost()).append("\n");
    	str.append("delete state cost: ").append(getDeleteStateCost()).append("\n");
    	return str.toString();
    }

}
