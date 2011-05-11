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
public class PostOrderIterator extends AbstractIterator {

	protected Stack<Node> nodePosition;

	public PostOrderIterator(Tree tree) {
		this(tree.getRoot());
	}

	public PostOrderIterator(Node node) {
		super(node);
		nodePosition = new Stack<Node>();
		if (node != null) {
			descend(node);
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
		Node nextSibling = node.getNextSibling();
		if (nextSibling != null) {
			descend(nextSibling);
		}
		return node;
	}

	/**
	 * <code>remove</code> method is optional and has not been
	 * implemented for this <code>Iterator</code>.
	 *
	 */
	public void remove() {}

	protected void descend(Node node) {
		nodePosition.push(node);
		while (node.hasChildren()) {
			node = node.child(0);
			nodePosition.push(node);
		}
	}

}
