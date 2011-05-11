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

import java.util.Vector;

/**
 * Class SchemaTreeNode
 * The Rootnode of the document tree.
 */
public class SchemaTreeNode extends TreeNode {

    /**
     * Does the schema have a particle?
     * @return null
     */
    public de.tudortmund.cs.bonxai.common.Particle getParticle() {
        return null;
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
        // The return of null is intended in the case of a SchemaTreeNode!
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
    protected Vector<TreeNode> getAncestorPath(Vector<TreeNode> ancestorNodes) {
        ancestorNodes.add(this);
        return ancestorNodes;
    }

    /**
     * Method to add a Treenode to the HashSet of childnodes
     * The parent-attribute of the new child will be set to the current TreeNode
     *
     * In case of SchemaTreeNode this method can be used in the given way
     * without any restrictions.
     *
     * All other classes derived from TreeNode must serve the following rule:
     * !This needs to ensure that the child does not already occur in the
     * ancestor path!
     *
     * @param child
     */
    public boolean addChild(TreeNode child) {
        /* The HashSet of TreeNode childs is already initialized, so the new
         * child can be put directly to this HashSet
         */
        this.childs.add(child);
        child.addParent(this);
        return true;
    }

    /**
     * Return user readable string representation of the element tree.
     */
    @Override
    public String toString() {
        String returnValue = "XSDSchema(";

        if (getChilds().isEmpty()) {
            return returnValue + ")";
        }

        for (TreeNode child: getChilds())
        {
            returnValue += child.getClass() + ", ";
        }
        return returnValue.substring(0, returnValue.length() - 2) + ")";
    }
}

