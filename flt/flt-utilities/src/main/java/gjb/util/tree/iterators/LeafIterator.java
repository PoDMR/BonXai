/**
 * Created on Mar 23, 2009
 * Modified on $Date: 2009-11-12 22:13:31 $
 */
package gjb.util.tree.iterators;

import gjb.util.tree.Node;
import gjb.util.tree.Tree;
import gjb.util.tree.selectors.NodeSelector;

/**
 * @author lucg5005
 * @version $Revision: 1.2 $
 *
 */
public class LeafIterator extends FilterIterator {

	public LeafIterator(Tree tree) {
		this(tree.getRoot());
	}

	public LeafIterator(Node rootNode) {
		super(new PreOrderIterator(rootNode), new LeafSelector());
	}

	public static class LeafSelector implements NodeSelector {

		public boolean isMatch(Node node) {
			return !node.hasChildren();
		}
		
	}

}
