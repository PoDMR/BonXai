package eu.fox7.bonxai.xsd.automaton.ParticleAutomatons;

import eu.fox7.bonxai.common.*;
import eu.fox7.bonxai.xsd.Element;
import eu.fox7.bonxai.xsd.ElementRef;
import eu.fox7.bonxai.xsd.Group;
import eu.fox7.bonxai.xsd.automaton.*;
import eu.fox7.bonxai.xsd.automaton.ParticleAutomatons.exceptions.*;

import java.util.*;

/**
 * This class provides methods to generate particles for given particle
 * automata. In order to construct a particle from a specified particle
 * automaton the method of state elimination is used.
 *
 * @author Dominik Wolff
 */
public class ParticleBuilder {

    /**
     * This method uses state elimination to generate a particle for a given
     * particle automaton. The new particle represents the particle automaton.
     * State elimination removes single states from the particle automaton by
     * constructing new transitions between predecessor and successor states of 
     * the has to be eliminated state annotated with new particle. So that the
     * state can be eliminated without a problem.
     *
     * @param particleAutomaton Automaton which is transfomed into a particle.
     * @return Particle representing the specified automaton.
     * @throws EmptySubsetParticleStateFieldException Exception which is thrown
     * if a subset state contains no particle states.
     * @throws EmptyProductParticleStateFieldException Exception which is thrown
     * if a product state contains no particle states.
     * @throws NotSupportedParticleAutomatonException Exception which is thrown
     * if a not supported particle automaton was specified as particle
     * automaton.
     */
    public Particle buildParticle(ParticleAutomaton particleAutomaton) throws EmptySubsetParticleStateFieldException, EmptyProductParticleStateFieldException, NotSupportedParticleAutomatonException {

        // Get particle of the current particle automaton
        Particle resultingParticle = null;

        // Check if a final state exists for the current particle automaton
        if (!particleAutomaton.getFinalStates().isEmpty()) {

            // Copy the specified particle automaton
            ParticleAutomaton particleAutomatonCopy = copyParticleAutomaton(particleAutomaton);

            // Check if more than one final state exist in the current particle automaton if this is the case add a single new final state to the automaton and remove the old final states from the set of final states
            if (particleAutomatonCopy.getFinalStates().size() > 1) {

                // Generate new final state
                ParticleState newFinalState = null;

                // Check what kind of automaton the particle automaton is
                if (particleAutomatonCopy instanceof ParticleAutomaton) {

                    // If the particle automaton is a particle automaton add new final state to the automaton
                    newFinalState = new ParticleState(particleAutomatonCopy.getNextStateNumber());
                    particleAutomatonCopy.addState(newFinalState);

                } else if (particleAutomatonCopy instanceof SubsetParticleAutomaton) {

                    // If the particle automaton is a subset automaton add new final state to the automaton which contains all states contained in the other final states
                    LinkedHashSet<ParticleState> containedStates = new LinkedHashSet<ParticleState>();

                    // For each final state in the automaton
                    for (Iterator<ParticleState> it = particleAutomatonCopy.getFinalStates().iterator(); it.hasNext();) {
                        SubsetParticleState finalState = (SubsetParticleState) it.next();

                        // Add each state contained in a final state to the subset of the new final state
                        for (Iterator<ParticleState> it2 = finalState.getParticleStates().iterator(); it2.hasNext();) {
                            ParticleState containedState = it2.next();
                            containedStates.add(containedState);
                        }
                    }

                    // Add new final subset state to the automaton
                    newFinalState = new SubsetParticleState(containedStates, particleAutomatonCopy.getNextStateNumber());
                    particleAutomatonCopy.addState(newFinalState);

                } else if (particleAutomatonCopy instanceof ProductParticleAutomaton) {

                    // If the particle automaton is a product automaton generate new particle states for a new final state
                    LinkedList<ParticleState> particleStates = new LinkedList<ParticleState>();

                    // The new particle states are all sink states
                    for (int i = 0; i < ((ProductParticleState) particleAutomatonCopy.getStartState()).getParticleStates().size(); i++) {
                        particleStates.add(null);
                    }

                    // Add new final state with new particle states to the automaton
                    newFinalState = new ProductParticleState(particleStates, particleAutomatonCopy.getNextStateNumber());
                    particleAutomatonCopy.addState(newFinalState);

                } else {

                    // No supported particle automaton
                    throw new NotSupportedParticleAutomatonException(particleAutomatonCopy, "buildParticle");
                }

                // Add to each final state of the automaton a transition to the new final state
                for (ParticleState currentFinalState: particleAutomatonCopy.getFinalStates()) {
                    // Add new epsilon transition from the current final state to the new final state to the automaton
                    ParticleTransition newParticleTransition = new ParticleTransition(currentFinalState, newFinalState, true);
                    particleAutomatonCopy.addTransition(newParticleTransition);
                }

                // Remove all old final states and add the new final state to the set of final states
                particleAutomatonCopy.clearFinalStates();
                particleAutomatonCopy.addFinalState(newFinalState);
            }

            // Get list containg all states of the current automaton
            LinkedList<State> stateList = new LinkedList<State>(particleAutomatonCopy.getStates());

            // Sort state list. Each state is given a weight depending on the number of incoming and outgoing transition, where outgoing transitions count double.
            Collections.sort(stateList, new Comparator<State>() {

                public int compare(State state1, State state2) {
                    Integer state1Edges = state1.getInTransitions().size() + 2 * state1.getOutTransitions().size();
                    Integer state2Edges = state2.getInTransitions().size() + 2 * state2.getOutTransitions().size();
                    return (state1Edges.compareTo(state2Edges));
                }
            });

            // Elimintate each state contained in the state list except the start and final state
            for (Iterator<State> it = stateList.iterator(); it.hasNext();) {
                ParticleState particleState = (ParticleState) it.next();

                // Check that the particle state is not the start state and not the final state
                if (particleAutomatonCopy.getStartState() != particleState && !particleAutomatonCopy.getFinalStates().contains(particleState)) {

                    // Eliminate the particle state from the automaton
                    eliminateState(particleState, particleAutomatonCopy);
                }
            }

            // After eliminating all states except final and start state only one transition from start to final state remains.
            if (particleAutomatonCopy.getStates().size() == 2) {

                // Transition from start to final state
                if(particleAutomatonCopy.getStartState().getOutTransitions().isEmpty()){
                    return new SequencePattern();
                }
                ParticleTransition startFinalTransition = (ParticleTransition) particleAutomatonCopy.getStartState().getOutTransitions().iterator().next();

                // Get the self loop of the start state
                ParticleTransition selfLoopStartState = (ParticleTransition) particleAutomatonCopy.getStartState().getNextStateTransitions().get(particleAutomatonCopy.getStartState());

                // Get the self loop of the final state
                ParticleTransition selfLoopFinalState = (ParticleTransition) particleAutomatonCopy.getFinalStates().iterator().next().getNextStateTransitions().get(particleAutomatonCopy.getFinalStates().iterator().next());

                // If either the start or the final state or both have self loops
                if ((selfLoopStartState != null && containsElementOrAnyPattern(selfLoopStartState.getResultingParticle())) || (selfLoopFinalState != null && containsElementOrAnyPattern(selfLoopFinalState.getResultingParticle()))) {

                    // Generate new sewuence pattern
                    SequencePattern sequencePattern = new SequencePattern();

                    // Check if a self loop is present for the start state and if it contains an element or any pattern
                    if (selfLoopStartState != null && containsElementOrAnyPattern(selfLoopStartState.getResultingParticle())) {

                        // Generate new counting pattern for the particle annotated to the self loop with minimal occurrence zero and unbounded maximal occurrence, because self loop can be skipped or used indefinitely
                        CountingPattern countingPattern = new CountingPattern(0, null);

                        // Add particle of the self loop to the counting pattern and add counting pattern to the new resulting particle
                        countingPattern.addParticle(selfLoopStartState.getResultingParticle());

                        // Add new counting pattern to the seqeunce pattern
                        sequencePattern.addParticle(countingPattern);
                    }

                    // Check if the resulting particle of the start to final transition is a sequence pattern
                    if (startFinalTransition.getResultingParticle() instanceof SequencePattern) {
                        SequencePattern resultingSequencePattern = (SequencePattern) startFinalTransition.getResultingParticle();

                        // If it is a sequence pattern add all contained particle to the new sequence pattern
                        for (Iterator<Particle> it = resultingSequencePattern.getParticles().iterator(); it.hasNext();) {
                            Particle particle = it.next();

                            // Add particle to sequence pattern
                            sequencePattern.addParticle(particle);
                        }
                    } else {

                        // Add particle to sequence pattern
                        sequencePattern.addParticle(startFinalTransition.getResultingParticle());
                    }

                    // Check if a self loop is present for the final state and if it contains an element or any pattern
                    if (selfLoopFinalState != null && containsElementOrAnyPattern(selfLoopFinalState.getResultingParticle())) {

                        // Generate new counting pattern for the particle annotated to the self loop with minimal occurrence zero and unbounded maximal occurrence, because self loop can be skipped or used indefinitely
                        CountingPattern countingPattern = new CountingPattern(0, null);

                        // Add particle of the self loop to the counting pattern and add counting pattern to the new resulting particle
                        countingPattern.addParticle(selfLoopFinalState.getResultingParticle());

                        // Add new counting pattern to the seqeunce pattern
                        sequencePattern.addParticle(countingPattern);
                    }

                    // Set resulting particel of the start to final transition to the new sequence pattern
                    startFinalTransition.setResultingParticle(sequencePattern);
                }

                // Check if the last transition between start and final state contains an epsilon
                if (startFinalTransition.hasEpsilon()) {

                    // Check if the resulting particle of the transition is a counting pattern
                    if (startFinalTransition.getResultingParticle() instanceof CountingPattern) {

                        // Set counting pattern minimal occurrence to zero
                        resultingParticle = startFinalTransition.getResultingParticle();
                        ((CountingPattern) resultingParticle).setMin(0);
                    } else {

                        // If the resulting particle is no counting pattern generate new counting pattern with a minimal occurrence of zero and let it contain the resulting particle
                        resultingParticle = new CountingPattern(0, 1);
                        ((CountingPattern) resultingParticle).addParticle(startFinalTransition.getResultingParticle());
                    }
                } else {

                    // If the last transition between start and final state contains no epsilon the particle annotated to the transition is the particle for the whole automaton
                    resultingParticle = startFinalTransition.getResultingParticle();
                }
            }

        }
        // Return particle for the specified automaton
        return resultingParticle;
    }

    /**
     * Check if a specified particle contains an element or any pattern. Can be
     * used to check if a current particle contains no importent information
     * such a particle can be removed easyly.
     *
     * @param particle Particle which may or may not contain an element or
     * any pattern.
     * @return <tt>true</tt> if the specified particle contains an element or
     * any pattern else <tt>false</tt>.
     */
    private boolean containsElementOrAnyPattern(Particle particle) {

        if (particle instanceof ParticleContainer) {

            // If particle is particle container check if particle container is counting pattern
            if (particle instanceof CountingPattern) {
                CountingPattern countingPattern = (CountingPattern) particle;

                // Check if maximal occurrence is not zero
                if (countingPattern.getMax() == null || countingPattern.getMax() != 0) {

                    // Get particle container to check contained particles
                    ParticleContainer particleContainer = (ParticleContainer) particle;

                    // For each particle contained in the particle container check if it contains an element or any pattern
                    for (Iterator<Particle> it = particleContainer.getParticles().iterator(); it.hasNext();) {
                        Particle containedParticle = it.next();

                        // Check if the particle contains an element or any pattern if it does return true
                        if (containsElementOrAnyPattern(containedParticle)) {
                            return true;
                        }
                    }
                } else {

                    // If  maximal occurrence is zero nothing is contained
                    return false;
                }
            } else {

                // The particle is no counting pattern but still a particle container
                ParticleContainer particleContainer = (ParticleContainer) particle;

                // For each particle contained in the particle container check if it contains an element or any pattern
                for (Iterator<Particle> it = particleContainer.getParticles().iterator(); it.hasNext();) {
                    Particle containedParticle = it.next();

                    // Check if the particle contains an element or any pattern if it does return true
                    if (containsElementOrAnyPattern(containedParticle)) {
                        return true;
                    }
                }
            }
        } else if (particle instanceof GroupRef) {

            // If particle is a group reference get group
            Group group = (Group) ((GroupRef) particle).getGroup();

            // Check if group contains element or any pattern
            return containsElementOrAnyPattern(group.getParticleContainer());

        } else if (particle instanceof AnyPattern || particle instanceof Element || particle instanceof ElementRef) {

            // If particle is any pattern, element or element ref return true
            return true;
        }

        // If no case matches or no return statement is reached return false
        return false;
    }

    /**
     * Constructs a copy of the given particle automaton. This copy can be used
     * if the original automaton is modified and the unmodified automaton is
     * still used for other purposes.
     *
     * @param particleAutomaton Automaton which should be copied by this method.
     * @return Copy of the specified particle automaton.
     * @throws EmptySubsetParticleStateFieldException Exception which is thrown
     * if a subset state contains no particle states.
     * @throws EmptyProductParticleStateFieldException Exception which is thrown
     * if a product state contains no particle states.
     * @throws NoSupportedParticleAutomaton Exception which is thrown if the
     * given particle automaton is not supported.
     */
    private ParticleAutomaton copyParticleAutomaton(ParticleAutomaton particleAutomaton) throws NotSupportedParticleAutomatonException, EmptySubsetParticleStateFieldException, EmptyProductParticleStateFieldException {

        // Check what kind of automaton the particle automaton is
        if (particleAutomaton instanceof ParticleAutomaton) {

            // Initialize map mapping original states to copy states
            HashMap<ParticleState, ParticleState> originalCopyMap = new HashMap<ParticleState, ParticleState>();

            // Generate copy of the start state
            ParticleState startStateCopy = new ParticleState(particleAutomaton.getStartState().getStateNumber());
            originalCopyMap.put(particleAutomaton.getStartState(), startStateCopy);

            // Generate new particle automaton for the copy
            ParticleAutomaton particleAutomatonCopy = new ParticleAutomaton(startStateCopy);

            // If the current particle state is a final state the copy is a final state too
            if (particleAutomaton.getFinalStates().contains(particleAutomaton.getStartState())) {
                particleAutomatonCopy.addFinalState(startStateCopy);
            }

            // Add other states copies to the particle automaton copy
            for (ParticleState particleState: particleAutomaton.getStates()) {
                // Check that the current particle state is not the start state
                if (particleState != particleAutomaton.getStartState()) {

                    // Create copy of the current particle state and add it to the particle automaton copy
                    ParticleState particleStateCopy = new ParticleState(particleState.getStateNumber());
                    originalCopyMap.put(particleState, particleStateCopy);
                    particleAutomatonCopy.addState(particleStateCopy);

                    // If the current particle state is a final state the copy is a final state too
                    if (particleAutomaton.getFinalStates().contains(particleState)) {
                        particleAutomatonCopy.addFinalState(particleStateCopy);
                    }
                }
            }

            // Add transitions to the particle automaton copy
            for (ParticleState particleState: particleAutomaton.getStates()) {
                // For each state copy all outgoing transitions
                for (Iterator<Transition> it2 = particleState.getOutTransitions().iterator(); it2.hasNext();) {
                    ParticleTransition particleTransition = (ParticleTransition) it2.next();

                    // Create copy of the current particle transition and add it to the particle automaton copy
                    ParticleTransition particleTransitionCopy = new ParticleTransition(originalCopyMap.get(particleTransition.getSourceState()), originalCopyMap.get(particleTransition.getDestinationState()), particleTransition.hasEpsilon());
                    particleTransitionCopy.setResultingParticle(particleTransition.getResultingParticle());
                    particleAutomatonCopy.addTransition(particleTransitionCopy);
                }
            }

            // Return copy
            return particleAutomatonCopy;

        } else if (particleAutomaton instanceof SubsetParticleAutomaton) {

            // Get original subset automaton
            SubsetParticleAutomaton subsetParticleAutomaton = (SubsetParticleAutomaton) particleAutomaton;

            // Initialize map mapping original subset states to copy subset states
            HashMap<SubsetParticleState, SubsetParticleState> originalCopyMap = new HashMap<SubsetParticleState, SubsetParticleState>();

            // Generate copy of the subset start state with copy of the subset particle states
            SubsetParticleState startStateCopy = new SubsetParticleState(((SubsetParticleState) subsetParticleAutomaton.getStartState()).getParticleStates(), subsetParticleAutomaton.getStartState().getStateNumber());
            originalCopyMap.put((SubsetParticleState) subsetParticleAutomaton.getStartState(), startStateCopy);

            // Generate new subset particle automaton for the copy
            SubsetParticleAutomaton subsetParticleAutomatonCopy = new SubsetParticleAutomaton(startStateCopy);

            // If the current particle state is a final state the copy is a final state too
            if (particleAutomaton.getFinalStates().contains(particleAutomaton.getStartState())) {
                subsetParticleAutomatonCopy.addFinalState(startStateCopy);
            }

            // Add other subset states copies to the subset particle automaton copy
            for (Iterator<ParticleState> it = subsetParticleAutomaton.getStates().iterator(); it.hasNext();) {
                SubsetParticleState subsetParticleState = (SubsetParticleState) it.next();

                // Check that the current subset particle state is not the start state
                if (subsetParticleState != subsetParticleAutomaton.getStartState()) {

                    // Create copy of the current subset particle state and add it to the subset particle automaton copy
                    SubsetParticleState subsetParticleStateCopy = new SubsetParticleState(((SubsetParticleState) subsetParticleAutomaton.getStartState()).getParticleStates(), subsetParticleState.getStateNumber());
                    originalCopyMap.put(subsetParticleState, subsetParticleStateCopy);
                    subsetParticleAutomatonCopy.addState(subsetParticleStateCopy);

                    // If the current subset particle state is a final state the copy is a final state too
                    if (subsetParticleAutomaton.getFinalStates().contains(subsetParticleState)) {
                        subsetParticleAutomatonCopy.addFinalState(subsetParticleStateCopy);
                    }
                }
            }

            // Add transitions to the particle automaton copy
            for (Iterator<ParticleState> it = subsetParticleAutomaton.getStates().iterator(); it.hasNext();) {
                SubsetParticleState subsetParticleState = (SubsetParticleState) it.next();

                // For each subset state copy all outgoing transitions
                for (Iterator<Transition> it2 = subsetParticleState.getOutTransitions().iterator(); it2.hasNext();) {
                    ParticleTransition particleTransition = (ParticleTransition) it2.next();

                    // Create copy of the current particle transition and add it to the subset particle automaton copy
                    ParticleTransition particleTransitionCopy = new ParticleTransition(originalCopyMap.get(particleTransition.getSourceState()), originalCopyMap.get(particleTransition.getDestinationState()), particleTransition.hasEpsilon());
                    particleTransitionCopy.setResultingParticle(particleTransition.getResultingParticle());
                    subsetParticleAutomatonCopy.addTransition(particleTransitionCopy);
                }
            }

            // Return subset copy
            return subsetParticleAutomatonCopy;

        } else if (particleAutomaton instanceof ProductParticleAutomaton) {

            // Get original product automaton
            ProductParticleAutomaton productParticleAutomaton = (ProductParticleAutomaton) particleAutomaton;

            // Initialize map mapping original product states to copy product states
            HashMap<ProductParticleState, ProductParticleState> originalCopyMap = new HashMap<ProductParticleState, ProductParticleState>();

            // Generate copy of the start state with the copy of the contained particle states
            ProductParticleState startStateCopy = new ProductParticleState(((ProductParticleState) particleAutomaton.getStartState()).getParticleStates(), particleAutomaton.getStartState().getStateNumber());
            originalCopyMap.put(((ProductParticleState) particleAutomaton.getStartState()), startStateCopy);

            // Generate new product particle automaton for the copy
            ProductParticleAutomaton productParticleAutomatonCopy = new ProductParticleAutomaton(startStateCopy);

            // If the current particle state is a final state the copy is a final state too
            if (particleAutomaton.getFinalStates().contains(particleAutomaton.getStartState())) {
                productParticleAutomatonCopy.addFinalState(startStateCopy);
            }

            // Add other product states copies to the product particle automaton copy
            for (Iterator<ParticleState> it = productParticleAutomaton.getStates().iterator(); it.hasNext();) {
                ProductParticleState productParticleState = (ProductParticleState) it.next();

                // Check that the current product particle state is not the start state
                if (productParticleState != productParticleAutomaton.getStartState()) {

                    // Create copy of the current product particle state and add it to the product particle automaton copy
                    ProductParticleState productParticleStateCopy = new ProductParticleState(productParticleState.getParticleStates(), productParticleState.getStateNumber());
                    originalCopyMap.put(productParticleState, productParticleStateCopy);
                    productParticleAutomaton.addState(productParticleStateCopy);

                    // If the current product particle state is a final state the copy is a final state too
                    if (productParticleAutomaton.getFinalStates().contains(productParticleState)) {
                        productParticleAutomatonCopy.addFinalState(productParticleStateCopy);
                    }
                }
            }

            // Add transitions to the product particle automaton copy
            for (Iterator<ParticleState> it = productParticleAutomaton.getStates().iterator(); it.hasNext();) {
                ProductParticleState productParticleState = (ProductParticleState) it.next();

                // For each state copy all outgoing transitions
                for (Iterator<Transition> it2 = productParticleState.getOutTransitions().iterator(); it2.hasNext();) {
                    ParticleTransition particleTransition = (ParticleTransition) it2.next();

                    // Create copy of the current particle transition and add it to the product particle automaton copy
                    ParticleTransition particleTransitionCopy = new ParticleTransition(originalCopyMap.get(particleTransition.getSourceState()), originalCopyMap.get(particleTransition.getDestinationState()), particleTransition.hasEpsilon());
                    particleTransitionCopy.setResultingParticle(particleTransition.getResultingParticle());
                    productParticleAutomatonCopy.addTransition(particleTransitionCopy);
                }
            }

            // Return product copy
            return productParticleAutomatonCopy;

        } else {

            // No supported particle automaton
            throw new NotSupportedParticleAutomatonException(particleAutomaton, "copyParticleAutomaton");
        }
    }

    /**
     * This method can be used to eliminate a specified state from the given
     * automaton. The specified state is removed after building transitions
     * between all of its predecessor states to all of its successor states.
     * These transitions are annotated with the particle of the incoming and
     * outgoing transition.
     *
     * @param particleState State which should be eliminated from the particle
     * automaton.
     * @param particleAutomaton Automaton containing the particle state.
     */
    private void eliminateState(ParticleState particleState, ParticleAutomaton particleAutomaton) {

        // Get set containing all incoming transitions of the current particle state
        LinkedHashSet<ParticleTransition> inTransitions = new LinkedHashSet<ParticleTransition>();

        // Get each incoming transition from the current statea and add it to the set
        for (Transition inTransition : particleState.getInTransitions()) {
            inTransitions.add((ParticleTransition) inTransition);
        }

        // Get set containing all outgoing transitions of the current particle state
        LinkedHashSet<ParticleTransition> outTransitions = new LinkedHashSet<ParticleTransition>();

        // Get each outgoing transition from the current statea and add it to the set
        for (Transition outTransition : particleState.getOutTransitions()) {
            outTransitions.add((ParticleTransition) outTransition);
        }

        // Check if both transition sets contain trainsitions
        if (!inTransitions.isEmpty() && !outTransitions.isEmpty()) {

            // Variable to store self loop transition in that is a transition connecting the particle state with itself
            ParticleTransition selfLoop = null;

            // For each transition of the incoming transitions check each transition of the outgoing transitions
            for (Iterator<ParticleTransition> it = inTransitions.iterator(); it.hasNext();) {
                ParticleTransition inTransition = it.next();

                // For each transition of the outgoing transitions check if both transitions are equal
                for (Iterator<ParticleTransition> it2 = outTransitions.iterator(); it2.hasNext();) {
                    ParticleTransition outTransition = it2.next();

                    // Check if incoming transition is outgoing transition ( Only one such transition can exist) and set self loop variable
                    if (inTransition == outTransition) {
                        selfLoop = inTransition;
                    }
                }
            }

            // Build for each pair of incoming and outgoing transitions a new transition
            for (Iterator<ParticleTransition> it = inTransitions.iterator(); it.hasNext();) {
                ParticleTransition inTransition = it.next();

                for (Iterator<ParticleTransition> it2 = outTransitions.iterator(); it2.hasNext();) {
                    ParticleTransition outTransition = it2.next();

                    // Check that current incoming transition is not the self loop transition
                    if (selfLoop != inTransition && selfLoop != outTransition) {

                        // Build new Transition which skips the specified state
                        State newSourceState = inTransition.getSourceState();
                        State newDestinationState = outTransition.getDestinationState();
                        ParticleTransition newParticleTransition = new ParticleTransition(newSourceState, newDestinationState, false);

                        // Get new result particle for the new particle transition which is a sequence pattern
                        Particle newResultingParticle = new SequencePattern();

                        // Build particle for the transition entering the state
                        Particle inParticle = inTransition.getResultingParticle();

                        // Check whether the particle contains an element or any pattern
                        if (containsElementOrAnyPattern(inParticle)) {

                            // If the current incoming transition is an epsilon transition generate new counting pattern with minimal occurrence zero and add particle to the counting pattern
                            if (inTransition.hasEpsilon()) {
                                inParticle = new CountingPattern(0, 1);
                                ((CountingPattern) inParticle).addParticle(inTransition.getResultingParticle());
                            }

                            // If particle is a sequence pattern
                            if (inParticle instanceof SequencePattern) {
                                SequencePattern inSequencePattern = (SequencePattern) inParticle;

                                // Add each particle contained in the current particle to the new resulting particle (This is done so that no sequence pattern is stored in a sequence pattern)
                                for (Particle sequenceParticle : inSequencePattern.getParticles()) {
                                    ((SequencePattern) newResultingParticle).addParticle(sequenceParticle);
                                }
                            } else {

                                // If particle is no sequence pattern add it to the new resulting particle
                                ((SequencePattern) newResultingParticle).addParticle(inParticle);
                            }
                        } else {

                            // If the particle contains no element or any pattern it is empty
                            inParticle = null;
                        }

                        // Check if a self loop is present for the current particle and if it contains an element or any pattern
                        if (selfLoop != null && containsElementOrAnyPattern(selfLoop.getResultingParticle())) {

                            // Generate new counting pattern for the particle annotated to the self loop with minimal occurrence zero and unbounded maximal occurrence, because self loop can be skipped or used indefinitely
                            CountingPattern countingPattern = new CountingPattern(0, null);

                            // Add particle of the self loop to the counting pattern and add counting pattern to the new resulting particle
                            countingPattern.addParticle(selfLoop.getResultingParticle());
                            ((SequencePattern) newResultingParticle).addParticle(countingPattern);
                        }

                        // Build particle for the transition exiting the state
                        Particle outParticle = outTransition.getResultingParticle();

                        // Check whether the particle contains an element or any pattern
                        if (containsElementOrAnyPattern(outParticle)) {

                            // If the current outgoing transition is an epsilon transition generate new counting pattern with minimal occurrence zero and add particle to the counting pattern
                            if (outTransition.hasEpsilon()) {
                                outParticle = new CountingPattern(0, 1);
                                ((CountingPattern) outParticle).addParticle(outTransition.getResultingParticle());
                            }

                            // If particle is a sequence pattern
                            if (outParticle instanceof SequencePattern) {
                                SequencePattern inSequencePattern = (SequencePattern) outParticle;

                                // Add each particle contained in the current particle to the new resulting particle (This is done so that no sequence pattern is stored in a sequence pattern)
                                for (Particle sequenceParticle : inSequencePattern.getParticles()) {
                                    ((SequencePattern) newResultingParticle).addParticle(sequenceParticle);
                                }
                            } else {

                                // If particle is no sequence pattern add it to the new resulting particle
                                ((SequencePattern) newResultingParticle).addParticle(outParticle);
                            }
                        } else {

                            // If the particle contains no element or any pattern it is empty
                            outParticle = null;
                        }

                        // If only one particle is in the sequence pattern the sequence pattern is not necessary
                        if (((SequencePattern) newResultingParticle).getParticles().size() == 1) {
                            newResultingParticle = ((SequencePattern) newResultingParticle).getParticles().getFirst();
                        }

                        // Set new resulting particle in the new particle transition
                        newParticleTransition.setResultingParticle(newResultingParticle);

                        // Add new transition to the automaton and check if this is successful
                        if (!particleAutomaton.addTransition(newParticleTransition)) {

                            // If the transition could not be added to the automaton find existing transition
                            for (Iterator<Transition> it3 = newSourceState.getOutTransitions().iterator(); it3.hasNext();) {
                                ParticleTransition transition = (ParticleTransition) it3.next();

                                // Check if current transition has same source and destination state as the new transition
                                if (transition.getDestinationState() == newDestinationState) {

                                    // Get resulting particle of that transition
                                    Particle oldResultingParticle = transition.getResultingParticle();

                                    // Check if the resulting particle of the existing transition is null
                                    if (oldResultingParticle == null) {

                                        // New resulting particle can be set as resutling particle of the existing transition because the old resulting particle was null
                                        transition.setResultingParticle(newResultingParticle);
                                    } else {

                                        // The resulting particle of the existing transition is not null so a new resulting particle which is a choice pattern is generated
                                        ChoicePattern newChoicePattern = new ChoicePattern();

                                        // Check if the new resutling particle is a choice pattern
                                        if (newResultingParticle instanceof ChoicePattern) {

                                            // Add particles contained in the choice pattern to the new choice pattern (No choice pattern in a choice pattern)
                                            for (Particle particle : ((ChoicePattern) newResultingParticle).getParticles()) {
                                                newChoicePattern.addParticle(particle);
                                            }
                                        } else {

                                            // Add new resulting particle to the new choice pattern
                                            newChoicePattern.addParticle(newResultingParticle);
                                        }

                                        // Check if the particle of the existing transition is a choice pattern
                                        if (oldResultingParticle instanceof ChoicePattern) {

                                            // Add particles contained in the choice pattern to the new choice pattern (No choice pattern in a choice pattern)
                                            for (Particle particle : ((ChoicePattern) oldResultingParticle).getParticles()) {
                                                newChoicePattern.addParticle(particle);
                                            }
                                        } else {

                                            // Add the particle of the existing transition to the new choice pattern
                                            newChoicePattern.addParticle(oldResultingParticle);
                                        }

                                        // Set new choice pattern as resulting particle of the existing transition
                                        transition.setResultingParticle(newChoicePattern);
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // After checking all transitions remove particle state which is no longer needed in the automaton
            particleAutomaton.removeState(particleState);

        } else {

            // If both transition sets are empty remove particle state from the automaton
            particleAutomaton.removeState(particleState);
        }
    }
}
