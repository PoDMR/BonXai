package gjb.flt.regex;

import gjb.util.Pair;
import gjb.util.tree.Node;
import gjb.util.tree.SExpressionParseException;
import gjb.util.tree.Tree;

import java.io.StringReader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>Class that implements a parser for regular expressions in prefix notation
 * (s-expressions a la Lisp).  The expression can be converted to a parse tree
 * representation or to an NFA.</p>
 * 
 * <p>The class supports the following operators: concat, union, intersection,
 * interleave, zeroOrOne, zeroOrMMore, oneOrMore, mToN as well as the empty
 * language and the empty string.  Those operators have
 * default associated symbols:
 * <table>
 *   <tr><th>operator name</th><th>default symbol</th><th>arity</th></tr>
 *   <tr><td>concat</td><td><code>.</code></td><td>n-ary</td></tr>
 *   <tr><td>union</td><td><code>|</code></td><td>n-ary</td></tr>
 *   <tr><td>intersection</td><td><code>&</code></td><td>n-ary</td></tr>
 *   <tr><td>interleave</td><td><code>#</code></td><td>n-ary</td></tr>
 *   <tr><td>zeroOrOne</td><td><code>?</code></td><td>unary</td></tr>
 *   <tr><td>zeroOrMore</td><td><code>*</code></td><td>unary</td></tr>
 *   <tr><td>oneOrMore</td><td><code>+</code></td><td>unary</td></tr>
 *   <tr><td>mToN</td><td><code>{m,n}</code></td><td>unary</td></tr>
 *   <tr><td>empty</td><td><code>EMPTY</code></td><td>0-ary</td></tr>
 *   <tr><td>epsilon</td><td><code>EPSILON</code></td><td>0-ary</td></tr>
 * </table>
 * The mToN operator is implemented via a regular expression, by default
 * <code>\\{(\\d*),(\\d*)\\}</code>.  Note that no spaces are allowed and
 * that either m or N is optional with the same semantics as Perl
 * regular expressions.</p>
 * 
 * <p>The interleave operator is syntactical sugar since <code>(# (a) (b))</code>
 * is shorthand for <code>(| (. (a) (b)) (. (b) (a)))</code>.  Note that the
 * interleave operator is n-ary.</p>
 * 
 * <p>These default symbols can be (partially) overridden by supplying
 * the appropriate <code>Properties</code> upon construction of a
 * <code>Regex</code>.</p>
 * 
 * <p>Examples:
 * <pre>
 *   (. (a) (| (b) (c)))
 * 
 *   (. (* (a)) (# (b) (c)))
 * 
 *   (? (. (| (+ (b)) (a)) (c)))
 * </pre></p>
 * 
 * <p>Usage:
 * <pre>
 *   Regex regex = new Regex();   // default symbols
 *   NFA nfa = regex.nfa("(. (| (a) (b)) (c))");
 * </pre>
 * <br/>
 * <pre>
 *   Properties p = new Properties();
 *   p.setProperty("concat", ",");
 *   Regex regex = new Regex(p);
 *   NFA nfa = regex.nfa("(, (| (a) (b)) (c))");
 * </pre></p>
 *   
 * @author gjb
 * @version 1.0
 */
public class Regex {

    protected static final String PATTERN_ESCAPE = "\\";
    public static final String LEFT_BRACKET = "(";
    public static final String RIGHT_BRACKET = ")";
    public static final String UNION_OPERATOR = "|";
    public static final String CONCAT_OPERATOR = ".";
    public static final String INTERSECTION_OPERATOR = "&";
    public static final String INTERLEAVE_OPERATOR = "#";
    public static final String ZERO_OR_ONE_OPERATOR = "?";
    public static final String ZERO_OR_MORE_OPERATOR = "*";
    public static final String ONE_OR_MORE_OPERATOR = "+";
    public static final String M_TO_N_LEFT_BRACKET = "{";
    public static final String M_TO_N_RIGHT_BRACKET = "}";
    public static final String M_TO_N_SEPARATOR = ",";
    public static final String M_TO_N_NUMBER_PATTERN = "(\\d*)";
    public static final String M_TO_N_MIN = "m";
    public static final String M_TO_N_MAX = "n";
    public static final String EPSILON_SYMBOL = "EPSILON";
    public static final String EMPTY_SYMBOL = "EMPTY";
    public static final String INFIX_SEP = " ";
    /**
     * <code>Properties</code> that store the mapping between operators and
     * the symbols used for them in the regular expressions
     */
	protected Properties properties = new Properties();
	/**
	 * list of the operator names to distinguish them from empty and epsilon
	 */
	protected String[] operatorNames = {"zeroOrOne", "zeroOrMore",
										"oneOrMore", "mToN", "concat",
										"union", "intersection",
										"interleave"};
	/**
	 * set of the operators to quickly check whether a string is an operator
	 */
	protected Set<String> operators;
	/**
	 * <code>Pattern</code> that encodes the mToN operator syntax;  this has to
	 * be implemented via a regular expression since <code>m</code> and <code>N</code>
	 * can have arbitrary values
	 */
	protected Pattern mToMPattern;

    protected Tree tree;

	/**
	 * constructor for a regular expression parser using the default symbols
	 */
	public Regex() {
		properties.setProperty("zeroOrOne", ZERO_OR_ONE_OPERATOR);
		properties.setProperty("zeroOrMore", ZERO_OR_MORE_OPERATOR);
		properties.setProperty("oneOrMore", ONE_OR_MORE_OPERATOR);
        properties.setProperty("mToNLeftBracket", M_TO_N_LEFT_BRACKET);
        properties.setProperty("mToNRightBracket", M_TO_N_RIGHT_BRACKET);
        properties.setProperty("mToNSeparator", M_TO_N_SEPARATOR);
        properties.setProperty("mToNNumberPattern", M_TO_N_NUMBER_PATTERN);
        properties.setProperty("mToNMin", M_TO_N_MIN);
        properties.setProperty("mToNMax", M_TO_N_MAX);
		properties.setProperty("mToN",
                               PATTERN_ESCAPE +
                               mToNLeftBracket() +
                               mToNNumberPattern() +
                               mToNSeparator() +
                               mToNNumberPattern() +
                               PATTERN_ESCAPE +
                               mToNRightBracket());
		properties.setProperty("concat", CONCAT_OPERATOR);
		properties.setProperty("union", UNION_OPERATOR);
		properties.setProperty("intersection", INTERSECTION_OPERATOR);
		properties.setProperty("interleave", INTERLEAVE_OPERATOR);
		properties.setProperty("empty", EMPTY_SYMBOL);
		properties.setProperty("epsilon", EPSILON_SYMBOL);
        properties.setProperty("leftBracket", LEFT_BRACKET);
        properties.setProperty("rightBracket", RIGHT_BRACKET);
        properties.setProperty("infixSep", INFIX_SEP);
		init();
	}

	/**
	 * constructor to override some or all of the default symbols
	 * @param ndProp <code>Properties</code> associating operator names with
	 * symbols
	 */
	@SuppressWarnings("unchecked")
    public Regex(Properties ndProp) {
		this();
		if (ndProp != null) {
		    for (Enumeration e = ndProp.propertyNames(); e.hasMoreElements(); ) {
		        String key = (String) e.nextElement();
		        properties.setProperty(key, ndProp.getProperty(key));
		    }
		    init();
		}
	}

	/**
	 * constructor for a regular expression parser using the default symbols
	 * @param regex regular expression to parse in prefix notation
	 * @throws SExpressionParseException thrown if the expression is not
	 * a valid s-expression
	 * @throws UnknownOperatorException thrown if an unknown operator is used
	 * in the regular expression
	 */
	public Regex(String regexStr)
	        throws SExpressionParseException, UnknownOperatorException {
	    this(regexStr, null);
	}

	/**
	 * constructor to override some or all of the default symbols
	 * @param regex regular expression to parse in prefix notation
	 * @param ndProp <code>Properties</code> associating operator names with
	 * symbols
	 * @throws SExpressionParseException thrown if the expression is not
	 * a valid s-expression
	 * @throws UnknownOperatorException thrown if an unknown operator is used
	 * in the regular expression
	 */
	public Regex(String regexStr, Properties ndProp)
            throws SExpressionParseException, UnknownOperatorException {
	    this(ndProp);
        this.tree = getTree(regexStr);
	}

    /**
     * constructor for a regular expression parser using the default symbols
     * @param tree Tree that represent the parse tree of a regular expression
     * @throws UnknownOperatorException thrown if an unknown operator is used
     * in the regular expression
     */
   public Regex(Tree tree) throws UnknownOperatorException {
        this(tree, null);
    }

    /**
     * constructor to override some or all of the default symbols
     * @param tree Tree that represent the parse tree of a regular expression
     * @param ndProp <code>Properties</code> associating operator names with
     * symbols
     * @throws UnknownOperatorException thrown if an unknown operator is used
     * in the regular expression
     */
    public Regex(Tree tree, Properties ndProp) throws UnknownOperatorException {
        this(ndProp);
        this.tree = tree;
    }

	/**
	 * helper method that computes the <code>Pattern</code>s for certain operators
	 *
	 */
	protected void init() {
	    mToMPattern = (Pattern.compile(properties.getProperty("mToN")));
	}
	
	public Properties getProperties() {
		return properties;
	}

	protected String operator(String type) {
		return properties.getProperty(type);
	}

	public String concatOperator() {
		return operator("concat");
	}

	public String unionOperator() {
		return operator("union");
	}

	public String intersectionOperator() {
		return operator("intersection");
	}

	public String interleaveOperator() {
	    return operator("interleave");
	}
	
	public String zeroOrOneOperator() {
		return operator("zeroOrOne");
	}

	public String zeroOrMoreOperator() {
		return operator("zeroOrMore");
	}

	public String oneOrMoreOperator() {
		return operator("oneOrMore");
	}

	public String mToNOperator() {
	    return operator("mToN");
	}
	
	public String mToNOperator(int minOccrus, int maxOccurs) {
		return mToNLeftBracket() + minOccrus + mToNSeparator() + maxOccurs + mToNRightBracket();
	}

	public String mToNLeftBracket() {
        return properties.getProperty("mToNLeftBracket");
    }

    public String mToNRightBracket() {
        return properties.getProperty("mToNRightBracket");
    }

    public String mToNNumberPattern() {
        return properties.getProperty("mToNNumberPattern");
    }

    public Matcher mToNMatcher(String pattern) {
    	return mToMPattern.matcher(pattern);
    }
    public String mToNSeparator() {
        return properties.getProperty("mToNSeparator");
    }

    public String mToNMin() {
        return properties.getProperty("mToNMin");
    }

    public String mToNMax() {
        return properties.getProperty("mToNMax");
    }

    public String emptySymbol() {
		return properties.getProperty("empty");
	}

	public String epsilonSymbol() {
	    return properties.getProperty("epsilon");
	}
   
    public String leftBracket() {
        return properties.getProperty("leftBracket");
    }

    public String rightBracket() {
        return properties.getProperty("rightBracket");
    }

	public boolean isOperatorSymbol(String symbol) {
		if (operators == null) {
			operators = new HashSet<String>();
			for (int i = 0; i < operatorNames.length; i++) {
				operators.add(properties.getProperty(operatorNames[i]));
			}
		}
		return operators.contains(symbol)
		|| symbol.matches(properties.getProperty("mToN"));
	}

	public boolean isNaryOperatorSymbol(String symbol) {
		return symbol.equals(unionOperator()) ||
		    symbol.equals(concatOperator()) ||
		    symbol.equals(interleaveOperator());
	}

	public boolean isEmptySymbol(String symbol) {
		return properties.getProperty("empty").equals(symbol);
	}

	public boolean isEpsilonSymbol(String symbol) {
	    return properties.getProperty("epsilon").equals(symbol);
	}

    /**
     * method to check whether a given string is a symbol of the regular
     * expression syntax
     * @param symbol
     * @return boolean true if the String is a symbol, false otherwise
     */
	public boolean isSymbol(String symbol) {
	    try {
	        identifySymbol(symbol);
            return true;
        } catch (UnknownOperatorException e) {
            return false;
        }
    }

    public boolean isSymbolName(String name) {
        return properties.containsKey(name);
    }

    /**
     * method that maps a symbol to its name;  a symbol can be an operator
     * or any other syntactic element of a regular expression
     * @param symbol
     * @return String name of the given symbol
     * @throws UnknownOperatorException thrown if the symbol is unknown
     */
    @SuppressWarnings("unchecked")
    public String identifySymbol(String symbol) throws UnknownOperatorException {
        for (Enumeration e = properties.propertyNames(); e.hasMoreElements(); ) {
            String name = (String) e.nextElement();
            String value = properties.getProperty(name);
            if (value.equals(symbol))
                return name;
        }
        if (symbol.matches(properties.getProperty("mToN"))) {
            return "mToN";
        }
        throw new UnknownOperatorException(symbol);
    }

    public Counting parseMtoN(String symbol) throws UnknownOperatorException {
    	Pattern pattern = Pattern.compile(mToNOperator());
    	Matcher matcher = pattern.matcher(symbol);
    	if (matcher.matches()) {
    		String mStr = matcher.group(1);
    		String nStr = matcher.group(2);
    		int m = mStr.length() > 0 ? Integer.valueOf(mStr) : 0;
    		int n = nStr.length() > 0 ? Integer.valueOf(nStr) : Integer.MAX_VALUE;
    		return new Counting(m, n);
    	} else {
    		throw new UnknownOperatorException(symbol);
    	}
    }

    /**
     * method that returns the symbol for a given syntactic element name
     * @param name of the syntactic element
     * @return String symbol
     * @throws UnknownOperatorException thrown if the name is unknown
     */
    public String getSymbol(String name) throws UnknownOperatorException {
        if (properties.containsKey(name)) {
            return properties.getProperty(name);
        } else
            throw new UnknownOperatorException(name);
    }

	/**
	 * method to parse the string representation of a regular expression
	 * that returns the parse tree; the regular expression must be given
	 * as an s-expression
	 * 
	 * @param regex regular expression to parse in prefix notation
	 * @return a <code>Tree</code> parse tree object
	 * @throws SExpressionParseException thrown if the expression is not
	 * a valid s-expression
	 */
	public Tree getTree(String regex) throws SExpressionParseException {
		try {
			return new Tree(new StringReader(regex));
		} catch (java.io.IOException e) {
			throw new SExpressionParseException(e);
		}
	}

    public boolean hasTree() {
        return tree != null;
    }

	/**
     * method the parse tree of the regular expression
     *
     * @return a <code>Tree</code> parse tree object
     */
    public Tree getTree() {
        return tree;
    }

    public Set<String> getAlphabet() {
        return java.util.Collections.unmodifiableSet(getAlphabetCount().keySet());
    }

    public Map<String,Integer> getAlphabetCount() {
        Map<String,Integer> alphabet = new HashMap<String,Integer>();
        if (tree != null) {
            for (Iterator<Node> it = tree.leaves(); it.hasNext(); ) {
                String symbol = it.next().getKey();
                if (!isEmptySymbol(symbol) && !isEpsilonSymbol(symbol)) {
                    if (!alphabet.containsKey(symbol))
                        alphabet.put(symbol, 0);
                    alphabet.put(symbol, alphabet.get(symbol) + 1);
                }
            }
        }
        return alphabet;
    }

    public int getMaximumOccurrencesOfSymbol() {
        Map<String,Integer> alphabetCount = getAlphabetCount();
        int max = 0;
        for (Integer count : alphabetCount.values())
            if (count > max)
                max = count;
        return max;
    }

    public int getTotalOccurrencesOfSymbol() {
        Map<String,Integer> alphabetCount = getAlphabetCount();
        int sum = 0;
        for (Integer count : alphabetCount.values())
            sum += count;
        return sum;
    }
    

    public String toString() {
        if (tree != null) {
            return tree.toSExpression();
        } else {
            return "Regex object without associated expression";
        }
    }

    public class Counting extends Pair<Integer,Integer> {

		protected Counting(Integer firstMember, Integer secondMember) {
			super(firstMember, secondMember);
		}

		public int getM() {
			return getFirst();
		}
		
		public int getN() {
			return getSecond();
		}
    	
    }

}
