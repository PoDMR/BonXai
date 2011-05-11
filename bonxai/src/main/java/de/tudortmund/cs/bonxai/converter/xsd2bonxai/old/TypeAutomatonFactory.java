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

import de.tudortmund.cs.bonxai.common.GroupRef;
import de.tudortmund.cs.bonxai.common.Particle;
import de.tudortmund.cs.bonxai.common.ParticleContainer;
import de.tudortmund.cs.bonxai.xsd.ComplexContentType;
import de.tudortmund.cs.bonxai.xsd.ComplexType;
import de.tudortmund.cs.bonxai.xsd.Element;
import de.tudortmund.cs.bonxai.xsd.ElementRef;
import de.tudortmund.cs.bonxai.xsd.Group;
import de.tudortmund.cs.bonxai.xsd.XSDSchema;
import de.tudortmund.cs.bonxai.xsd.Type;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;

/**
 * Factory class handles TypeAutomaton generation.
 *
 * Its only method createTypeAutomaton is given a schema object and builds a
 * type automaton, which corresponds to this schema. So that an ancestor-path
 * for each ComplexType of the schema can be generated via the type automaton.
 */
public class TypeAutomatonFactory {

    /**
     * This method generates a type automaton for a given XSD XSDSchema.
     *
     * Each ComplexType is given a corresponding state in the type automaton. A
     * transition between two states exists, if the type of the destination state
     * is a direct child of the type of the source state. Annotated to the
     * transition are all elements containig the type of the source state. For
     * each pair of states only one transition exists in the type automaton.
     *
     * @param schema        XSD XSDSchema, which should be transformed to a type
     *                      automaton for the purpose of finding an ancestor-path
     *                      for each ComplexType
     * @return correct type automaton for the schema
     */
    public TypeAutomaton createTypeAutomaton(XSDSchema schema) {

        // Generate typeAutomaton with root state
        TypeAutomatonState rootState = new TypeAutomatonState(null);
        TypeAutomaton typeAutomaton = new TypeAutomaton(rootState);

        // List of all states and Hashmap for ComplexType to State mapping
        LinkedList<TypeAutomatonState> states = new LinkedList<TypeAutomatonState>();
        states.add(rootState);
        HashMap<ComplexType, TypeAutomatonState> typeToState = new HashMap<ComplexType, TypeAutomatonState>();

        // Sort the types List
        Vector<Type> types = new Vector<Type>(schema.getTypeSymbolTable().getAllReferencedObjects());
        Collections.sort(types, new Comparator<Type>() {

            public int compare(Type type, Type secondType) {
                return type.getName().toString().compareTo(secondType.getName().toString());
            }
        });

        // For all ComplexTypes generate states
        for (Iterator<Type> it = types.iterator(); it.hasNext();) {
            Type type = it.next();
            if (type instanceof ComplexType) {
                TypeAutomatonState state = new TypeAutomatonState((ComplexType) type);
                states.add(state);
                typeToState.put((ComplexType) type, state);
            }
        }

        //For all states generate transitions
        for (Iterator<TypeAutomatonState> it = states.iterator(); it.hasNext();) {
            TypeAutomatonState state = it.next();
            if (state == rootState) {

                // For all global elements generate transitions from root
                LinkedList<Element> elements = schema.getElements();
                for (Iterator<Element> secondIt = elements.iterator(); secondIt.hasNext();) {
                    Element element = secondIt.next();
                    Type type = element.getType();
                    if (type instanceof ComplexType) {
                        TypeAutomatonState destinationState = typeToState.get((ComplexType) type);
                        TypeAutomatonTransition transition = new TypeAutomatonTransition(rootState, destinationState);
                        transition.addElement(element);
                        rootState.addOutTransition(transition);
                        TypeAutomatonTransition changedTransition = destinationState.addInTransition(transition);
                        typeAutomaton.registerTransition(element.getName(), changedTransition);
                    }
                }
            } else {
                // For all states with ComplexTypes containing ComplexContentTypes generate transitions from this state
                if (state.getType().getContent() instanceof ComplexContentType) {
                    ComplexContentType content = (ComplexContentType) state.getType().getContent();
                    HashMap<Group, Boolean> groupSeen = new HashMap<Group, Boolean>();
                    LinkedList<Element> elements = extractElements(content.getParticle(), groupSeen);
                    for (Iterator<Element> secondIt = elements.iterator(); secondIt.hasNext();) {
                        Element element = secondIt.next();
                        Type type = element.getType();
                        if (type instanceof ComplexType) {
                            TypeAutomatonState destinationState = typeToState.get((ComplexType) type);
                            TypeAutomatonTransition transition = new TypeAutomatonTransition(state, destinationState);
                            transition.addElement(element);
                            state.addOutTransition(transition);
                            TypeAutomatonTransition changedTransition = destinationState.addInTransition(transition);
                            typeAutomaton.registerTransition(element.getName(), changedTransition);
                        }
                    }
                }
            }
        }
        return typeAutomaton;
    }

    /**
     * This returns a list of elemnts for a specified particle.
     *
     * It is used to generate the elments for a TypeAutomatonState, which contains
     * a ComplexType with a ComplexContentType, whose Particle is used as parameter.
     * These elements are then used for TypeAutomatonTransitions, which are leaving
     * the TypeAutomatonState annotated with the extracted element, to the
     * TypeAutomatonState specidied by the ComplexType of this element.
     *
     * @param particle      Particle, which may contain Elements needed for
     *                      TypeAutomatonTransitions.
     * @param particle      Hashmap, which contains seen Groups.
     * @return list of all elements, which are first meet on a way from the root
     * of the specified particle
     */
    public LinkedList<Element> extractElements(Particle particle, HashMap<Group, Boolean> groupSeen) {
        LinkedList<Element> list = new LinkedList<Element>();
        if (particle instanceof Element) {
            list.add((Element) particle);
        }
        if (particle instanceof ElementRef && ((ElementRef) particle).getElement() != null) {
            list.add(((ElementRef) particle).getElement());
        }
        if (particle instanceof ParticleContainer && ((ParticleContainer) particle).getParticles() != null) {
            for (Iterator<Particle> it = ((ParticleContainer) particle).getParticles().iterator(); it.hasNext();) {
                list.addAll(extractElements(it.next(), groupSeen));
            }
        }
        if (particle instanceof GroupRef && (((GroupRef) particle).getGroup()) != null) {
            if (!groupSeen.containsKey((Group) ((GroupRef) particle).getGroup()) || !groupSeen.get((Group) ((GroupRef) particle).getGroup())) {
                groupSeen.put((Group) ((GroupRef) particle).getGroup(), true);
                for (Iterator<Particle> it = ((GroupRef) particle).getGroup().getParticleContainer().getParticles().iterator(); it.hasNext();) {
                    list.addAll(extractElements(it.next(), groupSeen));
                }
                groupSeen.put((Group) ((GroupRef) particle).getGroup(), false);
            }
        }
        return list;
    }
}
