/*
 * Created on Jul 20, 2007
 * Modified on $Date: 2009-10-27 14:14:01 $
 */
package eu.fox7.flt.automata.measures;

import static java.lang.Math.abs;
import static java.lang.Math.log;
import static java.lang.Math.rint;
import eu.fox7.flt.automata.factories.dense.DoubleMatrixRepresentationFactory;
import eu.fox7.flt.automata.impl.dense.MatrixRepresentation;
import eu.fox7.flt.automata.impl.sparse.StateNFA;
import eu.fox7.flt.automata.matchers.NFAMatcher;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

/**
 * This implementation was taken from:
 * The power and perils of MDL
 * Pieter Adriaans and Paul Vitanyi
 * arXiv:cs.LG/0612095v1, 19 December 2006
 * 
 * @author eu.fox7
 * @version $Revision: 1.1 $
 *
 */
public class ExactSampleEncodingCost implements SampleMeasure {

	protected DoubleMatrixRepresentationFactory factory = new DoubleMatrixRepresentationFactory();

    public ExactSampleEncodingCost() {
        super();
    }

    public double compute(StateNFA nfa, List<String[]> sample) {
        return compute(nfa, sample.toArray(new String[0][]));
    }

    public double compute(StateNFA nfa, String[][] sample) {
    	NFAMatcher matcher = new NFAMatcher(nfa);
        for (String[] example : sample)
            if (!matcher.matches(example))
                return Double.POSITIVE_INFINITY;
        MatrixRepresentation<Double> m = factory.create(nfa);
        double cost = 0.0;
        int maxLength = 2*nfa.getNumberOfStates();
        Map<Integer,Integer> sampleDistr = computeDistribution(sample, maxLength);
        for (int length = 1; length <= maxLength; length++)
            if (sampleDistr.containsKey(length)) {
                Double languageSize = m.getNumberOfAcceptedStrings(length);
                int sampleSize = sampleDistr.get(length);
                cost += cost(languageSize.doubleValue(), sampleSize, length);
            }
        return cost;
    }

    protected Map<Integer,Integer> computeDistribution(String[][] sample, int maxLength) {
        Map<Integer,Set<String>> map = new HashMap<Integer,Set<String>>();
        for (String[] example : sample) {
            int length = example.length;
            if (length <= maxLength) {
                if (!map.containsKey(length))
                    map.put(length, new HashSet<String>());
                map.get(length).add(StringUtils.join(example, ","));
            }
        }
        Map<Integer,Integer> distr = new HashMap<Integer,Integer>();
        for (Integer length : map.keySet())
            distr.put(length, map.get(length).size());
        return distr;
    }

    protected double cost(double n, double p, int length) {
        double cost = 0.0;
        if (abs(p - 1.0) < 1e-3 || abs(p - n) < 1e-3) {
            return 1 + 2*log(length)/log(2.0);
        } else if (p < n/2.0) {
            for (long i = 2; i <= rint(p); i++)
                cost -= log(i);
            for (long i = (long) rint(n - p + 1.0); i <= rint(n); i++)
                cost += log(i);
            return cost/log(2.0);
        } else {
            for (long i = 2; i <= rint(n - p); i++)
                cost -= log(i);
            for (long i = (long) rint(p + 1.0); i <= rint(n); i++)
                cost += log(i);
            return cost/log(2.0);
        }
    }

}