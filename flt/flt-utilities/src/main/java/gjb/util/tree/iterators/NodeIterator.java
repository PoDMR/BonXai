/**
 * Created on Sep 4, 2009
 * Modified on $Date: 2009-10-26 18:37:40 $
 */
package gjb.util.tree.iterators;

import gjb.util.tree.Node;

import java.util.Iterator;

/**
 * @author lucg5005
 * @version $Revision: 1.1 $
 *
 */
public interface NodeIterator extends Iterator<Node> {

	public Node getStartNode();

}
