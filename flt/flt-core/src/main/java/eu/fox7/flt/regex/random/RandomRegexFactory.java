/*
 * Created on May 25, 2007
 * Modified on $Date: 2009-10-27 14:14:01 $
 */
package eu.fox7.flt.regex.random;

import eu.fox7.flt.regex.InvalidXMLException;
import eu.fox7.flt.regex.NoRegularExpressionDefinedException;
import eu.fox7.flt.regex.Regex;
import eu.fox7.flt.regex.UnknownOperatorException;
import eu.fox7.flt.regex.converters.Simplifier;
import eu.fox7.math.IllDefinedDistributionException;
import eu.fox7.math.ProbabilityDistribution;
import eu.fox7.math.ProbabilityDistributionFactory;
import eu.fox7.util.AlphabetIterator;
import eu.fox7.util.RandomSelector;
import eu.fox7.util.SymbolIterator;
import eu.fox7.util.tree.Node;
import eu.fox7.util.tree.SExpressionParseException;
import eu.fox7.util.tree.Tree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.commons.lang.StringUtils;

/**
 * @author eu.fox7
 * @version $Revision: 1.1 $
 * 
 */
public class RandomRegexFactory {

    public static final String ALPHABET_SIZE = "alphabetSize";
    public static final String ALPHABET = "alphabet";
    public static final String USE_DEFAULT_ALPHABET = "useDefaultAlphabet";
    public static final String TYPE_DISTRIBUTION = "typeDistribution";
    public static final String DISTRIBUTION_SEPARATOR = ",";
    public static final String DISTRIBUTION_ASSIGNMENT = "=";
    public static final String PROBABILITY_DISTRIBUTION = "probabilityDistribution";
    public static final String FRACTION_UNARIES = "fractionUnaries";
    public static final String ALPHABET_SEP = ",";
    protected static final String defaultFractionUnaries = "0.3";
    public static final String OPERATOR_DISTRIBUTION = "operatorDistribution";
    public static final String NO_SIMPLIFICATION = "noSimplification";
    protected Properties properties;
    protected RandomSelector<Node> nodeSelector = new RandomSelector<Node>();
    protected Set<String> unaryOperators = new HashSet<String>();
    protected Set<String> naryOperators = new HashSet<String>();
    protected Map<String,Double> operatorDistribution;
    protected RandomSelector<String> operatorSelector;
    protected ProbabilityDistribution distr;

    public RandomRegexFactory(Properties properties) throws ConfigurationException {
        this.properties = properties;
        unaryOperators.add(Regex.ZERO_OR_ONE_OPERATOR);
        unaryOperators.add(Regex.ZERO_OR_MORE_OPERATOR);
        unaryOperators.add(Regex.ONE_OR_MORE_OPERATOR);
        naryOperators.add(Regex.UNION_OPERATOR);
        naryOperators.add(Regex.CONCAT_OPERATOR);
        operatorDistribution = computeOperatorDistr(properties.getProperty(OPERATOR_DISTRIBUTION,
                                                                           initDefaultOperatorDistribution(unaryOperators, naryOperators)));
        ProbabilityDistributionFactory factory = new ProbabilityDistributionFactory();
        try {
            operatorSelector = new RandomSelector<String>(operatorDistribution);
            distr = factory.create(properties.getProperty(PROBABILITY_DISTRIBUTION,
                                                          initDefaultDistributionString(getAlphabetSize())));
        } catch (IllDefinedDistributionException e) {
            e.printStackTrace();
            throw new ConfigurationException(e);
        }
    }

    protected String initDefaultOperatorDistribution(Set<String> unaryOperators,
                                                     Set<String> naryOperators) {
        assert unaryOperators != null && naryOperators != null : "operator sets can't be null";
        assert !unaryOperators.isEmpty() && !naryOperators.isEmpty() : "no operators defined";
        String[] strs = new String[unaryOperators.size() + naryOperators.size()];
        int i = 0;
        for (String operator : unaryOperators)
            strs[i++] = operator + DISTRIBUTION_ASSIGNMENT +
                            getFractionUnaries()/unaryOperators.size();
        for (String operator : naryOperators)
            strs[i++] = operator + DISTRIBUTION_ASSIGNMENT +
                            (1.0 - getFractionUnaries())/naryOperators.size();
        return StringUtils.join(strs, DISTRIBUTION_SEPARATOR);
    }

    protected Map<String,Double> computeOperatorDistr(String distrStr) {
        Map<String,Double> operatorDistr = new HashMap<String,Double>();
        String[] defs = distrStr.split(DISTRIBUTION_SEPARATOR);
        for (String def : defs) {
            String[] values = def.split(DISTRIBUTION_ASSIGNMENT);
            operatorDistr.put(values[0], Double.parseDouble(values[1]));
        }
        return operatorDistr;
    }

    public Regex create() throws ConfigurationException {
        try {
            Regex regex = new Regex(computeTree());
            if (properties.getProperty(NO_SIMPLIFICATION, "false").trim().toLowerCase().equals("true"))
                return regex;
            else {
                Simplifier simplifier = new Simplifier();
                return simplifier.simplifyRegex(regex);
            }
        } catch (UnknownOperatorException e) {
            throw new ConfigurationException(e);
        } catch (TransformerConfigurationException e) {
            throw new ConfigurationException(e);
        } catch (SExpressionParseException e) {
            throw new ConfigurationException(e);
        } catch (NoRegularExpressionDefinedException e) {
            throw new ConfigurationException(e);
        } catch (TransformerException e) {
            throw new ConfigurationException(e);
        } catch (InvalidXMLException e) {
            throw new ConfigurationException(e);
        }
    }

    public int getAlphabetSize() {
        return Integer.parseInt(properties.getProperty(ALPHABET_SIZE));
    }

    public String[] getAlphabet() throws ConfigurationException {
        if (properties.containsKey(ALPHABET)) {
            Set<String> set = new HashSet<String>();
            String[] alphabet = properties.getProperty(ALPHABET).split("\\s*" + ALPHABET_SEP + "\\s*");
            if (alphabet.length != getAlphabetSize())
                throw new ConfigurationException("alphabet size and alphabet don't match");
            for (int i = 0; i < alphabet.length; i++) {
                alphabet[i] = alphabet[i].trim();
                set.add(alphabet[i]);
            }
            if (set.size() != alphabet.length)
                throw new ConfigurationException("alphabet contains the same symbol multiple times");
            return alphabet;
        } else if (properties.containsKey(USE_DEFAULT_ALPHABET)) {
            if (getAlphabetSize() > 26)
                throw new ConfigurationException("default alphabet requested for alphabet size larger than 26");
            String[] alphabet = new String[getAlphabetSize()];
            AlphabetIterator alphabetIt = new AlphabetIterator();
            for (int i = 0; i < alphabet.length; i++)
                alphabet[i] = alphabetIt.next();
            return alphabet;
        } else {
            String[] alphabet = new String[getAlphabetSize()];
            SymbolIterator symbolIt = new SymbolIterator();
            for (int i = 0; i < getAlphabetSize(); i++)
                alphabet[i] = symbolIt.next();
            return alphabet;
        }
    }

    public int[] getTypeDistribution() throws ConfigurationException {
        int[] distr = new int[getAlphabetSize()];
        String[] distrStr = {};
        if (properties.containsKey(TYPE_DISTRIBUTION))
            distrStr = properties.getProperty(TYPE_DISTRIBUTION).split(DISTRIBUTION_SEPARATOR);
        if (distrStr.length > distr.length)
            throw new ConfigurationException("alphabet size and type distribution size should match");
        for (int i = 0; i < distrStr.length; i++)
            distr[i] = Integer.parseInt(distrStr[i].trim());
        for (int i = distrStr.length; i < distr.length; i++)
            distr[i] = 1;
        return distr;
    }

    public double getFractionUnaries() {
        return Double.parseDouble(properties.getProperty(FRACTION_UNARIES, defaultFractionUnaries));
    }

    protected Tree computeTree() throws ConfigurationException {
        Tree tree = new Tree();
        if (getAlphabetSize() > 0) {
            List<Node> nodes = initNodes();
            while (nodes.size() > 1) {
                List<Node> operands = new ArrayList<Node>();
                String operator = operatorSelector.selectOne();
                int subListSize = 1;
                if (naryOperators.contains(operator))
                    subListSize = Math.min(distr.getNext(), nodes.size());
                for (int i = 0; i < subListSize; i++)
                    operands.add(nodes.remove(0));
                Node opNode = new Node(operator);
                for (Node operand : operands) {
                    opNode.addChild(operand);
                }
                nodes.add(0, opNode);
            }
            String operator = operatorSelector.selectOne();
            if (unaryOperators.contains(operator)) {
                Node opNode = new Node(operator);
                opNode.addChild(nodes.remove(0));
                nodes.add(0, opNode);
            }
            tree.setRoot(nodes.get(0));
        } else {
            tree.setRoot(new Node(Regex.EPSILON_SYMBOL));
        }
        return tree;
    }

    protected List<Node> initNodes() throws ConfigurationException {
        List<Node> nodeList = new ArrayList<Node>();
        String[] alphabet = getAlphabet();
        int[] typeDistr = getTypeDistribution();
        assert alphabet.length == typeDistr.length : "size of alphabet and type distribution must match";
        for (int i = 0; i < alphabet.length; i++)
            for (int j = 0; j < typeDistr[i]; j++)
                nodeList.add(new Node(alphabet[i]));
        Collections.shuffle(nodeList);
        return nodeList;
    }

    protected String initDefaultDistributionString(int alphabetSize) {
        String str = "eu.fox7.math.UserDefinedDistribution(";
        if (alphabetSize > 1) {
            str += "1=0.0";
            String probStr = "";
            double sum = 0.0;
            for (int i = 3; i <= alphabetSize; i++) {
                double prob = Math.pow(2.0, 1 - i);
                probStr += DISTRIBUTION_SEPARATOR + i +
                           DISTRIBUTION_ASSIGNMENT + prob;
                sum += prob;
            }
            str += DISTRIBUTION_SEPARATOR + 2 + DISTRIBUTION_ASSIGNMENT + (1.0 - sum);
            str += probStr;
        } else {
            str += "1=1.0";
        }
        return str + ")";
    }

}
