package eu.fox7.bonxai.xsd.setOperations.difference;

import eu.fox7.bonxai.common.AllPattern;
import eu.fox7.bonxai.common.Annotation;
import eu.fox7.bonxai.common.AnyPattern;
import eu.fox7.bonxai.common.ChoicePattern;
import eu.fox7.bonxai.common.CountingPattern;
import eu.fox7.bonxai.common.GroupRef;
import eu.fox7.bonxai.common.Particle;
import eu.fox7.bonxai.common.ParticleContainer;
import eu.fox7.bonxai.common.SequencePattern;
import eu.fox7.bonxai.common.SymbolTableRef;
import eu.fox7.bonxai.xsd.*;
import eu.fox7.bonxai.xsd.XSDSchema.*;
import eu.fox7.bonxai.xsd.automaton.ParticleAutomatons.exceptions.*;
import eu.fox7.bonxai.xsd.automaton.exceptions.*;
import eu.fox7.bonxai.xsd.tools.*;

import java.util.*;

/**
 * The TypeDifferenceGenerator class is used to generate new types for a schema
 * difference. This types are computed via the used of the particle automaton or
 * a special container structure for simpleTypes.
 *
 * @author Dominik Wolff
 */
public class TypeDifferenceGenerator {

    // XSDSchema which will contain the difference of the minuend schema and the subtrahend schema
    private XSDSchema outputSchema;

    // Target namespace of the outputSchema
    private String outputTargetNamespace;

    // Map mapping target namespaces to output schemata, this is necessary to reference components in other schemata
    private LinkedHashMap<String, XSDSchema> namespaceOutputSchemaMap;

    // Map mapping target namespaces to old minuend schemata used to construct the corresponding output schema
    private LinkedHashMap<String, XSDSchema> namespaceOldSchemaMap;

    // Map mapping a namespace to all elements in a minuned schema with this target namespace which are in conflict with elements of the subtrahend schema with this target namespace
    private LinkedHashMap<String, LinkedHashSet<Element>> namespaceConflictingElementsMap;

    // Map mapping elements to groups which were created to reference these elements without being top-level elements.
    private LinkedHashMap<Element, Group> elementGroupMap;

    // Attribute particle difference generator of the type union generator class
    private AttributeParticleDifferenceGenerator attributeParticleDifferenceGenerator;

    // Particle difference generator of the type union generator class
    private ParticleDifferenceGenerator particleDifferenceGenerator;

    // Set containing all IDs used in all new output schemata
    private LinkedHashSet<String> usedIDs;

    // HashMap which contains for every local elemente in the given context its type reference
    private LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap;

    // HashMap which contains for every local elemente in the given context its old type reference
    private LinkedHashMap<String, SymbolTableRef<Type>> elementOldTypeMap;

    // HashMap which contains for every typ the attribute list which was computed for it
    private LinkedHashMap<Type, LinkedList<AttributeParticle>> newTypeNewAttributesMap;

    // Set of types which may be empty due to empty type definitions
    private LinkedHashSet<Type> possibleEmptyTypes;

    // Map mapping types to old schemata used to construct the new output schema
    private LinkedHashMap<Type, XSDSchema> typeOldSchemaMap;

    /**
     * Constructor for the TypeDifferenceGenerator class, which initializes
     * all necessary fields of the class. The TypeDifferenceGenerator mainly
     * needs this information for contained ParticleDifferenceGenerators and
     * AttributeParticleDifferenceGenerators.
     *
     * @param outputSchema XSDSchema which will contain the new element structure.
     * @param namespaceOutputSchemaMap Map mapping namespaces to output schemata.
     * @param namespaceOldSchemaMap Map mapping namespaces to old schemata of
     * other output schemata.
     * @param namespaceConflictingElementsMap Map maps namespaces to conflicting
     * Elements within that namespace.
     * @param elementGroupMap Map maps elements to groups they are now contained
     * in.
     * @param attributeParticleDifferenceGenerator Attribute particle difference
     * generator of the type union generator class.
     * @param particleDifferenceGenerator Particle difference generator of the
     * type union generator class.
     * @param newTypeNewAttributesMap HashMap which contains for every typ the
     * attribute list which was computed for it.
     * @param typeOldSchemaMap Map mapping types to old schemata used to
     * construct the new output schema.
     * @param usedIDs Set containing all IDs used in all new output schemata.
     * @param possibleEmptyTypes Set of types which may be empty due to empty
     * type definitions.
     */
    public TypeDifferenceGenerator(XSDSchema outputSchema,
            LinkedHashMap<String, XSDSchema> namespaceOutputSchemaMap,
            LinkedHashMap<String, XSDSchema> namespaceOldSchemaMap,
            LinkedHashMap<String, LinkedHashSet<Element>> namespaceConflictingElementsMap,
            LinkedHashMap<Element, Group> elementGroupMap,
            AttributeParticleDifferenceGenerator attributeParticleDifferenceGenerator,
            ParticleDifferenceGenerator particleDifferenceGenerator,
            LinkedHashSet<String> usedIDs,
            LinkedHashMap<Type, XSDSchema> typeOldSchemaMap,
            LinkedHashMap<Type, LinkedList<AttributeParticle>> newTypeNewAttributesMap,
            LinkedHashSet<Type> possibleEmptyTypes) {

        //Initialize all class fields.
        this.outputSchema = outputSchema;
        this.outputTargetNamespace = outputSchema.getTargetNamespace();
        this.namespaceOutputSchemaMap = namespaceOutputSchemaMap;
        this.namespaceOldSchemaMap = namespaceOldSchemaMap;
        this.namespaceConflictingElementsMap = namespaceConflictingElementsMap;
        this.elementGroupMap = elementGroupMap;
        this.attributeParticleDifferenceGenerator = attributeParticleDifferenceGenerator;
        this.particleDifferenceGenerator = particleDifferenceGenerator;
        this.usedIDs = usedIDs;
        this.typeOldSchemaMap = typeOldSchemaMap;
        this.newTypeNewAttributesMap = newTypeNewAttributesMap;
        this.possibleEmptyTypes = possibleEmptyTypes;
    }

    private Type createNewComplexType(ComplexType minuendComplexType, ComplexType subtrahendComplexType, String newTypeName) throws EmptySubsetParticleStateFieldException, NonDeterministicFiniteAutomataException, UniqueParticleAttributionViolationException, EmptyProductParticleStateFieldException, NoUniqueStateNumbersException, NoDestinationStateFoundException, NotSupportedParticleAutomatonException {

        // If minuend type is mixed and subtrahend type is not return minuend type
        if (minuendComplexType.getMixed() && !subtrahendComplexType.getMixed()) {
            return createNewComplexType(minuendComplexType, newTypeName);
        }

        // Get new name
        String newName = "{" + outputSchema.getTargetNamespace() + "}difference-type." + minuendComplexType.getLocalName() + "-" + subtrahendComplexType.getLocalName();
        ComplexType newComplexType = new ComplexType(newName, null);

        // Get new fields to store results in
        Content newContent = null;
        Particle newParticle = null;
        LinkedList<AttributeParticle> newAttribtibuteParticles = null;
        Type newBase = null;

        // Check if minuend content is empty and replace it with empty complex content
        if (minuendComplexType.getContent() == null) {
            minuendComplexType.setContent(new ComplexContentType());
        }

        // Check if subtrahend content is empty and replace it with empty complex content
        if (subtrahendComplexType.getContent() == null) {
            subtrahendComplexType.setContent(new ComplexContentType());
        }

        // Check if minuend complexType has complex or simple content
        if (minuendComplexType.getContent() instanceof ComplexContentType) {
            ComplexContentType minuendComplexContentType = (ComplexContentType) minuendComplexType.getContent();

            // Check if subtrahend complex type has complex or simple content
            if (subtrahendComplexType.getContent() instanceof ComplexContentType) {
                ComplexContentType subtrahendComplexContentType = (ComplexContentType) subtrahendComplexType.getContent();

                // Generate new particle
                newParticle = particleDifferenceGenerator.generateNewParticleDifference(minuendComplexContentType.getParticle(), subtrahendComplexContentType.getParticle(), elementTypeMap, elementOldTypeMap);

                // Generate new attribute
                newAttribtibuteParticles = attributeParticleDifferenceGenerator.generateAttributeParticleDifference(minuendComplexType.getAttributes(), subtrahendComplexType.getAttributes());
                newTypeNewAttributesMap.put(newComplexType, newAttribtibuteParticles);

            } else if (subtrahendComplexType.getContent() instanceof SimpleContentType) {
                SimpleContentType subtrahendSimpleContentType = (SimpleContentType) subtrahendComplexType.getContent();

                // Check if the inheritance is an extension or restriction
                if (subtrahendSimpleContentType.getInheritance() instanceof SimpleContentExtension) {
                    SimpleContentExtension simpleContentExtension = (SimpleContentExtension) subtrahendSimpleContentType.getInheritance();

                    // Generate new attribute
                    newAttribtibuteParticles = attributeParticleDifferenceGenerator.generateAttributeParticleDifference(minuendComplexType.getAttributes(), simpleContentExtension.getAttributes());

                    // Generate new particle
                    newParticle = particleDifferenceGenerator.generateNewParticleDifference(minuendComplexContentType.getParticle(), null, elementTypeMap, elementOldTypeMap);

                    // Check if minuend type is mixed
                    if (minuendComplexType.getMixed() || ((ComplexContentType) minuendComplexType.getContent()).getMixed()) {

                        // Check if the subtrahend type is a anySimpleType
                        if (containsUnrestrictedAnySimpleType((SimpleType) simpleContentExtension.getBase())) {

                            // Minuend type has a complexContent
                            Particle particle = minuendComplexContentType.getParticle();

                            // Check if content is empty
                            if (particle != null || !containsElementOrAnyPattern(particle)) {
                                newParticle = null;
                            } else if (isOptionalParticle(particle)) {

                                // Remove optional part of the particle and return new minuend type
                                newParticle = removeOptionalPart(newParticle, null);
                            }
                        }
                    } else {

                        // Check if the subtrahend type can be empty
                        if (canBeEmpty((SimpleType) simpleContentExtension.getBase())) {

                            // Minuend type has a complexContent
                            Particle particle = minuendComplexContentType.getParticle();

                            // Check if content is empty
                            if (particle != null || !containsElementOrAnyPattern(particle)) {
                                newParticle = null;
                            } else if (isOptionalParticle(particle)) {

                                // Remove optional part of the particle and return new minuend type
                                newParticle = removeOptionalPart(newParticle, null);
                            }
                        }
                    }
                }
            }

            // Check if difference of particles is empty
            if (newParticle == null) {

                // Check if difference of attribute particles is empty
                if (newAttribtibuteParticles == null) {
                    return null;
                } else {
                    newParticle = particleDifferenceGenerator.generateNewParticleDifference(minuendComplexContentType.getParticle(), null, elementTypeMap, elementOldTypeMap);
                }
            } else {

                // Check if difference of attribute particles is empty
                if (newAttribtibuteParticles == null) {
                    newAttribtibuteParticles = attributeParticleDifferenceGenerator.generateAttributeParticleDifference(minuendComplexType.getAttributes(), null);
                } else {
                    possibleEmptyTypes.add(newComplexType);
                }
            }

            // Get new content
            ComplexContentType newComplexContentType = new ComplexContentType();
            newComplexContentType.setParticle(newParticle);
            newContent = newComplexContentType;

        } else if (minuendComplexType.getContent() instanceof SimpleContentType) {
            SimpleContentType minuendSimpleContentType = (SimpleContentType) minuendComplexType.getContent();

            // Check if the inheritance is an extension
            if (minuendSimpleContentType.getInheritance() instanceof SimpleContentExtension) {
                SimpleContentExtension minuendExtension = (SimpleContentExtension) minuendSimpleContentType.getInheritance();

                // Check if subtrahend type is mixed
                if (subtrahendComplexType.getMixed() || (subtrahendComplexType.getContent() instanceof ComplexContentType && ((ComplexContentType) subtrahendComplexType.getContent()).getMixed())) {

                    // Subtrahend type has a complexContent
                    ComplexContentType complexContentType = (ComplexContentType) subtrahendComplexType.getContent();
                    Particle particle = complexContentType.getParticle();

                    // Generate new attribute
                    newAttribtibuteParticles = attributeParticleDifferenceGenerator.generateAttributeParticleDifference(minuendExtension.getAttributes(), subtrahendComplexType.getAttributes());
                    newTypeNewAttributesMap.put(newComplexType, newAttribtibuteParticles);

                    // Check if content is optional
                    if (particle == null || isOptionalParticle(particle)) {
                        newBase = null;
                    }
                } else if (subtrahendComplexType.getContent() instanceof SimpleContentType) {

                    // In case the subtrahend complexType has a simpleContent
                    SimpleContentType simpleContentType = (SimpleContentType) subtrahendComplexType.getContent();

                    if (simpleContentType.getInheritance() instanceof SimpleContentExtension) {

                        // Get difference of the minuend type and the subtrahend base type
                        SimpleContentExtension subtrahendExtension = (SimpleContentExtension) simpleContentType.getInheritance();

                        // Generate new attribute and base type
                        newAttribtibuteParticles = attributeParticleDifferenceGenerator.generateAttributeParticleDifference(minuendComplexType.getAttributes(), subtrahendExtension.getAttributes());
                        newBase = generateNewSimpleType((SimpleType) minuendExtension.getBase(), (SimpleType) subtrahendExtension.getBase(), getTypeName(minuendExtension.getBase(), subtrahendExtension.getBase()));
                    }
                }

                // Check if difference of particles is empty
                if (newParticle == null) {

                    // Check if difference of attribute particles is empty
                    if (newAttribtibuteParticles == null) {
                        return null;
                    } else {
                        newBase = generateNewSimpleType((SimpleType) minuendExtension.getBase(), getTypeName(minuendExtension.getBase()));
                    }
                } else {

                    // Check if difference of attribute particles is empty
                    if (newAttribtibuteParticles == null) {
                        newAttribtibuteParticles = attributeParticleDifferenceGenerator.generateAttributeParticleDifference(minuendComplexType.getAttributes(), null);
                    } else {
                        return createNewComplexType(minuendComplexType, newTypeName);
                    }
                }

                // Get new content
                SimpleContentExtension newSimpleContentExtension = new SimpleContentExtension(outputSchema.getTypeSymbolTable().getReference(newBase.getName()));

                if (newAttribtibuteParticles != null) {
                    newSimpleContentExtension.setAttributes(newAttribtibuteParticles);
                }
                SimpleContentType simpleContentType = new SimpleContentType();
                simpleContentType.setInheritance(newSimpleContentExtension);
                newContent = simpleContentType;
            }
        }

        // Create new complexType with new content
        newComplexType.setContent(newContent);

        // Set id and annotation for the new type
        newComplexType.setAnnotation(generateNewAnnotation(minuendComplexType.getAnnotation()));
        newComplexType.setId(getID(minuendComplexType.getId()));

        // Check if the minuend complexType is anonymous
        newComplexType.setIsAnonymous(minuendComplexType.isAnonymous());

        // Set value of "mixed" attribute to the same value of the minuend type "mixed" attribute
        newComplexType.setMixed(minuendComplexType.getMixed());

        // Set new attributes
        if (newContent instanceof ComplexContentType) {

            if (newAttribtibuteParticles != null) {
                newComplexType.setAttributes(newAttribtibuteParticles);
            }
        }

        // "block" attribute is present add "block" value to new type
        newComplexType.setBlockModifiers(getComplexTypeBlock(minuendComplexType));

        // Check if "final" attribute of minuend type and schema
        newComplexType.setFinalModifiers(getComplexTypeFinal(minuendComplexType));

        outputSchema.getTypeSymbolTable().updateOrCreateReference(newName, newComplexType);

        // Add new type to the list of top-level types if it is not anonymous
        if (newComplexType != null && !newComplexType.isAnonymous()) {
            outputSchema.addType(outputSchema.getTypeSymbolTable().getReference(newComplexType.getName()));
        }

        // Return new complexType
        return newComplexType;
    }

    /**
     * This method removes the optional part of a specified particle. The parent
     * particle is needed in order to decide whether the minimal occurrence of a
     * particle can be changed.
     *
     * @param particle Particle which will not be optional afterwards.
     * @param parentParticle Parent of the current particle, not necessarily the
     * direct parent of the particle because counting patterns are not counted
     * as parents.
     * @return
     */
    private Particle removeOptionalPart(Particle particle, Particle parentParticle) {

        // All pattern, sequence pattern and choice pattern are processed in the same way
        if (particle instanceof AllPattern || particle instanceof SequencePattern || particle instanceof ChoicePattern) {
            ParticleContainer particleContainer = (ParticleContainer) particle;

            // A particle container has to be empty to be optional
            if (particleContainer.getParticles().isEmpty()) {
                return null;
            } else {

                // Create new particle list to store particle with removed optional part
                LinkedList<Particle> particles = new LinkedList<Particle>();

                // Remove optional part from each contained particle
                for (Iterator<Particle> it = particleContainer.getParticles().iterator(); it.hasNext();) {
                    Particle containedParticle = it.next();

                    // Remove optional part from the contained particle
                    Particle newContainedParticle = removeOptionalPart(containedParticle, particle);

                    // Check if new contained particle is not null and add it to the list, if it is
                    if (newContainedParticle != null) {
                        particles.add(newContainedParticle);
                    }
                }

                // Check if new particle is empty
                if (particles.isEmpty()) {
                    return null;
                } else {

                    // Set new particles for the particle container and return the particle container
                    particleContainer.setParticles(particles);
                    return particleContainer;
                }
            }
        } else if (particle instanceof CountingPattern) {
            CountingPattern countingPattern = (CountingPattern) particle;

            // If minimal occurrence is zero and the parent particle is a choice pattern it is set to one instead (If the particle is in a sequence setting its minimal occurrence to one would result in a major change.
            // Instead an approximation is made, so that it is still possible for the particle to be optional.)
            if (countingPattern.getMin() == 0 && parentParticle instanceof ChoicePattern) {
                countingPattern.setMin(1);
            }

            // Remove optional part from the contained particle
            Particle newContainedParticle = removeOptionalPart(countingPattern.getParticles().getFirst(), parentParticle);

            // Check if the contained particle after removing the optional part is still not null
            if (newContainedParticle != null) {

                // If the new contained particle is not null create new particle list to store the new contained particle
                LinkedList<Particle> particles = new LinkedList<Particle>();
                particles.add(newContainedParticle);

                // Set new contained particle as particle of the counting pattern and return the counting pattern
                countingPattern.setParticles(particles);
                return countingPattern;
            } else {

                // If the contained particle is null the counting pattern is null as well
                return null;
            }
        } else {

            // If the particle is an element, element reference or an any the particle the particle is not changed
            return particle;
        }
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
     * This method checks if a specified particle is optional, means the content
     * of an element containing the type with this particle can be empty in an
     * XML instance.
     *
     * @param particle Particle for which is checked, if it is optional.
     * @return <tt>true</tt> if the particle is optional and <tt>false</tt> if
     * it is not.
     */
    public boolean isOptionalParticle(Particle particle) {

        if (particle instanceof AllPattern) {
            AllPattern allPattern = (AllPattern) particle;

            // An all pattern has to be empty to be optional
            if (allPattern.getParticles().isEmpty()) {
                return true;
            } else {

                // If the all pattern is not empty all of its contained particles have to be optional in order for the all itself to be optional
                boolean isOptional = true;

                // If the variable "isOptional" is true and the "isOptionalParticle" method returns true the variable is still true else it is false and never will be true again
                for (Iterator<Particle> it = allPattern.getParticles().iterator(); it.hasNext();) {
                    Particle containedParticle = it.next();
                    isOptional &= isOptionalParticle(containedParticle);
                }

                // Return result for the all pattern stored in the "isOptional" variable
                return isOptional;
            }
        } else if (particle instanceof SequencePattern) {
            SequencePattern sequencePattern = (SequencePattern) particle;

            // If the sequence pattern is empty it is optional
            if (sequencePattern.getParticles().isEmpty()) {
                return true;
            } else {

                // If the sequence pattern is not empty all of its contained particles have to be optional in order for the sequence itself to be optional
                boolean isOptional = true;

                // If the variable "isOptional" is true and the "isOptionalParticle" method returns true the variable is still true else it is false and never will be true again
                for (Iterator<Particle> it = sequencePattern.getParticles().iterator(); it.hasNext();) {
                    Particle containedParticle = it.next();
                    isOptional &= isOptionalParticle(containedParticle);
                }

                // Return result for the sequence pattern stored in the "isOptional" variable
                return isOptional;
            }
        } else if (particle instanceof ChoicePattern) {
            ChoicePattern choicePattern = (ChoicePattern) particle;

            // If the choice pattern is empty it is optional
            if (choicePattern.getParticles().isEmpty()) {
                return true;
            } else {

                // If the choice pattern is not empty one of its contained particles has to be optional in order for the choice itself to be optional
                boolean isOptional = false;

                // If the variable "isOptional" is false and the "isOptionalParticle" method returns false the variable is still false else it is true and never will be false again
                for (Iterator<Particle> it = choicePattern.getParticles().iterator(); it.hasNext();) {
                    Particle containedParticle = it.next();
                    isOptional |= isOptionalParticle(containedParticle);
                }

                // Return result for the choice pattern stored in the "isOptional" variable
                return isOptional;
            }
        } else if (particle instanceof CountingPattern) {
            CountingPattern countingPattern = (CountingPattern) particle;

            // Check if counting pattern has minimal occurrence zero, if it has the particle is optional
            if (countingPattern.getMin() == 0) {
                return true;
            } else {

                // If the minimal occurrence is not zero check the contained particle
                return isOptionalParticle(countingPattern.getParticles().getFirst());
            }
        } else {

            // If the particle is an element, element reference or an any the particle is not optional
            return false;
        }
    }

    /**
     * Create new complexType as a copy of the specified old complexType. All
     * informations contained in the old complexType are transfered to the new
     * complexType. The new complexType contains new attributes and so on.
     *
     * @param oldComplexType ComplexType used to create the new complexType as
     * an exact copy of the old complexType in the new schema.
     * @param oldSchema XSDSchema containing the old complexType and its type
     * reference.
     * @return New complexType in the new schema created from the specified old
     * complexType.
     */
    private Type createNewComplexType(ComplexType oldComplexType, String newTypeName) throws EmptySubsetParticleStateFieldException, NonDeterministicFiniteAutomataException, UniqueParticleAttributionViolationException, EmptyProductParticleStateFieldException, NoUniqueStateNumbersException, NoDestinationStateFoundException, NotSupportedParticleAutomatonException {

        // Create new content
        Content newContent = null;

        // If old content is an ComplexContentType create new ComplexContentType.
        if (oldComplexType.getContent() instanceof ComplexContentType) {

            // Create new ComplexContentType with the createNewComplexContentType method
            ComplexContentType oldComplexContentType = (ComplexContentType) oldComplexType.getContent();

            // Create new ComplexContentType with new particle and new ComplexContentInheritance
            ComplexContentType newComplexContentType = new ComplexContentType(particleDifferenceGenerator.generateNewParticleDifference(oldComplexContentType.getParticle(), null, elementTypeMap, elementOldTypeMap));

            // Set new fiels for the new ComplexContentType
            newComplexContentType.setAnnotation(generateNewAnnotation(oldComplexContentType.getAnnotation()));
            newComplexContentType.setId(getID(oldComplexContentType.getId()));
            newComplexContentType.setMixed(oldComplexContentType.getMixed());
            newContent = newComplexContentType;

        // If old content is an SimpleContentType create new SimpleContentType.
        } else if (oldComplexType.getContent() instanceof SimpleContentType) {

            // Create new SimpleContentType with the createNewSimpleContentType method
            SimpleContentType oldSimpleContentType = (SimpleContentType) oldComplexType.getContent();

            // Create new SimpleContentType
            SimpleContentType newSimpleContentType = new SimpleContentType();

            // Set id and annotation for the new SimpleContentType
            newSimpleContentType.setAnnotation(generateNewAnnotation(oldSimpleContentType.getAnnotation()));
            newSimpleContentType.setId(getID(oldSimpleContentType.getId()));

            // Set new created inheritance
            newSimpleContentType.setInheritance(createNewSimpleContentInheritance(oldSimpleContentType.getInheritance()));
            newContent = newSimpleContentType;
        }

        // Create new complexType with new content
        ComplexType newComplexType = new ComplexType(newTypeName, newContent);

        // Set id and annotation for the new type
        newComplexType.setAnnotation(generateNewAnnotation(oldComplexType.getAnnotation()));
        newComplexType.setId(getID(oldComplexType.getId()));

        // Set "abstract" and "mixed" values and check if complexType is anonymous
        newComplexType.setAbstract(oldComplexType.isAbstract());
        newComplexType.setIsAnonymous(oldComplexType.isAnonymous());
        newComplexType.setMixed(oldComplexType.getMixed());

        // Get list of all attribute particles contained in the old complexType
        LinkedList<AttributeParticle> oldAttributeParticles = oldComplexType.getAttributes();

        // Create for each attribute particle a new attribute particle and add it to the new complexType
        for (Iterator<AttributeParticle> it = oldAttributeParticles.iterator(); it.hasNext();) {
            AttributeParticle oldAttributeParticle = it.next();

            // Add new create attribute particle to the new extension
            newComplexType.addAttribute(attributeParticleDifferenceGenerator.generateNewAttributeParticle(oldAttributeParticle));
        }

        // "block" attribute is present add "block" value to new type
        newComplexType.setBlockModifiers(getComplexTypeBlock(oldComplexType));

        // "final" attribute is present add "final" value to new type
        newComplexType.setFinalModifiers(getComplexTypeFinal(oldComplexType));

        outputSchema.getTypeSymbolTable().updateOrCreateReference(newTypeName, newComplexType);

        // Add new type to the list of top-level types if it is not anonymous
        if (newComplexType != null && !newComplexType.isAnonymous()) {
            outputSchema.addType(outputSchema.getTypeSymbolTable().getReference(newComplexType.getName()));
        }

        // Return new complexType
        return newComplexType;
    }

    /**
     * Create new minuend type by computing the difference of the given minuend
     * simpleType and subtrahend complexType. The new type will be a simpleType
     * if the complexType has simpleContent or is mixed it is possible that the
     * minuend type is changed.
     *
     * @param minuendSimpleType Type from which the subtrahend type is removed.
     * @param subtrahendComplexType Type which is removed from the subtrahend
     * type.
     * @param typeName Name of the new type.
     * @return New simpleType which is based on the minuend type and constructed
     * by removing the subtrahend type from the minuend type.
     */
    private Type generateNewSimpleType(SimpleType minuendSimpleType, ComplexType subtrahendComplexType, String typeName) {

        // If subtrahend type is anyTyp return null
        if (minuendSimpleType.getName().equals("{http://www.w3.org/2001/XMLSchema}anyType")) {

            // Name of the any simple type
            String name = "{http://www.w3.org/2001/XMLSchema}anyType";

            // Check if any type has to be regstered in the output schema
            if (!outputSchema.getTypeSymbolTable().hasReference(name)) {
                outputSchema.getTypeSymbolTable().updateOrCreateReference(name, new SimpleType(name, null));
            }

            // Return any simple type
            return (SimpleType) outputSchema.getTypeSymbolTable().getReference(name).getReference();
        }

        // Check if contained attributes are optional
        if (attributeParticleDifferenceGenerator.isOptional(subtrahendComplexType.getAttributes())) {

            // Check if subtrahend type is mixed
            if (subtrahendComplexType.getMixed() || (subtrahendComplexType.getContent() instanceof ComplexContentType && ((ComplexContentType) subtrahendComplexType.getContent()).getMixed())) {

                // Subtrahend type has a complexContent
                ComplexContentType complexContentType = (ComplexContentType) subtrahendComplexType.getContent();
                Particle particle = complexContentType.getParticle();

                // Check if content is optional
                if (particle == null || isOptionalParticle(particle)) {
                    return null;
                }
            } else if (subtrahendComplexType.getContent() instanceof SimpleContentType) {

                // In case the subtrahend complexType has a simpleContent
                SimpleContentType simpleContentType = (SimpleContentType) subtrahendComplexType.getContent();

                if (simpleContentType.getInheritance() instanceof SimpleContentExtension) {

                    // Get difference of the minuend type and the subtrahend base type
                    SimpleContentExtension simpleContentExtension = (SimpleContentExtension) simpleContentType.getInheritance();
                    return generateNewSimpleType(minuendSimpleType, (SimpleType) simpleContentExtension.getBase(), getTypeName(minuendSimpleType, simpleContentExtension.getBase()));
                }
            }
        }

        // Create new simpleType with new inheritance
        SimpleType newSimpleType = generateNewSimpleType(minuendSimpleType, typeName);

        // Add new type to the list of top-level types if it is not anonymous
        if (newSimpleType != null && !newSimpleType.isAnonymous()) {
            outputSchema.addType(outputSchema.getTypeSymbolTable().getReference(newSimpleType.getName()));
        }

        // If subtrahend type is not mixed the equivalent type in the output schema is returned
        return newSimpleType;
    }

    /**
     * Checks if a simpleType can contain no content.
     *
     * @param simpleType SimpleType, which may be abel to be empty.
     * @return <tt>true<tt/> if the simpleType can be empty.
     */
    private boolean canBeEmpty(SimpleType simpleType) {

        // Check simpleType inheritance
        if (simpleType.getInheritance() instanceof SimpleContentList) {
            SimpleContentList simpleContentList = (SimpleContentList) simpleType.getInheritance();

            // Check if the item type of the list can contain empty content
            canBeEmpty((SimpleType) simpleContentList.getBase());

        } else if (simpleType.getInheritance() instanceof SimpleContentRestriction) {
            SimpleContentRestriction simpleContentRestriction = (SimpleContentRestriction) simpleType.getInheritance();

            // Check if restriction allowes empty content
            if ((!simpleContentRestriction.getEnumeration().isEmpty() && !simpleContentRestriction.getEnumeration().contains("")) ||
                    (simpleContentRestriction.getLength() != null && new Double(simpleContentRestriction.getLength().getValue()) > 0) ||
                    (simpleContentRestriction.getMinLength() != null && new Double(simpleContentRestriction.getMinLength().getValue()) > 0) ||
                    (simpleContentRestriction.getTotalDigits() != null && new Double(simpleContentRestriction.getTotalDigits().getValue()) > 0)) {
                return false;
            }
            return canBeEmpty((SimpleType) simpleContentRestriction.getBase());

        } else if (simpleType.getInheritance() instanceof SimpleContentUnion) {
            SimpleContentUnion simpleContentUnion = (SimpleContentUnion) simpleType.getInheritance();

            // Check each member type of the union if one can be empty the union can be empty
            for (Iterator<SymbolTableRef<Type>> it = simpleContentUnion.getAllMemberTypes().iterator(); it.hasNext();) {
                SymbolTableRef<Type> symbolTableRef = it.next();

                if (!canBeEmpty((SimpleType) symbolTableRef.getReference())) {
                    return false;
                }
            }
        } else if (simpleType.getInheritance() == null) {

            // List of build-in datatypes which can be empty
            LinkedHashSet<String> typesAllowingEmpty = new LinkedHashSet<String>();
            typesAllowingEmpty.add("{http://www.w3.org/2001/XMLSchema}string");
            typesAllowingEmpty.add("{http://www.w3.org/2001/XMLSchema}anySimpleType");
            typesAllowingEmpty.add("{http://www.w3.org/2001/XMLSchema}anyURI");
            typesAllowingEmpty.add("{http://www.w3.org/2001/XMLSchema}base64Binary");
            typesAllowingEmpty.add("{http://www.w3.org/2001/XMLSchema}hexBinary");
            typesAllowingEmpty.add("{http://www.w3.org/2001/XMLSchema}normalizedString");
            typesAllowingEmpty.add("{http://www.w3.org/2001/XMLSchema}token");
            typesAllowingEmpty.add("{http://www.w3.org/2001/XMLSchema}anyType");

            // Return true if the current build-in datatype is contained in the list
            if (typesAllowingEmpty.contains(simpleType.getName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Create new complexType by removing the subtrahend simpleType from the
     * minuend ComplexType. This is only possible if the subtrahend type is a
     * string and the minuend type has "mixed" value true and has either no
     * content or optional content.
     *
     * @param minuendComplexType Type from which the subtrahend type is removed
     * from.
     * @param subtrahendSimpleType Type is removed from the subtrahend type.
     * @return New type which is basically the minuend type without the content
     * of the subtrahend type.
     */
    private Type createNewComplexType(ComplexType minuendComplexType, SimpleType subtrahendSimpleType, String newTypeName) throws EmptySubsetParticleStateFieldException, NonDeterministicFiniteAutomataException, UniqueParticleAttributionViolationException, EmptyProductParticleStateFieldException, NoUniqueStateNumbersException, NoDestinationStateFoundException, NotSupportedParticleAutomatonException {

        // If subtrahend type is anyTyp return null
        if (subtrahendSimpleType.getName().equals("{http://www.w3.org/2001/XMLSchema}anyType")) {
            return null;
        }

        // Create new content
        Content newContent = null;

        if (minuendComplexType.getContent() instanceof ComplexContentType) {
            ComplexContentType minuendComplexContentType = (ComplexContentType) minuendComplexType.getContent();

            newContent = new ComplexContentType(particleDifferenceGenerator.generateNewParticleDifference(minuendComplexContentType.getParticle(), null, elementTypeMap, elementOldTypeMap), minuendComplexContentType.getMixed());

            // Check if minuend type is mixed
            if (minuendComplexType.getMixed() || ((ComplexContentType) minuendComplexType.getContent()).getMixed()) {

                // Check if the subtrahend type is a anySimpleType
                if (containsUnrestrictedAnySimpleType(subtrahendSimpleType)) {

                    // Minuend type has a complexContent
                    Particle particle = minuendComplexContentType.getParticle();

                    // Check if content is empty
                    if (minuendComplexType.getAttributes().isEmpty() && (particle != null || !containsElementOrAnyPattern(particle))) {
                        return null;
                    }

                    // Check if content is optional
                    if (isOptionalParticle(particle) && attributeParticleDifferenceGenerator.isOptional(minuendComplexType.getAttributes())) {

                        // Remove optional part of the particle and return new minuend type
                        ((ComplexContentType) newContent).setParticle(removeOptionalPart(((ComplexContentType) newContent).getParticle(), null));
                    }
                }
            } else {

                // Check if the subtrahend type can be empty
                if (canBeEmpty(subtrahendSimpleType)) {

                    // Minuend type has a complexContent
                    Particle particle = minuendComplexContentType.getParticle();

                    // Check if content is empty
                    if (minuendComplexType.getAttributes().isEmpty() && (particle != null || !containsElementOrAnyPattern(particle))) {
                        return null;
                    }

                    // Check if content is optional
                    if (isOptionalParticle(particle) && attributeParticleDifferenceGenerator.isOptional(minuendComplexType.getAttributes())) {

                        // Remove optional part of the particle and return new minuend type
                        ((ComplexContentType) newContent).setParticle(removeOptionalPart(((ComplexContentType) newContent).getParticle(), null));
                    }
                }
            }
        } else if (minuendComplexType.getContent() instanceof SimpleContentType) {

            // In case the minuend complexType has a simpleContent
            SimpleContentType simpleContentType = (SimpleContentType) minuendComplexType.getContent();

            // Check if the inheritance is an extension or restriction
            if (simpleContentType.getInheritance() instanceof SimpleContentExtension) {

                // Get difference of the minuend type and the subtrahend base type
                SimpleContentExtension simpleContentExtension = (SimpleContentExtension) simpleContentType.getInheritance();

                if (simpleContentExtension.getAttributes().isEmpty()) {

                    // Create new base type for the extension
                    Type newBaseType = generateNewSimpleType((SimpleType) simpleContentExtension.getBase(), subtrahendSimpleType, getTypeName(simpleContentExtension.getBase(), subtrahendSimpleType));

                    // If new base type is null the new type is null
                    if (newBaseType == null) {
                        return null;
                    } else {

                        // Create new SimpleContentType
                        newContent = new SimpleContentType();

                        // Create new SimpleContent extension with new base
                        SimpleContentExtension newSimpleContentExtension = new SimpleContentExtension(outputSchema.getTypeSymbolTable().getReference(newBaseType.getName()));

                        // Set new id and annotation in new extension
                        newSimpleContentExtension.setAnnotation(generateNewAnnotation(simpleContentExtension.getAnnotation()));
                        newSimpleContentExtension.setId(getID(simpleContentExtension.getId()));
                        ((SimpleContentType) newContent).setInheritance(simpleContentExtension);
                    }
                } else {

                    // Create new SimpleContentType
                    newContent = new SimpleContentType();

                    // Get new base type
                    Type newBaseType = generateNewSimpleType((SimpleType) simpleContentExtension.getBase(), getTypeName(simpleContentExtension.getBase()));

                    // Create new SimpleContent extension with new base
                    SimpleContentExtension newSimpleContentExtension = new SimpleContentExtension(outputSchema.getTypeSymbolTable().getReference(newBaseType.getName()));

                    // Set new id and annotation in new extension
                    newSimpleContentExtension.setAnnotation(generateNewAnnotation(simpleContentExtension.getAnnotation()));
                    newSimpleContentExtension.setId(getID(simpleContentExtension.getId()));

                    // Get list of all attribute particles contained in the old extension
                    LinkedList<AttributeParticle> oldAttributeParticles = simpleContentExtension.getAttributes();

                    // Create for each attribute particle a new attribute particle and add it to the new extension
                    for (Iterator<AttributeParticle> it = oldAttributeParticles.iterator(); it.hasNext();) {
                        AttributeParticle oldAttributeParticle = it.next();

                        // Add new create attribute particle to the new extension
                        newSimpleContentExtension.addAttribute(attributeParticleDifferenceGenerator.generateNewAttributeParticle(oldAttributeParticle));
                    }
                    ((SimpleContentType) newContent).setInheritance(simpleContentExtension);
                }
            }
        }

        // Set new fiels for the new ComplexContentType
        newContent.setAnnotation(generateNewAnnotation(minuendComplexType.getContent().getAnnotation()));
        newContent.setId(getID(minuendComplexType.getContent().getId()));

        // Create new complexType with new content
        ComplexType newComplexType = new ComplexType(newTypeName, newContent);

        // Set id and annotation for the new type
        newComplexType.setAnnotation(generateNewAnnotation(minuendComplexType.getAnnotation()));
        newComplexType.setId(getID(minuendComplexType.getId()));

        // Check if the minuend complexType is anonymous
        newComplexType.setIsAnonymous(minuendComplexType.isAnonymous());

        // Set value of "mixed" attribute to the same value of the minuend type "mixed" attribute
        newComplexType.setMixed(minuendComplexType.getMixed());

        // Get list of all attribute particles contained in the old complexType
        LinkedList<AttributeParticle> oldAttributeParticles = minuendComplexType.getAttributes();

        // Create for each attribute particle a new attribute particle and add it to the new complexType
        for (Iterator<AttributeParticle> it = oldAttributeParticles.iterator(); it.hasNext();) {
            AttributeParticle oldAttributeParticle = it.next();

            // Add new create attribute particle to the new complex Type
            newComplexType.addAttribute(attributeParticleDifferenceGenerator.generateNewAttributeParticle(oldAttributeParticle));
        }

        // "block" attribute is present add "block" value to new type
        newComplexType.setBlockModifiers(getComplexTypeBlock(minuendComplexType));

        // "final" attribute is present add "final" value to new type
        newComplexType.setFinalModifiers(getComplexTypeFinal(minuendComplexType));

        // If minuend type is not mixed or the subtrahend type is not string the difference only contains the minuend type
        outputSchema.getTypeSymbolTable().updateOrCreateReference(newComplexType.getName(), newComplexType);

        // Add new type to the list of top-level types if it is not anonymous
        if (newComplexType != null && !newComplexType.isAnonymous()) {
            outputSchema.addType(outputSchema.getTypeSymbolTable().getReference(newComplexType.getName()));
        }
        return newComplexType;
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
     * Method creates a new simpleType copying a specified old simpleType. Old
     * and new simpleType are mostly identical, but the new simpleType is
     * registered in the new schema where the old simpleType has to be
     * registered in the old schema.
     *
     * @param oldSimpleType SimpleType for which a copy is generated.
     * @param typeName Name of the new type.
     * @return New simpleType which is a copy of the specified old simpleType.
     */
    public SimpleType generateNewSimpleType(SimpleType oldSimpleType, String typeName) {

        // If type is build-in datatype generate new build-in datatype
        if (isBuiltInDatatype(typeName)) {
            SimpleType newSimpleType = new SimpleType(typeName, null);
            outputSchema.getTypeSymbolTable().updateOrCreateReference(newSimpleType.getName(), newSimpleType);
            return newSimpleType;
        }

        // Create new simpleType with new inheritance
        SimpleType newSimpleType = new SimpleType(typeName, generateNewSimpleTypeInheritance(oldSimpleType.getInheritance()));

        // "final" attribute is present add "final" value to new simpleType
        newSimpleType.setFinalModifiers(getSimpleTypeFinal(oldSimpleType));

        // Check wether the new simpleType has to be anonymous
        newSimpleType.setIsAnonymous(oldSimpleType.isAnonymous());

        // Set id and annotation for the new simple type
        newSimpleType.setAnnotation(generateNewAnnotation(oldSimpleType.getAnnotation()));
        newSimpleType.setId(getID(oldSimpleType.getId()));

        // Update type SymbolTable and return new simpleType
        outputSchema.getTypeSymbolTable().updateOrCreateReference(newSimpleType.getName(), newSimpleType);

        // Add new type to the list of top-level types if it is not anonymous
        if (newSimpleType != null && !newSimpleType.isAnonymous()) {
            outputSchema.addType(outputSchema.getTypeSymbolTable().getReference(newSimpleType.getName()));
        }
        return newSimpleType;
    }

    /**
     * Create new type for a given minuend type and a specified subtrahend type.
     * The new type should contain every content the minuend type defines
     * without the content the subtrahend type defines. Both types can be either
     * complexTypes or simpleTypes.
     *
     * @param minuendType Type from which the subtrahend type is removed from.
     * @param subtrahendType Type is removed from the subtrahend type.
     * @param newTypeName Name of the new type.
     * @param elementTypeMap Map mapping elements to their new types.
     * @param elementOldTypeMap Map mapping elements to their old types.
     * @return New type which is basically the minuend type without the content
     * of the subtrahend type.
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
    public Type generateNewType(Type minuendType, Type subtrahendType, String newTypeName, LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap, LinkedHashMap<String, SymbolTableRef<Type>> elementOldTypeMap) throws EmptySubsetParticleStateFieldException, NonDeterministicFiniteAutomataException, UniqueParticleAttributionViolationException, EmptyProductParticleStateFieldException, NoUniqueStateNumbersException, NoDestinationStateFoundException, NotSupportedParticleAutomatonException {
        Type newType = null;
        this.elementTypeMap = elementTypeMap;
        this.elementOldTypeMap = elementOldTypeMap;

        // Check if minuend type is simpleType
        if (minuendType instanceof SimpleType) {

            // Minuend type is a simpleType
            SimpleType minuendSimpleType = (SimpleType) minuendType;

            if (subtrahendType instanceof ComplexType) {

                // If the minuend type is a simpleType and the subtrahend type is a complexType
                ComplexType subtrahendComplexType = (ComplexType) subtrahendType;
                newType = generateNewSimpleType(minuendSimpleType, subtrahendComplexType, newTypeName);

            } else if (subtrahendType instanceof SimpleType) {

                // If both types are simpleTypes
                SimpleType subtrahendSimpleType = (SimpleType) subtrahendType;
                newType = generateNewSimpleType(minuendSimpleType, subtrahendSimpleType, newTypeName);
            } else {

                // Create new simpleType
                newType = generateNewSimpleType(minuendSimpleType, newTypeName);
            }
        } else if (minuendType instanceof ComplexType) {

            // Minuend type is a complexType
            ComplexType minuendComplexType = (ComplexType) minuendType;

            if (subtrahendType instanceof ComplexType) {

                // If both types are complexTypes
                ComplexType subtrahendComplexType = (ComplexType) subtrahendType;
                newType = createNewComplexType(minuendComplexType, subtrahendComplexType, newTypeName);

            } else if (subtrahendType instanceof SimpleType) {

                // If the minuend type is a complexType and the subtrahend type is a simpleType
                SimpleType subtrahendSimpleType = (SimpleType) subtrahendType;
                newType = createNewComplexType(minuendComplexType, subtrahendSimpleType, newTypeName);
            } else {

                // Create new complexType
                newType = createNewComplexType(minuendComplexType, newTypeName);
            }
        }
        return newType;
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

            // Return new created annotation
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
     * Create new simpleType inheritance (union, list, restriction) from a
     * specified simpleType inheritance by copying the old simpleType
     * inheritance.
     *
     * @param oldSimpleTypeInheritance Old simpleType inheritance which is
     * copied to create the new simpleType inheritance.
     * @param oldSchema XSDSchema containing the old simpleType inheritance.
     * @return New simpleType inheritance created by copying the old simpleType
     * inheritance.
     */
    private SimpleTypeInheritance generateNewSimpleTypeInheritance(SimpleTypeInheritance oldSimpleTypeInheritance) {

        // Create new simpleType inheritance
        SimpleTypeInheritance newSimpleTypeInheritance = null;

        // Check if the old inheritance is a list
        if (oldSimpleTypeInheritance instanceof SimpleContentList) {

            // Create new simpleType list
            SimpleContentList oldSimpleContentList = (SimpleContentList) oldSimpleTypeInheritance;
            newSimpleTypeInheritance = createNewSimpleContentList(oldSimpleContentList);

        // Check if the old inheritance is a restriction
        } else if (oldSimpleTypeInheritance instanceof SimpleContentRestriction) {

            // Create new simpleType restriction
            SimpleContentRestriction oldSimpleContentRestriction = (SimpleContentRestriction) oldSimpleTypeInheritance;
            newSimpleTypeInheritance = createNewSimpleContentRestriction(oldSimpleContentRestriction);

        // Check if the old inheritance is a union
        } else if (oldSimpleTypeInheritance instanceof SimpleContentUnion) {

            // Create new simpleType union
            SimpleContentUnion oldSimpleContentUnion = (SimpleContentUnion) oldSimpleTypeInheritance;
            newSimpleTypeInheritance = createNewSimpleContentUnion(oldSimpleContentUnion);
        }

        // Return new simpleType inheritance
        return newSimpleTypeInheritance;
    }

    /**
     * Create new list by copying the specified old list. The new list will
     * contain a new anonymous base type or a reference to a type contained in
     * another schema, this is achieved by copying the SymbolTableRef from the
     * old schema.
     *
     * @param oldSimpleContentList Old list used to create the new list.
     * @param oldSchema XSDSchema containing the old list.
     * @return New list which is a copy of the old list.
     */
    private SimpleContentList createNewSimpleContentList(SimpleContentList oldSimpleContentList) {

        // Prepare base type for the current component
        SimpleType newSimpleType = generateNewSimpleType((SimpleType) oldSimpleContentList.getBase(), getTypeName(oldSimpleContentList.getBase()));

        // Create new list with new base type
        SimpleContentList newSimpleContentList = new SimpleContentList(outputSchema.getTypeSymbolTable().getReference(newSimpleType.getName()));

        // Set new id and annotation in new list
        newSimpleContentList.setAnnotation(generateNewAnnotation(oldSimpleContentList.getAnnotation()));
        newSimpleContentList.setId(getID(oldSimpleContentList.getId()));

        // Return new list
        return newSimpleContentList;
    }

    /**
     * Create new restriction by copying the specified old restriction. The new
     * restriction will contain a new anonymous type, a new base type and new
     * "facets" used to restrict the new type.
     *
     * @param oldSimpleContentRestriction Old restriction used to create the
     * new restriction.
     * @param oldSchema XSDSchema containing the old restriction.
     * @return New restriction which is a copy of the old restriction.
     */
    private SimpleContentRestriction createNewSimpleContentRestriction(SimpleContentRestriction oldSimpleContentRestriction) {

        // Prepare base type for the current component
        SimpleType newBase = generateNewSimpleType((SimpleType) oldSimpleContentRestriction.getBase(), getTypeName(oldSimpleContentRestriction.getBase()));

        // Create new anonymous simpleType if old anonymous simpleType is present
        SimpleType newAnonymousSimpleType = null;
        if (oldSimpleContentRestriction.getAnonymousSimpleType() != null) {

            // Store new anonymous simpleType for later usage
            newAnonymousSimpleType = (SimpleType) generateNewSimpleType(oldSimpleContentRestriction.getAnonymousSimpleType(), getTypeName(oldSimpleContentRestriction.getAnonymousSimpleType()));
        }

        // Create new restriction with new base and new anonymous type
        SimpleContentRestriction newSimpleContentRestriction = new SimpleContentRestriction(outputSchema.getTypeSymbolTable().getReference(newBase.getName()), newAnonymousSimpleType);

        // Set new id and annotation in new restiction
        newSimpleContentRestriction.setAnnotation(generateNewAnnotation(oldSimpleContentRestriction.getAnnotation()));
        newSimpleContentRestriction.setId(getID(oldSimpleContentRestriction.getId()));

        // Get list of all attribute particles contained in the old restriction
        LinkedList<AttributeParticle> oldAttributeParticles = oldSimpleContentRestriction.getAttributes();

        // Create for each attribute particle a new attribute particle and add it to the new restriction
        for (Iterator<AttributeParticle> it = oldAttributeParticles.iterator(); it.hasNext();) {
            AttributeParticle oldAttributeParticle = it.next();

            // Add new create attribute particle to the new restriction
            newSimpleContentRestriction.addAttribute(attributeParticleDifferenceGenerator.generateNewAttributeParticle(oldAttributeParticle));
        }

        // Set "facets" of the new restriction
        newSimpleContentRestriction.setEnumeration(new LinkedList<String>(oldSimpleContentRestriction.getEnumeration()));

        if (oldSimpleContentRestriction.getFractionDigits() != null) {
            SimpleContentFixableRestrictionProperty<Integer> newFractionDigits = new SimpleContentFixableRestrictionProperty<Integer>(oldSimpleContentRestriction.getFractionDigits().getValue(), oldSimpleContentRestriction.getFractionDigits().getFixed());
            newSimpleContentRestriction.setFractionDigits(newFractionDigits);
        }
        if (oldSimpleContentRestriction.getLength() != null) {
            SimpleContentFixableRestrictionProperty<Integer> newLength = new SimpleContentFixableRestrictionProperty<Integer>(oldSimpleContentRestriction.getLength().getValue(), oldSimpleContentRestriction.getLength().getFixed());
            newSimpleContentRestriction.setLength(newLength);
        }
        if (oldSimpleContentRestriction.getMaxLength() != null) {
            SimpleContentFixableRestrictionProperty<Integer> newMaxLength = new SimpleContentFixableRestrictionProperty<Integer>(oldSimpleContentRestriction.getMaxLength().getValue(), oldSimpleContentRestriction.getMaxLength().getFixed());
            newSimpleContentRestriction.setMaxLength(newMaxLength);
        }
        if (oldSimpleContentRestriction.getMinLength() != null) {
            SimpleContentFixableRestrictionProperty<Integer> newMinLength = new SimpleContentFixableRestrictionProperty<Integer>(oldSimpleContentRestriction.getMinLength().getValue(), oldSimpleContentRestriction.getMinLength().getFixed());
            newSimpleContentRestriction.setMinLength(newMinLength);
        }
        if (oldSimpleContentRestriction.getTotalDigits() != null) {
            SimpleContentFixableRestrictionProperty<Integer> newTotalDigits = new SimpleContentFixableRestrictionProperty<Integer>(oldSimpleContentRestriction.getTotalDigits().getValue(), oldSimpleContentRestriction.getTotalDigits().getFixed());
            newSimpleContentRestriction.setTotalDigits(newTotalDigits);
        }
        if (oldSimpleContentRestriction.getMaxExclusive() != null) {
            SimpleContentFixableRestrictionProperty<String> newMaxExclusive = new SimpleContentFixableRestrictionProperty<String>(oldSimpleContentRestriction.getMaxExclusive().getValue(), oldSimpleContentRestriction.getMaxExclusive().getFixed());
            newSimpleContentRestriction.setMaxExclusive(newMaxExclusive);
        }
        if (oldSimpleContentRestriction.getMaxInclusive() != null) {
            SimpleContentFixableRestrictionProperty<String> newMaxInclusive = new SimpleContentFixableRestrictionProperty<String>(oldSimpleContentRestriction.getMaxInclusive().getValue(), oldSimpleContentRestriction.getMaxInclusive().getFixed());
            newSimpleContentRestriction.setMaxInclusive(newMaxInclusive);
        }
        if (oldSimpleContentRestriction.getMinExclusive() != null) {
            SimpleContentFixableRestrictionProperty<String> newMinExclusive = new SimpleContentFixableRestrictionProperty<String>(oldSimpleContentRestriction.getMinExclusive().getValue(), oldSimpleContentRestriction.getMinExclusive().getFixed());
            newSimpleContentRestriction.setMinExclusive(newMinExclusive);
        }
        if (oldSimpleContentRestriction.getMinInclusive() != null) {
            SimpleContentFixableRestrictionProperty<String> newMinInclusive = new SimpleContentFixableRestrictionProperty<String>(oldSimpleContentRestriction.getMinInclusive().getValue(), oldSimpleContentRestriction.getMinInclusive().getFixed());
            newSimpleContentRestriction.setMinInclusive(newMinInclusive);
        }
        if (oldSimpleContentRestriction.getPattern() != null) {
            SimpleContentRestrictionProperty<String> newPattern = new SimpleContentRestrictionProperty<String>(oldSimpleContentRestriction.getPattern().getValue());
            newSimpleContentRestriction.setPattern(newPattern);
        }
        if (oldSimpleContentRestriction.getWhitespace() != null) {
            SimpleContentFixableRestrictionProperty<SimpleContentPropertyWhitespace> newWhitespace = new SimpleContentFixableRestrictionProperty<SimpleContentPropertyWhitespace>(oldSimpleContentRestriction.getWhitespace().getValue(), oldSimpleContentRestriction.getWhitespace().getFixed());
            newSimpleContentRestriction.setWhitespace(newWhitespace);
        }

        // Return new restriction
        return newSimpleContentRestriction;
    }

    /**
     * Create new union by copying the specified old union. The new union will
     * contain new member types or referes to member types contained in other
     * schemata.
     *
     * @param createNewSimpleContentUnion Old union used to create the new
     * union.
     * @param oldSchema XSDSchema containing the old union.
     * @return New union which is a copy of the old union.
     */
    private SimpleTypeInheritance createNewSimpleContentUnion(SimpleContentUnion oldSimpleContentUnion) {

        // Create new list for all new member types
        LinkedList<SymbolTableRef<Type>> newMemberTypes = new LinkedList<SymbolTableRef<Type>>();

        // Check for each old member type the new type SymbolTable
        for (Iterator<SymbolTableRef<Type>> it = oldSimpleContentUnion.getAllMemberTypes().iterator(); it.hasNext();) {
            SymbolTableRef<Type> symbolTableRef = it.next();

            // Prepare member types for the new union component
            SimpleType newSimpleType = generateNewSimpleType((SimpleType) symbolTableRef.getReference(), getTypeName(symbolTableRef.getReference()));

            // Get new member type from the type SymbolTable of the new schema
            newMemberTypes.add(outputSchema.getTypeSymbolTable().getReference(newSimpleType.getName()));
        }

        // Create new union with new member types
        SimpleContentUnion newSimpleContentUnion = new SimpleContentUnion(newMemberTypes);

        // Set new id and annotation in new union
        newSimpleContentUnion.setAnnotation(generateNewAnnotation(oldSimpleContentUnion.getAnnotation()));
        newSimpleContentUnion.setId(getID(oldSimpleContentUnion.getId()));

        // Return new union
        return newSimpleContentUnion;
    }

    // Enumeration containing all build-in datatypes
    private enum BuiltInDatatypes {

        STRING("{http://www.w3.org/2001/XMLSchema}string"),
        BOOLEAN("{http://www.w3.org/2001/XMLSchema}boolean"),
        DECIMAL("{http://www.w3.org/2001/XMLSchema}decimal"),
        FLOAT("{http://www.w3.org/2001/XMLSchema}float"),
        DOUBLE("{http://www.w3.org/2001/XMLSchema}double"),
        DURATION("{http://www.w3.org/2001/XMLSchema}duration"),
        DATETIME("{http://www.w3.org/2001/XMLSchema}dateTime"),
        TIME("{http://www.w3.org/2001/XMLSchema}time"),
        DATE("{http://www.w3.org/2001/XMLSchema}date"),
        GYEARMONTH("{http://www.w3.org/2001/XMLSchema}gYearMonth"),
        GYEAR("{http://www.w3.org/2001/XMLSchema}gYear"),
        GMONTHDAY("{http://www.w3.org/2001/XMLSchema}gMonthDay"),
        GDAY("{http://www.w3.org/2001/XMLSchema}gDay"),
        GMONTH("{http://www.w3.org/2001/XMLSchema}gMonth"),
        HEXBINARY("{http://www.w3.org/2001/XMLSchema}hexBinary"),
        BASE64BINARY("{http://www.w3.org/2001/XMLSchema}base64Binary"),
        ANYURI("{http://www.w3.org/2001/XMLSchema}anyURI"),
        QNAME("{http://www.w3.org/2001/XMLSchema}QName"),
        NOTATION("{http://www.w3.org/2001/XMLSchema}NOTATION"),
        NORMALIZEDSTRING("{http://www.w3.org/2001/XMLSchema}normalizedString"),
        TOKEN("{http://www.w3.org/2001/XMLSchema}token"),
        LANGUAGE("{http://www.w3.org/2001/XMLSchema}language"),
        NMTOKEN("{http://www.w3.org/2001/XMLSchema}NMTOKEN"),
        NMTOKENS("{http://www.w3.org/2001/XMLSchema}NMTOKENS"),
        NAME("{http://www.w3.org/2001/XMLSchema}Name"),
        NCNAME("{http://www.w3.org/2001/XMLSchema}NCName"),
        ID("{http://www.w3.org/2001/XMLSchema}ID"),
        IDREF("{http://www.w3.org/2001/XMLSchema}IDREF"),
        IDREFS("{http://www.w3.org/2001/XMLSchema}IDREFS"),
        ENTITY("{http://www.w3.org/2001/XMLSchema}ENTITY"),
        ENTITIES("{http://www.w3.org/2001/XMLSchema}ENTITIES"),
        INTEGER("{http://www.w3.org/2001/XMLSchema}integer"),
        NONPOSITIVEINTEGER("{http://www.w3.org/2001/XMLSchema}nonPositiveInteger"),
        NEGATIVEINTEGER("{http://www.w3.org/2001/XMLSchema}negativeInteger"),
        LONG("{http://www.w3.org/2001/XMLSchema}long"),
        INT("{http://www.w3.org/2001/XMLSchema}int"),
        SHORT("{http://www.w3.org/2001/XMLSchema}short"),
        BYTE("{http://www.w3.org/2001/XMLSchema}byte"),
        NONNEGATIVEINTEGER("{http://www.w3.org/2001/XMLSchema}nonNegativeInteger"),
        UNSIGNEDLONG("{http://www.w3.org/2001/XMLSchema}unsignedLong"),
        UNSIGNEDINT("{http://www.w3.org/2001/XMLSchema}unsignedInt"),
        UNSIGNEDSHORT("{http://www.w3.org/2001/XMLSchema}unsignedShort"),
        UNSIGNEDBYTE("{http://www.w3.org/2001/XMLSchema}unsignedByte"),
        POSITIVEINTEGER("{http://www.w3.org/2001/XMLSchema}positiveInteger"),
        ANYTYPE("{http://www.w3.org/2001/XMLSchema}anyType"),
        ANYSIMPLETYPE("{http://www.w3.org/2001/XMLSchema}anySimpleType");
        private final String uri;

        BuiltInDatatypes(String uri) {
            this.uri = uri;
        }

        public String uri() {
            return uri;
        }
    }

    /**
     * Check if the specified name belongs to a build-in datatype.
     *
     * @param name Name of the type, which may be a build-in datatype.
     * @return <tt>true</tt> if the name belongs to a build-in datatype, else
     * <tt>false</tt>.
     */
    public boolean isBuiltInDatatype(String name) {

        // Compare each build-in datatype with the specified name
        for (BuiltInDatatypes builtInDatatypes : BuiltInDatatypes.values()) {

            // If the build-in datatype has the same name return true
            if (builtInDatatypes.uri().equals(name)) {
                return true;
            }
        }

        // If no build-in datatype with this name exist return false
        return false;
    }

    /**
     * Create new simpleContent inheritance (extension, restriction) from a
     * specified simpleContent inheritance by copying the old simpleContent
     * inheritance.
     *
     * @param oldSimpleContentInheritance Old simpleContent inheritance which is
     * copied to create the new simpleContent inheritance.
     * @param oldSchema XSDSchema containing the old simpleContent inheritance.
     * @return New simpleContent inheritance created by copying the old
     * simpleContent inheritance.
     */
    private SimpleContentInheritance createNewSimpleContentInheritance(SimpleContentInheritance oldSimpleContentInheritance) {

        // Create new simpleContent inheritance
        SimpleContentInheritance newSimpleContentInheritance = null;

        // Check if the old inheritance is an extension
        if (oldSimpleContentInheritance instanceof SimpleContentExtension) {

            // Create new simpleContent extension
            SimpleContentExtension oldSimpleContentExtension = (SimpleContentExtension) oldSimpleContentInheritance;

            // Prepare base type for the current component
            generateNewSimpleType((SimpleType) oldSimpleContentExtension.getBase(), getTypeName(oldSimpleContentExtension.getBase()));

            // Create new SimpleContent extension with new base
            SimpleContentExtension newSimpleContentExtension = new SimpleContentExtension(outputSchema.getTypeSymbolTable().getReference(oldSimpleContentExtension.getBase().getName()));

            // Set new id and annotation in new extension
            newSimpleContentExtension.setAnnotation(generateNewAnnotation(oldSimpleContentExtension.getAnnotation()));
            newSimpleContentExtension.setId(getID(oldSimpleContentExtension.getId()));

            // Get list of all attribute particles contained in the old extension
            LinkedList<AttributeParticle> oldAttributeParticles = oldSimpleContentExtension.getAttributes();

            // Create for each attribute particle a new attribute particle and add it to the new extension
            for (Iterator<AttributeParticle> it = oldAttributeParticles.iterator(); it.hasNext();) {
                AttributeParticle oldAttributeParticle = it.next();

                // Add new create attribute particle to the new extension
                newSimpleContentExtension.addAttribute(attributeParticleDifferenceGenerator.generateNewAttributeParticle(oldAttributeParticle));
            }
            newSimpleContentInheritance = newSimpleContentExtension;

        // Check if the old inheritance is a restriction
        } else if (oldSimpleContentInheritance instanceof SimpleContentRestriction) {

            // Create new simpleContent restriction
            SimpleContentRestriction oldSimpleContentRestriction = (SimpleContentRestriction) oldSimpleContentInheritance;
            newSimpleContentInheritance = createNewSimpleContentRestriction(oldSimpleContentRestriction);
        }

        // Return new simpleContent inheritance
        return newSimpleContentInheritance;
    }

    /**
     * Generates a new final value from a given complexType. The final
     * attribute of XML XSDSchema restricts type inheritance, so when computing the
     * difference of types the minuend final value is taken.
     *
     * @param complexType ComplexType contains a final value which is used to
     * compute the new final value.
     * @return Set of ComplexTypeInheritanceModifier, which represents the new
     * final value.
     */
    private HashSet<ComplexTypeInheritanceModifier> getComplexTypeFinal(ComplexType complexType) {

        // Generate new "final" attribute
        HashSet<ComplexTypeInheritanceModifier> finalValue = new HashSet<ComplexTypeInheritanceModifier>();

        // If "final" attribute is present add contained Final value from the "final" attribute of the new complexType
        if (complexType.getFinalModifiers() != null) {

            // Check if "extension" is contained and if add "extension" from "final" attribute of the new complexType
            if (complexType.getFinalModifiers().contains(ComplexTypeInheritanceModifier.Extension)) {
                finalValue.add(ComplexTypeInheritanceModifier.Extension);
            }
            // Check if "restriction" is contained and if add "restriction" from "final" attribute of the new complexType
            if (!complexType.getFinalModifiers().contains(ComplexTypeInheritanceModifier.Restriction)) {
                finalValue.add(ComplexTypeInheritanceModifier.Restriction);
            }
        } else if (typeOldSchemaMap.get(complexType).getFinalDefaults() != null) {

            // Add Final values contained in the default value
            if (typeOldSchemaMap.get(complexType).getFinalDefaults().contains(FinalDefault.extension)) {
                finalValue.add(ComplexTypeInheritanceModifier.Extension);
            }
            if (typeOldSchemaMap.get(complexType).getFinalDefaults().contains(FinalDefault.restriction)) {
                finalValue.add(ComplexTypeInheritanceModifier.Restriction);
            }
        }

        // Generate a set containing FinalDefault values for the Final values contained in the set of the new complexType
        LinkedHashSet<XSDSchema.FinalDefault> finalDefaults = new LinkedHashSet<XSDSchema.FinalDefault>();

        // Check if "extension" is contained and if add "extension"
        if (finalValue.contains(ComplexTypeInheritanceModifier.Extension)) {
            finalDefaults.add(XSDSchema.FinalDefault.extension);
        }
        // Check if "restriction" is contained and if add "restriction"
        if (finalValue.contains(ComplexTypeInheritanceModifier.Restriction)) {
            finalDefaults.add(XSDSchema.FinalDefault.restriction);
        }

        // Check if new "final" values is different than the default value of the output schema
        if (!(outputSchema.getFinalDefaults().containsAll(finalDefaults) && finalDefaults.containsAll(outputSchema.getFinalDefaults()))) {
            return finalValue;
        } else {

            // If output schema has the same "finalDefault" value as the new complexTypes "final" attribute return null
            return null;
        }
    }

    /**
     * Generates a new final value from a given simpleType. The final attribute
     * of XML XSDSchema restricts type inheritance, so when computing the
     * difference of types the minuend final value is computed.
     *
     * @param simpleType SimpleType contains a final value which is used to
     * compute the new final value.
     * @return Set of SimpleTypeInheritanceModifier, which represents the new
     * final value.
     */
    private HashSet<SimpleTypeInheritanceModifier> getSimpleTypeFinal(SimpleType simpleType) {

        // Generate new "final" attribute
        HashSet<SimpleTypeInheritanceModifier> finalValue = new HashSet<SimpleTypeInheritanceModifier>();

        // If "final" attribute is present add contained SimpleTypeInheritanceModifier value from the "final" attribute of the new simpleType
        if (simpleType.getFinalModifiers() != null) {

            // Check if "list" is contained and if add "list" from "final" attribute of the new simpleType
            if (simpleType.getFinalModifiers().contains(SimpleTypeInheritanceModifier.List)) {
                finalValue.add(SimpleTypeInheritanceModifier.List);
            }
            // Check if "restriction" is contained and if add "restriction" from "final" attribute of the new simpleType
            if (simpleType.getFinalModifiers().contains(SimpleTypeInheritanceModifier.Restriction)) {
                finalValue.add(SimpleTypeInheritanceModifier.Restriction);
            }
            // Check if "union" is contained and if add "union" from "final" attribute of the new simpleType
            if (simpleType.getFinalModifiers().contains(SimpleTypeInheritanceModifier.Union)) {
                finalValue.add(SimpleTypeInheritanceModifier.Union);
            }
        } else if (typeOldSchemaMap.get(simpleType).getFinalDefaults() != null) {

            // Add SimpleTypeInheritanceModifier values contained in the default value
            if (typeOldSchemaMap.get(simpleType).getFinalDefaults().contains(FinalDefault.list)) {
                finalValue.add(SimpleTypeInheritanceModifier.List);
            }
            if (typeOldSchemaMap.get(simpleType).getFinalDefaults().contains(FinalDefault.restriction)) {
                finalValue.add(SimpleTypeInheritanceModifier.Restriction);
            }
            if (typeOldSchemaMap.get(simpleType).getFinalDefaults().contains(FinalDefault.union)) {
                finalValue.add(SimpleTypeInheritanceModifier.Union);
            }
        }

        // If the type is a build-in datatype
        if (isBuiltInDatatype(simpleType.getName())) {
            return null;
        }

        // Generate a set containing FinalDefault values for the Final values contained in the set of the new simpleType
        LinkedHashSet<XSDSchema.FinalDefault> finalDefaults = new LinkedHashSet<XSDSchema.FinalDefault>();

        // Check if "list" is contained and if add "list"
        if (finalValue.contains(SimpleTypeInheritanceModifier.List)) {
            finalDefaults.add(XSDSchema.FinalDefault.list);
        }
        // Check if "restriction" is contained and if add "restriction"
        if (finalValue.contains(SimpleTypeInheritanceModifier.Restriction)) {
            finalDefaults.add(XSDSchema.FinalDefault.restriction);
        }
        // Check if "union" is contained and if add "union"
        if (finalValue.contains(SimpleTypeInheritanceModifier.Union)) {
            finalDefaults.add(XSDSchema.FinalDefault.union);
        }

        // Check if new "final" values is different than the default value of the output schema
        if (!(outputSchema.getFinalDefaults().containsAll(finalDefaults) && finalDefaults.containsAll(outputSchema.getFinalDefaults()))) {
            return finalValue;
        } else {

            // If output schema has the same "finalDefault" value as the new simpleTypes "final" attribute return null
            return null;
        }
    }

    /**
     * Generates a new "block" value from a given complexType. The "block"
     * attribute of XML XSDSchema restricts the use of derived types, so when
     * computing the difference the minuend "block" value is computed.
     *
     * @param complexType ComplexType contains a "block" value, which is used to
     * compute the new "block" attribute.
     * @return Set of ComplexTypeInheritanceModifier, which represents the new
     * "block" attribute.
     */
    private HashSet<ComplexTypeInheritanceModifier> getComplexTypeBlock(ComplexType complexType) {

        // Generate new "block" attribute
        HashSet<ComplexTypeInheritanceModifier> newBlock = new HashSet<ComplexTypeInheritanceModifier>();

        // If "block" attribute is present add contained ComplexTypeInheritanceModifier value from the "block" attribute of the new complexType
        if (complexType.getBlockModifiers() != null) {

            // Check if "extension" is contained and if add "extension" from "block" attribute of the new complexType
            if (complexType.getBlockModifiers().contains(ComplexTypeInheritanceModifier.Extension)) {
                newBlock.add(ComplexTypeInheritanceModifier.Extension);
            }
            // Check if "restriction" is contained and if add "restriction" from "block" attribute of the new complexType
            if (complexType.getBlockModifiers().contains(ComplexTypeInheritanceModifier.Restriction)) {
                newBlock.add(ComplexTypeInheritanceModifier.Restriction);
            }
        } else if (typeOldSchemaMap.get(complexType).getBlockDefaults() != null) {

            // Add Block values contained in the default value
            if (typeOldSchemaMap.get(complexType).getBlockDefaults().contains(BlockDefault.extension)) {
                newBlock.add(ComplexTypeInheritanceModifier.Extension);
            }
            if (typeOldSchemaMap.get(complexType).getBlockDefaults().contains(BlockDefault.restriction)) {
                newBlock.add(ComplexTypeInheritanceModifier.Restriction);
            }
        }

        // Generate a set containing BlockDefault values for the Block values contained in the set of the new complexType
        LinkedHashSet<XSDSchema.BlockDefault> blockDefaults = new LinkedHashSet<XSDSchema.BlockDefault>();

        // Check if "extension" is contained and if add "extension"
        if (newBlock.contains(ComplexTypeInheritanceModifier.Extension)) {
            blockDefaults.add(XSDSchema.BlockDefault.extension);
        }
        // Check if "restriction" is contained and if add "restriction"
        if (newBlock.contains(ComplexTypeInheritanceModifier.Restriction)) {
            blockDefaults.add(XSDSchema.BlockDefault.restriction);
        }

        // Check if new "block" values is different than the default value of the output schema
        if (!(outputSchema.getBlockDefaults().containsAll(blockDefaults) && blockDefaults.containsAll(outputSchema.getBlockDefaults()))) {
            return newBlock;
        } else {

            // If output schema has the same "blockDefault" value as the new complexTypes "block" attribute return null
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

    /**
     * This method generates for a specified SimpleTypeInheritanceContainer a new
     * SimpleType structure consisting of SimpleTypes with SimpleContentRestrictions.
     * In order to construct the new SimpleTypes the field contained in the
     * container are used to build new restrictions. If the container contains
     * more than one pattern for each pattern a new SimpleType is constructed
     * which contains the last SimpleType as base type.
     *
     * The specified SimpleTypeInheritanceContainer must contain a restiction
     * field else the result is null.
     *
     * @param simpleTypeInheritanceContainer Container which contains information
     * about an inheritance path.
     * @return Null if no restriction fields are present in the
     * SimpleTypeInheritanceContainer else the last SimpleType which was constructed.
     * This SimpleType is a child of all other generated SimpleTypes.
     */
    private SimpleType generateNewGlobalSimpleTypeWithRestriction(SimpleTypeInheritanceContainer simpleTypeInheritanceContainer) {

        // Check if restriction is contained in inheritance information
        if (simpleTypeInheritanceContainer.isHasSimpleTypeRestriction()) {

            // Create new restriction with base type as base
            String baseName = simpleTypeInheritanceContainer.getBase().getName();
            SimpleContentRestriction newSimpleContentRestriction = new SimpleContentRestriction(outputSchema.getTypeSymbolTable().getReference(baseName));

            // Set enumeration and length
            newSimpleContentRestriction.setEnumeration(simpleTypeInheritanceContainer.getSimpleTypeEnumeration());
            newSimpleContentRestriction.setLength(simpleTypeInheritanceContainer.getSimpleTypeLength());

            // If both maxExclusive and maxInclusive are present
            if (simpleTypeInheritanceContainer.getSimpleTypeMaxExclusive() != null && simpleTypeInheritanceContainer.getSimpleTypeMaxInclusive() != null) {

                // If maxExclusive is smaller or equal maxInclusive use maxExclusive else maxInclusive
                if (new Double(simpleTypeInheritanceContainer.getSimpleTypeMaxExclusive().getValue()) <= new Double(simpleTypeInheritanceContainer.getSimpleTypeMaxInclusive().getValue())) {
                    newSimpleContentRestriction.setMaxExclusive(simpleTypeInheritanceContainer.getSimpleTypeMaxExclusive());
                } else {
                    newSimpleContentRestriction.setMaxInclusive(simpleTypeInheritanceContainer.getSimpleTypeMaxInclusive());
                }
            } else {

                // Set maxExclusive or maxInclusive if both are not present at the same time
                newSimpleContentRestriction.setMaxExclusive(simpleTypeInheritanceContainer.getSimpleTypeMaxExclusive());
                newSimpleContentRestriction.setMaxInclusive(simpleTypeInheritanceContainer.getSimpleTypeMaxInclusive());
            }

            // If both minExclusive and minInclusive are present
            if (simpleTypeInheritanceContainer.getSimpleTypeMinExclusive() != null && simpleTypeInheritanceContainer.getSimpleTypeMinInclusive() != null) {

                // If minExclusive is greater or equal minInclusive use minExclusive else minInclusive
                if (new Double(simpleTypeInheritanceContainer.getSimpleTypeMinExclusive().getValue()) >= new Double(simpleTypeInheritanceContainer.getSimpleTypeMinInclusive().getValue())) {
                    newSimpleContentRestriction.setMinExclusive(simpleTypeInheritanceContainer.getSimpleTypeMinExclusive());
                } else {
                    newSimpleContentRestriction.setMinInclusive(simpleTypeInheritanceContainer.getSimpleTypeMinInclusive());
                }
            } else {

                // Set minExclusive or minInclusive if both are not present at the same time
                newSimpleContentRestriction.setMinExclusive(simpleTypeInheritanceContainer.getSimpleTypeMinExclusive());
                newSimpleContentRestriction.setMinInclusive(simpleTypeInheritanceContainer.getSimpleTypeMinInclusive());
            }

            // Set maxLength, minLength, totalDigits, fractionDigits and Whitespace
            newSimpleContentRestriction.setMaxLength(simpleTypeInheritanceContainer.getSimpleTypeMaxLength());
            newSimpleContentRestriction.setMinLength(simpleTypeInheritanceContainer.getSimpleTypeMinLength());
            newSimpleContentRestriction.setTotalDigits(simpleTypeInheritanceContainer.getSimpleTypeTotalDigits());
            newSimpleContentRestriction.setFractionDigits(simpleTypeInheritanceContainer.getSimpleTypeFractionDigits());
            newSimpleContentRestriction.setWhitespace(simpleTypeInheritanceContainer.getSimpleTypeWhitespace());

            // Create new type which contains the restriction
            String newTypeName = getRestrictionTypeName();
            SimpleType newSimpleType = new SimpleType(newTypeName, newSimpleContentRestriction, false);

            // Add type to schema
            outputSchema.getTypeSymbolTable().updateOrCreateReference(newSimpleType.getName(), newSimpleType);

            if (!outputSchema.getTypes().contains(newSimpleType)) {
                outputSchema.addType(outputSchema.getTypeSymbolTable().getReference(newSimpleType.getName()));
            }

            // Create new types for each pattern contained
            if (simpleTypeInheritanceContainer.getSimpleTypePatternList().size() >= 1) {

                for (Iterator<SimpleContentRestrictionProperty<String>> it = simpleTypeInheritanceContainer.getSimpleTypePatternList().iterator(); it.hasNext();) {
                    SimpleContentRestrictionProperty<String> pattern = it.next();
                    newSimpleContentRestriction.setPattern(pattern);

                    // If another pattern exists
                    if (it.hasNext()) {

                        // Create new restriction with base type as base
                        SimpleContentRestriction nextSimpleContentRestriction = new SimpleContentRestriction(outputSchema.getTypeSymbolTable().getReference(newSimpleType.getName()));
                        String nextTypeName = getRestrictionTypeName();

                        // Create new type which contains the restriction
                        SimpleType nextSimpleType = new SimpleType(nextTypeName, nextSimpleContentRestriction, false);

                        // Add type to schema
                        outputSchema.getTypeSymbolTable().updateOrCreateReference(nextSimpleType.getName(), nextSimpleType);

                        if (!outputSchema.getTypes().contains(nextSimpleType)) {
                            outputSchema.addType(outputSchema.getTypeSymbolTable().getReference(nextSimpleType.getName()));
                        }

                        // Update next restriction
                        newSimpleContentRestriction = nextSimpleContentRestriction;
                    }
                }
            }
            return newSimpleType;
        }
        return null;
    }

    /**
     * Constructs for a given SimpleType, which should be a SimpleType with
     * SimpleContentList inheritance, and a specified SimpleTypeInheritanceContainer
     * a new SimpleType, which restricts the given SimpleType. If the
     * SimpleTypeInheritanceContainer does not contain any restrictions for list
     * a null pointer is returned.
     *
     * SimpleType should contain a SimpleContentList inheritance if not null is
     * returned.
     *
     * @param simpleTypeInheritanceContainer Container which contains infomations
     * about list restriction.
     * @param simpleType SimpleType with SimpleContentList content, which is the
     * type from which the new type is derived.
     * @return Either a new SimpleType which restricts the specified Type or null.
     */
    private SimpleType generateNewGlobalSimpleTypeWithListRestriction(SimpleTypeInheritanceContainer simpleTypeInheritanceContainer, SimpleType simpleType) {

        // Check if List has facets
        if (!simpleTypeInheritanceContainer.getListEnumeration().isEmpty() ||
                simpleTypeInheritanceContainer.getListLength() != null ||
                simpleTypeInheritanceContainer.getListMaxLength() != null ||
                simpleTypeInheritanceContainer.getListMinLength() != null ||
                !simpleTypeInheritanceContainer.getListPatternList().isEmpty() ||
                simpleType.getInheritance() instanceof SimpleContentList) {

            // Generate new restriction and set new facets
            SimpleContentRestriction newSimpleContentRestriction = new SimpleContentRestriction(outputSchema.getTypeSymbolTable().getReference(simpleType.getName()));
            newSimpleContentRestriction.setEnumeration(simpleTypeInheritanceContainer.getListEnumeration());
            newSimpleContentRestriction.setLength(simpleTypeInheritanceContainer.getListLength());
            newSimpleContentRestriction.setMaxLength(simpleTypeInheritanceContainer.getListMaxLength());
            newSimpleContentRestriction.setMinLength(simpleTypeInheritanceContainer.getListMinLength());
            newSimpleContentRestriction.setWhitespace(simpleTypeInheritanceContainer.getListWhitespace());

            // Create new type to contain the restriction
            String newTypeName = getRestrictionTypeName();
            SimpleType newSimpleType = new SimpleType(newTypeName, newSimpleContentRestriction, false);
            outputSchema.getTypeSymbolTable().updateOrCreateReference(newSimpleType.getName(), newSimpleType);

            // Add new type as top-level type to the schema
            if (!outputSchema.getTypes().contains(newSimpleType)) {
                outputSchema.addType(outputSchema.getTypeSymbolTable().getReference(newSimpleType.getName()));
            }

            // Add new restrictions for each pattern contained in the container
            if (simpleTypeInheritanceContainer.getListPatternList().size() >= 1) {

                for (Iterator<SimpleContentRestrictionProperty<String>> it = simpleTypeInheritanceContainer.getListPatternList().iterator(); it.hasNext();) {
                    SimpleContentRestrictionProperty<String> pattern = it.next();
                    newSimpleContentRestriction.setPattern(pattern);

                    if (it.hasNext()) {

                        // Generate new restriction
                        SimpleContentRestriction nextSimpleContentRestriction = new SimpleContentRestriction(outputSchema.getTypeSymbolTable().getReference(newSimpleType.getName()));

                        // Create new type to contain the restriction
                        String nextTypeName = getRestrictionTypeName();
                        SimpleType nextSimpleType = new SimpleType(nextTypeName, nextSimpleContentRestriction, false);
                        outputSchema.getTypeSymbolTable().updateOrCreateReference(nextSimpleType.getName(), nextSimpleType);

                        // Add new type as top-level type to the schema
                        if (!outputSchema.getTypes().contains(nextSimpleType)) {
                            outputSchema.addType(outputSchema.getTypeSymbolTable().getReference(nextSimpleType.getName()));
                        }

                        // Set next restriction as new restriction
                        newSimpleContentRestriction = nextSimpleContentRestriction;
                    }
                }
            }
            return newSimpleType;
        }
        return null;
    }

    /**
     * For a given SimpleType a set of SimpleTypeInheritanceContainers is returned,
     * these contain among other things information about the base types. Each base
     * type is a ur base of the specified SimpleType which is derived for this
     * types by a chain of restrictions, lists and unions. Furthermore the
     * SimpleTypeInheritanceContainers contain information about how the base
     * types were restricted. This information is important to calculate the
     * difference of SimpleTypes.
     *
     * @param simpleType Type for which the informations are gathered.
     * @return Set of SimpleTypeInheritanceContainers, these are simple containers
     * to store informations which are found on the way to the base type.
     */
    private LinkedHashSet<SimpleTypeInheritanceContainer> getSimpleTypeInheritanceInformation(SimpleType simpleType) {

        // Generate new set containing inheritance informations for the given simpleType
        LinkedHashSet<SimpleTypeInheritanceContainer> simpleTypeInheritanceInformation = new LinkedHashSet<SimpleTypeInheritanceContainer>();

        // Check if simpleType has a list inheritance
        if (simpleType.getInheritance() instanceof SimpleContentList) {
            SimpleContentList simpleContentList = (SimpleContentList) simpleType.getInheritance();
            simpleTypeInheritanceInformation.addAll(getSimpleTypeInheritanceInformation((SimpleType) simpleContentList.getBase()));

            // For each found inheritance information in the list add list header
            for (Iterator<SimpleTypeInheritanceContainer> it = simpleTypeInheritanceInformation.iterator(); it.hasNext();) {
                SimpleTypeInheritanceContainer simpleTypeInheritanceContainer = it.next();
                simpleTypeInheritanceContainer.setHasList(true);
                simpleTypeInheritanceContainer.addSimpleContentList(simpleContentList);
            }
        } else if (simpleType.getInheritance() instanceof SimpleContentUnion) {
            SimpleContentUnion simpleContentUnion = (SimpleContentUnion) simpleType.getInheritance();
            LinkedList<SymbolTableRef<Type>> memberTypes = simpleContentUnion.getAllMemberTypes();

            // Get inheritance information for each member type of the union
            for (Iterator<SymbolTableRef<Type>> it = memberTypes.iterator(); it.hasNext();) {
                SymbolTableRef<Type> symbolTableRef = it.next();
                simpleTypeInheritanceInformation.addAll(getSimpleTypeInheritanceInformation((SimpleType) symbolTableRef.getReference()));
            }
        } else if (simpleType.getInheritance() instanceof SimpleContentRestriction) {
            SimpleContentRestriction simpleContentRestriction = (SimpleContentRestriction) simpleType.getInheritance();
            simpleTypeInheritanceInformation.addAll(getSimpleTypeInheritanceInformation((SimpleType) simpleContentRestriction.getBase()));

            // Get inheritance information for the contained base type and add facets to it
            for (Iterator<SimpleTypeInheritanceContainer> it = simpleTypeInheritanceInformation.iterator(); it.hasNext();) {
                SimpleTypeInheritanceContainer simpleTypeInheritanceContainer = it.next();

                // Check if base type is a list or another type variant
                if (((SimpleType) simpleContentRestriction.getBase()).getInheritance() instanceof SimpleContentList) {
                    simpleTypeInheritanceContainer.setHasSimpleTypeRestriction(true);
                    LinkedHashSet<SimpleContentList> simpleContentLists = new LinkedHashSet<SimpleContentList>();
                    simpleContentLists.add((SimpleContentList) ((SimpleType) simpleContentRestriction.getBase()).getInheritance());
                    simpleTypeInheritanceContainer.setSimpleContentLists(simpleContentLists);
                    simpleTypeInheritanceContainer.setListEnumeration(simpleContentRestriction.getEnumeration());
                    simpleTypeInheritanceContainer.setListLength(simpleContentRestriction.getLength());
                    simpleTypeInheritanceContainer.setListMaxLength(simpleContentRestriction.getMaxLength());
                    simpleTypeInheritanceContainer.setListMinLength(simpleContentRestriction.getMinLength());
                    simpleTypeInheritanceContainer.addListPattern(simpleContentRestriction.getPattern());
                    simpleTypeInheritanceContainer.setListWhitespace(simpleContentRestriction.getWhitespace());

                } else {
                    simpleTypeInheritanceContainer.setHasSimpleTypeRestriction(true);
                    simpleTypeInheritanceContainer.setSimpleTypeEnumeration(simpleContentRestriction.getEnumeration());
                    simpleTypeInheritanceContainer.setSimpleTypeFractionDigits(simpleContentRestriction.getFractionDigits());
                    simpleTypeInheritanceContainer.setSimpleTypeLength(simpleContentRestriction.getLength());
                    simpleTypeInheritanceContainer.setSimpleTypeMaxExclusive(simpleContentRestriction.getMaxExclusive());
                    simpleTypeInheritanceContainer.setSimpleTypeMaxInclusive(simpleContentRestriction.getMaxInclusive());
                    simpleTypeInheritanceContainer.setSimpleTypeMaxLength(simpleContentRestriction.getMaxLength());
                    simpleTypeInheritanceContainer.setSimpleTypeMinExclusive(simpleContentRestriction.getMinExclusive());
                    simpleTypeInheritanceContainer.setSimpleTypeMinInclusive(simpleContentRestriction.getMinInclusive());
                    simpleTypeInheritanceContainer.setSimpleTypeMinLength(simpleContentRestriction.getMinLength());
                    simpleTypeInheritanceContainer.addSimpleTypePattern(simpleContentRestriction.getPattern());
                    simpleTypeInheritanceContainer.setSimpleTypeTotalDigits(simpleContentRestriction.getTotalDigits());
                    simpleTypeInheritanceContainer.setSimpleTypeWhitespace(simpleContentRestriction.getWhitespace());
                }
            }
        } else if (simpleType.getInheritance() == null) {

            // Build-in types are the base of the inheritance information
            SimpleTypeInheritanceContainer inheritanceContainer = new SimpleTypeInheritanceContainer();
            inheritanceContainer.setBase(simpleType);
            simpleTypeInheritanceInformation.add(inheritanceContainer);
        }
        return simpleTypeInheritanceInformation;
    }

    /**
     * Generate new simpleType for given minunend and subtrahend simpleTypes.
     *
     * @param minuendSimpleType Minunend simpleType from which the subtrahend
     * simpleType is removed.
     * @param subtrahendSimpleType Subtrahend simpleType which is removed from
     * the minunend simpleType.
     * @param newTypeName New name of the build type.
     * @return New simpleType representing the difference of the minuend and
     * subtrahend simpleType.
     */
    public SimpleType generateNewSimpleType(SimpleType minuendSimpleType, SimpleType subtrahendSimpleType, String newTypeName) {

        // If subtrahend type is anyTyp return null
        if (subtrahendSimpleType == null || subtrahendSimpleType.getName().equals("{http://www.w3.org/2001/XMLSchema}anyType")) {
            return null;
        }

        // If subtrahend type contains an unrestricted anySimpleType return null
        if (containsUnrestrictedAnySimpleType(subtrahendSimpleType)) {
            return null;
        }

        // Check if minuend type contains an unrestricted anySimpleType
        if (minuendSimpleType == null || containsUnrestrictedAnySimpleType(minuendSimpleType)) {

            // Name of the any simple type
            String name = "{http://www.w3.org/2001/XMLSchema}anySimpleType";

            // Check if any type has to be regstered in the output schema
            if (!outputSchema.getTypeSymbolTable().hasReference(name)) {
                outputSchema.getTypeSymbolTable().updateOrCreateReference(name, new SimpleType(name, null));
            }

            // Return any simple type
            return (SimpleType) outputSchema.getTypeSymbolTable().getReference(name).getReference();
        }

        // If subtrahend type is anyTyp return null
        if (minuendSimpleType.getName().equals("{http://www.w3.org/2001/XMLSchema}anyType")) {

            // Name of the any simple type
            String name = "{http://www.w3.org/2001/XMLSchema}anyType";

            // Check if any type has to be regstered in the output schema
            if (!outputSchema.getTypeSymbolTable().hasReference(name)) {
                outputSchema.getTypeSymbolTable().updateOrCreateReference(name, new SimpleType(name, null));
            }

            // Return any simple type
            return (SimpleType) outputSchema.getTypeSymbolTable().getReference(name).getReference();
        }

        // Create new simpleType inheritance for the new anonymous simpleType
        SimpleTypeInheritance newSimpleTypeInheritance = generateNewSimpleTypeInheritance(minuendSimpleType, subtrahendSimpleType);

        if (newSimpleTypeInheritance == null) {
            return null;
        }

        // Create new anonymous simpleType with new "ID" attribute and annotation
        SimpleType newSimpleType = new SimpleType(newTypeName, newSimpleTypeInheritance, false);
        newSimpleType.setAnnotation(minuendSimpleType.getAnnotation());
        newSimpleType.setId(getID(minuendSimpleType.getId()));

        // Add new simpleType to the type SymbolTable of the output schema
        outputSchema.getTypeSymbolTable().updateOrCreateReference(newSimpleType.getName(), newSimpleType);

        // Add new type to the list of top-level types if it is not anonymous
        if (newSimpleType != null && !newSimpleType.isAnonymous()) {
            outputSchema.addType(outputSchema.getTypeSymbolTable().getReference(newSimpleType.getName()));
        }
        return newSimpleType;
    }

    /**
     * Check if the simpleType contains an unrestricted anySimpleType.
     * @param simpleType SimpleType for which is checked if it contains
     * unrestricted anySimpleType.
     * @return <tt>true</tt> if the simpleType contains unrestricted
     * anySimpleType.
     */
    private boolean containsUnrestrictedAnySimpleType(SimpleType simpleType) {

        // Check if simpleType has no inheritance
        if (simpleType.getInheritance() == null) {

            // If simpleType is build-in datatype check if it is an anySimpleType
            if (simpleType.getName().equals("{http://www.w3.org/2001/XMLSchema}anySimpleType")) {
                return true;
            }
        } else if (simpleType.getInheritance() instanceof SimpleContentList) {
            SimpleContentList simpleContentList = (SimpleContentList) simpleType.getInheritance();

            // Check base of the list type
            return containsUnrestrictedAnySimpleType((SimpleType) simpleContentList.getBase());

        } else if (simpleType.getInheritance() instanceof SimpleContentUnion) {
            SimpleContentUnion simpleContentUnion = (SimpleContentUnion) simpleType.getInheritance();
            LinkedList<SymbolTableRef<Type>> memberTypes = simpleContentUnion.getAllMemberTypes();

            // Check for each contained member type if it contains an unrestricted anySimpleType
            for (Iterator<SymbolTableRef<Type>> it = memberTypes.iterator(); it.hasNext();) {
                SymbolTableRef<Type> symbolTableRef = it.next();

                if (containsUnrestrictedAnySimpleType((SimpleType) symbolTableRef.getReference())) {
                    return true;
                }
            }
        } else if (simpleType.getInheritance() instanceof SimpleContentRestriction) {
            SimpleContentRestriction simpleContentRestriction = (SimpleContentRestriction) simpleType.getInheritance();

            // Check if restriction restricts a list or another type variant
            if (((SimpleType) simpleContentRestriction.getBase()).getInheritance() instanceof SimpleContentList) {

                // Check facets that do not allow an unrestricted use of anySimpleTyp
                if (!simpleContentRestriction.getEnumeration().isEmpty() ||
                        (simpleContentRestriction.getLength() != null && simpleContentRestriction.getLength().getValue() != 1) ||
                        (simpleContentRestriction.getLength() != null && simpleContentRestriction.getMaxLength().getValue() < 1) ||
                        (simpleContentRestriction.getLength() != null && simpleContentRestriction.getMinLength().getValue() > 1) ||
                        simpleContentRestriction.getPattern() != null ||
                        simpleContentRestriction.getWhitespace() != null) {
                    return false;
                }
            } else {

                // Check facets that do not allow an unrestricted use of anySimpleTyp
                if (!simpleContentRestriction.getEnumeration().isEmpty() ||
                        simpleContentRestriction.getFractionDigits() != null ||
                        simpleContentRestriction.getLength() != null ||
                        simpleContentRestriction.getMaxExclusive() != null ||
                        simpleContentRestriction.getMaxInclusive() != null ||
                        simpleContentRestriction.getMaxLength() != null ||
                        simpleContentRestriction.getMinExclusive() != null ||
                        simpleContentRestriction.getMinInclusive() != null ||
                        simpleContentRestriction.getMinLength() != null ||
                        simpleContentRestriction.getPattern() != null ||
                        simpleContentRestriction.getTotalDigits() != null ||
                        simpleContentRestriction.getWhitespace() != null) {
                    return false;
                }
            }
            return containsUnrestrictedAnySimpleType((SimpleType) simpleContentRestriction.getBase());
        }
        return false;
    }

    /**
     * Get name of the specified type.
     * @param type Type which has a name.
     * @return New name of the given Type.
     */
    private String getTypeName(Type type) {

        // Build-in data types are not renamed
        if (type.getNamespace().equals("http://www.w3.org/2001/XMLSchema")) {
            return type.getName();
        }

        // Get new name
        String typeName = "{" + outputSchema.getTargetNamespace() + "}difference-type." + type.getLocalName() + "-null";

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
     * New restriction type name.
     * @return New name for an restriction type.
     */
    private String getRestrictionTypeName() {

        // Generate new name for the new type
        String typeName = "{" + outputSchema.getTargetNamespace() + "}difference-type.restriction";

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
     * New list type name.
     * @return New name for an list type.
     */
    private String getListTypeName() {

        // Generate new name for the new type
        String typeName = "{" + outputSchema.getTargetNamespace() + "}difference-type.list";

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
     * New union type name.
     * @return New name for an union type.
     */
    private String getUnionTypeName() {

        // Generate new name for the new type
        String typeName = "{" + outputSchema.getTargetNamespace() + "}difference-type.union";

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
     * Get name for the specified minunend and subtrahend types.
     * @param minuendType Minuend Type with name.
     * @param subtrahendType Subtrahend Type with name.
     * @return New name for a new type.
     */
    private String getTypeName(Type minuendType, Type subtrahendType) {

        // Build-in data types are not renamed
        if (subtrahendType == null && minuendType.getNamespace().equals("http://www.w3.org/2001/XMLSchema")) {
            return minuendType.getName();
        }

        // Get new name
        String typeName = "{" + outputSchema.getTargetNamespace() + "}difference-type." + minuendType.getLocalName() + "-" + subtrahendType.getLocalName();

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

    private SimpleTypeInheritanceContainer getInheritanceContainerCopy(SimpleTypeInheritanceContainer inheritanceContainer) {

        // Get new container
        SimpleTypeInheritanceContainer newContainer = new SimpleTypeInheritanceContainer();

        // Generate copy of every facet
        newContainer.setHasSimpleTypeRestriction(inheritanceContainer.isHasSimpleTypeRestriction());
        newContainer.setSimpleTypeMinExclusive(inheritanceContainer.getSimpleTypeMinExclusive());
        newContainer.setSimpleTypeMaxExclusive(inheritanceContainer.getSimpleTypeMaxExclusive());
        newContainer.setSimpleTypeMinInclusive(inheritanceContainer.getSimpleTypeMinInclusive());
        newContainer.setSimpleTypeMaxInclusive(inheritanceContainer.getSimpleTypeMaxInclusive());
        newContainer.setSimpleTypeTotalDigits(inheritanceContainer.getSimpleTypeTotalDigits());
        newContainer.setSimpleTypeFractionDigits(inheritanceContainer.getSimpleTypeFractionDigits());
        newContainer.setSimpleTypeLength(inheritanceContainer.getSimpleTypeLength());
        newContainer.setSimpleTypeMinLength(inheritanceContainer.getSimpleTypeMinLength());
        newContainer.setSimpleTypeMaxLength(inheritanceContainer.getSimpleTypeMaxLength());
        newContainer.setSimpleTypePatternList(inheritanceContainer.getSimpleTypePatternList());
        newContainer.setSimpleTypeWhitespace(inheritanceContainer.getSimpleTypeWhitespace());
        newContainer.setSimpleTypeEnumeration(inheritanceContainer.getSimpleTypeEnumeration());
        newContainer.setHasList(inheritanceContainer.isHasList());
        newContainer.setSimpleContentLists(inheritanceContainer.getSimpleContentLists());
        newContainer.setListLength(inheritanceContainer.getListLength());
        newContainer.setListMinLength(inheritanceContainer.getListMinLength());
        newContainer.setListMaxLength(inheritanceContainer.getListMaxLength());
        newContainer.setListEnumeration(inheritanceContainer.getListEnumeration());
        newContainer.setListPatternList(inheritanceContainer.getListPatternList());
        newContainer.setListWhitespace(inheritanceContainer.getListWhitespace());
        newContainer.setBase(inheritanceContainer.getBase());

        // Remove null entries from lists
        for (Iterator<SimpleContentRestrictionProperty<String>> it = inheritanceContainer.getListPatternList().iterator(); it.hasNext();) {
            SimpleContentRestrictionProperty<String> simpleContentRestrictionProperty = it.next();

            if (simpleContentRestrictionProperty == null) {
                it.remove();
            }
        }
        for (Iterator<SimpleContentRestrictionProperty<String>> it = inheritanceContainer.getSimpleTypePatternList().iterator(); it.hasNext();) {
            SimpleContentRestrictionProperty<String> simpleContentRestrictionProperty = it.next();

            if (simpleContentRestrictionProperty == null) {
                it.remove();
            }
        }

        // Return the new container
        return newContainer;
    }

    private LinkedList<SimpleTypeInheritanceContainer> generateContainerDifference(LinkedHashSet<SimpleTypeInheritanceContainer> minunendContainers, LinkedHashSet<SimpleTypeInheritanceContainer> subtrahendContainers, boolean checkNotForList) {

        LinkedHashMap<SimpleTypeInheritanceContainer, LinkedHashSet<SimpleTypeInheritanceContainer>> seenSubtrahendContainers = new LinkedHashMap<SimpleTypeInheritanceContainer, LinkedHashSet<SimpleTypeInheritanceContainer>>();

        LinkedList<SimpleTypeInheritanceContainer> minuendContainers = new LinkedList<SimpleTypeInheritanceContainer>(minunendContainers);

        for (Iterator<SimpleTypeInheritanceContainer> it = minuendContainers.iterator(); it.hasNext();) {
            SimpleTypeInheritanceContainer minuendContainer = it.next();
            seenSubtrahendContainers.put(minuendContainer, new LinkedHashSet<SimpleTypeInheritanceContainer>());
        }

        LinkedList<SimpleTypeInheritanceContainer> removableContainers = new LinkedList<SimpleTypeInheritanceContainer>();

        for (int i = 0; i < minuendContainers.size(); i++) {
            SimpleTypeInheritanceContainer minuendContainer = minuendContainers.get(i);

            if (checkNotForList || !minuendContainer.isHasList()) {

                for (Iterator<SimpleTypeInheritanceContainer> it2 = subtrahendContainers.iterator(); it2.hasNext();) {
                    SimpleTypeInheritanceContainer subtrahendContainer = it2.next();

                    if (!seenSubtrahendContainers.get(minuendContainer).contains(subtrahendContainer)) {
                        seenSubtrahendContainers.get(minuendContainer).add(subtrahendContainer);

                        LinkedList<SimpleTypeInheritanceContainer> newContainers = generateNewSimpleTypeInheritanceContainerBase(minuendContainer, subtrahendContainer);

                        if (newContainers != null) {

                            for (Iterator<SimpleTypeInheritanceContainer> it3 = newContainers.iterator(); it3.hasNext();) {
                                SimpleTypeInheritanceContainer newContainer = it3.next();

                                if (newContainer != null) {

                                    seenSubtrahendContainers.put(newContainer, new LinkedHashSet<SimpleTypeInheritanceContainer>());
                                    seenSubtrahendContainers.get(newContainer).addAll(seenSubtrahendContainers.get(minuendContainer));
                                    minuendContainers.add(newContainer);
                                }
                            }
                        }
                        removableContainers.add(minuendContainer);
                        seenSubtrahendContainers.get(minuendContainer).addAll(subtrahendContainers);
                    }
                }
            } else {
                removableContainers.add(minuendContainer);
            }
        }

        for (Iterator<SimpleTypeInheritanceContainer> it = removableContainers.iterator(); it.hasNext();) {
            SimpleTypeInheritanceContainer removableContainer = it.next();
            minuendContainers.remove(removableContainer);
        }

        return minuendContainers;
    }

    private LinkedList<SimpleTypeInheritanceContainer> generateListContainerDifference(LinkedHashSet<SimpleTypeInheritanceContainer> minunendContainers, LinkedHashSet<SimpleTypeInheritanceContainer> subtrahendContainers) {

        LinkedHashMap<SimpleTypeInheritanceContainer, LinkedHashSet<SimpleTypeInheritanceContainer>> seenSubtrahendContainers = new LinkedHashMap<SimpleTypeInheritanceContainer, LinkedHashSet<SimpleTypeInheritanceContainer>>();

        LinkedList<SimpleTypeInheritanceContainer> minuendContainers = new LinkedList<SimpleTypeInheritanceContainer>(minunendContainers);

        for (Iterator<SimpleTypeInheritanceContainer> it = minuendContainers.iterator(); it.hasNext();) {
            SimpleTypeInheritanceContainer minuendContainer = it.next();
            seenSubtrahendContainers.put(minuendContainer, new LinkedHashSet<SimpleTypeInheritanceContainer>());
        }

        LinkedList<SimpleTypeInheritanceContainer> removableContainers = new LinkedList<SimpleTypeInheritanceContainer>();

        for (int i = 0; i < minuendContainers.size(); i++) {
            SimpleTypeInheritanceContainer minuendContainer = minuendContainers.get(i);


            for (Iterator<SimpleTypeInheritanceContainer> it2 = subtrahendContainers.iterator(); it2.hasNext();) {
                SimpleTypeInheritanceContainer subtrahendContainer = it2.next();

                if (!seenSubtrahendContainers.get(minuendContainer).contains(subtrahendContainer)) {
                    seenSubtrahendContainers.get(minuendContainer).add(subtrahendContainer);

                    LinkedList<SimpleTypeInheritanceContainer> newContainers = generateNewSimpleTypeInheritanceContainerList(minuendContainer, subtrahendContainer);

                    if (newContainers != null) {

                        for (Iterator<SimpleTypeInheritanceContainer> it3 = newContainers.iterator(); it3.hasNext();) {
                            SimpleTypeInheritanceContainer newContainer = it3.next();

                            if (newContainer != null) {

                                seenSubtrahendContainers.put(newContainer, new LinkedHashSet<SimpleTypeInheritanceContainer>());
                                seenSubtrahendContainers.get(newContainer).addAll(seenSubtrahendContainers.get(minuendContainer));
                                minuendContainers.add(newContainer);
                            }
                        }
                    }
                    removableContainers.add(minuendContainer);
                    seenSubtrahendContainers.get(minuendContainer).addAll(subtrahendContainers);
                }
            }
        }

        for (Iterator<SimpleTypeInheritanceContainer> it = removableContainers.iterator(); it.hasNext();) {
            SimpleTypeInheritanceContainer removableContainer = it.next();
            minuendContainers.remove(removableContainer);
        }

        return minuendContainers;
    }

    private SimpleTypeInheritance generateNewSimpleTypeInheritance(SimpleType minuendSimpleType, SimpleType subtrahendSimpleType) {

        // For each SimpleType its base types are found and stored in the simpleTypeInheritanceInformationMap, base types are the types from which the SimpleType is derived
        LinkedHashMap<SimpleType, LinkedHashSet<SimpleTypeInheritanceContainer>> simpleTypeInheritanceInformationMap = new LinkedHashMap<SimpleType, LinkedHashSet<SimpleTypeInheritanceContainer>>();
        simpleTypeInheritanceInformationMap.put(minuendSimpleType, getSimpleTypeInheritanceInformation(minuendSimpleType));
        simpleTypeInheritanceInformationMap.put(subtrahendSimpleType, getSimpleTypeInheritanceInformation(subtrahendSimpleType));

        // Get new containers from types without list
        LinkedHashSet<SimpleTypeInheritanceContainer> simpleTypeInheritanceContainers = new LinkedHashSet<SimpleTypeInheritanceContainer>();
        simpleTypeInheritanceContainers.addAll(generateContainerDifference(simpleTypeInheritanceInformationMap.get(minuendSimpleType), simpleTypeInheritanceInformationMap.get(subtrahendSimpleType), true));

        // Get new containers from types with list
        LinkedHashMap<SimpleContentList, LinkedHashSet<SimpleTypeInheritanceContainer>> minuendListInheritanceContainerMap = new LinkedHashMap<SimpleContentList, LinkedHashSet<SimpleTypeInheritanceContainer>>();
        LinkedHashMap<SimpleContentList, LinkedHashSet<SimpleTypeInheritanceContainer>> subtrahendListInheritanceContainerMap = new LinkedHashMap<SimpleContentList, LinkedHashSet<SimpleTypeInheritanceContainer>>();

        // Get minuned list containers
        for (Iterator<SimpleTypeInheritanceContainer> it = simpleTypeInheritanceInformationMap.get(minuendSimpleType).iterator(); it.hasNext();) {
            SimpleTypeInheritanceContainer simpleTypeInheritanceContainer = it.next();

            if (simpleTypeInheritanceContainer.isHasList()) {
                LinkedHashSet<SimpleTypeInheritanceContainer> currentInheritanceContainers = new LinkedHashSet<SimpleTypeInheritanceContainer>();

                if (minuendListInheritanceContainerMap.containsKey(simpleTypeInheritanceContainer.getSimpleContentLists().iterator().next())) {
                    currentInheritanceContainers = minuendListInheritanceContainerMap.get(simpleTypeInheritanceContainer.getSimpleContentLists().iterator().next());
                }
                currentInheritanceContainers.add(simpleTypeInheritanceContainer);
                minuendListInheritanceContainerMap.put(simpleTypeInheritanceContainer.getSimpleContentLists().iterator().next(), currentInheritanceContainers);
            }
        }

        // Get subtrahend list containers
        for (Iterator<SimpleTypeInheritanceContainer> it = simpleTypeInheritanceInformationMap.get(subtrahendSimpleType).iterator(); it.hasNext();) {
            SimpleTypeInheritanceContainer simpleTypeInheritanceContainer = it.next();

            if (simpleTypeInheritanceContainer.isHasList()) {
                LinkedHashSet<SimpleTypeInheritanceContainer> currentInheritanceContainers = new LinkedHashSet<SimpleTypeInheritanceContainer>();

                if (subtrahendListInheritanceContainerMap.containsKey(simpleTypeInheritanceContainer.getSimpleContentLists().iterator().next())) {
                    currentInheritanceContainers = subtrahendListInheritanceContainerMap.get(simpleTypeInheritanceContainer.getSimpleContentLists().iterator().next());
                }
                currentInheritanceContainers.add(simpleTypeInheritanceContainer);
                subtrahendListInheritanceContainerMap.put(simpleTypeInheritanceContainer.getSimpleContentLists().iterator().next(), currentInheritanceContainers);
            }
        }

        // Get new containers from types with list
        for (Iterator<SimpleContentList> it = minuendListInheritanceContainerMap.keySet().iterator(); it.hasNext();) {
            SimpleContentList minuendSimpleContentList = it.next();

            // Check if content is empty
            for (Iterator<SimpleContentList> it2 = subtrahendListInheritanceContainerMap.keySet().iterator(); it2.hasNext();) {
                SimpleContentList subtrahendSimpleContentList = it2.next();
                LinkedList<SimpleTypeInheritanceContainer> newContainers = generateContainerDifference(minuendListInheritanceContainerMap.get(minuendSimpleContentList), subtrahendListInheritanceContainerMap.get(subtrahendSimpleContentList), true);

                // If content is empty build new list containers
                if (newContainers.isEmpty()) {
                    simpleTypeInheritanceContainers.addAll(generateListContainerDifference(minuendListInheritanceContainerMap.get(minuendSimpleContentList), subtrahendListInheritanceContainerMap.get(subtrahendSimpleContentList)));
                }
            }
        }
        // If the list of inheritanceContainers is empty null is returned
        if (simpleTypeInheritanceContainers.isEmpty()) {
            return null;
        }
        HashMap<SimpleTypeInheritanceContainer, SimpleType> simpleTypeMap = new HashMap<SimpleTypeInheritanceContainer, SimpleType>();

        // For each SimpleTypeInheritanceContainer build a new SimpleType with or without restriction
        for (Iterator<SimpleTypeInheritanceContainer> it = simpleTypeInheritanceContainers.iterator(); it.hasNext();) {
            SimpleTypeInheritanceContainer simpleTypeInheritanceContainer = it.next();

            SimpleType newSimpleType = new SimpleType(simpleTypeInheritanceContainer.getBase().getName(), null, false);
            outputSchema.getTypeSymbolTable().updateOrCreateReference(newSimpleType.getName(), newSimpleType);

            if (simpleTypeInheritanceContainer.isHasSimpleTypeRestriction()) {
                newSimpleType = generateNewGlobalSimpleTypeWithRestriction(simpleTypeInheritanceContainer);
            }
            simpleTypeMap.put(simpleTypeInheritanceContainer, newSimpleType);
        }

        // List of memberTypes of the SimpleContentUnion which is returned by this method
        LinkedList<SymbolTableRef<Type>> globalMemberTypes = new LinkedList<SymbolTableRef<Type>>();
        HashMap<LinkedList<SimpleTypeInheritanceContainer>, SimpleType> listMap = new HashMap<LinkedList<SimpleTypeInheritanceContainer>, SimpleType>();
        LinkedHashSet<SimpleTypeInheritanceContainer> usedListContainers = new LinkedHashSet<SimpleTypeInheritanceContainer>();

        for (Iterator<SimpleTypeInheritanceContainer> it = simpleTypeInheritanceContainers.iterator(); it.hasNext();) {
            SimpleTypeInheritanceContainer simpleTypeInheritanceContainer = it.next();

            // If inheritance path contains no list add type to memberTypes list
            if (!simpleTypeInheritanceContainer.isHasList()) {
                globalMemberTypes.add(outputSchema.getTypeSymbolTable().getReference(simpleTypeMap.get(simpleTypeInheritanceContainer).getName()));
            } else {

                // Find containers with same list
                LinkedList<SimpleTypeInheritanceContainer> containersWithSameList = new LinkedList<SimpleTypeInheritanceContainer>();
                for (Iterator<SimpleTypeInheritanceContainer> it2 = simpleTypeInheritanceContainers.iterator(); it2.hasNext();) {
                    SimpleTypeInheritanceContainer otherSimpleTypeInheritanceContainer = it2.next();

                    if (simpleTypeInheritanceContainer.getSimpleContentLists().containsAll(otherSimpleTypeInheritanceContainer.getSimpleContentLists()) &&
                            otherSimpleTypeInheritanceContainer.getSimpleContentLists().containsAll(simpleTypeInheritanceContainer.getSimpleContentLists())) {
                        containersWithSameList.add(otherSimpleTypeInheritanceContainer);
                    }
                }

                SimpleType newListSimpleType = null;

                // If the new list should be unique the list of containers with same list has size one
                if (containersWithSameList.size() == 1) {
                    SimpleContentList newSimpleContentList = new SimpleContentList(outputSchema.getTypeSymbolTable().getReference(simpleTypeMap.get(simpleTypeInheritanceContainer).getName()));
                    String newListTypeName = getListTypeName();
                    newListSimpleType = new SimpleType(newListTypeName, newSimpleContentList, false);
                    newListSimpleType.setFinalModifiers(new HashSet<SimpleTypeInheritanceModifier>());
                    outputSchema.getTypeSymbolTable().updateOrCreateReference(newListSimpleType.getName(), newListSimpleType);

                    if (!outputSchema.getTypes().contains(newListSimpleType)) {
                        outputSchema.addType(outputSchema.getTypeSymbolTable().getReference(newListSimpleType.getName()));
                    }
                } else {

                    // If the no list exists
                    if (listMap.get(containersWithSameList) == null) {

                        LinkedHashSet<SimpleTypeInheritanceContainer> usedContainers = new LinkedHashSet<SimpleTypeInheritanceContainer>();

                        // Find memberTypes for new SimpleContentUnion
                        LinkedList<SymbolTableRef<Type>> newMemberTypes = new LinkedList<SymbolTableRef<Type>>();
                        for (Iterator<SimpleTypeInheritanceContainer> it2 = containersWithSameList.iterator(); it2.hasNext();) {
                            SimpleTypeInheritanceContainer inheritanceContainer = it2.next();
                            boolean useNewType = true;

                            for (Iterator<SimpleTypeInheritanceContainer> it3 = usedContainers.iterator(); it3.hasNext();) {
                                SimpleTypeInheritanceContainer container = it3.next();

                                if ((inheritanceContainer.getSimpleTypeEnumeration().containsAll(container.getSimpleTypeEnumeration()) && container.getSimpleTypeEnumeration().containsAll(inheritanceContainer.getSimpleTypeEnumeration())) &&
                                        (inheritanceContainer.getSimpleTypePatternList().containsAll(container.getSimpleTypePatternList()) && container.getSimpleTypePatternList().containsAll(inheritanceContainer.getSimpleTypePatternList())) &&
                                        (inheritanceContainer.getSimpleTypeFractionDigits() == container.getSimpleTypeFractionDigits() || (inheritanceContainer.getSimpleTypeFractionDigits() != null && container.getSimpleTypeFractionDigits() != null && inheritanceContainer.getSimpleTypeFractionDigits().getValue() == container.getSimpleTypeFractionDigits().getValue())) &&
                                        (inheritanceContainer.getSimpleTypeLength() == container.getSimpleTypeLength() || (inheritanceContainer.getSimpleTypeLength() != null && container.getSimpleTypeLength() != null && inheritanceContainer.getSimpleTypeLength().getValue() == container.getSimpleTypeLength().getValue())) &&
                                        (inheritanceContainer.getSimpleTypeMaxExclusive() == container.getSimpleTypeMaxExclusive() || (inheritanceContainer.getSimpleTypeMaxExclusive() != null && container.getSimpleTypeMaxExclusive() != null && inheritanceContainer.getSimpleTypeMaxExclusive().getValue() == container.getSimpleTypeMaxExclusive().getValue())) &&
                                        (inheritanceContainer.getSimpleTypeMaxInclusive() == container.getSimpleTypeMaxInclusive() || (inheritanceContainer.getSimpleTypeMaxInclusive() != null && container.getSimpleTypeMaxInclusive() != null && inheritanceContainer.getSimpleTypeMaxInclusive().getValue() == container.getSimpleTypeMaxInclusive().getValue())) &&
                                        (inheritanceContainer.getSimpleTypeMaxLength() == container.getSimpleTypeMaxLength() || (inheritanceContainer.getSimpleTypeMaxLength() != null && container.getSimpleTypeMaxLength() != null && inheritanceContainer.getSimpleTypeMaxLength().getValue() == container.getSimpleTypeMaxLength().getValue())) &&
                                        (inheritanceContainer.getSimpleTypeMinExclusive() == container.getSimpleTypeMinExclusive() || (inheritanceContainer.getSimpleTypeMinExclusive() != null && container.getSimpleTypeMinExclusive() != null && inheritanceContainer.getSimpleTypeMinExclusive().getValue() == container.getSimpleTypeMinExclusive().getValue())) &&
                                        (inheritanceContainer.getSimpleTypeMinInclusive() == container.getSimpleTypeMinInclusive() || (inheritanceContainer.getSimpleTypeMinInclusive() != null && container.getSimpleTypeMinInclusive() != null && inheritanceContainer.getSimpleTypeMinInclusive().getValue() == container.getSimpleTypeMinInclusive().getValue())) &&
                                        (inheritanceContainer.getSimpleTypeMinLength() == container.getSimpleTypeMinLength() || (inheritanceContainer.getSimpleTypeMinLength() != null && container.getSimpleTypeMinLength() != null && inheritanceContainer.getSimpleTypeMinLength().getValue() == container.getSimpleTypeMinLength().getValue())) &&
                                        (inheritanceContainer.getSimpleTypeTotalDigits() == container.getSimpleTypeTotalDigits() || (inheritanceContainer.getSimpleTypeTotalDigits() != null && container.getSimpleTypeTotalDigits() != null && inheritanceContainer.getSimpleTypeTotalDigits().getValue() == container.getSimpleTypeTotalDigits().getValue())) &&
                                        (inheritanceContainer.getSimpleTypeWhitespace() == container.getSimpleTypeWhitespace() || (inheritanceContainer.getSimpleTypeWhitespace() != null && container.getSimpleTypeWhitespace() != null && inheritanceContainer.getSimpleTypeWhitespace().getValue() == container.getSimpleTypeWhitespace().getValue()))) {
                                    useNewType = false;
                                }
                            }

                            if (useNewType) {
                                newMemberTypes.add(outputSchema.getTypeSymbolTable().getReference(simpleTypeMap.get(inheritanceContainer).getName()));
                            }
                            usedContainers.add(inheritanceContainer);
                        }

                        // Build new SimpleType with SimpleContentUnion
                        SimpleContentUnion newSimpleContentUnion = new SimpleContentUnion(newMemberTypes);
                        String newUnionTypeName = getUnionTypeName();
                        SimpleType newUnionSimpleType = new SimpleType(newUnionTypeName, newSimpleContentUnion, false);
                        newUnionSimpleType.setFinalModifiers(new HashSet<SimpleTypeInheritanceModifier>());

                        outputSchema.getTypeSymbolTable().updateOrCreateReference(newUnionSimpleType.getName(), newUnionSimpleType);

                        if (!outputSchema.getTypes().contains(newUnionSimpleType)) {
                            outputSchema.addType(outputSchema.getTypeSymbolTable().getReference(newUnionSimpleType.getName()));
                        }

                        // Build new SimpleType with SimpleContentList which derives from the SimpleType with SimpleContentUnion
                        SimpleContentList newSimpleContentList = new SimpleContentList(outputSchema.getTypeSymbolTable().getReference(newUnionSimpleType.getName()));
                        String newListTypeName = getListTypeName();
                        newListSimpleType = new SimpleType(newListTypeName, newSimpleContentList, false);
                        newListSimpleType.setFinalModifiers(new HashSet<SimpleTypeInheritanceModifier>());

                        outputSchema.getTypeSymbolTable().updateOrCreateReference(newListSimpleType.getName(), newListSimpleType);

                        if (!outputSchema.getTypes().contains(newListSimpleType)) {
                            outputSchema.addType(outputSchema.getTypeSymbolTable().getReference(newListSimpleType.getName()));
                        }
                        listMap.put(containersWithSameList, newListSimpleType);
                    } else {
                        newListSimpleType = listMap.get(containersWithSameList);
                    }
                }

                if (!simpleTypeInheritanceContainer.getListEnumeration().isEmpty() ||
                        simpleTypeInheritanceContainer.getListLength() != null ||
                        simpleTypeInheritanceContainer.getListMaxLength() != null ||
                        simpleTypeInheritanceContainer.getListMinLength() != null ||
                        !simpleTypeInheritanceContainer.getListPatternList().isEmpty()) {

                    if (newListSimpleType != null) {
                        boolean useNewType = true;

                        for (Iterator<SimpleTypeInheritanceContainer> it3 = usedListContainers.iterator(); it3.hasNext();) {
                            SimpleTypeInheritanceContainer container = it3.next();

                            if ((simpleTypeInheritanceContainer.getListEnumeration().containsAll(container.getListEnumeration()) && container.getListEnumeration().containsAll(simpleTypeInheritanceContainer.getListEnumeration())) &&
                                    (simpleTypeInheritanceContainer.getListPatternList().containsAll(container.getListPatternList()) && container.getListPatternList().containsAll(simpleTypeInheritanceContainer.getListPatternList())) &&
                                    (simpleTypeInheritanceContainer.getListLength() == container.getListLength() || (simpleTypeInheritanceContainer.getListLength() != null && container.getListLength() != null && simpleTypeInheritanceContainer.getListLength().getValue() == container.getListLength().getValue())) &&
                                    (simpleTypeInheritanceContainer.getListMaxLength() == container.getListMaxLength() || (simpleTypeInheritanceContainer.getListMaxLength() != null && container.getListMaxLength() != null && simpleTypeInheritanceContainer.getListMaxLength().getValue() == container.getListMaxLength().getValue())) &&
                                    (simpleTypeInheritanceContainer.getListMinLength() == container.getListMinLength() || (simpleTypeInheritanceContainer.getListMinLength() != null && container.getListMinLength() != null && simpleTypeInheritanceContainer.getListMinLength().getValue() == container.getListMinLength().getValue())) &&
                                    (simpleTypeInheritanceContainer.getListWhitespace() == container.getListWhitespace() || (simpleTypeInheritanceContainer.getListWhitespace() != null && container.getListWhitespace() != null && simpleTypeInheritanceContainer.getListWhitespace().getValue() == container.getListWhitespace().getValue()))) {
                                useNewType = false;
                            }
                        }
                        if (useNewType) {
                            String restictionTypeName = generateNewGlobalSimpleTypeWithListRestriction(simpleTypeInheritanceContainer, newListSimpleType).getName();
                            globalMemberTypes.add(outputSchema.getTypeSymbolTable().getReference(restictionTypeName));
                        }
                        usedListContainers.add(simpleTypeInheritanceContainer);
                    }

                } else {

                    if (newListSimpleType != null) {
                        globalMemberTypes.add(outputSchema.getTypeSymbolTable().getReference(newListSimpleType.getName()));
                    }
                }
            }
        }

        // The method retuns a SimpleContentUnion if its memberTypes are not empty
        if (!globalMemberTypes.isEmpty()) {
            SimpleContentUnion simpleContentUnion = new SimpleContentUnion(globalMemberTypes);
            return simpleContentUnion;
        }
        return null;
    }

    private LinkedList<SimpleTypeInheritanceContainer> generateNewSimpleTypeInheritanceContainerBase(SimpleTypeInheritanceContainer minuendContainer, SimpleTypeInheritanceContainer subtrahendContainer) {

        SimpleTypeInheritanceContainer newContainer = getInheritanceContainerCopy(minuendContainer);

        if (minuendContainer.getBase().getName().equals(subtrahendContainer.getBase().getName()) && (minuendContainer.isHasList() == subtrahendContainer.isHasList() || (minuendContainer.isHasList() == false))) {

            // If subrahend container contains unrestricted base type return null
            if (!subtrahendContainer.isHasSimpleTypeRestriction()) {
                return null;
            }

            // All base types should be the same
            if (newContainer.getBase() == null) {
                newContainer.setBase(subtrahendContainer.getBase());
            } else if (!newContainer.getBase().getName().equals(subtrahendContainer.getBase().getName())) {
                return null;
            }

            // Remove null references
            for (Iterator<SimpleContentRestrictionProperty<String>> it = subtrahendContainer.getSimpleTypePatternList().iterator(); it.hasNext();) {
                SimpleContentRestrictionProperty<String> simpleContentRestrictionProperty = it.next();

                if (simpleContentRestrictionProperty == null) {
                    it.remove();
                }
            }

            // Patterns of different restricitions are added to the pattern list
            if (!subtrahendContainer.getSimpleTypePatternList().isEmpty()) {

                for (Iterator<SimpleContentRestrictionProperty<String>> it3 = subtrahendContainer.getSimpleTypePatternList().iterator(); it3.hasNext();) {
                    SimpleContentRestrictionProperty<String> subtrahendPattern = it3.next();

                    for (Iterator<SimpleContentRestrictionProperty<String>> it4 = newContainer.getSimpleTypePatternList().iterator(); it4.hasNext();) {
                        SimpleContentRestrictionProperty<String> newPattern = it4.next();

                        if (newPattern != null && newPattern.getValue().equals(subtrahendPattern.getValue())) {
                            return null;
                        }
                    }
                }
            }

            // If enum list is empty and the current container contains enums add these to the list, else if the list is not empty check all enums contained in both list and remove them.
            if (!subtrahendContainer.getSimpleTypeEnumeration().isEmpty()) {

                if (newContainer.getSimpleTypeEnumeration().isEmpty()) {
                    return generateNewSimpleTypeInheritanceContainerBase(minuendContainer, new SimpleTypeInheritanceContainer());
                } else {
                    LinkedList<String> newEnumeration = new LinkedList<String>();
                    for (Iterator<String> it3 = subtrahendContainer.getSimpleTypeEnumeration().iterator(); it3.hasNext();) {
                        String enumeration = it3.next();

                        if (newContainer.getSimpleTypeEnumeration().contains(enumeration) && !subtrahendContainer.getSimpleTypeEnumeration().contains(enumeration)) {
                            newEnumeration.add(enumeration);
                        }
                    }
                    if (newEnumeration.isEmpty()) {
                        return null;
                    }
                    newContainer.setSimpleTypeEnumeration(newEnumeration);
                }
            }

            // Set whitespace to the most restrictive value
            if (subtrahendContainer.getSimpleTypeWhitespace() != null) {

                if (subtrahendContainer.getSimpleTypeWhitespace().getValue() == SimpleContentPropertyWhitespace.Collapse) {
                    newContainer.setSimpleTypeWhitespace(subtrahendContainer.getSimpleTypeWhitespace());
                }
                if (subtrahendContainer.getSimpleTypeWhitespace().getValue() == SimpleContentPropertyWhitespace.Replace && (newContainer.getSimpleTypeWhitespace() == null || newContainer.getSimpleTypeWhitespace().getValue() == SimpleContentPropertyWhitespace.Preserve)) {
                    newContainer.setSimpleTypeWhitespace(subtrahendContainer.getSimpleTypeWhitespace());
                }
                if (subtrahendContainer.getSimpleTypeWhitespace().getValue() == SimpleContentPropertyWhitespace.Preserve && newContainer.getSimpleTypeWhitespace() == null) {
                    newContainer.setSimpleTypeWhitespace(subtrahendContainer.getSimpleTypeWhitespace());
                }
            }


            // If a length value is present in the new container and the new and old value are the same no difference is possible
            if (subtrahendContainer.getSimpleTypeLength() != null) {

                if (newContainer.getSimpleTypeLength() != null && newContainer.getSimpleTypeLength().getValue() != subtrahendContainer.getSimpleTypeLength().getValue()) {
                    return null;
                }
                if (newContainer.getSimpleTypeMinLength() != null && newContainer.getSimpleTypeMinLength().getValue() == subtrahendContainer.getSimpleTypeLength().getValue()) {
                    newContainer.setSimpleTypeMinLength(new SimpleContentFixableRestrictionProperty<Integer>(subtrahendContainer.getSimpleTypeLength().getValue() + 1, subtrahendContainer.getSimpleTypeLength().getFixed()));
                }
                if (newContainer.getSimpleTypeMaxLength() != null && subtrahendContainer.getSimpleTypeLength().getValue() == newContainer.getSimpleTypeMaxLength().getValue()) {
                    newContainer.setSimpleTypeMaxLength(new SimpleContentFixableRestrictionProperty<Integer>(subtrahendContainer.getSimpleTypeLength().getValue() - 1, subtrahendContainer.getSimpleTypeLength().getFixed()));
                }
                if ((newContainer.getSimpleTypeMinLength() == null || (newContainer.getSimpleTypeMinLength() != null && newContainer.getSimpleTypeMinLength().getValue() < subtrahendContainer.getSimpleTypeLength().getValue())) &&
                        (newContainer.getSimpleTypeMaxLength() == null || (newContainer.getSimpleTypeMaxLength() != null && subtrahendContainer.getSimpleTypeLength().getValue() < newContainer.getSimpleTypeMaxLength().getValue()))) {
                    LinkedList<SimpleTypeInheritanceContainer> containers = new LinkedList<SimpleTypeInheritanceContainer>();

                    SimpleTypeInheritanceContainer container = getInheritanceContainerCopy(minuendContainer);
                    container.setSimpleTypeMaxLength(new SimpleContentFixableRestrictionProperty<Integer>(subtrahendContainer.getSimpleTypeLength().getValue() - 1, false));
                    LinkedList<SimpleTypeInheritanceContainer> newContainers = generateNewSimpleTypeInheritanceContainerBase(container, subtrahendContainer);

                    if (newContainers != null) {
                        containers.addAll(newContainers);
                    }
                    container = getInheritanceContainerCopy(minuendContainer);
                    container.setSimpleTypeMinLength(new SimpleContentFixableRestrictionProperty<Integer>(subtrahendContainer.getSimpleTypeLength().getValue() + 1, false));
                    newContainers = generateNewSimpleTypeInheritanceContainerBase(container, subtrahendContainer);

                    if (newContainers != null) {
                        containers.addAll(newContainers);
                    }
                    return containers;
                }
            }

            // Check if minuend restriction contains length facet
            if (newContainer.getSimpleTypeLength() != null) {

                // If minuend length is contained in subtrahend container return null
                if ((subtrahendContainer.getSimpleTypeMinLength() == null || newContainer.getSimpleTypeLength().getValue() >= subtrahendContainer.getSimpleTypeMinLength().getValue()) && (subtrahendContainer.getSimpleTypeMaxLength() == null || newContainer.getSimpleTypeLength().getValue() <= subtrahendContainer.getSimpleTypeMaxLength().getValue())) {
                    return null;
                }
            } else {

                if ((subtrahendContainer.getSimpleTypeMinLength() == null ||
                        (newContainer.getSimpleTypeMinLength() != null && newContainer.getSimpleTypeMinLength().getValue() >= subtrahendContainer.getSimpleTypeMinLength().getValue())) && (subtrahendContainer.getSimpleTypeMaxLength() == null || (newContainer.getSimpleTypeMaxLength() != null && newContainer.getSimpleTypeMaxLength().getValue() <= subtrahendContainer.getSimpleTypeMaxLength().getValue()))) {

                    if (subtrahendContainer.getSimpleTypeEnumeration().isEmpty() &&
                            subtrahendContainer.getSimpleTypeFractionDigits() == null &&
                            subtrahendContainer.getSimpleTypeMaxExclusive() == null &&
                            subtrahendContainer.getSimpleTypeMaxInclusive() == null &&
                            subtrahendContainer.getSimpleTypeMinExclusive() == null &&
                            subtrahendContainer.getSimpleTypeMinInclusive() == null &&
                            subtrahendContainer.getSimpleTypePatternList().isEmpty() &&
                            subtrahendContainer.getSimpleTypeTotalDigits() == null &&
                            subtrahendContainer.getSimpleTypeLength() == null) {
                        return null;
                    }
                }

                if ((subtrahendContainer.getSimpleTypeMinLength() != null && (newContainer.getSimpleTypeMinLength() == null || newContainer.getSimpleTypeMinLength().getValue() < subtrahendContainer.getSimpleTypeMinLength().getValue())) && (subtrahendContainer.getSimpleTypeMaxLength() != null && (newContainer.getSimpleTypeMaxLength() == null || newContainer.getSimpleTypeMaxLength().getValue() > subtrahendContainer.getSimpleTypeMaxLength().getValue()))) {

                    LinkedList<SimpleTypeInheritanceContainer> containers = new LinkedList<SimpleTypeInheritanceContainer>();

                    SimpleTypeInheritanceContainer container = getInheritanceContainerCopy(minuendContainer);
                    container.setSimpleTypeMaxLength(new SimpleContentFixableRestrictionProperty<Integer>(subtrahendContainer.getSimpleTypeMinLength().getValue() - 1, false));
                    LinkedList<SimpleTypeInheritanceContainer> newContainers = generateNewSimpleTypeInheritanceContainerBase(container, subtrahendContainer);

                    if (newContainers != null) {
                        containers.addAll(newContainers);
                    }
                    container = getInheritanceContainerCopy(minuendContainer);
                    container.setSimpleTypeMinLength(new SimpleContentFixableRestrictionProperty<Integer>(subtrahendContainer.getSimpleTypeMaxLength().getValue() + 1, false));
                    newContainers = generateNewSimpleTypeInheritanceContainerBase(container, subtrahendContainer);

                    if (newContainers != null) {
                        containers.addAll(newContainers);
                    }
                    return containers;
                }

                if (subtrahendContainer.getSimpleTypeMinLength() != null) {

                    if (newContainer.getSimpleTypeMaxLength() == null || (newContainer.getSimpleTypeMaxLength().getValue() > subtrahendContainer.getSimpleTypeMinLength().getValue() - 1)) {

                        if (newContainer.getSimpleTypeMinLength() == null || (newContainer.getSimpleTypeMinLength().getValue() < subtrahendContainer.getSimpleTypeMinLength().getValue())) {
                            newContainer.setSimpleTypeMaxLength(new SimpleContentFixableRestrictionProperty<Integer>(subtrahendContainer.getSimpleTypeMinLength().getValue() - 1, subtrahendContainer.getSimpleTypeMinLength().getFixed()));
                        }
                    }
                }

                if (subtrahendContainer.getSimpleTypeMaxLength() != null) {

                    if (newContainer.getSimpleTypeMinLength() == null || (newContainer.getSimpleTypeMinLength().getValue() < subtrahendContainer.getSimpleTypeMaxLength().getValue() + 1)) {

                        if (newContainer.getSimpleTypeMaxLength() == null || (newContainer.getSimpleTypeMaxLength().getValue() > subtrahendContainer.getSimpleTypeMaxLength().getValue())) {
                            newContainer.setSimpleTypeMinLength(new SimpleContentFixableRestrictionProperty<Integer>(subtrahendContainer.getSimpleTypeMaxLength().getValue() + 1, subtrahendContainer.getSimpleTypeMaxLength().getFixed()));
                        }
                    }
                }
            }

            // If maxLength is smaller than minLength no difference is possible
            if (newContainer.getSimpleTypeMaxLength() != null && newContainer.getSimpleTypeMinLength() != null && newContainer.getSimpleTypeMaxLength().getValue() < newContainer.getSimpleTypeMinLength().getValue()) {
                return null;
            }

            // If minLength or maxLength differ from length the difference is empty
            if (newContainer.getSimpleTypeLength() != null && (newContainer.getSimpleTypeMaxLength() != null && newContainer.getSimpleTypeMaxLength().getValue() != newContainer.getSimpleTypeLength().getValue() || newContainer.getSimpleTypeMinLength() != null && newContainer.getSimpleTypeMinLength().getValue() != newContainer.getSimpleTypeLength().getValue())) {
                return null;
            }

            if (subtrahendContainer.getSimpleTypeTotalDigits() == newContainer.getSimpleTypeTotalDigits() || subtrahendContainer.getSimpleTypeTotalDigits().getValue() != newContainer.getSimpleTypeTotalDigits().getValue()) {

                if ((newContainer.getSimpleTypeFractionDigits() == subtrahendContainer.getSimpleTypeFractionDigits()) || (newContainer.getSimpleTypeFractionDigits() != null && (subtrahendContainer.getSimpleTypeFractionDigits() == null || newContainer.getSimpleTypeFractionDigits().getValue() <= subtrahendContainer.getSimpleTypeFractionDigits().getValue()))) {

                    if (((subtrahendContainer.getSimpleTypeMinExclusive() == null && subtrahendContainer.getSimpleTypeMinInclusive() == null) || (subtrahendContainer.getSimpleTypeMinExclusive() != null && newContainer.getSimpleTypeMinExclusive() != null && new Double(newContainer.getSimpleTypeMinExclusive().getValue()) >= new Double(subtrahendContainer.getSimpleTypeMinExclusive().getValue())) || (subtrahendContainer.getSimpleTypeMinExclusive() != null && newContainer.getSimpleTypeMinInclusive() != null && new Double(newContainer.getSimpleTypeMinInclusive().getValue()) > new Double(subtrahendContainer.getSimpleTypeMinExclusive().getValue())) || (subtrahendContainer.getSimpleTypeMinInclusive() != null && newContainer.getSimpleTypeMinExclusive() != null && new Double(newContainer.getSimpleTypeMinExclusive().getValue()) >= new Double(subtrahendContainer.getSimpleTypeMinInclusive().getValue())) || (subtrahendContainer.getSimpleTypeMinInclusive() != null && newContainer.getSimpleTypeMinInclusive() != null && new Double(newContainer.getSimpleTypeMinInclusive().getValue()) >= new Double(subtrahendContainer.getSimpleTypeMinInclusive().getValue()))) && ((subtrahendContainer.getSimpleTypeMaxExclusive() == null && subtrahendContainer.getSimpleTypeMaxInclusive() == null) || (subtrahendContainer.getSimpleTypeMaxExclusive() != null && newContainer.getSimpleTypeMaxExclusive() != null && new Double(newContainer.getSimpleTypeMaxExclusive().getValue()) <= new Double(subtrahendContainer.getSimpleTypeMaxExclusive().getValue())) || (subtrahendContainer.getSimpleTypeMaxExclusive() != null && newContainer.getSimpleTypeMaxInclusive() != null && new Double(newContainer.getSimpleTypeMaxInclusive().getValue()) < new Double(subtrahendContainer.getSimpleTypeMaxExclusive().getValue())) || (subtrahendContainer.getSimpleTypeMaxInclusive() != null && newContainer.getSimpleTypeMaxExclusive() != null && new Double(newContainer.getSimpleTypeMaxExclusive().getValue()) <= new Double(subtrahendContainer.getSimpleTypeMaxInclusive().getValue())) || (subtrahendContainer.getSimpleTypeMaxInclusive() != null && newContainer.getSimpleTypeMaxInclusive() != null && new Double(newContainer.getSimpleTypeMaxInclusive().getValue()) <= new Double(subtrahendContainer.getSimpleTypeMaxInclusive().getValue())))) {

                        if (subtrahendContainer.getSimpleTypeEnumeration().isEmpty() &&
                                subtrahendContainer.getSimpleTypeFractionDigits() == null &&
                                newContainer.getSimpleTypeLength() == null &&
                                newContainer.getSimpleTypeMaxLength() == null &&
                                newContainer.getSimpleTypeMinLength() == null &&
                                subtrahendContainer.getSimpleTypePatternList().isEmpty() &&
                                subtrahendContainer.getSimpleTypeTotalDigits() == null) {
                            return null;
                        }
                    }

                    if ((subtrahendContainer.getSimpleTypeMinExclusive() != null && ((newContainer.getSimpleTypeMinInclusive() == null && newContainer.getSimpleTypeMinExclusive() == null) || (newContainer.getSimpleTypeMinInclusive() != null && new Double(newContainer.getSimpleTypeMinInclusive().getValue()) <= new Double(subtrahendContainer.getSimpleTypeMinExclusive().getValue())))) && (subtrahendContainer.getSimpleTypeMaxExclusive() != null && ((newContainer.getSimpleTypeMaxInclusive() == null && newContainer.getSimpleTypeMaxExclusive() == null) || (newContainer.getSimpleTypeMaxInclusive() != null && new Double(newContainer.getSimpleTypeMaxInclusive().getValue()) >= new Double(subtrahendContainer.getSimpleTypeMaxExclusive().getValue()))))) {
                        LinkedList<SimpleTypeInheritanceContainer> containers = new LinkedList<SimpleTypeInheritanceContainer>();

                        SimpleTypeInheritanceContainer container = getInheritanceContainerCopy(minuendContainer);
                        container.setSimpleTypeMinInclusive(new SimpleContentFixableRestrictionProperty<String>(subtrahendContainer.getSimpleTypeMaxExclusive().getValue(), false));
                        container.setSimpleTypeMinExclusive(null);
                        LinkedList<SimpleTypeInheritanceContainer> newContainers = generateNewSimpleTypeInheritanceContainerBase(container, subtrahendContainer);

                        if (newContainers != null) {
                            containers.addAll(newContainers);
                        }
                        container = getInheritanceContainerCopy(minuendContainer);
                        container.setSimpleTypeMaxInclusive(new SimpleContentFixableRestrictionProperty<String>(subtrahendContainer.getSimpleTypeMinExclusive().getValue(), false));
                        container.setSimpleTypeMaxExclusive(null);
                        newContainers = generateNewSimpleTypeInheritanceContainerBase(container, subtrahendContainer);

                        if (newContainers != null) {
                            containers.addAll(newContainers);
                        }
                        return containers;
                    }

                    if ((subtrahendContainer.getSimpleTypeMinInclusive() != null && ((newContainer.getSimpleTypeMinInclusive() == null && newContainer.getSimpleTypeMinExclusive() == null) || (newContainer.getSimpleTypeMinInclusive() != null && new Double(newContainer.getSimpleTypeMinInclusive().getValue()) < new Double(subtrahendContainer.getSimpleTypeMinInclusive().getValue())))) && (subtrahendContainer.getSimpleTypeMaxExclusive() != null && ((newContainer.getSimpleTypeMaxInclusive() == null && newContainer.getSimpleTypeMaxExclusive() == null) || (newContainer.getSimpleTypeMaxInclusive() != null && new Double(newContainer.getSimpleTypeMaxInclusive().getValue()) >= new Double(subtrahendContainer.getSimpleTypeMaxExclusive().getValue()))))) {
                        LinkedList<SimpleTypeInheritanceContainer> containers = new LinkedList<SimpleTypeInheritanceContainer>();

                        SimpleTypeInheritanceContainer container = getInheritanceContainerCopy(minuendContainer);
                        container.setSimpleTypeMinExclusive(new SimpleContentFixableRestrictionProperty<String>(subtrahendContainer.getSimpleTypeMaxExclusive().getValue(), false));
                        container.setSimpleTypeMinInclusive(null);
                        LinkedList<SimpleTypeInheritanceContainer> newContainers = generateNewSimpleTypeInheritanceContainerBase(container, subtrahendContainer);

                        if (newContainers != null) {
                            containers.addAll(newContainers);
                        }
                        container = getInheritanceContainerCopy(minuendContainer);
                        container.setSimpleTypeMaxInclusive(new SimpleContentFixableRestrictionProperty<String>(subtrahendContainer.getSimpleTypeMinInclusive().getValue(), false));
                        container.setSimpleTypeMaxExclusive(null);
                        newContainers = generateNewSimpleTypeInheritanceContainerBase(container, subtrahendContainer);

                        if (newContainers != null) {
                            containers.addAll(newContainers);
                        }
                        return containers;
                    }

                    if ((subtrahendContainer.getSimpleTypeMinExclusive() != null && ((newContainer.getSimpleTypeMinInclusive() == null && newContainer.getSimpleTypeMinExclusive() == null) || (newContainer.getSimpleTypeMinInclusive() != null && new Double(newContainer.getSimpleTypeMinInclusive().getValue()) <= new Double(subtrahendContainer.getSimpleTypeMinExclusive().getValue())))) && (subtrahendContainer.getSimpleTypeMaxInclusive() != null && ((newContainer.getSimpleTypeMaxInclusive() == null && newContainer.getSimpleTypeMaxExclusive() == null) || (newContainer.getSimpleTypeMaxInclusive() != null && new Double(newContainer.getSimpleTypeMaxInclusive().getValue()) > new Double(subtrahendContainer.getSimpleTypeMaxInclusive().getValue()))))) {
                        LinkedList<SimpleTypeInheritanceContainer> containers = new LinkedList<SimpleTypeInheritanceContainer>();

                        SimpleTypeInheritanceContainer container = getInheritanceContainerCopy(minuendContainer);
                        container.setSimpleTypeMinInclusive(new SimpleContentFixableRestrictionProperty<String>(subtrahendContainer.getSimpleTypeMaxInclusive().getValue(), false));
                        container.setSimpleTypeMinExclusive(null);
                        LinkedList<SimpleTypeInheritanceContainer> newContainers = generateNewSimpleTypeInheritanceContainerBase(container, subtrahendContainer);

                        if (newContainers != null) {
                            containers.addAll(newContainers);
                        }
                        container = getInheritanceContainerCopy(minuendContainer);
                        container.setSimpleTypeMaxExclusive(new SimpleContentFixableRestrictionProperty<String>(subtrahendContainer.getSimpleTypeMinExclusive().getValue(), false));
                        container.setSimpleTypeMaxInclusive(null);
                        newContainers = generateNewSimpleTypeInheritanceContainerBase(container, subtrahendContainer);

                        if (newContainers != null) {
                            containers.addAll(newContainers);
                        }
                        return containers;
                    }

                    if ((subtrahendContainer.getSimpleTypeMinInclusive() != null && ((newContainer.getSimpleTypeMinInclusive() == null && newContainer.getSimpleTypeMinExclusive() == null) || (newContainer.getSimpleTypeMinInclusive() != null && new Double(newContainer.getSimpleTypeMinInclusive().getValue()) < new Double(subtrahendContainer.getSimpleTypeMinInclusive().getValue())))) && (subtrahendContainer.getSimpleTypeMaxInclusive() != null && ((newContainer.getSimpleTypeMaxInclusive() == null && newContainer.getSimpleTypeMaxExclusive() == null) || (newContainer.getSimpleTypeMaxInclusive() != null && new Double(newContainer.getSimpleTypeMaxInclusive().getValue()) > new Double(subtrahendContainer.getSimpleTypeMaxInclusive().getValue()))))) {
                        LinkedList<SimpleTypeInheritanceContainer> containers = new LinkedList<SimpleTypeInheritanceContainer>();

                        SimpleTypeInheritanceContainer container = getInheritanceContainerCopy(minuendContainer);
                        container.setSimpleTypeMinExclusive(new SimpleContentFixableRestrictionProperty<String>(subtrahendContainer.getSimpleTypeMaxInclusive().getValue(), false));
                        newContainer.setSimpleTypeMinInclusive(null);
                        LinkedList<SimpleTypeInheritanceContainer> newContainers = generateNewSimpleTypeInheritanceContainerBase(container, subtrahendContainer);

                        if (newContainers != null) {
                            containers.addAll(newContainers);
                        }
                        container = getInheritanceContainerCopy(minuendContainer);
                        container.setSimpleTypeMaxExclusive(new SimpleContentFixableRestrictionProperty<String>(subtrahendContainer.getSimpleTypeMinInclusive().getValue(), false));
                        newContainer.setSimpleTypeMaxInclusive(null);
                        newContainers = generateNewSimpleTypeInheritanceContainerBase(container, subtrahendContainer);

                        if (newContainers != null) {
                            containers.addAll(newContainers);
                        }
                        return containers;
                    }

                    if ((subtrahendContainer.getSimpleTypeMinExclusive() != null && ((newContainer.getSimpleTypeMinInclusive() == null && newContainer.getSimpleTypeMinExclusive() == null) || (newContainer.getSimpleTypeMinInclusive() != null && new Double(newContainer.getSimpleTypeMinInclusive().getValue()) <= new Double(subtrahendContainer.getSimpleTypeMinExclusive().getValue())))) && (subtrahendContainer.getSimpleTypeMaxExclusive() != null && ((newContainer.getSimpleTypeMaxInclusive() == null && newContainer.getSimpleTypeMaxExclusive() == null) || (newContainer.getSimpleTypeMaxExclusive() != null && new Double(newContainer.getSimpleTypeMaxExclusive().getValue()) > new Double(subtrahendContainer.getSimpleTypeMaxExclusive().getValue()))))) {
                        LinkedList<SimpleTypeInheritanceContainer> containers = new LinkedList<SimpleTypeInheritanceContainer>();

                        SimpleTypeInheritanceContainer container = getInheritanceContainerCopy(minuendContainer);
                        container.setSimpleTypeMinInclusive(new SimpleContentFixableRestrictionProperty<String>(subtrahendContainer.getSimpleTypeMaxExclusive().getValue(), false));
                        container.setSimpleTypeMinExclusive(null);
                        LinkedList<SimpleTypeInheritanceContainer> newContainers = generateNewSimpleTypeInheritanceContainerBase(container, subtrahendContainer);

                        if (newContainers != null) {
                            containers.addAll(newContainers);
                        }
                        container = getInheritanceContainerCopy(minuendContainer);
                        container.setSimpleTypeMaxInclusive(new SimpleContentFixableRestrictionProperty<String>(subtrahendContainer.getSimpleTypeMinExclusive().getValue(), false));
                        container.setSimpleTypeMaxExclusive(null);
                        newContainers = generateNewSimpleTypeInheritanceContainerBase(container, subtrahendContainer);

                        if (newContainers != null) {
                            containers.addAll(newContainers);
                        }
                        return containers;
                    }

                    if ((subtrahendContainer.getSimpleTypeMinInclusive() != null && ((newContainer.getSimpleTypeMinInclusive() == null && newContainer.getSimpleTypeMinExclusive() == null) || (newContainer.getSimpleTypeMinInclusive() != null && new Double(newContainer.getSimpleTypeMinInclusive().getValue()) < new Double(subtrahendContainer.getSimpleTypeMinInclusive().getValue())))) && (subtrahendContainer.getSimpleTypeMaxExclusive() != null && ((newContainer.getSimpleTypeMaxInclusive() == null && newContainer.getSimpleTypeMaxExclusive() == null) || (newContainer.getSimpleTypeMaxExclusive() != null && new Double(newContainer.getSimpleTypeMaxExclusive().getValue()) > new Double(subtrahendContainer.getSimpleTypeMaxExclusive().getValue()))))) {
                        LinkedList<SimpleTypeInheritanceContainer> containers = new LinkedList<SimpleTypeInheritanceContainer>();

                        SimpleTypeInheritanceContainer container = getInheritanceContainerCopy(minuendContainer);
                        container.setSimpleTypeMinInclusive(new SimpleContentFixableRestrictionProperty<String>(subtrahendContainer.getSimpleTypeMaxExclusive().getValue(), false));
                        container.setSimpleTypeMinExclusive(null);
                        LinkedList<SimpleTypeInheritanceContainer> newContainers = generateNewSimpleTypeInheritanceContainerBase(container, subtrahendContainer);

                        if (newContainers != null) {
                            containers.addAll(newContainers);
                        }
                        container = getInheritanceContainerCopy(minuendContainer);
                        container.setSimpleTypeMaxExclusive(new SimpleContentFixableRestrictionProperty<String>(subtrahendContainer.getSimpleTypeMinInclusive().getValue(), false));
                        container.setSimpleTypeMaxInclusive(null);
                        newContainers = generateNewSimpleTypeInheritanceContainerBase(container, subtrahendContainer);

                        if (newContainers != null) {
                            containers.addAll(newContainers);
                        }
                        return containers;
                    }

                    if ((subtrahendContainer.getSimpleTypeMinExclusive() != null && ((newContainer.getSimpleTypeMinInclusive() == null && newContainer.getSimpleTypeMinExclusive() == null) || (newContainer.getSimpleTypeMinInclusive() != null && new Double(newContainer.getSimpleTypeMinInclusive().getValue()) <= new Double(subtrahendContainer.getSimpleTypeMinExclusive().getValue())))) && (subtrahendContainer.getSimpleTypeMaxInclusive() != null && ((newContainer.getSimpleTypeMaxInclusive() == null && newContainer.getSimpleTypeMaxExclusive() == null) || (newContainer.getSimpleTypeMaxExclusive() != null && new Double(newContainer.getSimpleTypeMaxExclusive().getValue()) > new Double(subtrahendContainer.getSimpleTypeMaxInclusive().getValue()))))) {
                        LinkedList<SimpleTypeInheritanceContainer> containers = new LinkedList<SimpleTypeInheritanceContainer>();

                        SimpleTypeInheritanceContainer container = getInheritanceContainerCopy(minuendContainer);
                        container.setSimpleTypeMinExclusive(new SimpleContentFixableRestrictionProperty<String>(subtrahendContainer.getSimpleTypeMaxInclusive().getValue(), false));
                        container.setSimpleTypeMinInclusive(null);
                        LinkedList<SimpleTypeInheritanceContainer> newContainers = generateNewSimpleTypeInheritanceContainerBase(container, subtrahendContainer);

                        if (newContainers != null) {
                            containers.addAll(newContainers);
                        }
                        container = getInheritanceContainerCopy(minuendContainer);
                        container.setSimpleTypeMaxInclusive(new SimpleContentFixableRestrictionProperty<String>(subtrahendContainer.getSimpleTypeMinExclusive().getValue(), false));
                        container.setSimpleTypeMaxExclusive(null);
                        newContainers = generateNewSimpleTypeInheritanceContainerBase(container, subtrahendContainer);

                        if (newContainers != null) {
                            containers.addAll(newContainers);
                        }
                        return containers;
                    }

                    if ((subtrahendContainer.getSimpleTypeMinInclusive() != null && ((newContainer.getSimpleTypeMinInclusive() == null && newContainer.getSimpleTypeMinExclusive() == null) || (newContainer.getSimpleTypeMinInclusive() != null && new Double(newContainer.getSimpleTypeMinInclusive().getValue()) < new Double(subtrahendContainer.getSimpleTypeMinInclusive().getValue())))) && (subtrahendContainer.getSimpleTypeMaxInclusive() != null && ((newContainer.getSimpleTypeMaxInclusive() == null && newContainer.getSimpleTypeMaxExclusive() == null) || (newContainer.getSimpleTypeMaxExclusive() != null && new Double(newContainer.getSimpleTypeMaxExclusive().getValue()) > new Double(subtrahendContainer.getSimpleTypeMaxInclusive().getValue()))))) {
                        LinkedList<SimpleTypeInheritanceContainer> containers = new LinkedList<SimpleTypeInheritanceContainer>();

                        SimpleTypeInheritanceContainer container = getInheritanceContainerCopy(minuendContainer);
                        container.setSimpleTypeMinExclusive(new SimpleContentFixableRestrictionProperty<String>(subtrahendContainer.getSimpleTypeMaxInclusive().getValue(), false));
                        container.setSimpleTypeMinInclusive(null);
                        LinkedList<SimpleTypeInheritanceContainer> newContainers = generateNewSimpleTypeInheritanceContainerBase(container, subtrahendContainer);

                        if (newContainers != null) {
                            containers.addAll(newContainers);
                        }
                        container = getInheritanceContainerCopy(minuendContainer);
                        container.setSimpleTypeMaxExclusive(new SimpleContentFixableRestrictionProperty<String>(subtrahendContainer.getSimpleTypeMinInclusive().getValue(), false));
                        container.setSimpleTypeMaxInclusive(null);
                        newContainers = generateNewSimpleTypeInheritanceContainerBase(container, subtrahendContainer);

                        if (newContainers != null) {
                            containers.addAll(newContainers);
                        }
                        return containers;
                    }

                    if ((subtrahendContainer.getSimpleTypeMinExclusive() != null && ((newContainer.getSimpleTypeMinInclusive() == null && newContainer.getSimpleTypeMinExclusive() == null) || (newContainer.getSimpleTypeMinExclusive() != null && new Double(newContainer.getSimpleTypeMinExclusive().getValue()) < new Double(subtrahendContainer.getSimpleTypeMinExclusive().getValue())))) && (subtrahendContainer.getSimpleTypeMaxExclusive() != null && ((newContainer.getSimpleTypeMaxInclusive() == null && newContainer.getSimpleTypeMaxExclusive() == null) || (newContainer.getSimpleTypeMaxInclusive() != null && new Double(newContainer.getSimpleTypeMaxInclusive().getValue()) >= new Double(subtrahendContainer.getSimpleTypeMaxExclusive().getValue()))))) {
                        LinkedList<SimpleTypeInheritanceContainer> containers = new LinkedList<SimpleTypeInheritanceContainer>();

                        SimpleTypeInheritanceContainer container = getInheritanceContainerCopy(minuendContainer);
                        container.setSimpleTypeMinInclusive(new SimpleContentFixableRestrictionProperty<String>(subtrahendContainer.getSimpleTypeMaxExclusive().getValue(), false));
                        container.setSimpleTypeMinExclusive(null);
                        LinkedList<SimpleTypeInheritanceContainer> newContainers = generateNewSimpleTypeInheritanceContainerBase(container, subtrahendContainer);

                        if (newContainers != null) {
                            containers.addAll(newContainers);
                        }
                        container = getInheritanceContainerCopy(minuendContainer);
                        container.setSimpleTypeMaxInclusive(new SimpleContentFixableRestrictionProperty<String>(subtrahendContainer.getSimpleTypeMinExclusive().getValue(), false));
                        container.setSimpleTypeMaxExclusive(null);
                        newContainers = generateNewSimpleTypeInheritanceContainerBase(container, subtrahendContainer);

                        if (newContainers != null) {
                            containers.addAll(newContainers);
                        }
                        return containers;
                    }

                    if ((subtrahendContainer.getSimpleTypeMinInclusive() != null && ((newContainer.getSimpleTypeMinInclusive() == null && newContainer.getSimpleTypeMinExclusive() == null) || (newContainer.getSimpleTypeMinExclusive() != null && new Double(newContainer.getSimpleTypeMinExclusive().getValue()) < new Double(subtrahendContainer.getSimpleTypeMinInclusive().getValue())))) && (subtrahendContainer.getSimpleTypeMaxExclusive() != null && ((newContainer.getSimpleTypeMaxInclusive() == null && newContainer.getSimpleTypeMaxExclusive() == null) || (newContainer.getSimpleTypeMaxInclusive() != null && new Double(newContainer.getSimpleTypeMaxInclusive().getValue()) >= new Double(subtrahendContainer.getSimpleTypeMaxExclusive().getValue()))))) {
                        LinkedList<SimpleTypeInheritanceContainer> containers = new LinkedList<SimpleTypeInheritanceContainer>();

                        SimpleTypeInheritanceContainer container = getInheritanceContainerCopy(minuendContainer);
                        container.setSimpleTypeMinInclusive(new SimpleContentFixableRestrictionProperty<String>(subtrahendContainer.getSimpleTypeMaxExclusive().getValue(), false));
                        container.setSimpleTypeMinExclusive(null);
                        LinkedList<SimpleTypeInheritanceContainer> newContainers = generateNewSimpleTypeInheritanceContainerBase(container, subtrahendContainer);

                        if (newContainers != null) {
                            containers.addAll(newContainers);
                        }
                        container = getInheritanceContainerCopy(minuendContainer);
                        container.setSimpleTypeMaxExclusive(new SimpleContentFixableRestrictionProperty<String>(subtrahendContainer.getSimpleTypeMinInclusive().getValue(), false));
                        container.setSimpleTypeMaxInclusive(null);
                        newContainers = generateNewSimpleTypeInheritanceContainerBase(container, subtrahendContainer);

                        if (newContainers != null) {
                            containers.addAll(newContainers);
                        }
                        return containers;
                    }

                    if ((subtrahendContainer.getSimpleTypeMinExclusive() != null && ((newContainer.getSimpleTypeMinInclusive() == null && newContainer.getSimpleTypeMinExclusive() == null) || (newContainer.getSimpleTypeMinExclusive() != null && new Double(newContainer.getSimpleTypeMinExclusive().getValue()) < new Double(subtrahendContainer.getSimpleTypeMinExclusive().getValue())))) && (subtrahendContainer.getSimpleTypeMaxInclusive() != null && ((newContainer.getSimpleTypeMaxInclusive() == null && newContainer.getSimpleTypeMaxExclusive() == null) || (newContainer.getSimpleTypeMaxInclusive() != null && new Double(newContainer.getSimpleTypeMaxInclusive().getValue()) > new Double(subtrahendContainer.getSimpleTypeMaxInclusive().getValue()))))) {
                        LinkedList<SimpleTypeInheritanceContainer> containers = new LinkedList<SimpleTypeInheritanceContainer>();

                        SimpleTypeInheritanceContainer container = getInheritanceContainerCopy(minuendContainer);
                        container.setSimpleTypeMinExclusive(new SimpleContentFixableRestrictionProperty<String>(subtrahendContainer.getSimpleTypeMaxInclusive().getValue(), false));
                        container.setSimpleTypeMinInclusive(null);
                        LinkedList<SimpleTypeInheritanceContainer> newContainers = generateNewSimpleTypeInheritanceContainerBase(container, subtrahendContainer);

                        if (newContainers != null) {
                            containers.addAll(newContainers);
                        }
                        container = getInheritanceContainerCopy(minuendContainer);
                        container.setSimpleTypeMaxInclusive(new SimpleContentFixableRestrictionProperty<String>(subtrahendContainer.getSimpleTypeMinExclusive().getValue(), false));
                        container.setSimpleTypeMaxInclusive(null);
                        newContainers = generateNewSimpleTypeInheritanceContainerBase(container, subtrahendContainer);

                        if (newContainers != null) {
                            containers.addAll(newContainers);
                        }
                        return containers;
                    }

                    if ((subtrahendContainer.getSimpleTypeMinInclusive() != null && ((newContainer.getSimpleTypeMinInclusive() == null && newContainer.getSimpleTypeMinExclusive() == null) || (newContainer.getSimpleTypeMinExclusive() != null && new Double(newContainer.getSimpleTypeMinExclusive().getValue()) < new Double(subtrahendContainer.getSimpleTypeMinInclusive().getValue())))) && (subtrahendContainer.getSimpleTypeMaxInclusive() != null && ((newContainer.getSimpleTypeMaxInclusive() == null && newContainer.getSimpleTypeMaxExclusive() == null) || (newContainer.getSimpleTypeMaxInclusive() != null && new Double(newContainer.getSimpleTypeMaxInclusive().getValue()) > new Double(subtrahendContainer.getSimpleTypeMaxInclusive().getValue()))))) {
                        LinkedList<SimpleTypeInheritanceContainer> containers = new LinkedList<SimpleTypeInheritanceContainer>();

                        SimpleTypeInheritanceContainer container = getInheritanceContainerCopy(minuendContainer);
                        container.setSimpleTypeMinExclusive(new SimpleContentFixableRestrictionProperty<String>(subtrahendContainer.getSimpleTypeMaxInclusive().getValue(), false));
                        container.setSimpleTypeMinInclusive(null);
                        LinkedList<SimpleTypeInheritanceContainer> newContainers = generateNewSimpleTypeInheritanceContainerBase(container, subtrahendContainer);

                        if (newContainers != null) {
                            containers.addAll(newContainers);
                        }
                        container = getInheritanceContainerCopy(minuendContainer);
                        container.setSimpleTypeMaxExclusive(new SimpleContentFixableRestrictionProperty<String>(subtrahendContainer.getSimpleTypeMinInclusive().getValue(), false));
                        container.setSimpleTypeMaxInclusive(null);
                        newContainers = generateNewSimpleTypeInheritanceContainerBase(container, subtrahendContainer);

                        if (newContainers != null) {
                            containers.addAll(newContainers);
                        }
                        return containers;
                    }

                    if ((subtrahendContainer.getSimpleTypeMinExclusive() != null && ((newContainer.getSimpleTypeMinInclusive() == null && newContainer.getSimpleTypeMinExclusive() == null) || (newContainer.getSimpleTypeMinExclusive() != null && new Double(newContainer.getSimpleTypeMinExclusive().getValue()) < new Double(subtrahendContainer.getSimpleTypeMinExclusive().getValue())))) && (subtrahendContainer.getSimpleTypeMaxExclusive() != null && ((newContainer.getSimpleTypeMaxInclusive() == null && newContainer.getSimpleTypeMaxExclusive() == null) || (newContainer.getSimpleTypeMaxExclusive() != null && new Double(newContainer.getSimpleTypeMaxExclusive().getValue()) > new Double(subtrahendContainer.getSimpleTypeMaxExclusive().getValue()))))) {
                        LinkedList<SimpleTypeInheritanceContainer> containers = new LinkedList<SimpleTypeInheritanceContainer>();

                        SimpleTypeInheritanceContainer container = getInheritanceContainerCopy(minuendContainer);
                        container.setSimpleTypeMinInclusive(new SimpleContentFixableRestrictionProperty<String>(subtrahendContainer.getSimpleTypeMaxExclusive().getValue(), false));
                        container.setSimpleTypeMinExclusive(null);
                        LinkedList<SimpleTypeInheritanceContainer> newContainers = generateNewSimpleTypeInheritanceContainerBase(container, subtrahendContainer);

                        if (newContainers != null) {
                            containers.addAll(newContainers);
                        }
                        container = getInheritanceContainerCopy(minuendContainer);
                        container.setSimpleTypeMaxInclusive(new SimpleContentFixableRestrictionProperty<String>(subtrahendContainer.getSimpleTypeMinExclusive().getValue(), false));
                        container.setSimpleTypeMaxExclusive(null);
                        newContainers = generateNewSimpleTypeInheritanceContainerBase(container, subtrahendContainer);

                        if (newContainers != null) {
                            containers.addAll(newContainers);
                        }
                        return containers;
                    }

                    if ((subtrahendContainer.getSimpleTypeMinInclusive() != null && ((newContainer.getSimpleTypeMinInclusive() == null && newContainer.getSimpleTypeMinExclusive() == null) || (newContainer.getSimpleTypeMinExclusive() != null && new Double(newContainer.getSimpleTypeMinExclusive().getValue()) < new Double(subtrahendContainer.getSimpleTypeMinInclusive().getValue())))) && (subtrahendContainer.getSimpleTypeMaxExclusive() != null && ((newContainer.getSimpleTypeMaxInclusive() == null && newContainer.getSimpleTypeMaxExclusive() == null) || (newContainer.getSimpleTypeMaxExclusive() != null && new Double(newContainer.getSimpleTypeMaxExclusive().getValue()) > new Double(subtrahendContainer.getSimpleTypeMaxExclusive().getValue()))))) {
                        LinkedList<SimpleTypeInheritanceContainer> containers = new LinkedList<SimpleTypeInheritanceContainer>();

                        SimpleTypeInheritanceContainer container = getInheritanceContainerCopy(minuendContainer);
                        container.setSimpleTypeMinInclusive(new SimpleContentFixableRestrictionProperty<String>(subtrahendContainer.getSimpleTypeMaxExclusive().getValue(), false));
                        container.setSimpleTypeMinExclusive(null);
                        LinkedList<SimpleTypeInheritanceContainer> newContainers = generateNewSimpleTypeInheritanceContainerBase(container, subtrahendContainer);

                        if (newContainers != null) {
                            containers.addAll(newContainers);
                        }
                        container = getInheritanceContainerCopy(minuendContainer);
                        container.setSimpleTypeMaxExclusive(new SimpleContentFixableRestrictionProperty<String>(subtrahendContainer.getSimpleTypeMinInclusive().getValue(), false));
                        container.setSimpleTypeMaxInclusive(null);
                        newContainers = generateNewSimpleTypeInheritanceContainerBase(container, subtrahendContainer);

                        if (newContainers != null) {
                            containers.addAll(newContainers);
                        }
                        return containers;
                    }

                    if ((subtrahendContainer.getSimpleTypeMinExclusive() != null && ((newContainer.getSimpleTypeMinInclusive() == null && newContainer.getSimpleTypeMinExclusive() == null) || (newContainer.getSimpleTypeMinExclusive() != null && new Double(newContainer.getSimpleTypeMinExclusive().getValue()) < new Double(subtrahendContainer.getSimpleTypeMinExclusive().getValue())))) && (subtrahendContainer.getSimpleTypeMaxInclusive() != null && ((newContainer.getSimpleTypeMaxInclusive() == null && newContainer.getSimpleTypeMaxExclusive() == null) || (newContainer.getSimpleTypeMaxExclusive() != null && new Double(newContainer.getSimpleTypeMaxExclusive().getValue()) > new Double(subtrahendContainer.getSimpleTypeMaxInclusive().getValue()))))) {
                        LinkedList<SimpleTypeInheritanceContainer> containers = new LinkedList<SimpleTypeInheritanceContainer>();

                        SimpleTypeInheritanceContainer container = getInheritanceContainerCopy(minuendContainer);
                        container.setSimpleTypeMinExclusive(new SimpleContentFixableRestrictionProperty<String>(subtrahendContainer.getSimpleTypeMaxInclusive().getValue(), false));
                        container.setSimpleTypeMinInclusive(null);
                        LinkedList<SimpleTypeInheritanceContainer> newContainers = generateNewSimpleTypeInheritanceContainerBase(container, subtrahendContainer);

                        if (newContainers != null) {
                            containers.addAll(newContainers);
                        }
                        container = getInheritanceContainerCopy(minuendContainer);
                        container.setSimpleTypeMaxInclusive(new SimpleContentFixableRestrictionProperty<String>(subtrahendContainer.getSimpleTypeMinExclusive().getValue(), false));
                        container.setSimpleTypeMaxExclusive(null);
                        newContainers = generateNewSimpleTypeInheritanceContainerBase(container, subtrahendContainer);

                        if (newContainers != null) {
                            containers.addAll(newContainers);
                        }
                        return containers;
                    }

                    if ((subtrahendContainer.getSimpleTypeMinInclusive() != null && ((newContainer.getSimpleTypeMinInclusive() == null && newContainer.getSimpleTypeMinExclusive() == null) || (newContainer.getSimpleTypeMinExclusive() != null && new Double(newContainer.getSimpleTypeMinExclusive().getValue()) < new Double(subtrahendContainer.getSimpleTypeMinInclusive().getValue())))) && (subtrahendContainer.getSimpleTypeMaxInclusive() != null && ((newContainer.getSimpleTypeMaxInclusive() == null && newContainer.getSimpleTypeMaxExclusive() == null) || (newContainer.getSimpleTypeMaxExclusive() != null && new Double(newContainer.getSimpleTypeMaxExclusive().getValue()) > new Double(subtrahendContainer.getSimpleTypeMaxInclusive().getValue()))))) {
                        LinkedList<SimpleTypeInheritanceContainer> containers = new LinkedList<SimpleTypeInheritanceContainer>();

                        SimpleTypeInheritanceContainer container = getInheritanceContainerCopy(minuendContainer);
                        container.setSimpleTypeMinExclusive(new SimpleContentFixableRestrictionProperty<String>(subtrahendContainer.getSimpleTypeMaxInclusive().getValue(), false));
                        container.setSimpleTypeMinInclusive(null);
                        LinkedList<SimpleTypeInheritanceContainer> newContainers = generateNewSimpleTypeInheritanceContainerBase(container, subtrahendContainer);

                        if (newContainers != null) {
                            containers.addAll(newContainers);
                        }
                        container = getInheritanceContainerCopy(minuendContainer);
                        container.setSimpleTypeMaxExclusive(new SimpleContentFixableRestrictionProperty<String>(subtrahendContainer.getSimpleTypeMinInclusive().getValue(), false));
                        container.setSimpleTypeMaxInclusive(null);
                        newContainers = generateNewSimpleTypeInheritanceContainerBase(container, subtrahendContainer);

                        if (newContainers != null) {
                            containers.addAll(newContainers);
                        }
                        return containers;
                    }

                    if (subtrahendContainer.getSimpleTypeMinInclusive() != null) {

                        if ((newContainer.getSimpleTypeMaxExclusive() == null || (new Double(newContainer.getSimpleTypeMaxExclusive().getValue()) > new Double(subtrahendContainer.getSimpleTypeMinInclusive().getValue()))) && (newContainer.getSimpleTypeMaxInclusive() == null || (new Double(newContainer.getSimpleTypeMaxInclusive().getValue()) >= new Double(subtrahendContainer.getSimpleTypeMinInclusive().getValue())))) {

                            if ((newContainer.getSimpleTypeMinInclusive() == null || (new Double(newContainer.getSimpleTypeMinInclusive().getValue()) < new Double(subtrahendContainer.getSimpleTypeMinInclusive().getValue()))) &&
                                    (newContainer.getSimpleTypeMinExclusive() == null || (new Double(newContainer.getSimpleTypeMinExclusive().getValue()) < new Double(subtrahendContainer.getSimpleTypeMinInclusive().getValue())))) {
                                newContainer.setSimpleTypeMaxExclusive(new SimpleContentFixableRestrictionProperty<String>(subtrahendContainer.getSimpleTypeMinInclusive().getValue(), subtrahendContainer.getSimpleTypeMinInclusive().getFixed()));
                                newContainer.setSimpleTypeMaxInclusive(null);
                            }
                        }
                    }

                    if (subtrahendContainer.getSimpleTypeMaxInclusive() != null) {

                        if ((newContainer.getSimpleTypeMinExclusive() == null || (new Double(newContainer.getSimpleTypeMinExclusive().getValue()) < new Double(subtrahendContainer.getSimpleTypeMaxInclusive().getValue()))) && (newContainer.getSimpleTypeMinInclusive() == null || (new Double(newContainer.getSimpleTypeMinInclusive().getValue()) <= new Double(subtrahendContainer.getSimpleTypeMaxInclusive().getValue())))) {

                            if ((newContainer.getSimpleTypeMaxInclusive() == null || (new Double(newContainer.getSimpleTypeMaxInclusive().getValue()) > new Double(subtrahendContainer.getSimpleTypeMaxInclusive().getValue()))) &&
                                    (newContainer.getSimpleTypeMaxExclusive() == null || (new Double(newContainer.getSimpleTypeMaxExclusive().getValue()) > new Double(subtrahendContainer.getSimpleTypeMaxInclusive().getValue())))) {
                                newContainer.setSimpleTypeMinExclusive(new SimpleContentFixableRestrictionProperty<String>(subtrahendContainer.getSimpleTypeMaxInclusive().getValue(), subtrahendContainer.getSimpleTypeMaxInclusive().getFixed()));
                                newContainer.setSimpleTypeMinInclusive(null);
                            }
                        }
                    }

                    if (subtrahendContainer.getSimpleTypeMinExclusive() != null) {

                        if ((newContainer.getSimpleTypeMaxExclusive() == null || (new Double(newContainer.getSimpleTypeMaxExclusive().getValue()) > new Double(subtrahendContainer.getSimpleTypeMinExclusive().getValue()))) && (newContainer.getSimpleTypeMaxInclusive() == null || (new Double(newContainer.getSimpleTypeMaxInclusive().getValue()) >= new Double(subtrahendContainer.getSimpleTypeMinExclusive().getValue())))) {

                            if ((newContainer.getSimpleTypeMinInclusive() == null || (new Double(newContainer.getSimpleTypeMinInclusive().getValue()) <= new Double(subtrahendContainer.getSimpleTypeMinExclusive().getValue()))) &&
                                    (newContainer.getSimpleTypeMinExclusive() == null || (new Double(newContainer.getSimpleTypeMinExclusive().getValue()) < new Double(subtrahendContainer.getSimpleTypeMinExclusive().getValue())))) {
                                newContainer.setSimpleTypeMaxInclusive(new SimpleContentFixableRestrictionProperty<String>(subtrahendContainer.getSimpleTypeMinExclusive().getValue(), subtrahendContainer.getSimpleTypeMinExclusive().getFixed()));
                                newContainer.setSimpleTypeMaxExclusive(null);
                            }
                        }
                    }

                    if (subtrahendContainer.getSimpleTypeMaxExclusive() != null) {

                        if ((newContainer.getSimpleTypeMinExclusive() == null || (new Double(newContainer.getSimpleTypeMinExclusive().getValue()) < new Double(subtrahendContainer.getSimpleTypeMaxExclusive().getValue()))) && (newContainer.getSimpleTypeMinInclusive() == null || (new Double(newContainer.getSimpleTypeMinInclusive().getValue()) <= new Double(subtrahendContainer.getSimpleTypeMaxExclusive().getValue())))) {

                            if ((newContainer.getSimpleTypeMaxInclusive() == null || (new Double(newContainer.getSimpleTypeMaxInclusive().getValue()) >= new Double(subtrahendContainer.getSimpleTypeMaxExclusive().getValue()))) &&
                                    (newContainer.getSimpleTypeMaxExclusive() == null || (new Double(newContainer.getSimpleTypeMaxExclusive().getValue()) > new Double(subtrahendContainer.getSimpleTypeMaxExclusive().getValue())))) {
                                newContainer.setSimpleTypeMinInclusive(new SimpleContentFixableRestrictionProperty<String>(subtrahendContainer.getSimpleTypeMaxExclusive().getValue(), subtrahendContainer.getSimpleTypeMaxExclusive().getFixed()));
                                newContainer.setSimpleTypeMinExclusive(null);
                            }
                        }
                    }

                    // If maxInclusive is smaller than minInclusive no intersection is possible
                    if (newContainer.getSimpleTypeMaxInclusive() != null && newContainer.getSimpleTypeMinInclusive() != null && new Double(newContainer.getSimpleTypeMaxInclusive().getValue()) < new Double(newContainer.getSimpleTypeMinInclusive().getValue())) {
                        return null;
                    }

                    // If maxInclusive is smaller equal than minExclusive no intersection is possible
                    if (newContainer.getSimpleTypeMaxInclusive() != null && newContainer.getSimpleTypeMinExclusive() != null && new Double(newContainer.getSimpleTypeMaxInclusive().getValue()) <= new Double(newContainer.getSimpleTypeMinExclusive().getValue())) {
                        return null;
                    }

                    // If maxExclusive is smaller equal than minInclusive no intersection is possible
                    if (newContainer.getSimpleTypeMaxExclusive() != null && newContainer.getSimpleTypeMinInclusive() != null && new Double(newContainer.getSimpleTypeMaxExclusive().getValue()) <= new Double(newContainer.getSimpleTypeMinInclusive().getValue())) {
                        return null;
                    }

                    // If maxExclusive is smaller equal than minExclusive no intersection is possible
                    if (newContainer.getSimpleTypeMaxExclusive() != null && newContainer.getSimpleTypeMinExclusive() != null && new Double(newContainer.getSimpleTypeMaxExclusive().getValue()) <= new Double(newContainer.getSimpleTypeMinExclusive().getValue())) {
                        return null;
                    }
                }
            }
        }
        LinkedList<SimpleTypeInheritanceContainer> newContainers = new LinkedList<SimpleTypeInheritanceContainer>();

        if (newContainer != null) {
            newContainers.add(newContainer);
        }
        return newContainers;
    }

    private LinkedList<SimpleTypeInheritanceContainer> generateNewSimpleTypeInheritanceContainerList(SimpleTypeInheritanceContainer minuendContainer, SimpleTypeInheritanceContainer subtrahendContainer) {
        SimpleTypeInheritanceContainer newContainer = getInheritanceContainerCopy(minuendContainer);

        // Check if list is contaiend
        if (minuendContainer.isHasList()) {

            // Add SimpleContentList to list of SimpleContentLists in order to find a new list
            if (!subtrahendContainer.getSimpleContentLists().isEmpty()) {
                for (Iterator<SimpleContentList> it3 = subtrahendContainer.getSimpleContentLists().iterator(); it3.hasNext();) {
                    SimpleContentList simpleContentList = it3.next();

                    if (simpleContentList != null) {
                        newContainer.getSimpleContentLists().add(simpleContentList);
                    }
                }
            }

            // Remove null entries from pattern list
            for (Iterator<SimpleContentRestrictionProperty<String>> it = subtrahendContainer.getSimpleTypePatternList().iterator(); it.hasNext();) {
                SimpleContentRestrictionProperty<String> simpleContentRestrictionProperty = it.next();

                if (simpleContentRestrictionProperty == null) {
                    it.remove();
                }
            }

            // Patterns of different restricitions are added to the pattern list
            if (!subtrahendContainer.getListPatternList().isEmpty()) {

                for (Iterator<SimpleContentRestrictionProperty<String>> it3 = subtrahendContainer.getListPatternList().iterator(); it3.hasNext();) {
                    SimpleContentRestrictionProperty<String> subtrahendPattern = it3.next();

                    for (Iterator<SimpleContentRestrictionProperty<String>> it4 = newContainer.getListPatternList().iterator(); it4.hasNext();) {
                        SimpleContentRestrictionProperty<String> newPattern = it4.next();

                        if (newPattern != null && newPattern.getValue().equals(subtrahendPattern.getValue())) {
                            return null;
                        }
                    }
                }
            }

            // If enum list is empty and the current container contains enums add these to the list, else if the list is not empty check all enums contained in both list and remove them.
            if (!subtrahendContainer.getListEnumeration().isEmpty()) {

                if (newContainer.getListEnumeration().isEmpty()) {
                    return generateNewSimpleTypeInheritanceContainerList(minuendContainer, new SimpleTypeInheritanceContainer());
                } else {
                    LinkedList<String> newEnumeration = new LinkedList<String>();
                    for (Iterator<String> it3 = subtrahendContainer.getListEnumeration().iterator(); it3.hasNext();) {
                        String enumeration = it3.next();

                        if (newContainer.getListEnumeration().contains(enumeration) && !subtrahendContainer.getListEnumeration().contains(enumeration)) {
                            newEnumeration.add(enumeration);
                        }
                    }
                    if (newEnumeration.isEmpty()) {
                        return null;
                    }
                    newContainer.setListEnumeration(newEnumeration);
                }
            }

            // Set whitespace to the most restrictive value
            if (subtrahendContainer.getListWhitespace() != null) {

                if (subtrahendContainer.getListWhitespace().getValue() == SimpleContentPropertyWhitespace.Collapse) {
                    newContainer.setListWhitespace(subtrahendContainer.getListWhitespace());
                }
                if (subtrahendContainer.getListWhitespace().getValue() == SimpleContentPropertyWhitespace.Replace && (newContainer.getListWhitespace() == null || newContainer.getListWhitespace().getValue() == SimpleContentPropertyWhitespace.Preserve)) {
                    newContainer.setListWhitespace(subtrahendContainer.getListWhitespace());
                }
                if (subtrahendContainer.getListWhitespace().getValue() == SimpleContentPropertyWhitespace.Preserve && newContainer.getListWhitespace() == null) {
                    newContainer.setListWhitespace(subtrahendContainer.getListWhitespace());
                }
            }


            // If a length value is present in the new container and the new and old value are the same no difference is possible
            if (subtrahendContainer.getListLength() != null) {

                // Check if new length components are neccessary
                if (newContainer.getListLength() != null && newContainer.getListLength().getValue() != subtrahendContainer.getListLength().getValue()) {
                    return null;
                }
                if (newContainer.getListMinLength() != null && newContainer.getListMinLength().getValue() == subtrahendContainer.getListLength().getValue()) {
                    newContainer.setListMinLength(new SimpleContentFixableRestrictionProperty<Integer>(subtrahendContainer.getListLength().getValue() + 1, subtrahendContainer.getListLength().getFixed()));
                }
                if (newContainer.getListMaxLength() != null && subtrahendContainer.getListLength().getValue() == newContainer.getListMaxLength().getValue()) {
                    newContainer.setListMaxLength(new SimpleContentFixableRestrictionProperty<Integer>(subtrahendContainer.getListLength().getValue() - 1, subtrahendContainer.getListLength().getFixed()));
                }

                // Check if container has to be split
                if ((newContainer.getListMinLength() == null || (newContainer.getListMinLength() != null && newContainer.getListMinLength().getValue() < subtrahendContainer.getListLength().getValue())) &&
                        (newContainer.getListMaxLength() == null || (newContainer.getListMaxLength() != null && subtrahendContainer.getListLength().getValue() < newContainer.getListMaxLength().getValue()))) {

                    // Get new container list
                    LinkedList<SimpleTypeInheritanceContainer> containers = new LinkedList<SimpleTypeInheritanceContainer>();

                    // Build first container
                    SimpleTypeInheritanceContainer container = getInheritanceContainerCopy(minuendContainer);
                    container.setListMaxLength(new SimpleContentFixableRestrictionProperty<Integer>(subtrahendContainer.getListLength().getValue() - 1, false));
                    LinkedList<SimpleTypeInheritanceContainer> newContainers = generateNewSimpleTypeInheritanceContainerList(container, subtrahendContainer);

                    if (newContainers != null) {
                        containers.addAll(newContainers);
                    }

                    // Build second container
                    container = getInheritanceContainerCopy(minuendContainer);
                    container.setListMinLength(new SimpleContentFixableRestrictionProperty<Integer>(subtrahendContainer.getListLength().getValue() + 1, false));
                    newContainers = generateNewSimpleTypeInheritanceContainerList(container, subtrahendContainer);

                    if (newContainers != null) {
                        containers.addAll(newContainers);
                    }

                    // Return new containers
                    return containers;
                }
            }

            // Check if minuend restriction contains length facet
            if (newContainer.getListLength() != null) {

                // If minuend length is contained in subtrahend container return null
                if ((subtrahendContainer.getListMinLength() == null || newContainer.getListLength().getValue() >= subtrahendContainer.getListMinLength().getValue()) && (subtrahendContainer.getListMaxLength() == null || newContainer.getListLength().getValue() <= subtrahendContainer.getListMaxLength().getValue())) {
                    return null;
                }
            } else {

                // Check if minunend container is contained in subtrahend container
                if ((subtrahendContainer.getListMinLength() == null || (newContainer.getListMinLength() != null && newContainer.getListMinLength().getValue() >= subtrahendContainer.getListMinLength().getValue())) && (subtrahendContainer.getListMaxLength() == null || (newContainer.getListMaxLength() != null && newContainer.getListMaxLength().getValue() <= subtrahendContainer.getListMaxLength().getValue()))) {

                    if (subtrahendContainer.getListEnumeration().isEmpty() && subtrahendContainer.getListPatternList().isEmpty() &&
                            subtrahendContainer.getSimpleTypeLength() == null) {
                        return null;
                    }
                }

                // Check if container has to be split
                if ((subtrahendContainer.getListMinLength() != null && (newContainer.getListMinLength() == null || newContainer.getListMinLength().getValue() < subtrahendContainer.getListMinLength().getValue())) && (subtrahendContainer.getListMaxLength() != null && (newContainer.getListMaxLength() == null || newContainer.getListMaxLength().getValue() > subtrahendContainer.getListMaxLength().getValue()))) {

                    // Get new container list
                    LinkedList<SimpleTypeInheritanceContainer> containers = new LinkedList<SimpleTypeInheritanceContainer>();

                    // Build first container
                    SimpleTypeInheritanceContainer container = getInheritanceContainerCopy(minuendContainer);
                    container.setListMaxLength(new SimpleContentFixableRestrictionProperty<Integer>(subtrahendContainer.getListMinLength().getValue() - 1, false));
                    LinkedList<SimpleTypeInheritanceContainer> newContainers = generateNewSimpleTypeInheritanceContainerList(container, subtrahendContainer);

                    if (newContainers != null) {
                        containers.addAll(newContainers);
                    }

                    // Build second container
                    container = getInheritanceContainerCopy(minuendContainer);
                    container.setListMinLength(new SimpleContentFixableRestrictionProperty<Integer>(subtrahendContainer.getListMaxLength().getValue() + 1, false));
                    newContainers = generateNewSimpleTypeInheritanceContainerList(container, subtrahendContainer);

                    if (newContainers != null) {
                        containers.addAll(newContainers);
                    }

                    // Return new containers
                    return containers;
                }

                // Get new max length
                if (subtrahendContainer.getListMinLength() != null) {

                    if (newContainer.getListMaxLength() == null || (newContainer.getListMaxLength().getValue() > subtrahendContainer.getListMinLength().getValue() - 1)) {

                        if (newContainer.getListMinLength() == null || (newContainer.getListMinLength().getValue() < subtrahendContainer.getListMinLength().getValue())) {
                            newContainer.setListMaxLength(new SimpleContentFixableRestrictionProperty<Integer>(subtrahendContainer.getListMinLength().getValue() - 1, subtrahendContainer.getListMinLength().getFixed()));
                        }
                    }
                }

                // Get new min length
                if (subtrahendContainer.getListMaxLength() != null) {

                    if (newContainer.getListMinLength() == null || (newContainer.getListMinLength().getValue() < subtrahendContainer.getListMaxLength().getValue() + 1)) {

                        if (newContainer.getListMaxLength() == null || (newContainer.getListMaxLength().getValue() > subtrahendContainer.getListMaxLength().getValue())) {
                            newContainer.setListMinLength(new SimpleContentFixableRestrictionProperty<Integer>(subtrahendContainer.getListMaxLength().getValue() + 1, subtrahendContainer.getListMaxLength().getFixed()));
                        }
                    }
                }
            }

            // If maxLength is smaller than minLength no difference is possible
            if (newContainer.getListMaxLength() != null && newContainer.getListMinLength() != null && newContainer.getListMaxLength().getValue() < newContainer.getListMinLength().getValue()) {
                return null;
            }

            // If minLength or maxLength differ from length the difference is empty
            if (newContainer.getListLength() != null && (newContainer.getListMaxLength() != null && newContainer.getListMaxLength().getValue() != newContainer.getListLength().getValue() || newContainer.getListMinLength() != null && newContainer.getListMinLength().getValue() != newContainer.getListLength().getValue())) {
                return null;
            }
        }
        LinkedList<SimpleTypeInheritanceContainer> newContainers = new LinkedList<SimpleTypeInheritanceContainer>();

        // Check if container is empty
        if (newContainer != null) {
            newContainers.add(newContainer);
        }

        return newContainers;
    }
}
