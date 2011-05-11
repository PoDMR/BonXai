package de.tudortmund.cs.bonxai.xsd.automaton.ParticleAutomatons;

import de.tudortmund.cs.bonxai.xsd.automaton.exceptions.NonDeterministicFiniteAutomataException;
import de.tudortmund.cs.bonxai.common.AllPattern;
import de.tudortmund.cs.bonxai.common.AnyPattern;
import de.tudortmund.cs.bonxai.common.ChoicePattern;
import de.tudortmund.cs.bonxai.common.CountingPattern;
import de.tudortmund.cs.bonxai.common.GroupRef;
import de.tudortmund.cs.bonxai.common.Particle;
import de.tudortmund.cs.bonxai.common.ParticleContainer;
import de.tudortmund.cs.bonxai.common.ProcessContentsInstruction;
import de.tudortmund.cs.bonxai.common.SequencePattern;
import de.tudortmund.cs.bonxai.common.SymbolTableRef;
import de.tudortmund.cs.bonxai.xsd.*;
import de.tudortmund.cs.bonxai.xsd.XSDSchema.Qualification;
import de.tudortmund.cs.bonxai.xsd.automaton.*;
import de.tudortmund.cs.bonxai.xsd.automaton.ParticleAutomatons.exceptions.*;
import de.tudortmund.cs.bonxai.xsd.setOperations.difference.*;
import de.tudortmund.cs.bonxai.xsd.setOperations.intersection.*;
import java.util.*;

/**
 * Class used to build particle automatons for various uses. Particle automatons
 * are used to represent particle which are used to describe XML XSDSchema content
 * models. So this class helps to build particle automatons from particle,
 * provides a method to create a subset automaton, in case the particle
 * automaton is not deterministic, or methods to create various product
 * automatons for particle intersections or differences. Each method should only
 * be used with a valid XML XSDSchema particle which respects the UPA and EDC
 * constraint, if this is not the case use can result in complications.
 *
 * @author Dominik Wolff
 */
public class ParticleAutomatonFactory {

    // Map mapping any patterns to their old schemata
    private LinkedHashMap<AnyPattern, XSDSchema> anyPatternOldSchemaMap;

    /**
     * This method constructs the current particle automaton structure for a
     * specified any pattern in the given particle automaton. The structure is a
     * transiton conncting the current start state with the current final state.
     * This transition is annotated with the any pattern.
     * 
     * @param currentStartState State which is used as start state for this
     * structure part.
     * @param anyPattern Any pattern which will be annotated to a transition
     * connecting the current start state to the current final state.
     * @param currentFinalState State which is used as final state for this
     * structure part.
     * @param particleAutomaton Automaton in which the new structure is
     * constructed. Should contain current start and final state.
     */
    private void buildAnyPatternStructure(ParticleState currentStartState, AnyPattern anyPattern, ParticleState currentFinalState, ParticleAutomaton particleAutomaton) {
// changed for interleave handling
//        if (anyPattern.getProcessContentsInstruction() == null || anyPattern.getProcessContentsInstruction() == ProcessContentsInstruction.Strict) {
    	if (anyPatternOldSchemaMap != null && (anyPattern.getProcessContentsInstruction() == null || anyPattern.getProcessContentsInstruction() == ProcessContentsInstruction.Strict)) {
            for (Element element: getContainedElements(anyPattern, anyPatternOldSchemaMap.get(anyPattern))) {
                element.setForm(XSDSchema.Qualification.qualified);

                // If the particle is an element construct the appropriate automaton structure
                buildElementStructure(currentStartState, element, currentFinalState, particleAutomaton);
            }
        } else {

            // Construct new particle transition and add given any pattern to the transition
            ParticleTransition newParticleTransition = new ParticleTransition(currentStartState, currentFinalState, false);
            newParticleTransition.addAnyPattern(anyPattern);

            // If current transition already exists in the specified automaton add any pattern to existing transition
            if (!particleAutomaton.addTransition(newParticleTransition)) {

                // Get existing particle transition and add any pattern
                ParticleTransition existingParticleTransition = (ParticleTransition) currentStartState.getNextStateTransitions().get(currentFinalState);
                existingParticleTransition.addAnyPattern(anyPattern);
            }
        }
    }

    /**
     * Build a new structure in the specified current particle automaton for a
     * given choice pattern. This is done by creating new structures for each
     * particle contained in the choice pattern and connecting these
     * structures to the given start and final states.
     *
     * @param currentStartState Each constructed structure begins in this state.
     * @param choicePattern Choice pattern which is transformed into a
     * current particle automaton structure.
     * @param currentFinalState Each constructed structure ends in this state.
     * @param particleAutomaton Particle automaton containing the current start
     * and final state. In this automaton the new structure is constructed.
     */
    private void buildChoicePatternStructure(ParticleState currentStartState, ChoicePattern choicePattern, ParticleState currentFinalState, ParticleAutomaton particleAutomaton) {

        // For each particle contained in the choice pattern build a new structure
        for (Particle particle: choicePattern.getParticles()) {
            if (particle instanceof Element) {
                // If the particle is an element construct the appropriate automaton structure
                buildElementStructure(currentStartState, (Element) particle, currentFinalState, particleAutomaton);
            } else if (particle instanceof ElementRef) {
                // If the particle is an element reference the appropriate automaton structure for the referred element is constructed
                buildElementStructure(currentStartState, ((ElementRef) particle).getElement(), currentFinalState, particleAutomaton);
            } else if (particle instanceof AnyPattern) {
                // If the particle is an any pattern construct the appropriate automaton structure
                buildAnyPatternStructure(currentStartState, (AnyPattern) particle, currentFinalState, particleAutomaton);
            } else if (particle instanceof SequencePattern) {
                // If the particle is a sequence pattern construct the appropriate automaton structure
                buildSequencePatternStructure(currentStartState, (SequencePattern) particle, currentFinalState, particleAutomaton);
            } else if (particle instanceof ChoicePattern) {
                // If the particle is a choice pattern construct the appropriate automaton structure
                buildChoicePatternStructure(currentStartState, (ChoicePattern) particle, currentFinalState, particleAutomaton);
            } else if (particle instanceof CountingPattern) {
                // If the particle is a counting pattern construct the appropriate automaton structure
                buildCountingPatternStructure(currentStartState, (CountingPattern) particle, currentFinalState, particleAutomaton);
            } else if (particle instanceof GroupRef) {
                // There is no structure for groups these are simply resolved
                resolveGroupReference(currentStartState, (GroupRef) particle, currentFinalState, particleAutomaton);
            }
        }

        // If the choice pattern is empty
        if (choicePattern.getParticles().isEmpty()) {

            // Construct new epsilon particle transition
            ParticleTransition newParticleTransition = new ParticleTransition(currentStartState, currentFinalState, true);

            // If current transition already exists in the specified automaton add epsilon to existing transition
            if (!particleAutomaton.addTransition(newParticleTransition)) {

                // Get existing particle transition and add epsilon
                ParticleTransition existingParticleTransition = (ParticleTransition) currentStartState.getNextStateTransitions().get(currentFinalState);
                existingParticleTransition.setEpsilon(true);
            }
        }
    }

    /**
     * Builds new couting pattern structure for the specified counting pattern
     * in the given particle automaton. A counting pattern is no normal particle
     * it contains counters that specify how many times another particle
     * contained in the counting pattern should appear at least and how many
     * times at most. In order to construct a structure that represents this
     * behaviour it is checked how many times the structure of the contained
     * particle has to appear repeatedly. These recurrence are connected with
     * each other and epsilon transitions are used to skip ahead.
     *
     * @param startState State which is used as start state for this structure.
     * @param countingPattern Patter which contains another particle which is
     * used to contruct a schema structure and counters for minimal appearance
     * and maximal appearance of this particle structure.
     * @param finalState State which is used as final state for this structure.
     * @param particleAutomaton Automaton in which the new structure is
     * constructed. Has to contain start and final state.
     */
    private void buildCountingPatternStructure(ParticleState startState, CountingPattern countingPattern, ParticleState finalState, ParticleAutomaton particleAutomaton) {

        // Build new first state and epsilon transition from start state to first state
        ParticleState newFirstState = new ParticleState(particleAutomaton.getNextStateNumber());
        particleAutomaton.addState(newFirstState);
        ParticleTransition startFirstTransition = new ParticleTransition(startState, newFirstState, true);
        particleAutomaton.addTransition(startFirstTransition);

        // Build new last state and epsilon transition from last state to final state
        ParticleState newLastState = new ParticleState(particleAutomaton.getNextStateNumber());
        particleAutomaton.addState(newLastState);
        ParticleTransition lastFinalTransition = new ParticleTransition(newLastState, finalState, true);
        particleAutomaton.addTransition(lastFinalTransition);

        // If the minimal occurrence of the particle contained in the counting pattern is 0 it is possible to skip the particle structure so an epsilon transition from the new first to the new last state is added
        if (countingPattern.getMin() == 0) {
            ParticleTransition firstLastTransition = new ParticleTransition(newFirstState, newLastState, true);
            particleAutomaton.addTransition(firstLastTransition);
        }

        // Initialize current start and current final state. The first current start state is the start state specified for the whole structure.
        ParticleState currentStartState = newFirstState;
        ParticleState currentFinalState;

        // Counter that specifies how many times the current particle structure has to be repeated
        int recurrence;

        // If the particle is bound to appear a given number of times at max set the recurrence counter to this value.
        if (countingPattern.getMax() != null) {
            recurrence = countingPattern.getMax();

        } else if (countingPattern.getMin() >= 1) {

            // If the particle can appear endlessly set the recurrence counter to the value of times it has to appear at least.
            recurrence = countingPattern.getMin();
        } else {

            // If no minimal and maximal occurrences are specified set the recurrence counter to one.
            recurrence = 1;
        }

        // Get current particle contained in the counting pattern
        Particle currentParticle = countingPattern.getParticles().iterator().next();

        // Repeatedly build the structure of the contained particle for and connect each structure reoccurrence with the last and the next reoccurrence until the recurrence counter is hit.
        for (int i = 0; i < recurrence; i++) {

            // Check if the current reoccurrence is the last one, if this is the case the last structure ends in the last state
            if (i + 1 == recurrence) {
                currentFinalState = newLastState;
            } else {

                // If the structure is repeated after the current structure a new final state for the current structure is necessary
                currentFinalState = new ParticleState(particleAutomaton.getNextStateNumber());
                particleAutomaton.addState(currentFinalState);

                // If the current structure ends in an accepting state for the current particle add a new particle transition from the current final state to the last state to the automaton
                if (i >= countingPattern.getMin() - 1) {
                    ParticleTransition newParticleTransition = new ParticleTransition(currentFinalState, newLastState, true);
                    particleAutomaton.addTransition(newParticleTransition);
                }
            }

            // Check what kind of currentParticle the currentParticle is.
            if (currentParticle instanceof Element) {
                // If the currentParticle is an element construct the appropriate automaton structure
                buildElementStructure(currentStartState, (Element) currentParticle, currentFinalState, particleAutomaton);
            } else if (currentParticle instanceof ElementRef) {
                // If the currentParticle is an element reference the appropriate automaton structure for the referred element is constructed
                buildElementStructure(currentStartState, ((ElementRef) currentParticle).getElement(), currentFinalState, particleAutomaton);
            } else if (currentParticle instanceof AnyPattern) {
                // If the currentParticle is an any pattern a structure for this any pattern is constructed
                buildAnyPatternStructure(currentStartState, (AnyPattern) currentParticle, currentFinalState, particleAutomaton);
            } else if (currentParticle instanceof AllPattern) {
                // If the currentParticle is an all pattern the all pattern is resolved and a for the reolved particle a new structure is generated
                resolveAllPattern(currentStartState, (AllPattern) currentParticle, currentFinalState, particleAutomaton);
            } else if (currentParticle instanceof SequencePattern) {
                // If the currentParticle is a sequence this a sequence structure is generated for the automaton
                buildSequencePatternStructure(currentStartState, (SequencePattern) currentParticle, currentFinalState, particleAutomaton);
            } else if (currentParticle instanceof ChoicePattern) {
                // If the currentParticle is a choice this a choice structure is generated for the automaton
                buildChoicePatternStructure(currentStartState, (ChoicePattern) currentParticle, currentFinalState, particleAutomaton);
            } else if (currentParticle instanceof GroupRef) {
                // There is no structure for groups these are simply resolved
                resolveGroupReference(startState, (GroupRef) currentParticle, finalState, particleAutomaton);
            }

            // If the current particle is allowed to appear endlessly and the current structure reoccurrence is the last add an epsilon transition from last state to first state
            if (countingPattern.getMax() == null && i + 1 == recurrence) {
                ParticleTransition firstLastTransition = new ParticleTransition(newLastState, newFirstState, true);
                particleAutomaton.addTransition(firstLastTransition);
            }

            // The start state of the next structure reoccurrence is the final state of the last structure reoccurrence
            currentStartState = currentFinalState;
        }
    }

    /**
     * This method generates a product automaton representing the difference of
     * two particles for a given minuned automaton representing the minuend
     * particle and subtrahend automaton representing the subtrahend particle.
     * The automaton is build like a normal product automaton, with the
     * difference that all final states of the product automaton are statest
     * which contain an accepting states of the minuned automaton and a non 
     * accepting state of the subtrahend automaton. Furthermore in order to
     * compute new states the difference of elements annotated to transitions
     * has to considered which is a mayor difference to normal product
     * automatons.
     *
     * @param minuendAutomaton Automaton representing the minuned particle.
     * @param subtrahendAutomaton Automaton representing the subtrahend
     * particle.
     * @param particleDifferenceGenerator Particle difference generator used to
     * cumpute new elements and any patterns.
     * @return A new product particle automaton representing the difference of
     * the minuend particle and subtrahend particle.
     * @throws NonDeterministicFiniteAutomataException Exception which is thrown
     * if a particle automaton is a no deterministic finite automaton.
     * @throws EmptyProductParticleStateFieldException Exception which is thrown
     * if a product state contains no particle states.
     * @throws UniqueParticleAttributionViolationException Exception which is
     * thrown a particle which was used to construct a particle automaton
     * violates the UPA constraint.
     */
    public ProductParticleAutomaton buildProductAutomatonDifference(ParticleAutomaton minuendAutomaton, ParticleAutomaton subtrahendAutomaton, ParticleDifferenceGenerator particleDifferenceGenerator) throws NonDeterministicFiniteAutomataException, UniqueParticleAttributionViolationException, EmptyProductParticleStateFieldException {
        // Initialize list of all particle automaton start states
        LinkedList<ParticleState> containedStartStates = new LinkedList<ParticleState>();

        // For each particle automaton add start state to the list which will be contained by the new product automaton start state
        containedStartStates.add(minuendAutomaton.getStartState());
        containedStartStates.add(subtrahendAutomaton.getStartState());

        // Generate new product automaton start state
        ProductParticleState startState = new ProductParticleState(containedStartStates, 1);

        // Build new product automaton and add start state to the automaton
        ProductParticleAutomaton newProductParticleAutomaton = new ProductParticleAutomaton(startState);

        // Stack containing all unprocessed states of the product automaton. Currently only the start state is unprocessed as no other states exist.
        Stack<ProductParticleState> stateStack = new Stack<ProductParticleState>();
        stateStack.push(startState);

        // As long as unprocessed states exist process the next state on the state stack
        while (!stateStack.isEmpty()) {

            // Get the current unprocessed state from the stack
            ProductParticleState currentState = stateStack.pop();

            // Get set of element names which are annotated to transitions leaving the states contained in the current product state
            LinkedHashSet<String> elementNames = getElementNames(currentState);

            // After obtaining the element names generate for each element name a new transition to a new destination state. Both state and transition can already exist in the product automaton in which case the
            // difference element generated for the element name is annotated to the existing transition and the new generate transition and state are not used.
            for (String elementName : elementNames) {

                // Process the current element name, generates new state and transition connecting the current state and the new state if necessary
                processElementName(currentState, elementName, newProductParticleAutomaton, minuendAutomaton, subtrahendAutomaton, stateStack, particleDifferenceGenerator);
            }

            // Get set of any patterns which are annotated to transitions leaving the states contained in the current product state
            LinkedHashSet<AnyPattern> anyPatterns = getAnyPatterns(currentState);

            // After obtaining the any patterns generate for each any pattern a new transition to a new destination state. Both state and transition can already exist in the product automaton in which case the
            // difference element generated for the any pattern is annotated to the existing transition and the new generate transition and state are not used.
            for (AnyPattern anyPattern : anyPatterns) {

                // Process the current element name, generates new state and transition connecting the current state and the new state if necessary
                processAnyPattern(currentState, anyPattern, newProductParticleAutomaton, minuendAutomaton, subtrahendAutomaton, stateStack, particleDifferenceGenerator);
            }
        }

        // Add new final states for the new product particle automaton
        addDifferenceFinalStates(newProductParticleAutomaton, minuendAutomaton, subtrahendAutomaton);

        // Return finished automaton
        return newProductParticleAutomaton;
    }

    /**
     * This method builds a new product automaton for the given set of particle
     * automatons. The new product automaton is the result of an intersection
     * so a transition exist in this automaton if it exist in all particle
     * automatons and final states are product states that contain only final
     * states. Furthermore any patterns are handled and new particle
     * intersections are constructed while building the automaton.
     *
     * @param particleAutomatons List of particle automatons, each automaton
     * represents a particle.
     * @param particleIntersectionGenerator Generator used to construct new
     * particles for transitions of the new product automaton and to check
     * whether certain particles are intersectable.
     * @return New particle automaton for the intersection of given particle
     * automata.
     * @throws EmptyProductParticleStateFieldException Exception which is thrown
     * if a product state contains no particle states.
     * @throws NonDeterministicFiniteAutomataException Exception which is thrown
     * if a particle automaton is no deterministic finite automaton.
     */
    public ProductParticleAutomaton buildProductAutomatonIntersection(LinkedList<ParticleAutomaton> particleAutomatons, ParticleIntersectionGenerator particleIntersectionGenerator) throws EmptyProductParticleStateFieldException, NonDeterministicFiniteAutomataException {

        // Construct a list for all start states contained by the new product start state
        LinkedList<ParticleState> startStates = new LinkedList<ParticleState>();

        // For each particle automaton add start state to the set of start states
        for (ParticleAutomaton particleAutomaton: particleAutomatons) {
            startStates.add(particleAutomaton.getStartState());
        }

        // Construct new start state for the a new product automaton and add the start state to new product automaton
        ProductParticleState startState = new ProductParticleState(startStates, 1);
        ProductParticleAutomaton newProductParticleAutomaton = new ProductParticleAutomaton(startState);

        // Stack of product particle states is used to visited states which are not completed yet, i.e. lack outgoing transitions.
        Stack<ProductParticleState> stateStack = new Stack<ProductParticleState>();
        stateStack.push(startState);

        // If an unprocessed state exists on the stack process this state until no states are left on the stack
        while (!stateStack.isEmpty()) {

            // Get current product state from the top of the stack
            ProductParticleState currentState = stateStack.pop();

            // Generate set to store all element names annotated to outgoing transitions of states contained in the current product state
            LinkedHashSet<String> elementNames = getElementNames(currentState);

            // Add for each element name a transition annotated with an element with given element name to the current state or add the element to an existing transition and create new destination state if necessary
            for (String elementName : elementNames) {

                // Create set of particle states contained by the new next state
                LinkedList<ParticleState> newContainedParticleStates = new LinkedList<ParticleState>();

                // Create set of elements whose intersection will be annotated to the new transition connecting the current state to the next state
                LinkedHashSet<Element> newTransitionElements = new LinkedHashSet<Element>();

                // For each particle state contained in the current product particle state check for each outgoing transition of that particle state if this transition contains the current element name
                for (ParticleState particleState : currentState.getParticleStates()) {

                    // Generate list of particle states which contains all particle states reached by reading the current element name in the current particle state
                    LinkedList<ParticleState> nextParticleStates = new LinkedList<ParticleState>();

                    // Create set of elements which may be annotated to the new transition
                    LinkedHashSet<Element> nextTransitionElements = new LinkedHashSet<Element>();
                    for (Transition transition : particleState.getOutTransitions()) {

                        // If the current transitions contains the current element name (either an elment with same name is annotated to the transition or an annotated any pattern contains the element name
                        // add the destination state of the transition to the set of reachable particle states
                        if (transition.getNameElementMap().containsKey(elementName) || particleIntersectionGenerator.isIncluded(elementName, ((ParticleTransition) transition).getAnyPatterns())) {
                            nextParticleStates.add((ParticleState) transition.getDestinationState());

                            // Add all elements with current element name to the set of elements which may be intersected for the new transition
                            if (transition.getNameElementMap().get(elementName) != null) {
                                nextTransitionElements.addAll(transition.getNameElementMap().get(elementName));
                            }
                            if (particleIntersectionGenerator.getIncluded(elementName, ((ParticleTransition) transition).getAnyPatterns()) != null) {
                                nextTransitionElements.add(particleIntersectionGenerator.getIncluded(elementName, ((ParticleTransition) transition).getAnyPatterns()));
                            }
                        }
                    }

                    // Only one state is reachable in the current particle automaton by reading the current element name in the current state (determinism) and the transition has only one element with this
                    // name annotated to itself (UPA constraint) the state and the element are added to the corresponding sets
                    if (nextParticleStates.size() == 1 && nextTransitionElements.size() <= 1) {
                        newContainedParticleStates.addAll(nextParticleStates);
                        newTransitionElements.addAll(nextTransitionElements);
                    } else if (nextParticleStates.isEmpty()) {

                        // Add sink state
                        newContainedParticleStates.add(null);
                    } else {

                        // A second state can be reached, so the given subtrahend automaton is not deterministic
                        throw new NonDeterministicFiniteAutomataException(particleAutomatons.get(currentState.getParticleStates().indexOf(particleState)), "buildProductAutomatonIntersection");
                    }
                }

                // If the new to be product automaton state contains for each particle automaton a particle state and the transition elements are intersectable, meaning the intersection of these elements
                // is not null a new transition and a new state can be generated in the product automaton
                if (newContainedParticleStates.size() == particleAutomatons.size() && !newContainedParticleStates.contains(null) && particleIntersectionGenerator.areIntersectableElements(newTransitionElements)) {


                    // Create new particle which is the result of the intersection of all elemente contained in the newTransitionElements set and add this new particle to the transition
                    Particle newParticle = particleIntersectionGenerator.generateNewLocalParticle(new LinkedHashSet<Particle>(newTransitionElements));

                    // Create new state and transition if possible
                    createNextProductStateAndTransition(newContainedParticleStates, newParticle, currentState, newProductParticleAutomaton, stateStack);
                }
            }

            // Generate set to store all any patterns annotated to outgoing transitions of states contained in the current product state
            LinkedHashSet<AnyPattern> anyPatterns = getAnyPatterns(currentState);

            LinkedHashSet<LinkedHashSet<AnyPattern>> alreadySeenAnyPatternSets = new LinkedHashSet<LinkedHashSet<AnyPattern>>();

            // Create for each any pattern a new transition and destination state to the product automaton if necessary. The transition will be annotated with the intersection of any patterns.
            for (AnyPattern anyPattern : anyPatterns) {

                // Create set of particle states contained by the new next state
                LinkedList<ParticleState> newContainedParticleStates = new LinkedList<ParticleState>();

                // Create set of any patterns whose intersection will be annotated to the new transition connecting the current state to the next state
                LinkedHashSet<AnyPattern> newTransitionAnyPatterns = new LinkedHashSet<AnyPattern>();
                newTransitionAnyPatterns.add(anyPattern);

                // Get for each particle state of the current product state a successor state reached by reading the current any pattern
                for (ParticleState particleState : currentState.getParticleStates()) {

                    // Generate set of particle states which contains all particle states reached by reading the current element name in the current particle state
                    LinkedList<ParticleState> nextParticleStates = new LinkedList<ParticleState>();

                    // Set containing all any patterns annotated to outgoing transitions of the current particle state with non empty intersection with the current any pattern
                    LinkedHashSet<AnyPattern> nextAnyPatterns = new LinkedHashSet<AnyPattern>();

                    // Check each transition of the current particle state for a matching any pattern
                    for (Transition transition : particleState.getOutTransitions()) {

                        // Check for each any pattern of the current transition if the intersection with the given any pattern is not empty
                        for (AnyPattern possibleAnyPattern: ((ParticleTransition) transition).getAnyPatterns()) {
                            // Create set of possible any patterns containing the current any pattern and a possible any pattern of the current transition
                            LinkedHashSet<AnyPattern> possibleAnyPatterns = new LinkedHashSet<AnyPattern>();
                            possibleAnyPatterns.add(possibleAnyPattern);
                            possibleAnyPatterns.add(anyPattern);

                            // If the intersection of the possible any patterns is not empty add the possible any pattern of the current transition to the set of next any patterns
                            if (particleIntersectionGenerator.areIntersectableAnyPatterns(possibleAnyPatterns)) {
                                nextAnyPatterns.add(possibleAnyPattern);

                                // Add destination state of the current transition to the set of next particle states
                                nextParticleStates.add((ParticleState) transition.getDestinationState());
                            }
                        }

                        // Only one state is reachable in the current particle automaton by reading the current nay pattern  in the current state (determinism) and the transition has only one any patter which
                        // has a non empty intersection with the current any pattern annotated to itself (UPA constraint) the state and the any pattern are added to the corresponding sets
                        if (nextParticleStates.size() == 1 && nextAnyPatterns.size() == 1) {
                            newContainedParticleStates.addAll(nextParticleStates);
                            newTransitionAnyPatterns.addAll(nextAnyPatterns);
                        } else {

                            // If the check fails the next state will contain a sink state (null), for this case no new state is generated later
                            if (!((ParticleTransition) transition).getAnyPatterns().isEmpty()) {
                                newContainedParticleStates.add(null);
                            }
                        }
                    }
                }

                // If the new to be product automaton state contains for each particle automaton a particle state and the transition any patterns are intersectable, meaning the intersection of these any
                // patterns is not null a new transition and a new state can be generated in the product automaton
                if (!alreadySeenAnyPatternSets.contains(newTransitionAnyPatterns) && newContainedParticleStates.size() == particleAutomatons.size() && !newContainedParticleStates.contains(null) && particleIntersectionGenerator.areIntersectableAnyPatterns(newTransitionAnyPatterns)) {

                    // Create new any pattern which is the result of the intersection of all given any patterns and add the any pattern to the transition
                    AnyPattern newAnyPattern = particleIntersectionGenerator.generateNewAnyPattern(newTransitionAnyPatterns);
                    alreadySeenAnyPatternSets.add(newTransitionAnyPatterns);

                    // Create new state and transition if possible
                    createNextProductStateAndTransition(newContainedParticleStates, newAnyPattern, currentState, newProductParticleAutomaton, stateStack);
                }
            }
        }

        // Set new final states in order to do this all states of the new automaton are traversed
        for (ParticleState particleState: newProductParticleAutomaton.getStates()) {
            ProductParticleState productState = (ProductParticleState) particleState;
        	
        	// Variable to check whether the state is final or not
            boolean finalState = true;

            // For each state contained in product state check if it is a final state
            for (ParticleState containedState: productState.getParticleStates()) {
                // If the current contained state is not final in the automaton containing it
                if (!particleAutomatons.get(productState.getParticleStates().indexOf(containedState)).getFinalStates().contains(containedState)) {

                    // Set boolean variable finalState to false
                    finalState = false;
                }
            }

            // If the state is final add it to the set of final states of the current product automaton
            if (finalState) {
                newProductParticleAutomaton.addFinalState(productState);
            }
        }

        // Return the new intersection product automaton
        return newProductParticleAutomaton;
    }

    /**
     * This method constructs the particle automaton structure for a specified
     * element in the given particle automaton. The structure is a transiton
     * conncting the current start state with the current final state. This
     * transition is annotated with the element.
     *
     * @param currentStartState State which is used as start state for this
     * structure part.
     * @param element Element which will be annotated to a transition connecting
     * the current start state to the current final state.
     * @param currentFinalState State which is used as final state for this
     * structure part.
     * @param particleAutomaton Automaton in which the new structure is
     * constructed. Should contain current start and final state.
     */
    private void buildElementStructure(ParticleState currentStartState, Element element, ParticleState currentFinalState, ParticleAutomaton particleAutomaton) {

        // Check if the specified is an abstract element which is basically equivalent to an epsilon in the particle automaton
        if (element.getAbstract()) {

            // Construct new epsilon particle transition
            ParticleTransition newParticleTransition = new ParticleTransition(currentStartState, currentFinalState, true);

            // If current transition already exists in the specified automaton add epsilon to existing transition
            if (!particleAutomaton.addTransition(newParticleTransition)) {

                // Get existing particle transition and add epsilon
                ParticleTransition existingParticleTransition = (ParticleTransition) currentStartState.getNextStateTransitions().get(currentFinalState);
                existingParticleTransition.setEpsilon(true);
            }
        } else {

            // Construct new particle transition and add given elemnt to the transition
            ParticleTransition newParticleTransition = new ParticleTransition(currentStartState, currentFinalState, false);
            newParticleTransition.addElement(element);

            // If current transition already exists in the specified automaton add element to existing transition
            if (!particleAutomaton.addTransition(newParticleTransition)) {

                // Get existing particle transition and add element
                ParticleTransition existingParticleTransition = (ParticleTransition) currentStartState.getNextStateTransitions().get(currentFinalState);
                existingParticleTransition.addElement(element);
            }
        }
    }

    /**
     * This method constructs a new interleave automaton, while this automaton
     * uses the product particle automaton structure it is no conventional
     * product automaton. For each state contained in a product state all
     * outgoing transitions are used to create new states so each product
     * state only changes one contained particle state when following a
     * transition instead of changing all contained states. Furthermore the
     * constructed automaton is not deterministic and before further used has to
     * made deterministic.
     *
     * @param particleAutomatons List of particle automatons used to construct
     * the interleave automaton. Each particle automaton represents a particle
     * contained in an interleave/all pattern.
     * @param resultingSchema XSDSchema which will contain the new particle
     * structure and accordingly contains the interleave/all pattern.
     * @return New interleave Automaton which represents a resolved interleave/
     * all pattern. The particle generated from the interleave automaton is a
     * valid XML XSDSchema particle and can be used as equivalent particle for the
     * invalid interleave particle.
     * @throws EmptyProductParticleStateFieldException Exception which is thrown
     * if a product state contains no particle states.
     */
    public ProductParticleAutomaton buildInterleaveParticleAutomaton(LinkedList<ParticleAutomaton> particleAutomatons, XSDSchema resultingSchema) throws EmptyProductParticleStateFieldException {

        // Get set containing all start states contained in the list of particle automatons
        LinkedList<ParticleState> startStates = new LinkedList<ParticleState>();

        // For each automaton add its start state to the set
        for (ParticleAutomaton automaton: particleAutomatons) {
            startStates.add(automaton.getStartState());
        }

        // Create new start state for the interleave automaton
        ProductParticleState startState = new ProductParticleState(startStates, 1);

        // Create new interleave automaton with the new start state
        ProductParticleAutomaton newInterleaveAutomaton = new ProductParticleAutomaton(startState);

        // Create new stack of unprocessed states, these states have to be processed befor the automaton is complete. Add current start state to the stack because it is currently the only state of the automaton.
        Stack<ProductParticleState> stateStack = new Stack<ProductParticleState>();
        stateStack.add(startState);

        // Mark already visited states of the automaton
        HashSet<ProductParticleState> visitedStates = new HashSet<ProductParticleState>();

        // While the stack contains unprocessed states process the next state
        while (!stateStack.isEmpty()) {

            // Get the current state that should be processed
            ProductParticleState currentState = stateStack.pop();

            // Check if the current state was already seen
            if (!visitedStates.contains(currentState)) {

                // For each state contained in the current state check every transition
                for (int i = 0; i < currentState.getParticleStates().size(); i++) {
                    ParticleState containedStates = currentState.getParticleStates().get(i);

                    // Create a new state and for each outgoing transition of the current contained state
                    for (Iterator<Transition> it1 = containedStates.getOutTransitions().iterator(); it1.hasNext();) {
                        ParticleTransition transition = (ParticleTransition) it1.next();

                        // Get copy of the list of states contained by the current state
                        LinkedList<ParticleState> newContainedStates = new LinkedList<ParticleState>(currentState.getParticleStates());

                        // Add destination state of the current transition at the position of the transition source state and removes source state from the list
                        newContainedStates.remove(i);
                        newContainedStates.add(i, (ParticleState) transition.getDestinationState());

                        // Create new interleave state and check if it is already contained in the automaton then add it to the automaton
                        ProductParticleState nextState = new ProductParticleState(newContainedStates, newInterleaveAutomaton.getNextStateNumber());
                        nextState = newInterleaveAutomaton.containsState(nextState);
                        newInterleaveAutomaton.addState(nextState);

                        // Construct new particle transition and add elements and any patterns of the current transition to the new transition
                        ParticleTransition newParticleTransition = new ParticleTransition(currentState, nextState, transition.hasEpsilon());
                        newParticleTransition.addAllElements(transition.getElements());
                        newParticleTransition.addAllAnyPatterns(transition.getAnyPatterns());

                        // Add transition to the interleave automton and next state to the stack
                        newInterleaveAutomaton.addTransition(newParticleTransition);
                        stateStack.add(nextState);
                    }
                }

                // After processing all contained states the current state is completely processed.
                visitedStates.add(currentState);
            }
        }

        // Set new final states in order to do this all states of the new automaton are traversed
        for (Iterator<ParticleState> it = newInterleaveAutomaton.getStates().iterator(); it.hasNext();) {
            ProductParticleState productState = (ProductParticleState) it.next();

            // Variable to check whether the state is final or not
            boolean finalState = true;

            // For each state contained in interleave state check if it is a final state
            for (ParticleState containedState: productState.getParticleStates()) {
                // If the current contained state is not final in the automaton containing it
                if (!particleAutomatons.get(productState.getParticleStates().indexOf(containedState)).getFinalStates().contains(containedState)) {

                    // Set boolean variable finalState to false
                    finalState = false;
                }
            }

            // If the state is final add it to the set of final states of the current interleave automaton
            if (finalState) {
                newInterleaveAutomaton.addFinalState(productState);
            }
        }

        // Return complete interleave automaton
        return newInterleaveAutomaton;
    }

    /**
     * This method creates a new particle automaton for a given particle. In
     * order to do this the particle is traversed and for each particle
     * contained in the particle a new structure is constructed. The resulting
     * particle automaton may not be deterministic because epsilon transtions
     * can be present.
     *
     * @param particle Particle for which a new automaton is created, this
     * automaton accepts all element structures that are valid for the particle.
     * @param anyPatternOldSchemaMap Map which maps any patterns to schemata
     * containing them.
     * @return New particle automaton representing the specified particle.
     */
    public ParticleAutomaton buildParticleAutomaton(Particle particle, LinkedHashMap<AnyPattern, XSDSchema> anyPatternOldSchemaMap) {

        // Set any pattern old schema map
   		this.anyPatternOldSchemaMap = anyPatternOldSchemaMap;

        // Construct new start state
        ParticleState startState = new ParticleState(1);

        // Build new particle automaton with start state
        ParticleAutomaton newParticleAutomaton = new ParticleAutomaton(startState);

        // Build new final state and add it to particle automaton
        ParticleState finalState = new ParticleState(newParticleAutomaton.getNextStateNumber());
        newParticleAutomaton.addState(finalState);
        newParticleAutomaton.addFinalState(finalState);

        // Check what kind of particle the particle is.
        if (particle instanceof Element) {
            // If the particle is an element construct the appropriate automaton structure
            buildElementStructure(startState, (Element) particle, finalState, newParticleAutomaton);
        } else if (particle instanceof ElementRef) {
            // If the particle is an element reference the appropriate automaton structure for the referred element is constructed
            buildElementStructure(startState, ((ElementRef) particle).getElement(), finalState, newParticleAutomaton);
        } else if (particle instanceof AnyPattern) {
            // If the particle is an any pattern a structure for this any pattern is constructed
            buildAnyPatternStructure(startState, (AnyPattern) particle, finalState, newParticleAutomaton);
        } else if (particle instanceof AllPattern) {
            // If the particle is an all pattern the all pattern is resolved and a for the reolved particle a new structure is generated
            resolveAllPattern(startState, (AllPattern) particle, finalState, newParticleAutomaton);
        } else if (particle instanceof SequencePattern) {
            // If the particle is a sequence this a sequence structure is generated for the automaton
            buildSequencePatternStructure(startState, (SequencePattern) particle, finalState, newParticleAutomaton);
        } else if (particle instanceof ChoicePattern) {
            // If the particle is a choice this a choice structure is generated for the automaton
            buildChoicePatternStructure(startState, (ChoicePattern) particle, finalState, newParticleAutomaton);
        } else if (particle instanceof CountingPattern) {
            // Counting patterns are resolved too, epsilon transitions are allowed
            buildCountingPatternStructure(startState, (CountingPattern) particle, finalState, newParticleAutomaton);
        } else if (particle instanceof GroupRef) {
            // There is no structure for groups these are simply resolved
            resolveGroupReference(startState, (GroupRef) particle, finalState, newParticleAutomaton);
        }

        // If no particle is present automaton consist of two states and an epsilon transitiom
        if (particle == null) {

            // Construct new epsilon particle transition
            ParticleTransition newParticleTransition = new ParticleTransition(startState, finalState, true);

            // If current transition already exists in the specified automaton add epsilon to existing transition
            if (!newParticleAutomaton.addTransition(newParticleTransition)) {

                // Get existing particle transition and add epsilon
                ParticleTransition existingParticleTransition = (ParticleTransition) startState.getNextStateTransitions().get(finalState);
                existingParticleTransition.setEpsilon(true);
            }
        }

        // After building the automaton structure recursivly return the resulting automaton
        return newParticleAutomaton;
    }

    /**
     * This method creates a new particle automaton structure for a given
     * sequence pattern in the specified particle automaton. In order to do this
     * a structure for each particle contained in the sequence pattern is
     * constructed and is connected to the structure of the next and previous
     * particle structure of particles contained in the sequence before and
     * after the current particle.
     *
     * @param startState Start state of the constructed sequence structure.
     * @param sequencePattern Sequence pattern containing particles used to
     * build new sub structures.
     * @param finalState Final state of the sequence pattern structure.
     * @param particleAutomaton Automaton containing the new structure and
     * specified start and final state. These are not the final and start states
     * of this automaton, bt the start and final states of the constructed
     * structure.
     */
    private void buildSequencePatternStructure(ParticleState startState, SequencePattern sequencePattern, ParticleState finalState, ParticleAutomaton particleAutomaton) {
        // Initialize current start state and current final state, used to construct the sequence structure. The next start state will be the current final state and so on.
        ParticleState currentStartState = startState;
        ParticleState currentFinalState;

        // Travers all particles contained in the sequence and construct a new sequence structure connecting all contained structures
        for (Iterator<Particle> it = sequencePattern.getParticles().iterator(); it.hasNext();) {
            Particle currentParticle = it.next();

            // If no more particles are contained in the sequence the current particle structe ends at the specified final state
            if (!it.hasNext()) {
                currentFinalState = finalState;
            } else {

                // Build new current final state if more particles exists in the sequence pattern, this new state is the final state for the current particle structure.
                currentFinalState = new ParticleState(particleAutomaton.getNextStateNumber());
                particleAutomaton.addState(currentFinalState);
            }

            // Begin resolving the current currentParticle into a structure connecting the current start state with the current final state
            if (currentParticle instanceof Element) {
                // If the currentParticle is an element construct the appropriate automaton structure
                buildElementStructure(currentStartState, (Element) currentParticle, currentFinalState, particleAutomaton);
            } else if (currentParticle instanceof ElementRef) {
                // If the currentParticle is an element reference the appropriate automaton structure for the referred element is constructed
                buildElementStructure(currentStartState, ((ElementRef) currentParticle).getElement(), currentFinalState, particleAutomaton);
            } else if (currentParticle instanceof AnyPattern) {
                // If the currentParticle is an any pattern a structure for this any pattern is constructed
                buildAnyPatternStructure(currentStartState, (AnyPattern) currentParticle, currentFinalState, particleAutomaton);
            } else if (currentParticle instanceof SequencePattern) {
                // If the currentParticle is a sequence this a sequence structure is generated for the automaton
                buildSequencePatternStructure(currentStartState, (SequencePattern) currentParticle, currentFinalState, particleAutomaton);
            } else if (currentParticle instanceof ChoicePattern) {
                // If the currentParticle is a choice this a choice structure is generated for the automaton
                buildChoicePatternStructure(currentStartState, (ChoicePattern) currentParticle, currentFinalState, particleAutomaton);
            } else if (currentParticle instanceof CountingPattern) {
                // Counting patterns are resolved too, epsilon transitions are allowed
                buildCountingPatternStructure(currentStartState, (CountingPattern) currentParticle, currentFinalState, particleAutomaton);
            } else if (currentParticle instanceof GroupRef) {
                // There is no structure for groups these are simply resolved
                resolveGroupReference(currentStartState, (GroupRef) currentParticle, currentFinalState, particleAutomaton);
            }

            // The final state of the current particle state structure is the start state of the next particle structure for the neext particle contained in the sequence pattern.
            currentStartState = currentFinalState;
        }

        // If the sequence pattern is empty
        if (sequencePattern.getParticles().isEmpty()) {
            // Construct new epsilon particle transition
            ParticleTransition newParticleTransition = new ParticleTransition(startState, finalState, true);

            // If current transition already exists in the specified automaton add epsilon to existing transition
            if (!particleAutomaton.addTransition(newParticleTransition)) {
                // Get existing particle transition and add epsilon
                ParticleTransition existingParticleTransition = (ParticleTransition) currentStartState.getNextStateTransitions().get(finalState);
                existingParticleTransition.setEpsilon(true);
            }
        }
    }

    /**
     * Constuct the subset particle automaton for a specified particle
     * automaton. As the name suggest the deterministic finite state automaton
     * is constructed via subset construction from the given particle automaton,
     * which can be non deterministic. In order to construct the new subset
     * automaton each particle state of the old automaton is checked for
     * reachable states and subsets of particle states are generated.
     *
     * @param particleAutomaton Particle automaton which may be non
     * deterministic. If this is the case the subset automaton of this particle
     * automaton is deterministic.
     * @return New deterministic subset automaton for the specified particle
     * automaton.
     * @throws EmptySubsetParticleStateFieldException Exception which is thrown
     * if a subset state contains no particle states.
     */
    public SubsetParticleAutomaton buildSubsetParticleAutomaton(ParticleAutomaton particleAutomaton) throws EmptySubsetParticleStateFieldException {
        // Build new start state for the new subset particle automaton by getting the set of reachable states for the start state of the given automaton
        LinkedHashSet<ParticleState> reachableStates = getReachableStates(particleAutomaton.getStartState());
        SubsetParticleState startState = new SubsetParticleState(reachableStates, 1);

        // Build new subset automaton with new start state
        SubsetParticleAutomaton subsetParticleAutomaton = new SubsetParticleAutomaton(startState);

        // Stack of subset particle states is used to visited states which are not completed yet, i.e. lack outgoing transitions.
        Stack<SubsetParticleState> subsetParticleStateStack = new Stack<SubsetParticleState>();
        subsetParticleStateStack.push(startState);

        // Create set of already existing subset particle states and add the start state to the set.
        LinkedHashSet<SubsetParticleState> alreadyExistingSubsetParticleStates = new LinkedHashSet<SubsetParticleState>();
        alreadyExistingSubsetParticleStates.add(startState);

        // As long as the stack is not empty process next subset particle state
        while (!subsetParticleStateStack.isEmpty()) {
            SubsetParticleState currentSubsetParticleState = subsetParticleStateStack.pop();

            // Collect all elements annotated to transitions leaving particle states which are contained in the current subset particle state
            LinkedHashSet<Element> currentReadableElements = new LinkedHashSet<Element>();
            for (ParticleState particleState : currentSubsetParticleState.getParticleStates()) {

                // Check for each contained particle state all outgoing transitions and add elements to set
                for (Transition transition : particleState.getOutTransitions()) {
                    currentReadableElements.addAll(transition.getElements());
                }
            }

            // Construct for each readable element a new transition to a new subset state.
            for (Element element : currentReadableElements) {

                // Create set of particle states contained by the new state
                LinkedHashSet<ParticleState> newContainedParticleStates = new LinkedHashSet<ParticleState>();

                // For each particle state contained in the current particle state find all states reachable by reading a transition annotated with given element and a couple of epsilon transitions.
                for (ParticleState particleState : currentSubsetParticleState.getParticleStates()) {
                    for (Transition transition : particleState.getOutTransitions()) {

                        if (transition.getElements().contains(element)) {
                            newContainedParticleStates.addAll(getReachableStates((ParticleState) transition.getDestinationState()));
                        }
                    }
                }

                // Create next subset particle state and check if the state is already present in the automaton then add it to the automaton
                SubsetParticleState nextState = new SubsetParticleState(newContainedParticleStates, subsetParticleAutomaton.getNextStateNumber());
                nextState = subsetParticleAutomaton.containsState(nextState);
                subsetParticleAutomaton.addState(nextState);

                // If the next state does not already exists add it to the set and push it onto the stack
                if (!alreadyExistingSubsetParticleStates.contains(nextState)) {
                    alreadyExistingSubsetParticleStates.add(nextState);
                    subsetParticleStateStack.push(nextState);
                }

                // Create new transition from the current state to the next state
                ParticleTransition particleTransition = new ParticleTransition(currentSubsetParticleState, nextState, false);

                // If the transition already exist in the automaton get old transition
                if (currentSubsetParticleState.getNextStateTransitions().containsKey(nextState)) {
                    particleTransition = (ParticleTransition) currentSubsetParticleState.getNextStateTransitions().get(nextState);
                } else {

                    // If transition does not exists in the particle automaton add it to the automaton
                    subsetParticleAutomaton.addTransition(particleTransition);
                }

                // Add element to the transition
                particleTransition.addElement(element);
            }

            // Collect all any patterns annotated to transitions leaving particle states which are contained in the current subset particle state
            LinkedHashSet<AnyPattern> anyPatterns = new LinkedHashSet<AnyPattern>();
            for (ParticleState particleState : currentSubsetParticleState.getParticleStates()) {

                // Check for each contained particle state all outgoing transitions and add any patterns to set
                for (Transition transition : particleState.getOutTransitions()) {
                    anyPatterns.addAll(((ParticleTransition) transition).getAnyPatterns());
                }
            }

            // Construct for each readable any pattern a new transition to a new subset state.
            for (AnyPattern anyPattern : anyPatterns) {

                // Create set of particle states contained by the new state
                LinkedHashSet<ParticleState> newContainedParticleStates = new LinkedHashSet<ParticleState>();

                // For each particle state contained in the current particle state find all states reachable by reading a transition annotated with given any pattern and a couple of epsilon transitions.
                for (ParticleState particleState : currentSubsetParticleState.getParticleStates()) {
                    for (Transition transition : particleState.getOutTransitions()) {

                        if (((ParticleTransition) transition).getAnyPatterns().contains(anyPattern)) {
                            newContainedParticleStates.addAll(getReachableStates((ParticleState) transition.getDestinationState()));
                        }
                    }
                }

                // Create next subset particle state and check if the state is already present in the automaton then add it to the automaton
                SubsetParticleState nextState = new SubsetParticleState(newContainedParticleStates, subsetParticleAutomaton.getNextStateNumber());
                nextState = subsetParticleAutomaton.containsState(nextState);
                subsetParticleAutomaton.addState(nextState);

                // If the next state does not already exists add it to the set and push it onto the stack
                if (!alreadyExistingSubsetParticleStates.contains(nextState)) {
                    alreadyExistingSubsetParticleStates.add(nextState);
                    subsetParticleStateStack.push(nextState);
                }

                // Create new transition from the current state to the next state
                ParticleTransition particleTransition = new ParticleTransition(currentSubsetParticleState, nextState, false);

                // If the transition already exist in the automaton get old transition
                if (currentSubsetParticleState.getNextStateTransitions().containsKey(nextState)) {
                    particleTransition = (ParticleTransition) currentSubsetParticleState.getNextStateTransitions().get(nextState);
                } else {

                    // If transition does not exists in the particle automaton add it to the automaton
                    subsetParticleAutomaton.addTransition(particleTransition);
                }

                // Add element to the transition
                particleTransition.addAnyPattern(anyPattern);
            }
        }

        // Check for each state of the subset particle automaton if it contains a final states of the particle automaton
        for (Iterator<ParticleState> it = subsetParticleAutomaton.getStates().iterator(); it.hasNext();) {
            SubsetParticleState subsetParticleState = (SubsetParticleState) it.next();

            // Check for all final states of the particle automaton if they appear in the set of the current subset particle state
            for (Iterator<ParticleState> it1 = particleAutomaton.getFinalStates().iterator(); it1.hasNext();) {
                ParticleState finalState = it1.next();

                // If the subset state contains a final state it is set as final state of the subset automaton
                if (subsetParticleState.getParticleStates().contains(finalState)) {
                    subsetParticleAutomaton.addFinalState(subsetParticleState);
                }
            }
        }

        // Return the new constructed subset particle automaton
        return subsetParticleAutomaton;
    }

    /**
     * This method constructs a substitution particle automaton. The resulting
     * automaton is a non deterministic automaton whose particle violates the
     * EDC constraint of XML XSDSchema. Basically the given minuend particle is used
     * to generate a new particle,represented by the substitution automaton, by
     * replacing elements of this particle with new elements if they share the
     * same name as an element of the subtrahend particle. So that each path to
     * an accepting final state contains one new element and only one.
     *
     * @param minuendParticle Particle used to construct the particle automaton.
     * @param subtrahendParticle Particle used to find element names.
     * @param particleDifferenceGenerator The particle difference generator is
     * used to create the new element.
     * @param elementNameTypeSymbolTableRefMap Map mapping element names to
     * type symbol table references, used to give the new elements a type.
     * @return Particle automaton which is not deterministic. Constructed as
     * described above.
     * @throws NonDeterministicFiniteAutomataException Exception which is thrown
     * if a particle automaton is no deterministic finite automaton.
     * @throws NoUniqueStateNumbersException Exception which is thrown if two
     * states with the same state number exist in the specified automaton.
     * @throws NoDestinationStateFoundException Exception which is thrown if the
     * method finds no destination state in the accepting automaton.
     * @throws EmptySubsetParticleStateFieldException Exception which is thrown
     * if a subset state contains no particle states.
     * @throws UniqueParticleAttributionViolationException Exception which is
     * thrown a particle which was used to construct a particle automaton
     * violates the UPA constraint.
     * @throws EmptyProductParticleStateFieldException Exception which is thrown
     * if a product state contains no particle states.
     */
    public ParticleAutomaton buildSubstitutionParticleAutomaton(Particle minuendParticle, Particle subtrahendParticle, ParticleDifferenceGenerator particleDifferenceGenerator, LinkedHashMap<String, SymbolTableRef<Type>> elementNameTypeSymbolTableRefMap) throws NonDeterministicFiniteAutomataException, NoUniqueStateNumbersException, NoDestinationStateFoundException, EmptySubsetParticleStateFieldException, UniqueParticleAttributionViolationException, EmptyProductParticleStateFieldException {

        // Get the names of all subtrahend particles.
        LinkedHashSet<String> elementNames = getAllElementNames(subtrahendParticle);

        // Build automaton for the given particle that does not accept
        ParticleAutomaton nonAcceptingAutomaton = buildSubsetParticleAutomaton(buildProductAutomatonDifference(buildSubsetParticleAutomaton(buildParticleAutomaton(minuendParticle, particleDifferenceGenerator.getAnyPatternOldSchemaMap())), buildSubsetParticleAutomaton(buildParticleAutomaton(new SequencePattern(), particleDifferenceGenerator.getAnyPatternOldSchemaMap())), particleDifferenceGenerator));
        nonAcceptingAutomaton.clearFinalStates();

        // Build the same automaton but with accepting final states
        ParticleAutomaton acceptingAutomaton = buildSubsetParticleAutomaton(buildProductAutomatonDifference(buildSubsetParticleAutomaton(buildParticleAutomaton(minuendParticle, particleDifferenceGenerator.getAnyPatternOldSchemaMap())), buildSubsetParticleAutomaton(buildParticleAutomaton(new SequencePattern(), particleDifferenceGenerator.getAnyPatternOldSchemaMap())), particleDifferenceGenerator));

        for (ParticleState particleState: acceptingAutomaton.getStates()) {
            particleState.setStateNumber(nonAcceptingAutomaton.getNextStateNumber() + particleState.getStateNumber());
        }


        // Add accepting automaton to non accepting automaton. (The unique numbers assigned to states of the automatons are not unquie anymore because both automatons contain states with same numbers)
        for (ParticleState state: acceptingAutomaton.getStates()) {
            nonAcceptingAutomaton.addState(state);

            // Final states of the accepting automaton are now final states of the non accepting automaton
            if (acceptingAutomaton.getFinalStates().contains(state)) {
                nonAcceptingAutomaton.addFinalState(state);
            }
        }

        // For all states of the former non accepting automaton add if necessary transitions to them connecting the former non accepting automaton and the accepting automaton
        for (ParticleState currentState: nonAcceptingAutomaton.getStates()) {
            // If the current state is not a state of the accepting automaton, the non accepting automaton now shares states with the accepting automaton, check if new transition should be added
            if (!acceptingAutomaton.getStates().contains(currentState)) {

                // Check each transition for elements possible contained in the element name set
                LinkedHashSet<Transition> outTransitions = new LinkedHashSet<Transition>(currentState.getOutTransitions());

                for (Transition transition: outTransitions) {
                    // Check for each transition all element names contained in the generated set
                    for (String elementName: elementNames) {
                        // Get set of all elements annotated to the transition with the specified name
                        LinkedHashSet<Element> transitionElements = transition.getNameElementMap().get(elementName);

                        // If the transition contains only one element with this name
                        if (transitionElements != null && transitionElements.size() == 1) {

                            // Initialize destination state variable
                            ParticleState destinationState = null;

                            // Check for each state of the accepting automaton if it should be the destination state
                            for (ParticleState state: acceptingAutomaton.getStates()) {
                                // Check if current state and the state of the accepting automaton share the same number (States with same number should be equivalent)
                                if (state.getStateNumber() == ((ParticleState)transition.getDestinationState()).getStateNumber() + nonAcceptingAutomaton.getNextStateNumber()) {

                                    // Check if destination state was not already set
                                    if (destinationState == null) {

                                        // Set new destination state to the state in the accepting automaton with the same number as the current state
                                        destinationState = state;
                                    } else {
                                        // Two states with same state number in the accepting automaton
                                        throw new NoUniqueStateNumbersException(acceptingAutomaton, "buildSubstitutionParticleAutomaton");
                                    }
                                }
                            }

                            // If a destination state was found
                            if (destinationState != null) {

                                // Generate new particle transition connecting the current state with the destination state in the accepting automaton
                                ParticleTransition particleTransition = new ParticleTransition(currentState, destinationState, false);

                                // Check if transition already exists in the product automaton
                                if (currentState.getNextStateTransitions().containsKey(destinationState)) {

                                    // Use existing particle transition instead of new transtition
                                    particleTransition = (ParticleTransition) currentState.getNextStateTransitions().get(destinationState);
                                } else {

                                    // If transition does not exist in the current product automaton add new transition to the automaton
                                    nonAcceptingAutomaton.addTransition(particleTransition);
                                }

                                // Create new element which is a copy of the transition element with a new type
                                Element newElement = particleDifferenceGenerator.generateNewElement(transitionElements.iterator().next(), null, elementNameTypeSymbolTableRefMap.get(elementName));
                                particleTransition.addElement(newElement);

                            } else {
                                // Destination state could not be found
                                throw new NoDestinationStateFoundException(acceptingAutomaton, elementName);
                            }
                        } else if (transitionElements != null && transitionElements.size() > 1) {

                            // A transition has more than one element with the same name annotated to itself. So the automaton is not deterministic.
                            throw new NonDeterministicFiniteAutomataException(nonAcceptingAutomaton, "buildSubstitutionParticleAutomaton");
                        }
                    }
                }
            }
        }

        // The non accepting automaton is now connected with the accepting automaton
        return nonAcceptingAutomaton;
    }

    /**
     * This method creates for a given set of states and a particle a new
     * state in the specified product automaton and a transition annotated with
     * the particle connecting the new state with the given current state. If
     * state and/or transition are already present in the automaton no new
     * state and/or transition are generated and the particle is added to the
     * existing transition.
     *
     * @param nextContainedStates States that will be contained by the next
     * state.
     * @param newParticle Particle which will be annotated to the transition
     * connecting the current state with the next state.
     * @param currentState State that is currently processed.
     * @param newAutomaton Automaton that will containing the new state and
     * transition, if generated.
     * @param stateStack Stack containing all states that are not processed yet.
     */
    private void createNextProductStateAndTransition(LinkedList<ParticleState> nextContainedStates, Particle newParticle, ProductParticleState currentState, ProductParticleAutomaton newAutomaton, Stack<ProductParticleState> stateStack) throws EmptyProductParticleStateFieldException {
        // Check if new particle exists
        if (newParticle != null) {

            // Generate new state with the nextContainedStates set and a new number
            ProductParticleState nextState = new ProductParticleState(nextContainedStates, newAutomaton.getNextStateNumber());

            // Check if the next state is not already present in the automaton
            if (nextState == newAutomaton.containsState(nextState)) {

                // Add new state to the product automaton and to the stack of unprocessed states
                newAutomaton.addState(nextState);
                stateStack.push(nextState);
            } else {

                // Get equivalent existing state
                nextState = newAutomaton.containsState(nextState);
            }

            // Create new transition connecting the current state and the next state
            ParticleTransition particleTransition = new ParticleTransition(currentState, nextState, false);

            // Check if transition already exists in the product automaton
            if (currentState.getNextStateTransitions().containsKey(nextState)) {

                // If transition exist get the existing transition
                particleTransition = (ParticleTransition) currentState.getNextStateTransitions().get(nextState);
            } else {

                // If transition does not exist in the current product automaton add new transition to the automaton
                newAutomaton.addTransition(particleTransition);
            }

            // Check if new particle is an element or an any pattern
            if (newParticle instanceof Element) {

                // Add element to transition
                particleTransition.addElement((Element) newParticle);
            } else if (newParticle instanceof AnyPattern) {

                // Use variable to check any pattern
                boolean newAny = true;

                // Check if same any pattern already exists for the given transition
                for (AnyPattern anyPattern: particleTransition.getAnyPatterns()) {
                    if ((anyPattern.getNamespace() == null && ((AnyPattern) newParticle).getNamespace() == null)||(anyPattern.getNamespace() != null && ((AnyPattern) newParticle).getNamespace() != null && anyPattern.getNamespace().equals(((AnyPattern) newParticle).getNamespace()))){
                        if (anyPattern.getProcessContentsInstruction() == ((AnyPattern) newParticle).getProcessContentsInstruction()) {
                            newAny = false;
                        }
                    }
                }

                // Add any pattern to transition if it is new
                if (newAny) {
                    particleTransition.addAnyPattern((AnyPattern) newParticle);
                }
            }
        }
    }

    /**
     * Get all element names used in the specified particle. The element names
     * are stored in a set so that element names which appeare more than once
     * in the particle are only contained once in the element name set.
     *
     * @param particle Particle which may contain element names found by this
     * method.
     * @return A set of all element names contained in the specified particle.
     */
    private LinkedHashSet<String> getAllElementNames(Particle particle) {

        // Initialize set which will contain all element names used in the specified partilce
        LinkedHashSet<String> allElementNames = new LinkedHashSet<String>();

        // Create particle stack which is used to traverse the particle
        Stack<Particle> particleStack = new Stack<Particle>();
        particleStack.add(particle);

        // As long as the particle stack contains unprocessed particles process the next particle
        while (!particleStack.isEmpty()) {

            // Get new particle from particle stack
            Particle currentParticle = particleStack.pop();

            // Check for each particle what kind of particle it is
            if (currentParticle instanceof Element) {
                // If the current particle is an element add element name to the element name set
                allElementNames.add(getInstanceName((Element) currentParticle));
            } else if (currentParticle instanceof ElementRef) {
                // If the current particle is an element reference add referred element name to the element name set
                allElementNames.add(getInstanceName(((ElementRef) currentParticle).getElement()));
            } else if (currentParticle instanceof ParticleContainer) {
                // If current particle is a particle container add contained particles to stack
                for (Particle containedParticle: ((ParticleContainer) currentParticle).getParticles()) {
                    // Add contained particle to particle stack
                    particleStack.add(containedParticle);
                }
            } else if (currentParticle instanceof GroupRef) {
                // If current particle is a group reference get group and contained particle container and add the particles contained in the particle container to the stack
                for (Particle containedParticle: ((GroupRef) currentParticle).getGroup().getParticleContainer().getParticles()) {
                    // Add contained particle to particle stack
                    particleStack.add(containedParticle);
                }
            }
        }

        // Return set of all element names used in the current particle
        return allElementNames;
    }

    /**
     * This method gets a set containing all any patterns annotated to outgoing
     * transitions of states contained in the specified current state.
     *
     * @param currentState Product state which contains substates.
     * @return Set of any patterns which are annotated to outgoing transitions
     * of the states contained by the specified product state.
     */
    private LinkedHashSet<AnyPattern> getAnyPatterns(ProductParticleState currentState) {
        // Initialize set which will contain all any patterns annotated to transitions leaving substates of the current state
        LinkedHashSet<AnyPattern> anyPatterns = new LinkedHashSet<AnyPattern>();

        // For each state contained in the current state check the outgoing transitions
        for (ParticleState particleState : currentState.getParticleStates()) {

            // Check if particle state is not null
            if (particleState != null) {

                // Add for each transition leaving the current substate all any patterns to the set
                for (Transition transition : particleState.getOutTransitions()) {
                    anyPatterns.addAll(((ParticleTransition) transition).getAnyPatterns());
                }
            }
        }
        return anyPatterns;
    }

    /**
     * This method gets a set containing all element names of elements annotated
     * to outgoing transitions of states contained in the specified current
     * product state.
     *
     * @param currentState Product state which contains a list of other states.
     * @return Set of element names. These names belong to elements annotated to
     * outgoing transitions of states contained by the specified current state.
     */
    private LinkedHashSet<String> getElementNames(ProductParticleState currentState) {

        // Initialize set of element names which are annotated to transitions leaving the states contained in the current product state
        LinkedHashSet<String> elementNames = new LinkedHashSet<String>();

        // In order to get all element names for each contained state all transitions of this state have to be checked
        for (ParticleState containedState: currentState.getParticleStates()) {
            // Check if contained state is not null
            if (containedState != null) {

                // For each transition all element names annotated to this transition have to be added to the element name set
                for (Iterator<Transition> it2 = containedState.getOutTransitions().iterator(); it2.hasNext();) {
                    ParticleTransition particleTransition = (ParticleTransition) it2.next();
                    elementNames.addAll(particleTransition.getNameElementMap().keySet());
                }
            }
        }
        return elementNames;
    }

    /**
     * Add new final states to the new product particle automaton. These are
     * product states that a contain minuend state which is a final states and
     * a subtrahend state that is not a final state.
     *
     * @param newProductParticleAutomaton Automaton for which the new final
     * states are generated.
     * @param minuendAutomaton Automaton containing the minuned states which
     * are now contained in the product states. Stores final state information.
     * @param subtrahendAutomaton Automaton containing the subtrahend states
     * which are now contained in the product states. Stores final state
     * information.
     */
    private void addDifferenceFinalStates(ProductParticleAutomaton newProductParticleAutomaton, ParticleAutomaton minuendAutomaton, ParticleAutomaton subtrahendAutomaton) {

        // Check for each state of the product automaton if it is a final state
        for (Iterator<ParticleState> it = newProductParticleAutomaton.getStates().iterator(); it.hasNext();) {
            ProductParticleState productParticleState = (ProductParticleState) it.next();

            // If the minuned state of the current product state is a final state and the subtrahend state is not a final state
            if (minuendAutomaton.getFinalStates().contains(productParticleState.getParticleStates().getFirst()) && !subtrahendAutomaton.getFinalStates().contains(productParticleState.getParticleStates().getLast())) {

                // Add product state to the set of final states
                newProductParticleAutomaton.addFinalState(productParticleState);
            }
        }
    }

    /**
     * Used to create a list of sequence patterns representing all sequence
     * patterns which match the all pattern for which the list is generated. In
     * order to create the list the particles contained in the all pattern are
     * permutated using a divide and conquere approach. The resulting list
     * can be used by a choice pattern which is then equivalent to the all
     * pattern.
     *
     * @param particleNumber Current number of the permutated particle.
     * @param particleArray Array containing all particles of an all pattern.
     * @return List of sequence patterns which can be used to by a choice
     * pattern.
     */
    private LinkedList<Particle> permutateAllPattern(int particleNumber, Particle[] particleArray) {

        // Linked list of particles which will be contained by a choice pattern
        LinkedList<Particle> choiceParticles = new LinkedList<Particle>();

        // If the particle number is zero
        if (particleNumber == 0) {

            // Create new sequence pattern and add all particles of the current particle array to the sequence pattern
            SequencePattern sequencePattern = new SequencePattern();
            for (Particle particle : particleArray) {
                sequencePattern.addParticle(particle);
            }

            // Add sequence pattern to particle set for the choice pattern
            choiceParticles.add(sequencePattern);
        } else {

            // Calculate permutations for fraction problems
            for (int i = particleNumber; 0 <= i; i--) {

                // Switch particle at position i with particle at position particleNumber
                Particle particle = particleArray[i];
                particleArray[i] = particleArray[particleNumber];
                particleArray[particleNumber] = particle;

                // Generate permutation for a smaller particle number and add results to the particle list
                choiceParticles.addAll(permutateAllPattern(particleNumber - 1, particleArray));

                // Switch particle at position i with particle at position particleNumber
                particle = particleArray[i];
                particleArray[i] = particleArray[particleNumber];
                particleArray[particleNumber] = particle;
            }
        }

        // Return list of sequence patterns, which can be used by a choice particle
        return choiceParticles;
    }

    /**
     * Processes an any pattern. To process an any pattern transitions of
     * both particle states contained in the specified product state are checked
     * for any elements including the specified any pattern. If such an any
     * pattern exist the destination of the transition is stored. If possible
     * new product states partially containing these list of destination states
     * are generated and a transitions connecting these states with the current
     * state are build annotated with new generated any patterns. Instead of
     * only one new state to states are generated because any patterns can
     * contain different namespaces and so a new state for an minuend any
     * pattern without the namespaces of the subtrahend any pattern is generated
     * and another new state for an any pattern containing the removed
     * namespaces is build.
     *
     * @param currentState State which is currently processed by the calling
     * method.
     * @param anyPattern Any pattern which is currently checked.
     * @param newProductParticleAutomaton Product automaton which will contain
     * the new states and transitions.
     * @param stateStack Stack containing all unprocessed product states of the
     * current automaton.
     * @param particleDifferenceGenerator Used to construct new particles and
     * to identify connections between particles.
     */
    private void processAnyPattern(ProductParticleState currentState, AnyPattern anyPattern, ProductParticleAutomaton newProductParticleAutomaton, ParticleAutomaton minuendAutomaton, ParticleAutomaton subtrahendAutomaton, Stack<ProductParticleState> stateStack, ParticleDifferenceGenerator particleDifferenceGenerator) throws NonDeterministicFiniteAutomataException, UniqueParticleAttributionViolationException, EmptyProductParticleStateFieldException {

        // Get minuned state of the current product state
        ParticleState minuendState = currentState.getParticleStates().getFirst();

        // Initialize variable for next minuned state and the minuend any pattern
        ParticleState nextMinuendState = null;
        AnyPattern minunedAnyPattern = null;

        // Check all outgoing transitions of the minuend state
        for (Transition transition : minuendState.getOutTransitions()) {

            // Check for each any pattern of the current transition if the intersection with the specified any pattern is not empty
            for (AnyPattern possibleAnyPattern: ((ParticleTransition) transition).getAnyPatterns()) {
                // Check if the intersection of the current any pattern with the possible any patterns is not empty
                LinkedHashSet<AnyPattern> anyPatterns = new LinkedHashSet<AnyPattern>();
                anyPatterns.add(anyPattern);
                anyPatterns.add(possibleAnyPattern);

                if (particleDifferenceGenerator.areIntersectableAnyPatterns(anyPatterns)) {

                    // Check if next minuend state is still empty and if it is set current destination state as new next minuned state
                    if (nextMinuendState == null) {
                        nextMinuendState = (ParticleState) transition.getDestinationState();
                    } else {

                        // A second state can be reached, so the given minuend automaton is not deterministic
                        throw new NonDeterministicFiniteAutomataException(minuendAutomaton, "processAnyPattern");
                    }

                    // Check if next minuend any pattern is still empty, if it is, set minuned element to the possible any pattern
                    if (minunedAnyPattern == null) {
                        minunedAnyPattern = possibleAnyPattern;
                    } else {

                        // If an any pattern exist which covers all elements of another any pattern the UPA is violated
                        throw new UniqueParticleAttributionViolationException(minuendAutomaton, "processElementName");
                    }
                }
            }
        }

        // Get subtrahend state of the current product state
        ParticleState subtrahendState = currentState.getParticleStates().getLast();

        // Initialize variable for next subtrahend state and the subtrahend any pattern
        ParticleState nextSubtrahendState = null;
        AnyPattern subtrahendAnyPattern = null;

        // Check all outgoing transitions of the subtrahend state
        for (Transition transition : subtrahendState.getOutTransitions()) {

            // Check for each any pattern of the current transition if the intersection with the given any pattern is not empty
            for (AnyPattern possibleAnyPattern: ((ParticleTransition) transition).getAnyPatterns()) {
                // Check if the intersection of the current any pattern with the possible any patterns is not empty
                LinkedHashSet<AnyPattern> anyPatterns = new LinkedHashSet<AnyPattern>();
                anyPatterns.add(anyPattern);
                anyPatterns.add(possibleAnyPattern);

                if (particleDifferenceGenerator.areIntersectableAnyPatterns(anyPatterns)) {

                    // Check if next subtrahend state is still empty and if it is set current destination state as new next subtrahend state
                    if (nextSubtrahendState == null) {
                        nextSubtrahendState = (ParticleState) transition.getDestinationState();
                    } else {

                        // A second state can be reached, so the given subtrahend automaton is not deterministic
                        throw new NonDeterministicFiniteAutomataException(subtrahendAutomaton, "processAnyPattern");
                    }

                    // Check if next subtrahend any pattern is still empty, if it is, set subtrahend any pattern to the possible any pattern
                    if (subtrahendAnyPattern == null) {
                        subtrahendAnyPattern = possibleAnyPattern;
                    } else {

                        // If an any pattern exist which covers all elements of another any pattern the UPA is violated
                        throw new UniqueParticleAttributionViolationException(subtrahendAutomaton, "processElementName");
                    }
                }
            }
        }

        // Check if the minuend state is not a sink state
        if (nextMinuendState != null) {

            // Generate new any pattern by removing the subtrahend namespaces from the minuned namespaces
            AnyPattern newAnyPattern = particleDifferenceGenerator.generateNewDifferenceAnyPattern(minunedAnyPattern, subtrahendAnyPattern);

            // Check if the new any pattern is not null (Difference of minuend and subtrahend any pattern is not empty)
            if (newAnyPattern != null) {

                // Create new state set containing the next minuned and subtrahend state
                LinkedList<ParticleState> nextContainedStates = new LinkedList<ParticleState>();
                nextContainedStates.add(nextMinuendState);

                // If the difference of both any patterns is not empty the next state of the subtrahend automaton is the sink state
                nextContainedStates.add(null);

                // Create new state and transtion with particle
                createNextProductStateAndTransition(nextContainedStates, newAnyPattern, currentState, newProductParticleAutomaton, stateStack);
            }

            // Generate new any pattern contsining the the removed namespaces
            AnyPattern newOtherAnyPattern = particleDifferenceGenerator.generateNewIntersectionAnyPattern(minunedAnyPattern, subtrahendAnyPattern);

            // Check if the new any pattern is not null
            if (newOtherAnyPattern != null) {

                // Create new state set containing the next minuned and subtrahend state
                LinkedList<ParticleState> nextContainedStates = new LinkedList<ParticleState>();
                nextContainedStates.add(nextMinuendState);

                // For the namespaces contained in the new any pattern the difference is empty so the next state is the next subtrahend state
                nextContainedStates.add(nextSubtrahendState);

                // Create new state and transtion with particle
                createNextProductStateAndTransition(nextContainedStates, newOtherAnyPattern, currentState, newProductParticleAutomaton, stateStack);
            }
        }
    }

    /**
     * Processes one element name. To process an element name all transitions of
     * both particle states contained in the specified product state are checked
     * for elements with the same name. If such elements exist the destination
     * of the transition is stored. If possible a new product state containing
     * the list of destination states is generated and a transition connecting
     * this state with the current state is build annotated with an element
     * generated from the elements found.
     *
     * @param currentState State which is currently processed by the calling
     * method.
     * @param elementName Name of the elements currently looked for.
     * @param newProductParticleAutomaton Product automaton which will contain
     * the new state and transition.
     * @param stateStack Stack containing all unprocessed product states of the
     * current automaton.
     * @param particleDifferenceGenerator Used to construct new particles and
     * to identify connections between particles.
     */
    private void processElementName(ProductParticleState currentState, String elementName, ProductParticleAutomaton newProductParticleAutomaton, ParticleAutomaton minuendAutomaton, ParticleAutomaton subtrahendAutomaton, Stack<ProductParticleState> stateStack, ParticleDifferenceGenerator particleDifferenceGenerator) throws NonDeterministicFiniteAutomataException, UniqueParticleAttributionViolationException, EmptyProductParticleStateFieldException {

        // Get minuned state of the current product state
        ParticleState minuendState = currentState.getParticleStates().getFirst();

        // Initialize variable for next minuned state and the minuend element
        ParticleState nextMinuendState = null;
        Element minunedElement = null;

        // Check all outgoing transitions of the minuend state
        for (Transition transition : minuendState.getOutTransitions()) {

            // Check if current transition is annotated with one or more elements with the same element name as the specified name
            if (transition.getNameElementMap().containsKey(elementName)) {

                // Check if next minuend state is still empty (There are no states in the product automaton where the minuned automaton is in a sink state)
                if (nextMinuendState == null) {

                    // Current destination state is new next minuned state
                    nextMinuendState = (ParticleState) transition.getDestinationState();
                } else {
                    // A second state can be reached, so the given minued automaton is not deterministic
                    throw new NonDeterministicFiniteAutomataException(minuendAutomaton, "processElementName");
                }

                // Check if the current transition is annotated with one element with the specified name
                if (transition.getNameElementMap().get(elementName).size() == 1) {

                    // Set minuned element to the current transition element
                    minunedElement = transition.getNameElementMap().get(elementName).iterator().next();
                } else {
                    // If more than one element with the same name exist the UPA is violated
                    throw new UniqueParticleAttributionViolationException(minuendAutomaton, "processElementName");
                }
            }
        }

        // Get subtrahend state of the current product state
        ParticleState subtrahendState = currentState.getParticleStates().getLast();

        // Initialize variable for next subtrahend state and the subtrahend element
        ParticleState nextSubtrahendState = null;
        Element subtrahendElement = null;

        // Check if subtrahend state is not null
        if (subtrahendState != null) {

            // Check all outgoing transitions of the subtrahend state
            for (Transition transition : subtrahendState.getOutTransitions()) {

                // Check if current transition is annotated with one or more elements with the same element name as the specified name
                if (transition.getNameElementMap().containsKey(elementName)) {

                    // Check if next subtrahned state is still empty
                    if (nextSubtrahendState == null) {

                        // Current destination state is new next subtrahend state
                        nextSubtrahendState = (ParticleState) transition.getDestinationState();
                    } else {
                        // A second state can be reached, so the given subtrahend automaton is not deterministic
                        throw new NonDeterministicFiniteAutomataException(subtrahendAutomaton, "processElementName");
                    }

                    // Check if the current transition is annotated with one element with the specified name
                    if (transition.getNameElementMap().get(elementName).size() == 1) {

                        // Set subtrahend element to the current transition element
                        subtrahendElement = transition.getNameElementMap().get(elementName).iterator().next();
                    } else {
                        // If more than one element with the same name exist the UPA is violated
                        throw new UniqueParticleAttributionViolationException(subtrahendAutomaton, "processElementName");
                    }
                }

                //Check if the current element name is included in any any pattern annotated to the current transition
                if (particleDifferenceGenerator.isIncluded(elementName, ((ParticleTransition) transition).getAnyPatterns())) {

                    // Check if the next subtrahend state is already filled
                    if (nextSubtrahendState != null) {

                        // A second state can be reached, so the given subtrahend automaton is not deterministic
                        throw new NonDeterministicFiniteAutomataException(subtrahendAutomaton, "processElementName");
                    } else {

                        // Current destination state is new next subtrahend state
                        nextSubtrahendState = (ParticleState) transition.getDestinationState();
                    }

                    // Check if the subtrahend element is already filled
                    if (subtrahendElement != null) {

                        // The UPA is violated as there is an any pattern and an element which is contained in the any pattern both leaving the current state
                        throw new UniqueParticleAttributionViolationException(subtrahendAutomaton, "processElementName");
                    } else {

                        // Get contained element if possible
                        Element containedElement = particleDifferenceGenerator.getIncluded(elementName, ((ParticleTransition) transition).getAnyPatterns());

                        if (containedElement != null) {

                            // Subtrahend element is the element contained in the any pattern
                            subtrahendElement = containedElement;

                        }
                    }
                }
            }
        }

        // Check if the next minuned state is not empty
        if (nextMinuendState != null) {

            // Create new state set containing the next minuned and subtrahend state
            LinkedList<ParticleState> nextContainedStates = new LinkedList<ParticleState>();
            nextContainedStates.add(nextMinuendState);

            // If the difference of the minuend element and the subtrahned element is empty or if an any pattern contains the element thus the difference is empty
            if (particleDifferenceGenerator.isDifferenceEmpty(minunedElement, subtrahendElement) || subtrahendElement == null) {

                // Add next subtrahend state to the contained states of the next product state
                nextContainedStates.add(nextSubtrahendState);
            } else {

                // If the difference is not empty the next state of the subtrahend automaton is the sink state
                nextContainedStates.add(null);
            }

            // Generate new element for the minuend element
            Element newMinunedElement = particleDifferenceGenerator.generateNewElement(minunedElement, null, particleDifferenceGenerator.getElementOldTypeMap().get(getInstanceName(minunedElement)));

            // Create new state and transtion with particle
            createNextProductStateAndTransition(nextContainedStates, newMinunedElement, currentState, newProductParticleAutomaton, stateStack);
        }
    }

    /**
     * For a specified particle state all reachable particle states are
     * computed. So a particle state which has outgoing epsilon transitions can
     * reach all states connected with these transitions. Method is used by the
     * subset construction to get all reachable states for a state of the used
     * particle automaton.
     *
     * @param particleState State of a particle automaton.
     * @return A set of particle states each state can be reached from the
     * specified particle state by following epsilon transitios.
     */
    private LinkedHashSet<ParticleState> getReachableStates(ParticleState particleState) {

        // Set containing all already reached particle states initially containing the specified state, which is reachable by itself
        LinkedHashSet<ParticleState> alreadyReachedParticleStates = new LinkedHashSet<ParticleState>();
        alreadyReachedParticleStates.add(particleState);

        // Create new stack for particle states and add specified particle state to stack
        Stack<ParticleState> particleStateStack = new Stack<ParticleState>();
        particleStateStack.add(particleState);

        // As long as the stack is not empty process next particle state
        while (!particleStateStack.isEmpty()) {

            // Get current particle state from stack
            ParticleState currentParticleState = particleStateStack.pop();

            // Check all outgoing transitions of the current particle state
            for (Iterator<Transition> it = currentParticleState.getOutTransitions().iterator(); it.hasNext();) {
                ParticleTransition particleTransition = (ParticleTransition) it.next();

                // Get destination state of the current particle transition
                ParticleState reachedParticleState = (ParticleState) particleTransition.getDestinationState();

                // If the reached state is not in the set of already reached particle states and the current transition is annotated with an epsilon add state to set
                if (particleTransition.hasEpsilon() && !alreadyReachedParticleStates.contains(reachedParticleState)) {
                    alreadyReachedParticleStates.add(reachedParticleState);

                    // Add reached state to particle state stack in order to find particle states which can be reached from this state
                    particleStateStack.add(reachedParticleState);
                }
            }
        }

        // Return complete set of all particle states reachable from the specified particle state
        return alreadyReachedParticleStates;
    }

    /**
     * Resolves the specified all pattern into an equivalen choice pattern which
     * can be used to build a new part of particle automaton structure for the
     * current given particle automaton.
     *
     * @param currentStartState Each constructed structure begins in this state.
     * @param allPattern All pattern which is resolved into a choice pattern 
     * which then is transformed into an automaton structure.
     * @param currentFinalState Each constructed structure ends in this state.
     * @param particleAutomaton Particle automaton containing the current start
     * and final state. In this automaton the new structure is constructed.
     */
    private void resolveAllPattern(ParticleState currentStartState, AllPattern allPattern, ParticleState currentFinalState, ParticleAutomaton particleAutomaton) {

        // Create new choice pattern
        ChoicePattern choicePattern = new ChoicePattern();

        // Get particles contained in the current all pattern
        Particle[] containedParticle = (Particle[]) allPattern.getParticles().toArray();

        // Add all possible particle sequences to the choice pattern
        choicePattern.setParticles(permutateAllPattern(allPattern.getParticles().size() - 1, containedParticle));

        // Build new choice structure for the resolved all pattern
        buildChoicePatternStructure(currentStartState, choicePattern, currentFinalState, particleAutomaton);
    }

    /**
     * Resolves a group reference particle in order to construct a new particle
     * automaton. If a group refernce is contained in a particle the the group
     * is resolved and the contained particle container is processed into a
     * valid particle structure.
     *
     * @param startState State in which the structure of the particle container
     * starts.
     * @param groupRef Reference to a group containing a particle container.
     * @param finalState Final state of the particle structure representing the
     * particle container of the referred group.
     * @param newParticleAutomaton Automaton in which the new particle structure
     * is constructed.
     */
    private void resolveGroupReference(ParticleState startState, GroupRef groupRef, ParticleState finalState, ParticleAutomaton newParticleAutomaton) {

        // Get group and contained particle container
        Group group = (Group) groupRef.getGroup();
        ParticleContainer particleContainer = group.getParticleContainer();

        if (particleContainer instanceof AllPattern) {
            // If the particleContainer is an all pattern the all pattern is resolved and a for the reolved particle a new structure is generated
            resolveAllPattern(startState, (AllPattern) particleContainer, finalState, newParticleAutomaton);
        } else if (particleContainer instanceof SequencePattern) {
            // If the particleContainer is a sequence pattern construct the appropriate automaton structure
            buildSequencePatternStructure(startState, (SequencePattern) particleContainer, finalState, newParticleAutomaton);
        } else if (particleContainer instanceof ChoicePattern) {
            // If the particleContainer is a choice pattern construct the appropriate automaton structure
            buildChoicePatternStructure(startState, (ChoicePattern) particleContainer, finalState, newParticleAutomaton);
        } else if (particleContainer instanceof CountingPattern) {
            // If the particleContainer is a counting pattern construct the appropriate automaton structure
            buildCountingPatternStructure(startState, (CountingPattern) particleContainer, finalState, newParticleAutomaton);
        }
    }

    /**
     * Get elements contained in the specified any pattern, if the any pattern
     * is processed strictly.
     *
     * @param anyPattern Any pattern which specifies the set of elements.
     * @param schema XSDSchema which contains the any element.
     * @return A set containing all elements contained in the specified any
     * pattern.
     */
    private LinkedHashSet<Element> getContainedElements(AnyPattern anyPattern, XSDSchema schema) {

        // Check if the any pattern is processed strictly
        if (anyPattern.getProcessContentsInstruction().equals(ProcessContentsInstruction.Strict)) {

            // Initalize set to store all top-level elements
            LinkedHashSet<Element> topLevelElements = new LinkedHashSet<Element>();

            // If any pattern namespace attribute contains "##any"
            if (anyPattern.getNamespace() == null || anyPattern.getNamespace().contains("##any")) {

                // Add all elements contained in the schema to the set
                topLevelElements.addAll(schema.getElements());

                // Add all elements contained in foreign schemata to the set
                for (ForeignSchema foreignSchema: schema.getForeignSchemas()) {
                    topLevelElements.addAll(foreignSchema.getSchema().getElements());
                }
                return topLevelElements;

            } else if (anyPattern.getNamespace().contains("##other")) {

                // If any pattern namespace attribute contains "##other" only add elements contained in foreign schemata to the set
                for (ForeignSchema foreignSchema: schema.getForeignSchemas()) {
                    topLevelElements.addAll(foreignSchema.getSchema().getElements());
                }
                return topLevelElements;

            } else {

                // Check list of namespaces
                for (String currentNamespace : anyPattern.getNamespace().split(" ")) {

                    // If any pattern namespace attribute contains "##local"
                    if (currentNamespace.contains("##local")) {

                        // Check if target namespace is empty
                        if (schema.getTargetNamespace().equals("")) {

                            // Add all elements contained in the schema to the set
                            topLevelElements.addAll(schema.getElements());
                        }
                    } else if (currentNamespace.contains("##targetNamespace") || currentNamespace.equals(schema.getTargetNamespace())) {

                        // Add all elements contained in the schema to the set
                        topLevelElements.addAll(schema.getElements());

                    } else {

                        // Find foreign schema with the specified namespace
                        for (ForeignSchema foreignSchema: schema.getForeignSchemas()) {
                            // Check if target namespace is empty
                            if (foreignSchema.getSchema().getTargetNamespace().equals("")) {

                                // Add all elements contained in the schema to the set
                                topLevelElements.addAll(foreignSchema.getSchema().getElements());
                            }
                            // Check if the current namespace is the namespace of the foreign schema
                            if (foreignSchema.getSchema().getTargetNamespace().equals(currentNamespace)) {
                                topLevelElements.addAll(foreignSchema.getSchema().getElements());
                            }
                        }
                    }
                }
                return topLevelElements;
            }
        } else {

            // If the any pattern is processed "lax" or "skip" no elements are returned
            return new LinkedHashSet<Element>();
        }
    }

    /**
     * Get the instance name of an element.
     * @param element Element which is used to get its instance name.
     * @return The instance name of an element. The name depends on the from
     * attribute. If it is unqualified the instance name contains no namespace.
     */
    private String getInstanceName(Element element) {
        // Use emtpy namespace if element is not qualified
        if (element.getForm() == Qualification.qualified) {
            return element.getName();
        } else {
            return "{}" + element.getLocalName();
        }
    }
}
