/*
 * Created on Feb 26, 2009
 * Modified on $Date: 2009-10-26 18:37:39 $
 */
package gjb.math;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Based on "Probability and statistics in engineering" by Hines, Montgomery,
 * Goldsman & Borror, 4th edition, Wiley, 2003, section 16.3, p. 496-499
 * @author gjb
 * @version $Revision: 1.1 $
 * 
 */
public class WilcoxonSignedRank {

	public boolean isApplicable(int n, Significance alpha) {
		return n >= alpha.minN();
	}

	public boolean test(double mu0, double[] observations, Significance alpha)
            throws InappropriateTestException {
        List<Double> diff = new ArrayList<Double>();
        for (double data : observations)
            diff.add(data - mu0);
        return test(diff, alpha);
    }

    public boolean test(double mu0, List<Double> observations,
                        Significance alpha)
            throws InappropriateTestException {
        List<Double> diff = new ArrayList<Double>();
        for (double data : observations)
            diff.add(data - mu0);
        return test(diff, alpha);
    }

    public boolean test(double[] obs1, double[] obs2, Significance alpha)
            throws InappropriateTestException {
        if (obs1.length != obs2.length)
            throw new InappropriateTestException("observations should have same length");
        List<Double> diff = new ArrayList<Double>();
        for (int i = 0; i < obs1.length; i++)
            diff.add(obs1[i] - obs2[i]);
        return test(diff, alpha);
    }
    
    public boolean test(List<Double> obs1, List<Double> obs2,
                        Significance alpha)
            throws InappropriateTestException {
        if (obs1.size() != obs2.size())
            throw new InappropriateTestException("observations should have same length");
        List<Double> diff = new ArrayList<Double>();
        for (int i = 0; i < obs1.size(); i++)
            diff.add(obs1.get(i) - obs2.get(i));
        return test(diff, alpha);
    }

    protected boolean test(List<Double> diff, Significance alpha)
            throws InappropriateTestException {
        double r = computeR(diff);
        int rCrit = WilcoxonTable.getCriticalValue(diff.size(), alpha);
        return r > rCrit;
    }

    protected double computeR(List<Double> diff) {
        rank(diff);
        List<Double> ranks = computeRanks(diff);
        double rPlus = 0;
        double rMin = 0;
        for (double rank : ranks)
            if (rank > 0.0)
                rPlus += rank;
            else
                rMin -= rank;
        double r = Math.min(rMin, rPlus);
        return r;
    }

    protected List<Double> computeRanks(List<Double> diff) {
        List<Double> rank = new ArrayList<Double>();
        for (int i = 0; i < diff.size(); ) {
            double r = i + 1;
            int count = 1;
            i++;
            while (i < diff.size() && Math.abs(diff.get(i - 1)) == Math.abs(diff.get(i))) {
                r += i + 1;
                count++;
                i++;
            }
            for (int j = 0; j < count; j++)
                rank.add(Math.signum(diff.get(i - count + j))*r/count);
        }
        return rank;
    }

    protected void rank(List<Double> obs) {
        Collections.sort(obs, new WilcoxonComparator());
    }

    protected static class WilcoxonComparator implements Comparator<Double> {

        public int compare(Double arg0, Double arg1) {
            return Double.compare(Math.abs(arg0.doubleValue()), Math.abs(arg1.doubleValue()));
        }
        
    }

/**
 * Based on "Probability and statistics in engineering" by Hines, Montgomery,
 * Goldsman & Borror, 4th edition, Wiley, 2003, Appendix table XI, p. 630
 * 
 * @author gjb
 * @version $Revision: 1.1 $
 *
 */
    public static class WilcoxonTable {
        
        protected static int[][] table = {
            {0, 2, 3, 5, 8, 10, 13, 17, 21, 25, 30, 35, 41, 47, 53, 60, 67, 75, 83, 91, 100, 110, 119, 130, 10, 151, 163, 175, 187, 200, 213, 227, 241, 256, 271, 286, 302, 319, 336, 353, 371, 389, 407, 426, 446, 466},
            {0, 2, 3, 5, 8, 10, 13, 17, 21, 25, 29, 34, 40, 46, 52, 58, 65, 73, 81, 89, 98, 107, 116, 126, 137, 147, 159, 170, 182, 195, 208, 221, 235, 249, 264, 279, 294, 310, 327, 343, 361, 378, 396, 415, 434},
            {0, 1, 3, 5, 7, 9, 12, 15, 19, 23, 27, 32, 37, 43, 49, 55, 62, 69, 76, 84, 92, 101, 110, 120, 130, 140, 151, 162, 173, 185, 198, 211, 224, 238, 252, 266, 281, 296, 312, 328, 345, 362, 379, 397},
            {0, 1, 3, 5, 7, 9, 12, 15, 19, 23, 27, 32, 37, 42, 48, 54, 61, 68, 75, 83, 91, 100, 109, 118, 128, 138, 148, 159, 171, 182, 194, 207, 220, 233, 247, 261, 276, 291, 307, 322, 339, 355, 373}
        };
        protected static double[] threshold = {1.645, 1.96, 2.33, 2.57};

        public static int getCriticalValue(int n, Significance alpha)
                throws InappropriateTestException {
            if (n >= alpha.minN()) {
                int index = n - alpha.minN();
                if (index < table[alpha.index()].length)
                    return table[alpha.index()][index];
                else {
                	double mean = n*(n+1.0)/4.0;
                	double sigma = Math.sqrt(n*(n+1.0)*(2.0*n + 1.0)/24.0);
                	return (int) Math.floor(mean - sigma*threshold[alpha.index()]);
                }
            } else {
                throw new InsufficientDataException(n + " data points are insufficient for " + alpha.level + ", at least " + alpha.minN() + " needed");
            }
        }

        public static boolean isSuitable(int n, Significance alpha) {
            return (n >= alpha.minN()) &&
                ((n - alpha.minN()) < table[alpha.index()].length);
        }

    }

    public enum Significance {
        ALPHA_0_10(0.10, 5, 0),
        ALPHA_0_05(0.05, 6, 1),
        ALPHA_0_02(0.02, 7, 2),
        ALPHA_0_01(0.01, 8, 3);
        
        private double level;
        private int minN;
        private int index;

        Significance(double level, int minN, int index) {
            this.level = level;
            this.minN = minN;
            this.index = index;
        }

        public double level() {
            return level;
        }

        public int minN() {
            return minN;
        }

        protected int index() {
            return index;
        }

    }
}
