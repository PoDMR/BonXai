/**
 * Created on Apr 2, 2009
 * Modified on $Date: 2009-11-12 22:18:25 $
 */
package eu.fox7.flt.regex.factories;

import java.util.LinkedList;
import java.util.List;

import eu.fox7.flt.regex.Regex;
import eu.fox7.flt.regex.UnknownOperatorException;
import eu.fox7.flt.regex.Regex.Counting;
import eu.fox7.flt.regex.converters.EpsilonEmptyEliminator;
import eu.fox7.flt.regex.measures.ContainsEpsilonTest;
import eu.fox7.util.tree.Node;
import eu.fox7.util.tree.Tree;


/**
 * @author lucg5005
 * @version $Revision: 1.2 $
 *
 */
public class Deriver {

	protected EpsilonEmptyEliminator eeNormalizer;
	protected ContainsEpsilonTest epsilonTester;
	protected Regex regex;

	public Deriver() {
		super();
		eeNormalizer = new EpsilonEmptyEliminator();
	}

	public Deriver(Regex regex) {
		this();
		this.regex = regex;
		this.epsilonTester = new ContainsEpsilonTest(regex);
		this.eeNormalizer = new EpsilonEmptyEliminator(regex);
	}

	public Regex derive(Regex regex, String symbol)
	        throws UnknownOperatorException {
		this.epsilonTester = new ContainsEpsilonTest(regex);
		Tree tree = regex.getTree();
		Tree newTree = new Tree();
		Node root = new Node(regex.concatOperator());
		root.addChild(new Node(symbol));
		root.addChild(derive(regex, symbol, tree.getRoot()));
		newTree.setRoot(root);
		Regex newRegex = new Regex(newTree, regex.getProperties());
		return eeNormalizer.normalize(newRegex);
	}

	public Node derive(String symbol, Node node) throws UnknownOperatorException {
		Node root = new Node(regex.concatOperator());
		root.addChild(new Node(symbol));
		root.addChild(derive(regex, symbol, node));
		return eeNormalizer.normalize(root);
	}

	protected Node derive(Regex regex, String symbol, Node node)
	        throws UnknownOperatorException {
		if (node.key().equals(regex.zeroOrOneOperator())) {
			return derive(regex, symbol, node.getChild(0));
		} else if (node.key().equals(regex.zeroOrMoreOperator())) {
			Node newNode = new Node(regex.concatOperator());
			newNode.addChild(derive(regex, symbol, node.getChild(0)));
			newNode.addChild(node.deepClone());
			return newNode;
		} else if (node.key().equals(regex.oneOrMoreOperator())) {
			Node newNode = new Node(regex.concatOperator());
			newNode.addChild(derive(regex, symbol, node.getChild(0)));
			Node childNode = new Node(regex.zeroOrMoreOperator());
			childNode.addChild(node.getChild(0).deepClone());
			newNode.addChild(childNode);
			return newNode;
		} else if (node.key().equals(regex.unionOperator())) {
			Node newNode = new Node(regex.unionOperator());
			for (int i = 0; i < node.getNumberOfChildren(); i++)
				newNode.addChild(derive(regex, symbol, node.getChild(i)));
			return newNode;
		} else if (node.key().equals(regex.concatOperator())) {
			Node newNode = new Node(regex.concatOperator());
			newNode.addChild(derive(regex, symbol, node.getChild(0)));
			for (int i = 1; i < node.getNumberOfChildren(); i++)
				newNode.addChild(node.getChild(i).deepClone());
			List<Node> additionals = new LinkedList<Node>();
			int i = 1;
			while (epsilonTester.test(node.getChild(i - 1)) && i < node.getNumberOfChildren()) {
				Node addNode = new Node(regex.concatOperator());
				addNode.addChild(derive(regex, symbol, node.getChild(i)));
				for (int j = i + 1; j < node.getNumberOfChildren(); j++)
					addNode.addChild(node.getChild(j).deepClone());
				additionals.add(addNode);
				i++;
			}
			if (additionals.isEmpty()) {
				return newNode;
			} else {
				additionals.add(0, newNode);
				newNode = new Node(regex.unionOperator());
				for (Node childNode : additionals)
					newNode.addChild(childNode);
				return newNode;
			}
		} else if (node.key().equals(regex.interleaveOperator())) {
			Node newNode = new Node(regex.unionOperator());
			for (int i = 0; i < node.getNumberOfChildren(); i++) {
				Node childNode = new Node(regex.interleaveOperator());
				for (int j = 0; j < node.getNumberOfChildren(); j++)
					if (i == j)
						childNode.addChild(derive(regex, symbol, node.getChild(j)));
					else
						childNode.addChild(node.getChild(j).deepClone());
				newNode.addChild(childNode);
			}
			return newNode;
		} else if (node.key().matches(regex.mToNNumberPattern())) {
			Node newNode = new Node(regex.concatOperator());
			newNode.addChild(derive(regex, symbol, node.getChild(0)));
			Counting counting = regex.parseMtoN(node.getKey());
			Node childNode = new Node(regex.mToNOperator(counting.getM() - 1,
					                                     counting.getN() - 1));
			childNode.addChild(node.getChild(0).deepClone());
			newNode.addChild(childNode);
			return newNode;
		} else if (regex.isEmptySymbol(node.getKey())) {
			return new Node(regex.emptySymbol());
		} else if (regex.isEpsilonSymbol(node.getKey())) {
			return new Node(regex.emptySymbol());
		} else if (node.isLeaf()){
			if (symbol.equals(node.getKey()))
				return new Node(regex.epsilonSymbol());
			else
				return new Node(regex.emptySymbol());
		} else {
			throw new UnknownOperatorException(node.getKey());
		}
	}

}
