/**
 * Created on Sep 25, 2009
 * Modified on $Date: 2009-10-26 18:37:40 $
 */
package gjb.util.tree.iterators;

import gjb.util.tree.Node;
import gjb.util.tree.selectors.Context;
import gjb.util.tree.selectors.NodeContextSelector;

import java.util.Iterator;

/**
 * @author lucg5005
 * @version $Revision: 1.1 $
 *
 */
public class ContextFilterIterator<T extends Context> implements Iterator<T> {

	protected Iterator<Node> iterator;
	protected NodeContextSelector<T> selector;
	protected T current;

	public ContextFilterIterator(NodeIterator it, NodeContextSelector<T> selector) {
		this.iterator = it;
		this.selector = selector;
		findNext();
	}

	@SuppressWarnings("unchecked")
    protected void findNext() {
		this.current = null;
		while (iterator.hasNext()) {
			Node node = iterator.next();
			if (selector.isMatch(node)) {
				current = (T) selector.getContext().clone();
				break;
			}
		}
	}

	public boolean hasNext() {
		return current != null;
	}

	public T next() {
		T result = current;
		findNext();
		return result;
	}

	public void remove() {}

	@Override
	public String toString() {
		return "current: " + current.toString();
	}

}
