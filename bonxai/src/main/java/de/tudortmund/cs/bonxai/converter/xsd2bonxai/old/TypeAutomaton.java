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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * Automaton for ancestor-path generation.
 *
 * This automaton consists of a start state {@link start} and a transition map
 * {@link transitionLookup}. Every state in the automaton represents a type in
 * the XSD datastructure. The ancestor-path for an element can then be generated
 * via an AncestorPathStrategy {@link AncestorPathStrategy}.
 */
public class TypeAutomaton {

    /**
     * Start state of the automaton. All visitings on this datastructure begin
     * here.
     */
    private TypeAutomatonState start;
    /**
     * Hashmap holding element names as keys and lists of transitions as values.
     * This way the types of a certain element can be extracted as destination
     * states of its associated transitions.
     */
    private HashMap<String, LinkedList<TypeAutomatonTransition>> transitionLookup;

    /**
     * Constructor of the TypeAutomaton, which gets a start state.
     * This method generates an automaton with only one state.
     *
     * @param start         start state of the automaton
     */
    public TypeAutomaton(TypeAutomatonState start) {
        this.start = start;
    }

    /**
     * This method returns the start state of this type automaton
     *
     * @return start state of the automaton
     */
    public TypeAutomatonState getStart() {
        return start;
    }

    /**
     * This method returns a set of all element names in the type automaton.
     *
     * @return Set of all element names
     */
    public Set<String> getElementNames() {
        if (transitionLookup == null) {
            transitionLookup = new HashMap<String, LinkedList<TypeAutomatonTransition>>();
        }
        return transitionLookup.keySet();
    }

    /**
     * This method returns a list of transitions for a certain element name, which
     * is given as a parameter.
     *
     * For an element name, which is in the transitionLookup, a list of all
     * transitions annotated with this element name is returned, else the method
     * returns a null pointer.
     *
     * @param elementName   name of a specified element, which is annotated to
     *                      the transitions returned
     * @return list of transitions or null pointer
     */
    public LinkedList<TypeAutomatonTransition> lookupTransitions(String elementName) {
        if (transitionLookup == null) {
            return null;
        }
        return transitionLookup.get(elementName);
    }

    /**
     * The method gets an element name and a transition as parameters and puts
     * them into the hashmap transitionLookup. Where the element name is the key
     * and the transition is the value.
     *
     * If the hashmap previously contained a mapping for this element name, the
     * old value is not replaced instead the transition is appended to the list
     * of transitions, already associated by the specified element name.
     *
     * Neither element name nor transition should be null pointers.
     *
     * @param elementName   name of the element annotated to the transition
     * @param transition    transition annotated with the specified element name
     */
    public void registerTransition(String elementName, TypeAutomatonTransition transition) {
        if (elementName != null && transition != null) {
            if (transitionLookup == null) {
                transitionLookup = new HashMap<String, LinkedList<TypeAutomatonTransition>>();
            }
            if (transitionLookup.get(elementName) != null && !transitionLookup.get(elementName).contains(transition)) {
                transitionLookup.get(elementName).add(transition);
                transitionLookup.put(elementName, transitionLookup.get(elementName));
            } else {
                LinkedList<TypeAutomatonTransition> transitionList = new LinkedList<TypeAutomatonTransition>();
                transitionList.add(transition);
                transitionLookup.put(elementName, transitionList);
            }
        }
    }

    /**
     * The method gets a type automaton transition and removes it from the
     * transitionLookup hashmap.
     *
     * @param transition    transition, which should be removed
     */
    public void removeTransition(TypeAutomatonTransition transition) {
        if (transition != null) {
            if (transitionLookup == null) {
                transitionLookup = new HashMap<String, LinkedList<TypeAutomatonTransition>>();
            }else{
               Set<String> elementNames = getElementNames();
                for (Iterator<String> it = elementNames.iterator(); it.hasNext();) {
                    String string = it.next();
                    if(transitionLookup.get(string).contains(transition)){
                        transitionLookup.get(string).remove(transition);
                    }
                }
            }
        }
    }

    /**
     * This method returns a list containing every type automaton state of this
     * type automaton.
     *
     * @return list containing all states of this type automaton
     * @param transition    transition annotated with the specified element name
     */
    public LinkedList<TypeAutomatonState> getStates() {
        LinkedList<TypeAutomatonState> states = new LinkedList<TypeAutomatonState>();
        if (transitionLookup != null) {
            for (Iterator<String> it = transitionLookup.keySet().iterator(); it.hasNext();) {
                for (Iterator<TypeAutomatonTransition> it2 = transitionLookup.get(it.next()).iterator(); it2.hasNext();) {
                    TypeAutomatonTransition transition = it2.next();
                    TypeAutomatonState state = transition.getSource();
                    if (!states.contains(state)) {
                        states.add(state);
                    }
                    state = transition.getDestination();
                    if (!states.contains(state)) {
                        states.add(state);
                    }
                }
            }
            return states;
        }
        return null;
    }

    public void trim() {
        boolean end = true;
        while (end) {
            end = false;
            for (TypeAutomatonState typeAutomatonState : getStates()) {
                if (typeAutomatonState.getInTransitions() == null && typeAutomatonState != start) {
                    end = true;
                    for (TypeAutomatonTransition typeAutomatonTransition : typeAutomatonState.getOutTransitions()) {
                        removeTransition(typeAutomatonTransition);
                        typeAutomatonTransition.getDestination().getInTransitions().remove(typeAutomatonTransition);
                    }
                    typeAutomatonState.getOutTransitions().clear();
                }
            }
        }
    }

}
