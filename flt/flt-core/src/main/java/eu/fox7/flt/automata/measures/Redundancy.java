/*
 * Created on Jun 7, 2007
 * Modified on $Date: 2009-10-27 14:14:01 $
 */
package eu.fox7.flt.automata.measures;

import eu.fox7.flt.automata.FeatureNotSupportedException;
import eu.fox7.flt.automata.converters.Minimizer;
import eu.fox7.flt.automata.converters.NFAMinimizer;
import eu.fox7.flt.automata.factories.sparse.GlushkovFactory;
import eu.fox7.flt.automata.impl.sparse.ModifiableStateNFA;
import eu.fox7.flt.automata.impl.sparse.SparseNFA;
import eu.fox7.flt.automata.impl.sparse.StateNFA;
import eu.fox7.flt.regex.UnknownOperatorException;
import eu.fox7.util.tree.SExpressionParseException;

/**
 * @author eu.fox7
 * @version $Revision: 1.1 $
 * 
 */
public class Redundancy {

    protected double stateRedundancy, transitionRedundancy, finalStateRedundancy;
    protected static GlushkovFactory glushkov = new GlushkovFactory();

    protected Redundancy(double stateRedundancy, double transitionRedundancy, double finalStateRedundancy) {
        this.stateRedundancy = stateRedundancy;
        this.transitionRedundancy = transitionRedundancy;
        this.finalStateRedundancy = finalStateRedundancy;
    }

    public static Redundancy compute(StateNFA nfa) {
    	Minimizer minimizer = new NFAMinimizer();
    	ModifiableStateNFA minNFA = new SparseNFA(nfa);
        minimizer.minimize(minNFA);
        double stateRedundancy = ((double) (nfa.getNumberOfStates() - minNFA.getNumberOfStates()))/nfa.getNumberOfStates();
        double transitionRedundancy = ((double) (nfa.getNumberOfTransitions() - minNFA.getNumberOfTransitions()))/nfa.getNumberOfTransitions();
        double finalStateRedundancy = ((double) (nfa.getNumberOfFinalStates() - minNFA.getNumberOfFinalStates()))/nfa.getNumberOfFinalStates();
        return new Redundancy(stateRedundancy, transitionRedundancy, finalStateRedundancy);
    }

    public static Redundancy compute(String regexStr)
            throws SExpressionParseException, UnknownOperatorException,
                   FeatureNotSupportedException {
        StateNFA nfa = glushkov.create(regexStr);
        return compute(nfa);
    }

    public double getFinalStateRedundancy() {
        return finalStateRedundancy;
    }

    public double getStateRedundancy() {
        return stateRedundancy;
    }

    public double getTransitionRedundancy() {
        return transitionRedundancy;
    }

}
