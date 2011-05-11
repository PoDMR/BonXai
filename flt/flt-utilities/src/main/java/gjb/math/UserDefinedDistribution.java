/*
 * Created on Jun 21, 2005
 * Modified on $Date: 2009-10-26 18:37:39 $
 */
package gjb.math;

import java.util.Enumeration;
import java.util.Properties;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.math.random.RandomDataImpl;

/**
 * @author gjb
 * @version $Revision: 1.1 $
 * 
 */
public class UserDefinedDistribution implements ProbabilityDistribution {

    protected int[] values;
    protected double[] probabilities;
    protected double[] cummProbabilities;
    protected RandomDataImpl rand;

    public UserDefinedDistribution(SortedMap<Integer,Double> distr)
            throws IllDefinedDistributionException {
        init(distr);
    }

    @SuppressWarnings("unchecked")
    public UserDefinedDistribution(Properties properties)
            throws IllDefinedDistributionException {
        SortedMap<Integer,Double> map = new TreeMap<Integer,Double>();
        for (Enumeration e = properties.propertyNames(); e.hasMoreElements(); ) {
            String valueStr = (String) e.nextElement();
            Integer value = Integer.valueOf(valueStr);
            Double prob = Double.valueOf(properties.getProperty(valueStr));
            map.put(value, prob);
        }
        init(map);
    }

    protected void init(SortedMap<Integer,Double> distr)
            throws IllDefinedDistributionException {
        values = new int[distr.size()];
        probabilities = new double[distr.size()];
        cummProbabilities = new double[distr.size()];
        int i = 0;
        double sum = 0.0;
        for (int value : distr.keySet()) {
            values[i] = value;
            probabilities[i] = distr.get(value);
            sum += probabilities[i];
            cummProbabilities[i] = sum;
            i++;
        }
        if (sum < 0.98 || sum > 1.001) {
            throw new IllDefinedDistributionException();
        }
        rand = new RandomDataImpl();
        rand.reSeed();
    }

    public int getValue(int index) {
        return values[index];
    }

    public double getProbability(int index) {
        return probabilities[index];
    }

    public int getNext() {
        double rand = this.rand.nextUniform(0.0, 1.0);
        for (int i = 0; i < cummProbabilities.length; i++) {
            if (rand <= cummProbabilities[i]) {
                return values[i];
            }
        }
        return values[values.length - 1];
    }

    public int getNextConstrained() {
        return getNext();
    }

    public String toString() {
        StringBuffer str = new StringBuffer();
        for (int i = 0; i < values.length; i++) {
            str.append(values[i]).append("\t");
            str.append(probabilities[i]).append("\t");
            str.append(cummProbabilities[i]).append("\n");
        }
        return str.toString();
    }
}
