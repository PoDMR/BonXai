/*
 * Created on Feb 22, 2005
 * Modified on $Date: 2009-10-27 14:14:01 $
 */
package gjb.flt.regex.measures;

import gjb.flt.automata.FeatureNotSupportedException;
import gjb.flt.automata.factories.sparse.GlushkovFactory;
import gjb.flt.automata.impl.sparse.StateNFA;
import gjb.flt.automata.measures.StringCost;
import gjb.flt.regex.UnknownOperatorException;
import gjb.util.tree.SExpressionParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * @author gjb
 * @version $Revision: 1.1 $
 * 
 * <p>Class that performs a cost analysis of a given sample with respect to a
 * regular expression.  Specifically, three distributions are calculated:
 * <ul>
 *   <li>the number of examples versus the cost</li>
 *   <li>the number of unique examples versus the cost</li>
 *   <li>the number of times an example occurs in the sample</li>
 * </ul>
 */
public class CostDistribution {

    /**
     * Map that maps a cost to the number of examples that have this cost
     */
    protected SortedMap<Integer,Integer> costDistr = new TreeMap<Integer,Integer>();
    
    /**
     * Map that maps a cost to the number of unique examples that have this cost
     */
    protected SortedMap<Integer,Integer> uniqueCostDistr = new TreeMap<Integer,Integer>();
    
    /**
     * Map that maps an example to the number of times it occurs in the sample
     */
    protected Map<String,Integer> exampleDistr = new HashMap<String,Integer>();

    /**
     * the total number of examples in the sample
     */
    protected int totalSampleSize = 0;
    
    /**
     * the minimal and maximal costs of examples in the sample
     */
    protected int minCost = Integer.MAX_VALUE, maxCost = Integer.MIN_VALUE,
              maxMatchCost = Integer.MIN_VALUE;
    
    /**
     * constructor that calculates the distributions fromm a given sample with respect
     * the specified regular expression
     * @param regexStr
     *            String that represents the regular expression to calculate the
     *            example encoding cost for
     * @param properties
     *            Properties that specify the syntax of the regular expression,
     *            null for the default values
     * @param reader
     *            BufferedReader to read the examples from, one example per line,
     *            tokens are separated by whitespace(s)
     * @throws SExpressionParseException
     *            thrown if the S-expression for the regular expression is invalid
     * @throws UnknownOperatorException
     *            thrown if an unknown operator is used in the S-expression of the
     *            regular expression
     * @throws FeatureNotSupportedException
     *            thrown if a feature is used that is not implemented in the underlying
     *            Glushkov class
     * @throws IOException
     *            thrown if exceptions occur while reading from reader
     */
    public CostDistribution(String regexStr, Properties properties,
                            BufferedReader reader)
        throws SExpressionParseException, UnknownOperatorException,
               FeatureNotSupportedException, IOException {
        GlushkovFactory glushkov = new GlushkovFactory(properties);
        StateNFA nfa = glushkov.create(regexStr);
        StringCost measure = new StringCost(nfa);
        String line = null;
        while ((line = reader.readLine()) != null) {
            String example = line.trim();
            if (example.startsWith("#")) continue;
            String[] str = example.split("\\s+");
            int cost = measure.compute(str);
            increment(example, cost);
            if (cost < minCost) minCost = cost;
            if (cost > maxCost) maxCost = cost;
            if (cost > maxMatchCost && cost < Integer.MAX_VALUE) maxMatchCost = cost;
        }
    }

    /**
     * method that returns the maximum cost value in the sample, this value can
     * be Integer.MAX_VALUE if the sample contains example that is not matched by
     * the given regular expression
     * @return Returns the maxCost.
     */
    public int getMaxCost() {
        return maxCost;
    }
    
    /**
     * method that returns the maximum match cost
     * @return Returns the maxMatchCost.
     */
    public int getMaxMatchCost() {
        return maxMatchCost;
    }
    
    /**
     * method that returns the minimal cost value in the sample
     * @return Returns the minCost.
     */
    public int getMinCost() {
        return minCost;
    }
    
    /**
     * method returing the Iterator over the costs for the cost distribution
     * @return Iterator over the Integer costs in the cost distribution
     */
    public Iterator<Integer> costIterator() {
        return costDistr.keySet().iterator();
    }
    
    /**
     * method returing the Iterator over the costs for the unique cost distribution
     * @return Iterator over the Integer costs in the unique cost distribution
     */
    public Iterator<Integer> uniqueCostIterator() {
        return uniqueCostDistr.keySet().iterator();
    }
    
    /**
     * method that returns the Iterator over the examples in the example distrubition
     * @return Iterator over the String examples in the example distribution
     */
    public Iterator<String> exampleIterator() {
        return exampleDistr.keySet().iterator();
    }
    
    /**
     * method that returns the number of examples with the specified cost
     * @param cost
     * @return number of examples with the specified cost
     */
    public int getCost(int cost) {
        if (costDistr.containsKey(cost)) {
            return costDistr.get(cost);
        } else {
            return 0;
        }
    }

    /**
     * method that returns the number of unique examples with the specified cost
     * @param cost
     * @return int that is the number of unique examples in the sample with the
     *         given cost
     */
    public int getUniqueCost(int cost) {
        if (uniqueCostDistr.containsKey(cost)) {
            return uniqueCostDistr.get(cost);
        } else {
            return 0;
        }
    }

    /**
     * method that returns the total number of examples in the sample
     * @return Returns the totalSampleSize.
     */
    public int getTotalSampleSize() {
        return totalSampleSize;
    }
    
    /**
     * method that returns the number of times the specified examples occurs in
     * the sample
     * @param example String that represents an example
     * @return int the number of times the given example occurs in the sample
     */
    public int getExampleNumber(String example) {
        if (uniqueCostDistr.containsKey(example)) {
            return uniqueCostDistr.get(example);
        } else {
            return 0;
        }
    }

    /**
     * method for book keeping, it initializes and increments the various counters 
     * @param example String representation of the example
     * @param cost Integer the cost of the example
     */
    protected void increment(String example, int cost) {
        if (!costDistr.containsKey(cost)) {
            costDistr.put(cost, 0);
        }
        costDistr.put(cost, costDistr.get(cost) + 1);
        if (!exampleDistr.containsKey(example)) {
            exampleDistr.put(example, 0);
            if (!uniqueCostDistr.containsKey(cost)) {
                uniqueCostDistr.put(cost, new Integer(0));
            }
            uniqueCostDistr.put(cost, uniqueCostDistr.get(cost) + 1);
        }
        exampleDistr.put(example, exampleDistr.get(example) + 1);
        totalSampleSize++;
    }

}
