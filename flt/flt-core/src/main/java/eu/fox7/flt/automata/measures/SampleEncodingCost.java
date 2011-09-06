/*
 * Created on Jul 19, 2007
 * Modified on $Date: 2009-10-27 14:14:01 $
 */
package eu.fox7.flt.automata.measures;

import static java.lang.Math.abs;
import static java.lang.Math.log;

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
public class SampleEncodingCost extends ExactSampleEncodingCost {

    @Override
    protected double cost(double n, double p, int length) {
        if (abs(p - 1.0) < 1e-3 || abs(p - n) < 1e-3) {
            return 1 + 2*log(length)/log(2.0);
        } else if (p <= n/2.0) {
            double x = p/n;
            return (2*log(length) - n*(x*log(x) + (1-x)*log(1-x)))/log(2.0);
        } else {
            return n - p;
        }
    }
    
}
