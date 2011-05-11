/*
 * Created on Aug 29, 2008
 * Modified on $Date: 2009-10-26 18:37:39 $
 */
package gjb.math;

import java.util.Collection;

/**
 * @author gjb
 * @version $Revision: 1.1 $
 * 
 */
public class Stats {

    protected double min = Double.MAX_VALUE, max = -Double.MAX_VALUE;
    protected double sum = 0.0, sumSqr = 0.0;
    int nrValues = 0;

    public void compute(double[] values) {
        for (double value : values)
            add(value);
    }

    public void compute(Collection<Double> values) {
        for (double value : values)
            add(value);
    }

    public void add(double value) {
        nrValues++;
        sum += value;
        sumSqr += value*value;
        if (value < min)
            min = value;
        if (value > max)
            max = value;
    }

    public void add(int value) {
        add((double) value);
    }

    public void add(float value) {
        add((double) value);
    }

    public double getMean() {
        return nrValues > 0 ? sum/nrValues : 0.0;
    }

    public double getStddev() {
        return nrValues > 1 ? Math.sqrt((sumSqr - sum*sum/nrValues)/(nrValues - 1)) : 0.0;
    }
    
    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

    public int getNumberOfValues() {
        return nrValues;
    }

    @Override
    public String toString() {
        return "N = " + getNumberOfValues() + ", mean = " + getMean() +
            ", stddev = " + getStddev() + ", min = " + getMin() +
            ", max = " + getMax();
    }

}
