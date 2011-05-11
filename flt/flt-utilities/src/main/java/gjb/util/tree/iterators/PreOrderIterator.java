/**
 * Created on Mar 23, 2009
 * Modified on $Date: 2009-11-12 22:13:31 $
 */
package gjb.util.tree.iterators;

import gjb.util.tree.Node;
import gjb.util.tree.Tree;

import java.util.Stack;

/**
 * @author lucg5005
 * @version $Revision: 1.2 $
 *
 */
public class PreOrderIterator extends AbstractIterator {

	protected Stack<Node> nodePosition;

	public PreOrderIterator(Tree tree) {
		this(tree.getRoot());
	}

	public PreOrderIterator(Node node) {
		super(node);
		nodePosition = new Stack<Node>();
		if (node != null) {
			nodePosition.push(node);
		}
	}

	/**
	 * <code>hasNext</code> method returns <code>true</code> if
	 * there are objects left to iterate over, false otherwise.
	 *
	 * @return a <code>boolean</code> value
	 */
	public boolean hasNext() {
		return !nodePosition.isEmpty();
	}

	/**
	 * <code>next</code> method returns the next
	 * <code>Object</code> if any.
	 *
	 * @return an <code>Object</code> value
	 */
	public Node next() {
		if (!hasNext()) {
			throw new java.util.NoSuchElementException();
		}
		Node node = nodePosition.pop();
		Node sibling = node.getNextSibling();
		if (sibling != null) {
			nodePosition.push(sibling);
		}
		if (node.hasChildren()) {
			nodePosition.push(node.child(0));
		}
		return node;
	}

	/**
	 * <code>remove</code> method is optional and has not been
	 * implemented for this <code>Iterator</code>.
	 *
	 */
	public void remove() {}

}
