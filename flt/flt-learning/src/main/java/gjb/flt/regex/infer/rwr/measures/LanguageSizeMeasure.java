/*
 * Created on Sep 11, 2008
 * Modified on $Date: 2009-10-27 09:30:13 $
 */
package gjb.flt.regex.infer.rwr.measures;

import gjb.flt.regex.infer.rwr.AutomatonRewriter;
import gjb.flt.regex.infer.rwr.impl.Automaton;
import gjb.flt.regex.infer.rwr.impl.GraphAutomatonFactory;
import gjb.flt.regex.infer.rwr.impl.Opportunity;

/**
 * @author gjb
 * @version $Revision: 1.2 $
 * 
 */
public class LanguageSizeMeasure implements OpportunityMeasure {

    protected int maxLength = -1;
    protected static GraphAutomatonFactory factory = new GraphAutomatonFactory();

    public int getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    public Opportunity measure(Automaton oldAutomaton, Automaton newAutomaton,
                               AutomatonRewriter repairer,
                               AutomatonRewriter rewriter, int[] indices) {
        double measure = compute(newAutomaton);
        return new Opportunity(indices, measure, repairer, rewriter);
    }

    public double compute(Automaton automaton) {
        int alphabetSize = automaton.getNumberOfStates() - 1;
        if (getMaxLength() < 0)
            setMaxLength(2*alphabetSize);
        double sum = 0.0;
        double norm = 0.0;
        double[]  stringCount = initStringCount(automaton);
        if (automaton.acceptsEpsilon()) {
            sum += 1.0;
            norm += 1.0;
        }
        for (int length = 1; length <= getMaxLength(); length++) {
            sum += Math.log1p(computeStringCount(automaton, stringCount));
            norm += Math.log1p(Math.pow(alphabetSize, length));
            if (length < maxLength)
                stringCount = updateStringCount(automaton, stringCount);
        }
        return (sum - norm)/norm;
    }

    protected double[] updateStringCount(Automaton newAutomaton, double[] stringCount) {
        double[] newCount = new double[stringCount.length];
        for (int j = 0; j < stringCount.length; j++) {
            newCount[j] = 0.0;
            for (int i = 0; i < stringCount.length; i++)
                newCount[j] += stringCount[i]*newAutomaton.get(i, j);
        }
        return newCount;
    }

    protected double computeStringCount(Automaton newAutomaton, double[] stringCount) {
        double count = 0.0;
        for (int i = 0; i < newAutomaton.getNumberOfStates() - 1; i++)
            count += stringCount[i]*newAutomaton.get(i, newAutomaton.getNumberOfStates() - 1);
        return count;
    }

    protected double[] initStringCount(Automaton newAutomaton) {
        double[] stringCount = new double[newAutomaton.getNumberOfStates() - 1];
        for (int j = 0; j < newAutomaton.getNumberOfStates() - 1; j++)
            stringCount[j] = newAutomaton.get(newAutomaton.getNumberOfStates() - 1, j);
        return stringCount;
    }

}
