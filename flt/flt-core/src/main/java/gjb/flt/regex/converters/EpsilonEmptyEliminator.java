/**
 * Created on Apr 3, 2009
 * Modified on $Date: 2009-11-12 22:18:04 $
 */
package gjb.flt.regex.converters;

import java.util.LinkedList;
import java.util.List;

import gjb.flt.regex.Regex;
import gjb.flt.regex.UnknownOperatorException;
import gjb.flt.regex.Regex.Counting;
import gjb.flt.regex.measures.ContainsEpsilonTest;
import gjb.util.tree.Node;
import gjb.util.tree.SExpressionParseException;
import gjb.util.tree.Tree;

/**
 * @author lucg5005
 * @version $Revision: 1.2 $
 *
 */
public class EpsilonEmptyEliminator implements Normalizer {

	protected Regex regex;
	protected ContainsEpsilonTest epsilonTester;

	public EpsilonEmptyEliminator() {
		super();
	}

	public EpsilonEmptyEliminator(Regex regex) {
		this();
		this.regex = regex;
	}

	public Regex normalize(Regex regex) {
		this.epsilonTester = new ContainsEpsilonTest(regex);
		Tree tree = regex.getTree();
		Tree newTree = new Tree();
		try {
			newTree.setRoot(modify(tree.getRoot(), regex));
			return new Regex(newTree, regex.getProperties());
		} catch (UnknownOperatorException e) {
			throw new RuntimeException(e);
		}
	}

	public String normalize(String regexStr)
	        throws SExpressionParseException {
		this.epsilonTester = new ContainsEpsilonTest(regex);
		Tree tree = regex.getTree(regexStr);
		Tree newTree = new Tree();
		try {
			newTree.setRoot(modify(tree.getRoot(), regex));
			Regex newRegex = new Regex(newTree, regex.getProperties());
			return newRegex.toString();
		} catch (UnknownOperatorException e) {
			throw new RuntimeException(e);
		}
	}

	public Node normalize(Node node) throws UnknownOperatorException {
		return modify(node, this.regex);
	}

	protected Node modify(Node node, Regex regex) throws UnknownOperatorException {
		List<Node> newChildren = new LinkedList<Node>();
		boolean hasEpsilon = false;
		boolean hasEmpty = false;
		for (int i = 0; i < node.getNumberOfChildren(); i++) {
			Node newChild = modify(node.getChild(i), regex);
			if (newChild.getKey().equals(regex.emptySymbol()))
				hasEmpty = true;
			else if (newChild.getKey().equals(regex.epsilonSymbol()))
				hasEpsilon = true;
			else
				newChildren.add(newChild);
		}
		if (node.isLeaf()) {
			return new Node(node.getKey());
		} else if (regex.isNaryOperatorSymbol(node.getKey())) {
			if (hasEmpty) {
				if (node.getKey().equals(regex.concatOperator()) ||
						node.getKey().equals(regex.interleaveOperator()))
					return new Node(regex.emptySymbol());
				/* union */
				if (newChildren.size() == 0)
					if (hasEpsilon)
						return new Node(regex.epsilonSymbol());
					else
						return new Node(regex.emptySymbol());
			}				
			if (newChildren.size() == 0) {
				return new Node(regex.epsilonSymbol());
			} else if (newChildren.size() == 1) {
				if (node.getKey().equals(regex.unionOperator()) &&
						hasEpsilon &&
						!epsilonTester.test(newChildren.get(0))) {
					Node newNode = new Node(regex.zeroOrOneOperator());
					newNode.addChild(newChildren.get(0));
					return newNode;
				} else {
					return newChildren.get(0);
				}
			} else {
				Node newNode = new Node(node.getKey());
				for (Node childNode : newChildren)
					newNode.addChild(childNode);
				if (node.getKey().equals(regex.unionOperator()) &&
						hasEpsilon  &&
						!containsEpsilon(newChildren)) {
					Node optionalNode = new Node(regex.zeroOrOneOperator());
					optionalNode.addChild(newNode);
					return optionalNode;
				} else {
					return newNode;
				}
			}				
		} else { /* unary operator */
			if (hasEmpty) {
				if (node.getKey().equals(regex.oneOrMoreOperator())) {
					return new Node(regex.emptySymbol());
				} else if (node.getKey().matches(regex.mToNOperator())) {
					Counting count = regex.parseMtoN(node.getKey());
					if (count.getM() == 0)
						return new Node(regex.epsilonSymbol());
					else
						return new Node(regex.emptySymbol());
				} else {
					return new Node(regex.epsilonSymbol());
				}
			} else if (hasEpsilon) {
				return new Node(regex.epsilonSymbol());
			} else {
				Node newNode = new Node(node.getKey());
				newNode.addChild(newChildren.get(0));
				return newNode;
			}
		}
	}

	protected boolean containsEpsilon(List<Node> nodes)
	        throws UnknownOperatorException {
		for (Node node : nodes)
			if (epsilonTester.test(node))
				return true;
		return false;
	}

}
