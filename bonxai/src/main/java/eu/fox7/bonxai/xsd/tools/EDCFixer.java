package eu.fox7.bonxai.xsd.tools;

import eu.fox7.bonxai.common.AnyAttribute;
import eu.fox7.bonxai.common.AnyPattern;
import eu.fox7.bonxai.common.IdentifiedNamespace;
import eu.fox7.bonxai.common.NamespaceList;
import eu.fox7.bonxai.common.Particle;
import eu.fox7.bonxai.common.ParticleContainer;
import eu.fox7.bonxai.common.SymbolTableRef;
import eu.fox7.bonxai.xsd.*;
import eu.fox7.bonxai.xsd.automaton.*;
import eu.fox7.bonxai.xsd.automaton.TypeAutomatons.*;
import eu.fox7.bonxai.xsd.automaton.TypeAutomatons.exceptions.*;
import eu.fox7.bonxai.xsd.automaton.exceptions.*;
import eu.fox7.bonxai.xsd.setOperations.SchemaGroup;
import eu.fox7.bonxai.xsd.setOperations.union.*;

import java.util.*;

/**
 * EDCFixer class used to fix schemata, which violate the EDC constraint of
 * XML XSDSchema. This is done by using a subset automaton for the type automaton
 * of the given schema. In special cases a product automaton has to be used if
 * multiple schemata with same namespace are contained in the given schema
 * structure.
 *
 * @author Dominik Wolff
 */
public class EDCFixer {

    // Set of schema groups, for each group a new output schema is constructed
    private LinkedHashSet<SchemaGroup> schemaGroups = new LinkedHashSet<SchemaGroup>();

    // NamespaceAbbreviationMap maps namespaces to abbreviations
    private LinkedHashMap<String, String> namespaceAbbreviationMap = new LinkedHashMap<String, String>();

    // Set containing all old schemata
    private LinkedHashSet<XSDSchema> oldSchemata = new LinkedHashSet<XSDSchema>();

    // Map mapping each any attribute to the old schema, that contains the any attribute
    private LinkedHashMap<AnyAttribute, XSDSchema> anyAttributeOldSchemaMap = new LinkedHashMap<AnyAttribute, XSDSchema>();

    // Map mapping attributes to old schemata used to construct the new output schema
    private LinkedHashMap<Attribute, XSDSchema> attributeOldSchemaMap = new LinkedHashMap<Attribute, XSDSchema>();

    // Map mapping types to old schemata used to construct the new output schema
    private LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();

    // Map mapping any patterns to their old schemata
    private LinkedHashMap<AnyPattern, XSDSchema> anyPatternOldSchemaMap = new LinkedHashMap<AnyPattern, XSDSchema>();

    // Map mapping elements to old schemata used to construct the new output schema
    private LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();

    /**
     * Fixes the EDC constraint for a given schema.
     *
     * @param originalSchema XSDSchema which is not valid with respect to the the
     * EDC constraint.
     * @param workingDirectory Directory used for the schema location.
     * @return New schema for which the EDC constraint is valid.
     * @throws InvalidTypeStateContentException Exception that is thrown if a
     * type automaton state contains invalid content.
     * @throws EmptyProductTypeStateFieldException Exception which is thrown
     * if a product state contains no type states.
     * @throws NonDeterministicFiniteAutomataException Exception which is thrown
     * if a particle automaton is no deterministic finite automaton.
     * @throws EmptySubsetTypeStateFieldException Exception which is thrown
     * if a subset state contains no type states.
     */
    public XSDSchema fixEDC(XSDSchema originalSchema, String workingDirectory) throws InvalidTypeStateContentException, EmptyProductTypeStateFieldException, NonDeterministicFiniteAutomataException, EmptySubsetTypeStateFieldException {

        // For the original schema resolve substitutionGroups
        ElementInheritanceResolver elementInheritanceResolver = new ElementInheritanceResolver(originalSchema);
        elementInheritanceResolver.resolveSubstitutionGroups();

        // For the original schema resolve type inheritance
        ComplexTypeInheritanceResolver complexTypeInheritanceResolver = new ComplexTypeInheritanceResolver(originalSchema);
        return complexTypeInheritanceResolver.getSchemaWithoutComplexTypeInheritance(workingDirectory);
    }

    /**
     * Fixes the EDC constraint for a given schema, without inheritance.
     * 
     * @param originalSchema XSDSchema which is not valid with respect to the the
     * EDC constraint.
     * @param workingDirectory Directory used for the schema location.
     * @return New schema for which the EDC constraint is valid.
     * @throws InvalidTypeStateContentException Exception that is thrown if a
     * type automaton state contains invalid content.
     * @throws EmptyProductTypeStateFieldException Exception which is thrown
     * if a product state contains no type states.
     * @throws NonDeterministicFiniteAutomataException Exception which is thrown
     * if a particle automaton is no deterministic finite automaton.
     * @throws EmptySubsetTypeStateFieldException Exception which is thrown
     * if a subset state contains no type states.
     */
    public XSDSchema fixEDCWithoutInheritance(XSDSchema originalSchema, String workingDirectory) throws InvalidTypeStateContentException, EmptyProductTypeStateFieldException, NonDeterministicFiniteAutomataException, EmptySubsetTypeStateFieldException {
        // Get namespace to namespace abbreviation map
        namespaceAbbreviationMap = getNamespaceAbbreviationMap(originalSchema);

        // Build schema groups with flattened original and imported schemata
        schemaGroups = buildSchemaGroups(originalSchema);

        // Get a list of all old schemata (contains imported schemata and original schemata as well)
        oldSchemata = getOldSchemata(originalSchema);

        // HashMap containing a schema name for each schema group
        HashMap<SchemaGroup, String> schemaGroupSchemaNameMap = new HashMap<SchemaGroup, String>();

        // Create schema names for all schema groups
        for (SchemaGroup schemaGroup: schemaGroups) {
            // Set containing the names of all minuend schemata in sorted order
            TreeSet<String> sortedMinuendSchemaNames = new TreeSet<String>(schemaGroup.getMinuendSchemaNames());

            // New schema name. The name has the form: "(SchemaA.SchemaB.SchemaC)"
            String schemaGroupSchemaName = "";

            if (sortedMinuendSchemaNames.size() > 1) {
                schemaGroupSchemaName = "(";

                // Generate new name from sorted minuend schema names
                for (Iterator<String> it = sortedMinuendSchemaNames.iterator(); it.hasNext();) {
                    String minuendSchemaName = it.next();
                    schemaGroupSchemaName += minuendSchemaName;

                    if (it.hasNext()) {
                        schemaGroupSchemaName += ".";
                    } else {
                        schemaGroupSchemaName += ")";
                    }
                }
            } else {
                schemaGroupSchemaName = sortedMinuendSchemaNames.iterator().next();
            }

            // If the new schema name already exist construct a new name by adding (number) to the end of the original name
            int number = 1;
            String originalName = schemaGroupSchemaName;

            // Check if the schema name is already in use
            while (schemaGroupSchemaNameMap.containsValue(schemaGroupSchemaName)) {

                schemaGroupSchemaName += originalName + "(" + number + ")";
                number++;
            }

            // Add new schema name and schema group to map
            schemaGroupSchemaNameMap.put(schemaGroup, schemaGroupSchemaName);
        }

        // Set containing schemata resulting from the union of all schemata in a schema group
        LinkedHashSet<XSDSchema> outputSchemata = new LinkedHashSet<XSDSchema>();

        // HashMap mapping each schema group to its output schema
        LinkedHashMap<SchemaGroup, XSDSchema> schemaGroupOutputSchemaMap = new LinkedHashMap<SchemaGroup, XSDSchema>();

        // This Hashmap maps namespaces to corresponding outputSchemata.
        LinkedHashMap<String, XSDSchema> namespaceOutputSchemaMap = new LinkedHashMap<String, XSDSchema>();

        // HashMap mapping namespaces to top-level elements which are present in more than one schema of the schema group with the corresponding namespace
        LinkedHashMap<String, LinkedHashSet<Element>> namespaceConflictingElementsMap = getNamespaceConflictingElementsMap();

        // HashMap mapping namespaces to top-level attributes which are present in more than one schema of the schema group with the corresponding namespace
        LinkedHashMap<String, LinkedHashSet<Attribute>> namespaceConflictingAttributeMap = getNamespaceConflictingAttributesMap();

        // Set containing all IDs used in all new output schemata
        LinkedHashSet<String> usedIDs = new LinkedHashSet<String>();

        // Update the old schema maps
        if (anyAttributeOldSchemaMap.isEmpty() && attributeOldSchemaMap.isEmpty() && anyPatternOldSchemaMap.isEmpty() && elementOldSchemaMap.isEmpty()) {
            getOldSchemaMaps();
        } else {
            getTypeOldSchemaMap();
        }

        // Get set of other schemata
        LinkedHashSet<XSDSchema> otherSchemata = new LinkedHashSet<XSDSchema>();

        // Create output schema for each schema group
        for (SchemaGroup schemaGroup: schemaGroups) {
            // Generate new output schema with target namespace of the schema group
            XSDSchema outputSchema = new XSDSchema(schemaGroup.getTargetNamespace());

            // Construct new IdentifiedNamespace for target namespace and add it to outputSchema
            IdentifiedNamespace identifiedNamespace = constructNewIdentifiedNamespace(outputSchema, schemaGroup.getTargetNamespace());
            outputSchema.getNamespaceList().addIdentifiedNamespace(identifiedNamespace);

            // Add output schema to schemaGroupOutputSchemaMap and outputSchemata set
            schemaGroupOutputSchemaMap.put(schemaGroup, outputSchema);
            outputSchemata.add(outputSchema);

            // Add namespace to namespaceOutputSchemaMap
            namespaceOutputSchemaMap.put(schemaGroup.getTargetNamespace(), outputSchema);

            // Set schema location for output schema
            String schemaLocation = workingDirectory + schemaGroupSchemaNameMap.get(schemaGroup) + ".xsd";
            outputSchema.setSchemaLocation(schemaLocation);
        }

        // After creating all ouptut schema stumps generate union for schema group schemata
        for (SchemaGroup schemaGroup: schemaGroups) {
            // New schemaUnionGenerator is initialized with schema group schema set and namespaceOutputSchemaMap (Which will help if Particles contain Elements with foreign namespaces)
            SchemaUnionGenerator schemaUnionGenerator = new SchemaUnionGenerator(schemaGroup.getMinuendSchemata(), namespaceOutputSchemaMap, namespaceConflictingElementsMap, namespaceConflictingAttributeMap, anyAttributeOldSchemaMap, attributeOldSchemaMap, typeOldSchemaMap, anyPatternOldSchemaMap, elementOldSchemaMap, usedIDs, otherSchemata, namespaceAbbreviationMap, workingDirectory);

            // Create new attributeFormDefault attribute for the output schema, which has the value unqualified
            XSDSchema.Qualification attributeFormDefault = schemaUnionGenerator.generateNewAttributeFormDefaultAttribute();
            schemaGroupOutputSchemaMap.get(schemaGroup).setAttributeFormDefault(attributeFormDefault);

            // Create new elementFormDefault attribute for the output schema, which has the value unqualified
            XSDSchema.Qualification elementFormDefault = schemaUnionGenerator.generateNewElementFormDefaultAttribute();
            schemaGroupOutputSchemaMap.get(schemaGroup).setElementFormDefault(elementFormDefault);

            // Create new finalDefault attribute for the output schema, which contains only FinalDefaults contained in each schema of the schema set
            LinkedHashSet<XSDSchema.FinalDefault> finalDefault = schemaUnionGenerator.generateNewFinalDefaultAttribute();
            schemaGroupOutputSchemaMap.get(schemaGroup).setFinalDefaults(finalDefault);

            // Create new blockDefault attribute for the output schema, which contains only BlockDefault contained in each schema of the schema set
            LinkedHashSet<XSDSchema.BlockDefault> blockDefault = schemaUnionGenerator.generateNewBlockDefaultAttribute();
            schemaGroupOutputSchemaMap.get(schemaGroup).setBlockDefaults(blockDefault);

            // Generate new type automaton factory, which is used to construct a new type automaton for the schema set
            TypeAutomatonFactory typeAutomatonFactory = new TypeAutomatonFactory();

            // Build for each schema a type automaton
            LinkedList<TypeAutomaton> typeAutomatons = new LinkedList<TypeAutomaton>();
            for (XSDSchema schema: schemaGroup.getMinuendSchemata()) {
                typeAutomatons.add(typeAutomatonFactory.buildTypeAutomaton(schema, anyPatternOldSchemaMap));
            }

            // Build type automaton for the type automaton list
            TypeAutomaton typeAutomaton = null;

            if (typeAutomatons.size() == 1) {
                typeAutomaton = typeAutomatonFactory.buildSubsetTypeAutomaton(typeAutomatons.getFirst());
            } else {
                typeAutomaton = typeAutomatonFactory.buildProductTypeAutomaton(typeAutomatons);
            }

            // Get map mapping names to sets of top-level elements
            LinkedHashMap<String, LinkedHashSet<Element>> nameTopLevelElementsMap = new LinkedHashMap<String, LinkedHashSet<Element>>();

            // Update nameTopLevelElementsMap with element names found on outgoing transitions of the start state
            for (Transition transition : typeAutomaton.getStartState().getOutTransitions()) {
                nameTopLevelElementsMap.putAll(transition.getNameElementMap());
            }

            // Create map mapping particle states to type names of the new output schema
            LinkedHashMap<TypeState, String> stateTypeNameMap = new LinkedHashMap<TypeState, String>();

            // Create for each state of the type automaton, with exception of the start state, a type stub
            for (State state: typeAutomaton.getStates()) {
                // Check if the current state is the start state of the automaton, which contains no types and does not correspond to a type
                if (state != typeAutomaton.getStartState()) {
                    TypeState typeState = (TypeState) state;

                    // Get name of the new type (if anyType is contained it is anytype etc.)
                    String typeName = schemaUnionGenerator.getTypeName(typeState, schemaGroupOutputSchemaMap.get(schemaGroup));

                    // Update stateTypeNameMap
                    stateTypeNameMap.put(typeState, typeName);

                    // Create type stub in the output schema
                    if (!schemaGroupOutputSchemaMap.get(schemaGroup).getTypeSymbolTable().hasReference(typeName)) {
                        schemaGroupOutputSchemaMap.get(schemaGroup).getTypeSymbolTable().updateOrCreateReference(typeName, null);
                    }
                }
            }

            // Map mapping top-level elements to references of their types.
            LinkedHashMap<String, SymbolTableRef<Type>> topLevelElementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();

            // For each outgoing transition of the start state and for each element name annotated to these transitions update the topLevelElementTypeMap
            for (Transition outTransition : typeAutomaton.getStartState().getOutTransitions()) {
                for (String elementName : outTransition.getNameElementMap().keySet()) {

                    // Get name of the reached type
                    String typeName = stateTypeNameMap.get((TypeState) outTransition.getDestinationState());

                    // Put new entry into the elementTypeMap, for the current element namen and the corresponding type
                    SimpleType stubSimpleType = new SimpleType(typeName, null);
                    stubSimpleType.setIsAnonymous(schemaUnionGenerator.isAnonymous(((TypeState) outTransition.getDestinationState()).getTypes(), schemaGroupOutputSchemaMap.get(schemaGroup)));
                    topLevelElementTypeMap.put(elementName, schemaGroupOutputSchemaMap.get(schemaGroup).getTypeSymbolTable().updateOrCreateReference(typeName, stubSimpleType));
                }
            }

            // Initialize new generator classes
            ParticleUnionGenerator particleUnionGenerator = null;
            AttributeParticleUnionGenerator attributeParticleUnionGenerator = null;
            TypeUnionGenerator typeUnionGenerator = null;

            // New ParticleDifferenceGenerator has to know the namespaceConflictingElementsMap and elementGroupMap for ElementRef handling.
            particleUnionGenerator = new ParticleUnionGenerator(schemaGroupOutputSchemaMap.get(schemaGroup), namespaceOutputSchemaMap, elementOldSchemaMap, anyPatternOldSchemaMap, namespaceConflictingElementsMap, topLevelElementTypeMap, usedIDs, otherSchemata, namespaceAbbreviationMap, workingDirectory);

            // New AttributeParticleDifferenceGenerator
            attributeParticleUnionGenerator = new AttributeParticleUnionGenerator(schemaGroupOutputSchemaMap.get(schemaGroup), namespaceOutputSchemaMap, attributeOldSchemaMap, anyAttributeOldSchemaMap, namespaceConflictingAttributeMap, usedIDs, otherSchemata, namespaceAbbreviationMap, workingDirectory);

            // New TypeDifferenceGenerator has to know all four HashMaps for AttributeRef and ElementRef handling.
            typeUnionGenerator = new TypeUnionGenerator(schemaGroupOutputSchemaMap.get(schemaGroup), namespaceOutputSchemaMap, typeOldSchemaMap, usedIDs, attributeParticleUnionGenerator, particleUnionGenerator);

            particleUnionGenerator.setTypeUnionGeneratornion(typeUnionGenerator);
            attributeParticleUnionGenerator.setTypeUnionGenerator(typeUnionGenerator);

            // Each state of the type automaton, with exception of the start state, corresponds to a type of the output schema, these types are now generated
            for (TypeState typeState: typeAutomaton.getStates()) {
                // Check if the current state is the start state of the automaton, which contains no types and does not correspond to a type
                if (typeState != typeAutomaton.getStartState()) {

                    // Map mapping element names of the content model to type references, used for these elements
                    LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();

                    // For each outgoing transition of the current state and for each element name annotated to these transitions update the elementTypeMap
                    for (Transition outTransition : typeState.getOutTransitions()) {
                        for (String elementName : outTransition.getNameElementMap().keySet()) {

                            // Get name of the reached type
                            String typeName = stateTypeNameMap.get((TypeState) outTransition.getDestinationState());

                            // Put new entry into the elementTypeMap, for the current element namen and the corresponding type
                            SimpleType stubSimpleType = new SimpleType(typeName, null);
                            stubSimpleType.setIsAnonymous(schemaUnionGenerator.isAnonymous(((TypeState) outTransition.getDestinationState()).getTypes(), schemaGroupOutputSchemaMap.get(schemaGroup)));

                            // Generate type stub for elements and "type" attribute computation
                            if (schemaGroupOutputSchemaMap.get(schemaGroup).getTypeSymbolTable().getReference(typeName).getReference() != null) {
                                elementTypeMap.put(elementName, schemaGroupOutputSchemaMap.get(schemaGroup).getTypeSymbolTable().getReference(typeName));
                            } else {
                                elementTypeMap.put(elementName, schemaGroupOutputSchemaMap.get(schemaGroup).getTypeSymbolTable().updateOrCreateReference(typeName, stubSimpleType));
                            }
                        }
                    }

                    // Get type name for the current type
                    String newTypeName = stateTypeNameMap.get(typeState);

                    // Check if output schema contains type stub and only a stub for the given type
                    if (schemaGroupOutputSchemaMap.get(schemaGroup).getTypeSymbolTable().hasReference(newTypeName) && schemaGroupOutputSchemaMap.get(schemaGroup).getTypeSymbolTable().getReference(newTypeName) != null) {

                        // Generate new type for the given automaton state
                        typeUnionGenerator.generateNewType(typeState.getTypes(), elementTypeMap, newTypeName);
                    }
                }
            }

            // Add top-level elements to schema and take care of possible intersections
            for (String elementName: nameTopLevelElementsMap.keySet()) {
                // Get old element set if more than one element with the current name exist
                LinkedHashSet<Element> oldElements = new LinkedHashSet<Element>(nameTopLevelElementsMap.get(elementName));

                // Remove old abstract elements
                for (Iterator<Element> it = oldElements.iterator(); it.hasNext();) {
                    Element oldElement = it.next();

                    if (oldElement.getAbstract()) {
                        it.remove();
                    }
                }

                // Generate new top-level element for the top-level element set only if not all old element are abstract
                if (oldElements != null) {
                    particleUnionGenerator.generateNewTopLevelElement(oldElements, topLevelElementTypeMap.get(elementName));
                }
            }

            // Create nameTopLevelAttributesMap map in order to construct new top-level attributes
            HashMap<String, LinkedHashSet<Attribute>> nameTopLevelAttributesMap = schemaUnionGenerator.getNameTopLevelAttributesMap();

            // Add top-level attributes to schema and take care of possible intersections
            for (String attributeName: nameTopLevelAttributesMap.keySet()) {
                if (nameTopLevelAttributesMap.get(attributeName).size() == 1) {

                    // Get old attribute set if more than one attribute with the current name exist
                    LinkedList<AttributeParticle> oldAttributes = new LinkedList<AttributeParticle>(nameTopLevelAttributesMap.get(attributeName));

                    // Generate new top-level attribute for the top-level attribute set
                    attributeParticleUnionGenerator.generateNewTopLevelAttribute(oldAttributes);
                }
            }
        }

        // Add import-components to schemata
        for (SchemaGroup schemaGroup: schemaGroups) {
            XSDSchema outputSchema = schemaGroupOutputSchemaMap.get(schemaGroup);
            outputSchema.getNamespaceList().addIdentifiedNamespace(new IdentifiedNamespace("xs", "http://www.w3.org/2001/XMLSchema"));

            // Construct extended schema group namespace set, which contains all namespaces imported by schemata used to build this output schema and if schema group
            // contains an original schema the namspaces of all other output schemata which were constructed for original schema groups
            LinkedHashSet<String> extendedSchemaGroupNamespaces = new LinkedHashSet<String>();
            extendedSchemaGroupNamespaces.addAll(schemaGroup.getNamespaces());

            // Check if schema group contains an original schema
            if (schemaGroup.isOriginal()) {

                // For each schema group add schema group namespace to extended schema group namespace set if the schema group contains an original schema
                for (SchemaGroup currentSchemaGroup: schemaGroups) {
                    // Current schema group namespace is added if schema contains original schema
                    if (schemaGroup != currentSchemaGroup) {
                        extendedSchemaGroupNamespaces.add(currentSchemaGroup.getTargetNamespace());
                    }
                }
            }

            // Construct import-components for namespaces contained in the extended schema group namespace set
            for (String namespace: extendedSchemaGroupNamespaces) {
                // Handle empty namespace
                if (namespace == null) {
                    namespace = "";
                }
                XSDSchema referencedSchema = namespaceOutputSchemaMap.get(namespace);

                // Construct new import-component, which refers to the referencedSchema
                ImportedSchema importedSchema = new ImportedSchema(namespace, referencedSchema.getSchemaLocation().substring(referencedSchema.getSchemaLocation().lastIndexOf("/") + 1));
                importedSchema.setParentSchema(outputSchema);
                importedSchema.setSchema(referencedSchema);

                // Check if output schema already contains an import-component with same namespace and schema location
                for (ForeignSchema foreignSchema: outputSchema.getForeignSchemas()) {
                    if (foreignSchema instanceof ImportedSchema) {
                        ImportedSchema currentImportedSchema = (ImportedSchema) foreignSchema;

                        // Check namespace and schema location of currentImportedSchema and importedSchema
                        if (importedSchema != null && currentImportedSchema.getNamespace().equals(importedSchema.getNamespace()) && currentImportedSchema.getSchemaLocation().equals(importedSchema.getSchemaLocation())) {

                            // If import-component exists in output schema set importedSchema to null
                            importedSchema = null;
                        }
                    }
                }

                // Check if imported schema is empty
                if (importedSchema != null) {

                    // Add new import-component to the output schema
                    outputSchema.addForeignSchema(importedSchema);

                    // Construct new IdentifiedNamespace for import-component
                    IdentifiedNamespace identifiedNamespace = constructNewIdentifiedNamespace(outputSchema, namespace);

                    // Add new IdentifiedNamespace to output schema
                    if (identifiedNamespace != null) {
                        outputSchema.getNamespaceList().addIdentifiedNamespace(identifiedNamespace);
                    }
                }
            }
        }

        // Check for each schema group if it contains an origianl schema
        for (SchemaGroup schemaGroup: schemaGroups) {
            // Check if schema group contains an original schema
            if (schemaGroup.isOriginal()) {
                return schemaGroupOutputSchemaMap.get(schemaGroup);
            }
        }
        return null;
    }

    /**
     * This method checks a schema for namespaces and corresponding
     * abbreviations. A map mapping namespaces to abbreviations is returned. It
     * can be used to generate new IdentifiedNamespaces if needed.
     *
     * @param originalSchema Original schema. A schema structure may contain
     * namespace abbreviations found by this method.
     * @return HashMap mapping namespaces to abbreviations. Not every namespace
     * has a unique abbreviation.
     */
    private LinkedHashMap<String, String> getNamespaceAbbreviationMap(XSDSchema originalSchema) {

        // HashMap containing for each namespace a unique abbreviation
        namespaceAbbreviationMap = new LinkedHashMap<String, String>();

        // List of already used abbreviations
        LinkedList<String> alreadyUsedAbbreviations = new LinkedList<String>();

        // Initialize stack and schema set
        LinkedHashSet<XSDSchema> schemata = new LinkedHashSet<XSDSchema>();
        Stack<XSDSchema> stack = new Stack<XSDSchema>();

        // Add parent schema to stack and schema set
        schemata.add(originalSchema);
        stack.add(originalSchema);

        // As long as the stack contains a schema check next schema for foreign schemata and check for namespace abbreviations
        while (!stack.isEmpty()) {
            XSDSchema currentSchema = stack.pop();

            // Get current schema namespace list
            NamespaceList namespaceList = currentSchema.getNamespaceList();
            LinkedHashSet<IdentifiedNamespace> identifiedNamespaces = namespaceList.getIdentifiedNamespaces();

            // Check each identified namespace
            for (IdentifiedNamespace identifiedNamespace: identifiedNamespaces) {
                // If the namespaceAbbreviationMap does not contain the identified namespace namespace and the abbreviation is not contained in the list of already used abbreviations,
                // namespace and abbreviation can be added to map
                if (!namespaceAbbreviationMap.containsKey(identifiedNamespace.getUri()) && !alreadyUsedAbbreviations.contains(identifiedNamespace.getIdentifier())) {

                    namespaceAbbreviationMap.put(identifiedNamespace.getUri(), identifiedNamespace.getIdentifier());
                    alreadyUsedAbbreviations.add(identifiedNamespace.getIdentifier());
                }
            }
            LinkedList<ForeignSchema> foreignSchemata = currentSchema.getForeignSchemas();

            // For each contained foreign schema add schema to schema set and stack
            for (ForeignSchema foreignSchema: foreignSchemata) {
                // Check if schema is not null and not already contained in the schema set
                if (foreignSchema.getSchema() != null && !schemata.contains(foreignSchema.getSchema())) {
                    schemata.add(foreignSchema.getSchema());
                    stack.add(foreignSchema.getSchema());
                }
            }
        }
        return namespaceAbbreviationMap;
    }

    /**
     * This method can be used to set old schema maps manually. Use with
     * caution.
     *
     * @param anyAttributeOldSchemaMap Map mapping each any attribute to the old
     * schema, that contains the any attribute
     * @param attributeOldSchemaMap  Map mapping attributes to old schemata used
     * to construct the new output schema.
     * @param anyPatternOldSchemaMap Map mapping any patterns to their old
     * schemata.
     * @param elementOldSchemaMap Map mapping elements to old schemata used to
     * construct the new output schema.
     */
    public void setOldSchemaMaps(LinkedHashMap<AnyAttribute, XSDSchema> anyAttributeOldSchemaMap,
            LinkedHashMap<Attribute, XSDSchema> attributeOldSchemaMap,
            LinkedHashMap<AnyPattern, XSDSchema> anyPatternOldSchemaMap,
            LinkedHashMap<Element, XSDSchema> elementOldSchemaMap) {

        this.anyAttributeOldSchemaMap = anyAttributeOldSchemaMap;
        this.attributeOldSchemaMap = attributeOldSchemaMap;
        this.anyPatternOldSchemaMap = anyPatternOldSchemaMap;
        this.elementOldSchemaMap = elementOldSchemaMap;
    }

    /**
     * Constructs schema groups for an original schema. Each original schema and
     * the schemata imported by these schemata are added to corresponding schema
     * groups. Further more all schemata contained in schema groups are
     * flattened.
     *
     * @param originalSchema XSDSchema which is used to generate a set operation.
     * @return Set of schema groups, filled with schemata found in the schema
     * structure of the original schema. Included and imported schemata are
     * merged with their parents.
     */
    private LinkedHashSet<SchemaGroup> buildSchemaGroups(XSDSchema originalSchema) {

        // For the targetNamespace a new schemaGroup is build while the schema is placed in corresponding groups
        schemaGroups = new LinkedHashSet<SchemaGroup>();

        // Check if  schema group already exists or get new one
        SchemaGroup currentSchemaGroup = getSchemaGroup(schemaGroups, originalSchema.getTargetNamespace());

        // Add original XSDSchema to current schema group
        currentSchemaGroup.addMinuendSchema(originalSchema, originalSchema, true);
        schemaGroups.add(currentSchemaGroup);

        // For the original schema, all imported schemata found in its schema hierarchy are added to corresponding schema groups
        for (ImportedSchema importedForeignSchema: getImportedForeignSchemata(originalSchema)) {
            // If the importedForeignSchema contains a schema this is added to a schema group with the same namespace
            if (importedForeignSchema.getSchema() != null) {

                // The imported XSDSchema is not an original XSDSchema
                XSDSchema importedSchema = importedForeignSchema.getSchema();
                String targetNamespace = importedSchema.getTargetNamespace();

                // Check if  schema group already exists or get new one
                currentSchemaGroup = getSchemaGroup(schemaGroups, targetNamespace);

                // Add imported XSDSchema to current schema group if not already present
                currentSchemaGroup.addMinuendSchema(importedSchema, originalSchema, false);
                schemaGroups.add(currentSchemaGroup);
            }
        }

        // For all schemata contained in schemaGroups resolve includes and redefines
        for (SchemaGroup schemaGroup: schemaGroups) {
            for (XSDSchema fileGroupSchema: schemaGroup.getMinuendSchemata()) {
                // FlattenSchema resolves includes and redefines until the schema is "flat" (Only imports remain as direct children of the schema in schema hierachy)
                flattenSchema(fileGroupSchema);
            }
        }

        // Return schemaGroups with flattened original and imported schemata
        return schemaGroups;
    }

    /**
     * Checks wether a schema group with specified target namespace exists and
     * if it exists returns the schema group. If the schema group does not exist
     * a new schema group is constructed with given target namespace.
     *
     * @param schemaGroups Set of schema groups for which the inclusion is tested.
     * @param targetNamespace Namespace of the sought-after schema group.
     * @return Found schema group or a new schema group
     */
    private SchemaGroup getSchemaGroup(LinkedHashSet<SchemaGroup> schemaGroups, String targetNamespace) {

        // Check if schema group with same targetnamespace already exists
        SchemaGroup currentSchemaGroup = null;

        // Get current schema group
        for (SchemaGroup schemaGroup: schemaGroups) {
            if (schemaGroup.getTargetNamespace().equals(targetNamespace)) {
                currentSchemaGroup = schemaGroup;
            }
        }
        // If no schema group for the targetnamespace of the original schema exists, generate a new one
        if (currentSchemaGroup == null) {
            currentSchemaGroup = new SchemaGroup(targetNamespace);
            schemaGroups.add(currentSchemaGroup);
        }
        return currentSchemaGroup;
    }

    /**
     * Method returns all imported schemata present in the given schema
     * structure. The specified schema is the root of the schema structure. If
     * an schema, reached by using includes and redefines from the root, in this
     * structure contains an import-component this is added to the set,
     * which is returned after traversing all schemata.
     *
     * @param schema Root of the schema structure.
     * @return Set containing all import-components found in the specified schema
     * structure.
     */
    private LinkedHashSet<ImportedSchema> getImportedForeignSchemata(XSDSchema schema) {

        // Initialize set to save imported schemata
        LinkedHashSet<ImportedSchema> importedSchemata = new LinkedHashSet<ImportedSchema>();

        // Use stack and set of already seen ForeignSchemata to traveser schema hierarchy
        LinkedHashSet<ForeignSchema> alreadySeenForeignSchemas = new LinkedHashSet<ForeignSchema>();
        Stack<ForeignSchema> stack = new Stack<ForeignSchema>();
        stack.addAll(schema.getForeignSchemas());

        // As long as the stack is not empty check current schema children
        while (!stack.isEmpty()) {

            // If the current schema is an ImportedSchema it is add to the importedSchemata set
            ForeignSchema currentForeignSchema = stack.pop();

            if (currentForeignSchema instanceof ImportedSchema) {
                ImportedSchema currentImportedSchema = (ImportedSchema) currentForeignSchema;
                importedSchemata.add(currentImportedSchema);
            } else {

                // Get ForeinSchemata of the current ForeignSchema and add them to the stack if not already visited
                LinkedList<ForeignSchema> currentForeignSchemaForeignSchemata = currentForeignSchema.getSchema().getForeignSchemas();
                for (ForeignSchema currentForeignSchemaForeignSchema: currentForeignSchemaForeignSchemata) {
                    if (!alreadySeenForeignSchemas.contains(currentForeignSchemaForeignSchema)) {
                        stack.add(currentForeignSchemaForeignSchema);
                    }
                }
            }

            // Add current ForeignSchema to the set of already seen ForeignSchemata
            alreadySeenForeignSchemas.add(currentForeignSchema);
        }
        return importedSchemata;
    }

    /**
     * This method "flattens" a schema. In other words all included and redefined
     * schemata are integrated into the base schema. Because there is no
     * intersection between components of included/redefined schemata and all
     * components are in the same namespace as the base schema these schemata and
     * the base schema can easily be integrated.
     *
     * @param schema XSDSchema which should be flattened.
     */
    private void flattenSchema(XSDSchema schema) {

        // Initialize ForeignSchemaResolver
        IncludedSchemaResolver includedSchemaResolver = new IncludedSchemaResolver();
        RedefinedSchemaResolver redefinedSchemaResolver = new RedefinedSchemaResolver();

        // As long as the schema is flat ForeignSchemata will be resolved
        boolean flat = false;
        while (!flat) {

            // If no include or redefine elements are present in the schema, it is flat
            flat = true;

            // Resolve all include and redefine elements in the schema
            LinkedList<ForeignSchema> foreignSchemas = schema.getForeignSchemas();
            for (ForeignSchema foreignSchema: foreignSchemas) {
                if (foreignSchema instanceof RedefinedSchema) {
                    redefinedSchemaResolver.resolveRedefine(foreignSchema.getParentSchema(), (RedefinedSchema) foreignSchema);
                    flat = false;
                }
                if (foreignSchema instanceof IncludedSchema) {
                    includedSchemaResolver.resolveInclude(foreignSchema.getParentSchema(), (IncludedSchema) foreignSchema);
                    flat = false;
                }
            }
        }
    }

    /**
     * Constructs a set of old schemata. The original schema and the schemata
     * imported by this schema are added to schema set. Further more all
     * schemata contained are flattened.
     *
     * @param originalSchema XSDSchema used to generate a EDC fix operation.
     * @return Set of schemata, filled with schemata found in the schema
     * structure of the original schema. Included and imported schemata are
     * merged with their parents.
     */
    private LinkedHashSet<XSDSchema> getOldSchemata(XSDSchema originalSchema) {

        // Original schemata are contained in the old schema set
        oldSchemata = new LinkedHashSet<XSDSchema>();
        oldSchemata.add(originalSchema);

        // For the original schema, all imported schemata found in its schema hierarchy are added to the old schemata set
        for (ImportedSchema importedForeignSchema: getAllImportedForeignSchemata(originalSchema)) {
            // If the importedForeignSchema contains a schema this is added to the old schemata set
            if (importedForeignSchema.getSchema() != null) {
                oldSchemata.add(importedForeignSchema.getSchema());
            }
        }

        // Check for each old schemata
        for (XSDSchema schema: oldSchemata) {
            // Check if attributes in schema are per default qualified
            if (schema.getAttributeFormDefault() != null && schema.getAttributeFormDefault() == XSDSchema.Qualification.qualified) {
                ForeignSchemaResolver foreignSchemaResolver = new ForeignSchemaResolver();
                foreignSchemaResolver.removeAttributeFormDefault(schema);
            }

            // Check if attributes in schema are per default qualified
            if (schema.getElementFormDefault() != null && schema.getElementFormDefault() == XSDSchema.Qualification.qualified) {
                ForeignSchemaResolver foreignSchemaResolver = new ForeignSchemaResolver();
                foreignSchemaResolver.removeElementFormDefault(schema);
            }
        }

        // For all old schemata resolve includes and redefines
        for (XSDSchema oldSchema: oldSchemata) {
            // FlattenSchema resolves includes and redefines until the schema is "flat" (Only imports remain as direct children of the schema in schema hierachy)
            flattenSchema(oldSchema);
        }

        // Return schemaGroups with flattened original and imported schemata
        return oldSchemata;
    }

    /**
     * Method returns all imported schemata present in the given schema structure.
     * The specified schema is the root of the schema structure. If a schema
     * in this structure contains an import-component this is added to the set,
     * which is returned after traversing all schemata.
     *
     * @param schema Root of the schema structure.
     * @return Set containing all import-components found in the specified schema
     * structure.
     */
    private LinkedHashSet<ImportedSchema> getAllImportedForeignSchemata(XSDSchema schema) {

        // Initialize set to save imported schemata
        LinkedHashSet<ImportedSchema> importedSchemata = new LinkedHashSet<ImportedSchema>();

        // Use stack and set of already seen ForeignSchemata to traveser schema hierarchy
        LinkedHashSet<ForeignSchema> alreadySeenForeignSchemas = new LinkedHashSet<ForeignSchema>();
        Stack<ForeignSchema> stack = new Stack<ForeignSchema>();
        stack.addAll(schema.getForeignSchemas());

        // As long as the stack is not empty check current schema children
        while (!stack.isEmpty()) {

            // If the current schema is an ImportedSchema it is add to the importedSchemata set
            ForeignSchema currentForeignSchema = stack.pop();

            // Check if current foreign schema is an imported schema
            if (currentForeignSchema instanceof ImportedSchema) {
                ImportedSchema currentImportedSchema = (ImportedSchema) currentForeignSchema;
                importedSchemata.add(currentImportedSchema);
            }

            // Get ForeinSchemata of the current ForeignSchema and add them to the stack if not already visited
            LinkedList<ForeignSchema> currentForeignSchemaForeignSchemata = currentForeignSchema.getSchema().getForeignSchemas();
            for (ForeignSchema currentForeignSchemaForeignSchema: currentForeignSchemaForeignSchemata) {
                if (!alreadySeenForeignSchemas.contains(currentForeignSchemaForeignSchema)) {
                    stack.add(currentForeignSchemaForeignSchema);
                }
            }

            // Add current ForeignSchema to the set of already seen ForeignSchemata
            alreadySeenForeignSchemas.add(currentForeignSchema);
        }
        return importedSchemata;
    }

    /**
     * Get HashMap mapping namespaces to elements which are in conflict in the
     * specified namespace. Is used when a new element reference should be
     * created.
     *
     * @return Map mapping namespaces to conflicting minuend elements.
     */
    private LinkedHashMap<String, LinkedHashSet<Element>> getNamespaceConflictingElementsMap() {

        // Map mapping namespaces to elements which are in conflict
        LinkedHashMap<String, LinkedHashSet<Element>> namespaceConflictingElementsMap = new LinkedHashMap<String, LinkedHashSet<Element>>();

        // For each schema group check contained schemata for top-level elements
        for (SchemaGroup schemaGroup: schemaGroups) {
            // Get map mapping names to sets of top-level elements
            LinkedHashMap<String, LinkedHashSet<Element>> nameTopLevelElementsMap = new LinkedHashMap<String, LinkedHashSet<Element>>();

            // For each schema group check contained schemata
            for (XSDSchema schema: schemaGroup.getMinuendSchemata()) {
                // Check each top-level element of the current schema
                for (Element element: schema.getElements()) {
                    // Check if the element is abstract
                    if (!element.getAbstract()) {
                        // Update nameTopLevelElementsMap with new top-level element for the element name
                        if (!nameTopLevelElementsMap.containsKey(element.getName())) {
                            nameTopLevelElementsMap.put(element.getName(), new LinkedHashSet<Element>());
                        }
                        LinkedHashSet<Element> currentElements = nameTopLevelElementsMap.get(element.getName());
                        currentElements.add(element);
                        nameTopLevelElementsMap.put(element.getName(), currentElements);
                    }
                }
            }

            // For each entry of the nameTopLevelElementsMap check if top-level elements are in conflict
            for (String elementName: nameTopLevelElementsMap.keySet()) {
                // If more than one element with the same name exist a conflict is present
                if (nameTopLevelElementsMap.get(elementName).size() > 1) {

                    // Update namespaceConflictingElementsMap with new top-level element for the schema name
                    if (!namespaceConflictingElementsMap.containsKey(schemaGroup.getTargetNamespace())) {
                        namespaceConflictingElementsMap.put(schemaGroup.getTargetNamespace(), new LinkedHashSet<Element>());
                    }
                    LinkedHashSet<Element> currentElements = namespaceConflictingElementsMap.get(schemaGroup.getTargetNamespace());
                    currentElements.addAll(nameTopLevelElementsMap.get(elementName));
                    namespaceConflictingElementsMap.put(schemaGroup.getTargetNamespace(), currentElements);
                }
            }
        }
        // Return complete HashMap
        return namespaceConflictingElementsMap;
    }

    /**
     * Get HashMap mapping namespaces to attributes which are in conflict in the
     * specified namespace. Is used when a new attribute reference should be
     * created.
     *
     * @return Map mapping namespaces to conflicting attributes.
     */
    private LinkedHashMap<String, LinkedHashSet<Attribute>> getNamespaceConflictingAttributesMap() {

        // Map mapping namespaces to attributes which are in conflict
        LinkedHashMap<String, LinkedHashSet<Attribute>> namespaceConflictingAttributesMap = new LinkedHashMap<String, LinkedHashSet<Attribute>>();

        // For each schema group check contained schemata for top-level attributes
        for (SchemaGroup schemaGroup: schemaGroups) {
            // Get map mapping names to sets of top-level attributes
            LinkedHashMap<String, LinkedHashSet<Attribute>> nameTopLevelAttributesMap = new LinkedHashMap<String, LinkedHashSet<Attribute>>();

            // For each schema group check contained schemata
            for (XSDSchema schema: schemaGroup.getMinuendSchemata()) {
                // Check each top-level attribute of the current schema
                for (Attribute attribute: schema.getAttributes()) {
                    // Update nameTopLevelAttributesMap with new top-level attribute for the attribute name
                    if (!nameTopLevelAttributesMap.containsKey(attribute.getName())) {
                        nameTopLevelAttributesMap.put(attribute.getName(), new LinkedHashSet<Attribute>());
                    }
                    LinkedHashSet<Attribute> currentAttributes = nameTopLevelAttributesMap.get(attribute.getName());
                    currentAttributes.add(attribute);
                    nameTopLevelAttributesMap.put(attribute.getName(), currentAttributes);
                }
                namespaceConflictingAttributesMap.put(schemaGroup.getTargetNamespace(), new LinkedHashSet<Attribute>());
            }

            // For each entry of the nameTopLevelAttributesMap check if top-level attributes are in conflict
            for (String attributeName: nameTopLevelAttributesMap.keySet()) {
                // If more than one attribute with the same name exist a conflict is present
                if (nameTopLevelAttributesMap.get(attributeName).size() > 1) {

                    // Update namespaceConflictingAttributesMap with new top-level attribute for the schema name
                    if (!namespaceConflictingAttributesMap.containsKey(schemaGroup.getTargetNamespace())) {
                        namespaceConflictingAttributesMap.put(schemaGroup.getTargetNamespace(), new LinkedHashSet<Attribute>());
                    }
                    LinkedHashSet<Attribute> currentAttributes = namespaceConflictingAttributesMap.get(schemaGroup.getTargetNamespace());
                    currentAttributes.addAll(nameTopLevelAttributesMap.get(attributeName));
                    namespaceConflictingAttributesMap.put(schemaGroup.getTargetNamespace(), currentAttributes);
                }
            }
        }
        // Return complete HashMap
        return namespaceConflictingAttributesMap;
    }

    /**
     * Update the old schema maps, so that for each component the schema
     * containing the component can be found. Schemas may contain default
     * values.
     */
    private void getOldSchemaMaps() {
        for (XSDSchema schema: oldSchemata) {
            // Get all referenced types in the current schema
            for (Type type: schema.getTypeSymbolTable().getAllReferencedObjects()) {
                // If the referred type has the same namespace as the schema add type to map
                if (type.getNamespace().equals(schema.getTargetNamespace())) {
                    typeOldSchemaMap.put(type, schema);

                    // Check if type is compleType
                    if (type instanceof ComplexType) {
                        ComplexType complexType = (ComplexType) type;

                        // Get attribute particles contained in the compleType
                        for (AttributeParticle attributeParticle: complexType.getAttributes()) {
                            // Check that attribute particle is attribute or any attribute and add it to the map
                            if (attributeParticle instanceof Attribute) {
                                attributeOldSchemaMap.put((Attribute) attributeParticle, schema);
                            } else if (attributeParticle instanceof AnyAttribute) {
                                anyAttributeOldSchemaMap.put((AnyAttribute) attributeParticle, schema);
                            }
                        }

                        // If complexType has complex content check content for elements and attributes
                        if (complexType.getContent() instanceof ComplexContentType) {
                            ComplexContentType complexContentType = (ComplexContentType) complexType.getContent();

                            if (complexContentType.getInheritance() != null) {
                                if (complexContentType.getInheritance().getAttributes() != null) {

                                    // Get attribute particles contained in the complex content inheritance
                                    for (AttributeParticle attributeParticle: complexContentType.getInheritance().getAttributes()) {
                                        // Check that attribute particle is attribute or any attribute and add it to the map
                                        if (attributeParticle instanceof Attribute) {
                                            attributeOldSchemaMap.put((Attribute) attributeParticle, schema);
                                        } else if (attributeParticle instanceof AnyAttribute) {
                                            anyAttributeOldSchemaMap.put((AnyAttribute) attributeParticle, schema);
                                        }
                                    }
                                }
                            }
                            if (complexContentType.getParticle() != null) {
                                // Get elements contained in the complex content
                                for (Element element: getContainedElements(complexContentType.getParticle())) {
                                    elementOldSchemaMap.put(element, schema);
                                }

                                // Get any pattern contained in the comlex content
                                for (AnyPattern anyPattern: getContainedAnyPattern(complexContentType.getParticle())) {
                                    anyPatternOldSchemaMap.put(anyPattern, schema);
                                }
                            }
                        }

                        // If complexType has simple content check content for attributes
                        if (complexType.getContent() instanceof SimpleContentType) {
                            SimpleContentType simpleContentType = (SimpleContentType) complexType.getContent();

                            // Check if content inheritance is an extension
                            if (simpleContentType.getInheritance() instanceof SimpleContentExtension) {
                                SimpleContentExtension simpleContentExtension = (SimpleContentExtension) simpleContentType.getInheritance();

                                // Get attribute particles contained in the extension
                                for (AttributeParticle attributeParticle: simpleContentExtension.getAttributes()) {
                                    // Check that attribute particle is attribute or any attribute and add it to the map
                                    if (attributeParticle instanceof Attribute) {
                                        attributeOldSchemaMap.put((Attribute) attributeParticle, schema);
                                    } else if (attributeParticle instanceof AnyAttribute) {
                                        anyAttributeOldSchemaMap.put((AnyAttribute) attributeParticle, schema);
                                    }
                                }
                            } else if (simpleContentType.getInheritance() instanceof SimpleContentRestriction) {
                                SimpleContentRestriction simpleContentRestriction = (SimpleContentRestriction) simpleContentType.getInheritance();

                                // Get attribute particles contained in the extension
                                for (AttributeParticle attributeParticle: simpleContentRestriction.getAttributes()) {
                                    // Check that attribute particle is attribute or any attribute and add it to the map
                                    if (attributeParticle instanceof Attribute) {
                                        attributeOldSchemaMap.put((Attribute) attributeParticle, schema);
                                    } else if (attributeParticle instanceof AnyAttribute) {
                                        anyAttributeOldSchemaMap.put((AnyAttribute) attributeParticle, schema);
                                    }
                                }
                            }

                            // Check if content inheritance is a restriction
                            if (simpleContentType.getInheritance() instanceof SimpleContentRestriction) {
                                SimpleContentRestriction simpleContentRestriction = (SimpleContentRestriction) simpleContentType.getInheritance();

                                // Get attribute particles contained in the restriction
                                for (AttributeParticle attributeParticle: simpleContentRestriction.getAttributes()) {
                                    // Check that attribute particle is attribute and add it to the map
                                    if (attributeParticle instanceof Attribute) {
                                        attributeOldSchemaMap.put((Attribute) attributeParticle, schema);
                                    } else if (attributeParticle instanceof AnyAttribute) {
                                        anyAttributeOldSchemaMap.put((AnyAttribute) attributeParticle, schema);
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Get attributes contained in groups of the schema
            for (AttributeGroup attributeGroup: schema.getAttributeGroups()) {
                // Get attribute particles contained in the attribute group
                for (AttributeParticle attributeParticle: attributeGroup.getAttributeParticles()) {
                    // Check that attribute particle is attribute or any attribute and add it to the map
                    if (attributeParticle instanceof Attribute) {
                        attributeOldSchemaMap.put((Attribute) attributeParticle, schema);
                    } else if (attributeParticle instanceof AnyAttribute) {
                        anyAttributeOldSchemaMap.put((AnyAttribute) attributeParticle, schema);
                    }
                }
            }

            // Get elements and any pattern contained in groups of the schema
            for (Group group: schema.getGroups()) {
                // Get elements contained in the group and add them to map
                for (Element element: getContainedElements(group.getParticleContainer())) {
                    elementOldSchemaMap.put(element, schema);
                }
                // Get any pattern contained in the group and add them to map
                for (AnyPattern anyPattern: getContainedAnyPattern(group.getParticleContainer())) {
                    anyPatternOldSchemaMap.put(anyPattern, schema);
                }
            }

            // Get top-level elements contained in the schema and add them to map
            for (Element topLevelElement: schema.getElements()) {
                elementOldSchemaMap.put(topLevelElement, schema);
            }

            // Get top-level attributes contained in the schema and add them to map
            for (Attribute topLevelAttributes: schema.getAttributes()) {
                attributeOldSchemaMap.put(topLevelAttributes, schema);
            }
        }
    }

    /**
     * Update the type old schema map, so that for each type the schema
     * containing the component can be found. Schemas may contain default
     * values.
     */
    private void getTypeOldSchemaMap() {
        for (XSDSchema schema: oldSchemata) {
            // Get all referenced types in the current schema
            for (Type type: schema.getTypeSymbolTable().getAllReferencedObjects()) {
                // If the referred type has the same namespace as the schema add type to map
                if (type.getNamespace().equals(schema.getTargetNamespace())) {
                    typeOldSchemaMap.put(type, schema);
                }
            }
        }
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
            for (Particle currentParticle: particles) {
                containedElements.addAll(getContainedElements(currentParticle));
            }
        }
        return containedElements;
    }

    /**
     * Get all any patterns contained in the specified particle.
     *
     * @param particle Particle which is the root of the particle structure.
     * @return Set containing all any patterns contained in the specified
     * particle.
     */
    private LinkedHashSet<AnyPattern> getContainedAnyPattern(Particle particle) {

        // Initialize set which will contain all any patterns contained in the specified particle
        LinkedHashSet<AnyPattern> containedAnyPatterns = new LinkedHashSet<AnyPattern>();

        // Check if the particle is an any pattern and add it to the set
        if (particle instanceof AnyPattern) {
            containedAnyPatterns.add((AnyPattern) particle);
        } else if (particle instanceof ParticleContainer) {
            ParticleContainer particleContainer = (ParticleContainer) particle;

            // Check if the particle is a particle container and add any patterns contained in the container to the set
            LinkedList<Particle> particles = particleContainer.getParticles();
            for (Particle currentParticle: particles) {
                containedAnyPatterns.addAll(getContainedAnyPattern(currentParticle));
            }
        }
        return containedAnyPatterns;
    }

    /**
     * This method constructs a new IdentifiedNamespace for a given schema and
     * namespace. IdentifiedNamespace are used to map namespaces to
     * identifier/abbreviations. If the namespace is already attributed to an
     * abbreviation no IdentifiedNamespace is constructed and this mehtod returns
     * a null-pointer.
     *
     * @param schema XSDSchema in which the new IdentifiedNamespace can be placed.
     * @param namespace Namespace of the new IdentifiedNamespace, if a new
     * IdentifiedNamespace is generated.
     * @return Null if the given schema already contains an IdentifiedNamespace
     * for the specified namespace.
     */
    private IdentifiedNamespace constructNewIdentifiedNamespace(XSDSchema schema, String namespace) {

        // Construct new IdentifiedNamespace for import-component
        IdentifiedNamespace identifiedNamespace = null;

        // Check if namespace abbreviation is present in schema
        if (schema.getNamespaceList().getNamespaceByUri(namespace).getIdentifier() == null) {

            // Check if abbrevation exists in namespaceAbbreviationMap, if not generate new abbreviation (Form i.e. ns2)
            if (namespaceAbbreviationMap.containsKey(namespace)) {
                identifiedNamespace = new IdentifiedNamespace(namespaceAbbreviationMap.get(namespace), namespace);
            } else {

                // Finde new abbreviation for namespace
                int number = 1;
                while (identifiedNamespace == null) {
                    String abbreviation = "ns" + number;

                    // If abbreviation is not used in schema construct new IdentifiedNamespace
                    if (schema.getNamespaceList().getNamespaceByIdentifier(abbreviation).getUri() == null) {
                        identifiedNamespace = new IdentifiedNamespace(abbreviation, namespace);
                    }
                    number++;
                }
            }
        }
        return identifiedNamespace;
    }
}
