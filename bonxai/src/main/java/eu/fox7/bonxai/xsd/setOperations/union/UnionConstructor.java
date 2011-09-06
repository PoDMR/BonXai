package eu.fox7.bonxai.xsd.setOperations.union;

import eu.fox7.bonxai.common.*;
import eu.fox7.bonxai.xsd.*;
import eu.fox7.bonxai.xsd.Element;
import eu.fox7.bonxai.xsd.Group;
import eu.fox7.bonxai.xsd.automaton.TypeAutomatons.exceptions.EmptyProductTypeStateFieldException;
import eu.fox7.bonxai.xsd.automaton.TypeAutomatons.exceptions.InvalidTypeStateContentException;
import eu.fox7.bonxai.xsd.automaton.exceptions.NonDeterministicFiniteAutomataException;
import eu.fox7.bonxai.xsd.setOperations.SchemaGroup;

import java.util.*;

/**
 * The UnionConstructor class is given a set of schema groups. For each of these
 * groups a new output schemata is generated following the union process. This
 * class is mainly responsible for initializing the new output schemata and
 * adding namespaces and import-components to them. In order to generate a new
 * output schema for a schema group the SchemaUnionGenerator is called.
 *
 * @author Dominik Wolff
 */
public class UnionConstructor {

    // Set of schema groups, for each group a new output schema is constructed
    private LinkedHashSet<SchemaGroup> schemaGroups = new LinkedHashSet<SchemaGroup>();

    // Directory where the new output schemata will be stored (Default is ""c:\")
    private String workingDirectory = "c:/";

    // Map mapping schema groups to schema names
    private HashMap<SchemaGroup, String> schemaGroupSchemaNameMap = new HashMap<SchemaGroup, String>();

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

    // Map mapping any patterns to their old schemata.
    private LinkedHashMap<AnyPattern, XSDSchema> anyPatternOldSchemaMap = new LinkedHashMap<AnyPattern, XSDSchema>();

    // Map mapping elements to old schemata used to construct the new output schema
    private LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();

    /**
     * Constuctor of the UnionConstructor class. Specified parameter are used to
     * initialize the class fields.
     *
     * @param schemaGroups Set of schema groups.
     * @param workingDirectory Directory where the output schema files will be
     * stored.
     * @param schemaGroupSchemaNameMap HashMap mapping schema groups to schema
     * names. Used to given output schemas a name.
     * @param namespaceAbbreviationMap HashMap mapping namespaces to
     * abbreviations. Used to construct new IdentifiedNamespace if needed.
     * @param oldSchemata Set of old schemata used to construct the new schema.
     */
    public UnionConstructor(LinkedHashSet<SchemaGroup> schemaGroups, String workingDirectory, HashMap<SchemaGroup, String> schemaGroupSchemaNameMap, LinkedHashMap<String, String> namespaceAbbreviationMap, LinkedHashSet<XSDSchema> oldSchemata) {

        // Initialize class fields
        this.schemaGroupSchemaNameMap = schemaGroupSchemaNameMap;
        this.schemaGroups = schemaGroups;
        this.workingDirectory = workingDirectory;
        this.namespaceAbbreviationMap = namespaceAbbreviationMap;
        this.oldSchemata = oldSchemata;
    }

    /**
     * Method constructs a union of schemata for a given schema groups. Each
     * output schema is initialized and given to a SchemaUnionGenerator to
     * generate a union of schemata stored in the output schema. Afterwards
     * import-components for each schema are set and no new main schema is
     * constructed, because there are no emtpy schemata and every original
     * schema is a main schema of its own.
     *
     * @return Set of output schemata. Union process is completed.
     * @throws InvalidTypeStateContentException Exception that is thrown if a
     * type automaton state contains invalid content.
     * @throws EmptyProductTypeStateFieldException Exception which is thrown
     * if a product type state contains no type states.
     * @throws NonDeterministicFiniteAutomataException Exception which is thrown
     * if a type automaton is a no deterministic finite automaton.
     */
    public LinkedHashSet<XSDSchema> constructUnionSchemata() throws InvalidTypeStateContentException, EmptyProductTypeStateFieldException, NonDeterministicFiniteAutomataException {

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
        getOldSchemaMaps();

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

        // After creating all ouptut schema stumps generate union for schema group schemata
        for (Iterator<SchemaGroup> it = schemaGroups.iterator(); it.hasNext();) {
            SchemaGroup schemaGroup = it.next();

            // New schemaUnionGenerator is initialized with schema group schema set and namespaceOutputSchemaMap (Which will help if Particles contain Elements with foreign namespaces)
            SchemaUnionGenerator schemaUnionGenerator = new SchemaUnionGenerator(schemaGroup.getMinuendSchemata(), namespaceOutputSchemaMap, namespaceConflictingElementsMap, namespaceConflictingAttributeMap, anyAttributeOldSchemaMap, attributeOldSchemaMap, typeOldSchemaMap, anyPatternOldSchemaMap, elementOldSchemaMap, usedIDs, otherSchemata, namespaceAbbreviationMap, workingDirectory);
            schemaUnionGenerator.generateUnion(schemaGroupOutputSchemaMap.get(schemaGroup));
        }

        // Add import-components to schemata
        for (Iterator<SchemaGroup> it = schemaGroups.iterator(); it.hasNext();) {
            SchemaGroup schemaGroup = it.next();
            XSDSchema outputSchema = schemaGroupOutputSchemaMap.get(schemaGroup);
            outputSchema.getNamespaceList().addIdentifiedNamespace(new IdentifiedNamespace("xs", "http://www.w3.org/2001/XMLSchema"));

            // Construct extended schema group namespace set, which contains all namespaces imported by schemata used to build this output schema and if schema group
            // contains an original schema the namspaces of all other output schemata which were constructed for original schema groups
            LinkedHashSet<String> extendedSchemaGroupNamespaces = new LinkedHashSet<String>();
            extendedSchemaGroupNamespaces.addAll(schemaGroup.getNamespaces());

            // Check if schema group contains an original schema
            if (schemaGroup.isOriginal()) {

                // For each schema group add schema group namespace to extended schema group namespace set if the schema group contains an original schema
                for (Iterator<SchemaGroup> it2 = schemaGroups.iterator(); it2.hasNext();) {
                    SchemaGroup currentSchemaGroup = it2.next();

                    // Current schema group namespace is added if schema contains original schema
                    if (schemaGroup != currentSchemaGroup) {
                        extendedSchemaGroupNamespaces.add(currentSchemaGroup.getTargetNamespace());
                    }
                }
            }

            // Construct import-components for namespaces contained in the extended schema group namespace set
            for (Iterator<String> it2 = extendedSchemaGroupNamespaces.iterator(); it2.hasNext();) {
                String namespace = it2.next();

                // Handle empty namespace
                if(namespace == null){
                    namespace = "";
                }
                XSDSchema referencedSchema = namespaceOutputSchemaMap.get(namespace);

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
                    if (identifiedNamespace != null) {
                        outputSchema.getNamespaceList().addIdentifiedNamespace(identifiedNamespace);
                    }
                }
            }
        }

        // Add other schemata to output schemata
        outputSchemata.addAll(otherSchemata);

        // Return the set of output schemata
        return outputSchemata;
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
        for (Iterator<SchemaGroup> it = schemaGroups.iterator(); it.hasNext();) {
            SchemaGroup schemaGroup = it.next();

            // Get map mapping names to sets of top-level elements
            LinkedHashMap<String, LinkedHashSet<Element>> nameTopLevelElementsMap = new LinkedHashMap<String, LinkedHashSet<Element>>();

            // For each schema group check contained schemata
            for (Iterator<XSDSchema> it2 = schemaGroup.getMinuendSchemata().iterator(); it2.hasNext();) {
                XSDSchema schema = it2.next();

                // Check each top-level element of the current schema
                for (Iterator<Element> it3 = schema.getElements().iterator(); it3.hasNext();) {
                    Element element = it3.next();

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
            for (Iterator<String> it3 = nameTopLevelElementsMap.keySet().iterator(); it3.hasNext();) {
                String elementName = it3.next();

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
        for (Iterator<SchemaGroup> it = schemaGroups.iterator(); it.hasNext();) {
            SchemaGroup schemaGroup = it.next();

            // Get map mapping names to sets of top-level attributes
            LinkedHashMap<String, LinkedHashSet<Attribute>> nameTopLevelAttributesMap = new LinkedHashMap<String, LinkedHashSet<Attribute>>();

            // For each schema group check contained schemata
            for (Iterator<XSDSchema> it2 = schemaGroup.getMinuendSchemata().iterator(); it2.hasNext();) {
                XSDSchema schema = it2.next();

                // Check each top-level attribute of the current schema
                for (Iterator<Attribute> it3 = schema.getAttributes().iterator(); it3.hasNext();) {
                    Attribute attribute = it3.next();

                    // Update nameTopLevelAttributesMap with new top-level attribute for the attribute name
                    if (!nameTopLevelAttributesMap.containsKey(attribute.getName())) {
                        nameTopLevelAttributesMap.put(attribute.getName(), new LinkedHashSet<Attribute>());
                    }
                    LinkedHashSet<Attribute> currentAttributes = nameTopLevelAttributesMap.get(attribute.getName());
                    currentAttributes.add(attribute);
                    nameTopLevelAttributesMap.put(attribute.getName(), currentAttributes);
                }
            }

            // For each entry of the nameTopLevelAttributesMap check if top-level attributes are in conflict
            for (Iterator<String> it3 = nameTopLevelAttributesMap.keySet().iterator(); it3.hasNext();) {
                String attributeName = it3.next();

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