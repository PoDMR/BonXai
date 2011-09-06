package eu.fox7.bonxai.xsd.setOperations.difference;

import eu.fox7.bonxai.common.AllPattern;
import eu.fox7.bonxai.common.Annotation;
import eu.fox7.bonxai.common.AnyPattern;
import eu.fox7.bonxai.common.ChoicePattern;
import eu.fox7.bonxai.common.CountingPattern;
import eu.fox7.bonxai.common.GroupRef;
import eu.fox7.bonxai.common.IdentifiedNamespace;
import eu.fox7.bonxai.common.Particle;
import eu.fox7.bonxai.common.ParticleContainer;
import eu.fox7.bonxai.common.ProcessContentsInstruction;
import eu.fox7.bonxai.common.SequencePattern;
import eu.fox7.bonxai.common.SymbolTableRef;
import eu.fox7.bonxai.xsd.*;
import eu.fox7.bonxai.xsd.Element.*;
import eu.fox7.bonxai.xsd.XSDSchema.*;
import eu.fox7.bonxai.xsd.automaton.ParticleAutomatons.*;
import eu.fox7.bonxai.xsd.automaton.ParticleAutomatons.exceptions.*;
import eu.fox7.bonxai.xsd.automaton.exceptions.*;

import java.util.*;

/**
 * Class used by the difference process to generate new particles either
 * these particles are the result of a difference computation, i.e. by removing
 * one element from another, or are copies of particles which are contained in
 * the minuend schema but not in the subtrahend schema. In the later case the
 * copy is not trivial because references to other schemata have to be set anew
 * and it is possible that referenced components are no longer present so
 * alternitives are used, i.e. a group reference for an element reference if the
 * element is removed to create a new element as result of a difference process.
 *
 * @author Dominik Wolff
 */
public class ParticleDifferenceGenerator {

    // XSDSchema which will contain the difference of the minuend schema and the subtrahend schema
    private XSDSchema outputSchema;

    // Map mapping target namespaces to output schemata, this is necessary to reference components in other schemata
    private LinkedHashMap<String, XSDSchema> namespaceOutputSchemaMap;

    // Map mapping a namespace to all elements in a minuned schema with this target namespace which are in conflict with elements of the subtrahend schema with this target namespace
    private LinkedHashMap<String, LinkedHashSet<Element>> namespaceConflictingElementsMap;

    // Map mapping elements to groups which were created to reference these elements without being top-level elements
    private LinkedHashMap<Element, Group> elementGroupMap;

    // HashMap mapping any patterns to their old schemata
    private LinkedHashMap<AnyPattern, XSDSchema> anyPatternOldSchemaMap;

    // HashMap mapping elements to old schemata used to construct the new output schema
    private LinkedHashMap<Element, XSDSchema> elementOldSchemaMap;

    // HashMap mapping top-level elements to references of their types
    private LinkedHashMap<String, SymbolTableRef<Type>> topLevelElementTypeMap;

    // HashMap which contains for every local elemente in the given context its type reference
    private HashMap<String, SymbolTableRef<Type>> elementTypeMap;

    // HashMap which contains for every local elemente in the given context its old type reference
    private HashMap<String, SymbolTableRef<Type>> elementOldTypeMap;

    // Set of schemata not contained in a schema group
    private LinkedHashSet<XSDSchema> otherSchemata = new LinkedHashSet<XSDSchema>();

    // NamespaceAbbreviationMap maps namespaces to abbreviations
    private LinkedHashMap<String, String> namespaceAbbreviationMap;

    // Directory where the new output schemata will be stored (Default is ""c:\")
    private String workingDirectory = "c:/";

    // Type difference generator of the attribute particle difference generator class
    private TypeDifferenceGenerator typeDifferenceGenerator;

    // Set containing all IDs used in all new output schemata
    private LinkedHashSet<String> usedIDs;

    /**
     * Constructor for the ParticleDifferenceGenerator class, which initializes
     * all necessary fields of the class. The ParticleDifferenceGenerator needs
     * information about the output schema and about elements which are in
     * conflict so that instead of top-level elements need groups can be
     * created. These groups allow to reference the old elments while the top
     * level elements are replaced by new elements.
     *
     * @param outputSchema XSDSchema which will contain the new element structure.
     * @param namespaceOutputSchemaMap Map mapping namespaces to output
     * schemata.
     * @param namespaceConflictingElementsMap Map maps namespaces to conflicting
     * Elements within that namespace.
     * @param elementGroupMap Map maps elements to groups they are now contained
     * in.
     * @param anyPatternOldSchemaMap HashMap mapping any patterns to their old
     * schemata.
     * @param elementOldSchemaMap HashMap mapping elements to old schemata used
     * to construct the new output schema.
     * @param workingDirectory Directory where the new output schemata will be
     * stored.
     * @param topLevelElementTypeMap HashMap mapping top-level elements to
     * references of their types.
     * @param namespaceAbbreviationMap NamespaceAbbreviationMap maps namespaces
     * to abbreviations.
     * @param otherSchemata Set of schemata not contained in a schema group.
     * @param usedIDs Set containing all IDs used in all new output schemata.
     */
    public ParticleDifferenceGenerator(XSDSchema outputSchema,
            LinkedHashMap<String, XSDSchema> namespaceOutputSchemaMap,
            LinkedHashMap<String, LinkedHashSet<Element>> namespaceConflictingElementsMap,
            LinkedHashMap<Element, Group> elementGroupMap,
            LinkedHashMap<AnyPattern, XSDSchema> anyPatternOldSchemaMap,
            LinkedHashMap<Element, XSDSchema> elementOldSchemaMap,
            LinkedHashMap<String, SymbolTableRef<Type>> topLevelElementTypeMap,
            LinkedHashSet<XSDSchema> otherSchemata,
            LinkedHashMap<String, String> namespaceAbbreviationMap,
            String workingDirectory,
            LinkedHashSet<String> usedIDs) {

        this.outputSchema = outputSchema;
        this.namespaceOutputSchemaMap = namespaceOutputSchemaMap;
        this.namespaceConflictingElementsMap = namespaceConflictingElementsMap;
        this.elementGroupMap = elementGroupMap;
        this.anyPatternOldSchemaMap = anyPatternOldSchemaMap;
        this.elementOldSchemaMap = elementOldSchemaMap;
        this.topLevelElementTypeMap = topLevelElementTypeMap;
        this.otherSchemata = otherSchemata;
        this.namespaceAbbreviationMap = namespaceAbbreviationMap;
        this.workingDirectory = workingDirectory;
        this.usedIDs = usedIDs;
    }

    /**
     * Get the anyPatternOldSchemaMap of the ParticleDifferenceGenerator.
     *
     * @return HashMap mapping any patterns to their old schemata.
     */
    public LinkedHashMap<AnyPattern, XSDSchema> getAnyPatternOldSchemaMap() {
        return anyPatternOldSchemaMap;
    }

    /**
     * Set the TypeDifferenceGenerator of the ParticleDifferenceGenerator.
     *
     * @param typeDifferenceGenerator Generator used to construct new types.
     */
    public void setTypeDifferenceGenerator(TypeDifferenceGenerator typeDifferenceGenerator) {
        this.typeDifferenceGenerator = typeDifferenceGenerator;
    }

    /**
     * Get the elementOldTypeMap of the ParticleDifferenceGenerator.
     *
     * @return HashMap which contains for every local elemente in the given
     * context its old type reference.
     */
    public HashMap<String, SymbolTableRef<Type>> getElementOldTypeMap() {
        return elementOldTypeMap;
    }

    /**
     * Get element with specified element name includes in the set of any
     * patterns if such an element exists.
     *
     * @param elementName Name of the specific element.
     * @param anyPatterns Set of any patterns.
     * @return Element if an element with specified name exists in the set of
     * any patterns.
     */
    public Element getIncluded(String elementName, LinkedHashSet<AnyPattern> anyPatterns) {

        // Check for each any pattern if the element name is included
        for (Iterator<AnyPattern> it = anyPatterns.iterator(); it.hasNext();) {
            AnyPattern anyPattern = it.next();

            // Get elements contained in the any pattern
            for (Iterator<Element> it2 = getContainedElements(anyPattern, anyPatternOldSchemaMap.get(anyPattern)).iterator(); it2.hasNext();) {
                Element containedElement = it2.next();

                // Check if both elements contain the same name
                if (containedElement.getName().equals(elementName)) {
                    return containedElement;
                }
            }
        }
        return null;
    }

    /**
     * Generate for a given minuend particle and a given subtrahend particle a
     * new particle via particle automaton. The new particle may contain
     * elements with empty type definitions and may violate the UPA-constraint.
     *
     * @param minuendParticle Particle from which the subtrahend particle is
     * removed from.
     * @param subtrahendParticle Particle which is removed from the minuend
     * particle.
     * @param elementTypeMap Map mapping elements to types.
     * @param elementOldTypeMap Map mapping elements to old types.
     * @return New particle as result of the difference of given particles.
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
     */
    public Particle generateNewParticleDifference(Particle minuendParticle, Particle subtrahendParticle, LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap, LinkedHashMap<String, SymbolTableRef<Type>> elementOldTypeMap) throws EmptySubsetParticleStateFieldException, NonDeterministicFiniteAutomataException, UniqueParticleAttributionViolationException, EmptyProductParticleStateFieldException, NoUniqueStateNumbersException, NoDestinationStateFoundException, NotSupportedParticleAutomatonException {
        this.elementTypeMap = elementTypeMap;
        this.elementOldTypeMap = elementOldTypeMap;

        // Check if subtrahend particle is not null
        if (subtrahendParticle == null) {
            return generateNewParticle(minuendParticle);
        } else {

            // Create new particle automaton factory
            ParticleAutomatonFactory particleAutomatonFactory = new ParticleAutomatonFactory();

            // Build new particle automaton for the current particle, determinise it and add it to the particle automaton set
            ParticleAutomaton minuendParticleAutomaton = particleAutomatonFactory.buildParticleAutomaton(minuendParticle, anyPatternOldSchemaMap);
            minuendParticleAutomaton = particleAutomatonFactory.buildSubsetParticleAutomaton(minuendParticleAutomaton);

            ParticleAutomaton subtrahendParticleAutomaton = particleAutomatonFactory.buildParticleAutomaton(subtrahendParticle, anyPatternOldSchemaMap);
            subtrahendParticleAutomaton = particleAutomatonFactory.buildSubsetParticleAutomaton(subtrahendParticleAutomaton);

            // All determinstic automatons are used to construct a product automaton which represents the difference of all particles
            ProductParticleAutomaton productParticleAutomaton = particleAutomatonFactory.buildProductAutomatonDifference(minuendParticleAutomaton, subtrahendParticleAutomaton, this);
            ParticleAutomaton substitutionParticleAutomaton = particleAutomatonFactory.buildSubstitutionParticleAutomaton(minuendParticle, subtrahendParticle, this, elementTypeMap);

            // Via state elimination a new Particle is generate from the product automaton.
            ParticleBuilder particleBuilder = new ParticleBuilder();

            // Get new particles
            Particle productParticle = particleBuilder.buildParticle(productParticleAutomaton);
            Particle substitutionParticle = particleBuilder.buildParticle(substitutionParticleAutomaton);

            // Use choice pattern to store new particles
            ChoicePattern newChoicePattern = new ChoicePattern();

            // Check if particles are null
            if (productParticle != null) {
                newChoicePattern.addParticle(productParticle);
            }
            if (substitutionParticle != null) {
                newChoicePattern.addParticle(substitutionParticle);
            }

            // Check if choice pattern contains particles
            if (newChoicePattern.getParticles().isEmpty()) {
                return null;
            }

            // Replace elements with group references if necessary
            replaceElementsWithGroupReferences(newChoicePattern, 0, null);
            return newChoicePattern;
        }
    }

    /**
     * Replaces elements with foreign namespaces in the specified particle.
     * Instead of elements new group references are placed in the particle
     * linking to the same elments in foreign schemata.
     *
     * @param particle Particle, which is the context of this operation.
     * @param particlePosition Position of the current particle in a parent
     * particle.
     * @param parentParticle Parent of the current particle. Only Particle
     * containers can be parents.
     */
    private void replaceElementsWithGroupReferences(Particle particle, int particlePosition, ParticleContainer parentParticle) {

        // For an element check if element has to be replaced
        if (particle instanceof Element) {
            Element element = (Element) particle;

            // Check again if the element was qualified and if add new group to an other schema
            if (element.getForm() != null && element.getForm() == XSDSchema.Qualification.qualified && !element.getNamespace().equals(outputSchema.getTargetNamespace())) {

                // Get other schema to store element group in
                XSDSchema otherSchema = new XSDSchema(element.getNamespace());
                otherSchema.getNamespaceList().addIdentifiedNamespace(new IdentifiedNamespace("xs", "http://www.w3.org/2001/XMLSchema"));
                otherSchema.setSchemaLocation(workingDirectory + "OtherSchema(" + element.getNamespace() + ").xsd");

                // Check if schema exist in schema groups
                if (namespaceOutputSchemaMap.get(element.getNamespace()) != null) {
                    otherSchema = namespaceOutputSchemaMap.get(element.getNamespace());
                } else {

                    // Check if other schema is present in other schema list
                    for (Iterator<XSDSchema> it = otherSchemata.iterator(); it.hasNext();) {
                        XSDSchema currentOtherSchema = it.next();

                        // If other schema has same namespace as the element use this schema
                        if (otherSchema.getTargetNamespace().equals(element.getNamespace())) {
                            otherSchema = currentOtherSchema;
                        }
                    }

                    // Add other schema to schema set
                    otherSchemata.add(otherSchema);
                }

                // Get new particle container, which contains the element
                ChoicePattern choicePattern = new ChoicePattern();
                choicePattern.addParticle(element);

                // Build new group name and avoid conflicts
                String newGroupNameBase = "{" + otherSchema.getTargetNamespace() + "}" + "element.group." + element.getLocalName();
                String newGroupName = newGroupNameBase;
                int number = 1;

                // Check if group name is already taken in the other schema and add new number to the group name base until the name is available
                while (otherSchema.getGroupSymbolTable().hasReference(newGroupName)) {
                    newGroupName = newGroupNameBase + "." + number;
                    number++;
                }

                // Build new group and add it to the other schema
                Group newGroup = new Group(newGroupName, choicePattern);
                otherSchema.getGroupSymbolTable().updateOrCreateReference(newGroupName, newGroup);
                otherSchema.addGroup(otherSchema.getGroupSymbolTable().getReference(newGroupName));

                // Generate new import components in both schemata if necessary
                generateNewImport(outputSchema, otherSchema);
                generateNewImport(otherSchema, outputSchema);

                // Register group in output schema and return group reference
                outputSchema.getGroupSymbolTable().updateOrCreateReference(newGroupName, newGroup);

                // Replace old element with group reference
                parentParticle.setParticle(particlePosition, new GroupRef(outputSchema.getGroupSymbolTable().getReference(newGroupName)));
            }
        } else if (particle instanceof ParticleContainer) {
            ParticleContainer particleContainer = (ParticleContainer) particle;

            // Check for a particle container all contained elements
            for (int i = 0; i < particleContainer.getParticles().size(); i++) {
                Particle containedParticle = particleContainer.getParticles().get(i);

                // Replace elements in particle container
                replaceElementsWithGroupReferences(containedParticle, i, particleContainer);
            }
        }
    }

    private String getDifferenceNamespace(AnyPattern minunedAny, AnyPattern subtrahendAny) {

        // Get minuend Namespace
        String minuendNamespace = null;

        // Check if namespace pattern is present or not for the minuend any pattern
        if (minunedAny.getNamespace() == null) {
            minuendNamespace = "##any";
        } else {
            minuendNamespace = minunedAny.getNamespace();
        }

        // Get subtrahend Namespace
        String subtrahendNamespace = null;

        // Check if subtrahend any pattern is present
        if (subtrahendAny != null) {

            // Check if namespace pattern is present or not for the minuend any pattern
            if (subtrahendAny.getNamespace() == null) {
                subtrahendNamespace = "##any";
            } else {
                subtrahendNamespace = subtrahendAny.getNamespace();
            }
        } else {
            subtrahendNamespace = "";
        }

        // Check if minuend namespace contains any, other or a list of namespaces
        if (minuendNamespace.contains("##any") || (minuendNamespace.contains("##other") && !anyPatternOldSchemaMap.get(minunedAny).getTargetNamespace().equals(outputSchema.getTargetNamespace()))) {

            // Check if subtrahend namespace contains any, other or a list of namespaces
            if (subtrahendNamespace.contains("##any")) {

                // Return null if both namespaces are any
                return "";

            } else if (subtrahendNamespace.contains("##other")) {

                // New namespace will contain local
                String newNamespace = "##local";

                // Check if namespace of subtrahend any pattern is not local
                if (!anyPatternOldSchemaMap.get(subtrahendAny).getTargetNamespace().equals("")) {

                    // Check if namespace of subtrahend any pattern is current target namespace and add it to list else add namespace to list
                    if (anyPatternOldSchemaMap.get(subtrahendAny).getTargetNamespace().equals(outputSchema.getTargetNamespace())) {
                        newNamespace += " ##targetNamespace";
                    } else {
                        newNamespace += " " + anyPatternOldSchemaMap.get(subtrahendAny).getTargetNamespace();
                    }
                }
                return newNamespace;
            } else {

                // Check if any can be other
                if (subtrahendAny != null) {
                    if ((anyPatternOldSchemaMap.get(subtrahendAny).getTargetNamespace().equals(outputSchema.getTargetNamespace()) && subtrahendNamespace.contains("##targetNamespace") || subtrahendNamespace.contains(outputSchema.getTargetNamespace())) && subtrahendNamespace.contains("##local")) {
                        return "##other";
                    }
                }

                // If subtrahend namespace is a list return any
                return "##any";
            }
        } else if (minuendNamespace.contains("##other")) {

            // Check if subtrahend namespace contains any, other or a list of namespaces
            if (subtrahendNamespace.contains("##any")) {

                // Return null if subtrahend namespace is any
                return "";

            } else if (subtrahendNamespace.contains("##other")) {

                // If both other are in the same namespace return null
                if (anyPatternOldSchemaMap.get(subtrahendAny).getTargetNamespace().equals(anyPatternOldSchemaMap.get(minunedAny).getTargetNamespace())) {
                    return "";
                } else {

                    // Return namespace of subtrahend other
                    return anyPatternOldSchemaMap.get(subtrahendAny).getTargetNamespace();
                }
            } else {

                // If subtrahend namespace is a list return other
                return "##other";
            }
        } else {

            // Check if subtrahend namespace contains any, other or a list of namespaces
            if (subtrahendNamespace.contains("##any")) {

                // Return null if subtrahend namespace is any
                return "";

            } else if (subtrahendNamespace.contains("##other")) {

                // Get new namespace to store result in
                String newNamespace = "";

                // Check if minuend namespace contains local
                if (minuendNamespace.contains("##local")) {
                    newNamespace += "##local";
                }

                // Check if minuned namespace contains namespace of other
                if (minuendNamespace.contains(anyPatternOldSchemaMap.get(subtrahendAny).getTargetNamespace()) || (minuendNamespace.contains("##targetNamespace") && anyPatternOldSchemaMap.get(minuendNamespace).getTargetNamespace().equals(anyPatternOldSchemaMap.get(subtrahendAny).getTargetNamespace()))) {

                    // Check if namespace of subtrahend any pattern is not local
                    if (!anyPatternOldSchemaMap.get(subtrahendAny).getTargetNamespace().equals("")) {

                        // Add delimiter
                        if (!newNamespace.equals("")) {
                            newNamespace += " ";
                        }

                        // Check if namespace of subtrahend any pattern is current target namespace and add it to list else add namespace to list
                        if (anyPatternOldSchemaMap.get(subtrahendAny).getTargetNamespace().equals(outputSchema.getTargetNamespace())) {
                            newNamespace += "##targetNamespace";
                        } else {
                            newNamespace += anyPatternOldSchemaMap.get(subtrahendAny).getTargetNamespace();
                        }
                    }
                }

                // Check if result is empty
                if (newNamespace.equals("")) {
                    return "";
                } else {
                    return newNamespace;
                }
            } else {

                // Get set of contained namespaces for minuend pattern particle
                LinkedHashSet<String> minuendNamespaces = new LinkedHashSet<String>();

                // Split minuend namespace
                for (String currentNamespace : minuendNamespace.split(" ")) {

                    // Instead of target namespaces add namespace value
                    if (currentNamespace.equals("##targetNamespace")) {
                        minuendNamespaces.add(anyPatternOldSchemaMap.get(minunedAny).getTargetNamespace());
                    } else {
                        minuendNamespaces.add(currentNamespace);
                    }
                }

                // Get set of contained namespaces for subtrahend pattern particle
                LinkedHashSet<String> subtrahendNamespaces = new LinkedHashSet<String>();

                // Split minuend namespace
                for (String currentNamespace : subtrahendNamespace.split(" ")) {

                    // Instead of target namespaces add namespace value
                    if (currentNamespace.equals("##targetNamespace")) {
                        subtrahendNamespaces.add(anyPatternOldSchemaMap.get(subtrahendAny).getTargetNamespace());
                    } else {
                        subtrahendNamespaces.add(currentNamespace);
                    }
                }

                // Remove all subtrahend namespaces from minuend namespaces
                minuendNamespaces.removeAll(subtrahendNamespaces);
                String newNamespace = "";

                // Write new namespace
                for (Iterator<String> it = minuendNamespaces.iterator(); it.hasNext();) {
                    String currentNamespace = it.next();

                    // Instead of namespace value add target namespace
                    if (currentNamespace.equals(outputSchema.getTargetNamespace())) {
                        newNamespace += "##targetNamespace";
                    } else {
                        newNamespace += currentNamespace;
                    }

                    // Add delimiter
                    if (it.hasNext()) {
                        newNamespace += " ";
                    }
                }

                // Check if result is empty
                if (newNamespace.equals("")) {
                    return "";
                } else {
                    return newNamespace;
                }
            }
        }
    }

    /**
     * Create new particle for a given element reference.
     *
     * @param oldElementRef Element reference, which is used to construct the
     * new particle.
     * @return Either a goup reference, an element reference or an element.
     */
    private Particle generateNewElementRef(ElementRef oldElementRef) {

        // If referenced element is abstract return null else generate new particle
        if (!oldElementRef.getElement().getAbstract()) {

            // Check if the referrerd element uses the same type as the type specified by the type automaton for the element name and if the referrerd element is in a conflict
            if (elementTypeMap.get(getInstanceName(oldElementRef.getElement())) == topLevelElementTypeMap.get(oldElementRef.getElement().getName()) && (namespaceConflictingElementsMap.get(oldElementRef.getElement().getNamespace()) == null || !namespaceConflictingElementsMap.get(oldElementRef.getElement().getNamespace()).contains(oldElementRef.getElement()))) {

                // Create new element reference with correct reference stored in the output schema
                ElementRef newElementRef = new ElementRef(outputSchema.getElementSymbolTable().getReference(oldElementRef.getElement().getName()));

                // Set new id and annotation for new element reference
                newElementRef.setAnnotation(generateNewAnnotation(oldElementRef.getAnnotation()));
                newElementRef.setId(getID(oldElementRef.getId()));

                // Return new group reference
                return newElementRef;
            } else {

                // Top-level elements are qualified
                oldElementRef.getElement().setForm(Qualification.qualified);

                // Generate new element for the old element reference
                Particle newElement = generateNewElement(oldElementRef.getElement(), null, elementOldTypeMap.get(getInstanceName(oldElementRef.getElement())));

                // If the new element is an element and not group reference add referenced element to the particle set
                if (newElement instanceof Element) {

                    // If new element is element get annotation and id of the old element
                    newElement.setAnnotation(generateNewAnnotation(oldElementRef.getElement().getAnnotation()));
                    newElement.setId(getID(oldElementRef.getElement().getId()));
                } else {

                    // IF new element is contained in group get annotation and id of the old element reference
                    newElement.setAnnotation(generateNewAnnotation(oldElementRef.getAnnotation()));
                    newElement.setId(getID(oldElementRef.getId()));
                }
                return newElement;
            }
        } else {
            return null;
        }
    }

    /**
     * Create new constraint (Key, KeyRef, Unique) by copying the old
     * constraint. All information stored in the old constraint is present in
     * the new constraint.
     *
     * @param oldParticle Constraint which is used to crated the new constraint,
     * so that the new constraint is a copy of the old constraint.
     * @param oldSchema XSDSchema containing the specified old constraint.
     * @return New constraint, copy the old constraint.
     */
    private Constraint generateNewConstraint(Constraint oldConstraint) {

        // Create new constraint
        Constraint newConstraint = null;

        // If old constraint is an Key create new Key.
        if (oldConstraint instanceof Key) {

            // Create new Key with the createNewKey method
            Key oldKey = (Key) oldConstraint;
            newConstraint = generateNewKey(oldKey);

        // If old constraint is an KeyRef create new KeyRef.
        } else if (oldConstraint instanceof KeyRef) {

            // Create new KeyRef with the createNewKeyRef method
            KeyRef oldKeyRef = (KeyRef) oldConstraint;
            newConstraint = generateNewKeyRef(oldKeyRef);

        // If old constraint is a Unique create new Unique.
        } else if (oldConstraint instanceof Unique) {

            // Create new Unique with the createNewUnique method
            Unique oldUnique = (Unique) oldConstraint;
            newConstraint = generateNewUnique(oldUnique);
        }

        // Return new constraint after using other create-methods to create a copie of the old constraint
        return newConstraint;
    }

    /**
     * Create new key by copying the specified old key. New key contains all
     * information of the old key, i.e. all fields of the old key are still
     * present. New key is registered in the constraint SymbolTable.
     *
     * @param oldKey Old key used to construct the new key.
     * @return New key, copy of the old key.
     */
    private Key generateNewKey(Key oldKey) {

        // Create new key with new name and selector
        Key newKey = new Key(oldKey.getName(), oldKey.getSelector());

        // Add for each field of the old key a new field to the new key
        for (Iterator<String> it = oldKey.getFields().iterator(); it.hasNext();) {
            String oldField = it.next();

            // Add new field to the new key
            newKey.addField(oldField);
        }

        // Set id and annotation for new key
        newKey.setAnnotation(generateNewAnnotation(oldKey.getAnnotation()));
        newKey.setId(getID(oldKey.getId()));

        // Add new key to SymbolTabel and new key name to list of constraint names
        outputSchema.getKeyAndUniqueSymbolTable().updateOrCreateReference(newKey.getName(), newKey);
        outputSchema.addConstraintName(newKey.getName());

        // Return new key
        return newKey;
    }

    /**
     * Create new key by copying the specified old key. New key contains all
     * information of the old key, i.e. all fields of the old key are still
     * present. New key is registered in the constraint SymbolTable.
     *
     * @param oldKey Old key used to construct the new key.
     * @return New key, copy of the old key.
     */
    private KeyRef generateNewKeyRef(KeyRef oldKeyRef) {

        // Check if key/unique is contained in the output schema or in a different output schema of another schema group
        if (!oldKeyRef.getKeyOrUnique().getNamespace().equals(outputSchema.getTargetNamespace())) {

            // Get the output schema in which the referenced key/unique will be contained
            XSDSchema otherOutputSchema = namespaceOutputSchemaMap.get(oldKeyRef.getKeyOrUnique().getNamespace());

            // Get SymbolTableRef in the other output schema
            SymbolTableRef<SimpleConstraint> symbolTableRef = otherOutputSchema.getKeyAndUniqueSymbolTable().getReference(oldKeyRef.getKeyOrUnique().getName());

            // Add this SymbolTableRef to the output schema (This is correct even when the key/unique is not present in the other output schema)
            outputSchema.getKeyAndUniqueSymbolTable().setReference(symbolTableRef.getKey(), symbolTableRef);
        }

        // Create new key with new name, selector and reference
        KeyRef newKeyRef = new KeyRef(oldKeyRef.getName(), oldKeyRef.getSelector(), outputSchema.getKeyAndUniqueSymbolTable().getReference(oldKeyRef.getKeyOrUnique().getName()));

        // Add for each field of the old key reference a new field to the new key reference
        for (Iterator<String> it = oldKeyRef.getFields().iterator(); it.hasNext();) {
            String oldField = it.next();

            // Add new field to the new key
            newKeyRef.addField(oldField);
        }

        // Set id and annotation for new key reference
        newKeyRef.setAnnotation(generateNewAnnotation(oldKeyRef.getAnnotation()));
        newKeyRef.setId(getID(oldKeyRef.getId()));

        // Add new key reference name to list of constraint names
        outputSchema.addConstraintName(newKeyRef.getName());

        // Return new key reference
        return newKeyRef;
    }

    /**
     * Generates a new import element in the specified importing XSDSchema, which
     * references the imported schema.
     *
     * @param importingSchema XSDSchema, in which the new import will be present.
     * @param importedSchema XSDSchema, which is imported.
     */
    private void generateNewImport(XSDSchema importingSchema, XSDSchema importedSchema) {

        // Construct new import-component, which refers to the referencedSchema
        ImportedSchema newImport = new ImportedSchema(importedSchema.getTargetNamespace(), importedSchema.getSchemaLocation().substring(importedSchema.getSchemaLocation().lastIndexOf("/") + 1));
        newImport.setParentSchema(importingSchema);
        newImport.setSchema(importedSchema);

        // Check if output schema already contains an import-component with same namespace and schema location
        for (Iterator<ForeignSchema> it3 = importingSchema.getForeignSchemas().iterator(); it3.hasNext();) {
            ForeignSchema foreignSchema = it3.next();

            if (foreignSchema instanceof ImportedSchema) {
                ImportedSchema currentImportedSchema = (ImportedSchema) foreignSchema;

                // Check namespace and schema location of currentImportedSchema and importedSchema
                if (currentImportedSchema.getNamespace().equals(newImport.getNamespace()) && currentImportedSchema.getSchemaLocation().equals(newImport.getSchemaLocation())) {

                    // If import-component exists in output schema set importedSchema to null
                    importedSchema = null;
                }
            }
        }

        // Check that imported schema is not null
        if (importedSchema != null) {

            // Add new import-component to the output schema
            importingSchema.addForeignSchema(newImport);

            // Construct new IdentifiedNamespace for import-component
            IdentifiedNamespace identifiedNamespace = constructNewIdentifiedNamespace(importingSchema, importedSchema.getTargetNamespace());

            // Add new IdentifiedNamespace to output schema
            if (identifiedNamespace != null) {
                importingSchema.getNamespaceList().addIdentifiedNamespace(identifiedNamespace);
            }
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
     * Create new unique by copying the specified old unique. New unique
     * contains all information of the old unique, i.e. all fields of the
     * old unique are still present. New unique is registered in the constraint
     * SymbolTable.
     *
     * @param oldUnique Old unique used to construct the new unique.
     * @return New unique, copy of the old unique.
     */
    private Unique generateNewUnique(Unique oldUnique) {

        // Create new unique with new name and selector
        Unique newUnique = new Unique(oldUnique.getName(), oldUnique.getSelector());

        // Add for each field of the old unique a new field to the new unique
        for (Iterator<String> it = oldUnique.getFields().iterator(); it.hasNext();) {
            String oldField = it.next();

            // Add new field to the new unique
            newUnique.addField(oldField);
        }

        // Set id and annotation for new unique
        newUnique.setAnnotation(generateNewAnnotation(oldUnique.getAnnotation()));
        newUnique.setId(getID(oldUnique.getId()));

        // Add new unique to SymbolTabel and new unique name to list of constraint names
        outputSchema.getKeyAndUniqueSymbolTable().updateOrCreateReference(newUnique.getName(), newUnique);
        outputSchema.addConstraintName(newUnique.getName());

        // Return new unique
        return newUnique;
    }

    /**
     * Create new particle (AnyPattern, Element, ElementRef, GroupRef,
     * ParticleContainer) by copying the old particle. All information stored
     * in the old particle is present in the new particle.
     *
     * @param oldParticle Particle which is used to created the new particle, so
     * that the new particle is a copy of the old particle.
     * @return New particle, copy the old particle.
     */
    public Particle generateNewParticle(Particle oldParticle) {

        // Create new particle
        Particle newParticle = null;

        // If old particle is an AnyPattern create new AnyPattern.
        if (oldParticle instanceof AnyPattern) {

            // Create new AnyPattern with the createNewAnyPattern method
            AnyPattern oldAnyPattern = (AnyPattern) oldParticle;
            newParticle = generateNewAnyPattern(oldAnyPattern);

        // If old particle is an Element create new Element.
        } else if (oldParticle instanceof Element) {

            // Create new Element with the createNewElement method
            Element oldElement = (Element) oldParticle;
            newParticle = generateNewElement(oldElement, null, elementOldTypeMap.get(getInstanceName(oldElement)));

        // If old particle is a ElementRef create new ElementRef.
        } else if (oldParticle instanceof ElementRef) {

            // Create new ElementRef with the createNewElementRef method
            ElementRef oldElementRef = (ElementRef) oldParticle;
            newParticle = generateNewElementRef(oldElementRef);

        // If old particle is a GroupRef create new GroupRef.
        } else if (oldParticle instanceof GroupRef) {

            // Create new GroupRef with the createNewGroupRef method
            GroupRef oldGroupRef = (GroupRef) oldParticle;
            newParticle = generateNewGroupRef(oldGroupRef);

        // If old particle is a ParticleContainer create new ParticleContainer.
        } else if (oldParticle instanceof ParticleContainer) {

            // Create new ParticleContainer with the createNewParticleContainer method
            ParticleContainer oldParticleContainer = (ParticleContainer) oldParticle;
            newParticle = generateNewParticleContainer(oldParticleContainer);
        }

        // Return new particle after using other create-methods to create a copie of the old particle
        replaceElementsWithGroupReferences(newParticle, 0, null);
        return newParticle;
    }

    /**
     * Creates a new particle container with new particles after the model of
     * the specified old particle container. For each particle contained in the
     * old particle container a new particle is created. IDs, annotations and
     * min/maxOccurs (for CountingPatterns) are set to the values of the old
     * particle container for the new particle container.
     *
     * @param oldParticleContainer Particle container used to create a new
     * particle container, which stores for each particle of the old particle
     * container a new particle matching the old particle.
     * @param oldSchema XSDSchema in which the partitcle container is contained
     * (not directly because particle container are only allowed in groups and
     * types but indirect)
     * @return New particle container matching the old particle container with
     * new particles.
     */
    private ParticleContainer generateNewParticleContainer(ParticleContainer oldParticleContainer) {

        // Create new particle container
        ParticleContainer newParticleContainer = null;

        // If old particle container is an AllPattern create new AllPattern.
        if (oldParticleContainer instanceof AllPattern) {
            newParticleContainer = new AllPattern();

        // If old particle container is a ChoicePattern create new ChoicePattern.
        } else if (oldParticleContainer instanceof ChoicePattern) {
            newParticleContainer = new ChoicePattern();

        // If old particle container is a SequencePattern create new SequencePattern.
        } else if (oldParticleContainer instanceof SequencePattern) {
            newParticleContainer = new SequencePattern();

        // If old particle container is a CountingPattern create new CountingPattern.
        } else if (oldParticleContainer instanceof CountingPattern) {

            // Get old counting pattern to set min and max values
            CountingPattern oldCountingPattern = (CountingPattern) oldParticleContainer;

            // Create new counting pattern with min and max values of the old counting pattern
            newParticleContainer = new CountingPattern(oldCountingPattern.getMin(), oldCountingPattern.getMax());
        }

        // Set new id and annotation for the new particle container
        newParticleContainer.setAnnotation(generateNewAnnotation(oldParticleContainer.getAnnotation()));
        newParticleContainer.setId(getID(oldParticleContainer.getId()));

        // Get list of all old particles containes in the old particle container
        LinkedList<Particle> oldParticles = oldParticleContainer.getParticles();

        // For each old particle create a new particle and add it to new particle container
        for (Iterator<Particle> it = oldParticles.iterator(); it.hasNext();) {
            Particle oldParticle = it.next();

            // New create particle is added to new particle container
            newParticleContainer.addParticle(generateNewParticle(oldParticle));
        }

        // Return new created particle container after it is complete
        return newParticleContainer;
    }

    /**
     * This method create a new top-level element which is the result of a
     * difference computation. The computed difference is the difference of
     * the minuend element and the subtrahend element. Most important if the
     * minuend element is defined abstract the result will be empty.
     *
     * @param minuendElement Element from which the subtrahend element and its
     * attributes are removed from.
     * @param subtrahendElement Element which is removed from the minuend
     * element.
     * @param typeRef Reference to the type of the resulting element.
     */
    public void generateNewTopLevelElement(Element minuendElement, Element subtrahendElement, SymbolTableRef<Type> typeRef) {

        // If minuend element is abstract nothing is returned
        if (minuendElement.getAbstract() || typeRef.getReference().isDummy()) {
            return;
        }

        // Create new difference element
        Element newElement = null;

        // Check if subtrahend element is abstract
        if (subtrahendElement != null && !subtrahendElement.getAbstract()) {
            newElement = generateNewElement(minuendElement, subtrahendElement, typeRef);
        } else {
            newElement = generateNewElement(minuendElement, null, typeRef);
        }

        // Set "abstract" value for new top-level element to the value of the minuend element (Abstract elements can not be used in the schema)
        newElement.setAbstract(minuendElement.getAbstract());
        newElement.setForm(null);

        // "final" attribute is present add "final" value to new element
        newElement.setFinalModifiers(getFinal(minuendElement));

        // Add new element to the list of top-level elements
        outputSchema.addElement(outputSchema.getElementSymbolTable().getReference(newElement.getName()));
    }

    /**
     * Create new element which represents the result of the difference of the
     * minuend element and the subtrahend element. The new elemente is used in
     * particles of complexTypes and by the top-level element creation method.
     * All attributes contained in the element are the results of the difference
     * computation.
     *
     * @param minuendElement Element from which the subtrahend element and its
     * attributes are removed from.
     * @param subtrahendElement Element which is removed from the minuend
     * element.
     * @param typeRef Reference to the type of the resulting element. Given by
     * the caller.
     * @return Resulting element of the difference process. May be null if
     * the result is empty, i.e. both elements have the same "fixed" values and
     * the result is null.
     */
    public Element generateNewElement(Element minuendElement, Element subtrahendElement, SymbolTableRef<Type> typeRef) {

        // Create new elment with same name as the minuend element
        Element newElement = new Element(minuendElement.getName());

        // Set new type to the type found in the type SymbolTable of the schema and check if "type" attribute is needed
        newElement.setType(typeRef);
        newElement.setTypeAttr(minuendElement.getTypeAttr());

        // Only if the subtrahned element is not nillable and minuend element is nillable set the value of the "nillable" attribute to true
        if ((subtrahendElement != null && !subtrahendElement.getNillable()) && minuendElement.getNillable()) {
            newElement.setNillable();
        }

        // Set id and annotation for the new element to the annotations of the old minuend element.
        newElement.setAnnotation(generateNewAnnotation(minuendElement.getAnnotation()));
        newElement.setId(getID(minuendElement.getId()));

        // The "default" value of the new element is the same as the "default" value of the minuend element
        newElement.setDefault(minuendElement.getDefault());

        // If both element contain the same "fixed" value the difference is empty
        if (minuendElement.getFixed() != null && (subtrahendElement != null && subtrahendElement.getFixed() != null && subtrahendElement.getFixed().equals(minuendElement.getFixed()))) {
            return null;
        } else {
            newElement.setFixed(minuendElement.getFixed());
        }

        // "block" attribute is present add "block" value to new element
        newElement.setBlockModifiers(getBlock(minuendElement));

        // Check if "form" attribute is present in minuend element
        if (minuendElement.getForm() == null) {

            // Set "form" value to the value of the "elementFormDefault" attribute
            newElement.setForm(null);
        } else {

            // "form" attribute is present add "form" value to minuend element
            if (minuendElement.getForm() == Qualification.qualified) {
                newElement.setForm(minuendElement.getForm());
            }
        }

        // Add for each constraint of the minuend element a new constraint
//        for (Iterator<Constraint> it = minuendElement.getConstraints().iterator(); it.hasNext();) {
//            Constraint oldConstraint = it.next();
//
//            // Add new constraint to the new element
//            newElement.addConstraint(generateNewConstraint(oldConstraint));
//        }
        outputSchema.getElementSymbolTable().updateOrCreateReference(newElement.getName(), newElement);

        // Return new complete element
        return newElement;
    }

    /**
     * This method checks if the difference of a given minuend element and a
     * specified subtrahend element is empty. Difference is only computed for
     * the element itself and not the contained type definition.
     *
     * @param minunedElement Element from which the subtrahend is deducted.
     * @param subtrahendElement Element which is deducted from the minuned
     * element.
     * @return <tt>true</tt> if the difference of the minuend element and the
     * subtrahend element is empyt.
     */
    public boolean isDifferenceEmpty(Element minunedElement, Element subtrahendElement) {

        // If the minuend element is null the difference can not be constructed
        if (minunedElement == null) {
            return true;
        }

        // If the subtrahend element is null the difference is not empty
        if (subtrahendElement == null) {
            return false;
        }

        // Check if minuned element has the same name as the subtrahend name
        if (minunedElement.getLocalName().equals(subtrahendElement.getLocalName())) {

            // Check if form attribute of both elements is equal
            if (minunedElement.getForm() == subtrahendElement.getForm()) {

                // Check that no element is abstract
                if (!minunedElement.getAbstract() && !subtrahendElement.getAbstract()) {

                    // Check that the subtrahend element has no "fixed" value or that both elements have the same "fixed" value
                    if (subtrahendElement.getFixed() == null || subtrahendElement.getFixed().equals(minunedElement.getFixed())) {

                        // If all these checks are passed the difference of both elements is empty (Type is checked by the particle automaton process)
                        return true;
                    } else {

                        // If the subtrahend element is fixed the difference is not empty if the minuned "fixed" value is not the same(Result: minuned element)
                        return false;
                    }
                } else {

                    // If one of the two elements is abstract the difference is not empty (Result: minuned element)
                    return false;
                }
            } else {

                // Both elements have different form attributes so the difference is not empty (Result: minuend element)
                return false;
            }
        } else {

            // Both elements have different names so the difference is not empty (Result: minuned element)
            return false;
        }
    }

    /**
     * Creates a new annotation with new appInfos and new documentations by
     * copying the given old annotation.
     *
     * @param oldAnnotation Blueprint for the new annotation.
     * @return New Annotation matching the old annotation.
     */
    private Annotation generateNewAnnotation(Annotation oldAnnotation) {

        // Check if old annotation is present
        if (oldAnnotation != null) {

            // Create new annotation
            Annotation newAnnotation = new Annotation();

            // Add id of the old annotation to the new annotation
            if (oldAnnotation.getId() != null) {
                newAnnotation.setId(getID(oldAnnotation.getId()));
            }

            // Get all old appInfos
            LinkedList<AppInfo> oldAppInfos = oldAnnotation.getAppInfos();

            // For each old appInfo create a new appInfo and add it to the annotation
            for (Iterator<AppInfo> it = oldAppInfos.iterator(); it.hasNext();) {
                AppInfo oldAppInfo = it.next();

                // Create new appInfo and add it to the appInfo list of the new annotation
                newAnnotation.addAppInfos(generateNewAppInfo(oldAppInfo));
            }

            // Get all old documentations
            LinkedList<Documentation> oldDocumentations = oldAnnotation.getDocumentations();

            // For each old documentation create a new documentation and add it to the annotation
            for (Iterator<Documentation> it = oldDocumentations.iterator(); it.hasNext();) {
                Documentation oldDocumentation = it.next();

                // Create new documentation and add it to the documentation list of the new annotation
                newAnnotation.addDocumentations(generateNewDocumentation(oldDocumentation));
            }

            // Retrun new created annotation
            return newAnnotation;
        }
        return null;
    }

    /**
     * Create new appInfo after the model of a specified old appInfo.
     *
     * @param oldAppInfo Old appInfo used to create the new appInfo.
     * @return New appInfo containing all information of the specified old
     * appInfo.
     */
    private AppInfo generateNewAppInfo(AppInfo oldAppInfo) {

        // Create new appInfo
        AppInfo newAppInfo = new AppInfo();

        // Set new node list for the new appInfo (node list is taken directly from the old appInfo because no operation changes this DOM structure)
        newAppInfo.setNodeList(oldAppInfo.getNodeList());

        // Set new source for the new appInfo
        newAppInfo.setSource(oldAppInfo.getSource());
        return newAppInfo;
    }

    /**
     * Create new documentation after the model of a specified old documentation.
     *
     * @param oldAppInfo Old documentation used to create the new documentation.
     * @return New documentation containing all information of the specified old
     * documentation.
     */
    private Documentation generateNewDocumentation(Documentation oldDocumentation) {

        // Create new documentation
        Documentation newDocumentation = new Documentation();

        // Set new node list for the new documentation (node list is taken directly from the old appInfo because no operation changes this DOM structure)
        newDocumentation.setNodeList(oldDocumentation.getNodeList());

        // Set new source and XmlLang attributes for the new documentation
        newDocumentation.setSource(oldDocumentation.getSource());
        newDocumentation.setXmlLang(oldDocumentation.getXmlLang());
        return newDocumentation;
    }

    /**
     * Creates new any pattern for a given any pattern. New and old any pattern
     * are identical but different objects. AnyPatterns do not contain
     * references to schema components.
     *
     * @param anyPattern Blueprint for the new any pattern.
     * @return New any pattern an exact copy of the specified old any pattern.
     */
    private Particle generateNewAnyPattern(AnyPattern anyPattern) {

        // Check if any pattern contains a "processContents" attribute
        if (anyPattern.getProcessContentsInstruction().equals(ProcessContentsInstruction.Strict)) {

            // Create new choice pattern to contain the referred elements
            ChoicePattern choicePattern = new ChoicePattern();

            // Add each referred element to the choice pattern
            for (Iterator<Element> it = getContainedElements(anyPattern, anyPatternOldSchemaMap.get(anyPattern)).iterator(); it.hasNext();) {
                Element element = it.next();
                choicePattern.addParticle(generateNewElement(element, null, elementOldTypeMap.get(getInstanceName(element))));
            }

            // Check if choice contains no elements
            if (choicePattern.getParticles().isEmpty()) {
                return null;
            }
            return choicePattern;
        } else {

            // Create new any pattern with new "ID" attribute and annotation
            AnyPattern newAnyPattern = new AnyPattern(ProcessContentsInstruction.Skip, getDifferenceNamespace(anyPattern, null));
            newAnyPattern.setId(getID(anyPattern.getId()));
            newAnyPattern.setAnnotation(generateNewAnnotation(anyPattern.getAnnotation()));
            return newAnyPattern;
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

    /**
     * This method creates a new top-level group. This group will be a copie of
     * the specified old group contained in the given old schema. Furthermore
     * the new group will be registered in the SymbolTable of the new schema and
     * in the list of top-level groups.
     *
     * @param topLevelGroup Group which is the blueprint for the new group.
     * @return A SymbolTableRef to the new group contained in the new schema.
     */
    private Group generateNewTopLevelGroup(Group topLevelGroup) {

        // Create new particle container for the new group by copying the old particle container
        ParticleContainer newContainer = generateNewParticleContainer(topLevelGroup.getParticleContainer());

        // Create new group with the name of the old group and a new container
        Group newGroup = new Group("{" + outputSchema.getTargetNamespace() + "}" + topLevelGroup.getLocalName(), newContainer);

        // Create new annotation and id and add them to the new group
        newGroup.setAnnotation(generateNewAnnotation(topLevelGroup.getAnnotation()));
        newGroup.setId(getID(topLevelGroup.getId()));

        // Create new SymbolTableRef
        outputSchema.getGroupSymbolTable().updateOrCreateReference(newGroup.getName(), newGroup);

        // Add new group to the list of top-level groups
        outputSchema.addGroup(outputSchema.getGroupSymbolTable().getReference(newGroup.getName()));

        return newGroup;
    }

    /**
     * Create new any pattern by removing the subtrahend any pattern from the
     * minuend any pattern. Basically this is done by removing the namespaces
     * suppurted by the subtrahend any pattern from the namespaces of the
     * minuend any pattern.
     *
     * @param minunedAnyPattern Any pattern from which the subtrahend any
     * pattern is removed.
     * @param subtrahendAnyPattern Any pattern which is removed from the minuned
     * any pattern.
     * @return New any patter which only contains namespaces contained in the
     * minuend any pattern but not in the subtrahend.
     */
    public AnyPattern generateNewDifferenceAnyPattern(AnyPattern minunedAnyPattern, AnyPattern subtrahendAnyPattern) {

        // Get new "namespace" attribute which can be empty if the subtrahend any pattern contains the minuend any pattern
        String newNamespace = getDifferenceNamespace(minunedAnyPattern, subtrahendAnyPattern);

        // Check if difference is possible. This is the case if the "namespace" attribute is not empty.
        if (newNamespace != null && !newNamespace.equals("")) {

            // Create new any pattern with namespace difference
            AnyPattern newAnyPattern = new AnyPattern(ProcessContentsInstruction.Skip, newNamespace);

            // Set new id and annotation for new any pattern
            newAnyPattern.setAnnotation(generateNewAnnotation(minunedAnyPattern.getAnnotation()));
            newAnyPattern.setId(getID(minunedAnyPattern.getId()));

            // Return new any pattern
            return newAnyPattern;
        } else {

            // Intersection is empty
            return null;
        }
    }

    /**
     * Method tests if a set of any patterns has a non empty intersection. This
     * is the case if the intersection of namespace elements is not empty. If
     * the intersection is not empty a new any pattern can be generated.
     *
     * @param anyPatterns Set of AnyPatterns for which the test is performed.
     * @return <tt>true</tt> is the intersection is possible and not empty, else
     * <tt>false</tt>.
     */
    public boolean areIntersectableAnyPatterns(LinkedHashSet<AnyPattern> anyPatterns) {

        // Check if only one any pattern is present
        if (anyPatterns.size() == 1) {
            return true;
        }
        String namespaceIntersection = getIntersectionNamespace(anyPatterns);

        // Check if new namespace attribute contains values
        if (namespaceIntersection != null && !namespaceIntersection.equals("")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Constructs the intersection of a minunend and a subtrahend any pattern.
     * This is usefull for the particle automaton if an minuend any pattern is
     * split into two any patterns.
     *
     * @param minunedAnyPattern Any pattern which is a minuend particle.
     * @param subtrahendAnyPattern Any pattern which is a subtrahend particle.
     * @return New any pattern which contains the intersection of both
     * minuend and subtrahend any pattern.
     */
    public AnyPattern generateNewIntersectionAnyPattern(AnyPattern minunedAnyPattern, AnyPattern subtrahendAnyPattern) {

        // Get set containing minuend and subtrahend any pattern
        LinkedHashSet<AnyPattern> anyPatterns = new LinkedHashSet<AnyPattern>();
        anyPatterns.add(minunedAnyPattern);
        anyPatterns.add(subtrahendAnyPattern);

        // Generate new "namespace" attribute and check if the attribute is valid
        String newNamespace = getIntersectionNamespace(anyPatterns);

        // Return null if the "namespace" attribute is invalid
        if (newNamespace.equals("")) {
            return null;
        }

        // Create new any pattern with namespace intersection
        AnyPattern newAnyPattern = new AnyPattern(ProcessContentsInstruction.Skip, newNamespace);

        // Set new id and annotation for new any pattern
        newAnyPattern.setAnnotation(generateNewAnnotation(minunedAnyPattern.getAnnotation()));
        newAnyPattern.setId(getID(minunedAnyPattern.getId()));

        // Return the new any pattern
        return newAnyPattern;
    }

    /**
     * Generates a new "namespace" attribute for a new any pattern. The
     * attribute is constructed as intersection of the given any pattern set. To
     * generate an accurate intersection the target namespaces of the any
     * patterns are considered too. This is important for values like "##other",
     * which selects all namespaces different from the target namespace of the
     * current any pattern.
     *
     * @param anyPatterns Set of any patterns each may contain a "namespace"
     * attribute.
     * @return New list of namespaces. The list is represented as String and
     * represents an intersection of the "namespace" values of specified any
     * patterns.
     */
    private String getIntersectionNamespace(LinkedHashSet<AnyPattern> anyPatterns) {

        // Use boolean variabel to check if each any pattern has "namespace" value "##any"
        boolean any = true;

        // Get a set containing all namespaces contained in the any patterns
        HashSet<String> namespaces = new HashSet<String>();

        // For each any pattern check the contained "namespace" attribute
        for (Iterator<AnyPattern> it = anyPatterns.iterator(); it.hasNext();) {
            AnyPattern anyPattern = it.next();

            if (anyPattern != null) {

                // If any pattern contains "namespace" attribute check contained values else add "##any" to the namespace
                if (anyPattern.getNamespace() != null) {

                    // Get current "namespace" attribute
                    String namespace = anyPattern.getNamespace();

                    // If "namespace" attribute equals "##any" or "##other" or "##local" add namespace to namespace set
                    if (namespace.equals("##any") || namespace.equals("##other") || namespace.equals("##local")) {
                        namespaces.add(namespace);
                    } else if (namespace.equals("##targetNamespace")) {

                        // If "namespace" attribute equals "##targetNamespace" get the target namespace of the containing schema and add it to the namespace set
                        namespaces.add(anyPatternOldSchemaMap.get(anyPattern).getTargetNamespace());
                    } else {

                        // If the "namespace" attribute contains a list of namespaces split the string and add namespaces to the namespace set
                        for (String currentNamespace : namespace.split(" ")) {

                            // If current namespace equals "##local" or an URI add the namespace to the set
                            if (currentNamespace.equals("##local") || !currentNamespace.equals("##targetNamespace")) {
                                namespaces.add(currentNamespace);

                            } else if (currentNamespace.equals("##targetNamespace")) {

                                // For a current namespace with value "##targetNamespace" get the target namespace of the containing schema and add it to the namespace set
                                namespaces.add(anyPatternOldSchemaMap.get(anyPattern).getTargetNamespace());
                            }
                        }
                    }
                } else {

                    // If any pattern has no "namespace" attribute the default values is "##any"
                    namespaces.add("##any");
                }

                // If the current any pattern contains a "namespace" attribute which contains no "##any" the any pattern has no "##any" value
                if (anyPattern.getNamespace() != null && !anyPattern.getNamespace().contains("##any")) {
                    any = false;
                }
            }
        }

        // If each any pattern contained an "##any" value return "##any" as value of the any pattern
        if (any) {
            return "##any";
        }

        // Check for each "namespace" if each any pattern contains it
        for (Iterator<String> it = namespaces.iterator(); it.hasNext();) {
            String namespace = it.next();
            for (Iterator<AnyPattern> it2 = anyPatterns.iterator(); it2.hasNext();) {
                AnyPattern anyPattern = it2.next();

                if (anyPattern != null && namespaces.contains(namespace)) {

                    // Set namespace of the any attribute to the default value
                    if (anyPattern.getNamespace() == null) {
                        anyPattern.setNamespace("##any");
                    }

                    // If current "namespace" is "##any" it can be removed (as it was checked beforehand)
                    if (namespace.equals("##any")) {
                        it.remove();

                    } else if (namespace.equals("##other")) {

                        // If current "namespace" is "##other" and if current any pattern contains no "namespace" with "##any" or "##other" remove it
                        if (anyPattern.getNamespace() != null && !anyPattern.getNamespace().contains("##any") && !anyPattern.getNamespace().contains("##other")) {
                            it.remove();
                        }
                    } else if (namespace.equals("##local")) {

                        // If current "namespace" is "##local" and if current any pattern contains no "namespace" with "##any" or "##local", remove it
                        if (anyPattern.getNamespace() != null && !anyPattern.getNamespace().contains("##any") && !anyPattern.getNamespace().contains("##local")) {
                            it.remove();
                        }
                    } else {

                        // If current "namespace" has any other value and if current any pattern contains no "namespace" with "##any"or the "namespace" value itself or "other", when the target namespace is not the specified namespace, remove it
                        if (anyPattern.getNamespace() != null && !anyPattern.getNamespace().contains("##any") && !anyPattern.getNamespace().contains(namespace) && !(anyPattern.getNamespace().contains("##targetNamespace") && anyPatternOldSchemaMap.get(anyPattern).getTargetNamespace().equals(namespace)) && !((anyPattern.getNamespace().contains("##other") && !anyPatternOldSchemaMap.get(anyPattern).getTargetNamespace().equals(namespace)))) {
                            it.remove();
                        }
                    }
                }
            }
        }

        // After removing all "namespace" values not contained in each any pattern check if namespace set contains "##other"
        if (namespaces.contains("##other")) {

            // For each any pattern check if the new "namespace" attribute will have value "##other"
            for (Iterator<AnyPattern> it = anyPatterns.iterator(); it.hasNext();) {
                AnyPattern anyPattern = it.next();

                // If an "##other" valued "namepace" with same target namespace exist return "##other"
                if (anyPattern.getNamespace() != null && anyPattern.getNamespace().contains("##other") && anyPatternOldSchemaMap.get(anyPattern).getTargetNamespace().equals(outputSchema.getTargetNamespace())) {
                    return "##other";
                }
            }

            // If no "##other" valued "namespace" was constructed return "##any"
            return "##any";
        } else {

            // Build "namespace" value string list
            String newNamespace = "";

            // For each namespace contained in the namespace list add a value to the string
            for (Iterator<String> it = namespaces.iterator(); it.hasNext();) {
                String namespace = it.next();

                // If current namespace is the target namespace add value "##targetNamespace" to the string else the namespace value
                if (outputSchema.getTargetNamespace().equals(namespace)) {
                    newNamespace += "##targetNamespace";
                } else {
                    newNamespace += namespace;
                }

                // Check if namespace follows and add whitespace
                if (it.hasNext()) {
                    newNamespace += " ";
                }
            }

            // Return the generated "namespace" attribute
            return newNamespace;
        }
    }

    /**
     * Create new group reference for a given old group reference. The new group
     * reference is a complete copy of the old group reference. If the reference
     * links to a group in another schema this element will be referred to by
     * the new group reference as well. If it links to an group of the old
     * schema the new refernce will link to the corresponding group in the
     * current new schema.
     *
     * @param oldGroupRef Old group reference which is used to construct the new
     * group reference.
     * @return New group reference linking to the correct group (either a new
     * group in the new schema or an old group in a foreign schema).
     */
    private Particle generateNewGroupRef(GroupRef oldGroupRef) {

        // Get new map mapping element names to type references for all elements contained in the group reference
        HashMap<String, SymbolTableRef<Type>> groupElementTypeMap = getGroupElementTypeMap(oldGroupRef);

        // For all element names check if the type specified in the particle map is equivalent to the type of the group map
        for (Iterator<String> it = groupElementTypeMap.keySet().iterator(); it.hasNext();) {
            String elementName = it.next();

            // Check if the element name is contained in the elementTypeMap and if both entries are not the same
            if (elementOldTypeMap.containsKey(elementName) && elementOldTypeMap.get(elementName) != groupElementTypeMap.get(elementName)) {
                ParticleContainer particleContainer = oldGroupRef.getGroup().getParticleContainer();

                // Return contained particle container instead of the group
                return generateNewParticleContainer(particleContainer);
            }
        }

        // Generate new top-level group
        Group newGroup = generateNewTopLevelGroup((Group) oldGroupRef.getGroup());

        // Create new group reference with correct reference stored in the output schema
        GroupRef newGroupRef = new GroupRef(outputSchema.getGroupSymbolTable().getReference(newGroup.getName()));

        // Set new id and annotation for new group reference
        newGroupRef.setAnnotation(generateNewAnnotation(oldGroupRef.getAnnotation()));
        newGroupRef.setId(getID(oldGroupRef.getId()));

        // Return new group reference
        return newGroupRef;
    }

    /**
     * Check if the specified element name is included in an any pattern of the
     * given set of any patterns.
     *
     * @param elementName Specific element name, which may be included in an
     * any pattern of the given set of any patterns.
     * @param anyPatterns Set of any patterns.
     * @return <tt>true</tt> if the element is included in a given any pattern.
     */
    public boolean isIncluded(String elementName, LinkedHashSet<AnyPattern> anyPatterns) {

        // Get namespace of the element
        String namespaceElement = elementName.substring(1, elementName.lastIndexOf("}"));

        // For each any patter is tested if one includes the specfied namespace
        for (Iterator<AnyPattern> it = anyPatterns.iterator(); it.hasNext();) {
            AnyPattern anyPattern = it.next();

            // Compare namespace with different "namespace" attribute values
            if (anyPattern.getNamespace() == null || anyPattern.getNamespace().contains("##any")) {
                return true;
            } else if (anyPattern.getNamespace().contains("##other") && !anyPatternOldSchemaMap.get(anyPattern).getTargetNamespace().equals(namespaceElement)) {
                return true;
            } else if (anyPattern.getNamespace().contains("##local") && namespaceElement.equals("")) {
                return true;
            } else if (anyPattern.getNamespace().contains("##targetNamespace") && anyPatternOldSchemaMap.get(anyPattern).getTargetNamespace().equals(namespaceElement)) {
                return true;
            } else if (anyPattern.getNamespace().contains(namespaceElement)) {
                return true;
            }
        }

        // If namespace is not included return false
        return false;
    }

    /**
     * This method constructs an element name to type reference map for all
     * element names contained in the specified particle. This map can be
     * compared to the elementTypeMap of the current particle to check whether
     * a the group has to be resolved or not.
     *
     * @param particle Particle containing other particles, liked elements.
     * @return New map mapping element names to type references.
     */
    private HashMap<String, SymbolTableRef<Type>> getGroupElementTypeMap(Particle particle) {

        // Initialize new group element names to types map
        HashMap<String, SymbolTableRef<Type>> groupElementTypeMap = new HashMap<String, SymbolTableRef<Type>>();

        // Check if particle is a particle container
        if (particle instanceof ParticleContainer) {
            ParticleContainer particleContainer = (ParticleContainer) particle;

            // Check for each contained particle, if the particle contains elements and update groupElementTypeMap
            for (Iterator<Particle> it = particleContainer.getParticles().iterator(); it.hasNext();) {
                Particle containedParticle = it.next();
                groupElementTypeMap.putAll(getGroupElementTypeMap(containedParticle));
            }
        } else if (particle instanceof GroupRef) {

            // If particle is a group reference check referred group for elements
            Group group = (Group) ((GroupRef) particle).getGroup();
            groupElementTypeMap.putAll(getGroupElementTypeMap(group.getParticleContainer()));

        } else if (particle instanceof Element) {

            // If particle is an element add new entry to the groupElementTypeMap
            Element element = (Element) particle;
            groupElementTypeMap.put(element.getName(), outputSchema.getTypeSymbolTable().getReference(element.getType().getName()));

        } else if (particle instanceof ElementRef) {

            // If particle is an element reference add new entry to the groupElementTypeMap for the referred element
            Element element = ((ElementRef) particle).getElement();
            groupElementTypeMap.put(element.getName(), outputSchema.getTypeSymbolTable().getReference(element.getType().getName()));
        }

        // Return fully updated groupElementTypeMap
        return groupElementTypeMap;
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
        if (anyPattern.getProcessContentsInstruction().equals(ProcessContentsInstruction.Strict) || anyPattern.getProcessContentsInstruction().equals(ProcessContentsInstruction.Lax)) {

            // Initalize set to store all top-level elements
            LinkedHashSet<Element> topLevelElements = new LinkedHashSet<Element>();

            // If any pattern namespace attribute contains "##any"
            if (anyPattern.getNamespace() == null || anyPattern.getNamespace().contains("##any")) {

                // Add all elements contained in the schema to the set
                topLevelElements.addAll(schema.getElements());

                // Add all elements contained in foreign schemata to the set
                for (Iterator<ForeignSchema> it = schema.getForeignSchemas().iterator(); it.hasNext();) {
                    ForeignSchema foreignSchema = it.next();
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
                        } else {

                            // If any pattern namespace attribute contains "##local" only add elements contained in foreign schemata to the set
                            for (Iterator<ForeignSchema> it = anyPatternOldSchemaMap.get(anyPattern).getForeignSchemas().iterator(); it.hasNext();) {
                                ForeignSchema foreignSchema = it.next();

                                // Check if the current namespace is the namespace of the foreign schema
                                if (foreignSchema.getSchema().getTargetNamespace().equals("")) {
                                    topLevelElements.addAll(foreignSchema.getSchema().getElements());
                                }
                            }
                            return topLevelElements;
                        }
                    } else if (currentNamespace.contains("##targetNamespace")) {

                        // Add all elements contained in the schema to the set
                        topLevelElements.addAll(schema.getElements());

                    } else {

                        // Find foreign schema with the specified namespace
                        for (Iterator<ForeignSchema> it = schema.getForeignSchemas().iterator(); it.hasNext();) {
                            ForeignSchema foreignSchema = it.next();

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
     * Generates a new "final" attribute from a given element. Because the
     * "final" attribute of an element restricts the type inheritance, the
     * minuend "final" value is computed as result of the difference. Only
     * top-level elements may contain "final" values.
     *
     * @param element Element may contains a "final" value, which then can be
     * used to compute the new "final" value.
     * @return Set of Final enums, which represents the new "final" value.
     */
    private HashSet<Final> getFinal(Element element) {

        // Generate new "final" attribute
        HashSet<Final> finalValue = new HashSet<Final>();

        // If "final" attribute is present add contained Final value from the "final" attribute of the new element
        if (element.getFinalModifiers() != null) {

            // Check if "extension" is contained and if add "extension" from "final" attribute of the new element
            if (element.getFinalModifiers().contains(Final.extension)) {
                finalValue.add(Final.extension);
            }
            // Check if "restriction" is contained and if add "restriction" from "final" attribute of the new element
            if (element.getFinalModifiers().contains(Final.restriction)) {
                finalValue.add(Final.restriction);
            }
        } else if (elementOldSchemaMap.get(element).getFinalDefaults() != null) {

            // Add Final values contained in the default value
            if (elementOldSchemaMap.get(element).getFinalDefaults().contains(FinalDefault.extension)) {
                finalValue.add(Final.extension);
            }
            if (elementOldSchemaMap.get(element).getFinalDefaults().contains(FinalDefault.restriction)) {
                finalValue.add(Final.restriction);
            }
        }

        // Generate a set containing FinalDefault values for the Final values contained in the set of the new element
        LinkedHashSet<XSDSchema.FinalDefault> finalDefaults = new LinkedHashSet<XSDSchema.FinalDefault>();

        // Check if "extension" is contained and if add "extension"
        if (finalValue.contains(Final.extension)) {
            finalDefaults.add(XSDSchema.FinalDefault.extension);
        }
        // Check if "restriction" is contained and if add "restriction"
        if (finalValue.contains(Final.restriction)) {
            finalDefaults.add(XSDSchema.FinalDefault.restriction);
        }

        // Check if new "final" values is different than the default value of the output schema
        if (!(outputSchema.getFinalDefaults().containsAll(finalDefaults) && finalDefaults.containsAll(outputSchema.getFinalDefaults()))) {
            return finalValue;
        } else {

            // If output schema has the same "finalDefault" value as the new elements "final" attribute return null
            return null;
        }
    }

    /**
     * Generates a new "block" value from a given element. The "block"
     * attribute of XML XSDSchema restricts the use of derived types, so when
     * computing the difference the most minuend "block" value is computed.
     *
     * @param element Element contains a "block" value, which is used to compute
     * the new "block" attribute.
     * @return Set of ComplexTypeInheritanceModifier, which represents the new
     * "block" attribute.
     */
    private HashSet<Block> getBlock(Element element) {

        // Generate new "block" attribute
        HashSet<Block> block = new HashSet<Block>();

        // If "block" attribute is present add contained Block value from the "block" attribute of the new element
        if (element.getBlockModifiers() != null) {

            // Check if "extension" is contained and if add "extension" from "block" attribute of the new element
            if (element.getBlockModifiers().contains(Block.extension)) {
                block.add(Block.extension);
            }
            // Check if "restriction" is contained and if add "restriction" from "block" attribute of the new element
            if (element.getBlockModifiers().contains(Block.restriction)) {
                block.add(Block.restriction);
            }
            // Check if "substitution" is  contained and if add "substitution" from "block" attribute of the new element
            if (element.getBlockModifiers().contains(Block.substitution)) {
                block.add(Block.substitution);
            }
        } else if (elementOldSchemaMap.get(element) != null && elementOldSchemaMap.get(element).getBlockDefaults() != null) {

            // Add Block values contained in the default value
            if (elementOldSchemaMap.get(element).getBlockDefaults().contains(BlockDefault.extension)) {
                block.add(Block.extension);
            }
            if (elementOldSchemaMap.get(element).getBlockDefaults().contains(BlockDefault.restriction)) {
                block.add(Block.restriction);
            }
            if (elementOldSchemaMap.get(element).getBlockDefaults().contains(BlockDefault.substitution)) {
                block.add(Block.substitution);
            }
        }

        // Generate a set containing BlockDefault values for the Block values contained in the set of the new element
        LinkedHashSet<XSDSchema.BlockDefault> blockDefaults = new LinkedHashSet<XSDSchema.BlockDefault>();

        // Check if "extension" is contained and if add "extension"
        if (block.contains(Block.extension)) {
            blockDefaults.add(XSDSchema.BlockDefault.extension);
        }
        // Check if "restriction" is contained and if add "restriction"
        if (block.contains(Block.restriction)) {
            blockDefaults.add(XSDSchema.BlockDefault.restriction);
        }
        // Check if "substitution" is contained and if add "substitution"
        if (block.contains(Block.substitution)) {
            blockDefaults.add(XSDSchema.BlockDefault.substitution);
        }

        // Check if new "block" values is different than the default value of the output schema
        if (!(outputSchema.getBlockDefaults().containsAll(blockDefaults) && blockDefaults.containsAll(outputSchema.getBlockDefaults()))) {
            return block;
        } else {

            // If output schema has the same "blockDefault" value as the new elements "block" attribute return null
            return null;
        }
    }

    /**
     * Generates a new ID for a given ID. New ID is valid for all output schema.
     *
     * @param id String, which contains an ID.
     * @return New ID for the given ID.
     */
    private String getID(String id) {

        // Check if a ID was generated
        if (id != null) {

            //  Use ID base and number to find a valid ID
            String newIDBase = id;
            int number = 1;

            // As long as the new ID is invalid use another new ID
            while (usedIDs.contains(id)) {

                // Add a number to the base of the new ID
                id = newIDBase + "." + number;
                number++;
            }

            // Add new ID to the set of used IDs
            usedIDs.add(id);
        }
        return id;
    }
}
