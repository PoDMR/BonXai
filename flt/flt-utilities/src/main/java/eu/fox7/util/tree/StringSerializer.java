/*
 * Created on Nov 8, 2005
 * Modified on $Date: 2009-11-12 22:13:31 $
 */
package eu.fox7.util.tree;

/**
 * @author eu.fox7
 * @version $Revision: 1.2 $
 * 
 */
public class StringSerializer extends AbstractSerializer<String> {

    protected int initialIndent = 0;
    protected int indentIncrement = 2;
    protected IndentedNodeSerializer<String> nodeSerializer;

    public String serialize(Tree tree) {
        return serialize(tree, new StringNodeSerializer());
    }

    public String serialize(Tree tree,
                            NodeSerializer<String> nodeSerializer) {
        if (nodeSerializer != null)
            this.nodeSerializer = (IndentedNodeSerializer<String>) nodeSerializer;
        if (tree == null)
            return null;
        else 
            return nodeSerializer.serialize(tree.getRoot());
    }

    public int getIndentIncrement() {
        return indentIncrement;
    }

    public void setIndentIncrement(int indentIncrement) {
        this.indentIncrement = indentIncrement;
    }

    public int getInitialIndent() {
        return initialIndent;
    }

    public void setInitialIndent(int initialIndent) {
        this.initialIndent = initialIndent;
        nodeSerializer.setInitialIndent(initialIndent);
     }

    public class StringNodeSerializer implements IndentedNodeSerializer<String> {
        
        protected String indent = "";

        public String serialize(Node node) {
            if (node == null) return getEmptyTreeString();
            StringBuilder str = new StringBuilder();
            str.append(indent).append(serializeKey(node)).append("\n");
            str.append(indent).append(serializeValue(node)).append("\n");
            incrementIndent();
            for (int i = 0; i < node.getNumberOfChildren(); i++)
                str.append(serialize(node.child(i)));
            decrementIndent();
            return str.toString();
        }

        protected String serializeKey(Node node) {
            return "key: " + node.getKey();
        }

        protected String serializeValue(Node node) {
            return "value: [" + (node.getValue() == null ? "" : node.getValue().toString()) + "]";
        }

        public void setInitialIndent(int indentNumber) {
            indent = "";
            for (int i = 0; i < indentNumber; i++)
                indent += " ";
        }

        protected void incrementIndent() {
            for (int i = 0; i < getIndentIncrement(); i++)
                indent += " ";
        }

        protected void decrementIndent() {
            indent = indent.substring(getIndentIncrement());
        }

    }

}
