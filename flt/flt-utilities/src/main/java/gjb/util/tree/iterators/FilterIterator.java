/**
 * Created on Sep 4, 2009
 * Modified on $Date: 2009-10-26 18:37:40 $
 */
package gjb.util.tree.iterators;

import java.util.Iterator;

import gjb.util.tree.Node;
import gjb.util.tree.selectors.NodeSelector;

/**
 * @author lucg5005
 * @version $Revision: 1.1 $
 *
 */
public class FilterIterator extends AbstractIterator {

	protected Iterator<Node> iterator;
	protected NodeSelector selector;
	protected Node current;

	public FilterIterator(NodeIterator it, NodeSelector selector) {
		super(it.getStartNode());
		this.iterator = it;
		this.selector = selector;
		findNext();
	}

	protected void findNext() {
		this.current = null;
		while (iterator.hasNext()) {
			Node node = iterator.next();
			if (selector.isMatch(node)) {
				current = node;
				break;
			}
		}
	}

	public boolean hasNext() {
		return current != null;
	}

	public Node next() {
		Node result = current;
		findNext();
		return result;
	}

	public void remove() {}

	@Override
	public String toString() {
		return super.toString() + "\n" + "current: " + current.getKey();
	}

}
