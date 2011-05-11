package de.tudortmund.cs.bonxai.xsd.setOperations.difference;

import de.tudortmund.cs.bonxai.common.*;
import de.tudortmund.cs.bonxai.xsd.automaton.ParticleAutomatons.exceptions.*;
import de.tudortmund.cs.bonxai.xsd.automaton.TypeAutomatons.exceptions.*;
import de.tudortmund.cs.bonxai.xsd.automaton.exceptions.*;
import de.tudortmund.cs.bonxai.xsd.setOperations.*;
import de.tudortmund.cs.bonxai.xsd.*;
import de.tudortmund.cs.bonxai.xsd.Element;
import de.tudortmund.cs.bonxai.xsd.Group;
import java.util.*;

/**
 * The DifferenceConstructor class is given a set of schema groups. For these
 * new output schemata are constructed following the difference process. This
 * class is responsible for initializing the new output schemata and adding new
 * namespaces and import-components to the schemata. To generate a new output
 * schema for a schema group the SchemaDifferenceGenerator is called.
 *
 * @author Dominik Wolff
 */
public class DifferenceConstructor {

    // Set of schema groups, for each group a new output schema is constructed
    private LinkedHashSet<SchemaGroup> schemaGroups = new LinkedHashSet<SchemaGroup>();

    // Directory where the new output schemata will be stored (Default is ""c:\")
    private String workingDirectory = "c:/";

    // Map mapping schema groups to schema names
    private HashMap<SchemaGroup, String> schemaGroupSchemaNameMap;

    // NamespaceAbbreviationMap maps namespaces to abbreviations
    private LinkedHashMap<String, String> namespaceAbbreviationMap;

    // Set containing all old schemata
    private LinkedHashSet<XSDSchema> oldSchemata = new LinkedHashSet<XSDSchema>();

    // Map mapping each any attribute to the old schema, that contains the any attribute
    private LinkedHashMap<AnyAttribute, XSDSchema> anyAttributeOldSchemaMap = new LinkedHashMap<AnyAttribute, XSDSchema>();

    // Map mapping types to old schemata used to construct the new output schema
    private LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();

    // Map mapping any patterns to their old schemata.
    private LinkedHashMap<AnyPattern, XSDSchema> anyPatternOldSchemaMap = new LinkedHashMap<AnyPattern, XSDSchema>();

    // Map mapping elements to old schemata used to construct the new output schema
    private LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();

    // Map mapping attributes to old schemata used to construct the new output schema
    private LinkedHashMap<Attribute, XSDSchema> attributeOldSchemaMap = new LinkedHashMap<Attribute, XSDSchema>();

    /**
     * Constuctor of the DifferenceConstructor class. Specified parameter are
     * used to initialize the class fields.
     *
     * @param schemaGroups Set of schema groups.
     * @param workingDirectory Directory where the output schema files will be
     * stored.
     * @param schemaGroupSchemaNameMap HashMap mapping schema groups to schema
     * names. Used to given output schemas a name.
     * @param namespaceAbbreviationMap HashMap mapping namespaces to
     * abbreviations. Used to construct new IdentifiedNamespace if needed.
     * @param oldSchemata Set containing all old schemata used to construct the
     * new schemata.
     */
    public DifferenceConstructor(LinkedHashSet<SchemaGroup> schemaGroups, String workingDirectory, HashMap<SchemaGroup, String> schemaGroupSchemaNameMap, LinkedHashMap<String, String> namespaceAbbreviationMap, LinkedHashSet<XSDSchema> oldSchemata) {

        // Initialize class fields
        this.schemaGroupSchemaNameMap = schemaGroupSchemaNameMap;
        this.schemaGroups = schemaGroups;
        this.workingDirectory = workingDirectory;
        this.namespaceAbbreviationMap = namespaceAbbreviationMap;
        this.oldSchemata = oldSchemata;
    }

    /**
     * Method constructs a difference schema set for given schema groups. Each
     * output schema is initialized and given to a SchemaDifferenceGenerator to
     * generate a difference of schemata stored in the output schema. Afterwards
     * import-components for each schema are set and a new main schema is
     * constructed if needed.
     *
     * @return New output schema. Difference process is completed.
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
    public XSDSchema constructDifferenceSchemata() throws InvalidTypeStateContentException, EmptyProductTypeStateFieldException, NonDeterministicFiniteAutomataException, EmptySubsetParticleStateFieldException, UniqueParticleAttributionViolationException, EmptyProductParticleStateFieldException, NoUniqueStateNumbersException, NoDestinationStateFoundException, NotSupportedParticleAutomatonException, EmptySubsetTypeStateFieldException {

        // Set containing schemata resulting from the difference of all schemata in a schema group
        LinkedHashSet<XSDSchema> outputSchemata = new LinkedHashSet<XSDSchema>();

        // HashMap mapping each schema group to its output schema
        LinkedHashMap<SchemaGroup, XSDSchema> schemaGroupOutputSchemaMap = new LinkedHashMap<SchemaGroup, XSDSchema>();

        // This Hashmap maps namespaces to corresponding OutputSchemata.
        LinkedHashMap<String, XSDSchema> namespaceOutputSchemaMap = new LinkedHashMap<String, XSDSchema>();

        // HashMap mapping namespaces to elements which are present in the minuend and subtrahend schema of the schema group with the corresponding namespace
        LinkedHashMap<String, LinkedHashSet<Element>> namespaceConflictingElementsMap = getNamespaceConflictingElementsMap();

        // The elementGroupMap maps original minuend schema elements to groups. Elements contained are no present in the output schema because a new difference element is
        // generated for them but are still contained in their original stat in given groups.
        LinkedHashMap<Element, Group> elementGroupMap = new LinkedHashMap<Element, Group>();

        // Update the old schema maps
        getOldSchemaMaps();

        // Get set containing all IDs used in all new output schemata
        LinkedHashSet<String> usedIDs = new LinkedHashSet<String>();

        // Get set of other schemata
        LinkedHashSet<XSDSchema> otherSchemata = new LinkedHashSet<XSDSchema>();

        // Create output schema for each schema group
        for (Iterator<SchemaGroup> it = schemaGroups.iterator(); it.hasNext();) {
            SchemaGroup schemaGroup = it.next();

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

        // After creating all ouptut schema stumps generate difference for schema group schemata
        for (Iterator<SchemaGroup> it = schemaGroups.iterator(); it.hasNext();) {
            SchemaGroup schemaGroup = it.next();

            // New schemaDifferenceGenerator is initialized with schema group minuend schema and subtrahend schema and namespaceOutputSchemaMap
            // (Which will help if Particles contain Elements with foreign namespaces)
            XSDSchema minuendSchema = schemaGroup.getMinuendSchemata().iterator().next();
            XSDSchema subtrahendSchema = null;

            // Check if subtrahend schema is present
            if (!schemaGroup.getSubtrahendSchemata().isEmpty()) {
                subtrahendSchema = schemaGroup.getSubtrahendSchemata().iterator().next();
            }

            // Get schema difference generator
            SchemaDifferenceGenerator schemaDifferenceGenerator = new SchemaDifferenceGenerator(minuendSchema, subtrahendSchema, namespaceOutputSchemaMap, namespaceOutputSchemaMap, namespaceConflictingElementsMap, elementGroupMap, anyAttributeOldSchemaMap, attributeOldSchemaMap, typeOldSchemaMap, anyPatternOldSchemaMap, elementOldSchemaMap, usedIDs, otherSchemata, namespaceAbbreviationMap, workingDirectory);
            schemaDifferenceGenerator.generateDifference(schemaGroupOutputSchemaMap.get(schemaGroup));
        }

        // Check if output schemata contain components or not
        for (Iterator<SchemaGroup> it = schemaGroups.iterator(); it.hasNext();) {
            SchemaGroup schemaGroup = it.next();
            XSDSchema outputSchema = schemaGroupOutputSchemaMap.get(schemaGroup);

            // If all component lists are empty the output schema itself is of no use
            if (outputSchema.getAttributeGroups().isEmpty() && outputSchema.getAttributes().isEmpty() && outputSchema.getElements().isEmpty() && outputSchema.getForeignSchemas().isEmpty() && outputSchema.getGroups().isEmpty() && outputSchema.getTypes().isEmpty()) {

                // Set output schema to null and remove it from output schema set
                outputSchemata.remove(outputSchema);
                namespaceOutputSchemaMap.put(outputSchema.getTargetNamespace(), null);
                outputSchema = null;
            }
        }

        // After removing unnecessary output schemata, the remaining output schemata need to linked with each other if allowed
        for (Iterator<SchemaGroup> it = schemaGroups.iterator(); it.hasNext();) {
            SchemaGroup schemaGroup = it.next();

            // Only if output schema is present add namespaces to schema
            XSDSchema outputSchema = schemaGroupOutputSchemaMap.get(schemaGroup);
            if (outputSchema != null) {
                outputSchema.getNamespaceList().addIdentifiedNamespace(new IdentifiedNamespace("xs", "http://www.w3.org/2001/XMLSchema"));

                // The schema group contains all namespaces imported by schemata used to build this output schema
                for (Iterator<String> it2 = schemaGroup.getNamespaces().iterator(); it2.hasNext();) {
                    String namespace = it2.next();

                    // Handle empty namespace
                    if (namespace == null) {
                        namespace = "";
                    }

                    // If referenced schema exists generate new import-component
                    XSDSchema referencedSchema = namespaceOutputSchemaMap.get(namespace);
                    if (referencedSchema != null) {

                        // Construct new import-component, which refers to the referencedSchema
                        ImportedSchema importedSchema = new ImportedSchema(namespace, referencedSchema.getSchemaLocation().substring(referencedSchema.getSchemaLocation().lastIndexOf("/") + 1));
                        importedSchema.setParentSchema(outputSchema);
                        importedSchema.setSchema(referencedSchema);

                        // Check if output schema already contains an import-component with same namespace and schema location
                        for (Iterator<ForeignSchema> it3 = outputSchema.getForeignSchemas().iterator(); it3.hasNext();) {
                            ForeignSchema foreignSchema = it3.next();

                            if (foreignSchema instanceof ImportedSchema) {
                                ImportedSchema currentImportedSchema = (ImportedSchema) foreignSchema;

                                // Check namespace and schema location of currentImportedSchema and importedSchema
                                if (importedSchema != null && currentImportedSchema.getNamespace().equals(importedSchema.getNamespace()) && currentImportedSchema.getSchemaLocation().equals(importedSchema.getSchemaLocation())) {

                                    // If import-component exists in output schema set importedSchema to null
                                    importedSchema = null;
                                }
                            }
                        }
                        if (importedSchema != null) {

                            // Add new import-component to the output schema
                            outputSchema.addForeignSchema(importedSchema);

                            // Construct new IdentifiedNamespace for import-component
                            IdentifiedNamespace identifiedNamespace = constructNewIdentifiedNamespace(outputSchema, namespace);

                            // Add new IdentifiedNamespace to output schema
                            outputSchema.getNamespaceList().addIdentifiedNamespace(identifiedNamespace);
                        }
                    }
                }
            }
        }

        // Check if new main schema is needed this is the case if no schema contains all other schemata
        boolean needNewMain = needNewMainSchema(outputSchemata);

        // If new main schema is needed
        if (needNewMain) {

            // Generate new schema with empty targetnamespace
            XSDSchema mainSchema = new XSDSchema("");

            // Set schema location and schema name (Form: DifferenceMain)
            String schemaLocation = workingDirectory + "DifferenceMain" + ".xsd";
            mainSchema.setSchemaLocation(schemaLocation);

            // Set import-component for each output schema
            for (Iterator<XSDSchema> it = outputSchemata.iterator(); it.hasNext();) {
                XSDSchema outputSchema = it.next();
                String outputSchemaNamespace = outputSchema.getTargetNamespace();

                // Construct new import-component, which refers to the outputSchema
                ImportedSchema importedSchema = new ImportedSchema(outputSchemaNamespace, outputSchema.getSchemaLocation().substring(outputSchema.getSchemaLocation().lastIndexOf("/") + 1));
                importedSchema.setParentSchema(mainSchema);
                importedSchema.setSchema(outputSchema);

                // Add new import-component to the main schema
                outputSchema.addForeignSchema(importedSchema);

                // Construct new IdentifiedNamespace for import-component
                IdentifiedNamespace identifiedNamespace = constructNewIdentifiedNamespace(mainSchema, outputSchemaNamespace);

                // Add new IdentifiedNamespace to main schema
                outputSchema.getNamespaceList().addIdentifiedNamespace(identifiedNamespace);
            }

            // Add new main schema to output schema set
            return mainSchema;
        } else {

            // Check for each output schema if all other output schemata can be reached
            for (Iterator<XSDSchema> it = outputSchemata.iterator(); it.hasNext();) {
                XSDSchema outputSchema = it.next();

                // Check that schema is an difference schema and no other schema
                if (outputSchema.getSchemaLocation().substring(outputSchema.getSchemaLocation().lastIndexOf("/") + 1).startsWith("Difference")) {

                    // Initialize stack and visited schema set
                    LinkedHashSet<XSDSchema> visitedSchemata = new LinkedHashSet<XSDSchema>();
                    Stack<XSDSchema> stack = new Stack<XSDSchema>();

                    // Then add output schema to stack and visited schema set
                    visitedSchemata.add(outputSchema);
                    stack.add(outputSchema);

                    // As long as the stack contains a schema pop schema and check it for foreign schemata
                    while (!stack.isEmpty()) {
                        XSDSchema currentSchema = stack.pop();
                        LinkedList<ForeignSchema> foreignSchemata = currentSchema.getForeignSchemas();

                        // For each contained foreign schema add schema to schema set and stack
                        for (Iterator<ForeignSchema> it2 = foreignSchemata.iterator(); it2.hasNext();) {
                            ForeignSchema foreignSchema = it2.next();

                            // Check if schema is not null and not already contained in the schema set
                            if (foreignSchema.getSchema() != null && !visitedSchemata.contains(foreignSchema.getSchema())) {

                                // Check if foreign schema is other schema
                                if (!foreignSchema.getSchema().getSchemaLocation().substring(outputSchema.getSchemaLocation().lastIndexOf("/") + 1).startsWith("Other")) {

                                    visitedSchemata.add(foreignSchema.getSchema());
                                    stack.add(foreignSchema.getSchema());
                                }
                            }
                        }
                    }

                    // If the output schema contains all other output schemata it is a valid main schema and no other main is needed
                    if (outputSchemata.containsAll(visitedSchemata) && visitedSchemata.containsAll(outputSchemata)) {
                        return outputSchema;
                    }
                }
            }
            return null;
        }
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

    /**
     * This method checks if a new main schema is needed. This is the case if
     * no output schema contains all other output schema via import-components
     * direct or schema structure indirect.
     *
     * @param outputSchemata Set of schemata for which is checked if new main
     * schema is needed.
     * @return <tt>true</tt> if a new main schema is needed, otherwise
     * <tt>false</tt>.
     */
    private boolean needNewMainSchema(LinkedHashSet<XSDSchema> outputSchemata) {

        // Check for each output schema if all other output schemata can be reached
        for (Iterator<XSDSchema> it = outputSchemata.iterator(); it.hasNext();) {
            XSDSchema outputSchema = it.next();

            // Check that schema is an difference schema and no other schema
            if (outputSchema.getSchemaLocation().substring(outputSchema.getSchemaLocation().lastIndexOf("/") + 1).startsWith("Difference")) {

                // Initialize stack and visited schema set
                LinkedHashSet<XSDSchema> visitedSchemata = new LinkedHashSet<XSDSchema>();
                Stack<XSDSchema> stack = new Stack<XSDSchema>();

                // Then add output schema to stack and visited schema set
                visitedSchemata.add(outputSchema);
                stack.add(outputSchema);

                // As long as the stack contains a schema pop schema and check it for foreign schemata
                while (!stack.isEmpty()) {
                    XSDSchema currentSchema = stack.pop();
                    LinkedList<ForeignSchema> foreignSchemata = currentSchema.getForeignSchemas();

                    // For each contained foreign schema add schema to schema set and stack
                    for (Iterator<ForeignSchema> it2 = foreignSchemata.iterator(); it2.hasNext();) {
                        ForeignSchema foreignSchema = it2.next();

                        // Check if schema is not null and not already contained in the schema set
                        if (foreignSchema.getSchema() != null && !visitedSchemata.contains(foreignSchema.getSchema())) {

                            // Check if foreign schema is other schema
                            if (!foreignSchema.getSchema().getSchemaLocation().substring(outputSchema.getSchemaLocation().lastIndexOf("/") + 1).startsWith("Other")) {

                                visitedSchemata.add(foreignSchema.getSchema());
                                stack.add(foreignSchema.getSchema());
                            }
                        }
                    }
                }

                // If the output schema contains all other output schemata it is a valid main schema and no other main is needed
                if (outputSchemata.containsAll(visitedSchemata) && visitedSchemata.containsAll(outputSchemata)) {
                    return false;
                }
            }
        }
        return true;
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

        // For each schema group check subtrahend and minuend schema
        for (Iterator<SchemaGroup> it = schemaGroups.iterator(); it.hasNext();) {
            SchemaGroup schemaGroup = it.next();

            if (!schemaGroup.getSubtrahendSchemata().isEmpty() && !schemaGroup.getMinuendSchemata().isEmpty()) {

                // Get minuend and subtrahend schema
                XSDSchema minuendSchema = schemaGroup.getMinuendSchemata().iterator().next();
                XSDSchema subtrahendSchema = schemaGroup.getSubtrahendSchemata().iterator().next();

                // Compare all elements contained in the minuend schema with all elements of the subtrahend schema
                for (Iterator<Element> it2 = minuendSchema.getElements().iterator(); it2.hasNext();) {
                    Element minuendElement = it2.next();

                    for (Iterator<Element> it3 = subtrahendSchema.getElements().iterator(); it3.hasNext();) {
                        Element subtrahendElement = it3.next();

                        // If both elements have the same name and the minuend element is not abstract add new entry to map
                        if (subtrahendElement.getName().equals(minuendElement.getName()) && !minuendElement.getAbstract()) {

                            // Get current entry (is null if the entry does not exist)
                            LinkedHashSet<Element> elementSet = namespaceConflictingElementsMap.get(minuendElement.getNamespace());

                            // Check if the element set is still null
                            if (elementSet == null) {
                                elementSet = new LinkedHashSet<Element>();
                            }

                            // Add element to set and set to map
                            elementSet.add(minuendElement);
                            namespaceConflictingElementsMap.put(minuendElement.getNamespace(), elementSet);
                        }
                    }
                }
            }
        }

        // Return complete HashMap
        return namespaceConflictingElementsMap;
    }

    /**
     * Get HashMap mapping namespaces to minuned schemata contained in schema
     * groups. These schemata are used to construct new schemata, so they are
     * old schemata with the same target namespace as the new schemata.
     *
     * @return Map which maps target namespaces to old minuend schemata with
     * these namespaces.
     */
    private LinkedHashMap<String, XSDSchema> getNamespaceOldSchemaMap() {

        // Map mapping target namespaces to old minuend schemata used to construct the corresponding output schema
        LinkedHashMap<String, XSDSchema> namespaceOldSchemaMap = new LinkedHashMap<String, XSDSchema>();

        // For each schema group add minuned schema to map
        for (Iterator<SchemaGroup> it = schemaGroups.iterator(); it.hasNext();) {
            SchemaGroup schemaGroup = it.next();
            namespaceOldSchemaMap.put(schemaGroup.getTargetNamespace(), schemaGroup.getMinuendSchemata().iterator().next());
        }
        return namespaceOldSchemaMap;
    }

    /**
     * Get a set of all top level elements contained in subtrahend schemata.
     * This set can be used to compute the intersecetion of an element and an
     * any pattern.
     *
     * @return Set of elements, each element is top level defined in a
     * subtrahend schema.
     */
    private LinkedHashSet<Element> getTopLevelSubtrahendElements() {

        // Set containing all top level Elements contained in subtrahend schemata
        LinkedHashSet<Element> topLevelSubtrahendElements = new LinkedHashSet<Element>();

        // For each schema group check subtrahend schema
        for (Iterator<SchemaGroup> it = schemaGroups.iterator(); it.hasNext();) {
            SchemaGroup schemaGroup = it.next();

            // Get subtrahend schema and its topl level elements
            XSDSchema subtrahendSchema = schemaGroup.getSubtrahendSchemata().iterator().next();
            topLevelSubtrahendElements.addAll(subtrahendSchema.getElements());
        }
        return topLevelSubtrahendElements;
    }

    /**
     * Get HashMap mapping any patterns to namespaces. This map can be necessary
     * if the difference of an any pattern with another any pattern or an
     * element should be computed. Furhtermore it is needed if a new any pattern
     * should be generated.
     *
     * @return Map which maps any patterns to namespaces. The namespace of an
     * any pattern is the namespace of the pattern containing the any pattern.
     */
    private LinkedHashMap<AnyPattern, String> getAnyPatternTargetNamespaceMap() {

        // Map mapping any patterns to the targetNamespaces of their schemata.
        LinkedHashMap<AnyPattern, String> anyPatternTargetNamespaceMap = new LinkedHashMap<AnyPattern, String>();

        // For each schema group check subtrahend and minuend schema
        for (Iterator<SchemaGroup> it = schemaGroups.iterator(); it.hasNext();) {
            SchemaGroup schemaGroup = it.next();

            // Get minuend and subtrahend schema
            XSDSchema minuendSchema = schemaGroup.getMinuendSchemata().iterator().next();
            XSDSchema subtrahendSchema = schemaGroup.getSubtrahendSchemata().iterator().next();

            // Add each any pattern of the minuend schema to the map
            for (Iterator<AnyPattern> it2 = getAllAnyPatterns(minuendSchema).iterator(); it2.hasNext();) {
                AnyPattern anyPattern = it2.next();
                anyPatternTargetNamespaceMap.put(anyPattern, minuendSchema.getTargetNamespace());
            }

            // Add each any pattern of the subtrahend schema to the map
            for (Iterator<AnyPattern> it2 = getAllAnyPatterns(subtrahendSchema).iterator(); it2.hasNext();) {
                AnyPattern anyPattern = it2.next();
                anyPatternTargetNamespaceMap.put(anyPattern, subtrahendSchema.getTargetNamespace());
            }
        }

        // Return complete HashMap
        return anyPatternTargetNamespaceMap;

    }

    /**
     * Get all any pattern defined in the specified schema. Method is used to
     * construct the anyPatternTargetNamespaceMap map, which is needed by the
     * particle difference generator.
     *
     * @param schema XSDSchema containing the found any patterns.
     * @return Set of any patterns found in the specified schema.
     */
    private LinkedHashSet<AnyPattern> getAllAnyPatterns(XSDSchema schema) {

        // Set containing all any patterns found in the schema
        LinkedHashSet<AnyPattern> anyPatterns = new LinkedHashSet<AnyPattern>();

        // Get any patterns contained in groups
        for (Iterator<Group> it = schema.getGroups().iterator(); it.hasNext();) {
            Group group = it.next();
            anyPatterns.addAll(getAllAnyPatterns(group.getParticleContainer()));
        }

        // Get any patterns contained in types
        for (Iterator<Type> it = schema.getTypeSymbolTable().getAllReferencedObjects().iterator(); it.hasNext();) {
            Type type = it.next();

            // Only complex types with complex content contain any patterns
            if (type instanceof ComplexType && ((ComplexType) type).getContent() instanceof ComplexContentType) {
                ComplexContentType complexContentType = (ComplexContentType) ((ComplexType) type).getContent();
                anyPatterns.addAll(getAllAnyPatterns(complexContentType.getParticle()));
            }
        }
        return anyPatterns;
    }

    /**
     * Get all any patterns locally defined in the specified particle. Can be
     * used to get all any patterns contained in a schema.
     *
     * @param particle Particle can be any type of particle.
     * @return Set containing all locally defined any patterns of the specified
     * particle.
     */
    private LinkedHashSet<AnyPattern> getAllAnyPatterns(Particle particle) {

        // Set containing all any patterns found in the particle
        LinkedHashSet<AnyPattern> anyPatterns = new LinkedHashSet<AnyPattern>();

        //Check what kind of particle the particle is
        if (particle instanceof ParticleContainer) {

            // If the particle is a particle container check all contained particles
            ParticleContainer particleContainer = (ParticleContainer) particle;

            // Check each contained particle and add contained any patterns to set
            for (Iterator<Particle> it = particleContainer.getParticles().iterator(); it.hasNext();) {
                Particle containedParticle = it.next();
                anyPatterns.addAll(getAllAnyPatterns(containedParticle));
            }
        } else if (particle instanceof AnyPattern) {

            // If particle is an any pattern add any pattern to set
            anyPatterns.add((AnyPattern) particle);
        }
        return anyPatterns;
    }

    /**
     * Update the old schema maps, so that for each component the schema
     * containing the component can be found. Schemas may contain default
     * values.
     */
    private void getOldSchemaMaps() {
        for (Iterator<XSDSchema> it2 = oldSchemata.iterator(); it2.hasNext();) {
            XSDSchema schema = it2.next();

            // Get all referenced types in the current schema
            for (Iterator<Type> it3 = schema.getTypeSymbolTable().getAllReferencedObjects().iterator(); it3.hasNext();) {
                Type type = it3.next();

                // If the referred type has the same namespace as the schema add type to map
                if (type.getNamespace().equals(schema.getTargetNamespace())) {
                    typeOldSchemaMap.put(type, schema);

                    // Check if type is compleType
                    if (type instanceof ComplexType) {
                        ComplexType complexType = (ComplexType) type;

                        // Get attribute particles contained in the compleType
                        for (Iterator<AttributeParticle> it4 = complexType.getAttributes().iterator(); it4.hasNext();) {
                            AttributeParticle attributeParticle = it4.next();

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
                                    for (Iterator<AttributeParticle> it4 = complexContentType.getInheritance().getAttributes().iterator(); it4.hasNext();) {
                                        AttributeParticle attributeParticle = it4.next();

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
                                for (Iterator<Element> it4 = getContainedElements(complexContentType.getParticle()).iterator(); it4.hasNext();) {
                                    Element element = it4.next();
                                    elementOldSchemaMap.put(element, schema);
                                }

                                // Get any pattern contained in the comlex content
                                for (Iterator<AnyPattern> it4 = getContainedAnyPattern(complexContentType.getParticle()).iterator(); it4.hasNext();) {
                                    AnyPattern anyPattern = it4.next();
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
                                for (Iterator<AttributeParticle> it4 = simpleContentExtension.getAttributes().iterator(); it4.hasNext();) {
                                    AttributeParticle attributeParticle = it4.next();

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
                                for (Iterator<AttributeParticle> it4 = simpleContentRestriction.getAttributes().iterator(); it4.hasNext();) {
                                    AttributeParticle attributeParticle = it4.next();

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
            for (Iterator<AttributeGroup> it3 = schema.getAttributeGroups().iterator(); it3.hasNext();) {
                AttributeGroup attributeGroup = it3.next();

                // Get attribute particles contained in the attribute group
                for (Iterator<AttributeParticle> it4 = attributeGroup.getAttributeParticles().iterator(); it4.hasNext();) {
                    AttributeParticle attributeParticle = it4.next();

                    // Check that attribute particle is attribute or any attribute and add it to the map
                    if (attributeParticle instanceof Attribute) {
                        attributeOldSchemaMap.put((Attribute) attributeParticle, schema);
                    } else if (attributeParticle instanceof AnyAttribute) {
                        anyAttributeOldSchemaMap.put((AnyAttribute) attributeParticle, schema);
                    }
                }
            }

            // Get elements and any pattern contained in groups of the schema
            for (Iterator<Group> it3 = schema.getGroups().iterator(); it3.hasNext();) {
                Group group = it3.next();

                // Get elements contained in the group and add them to map
                for (Iterator<Element> it4 = getContainedElements(group.getParticleContainer()).iterator(); it4.hasNext();) {
                    Element element = it4.next();
                    elementOldSchemaMap.put(element, schema);
                }
                // Get any pattern contained in the group and add them to map
                for (Iterator<AnyPattern> it4 = getContainedAnyPattern(group.getParticleContainer()).iterator(); it4.hasNext();) {
                    AnyPattern anyPattern = it4.next();
                    anyPatternOldSchemaMap.put(anyPattern, schema);
                }
            }

            // Get top-level elements contained in the schema and add them to map
            for (Iterator<Element> it3 = schema.getElements().iterator(); it3.hasNext();) {
                Element topLevelElement = it3.next();
                elementOldSchemaMap.put(topLevelElement, schema);
            }

            // Get top-level attributes contained in the schema and add them to map
            for (Iterator<Attribute> it3 = schema.getAttributes().iterator(); it3.hasNext();) {
                Attribute topLevelAttributes = it3.next();
                attributeOldSchemaMap.put(topLevelAttributes, schema);
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
            for (Iterator<Particle> it = particles.iterator(); it.hasNext();) {
                Particle currentParticle = it.next();
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
            for (Iterator<Particle> it = particles.iterator(); it.hasNext();) {
                Particle currentParticle = it.next();
                containedAnyPatterns.addAll(getContainedAnyPattern(currentParticle));
            }
        }
        return containedAnyPatterns;
    }
}
