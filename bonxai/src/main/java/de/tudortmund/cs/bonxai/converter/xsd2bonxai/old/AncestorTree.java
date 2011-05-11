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
package de.tudortmund.cs.bonxai.converter.xsd2bonxai.old;

import de.tudortmund.cs.bonxai.bonxai.*;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;

/**
 * Class representing an ancestor tree, it helps generating distinct ancestor-
 * paths for the ComplexType of its root node .
 */
public class AncestorTree {

    /**
     * Root of this tree.
     */
    private AncestorTreeNode root;
    /**
     * List of leaf nodes for this tree.
     */
    private LinkedList<AncestorTreeNode> leafs;
    /**
     * List of all nodes, which are on the same layer of this tree and currently
     * used to generate the ancestor-paths.
     */
    private LinkedList<AncestorTreeNode> currentLayer;
    /**
     * List of all nodes, which are on the same layer of this tree and are used
     * in the next step to generate the ancestor-paths.
     *
     * Next layer is one layer below current layer.
     */
    private LinkedList<AncestorTreeNode> nextLayer;
    /**
     * List of all transitions, which are going into current layer nodes.
     */
    private LinkedList<AncestorTreeTransition> currentTransitionLayer;
    /**
     * List of all transitions, which are going into next layer nodes.
     */
    private LinkedList<AncestorTreeTransition> nextTransitionLayer;
    /**
     * List of all ancestor trees, containing this one as well. Used to propagate
     * the marked element names.
     */
    private AncestorTreeList treeList;

    /**
     * Constructor of the this class. It is parameterized with the root of the
     * new tree, a start transition, which should point to the root and the tree
     * list in which the tree is contained.
     *
     * @param root              root of this tree
     * @param startTransition   transition, which enters root and has no source node
     * @param treeList          tree list, which contains all trees currently viewed
     */
    public AncestorTree(AncestorTreeNode root, AncestorTreeTransition startTransition, AncestorTreeList treeList) {
        this.root = root;
        root.setIn(startTransition);
        this.treeList = treeList;
        treeList.setCurrentMark(startTransition.getElementName());
        currentTransitionLayer = new LinkedList<AncestorTreeTransition>();
        currentTransitionLayer.add(startTransition);
        nextTransitionLayer = new LinkedList<AncestorTreeTransition>();
        currentLayer = new LinkedList<AncestorTreeNode>();
        currentLayer.add(root);
        nextLayer = new LinkedList<AncestorTreeNode>();
        leafs = new LinkedList<AncestorTreeNode>();
    }

    /**
     * Returns the root node of this tree.
     *
     * @return node, which is at the root of this tree
     */
    public AncestorTreeNode getRoot() {
        return root;
    }

    /**
     * Returns a list of leafs for this tree.
     *
     * @return list of leafs
     */
    public LinkedList<AncestorTreeNode> getLeafs() {
        return leafs;
    }

    /**
     * Adds a leaf node to the list of leafs.
     *
     * @param leaf      node, which is a leaf
     */
    public void addLeaf(AncestorTreeNode leaf) {
        leafs.add(leaf);
    }

    /**
     * Returns the list of nodes, which are in the current layer
     *
     * @return list of current layer nodes
     */
    public LinkedList<AncestorTreeNode> getCurrentLayer() {
        return currentLayer;
    }

    /**
     * Returns the list of nodes, which are in the next layer
     *
     * @return list of next layer nodes
     */
    public LinkedList<AncestorTreeNode> getNextLayer() {
        return nextLayer;
    }

    /**
     * Returns the list of transitions, which are entering the current layer
     *
     * @return list of current layer transitions
     */
    public LinkedList<AncestorTreeTransition> getCurrentTransitionLayer() {
        return currentTransitionLayer;
    }

    /**
     * Returns the list of transitions, which are entering the next layer
     *
     * @return list of next layer transitions
     */
    public LinkedList<AncestorTreeTransition> getNextTransitionLayer() {
        return nextTransitionLayer;
    }

    /**
     * Swaps the current node layer with the next one and replaces the next node
     * with an empty one.
     */
    public void swapLayers() {
        currentLayer = nextLayer;
        nextLayer = new LinkedList<AncestorTreeNode>();
        currentTransitionLayer = nextTransitionLayer;
        nextTransitionLayer = new LinkedList<AncestorTreeTransition>();
    }

    /**
     * Returns the complete ancestor-path of this ancestor tree. It is later used
     * as part of the ancestor-path for the complex type of the root node state.
     *
     * @return list, which contains different ancestor-paths for each path from a
     * leaf node to the root node. Should be used in a orExpression.
     */
    public Vector<AncestorPatternParticle> buildAncestorVector() {
        Vector<AncestorPatternParticle> orVector = new Vector<AncestorPatternParticle>();
        for (int i = 0; i < leafs.size(); i++) {
            orVector.add(new SequenceExpression(buildSequenceVector(leafs.get(i), null)));
        }
        return orVector;
    }

    /**
     * Returns the ancestor-path for a path from the specified node to the root
     * node in the ancestor tree.
     *
     * @param node          node, in which the path starts
     * @return list, which contains node-root ancestor-path
     */
    public Vector<AncestorPatternParticle> buildSequenceVector(AncestorTreeNode node, AncestorTreeNode start) {
        Vector<AncestorPatternParticle> sequenceVector = new Vector<AncestorPatternParticle>();
        if (node != null && node != start) {
            if (!node.getRecursionIn().isEmpty()) {
                for (Iterator<AncestorTreeTransition> it = node.getRecursionIn().iterator(); it.hasNext();) {
                    AncestorTreeTransition ancestorTreeTransition = it.next();
                    Vector<AncestorPatternParticle> recVector = new Vector<AncestorPatternParticle>();
                    String namespace = ancestorTreeTransition.getElementName().substring(1, ancestorTreeTransition.getElementName().indexOf("}"));
                    String element = ancestorTreeTransition.getElementName().substring(ancestorTreeTransition.getElementName().lastIndexOf("}") + 1);
                    recVector.add(new SingleSlashPrefixElement(namespace, element));
                    recVector.addAll(buildSequenceVector(ancestorTreeTransition.getSource(), node));
                    sequenceVector.add(new CardinalityParticle(new SequenceExpression(recVector), 0, null));
                }
            }
            String namespace = node.getIn().getElementName().substring(1, node.getIn().getElementName().indexOf("}"));
            String element = node.getIn().getElementName().substring(node.getIn().getElementName().indexOf("}") + 1, node.getIn().getElementName().length());
            if (!node.getIsAtRoot() && node.getOut().isEmpty()) {
                sequenceVector.add(new DoubleSlashPrefixElement(namespace, element));
            } else {
                sequenceVector.add(new SingleSlashPrefixElement(namespace, element));
            }
            sequenceVector.addAll(buildSequenceVector(node.getIn().getSource(), start));
        }
        return sequenceVector;
    }

    /**
     * Returns the the node with the corresponding State if it exists on the
     * path from the specified node to the root of the tree.
     *
     * @param node              node, in which the path starts
     * @param searchedState     node, in which the path starts
     * @return node, which contains state or null-pointer
     */
    public AncestorTreeNode seenState(AncestorTreeNode node, TypeAutomatonState searchedState) {
        if (node != null) {
            if (node.getState() == searchedState) {
                return node;
            } else {
                return seenState(node.getIn().getSource(), searchedState);
            }
        }
        return null;
    }
}
