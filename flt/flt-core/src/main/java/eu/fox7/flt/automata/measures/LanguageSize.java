/*
 * Created on Jun 4, 2007
 * Modified on $Date: 2009-10-27 14:14:01 $
 */
package eu.fox7.flt.automata.measures;

import eu.fox7.flt.automata.factories.dense.DoubleMatrixRepresentationFactory;
import eu.fox7.flt.automata.impl.dense.MatrixRepresentation;
import eu.fox7.flt.automata.impl.sparse.StateNFA;

import static java.lang.Math.log1p;
import static java.lang.Math.pow;

/**
 * @author eu.fox7
 * @version $Revision: 1.1 $
 * 
 */
public class LanguageSize implements LanguageMeasure<Double>, RelativeLanguageMeasure<Double> {

	protected DoubleMatrixRepresentationFactory factory = new DoubleMatrixRepresentationFactory();

    public Double compute(StateNFA nfa) {
        MatrixRepresentation<Double> m = factory.create(nfa);
        double sum = 0.0;
        double norm = 0.0;
        int maxLength = 2*nfa.getNumberOfStates();
        for (int length = 0; length <= maxLength; length++) {
            sum += log1p(m.getNumberOfAcceptedStrings(length).doubleValue());
            norm += log1p(pow(nfa.getNumberOfSymbols(), length));
        }
        return (sum - norm)/norm;
    }

    public Double compute(StateNFA inclNFA, StateNFA nfa) {
        MatrixRepresentation<Double> inclM = factory.create(inclNFA);
        MatrixRepresentation<Double> m = factory.create(nfa);
        double sum = 0.0;
        double norm = 0.0;
        int maxLength = 2*Math.max(nfa.getNumberOfStates(), inclNFA.getNumberOfStates());
        for (int length = 0; length < maxLength; length++) {
            sum += log1p(inclM.getNumberOfAcceptedStrings(length).doubleValue());
            norm += log1p(m.getNumberOfAcceptedStrings(length).doubleValue());
        }
        return (sum - norm)/norm;
    }

}
