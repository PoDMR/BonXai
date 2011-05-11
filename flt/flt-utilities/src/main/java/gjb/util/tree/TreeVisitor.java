/**
 * Created on Mar 23, 2009
 * Modified on $Date: 2009-10-26 18:37:39 $
 */
package gjb.util.tree;

import gjb.util.tree.iterators.PreOrderIterator;

import java.util.Iterator;
import java.util.Map;

/**
 * @author lucg5005
 * @version $Revision: 1.1 $
 *
 */
public class TreeVisitor {

	protected java.util.Iterator<Node> iterator;

	public TreeVisitor(Tree tree) {
		this(new PreOrderIterator(tree));
	}

	public TreeVisitor(java.util.Iterator<Node> iterator) {
		super();
		this.iterator = iterator;
	}

	public void visit(NodeVisitor visitor, Map<String,Object> parameters)
            throws NodeTransformException {
		for (Iterator<Node> it = iterator; it.hasNext(); ) {
			visitor.visit(it.next(), parameters);
		}
	}

}
