/**
 * Created on Mar 23, 2009
 * Modified on $Date: 2009-11-12 22:13:31 $
 */
package eu.fox7.util.tree.iterators;

import eu.fox7.util.tree.Node;
import eu.fox7.util.tree.Tree;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * @author lucg5005
 * @version $Revision: 1.2 $
 *
 */
public class BreadthFirstIterator extends AbstractIterator {

	protected LinkedList<Node> nodeQueue;

	public BreadthFirstIterator(Tree tree) {
		this(tree.getRoot());
	}

	public BreadthFirstIterator(Node node) {
		super(node);
		nodeQueue = new LinkedList<Node>();
		if (node != null) {
			nodeQueue.add(node);
		}
	}

	/**
	 * <code>hasNext</code> method returns <code>true</code> if
	 * there are objects left to iterate over, false otherwise.
	 *
	 * @return a <code>boolean</code> value
	 */
	public boolean hasNext() {
		return nodeQueue.size() > 0;
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
		Node node = nodeQueue.removeFirst();
		for (Iterator<Node> it = node.children(); it.hasNext(); ) {
			nodeQueue.addLast(it.next());
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
