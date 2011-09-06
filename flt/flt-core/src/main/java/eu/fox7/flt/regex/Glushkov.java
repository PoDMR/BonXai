package eu.fox7.flt.regex;

import eu.fox7.flt.automata.FeatureNotSupportedException;
import eu.fox7.util.tree.Node;
import eu.fox7.util.tree.SExpressionParseException;
import eu.fox7.util.tree.Tree;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class Glushkov {

    public static final String MARK_SEP = "_";

    /**
     * String representing the label of the initial state in any Glushkov
     * automaton
     */
    public static final String INITIAL_STATE = "q_I";

    /**
     * Regex <code>regex</code> is the object used to convert a regular
     * expression into a AST.
     *  
     */
    protected Regex regex;

    /**
     * Creates a new <code>Glushkov</code> instance using the default symbols
     * for regex interpretation.
     *  
     */
    public Glushkov() {
        regex = new Regex();
    }

    /**
     * Creates a new <code>Glushkov</code> instance with non-default regex
     * symbols.
     * 
     * @param properties
     *            a <code>Properties</code> value that store the non-default
     *            regex operator symbols.
     */
    public Glushkov(Properties properties) {
        if (properties != null)
            regex = new Regex(properties);
        else
            regex = new Regex();
    }

    /**
     * method that returning the Regex object used to construct the Gluskov automaton
     * 
     * @return Regex the regular expression factory object used in the Glushkov
     *         automaton construction
     */
    public Regex regex() {
        return regex;
    }

    public boolean isAmbiguous(String regex) throws SExpressionParseException,
            UnknownOperatorException, FeatureNotSupportedException {
        return isAmbiguous(this.regex.getTree(regex));
    }

    public boolean isAmbiguous(Tree tree) throws UnknownOperatorException,
            FeatureNotSupportedException {
        Set<String> sigma = symbols(tree);
        Tree markedTree = mark(tree);
        Set<String> pi = symbols(markedTree);
        Set<String> firstSet = first(markedTree);
        Map<String,Set<String>> followMap = new HashMap<String,Set<String>>();
        for (String symbol: pi)
            followMap.put(symbol, follow(markedTree, symbol));
        for (String symbol : sigma) {
            Set<String> match = matchMark(firstSet, symbol);
            if (match.size() > 1)
                return true;
            for (String fromState : pi) {
                match = matchMark(followMap.get(fromState), symbol);
                if (match.size() > 1)
                    return true;
            }
        }
        return false;
    }

    public Set<String> matchMark(Set<String> set, String symbol) {
        Set<String> match = new HashSet<String>();
        for (String markedSymbol : set)
            if (unmark(markedSymbol).equals(symbol))
                match.add(markedSymbol);
        return match;
    }

    public Tree mark(Tree inTree) {
        Tree outTree = new Tree(inTree);
        int i = 0;
        for (Iterator<Node> it = outTree.leaves(); it.hasNext();) {
            Node node = it.next();
            if (!regex.isOperatorSymbol(node.key()) &&
                    !regex.isEmptySymbol(node.key()) &&
                    !regex.isEpsilonSymbol(node.key())) {
                node.setKey(mark(node.key(), ++i));
            }
        }
        return outTree;
    }

    public Tree unmark(Tree inTree) {
        Tree outTree = new Tree(inTree);
        for (Iterator<Node> it = outTree.leaves(); it.hasNext();) {
            Node node = it.next();
            if (!regex.isOperatorSymbol(node.key()) &&
                    !regex.isEmptySymbol(node.key()) &&
                    !regex.isEpsilonSymbol(node.key())) {
                node.setKey(unmark(node.key()));
            }
        }
        return outTree;
    }
    
    public boolean isOptional(Tree tree)
            throws FeatureNotSupportedException, UnknownOperatorException {
        return tree.isEmpty() ? false : isOptional(tree.getRoot());
    }

    public boolean isOptional(Node node)
            throws FeatureNotSupportedException, UnknownOperatorException {
        if (node.hasChildren()) {
            if (node.key().equals(regex.zeroOrOneOperator())
                    || node.key().equals(regex.zeroOrMoreOperator())) {
                return true;
            } else if (node.key().equals(regex.oneOrMoreOperator())) {
                return isOptional(node.child(0));
            } else if (node.key().equals(regex.unionOperator())) {
                for (int i = 0; i < node.getNumberOfChildren(); i++) {
                    if (isOptional(node.child(i)))
                        return true;
                }
                return false;
            } else if (node.key().equals(regex.concatOperator())) {
                for (int i = 0; i < node.getNumberOfChildren(); i++) {
                    if (!isOptional(node.child(i)))
                        return false;
                }
                return true;
            } else if (node.key().equals(regex.intersectionOperator())) {
                for (int i = 0; i < node.getNumberOfChildren(); i++) {
                    if (!isOptional(node.child(i)))
                        return false;
                }
                return true;
            } else if (node.key().equals(regex.interleaveOperator())) {
                throw new FeatureNotSupportedException(
                        "isOptional doesn't support interleave");
            } else {
                throw new UnknownOperatorException(node.key());
            }
        } else if (node.key().equals(regex.epsilonSymbol())) {
            return true;
        } else {
            return false;
        }
    }

    public Set<String> first(Tree tree) throws FeatureNotSupportedException,
            UnknownOperatorException {
        return tree.getRoot() == null ? new HashSet<String>() : first(tree.getRoot());
    }

    public Set<String> first(Node node) throws FeatureNotSupportedException,
            UnknownOperatorException {
        return first(node, 0);
    }

    public Set<Node> firstNodes(Node node) throws FeatureNotSupportedException,
    UnknownOperatorException {
        return firstNodes(node, 0);
    }
    
    protected Set<Node> firstNodes(Node node, int offset)
            throws FeatureNotSupportedException, UnknownOperatorException {
        Set<Node> set = new HashSet<Node>();
        if (node.hasChildren()) {
            if (node.key().equals(regex.concatOperator())) {
                set.addAll(firstNodes(node.child(offset)));
                for (int i = offset; i < node.getNumberOfChildren() - 1; i++) {
                    if (isOptional(node.child(i))) {
                        set.addAll(firstNodes(node.child(i + 1)));
                    } else {
                        break;
                    }
                }
            } else if (node.key().equals(regex.unionOperator())) {
                for (int i = 0; i < node.getNumberOfChildren(); i++) {
                    set.addAll(firstNodes(node.child(i)));
                }
            } else if (node.key().equals(regex.intersectionOperator())) {
                set.addAll(firstNodes(node.child(0)));
                for (int i = 1; i < node.getNumberOfChildren(); i++) {
                    set.retainAll(first(node.child(i)));
                }

            } else if (node.key().equals(regex.zeroOrOneOperator())
                    || node.key().equals(regex.zeroOrMoreOperator())
                    || node.key().equals(regex.oneOrMoreOperator())) {
                set.addAll(firstNodes(node.child(0)));
            } else {
                throw new UnknownOperatorException(node.key());
            }
        } else if (node.key().equals(regex.emptySymbol()) ||
                node.key().equals(regex.epsilonSymbol())) {

        } else {
            set.add(node);
        }
        return set;
        
    }

    protected Set<String> first(Node node, int offset)
            throws FeatureNotSupportedException, UnknownOperatorException {
        Set<String> set = new HashSet<String>();
        if (node.hasChildren()) {
            if (node.key().equals(regex.concatOperator())) {
                set.addAll(first(node.child(offset)));
                for (int i = offset; i < node.getNumberOfChildren() - 1; i++) {
                    if (isOptional(node.child(i))) {
                        set.addAll(first(node.child(i + 1)));
                    } else {
                        break;
                    }
                }
            } else if (node.key().equals(regex.unionOperator())) {
                for (int i = 0; i < node.getNumberOfChildren(); i++) {
                    set.addAll(first(node.child(i)));
                }
            } else if (node.key().equals(regex.intersectionOperator())) {
                set.addAll(first(node.child(0)));
                for (int i = 1; i < node.getNumberOfChildren(); i++) {
                    set.retainAll(first(node.child(i)));
                }

            } else if (node.key().equals(regex.zeroOrOneOperator())
                    || node.key().equals(regex.zeroOrMoreOperator())
                    || node.key().equals(regex.oneOrMoreOperator())) {
                set.addAll(first(node.child(0)));
            } else {
                throw new UnknownOperatorException(node.key());
            }
        } else if (node.key().equals(regex.emptySymbol()) ||
                node.key().equals(regex.epsilonSymbol())) {

        } else {
            set.add(node.key());
        }
        return set;
    }

    public Set<String> last(Tree tree) throws FeatureNotSupportedException,
            UnknownOperatorException {
        return tree.getRoot() == null ? new HashSet<String>() : last(tree.getRoot());
    }

    protected Set<String> last(Node node) throws FeatureNotSupportedException,
            UnknownOperatorException {
        Set<String> set = new HashSet<String>();
        if (node.hasChildren()) {
            if (node.key().equals(regex.concatOperator())) {
                set.addAll(last(node.child(node.getNumberOfChildren() - 1)));
                for (int i = node.getNumberOfChildren() - 1; i > 0; i--) {
                    if (isOptional(node.child(i))) {
                        set.addAll(last(node.child(i - 1)));
                    } else {
                        break;
                    }
                }
            } else if (node.key().equals(regex.unionOperator())) {
                for (int i = 0; i < node.getNumberOfChildren(); i++) {
                    set.addAll(last(node.child(i)));
                }
            } else if (node.key().equals(regex.intersectionOperator())) {
                set.addAll(last(node.child(0)));
                for (int i = 1; i < node.getNumberOfChildren(); i++) {
                    set.retainAll(last(node.child(i)));
                }
            } else if (node.key().equals(regex.zeroOrOneOperator())
                    || node.key().equals(regex.zeroOrMoreOperator())
                    || node.key().equals(regex.oneOrMoreOperator())) {
                set.addAll(last(node.child(0)));
            } else {
                throw new UnknownOperatorException(node.key());
            }
        } else if (node.key().equals(regex.emptySymbol()) ||
                node.key().equals(regex.epsilonSymbol())) {

        } else {
            set.add(node.key());
        }
        return set;
    }

    public Set<Node> lastNodes(Node node)
            throws FeatureNotSupportedException, UnknownOperatorException {
        Set<Node> set = new HashSet<Node>();
        if (node.hasChildren()) {
            if (node.key().equals(regex.concatOperator())) {
                set.addAll(lastNodes(node.child(node.getNumberOfChildren() - 1)));
                for (int i = node.getNumberOfChildren() - 1; i > 0; i--) {
                    if (isOptional(node.child(i))) {
                        set.addAll(lastNodes(node.child(i - 1)));
                    } else {
                        break;
                    }
                }
            } else if (node.key().equals(regex.unionOperator())) {
                for (int i = 0; i < node.getNumberOfChildren(); i++) {
                    set.addAll(lastNodes(node.child(i)));
                }
            } else if (node.key().equals(regex.intersectionOperator())) {
                set.addAll(lastNodes(node.child(0)));
                for (int i = 1; i < node.getNumberOfChildren(); i++) {
                    set.retainAll(last(node.child(i)));
                }
            } else if (node.key().equals(regex.zeroOrOneOperator())
                    || node.key().equals(regex.zeroOrMoreOperator())
                    || node.key().equals(regex.oneOrMoreOperator())) {
                set.addAll(lastNodes(node.child(0)));
            } else {
                throw new UnknownOperatorException(node.key());
            }
        } else if (node.key().equals(regex.emptySymbol()) ||
                node.key().equals(regex.epsilonSymbol())) {
            
        } else {
            set.add(node);
        }
        return set;
    }
    
    public Set<String> follow(Tree tree, String symbol)
            throws FeatureNotSupportedException, UnknownOperatorException {
        Node node = tree.findFirstNodeWithKey(symbol);
        if (node != null) {
            return follow(node);
        }
        return null;
    }

    protected Set<String> follow(Node node) throws FeatureNotSupportedException,
            UnknownOperatorException {
        if (node.hasParent()) {
            Node parent = node.getParent();
            if (parent.key().equals(regex.zeroOrOneOperator())) {
                return follow(parent);
            } else if (parent.key().equals(regex.zeroOrMoreOperator())
                    || parent.key().equals(regex.oneOrMoreOperator())) {
                Set<String> set = first(node);
                set.addAll(follow(parent));
                return set;
            } else if (parent.key().equals(regex.unionOperator())) {
                return follow(parent);
            } else if (parent.key().equals(regex.concatOperator())) {
                Set<String> set = new HashSet<String>();
                if (node.hasNextSibling()) {
                    set.addAll(first(parent, node.getChildIndex() + 1));
                    boolean optionalSiblings = true;
                    Node sibling = node;
                    do {
                        sibling = sibling.getNextSibling();
                        if (!isOptional(sibling)) {
                            optionalSiblings = false;
                            break;
                        }
                    } while (sibling.hasNextSibling());
                    if (optionalSiblings) {
                        set.addAll(follow(parent));
                    }
                } else {
                    set.addAll(follow(parent));
                }
                return set;
            } else if (parent.key().equals(regex.intersectionOperator())) {
                throw new FeatureNotSupportedException(
                        "follow not supported for intersection");
            } else if (parent.key().equals(regex.interleaveOperator())) {
                throw new FeatureNotSupportedException(
                        "follow not supported for interleave");
            } else {
                throw new UnknownOperatorException(node.key());
            }
        } else {
            return new HashSet<String>();
        }
    }

    public Set<Node> followNodes(Node node) throws FeatureNotSupportedException,
            UnknownOperatorException {
        if (node.hasParent()) {
            Node parent = node.getParent();
            if (parent.key().equals(regex.zeroOrOneOperator())) {
                return followNodes(parent);
            } else if (parent.key().equals(regex.zeroOrMoreOperator())
                    || parent.key().equals(regex.oneOrMoreOperator())) {
                Set<Node> set = firstNodes(node);
                set.addAll(followNodes(parent));
                return set;
            } else if (parent.key().equals(regex.unionOperator())) {
                return followNodes(parent);
            } else if (parent.key().equals(regex.concatOperator())) {
                Set<Node> set = new HashSet<Node>();
                if (node.hasNextSibling()) {
                    set.addAll(firstNodes(parent, node.getChildIndex() + 1));
                    boolean optionalSiblings = true;
                    Node sibling = node;
                    do {
                        sibling = sibling.getNextSibling();
                        if (!isOptional(sibling)) {
                            optionalSiblings = false;
                            break;
                        }
                    } while (sibling.hasNextSibling());
                    if (optionalSiblings)
                        set.addAll(followNodes(parent));
                } else {
                    set.addAll(followNodes(parent));
                }
                return set;
            } else if (parent.key().equals(regex.intersectionOperator())) {
                throw new FeatureNotSupportedException("follow not supported for intersection");
            } else if (parent.key().equals(regex.interleaveOperator())) {
                throw new FeatureNotSupportedException("follow not supported for interleave");
            } else {
                throw new UnknownOperatorException(node.key());
            }
        } else {
            return new HashSet<Node>();
        }
    }
    
    public Set<String> symbols(Tree tree) {
        Set<String> symbols = new HashSet<String>();
        for (Iterator<Node> it = tree.leaves(); it.hasNext();) {
            String symbol = it.next().key();
			if (!regex.isEmptySymbol(symbol) && !regex.isEpsilonSymbol(symbol))
                symbols.add(symbol);
        }
        return symbols;
    }

    public static String mark(String str, int i) {
        return str + MARK_SEP + i;
    }

    public static String unmark(String str) {
        if (str.equals(Glushkov.INITIAL_STATE))
            return str;
        else {
            int pos = str.lastIndexOf(MARK_SEP);
            if (pos >= 0)
                return str.substring(0, pos);
            else
                return str;
        }
    }

}