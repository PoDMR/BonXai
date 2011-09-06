/**
 * Created on Sep 4, 2009
 * Modified on $Date: 2009-10-26 18:37:40 $
 */
package eu.fox7.util.tree.selectors;

import eu.fox7.util.tree.Node;

/**
 * @author lucg5005
 * @version $Revision: 1.1 $
 *
 */
public interface NodeSelector {

	public boolean isMatch(Node node);

}
