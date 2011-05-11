/**
 * Created on Sep 3, 2009
 * Modified on $Date: 2009-11-12 22:13:31 $
 */
package gjb.util.tree.selectors;

import gjb.util.tree.Node;
import gjb.util.tree.Tree;

/**
 * @author lucg5005
 * @version $Revision: 1.2 $
 *
 */
public class PositionalNodeSelector {

	public Node select(Node startNode, int... position)
	        throws IndexOutOfBoundsException {
		Node node = startNode;
		for (int i = 0; i < position.length; i++)
			if (position[i] == -1)
				if (node.hasParent())
					node = node.getParent();
				else
					throw new IndexOutOfBoundsException();
			else
				node = node.getChild(position[i]);
		return node;
	}

	public Node select(Tree tree, int... position)
	        throws IndexOutOfBoundsException {
		if (tree.getRoot() == null)
			throw new IndexOutOfBoundsException();
		else
			return select(tree.getRoot(), position);
	}

}
