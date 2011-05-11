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
import de.tudortmund.cs.bonxai.xsd.ComplexType;


/**
 * Represents a state in the TypeAutomaton
 *
 * Each state consists of a ComplexType and lists of incoming and outgoing
 * transitions.
 *
 * Intuitively the automaton can be seen as a specialized graph on types with
 * elements as transitional output.
 */
public class TypeAutomatonState {

    /**
     * ComplexType represented by this state.
     */
    private ComplexType type;
    /**
     * List of incoming transitions.
     * This state is the destination state of this transitions.
     */
    private LinkedList<TypeAutomatonTransition> in;
    /**
     * List of outgoing transitions.
     * This state is the source state of this transitions.
     */
    private LinkedList<TypeAutomatonTransition> out;

    /**
     * Constructor of the TypeAutomatonState generates a state, which has no
     * incoming or outgoing transitions. The type of the state is given via the
     * parameter.
     *
     * @param type          type of the constructed state
     */
    public TypeAutomatonState(ComplexType type) {
        this.type = type;
    }

    /**
     * This method adds the received transition to the list of incoming transitions.
     *
     * If the transition already exists, with same source and destination state,
     * the element list of both transitions are merged.
     * If the transition is a null pointer nothing changes.
     *
     * @param transition    incoming transitions, which should be added to the
     *                      state
     * @return transition, which was edited
     */
    protected TypeAutomatonTransition addInTransition(TypeAutomatonTransition transition) {
        if (transition != null) {
            if (in == null) {
                in = new LinkedList<TypeAutomatonTransition>();
            }
            for (int i = 0; i < in.size(); i++) {
                if (in.get(i) != transition && in.get(i).getDestination() == transition.getDestination() && in.get(i).getSource() == transition.getSource()) {
                    for (int j = 0; j < transition.getElements().size(); j++) {
                        in.get(i).addElement(transition.getElements().get(j));
                    }
                    return in.get(i);
                }
            }
            in.add(transition);
            return transition;
        }
        return null;
    }

    /**
     * This method adds the received transition to the list of outgoing transitions.
     *
     * If the transition already exists, with same source and destination state,
     * the element list of both transitions are merged.
     * If the transition is a null pointer nothing changes.
     *
     * @param transition    incoming transition, which should be added to the
     *                      state
     *  @return transition, which was edited
     */
    protected TypeAutomatonTransition addOutTransition(TypeAutomatonTransition transition) {
        if (transition != null) {
            if (out == null) {
                out = new LinkedList<TypeAutomatonTransition>();
            }
            for (int i = 0; i < out.size(); i++) {
                if (out.get(i) != transition && out.get(i).getDestination() == transition.getDestination() && out.get(i).getSource() == transition.getSource()) {
                    for (int j = 0; j < transition.getElements().size(); j++) {
                        out.get(i).addElement(transition.getElements().get(j));
                    }
                    return out.get(i);
                }
            }
            out.add(transition);
            return transition;
        }
        return null;
    }

    /**
     * Returns a list of incoming transitions of this state or a null pointer if
     * the list is empty.
     *
     * @return list of incoming transitions ot null pointer
     */
    protected LinkedList<TypeAutomatonTransition> getInTransitions() {
        if (in == null) {
            return null;
        }
        return in;
    }

    /**
     * Returns a list of outgoing transitions of this state or a null pointer if
     * the list is empty.
     *
     * @return list of outgoing transitions ot null pointer
     */
    protected LinkedList<TypeAutomatonTransition> getOutTransitions() {
        if (out == null) {
            return null;
        }
        return out;
    }

    /**
     * Returns the ComplexType of this state.
     *
     * @return type, which is represented by this state
     */
    protected ComplexType getType() {
        return type;
    }
}
