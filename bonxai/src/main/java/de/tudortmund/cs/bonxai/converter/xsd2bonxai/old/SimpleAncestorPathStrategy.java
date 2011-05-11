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

import de.tudortmund.cs.bonxai.converter.xsd2bonxai.old.AncestorTreeNode;
import de.tudortmund.cs.bonxai.bonxai.*;
import java.util.*;

/**
 * Class representing a short and simple ancestor-path generation Strategy.
 *
 * It implements the inteface AncestorPathStrategy and will be used  in the
 * ExpressionsConverter {@link ExpressionsConverter}.
 * As described in {@link AncestorPathStrategy} it generates an ancestor-
 * paths for each state of the type automaton. In difference to other implementations
 * calculated ancestor-pahts will be short but not as precise as a full blown
 * regular expression for the path to respective type or child-path in Bonxai.
 * Never the less it will be correct.
 */
public class SimpleAncestorPathStrategy implements AncestorPathStrategy {

    /**
     * The calculate method receives an automaton and generates a hashmap holding
     * ancestor-patterns as values for each state of the type automaton.
     *
     * Neither a key nor a value of the hashmap is null.
     * For every state of the automaton, there is a key in the hashmap.
     *
     * @param automaton          type automaton, which was generated to find the
     *                           ancestor-path for each type
     * @return hashmap with types as keys and ancestor-paths as values
     */
    public HashMap<TypeAutomatonState, AncestorPattern> calculate(TypeAutomaton automaton) {
        HashMap<TypeAutomatonState, Vector<AncestorPatternParticle>> partMap = new HashMap<TypeAutomatonState, Vector<AncestorPatternParticle>>();
        for (Iterator<String> it = automaton.getElementNames().iterator(); it.hasNext();) {
            String elementName = it.next();
            LinkedList<TypeAutomatonTransition> transitions = automaton.lookupTransitions(elementName);
            //If only one transition for an element name exists, only the rule (//element name) is needed
            boolean sameEnd = true;
            boolean isNotOnlyRoot = false;

            for (int i = 0; i < transitions.size(); i++) {
                for (int j = i+1; j < transitions.size(); j++) {
                   if(transitions.get(i).getDestination() != transitions.get(i).getDestination()){
                       sameEnd = false;
                   }
                   if(transitions.get(i).getSource().getType() != null){
                       isNotOnlyRoot = true;
                   }
                }
            }
            if (transitions.size() == 1 || sameEnd && transitions.size()>1) {
                String namespace = elementName.substring(1, elementName.indexOf("}"));
                String element = elementName.substring(elementName.indexOf("}") + 1, elementName.length());
                AncestorPatternElement ancestorPatternPart;
                if (!isNotOnlyRoot) {
                    ancestorPatternPart = new SingleSlashPrefixElement(namespace, element);
                } else {
                    ancestorPatternPart = new DoubleSlashPrefixElement(namespace, element);
                }
                if (partMap.get(transitions.element().getDestination()) != null) {
                    if (ancestorPatternPart instanceof SingleSlashPrefixElement) {
                        partMap.get(transitions.element().getDestination()).add((SingleSlashPrefixElement) ancestorPatternPart);
                    } else {
                        partMap.get(transitions.element().getDestination()).add((DoubleSlashPrefixElement) ancestorPatternPart);
                    }
                } else {
                    Vector<AncestorPatternParticle> partVector = new Vector<AncestorPatternParticle>();
                    if (ancestorPatternPart instanceof SingleSlashPrefixElement) {
                        partVector.add((SingleSlashPrefixElement) ancestorPatternPart);
                    } else {
                        partVector.add((DoubleSlashPrefixElement) ancestorPatternPart);
                    }
                    partMap.put(transitions.element().getDestination(), partVector);
                }
            } else {
                AncestorTreeList ancestorTreeList = new AncestorTreeList();
                LinkedList<TypeAutomatonState> rootStates = new LinkedList<TypeAutomatonState>();
                for (int i = 0; i < transitions.size(); i++) {
                    if (!rootStates.contains(transitions.get(i).getDestination())) {
                        AncestorTreeNode root = new AncestorTreeNode(transitions.get(i).getDestination());
                        AncestorTreeTransition toRoot = new AncestorTreeTransition(null, root, elementName);
                        ancestorTreeList.getTreeList().add(new AncestorTree(root, toRoot, ancestorTreeList));
                        rootStates.add(transitions.get(i).getDestination());
                    }
                }
                // Until the tree list is empty
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
                                ancestorTreeList.getTreeList().get(i).addLeaf(node);
                            }
                        }
                        // If it is the last tree stop
                        if (ancestorTreeList.getTreeList().size() == 1) {
                            ancestorTreeList.setAllNextMarkToFalse();
                        }
                        if (ancestorTreeList.getTreeList().get(i).getNextLayer().isEmpty()) {
                            // Build ancestor pattern part for this tree
                            Vector<AncestorPatternParticle> treeVector = ancestorTreeList.getTreeList().get(i).buildAncestorVector();
                            if (partMap.get(ancestorTreeList.getTreeList().get(i).getRoot().getState()) != null) {
                                for (int j = 0; j < treeVector.size(); j++) {
                                    partMap.get(ancestorTreeList.getTreeList().get(i).getRoot().getState()).add(treeVector.get(j));
                                }
                            } else {
                                Vector<AncestorPatternParticle> partVector = new Vector<AncestorPatternParticle>();
                                partVector.addAll(treeVector);
                                partMap.put(ancestorTreeList.getTreeList().get(i).getRoot().getState(), partVector);
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
        HashMap<TypeAutomatonState, AncestorPattern> returnMap = new HashMap<TypeAutomatonState, AncestorPattern>();

        // Sort the states List
        Vector<TypeAutomatonState> states = new Vector<TypeAutomatonState>(partMap.keySet());
        Collections.sort(states, new Comparator<TypeAutomatonState>() {

            public int compare(TypeAutomatonState typeAutomatonState, TypeAutomatonState secondtypeAutomatonState) {
                return typeAutomatonState.getType().getName().toString().compareTo(secondtypeAutomatonState.getType().getName().toString());
            }
        });

        for (Iterator<TypeAutomatonState> it = states.iterator(); it.hasNext();) {
            TypeAutomatonState typeAutomatonState = it.next();
            if (partMap.get(typeAutomatonState).size() == 1) {
                returnMap.put(typeAutomatonState, new AncestorPattern(partMap.get(typeAutomatonState).firstElement()));
            } else {
                returnMap.put(typeAutomatonState, new AncestorPattern(new OrExpression(partMap.get(typeAutomatonState))));
            }
        }
        return returnMap;
    }
}
