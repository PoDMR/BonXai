/**
 * This file is part of BonXai.
 *
 * BonXai is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * BonXai is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with BonXai.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.tudortmund.cs.bonxai.converter.bonxai2xsd.old;

import java.util.LinkedHashSet;
import java.util.Vector;
import de.tudortmund.cs.bonxai.bonxai.Expression;
import de.tudortmund.cs.bonxai.bonxai.ChildPattern;

/**
 * Class ElementTreeNode
 */
public class ElementTreeNode extends TreeNode {

    private de.tudortmund.cs.bonxai.bonxai.Expression expression;
    private String namespace;
    private String name;
    private LinkedHashSet<de.tudortmund.cs.bonxai.bonxai.Constraint> constraintList;
    private ChildPattern childPattern;

    /**
     * Constructor of the ElementTreeNode
     * @param namespace
     * @param name
     * @param expression
     * @param parent
     */
    public ElementTreeNode(String namespace, String name, Expression expression) {
        // The new constructor (without a parent attribute) is important for the
        // check if there is already a Node with the same Expression in the
        // AncestorPath. See documentation of method: addChild
        this.namespace = namespace;
        this.name = name;
        this.expression = expression;
        this.constraintList = new LinkedHashSet<de.tudortmund.cs.bonxai.bonxai.Constraint>();
        this.childPattern = null;
    }

    /**
     * Get the particle out of the given expression
     * @return
     */
    public de.tudortmund.cs.bonxai.common.Particle getParticle() {
        return this.getExpression().getChildPattern().getElementPattern().getRegexp();
    }

    public ChildPattern getChildPattern() {
        return this.childPattern;
    }

    public void setChildPattern(ChildPattern childPattern) {
        this.childPattern = childPattern;
    }

    /**
     * Get the expression
     * @return
     */
    public Expression getExpression() {
        return expression;
    }

    /**
     * Set the expression
     *
     * @param expression
     */
    public void setExpression(Expression expression) {
        this.expression = expression;
    }

    /**
     * Get the name of the ElementTreeNode
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Get the namespace of the ElementTreeNode
     * @return
     */
    public String getNamespace() {
        return namespace;
    }

    /**
     * Get the fully qualified name of the ElementTreeNode
     * @return
     */
    public String getFQName() {
        return "{" + namespace + "}" + name;
    }

    /**
     * Recursive method construct for getting the AncestorPath in form of a
     * Vector.
     *
     * Returns the root node of the schema as the first object in the element,
     * and all further nodes in the respective order.
     *
     * The method without a parameter is the usual way for getting the ancestor
     * path.
     * The one with the result vector as parameter is for the recursion only!
     *
     * @return ancestorTreeNodes
     */
    public Vector<TreeNode> getAncestorPath() {
        Vector<TreeNode> ancestorTreeNodes = new Vector<TreeNode>();
        ancestorTreeNodes = this.getAncestorPath(ancestorTreeNodes);

        return ancestorTreeNodes;
    }

    /**
     * This method is for the recursion only! Please use the method without a
     * parameter for getting the ancestor path of this current node.
     * @param ancestorTreeNodes
     * @return ancestorTreeNodes
     */
    protected Vector<TreeNode> getAncestorPath(Vector<TreeNode> ancestorTreeNodes) {
        if (!this.getParents().isEmpty()) {
            ((TreeNode) this.getParents().iterator().next()).getAncestorPath(ancestorTreeNodes);
        }
        ancestorTreeNodes.add(this);
        return ancestorTreeNodes;
    }

    /**
     * Method to add a Treenode to the HashSet of childnodes
     * The parent-attribute of the new child will be set to the current TreeNode
     *
     * !This needs to ensure that the child does not already occur in the
     * ancestor path!
     *
     * @param child
     */
    public boolean addChild(TreeNode child) {
        /* The HashSet of TreeNode childs is already initialized, so the new
         * child can be put directly to this HashSet
         */
        Vector<TreeNode> ancestorPath = this.getAncestorPath();

        for (TreeNode currentTreeNode : ancestorPath) {
            // necessary check for casting of currentTreeNode to ElementTreeNode to make method getExpression available
            if (currentTreeNode instanceof ElementTreeNode && child instanceof ElementTreeNode) {
                ElementTreeNode a = (ElementTreeNode) currentTreeNode;
                ElementTreeNode b = (ElementTreeNode) child;

                if ((a.getExpression() == null || b.getExpression() == null)
                        || (a.getName().equals(b.getName()))
                        && (a.getNamespace().equals(b.getNamespace()))
                        && (a.getExpression().equals(b.getExpression()))) {
                    return false;
                }
            }
        }

        this.childs.add(child);
        child.addParent(this);
        return true;
    }

    /**
     * Setter for an element constraint.
     * This is used by the GrammarConverter to add a matching constraint to this
     * ElementTreeNode
     *
     * @param constraint
     */
    public void addConstraint(de.tudortmund.cs.bonxai.bonxai.Constraint constraint) {
        this.constraintList.add(constraint);
    }

    /**
     * Getter for an element constraint.
     * This is used by the conversion of an element from Bonxai to XSD to check
     * for a matching constraint.
     *
     * @param constraint
     */
    public LinkedHashSet<de.tudortmund.cs.bonxai.bonxai.Constraint> getConstraints() {
        return this.constraintList;
    }

    /**
     * Return user readable string representation of the element tree.
     */
    @Override
    public String toString() {
        String returnValue = "{" + getNamespace() + "}" + getName() + "(";

        if (getChilds().isEmpty()) {
            return returnValue + ")";
        }

        for (TreeNode child : getChilds()) {
            // returnValue += child.toString() + ", ";
            returnValue += child.getClass() + ", ";
        }
        return returnValue.substring(0, returnValue.length() - 2) + ")";
    }
}
