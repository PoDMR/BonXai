/**
 * Created on Oct 19, 2009
 * Modified on $Date: 2009-11-12 22:18:04 $
 */
package eu.fox7.flt.regex.generators;

import eu.fox7.flt.automata.FeatureNotSupportedException;
import eu.fox7.flt.automata.factories.sparse.ThompsonFactory;
import eu.fox7.flt.automata.impl.sparse.StateNFA;
import eu.fox7.flt.regex.Regex;
import eu.fox7.flt.regex.UnknownOperatorException;
import eu.fox7.math.ExponentialIntegerDistribution;
import eu.fox7.math.IllDefinedDistributionException;
import eu.fox7.math.ProbabilityDistribution;
import eu.fox7.math.UniformDistribution;
import eu.fox7.math.UserDefinedDistribution;
import eu.fox7.util.CarthesianProduct;
import eu.fox7.util.tree.Node;
import eu.fox7.util.tree.NodeTransformException;
import eu.fox7.util.tree.NodeVisitor;
import eu.fox7.util.tree.SExpressionParseException;
import eu.fox7.util.tree.Tree;
import eu.fox7.util.tree.TreeVisitor;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

/**
 * @author lucg5005
 * @version $Revision: 1.2 $
 *
 */
public class LanguageGenerator {

	protected Regex regex;
    protected String regexStr;
    protected Map<String,ProbabilityDistribution> distributionMap = new HashMap<String,ProbabilityDistribution>();
    protected static final double DEFAULT_AVERAGE_REPETITIONS = 4.0;
    protected double averageRepetitions = DEFAULT_AVERAGE_REPETITIONS;
    protected static final double DEFAULT_IGNORE_OPTIONAL = 0.5;
    protected double ignoreOptionalProbability = DEFAULT_IGNORE_OPTIONAL;
	protected eu.fox7.flt.automata.generators.LanguageGenerator languageGenerator;
	protected Tree tree;
	protected StateNFA nfa;

    public LanguageGenerator(String regexStr)
            throws SExpressionParseException, UnknownOperatorException {
    	this(regexStr, null);
    }

    public LanguageGenerator(String regexStr, Properties ndProp)
            throws SExpressionParseException, UnknownOperatorException {
    	this.regex = new Regex(ndProp);
    	computeDefaultDistritions();
    	this.regexStr = regexStr;
    	this.tree = this.regex.getTree(regexStr);
    	TreeVisitor treeVisitor = new TreeVisitor(tree);
    	try {
    		treeVisitor.visit(new probabilityDistributionVisitor(), null);
    	} catch (NodeTransformException e) {
    		throw (UnknownOperatorException) e.getException();
    	}
    	ThompsonFactory factory = new ThompsonFactory();
    	this.nfa = factory.create(tree);
    }

    public void addDistribution(String name, ProbabilityDistribution distr) {
        distributionMap.put(name, distr);
    }

    public Iterator<String> distributionNameIterator() {
        return distributionMap.keySet().iterator();
    }

    public ProbabilityDistribution getDistribution(String name) {
        return distributionMap.get(name);
    }

    /**
     * @return Returns the averageRepetitions.
     */
    public double getAverageRepetitions() {
        return averageRepetitions;
    }

    /**
     * @param averageRepetitions The averageRepetitions to set.
     */
    public void setAverageRepetitions(double averageRepetitions) {
        this.averageRepetitions = averageRepetitions;
        computeDefaultDistritions();
    }

    /**
     * @return Returns the ignoreOptionalProbability.
     */
    public double getIgnoreOptionalProbability() {
        return ignoreOptionalProbability;
    }

    /**
     * @param ignoreOptionalProbability The ignoreOptionalProbability to set.
     */
    public void setIgnoreOptionalProbability(double ignoreOptionalProbability) {
        this.ignoreOptionalProbability = ignoreOptionalProbability;
        computeDefaultDistritions();
    }

    public Tree getTree() {
    	return tree;
    }

    public Regex getRegex() {
    	return regex;
    }

    public StateNFA getNFA() {
    	return nfa;
    }

    /**
     * method that returns an Iterator over all accepted symbol strings upto the
     * length specified; symbol strings are encoded as List objects
     * @param length
     *            int that specifies the length of the longest accepted string
     * @return Iterator over the accepted strings encoded as Lists
     */
    public Iterator<List<String>> generatingRun(int length) {
    	if (languageGenerator == null)
    		languageGenerator = new eu.fox7.flt.automata.generators.LanguageGenerator(nfa);
        return languageGenerator.generatingRun(length);
    }

    public List<String> generateRandomExample(double repetitionStopProbability,
                                      double ignoreOptionalProbability) 
            throws UnknownOperatorException {
        setAverageRepetitions(1.0/repetitionStopProbability);
        setIgnoreOptionalProbability(ignoreOptionalProbability);
        return generateRandomExample();
    }

    public List<String> generateRandomExample() throws UnknownOperatorException {
        if (!tree.isEmpty()) {
            return generateRandomExample(tree.getRoot());
        } else {
            return null;
        }
    }

    protected List<String> generateRandomExample(Node node)
            throws UnknownOperatorException {
        String key = node.key();
        ProbabilityDistribution distr = distributionMap.get(node.value());
        if (regex.isOperatorSymbol(key)) {
            if (regex.concatOperator().equals(key)) {
                List<String> examplePart = new LinkedList<String>();
                for (int i = 0; i < node.getNumberOfChildren(); i++) {
                    examplePart.addAll(generateRandomExample(node.child(i)));
                }
                return examplePart;
            } else if (regex.unionOperator().equals(key)) {
                int index = distr.getNext();
                return generateRandomExample(node.child(index));
            } else if (regex.zeroOrMoreOperator().equals(key)) {
                List<String> examplePart = new LinkedList<String>();
                int number = distr.getNext();
                for (int i = 0; i < number; i++) {
                    examplePart.addAll(generateRandomExample(node.child(0)));
                }
                return examplePart;
            } else if (regex.oneOrMoreOperator().equals(key)) {
                List<String> examplePart = new LinkedList<String>();
                examplePart.addAll(generateRandomExample(node.child(0)));
                int number = distr.getNext();
                for (int i = 1; i < number; i++) {
                    examplePart.addAll(generateRandomExample(node.child(0)));
                }
                return examplePart;
            } else if (regex.zeroOrOneOperator().equals(key)) {
                int number = distr.getNext();
                if (number == 1) {
                    return generateRandomExample(node.child(0));
                } else {
                    return new LinkedList<String>();
                }
            } else {
                throw new UnknownOperatorException(key);
            }
        } else if (regex.isEpsilonSymbol(key)) {
            return new LinkedList<String>();
        } else if (regex.isEmptySymbol(key)) {
            throw new UnknownOperatorException(key);
        } else if (node.isLeaf()) {
            List<String> examplePart = new LinkedList<String>();
            examplePart.add(key);
            return examplePart;
        } else {
            throw new UnknownOperatorException(key);
        }
    }

    protected void createDefault(String type, int alternatives) {
        String name = "default-" + type + "-" + alternatives;
        if (!distributionMap.containsKey(name)) {
            distributionMap.put(name, new UniformDistribution(0, alternatives - 1));
        }
    }

    protected void computeDefaultDistritions() {
        ExponentialIntegerDistribution distr = new ExponentialIntegerDistribution(averageRepetitions);
        distr.setMin(0);
        distr.setMax(10);
        distributionMap.put("default-zeroOrMore", distr);
        distr = new ExponentialIntegerDistribution(averageRepetitions);
        distr.setMin(1);
        distr.setMax(10);
        distributionMap.put("default-oneOrMore", distr);
        SortedMap<Integer,Double> map = new TreeMap<Integer,Double>();
        map.put(0, ignoreOptionalProbability);
        map.put(1, 1.0 - ignoreOptionalProbability);
        try {
            distributionMap.put("default-zeroOrOne", new UserDefinedDistribution(map));
        } catch (IllDefinedDistributionException e) {
            throw new RuntimeException(e);
        }
    }

    public SortedSet<String> getAllCharacteristicStrings(int iterations)
            throws FeatureNotSupportedException {
        return getAllCharacteristicStrings(tree.getRoot(), iterations);
    }

    @SuppressWarnings("unchecked")
    public SortedSet<String> getAllCharacteristicStrings(Node node, int iterations)
            throws FeatureNotSupportedException {
        SortedSet<String> set = new TreeSet<String>();
        if (!regex.isOperatorSymbol(node.key())) {
            set.add(node.key());
        } else if (node.key().equals(regex.concatOperator())) {
            List<SortedSet<String>> parts = new LinkedList<SortedSet<String>>();
            for (int i = 0; i < node.getNumberOfChildren(); i++) {
                parts.add(getAllCharacteristicStrings(node.child(i), iterations));                
            }
            CarthesianProduct prod = new CarthesianProduct(parts);
            for (Iterator it = prod.iterator(); it.hasNext(); ) {
                List tokenList = (List) it.next();
                String str = StringUtils.join(tokenList.iterator(), " ").trim().replaceAll("\\s+", " ");
                set.add(str);
            }
        } else if (node.key().equals(regex.unionOperator())) {
            for (int i = 0; i < node.getNumberOfChildren(); i++) {
                set.addAll(getAllCharacteristicStrings(node.child(i), iterations));
            }
        } else if (node.key().equals(regex.zeroOrOneOperator())) {
            set.add("");
            set.addAll(getAllCharacteristicStrings(node.child(0), iterations));
        } else if (node.key().equals(regex.zeroOrMoreOperator())) {
            set.add("");
            addProductStrings(node, iterations, set);
        } else if (node.key().equals(regex.oneOrMoreOperator())) {
            addProductStrings(node, iterations, set);
        } else {
            throw new FeatureNotSupportedException(node.key());
        }
        return set;
    }

    @SuppressWarnings("unchecked")
    protected void addProductStrings(Node node, int iterations, Set<String> set)
            throws FeatureNotSupportedException, IndexOutOfBoundsException {
        SortedSet<String> basis = getAllCharacteristicStrings(node.child(0), iterations);
        List<List<String>> parts = new LinkedList<List<String>>();
        for (int i = 0; i < iterations; i++) {
            parts.add(new LinkedList<String>(basis));
            CarthesianProduct prod = new CarthesianProduct(parts);
            for (Iterator it = prod.iterator(); it.hasNext(); ) {
                List<String> tokenList = (List<String>) it.next();
                String str = StringUtils.join(tokenList.iterator(), " ").trim().replaceAll("\\s+", " ");
                set.add(str);
            }
        }
    }

    protected class probabilityDistributionVisitor implements NodeVisitor {

        public void visit(Node node, Map<String,Object> parameters)
                throws NodeTransformException {
		    String key = node.key().trim();
		    Pattern pattern = Pattern.compile("^([^\\[]+)\\[([^\\]]+)\\]$");
		    Matcher matcher = pattern.matcher(key);
		    if (matcher.matches()) {
		        String operator = matcher.group(1);
		        String distributionName = matcher.group(2);
		        node.setKey(operator);
		        node.setValue(distributionName);
		    } else {
		        if (regex.isOperatorSymbol(key)) {
		            if (regex.concatOperator().equals(key)) {
		            } else if (regex.unionOperator().equals(key)) {
		                node.setValue("default-union-" + node.getNumberOfChildren());
		                createDefault("union", node.getNumberOfChildren());
		            } else if (regex.zeroOrMoreOperator().equals(key)) {
		                node.setValue("default-zeroOrMore");
		            } else if (regex.oneOrMoreOperator().equals(key)) {
		                node.setValue("default-oneOrMore");
		            } else if (regex.zeroOrOneOperator().equals(key)) {
		                node.setValue("default-zeroOrOne");
		            } else {
		                throw new NodeTransformException("unknown operator encountered",
		                                                 new UnknownOperatorException(key));
		            }
		        } else if (regex.isEpsilonSymbol(node.key())) {
		        } else if (regex.isEmptySymbol(node.key())) {
	                throw new NodeTransformException("unknown operator encountered",
	                                                 new UnknownOperatorException(key));
		        } else if (node.isLeaf()) {
		        } else {
	                throw new NodeTransformException("unknown operator encountered",
	                                                 new UnknownOperatorException(key));
		        }
		        
		    }
		}
		
    }

    @Override
    public String toString() {
    	return "generator for " + getRegex().toString();
    }

}
