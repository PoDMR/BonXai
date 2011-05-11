/*
 * Created on Apr 27, 2006
 * Modified on $Date: 2009-11-12 22:13:31 $
 */
package gjb.util.tree;

import java.util.Iterator;

/**
 * @author gjb
 * @version $Revision: 1.2 $
 * 
 */
public class SExpressionSerializer extends AbstractSerializer<String> {

    public String serialize(Tree tree) {
        return serialize(tree, new SExpressionNodeSerializer());
    }

    public String serialize(Tree tree, NodeSerializer<String> nodeSerializer) {
        if (nodeSerializer != null)
            this.nodeSerializer = nodeSerializer;
        if (tree == null)
            return null;
        else if (tree.getRoot() != null)
            return nodeSerializer.serialize(tree.getRoot());
        else
            return "()";
    }

    public class SExpressionNodeSerializer implements NodeSerializer<String> {

        public String serialize(Node node) {
            StringBuilder str = new StringBuilder();
            str.append("(");
            str.append(node.key() != null ? node.key() : "node");
            if (node.getNumberOfChildren() > 0) {
                for (Iterator<Node> it = node.children(); it.hasNext(); ) {
                    str.append(" ").append(serialize(it.next()));
                }
            }
            str.append(")");
            return str.toString();
        }

    }

}
