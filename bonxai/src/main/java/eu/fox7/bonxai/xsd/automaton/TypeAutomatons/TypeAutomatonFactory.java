package eu.fox7.bonxai.xsd.automaton.TypeAutomatons;

import eu.fox7.bonxai.common.*;
import eu.fox7.bonxai.xsd.*;
import eu.fox7.bonxai.xsd.Element;
import eu.fox7.bonxai.xsd.Group;
import eu.fox7.bonxai.xsd.automaton.*;
import eu.fox7.bonxai.xsd.automaton.TypeAutomatons.exceptions.*;
import eu.fox7.bonxai.xsd.automaton.exceptions.*;

import java.util.*;

/**
 * Class used to build type automatons for various uses. Type automatons are 
 * used to get types for elements contained in the XML XSDSchema schemata used to
 * build the type automatons. So this class helps to build type automatons from
 * XML XSDSchema schemata, provides a method to create a subset automaton, in case
 * the type automaton is not deterministic, or a method to create a product
 * automatons for a given list of type automatons. Each method should be used
 * with a valid XML XSDSchema schemata which respects the UPA and EDC constraint,
 * if this is not the case use can result in complications.
 *
 * @author Dominik Wolff
 */
public class TypeAutomatonFactory {

    /**
     * Builds a <tt>TypeAutomaton</tt> for a given schema. The type automaton
     * contains in its states types of the schema and its transitions are 
     * annotated with elements of the schema. When a transition annotated with
     * an element enters a type state this means the element contains the type
     * which is contained the type state of the type automaton.
     *
     * @param schema XSDSchema which is used to construct the type automaton.
     * @param anyPatternSchemaMap Map mapping an any pattern to the schema
     * containing the any pattern. Used for elements contained in any patterns.
     * @return <tt>TypeAutomaton</tt> for the given schema object.
     * @throws InvalidTypeStateContentException Exception that is thrown if a
     * type automaton state contains invalid content.
     */
    public TypeAutomaton buildTypeAutomaton(XSDSchema schema, HashMap<AnyPattern, XSDSchema> anyPatternSchemaMap) throws InvalidTypeStateContentException {

        // Create new type automaton
        TypeAutomaton typeAutomaton = new TypeAutomaton();

        // Create stack that contains all type states that were not processed yet
        Stack<TypeState> typeStateStack = new Stack<TypeState>();
        typeStateStack.push(typeAutomaton.getStartState());

        // Create set of already existing type states and add the start state to the set.
        LinkedHashSet<TypeState> alreadyExistingTypeStates = new LinkedHashSet<TypeState>();
        alreadyExistingTypeStates.add(typeAutomaton.getStartState());

        // As long as the stack of type states is not empty process the next type state
        while (!typeStateStack.isEmpty()) {

            // Get the next type state from the stack
            TypeState currentState = typeStateStack.pop();

            // If the state is a start state
            if (currentState instanceof TypeStartState) {

                // Create list of top-level Elements, which are not abstract
                LinkedHashSet<Element> notAbstractTopLevelElements = new LinkedHashSet<Element>(schema.getElements());

                // Check each element in the set
                for (Iterator<Element> it = notAbstractTopLevelElements.iterator(); it.hasNext();) {
                    Element element = it.next();
                    element.setForm(XSDSchema.Qualification.qualified);

                    // Remove element if it is abstract
                    if (element.getAbstract()) {
                        it.remove();
                    }
                }

                // For each top-level element of the schema generate a new type state and transition
                createNextProductStateAndTransition(currentState, notAbstractTopLevelElements, typeAutomaton, typeStateStack, alreadyExistingTypeStates);
            } else {
                // If the type of the element is a simpleType no outgoing Transitions have to be added to the nextState else the complexType of this state may contain elements for which new transitions have to be added.
                if (currentState.getTypes().iterator().next() instanceof ComplexType) {
                    ComplexType complexType = (ComplexType) currentState.getTypes().iterator().next();

                    // If the content of the complexType is a SimpleContent the inhertiance structure contains no elements else it is a ComplexContent which is checked for elements even when no inhertance is present.
                    if (complexType.getContent() instanceof ComplexContentType) {
                        ComplexContentType complexContent = (ComplexContentType) complexType.getContent();

                        // If no inheritance is present the particles of the ComplexContentType are checked for Elements else the inheritance is taken care of.
                        if (complexContent.getInheritance() == null || complexContent.getInheritance() instanceof ComplexContentRestriction) {

                            // Get particle which is contained in the complex type and the elements contained in the particle
                            Particle particle = complexContent.getParticle();
                            LinkedHashSet<Element> containedElements = getContainedElements(particle, anyPatternSchemaMap);

                            // Create new type states and transitions
                            createNextProductStateAndTransition(currentState, containedElements, typeAutomaton, typeStateStack, alreadyExistingTypeStates);

                        } else if (complexContent.getInheritance() != null && complexContent.getInheritance() instanceof ComplexContentExtension) {

                            // For an extension under a complexType the particles of the extending complexType are appended to the particles of the extended complexType. In order to visit all elements under
                            // this inhertance the extended complexType must be visited.

                            // As long as the current type is not empty check particle of the current type
                            while (complexType != null) {

                                // Get particle which is contained in the complex type and the elements contained in the particle
                                Particle particle = complexContent.getParticle();
                                LinkedHashSet<Element> containedElements = getContainedElements(particle, anyPatternSchemaMap);

                                // Create new type states and transitions
                                createNextProductStateAndTransition(currentState, containedElements, typeAutomaton, typeStateStack, alreadyExistingTypeStates);

                                // Get the base type of the current complex type and set it as new current complex type
                                complexType = (ComplexType) complexContent.getInheritance().getBase();

                                // Check content of the new complex type
                                if (complexType != null && complexType.getContent() instanceof ComplexContentType) {
                                    complexContent = (ComplexContentType) complexType.getContent();
                                }
                            }
                        }
                    }
                }
            }
        }
        return typeAutomaton;
    }

    /**
     * Builds a <tt>TypeAutomaton</tt> for a given particle. The type automaton
     * contains in its states types of the schema and its transitions are
     * annotated with elements of the schema. When a transition annotated with
     * an element enters a type state this means the element contains the type
     * which is contained the type state of the type automaton.
     *
     * @param particle Particle which is used as root of the new type automaton.
     * @param anyPatternSchemaMap Map mapping an any pattern to the schema
     * containing the any pattern. Used for elements contained in any patterns.
     * @return <tt>TypeAutomaton</tt> for the given particle object.
     * @throws InvalidTypeStateContentException Exception that is thrown if a
     * type automaton state contains invalid content.
     */
    public TypeAutomaton buildTypeAutomaton(Particle particle, HashMap<AnyPattern, XSDSchema> anyPatternSchemaMap) throws InvalidTypeStateContentException {

        // Create new type automaton
        TypeAutomaton typeAutomaton = new TypeAutomaton();

        // Create stack that contains all type states that were not processed yet
        Stack<TypeState> typeStateStack = new Stack<TypeState>();
        typeStateStack.push(typeAutomaton.getStartState());

        // Create set of already existing type states and add the start state to the set.
        LinkedHashSet<TypeState> alreadyExistingTypeStates = new LinkedHashSet<TypeState>();
        alreadyExistingTypeStates.add(typeAutomaton.getStartState());

        // As long as the stack of type states is not empty process the next type state
        while (!typeStateStack.isEmpty()) {

            // Get the next type state from the stack
            TypeState currentState = typeStateStack.pop();

            // If the state is a start state
            if (currentState instanceof TypeStartState) {
                createNextProductStateAndTransition(currentState, getContainedElements(particle, anyPatternSchemaMap), typeAutomaton, typeStateStack, alreadyExistingTypeStates);
            } else {
                // If the type of the element is a simpleType no outgoing Transitions have to be added to the nextState else the complexType of this state may contain elements for which new transitions have to be added.
                if (currentState.getTypes().iterator().next() instanceof ComplexType) {
                    ComplexType complexType = (ComplexType) currentState.getTypes().iterator().next();

                    // If the content of the complexType is a SimpleContent the inhertiance structure contains no elements else it is a ComplexContent which is checked for elements even when no inhertance is present.
                    if (complexType.getContent() instanceof ComplexContentType) {
                        ComplexContentType complexContent = (ComplexContentType) complexType.getContent();

                        // If no inheritance is present the particles of the ComplexContentType are checked for Elements else the inheritance is taken care of.
                        if (complexContent.getInheritance() == null || complexContent.getInheritance() instanceof ComplexContentRestriction) {

                            // Get particle which is contained in the complex type and the elements contained in the particle
                            Particle currentParticle = complexContent.getParticle();
                            LinkedHashSet<Element> containedElements = getContainedElements(currentParticle, anyPatternSchemaMap);

                            // Create new type states and transitions
                            createNextProductStateAndTransition(currentState, containedElements, typeAutomaton, typeStateStack, alreadyExistingTypeStates);

                        } else if (complexContent.getInheritance() != null && complexContent.getInheritance() instanceof ComplexContentExtension) {

                            // For an extension under a complexType the particles of the extending complexType are appended to the particles of the extended complexType. In order to visit all elements under
                            // this inhertance the extended complexType must be visited.

                            // As long as the current type is not empty check particle of the current type
                            while (complexType != null) {

                                // Get particle which is contained in the complex type and the elements contained in the particle
                                Particle currentParticle = complexContent.getParticle();
                                LinkedHashSet<Element> containedElements = getContainedElements(currentParticle, anyPatternSchemaMap);

                                // Create new type states and transitions
                                createNextProductStateAndTransition(currentState, containedElements, typeAutomaton, typeStateStack, alreadyExistingTypeStates);

                                // Get the base type of the current complex type and set it as new current complex type
                                complexType = (ComplexType) complexContent.getInheritance().getBase();

                                // Check content of the new complex type
                                if (complexType != null && complexType.getContent() instanceof ComplexContentType) {
                                    complexContent = (ComplexContentType) complexType.getContent();
                                }
                            }
                        }
                    }
                }
            }
        }
        return typeAutomaton;
    }

    /**
     * This method builds a <tt>ProductTypeAutomaton</tt> for a specified list
     * of <tt>TypeAutomatons</tt> following standard product automaton
     * construction.
     *
     * (The <tt>TypeAutomatons</tt> must be deterministic automatons, meaning
     * neither the UPA constraint nor the EDC constraint of the underlying XML
     * XSDSchema schemata must be violated, else the the constructed product
     * automaton is broken)
     *
     * @param typeAutomatons List of type automatons used to construct the
     * product automaton.
     * @return a <tt>ProductTypeAutomaton</tt> which is the product of the
     * specified type automatons.
     * @throws InvalidTypeStateContentException Exception that is thrown if a
     * type automaton state contains invalid content.
     * @throws EmptyProductTypeStateFieldException Exception which is thrown
     * if a product type state contains no type states.
     * @throws NonDeterministicFiniteAutomataException Exception which is thrown
     * if a type automaton is a no deterministic finite automaton.
     */
    public ProductTypeAutomaton buildProductTypeAutomaton(LinkedList<TypeAutomaton> typeAutomatons) throws InvalidTypeStateContentException, EmptyProductTypeStateFieldException, NonDeterministicFiniteAutomataException {

        // Construct a list for all start states contained by the new product start state
        LinkedList<TypeState> startStates = new LinkedList<TypeState>();

        // For each type automaton add start state to the set of start states
        for (TypeAutomaton typeAutomaton: typeAutomatons) {
            startStates.add(typeAutomaton.getStartState());
        }

        // Construct new start state for the a new product automaton and add the start state to new product automaton
        ProductTypeState startState = new ProductTypeState(startStates);
        ProductTypeAutomaton newProductTypeAutomaton = new ProductTypeAutomaton(startState);

        // Stack of product type states is used to visited states which are not completed yet, i.e. lack outgoing transitions.
        Stack<ProductTypeState> stateStack = new Stack<ProductTypeState>();
        stateStack.push(startState);

        // If an unprocessed state exists on the stack process this state until no states are left on the stack
        while (!stateStack.isEmpty()) {

            // Get current product state from the top of the stack
            ProductTypeState currentState = stateStack.pop();

            // Generate set to store all element names annotated to outgoing transitions of states contained in the current product state
            LinkedHashSet<String> elementNames = new LinkedHashSet<String>();

            // In order to get all element names for each contained state all transitions of this state have to be checked
            for (TypeState containedState: currentState.getTypeStates()) {
                // No sink states are allowed
                if (containedState != null) {

                    // For each transition all element names annotated to this transition have to be added to the element name set
                    for (Transition transition: containedState.getOutTransitions()) {
                        elementNames.addAll(transition.getNameElementMap().keySet());
                    }
                }
            }

            // Add for each element name a transition annotated with an element with given element name to the current state or add the element to an existing transition and create new destination state if necessary
            for (String elementName : elementNames) {

                // Create list of type states contained by the new next state
                LinkedList<TypeState> newContainedTypeStates = new LinkedList<TypeState>();

                // For each type state contained in the current product type state check for each outgoing transition of that type state if this transition contains the current element name
                for (TypeState typeState : currentState.getTypeStates()) {

                    // Generate list of type states which contains all type states reached by reading the current element name in the current type state
                    LinkedList<TypeState> nextTypeStates = new LinkedList<TypeState>();

                    // Check that type state is no sink state
                    if (typeState != null) {

                        // Check for each transition the element name
                        for (Transition transition : typeState.getOutTransitions()) {

                            // If the current transitions contains the current element name add the destination state to the list
                            if (transition.getNameElementMap().containsKey(elementName)) {
                                nextTypeStates.add((TypeState) transition.getDestinationState());
                            }
                        }
                    }

                    // Only one state is reachable in the current particle automaton by reading the current element name in the current state (determinism)
                    if (nextTypeStates.size() == 1) {
                        newContainedTypeStates.addAll(nextTypeStates);
                    } else if (nextTypeStates.isEmpty()) {

                        // Add sink state
                        newContainedTypeStates.add(null);
                    } else {

                        // A second state can be reached, so the given subtrahend automaton is not deterministic
                        throw new NonDeterministicFiniteAutomataException(typeAutomatons.get(currentState.getTypeStates().indexOf(typeState)), "buildProductTypeAutomaton");
                    }
                }

                // Variable to check wether a new product state is build
                boolean createNewType = false;

                // Check if only sink states are contained in the new product state type list
                for (TypeState typeState: newContainedTypeStates) {
                    // If type state is not a sink state generate new product state
                    if (typeState != null) {
                        createNewType = true;
                    }
                }

                // If the new to be product automaton state contains for each type automaton a type state a new state can be generated in the product automaton
                if (newContainedTypeStates.size() == typeAutomatons.size() && createNewType) {

                    // Generate new state with the nextContainedStates set and a new number
                    ProductTypeState nextState = new ProductTypeState(newContainedTypeStates);

                    // Check if the next state is not already present in the automaton
                    if (nextState == newProductTypeAutomaton.containsState(nextState)) {

                        // Add new state to the product automaton and to the stack of unprocessed states
                        newProductTypeAutomaton.addState(nextState);
                        stateStack.push(nextState);
                    } else {

                        // Get equivalent existing state
                        nextState = newProductTypeAutomaton.containsState(nextState);
                    }

                    // Create new transition connecting the current state and the next state
                    Transition transition = new Transition(currentState, nextState);

                    // Check if transition already exists in the product automaton
                    if (currentState.getNextStateTransitions().containsKey(nextState)) {

                        // If transition exist get the existing transition
                        transition = currentState.getNextStateTransitions().get(nextState);
                    } else {

                        // If transition does not exist in the current product automaton add new transition to the automaton
                        newProductTypeAutomaton.addTransition(transition);
                    }

                    // Get elements which should be added to the transition
                    for (TypeState typeState: currentState.getTypeStates()) {
                        // Check that the type state is no sink state
                        if (typeState != null) {

                            // Initialize set to store elements in
                            LinkedHashSet<Element> elements = new LinkedHashSet<Element>();

                            // For each transition check if elements with specified name exist
                            for (Transition currentTransition : typeState.getOutTransitions()) {

                                // Get the elements with specified name via the name to element map of current transition
                                if (currentTransition.getNameElementMap().containsKey(elementName)) {
                                    elements.addAll(currentTransition.getNameElementMap().get(elementName));
                                }
                            }
                            // Add elements to new subset automaton transition
                            transition.addAllElements(elements);
                        }
                    }
                }
            }
        }

        // Return finished automaton
        return newProductTypeAutomaton;
    }

    /**
     * This method build a <tt>SubsetTypeAutomaton</tt> for a specified
     * <tt>TypeAutomaton</tt> according to subset automaton construction. In
     * general it should be helpful for construction a deterministic finite
     * automaton (the subset automaton) from a non deterministic finite
     * automaton (the type automaton in this case). In order to construct the
     * new subset automaton each particle state of the old automaton is checked
     * for reachable states and subsets of particle states are generated.
     *
     * @param typeAutomaton Type automaton which may be non deterministic. If
     * this is the case the subset automaton of this type automaton is
     * deterministic.
     * @return New deterministic subset automaton for the specified type
     * automaton.
     * @throws EmptySubsetTypeStateFieldException Exception which is thrown
     * if a subset type state contains no type states.
     * @throws InvalidTypeStateContentException Exception that is thrown if a
     * type automaton state contains invalid content.
     */
    public SubsetTypeAutomaton buildSubsetTypeAutomaton(TypeAutomaton typeAutomaton) throws EmptySubsetTypeStateFieldException, InvalidTypeStateContentException {
        // Build new start state for the new subset type automaton by getting the set of reachable states for the start state of the given automaton
        LinkedHashSet<TypeState> reachableStates = new LinkedHashSet<TypeState>();
        reachableStates.add(typeAutomaton.getStartState());

        SubsetTypeState startState = new SubsetTypeState(reachableStates);

        // Build new subset automaton with new start state
        SubsetTypeAutomaton subsetTypeAutomaton = new SubsetTypeAutomaton(startState);

        // Stack of subset type states is used to visited states which are not completed yet, i.e. lack outgoing transitions.
        Stack<SubsetTypeState> subsetTypeStateStack = new Stack<SubsetTypeState>();
        subsetTypeStateStack.push(startState);

        // Create set of already existing subset type states and add the start state to the set.
        LinkedHashSet<SubsetTypeState> alreadyExistingSubsetTypeStates = new LinkedHashSet<SubsetTypeState>();
        alreadyExistingSubsetTypeStates.add(startState);

        // As long as the stack is not empty process next subset type state
        while (!subsetTypeStateStack.isEmpty()) {
            SubsetTypeState currentSubsetTypeState = subsetTypeStateStack.pop();

            // Collect all element names annotated to transitions leaving type states which are contained in the current subset type state
            LinkedHashSet<String> currentReadableElementNames = new LinkedHashSet<String>();
            for (TypeState typeState : currentSubsetTypeState.getTypeStates()) {

                // Check for each contained type state all outgoing transitions and add element names to set
                for (Transition transition : typeState.getOutTransitions()) {
                    currentReadableElementNames.addAll(transition.getNameElementMap().keySet());
                }
            }

            // Construct for each readable element name a new transition to a new subset state.
            for (String elementName : currentReadableElementNames) {

                // Create set of type states contained by the new state
                LinkedHashSet<TypeState> newContainedTypeStates = new LinkedHashSet<TypeState>();

                // For each type state contained in the current subset type state find all states reachable by reading a transition annotated with given element name
                for (TypeState typeState : currentSubsetTypeState.getTypeStates()) {
                    for (Transition transition : typeState.getOutTransitions()) {

                        if (transition.getNameElementMap().containsKey(elementName)) {
                            newContainedTypeStates.add((TypeState) transition.getDestinationState());
                        }
                    }
                }

                // Create next subset type state and check if the state is already present in the automaton then add it to the automaton
                SubsetTypeState nextState = new SubsetTypeState(newContainedTypeStates);
                nextState = subsetTypeAutomaton.containsState(nextState);
                subsetTypeAutomaton.addState(nextState);

                // If the next state does not already exists add it to the set and push it onto the stack
                if (!alreadyExistingSubsetTypeStates.contains(nextState)) {
                    alreadyExistingSubsetTypeStates.add(nextState);
                    subsetTypeStateStack.push(nextState);
                }

                // Create new transition from the current state to the next state
                Transition transition = new Transition(currentSubsetTypeState, nextState);

                // If the transition already exist in the automaton get old transition
                if (currentSubsetTypeState.getNextStateTransitions().containsKey(nextState)) {
                    transition = currentSubsetTypeState.getNextStateTransitions().get(nextState);
                } else {

                    // If transition does not exists in the type automaton add it to the automaton
                    subsetTypeAutomaton.addTransition(transition);
                }

                // Add elements to the transition
                for (TypeState typeState : currentSubsetTypeState.getTypeStates()) {

                    // Initialize set to store elements in
                    LinkedHashSet<Element> elements = new LinkedHashSet<Element>();

                    // For each transition check if elements with specified name exist
                    for (Transition currentTransition : typeState.getOutTransitions()) {

                        // Get the elements with specified name via the name to element map of current transition
                        if (currentTransition.getNameElementMap().containsKey(elementName)) {
                            elements.addAll(currentTransition.getNameElementMap().get(elementName));
                        }
                    }

                    // Add elements to new subset automaton transition
                    transition.addAllElements(elements);
                }
            }
        }
        return subsetTypeAutomaton;
    }

    /**
     * Get all elements contained in the specified particle. This includes even
     * elements not directly contained as childen of the current particle.
     *
     * @param particle Particle which is the root of the particle structure.
     * @param anyPatternSchemaMap Map mapping an any pattern to the schema
     * containing the any pattern. Used for elements contained in any patterns.
     * @return Set containing all elements contained in the specified particle.
     */
    private LinkedHashSet<Element> getContainedElements(Particle particle, HashMap<AnyPattern, XSDSchema> anyPatternSchemaMap) {

        // Initialize set which will contain all elements contained in the specified particle
        LinkedHashSet<Element> containedElements = new LinkedHashSet<Element>();

        // Check if the particle is an element and add it to the set
        if (particle instanceof Element) {
            containedElements.add((Element) particle);
        } else if (particle instanceof ElementRef) {
            ElementRef elementRef = (ElementRef) particle;

            // Top-level elements are qualified
            elementRef.getElement().setForm(XSDSchema.Qualification.qualified);

            // Check if the particle is an element reference and add refernced element to the set if not abstract
            if (!elementRef.getElement().getAbstract()) {
                containedElements.add(elementRef.getElement());
            }
        } else if (particle instanceof AnyPattern) {
            AnyPattern anyPattern = (AnyPattern) particle;

            // Get elements contained in th any pattern
            for (Iterator<Element> it = getContainedElements(anyPattern, anyPatternSchemaMap.get(anyPattern)).iterator(); it.hasNext();) {
                Element element = it.next();
                element.setForm(XSDSchema.Qualification.qualified);
            }
            containedElements.addAll(getContainedElements(anyPattern, anyPatternSchemaMap.get(anyPattern)));

        } else if (particle instanceof GroupRef) {
            GroupRef groupRef = (GroupRef) particle;

            // Check if the particle is a group reference and add elements contained in the referenced group to the set
            containedElements.addAll(getContainedElements(((Group) groupRef.getGroup()).getParticleContainer(), anyPatternSchemaMap));
        } else if (particle instanceof ParticleContainer) {
            ParticleContainer particleContainer = (ParticleContainer) particle;

            // Check if the particle is a particle container and add elements contained in the container to the set
            LinkedList<Particle> particles = particleContainer.getParticles();
            for (Iterator<Particle> it = particles.iterator(); it.hasNext();) {
                Particle currentParticle = it.next();
                containedElements.addAll(getContainedElements(currentParticle, anyPatternSchemaMap));
            }
        }
        return containedElements;
    }

    /**
     * This method creates for a given type states and a set of elements a new
     * states in the specified type automaton and transitions annotated with
     * some of the elements connecting the new states with the given current
     * state. If a state and/or transition are already present in the automaton
     * no new state and/or transition are generated and the element is added to
     * the existing transition.
     *
     * @param currentState State which is currently processed by the automaton
     * construction.
     * @param transitionElements Elements which will be annotated to transitions
     * leaving the current state.
     * @param typeAutomaton Automaton containing the current state and the new
     * created states and transitions.
     * @param typeStateStack Stack which contains all type states not yet
     * processed.
     * @param alreadyExistingTypeStates Set containing all existing states of
     * the type automaton.
     * @throws InvalidTypeStateContentException Exception thrown if the content
     * of a type state is invalid.
     */
    private void createNextProductStateAndTransition(TypeState currentState, LinkedHashSet<Element> transitionElements, TypeAutomaton typeAutomaton, Stack<TypeState> typeStateStack, LinkedHashSet<TypeState> alreadyExistingTypeStates) throws InvalidTypeStateContentException {

        // For each element update the type automaton
        for (Element element: transitionElements) {
            // Get the type of the current element
            Type elementType = element.getType();

            // Create next type state and check if the state is already present in the automaton then add it to the automaton
            TypeState nextState = new TypeState(elementType);
            nextState = typeAutomaton.containsState(nextState);
            typeAutomaton.addState(nextState);

            // If the next state does not already exists add it to the set and push it onto the stack
            if (!alreadyExistingTypeStates.contains(nextState)) {
                alreadyExistingTypeStates.add(nextState);
                typeStateStack.push(nextState);
            }

            // If there is already a transition from the current state to next state, add element to the transition
            if (currentState.getNextStateTransitions().containsKey(nextState)) {
                currentState.getNextStateTransitions().get(nextState).addElement(element);
            } else {

                // If no transition exist, create a new Transition between the current state and the next state and add element to transition and transition to the type automaton
                Transition transition = new Transition(currentState, nextState);
                transition.addElement(element);
                typeAutomaton.addTransition(transition);
            }
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
        if (anyPattern.getProcessContentsInstruction().equals(ProcessContentsInstruction.Strict) || anyPattern.getProcessContentsInstruction().equals(ProcessContentsInstruction.Lax) ) {

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
                for (Iterator<ForeignSchema> it = schema.getForeignSchemas().iterator(); it.hasNext();) {
                    ForeignSchema foreignSchema = it.next();
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
                        }else {

                            // If any pattern namespace attribute contains "##local" only add elements contained in foreign schemata to the set
                            for (ForeignSchema foreignSchema: schema.getForeignSchemas()) {
                                // Check if the current namespace is the namespace of the foreign schema
                                if (foreignSchema.getSchema().getTargetNamespace().equals("")) {
                                    topLevelElements.addAll(foreignSchema.getSchema().getElements());
                                }
                            }
                            return topLevelElements;
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
}
