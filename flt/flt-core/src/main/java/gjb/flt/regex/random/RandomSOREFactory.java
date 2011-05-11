/*
 * Created on Feb 26, 2007
 * Modified on $Date: 2009-10-27 14:14:01 $
 */
package gjb.flt.regex.random;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

import gjb.flt.regex.InvalidXMLException;
import gjb.flt.regex.NoRegularExpressionDefinedException;
import gjb.flt.regex.Regex;
import gjb.flt.regex.UnknownOperatorException;
import gjb.flt.regex.converters.Simplifier;
import gjb.math.IllDefinedDistributionException;
import gjb.math.ProbabilityDistribution;
import gjb.math.ProbabilityDistributionFactory;
import gjb.util.RandomSelector;
import gjb.util.SymbolIterator;
import gjb.util.tree.Node;
import gjb.util.tree.SExpressionParseException;
import gjb.util.tree.Tree;

/**
 * @author gjb
 * @version $Revision: 1.1 $
 * 
 */
public class RandomSOREFactory {

    public static final String ALPHABET_SIZE = "alphabetSize";
    public static final String ALPHABET = "alphabet";
    public static final String PROBABILITY_DISTRIBUTION = "probabilityDistribution";
    public static final String FRACTION_UNARIES = "fractionUnaries";
    public static final String ALPHABET_SEP = ",";
    protected static final String defaultFractionUnaries = "0.3";
    protected Properties properties;
    protected RandomSelector<Node> nodeSelector = new RandomSelector<Node>();
    protected RandomSelector<String> operatorSelector = new RandomSelector<String>();
    protected Set<String> unaryOperators = new HashSet<String>();
    protected Set<String> naryOperators = new HashSet<String>();
    protected ProbabilityDistribution distr;

    public RandomSOREFactory(Properties properties) throws ConfigurationException {
        this.properties = properties;
        unaryOperators.add(Regex.ZERO_OR_ONE_OPERATOR);
        unaryOperators.add(Regex.ZERO_OR_MORE_OPERATOR);
        unaryOperators.add(Regex.ONE_OR_MORE_OPERATOR);
        naryOperators.add(Regex.UNION_OPERATOR);
        naryOperators.add(Regex.CONCAT_OPERATOR);
        ProbabilityDistributionFactory factory = new ProbabilityDistributionFactory();
        try {
            distr = factory.create(properties.getProperty(PROBABILITY_DISTRIBUTION,
                                                          initDefaultDistributionString(getAlphabetSize())));
        } catch (IllDefinedDistributionException e) {
            e.printStackTrace();
            throw new ConfigurationException(e);
        }
    }

    public Regex create() throws ConfigurationException {
        try {
            Simplifier simplifier = new Simplifier();
            return simplifier.simplifyRegex(new Regex(computeTree()));
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

    public double getFractionUnaries() {
        return Double.parseDouble(properties.getProperty(FRACTION_UNARIES, defaultFractionUnaries));
    }

    protected Tree computeTree() throws ConfigurationException {
        Tree tree = new Tree();
        if (getAlphabetSize() > 0) {
            Set<Node> nodes = initNodes();
            while (nodes.size() > 1) {
                Set<Node> operands = nodeSelector.selectSubsetFrom(nodes, distr.getNext());
                String operator;
                if (operands.size() == 1)
                    operator = operatorSelector.selectSubsetFrom(unaryOperators, 1).iterator().next();
                else
                    operator = operatorSelector.selectSubsetFrom(naryOperators, 1).iterator().next();
                Node opNode = new Node(operator);
                for (Node operand : operands) {
                    opNode.addChild(operand);
                    nodes.remove(operand);
                }
                nodes.add(opNode);
                
            }
            tree.setRoot(nodes.iterator().next());
        } else {
            tree.setRoot(new Node(Regex.EPSILON_SYMBOL));
        }
        return tree;
    }

    protected Set<Node> initNodes() throws ConfigurationException {
        Set<Node> set = new HashSet<Node>();
        if (properties.containsKey(ALPHABET)) {
            String[] alphabet = properties.getProperty(ALPHABET).split("\\s*" + ALPHABET_SEP + "\\s*");
            if (alphabet.length != getAlphabetSize())
                throw new ConfigurationException("alphabet size and alphabet don't match");
            for (int i = 0; i < alphabet.length; i++)
                set.add(new Node(alphabet[i].trim()));
        } else {
            SymbolIterator symbolIt = new SymbolIterator();
            for (int i = 0; i < getAlphabetSize(); i++)
                set.add(new Node(symbolIt.next()));
        }
        return set;
    }

    protected String initDefaultDistributionString(int alphabetSize) {
        String str = "gjb.math.UserDefinedDistribution(1=";
        if (alphabetSize > 1) {
            str += getFractionUnaries();
            for (int i = 2; i <= alphabetSize; i++)
                str += ", " + i + "=" + (1.0-getFractionUnaries())/(alphabetSize - 1);
        } else {
            str += "1.0";
        }
        return str + ")";
    }

}
