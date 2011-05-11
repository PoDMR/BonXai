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

/**
 * Class representing a transition in the ancestor tree.
 *
 * It is annotated with an element name, which helps to generate ancestor paths
 * for the simple ancestor-path generation strategy .
 */
class AncestorTreeTransition {

    /**
     * Source node of this transition.
     *
     * If the destination node of this transition is the root of the tree, the
     * source node is a null-pointer.
     */
    private AncestorTreeNode source;
    /**
     * Destination node of this transition.
     */
    private AncestorTreeNode destination;
    /**
     * Single element name annotated to the transition.
     */
    private String elementName;

    /**
     * Constructor of the AncestorTreeTransition, which obtains a source, a
     * destination node and an element name.
     *
     * @param source            node in which the transition starts
     * @param destination       node in which the transition ends
     * @param elementName       name of the annotated element
     */
    public AncestorTreeTransition(AncestorTreeNode source, AncestorTreeNode destination, String elementName) {
        this.source = source;
        this.destination = destination;
        this.elementName = elementName;
    }

    /**
     * Returns the source node of this transition.
     *
     * @return node in which the transition starts
     */
    public AncestorTreeNode getSource() {
        return source;
    }

    /**
     * Returns the destination node of this transition.
     *
     * @return node in which the transition ends
     */
    public AncestorTreeNode getDestination() {
        return destination;
    }

    /**
     * Returns the annotated elment name of this transition.
     *
     * @return annotated element name of thie transition
     */
    public String getElementName() {
        return elementName;
    }
}
