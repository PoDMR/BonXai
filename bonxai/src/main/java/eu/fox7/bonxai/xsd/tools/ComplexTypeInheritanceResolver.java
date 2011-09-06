package eu.fox7.bonxai.xsd.tools;

import eu.fox7.bonxai.common.*;
import eu.fox7.bonxai.xsd.*;
import eu.fox7.bonxai.xsd.Element;
import eu.fox7.bonxai.xsd.Element.Block;
import eu.fox7.bonxai.xsd.Group;
import eu.fox7.bonxai.xsd.XSDSchema.*;
import eu.fox7.bonxai.xsd.automaton.TypeAutomatons.exceptions.*;
import eu.fox7.bonxai.xsd.automaton.exceptions.NonDeterministicFiniteAutomataException;

import java.util.*;

/**
 * The ComplexTypeInheritanceResolver class is used to resolve all complexType
 * inheritance in the specified main schema and its external schemata. Not all
 * types of inheritance can be removed, for exampel complexTypes with simple
 * content can not be removed completly because there is no equivalent
 * representation .
 * 
 * @author Dominik Wolff
 */
public class ComplexTypeInheritanceResolver extends ResolverTool {

    // List containing all schemata contained under the current schema.
    private LinkedHashSet<XSDSchema> schemata = null;

    // Build map, which stores the base type of each complexType
    private LinkedHashMap<ComplexType, Type> complexTypeBaseTypeMap = null;

    // Map mapping each any attribute to the old schema, that contains the any attribute
    private LinkedHashMap<AnyAttribute, XSDSchema> anyAttributeOldSchemaMap = new LinkedHashMap<AnyAttribute, XSDSchema>();

    // Map mapping attributes to old schemata used to construct the new output schema
    private LinkedHashMap<Attribute, XSDSchema> attributeOldSchemaMap = new LinkedHashMap<Attribute, XSDSchema>();

    // Map mapping any patterns to their old schemata.
    private LinkedHashMap<AnyPattern, XSDSchema> anyPatternOldSchemaMap = new LinkedHashMap<AnyPattern, XSDSchema>();

    // Map mapping elements to old schemata used to construct the new output schema
    private LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();

    // Map mapping types to old schemata used to construct the new output schema
    private LinkedHashMap<Type, XSDSchema> complexTypeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();

    /**
     * Constructor method of the ComplexTypeInheritanceResolver class. The
     * specified schema is used to remove element inheritance from this schema
     * and all schemata contained in it (through include/redefine/import
     * components).
     *
     * @param schema XSDSchema for which all element inheritance is resolved.
     */
    public ComplexTypeInheritanceResolver(XSDSchema schema) {

        // Get all schemata contained under the current schema
        schemata = getSchemata(schema);

        // Use foreign schema resolver to resolve elementFormDefault attributes in each schema
        ForeignSchemaResolver foreignSchemaResolver = new ForeignSchemaResolver();
        for (Iterator<XSDSchema> it = schemata.iterator(); it.hasNext();) {
            XSDSchema currentSchema = it.next();

            // Resolve attributes for schemata with default form qualified
            if (schema.getElementFormDefault().equals(Qualification.qualified)) {
                foreignSchemaResolver.removeElementFormDefault(currentSchema);
            }
            if (schema.getAttributeFormDefault().equals(Qualification.qualified)) {
                foreignSchemaResolver.removeAttributeFormDefault(currentSchema);
            }
        }
    }

    /**
     * Resolves complexType inheritance for the specified main schema and its
     * external schemata. The result is a pseudo-schema, which violates the
     * EDC rule.
     */
    public void resolveComplexTypeInheritance() {

        // Get old schema mpas for EDCFixer
        getOldSchemaMaps();

        // Build map, which stores the base type of each complexType
        complexTypeBaseTypeMap = new LinkedHashMap<ComplexType, Type>();

        // Get base types of complexTypes
        for (Iterator<XSDSchema> it = schemata.iterator(); it.hasNext();) {
            XSDSchema currentSchema = it.next();
            complexTypeBaseTypeMap.putAll(getCurrentComplexTypeBaseTypeMap(currentSchema));
        }

        // Get lists to store extended and restricted types in
        LinkedList<Type> extendedTypes = new LinkedList<Type>();
        LinkedList<Type> restrictedTypes = new LinkedList<Type>();

        // For each schema resolve all complexTypes with inheritance
        for (Iterator<XSDSchema> it = schemata.iterator(); it.hasNext();) {
            XSDSchema currentSchema = it.next();

            // For each complexType with inheritance resolve inheritance
            for (Iterator<ComplexType> it2 = getCurrentComplexTypeBaseTypeMap(currentSchema).keySet().iterator(); it2.hasNext();) {
                ComplexType complexType = it2.next();

                // Resolve ComplexType inheritance for Simple and ComplexContent
                if (complexType.getContent() instanceof ComplexContentType) {
                    ComplexContentType oldComplexContentType = (ComplexContentType) complexType.getContent();

                    // Build new complexType for complex restriction inheritance
                    if (oldComplexContentType.getInheritance() != null && oldComplexContentType.getInheritance() instanceof ComplexContentRestriction) {
                        restrictedTypes.add(complexType);
                        resolveComplexContentRestriction(complexType);
                    }

                    // Build new complexType for complex extension inheritance
                    if (oldComplexContentType.getInheritance() != null && oldComplexContentType.getInheritance() instanceof ComplexContentExtension) {
                        extendedTypes.add(complexType);
                        resolveComplexContentExtension(complexType);
                    }
                } else if (complexType.getContent() instanceof SimpleContentType) {
                    SimpleContentType oldSimpleContentType = (SimpleContentType) complexType.getContent();

                    // Build new complexType for simple restriction inheritance
                    if (oldSimpleContentType.getInheritance() != null && oldSimpleContentType.getInheritance() instanceof SimpleContentRestriction) {
                        restrictedTypes.add(complexType);
                        resolveSimpleContent(complexType, currentSchema);
                    }

                    // Build new complexType for simple extension inheritance
                    if (oldSimpleContentType.getInheritance() != null && oldSimpleContentType.getInheritance() instanceof SimpleContentExtension) {
                        extendedTypes.add(complexType);
                        resolveSimpleContent(complexType, currentSchema);
                    }
                }
            }
        }

        // For each ComplexType with inheritance all of it derived types are possible substitutions.
        HashMap<ComplexType, LinkedHashSet<ComplexType>> typeSubstitutionTypesMap = getTypeSubstitutionTypesMap(extendedTypes, restrictedTypes);

        // Replace elements with base types
        replaceElements(typeSubstitutionTypesMap, extendedTypes, restrictedTypes);

        // Resolve abstract types
        removeAbstractTypes();
    }

    /**
     * Remove content from abstract types.
     */
    private void removeAbstractTypes() {

        // Check each schema
        for (Iterator<XSDSchema> it = schemata.iterator(); it.hasNext();) {
            XSDSchema schema = it.next();

            // Check schema types
            for (Iterator<Type> it2 = schema.getTypes().iterator(); it2.hasNext();) {
                Type type = it2.next();

                // Only complexTypes can be absttract
                if (type instanceof ComplexType) {
                    ComplexType complexType = (ComplexType) type;

                    // If type is abstract set content and attributes to null
                    if (complexType.isAbstract()) {
                        complexType.setContent(null);
                        complexType.setAttributes(new LinkedList<AttributeParticle>());
                        complexType.setMixed(false);
                    }
                }
            }
        }
    }

    /**
     * Resolves complexType inheritance and fixes the EDC rule for the returned
     * schema, as well as fixing choice patterns, that are not optimal.
     *
     * @param workingDirectory Directory for the schema location of the new
     * schemata.
     * @return New schema, which contains no complexType inheritance.
     * @throws InvalidTypeStateContentException Exception that is thrown if a
     * type automaton state contains invalid content.
     * @throws EmptyProductTypeStateFieldException Exception which is thrown
     * if a product state contains no type states.
     * @throws NonDeterministicFiniteAutomataException Exception which is thrown
     * if a particle automaton is no deterministic finite automaton.
     * @throws EmptySubsetTypeStateFieldException Exception which is thrown
     * if a subset state contains no type states.
     */
    public XSDSchema getSchemaWithoutComplexTypeInheritance(String workingDirectory) throws InvalidTypeStateContentException, EmptyProductTypeStateFieldException, NonDeterministicFiniteAutomataException, EmptySubsetTypeStateFieldException {

        // Resolve complexType inheritance
        resolveComplexTypeInheritance();

        // Fix EDC via EDCFixer
        EDCFixer edcFixer = new EDCFixer();
        edcFixer.setOldSchemaMaps(anyAttributeOldSchemaMap, attributeOldSchemaMap, anyPatternOldSchemaMap, elementOldSchemaMap);

        // Get current main schema with fixed EDC
        XSDSchema schema = edcFixer.fixEDCWithoutInheritance(schemata.iterator().next(), workingDirectory);

        // Fix choice patterns in new main schema
        fixChoicePattern(schema);
        return schema;
    }

    /**
     * Resolves a given simple content complexType in the specified schema.
     *
     * @param complexType Type, which is changed due to resolving the contained
     * type inheritance.
     * @param schema XSDSchema, which contains the complexType.
     * @return New complexType, without inheritance.
     */
    private ComplexType resolveSimpleContent(ComplexType complexType, XSDSchema schema) {

        // Check if complexType has simple content
        if (complexType.getContent() instanceof SimpleContentType) {
            SimpleContentType oldSimpleContentType = (SimpleContentType) complexType.getContent();

            // Check if inheritance is present
            if (oldSimpleContentType.getInheritance() != null) {

                // Generate new simple content
                SimpleContentType newSimpleContentType = new SimpleContentType();

                // Set new id and annotation
                newSimpleContentType.setAnnotation(oldSimpleContentType.getAnnotation());

                // Get id of the old schema
                if (oldSimpleContentType.getId() != null) {
                    newSimpleContentType.setId(oldSimpleContentType.getId());
                }

                // Get new simple content base
                SimpleType newBase = getSimpleContentExtensionBase(complexType);

                // Add new base type to schema symbolTable
                if (!schema.getTypeSymbolTable().hasReference(newBase.getName())) {
                    schema.getTypeSymbolTable().updateOrCreateReference(newBase.getName(), newBase);
                }

                // Get new simple content restriction if base type is restricted
                SimpleContentRestriction simpleContentRestriction = getSimpleContentRestriction(complexType, new SimpleContentRestriction());

                // Check if restriction is neccesary
                if (simpleContentRestriction != null &&
                        (simpleContentRestriction.getFractionDigits() != null ||
                        simpleContentRestriction.getLength() != null ||
                        simpleContentRestriction.getMaxExclusive() != null ||
                        simpleContentRestriction.getMaxInclusive() != null ||
                        simpleContentRestriction.getMaxLength() != null ||
                        simpleContentRestriction.getMinExclusive() != null ||
                        simpleContentRestriction.getMinInclusive() != null ||
                        simpleContentRestriction.getMinLength() != null ||
                        simpleContentRestriction.getPattern() != null ||
                        simpleContentRestriction.getTotalDigits() != null ||
                        simpleContentRestriction.getWhitespace() != null ||
                        !simpleContentRestriction.getEnumeration().isEmpty())) {

                    // Set new base type as base of the restriction
                    simpleContentRestriction.setBase(schema.getTypeSymbolTable().getReference(newBase.getName()));

                    // Create new base type with the new restriction and add it as top-level type to the current schema
                    newBase = new SimpleType(getRestrictionTypeName(schema, newBase.getLocalName()), simpleContentRestriction);
                    schema.getTypeSymbolTable().updateOrCreateReference(newBase.getName(), newBase);
                    schema.addType(schema.getTypeSymbolTable().getReference(newBase.getName()));
                }

                // Get new extension with new attributes
                SimpleContentExtension simpleContentExtension = new SimpleContentExtension(schema.getTypeSymbolTable().getReference(newBase.getName()));


                // Add attributes to simple content extension
                simpleContentExtension.setAttributes(getInheritedAttributes(complexType, new HashMap<String, AttributeParticle>()));


                // Set extension in simple content
                newSimpleContentType.setInheritance(simpleContentExtension);

                // Set simple content in complexType
                complexType.setContent(newSimpleContentType);
            }
        }
        return complexType;
    }

    /**
     * Get new simple content restriction for the specified complexType. The
     * given restriction is filled with valid informations to be the new
     * restriction.
     *
     * @param complexType ComplexType with simple content inheritance.
     * @param simpleContentRestriction New restriction, which is now made a
     * valid restriction for the given complexType.
     * @return New valid restriction for the specified complexType.
     */
    private SimpleContentRestriction getSimpleContentRestriction(ComplexType complexType, SimpleContentRestriction simpleContentRestriction) {

        // Check if complexType contains simple content
        if (complexType.getContent() instanceof SimpleContentType) {
            SimpleContentType oldSimpleContentType = (SimpleContentType) complexType.getContent();

            // Check if simple content contains extension
            if (oldSimpleContentType.getInheritance() != null && oldSimpleContentType.getInheritance() instanceof SimpleContentExtension) {
                SimpleContentExtension simpleContentExtension = (SimpleContentExtension) oldSimpleContentType.getInheritance();

                // If the base type is a complexType search this type for restrictions
                if (simpleContentExtension.getBase() instanceof ComplexType) {
                    return getSimpleContentRestriction((ComplexType) simpleContentExtension.getBase(), simpleContentRestriction);
                }
            }

            // Check if simple content contains restriction
            if (oldSimpleContentType.getInheritance() != null && oldSimpleContentType.getInheritance() instanceof SimpleContentRestriction) {
                SimpleContentRestriction oldSimpleContentRestriction = (SimpleContentRestriction) oldSimpleContentType.getInheritance();

                // Set new facets for the new restriction
                if (simpleContentRestriction.getFractionDigits() == null) {
                    simpleContentRestriction.setFractionDigits(oldSimpleContentRestriction.getFractionDigits());
                }
                if (simpleContentRestriction.getLength() == null) {
                    simpleContentRestriction.setFractionDigits(oldSimpleContentRestriction.getLength());
                }
                if (simpleContentRestriction.getMaxExclusive() == null) {
                    simpleContentRestriction.setMaxExclusive(oldSimpleContentRestriction.getMaxExclusive());
                }
                if (simpleContentRestriction.getMaxInclusive() == null) {
                    simpleContentRestriction.setMaxInclusive(oldSimpleContentRestriction.getMaxInclusive());
                }
                if (simpleContentRestriction.getMaxLength() == null) {
                    simpleContentRestriction.setMaxLength(oldSimpleContentRestriction.getMaxLength());
                }
                if (simpleContentRestriction.getMinExclusive() == null) {
                    simpleContentRestriction.setMinExclusive(oldSimpleContentRestriction.getMinExclusive());
                }
                if (simpleContentRestriction.getMinInclusive() == null) {
                    simpleContentRestriction.setMinInclusive(oldSimpleContentRestriction.getMinInclusive());
                }
                if (simpleContentRestriction.getMinLength() == null) {
                    simpleContentRestriction.setMinLength(oldSimpleContentRestriction.getMinLength());
                }
                if (simpleContentRestriction.getPattern() == null) {
                    simpleContentRestriction.setPattern(oldSimpleContentRestriction.getPattern());
                }
                if (simpleContentRestriction.getTotalDigits() == null) {
                    simpleContentRestriction.setTotalDigits(oldSimpleContentRestriction.getTotalDigits());
                }
                if (simpleContentRestriction.getWhitespace() == null) {
                    simpleContentRestriction.setWhitespace(oldSimpleContentRestriction.getWhitespace());
                }
                if (simpleContentRestriction.getEnumeration() == null) {
                    simpleContentRestriction.addEnumeration(oldSimpleContentRestriction.getEnumeration());
                }

                // If the base type is a complexType search this type for restrictions
                if (oldSimpleContentRestriction.getBase() instanceof ComplexType) {
                    return getSimpleContentRestriction((ComplexType) oldSimpleContentRestriction.getBase(), simpleContentRestriction);
                }
            }
        }
        return simpleContentRestriction;
    }

    /**
     * Get the base of the new simple content extension.
     *
     * @param complexType Type contaiing the new base type.
     * @return SimpleType, which is the new base of a new simple content
     * extension.
     */
    private SimpleType getSimpleContentExtensionBase(ComplexType complexType) {

        // Check if complexType contains simple content
        if (complexType.getContent() instanceof SimpleContentType) {
            SimpleContentType oldSimpleContentType = (SimpleContentType) complexType.getContent();

            // If simple content contains extension
            if (oldSimpleContentType.getInheritance() != null && oldSimpleContentType.getInheritance() instanceof SimpleContentExtension) {
                SimpleContentExtension simpleContentExtension = (SimpleContentExtension) oldSimpleContentType.getInheritance();

                // If base type is complexType search this type for a base type if the base type is a simpleType return it
                if (simpleContentExtension.getBase() instanceof ComplexType) {
                    return getSimpleContentExtensionBase((ComplexType) simpleContentExtension.getBase());
                } else {
                    return (SimpleType) simpleContentExtension.getBase();
                }
            } else if (oldSimpleContentType.getInheritance() != null && oldSimpleContentType.getInheritance() instanceof SimpleContentRestriction) {
                SimpleContentRestriction simpleContentRestriction = (SimpleContentRestriction) oldSimpleContentType.getInheritance();

                // If simple content contains a restriction
                if (simpleContentRestriction.getBase() instanceof ComplexType && simpleContentRestriction.getAnonymousSimpleType() == null) {

                    // Check base of the restriction if it is a complexType and no anonymous simpleType is present
                    return getSimpleContentExtensionBase((ComplexType) simpleContentRestriction.getBase());

                } else if (simpleContentRestriction.getBase() instanceof SimpleType) {

                    // Return base type if it is an non anonymous simpleType
                    return (SimpleType) simpleContentRestriction.getBase();

                } else if (simpleContentRestriction.getAnonymousSimpleType() != null) {

                    // Return anonymous simpleType
                    return simpleContentRestriction.getAnonymousSimpleType();
                }
            }
        }
        return null;
    }

    /**
     * Resolves a given complex content extension complexType.
     *
     * @param complexType Type, which is changed due to resolving the contained
     * type inheritance.
     * @return New complexType, without inheritance.
     */
    private ComplexType resolveComplexContentExtension(ComplexType complexType) {

        // Check if complexType contains complex content
        if (complexType.getContent() instanceof ComplexContentType) {
            ComplexContentType oldComplexContentType = (ComplexContentType) complexType.getContent();

            // Check if it contains a extension
            if (oldComplexContentType.getInheritance() != null && oldComplexContentType.getInheritance() instanceof ComplexContentExtension) {

                // Get new seqenze pattern
                SequencePattern sequencePattern = new SequencePattern();

                // Get particles contained in the current type and its base types
                LinkedList<Particle> oldParticles = getInheritedParticle(complexType);

                // Add old particles to the sequence
                for (Iterator<Particle> it = oldParticles.iterator(); it.hasNext();) {
                    Particle particle = it.next();

                    // If particle is not null add it to the sequence
                    if (particle != null) {
                        sequencePattern.addParticle(particle);
                    }
                }

                // Add attributes to simple content extension
                complexType.setAttributes(getInheritedAttributes(complexType, new HashMap<String, AttributeParticle>()));

                // Set new attributes and mixed
                complexType.setMixed(getInheritedMixed(complexType));

                // If particle is present set new complex content else set no content at all
                if (!sequencePattern.getParticles().isEmpty()) {
                    complexType.setContent(new ComplexContentType(sequencePattern, getInheritedMixed(complexType)));
                } else {
                    complexType.setContent(null);
                }
            }
        }
        return complexType;
    }

    /**
     * Resolves a given complex content restriction complexType.
     *
     * @param complexType Type, which is changed due to resolving the contained
     * type inheritance.
     * @return New complexType, without inheritance.
     */
    private ComplexType resolveComplexContentRestriction(ComplexType complexType) {

        // Check if complexType contains complex content
        if (complexType.getContent() instanceof ComplexContentType) {
            ComplexContentType oldComplexContentType = (ComplexContentType) complexType.getContent();

            // Check if it contains a restriction
            if (oldComplexContentType.getInheritance() != null && oldComplexContentType.getInheritance() instanceof ComplexContentRestriction) {

                // Add attribute group to simple content extension
                complexType.setAttributes(getInheritedAttributes(complexType, new HashMap<String, AttributeParticle>()));

                // Set new attributes and mixed
                complexType.setMixed(getInheritedMixed(complexType));

                // If particle is present set new complex content else set no content at all
                if (oldComplexContentType.getParticle() != null) {
                    complexType.setContent(new ComplexContentType(oldComplexContentType.getParticle(), getInheritedMixed(complexType)));
                } else {
                    complexType.setContent(null);
                }
            }
        }
        return complexType;
    }

    /**
     * Update the old schema maps, so that for each component the schema
     * containing the component can be found. Schemas may contain default
     * values.
     */
    private void getOldSchemaMaps() {
        for (Iterator<XSDSchema> it2 = schemata.iterator(); it2.hasNext();) {
            XSDSchema schema = it2.next();

            // Get all referenced types in the current schema
            for (Iterator<Type> it3 = schema.getTypeSymbolTable().getAllReferencedObjects().iterator(); it3.hasNext();) {
                Type type = it3.next();

                // If the referred type has the same namespace as the schema add type to map
                if (type.getNamespace().equals(schema.getTargetNamespace())) {

                    // Check if type is compleType
                    if (type instanceof ComplexType) {
                        ComplexType complexType = (ComplexType) type;
                        complexTypeOldSchemaMap.put(type, schema);

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
                            } else if (simpleContentType.getInheritance() instanceof SimpleContentRestriction) {
                                SimpleContentRestriction simpleContentRestriction = (SimpleContentRestriction) simpleContentType.getInheritance();

                                // Get attribute particles contained in the extension
                                for (Iterator<AttributeParticle> it4 = simpleContentRestriction.getAttributes().iterator(); it4.hasNext();) {
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

    /**
     * Get anyAttributeOldSchemaMap of the ComplexTypeInheritanceResolver class.
     *
     * @return The anyAttributeOldSchemaMap, which maps any attributes to their
     * schemata.
     */
    public LinkedHashMap<AnyAttribute, XSDSchema> getAnyAttributeOldSchemaMap() {
        return anyAttributeOldSchemaMap;
    }

    /**
     * Get anyPatternOldSchemaMap of the ComplexTypeInheritanceResolver class.
     *
     * @return The anyPatternOldSchemaMap, which maps any patterns to their
     * schemata.
     */
    public LinkedHashMap<AnyPattern, XSDSchema> getAnyPatternOldSchemaMap() {
        return anyPatternOldSchemaMap;
    }

    /**
     * Get attributeOldSchemaMap of the ComplexTypeInheritanceResolver class.
     *
     * @return The attributeOldSchemaMap, which maps attributes to their
     * schemata.
     */
    public LinkedHashMap<Attribute, XSDSchema> getAttributeOldSchemaMap() {
        return attributeOldSchemaMap;
    }

    /**
     * Get elementOldSchemaMap of the ComplexTypeInheritanceResolver class.
     *
     * @return The elementOldSchemaMap, which maps elements to their schemata.
     */
    public LinkedHashMap<Element, XSDSchema> getElementOldSchemaMap() {
        return elementOldSchemaMap;
    }

    /**
     * Get all inherited particles of the specified complexType.
     *
     * @param complexType Type which may contain a particle and inherit other
     * particles.
     * @return List of inherited particles.
     */
    private LinkedList<Particle> getInheritedParticle(ComplexType complexType) {

        // List of inherited particles
        LinkedList<Particle> inheritedParticles = new LinkedList<Particle>();

        // Only complex content can contain particles
        if (complexType.getContent() instanceof ComplexContentType) {
            ComplexContentType complexContentType = (ComplexContentType) complexType.getContent();

            // Restrictions and complex types with no inheritance contain their real particles
            if (complexContentType.getInheritance() != null && complexContentType.getInheritance() instanceof ComplexContentRestriction || complexContentType.getInheritance() == null) {

                // Add current particle to the inherited particles list and return list
                inheritedParticles.add(complexContentType.getParticle());
                return inheritedParticles;

            } else if (complexContentType.getInheritance() != null && complexContentType.getInheritance() instanceof ComplexContentExtension) {

                // Get inherited particles of the extension and add current particle then return the list
                inheritedParticles = getInheritedParticle((ComplexType) complexContentType.getInheritance().getBase());
                inheritedParticles.add(complexContentType.getParticle());
                return inheritedParticles;
            }
        }
        return new LinkedList<Particle>();
    }

    /**
     * Get all inherited attribute particles of the specified complexType.
     *
     * @param complexType Type which may contain attribute particles and
     * inherit other attribute particles.
     * @param attribueNameAttributeParticleMap Hashmap mapping attribute names
     * to attribute particles.
     * @return List of inherited attribute particles.
     */
    private LinkedList<AttributeParticle> getInheritedAttributes(ComplexType complexType, HashMap<String, AttributeParticle> attribueNameAttributeParticleMap) {

        // If current complexType has no simple conten
        if (complexType.getContent() instanceof ComplexContentType || complexType.getContent() == null) {
            ComplexContentType complexContentType = (ComplexContentType) complexType.getContent();

            // If current complexType has complex content
            if (complexContentType != null && complexContentType.getInheritance() != null && complexContentType.getInheritance() instanceof ComplexContentInheritance) {
                ComplexContentInheritance complexContentInheritance = complexContentType.getInheritance();

                // Get attribute particles of the current complex content
                LinkedList<AttributeParticle> attributeParticles = complexContentInheritance.getAttributes();
                updateAttribueNameAttributeParticleMap(attributeParticles, attribueNameAttributeParticleMap);

                // Get inherited attributes
                getInheritedAttributes((ComplexType) complexContentInheritance.getBase(), attribueNameAttributeParticleMap);

            } else if (complexContentType == null || complexContentType.getInheritance() == null) {

                // Get attribute particles of the current complex type
                LinkedList<AttributeParticle> attributeParticles = complexType.getAttributes();
                updateAttribueNameAttributeParticleMap(attributeParticles, attribueNameAttributeParticleMap);
            }
        } else if (complexType.getContent() instanceof SimpleContentType) {
            SimpleContentType simpleContentType = (SimpleContentType) complexType.getContent();

            // Check simple content with simple content extension or restriction
            if (simpleContentType.getInheritance() != null && simpleContentType.getInheritance() instanceof SimpleContentExtension) {
                SimpleContentExtension simpleContentExtension = (SimpleContentExtension) simpleContentType.getInheritance();

                // Get attributes contained in extension
                LinkedList<AttributeParticle> attributeParticles = simpleContentExtension.getAttributes();
                updateAttribueNameAttributeParticleMap(attributeParticles, attribueNameAttributeParticleMap);

                // Only a complexType can contain attributes so check if the base type is a complexType
                if (simpleContentExtension.getBase() instanceof ComplexType) {
                    getInheritedAttributes((ComplexType) simpleContentExtension.getBase(), attribueNameAttributeParticleMap);
                }
            } else if (simpleContentType.getInheritance() != null && simpleContentType.getInheritance() instanceof SimpleContentRestriction) {
                SimpleContentRestriction simpleContentRestriction = (SimpleContentRestriction) simpleContentType.getInheritance();

                // Get attributes contained in restriction
                LinkedList<AttributeParticle> attributeParticles = simpleContentRestriction.getAttributes();
                updateAttribueNameAttributeParticleMap(attributeParticles, attribueNameAttributeParticleMap);

                // Only a complexType can contain attributes so check if the base type is a complexType
                if (simpleContentRestriction.getBase() instanceof ComplexType) {
                    getInheritedAttributes((ComplexType) simpleContentRestriction.getBase(), attribueNameAttributeParticleMap);
                }
            }
        }

        // Get new attribute particles
        LinkedList<AttributeParticle> newAttributeParticles = new LinkedList<AttributeParticle>();

        // Add attributes contained in the attreibute Map to the new attribute particles
        for (Iterator<String> it = attribueNameAttributeParticleMap.keySet().iterator(); it.hasNext();) {
            String attributeParticleName = it.next();

            if (!attributeParticleName.equals("")) {
                newAttributeParticles.add(attribueNameAttributeParticleMap.get(attributeParticleName));
            }
        }

        // Add any attribute to new attribute particles
        if (attribueNameAttributeParticleMap.containsKey("")) {
            newAttributeParticles.add(attribueNameAttributeParticleMap.get(""));
        }

        // Return new attribute particles
        return newAttributeParticles;
    }

    /**
     * Method that gets the instance name of an attribute.
     *
     * @param attribute Attribute, which instance name is requested.
     * @return Instance name of the attribute.
     */
    private String getInstanceName(Attribute attribute) {

        // Use emtpy namespace if element is not qualified
        if (attribute.getForm() == Qualification.qualified) {
            return attribute.getName();
        } else {
            return "{}" + attribute.getLocalName();
        }
    }

    /**
     * Get for a specified schema a map mapping complexTypes to their base
     * types.
     *
     * @param schema XSDSchema, which contains the complexTypes.
     * @return A map mapping complexTypes to types they are derived from.
     */
    private LinkedHashMap<ComplexType, Type> getCurrentComplexTypeBaseTypeMap(XSDSchema schema) {
        LinkedHashMap<ComplexType, Type> currentComplexTypeBaseTypeMap = new LinkedHashMap<ComplexType, Type>();

        // For each type in the schema check if it is a derived type
        for (Iterator<SymbolTableRef<Type>> it = schema.getTypeSymbolTable().getReferences().iterator(); it.hasNext();) {
            Type currentType = it.next().getReference();

            // Check that only complexTypes are tested
            if (currentType instanceof ComplexType) {
                ComplexType complexType = (ComplexType) currentType;

                // Check if schema contains the current type
                if (schema.getTypes().contains(currentType) || complexType.isAnonymous()) {

                    if (complexType.getContent() instanceof ComplexContentType) {
                        ComplexContentType complexContentType = (ComplexContentType) complexType.getContent();

                        if (complexContentType.getInheritance() != null) {

                            // Get base type of the current ComplexContentType
                            ComplexContentInheritance complexContentInheritance = complexContentType.getInheritance();
                            currentComplexTypeBaseTypeMap.put(complexType, complexContentInheritance.getBase());
                        }
                    } else if (complexType.getContent() instanceof SimpleContentType) {
                        SimpleContentType simpleContentType = (SimpleContentType) complexType.getContent();

                        if (simpleContentType.getInheritance() instanceof SimpleContentExtension) {

                            // Get base type of the current SimpleContentType
                            SimpleContentExtension simpleContentExtension = (SimpleContentExtension) simpleContentType.getInheritance();
                            currentComplexTypeBaseTypeMap.put(complexType, simpleContentExtension.getBase());

                        } else if (simpleContentType.getInheritance() instanceof SimpleContentRestriction) {

                            // Get base type of the current SimpleContentType
                            SimpleContentRestriction simpleContentRestriction = (SimpleContentRestriction) simpleContentType.getInheritance();
                            currentComplexTypeBaseTypeMap.put(complexType, simpleContentRestriction.getBase());
                        }
                    }
                }
            }
        }
        return currentComplexTypeBaseTypeMap;
    }

    /**
     * Checks if a complexType or one of the types it is derived from is mixed.
     *
     * @param complexType ComlpexType the check is performed for.
     * @return <tt>true<tt/> if the complexType has mixed content.
     */
    private boolean getInheritedMixed(ComplexType complexType) {

        // If complexType is mixed return true
        if (complexType.getMixed()) {
            return true;
        }

        // Check if ComplexContentType is mixed
        if (complexType.getContent() instanceof ComplexContentType) {
            ComplexContentType complexContentType = (ComplexContentType) complexType.getContent();

            // If ComplexContentType is mixed return true
            if (complexContentType.getMixed()) {
                return true;
            } else if (complexContentType.getInheritance() instanceof ComplexContentExtension) {

                // Check base type of the current complexType with extension
                ComplexContentExtension complexContentExtension = (ComplexContentExtension) complexContentType.getInheritance();
                return getInheritedMixed((ComplexType) complexContentExtension.getBase());

            } else if (complexContentType.getInheritance() instanceof ComplexContentRestriction) {

                // Check base type of the current complexType with restriction
                ComplexContentRestriction complexContentRestriction = (ComplexContentRestriction) complexContentType.getInheritance();
                return getInheritedMixed((ComplexType) complexContentRestriction.getBase());
            }
        }
        return false;
    }

    /**
     * This method returns all attribute particles contained in the specified
     * attribute group and adds seen attribute groups to the specified list of
     * attribute groups.
     *
     * @param attributeGroup Attribute group which contains the returned
     * attribute particles.
     * @param containedAttributeGroupAttributeRefMap Map which maps attribute
     * groups which were seen when traversing the content of the attribute
     * group, to attribute references.
     * @return List of attribute particles contained by the attribute group.
     */
    private LinkedList<AttributeParticle> getAttributeParticles(AttributeGroup attributeGroup, LinkedHashMap<AttributeGroup, AttributeGroupRef> containedAttributeGroupAttributeRefMap) {

        // List of contained attribute particles
        LinkedList<AttributeParticle> attributeParticles = new LinkedList<AttributeParticle>();

        // All all attribute particle contained in the current attribute group to the list if they are not references to attribute groups
        for (Iterator<AttributeParticle> it = attributeGroup.getAttributeParticles().iterator(); it.hasNext();) {
            AttributeParticle attributeParticle = it.next();

            // If attribute particle is attribute group reference, check the referred attribute group for attribute particles
            if (attributeParticle instanceof AttributeGroupRef) {
                AttributeGroup currentAttributeGroup = ((AttributeGroupRef) attributeParticle).getAttributeGroup();

                // Add attribute group to attribute group list and contained attribute particles to attribute particle list
                containedAttributeGroupAttributeRefMap.put(currentAttributeGroup, (AttributeGroupRef) attributeParticle);
                attributeParticles.addAll(getAttributeParticles(currentAttributeGroup, containedAttributeGroupAttributeRefMap));
            } else {

                // Add contained attribute particle to attribute particle list
                attributeParticles.add(attributeParticle);
            }
        }

        // Return the complete list of contained attribute particles
        return attributeParticles;
    }

    /**
     * Get new name for an restriction type.
     *
     * @return New name of an restriction type.
     */
    private String getRestrictionTypeName(XSDSchema schema, String baseTypeName) {

        // Generate new name for the new type
        String typeName = "{" + schema.getTargetNamespace() + "}type.restriction." + baseTypeName;

        // Use type name base and number to find valid type name
        String typeNameBase = typeName;
        int number = 1;

        // As long as the type name already exists check next type name
        while (schema.getTypeSymbolTable().hasReference(typeName)) {

            // Type name consists of a type name base and a number
            typeName = typeNameBase + "." + number;
            number++;
        }
        return typeName;
    }

    /**
     * Updates the attribueNameAttributeParticleMap, which is used to find
     * inherited attribute particles.
     *
     * @param attributeParticles Attribute particles used to update the
     * attribueNameAttributeParticleMap.
     * @param attribueNameAttributeParticleMap Map mapping attribute names to
     * attribute particles.
     */
    private void updateAttribueNameAttributeParticleMap(LinkedList<AttributeParticle> attributeParticles, HashMap<String, AttributeParticle> attribueNameAttributeParticleMap) {

        // Get attribute particles of the current complex type
        for (int i = 0; i < attributeParticles.size(); i++) {
            AttributeParticle attributeParticle = attributeParticles.get(i);

            // Add attribute particles to attribute map
            if (attributeParticle instanceof Attribute) {
                Attribute attribute = (Attribute) attributeParticle;

                // If no attribute with current name exist add attribute to map
                if (!attribueNameAttributeParticleMap.containsKey(getInstanceName(attribute))) {
                    attribueNameAttributeParticleMap.put(getInstanceName(attribute), attributeParticle);
                }
            } else if (attributeParticle instanceof AttributeRef) {
                Attribute attribute = ((AttributeRef) attributeParticle).getAttribute();

                // If no attribute with current name exist add attribute to map
                if (!attribueNameAttributeParticleMap.containsKey(getInstanceName(attribute))) {
                    attribueNameAttributeParticleMap.put(getInstanceName(attribute), attributeParticle);
                }
            } else if (attributeParticle instanceof AttributeGroupRef) {
                AttributeGroup attributeGroup = ((AttributeGroupRef) attributeParticle).getAttributeGroup();

                // Add attribute particles contained in groups to the attribute particle list
                attributeParticles.addAll(getAttributeParticles(attributeGroup, new LinkedHashMap<AttributeGroup, AttributeGroupRef>()));

            } else if (attributeParticle instanceof AnyAttribute) {

                // Add any attribute as empty string
                if (!attribueNameAttributeParticleMap.containsKey("")) {
                    attribueNameAttributeParticleMap.put("", attributeParticle);
                }
            }
        }
    }

    /**
     * Gets new typeSubstitutionTypesMap, which maps types to types they can be
     * substituted by.
     *
     * @param extendedTypes List of all extended Types.
     * @param restrictedTypes List of all restricted Types.
     * @return Map mapping types to types they can be substituted by.
     */
    private HashMap<ComplexType, LinkedHashSet<ComplexType>> getTypeSubstitutionTypesMap(LinkedList<Type> extendedTypes, LinkedList<Type> restrictedTypes) {

        // Map mapping types to the types they can be substituted by
        HashMap<ComplexType, LinkedHashSet<ComplexType>> typeSubstitutionTypesMap = new HashMap<ComplexType, LinkedHashSet<ComplexType>>();

        // Get all types with inheritance and update new map
        for (Iterator<ComplexType> it = complexTypeBaseTypeMap.keySet().iterator(); it.hasNext();) {
            ComplexType complexType = it.next();

            // If the base type of the current complexType is a complexType
            if (complexTypeBaseTypeMap.get(complexType) instanceof ComplexType) {
                ComplexType baseComplexType = (ComplexType) complexTypeBaseTypeMap.get(complexType);

                // If no entry for the current base type exists
                if (!typeSubstitutionTypesMap.containsKey(baseComplexType)) {
                    HashSet<ComplexTypeInheritanceModifier> blocks = baseComplexType.getBlockModifiers();

                    // Check if schema contains block defaults
                    if (baseComplexType.getBlockModifiers() == null) {
                        blocks = new HashSet<ComplexTypeInheritanceModifier>();

                        if (complexTypeOldSchemaMap.get(baseComplexType).getBlockDefaults().contains(BlockDefault.extension)) {
                            blocks.add(ComplexTypeInheritanceModifier.Extension);
                        }
                        if (complexTypeOldSchemaMap.get(baseComplexType).getBlockDefaults().contains(BlockDefault.restriction)) {
                            blocks.add(ComplexTypeInheritanceModifier.Restriction);
                        }
                    }

                    // Get types derived from the current complexType
                    LinkedHashSet<ComplexType> derivedTypes = getDerivedTypes(baseComplexType, blocks, extendedTypes, restrictedTypes);
                    typeSubstitutionTypesMap.put(baseComplexType, derivedTypes);
                }
            }
        }
        return typeSubstitutionTypesMap;
    }

    /**
     * Get types that derive from the specified base type.
     *
     * @param baseComplexType Base type used to find its derived types.
     * @param blocks Set containing all inheritance variants that are blocked.
     * @param extendedTypes List of all extended Types.
     * @param restrictedTypes List of all restricted Types.
     * @return Set of complexTypes that are derived from the base typ.
     */
    private LinkedHashSet<ComplexType> getDerivedTypes(ComplexType baseComplexType, HashSet<ComplexTypeInheritanceModifier> blocks, LinkedList<Type> extendedTypes, LinkedList<Type> restrictedTypes) {

        // Get set of types derived from specified base type
        LinkedHashSet<ComplexType> derivedTypes = new LinkedHashSet<ComplexType>();
        derivedTypes.add(baseComplexType);

        // For each complexType with inheritance check if it derives from the current base type
        for (Iterator<ComplexType> it = complexTypeBaseTypeMap.keySet().iterator(); it.hasNext();) {
            ComplexType complexType = it.next();

            if (extendedTypes.contains(complexType) && (blocks.isEmpty() || !blocks.contains(ComplexTypeInheritanceModifier.Extension))) {

                // Update derived types
                if (complexTypeBaseTypeMap.get(complexType) == baseComplexType) {
                    derivedTypes.add(complexType);
                    derivedTypes.addAll(getDerivedTypes(complexType, blocks, extendedTypes, restrictedTypes));
                }
            }

            if (restrictedTypes.contains(complexType) && (blocks.isEmpty() || !blocks.contains(ComplexTypeInheritanceModifier.Restriction))) {

                // Update derived types
                if (complexTypeBaseTypeMap.get(complexType) == baseComplexType) {
                    derivedTypes.add(complexType);
                    derivedTypes.addAll(getDerivedTypes(complexType, blocks, extendedTypes, restrictedTypes));
                }
            }
        }
        return derivedTypes;
    }

    private void replaceElements(HashMap<ComplexType, LinkedHashSet<ComplexType>> typeSubstitutionTypesMap, LinkedList<Type> extendedTypes, LinkedList<Type> restrictedTypes) {
        for (Iterator<XSDSchema> it2 = schemata.iterator(); it2.hasNext();) {
            XSDSchema schema = it2.next();

            // Get all referenced types in the current schema
            for (Iterator<Type> it3 = schema.getTypeSymbolTable().getAllReferencedObjects().iterator(); it3.hasNext();) {
                Type type = it3.next();

                // If the referred type has the same namespace as the schema add type to map
                if (type.getNamespace().equals(schema.getTargetNamespace())) {

                    // Check if type is compleType
                    if (type instanceof ComplexType) {
                        ComplexType complexType = (ComplexType) type;

                        // If complexType has complex content check content for elements
                        if (complexType.getContent() instanceof ComplexContentType) {
                            ComplexContentType complexContentType = (ComplexContentType) complexType.getContent();

                            if (complexContentType.getParticle() != null) {

                                // Replace elements contained in the particle
                                replaceElements(complexContentType.getParticle(), null, 0, typeSubstitutionTypesMap, extendedTypes, restrictedTypes);
                            }
                        }
                    }
                }
            }

            // Get elements contained in groups of the schema
            for (Iterator<Group> it3 = schema.getGroups().iterator(); it3.hasNext();) {
                Group group = it3.next();

                // Replace elements contained in the particle
                replaceElements(group.getParticleContainer(), null, 0, typeSubstitutionTypesMap, extendedTypes, restrictedTypes);
            }

            // Get top-level elements contained in the schema
            for (Iterator<Element> it3 = schema.getElements().iterator(); it3.hasNext();) {
                Element topLevelElement = it3.next();

                // Check if element contains a base type
                if (typeSubstitutionTypesMap.keySet().contains(topLevelElement.getType())) {
                    ComplexType baseComplexType = ((ComplexType) topLevelElement.getType());

                    // Get block attributes contained in the complexType
                    HashSet<ComplexTypeInheritanceModifier> blocks = baseComplexType.getBlockModifiers();

                    // Check if schema contains block defaults
                    if (baseComplexType.getBlockModifiers() == null) {
                        blocks = new HashSet<ComplexTypeInheritanceModifier>();

                        if (complexTypeOldSchemaMap.get(baseComplexType).getBlockDefaults().contains(BlockDefault.extension)) {
                            blocks.add(ComplexTypeInheritanceModifier.Extension);
                        }
                        if (complexTypeOldSchemaMap.get(baseComplexType).getBlockDefaults().contains(BlockDefault.restriction)) {
                            blocks.add(ComplexTypeInheritanceModifier.Restriction);
                        }
                    }

                    // Check block attribute of element
                    if (topLevelElement.getBlockModifiers() != null) {

                        if (topLevelElement.getBlockModifiers().contains(Block.extension)) {
                            blocks.add(ComplexTypeInheritanceModifier.Extension);
                        }
                        if (topLevelElement.getBlockModifiers().contains(Block.restriction)) {
                            blocks.add(ComplexTypeInheritanceModifier.Restriction);
                        }
                    }

                    // Get set of substitution complexTypes
                    LinkedHashSet<ComplexType> complexTypes = getDerivedTypes(baseComplexType, blocks, extendedTypes, restrictedTypes);

                    // Replace Element
                    replaceTopLevelElementsWithMoreElements(topLevelElement, schema, complexTypes);
                }
            }
        }
    }

    /**
     * Replaces element in a particle struture with a choice pattern containing
     * copies of the element with different typ-definitions.
     *
     * @param particle Current particle which may be an element.
     * @param parentParticle Particle containing the current particle.
     * @param position Position of the current particle in the parent particle.
     * @param typeSubstitutionTypesMap Map mapping types to types they can be
     * substituted by.
     * @param extendedTypes List of all extended Types.
     * @param restrictedTypes List of all restricted Types.
     */
    private void replaceElements(Particle particle, ParticleContainer parentParticle, int position, HashMap<ComplexType, LinkedHashSet<ComplexType>> typeSubstitutionTypesMap, LinkedList<Type> extendedTypes, LinkedList<Type> restrictedTypes) {

        // Check if the particle is an element
        if (particle instanceof Element) {
            Element element = (Element) particle;

            // Check if element contains a base type
            if (typeSubstitutionTypesMap.keySet().contains(element.getType())) {
                ComplexType baseComplexType = ((ComplexType) element.getType());

                // Get block attributes contained in the complexType
                HashSet<ComplexTypeInheritanceModifier> blocks = baseComplexType.getBlockModifiers();

                // Check if schema contains block defaults
                if (baseComplexType.getBlockModifiers() == null) {
                    blocks = new HashSet<ComplexTypeInheritanceModifier>();

                    if (complexTypeOldSchemaMap.get(baseComplexType).getBlockDefaults().contains(BlockDefault.extension)) {
                        blocks.add(ComplexTypeInheritanceModifier.Extension);
                    }
                    if (complexTypeOldSchemaMap.get(baseComplexType).getBlockDefaults().contains(BlockDefault.restriction)) {
                        blocks.add(ComplexTypeInheritanceModifier.Restriction);
                    }
                }

                // Check block attribute of element
                if (element.getBlockModifiers() != null) {

                    if (element.getBlockModifiers().contains(Block.extension)) {
                        blocks.add(ComplexTypeInheritanceModifier.Extension);
                    }
                    if (element.getBlockModifiers().contains(Block.restriction)) {
                        blocks.add(ComplexTypeInheritanceModifier.Restriction);
                    }
                }

                // Get set of substitution complexTypes
                LinkedHashSet<ComplexType> complexTypes = getDerivedTypes(baseComplexType, blocks, extendedTypes, restrictedTypes);

                // Replace Element
                relaceElementWithChoicePattern(element, parentParticle, position, complexTypes);
            }
        } else if (particle instanceof ParticleContainer) {
            ParticleContainer particleContainer = (ParticleContainer) particle;

            // Check if the particle is a particle container
            LinkedList<Particle> particles = particleContainer.getParticles();
            for (int i = 0; i < particles.size(); i++) {
                Particle currentParticle = particles.get(i);

                // Replace elements in particle container
                replaceElements(currentParticle, particleContainer, i, typeSubstitutionTypesMap, extendedTypes, restrictedTypes);
            }
        }
    }

    /**
     * Replaces element in a particle struture with a choice pattern containing
     * copies of the element with different typ-definitions.
     *
     * @param element Element, which is replaced.
     * @param parentParticle Particle containing the current element.
     * @param position Position of the current element in the parent particle.
     * @param complexTypes Set of complexTypes which are used in the new
     * elements.
     */
    private void relaceElementWithChoicePattern(Element element, ParticleContainer parentParticle, int position, LinkedHashSet<ComplexType> complexTypes) {

        // Create new choice pattern
        ChoicePattern choicePattern = new ChoicePattern();

        // For each complexType add a new element to the choice pattern
        for (Iterator<ComplexType> it = complexTypes.iterator(); it.hasNext();) {
            ComplexType complexType = it.next();

            if (!complexType.isAbstract()) {

                // Create new element as copy of the old element
                Element newElement = new Element(element.getName(), new SymbolTableRef<Type>(complexType.getName(), complexType));

                // Copy element and its attributes
                newElement.setAbstract(element.getAbstract());
                newElement.setAnnotation(element.getAnnotation());
                newElement.setBlockModifiers(element.getBlockModifiers());
                newElement.setDefault(element.getDefault());
                newElement.setFinalModifiers(element.getFinalModifiers());
                newElement.setFixed(element.getFixed());
                newElement.setForm(element.getForm());
                newElement.setId(element.getId());
                newElement.setTypeAttr(!complexType.isAnonymous());

                // Set nillable attribute
                if (element.getNillable()) {
                    newElement.setNillable();
                }

                // Update old schema map
                elementOldSchemaMap.put(newElement, elementOldSchemaMap.get(element));

                // Add element to choice pattern
                choicePattern.addParticle(newElement);
            }
        }

        // Replace element with new choice pattern if choice pattern contains more than one element
        if (choicePattern.getParticles().size() > 1) {
            parentParticle.setParticle(position, choicePattern);
        }
    }

    /**
     * Replaces top-level elements with more elements of the same elements but
     * with different typ-definitions.
     *
     * @param element Element which is used to be replaced by copies of itself.
     * @param parentSchema XSDSchema which contains the current element.
     * @param complexTypes Set of complexTypes which are used in the new
     * elements.
     */
    private void replaceTopLevelElementsWithMoreElements(Element element, XSDSchema parentSchema, LinkedHashSet<ComplexType> complexTypes) {

        // For each complexType add a new element to the choice pattern
        for (Iterator<ComplexType> it = complexTypes.iterator(); it.hasNext();) {
            ComplexType complexType = it.next();

            if (!complexType.isAbstract()) {

                // Create new element as copy of the old element
                Element newElement = new Element(element.getName(), new SymbolTableRef<Type>(complexType.getName(), complexType));

                // Copy element and its attributes
                newElement.setAbstract(element.getAbstract());
                newElement.setAnnotation(element.getAnnotation());
                newElement.setBlockModifiers(element.getBlockModifiers());
                newElement.setDefault(element.getDefault());
                newElement.setFinalModifiers(element.getFinalModifiers());
                newElement.setFixed(element.getFixed());
                newElement.setForm(element.getForm());
                newElement.setId(element.getId());
                newElement.setTypeAttr(!complexType.isAnonymous());

                // Set nillable attribute
                if (element.getNillable()) {
                    newElement.setNillable();
                }

                // Update old schema map
                elementOldSchemaMap.put(newElement, elementOldSchemaMap.get(element));

                // Add element to schema
                parentSchema.addElement(new SymbolTableRef<Element>(newElement.getName(), newElement));
            }
        }
    }

    /**
     * Fix choice patterns, that contain the same elements with the same types
     * multiple times for a given schema.
     * 
     * @param schema XSDSchema for which those choice patterns are fixed.
     */
    public void fixChoicePattern(XSDSchema schema) {
        for (Iterator<XSDSchema> it2 = getSchemata(schema).iterator(); it2.hasNext();) {
            XSDSchema currentSchema = it2.next();

            // Get all referenced types in the current schema
            for (Iterator<Type> it3 = currentSchema.getTypeSymbolTable().getAllReferencedObjects().iterator(); it3.hasNext();) {
                Type type = it3.next();

                // If the referred type has the same namespace as the schema
                if (type.getNamespace().equals(currentSchema.getTargetNamespace())) {

                    // Check if type is compleType
                    if (type instanceof ComplexType) {
                        ComplexType complexType = (ComplexType) type;

                        // If complexType has complex content check content for choice patterns
                        if (complexType.getContent() instanceof ComplexContentType) {
                            ComplexContentType complexContentType = (ComplexContentType) complexType.getContent();

                            if (complexContentType.getParticle() != null) {

                                // Fix choice patterns contained in the particle
                                fixChoicePattern(complexContentType.getParticle(), null, 0, new LinkedHashSet<Element>());
                            }
                        }
                    }
                }
            }

            // Get elements and any pattern contained in groups of the schema
            for (Iterator<Group> it3 = currentSchema.getGroups().iterator(); it3.hasNext();) {
                Group group = it3.next();

                // Fix choice patterns contained in the particle
                fixChoicePattern(group.getParticleContainer(), null, 0, new LinkedHashSet<Element>());
            }
        }
    }

    /**
     * Fix choice patterns, that contain the same elements with the same types
     * multiple times, in a given particle structure.
     *
     * @param particle Particle, which is checked.
     * @param parentParticle Parent of the current particle.
     * @param position Position of the current particle in the parent particle.
     * @param currentElements List of seen elements.
     */
    private void fixChoicePattern(Particle particle, ParticleContainer parentParticle, int position, LinkedHashSet<Element> currentElements) {

        if (particle instanceof ParticleContainer) {
            ParticleContainer particleContainer = (ParticleContainer) particle;

            // Choice has special handling
            if (particleContainer instanceof ChoicePattern) {
                LinkedList<Particle> particles = particleContainer.getParticles();

                // Get element sets of removable and current elements
                LinkedHashSet<Element> removableElements = new LinkedHashSet<Element>();

                // List of current elements meet on this recursion level
                LinkedHashSet<Element> newCurrentElements = new LinkedHashSet<Element>();

                for (int i = 0; i < particles.size(); i++) {
                    Particle currentParticle = particles.get(i);

                    // Fix choice patterns in particle container
                    fixChoicePattern(currentParticle, particleContainer, i, currentElements);

                    // Check if the particle is an element
                    if (particleContainer.getParticles().get(i) instanceof Element) {
                        Element element = (Element) particleContainer.getParticles().get(i);

                        // Check if same element is already contained
                        for (Iterator<Element> it = currentElements.iterator(); it.hasNext();) {
                            Element currentElement = it.next();

                            if (element.getName().equals(currentElement.getName()) &&
                                    element.getAbstract() == currentElement.getAbstract() &&
                                    element.getAnnotation() == currentElement.getAnnotation() &&
                                    element.getBlockModifiers() == currentElement.getBlockModifiers() &&
                                    element.getDefault() == currentElement.getDefault() &&
                                    element.getFinalModifiers() == currentElement.getFinalModifiers() &&
                                    element.getFixed() == currentElement.getFixed() &&
                                    element.getForm() == currentElement.getForm() &&
                                    element.getId() == currentElement.getId() &&
                                    element.getTypeAttr() == currentElement.getTypeAttr() &&
                                    element.getNillable() == currentElement.getNillable() &&
                                    element.getType() == currentElement.getType()) {

                                removableElements.add(element);
                            }
                        }
                        newCurrentElements.add(element);
                        currentElements.add(element);
                    }
                }
                // Remove new current elements form the original current elements
                currentElements.removeAll(newCurrentElements);

                // Update particle list
                particles = new LinkedList<Particle>();

                for (Iterator<Particle> it = particleContainer.getParticles().iterator(); it.hasNext();) {
                    Particle currentParticle = it.next();

                    // Check that only particles which were not removed and are not null are added to the new particles
                    if (currentParticle != null && !removableElements.contains(currentParticle)) {
                        particles.add(currentParticle);
                    }
                }

                // Set new particles
                particleContainer.setParticles(particles);

                // Check if choice pattern is empty or contains only one element
                if (particleContainer.getParticles().isEmpty()) {
                    parentParticle.setParticle(position, null);

                } else if (particleContainer.getParticles().size() == 1 && parentParticle != null) {

                    // If choice pattern contains only one element replace choice pattern with element
                    parentParticle.setParticle(position, particleContainer.getParticles().getFirst());
                }
            } else {

                // Get new current elements
                currentElements = new LinkedHashSet<Element>();

                // Check if the particle is a particle container with contained particles
                LinkedList<Particle> particles = particleContainer.getParticles();
                for (int i = 0; i < particles.size(); i++) {
                    Particle currentParticle = particles.get(i);

                    // Fix choice patterns in particle container
                    fixChoicePattern(currentParticle, particleContainer, i, currentElements);
                }
            }
        }
    }
}
