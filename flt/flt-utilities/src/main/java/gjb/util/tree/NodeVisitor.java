package gjb.util.tree;

import java.util.Map;

/**
 * Interface <code>NodeVisitor</code> should be implemented by classes
 * that provide a way to either query or modify a <code>Node</code>.
 * Note that the visited <code>Tree</code> is modified &quot;in
 * place&quot;.  To obtain a modified copy of the <code>Tree</code>,
 * the <code>NodeModifier</code> interface should be used. <br/>
 *
 * The following is a simple example of an implementation that will
 * convert each node's key to uppercase (note that the parameters are
 * not used in this example):
 *
 * <pre>
 *   public class UpperCaseVisitor implements NodeVisitor {
 *     public void visit(Node node, Map parameters) {
 *       node.setKey(node.key().toUpperCase());
 *     }
 *   }
 * </pre>
 * 
 * This visitor can be applied to a tree as follows:
 *
 * <pre>
 *   NodeVisitor visitor = new UpperCaseVisitor();
 *   tree.visit(visitor, null);
 * </pre>
 *
 * The following example uses the parameters to count the number of
 * times the various keys occur in the tree's nodes.
 *
 * <pre>
 * public class CountKeysVisitor implements NodeVisitor {
 *   public void visit(Node node, Map counter) {
 *     if (!counter.containsKey(node.key())) {
 *       counter.put(node.key(), new Integer(0));
 *     }
 *     counter.put(node.key(),
 *                 new Integer(((Integer) counter.get(node.key())).intValue() + 1));
 *     }
 *   }
 * </pre>
 *
 * It can be used as follows:
 * <pre>
 *   NodeVisitor visitor = new CountKeysVisitor();
 *   Map counter = new HashMap();
 *   tree.visit(visitor, counter);
 * </pre>
 *
 * <code>counter</code> will now contain a mapping from keys to the
 * number of nodes they occur in.
 *
 * @author <a href="mailto:gjb@luc.ac.be">Geert Jan Bex</a>
 * @version 1.0
 */
public interface NodeVisitor {

	/**
	 * <code>visit</code> method must be implemented such that it uses
	 * the information in the node or modifies the latter.  Paramters
	 * can be provided via a <code>Map</code> and hence can be
	 * modified at will during a visit.
	 *
	 * @param node a <code>Node</code> value that is currently visited
	 * @param paramters a <code>Map</code> value key-value pair that
	 * represent parameters of the visitor
	 */
	public void visit(Node node, Map<String,Object> paramters)
	        throws NodeTransformException;

}
