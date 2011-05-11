package de.tudortmund.cs.bonxai.xsd.setOperations.difference;

import de.tudortmund.cs.bonxai.common.AnyAttribute;
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
import de.tudortmund.cs.bonxai.xsd.automaton.*;
import de.tudortmund.cs.bonxai.xsd.automaton.ParticleAutomatons.exceptions.*;
import de.tudortmund.cs.bonxai.xsd.automaton.TypeAutomatons.*;
import de.tudortmund.cs.bonxai.xsd.automaton.TypeAutomatons.exceptions.*;
import de.tudortmund.cs.bonxai.xsd.automaton.exceptions.NonDeterministicFiniteAutomataException;
import de.tudortmund.cs.bonxai.xsd.tools.ForeignSchemaResolver;

import java.util.*;

/**
 * The SchemaDifferenceGenerator class is responsible for creating a new schema
 * for a given minuend schema and specified subtrahend schema. The new schema
 * should be valid for every XML instance which is valid for the minuend schema
 * and not valid for the subtrahend schema. Because the new schema is only an
 * approximation there are cases where schemata are accepted which do not follow
 * this rule.
 *
 * @author Dominik Wolff
 */
public class SchemaDifferenceGenerator {

    // Store minuend and subtrahend XSDSchema in SchemaDifferenceGenerator
    private XSDSchema minuendSchema = null,  subtrahendSchema = null;

    // HashMap mapping namespaces to output schemata (Can be used if an element with a foreign namespace is referenced).
    private LinkedHashMap<String, XSDSchema> namespaceOutputSchemaMap;

    // Map mapping target namespaces to old minuend schemata used to construct the corresponding output schema
    private LinkedHashMap<String, XSDSchema> namespaceOldSchemaMap;

    // HashMap mapping namespaces to elements which are present in the minuend and subtrahend schema of the schema group with the corresponding namespace
    private LinkedHashMap<String, LinkedHashSet<Element>> namespaceConflictingElementsMap;

    // The elementGroupMap maps original minuend schema elements to groups. Elements contained are no present in the output schema because a new difference element is
    // generated for them but are still contained in their original stat in given groups
    private LinkedHashMap<Element, Group> elementGroupMap;

    // Map mapping each any attribute to the old schema, that contains the any attribute
    private LinkedHashMap<AnyAttribute, XSDSchema> anyAttributeOldSchemaMap;

    // Map mapping attributes to old schemata used to construct the new output schema
    private LinkedHashMap<Attribute, XSDSchema> attributeOldSchemaMap;

    // Map mapping types to old schemata used to construct the new output schema
    private LinkedHashMap<Type, XSDSchema> typeOldSchemaMap;

    // Map mapping any patterns to their old schemata.
    private LinkedHashMap<AnyPattern, XSDSchema> anyPatternOldSchemaMap;

    // Map mapping elements to old schemata used to construct the new output schema
    private LinkedHashMap<Element, XSDSchema> elementOldSchemaMap;

    // Set containing all IDs used in all new output schemata
    private LinkedHashSet<String> usedIDs;

    // Set of schemata not contained in a schema group
    private LinkedHashSet<XSDSchema> otherSchemata;

    // NamespaceAbbreviationMap maps namespaces to abbreviations
    private LinkedHashMap<String, String> namespaceAbbreviationMap;

    // Directory where the new output schemata will be stored (Default is ""c:\")
    private String workingDirectory = "c:/";

    /**
     * Constructor of the SchemaDifferenceGenerator class. All parameter are
     * used to generate a new output schema.
     *
     * @param minuendSchema XSDSchema from which the subtrahend schema is
     * ground-off.
     * @param subtrahendSchema XSDSchema which is ground-off from the minuend
     * schema.
     * @param namespaceOutputSchemaMap Map mapping namespaces to output
     * schemata.
     * @param namespaceOldSchemaMap Map mapping target namespaces to old minuend
     * schemata used to construct the corresponding output schema.
     * @param namespaceConflictingElementsMap Map mapping namespaces to elements
     * which need to be computed new for the corresponding output schema.
     * @param elementGroupMap Map mapping original elements of the minuend
     * schema to groups containing these elements in the output schema.
     * @param anyAttributeOldSchemaMap Map mapping each any attribute to the old
     * schema, that contains the any attribute.
     * @param namespaceAbbreviationMap NamespaceAbbreviationMap maps namespaces
     * to abbreviations.
     * @param typeOldSchemaMap Map mapping types to old schemata used to
     * construct the new output schema.
     * @param attributeOldSchemaMap  Map mapping attributes to old schemata used
     * to construct the new output schema.
     * @param otherSchemata Set of schemata not contained in a schema group.
     * @param elementOldSchemaMap Map mapping elements to old schemata used to
     * construct the new output schema.
     * @param anyPatternOldSchemaMap Map mapping any patterns to their old
     * schemata.
     * @param usedIDs Set containing all IDs used in all new output schemata.
     * @param workingDirectory Directory where the new output schemata will be
     * stored.
     */
    public SchemaDifferenceGenerator(XSDSchema minuendSchema,
            XSDSchema subtrahendSchema,
            LinkedHashMap<String, XSDSchema> namespaceOutputSchemaMap,
            LinkedHashMap<String, XSDSchema> namespaceOldSchemaMap,
            LinkedHashMap<String, LinkedHashSet<Element>> namespaceConflictingElementsMap,
            LinkedHashMap<Element, Group> elementGroupMap,
            LinkedHashMap<AnyAttribute, XSDSchema> anyAttributeOldSchemaMap,
            LinkedHashMap<Attribute, XSDSchema> attributeOldSchemaMap,
            LinkedHashMap<Type, XSDSchema> typeOldSchemaMap,
            LinkedHashMap<AnyPattern, XSDSchema> anyPatternOldSchemaMap,
            LinkedHashMap<Element, XSDSchema> elementOldSchemaMap,
            LinkedHashSet<String> usedIDs,
            LinkedHashSet<XSDSchema> otherSchemata,
            LinkedHashMap<String, String> namespaceAbbreviationMap,
            String workingDirectory) {

        // Initialize all class fields
        this.minuendSchema = minuendSchema;
        this.subtrahendSchema = subtrahendSchema;
        this.namespaceOutputSchemaMap = namespaceOutputSchemaMap;
        this.namespaceOldSchemaMap = namespaceOldSchemaMap;
        this.namespaceConflictingElementsMap = namespaceConflictingElementsMap;
        this.elementGroupMap = elementGroupMap;
        this.anyAttributeOldSchemaMap = anyAttributeOldSchemaMap;
        this.attributeOldSchemaMap = attributeOldSchemaMap;
        this.typeOldSchemaMap = typeOldSchemaMap;
        this.anyPatternOldSchemaMap = anyPatternOldSchemaMap;
        this.elementOldSchemaMap = elementOldSchemaMap;
        this.usedIDs = usedIDs;
        this.otherSchemata = otherSchemata;
        this.namespaceAbbreviationMap = namespaceAbbreviationMap;
        this.workingDirectory = workingDirectory;
    }

    /**
     * This method uses the subtrahend schema and the minuned schema of the
     * SchemaDifferenceGenerator to generate a new schema. This schema is stored
     * in the given output schema.
     *
     * In order to construct a difference schema first all default values are
     * set to the values of the minuned schema. Then original attribute groups,
     * groups and types are taken from the minuned schema an set in the
     * output schema. After this the new types of the type automaton are set.
     * And at last top-level elements and attributes are set either to the
     * minuend elements/attributes or to new elements/attributes generated for
     * conflicting elements/attributes.
     *
     * @param outputSchema XSDSchema which is the container of the new schema
     * generated via difference construction.
     * @throws InvalidTypeStateContentException Exception that is thrown if a
     * type automaton state contains invalid content.
     * @throws NonDeterministicFiniteAutomataException Exception which is thrown
     * if a particle automaton is no deterministic finite automaton.
     * @throws EmptyProductParticleStateFieldException Exception which is thrown
     * if a product state contains no particle states.
     * @throws EmptySubsetParticleStateFieldException Exception which is thrown
     * if a subset state contains no particle states.
     * @throws UniqueParticleAttributionViolationException Exception which is
     * thrown if the UPA-constraint is violated.
     * @throws NotSupportedParticleAutomatonException Exception which is thrown
     * if a particle automaton is not supported by a specified method.
     * @throws NoUniqueStateNumbersException Exception which is thrown if two
     * states with the same state number exist in the specified automaton.
     * @throws NoDestinationStateFoundException Exception which is thrown if two
     * states with the same state number exist in the specified automaton.
     * @throws EmptyProductTypeStateFieldException Exception which is thrown
     * if a product state contains no type states.
     * @throws EmptySubsetTypeStateFieldException Exception which is thrown if a
     * subset type state contains no type states.
     */
    public void generateDifference(XSDSchema outputSchema) throws InvalidTypeStateContentException, EmptyProductTypeStateFieldException, NonDeterministicFiniteAutomataException, EmptySubsetParticleStateFieldException, UniqueParticleAttributionViolationException, EmptyProductParticleStateFieldException, NoUniqueStateNumbersException, NoDestinationStateFoundException, NotSupportedParticleAutomatonException, EmptySubsetTypeStateFieldException {

        // Set default values of the new output schema to the values of the minuend schema. These values are not change due to the fact that figuratively speaking the
        // output schema is the minuend schema minus the subtrahend schema.

        // Create new attributeFormDefault attribute for the output schema, which has the value unqualified
        XSDSchema.Qualification attributeFormDefault = generateNewAttributeFormDefaultAttribute();
        outputSchema.setAttributeFormDefault(attributeFormDefault);

        // Create new elementFormDefault attribute for the output schema, which has the value unqualified
        XSDSchema.Qualification elementFormDefault = generateNewElementFormDefaultAttribute();
        outputSchema.setElementFormDefault(elementFormDefault);

        // Create new finalDefault attribute for the output schema, which contains only FinalDefaults contained in each schema of the schema set
        LinkedHashSet<XSDSchema.FinalDefault> finalDefault = generateNewFinalDefaultAttribute();
        outputSchema.setFinalDefaults(finalDefault);

        // Create new blockDefault attribute for the output schema, which contains only BlockDefault contained in each schema of the schema set
        LinkedHashSet<XSDSchema.BlockDefault> blockDefault = generateNewBlockDefaultAttribute();
        outputSchema.setBlockDefaults(blockDefault);

        LinkedHashSet<XSDSchema> schemata = new LinkedHashSet<XSDSchema>();
        schemata.add(minuendSchema);
        schemata.add(subtrahendSchema);

        // Generate new type automaton factory, which is used to construct a new product type automaton for the schema set
        TypeAutomatonFactory typeAutomatonFactory = new TypeAutomatonFactory();

        // Build for each schema a type automaton
        LinkedList<TypeAutomaton> typeAutomatons = new LinkedList<TypeAutomaton>();
        for (Iterator<XSDSchema> it = schemata.iterator(); it.hasNext();) {
            XSDSchema schema = it.next();

            if (schema != null) {
                typeAutomatons.add(typeAutomatonFactory.buildTypeAutomaton(schema, anyPatternOldSchemaMap));
            }
        }

        // Build product automaton for the type automaton list
        ProductTypeAutomaton productTypeAutomaton = typeAutomatonFactory.buildProductTypeAutomaton(typeAutomatons);

        // Map mapping new types to old minuned types
        LinkedHashMap<Type, Type> newTypeOldTypeMap = new LinkedHashMap<Type, Type>();

        // Create map mapping product particle states to type names of the new output schema
        LinkedHashMap<ProductTypeState, String> stateTypeNameMap = new LinkedHashMap<ProductTypeState, String>();

        // Create map mapping product particle states to old type names of the new output schema
        LinkedHashMap<ProductTypeState, String> stateOldTypeNameMap = new LinkedHashMap<ProductTypeState, String>();

        // Create for each state of the product automaton, with exception of the start state, a type stub
        for (Iterator<TypeState> it = productTypeAutomaton.getStates().iterator(); it.hasNext();) {
            State state = it.next();

            // Check if the current state is the start state of the automaton, which contains no types and does not correspond to a type
            if (state != productTypeAutomaton.getStartState()) {
                ProductTypeState productTypeState = (ProductTypeState) state;

                // If the minuend state is empty no type needs to be generated
                if (productTypeState.getTypeStates().getFirst() != null) {

                    // Get name of the new type (if anyType is contained it is anytype etc.)
                    String typeName = getOldTypeName(productTypeState.getTypeStates().getFirst(), outputSchema);

                    // Update stateTypeNameMap
                    stateOldTypeNameMap.put(productTypeState, typeName);

                    // Create type stub in the output schema
                    if (!outputSchema.getTypeSymbolTable().hasReference(typeName)) {
                        outputSchema.getTypeSymbolTable().updateOrCreateReference(typeName, null);
                    }

                    // Get type names of old types
                    if (productTypeState.getTypeStates().getLast() != null) {

                        // Get name of the new type (if anyType is contained it is anytype etc.)
                        typeName = getTypeName(productTypeState, outputSchema);

                        // Update stateTypeNameMap
                        stateTypeNameMap.put(productTypeState, typeName);

                        // Create type stub in the output schema
                        if (!outputSchema.getTypeSymbolTable().hasReference(typeName)) {
                            outputSchema.getTypeSymbolTable().updateOrCreateReference(typeName, null);
                        }
                    }
                }
            }
        }

        // Map mapping top-level elements to references of their types.
        LinkedHashMap<String, SymbolTableRef<Type>> topLevelElementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();

        // For each outgoing transition of the start state and for each element name annotated to these transitions update the topLevelElementTypeMap
        for (Transition outTransition : productTypeAutomaton.getStartState().getOutTransitions()) {
            for (String elementName : outTransition.getNameElementMap().keySet()) {

                if (((ProductTypeState) outTransition.getDestinationState()).getTypeStates().getFirst() != null) {

                    // Get name of the reached type
                    String typeName = null;

                    // Set new type name depending on if the product type state contains an subtrahend state
                    if (((ProductTypeState) outTransition.getDestinationState()).getTypeStates().getLast() != null) {
                        typeName = stateTypeNameMap.get((ProductTypeState) outTransition.getDestinationState());
                    } else {
                        typeName = stateOldTypeNameMap.get((ProductTypeState) outTransition.getDestinationState());
                    }

                    // Put new entry into the elementTypeMap, for the current element namen and the corresponding type
                    SimpleType stubSimpleType = new SimpleType(typeName, null);
                    stubSimpleType.setIsAnonymous(isAnonymous(((ProductTypeState) outTransition.getDestinationState()).getTypes(), outputSchema));

                    if (!stubSimpleType.getNamespace().equals("http://www.w3.org/2001/XMLSchema")) {
                        stubSimpleType.setDummy(true);
                    }
                    topLevelElementTypeMap.put(elementName, outputSchema.getTypeSymbolTable().updateOrCreateReference(typeName, stubSimpleType));
                }
            }
        }

        // Get map and type set to store attribute list of complexTypes, which may contain empty content
        LinkedHashMap<Type, LinkedList<AttributeParticle>> newTypeNewAttributesMap = new LinkedHashMap<Type, LinkedList<AttributeParticle>>();
        LinkedHashSet<Type> possibleEmptyTypes = new LinkedHashSet<Type>();

        // Initialize new generator classes
        ParticleDifferenceGenerator particleDifferenceGenerator = null;
        AttributeParticleDifferenceGenerator attributeParticleDifferenceGenerator = null;
        TypeDifferenceGenerator typeDifferenceGenerator = null;

        // New ParticleDifferenceGenerator has to know the namespaceConflictingElementsMap and elementGroupMap for ElementRef handling.
        particleDifferenceGenerator = new ParticleDifferenceGenerator(outputSchema, namespaceOutputSchemaMap, namespaceConflictingElementsMap, elementGroupMap, anyPatternOldSchemaMap, elementOldSchemaMap, topLevelElementTypeMap, otherSchemata, namespaceAbbreviationMap, workingDirectory, usedIDs);

        // New AttributeParticleDifferenceGenerator
        attributeParticleDifferenceGenerator = new AttributeParticleDifferenceGenerator(outputSchema, namespaceOutputSchemaMap, namespaceOldSchemaMap, anyAttributeOldSchemaMap, attributeOldSchemaMap, usedIDs, namespaceAbbreviationMap, otherSchemata, workingDirectory);

        // New TypeDifferenceGenerator has to know all four HashMaps for AttributeRef and ElementRef handling.
        typeDifferenceGenerator = new TypeDifferenceGenerator(outputSchema, namespaceOutputSchemaMap, namespaceOldSchemaMap, namespaceConflictingElementsMap, elementGroupMap, attributeParticleDifferenceGenerator, particleDifferenceGenerator, usedIDs, typeOldSchemaMap, newTypeNewAttributesMap, possibleEmptyTypes);

        // Set type generator for other generators
        particleDifferenceGenerator.setTypeDifferenceGenerator(typeDifferenceGenerator);
        attributeParticleDifferenceGenerator.setTypeDifferenceGenerator(typeDifferenceGenerator);

        // Each state of the product automaton, with exception of the start state, corresponds to a type of the output schema, these types are now generated
        for (Iterator<TypeState> it = productTypeAutomaton.getStates().iterator(); it.hasNext();) {
            ProductTypeState productTypeState = (ProductTypeState) it.next();

            // Check if the current state is the start state of the automaton, which contains no types and does not correspond to a type
            if (productTypeState != productTypeAutomaton.getStartState()) {

                // Map mapping element names of the content model to type references, used for these elements
                LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();

                // Map mapping element names of the content model to old type references, used for these elements
                LinkedHashMap<String, SymbolTableRef<Type>> elementOldTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();

                // For each outgoing transition of the current state and for each element name annotated to these transitions update the elementTypeMap
                for (Transition outTransition : productTypeState.getOutTransitions()) {
                    for (String elementName : outTransition.getNameElementMap().keySet()) {

                        if (((ProductTypeState) outTransition.getDestinationState()).getTypeStates().getFirst() != null) {

                            // Get name of the reached type
                            String typeName = stateOldTypeNameMap.get((ProductTypeState) outTransition.getDestinationState());

                            // Put new entry into the elementTypeMap, for the current element namen and the corresponding type
                            SimpleType stubSimpleType = new SimpleType(typeName, null);
                            stubSimpleType.setIsAnonymous(isAnonymous(((ProductTypeState) outTransition.getDestinationState()).getTypes(), outputSchema));

                            if (!stubSimpleType.getNamespace().equals("http://www.w3.org/2001/XMLSchema")) {
                                stubSimpleType.setDummy(true);
                            }

                            // Generate type stub for elements and "type" attribute computation
                            if (outputSchema.getTypeSymbolTable().getReference(typeName).getReference() != null) {
                                elementOldTypeMap.put(elementName, outputSchema.getTypeSymbolTable().getReference(typeName));
                            } else {
                                elementOldTypeMap.put(elementName, outputSchema.getTypeSymbolTable().updateOrCreateReference(typeName, stubSimpleType));
                            }

                            // Generate second type stub for product type with subtrahend type
                            if (((ProductTypeState) outTransition.getDestinationState()).getTypeStates().getLast() != null) {
                                // Get name of the reached type
                                typeName = stateTypeNameMap.get((ProductTypeState) outTransition.getDestinationState());

                                // Put new entry into the elementTypeMap, for the current element namen and the corresponding type
                                stubSimpleType = new SimpleType(typeName, null);
                                stubSimpleType.setIsAnonymous(isAnonymous(((ProductTypeState) outTransition.getDestinationState()).getTypes(), outputSchema));

                                if (!stubSimpleType.getNamespace().equals("http://www.w3.org/2001/XMLSchema")) {
                                    stubSimpleType.setDummy(true);
                                }

                                // Generate type stub for elements and "type" attribute computation
                                if (outputSchema.getTypeSymbolTable().getReference(typeName).getReference() != null) {
                                    elementTypeMap.put(elementName, outputSchema.getTypeSymbolTable().getReference(typeName));
                                } else {
                                    elementTypeMap.put(elementName, outputSchema.getTypeSymbolTable().updateOrCreateReference(typeName, stubSimpleType));
                                }
                            }
                        }
                    }
                }

                // Get type name for the current type
                String newTypeName = stateTypeNameMap.get(productTypeState);

                // Check if output schema contains type stub and only a stub for the given type
                if (outputSchema.getTypeSymbolTable().hasReference(newTypeName) && outputSchema.getTypeSymbolTable().getReference(newTypeName) != null) {

                    // Get minuend type of the minuend type state
                    TypeState minuendState = productTypeState.getTypeStates().getFirst();
                    Type minuendType = null;

                    // Get subtrahend type of the subtrahend type state
                    TypeState subtrahendState = productTypeState.getTypeStates().getLast();                 
                    Type subtrahendType = null;

                    // Check if minuend type exists
                    if (minuendState != null) {
                        minuendType = minuendState.getTypes().iterator().next();
                    }

                    // Check if subtrahend type exists
                    if (subtrahendState != null) {
                        subtrahendType = subtrahendState.getTypes().iterator().next();
                    }

                    // Generate new type for the given automaton state
                    if (minuendState != null && subtrahendState != null) {
                        newTypeOldTypeMap.put(typeDifferenceGenerator.generateNewType(minuendType, subtrahendType, newTypeName, elementTypeMap, elementOldTypeMap), minuendType);
                    }
                }

                // Get old type name for the current old type
                String newOldTypeName = stateOldTypeNameMap.get(productTypeState);

                // Check if output schema contains type stub and only a stub for the given type
                if (outputSchema.getTypeSymbolTable().hasReference(newOldTypeName) && outputSchema.getTypeSymbolTable().getReference(newOldTypeName) != null) {

                    // Get minuend type of the minuend type state
                    TypeState minuendState = productTypeState.getTypeStates().getFirst();
                    Type minuendType = null;

                    // Check if minuend type exists
                    if (minuendState != null) {
                        minuendType = minuendState.getTypes().iterator().next();
                    }

                    // Generate new type for the given automaton state
                    if (minuendState != null) {
                        typeDifferenceGenerator.generateNewType(minuendType, null, newOldTypeName, elementTypeMap, elementOldTypeMap);
                    }
                }
            }
        }

        // Remove empty type definitions from schema
        for (Iterator<XSDSchema> it = otherSchemata.iterator(); it.hasNext();) {
            XSDSchema otherSchema = it.next();
            removeEmptyTypes(otherSchema, typeDifferenceGenerator, particleDifferenceGenerator, attributeParticleDifferenceGenerator, newTypeOldTypeMap, newTypeNewAttributesMap, possibleEmptyTypes);
        }
        removeEmptyTypes(outputSchema, typeDifferenceGenerator, particleDifferenceGenerator, attributeParticleDifferenceGenerator, newTypeOldTypeMap, newTypeNewAttributesMap, possibleEmptyTypes);

        // Get new top-level elements
        for (Iterator<Element> it = minuendSchema.getElements().iterator(); it.hasNext();) {
            Element minuendTopLevelElement = it.next();
            Element subtrahendElement = null;

            // Check subtrahend schema for elements
            for (Iterator<Element> it2 = subtrahendSchema.getElements().iterator(); it2.hasNext();) {
                Element subtrahendTopLevelElement = it2.next();

                if (subtrahendTopLevelElement.getName().equals(minuendTopLevelElement.getName())) {
                    subtrahendElement = subtrahendTopLevelElement;
                }
            }

            // Generate new top-level element
            particleDifferenceGenerator.generateNewTopLevelElement(minuendTopLevelElement, subtrahendElement, topLevelElementTypeMap.get(minuendTopLevelElement.getName()));
        }

        // Generate new top-level attributes for each top-level attribute of the minuned schema
        for (Iterator<Attribute> it = minuendSchema.getAttributes().iterator(); it.hasNext();) {
            Attribute topLevelAttribute = it.next();
            attributeParticleDifferenceGenerator.generateNewTopLevelAttribute(topLevelAttribute);
        }

        // Remove unused type definitions from schema
        for (Iterator<XSDSchema> it = otherSchemata.iterator(); it.hasNext();) {
            XSDSchema otherSchema = it.next();
            removeUnusedTypes(otherSchema);
        }
        removeUnusedTypes(outputSchema);
    }

    /**
     * Method removes all unused types from the specified schema. A type is
     * unused if the type definition is not used in an element, an attribute or
     * another type.
     *
     * @param outputSchema XSDSchema for which the unused types are removed.
     */
    private void removeUnusedTypes(XSDSchema outputSchema) {
        LinkedHashSet<Type> usedTypes = new LinkedHashSet<Type>();

        // Get all referenced types in the current schema
        for (Iterator<Type> it3 = outputSchema.getTypeSymbolTable().getAllReferencedObjects().iterator(); it3.hasNext();) {
            Type type = it3.next();

            // If the referred type has the same namespace as the schema add type to set
            if (type.getNamespace().equals(outputSchema.getTargetNamespace())) {

                // Check if type is compleType
                if (type instanceof ComplexType) {
                    ComplexType complexType = (ComplexType) type;

                    // Get attribute particles contained in the compleType
                    for (Iterator<AttributeParticle> it4 = complexType.getAttributes().iterator(); it4.hasNext();) {
                        AttributeParticle attributeParticle = it4.next();

                        // Check that attribute particle is attribute and add the type to the set
                        if (attributeParticle instanceof Attribute) {
                            usedTypes.add(((Attribute) attributeParticle).getSimpleType());
                        }
                    }

                    // If complexType has complex content check content for elements and attributes
                    if (complexType.getContent() instanceof ComplexContentType) {
                        ComplexContentType complexContentType = (ComplexContentType) complexType.getContent();

                        if (complexContentType.getInheritance() != null) {
                            if (complexContentType.getInheritance().getAttributes() != null) {

                                // Get attribute particles contained in the complex content inheritance
                                for (Iterator<AttributeParticle> it4 = complexContentType.getInheritance().getAttributes().iterator(); it4.hasNext();) {
                                    AttributeParticle attributeParticle = it4.next();

                                    // Check that attribute particle is attribute and add the type to the set
                                    if (attributeParticle instanceof Attribute) {
                                        usedTypes.add(((Attribute) attributeParticle).getSimpleType());
                                    }
                                }
                            }
                        }

                        // Check if particle is present
                        if (complexContentType.getParticle() != null) {

                            // Get elements contained in the complex content
                            for (Iterator<Element> it4 = getContainedElements(complexContentType.getParticle()).iterator(); it4.hasNext();) {
                                Element element = it4.next();
                                usedTypes.add(element.getType());
                            }
                        }
                    }

                    // If complexType has simple content check content for attributes
                    if (complexType.getContent() instanceof SimpleContentType) {
                        SimpleContentType simpleContentType = (SimpleContentType) complexType.getContent();

                        // Check if content inheritance is an extension
                        if (simpleContentType.getInheritance() instanceof SimpleContentExtension) {
                            SimpleContentExtension simpleContentExtension = (SimpleContentExtension) simpleContentType.getInheritance();
                            usedTypes.add(simpleContentExtension.getBase());

                            // Get attribute particles contained in the extension
                            for (Iterator<AttributeParticle> it4 = simpleContentExtension.getAttributes().iterator(); it4.hasNext();) {
                                AttributeParticle attributeParticle = it4.next();

                                // Check that attribute particle is attribute and add the type to the set
                                if (attributeParticle instanceof Attribute) {
                                    usedTypes.add(((Attribute) attributeParticle).getSimpleType());
                                }
                            }
                        }

                        // Check if content inheritance is a restriction
                        if (simpleContentType.getInheritance() instanceof SimpleContentRestriction) {
                            SimpleContentRestriction simpleContentRestriction = (SimpleContentRestriction) simpleContentType.getInheritance();
                            usedTypes.add(simpleContentRestriction.getBase());

                            // Get attribute particles contained in the restriction
                            for (Iterator<AttributeParticle> it4 = simpleContentRestriction.getAttributes().iterator(); it4.hasNext();) {
                                AttributeParticle attributeParticle = it4.next();

                                // Check that attribute particle is attribute and add the type to the set
                                if (attributeParticle instanceof Attribute) {
                                    usedTypes.add(((Attribute) attributeParticle).getSimpleType());
                                }
                            }
                        }
                    }
                }

                // Check types that are simpleType
                if (type instanceof SimpleType) {
                    SimpleType simpleType = (SimpleType) type;

                    // Check member types of a simple content union
                    if (simpleType.getInheritance() instanceof SimpleContentUnion) {
                        SimpleContentUnion simpleContentUnion = (SimpleContentUnion) simpleType.getInheritance();

                        for (Iterator<SymbolTableRef<Type>> it = simpleContentUnion.getAllMemberTypes().iterator(); it.hasNext();) {
                            SymbolTableRef<Type> symbolTableRef = it.next();
                            usedTypes.add(symbolTableRef.getReference());
                        }
                    }

                    // Check item type of a simple content list
                    if (simpleType.getInheritance() instanceof SimpleContentList) {
                        SimpleContentList simpleContentList = (SimpleContentList) simpleType.getInheritance();
                        usedTypes.add(simpleContentList.getBase());
                    }

                    // Check item types of a simple content restriction
                    if (simpleType.getInheritance() instanceof SimpleContentRestriction) {
                        SimpleContentRestriction simpleContentRestriction = (SimpleContentRestriction) simpleType.getInheritance();
                        usedTypes.add(simpleContentRestriction.getBase());
                    }
                }
            }
        }

        // Check top-level elements for types
        for (Iterator<Element> it3 = outputSchema.getElements().iterator(); it3.hasNext();) {
            Element topLevelElement = it3.next();
            usedTypes.add(topLevelElement.getType());
        }

        // Check top-level attributes for types
        for (Iterator<Attribute> it3 = outputSchema.getAttributes().iterator(); it3.hasNext();) {
            Attribute topLevelAttribute = it3.next();
            usedTypes.add(topLevelAttribute.getSimpleType());
        }

        // Remove unused types from list and symbolTable
        for (Iterator<Type> it = outputSchema.getTypeSymbolTable().getAllReferencedObjects().iterator(); it.hasNext();) {
            Type type = it.next();

            if (type != null && !usedTypes.contains(type)) {
                outputSchema.removeType(outputSchema.getTypeSymbolTable().getReference(type.getName()));
                outputSchema.getTypeSymbolTable().removeReference(type.getName());
            }
        }
    }

    /**
     * Removes empty type definitions from specified schema.
     *
     * @param outputSchema XSDSchema for which empty types are removed.
     * @param typeDifferenceGenerator Generator used to generate new types.
     * @param particleDifferenceGenerator Generator used to generate new 
     * particles.
     * @param attributeParticleDifferenceGenerator Generator used to generate
     * new attributes.
     * @param newTypeOldTypeMap Map mapping new types to old types.
     * @param newTypeNewAttributesMap Map mapping new types to new attribute
     * lists.
     * @param possibleEmptyTypes Types which may be empty, due to content
     * removal.
     * @throws EmptySubsetParticleStateFieldException Exception which is thrown
     * if a subset state contains no particle states.
     */
    private void removeEmptyTypes(XSDSchema outputSchema, TypeDifferenceGenerator typeDifferenceGenerator, ParticleDifferenceGenerator particleDifferenceGenerator, AttributeParticleDifferenceGenerator attributeParticleDifferenceGenerator, LinkedHashMap<Type, Type> newTypeOldTypeMap, LinkedHashMap<Type, LinkedList<AttributeParticle>> newTypeNewAttributesMap, LinkedHashSet<Type> possibleEmptyTypes) throws EmptySubsetParticleStateFieldException {

        // Use boolean variable to check if repair is needed
        boolean needsRepair = true;

        // As long as not all types are valid repair schema
        while (needsRepair) {
            needsRepair = false;

            // Get all referenced types in the current schema
            for (Iterator<Type> it3 = outputSchema.getTypeSymbolTable().getAllReferencedObjects().iterator(); it3.hasNext();) {
                Type type = it3.next();

                // If the referred type has the same namespace as the schema check type
                if (type.getNamespace().equals(outputSchema.getTargetNamespace())) {

                    // Check if type is compleType
                    if (type instanceof ComplexType) {
                        ComplexType complexType = (ComplexType) type;

                        // If complexType has complex content check content for elements
                        if (complexType.getContent() instanceof ComplexContentType) {
                            ComplexContentType complexContentType = (ComplexContentType) complexType.getContent();

                            // If complexType has a particle
                            if (complexContentType.getParticle() != null) {

                                // and if the particle is optional
                                if (typeDifferenceGenerator.isOptionalParticle(complexContentType.getParticle())) {

                                    // check if the particle has to be removed
                                    if (checkParticle(complexContentType.getParticle())) {
                                        complexContentType.setParticle(null);
                                    } else {

                                        // Generate new type content if neither attribute nor element content is null
                                        if (newTypeNewAttributesMap.get(complexType) != null && possibleEmptyTypes.contains(complexType)) {
                                            complexType.setAttributes(attributeParticleDifferenceGenerator.generateAttributeParticleDifference(((ComplexType) newTypeOldTypeMap.get(complexType)).getAttributes(), null));
                                            complexContentType.setParticle(particleDifferenceGenerator.generateNewParticle(((ComplexContentType) ((ComplexType) newTypeOldTypeMap.get(complexType)).getContent()).getParticle()));
                                        }
                                    }
                                } else {

                                    // and if the particle is not optional and has to be removed the current type is set to empty
                                    if (checkParticle(complexContentType.getParticle())) {

                                        // Generate new type content if attribute is not null
                                        if (newTypeNewAttributesMap.get(complexType) != null) {
                                            complexType.setAttributes(newTypeNewAttributesMap.get(type));
                                            complexContentType.setParticle(particleDifferenceGenerator.generateNewParticle(((ComplexContentType) ((ComplexType) newTypeOldTypeMap.get(complexType)).getContent()).getParticle()));
                                        } else {
                                            complexType.setDummy(true);
                                            needsRepair = true;
                                        }
                                    } else {

                                        // Generate new type content if neither attribute nor element content is null
                                        if (newTypeNewAttributesMap.get(complexType) != null && possibleEmptyTypes.contains(complexType)) {
                                            complexType.setAttributes(attributeParticleDifferenceGenerator.generateAttributeParticleDifference(((ComplexType) newTypeOldTypeMap.get(complexType)).getAttributes(), null));
                                            complexContentType.setParticle(particleDifferenceGenerator.generateNewParticle(((ComplexContentType) ((ComplexType) newTypeOldTypeMap.get(complexType)).getContent()).getParticle()));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Remove groups without content
            for (Iterator<Group> it = outputSchema.getGroups().iterator(); it.hasNext();) {
                Group group = it.next();

                // Get element contained in group
                Element element = (Element) group.getParticleContainer().getParticles().getFirst();

                if (element.getType() == null || element.getType().isDummy()) {
                    outputSchema.removeGroup(outputSchema.getGroupSymbolTable().getReference(group.getName()));
                    outputSchema.getGroupSymbolTable().removeReference(group.getName());
                }
            }

            // Remove all empty types from symbolTable and list
            for (Iterator<Type> it3 = outputSchema.getTypeSymbolTable().getAllReferencedObjects().iterator(); it3.hasNext();) {
                Type type = it3.next();

                if (type.isDummy()) {
                    outputSchema.removeType(outputSchema.getTypeSymbolTable().getReference(type.getName()));
                    outputSchema.getTypeSymbolTable().removeReference(type.getName());
                }
            }
        }
    }

    /**
     * Remove particles with empty types from the current particle and check if
     * specified particle has to be removed.
     *
     * @param particle Particle which may contain other particles. Particles can
     * contain empty types, which are removed along with their particles.
     * @return <tt>true<tt/> if the specified particle has to be removed.
     */
    private boolean checkParticle(Particle particle) {

        // If the current particle is an element and the type definition of this element is empty remove element
        if (particle instanceof Element && (((Element) particle).getType() == null || ((Element) particle).getType().isDummy())) {
            return true;
        } else if (particle instanceof SequencePattern) {
            SequencePattern sequencePattern = (SequencePattern) particle;

            // If a particle in the sequence has to be removed the whole sequence is removed
            LinkedList<Particle> particles = sequencePattern.getParticles();
            for (Iterator<Particle> it = particles.iterator(); it.hasNext();) {
                Particle currentParticle = it.next();

                // Check if the contained particle is empty
                if (checkParticle(currentParticle)) {

                    // Check if counting pattern allows optional use
                    if (particle instanceof CountingPattern) {
                        CountingPattern countingPattern = (CountingPattern) particle;

                        if (countingPattern.getMin() != 0) {
                            return true;
                        } else {
                            it.remove();
                        }
                    } else {
                        return true;
                    }
                }
            }
            // If sequence is empty afterwards remove choice
            if (particles.isEmpty()) {
                return true;
            } else {
                sequencePattern.setParticles(particles);
            }
        } else if (particle instanceof ChoicePattern) {
            ChoicePattern choicePattern = (ChoicePattern) particle;

            // If a particle in a choice has to be removed remove only this particle from the choice
            LinkedList<Particle> particles = choicePattern.getParticles();
            for (Iterator<Particle> it = particles.iterator(); it.hasNext();) {
                Particle currentParticle = it.next();

                // Check if the contained particle is empty
                if (checkParticle(currentParticle)) {
                    it.remove();
                }
            }

            // If choice is empty afterwards remove choice
            if (particles.isEmpty()) {
                return true;
            } else {
                choicePattern.setParticles(particles);
            }
        } else if (particle instanceof CountingPattern) {
            CountingPattern countingPattern = (CountingPattern) particle;
            Particle containedParticle = countingPattern.getParticles().getFirst();

            // Check if contained particle is removed
            if (checkParticle(containedParticle)) {
                return true;
            }
        } else if (particle instanceof GroupRef) {
            Group group = (Group) ((GroupRef) particle).getGroup();

            // Get element contained in group
            Element element = (Element) group.getParticleContainer().getParticles().getFirst();

            if (element.getType() == null || element.getType().isDummy()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get all elements contained in the specified particle.
     *
     * @param particle Particle which is the root of the particle structure.
     * @return Set containing all elements contained in the specified particle.
     */
    private LinkedHashSet<Element> getContainedElements(Particle particle) {

        // Initialize set which will contain all elements contained in the specified particle
        LinkedHashSet<Element> containedElements = new LinkedHashSet<Element>();

        // Check if the particle is an element and add it to the set
        if (particle instanceof Element) {
            containedElements.add((Element) particle);
        } else if (particle instanceof ParticleContainer) {
            ParticleContainer particleContainer = (ParticleContainer) particle;

            // Check if the particle is a particle container and add elements contained in the container to the set
            LinkedList<Particle> particles = particleContainer.getParticles();
            for (Iterator<Particle> it = particles.iterator(); it.hasNext();) {
                Particle currentParticle = it.next();
                containedElements.addAll(getContainedElements(currentParticle));
            }
        }
        return containedElements;
    }

    /**
     * Generate a new blockDefault attribute for the output schema. This
     * attribute contains only BlockDefault contained in each schema of the
     * schema set.
     *
     * @return New blockDefault attribute, which is a BlockDefault, for the
     * output schema.
     */
    private LinkedHashSet<XSDSchema.BlockDefault> generateNewBlockDefaultAttribute() {

        // Create blockDefault attribute, which contains every possible BlockDefault value
        LinkedHashSet<XSDSchema.BlockDefault> blockDefaults = new LinkedHashSet<XSDSchema.BlockDefault>(minuendSchema.getBlockDefaults());

        LinkedHashSet<XSDSchema> schemata = new LinkedHashSet<XSDSchema>();
        schemata.add(minuendSchema);

        if (subtrahendSchema != null) {
            schemata.add(subtrahendSchema);
        }

        // Use foreign schema resolver to resolve blockDefault attributes in each schema, that does not contain the same blockDefault attribute as the output schema
        ForeignSchemaResolver foreignSchemaResolver = new ForeignSchemaResolver();
        for (Iterator<XSDSchema> it = schemata.iterator(); it.hasNext();) {
            XSDSchema schema = it.next();

            // Check if both blockDefault attributes do not contain the same values
            if (!(schema.getBlockDefaults().containsAll(blockDefaults) && blockDefaults.containsAll(schema.getBlockDefaults()))) {
                foreignSchemaResolver.removeBlockDefault(schema);
            }
        }
        return blockDefaults;
    }

    /**
     * Generate a new finalDefault attribute for the output schema. This
     * attribute contains only FinalDefaults contained in each schema of the
     * schema set.
     *
     * @return New finalDefault attribute, which is a FinalDefault, for the
     * output schema.
     */
    private LinkedHashSet<XSDSchema.FinalDefault> generateNewFinalDefaultAttribute() {

        // Create finalDefault attribute, which contains every possible FinalDefault value
        LinkedHashSet<XSDSchema.FinalDefault> finalDefaults = new LinkedHashSet<XSDSchema.FinalDefault>(minuendSchema.getFinalDefaults());

        // Get schema set and add minuend schema
        LinkedHashSet<XSDSchema> schemata = new LinkedHashSet<XSDSchema>();
        schemata.add(minuendSchema);

        // Check  if subtrahend schema is present and add it to the set if possible
        if (subtrahendSchema != null) {
            schemata.add(subtrahendSchema);
        }

        // Use foreign schema resolver to resolve finalDefault attributes in each schema, that does not contain the same finalDefault attribute as the output schema
        ForeignSchemaResolver foreignSchemaResolver = new ForeignSchemaResolver();
        for (Iterator<XSDSchema> it = schemata.iterator(); it.hasNext();) {
            XSDSchema schema = it.next();

            // Check if both finalDefault attributes do not contain the same values
            if (!(schema.getFinalDefaults().containsAll(finalDefaults) && finalDefaults.containsAll(schema.getFinalDefaults()))) {
                foreignSchemaResolver.removeFinalDefault(schema);
            }
        }
        return finalDefaults;
    }

    /**
     * Generate a new elementFormDefault attribute for the output schema.
     * (Per default this attribute has the value unqualified, which is the
     * default, if no attribute is present.)
     *
     * @return New elementFormDefault attribute, which is a Qualification, for
     * the output schema.
     */
    private XSDSchema.Qualification generateNewElementFormDefaultAttribute() {
        boolean isQualified = false;

        // Get schema set and add minuend schema
        LinkedHashSet<XSDSchema> schemata = new LinkedHashSet<XSDSchema>();
        schemata.add(minuendSchema);

        // Check  if subtrahend schema is present and add it to the set if possible
        if (subtrahendSchema != null) {
            schemata.add(subtrahendSchema);
        }

        // For all schemata check the contained elementFormDefault attribute values
        for (Iterator<XSDSchema> it = schemata.iterator(); it.hasNext();) {
            XSDSchema schema = it.next();

            // Check if attributes in schema are per default qualified
            if (schema.getElementFormDefault() != null && schema.getElementFormDefault() == XSDSchema.Qualification.qualified) {
                isQualified = true;
            }
        }

        // If a single schema contains an elementFormDefault attribute with value qualified resolve elementFormDefault in all schemata
        if (isQualified) {

            // Use foreign schema resolver to resolve elementFormDefault attributes in each schema
            ForeignSchemaResolver foreignSchemaResolver = new ForeignSchemaResolver();
            for (Iterator<XSDSchema> it = schemata.iterator(); it.hasNext();) {
                XSDSchema schema = it.next();
                foreignSchemaResolver.removeElementFormDefault(schema);
            }
        }

        // New output schema has no elementFormDefault attribute per default
        return XSDSchema.Qualification.unqualified;
    }

    /**
     * Generate a new attributeFormDefault attribute for the output schema.
     * (Per default this attribute has the value unqualified, which is the
     * default, if no attribute is present.)
     *
     * @return New attributeFormDefault attribute, which is a Qualification, for
     * the output schema.
     */
    private XSDSchema.Qualification generateNewAttributeFormDefaultAttribute() {
        boolean isQualified = false;

        // Get schema set and add minuend schema
        LinkedHashSet<XSDSchema> schemata = new LinkedHashSet<XSDSchema>();
        schemata.add(minuendSchema);

        // Check  if subtrahend schema is present and add it to the set if possible
        if (subtrahendSchema != null) {
            schemata.add(subtrahendSchema);
        }

        // For all schemata check the contained attributeFormDefault attribute values
        for (Iterator<XSDSchema> it = schemata.iterator(); it.hasNext();) {
            XSDSchema schema = it.next();

            // Check if attributes in schema are per default qualified
            if (schema.getAttributeFormDefault() != null && schema.getAttributeFormDefault() == XSDSchema.Qualification.qualified) {
                isQualified = true;
            }
        }

        // If a single schema contains an attributeFormDefault attribute with value qualified resolve attributeFormDefault in all schemata
        if (isQualified) {

            // Use foreign schema resolver to resolve attributeFormDefault attributes in each schema
            ForeignSchemaResolver foreignSchemaResolver = new ForeignSchemaResolver();
            for (Iterator<XSDSchema> it = schemata.iterator(); it.hasNext();) {
                XSDSchema schema = it.next();
                foreignSchemaResolver.removeAttributeFormDefault(schema);
            }
        }

        // New output schema has no attributeFormDefault attribute per default
        return XSDSchema.Qualification.unqualified;
    }

    /**
     * This method checks whether the type, generate from the specified type
     * list, is anonymous or not. It is anonymous if all types contained in the
     * type list are anoymous.
     *
     * @param types List of types used to construct a new type.
     * @param outputSchema XSDSchema which is the container of the new schema
     * generated via difference construction.
     * @return <tt>true</tt> if all specified types are anonymous, else
     * <tt>false</tt>.
     */
    private boolean isAnonymous(LinkedHashSet<Type> types, XSDSchema outputSchema) {

        // If type list contains more than one type the return false
        if (types.size() != 1 || containsStrictAnyPattern(types) || !types.iterator().next().getNamespace().equals(outputSchema.getTargetNamespace())) {
            return false;
        }

        // Check for each type, if the type is anonymous
        for (Iterator<Type> it = types.iterator(); it.hasNext();) {
            Type type = it.next();

            // If a single type is not anonymous return false
            if (!type.isAnonymous()) {
                return false;
            }
        }

        // If all given types are anonymous, true is returned
        return true;
    }

    private boolean containsStrictAnyPattern(Particle particle) {

        // Check if the current particle is a particle
        if (particle instanceof ParticleContainer) {

            // For each particle contained in the particle container check if any patterns are contained
            ParticleContainer particleContainer = (ParticleContainer) particle;
            for (Iterator<Particle> it = particleContainer.getParticles().iterator(); it.hasNext();) {
                Particle containedParticle = it.next();
                return containsStrictAnyPattern(containedParticle);
            }
        } else if (particle instanceof GroupRef) {

            // If particle is a group reference check referenced group
            Group group = (Group) ((GroupRef) particle).getGroup();
            return containsStrictAnyPattern(group.getParticleContainer());

        } else if (particle instanceof AnyPattern && ((AnyPattern) particle).getProcessContentsInstruction().equals(ProcessContentsInstruction.Strict)) {

            // If particle or any pattern return true
            return true;
        }

        // In all other cases return false
        return false;
    }

        /**
     * Checks if a list of types contains a strict validated any pattern
     * @param types List of types, which may contain a strict validated any
     * pattern.
     * @return <tt>true</tt> if the particle contains strict validated any
     * pattern.
     */
    private boolean containsStrictAnyPattern(LinkedHashSet<Type> types) {

        // Check for each type if a strict validates any pattern is contained
        for (Type type : types) {

            // Only complexTypes can contain any patterns
            if (type instanceof ComplexType) {
                ComplexType complexType = (ComplexType) type;

                // Check if compleType contains strict any pattern
                if (complexType.getContent() instanceof ComplexContentType) {
                    ComplexContentType complexContentType = (ComplexContentType) complexType.getContent();
                    return containsStrictAnyPattern(complexContentType.getParticle());
                }
            }
        }
        return false;
    }

    /**
     * Create new type name for the specified product type state, which contains
     * types. The new name is constructed by using the names of these contained
     * types.
     *
     * @param productTypeState State, which contains types.
     * @param outputSchema XSDSchema in which the new type is registered. Used to
     * check which type names already exist.
     * @return New type name for the specified type set not used in the output
     * schema yet.
     */
    private String getTypeName(ProductTypeState productTypeState, XSDSchema outputSchema) {
        TypeState minuendState = productTypeState.getTypeStates().getFirst();
        TypeState subtrahendState = productTypeState.getTypeStates().getLast();

        // Build-in data types are not renamed
        if (minuendState.getTypes().iterator().next().getNamespace().equals("http://www.w3.org/2001/XMLSchema") && (subtrahendState == null || (subtrahendState != null && !minuendState.getTypes().iterator().next().getName().equals(subtrahendState.getTypes().iterator().next().getName())))) {
            return minuendState.getTypes().iterator().next().getName();
        }

        // Get minuend and subtrahend type name
        String minuendTypeName = null;
        String subtrahendTypeName = null;

        // Check if minuend type name exists
        if (minuendState != null) {
            minuendTypeName = minuendState.getTypes().iterator().next().getLocalName();
        }

        // Check if subtrahend type name exists
        if (subtrahendState != null) {
            subtrahendTypeName = subtrahendState.getTypes().iterator().next().getLocalName();
        }

        // Get new name
        String typeName = "{" + outputSchema.getTargetNamespace() + "}difference-type." + minuendTypeName + "-" + subtrahendTypeName;

        // Use type name base and number to find valid type name
        String typeNameBase = typeName;
        int number = 1;

        // As long as the type name already exists check next type name
        while (outputSchema.getTypeSymbolTable().hasReference(typeName)) {

            // Type name consists of a type name base and a number
            typeName = typeNameBase + "." + number;
            number++;
        }
        return typeName;
    }

    private String getOldTypeName(TypeState minuendState, XSDSchema outputSchema) {

        // Build-in data types are not renamed
        if (minuendState.getTypes().iterator().next().getNamespace().equals("http://www.w3.org/2001/XMLSchema")) {
            return minuendState.getTypes().iterator().next().getName();
        }

        // Get minuend and subtrahend type name
        String minuendTypeName = null;
        String subtrahendTypeName = null;

        // Check if minuend type name exists
        if (minuendState != null) {
            minuendTypeName = minuendState.getTypes().iterator().next().getLocalName();
        }

        // Get new name
        String typeName = "{" + outputSchema.getTargetNamespace() + "}difference-type." + minuendTypeName + "-" + subtrahendTypeName;

        // Use type name base and number to find valid type name
        String typeNameBase = typeName;
        int number = 1;

        // As long as the type name already exists check next type name
        while (outputSchema.getTypeSymbolTable().hasReference(typeName)) {

            // Type name consists of a type name base and a number
            typeName = typeNameBase + "." + number;
            number++;
        }
        return typeName;
    }
}
