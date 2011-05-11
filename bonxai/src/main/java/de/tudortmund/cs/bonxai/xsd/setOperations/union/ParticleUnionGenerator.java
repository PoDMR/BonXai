package de.tudortmund.cs.bonxai.xsd.setOperations.union;

import de.tudortmund.cs.bonxai.common.AllPattern;
import de.tudortmund.cs.bonxai.common.Annotation;
import de.tudortmund.cs.bonxai.common.AnyPattern;
import de.tudortmund.cs.bonxai.common.ChoicePattern;
import de.tudortmund.cs.bonxai.common.CountingPattern;
import de.tudortmund.cs.bonxai.common.GroupRef;
import de.tudortmund.cs.bonxai.common.IdentifiedNamespace;
import de.tudortmund.cs.bonxai.common.Particle;
import de.tudortmund.cs.bonxai.common.ParticleContainer;
import de.tudortmund.cs.bonxai.common.ProcessContentsInstruction;
import de.tudortmund.cs.bonxai.common.SequencePattern;
import de.tudortmund.cs.bonxai.common.SymbolTableRef;
import de.tudortmund.cs.bonxai.xsd.*;
import de.tudortmund.cs.bonxai.xsd.Element.*;
import de.tudortmund.cs.bonxai.xsd.XSDSchema.BlockDefault;
import de.tudortmund.cs.bonxai.xsd.XSDSchema.FinalDefault;
import de.tudortmund.cs.bonxai.xsd.XSDSchema.Qualification;
import java.util.*;

/**
 * This class is used to unite Particles like the ones contained in the ComplexContent
 * of ComplexTypes. Another use is to create new global Elements or Groups for an
 * otputSchema, which represents the union of a set of schemata.
 * @author Dominik Wolff
 */
public class ParticleUnionGenerator {

    // XSDSchema which will contain the union of schemata contained in the schema group
    private XSDSchema outputSchema;

    // HashMap mapping namespaces to output schemata (Can be used if an element with a foreign namespace is referenced)
    private LinkedHashMap<String, XSDSchema> namespaceOutputSchemaMap;

    // HashMap mapping elements to old schemata used to construct the new output schema
    private LinkedHashMap<Element, XSDSchema> elementOldSchemaMap;

    // Map mapping any patterns to their old schemata
    private LinkedHashMap<AnyPattern, XSDSchema> anyPatternOldSchemaMap;

    // HashMap mapping namespaces to elements which are present in the minuend and subtrahend schema of the schema group with the corresponding namespace
    private LinkedHashMap<String, LinkedHashSet<Element>> namespaceConflictingElementsMap;

    // HashMap mapping top-level elements to references of their types
    private LinkedHashMap<String, SymbolTableRef<Type>> topLevelElementTypeMap;

    // HashMap which contains for every local elemente in the given context its type reference
    private LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap;

    // Set containing all IDs used in all new output schemata
    private LinkedHashSet<String> usedIDs;

    // Type union generator of the attribute particle union generator class
    private TypeUnionGenerator typeUnionGenerator;

    // Set of schemata not contained in a schema group
    private LinkedHashSet<XSDSchema> otherSchemata = new LinkedHashSet<XSDSchema>();

    // NamespaceAbbreviationMap maps namespaces to abbreviations
    private LinkedHashMap<String, String> namespaceAbbreviationMap;

    // Directory where the new output schemata will be stored (Default is ""c:\")
    private String workingDirectory = "c:/";

    // Set containing all key references used in the output schema
    private LinkedHashSet<KeyRef> usedKeyRefs;

    /**
     * Constructor of the ParticleUnionGenerator class. Getting parameters to
     * update contained fields.
     *
     * @param outputSchema XSDSchema which will contain the union of schemata
     * contained in the schema group.
     * @param namespaceOutputSchemaMap  HashMap mapping namespaces to output
     * schemata.
     * @param elementOldSchemaMap HashMap mapping elements to old schemata used
     * to construct the new output schema.
     * @param anyPatternOldSchemaMap Map mapping any patterns to their old
     * schemata.
     * @param namespaceConflictingElementsMap HashMap mapping namespaces to
     * elements which are present in the minuend and subtrahend schema of the
     * schema group with the corresponding namespace.
     * @param topLevelElementTypeMap HashMap mapping top-level elements to
     * references of their types.
     * @param usedIDs Set containing all IDs used in all new output schemata.
     * @param otherSchemata Set of schemata not contained in a schema group.
     * @param namespaceAbbreviationMap NamespaceAbbreviationMap maps namespaces
     * to abbreviations.
     * @param workingDirectory Directory where the new output schemata will be
     * stored.
     */
    public ParticleUnionGenerator(XSDSchema outputSchema, LinkedHashMap<String, XSDSchema> namespaceOutputSchemaMap, LinkedHashMap<Element, XSDSchema> elementOldSchemaMap, LinkedHashMap<AnyPattern, XSDSchema> anyPatternOldSchemaMap, LinkedHashMap<String, LinkedHashSet<Element>> namespaceConflictingElementsMap, LinkedHashMap<String, SymbolTableRef<Type>> topLevelElementTypeMap, LinkedHashSet<String> usedIDs, LinkedHashSet<XSDSchema> otherSchemata, LinkedHashMap<String, String> namespaceAbbreviationMap, String workingDirectory) {

        // Initialize class fields
        this.outputSchema = outputSchema;
        this.namespaceOutputSchemaMap = namespaceOutputSchemaMap;
        this.elementOldSchemaMap = elementOldSchemaMap;
        this.anyPatternOldSchemaMap = anyPatternOldSchemaMap;
        this.namespaceConflictingElementsMap = namespaceConflictingElementsMap;
        this.topLevelElementTypeMap = topLevelElementTypeMap;
        this.usedIDs = usedIDs;
        this.otherSchemata = otherSchemata;
        this.namespaceAbbreviationMap = namespaceAbbreviationMap;
        this.workingDirectory = workingDirectory;
        usedKeyRefs = new LinkedHashSet<KeyRef>();
    }

    /**
     * Set new type union generator for the particle generator.
     *
     * @param typeUnionGenerator Type union generator, which is used in the
     * particle generator.
     */
    public void setTypeUnionGeneratornion(TypeUnionGenerator typeUnionGenerator) {
        this.typeUnionGenerator = typeUnionGenerator;
    }


//    public void removeKeyReferenceWithoutKey() {
//
//        for (Iterator<KeyRef> it = usedKeyRefs.iterator(); it.hasNext();) {
//            KeyRef keyRef = it.next();
//
//            if (keyRef.getKeyOrUnique() == null) {
//
//
//                outputSchema.getConstraintNames().remove(keyRef.getName());
//
//            }
//
//        }
//    }
//    public LinkedList<SimpleConstraint> generateNewSimpleConstraints(LinkedHashSet<Element> elements, XSDSchema schema) {
//
//        LinkedList<SimpleConstraint> constraints = new LinkedList<SimpleConstraint>();
//
//        LinkedHashMap<String, LinkedList<Key>> selectorKeyMap = new LinkedHashMap<String, LinkedList<Key>>();
//        LinkedHashMap<String, LinkedList<KeyRef>> selectorKeyRefMap = new LinkedHashMap<String, LinkedList<KeyRef>>();
//        LinkedHashMap<String, LinkedList<Unique>> selectorUniqueMap = new LinkedHashMap<String, LinkedList<Unique>>();
//
//        for (Iterator<Element> it = elements.iterator(); it.hasNext();) {
//            Element element = it.next();
//
//            for (Iterator<Constraint> it2 = element.getConstraints().iterator(); it2.hasNext();) {
//                Constraint constraint = it2.next();
//
//                if (constraint instanceof Key) {
//                    Key key = (Key) constraint;
//
//                    if (!selectorKeyMap.containsKey(key.getSelector())) {
//                        selectorKeyMap.put(key.getSelector(), new LinkedList<Key>());
//                    }
//                    LinkedList<Key> keys = new LinkedList<Key>();
//                    keys.add((Key) constraint);
//                    selectorKeyMap.put(key.getSelector(), keys);
//                }
//
//                if (constraint instanceof KeyRef) {
//                    KeyRef keyRef = (KeyRef) constraint;
//
//                    if (!selectorKeyRefMap.containsKey(keyRef.getSelector())) {
//                        selectorKeyRefMap.put(keyRef.getSelector(), new LinkedList<KeyRef>());
//                    }
//                    LinkedList<KeyRef> keyRefs = new LinkedList<KeyRef>();
//                    keyRefs.add((KeyRef) constraint);
//                    selectorKeyRefMap.put(keyRef.getSelector(), keyRefs);
//                }
//
//                if (constraint instanceof Unique) {
//                    Unique unique = (Unique) constraint;
//
//                    if (!selectorUniqueMap.containsKey(unique.getSelector())) {
//                        selectorUniqueMap.put(unique.getSelector(), new LinkedList<Unique>());
//                    }
//                    LinkedList<Unique> uniques = new LinkedList<Unique>();
//                    uniques.add((Unique) constraint);
//                    selectorUniqueMap.put(unique.getSelector(), uniques);
//                }
//            }
//        }
//
//        for (Iterator<String> it = selectorKeyMap.keySet().iterator(); it.hasNext();) {
//            String selector = it.next();
//
//            if (selectorKeyMap.get(selector).size() == elements.size()) {
//                HashSet<String> fields = null;
//
//                String keyName = selectorKeyMap.get(selector).getFirst().getName();
//
//                for (Iterator<Key> it2 = selectorKeyMap.get(selector).iterator(); it2.hasNext();) {
//                    Key key = it2.next();
//
//
//                    if (fields == null) {
//                        fields = new HashSet<String>();
//                        fields.addAll(key.getFields());
//                    } else {
//
//                        for (Iterator<String> it3 = fields.iterator(); it3.hasNext();) {
//                            String field = it3.next();
//
//                            if (!key.getFields().contains(field)) {
//                                fields.remove(it);
//                            }
//                        }
//                    }
//                }
//
//                if (!fields.isEmpty()) {
//                    Key newKey = new Key(keyName, selector);
//
//                    for (Iterator<String> it2 = fields.iterator(); it2.hasNext();) {
//                        String field = it2.next();
//                        newKey.addField(field);
//                    }
//                    constraints.add(newKey);
//                }
//            }
//        }
//        return constraints;
//    }
    /**
     * Create new key by copying the specified old key. New key contains all
     * information of the old key, i.e. all fields of the old key are still
     * present. New key is registered in the constraint SymbolTable.
     *
     * @param oldKey Old key used to construct the new key.
     * @return New key, copy of the old key.
     */
//    private KeyRef generateNewKeyRef(KeyRef oldKeyRef, XSDSchema schema) {
//
//        // Check if key/unique is contained in the output schema or in a different output schema of another schema group
//        if (!oldKeyRef.getKeyOrUnique().getNamespace().equals(outputSchema.getTargetNamespace())) {
//
//            // Get the output schema in which the referenced key/unique will be contained
//            XSDSchema otherOutputSchema = namespaceOutputSchemaMap.get(oldKeyRef.getKeyOrUnique().getNamespace());
//
//            // Get SymbolTableRef in the other output schema
//            SymbolTableRef<SimpleConstraint> symbolTableRef = otherOutputSchema.getKeyAndUniqueSymbolTable().getReference(oldKeyRef.getKeyOrUnique().getName());
//
//            // Add this SymbolTableRef to the output schema (This is correct even when the key/unique is not present in the other output schema)
//            outputSchema.getKeyAndUniqueSymbolTable().setReference(symbolTableRef.getKey(), symbolTableRef);
//        }
//
//        // Create new key with new name, selector and reference
//        KeyRef newKeyRef = new KeyRef(oldKeyRef.getName(), oldKeyRef.getSelector(), schema.getKeyAndUniqueSymbolTable().getReference(oldKeyRef.getKeyOrUnique().getName()));
//
//        // Add for each field of the old key reference a new field to the new key reference
//        for (Iterator<String> it = oldKeyRef.getFields().iterator(); it.hasNext();) {
//            String oldField = it.next();
//
//            // Add new field to the new key
//            newKeyRef.addField(oldField);
//        }
//
//        // Set id and annotation for new key reference
//        newKeyRef.setAnnotation(generateNewAnnotation(oldKeyRef.getAnnotation()));
//        newKeyRef.setId(oldKeyRef.getId());
//
//        // Add new key reference name to list of constraint names
//        schema.addConstraintName(newKeyRef.getName());
//
//        // Return new key reference
//        return newKeyRef;
//    }
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
                Particle newElement = generateNewElement(oldElementRef.getElement(), elementTypeMap.get(getInstanceName(oldElementRef.getElement())));

                // Get new set of particle, which currently will only contain the element reference
                LinkedHashSet<Particle> oldParticles = new LinkedHashSet<Particle>();
                oldParticles.add(oldElementRef);

                // If the new element is an element and not group reference add referenced element to the particle set
                if (newElement instanceof Element) {
                    oldParticles.add(oldElementRef.getElement());
                }

                // Use particle set to create new "id" attribute and annotation
                newElement.setAnnotation(getAnnotation(oldParticles));
                newElement.setId(getID(oldParticles));
                return newElement;
            }
        } else {
            return null;
        }
    }

    /**
     * This method generates a new particle for a given any pattern.
     *
     * @param anyPattern Given any pattern, which is converted into a new
     * any pattern or choice.
     * @return Choice or any pattern, which represents the specified any
     * pattern.
     */
    private Particle generateNewAnyPattern(AnyPattern anyPattern) {

        // Check if any pattern contains a "processContents" attribute
        if (anyPattern.getProcessContentsInstruction().equals(ProcessContentsInstruction.Strict)) {

            // Create new choice pattern to contain the referred elements
            ChoicePattern choicePattern = new ChoicePattern();

            // Add each referred element to the choice pattern
            for (Iterator<Element> it = getContainedElements(anyPattern).iterator(); it.hasNext();) {
                Element element = it.next();
                choicePattern.addParticle(generateNewElement(element, elementTypeMap.get(getInstanceName(element))));
            }
            if (choicePattern.getParticles().isEmpty()) {
                return null;
            }
            return choicePattern;
        } else {

            // Create new any pattern with new "ID" attribute and annotation
            AnyPattern newAnyPattern = new AnyPattern(ProcessContentsInstruction.Skip, getNamespace(anyPattern));
            newAnyPattern.setId(getID(anyPattern.getId()));
            newAnyPattern.setAnnotation(generateNewAnnotation(anyPattern.getAnnotation()));
            return newAnyPattern;
        }
    }

    /**
     * This method constructs the "namespace" attribute of an any pattern.
     *
     * @param oldAnyPattern An any pattern, which "namespace" attribute is used
     * to create the new "namespace" attribute.
     * @return String representing the new "namespace" attribute or null.
     */
    private String getNamespace(AnyPattern oldAnyPattern) {

        // Initialize namespace set, which will contain found namespaces
        LinkedHashSet<String> namespaces = new LinkedHashSet<String>();
        String namespaceValue = oldAnyPattern.getNamespace();

        // Check if the "namespace" attribute is present and if the resulting "namespace" attribute is already known
        if (namespaceValue != null && !(namespaces.contains("##any") || (namespaces.contains("##other") && (namespaces.contains("##local") || namespaces.contains("##targetNamespace"))))) {

            // Check if "##any" value is contained and add it to the set
            if (namespaceValue.contains("##any")) {
                namespaces.add("##any");
            }
            // Check if "##other" value is contained in an any pattern with same namespace as the current ouput schema and add "##other" to the set
            if (namespaceValue.contains("##other") && anyPatternOldSchemaMap.get(oldAnyPattern).getTargetNamespace().equals(outputSchema.getTargetNamespace())) {
                namespaces.add("##other");
            }
            // Check if "##other" value is contained in an any pattern with a different namespace as the current ouput schema and add "##any" to the set
            if (namespaceValue.contains("##other") && !anyPatternOldSchemaMap.get(oldAnyPattern).getTargetNamespace().equals(outputSchema.getTargetNamespace())) {
                namespaces.add("##any");
            }
            // Check if "##local" value is contained and add it to the set
            if (namespaceValue.contains("##local")) {
                namespaces.add("##local");
            }
            // Check if "##targetNamespace" value is contained in an any pattern with same namespace as the current ouput schema or a namespace equal to the target namespace and add it to the set
            if (namespaceValue.contains("##targetNamespace") && anyPatternOldSchemaMap.get(oldAnyPattern).getTargetNamespace().equals(outputSchema.getTargetNamespace()) || (namespaceValue.contains(anyPatternOldSchemaMap.get(oldAnyPattern).getTargetNamespace()) && !anyPatternOldSchemaMap.get(oldAnyPattern).getTargetNamespace().equals(""))) {
                namespaces.add("##targetNamespace");
            }
            // Check if "##targetNamespace" value is contained in an any pattern with a different namespace as the current ouput schema
            if (namespaceValue.contains("##targetNamespace") && !anyPatternOldSchemaMap.get(oldAnyPattern).getTargetNamespace().equals(outputSchema.getTargetNamespace())) {

                // For the empty namespace add "##local"
                if (anyPatternOldSchemaMap.get(oldAnyPattern).getTargetNamespace().equals("")) {
                    namespaces.add("##local");
                } else {
                    namespaces.add(anyPatternOldSchemaMap.get(oldAnyPattern).getTargetNamespace());
                }
            }
            // If "namespace" attribute contains no "##any" or "##other" value a list of namespaces could be contained
            if (!namespaceValue.contains("##any") && !namespaceValue.contains("##other")) {
                String[] namespaceValueArray = namespaceValue.split(" ");

                // Check for each namespace if it is not the target namespace or any of the already checked namespaces and add it to the set
                for (String namespace : namespaceValueArray) {
                    if (!namespace.equals(anyPatternOldSchemaMap.get(oldAnyPattern).getTargetNamespace()) && !namespaces.contains(namespace) && !namespace.equals("##local") && !namespace.equals("##targetNamespace")) {
                        namespaces.add(namespace);
                    }
                }
            }
        } else {

            // If no namespace is present the default value is "##any"
            namespaces.add("##any");
        }

        // Get new "namespace" attribute
        String newNamespace = "";

        // Check if set contains "##any" value or "##other" value with "##local" value or "##targetNamespace" value if it does the "namespace" attribute contains only an "##any" value
        if (namespaces.contains("##any") || (namespaces.contains("##other") && (namespaces.contains("##local") || namespaces.contains("##targetNamespace")))) {
            newNamespace = "##any";
        } else {

            // Check if set contains "##other" value and no "##any", "##local", "##targetNamespace"  values if it does the "namespace" attribute contains only a "##other" value
            if (namespaces.contains("##other")) {
                newNamespace = "##other";
            } else {

                // Add the other namespaces contained in the set to the new "namespace" attribute
                for (Iterator<String> it = namespaces.iterator(); it.hasNext();) {
                    String namespace = it.next();

                    // Check if whitespace is needed
                    if (!newNamespace.endsWith(" ") && newNamespace.length() >= 1) {
                        newNamespace += " ";
                    }

                    // Add namespace to "namespace" attribute
                    newNamespace += namespace;
                }
            }
        }

        // If no new "namespace" attribute was construted return null else return the new "namespace" attribute
        if (newNamespace.equals("")) {
            return null;
        } else {
            return newNamespace;
        }
    }

    /**
     * Get elements contained in the specified any pattern, if the any pattern
     * is processed strictly.
     *
     * @param anyPattern Any pattern which specifies the set of elements.
     * @return A set containing all elements contained in the specified any
     * pattern.
     */
    public LinkedHashSet<Element> getContainedElements(AnyPattern anyPattern) {

        // Check if the any pattern is processed strictly
        if (anyPattern.getProcessContentsInstruction().equals(ProcessContentsInstruction.Strict)) {

            // Initalize set to store all top-level elements
            LinkedHashSet<Element> topLevelElements = new LinkedHashSet<Element>();

            // If any pattern namespace attribute contains "##any"
            if (anyPattern.getNamespace().contains("##any")) {

                // Add all elements contained in the schema to the set
                topLevelElements.addAll(anyPatternOldSchemaMap.get(anyPattern).getElements());

                // Add all elements contained in foreign schemata to the set
                for (Iterator<ForeignSchema> it = anyPatternOldSchemaMap.get(anyPattern).getForeignSchemas().iterator(); it.hasNext();) {
                    ForeignSchema foreignSchema = it.next();
                    topLevelElements.addAll(foreignSchema.getSchema().getElements());
                }
                return topLevelElements;

            } else if (anyPattern.getNamespace().contains("##other")) {

                // If any pattern namespace attribute contains "##other" only add elements contained in foreign schemata to the set
                for (Iterator<ForeignSchema> it = anyPatternOldSchemaMap.get(anyPattern).getForeignSchemas().iterator(); it.hasNext();) {
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
                        if (anyPatternOldSchemaMap.get(anyPattern).getTargetNamespace().equals("")) {

                            // Add all elements contained in the schema to the set
                            topLevelElements.addAll(anyPatternOldSchemaMap.get(anyPattern).getElements());
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
                    } else if (currentNamespace.contains("##targetNamespace") || currentNamespace.equals(outputSchema.getTargetNamespace())) {

                        // Add all elements contained in the schema to the set
                        topLevelElements.addAll(anyPatternOldSchemaMap.get(anyPattern).getElements());

                    } else {

                        // Find foreign schema with the specified namespace
                        for (Iterator<ForeignSchema> it = anyPatternOldSchemaMap.get(anyPattern).getForeignSchemas().iterator(); it.hasNext();) {
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
            return null;
        }
    }

    /**
     * Method that gets the instance name of an element.
     * 
     * @param element Element, which instance name is requested.
     * @return Instance name of the element.
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
     * Creates a union of given particles, the result of this union is a new
     * particle. This new particle respresents all specified particles and
     * is valid for the output schema.
     *
     * @param particles Set of particles used to generate a new particle.
     * @param elementTypeMap Map mapping local element names to types.
     * @return New particle, which represents the particle set.
     */
    public Particle generateParticleUnion(LinkedHashSet<Particle> particles, LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap) {

        // Load current element to type reference map
        this.elementTypeMap = elementTypeMap;

        // If only one particle is contained in the particle set
        if (particles.size() == 1) {
            return generateNewParticle(particles.iterator().next(), false);
        } else {

            // Generate new choice pattern, which will contain the different particles
            ChoicePattern choicePattern = new ChoicePattern();

            // Add for each particle contained in the particle set a new particle to the choice pattern
            for (Iterator<Particle> it = particles.iterator(); it.hasNext();) {
                Particle particle = it.next();
                choicePattern.addParticle(generateNewParticle(particle, true));
            }

            // Check if choice is empty
            if (choicePattern.getParticles().isEmpty()) {
                return null;
            }

            // Return new choice pattern
            return choicePattern;
        }
    }

    /**
     * This method resolves a given all pattern into a choice pattern, which
     * contains all element permutations.
     *
     * @param allPattern All pattern which should be resolved.
     * @return New choice pattern representing the specified all pattern.
     */
    private ChoicePattern resolveAllPattern(AllPattern allPattern) {

        // Create new choice pattern
        ChoicePattern choicePattern = new ChoicePattern();

        // Get particles contained in the current all pattern      
        Particle[] containedParticle = new Particle[allPattern.getParticles().size()];
        for (int i = 0; i < containedParticle.length; i++) {
            Particle particle = allPattern.getParticles().get(i);
            containedParticle[i] = particle;
        }

        // Add all possible particle sequences to the choice pattern
        choicePattern.setParticles(permutateAllPattern(allPattern.getParticles().size() - 1, containedParticle));

        // Set new id and annotation and return the new choice pattern
        choicePattern.setId(getID(allPattern.getId()));
        choicePattern.setAnnotation(allPattern.getAnnotation());
        return choicePattern;
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
                sequencePattern.addParticle(generateNewParticle(particle, true));
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
     * Create new particle (AnyPattern, Element, ElementRef, GroupRef,
     * ParticleContainer) by copying the old particle. All information stored
     * in the old particle is present in the new particle.
     *
     * @param oldParticle Particle which is used to created the new particle, so
     * that the new particle is a copy of the old particle.
     * @param resolveAllPattern Boolean which is <tt>true</tt> if the contained
     * should be resolved.
     * @return New particle, copy the old particle.
     */
    public Particle generateNewParticle(Particle oldParticle, boolean resolveAllPattern) {

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
            newParticle = generateNewElement(oldElement, elementTypeMap.get(getInstanceName(oldElement)));

        // If old particle is a ElementRef create new ElementRef.
        } else if (oldParticle instanceof ElementRef) {

            // Create new ElementRef with the createNewElementRef method
            ElementRef oldElementRef = (ElementRef) oldParticle;
            newParticle = generateNewElementRef(oldElementRef);

        // If old particle is a GroupRef create new GroupRef.
        } else if (oldParticle instanceof GroupRef) {

            // Create new GroupRef with the createNewGroupRef method
            GroupRef oldGroupRef = (GroupRef) oldParticle;
            newParticle = generateNewGroupRef(oldGroupRef, resolveAllPattern);

        // If old particle is a ParticleContainer create new ParticleContainer.
        } else if (oldParticle instanceof ParticleContainer) {

            // Create new ParticleContainer with the createNewParticleContainer method
            ParticleContainer oldParticleContainer = (ParticleContainer) oldParticle;
            newParticle = generateNewParticleContainer(oldParticleContainer, resolveAllPattern);
        }

        // Return new particle after using other create-methods to create a copie of the old particle
        return newParticle;
    }

    /**
     * This method creates a new top-level element. This element will be a copie
     * of the specified old element contained in the given old schema.
     * Furthermore the new element will be registered in the SymbolTable of the
     * output schema and in the list of top-level elements.
     *
     * Only top-level elements contain "final" and "abstract" values.
     *
     * @param topLevelElement Element which is the blueprint for the new
     * element.
     */
    private void generateNewTopLevelElement(Element topLevelElement) {

        // Create new element
        Element newElement = (Element) generateNewElement(topLevelElement, elementTypeMap.get(getInstanceName(topLevelElement)));

        // Set "abstract" value for new top-level element
        newElement.setAbstract(topLevelElement.getAbstract());
        newElement.setForm(null);

        // Check if "final" attribute is present in old top-level element
        if (topLevelElement.getFinalModifiers() != null) {

            // "final" attribute is present add "final" value to new element
            newElement.setFinalModifiers(topLevelElement.getFinalModifiers());
        }

        // Add new element to the list of top-level elements
        if (!outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference(newElement.getName()))) {
            outputSchema.addElement(outputSchema.getElementSymbolTable().getReference(newElement.getName()));
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
//    private Constraint generateNewConstraint(Constraint oldConstraint, XSDSchema schema) {
//
//        // Create new constraint
//        Constraint newConstraint = null;
//
//        // If old constraint is an Key create new Key.
//        if (oldConstraint instanceof Key) {
//
//            // Create new Key with the createNewKey method
//            Key oldKey = (Key) oldConstraint;
//            newConstraint = generateNewKey(oldKey, schema);
//
//        // If old constraint is an KeyRef create new KeyRef.
//        } else if (oldConstraint instanceof KeyRef) {
//
//            // Create new KeyRef with the createNewKeyRef method
//            KeyRef oldKeyRef = (KeyRef) oldConstraint;
//            newConstraint = generateNewKeyRef(oldKeyRef, schema);
//
//        // If old constraint is a Unique create new Unique.
//        } else if (oldConstraint instanceof Unique) {
//
//            // Create new Unique with the createNewUnique method
//            Unique oldUnique = (Unique) oldConstraint;
//            newConstraint = generateNewUnique(oldUnique, schema);
//        }
//
//        // Return new constraint after using other create-methods to create a copie of the old constraint
//        return newConstraint;
//    }
    /**
     * Create new key by copying the specified old key. New key contains all
     * information of the old key, i.e. all fields of the old key are still
     * present. New key is registered in the constraint SymbolTable.
     *
     * @param oldKey Old key used to construct the new key.
     * @return New key, copy of the old key.
     */
//    private Key generateNewKey(Key oldKey, XSDSchema schema) {
//
//        // Create new key with new name and selector
//        Key newKey = new Key(oldKey.getName(), oldKey.getSelector());
//
//        // Add for each field of the old key a new field to the new key
//        for (Iterator<String> it = oldKey.getFields().iterator(); it.hasNext();) {
//            String oldField = it.next();
//
//            // Add new field to the new key
//            newKey.addField(oldField);
//        }
//
//        // Set id and annotation for new key
//        newKey.setAnnotation(generateNewAnnotation(oldKey.getAnnotation()));
//        newKey.setId(oldKey.getId());
//
//        // Add new key to SymbolTabel and new key name to list of constraint names
//        schema.getKeyAndUniqueSymbolTable().updateOrCreateReference(newKey.getName(), newKey);
//        schema.addConstraintName(newKey.getName());
//
//        // Return new key
//        return newKey;
//    }
    /**
     * Create new unique by copying the specified old unique. New unique
     * contains all information of the old unique, i.e. all fields of the
     * old unique are still present. New unique is registered in the constraint
     * SymbolTable.
     *
     * @param oldUnique Old unique used to construct the new unique.
     * @return New unique, copy of the old unique.
     */
//    private Unique generateNewUnique(Unique oldUnique, XSDSchema schema) {
//
//        // Create new unique with new name and selector
//        Unique newUnique = new Unique(oldUnique.getName(), oldUnique.getSelector());
//
//        // Add for each field of the old unique a new field to the new unique
//        for (Iterator<String> it = oldUnique.getFields().iterator(); it.hasNext();) {
//            String oldField = it.next();
//
//            // Add new field to the new unique
//            newUnique.addField(oldField);
//        }
//
//        // Set id and annotation for new unique
//        newUnique.setAnnotation(generateNewAnnotation(oldUnique.getAnnotation()));
//        newUnique.setId(oldUnique.getId());
//
//        // Add new unique to SymbolTabel and new unique name to list of constraint names
//        schema.getKeyAndUniqueSymbolTable().updateOrCreateReference(newUnique.getName(), newUnique);
//        schema.addConstraintName(newUnique.getName());
//
//        // Return new unique
//        return newUnique;
//    }
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
            groupElementTypeMap.put(getInstanceName(element), outputSchema.getTypeSymbolTable().getReference(element.getType().getName()));

        } else if (particle instanceof ElementRef) {

            // If particle is an element reference add new entry to the groupElementTypeMap for the referred element
            Element element = ((ElementRef) particle).getElement();
            groupElementTypeMap.put(getInstanceName(element), outputSchema.getTypeSymbolTable().getReference(element.getType().getName()));
        }

        // Return fully updated groupElementTypeMap
        return groupElementTypeMap;
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
        } else if (particle instanceof GroupRef) {

            // If particle is a group reference check referred group for elements
            Group group = (Group) ((GroupRef) particle).getGroup();
            containedElements.addAll(getContainedElements(group.getParticleContainer()));

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
     * Create new group reference for a given old group reference. The new group
     * reference can be a complete copy of the old group reference. If the reference
     * links to a group in another schema this element will be referred to by
     * the new group reference as well. If the type of an element contained in
     * the group is changed due to the type automaton the group is resolved.
     *
     * @param oldGroupRef Old group reference which is used to construct the new
     * group reference.
     * @param resolveAllPattern Boolean which is <tt>true</tt> if the contained
     * should be resolved.
     * @return New group reference linking to the correct group or a particle
     * container if the group is resolved.
     */
    private Particle generateNewGroupRef(GroupRef oldGroupRef, boolean resolveAllPattern) {

        // Get new map mapping element names to type references for all elements contained in the group reference
        HashMap<String, SymbolTableRef<Type>> groupElementTypeMap = getGroupElementTypeMap(oldGroupRef);

        // For all element names check if the type specified in the particle map is equivalent to the type of the group map
        for (Iterator<String> it = groupElementTypeMap.keySet().iterator(); it.hasNext();) {
            String elementName = it.next();

            // Check if the element name is contained in the elementTypeMap and if both entries are not the same
            if (elementTypeMap.containsKey(elementName) && elementTypeMap.get(elementName) != groupElementTypeMap.get(elementName) || getContainedElements(oldGroupRef.getGroup().getParticleContainer()).size() == 1) {
                ParticleContainer particleContainer = oldGroupRef.getGroup().getParticleContainer();

                // Return contained particle container instead of the group
                return generateNewParticleContainer(particleContainer, resolveAllPattern);
            }
        }

        // Generate new top-level group
        Group newGroup = generateNewTopLevelGroup((Group) oldGroupRef.getGroup(), resolveAllPattern);

        // Create new group reference with correct reference stored in the output schema
        GroupRef newGroupRef = new GroupRef(outputSchema.getGroupSymbolTable().getReference(newGroup.getName()));

        // Set new id and annotation for new group reference
        newGroupRef.setAnnotation(generateNewAnnotation(oldGroupRef.getAnnotation()));
        newGroupRef.setId(getID(oldGroupRef.getId()));

        // Return new group reference
        return newGroupRef;
    }

    /**
     * Create new element as a copy of the specified old element. All
     * informations contained in the old element are transfered to the new
     * element. The new element contains a new type, new constraints and so on.
     *
     * @param oldElement Element used to create the new element as an exact copy
     * of the old element in the new schema.
     * @param typeSymbolTableRef SymbolTable reference which contains the new
     * type of the new element.
     * @return New element in the new schema created from the specified old
     * element or a group reference to a group containing the new element.
     */
    private Particle generateNewElement(Element oldElement, SymbolTableRef<Type> typeSymbolTableRef) {

        // Initialize new element name
        Element newElement = null;

        // If element is qualified the namespace is important
        if ((oldElement.getForm() == null || oldElement.getForm() != XSDSchema.Qualification.qualified) && !oldElement.getNamespace().equals(outputSchema.getTargetNamespace())) {

            // Create new elment with same local name but different namespace as the old element but new type
            newElement = new Element("{" + outputSchema.getTargetNamespace() + "}" + oldElement.getLocalName());
        } else {

            // Create new elment with same name as the old element but new type
            newElement = new Element(oldElement.getName());
        }

        // Set new type to the type found in the type SymbolTable of the schema and check if "type" attribute is needed
        newElement.setType(typeSymbolTableRef);

        // Build-in datatypes are not anonymous
        if (typeUnionGenerator.isBuiltInDatatype(typeSymbolTableRef.getKey())) {
            newElement.setTypeAttr(false);
        } else {
            newElement.setTypeAttr(!typeSymbolTableRef.getReference().isAnonymous());
        }

        // Set "nillable" value of the new element
        if (oldElement.isNillable()) {
            newElement.setNillable();
        }

        // Set id and annotation for the new element
        newElement.setAnnotation(generateNewAnnotation(oldElement.getAnnotation()));
        newElement.setId(getID(oldElement.getId()));

        // Set "fixed" and "default" values of the new element
        newElement.setDefault(oldElement.getDefault());
        newElement.setFixed(oldElement.getFixed());

        // Check if "block" attribute is present in old element
        if (oldElement.getBlockModifiers() == null) {

            // Add "blockDefault" attribute values to new element (resolving "blockDefault" indirectly)
            if (elementOldSchemaMap.get(oldElement).getBlockDefaults().contains(BlockDefault.extension)) {
                newElement.addBlockModifier(Block.extension);
            }
            if (elementOldSchemaMap.get(oldElement).getBlockDefaults().contains(BlockDefault.restriction)) {
                newElement.addBlockModifier(Block.restriction);
            }
            if (elementOldSchemaMap.get(oldElement).getBlockDefaults().contains(BlockDefault.substitution)) {
                newElement.addBlockModifier(Block.substitution);
            }
        } else {

            // "block" attribute is present add "block" value to new element
            newElement.setBlockModifiers(oldElement.getBlockModifiers());
        }

        // Check if "form" attribute is present in old element
        if (oldElement.getForm() != null) {

            // "form" attribute is present add "form" value to new element
            newElement.setForm(oldElement.getForm());
        }

        // Check again if the element was qualified and if add new group to an other schema
        if (oldElement.getForm() != null && oldElement.getForm() == XSDSchema.Qualification.qualified && !oldElement.getNamespace().equals(outputSchema.getTargetNamespace())) {

            // Get other schema to store element group in
            XSDSchema otherSchema = new XSDSchema(oldElement.getNamespace());
            otherSchema.getNamespaceList().addIdentifiedNamespace(new IdentifiedNamespace("xs", "http://www.w3.org/2001/XMLSchema"));
            otherSchema.setSchemaLocation(workingDirectory + "OtherSchema(" + oldElement.getNamespace() + ").xsd");

            // Check if schema exist in schema groups
            if (namespaceOutputSchemaMap.get(oldElement.getNamespace()) != null) {
                otherSchema = namespaceOutputSchemaMap.get(oldElement.getNamespace());
            } else {

                // Check if other schema is present in other schema list
                for (Iterator<XSDSchema> it = otherSchemata.iterator(); it.hasNext();) {
                    XSDSchema currentOtherSchema = it.next();

                    // If other schema has same namespace as the element use this schema
                    if (otherSchema.getTargetNamespace().equals(oldElement.getNamespace())) {
                        otherSchema = currentOtherSchema;
                    }
                }

                // Add other schema to schema set
                otherSchemata.add(otherSchema);
            }

            // Get new particle container, which contains the element
            ChoicePattern choicePattern = new ChoicePattern();
            choicePattern.addParticle(newElement);

            // Build new group name and avoid conflicts
            String newGroupNameBase = "{" + otherSchema.getTargetNamespace() + "}" + "element.group." + newElement.getLocalName();
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
            return new GroupRef(outputSchema.getGroupSymbolTable().getReference(newGroupName));
        }

        // Add for each constraint of the old element a new constraint
//        for (Iterator<Constraint> it = oldElement.getConstraints().iterator(); it.hasNext();) {
//            Constraint oldConstraint = it.next();
//
//            // Add new constraint to the new element
//            newElement.addConstraint(generateNewConstraint(oldConstraint, outputSchema));
//        }

        // Return new complete element
        return newElement;
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
     * This method creates a new top-level group. This group will be a copie of
     * the specified old group contained in the given old schema. Furthermore
     * the new group will be registered in the SymbolTable of the new schema and
     * in the list of top-level groups.
     *
     * @param topLevelGroup Group which is the blueprint for the new group.
     * @param resolveAllPattern Boolean which is <tt>true</tt> if the contained
     * should be resolved.
     * @return A SymbolTableRef to the new group contained in the new schema.
     */
    private Group generateNewTopLevelGroup(Group topLevelGroup, boolean resolveAllPattern) {

        // Check if group exists already
        if (!outputSchema.getGroupSymbolTable().hasReference(topLevelGroup.getName())) {

            // Create new particle container for the new group by copying the old particle container
            ParticleContainer newContainer = generateNewParticleContainer(topLevelGroup.getParticleContainer(), resolveAllPattern);

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
        } else {
            return outputSchema.getGroupSymbolTable().getReference(topLevelGroup.getName()).getReference();
        }
    }

    /**
     * This method generates a new "form" attribute from the "form" attributes
     * of given elements.
     *
     * @param elements Element set which is used to generate a new "form"
     * attribute.
     * @return New "form" attribute build from various "form" attributes.
     */
    private XSDSchema.Qualification getForm(LinkedHashSet<Element> elements) {

        // Check all elemens contained in the element set (only elements can contain "form" attributes)
        for (Iterator<Element> it = elements.iterator(); it.hasNext();) {
            Element element = it.next();

            // If current "form" attribute has a value, which is qualified, the new "form" attribute has the same value
            if (element.getForm() != null || element.getForm() == XSDSchema.Qualification.qualified) {
                return XSDSchema.Qualification.qualified;
            }
        }

        // The default value for the "form" attribute is unqualified
        return null;
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
    private ParticleContainer generateNewParticleContainer(ParticleContainer oldParticleContainer, boolean resolveAllPattern) {

        // Create new particle container
        ParticleContainer newParticleContainer = null;

        // If old particle container is an AllPattern create new AllPattern.
        if (oldParticleContainer instanceof AllPattern) {

            // If new all pattern should be resolved
            if (resolveAllPattern) {
                newParticleContainer = resolveAllPattern((AllPattern) oldParticleContainer);
            } else {
                newParticleContainer = new AllPattern();
            }

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
            newParticleContainer.addParticle(generateNewParticle(oldParticle, resolveAllPattern));
        }

        // Return new created particle container after it is complete
        return newParticleContainer;
    }

    /**
     * This method determines if a type is anonymous or not. If the type is a
     * top-level type <tt>true</tt> is returned.
     *
     * @param simpleType Type which is either anonymous or not.
     * @return  <tt>true</tt> if the type is anonymous else <tt>false</tt>.
     */
    private boolean getTypeAttribute(Type type) {

        // Check if the type is not null and not anonymous or a build-in datatype
        if (type != null && !type.isAnonymous() || typeUnionGenerator.isBuiltInDatatype(type.getName())) {
            return true;
        }
        return false;
    }

    /**
     * Constructs a new top-level element for a given set of elements and a type
     * reference, which referrers to a type for the new top-level element. All
     * attributes are constucted as result of a union of attributes contained in
     * the elements of the set.
     *
     * @param topLevelElements Set of top-level elements used to construct a new
     * top-level element for the new output schema.
     * @param typeRef Reference to the type of the new top-level element.
     * @return New top-level Element, generate by computing the union of the
     * specified elements.
     */
    @SuppressWarnings("unchecked")
	public Element generateNewTopLevelElement(LinkedHashSet<Element> topLevelElements, SymbolTableRef<Type> typeRef) {

        // Create new elment with same name as the old element but new type
        Element newElement = new Element(topLevelElements.iterator().next().getName(), typeRef);
        newElement.setTypeAttr(getTypeAttribute(typeRef.getReference()));

        // Set new "abstract" and "nillable" attribute for the new element
        newElement.setAbstract(getAbstract(topLevelElements));
        if (getNillable(topLevelElements)) {
            newElement.setNillable();
        }

        // Set new "block" and "final" attribute for the new element
        newElement.setBlockModifiers(getBlock(topLevelElements));
        newElement.setFinalModifiers(getFinal(topLevelElements));
        newElement.setForm(null);

        // Set new annotation and "id" attribute for the new element
        newElement.setAnnotation(getAnnotation(new LinkedHashSet<Particle>(topLevelElements)));
        newElement.setId(getID(new LinkedHashSet<Particle>(topLevelElements)));

        // Get new "fixed" attribute, this can result in a new type
        Object object = getFixed(topLevelElements, typeRef);
        if (object instanceof String) {

            // If result is a String, set new "fixed" value
            newElement.setFixed((String) object);
        } else if (object instanceof SymbolTableRef) {

            // If result is a reference to a type, set new anonymous type
            newElement.setType((SymbolTableRef<Type>) object);
            newElement.setTypeAttr(false);
        }

        // Set new "default" attribute for the new element, if "fixed" is not present
        if (newElement.getFixed() == null) {
            newElement.setDefault(getDefault(topLevelElements));
        }

//        LinkedList<SimpleConstraint> simpleConstraints = generateNewSimpleConstraints(topLevelElements, outputSchema);

//        for (Iterator<SimpleConstraint> it = simpleConstraints.iterator(); it.hasNext();) {
//            SimpleConstraint simpleConstraint = it.next();
//            newElement.addConstraint(simpleConstraint);
//        }

        // Add new element to element SymbolTable of the output schema
        outputSchema.getElementSymbolTable().updateOrCreateReference(newElement.getName(), newElement);

        // Add new element to the list of top-level elements
        if (!outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference(newElement.getName()))) {
            outputSchema.addElement(outputSchema.getElementSymbolTable().getReference(newElement.getName()));
        }
        return newElement;
    }

    /**
     * This method checks if all specified elements are abstract. If this is the
     * case the element generated for these elements is abstract as well, else
     * the new element is not abstract.
     *
     * @param elements Set of elements, which is used to generate a new element.
     * @return <tt>true</tt> if all specified elements are abstract, else
     * <tt>false</tt>.
     */
    private boolean getAbstract(LinkedHashSet<Element> elements) {
        boolean allAbstract = true;

        // For all elements contained in the given element set check if elements are abstract
        for (Element element : elements) {

            // Check if element is not abstract
            if (!element.getAbstract()) {
                allAbstract = false;
            }
        }
        return allAbstract;
    }

    /**
     * This method creates a new "block" attribute for a given set of elements.
     * The new "block" attribute is different than the default attribute of the
     * output schema and contains only Block values contained in all specified
     * "block" attribute.
     *
     * @param elements Set of elements, each may contain a "block" attribute.
     * @return Set of Block values or null if the set is equivalent to the set
     * contained by the output schema.
     */
    private HashSet<Block> getBlock(LinkedHashSet<Element> elements) {

        // Generate new "block" attribute, which contains every Block value
        HashSet<Block> block = new HashSet<Block>();
        block.add(Block.extension);
        block.add(Block.restriction);
        block.add(Block.substitution);

        // Check for each element the "block" attribute
        for (Element element : elements) {

            // If "block" attribute is present remove not contained Block value from the "block" attribute of the new element
            if (element.getBlockModifiers() != null) {

                // Check if "extension" is not contained and if remove "extension" from "block" attribute of the new element
                if (!element.getBlockModifiers().contains(Block.extension)) {
                    block.remove(Block.extension);
                }
                // Check if "restriction" is not contained and if remove "restriction" from "block" attribute of the new element
                if (!element.getBlockModifiers().contains(Block.restriction)) {
                    block.remove(Block.restriction);
                }
                // Check if "substitution" is not contained and if remove "substitution" from "block" attribute of the new element
                if (!element.getBlockModifiers().contains(Block.substitution)) {
                    block.remove(Block.substitution);
                }
            } else if (elementOldSchemaMap.get(element).getBlockDefaults() != null) {

                // Remove Block values not contained in the default value
                if (!elementOldSchemaMap.get(element).getBlockDefaults().contains(BlockDefault.extension)) {
                    block.remove(Block.extension);
                }
                if (!elementOldSchemaMap.get(element).getBlockDefaults().contains(BlockDefault.restriction)) {
                    block.remove(Block.restriction);
                }
                if (!elementOldSchemaMap.get(element).getBlockDefaults().contains(BlockDefault.substitution)) {
                    block.remove(Block.substitution);
                }
            } else {

                // Remove every Block value from the "block" attribute of the new element
                block.clear();
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
     * This method creates a new "final" attribute for a given set of elements.
     * The new "final" attribute is different than the default attribute of the
     * output schema and contains only Final values contained in all specified
     * "final" attribute.
     *
     * @param elements Set of elements, each may contain a "final" attribute.
     * @return Set of Final values or null if the set is equivalent to the set
     * contained by the output schema.
     */
    private HashSet<Final> getFinal(LinkedHashSet<Element> elements) {

        // Generate new "final" attribute, which contains every Final value
        HashSet<Final> finalValue = new HashSet<Final>();
        finalValue.add(Final.extension);
        finalValue.add(Final.restriction);

        // Check for each element the "final" attribute
        for (Element element : elements) {

            // If "final" attribute is present remove not contained Final value from the "final" attribute of the new element
            if (element.getFinalModifiers() != null) {

                // Check if "extension" is not contained and if remove "extension" from "final" attribute of the new element
                if (!element.getFinalModifiers().contains(Final.extension)) {
                    finalValue.remove(Final.extension);
                }
                // Check if "restriction" is not contained and if remove "restriction" from "final" attribute of the new element
                if (!element.getFinalModifiers().contains(Final.restriction)) {
                    finalValue.remove(Final.restriction);
                }
            } else if (elementOldSchemaMap.get(element).getFinalDefaults() != null) {

                // Remove Final values not contained in the default value
                if (!outputSchema.getFinalDefaults().contains(FinalDefault.extension)) {
                    finalValue.remove(Final.extension);
                }
                if (!outputSchema.getFinalDefaults().contains(FinalDefault.restriction)) {
                    finalValue.remove(Final.restriction);
                }
            } else {

                // Remove every Final value from the "final" attribute of the new element
                finalValue.clear();
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
     * This method checks if one of the specified elements is nillable. If this
     * is the case the element generated for these elements is nillable as well,
     * else the new element is not nillable.
     *
     * @param elements Set of elements, which is used to generate a new element.
     * @return <tt>true</tt> if a single specified elements is nillable, else
     * <tt>false</tt>.
     */
    private boolean getNillable(LinkedHashSet<Element> elements) {

        // Check if the element set contains an element with "nillable" attribute
        for (Element element : elements) {

            // If a single element is nillable the new element is nillable as well
            if (element.isNillable()) {
                return true;
            }
        }
        return false;
    }

    /**
     * This method creates a new annotation for a given set of particles. Each
     * particle may contain an annotation, these annotations are used to
     * contstruct the new annotation, which contains the app infos and
     * documentations of the old annotations.
     *
     * @param particles Set of particles, which is used to construct a new
     * particle. This particle will contain the new annotation.
     * @return New Annotation, which contains the information stored in the
     * old annotations contained in the specified particles.
     */
    private Annotation getAnnotation(LinkedHashSet<Particle> particles) {

        // Create new annotation
        Annotation newAnnotation = null;

        // Check each particle for a contained annotation
        for (Particle particle : particles) {

            // Check if particle contains an annotation
            if (particle.getAnnotation() != null) {
                Annotation oldAnnotation = particle.getAnnotation();

                // If new annotation is still null initialize new annotation
                if (newAnnotation == null) {
                    newAnnotation = new Annotation();
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
            }
        }
        return newAnnotation;
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
     * This method computes a new "default" attribute for the given set of
     * elements. A new "default" attribute only exists if only one element in
     * the set contains a "default" attribute. If more than one different
     * "default" attribute exists no attribute can be constructed.
     *
     * @param elements Set of elements, which is used to construct a new
     * element.
     * @return A String, which represents the new "defaul" attribute or a null
     * pointer, if no atttribute could be constructed.
     */
    private String getDefault(LinkedHashSet<Element> elements) {
        LinkedHashSet<String> defaultValues = new LinkedHashSet<String>();

        // Check each element for a "default" attribute
        for (Element element : elements) {

            // If element contains a "default" attribute add the value of this attribute to the value set
            if (element.getDefault() != null) {
                defaultValues.add(element.getDefault());
            }
        }

        // If only one "default" value exist in the set return this value
        if (defaultValues.size() == 1) {
            return defaultValues.iterator().next();
        } else {

            // If more than one "default" value exist in the set no unambiguous value exists
            return null;
        }
    }

    /**
     * This method generates a new "fixed" attribute for a given set of
     * elements. If not all elements contain the same "fixed" attribute either
     * null or a new type are returned. The type used enumerations fo the
     * different "fixed" values.
     *
     * @param elements Set of elements, each element may contain a "fixed"
     * attribute.
     * @param newTypeRef Type reference of the type contained in the new
     * element.
     * @return If no "fixed" attribute was constructed a null pointer. If a
     * "fixed" attribute was constructed a String containing the value of the
     * "fixed" attribute. If no unambiguous "fixed" attribute could be
     * constructed a new type, which used enumerations for an equivalent
     * behaviour.
     */
    private Object getFixed(LinkedHashSet<Element> elements, SymbolTableRef<Type> newTypeRef) {

        // Use set of strings to store "fixed" values and boolean variable to check if all elements are fixed
        LinkedHashSet<String> fixed = new LinkedHashSet<String>();
        boolean allFixed = true;

        // Check the specified elements for "fixed" attribute
        for (Element element : elements) {

            // Check if element contains "fixed" attribute and if each element until now contained an "fixed" attribute
            if (allFixed && element.getFixed() != null) {
                fixed.add(element.getFixed());
            } else {

                // If one element is not fixed set allFixed variable to false
                allFixed = false;
            }
        }

        // Check if all elements are fixed
        if (allFixed) {

            // Check if only one "fixed" value was used
            if (fixed.size() == 1) {

                // If only one "fixed" value exist the new "fixed" attribute has this value
                return fixed.iterator().next();
            } else if (fixed.size() > 1 && newTypeRef.getReference() instanceof SimpleType) {

                // Check if the referred type is an anyTyp or anySimpleType if that is the case replace it with a string type
                if (newTypeRef.getReference().getName().equals("{http://www.w3.org/2001/XMLSchema}anyType") || newTypeRef.getReference().getName().equals("{http://www.w3.org/2001/XMLSchema}anySimpleType")) {
                    String name = "{http://www.w3.org/2001/XMLSchema}string";

                    // Check if string type exist in output schema
                    if (!outputSchema.getTypeSymbolTable().hasReference(name)) {
                        outputSchema.getTypeSymbolTable().updateOrCreateReference(name, new SimpleType(name, null));
                    }

                    // New type is now no anyType anymore
                    newTypeRef = outputSchema.getTypeSymbolTable().getReference(name);
                }

                // If more than one "fixed" value exist and if the specified type reference of the new element referrers to a simpleType create new simple content restriction
                SimpleContentRestriction simpleContentRestriction = new SimpleContentRestriction(newTypeRef);

                // Add new enumerations, created by using the "fixed" values, to the restriction
                simpleContentRestriction.addEnumeration(new LinkedList<String>(fixed));

                // Create new anonymous type containing the new restriction and add it to the output schema
                SimpleType restrictionType = new SimpleType("{" + outputSchema.getTargetNamespace() + "}" + "simpleType" + UUID.randomUUID(), simpleContentRestriction, true);
                outputSchema.getTypeSymbolTable().updateOrCreateReference(restrictionType.getName(), restrictionType);

                // Return new anonymous type
                return outputSchema.getTypeSymbolTable().getReference(restrictionType.getName());
            } else {
                return null;
            }
        } else {

            // If not all elements are fixed return null
            return null;
        }
    }

    /**
     * Generates a new ID for a given set of particles. New ID is valid for
     * all output schema.
     *
     * @param particles Set of particles, each particle may contain an ID.
     * @return New ID for the particle build for the particle set.
     */
    private String getID(LinkedHashSet<Particle> particles) {

        // Initialize new ID
        String newID = "";

        // Check for each particle if an ID is contained
        for (Iterator<Particle> it = particles.iterator(); it.hasNext();) {
            Particle particle = it.next();

            // If an ID is contained append ID to new ID
            if (particle.getId() != null) {
                newID += particle.getId();
            }
            if (!newID.equals("") && it.hasNext()) {
                newID += ".";
            }
        }

        // Check if a new ID was generated
        if (!newID.equals("")) {

            //  Use ID base and number to find a valid ID
            String newIDBase = newID;
            int number = 1;

            // As long as the new ID is invalid use another new ID
            while (usedIDs.contains(newID)) {

                // Add a number to the base of the new ID
                newID = newIDBase + "." + number;
                number++;
            }

            // Add new ID to the set of used IDs
            usedIDs.add(newID);
        } else {
            return null;
        }
        return newID;
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

    /**
     * Creates a new annotation with new appInfos and new documentations by
     * copying the given old annotation.
     *
     * @param oldAnnotation Blueprint for the new annotation.
     * @return New Annotation matching the old annotation.
     */
    private Annotation generateNewAnnotation(Annotation oldAnnotation) {

        // Check if old annotation exists
        if (oldAnnotation != null) {

            // Create new annotation
            Annotation newAnnotation = new Annotation();

            // Add id of the old annotation to the new annotation
            newAnnotation.setId(getID(oldAnnotation.getId()));

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
        } else {
            return null;
        }
    }
}
