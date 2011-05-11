/*
 * Created on Jun 7, 2007
 * Modified on $Date: 2009-10-27 14:14:01 $
 */
package gjb.flt.automata.measures;

import gjb.flt.automata.FeatureNotSupportedException;
import gjb.flt.automata.converters.Minimizer;
import gjb.flt.automata.converters.NFAMinimizer;
import gjb.flt.automata.factories.sparse.GlushkovFactory;
import gjb.flt.automata.impl.sparse.ModifiableStateNFA;
import gjb.flt.automata.impl.sparse.SparseNFA;
import gjb.flt.automata.impl.sparse.StateNFA;
import gjb.flt.regex.UnknownOperatorException;
import gjb.util.tree.SExpressionParseException;

/**
 * @author gjb
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
