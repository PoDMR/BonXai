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

import de.tudortmund.cs.bonxai.common.Particle;
import de.tudortmund.cs.bonxai.bonxai.Expression;
import java.util.LinkedHashSet;
import java.util.Vector;

/**
 * Class GroupTreeNode
 */
public class GroupTreeNode extends TreeNode {

    private String name;
    private de.tudortmund.cs.bonxai.bonxai.Expression expression;
    private de.tudortmund.cs.bonxai.common.GroupRef groupRef;
    private LinkedHashSet<String> namespaces = new LinkedHashSet<String>();

    /**
     * Constructor of GroupTreeNode.
     *
     */
    public GroupTreeNode(String name, de.tudortmund.cs.bonxai.bonxai.Expression expression, de.tudortmund.cs.bonxai.common.GroupRef groupRef) {
        this.name = name;
        this.expression = expression;
        this.groupRef = groupRef;
    }

    /**
     * get the particle from the expression
     *
     * @return Particle from Expression
     */
    public de.tudortmund.cs.bonxai.common.Particle getParticle() {
        if (this.getExpression() instanceof Expression) {
            return (Particle) ((Expression) this.getExpression()).getChildPattern().getElementPattern().getRegexp();
        }
        return null;
    }

    /**
     * get the expression.
     * @return expression
     */
    public de.tudortmund.cs.bonxai.bonxai.Expression getExpression() {
        return this.expression;
    }

    /**
     * get the groupRef from which the GroupTreeNode has been created.
     * @return expression
     */
    public de.tudortmund.cs.bonxai.common.GroupRef getGroupRef() {
        return this.groupRef;
    }

    /**
     * set the groupRef. This is used by the grammar converter in order to
     * reduce the document tree and handle Groups. There can new groups be
     * created and then GroupRef has to be updated.
     *
     * @return expression
     */
    public void setGroupRef(de.tudortmund.cs.bonxai.common.GroupRef groupRef) {
        this.groupRef = groupRef;
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

        /* The HashSet of TreeNode childs is already initialized, so the new
         * child can be put directly to this HashSet
         */
        Vector<TreeNode> ancestorPath = this.getAncestorPath();

        for (TreeNode currentTreeNode : ancestorPath) {
            // necessary check for casting of currentTreeNode to ElementTreeNode to make method getExpression available
            if (currentTreeNode instanceof GroupTreeNode && child instanceof GroupTreeNode) {
                GroupTreeNode a = (GroupTreeNode) currentTreeNode;
                GroupTreeNode b = (GroupTreeNode) child;

                if (a.getName().equals(b.getName())) {
                    return false;
                }
            }
        }


        /* QUESTIONS:
         * - Are there any important restrictions for adding a child to a group?
         * - We have check, if there are empty group as leaf nodes, when the
         *   Tree is completely generated.
         */
        this.childs.add(child);
        child.addParent(this);
        return true;
    }

    /**
     * Recursive method construct for getting the AncestorPath in form of a
     * Vector.
     *
     * Returns the root node of the schema as the first object in the element,
     * and all further nodes in the respective order.
     *
     * The method without a parameter is the usual way for getting the Ancestor
     * Path.
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
     * @throws Exception
     */
    protected Vector<TreeNode> getAncestorPath(Vector<TreeNode> ancestorTreeNodes) {
        if (!this.getParents().isEmpty()) {
            ((TreeNode) this.getParents().iterator().next()).getAncestorPath(ancestorTreeNodes);
        }
        return ancestorTreeNodes;
    }

    /**
     * Set group name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get group name
     */
    public String getName() {
        return this.name;
    }

    /**
     * addNamespaces.
     * Add a LinkedHashSet of namespaces to the existing LinkedHashSet of
     * namespaces in which this group is used
     *
     * This method is used by the GrammarConverter in case of the
     * group-handling.
     */
    public void addNamespaces(LinkedHashSet<String> namespaces) {
        this.namespaces.addAll(namespaces);
    }

    /**
     * Get the LinkedHashSet of namespaces in which this group is used.
     */
    public LinkedHashSet<String> getNamespaces() {
        return this.namespaces;
    }

    /**
     * Return user readable string representation of the element tree.
     */
    @Override
    public String toString() {
        String returnValue = "[group](";

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