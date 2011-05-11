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

import java.util.LinkedList;

/**
 * Class representing a node in the ancestor tree.
 *
 * It contains a TypeAutomatonState, which is used to generate an out transitions
 * for this node.
 */
class AncestorTreeNode {

    /**
     * TypeAutomatonState, mainly used to generate new out going transitions.
     */
    private TypeAutomatonState state;
    /**
     * Single incoming transition for this node.
     */
    private AncestorTreeTransition in;
    /**
     * List of outgoing transitions for this node.
     */
    private LinkedList<AncestorTreeTransition> out;
    /**
     * Boolean field to check if the ancestor path begins at the root of the
     * XSDSchema. If this is the case the path begins with a single slash else with
     * a double slash.
     */
    private boolean isAtRoot;
    /**
     * List of incoming transitions for this node, from nodes on higher layers.
     */
    private LinkedList<AncestorTreeTransition> recursionIn;

    /**
     * Constructor of the AncestorTreeNode, which obtains a type automaton state.
     *
     * @param state         TypeAutomatonState, to generate outgoing transitions
     */
    AncestorTreeNode(TypeAutomatonState state) {
        this.state = state;
        out = new LinkedList<AncestorTreeTransition>();
        recursionIn = new LinkedList<AncestorTreeTransition>();
        isAtRoot = false;
    }

    /**
     * Returns the type automaton state of this node.
     *
     * @return type automaton state
     */
    public TypeAutomatonState getState() {
        return state;
    }

    /**
     * Sets the incomig transition of this node.
     *
     * @param in         Incomin transition, which has this node as destination
     */
    public void setIn(AncestorTreeTransition in) {
        this.in = in;
    }

    /**
     * Returns the incoming transition of this node.
     *
     * @return transition, which has this node as destination
     */
    public AncestorTreeTransition getIn() {
        return in;
    }

    /**
     * Adds the transition to the list of outgoing transitions.
     *
     * @param outTransition     transition, which leaves this node
     */
    public void addOut(AncestorTreeTransition outTransition) {
        out.add(outTransition);
    }

    /**
     * Returns the outgoing transitions of this node.
     *
     * @return transitions, which have this node as source
     */
    public LinkedList<AncestorTreeTransition> getOut() {
        return out;
    }

    /**
     * Sets the field isAtRoot for this transition.
     *
     * @param isAtRoot      boolean, which contains information for the prefix of
     * an ancestor-path from this node to the root node, if the node is a leaf.
     */
    public void setIsAtRoot(boolean isAtRoot) {
        this.isAtRoot = isAtRoot;
    }

    /**
     * Returns the isAtRoot field of this node.
     *
     * @return boolean, which is true if the node is a leaf and the ancestor-path
     * from this node to the root node begins with a single slash
     */
    public boolean getIsAtRoot() {
        return isAtRoot;
    }

    /**
     * Returns the incoming transitions of this node, from higher layer nodes
     *
     * @return transitions, which have this node as destination and which source
     * is on a higher layer.
     */
    public LinkedList<AncestorTreeTransition> getRecursionIn() {
        return recursionIn;
    }

    /**
     * Adds the transition to the list of incoming recursion transitions.
     *
     * @param recursionInTransition     transition, which enters this node from
     * a higher layer node.
     */
    public void addRecursionIn(AncestorTreeTransition recursionInTransition) {
        recursionIn.add(recursionInTransition);
    }
}
