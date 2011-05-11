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

/**
 * Basic abstract TreeNode Class.
 *
 * This class is the abstract baseclass of the tree-implementation representing
 * ancestor paths of the Bonxai structure.
 *
 * It implements a recursive structure (one node) with double linked connections
 * of each node to its one predecessor and many possible successors.
 *
 * A node without a connection to a predecessor is the root node of the tree and
 * a node without any successors is a leafnode.
 *
 * This tree is required to provide ancestor paths, ignoring the groups during
 * the upward traversing, for each element in the tree, which can be matched
 * against the compiled ancestor patterns.
 *
 * Each node in the tree needs to store an association to the original rule in
 * the Bonxai schema, defining its identity.
 *
 * The tree must provide a method to check if the current node already exists in
 * its ancestor path, to check for tree recursion.
 *
 * Derived classes are ExpressionTreeNode and GroupTreeNode.
 *
 */
public abstract class TreeNode {

    protected LinkedHashSet<TreeNode> childs = new LinkedHashSet<TreeNode>();
    private String id = "";
    protected LinkedHashSet<TreeNode> parents = new LinkedHashSet<TreeNode>();

    /**
     * Method to return the complete LinkedHashSet of the direct successors
     * (childnodes) of this current node
     * @return childs
     */
    public LinkedHashSet<TreeNode> getChilds() {
        LinkedHashSet<TreeNode> returnChilds = new LinkedHashSet<TreeNode> (this.childs);
        return returnChilds;
    }

    /**
     * Method to add a Treenode to the LinkedHashSet of childnodes.
     *
     * The parent-attribute of the new child will be set to the current TreeNode
     *
     * !This needs to ensure that the child does not already occur in the
     * ancestor path!
     *
     * @param child
     */
    public abstract boolean addChild(TreeNode child);

    /**
     * Method "generateId".
     *
     * There might be elements in the tree, which use the same rule from the
     * Bonxai schema, but still differ because of different elements deep in the
     * child structures, see group example below.
     *
     * To check for these we need to calculate some kind of "element ID", which
     * consists of the IDs of all its child (up to unlimited depth) to check for
     * equality of elements in the tree. (See group example below)
     *
     * Build some kind of hash table, associating all occurrences of some Bonxai
     * type with its calculated IDs to check for different virtual types based
     * on differentiating IDs.
     *
     * We need to generate different complex types insinde the XSD for Bonxai
     * types with different IDs.
     * If a group has different associated IDs some instances of this groups
     * need to be inlined in the child patterns.
     *
     * Method for generating the so called "Id" from the existing childnodes in
     * the tree structure. The Id will be set into the String attribute "id" and
     * directly be returned as a result of the method.
     *
     * @return id
     */
    public String generateId()
    {
        this.id = toString();
        return this.getId();
    }

    /**
     * Method for getting the already generated ID
     * (see documentation of method "generateId")
     * @return
     */
    public String getId() {
        // perhaps nothing more has to be done here
        if (this.id.equals(""))
            return this.generateId();
        else
            return this.id;
    }

    /**
     * Method for returning the direct predecessor of the current TreeNode
     * @return parent
     */
    public LinkedHashSet<TreeNode> getParents() {
        return this.parents;
    }

    /**
     * Method for setting the direct predecessor of the current TreeNode
     * The current TreeNode will be added to the child list of the given Parent
     * if it is not already in there. (prevention of infinite loops)
     * @param parent
     */
    protected void addParent(TreeNode parent) {
        this.parents.add(parent);
    }

    /**
     * Recursive method construct for getting the AncestorPath in form of a
     * Vector.
     *
     * Returns the root node of the schema as the first object in the element,
     * and all further nodes in the respective order.
     *
     */
    public abstract Vector<TreeNode> getAncestorPath();

    /**
     * PLEASE USE THE METHOD ABOVE TO DETERMINE THE ANCESTORPATH OF A NODE
     * @param ancestorNodes
     */
    protected abstract Vector<TreeNode> getAncestorPath(Vector<TreeNode> ancestorNodes);

    /**
     * Abstract method for getting the complete Particle of the Bonxai
     * datastructure. All derived classes have to implement this method.
     * @return
     */
    public abstract de.tudortmund.cs.bonxai.common.Particle getParticle();

    /**
     * Method for deleting a given child from the LinkedHashSet "childs"
     * @param child
     */
    public void removeChild(Object child) {
        this.childs.remove(child);
    }

    /**
     * Method for setting the ID manually (used in case of tree collapsing)
     * @param val
     */
    public void setId(String val) {
        this.id = val;
    }

    /**
     * Method for determining if the current TreeNode is a leaf
     * @return boolean
     */
    public boolean isLeaf() {
        return this.childs.isEmpty();
    }

    /**
     * Method for getting the amount of direct childs of this TreeNode
     * @return int
     */
    public int getChildCount() {
        return this.childs.size();
    }

    /*
     * Method to for replacing a child with the given substitutionNode
     * There is no check for the ancestorpath like in method addChild()!
     */
    public void replaceChild(TreeNode child, TreeNode substitutionNode){
        if (this.getChilds().contains(child)){
            this.removeChild(child);
            this.childs.add(substitutionNode);
            substitutionNode.addParent(this);
        }
    }
}

