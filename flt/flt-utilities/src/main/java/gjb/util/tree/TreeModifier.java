/**
 * Created on Mar 23, 2009
 * Modified on $Date: 2009-11-12 22:13:31 $
 */
package gjb.util.tree;

import java.util.Map;

/**
 * @author lucg5005
 * @version $Revision: 1.2 $
 *
 */
public class TreeModifier {

	protected Tree tree;

	public TreeModifier(Tree tree) {
		super();
		this.tree = tree;
	}

	public Tree modify(NodeModifier modifier, Map<String,Object> parameters)
            throws NodeTransformException {
		Tree newTree = new Tree();
		if (!tree.isEmpty())
			newTree.setRoot(modify(tree.getRoot(), modifier, parameters));
		return newTree;
	}
	
	protected Node modify(Node node, NodeModifier modifier,
                          Map<String,Object> parameters)
	        throws NodeTransformException {
		Node newNode = modifier.modify(node, parameters);
		for (int i = 0; i < node.getNumberOfChildren(); i++)
			newNode.addChild(modify(node.getChild(i), modifier, parameters));
		return newNode;
	}

}
