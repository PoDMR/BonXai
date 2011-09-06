/**
 * Created on Sep 4, 2009
 * Modified on $Date: 2009-10-26 18:37:40 $
 */
package eu.fox7.util.tree.iterators;

import eu.fox7.util.tree.Node;

/**
 * @author lucg5005
 * @version $Revision: 1.1 $
 *
 */
public abstract class AbstractIterator implements NodeIterator {

	protected Node startNode;

	AbstractIterator(Node node) {
		this.startNode = node;
	}

	public Node getStartNode() {
		return startNode;
	}

	@Override
	public String toString() {
		return "start node: " + getStartNode().getKey();
	}

}
