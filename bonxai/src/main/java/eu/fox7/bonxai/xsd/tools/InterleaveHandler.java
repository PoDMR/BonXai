package eu.fox7.bonxai.xsd.tools;

import eu.fox7.bonxai.common.AllPattern;
import eu.fox7.bonxai.common.CountingPattern;
import eu.fox7.bonxai.common.GroupRef;
import eu.fox7.bonxai.common.IdentifiedNamespace;
import eu.fox7.bonxai.common.Particle;
import eu.fox7.bonxai.common.ParticleContainer;
import eu.fox7.bonxai.common.SequencePattern;
import eu.fox7.bonxai.common.SymbolTableRef;
import eu.fox7.bonxai.xsd.*;
import eu.fox7.bonxai.xsd.automaton.ParticleAutomatons.ParticleAutomaton;
import eu.fox7.bonxai.xsd.automaton.ParticleAutomatons.ParticleAutomatonFactory;
import eu.fox7.bonxai.xsd.automaton.ParticleAutomatons.ParticleBuilder;
import eu.fox7.bonxai.xsd.automaton.ParticleAutomatons.ProductParticleAutomaton;
import eu.fox7.bonxai.xsd.automaton.ParticleAutomatons.SubsetParticleAutomaton;
import eu.fox7.bonxai.xsd.automaton.ParticleAutomatons.exceptions.EmptyProductParticleStateFieldException;
import eu.fox7.bonxai.xsd.automaton.ParticleAutomatons.exceptions.EmptySubsetParticleStateFieldException;
import eu.fox7.bonxai.xsd.automaton.ParticleAutomatons.exceptions.NotSupportedParticleAutomatonException;

import java.util.*;

/**
 * Repair a XML XSDSchema which is invalid, because of not allowed allPatterns
 * @author Lars Schmidt, Dominik Wolff
 */
public class InterleaveHandler {

    // This is the source schema AND the target schema of the repairing process.
    private XSDSchema xmlSchema;
    // Set of already seen schemas in the progress of finding all contentModels.
    private LinkedHashSet<XSDSchema> alreadySeenSchemas = new LinkedHashSet<XSDSchema>();
    // Set holding all complexTypes found in method findContentModels
    private LinkedHashSet<ComplexType> allComplexTypes = new LinkedHashSet<ComplexType>();
    // Set holding all groups found in method findContentModels
    private LinkedHashSet<eu.fox7.bonxai.xsd.Group> allGroups = new LinkedHashSet<eu.fox7.bonxai.xsd.Group>();
    // HashMap mapping elements to groups if these elements are in another namespace as the schema
    private LinkedHashMap<Element, SymbolTableRef<Group>> elementToSymbolTableGroupRef = new LinkedHashMap<Element, SymbolTableRef<Group>>();
    // Set containing all group names for groups contained in the 
    private LinkedHashSet<String> existingGroupNames = new LinkedHashSet<String>();
    // Map mapping namespaces to schema objects.
    private LinkedHashMap<String, XSDSchema> namespaceSchemaMap = new LinkedHashMap<String, XSDSchema>();
    // HashMap mapping elements to element references, these are lost in the all pattern resolve process.
    private LinkedHashMap<Element, ElementRef> elementElementReferenceMap = new LinkedHashMap<Element, ElementRef>();
    // Map mapping elements to schemata which contain the element.
    private LinkedHashMap<Element, XSDSchema> topLevelElementSchemaMap = new LinkedHashMap<Element, XSDSchema>();
    // Map mapping groups to schemata which contain the element.
    private LinkedHashMap<Group, XSDSchema> topLevelGroupSchemaMap = new LinkedHashMap<Group, XSDSchema>();
    // Map mapping type to schemata which contain the element.
    private LinkedHashMap<Type, XSDSchema> topLevelTypeSchemaMap = new LinkedHashMap<Type, XSDSchema>();
    // Map mapping type to schemata which contain the element.
    private LinkedHashSet<Type> typesSetToTopLevel = new LinkedHashSet<Type>();

    //
    /**
     * Constructor of class InterleaveHandler
     * @param xmlSchema     This is the source schema AND the target schema of the repairing process.
     */
    public InterleaveHandler(XSDSchema xmlSchema) {
        this.xmlSchema = xmlSchema;
    }

    private void getElementReferences(Particle particle) {

        // Check what kind of particle the particle is
        if (particle instanceof ElementRef) {
            ElementRef elementRef = (ElementRef) particle;

            // Update element to element reference map
            elementElementReferenceMap.put(elementRef.getElement(), elementRef);

        } else if (particle instanceof ParticleContainer) {
            ParticleContainer particleContainer = (ParticleContainer) particle;

            // Get element references in all contained particles
            for (Iterator<Particle> it = particleContainer.getParticles().iterator(); it.hasNext();) {
                Particle containedParticle = it.next();
                getElementReferences(containedParticle);
            }
        } else if (particle instanceof GroupRef) {
            Group group = (Group) ((GroupRef) particle).getGroup();

            // Get element references in all contained particles
            for (Iterator<Particle> it = group.getParticleContainer().getParticles().iterator(); it.hasNext();) {
                Particle containedParticle = it.next();
                getElementReferences(containedParticle);
            }
        }
    }

    /**
     * The "repair"-method starts the repair process for a given schema.
     *
     * At first all contentModels are collected from a given schema and all used
     * (imported, included, redefined) external schemas recursively. Invalid
     * allPatterns can be defined in complexTypes or groups in XML XSDSchema, so
     * these components have to be found.
     *
     * After that the repairings progress is startet for the found complexTypes
     * and groups.
     *
     * @param xmlSchema     This is the source schema AND the target schema of the repairing process.
     */
    public void repair(XSDSchema xmlSchema) throws EmptySubsetParticleStateFieldException, EmptyProductParticleStateFieldException, NotSupportedParticleAutomatonException {
        // Collect all contentModels from the given schema and its foreign schemas
        findContentModels(xmlSchema);

        // Start the fixing of invalid AllPatterns
        repairComplexTypes();
        repairGroups();
    }

    /**
     * Find all contentModels under complexTypes and groups in the given schema
     * hierarchy. This method calls itself recursively for each external child
     * schema.
     *
     * @param xmlSchema     Root of the schema hierarchy tree
     */
    private void findContentModels(XSDSchema xmlSchema) {
        this.alreadySeenSchemas.add(xmlSchema);
        namespaceSchemaMap.put(xmlSchema.getTargetNamespace(), xmlSchema);

        // Walk over all types defined in the type list of the given
        // schema and collect all complexTypes
        for (Iterator<Type> it = xmlSchema.getTypeSymbolTable().getAllReferencedObjects().iterator(); it.hasNext();) {
            Type type = it.next();
            if (type instanceof ComplexType) {
                allComplexTypes.add((ComplexType) type);
            }
            topLevelTypeSchemaMap.put(type, xmlSchema);
        }

        // Collect all groups defined in the group list of the given schema
        for (Iterator<eu.fox7.bonxai.xsd.Group> it = xmlSchema.getGroups().iterator(); it.hasNext();) {
            eu.fox7.bonxai.xsd.Group group = it.next();
            allGroups.add(group);
            existingGroupNames.add(group.getName());
            topLevelGroupSchemaMap.put(group, xmlSchema);
        }

        // Update top level element to schema map
        for (Iterator<Element> it = xmlSchema.getElements().iterator(); it.hasNext();) {
            Element topLevelElement = it.next();
            topLevelElementSchemaMap.put(topLevelElement, xmlSchema);
        }

        // For each foreignSchema defined in the current schema call the method
        // findContentModels recursively
        if (xmlSchema.getForeignSchemas() != null && !xmlSchema.getForeignSchemas().isEmpty()) {
            for (Iterator<ForeignSchema> it = xmlSchema.getForeignSchemas().iterator(); it.hasNext();) {
                ForeignSchema foreignSchema = it.next();
                if (!this.alreadySeenSchemas.contains(foreignSchema.getSchema())) {
                    this.findContentModels(foreignSchema.getSchema());
                }
            }
        }
    }

    /**
     * Find allPatterns in the particle structure of all found complexTypes
     */
    private void repairComplexTypes() throws EmptySubsetParticleStateFieldException, EmptyProductParticleStateFieldException, NotSupportedParticleAutomatonException {
        for (Iterator<ComplexType> it = this.allComplexTypes.iterator(); it.hasNext();) {
            ComplexType complexType = it.next();
            if (complexType.getContent() != null && complexType.getContent() instanceof ComplexContentType) {
                ComplexContentType complexContentType = (ComplexContentType) complexType.getContent();
                if (complexContentType.getParticle() != null) {
                    Particle particle = this.traverseParticle(complexContentType.getParticle(), null);
                    if (particle != complexContentType.getParticle() && !(complexContentType.getParticle() instanceof GroupRef)) {
                        complexContentType.setParticle(particle);
                    }
                }
            }
        }

        for (Iterator<ComplexType> it = this.allComplexTypes.iterator(); it.hasNext();) {
            ComplexType complexType = it.next();
            if (complexType.getContent() != null && complexType.getContent() instanceof ComplexContentType) {
                ComplexContentType complexContentType = (ComplexContentType) complexType.getContent();
                if (complexContentType.getParticle() != null) {
                    Particle particle = this.replaceForeignElements(complexContentType.getParticle(), null, topLevelTypeSchemaMap.get(complexType));
                    if (particle != complexContentType.getParticle()&& !(complexContentType.getParticle() instanceof GroupRef)) {
                        complexContentType.setParticle(particle);
                    }
                }
            }
        }
    }

    /**
     * Find allPatterns in the particle structure of all found groups
     */
    private void repairGroups() throws EmptySubsetParticleStateFieldException, EmptyProductParticleStateFieldException, NotSupportedParticleAutomatonException {
        for (Iterator<eu.fox7.bonxai.xsd.Group> it = this.allGroups.iterator(); it.hasNext();) {
            eu.fox7.bonxai.xsd.Group group = it.next();
            if (group.getParticleContainer() != null) {
                Particle particle = this.traverseParticle(group.getParticleContainer(), null);
                if (particle != group.getParticleContainer()) {
                    group.setParticleContainer((ParticleContainer) particle);
                }
            }
        }

        for (Iterator<eu.fox7.bonxai.xsd.Group> it = this.allGroups.iterator(); it.hasNext();) {
            eu.fox7.bonxai.xsd.Group group = it.next();
            if (group.getParticleContainer() != null) {
                Particle particle = this.replaceForeignElements(group.getParticleContainer(), null, topLevelGroupSchemaMap.get(group));
                if (particle != group.getParticleContainer()) {
                    group.setParticleContainer((ParticleContainer) particle);
                }
            }
        }
    }

    /**
     * Traverse over a given particle structure and fix invalid allPatterns
     * @param particle      Parameter holding the current particle for the check of invalid allPatterns
     * @param parent        In each recursive call of this method this parameter represents the direct parent particle of the current one.
     * @return Particle     Fixed particle without invalid allPatterns
     */
    private Particle traverseParticle(Particle particle, Particle parent) throws EmptySubsetParticleStateFieldException, EmptyProductParticleStateFieldException, NotSupportedParticleAutomatonException {

        if (particle instanceof ParticleContainer) {

            // Case: particle is a ParticleContainer (SequencePattern, ChoicePattern, AllPattern, CountingPattern)

            ParticleContainer particleContainer = (ParticleContainer) particle;

            // Traversion into the given ParticleContainer
            for (Iterator<Particle> it = particleContainer.getParticles().iterator(); it.hasNext();) {
                Particle innerParticle = it.next();
                traverseParticle(innerParticle, particle);
            }
            // Handling of an AllPattern
            int positionOfAllPattern = 0;
            if (parent != null) {
                positionOfAllPattern = ((ParticleContainer) parent).getParticles().indexOf(particle);
            }
            return handleAll(particleContainer, parent, positionOfAllPattern);

        } else if (particle instanceof GroupRef) {

            // Case: particle is a Group

            Group group = (Group) ((GroupRef) particle).getGroup();
            if (group.getParticleContainer() instanceof ParticleContainer) {
                ParticleContainer particleContainer = (ParticleContainer) group.getParticleContainer();

                // Traversion into the ParticleContainer of the given group
                for (Iterator<Particle> it = group.getParticleContainer().getParticles().iterator(); it.hasNext();) {
                    Particle innerParticle = it.next();
                    if (innerParticle instanceof ParticleContainer) {
                        traverseParticle(innerParticle, group.getParticleContainer());
                    }
                }
                // Handling of an AllPattern
                int positionOfAllPattern = 0;
                if (parent != null) {
                    positionOfAllPattern = ((ParticleContainer) parent).getParticles().indexOf(particle);
                }
                return handleAll(particleContainer, parent, positionOfAllPattern);
            }
            return particle;
        } else {

            // Case: none of the cases above - no handling is necessary

            return particle;
        }
    }

    /**
     * Method for repairing an invalid allPattern with the usage of an
     * InterleaveAutomaton, that converts the structure of such an all to an
     * equivalent particle structure without all.
     *
     * @param particleContainer     If this particleContainer is an invalid
     *                              allPattern, it will be fixed. Otherwise the
     *                              particleContainer will be returned without
     *                              any changes.
     * @param parent                The parent particle of the specified
     *                              particleContainer
     * @return Particle             Either an equivalent particle structure for
     *                              a invalid allPattern, or the given
     *                              particleContainer itself
     */
    private Particle handleAll(ParticleContainer particleContainer, Particle parent, int positionOfAllPattern) throws EmptySubsetParticleStateFieldException, EmptyProductParticleStateFieldException, NotSupportedParticleAutomatonException {
        if (particleContainer instanceof AllPattern) {
            AllPattern allPattern = (AllPattern) particleContainer;

            // Check if allPattern is valid or invalid. In later case build new particle via automatons.
            if (!isValidAll(allPattern, parent)) {

                // Get element to element reference map update
                getElementReferences(allPattern);

                // Build interleave-automaton with subsetParticleAutomatons list
                ParticleAutomatonFactory automatonFactory = new ParticleAutomatonFactory();
                ParticleBuilder particleBuilder = new ParticleBuilder();
                LinkedList<ParticleAutomaton> particleAutomatons = new LinkedList<ParticleAutomaton>();

                for (Iterator<Particle> it1 = allPattern.getParticles().iterator(); it1.hasNext();) {
                    Particle allChildParticle = it1.next();

                    // Build particle-automaton, which can be non deterministic
                    ParticleAutomaton particleAutomaton = automatonFactory.buildParticleAutomaton(allChildParticle, null);

                    //Build subset automaton of given particle-automaton, to ensure determinism
                    ParticleAutomaton subsetParticleAutomaton = automatonFactory.buildSubsetParticleAutomaton(particleAutomaton);
                    particleAutomatons.add(subsetParticleAutomaton);
                }

                // To ensure determinism a subset-automaton for the constructed interleave-automaton is built
                ProductParticleAutomaton interleaveParticleAutomaton = automatonFactory.buildInterleaveParticleAutomaton(particleAutomatons, xmlSchema);
                SubsetParticleAutomaton subsetInterleaveParticleAutomaton = automatonFactory.buildSubsetParticleAutomaton((ParticleAutomaton) interleaveParticleAutomaton);

                // Build new particle for the invalid interleave/all structure
                Particle resultingParticle = particleBuilder.buildParticle(subsetInterleaveParticleAutomaton);

                // Replace the old particle with the newly generated particle
                if (parent != null) {
                    ((ParticleContainer) parent).setParticle(positionOfAllPattern, resultingParticle);
                    return particleContainer;
                } else {
                    return resultingParticle;
                }
            } else {
                return particleContainer;
            }
        } else {
            return particleContainer;
        }
    }

    /**
     * Checks wether an allPattern is valid or not. An allPattern is invalid, if
     * it does not contain only elements or countingPattern with valid occurency
     * values, or it is used in an invalid parent particle (not top-level).
     * @param allPattern    allPattern, that will be checked for validity
     * @param parent        Parent particle of the given allPattern
     * @return boolean      Boolean value representing the validity of the
     *                      checked allPattern
     */
    private boolean isValidAll(AllPattern allPattern, Particle parent) {

        // First check if children of all are valid
        for (Iterator<Particle> it = allPattern.getParticles().iterator(); it.hasNext();) {
            Particle particle = it.next();

            // If not all particles of the all are elements the allPattern is invalid
            if (!((particle instanceof Element) || (particle instanceof ElementRef) || (particle instanceof CountingPattern))) {
                return false;
            } else {
                // For CountingPatterns check if minOccurs and maxOccurs are valid and if particles are elements
                if (particle instanceof CountingPattern) {
                    CountingPattern countingPattern = (CountingPattern) particle;

                    if (!((countingPattern.getParticles().getFirst() instanceof Element) || (countingPattern.getParticles().getFirst() instanceof ElementRef))) {
                        return false;
                    }
                    // MinOccurs can be 0 or 1
                    if (countingPattern.getMin() != 0 && countingPattern.getMin() != 1) {
                        return false;
                    }
                    // MaxOccurs can be 0 and 1 and not unbounded (null)
                    if (countingPattern.getMax() == null || (countingPattern.getMax() != 0 && countingPattern.getMax() != 1)) {
                        return false;
                    }
                }
            }
        }

        // Then check if the parent is valid
        if (parent != null) {
            if (parent instanceof CountingPattern) {
                CountingPattern countingPattern = (CountingPattern) parent;

                // MinOccurs can be 0 or 1
                if (countingPattern.getMin() != 0 && countingPattern.getMin() != 1) {
                    return false;
                }
                // MaxOccurs can be 1 and not unbounded (null)
                if (countingPattern.getMax() == null || countingPattern.getMax() != 1) {
                    return false;
                }
            } else {
                return false;
            }
        }
        // If all invalid cases are checked, the all is valid
        return true;
    }

    /**
     * Method generates a new group reference for a foreign element which is
     * not valid in the current schema. The group is placed in a foreign schema
     * which is imported in the current schema, if no import component exists
     * beforehand, a new import component is constructed.
     *
     * @param element Element with foreign namespace.
     * @param currentSchema XSDSchema containing the particle that contains the
     * specified element.
     * @return A new group reference which can replace the specified element.
     * In turn making the particle valid.
     */
    private GroupRef generateExternalGroupForElement(Element element, XSDSchema currentSchema) {

        // Check if group for this elment was already constructed
        if (this.elementToSymbolTableGroupRef.containsKey(element)) {

            // Get symbol table reference to an existing group
            SymbolTableRef<Group> symbolTableRef = elementToSymbolTableGroupRef.get(element);

            // Create new group reference to this group
            GroupRef groupRef = new GroupRef(symbolTableRef);

            // Find schema to contain the new group
            XSDSchema schema = findForeignSchemaForNamespace(currentSchema, element.getNamespace());

            // Check if symbol table of the schema contains the reference to the group
            if (!schema.getGroupSymbolTable().getReferences().contains(symbolTableRef)) {

                // If symbol table does not contain the reference add new group to the schema
                if (!schema.getGroups().contains(symbolTableRef.getReference())) {
                    schema.getGroupSymbolTable().setReference(symbolTableRef.getKey(), symbolTableRef);
                    schema.addGroup(symbolTableRef);
                }
            }

            // Return new group reference
            return groupRef;
        } else {

            // Symbol table reference for a group containing the specified element does not exist
            String groupName = null;
            int number = 1;

            // As long as no new abbreviation is found increment the number
            while (groupName == null) {

                // Create possible name, i.e. "group.foo.1"
                String possibleName = "{" + element.getNamespace() + "}group." + element.getLocalName() + "." + number;

                // Check if new abbreviation is already taken
                if (!existingGroupNames.contains(possibleName)) {
                    groupName = possibleName;
                }
                number++;
            }

            // Generate new group with sequence containing element
            SequencePattern sequencePattern = new SequencePattern();
            sequencePattern.addParticle(element);
            Group newGroup = new Group(groupName, sequencePattern);

            // Find schema which shall contain the new group
            XSDSchema schema = findForeignSchemaForNamespace(currentSchema, element.getNamespace());

            // Generate new symbol table reference and add group to schema
            schema.getGroupSymbolTable().updateOrCreateReference(groupName, newGroup);
            schema.addGroup(schema.getGroupSymbolTable().getReference(groupName));

            // Set symbol table reference for the new group in the current schema
            currentSchema.getGroupSymbolTable().setReference(groupName, schema.getGroupSymbolTable().getReference(groupName));
            GroupRef groupRef = new GroupRef(schema.getGroupSymbolTable().getReference(groupName));
            elementToSymbolTableGroupRef.put(element, schema.getGroupSymbolTable().getReference(groupName));
            return groupRef;
        }
    }

    /**
     * Find schema with the specified namespace in the schema structure. Check
     * if specified current schema contains an import component with same
     * namespace. Is this the case no new import component and abbreviation is
     * generated. On the other hand if no import component for the given
     * namespace exists a new import component to an exisiting schema with same
     * namespace is added to the current schema and a new abbreviation for the
     * namespace is generated.
     *
     * @param currentSchema XSDSchema containing an element with foreign namespace
     * in a former all pattern.
     * @param namespace Namespace of the invalid element.
     * @return XSDSchema which could contain the element in a group.
     */
    private XSDSchema findForeignSchemaForNamespace(XSDSchema currentSchema, String namespace) {

        // Import exists
        for (Iterator<ForeignSchema> it1 = currentSchema.getForeignSchemas().iterator(); it1.hasNext();) {
            ForeignSchema foreignSchema = (ImportedSchema) it1.next();
            if (foreignSchema instanceof ImportedSchema) {
                ImportedSchema importedSchema = (ImportedSchema) foreignSchema;
                if (importedSchema.getNamespace().equals(namespace)) {
                    return importedSchema.getSchema();
                }
            }
        }

        // Get existing schema with current namespace
        XSDSchema schema = namespaceSchemaMap.get(namespace);

        // Generate new import component for schema in current schema
        ImportedSchema importedSchema = new ImportedSchema(schema.getTargetNamespace(), schema.getSchemaLocation());
        currentSchema.addForeignSchema(importedSchema);
        importedSchema.setParentSchema(currentSchema);
        importedSchema.setSchema(schema);

        // Check if abbreviation exists for the new import component
        if (currentSchema.getNamespaceList().getNamespaceByUri(namespace).getIdentifier() == null) {
            String abbreviation = null;
            int number = 1;

            // As long as no new abbreviation is found increment the number
            while (abbreviation == null) {

                // Check if new abbreviation is already taken
                if (currentSchema.getNamespaceList().getNamespaceByIdentifier("ns" + number).getUri() == null) {
                    abbreviation = "ns" + number;
                }
                number++;
            }

            // Generate new identified namespace with new abbreviation and namespace
            IdentifiedNamespace identifiedNamespace = new IdentifiedNamespace(abbreviation, schema.getTargetNamespace());
            currentSchema.getNamespaceList().addIdentifiedNamespace(identifiedNamespace);
        }

        // Return found schema
        return schema;
    }

    private Particle replaceForeignElements(Particle particle, Particle parentParticle, XSDSchema schema) {

        if (particle instanceof ParticleContainer) {

            // Case: particle is a ParticleContainer (SequencePattern, ChoicePattern, AllPattern, CountingPattern)
            ParticleContainer particleContainer = (ParticleContainer) particle;

            // Traversion into the given ParticleContainer
            for (Iterator<Particle> it = particleContainer.getParticles().iterator(); it.hasNext();) {
                Particle innerParticle = it.next();
                replaceForeignElements(innerParticle, particle, schema);
            }
        } else if (particle instanceof Element) {

            // Case: particle is an Element
            Element element = (Element) particle;

            if (elementElementReferenceMap.containsKey(element)) {

                // Replace element with element reference
                int positionOfElement = ((ParticleContainer) parentParticle).getParticles().indexOf(element);
                ((ParticleContainer) parentParticle).setParticle(positionOfElement, elementElementReferenceMap.get(element));

                // Get new import component if necessary
                if (!element.getNamespace().equals(schema.getTargetNamespace())) {

                    boolean importNecessary = true;
                    for (Iterator<ForeignSchema> it = schema.getForeignSchemas().iterator(); it.hasNext();) {
                        ForeignSchema foreignSchema = it.next();

                        if (foreignSchema.getSchema() == topLevelElementSchemaMap.get(element)) {
                            importNecessary = false;
                        }
                    }

                    // New import component is necessary
                    if (importNecessary) {
                        XSDSchema foreignSchema = topLevelElementSchemaMap.get(element);

                        // Generate new import component for schema in current schema
                        ImportedSchema importedSchema = new ImportedSchema(foreignSchema.getTargetNamespace(), foreignSchema.getSchemaLocation());
                        schema.addForeignSchema(importedSchema);
                        importedSchema.setParentSchema(schema);
                        importedSchema.setSchema(foreignSchema);

                        // Check if abbreviation exists for the new import component
                        if (schema.getNamespaceList().getNamespaceByUri(foreignSchema.getTargetNamespace()).getIdentifier() == null) {
                            String abbreviation = null;
                            int number = 1;

                            // As long as no new abbreviation is found increment the number
                            while (abbreviation == null) {

                                // Check if new abbreviation is already taken
                                if (schema.getNamespaceList().getNamespaceByIdentifier("ns" + number).getUri() == null) {
                                    abbreviation = "ns" + number;
                                }
                                number++;
                            }

                            // Generate new identified namespace with new abbreviation and namespace
                            IdentifiedNamespace identifiedNamespace = new IdentifiedNamespace(abbreviation, foreignSchema.getTargetNamespace());
                            schema.getNamespaceList().addIdentifiedNamespace(identifiedNamespace);
                        }
                    }
                }

                return elementElementReferenceMap.get(element);

            } else {

                // Check if element is a foreign element
                if (!element.getNamespace().equals(schema.getTargetNamespace())) {

                    // Replace element with group reference and add import if necessary
                    int positionOfElement = ((ParticleContainer) parentParticle).getParticles().indexOf(element);

                    GroupRef groupRef = generateExternalGroupForElement(element, schema);
                    ((ParticleContainer) parentParticle).setParticle(positionOfElement, groupRef);

                    return groupRef;
                } else {
                    if (element.getTypeAttr() == false || element.getType().isAnonymous()) {
                        element.setTypeAttr(true);
                        element.getType().setIsAnonymous(false);
                        if (!typesSetToTopLevel.contains(element.getType())) {
                            schema.addType(schema.getTypeSymbolTable().getReference(element.getType().getName()));
                            typesSetToTopLevel.add(element.getType());
                        }
                    }
                }
            }
        }
        return particle;
    }
}
