package gjb.util.tree;

import java.util.Map;

/**
 * Interface <code>NodeModifier</code> should be implemented by
 * classes that provide a way to either modify a <code>Node</code> or
 * create a new one based on the current one.  Note that the modified
 * <code>Tree</code> is cloned during the modification, hence simply
 * modifyiing existing <code>Node</code>s will destroy the original
 * <code>Tree</code>.  The <code>modify</code> method should always
 * return a newly constructed <code>Node</code> unless one
 * <em>really</em> knows what one is doing.
 *
 * The following example illustrates the implementation of this
 * interface:
 *
 * <pre>
 *   public class UpperCaseModifier implements NodeModifier {
 *     public Node modify(Node node, Map parameters) {
 *       return new Node(node.key().toUpperCase());
 *     }
 *   }
 * </pre>
 *
 * It can be called with:
 *
 * <pre>
 *   NodeModifier modifier = new UpperCaseModifier();
 *   Tree tree = tree1.modify(modifier, null);
 * </pre>
 *
 * @author <a href="mailto:gjb@luc.ac.be">Geert Jan Bex</a>
 * @version 1.0
 */
public interface NodeModifier {

	/**
	 * <code>modify</code> method must be implemented such that it
	 * constructs a new <code>Node</code> based on the currently
	 * modified one.  Paramters can be provided via a <code>Map</code>
	 * and hence can be modified at will during a modification.
	 *
	 * @param node a <code>Node</code> value that is currently modified
	 * @param paramters a <code>Map</code> value key-value pair that
	 * represent parameters of the modifier
	 * @return a newly constructed <code>Node</code> value
	 */
	public Node modify(Node node, Map<String,Object> paramters)
	        throws NodeTransformException;

}
