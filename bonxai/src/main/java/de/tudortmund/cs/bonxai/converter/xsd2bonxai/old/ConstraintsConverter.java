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

import de.tudortmund.cs.bonxai.bonxai.AncestorPattern;
import de.tudortmund.cs.bonxai.bonxai.AncestorPatternParticle;
import de.tudortmund.cs.bonxai.bonxai.ConstraintList;
import de.tudortmund.cs.bonxai.bonxai.DoubleSlashPrefixElement;
import de.tudortmund.cs.bonxai.bonxai.KeyConstraint;
import de.tudortmund.cs.bonxai.bonxai.KeyRefConstraint;
import de.tudortmund.cs.bonxai.bonxai.OrExpression;
import de.tudortmund.cs.bonxai.bonxai.SingleSlashPrefixElement;
import de.tudortmund.cs.bonxai.bonxai.Bonxai;
import de.tudortmund.cs.bonxai.bonxai.UniqueConstraint;
import de.tudortmund.cs.bonxai.xsd.ComplexContentType;
import de.tudortmund.cs.bonxai.xsd.ComplexType;
import de.tudortmund.cs.bonxai.xsd.Constraint;
import de.tudortmund.cs.bonxai.xsd.Element;
import de.tudortmund.cs.bonxai.xsd.Group;
import de.tudortmund.cs.bonxai.xsd.Key;
import de.tudortmund.cs.bonxai.xsd.KeyRef;
import de.tudortmund.cs.bonxai.xsd.XSDSchema;
import de.tudortmund.cs.bonxai.xsd.SimpleType;
import de.tudortmund.cs.bonxai.xsd.Unique;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;

/**
 * Bonxai2XSDConverter for constraints.
 *
 * This converter converts {@link de.tudortmund.cs.bonxai.xsd.Constraint} elements from a {@link
 * XSDSchema} to {@link de.tudortmund.cs.bonxai.bonxai.Constraint}s.
 *
 */
class ConstraintsConverter implements Converter {

    /**
     * Converts {@link de.tudortmund.cs.bonxai.xsd.Constraint}s from the given schema to {@link
     * Bonxai.Constrains}s in the given bonxai.
     *
     * This method receives the schema object to convert to the given bonxai
     * object. It is responsible to read all {@link de.tudortmund.cs.bonxai.xsd.Constraint}s from
     * schema and to create corresponding {@link de.tudortmund.cs.bonxai.bonxai.Constraint}s in bonxai.
     *
     * For convenience reasons, the method resturns the given bonxai parameter
     * again, although it manipulates it directly.
     *
     * @see Bonxai2XSDConverter
     */
    public Bonxai convert(XSDSchema schema, TypeAutomaton automaton, Bonxai bonxai) {
        TypeAutomatonFactory automatonFactory = new TypeAutomatonFactory();
        ConstraintList constraintList = new ConstraintList();

        // Find all Elements with Constrains and save them in the following list
        LinkedList<Element> constraintElement = new LinkedList<Element>();

        // For each state of the type automaton find constraints and if necessary add new State to the automaton
        TypeAutomatonState destinationState = new TypeAutomatonState(null);
        for (Iterator<TypeAutomatonState> it = automaton.getStates().iterator(); it.hasNext();) {
            TypeAutomatonState state = it.next();
            if (state.getType() == null) {
                for (Iterator<Element> secondIt = schema.getElements().iterator(); secondIt.hasNext();) {
                    Element element = secondIt.next();
                    if (!element.getConstraints().isEmpty()) {
                        if (!constraintElement.contains(element)) {
                            constraintElement.add(element);
                        }
                        if (element.getType() instanceof SimpleType) {
                            TypeAutomatonTransition transition = new TypeAutomatonTransition(state, destinationState);
                            transition.addElement(element);
                            state.addOutTransition(transition);
                            TypeAutomatonTransition changedTransition = destinationState.addInTransition(transition);
                            automaton.registerTransition(element.getName(), changedTransition);
                        }
                    }
                }

            } else {
                if (state.getType().getContent() instanceof ComplexContentType) {
                    ComplexContentType content = (ComplexContentType) state.getType().getContent();
                    HashMap<Group, Boolean> groupSeen = new HashMap<Group, Boolean>();
                    LinkedList<Element> elements = automatonFactory.extractElements(content.getParticle(), groupSeen);
                    for (Iterator<Element> secondIt = elements.iterator(); secondIt.hasNext();) {
                        Element element = secondIt.next();
                        if (!element.getConstraints().isEmpty()) {
                            if (!constraintElement.contains(element)) {
                                constraintElement.add(element);
                            }
                            if (element.getType() instanceof SimpleType) {
                                TypeAutomatonTransition transition = new TypeAutomatonTransition(state, destinationState);
                                transition.addElement(element);
                                state.addOutTransition(transition);
                                TypeAutomatonTransition changedTransition = destinationState.addInTransition(transition);
                                automaton.registerTransition(element.getName(), changedTransition);
                            }
                        }
                    }
                }
            }
        }
        // If an element containing a constraint appears only on one transition the ancestor-path is simple
        for (Iterator<Element> it = constraintElement.iterator(); it.hasNext();) {
            Element element = it.next();
            LinkedList<TypeAutomatonTransition> transitions = automaton.lookupTransitions(element.getName());
            if (transitions.size() == 1) {
                AncestorPattern constraintAncestorPattern;
                if (transitions.element().getSource().getType() == null) {
                    constraintAncestorPattern = new AncestorPattern(new SingleSlashPrefixElement(element.getNamespace(), element.getLocalName()));
                } else {
                    constraintAncestorPattern = new AncestorPattern(new DoubleSlashPrefixElement(element.getNamespace(), element.getLocalName()));
                }
                for (Iterator<Constraint> it2 = element.getConstraints().iterator(); it2.hasNext();) {
                    Constraint constraint = it2.next();
                    if (constraint instanceof Key) {
                        Key key = (Key) constraint;
                        KeyConstraint keyConstraint = new KeyConstraint(key.getName(), constraintAncestorPattern, key.getSelector(), key.getFields());
                        constraintList.addConstraint(keyConstraint);
                    }
                    if (constraint instanceof KeyRef) {
                        KeyRef keyRef = (KeyRef) constraint;
                        KeyRefConstraint keyRefConstraint = new KeyRefConstraint(keyRef.getName(), constraintAncestorPattern, keyRef.getSelector(), keyRef.getFields());
                        constraintList.addConstraint(keyRefConstraint);
                    }
                    if (constraint instanceof Unique) {
                        Unique unique = (Unique) constraint;
                        UniqueConstraint uniqueConstraint = new UniqueConstraint(constraintAncestorPattern, unique.getSelector(), unique.getFields());
                        constraintList.addConstraint(uniqueConstraint);
                    }
                }
            } else {
                // Generate ancestor-trees for complex ancestor-paths
                AncestorTreeList ancestorTreeList = new AncestorTreeList();
                // For each element only one ancestor-tree (Warning this mean no that an element name might not appear multipe times)
                LinkedList<Element> toRootElements = new LinkedList<Element>();
                int pos = 0;
                for (int i = 0; i < transitions.size(); i++) {
                    for (int j = 0; j < transitions.get(i).getElements().size(); j++) {
                        if (transitions.get(i).getElements().get(j).getName().equals(element.getName())) {
                            pos = j;
                        }
                    }
                    if (!toRootElements.contains(transitions.get(i).getElements().get(pos))) {
                        AncestorTreeNode root = new AncestorTreeNode(transitions.get(i).getDestination());
                        AncestorTreeTransition toRoot = new AncestorTreeTransition(null, root, element.getName());
                        ancestorTreeList.getTreeList().add(new AncestorTree(root, toRoot, ancestorTreeList));
                        toRootElements.add(transitions.get(i).getElements().get(pos));
                    }
                }
                // Until the tree list is empty do
                while (!ancestorTreeList.getTreeList().isEmpty()) {
                    // For each tree
                    for (int i = 0; i < ancestorTreeList.getTreeList().size(); i++) {
                        // View all nodes on current layer
                        for (int j = 0; j < ancestorTreeList.getTreeList().get(i).getCurrentLayer().size(); j++) {
                            AncestorTreeNode node = ancestorTreeList.getTreeList().get(i).getCurrentLayer().get(j);
                            // If node intransition is marked
                            if (ancestorTreeList.getCurrentMarks().get(node.getIn().getElementName())) {
                                LinkedList<TypeAutomatonTransition> transitionList = automaton.lookupTransitions(node.getIn().getElementName());
                                // View all transitions with intransition element name
                                for (int k = 0; k < transitionList.size(); k++) {
                                    // Chose only the transitions which enter node
                                    if (transitionList.get(k).getDestination() == node.getState()) {
                                        if (transitionList.get(k).getSource() == automaton.getStart()) {
                                            node.setIsAtRoot(true);
                                            ancestorTreeList.getTreeList().get(i).addLeaf(node);
                                        } else {
                                            // View the transitions which are leaving the source state of the transitions entering the node
                                            LinkedList<String> usedElements = new LinkedList<String>();
                                            for (int l = 0; l < transitionList.get(k).getSource().getInTransitions().size(); l++) {
                                                TypeAutomatonTransition inTransition = transitionList.get(k).getSource().getInTransitions().get(l);
                                                // And build Transitions and Nodes for each TypeTransition and element name
                                                for (int m = 0; m < inTransition.getElements().size(); m++) {
                                                    if (!usedElements.contains(inTransition.getElements().get(m).getName())) {
                                                        AncestorTreeNode recNode = ancestorTreeList.getTreeList().get(i).seenState(node, inTransition.getDestination());
                                                        if (recNode != null) {
                                                            AncestorTreeTransition toRecNode = new AncestorTreeTransition(node, recNode, inTransition.getElements().get(m).getName());
                                                            usedElements.add(inTransition.getElements().get(m).getName());
                                                            recNode.addRecursionIn(toRecNode);
                                                            node.addOut(toRecNode);
                                                        } else {
                                                            AncestorTreeNode newTreeNode = new AncestorTreeNode(inTransition.getDestination());
                                                            AncestorTreeTransition toNewTreeNode = new AncestorTreeTransition(node, newTreeNode, inTransition.getElements().get(m).getName());
                                                            usedElements.add(inTransition.getElements().get(m).getName());
                                                            newTreeNode.setIn(toNewTreeNode);
                                                            node.addOut(toNewTreeNode);
                                                            ancestorTreeList.setNextMark(toNewTreeNode.getElementName());
                                                            ancestorTreeList.getTreeList().get(i).getNextLayer().add(newTreeNode);
                                                            ancestorTreeList.getTreeList().get(i).getNextTransitionLayer().add(toNewTreeNode);
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            } else {
                                // If the intransition is not marked the node is a leaf
                                ancestorTreeList.getTreeList().get(i).addLeaf(node);
                            }
                        }
                        // If it is the last tree stop
                        if (ancestorTreeList.getTreeList().size() == 1) {
                            ancestorTreeList.setAllNextMarkToFalse();
                        }
                        if (ancestorTreeList.getTreeList().get(i).getNextLayer().isEmpty()) {
                            if ((element.getType() instanceof ComplexType && ((ComplexType) element.getType() == ancestorTreeList.getTreeList().get(i).getRoot().getState().getType())) || (element.getType() instanceof SimpleType && (null == ancestorTreeList.getTreeList().get(i).getRoot().getState().getType()))) {
                                Vector<AncestorPatternParticle> ancestorTreeVector = ancestorTreeList.getTreeList().get(i).buildAncestorVector();
                                AncestorPattern constraintAncestorPattern;
                                if (ancestorTreeVector.size() == 1) {
                                    constraintAncestorPattern = new AncestorPattern(ancestorTreeVector.firstElement());
                                } else {
                                    constraintAncestorPattern = new AncestorPattern(new OrExpression(ancestorTreeList.getTreeList().get(i).buildAncestorVector()));
                                }
                                for (Iterator<Constraint> it2 = element.getConstraints().iterator(); it2.hasNext();) {
                                    Constraint constraint = it2.next();
                                    if (constraint instanceof Key) {
                                        Key key = (Key) constraint;
                                        KeyConstraint keyConstraint = new KeyConstraint(key.getName(), constraintAncestorPattern, key.getSelector(), key.getFields());
                                        constraintList.addConstraint(keyConstraint);
                                    }
                                    if (constraint instanceof KeyRef) {
                                        KeyRef keyRef = (KeyRef) constraint;
                                        KeyRefConstraint keyRefConstraint = new KeyRefConstraint(keyRef.getName(), constraintAncestorPattern, keyRef.getSelector(), keyRef.getFields());
                                        constraintList.addConstraint(keyRefConstraint);
                                    }
                                    if (constraint instanceof Unique) {
                                        Unique unique = (Unique) constraint;
                                        UniqueConstraint uniqueConstraint = new UniqueConstraint(constraintAncestorPattern, unique.getSelector(), unique.getFields());
                                        constraintList.addConstraint(uniqueConstraint);
                                    }
                                }
                            }
                            ancestorTreeList.getTreeList().remove(i);
                            i -= 1;
                        } else {
                            ancestorTreeList.getTreeList().get(i).swapLayers();
                        }
                    }
                    ancestorTreeList.swapMarks();
                }
            }
        }
        bonxai.setConstraintList(constraintList);
        // Remove changes on type automaton
        if (destinationState.getInTransitions() != null) {
            for (Iterator<TypeAutomatonTransition> it = destinationState.getInTransitions().iterator(); it.hasNext();) {
                TypeAutomatonTransition transition = it.next();
                transition.getSource().getOutTransitions().remove(transition);
                automaton.removeTransition(transition);
            }
        }
        return bonxai;
    }
}
