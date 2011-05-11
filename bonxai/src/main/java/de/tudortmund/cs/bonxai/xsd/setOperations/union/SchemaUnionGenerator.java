package de.tudortmund.cs.bonxai.xsd.setOperations.union;

import de.tudortmund.cs.bonxai.xsd.automaton.TypeAutomatons.*;
import de.tudortmund.cs.bonxai.xsd.automaton.TypeAutomatons.exceptions.*;
import de.tudortmund.cs.bonxai.xsd.automaton.exceptions.*;
import de.tudortmund.cs.bonxai.xsd.tools.ForeignSchemaResolver;
import de.tudortmund.cs.bonxai.common.*;
import de.tudortmund.cs.bonxai.xsd.*;
import de.tudortmund.cs.bonxai.xsd.Element;
import de.tudortmund.cs.bonxai.xsd.Group;
import de.tudortmund.cs.bonxai.xsd.automaton.*;
import java.util.*;

/**
 * This class is responsible for computing new schemata representing the union
 * of sets of schemata. In order to unite differnent Schemata all Schemata
 * should have the same target namespace so that the new XSDSchema has the same
 * target namespace. Furthermore the class supplies all needed methods to
 * unite Schemata and there attributes, like methods to unite default values and
 * so on.
 *
 * @author Dominik Wolff
 */
public class SchemaUnionGenerator {

    // Set containing the minuend schemata used to build the new output schema
    private LinkedHashSet<XSDSchema> minuendSchemata;

    // HashMap mapping namespaces to output schemata (Can be used if an element with a foreign namespace is referenced).
    private LinkedHashMap<String, XSDSchema> namespaceOutputSchemaMap;

    // HashMap mapping namespaces to top-level elements which are present in more than one schema of the schema group with the corresponding namespace
    private LinkedHashMap<String, LinkedHashSet<Element>> namespaceConflictingElementsMap;

    // HashMap mapping namespaces to top-level attributes which are present in more than one schema of the schema group with the corresponding namespace
    private LinkedHashMap<String, LinkedHashSet<Attribute>> namespaceConflictingAttributeMap;

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
     * Constructor of the schema union generator class, which initializes the
     * fields of the class.
     *
     * @param minuendSchemata Set containing the minuend schemata used to build
     * the new output schema.
     * @param namespaceOutputSchemaMap HashMap mapping namespaces to output
     * schemata.
     * @param namespaceConflictingElementsMap HashMap mapping namespaces to
     * top-level elements which are present in more than one schema of the
     * schema group with the corresponding namespace.
     * @param namespaceConflictingAttributeMap HashMap mapping namespaces to
     * top-level attributes which are present in more than one schema of the
     * schema group with the corresponding namespace
     * @param anyAttributeOldSchemaMap Map mapping each any attribute to the old
     * schema, that contains the any attribute.
     * @param attributeOldSchemaMap Map mapping attributes to old schemata
     * used to construct the new output schema.
     * @param typeOldSchemaMap Map mapping types to old schemata used to
     * construct the new output schema.
     * @param anyPatternOldSchemaMap Map mapping any patterns to their old
     * schemata.
     * @param elementOldSchemaMap Map mapping elements to old schemata used to
     * construct the new output schema.
     * @param usedIDs Set containing all IDs used in all new output schemata.
     * @param otherSchemata Set of schemata not contained in a schema group.
     * @param namespaceAbbreviationMap NamespaceAbbreviationMap maps namespaces
     * to abbreviations.
     * @param workingDirectory Directory where the new output schemata will be
     * stored.
     */
    public SchemaUnionGenerator(LinkedHashSet<XSDSchema> minuendSchemata, LinkedHashMap<String, XSDSchema> namespaceOutputSchemaMap, LinkedHashMap<String, LinkedHashSet<Element>> namespaceConflictingElementsMap, LinkedHashMap<String, LinkedHashSet<Attribute>> namespaceConflictingAttributeMap, LinkedHashMap<AnyAttribute, XSDSchema> anyAttributeOldSchemaMap, LinkedHashMap<Attribute, XSDSchema> attributeOldSchemaMap, LinkedHashMap<Type, XSDSchema> typeOldSchemaMap, LinkedHashMap<AnyPattern, XSDSchema> anyPatternOldSchemaMap, LinkedHashMap<Element, XSDSchema> elementOldSchemaMap, LinkedHashSet<String> usedIDs, LinkedHashSet<XSDSchema> otherSchemata, LinkedHashMap<String, String> namespaceAbbreviationMap, String workingDirectory) {

        // Initialize all class fields
        this.minuendSchemata = minuendSchemata;
        this.namespaceOutputSchemaMap = namespaceOutputSchemaMap;
        this.namespaceConflictingElementsMap = namespaceConflictingElementsMap;
        this.namespaceConflictingAttributeMap = namespaceConflictingAttributeMap;
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
     * Generate a new schema union. The result is stored in the specified
     * output schema.
     *
     * @param outputSchema The schema, in which the result of the union is
     * stored.
     * @throws InvalidTypeStateContentException Exception that is thrown if a
     * type automaton state contains invalid content.
     * @throws EmptyProductTypeStateFieldException Exception which is thrown
     * if a product state contains no type states.
     * @throws NonDeterministicFiniteAutomataException Exception which is thrown
     * if a particle automaton is no deterministic finite automaton.
     */
    public void generateUnion(XSDSchema outputSchema) throws InvalidTypeStateContentException, EmptyProductTypeStateFieldException, NonDeterministicFiniteAutomataException {

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

        // Generate new type automaton factory, which is used to construct a new product type automaton for the schema set
        TypeAutomatonFactory typeAutomatonFactory = new TypeAutomatonFactory();

        // Build for each schema a type automaton
        LinkedList<TypeAutomaton> typeAutomatons = new LinkedList<TypeAutomaton>();
        for (Iterator<XSDSchema> it = minuendSchemata.iterator(); it.hasNext();) {
            XSDSchema schema = it.next();
            typeAutomatons.add(typeAutomatonFactory.buildTypeAutomaton(schema, anyPatternOldSchemaMap));
        }

        // Build product automaton for the type automaton list
        ProductTypeAutomaton productTypeAutomaton = typeAutomatonFactory.buildProductTypeAutomaton(typeAutomatons);

        // Get map mapping names to sets of top-level elements
        LinkedHashMap<String, LinkedHashSet<Element>> nameTopLevelElementsMap = new LinkedHashMap<String, LinkedHashSet<Element>>();

        // Update nameTopLevelElementsMap with element names found on outgoing transitions of the product start state
        for (Transition transition : productTypeAutomaton.getStartState().getOutTransitions()) {
            nameTopLevelElementsMap.putAll(transition.getNameElementMap());
        }

        // Create map mapping product particle states to type names of the new output schema
        LinkedHashMap<ProductTypeState, String> stateTypeNameMap = new LinkedHashMap<ProductTypeState, String>();

        // Create for each state of the product automaton, with exception of the start state, a type stub
        for (Iterator<TypeState> it = productTypeAutomaton.getStates().iterator(); it.hasNext();) {
            State state = it.next();

            // Check if the current state is the start state of the automaton, which contains no types and does not correspond to a type
            if (state != productTypeAutomaton.getStartState()) {
                ProductTypeState productTypeState = (ProductTypeState) state;

                // Get name of the new type (if anyType is contained it is anytype etc.)
                String typeName = getTypeName(productTypeState, outputSchema);

                // Update stateTypeNameMap
                stateTypeNameMap.put(productTypeState, typeName);

                // Create type stub in the output schema
                if (!outputSchema.getTypeSymbolTable().hasReference(typeName)) {
                    outputSchema.getTypeSymbolTable().updateOrCreateReference(typeName, null);
                }
            }
        }

        // Map mapping top-level elements to references of their types.
        LinkedHashMap<String, SymbolTableRef<Type>> topLevelElementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();

        // For each outgoing transition of the start state and for each element name annotated to these transitions update the topLevelElementTypeMap
        for (Transition outTransition : productTypeAutomaton.getStartState().getOutTransitions()) {
            for (String elementName : outTransition.getNameElementMap().keySet()) {

                // Get name of the reached type
                String typeName = stateTypeNameMap.get((ProductTypeState) outTransition.getDestinationState());

                // Put new entry into the elementTypeMap, for the current element namen and the corresponding type
                SimpleType stubSimpleType = new SimpleType(typeName, null);
                stubSimpleType.setIsAnonymous(isAnonymous(((ProductTypeState) outTransition.getDestinationState()).getTypes(), outputSchema));
                topLevelElementTypeMap.put(elementName, outputSchema.getTypeSymbolTable().updateOrCreateReference(typeName, stubSimpleType));
            }
        }

        // Initialize new generator classes
        ParticleUnionGenerator particleUnionGenerator = null;
        AttributeParticleUnionGenerator attributeParticleUnionGenerator = null;
        TypeUnionGenerator typeUnionGenerator = null;

        // New ParticleDifferenceGenerator has to know the namespaceConflictingElementsMap and elementGroupMap for ElementRef handling.
        particleUnionGenerator = new ParticleUnionGenerator(outputSchema, namespaceOutputSchemaMap, elementOldSchemaMap, anyPatternOldSchemaMap, namespaceConflictingElementsMap, topLevelElementTypeMap, usedIDs, otherSchemata, namespaceAbbreviationMap, workingDirectory);

        // New AttributeParticleDifferenceGenerator
        attributeParticleUnionGenerator = new AttributeParticleUnionGenerator(outputSchema, namespaceOutputSchemaMap, attributeOldSchemaMap, anyAttributeOldSchemaMap, namespaceConflictingAttributeMap, usedIDs, otherSchemata, namespaceAbbreviationMap, workingDirectory);

        // New TypeDifferenceGenerator has to know all four HashMaps for AttributeRef and ElementRef handling.
        typeUnionGenerator = new TypeUnionGenerator(outputSchema, namespaceOutputSchemaMap, typeOldSchemaMap, usedIDs, attributeParticleUnionGenerator, particleUnionGenerator);

        particleUnionGenerator.setTypeUnionGeneratornion(typeUnionGenerator);
        attributeParticleUnionGenerator.setTypeUnionGenerator(typeUnionGenerator);

        // Each state of the product automaton, with exception of the start state, corresponds to a type of the output schema, these types are now generated
        for (Iterator<TypeState> it = productTypeAutomaton.getStates().iterator(); it.hasNext();) {
            ProductTypeState productTypeState = (ProductTypeState) it.next();

            // Check if the current state is the start state of the automaton, which contains no types and does not correspond to a type
            if (productTypeState != productTypeAutomaton.getStartState()) {

                // Map mapping element names of the content model to type references, used for these elements
                LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();

                // For each outgoing transition of the current state and for each element name annotated to these transitions update the elementTypeMap
                for (Transition outTransition : productTypeState.getOutTransitions()) {
                    for (String elementName : outTransition.getNameElementMap().keySet()) {

                        // Get name of the reached type
                        String typeName = stateTypeNameMap.get((ProductTypeState) outTransition.getDestinationState());

                        // Put new entry into the elementTypeMap, for the current element namen and the corresponding type
                        SimpleType stubSimpleType = new SimpleType(typeName, null);
                        stubSimpleType.setIsAnonymous(isAnonymous(((ProductTypeState) outTransition.getDestinationState()).getTypes(), outputSchema));

                        // Generate type stub for elements and "type" attribute computation
                        if (outputSchema.getTypeSymbolTable().getReference(typeName).getReference() != null) {
                            elementTypeMap.put(elementName, outputSchema.getTypeSymbolTable().getReference(typeName));
                        } else {
                            elementTypeMap.put(elementName, outputSchema.getTypeSymbolTable().updateOrCreateReference(typeName, stubSimpleType));
                        }
                    }
                }

                // Get type name for the current type
                String newTypeName = stateTypeNameMap.get(productTypeState);

                // Check if output schema contains type stub and only a stub for the given type
                if (outputSchema.getTypeSymbolTable().hasReference(newTypeName) && outputSchema.getTypeSymbolTable().getReference(newTypeName) != null) {

                    // Generate new type for the given automaton state
                    typeUnionGenerator.generateNewType(productTypeState.getTypes(), elementTypeMap, newTypeName);
                }
            }
        }

        // Add top-level elements to schema and take care of possible intersections
        for (Iterator<String> it = nameTopLevelElementsMap.keySet().iterator(); it.hasNext();) {
            String elementName = it.next();

            // Get old element set if more than one element with the current name exist
            LinkedHashSet<Element> oldElements = new LinkedHashSet<Element>(nameTopLevelElementsMap.get(elementName));

            // Remove old abstract elements
            for (Iterator<Element> it2 = oldElements.iterator(); it2.hasNext();) {
                Element oldElement = it2.next();

                if (oldElement.getAbstract()) {
                    it2.remove();
                }
            }

            // Generate new top-level element for the top-level element set only if not all old element are abstract
            if (oldElements != null) {
                particleUnionGenerator.generateNewTopLevelElement(oldElements, topLevelElementTypeMap.get(elementName));
            }
        }

        // Create nameTopLevelAttributesMap map in order to construct new top-level attributes
        HashMap<String, LinkedHashSet<Attribute>> nameTopLevelAttributesMap = getNameTopLevelAttributesMap();

        // Add top-level attributes to schema and take care of possible intersections
        for (Iterator<String> it = nameTopLevelAttributesMap.keySet().iterator(); it.hasNext();) {
            String attributeName = it.next();

            if (nameTopLevelAttributesMap.get(attributeName).size() == 1) {

                // Get old attribute set if more than one attribute with the current name exist
                LinkedList<AttributeParticle> oldAttributes = new LinkedList<AttributeParticle>(nameTopLevelAttributesMap.get(attributeName));

                // Generate new top-level attribute for the top-level attribute set
                attributeParticleUnionGenerator.generateNewTopLevelAttribute(oldAttributes);
            }
        }
    }

    /**
     * Get new HashMap mapping top-level attribute names to top-level
     * attributes. It is possible that for one name more than one top-level
     * attribute exist, this is the case if a conflict between this attributes
     * is present.
     *
     * @return Map mapping top-level attribute names to top-level attributes.
     */
    public HashMap<String, LinkedHashSet<Attribute>> getNameTopLevelAttributesMap() {
        HashMap<String, LinkedHashSet<Attribute>> nameTopLevelAttributesMap = new HashMap<String, LinkedHashSet<Attribute>>();

        // For each schema get contained top-level attributes
        for (Iterator<XSDSchema> it = minuendSchemata.iterator(); it.hasNext();) {
            XSDSchema schema = it.next();

            // Get top-level attributes contained in the schema
            for (Iterator<Attribute> it2 = schema.getAttributes().iterator(); it2.hasNext();) {
                Attribute attribute = it2.next();

                // Update nameTopLevelAttributesMap with new entry for the current top-level attribute
                if (!nameTopLevelAttributesMap.containsKey(attribute.getName())) {
                    nameTopLevelAttributesMap.put(attribute.getName(), new LinkedHashSet<Attribute>());
                }
                LinkedHashSet<Attribute> attributeSet = nameTopLevelAttributesMap.get(attribute.getName());
                attributeSet.add(attribute);
                nameTopLevelAttributesMap.put(attribute.getName(), attributeSet);
            }
        }
        return nameTopLevelAttributesMap;
    }

    /**
     * Create new type name for the specified type state, which contains
     * types. The new name is constructed by using the names of these contained
     * types.
     *
     * @param typeState State, which contains types.
     * @param outputSchema XSDSchema in which the new type is registered. Used to
     * check which type names already exist.
     * @return New type name for the specified type set not used in the output
     * schema yet.
     */
    public String getTypeName(TypeState typeState, XSDSchema outputSchema) {

        // Get the set of types contained in the type state
        LinkedHashSet<Type> containedTypes = typeState.getTypes();

        // Generate new name for the new type
        String typeName = "{" + outputSchema.getTargetNamespace() + "}";

        if (containedTypes.size() != 1) {
            typeName += "union-type.";
        }

        // Get set of all contained type names
        LinkedHashSet<String> typeNames = new LinkedHashSet<String>();

        // Use variable to check if complexType is contained
        boolean containsComplexType = false;

        // Add type name to set of conatined type names
        for (Iterator<Type> it = containedTypes.iterator(); it.hasNext();) {
            Type type = it.next();
            typeNames.add(type.getName());

            // Check if type is complexType
            if (type instanceof ComplexType) {
                containsComplexType = true;
            }
        }

        // Build-in data types are not renamed
        if (typeNames.size() == 1 && containedTypes.iterator().next().getNamespace().equals("http://www.w3.org/2001/XMLSchema")) {
            return containedTypes.iterator().next().getName();
        }

        // If anyType, anySimpleType or anyComplexType is contained return the any type name
        if (typeNames.contains("{http://www.w3.org/2001/XMLSchema}anyType")) {
            return "{http://www.w3.org/2001/XMLSchema}anyType";
        }
        if (!containsComplexType && typeNames.contains("{http://www.w3.org/2001/XMLSchema}anySimpleType")) {
            return "{http://www.w3.org/2001/XMLSchema}anySimpleType";
        }

        // Create new name
        for (Iterator<Type> it = containedTypes.iterator(); it.hasNext();) {
            Type type = it.next();

            // Add each type name to the new name
            typeName += type.getLocalName();

            // If current restriction type is not the last add a "." to the name
            if (it.hasNext()) {
                typeName += ".";
            }
        }

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

    /**
     * Generate a new blockDefault attribute for the output schema. This
     * attribute contains only BlockDefault contained in each schema of the
     * schema set.
     *
     * @return New blockDefault attribute, which is a BlockDefault, for the
     * output schema.
     */
    public LinkedHashSet<XSDSchema.BlockDefault> generateNewBlockDefaultAttribute() {

        // Create blockDefault attribute, which contains every possible BlockDefault value
        LinkedHashSet<XSDSchema.BlockDefault> blockDefaults = new LinkedHashSet<XSDSchema.BlockDefault>();
        blockDefaults.add(XSDSchema.BlockDefault.extension);
        blockDefaults.add(XSDSchema.BlockDefault.restriction);
        blockDefaults.add(XSDSchema.BlockDefault.substitution);

        // Check for each schema the blockDefault attribute
        for (Iterator<XSDSchema> it = minuendSchemata.iterator(); it.hasNext();) {
            XSDSchema schema = it.next();

            // If blockDefault attribute is present remove contained BlockDefault value from the blockDefault attribute of the output schema
            if (schema.getBlockDefaults() != null) {

                // Check if "extension" is not contained and if remove "extension" from blockDefault attribute of the output schema
                if (!schema.getBlockDefaults().contains(XSDSchema.BlockDefault.extension)) {
                    blockDefaults.remove(XSDSchema.BlockDefault.extension);
                }

                // Check if "restriction" is not contained and if remove "restriction" from blockDefault attribute of the output schema
                if (!schema.getBlockDefaults().contains(XSDSchema.BlockDefault.restriction)) {
                    blockDefaults.remove(XSDSchema.BlockDefault.restriction);
                }

                // Check if "substitution" is not contained and if remove "substitution" from blockDefault attribute of the output schema
                if (!schema.getBlockDefaults().contains(XSDSchema.BlockDefault.substitution)) {
                    blockDefaults.remove(XSDSchema.BlockDefault.substitution);
                }
            } else {

                // Remove every BlockDefault value from the blockDefault attribute of the output schema
                blockDefaults = new LinkedHashSet<XSDSchema.BlockDefault>();
            }
        }

        // Use foreign schema resolver to resolve blockDefault attributes in each schema, that does not contain the same blockDefault attribute as the output schema
        ForeignSchemaResolver foreignSchemaResolver = new ForeignSchemaResolver();
        for (Iterator<XSDSchema> it = minuendSchemata.iterator(); it.hasNext();) {
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
    public LinkedHashSet<XSDSchema.FinalDefault> generateNewFinalDefaultAttribute() {

        // Create finalDefault attribute, which contains every possible FinalDefault value
        LinkedHashSet<XSDSchema.FinalDefault> finalDefaults = new LinkedHashSet<XSDSchema.FinalDefault>();
        finalDefaults.add(XSDSchema.FinalDefault.extension);
        finalDefaults.add(XSDSchema.FinalDefault.restriction);
        finalDefaults.add(XSDSchema.FinalDefault.list);
        finalDefaults.add(XSDSchema.FinalDefault.union);

        // Check for each schema the finalDefault attribute
        for (Iterator<XSDSchema> it = minuendSchemata.iterator(); it.hasNext();) {
            XSDSchema schema = it.next();

            // If finalDefault attribute is present remove contained FinalDefault value from the finalDefault attribute of the output schema
            if (schema.getFinalDefaults() != null) {

                // Check if "extension" is  not contained and if remove "extension" from finalDefault attribute of the output schema
                if (!schema.getFinalDefaults().contains(XSDSchema.FinalDefault.extension)) {
                    finalDefaults.remove(XSDSchema.FinalDefault.extension);
                }

                // Check if "restriction" is not contained and if remove "restriction" from finalDefault attribute of the output schema
                if (!schema.getFinalDefaults().contains(XSDSchema.FinalDefault.restriction)) {
                    finalDefaults.remove(XSDSchema.FinalDefault.restriction);
                }

                // Check if "list" is not contained and if remove "list" from finalDefault attribute of the output schema
                if (!schema.getFinalDefaults().contains(XSDSchema.FinalDefault.list)) {
                    finalDefaults.remove(XSDSchema.FinalDefault.list);
                }

                // Check if "union" is not contained and if remove "union" from finalDefault attribute of the output schema
                if (!schema.getFinalDefaults().contains(XSDSchema.FinalDefault.union)) {
                    finalDefaults.remove(XSDSchema.FinalDefault.union);
                }
            } else {

                // Remove every FinalDefault value from the finalDefault attribute of the output schema
                finalDefaults = new LinkedHashSet<XSDSchema.FinalDefault>();
            }
        }

        // Use foreign schema resolver to resolve finalDefault attributes in each schema, that does not contain the same finalDefault attribute as the output schema
        ForeignSchemaResolver foreignSchemaResolver = new ForeignSchemaResolver();
        for (Iterator<XSDSchema> it = minuendSchemata.iterator(); it.hasNext();) {
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
    public XSDSchema.Qualification generateNewElementFormDefaultAttribute() {
        boolean isQualified = false;

        // For all schemata check the contained elementFormDefault attribute values
        for (Iterator<XSDSchema> it = minuendSchemata.iterator(); it.hasNext();) {
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
            for (Iterator<XSDSchema> it = minuendSchemata.iterator(); it.hasNext();) {
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
    public XSDSchema.Qualification generateNewAttributeFormDefaultAttribute() {
        boolean isQualified = false;

        // For all schemata check the contained attributeFormDefault attribute values
        for (Iterator<XSDSchema> it = minuendSchemata.iterator(); it.hasNext();) {
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
            for (Iterator<XSDSchema> it = minuendSchemata.iterator(); it.hasNext();) {
                XSDSchema schema = it.next();
                foreignSchemaResolver.removeAttributeFormDefault(schema);
            }
        }

        // New output schema has no attributeFormDefault attribute per default
        return XSDSchema.Qualification.unqualified;
    }

    /**
     * This method checks whether the type, generate from the specified type
     * list, is anonymous or not. 
     *
     * @param types List of types used to construct a new type.
     * @param outputSchema Output schema used to get the current target
     * namespace.
     * @return <tt>true</tt> if all specified types are anonymous, else
     * <tt>false</tt>.
     */
    public boolean isAnonymous(LinkedHashSet<Type> types, XSDSchema outputSchema) {

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

    /**
     * Checks if a particle contains a strict validated any pattern
     * @param particle Particle, which may contain a strict validated any
     * pattern.
     * @return <tt>true</tt> if the particle contains strict validated any
     * pattern.
     */
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
}
