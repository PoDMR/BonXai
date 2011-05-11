/**
 * Created on Mar 23, 2009
 * Modified on $Date: 2009-11-12 22:18:04 $
 */
package gjb.flt.regex.converters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import gjb.flt.regex.Regex;
import gjb.flt.regex.UnknownOperatorException;
import gjb.flt.regex.Regex.Counting;
import gjb.util.tree.Node;
import gjb.util.tree.NodeTransformException;
import gjb.util.tree.NodeVisitor;
import gjb.util.tree.SExpressionParseException;
import gjb.util.tree.Tree;
import gjb.util.tree.TreeVisitor;
import gjb.util.tree.iterators.PostOrderIterator;

/**
 * @author lucg5005
 * @version $Revision: 1.2 $
 *
 */
public class LexicographicNormalizer implements Converter, Normalizer {

	protected Regex regex;

	public LexicographicNormalizer() {
		super();
	}

	public LexicographicNormalizer(Regex regex) {
		this.regex = regex;
	}

	public String convert(Regex regex) {
		return normalize(regex).toString();
	}

	public String convert(String regexStr)
	        throws SExpressionParseException, UnknownOperatorException {
		return normalize(regexStr);
	}

	public Regex normalize(Regex regex) {
		Tree tree = regex.getTree();
		TreeVisitor treeVisitor = new TreeVisitor(new PostOrderIterator(tree));
		try {
			treeVisitor.visit(new LexicographicVisitor(regex), null);
		} catch (NodeTransformException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		return regex;		
	}

	public String normalize(String regexStr)
	        throws SExpressionParseException, UnknownOperatorException {
		if (this.regex == null)
			this.regex = new Regex();
		Tree tree = regex.getTree(regexStr);
		TreeVisitor treeVisitor = new TreeVisitor(new PostOrderIterator(tree));
		try {
			treeVisitor.visit(new LexicographicVisitor(regex), null);
		} catch (NodeTransformException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		Regex newRegex = new Regex(tree, regex.getProperties());
		return newRegex.toString();		
	}
	
	public class LexicographicVisitor implements NodeVisitor {

		protected NodeComparator cmp;
		protected Regex regex;

		public LexicographicVisitor(Regex regex) {
			this.regex = regex;
			this.cmp = new NodeComparator(regex);
		}

		public void visit(Node node, Map<String, Object> parameters)
				throws NodeTransformException {
			if (regex.unionOperator().equals(node.getKey())) {
				List<Node> children = new ArrayList<Node>();
				for (int i = 0; i < node.getNumberOfChildren(); i++)
					children.add(node.getChild(i));
				Collections.sort(children, cmp);
				for (int i = node.getNumberOfChildren() - 1; i >= 0; i--)
					node.removeChild(i);
				for (Node child : children)
					node.addChild(child);
			}
		}
		
	}

	public class NodeComparator implements Comparator<Node> {

		protected Regex regex;

		public NodeComparator(Regex regex) {
			this.regex = regex;
		}

		public int compare(Node node1, Node node2) {
			if (node1.isLeaf() && node2.isLeaf()) {
				int rank1 = computeSymbolRank(node1.getKey());
				int rank2 = computeSymbolRank(node2.getKey());
				if (rank1 > 1 && rank2 > 1)
					return node1.getKey().compareTo(node2.getKey());
				else
					return Integer.valueOf(rank1).compareTo(Integer.valueOf(rank2));
			} else if (node1.isLeaf())
				return 1;
			else if (node2.isLeaf())
				return -1;
			else {
				if (node1.getKey().equals(node2.getKey())) {
					for (int i = 0; i < Math.min(node1.getNumberOfChildren(), node2.getNumberOfChildren()); i++) {
						int cmp = compare(node1.getChild(i), node2.getChild(i));
						if (cmp != 0)
							return cmp;
					}
					return Integer.valueOf(node1.getNumberOfChildren()).compareTo(Integer.valueOf(node2.getNumberOfChildren()));
				} else if (node1.getKey().startsWith(regex.mToNLeftBracket()) &&
						   node2.getKey().startsWith(regex.mToNLeftBracket())){
					try {
						Counting c1 = regex.parseMtoN(node1.getKey());
						Counting c2 = regex.parseMtoN(node2.getKey());
						int m1 = c1.getM();
						int n1 = c1.getN();
						int d1 = n1 - m1;
						int m2 = c2.getM();
						int n2 = c2.getN();
						int d2 = n2 - m2;
						if (d1 < d2)
							return 1;
						else if (d2 < d1)
							return -1;
						else if (m1 < m2)
							return -1;
						else if (m2 < m1)
							return 1;
						else
							return compare(node1.getChild(0), node2.getChild(0));
					} catch (UnknownOperatorException e) {
						e.printStackTrace();
						throw new RuntimeException(e);
					}
				} else {
					try {
						int rank1 = computeRank(node1.getKey());
						int rank2 = computeRank(node2.getKey());
						return Integer.valueOf(rank1).compareTo(Integer.valueOf(rank2));
					} catch (UnknownOperatorException e) {
						e.printStackTrace();
						throw new RuntimeException(e);
					}
				}
			}
		}
		
		protected int computeRank(String operator)
		        throws UnknownOperatorException {
			if (regex.zeroOrMoreOperator().equals(operator))
				return 0;
			else if (regex.oneOrMoreOperator().equals(operator))
				return 1;
			else if (regex.zeroOrOneOperator().equals(operator))
				return 3;
			else if (regex.interleaveOperator().equals(operator))
				return 4;
			else if (regex.unionOperator().equals(operator))
				return 5;
			else if (regex.concatOperator().equals(operator))
				return 6;
			else if (operator.matches(regex.mToNOperator()))
				return 2;
			else
				throw new UnknownOperatorException(operator);
		}

		protected int computeSymbolRank(String symbol) {
			if (regex.emptySymbol().equals(symbol))
				return 0;
			else if (regex.epsilonSymbol().equals(symbol))
				return 1;
			else
				return 2;
		}

	}

}
