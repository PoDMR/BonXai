/*
 * Created on May 28, 2007
 * Modified on $Date: 2009-10-27 14:14:01 $
 */
package gjb.flt.automata.measures;

import gjb.flt.automata.impl.sparse.StateNFA;
import gjb.flt.automata.matchers.NFAMatcher;

/**
 * @author gjb
 * @version $Revision: 1.1 $
 * 
 */
public class SampleError implements SampleMeasure {

    public double compute(StateNFA nfa, String[][] sample) {
    	NFAMatcher matcher = new NFAMatcher(nfa);
        double error = 0.0;
        for (int exampleNumber = 0; exampleNumber < sample.length; exampleNumber++) {
            String[] example = sample[exampleNumber];
            if (!matcher.matches(example))
                error += 1.0;
        }
        error /= sample.length;
        return error;
    }
    
}
