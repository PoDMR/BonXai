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
import de.tudortmund.cs.bonxai.xsd.Element;

/**
 * Transition of the TypeAutomaton.
 *
 * Each transition connects a source state with a destination state and is
 * annotated with elements, which are associated with the type of the destination
 * state.
 */
public class TypeAutomatonTransition {

    /**
     * Source state of this transition.
     */
    private TypeAutomatonState source;
    /**
     * Destination state of this transition. The elements specified by the
     * annotation are of the type represented by this state.
     */
    private TypeAutomatonState destination;
    /**
     * List of elements, which are annotated to the transition.
     *
     * Instead of having a transitons for each element a single transition holds
     * a list of elements. So for each pair of source and destination states
     * only one transition exists.
     */
    private LinkedList<Element> elements;

    /**
     * Constructor of the TypeAutomatonTransition, which obtains a source and a
     * destination state.
     *
     * The elements are later annotated via the addElement method {@link addElement}.
     *
     * @param source            state in which the transition starts
     * @param destination       state in which the transition ends
     */
    public TypeAutomatonTransition(TypeAutomatonState source, TypeAutomatonState destination) {
        this.source = source;
        this.destination = destination;
    }

    /**
     * Appends the specified element to the end of the list of elements held by
     * this transition.
     *
     * @param element           element, which is added to the list of elements
     *                          held by this transition
     */
    public void addElement(Element element) {
        if (element != null) {
            if (elements == null) {
                elements = new LinkedList<Element>();
            }
            if (!elements.contains(element)) elements.add(element);
        }
    }

    /**
     * Returns the list of elements held by this transition.
     *
     * If the list is empty a null pointer is returned instead of an empty list.
     *
     * @return list of elements held by this transition or null pointer
     */
    public LinkedList<Element> getElements() {
        if (elements == null) {
            return null;
        }
        return elements;
    }

    /**
     * Returns the source state of this transition.
     *
     * @return state in which the transition starts
     */
    public TypeAutomatonState getSource() {
        return source;
    }

    /**
     * Returns the destination state of this transition.
     *
     * @return state in which the transition ends
     */
    public TypeAutomatonState getDestination() {
        return destination;
    }
}
