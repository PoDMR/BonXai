/**
 * Created on Apr 21, 2009
 * Modified on $Date: 2009-11-12 22:13:31 $
 */
package gjb.util.tree.converters;

import gjb.util.tree.Node;
import gjb.util.tree.NodeTransformException;
import gjb.util.tree.NodeVisitor;
import gjb.util.tree.Tree;
import gjb.util.tree.TreeVisitor;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

/**
 * @author lucg5005
 * @version $Revision: 1.2 $
 *
 */
public class NodePropertyWriter {

	public static final String DEFAULT_NULL_STR = "null";
	public static final String DEFAULT_SEP = ";";
	protected String nullStr = DEFAULT_NULL_STR;
	protected String sepStr = DEFAULT_SEP;
	protected Writer writer;
	protected int nrAncesters, nrLeftSiblings;

	public NodePropertyWriter(Writer writer) {
		this(writer, 1, 1);
	}

	public NodePropertyWriter(Writer writer,
			                            int nrAncesters, int nrLeftSiblings) {
		super();
		this.writer = writer;
		this.nrAncesters = nrAncesters;
		this.nrLeftSiblings = nrLeftSiblings;
	}

	public int getNrAncesters() {
		return nrAncesters;
	}

	public int getNrLeftSiblings() {
		return nrLeftSiblings;
	}

	public String getNullStr() {
		return nullStr;
	}

	public void setNullStr(String nullStr) {
		this.nullStr = nullStr;
	}
	
	public String getSepStr() {
		return sepStr;
	}

	public void setSepStr(String sepStr) {
		this.sepStr = sepStr;
	}

	public void write(Tree tree) throws NodeTransformException {
		if (!tree.isEmpty()) {
			TreeVisitor treeVisitor = new TreeVisitor(tree);
			treeVisitor.visit(new ParentLeftSiblingDepthVisitor(), null);
		}
	}

	protected class ParentLeftSiblingDepthVisitor implements NodeVisitor {

		public void visit(Node node, Map<String, Object> paramters)
				throws NodeTransformException {
			try {
				writer.append(node.getKey()).append("\t");
				writer.append(computeAncestorString(node)).append("\t");
				writer.append(computeLeftSiblingString(node)).append("\t");
				writer.append(Integer.toString(node.getDepth())).append("\t");
				if (node.hasParent())
					writer.append(Integer.toString(node.getChildIndex()));
				else
					writer.append("0");
				writer.append("\t").append(Integer.toString(node.getNumberOfChildren()));
				writer.append("\n");
			} catch (IOException e) {
				throw new NodeTransformException("I/O problem", e);
			}
		}

		protected String computeAncestorString(Node node) {
			String[] ancStr = new String[getNrAncesters()];
			int i = getNrAncesters() - 1;
			while (node.hasParent() && i >= 0) {
				node = node.getParent();
				ancStr[i--] = node.getKey();
			}
			for (int j = i; j >= 0; j--)
				ancStr[i--] = getNullStr();
			return StringUtils.join(ancStr, getSepStr());
		}

		protected String computeLeftSiblingString(Node node) {
			String[] lsStr = new String[getNrLeftSiblings()];
			int i = getNrLeftSiblings() - 1;
			while (node.hasPreviousSibling() && i >= 0) {
				node = node.getPreviousSibling();
				lsStr[i--] = node.getKey();
			}
			for (int j = i; j >= 0; j--)
				lsStr[i--] = getNullStr();
			return StringUtils.join(lsStr, getSepStr());
		}

	}

}
