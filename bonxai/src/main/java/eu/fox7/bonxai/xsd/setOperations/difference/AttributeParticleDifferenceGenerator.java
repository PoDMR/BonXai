package eu.fox7.bonxai.xsd.setOperations.difference;

import eu.fox7.bonxai.common.Annotation;
import eu.fox7.bonxai.common.AnyAttribute;
import eu.fox7.bonxai.common.IdentifiedNamespace;
import eu.fox7.bonxai.common.ProcessContentsInstruction;
import eu.fox7.bonxai.common.SymbolTableRef;
import eu.fox7.bonxai.xsd.*;
import eu.fox7.bonxai.xsd.XSDSchema.Qualification;

import java.util.*;

/**
 * The AttributeParticleDifferenceGenerator class is responsible for creating
 * the difference of AttributeParticles. In order to do this the class contains
 * methods to compute the difference of various AttributeParticle parameters as
 * well as Attributes, AttributeRefs, AnyAttributes and so on. Furthermore a
 * distinction is drawn between local and global AttributeParticles, where local
 * Attributes are not registered in the resulting schema object itself.
 *
 * @author Dominik Wolff
 */
public class AttributeParticleDifferenceGenerator {

    // XSDSchema which will contain the difference of the minuend schema and the subtrahend schema
    private XSDSchema outputSchema;

    // Map mapping target namespaces to output schemata, this is necessary to reference components in other schemata
    private LinkedHashMap<String, XSDSchema> namespaceOutputSchemaMap;

    // Map mapping target namespaces to old minuend schemata used to construct the corresponding output schema
    private LinkedHashMap<String, XSDSchema> namespaceOldSchemaMap;

    // Map mapping each any attribute to the old schema, that contains the any attribute
    private LinkedHashMap<AnyAttribute, XSDSchema> anyAttributeOldSchemaMap;

    // HashMap mapping attributes to old schemata used to construct the new output schema
    private LinkedHashMap<Attribute, XSDSchema> attributeOldSchemaMap;

    // Set containing all IDs used in all new output schemata
    private LinkedHashSet<String> usedIDs;

    // Set of schemata not contained in a schema group
    private LinkedHashSet<XSDSchema> otherSchemata = new LinkedHashSet<XSDSchema>();

    // NamespaceAbbreviationMap maps namespaces to abbreviations
    private LinkedHashMap<String, String> namespaceAbbreviationMap;

    // Directory where the new output schemata will be stored (Default is ""c:\")
    private String workingDirectory = "c:/";

    // Type difference generator of the attribute particle difference generator class
    private TypeDifferenceGenerator typeDifferenceGenerator;

    /**
     * Constructor for the AttributeParticleDifferenceGenerator class, which
     * initializes all necessary fields of the class. The
     * AttributeParticleDifferenceGenerator mainly needs this information for
     * contained TypeDifferenceGenerators, which are used to generate new
     * anonymous types for i.e. attributes.
     *
     * @param outputSchema XSDSchema which will contain the new element structure.
     * @param namespaceOutputSchemaMap Map mapping namespaces to output
     * schemata.
     * @param namespaceOldSchemaMap Map mapping target namespaces to old minuend
     * schemata used to construct the corresponding output schema.
     * @param anyAttributeOldSchemaMap Map mapping each any attribute to the old
     * schema, that contains the any attribute.
     * @param attributeOldSchemaMap HashMap mapping attributes to old schemata
     * used to construct the new output schema.
     * @param usedIDs Set containing all IDs used in all new output schemata.
     * @param namespaceAbbreviationMap NamespaceAbbreviationMap maps namespaces
     * to abbreviations.
     * @param otherSchemata Set of schemata not contained in a schema group.
     * @param workingDirectory Directory where the new output schemata will be
     * stored.
     */
    public AttributeParticleDifferenceGenerator(XSDSchema outputSchema,
            LinkedHashMap<String, XSDSchema> namespaceOutputSchemaMap,
            LinkedHashMap<String, XSDSchema> namespaceOldSchemaMap,
            LinkedHashMap<AnyAttribute, XSDSchema> anyAttributeOldSchemaMap,
            LinkedHashMap<Attribute, XSDSchema> attributeOldSchemaMap,
            LinkedHashSet<String> usedIDs,
            LinkedHashMap<String, String> namespaceAbbreviationMap,
            LinkedHashSet<XSDSchema> otherSchemata, String workingDirectory) {

        //Initialize all class fields.
        this.outputSchema = outputSchema;
        this.namespaceOutputSchemaMap = namespaceOutputSchemaMap;
        this.namespaceOldSchemaMap = namespaceOldSchemaMap;
        this.anyAttributeOldSchemaMap = anyAttributeOldSchemaMap;
        this.attributeOldSchemaMap = attributeOldSchemaMap;
        this.usedIDs = usedIDs;
        this.namespaceAbbreviationMap = namespaceAbbreviationMap;
        this.otherSchemata = otherSchemata;
        this.workingDirectory = workingDirectory;
    }

    /**
     * When building a new complexType it is necessary to build the difference
     * of contained attributes. This method serves this purpose. A difference
     * for the given minuend and subtrahend attributes is constructed by
     * comparing attributes, attribute references and any attributes and
     * building differences for each, if they match other attribute particles.
     * Form and use attributes are important for the difference construction.
     *
     * @param minuendAttributeParticles Attributes of the minuned type.
     * @param subtrahendAttributeParticles Attributes of the subtrahend type
     * @return New attribute list which represents the difference of both
     * attribute lists.
     */
    public LinkedList<AttributeParticle> generateAttributeParticleDifference(LinkedList<AttributeParticle> minuendAttributeParticles, LinkedList<AttributeParticle> subtrahendAttributeParticles) {

        // Create map which maps attribute names to arrays of attributes, which can be used to generate new attributes
        LinkedHashMap<String, AttributeParticle[]> attributeNameAttributeParticlesMap = new LinkedHashMap<String, AttributeParticle[]>();

        // Create array of any attributes
        AnyAttribute[] anyAttributes = new AnyAttribute[2];

        // Get map mapping attribute groups to attribute group references
        LinkedHashMap<AttributeGroup, AttributeGroupRef> containedAttributeGroupAttributeRefMap = new LinkedHashMap<AttributeGroup, AttributeGroupRef>();
        LinkedList<AttributeGroup> containedAttributeGroups = new LinkedList<AttributeGroup>();

        // Build list from minuend and subtrahend input
        LinkedList<LinkedList<AttributeParticle>> attributeParticleLists = new LinkedList<LinkedList<AttributeParticle>>();
        attributeParticleLists.add(minuendAttributeParticles);

        // Subtrahend attribute particles can be null this is checked before they are added to the list
        if (subtrahendAttributeParticles != null) {
            attributeParticleLists.add(subtrahendAttributeParticles);
        }

        // Build attributeNameAttributeParticlesMap, which contains for each attribute name all corresponding attribute particles
        for (ListIterator<LinkedList<AttributeParticle>> it = attributeParticleLists.listIterator(); it.hasNext();) {
            LinkedList<AttributeParticle> attributeParticles = it.next();

            // Check each attribute particle contained in an attribute list of the attribute list list
            for (Iterator<AttributeParticle> it2 = attributeParticles.iterator(); it2.hasNext();) {
                AttributeParticle attributeParticle = it2.next();

                // Check if attribute particle is an attribute
                if (attributeParticle instanceof Attribute || attributeParticle instanceof AttributeRef) {

                    // Update attributeNameAttributeParticlesMap
                    updateAttributeNameAttributeParticlesMap(attributeNameAttributeParticlesMap, attributeParticle, 2, it.previousIndex());
                } else if (attributeParticle instanceof AnyAttribute) {
                    AnyAttribute anyAttribute = (AnyAttribute) attributeParticle;

                    // If attribute particle is an any attribute add any attribute to any attribute list
                    if (anyAttribute.getProcessContentsInstruction().equals(ProcessContentsInstruction.Skip) || anyAttribute.getProcessContentsInstruction().equals(ProcessContentsInstruction.Lax)) {
                        anyAttributes[it.previousIndex()] = anyAttribute;
                    }

                    // Add attributes contained in any attributes
                    for (Iterator<Attribute> it3 = getContainedAttributes(anyAttribute).iterator(); it3.hasNext();) {
                        Attribute attribute = it3.next();
                        updateAttributeNameAttributeParticlesMap(attributeNameAttributeParticlesMap, attribute, 2, it.previousIndex());
                    }
                } else if (attributeParticle instanceof AttributeGroupRef) {
                    AttributeGroup attributeGroup = ((AttributeGroupRef) attributeParticle).getAttributeGroup();

                    // If attribute particle is an attribute group add attribute group to map and list
                    containedAttributeGroupAttributeRefMap.put(attributeGroup, (AttributeGroupRef) attributeParticle);
                    containedAttributeGroups.add(attributeGroup);

                    // Check all attribute particles contained in the attribute group
                    for (Iterator<AttributeParticle> it3 = getAttributeParticles(attributeGroup, containedAttributeGroupAttributeRefMap).iterator(); it3.hasNext();) {
                        AttributeParticle groupAttributeParticle = it3.next();

                        // Update containedAttributeGroups list as well
                        containedAttributeGroups.addAll(containedAttributeGroupAttributeRefMap.keySet());

                        if (groupAttributeParticle instanceof Attribute || groupAttributeParticle instanceof AttributeRef) {

                            // Update attributeNameAttributeParticlesMap
                            updateAttributeNameAttributeParticlesMap(attributeNameAttributeParticlesMap, groupAttributeParticle, 2, it.previousIndex());
                        } else if (groupAttributeParticle instanceof AnyAttribute) {
                            AnyAttribute anyAttribute = (AnyAttribute) groupAttributeParticle;

                            // If attribute particle is an any attribute add any attribute to any attribute list
                            if (anyAttribute.getProcessContentsInstruction().equals(ProcessContentsInstruction.Skip) || anyAttribute.getProcessContentsInstruction().equals(ProcessContentsInstruction.Lax)) {
                                anyAttributes[it.previousIndex()] = anyAttribute;
                            }

                            // Add attributes contained in any attributes
                            for (Iterator<Attribute> it4 = getContainedAttributes(anyAttribute).iterator(); it4.hasNext();) {
                                Attribute attribute = it4.next();
                                updateAttributeNameAttributeParticlesMap(attributeNameAttributeParticlesMap, attribute, 2, it.previousIndex());
                            }
                        }
                    }
                }
            }
        }

        // If in some complexTypes no attribute with corresponding name is present but an any attribute this any attribute is placed in the attributeNameAttributeParticlesMap
        for (int i = 0; i < anyAttributes.length; i++) {
            AnyAttribute anyAttribute = anyAttributes[i];

            // For attribute name entry in the attributeNameAttributeParticlesMap check if the current array position is null and if  place any attribute in this position
            for (Iterator<String> it = attributeNameAttributeParticlesMap.keySet().iterator(); it.hasNext();) {
                String attributeName = it.next();

                // If the a current array position is null place any attribute in this position (can be null as well)
                if (attributeNameAttributeParticlesMap.get(attributeName)[i] == null && anyAttribute != null && isIncluded(attributeName, anyAttribute)) {
                    attributeNameAttributeParticlesMap.get(attributeName)[i] = anyAttribute;
                }
            }
        }

        // Get set of removable attributes
        LinkedHashSet<String> removableAttributes = new LinkedHashSet<String>();

        // Check for each entry if the minuend attribute particle is present
        for (Iterator<String> it = attributeNameAttributeParticlesMap.keySet().iterator(); it.hasNext();) {
            String attributeName = it.next();

            if (attributeNameAttributeParticlesMap.get(attributeName)[0] == null) {
                removableAttributes.add(attributeName);
            }
        }

        // Remove entries without minuend attribute particles
        for (Iterator<String> it = removableAttributes.iterator(); it.hasNext();) {
            String attributeName = it.next();
            attributeNameAttributeParticlesMap.remove(attributeName);
        }

        // Check if prohibited and required are use attributes of the same minuend and subtrahend attribute
        for (Iterator<String> it = attributeNameAttributeParticlesMap.keySet().iterator(); it.hasNext();) {
            String attributeName = it.next();

            // Usa variables to store result
            boolean isRequired = false;
            boolean isProhibited = false;

            for (int i = 0; i < 2; i++) {

                // If attribute is not present it is the same as if the attribute would be prohibited
                if (attributeNameAttributeParticlesMap.get(attributeName)[i] == null) {
                    isProhibited = true;

                } else if (attributeNameAttributeParticlesMap.get(attributeName)[i] instanceof Attribute) {
                    Attribute attribute = (Attribute) attributeNameAttributeParticlesMap.get(attributeName)[i];

                    // If attribute has use set variable respectivly
                    if (attribute.getUse() != null && attribute.getUse() == AttributeUse.Required) {
                        isRequired = true;
                    }
                    if (attribute.getUse() != null && attribute.getUse() == AttributeUse.Prohibited) {
                        isProhibited = true;
                    }
                } else if (attributeNameAttributeParticlesMap.get(attributeName)[i] instanceof AttributeRef) {
                    AttributeRef attributeRef = (AttributeRef) attributeNameAttributeParticlesMap.get(attributeName)[i];

                    // If attribute reference has use set variable respectivly
                    if (attributeRef.getUse() != null && attributeRef.getUse() == AttributeUse.Required) {
                        isRequired = true;
                    }
                    if (attributeRef.getUse() != null && attributeRef.getUse() == AttributeUse.Prohibited) {
                        isProhibited = true;
                    }
                }
            }

            // If both variables are true only the minuend attribute particles are a valid result
            if (isProhibited && isRequired && subtrahendAttributeParticles != null) {
                return generateAttributeParticleDifference(minuendAttributeParticles, null);
            }
        }

        // Get list of new attribute uses
        LinkedList<AttributeUse> attributeUses = getAttributeUses(attributeNameAttributeParticlesMap);

        // Get new list to store simpleTypes in and new boolean variable to check wether a typ is changed
        LinkedList<SimpleType> simpleTypes = new LinkedList<SimpleType>();
        boolean changedAType = false;

        // Check for each attribute name the new typ definition
        for (Iterator<String> it = attributeNameAttributeParticlesMap.keySet().iterator(); it.hasNext();) {
            String attributeName = it.next();

            // Check if minuend attriute particle is attribute
            if (attributeNameAttributeParticlesMap.get(attributeName)[0] instanceof Attribute) {
                Attribute minuendAttribute = (Attribute) attributeNameAttributeParticlesMap.get(attributeName)[0];

                // Check if subtrahend attriute particle is attribute
                if (attributeNameAttributeParticlesMap.get(attributeName)[1] instanceof Attribute) {
                    Attribute subtrahendAttribute = (Attribute) attributeNameAttributeParticlesMap.get(attributeName)[1];

                    // If both attributes contain the same fixed values set result to empty
                    if ((minuendAttribute.getFixed() != null && minuendAttribute.getFixed().equals(subtrahendAttribute.getFixed())) || subtrahendAttribute.getUse() == AttributeUse.Prohibited) {
                        simpleTypes.add(null);
                    } else {

                        // Generate new simpleType
                        SimpleType newSimpleType = typeDifferenceGenerator.generateNewSimpleType(minuendAttribute.getSimpleType(), subtrahendAttribute.getSimpleType(), getTypeName(minuendAttribute.getSimpleType(), subtrahendAttribute.getSimpleType()));

                        // If new simpleType is not null set changedAType to true
                        if (newSimpleType != null) {

                            // If another simpleType was already changed return unchanged minuend attribute particles
                            if (changedAType == true) {
                                return generateAttributeParticleDifference(minuendAttributeParticles, null);
                            }
                            changedAType = true;
                        }

                        // Add simpleType to list
                        simpleTypes.add(newSimpleType);
                    }
                } else if (attributeNameAttributeParticlesMap.get(attributeName)[1] instanceof AttributeRef) {
                    AttributeRef subtrahendAttributeRef = (AttributeRef) attributeNameAttributeParticlesMap.get(attributeName)[1];

                    // If both attributes contain the same fixed values set result to empty
                    if ((minuendAttribute.getFixed() != null && minuendAttribute.getFixed().equals(subtrahendAttributeRef.getFixed())) || subtrahendAttributeRef.getUse() == AttributeUse.Prohibited) {
                        simpleTypes.add(null);
                    } else {

                        // Generate new simpleType
                        SimpleType newSimpleType = typeDifferenceGenerator.generateNewSimpleType(minuendAttribute.getSimpleType(), subtrahendAttributeRef.getAttribute().getSimpleType(), getTypeName(minuendAttribute.getSimpleType(), subtrahendAttributeRef.getAttribute().getSimpleType()));

                        // If new simpleType is not null set changedAType to true
                        if (newSimpleType != null) {

                            // If another simpleType was already changed return unchanged minuend attribute particles
                            if (changedAType == true) {
                                return generateAttributeParticleDifference(minuendAttributeParticles, null);
                            }
                            changedAType = true;
                        }

                        // Add simpleType to list
                        simpleTypes.add(newSimpleType);
                    }
                } else if (attributeNameAttributeParticlesMap.get(attributeName)[1] instanceof AnyAttribute) {

                    // If subtrahend attribute particle is an any attribute the type difference is empty
                    simpleTypes.add(null);

                } else if (attributeNameAttributeParticlesMap.get(attributeName)[1] == null) {

                    // If subtrahend attribute particle is an empty attribute minuend type is not changed
                    simpleTypes.add(minuendAttribute.getSimpleType());
                }
            } else if (attributeNameAttributeParticlesMap.get(attributeName)[0] instanceof AttributeRef) {
                AttributeRef minuendAttributeRef = (AttributeRef) attributeNameAttributeParticlesMap.get(attributeName)[0];

                // Check if subtrahend attriute particle is attribute
                if (attributeNameAttributeParticlesMap.get(attributeName)[1] instanceof Attribute) {
                    Attribute subtrahendAttribute = (Attribute) attributeNameAttributeParticlesMap.get(attributeName)[1];

                    // If both attributes contain the same fixed values set result to empty
                    if ((minuendAttributeRef.getFixed() != null && minuendAttributeRef.getFixed().equals(subtrahendAttribute.getFixed())) || subtrahendAttribute.getUse() == AttributeUse.Prohibited) {
                        simpleTypes.add(null);
                    } else {

                        // Generate new simpleType
                        SimpleType newSimpleType = typeDifferenceGenerator.generateNewSimpleType(minuendAttributeRef.getAttribute().getSimpleType(), subtrahendAttribute.getSimpleType(), getTypeName(minuendAttributeRef.getAttribute().getSimpleType(), subtrahendAttribute.getSimpleType()));

                        // If new simpleType is not null set changedAType to true
                        if (newSimpleType != null) {

                            // If another simpleType was already changed return unchanged minuend attribute particles
                            if (changedAType == true) {
                                return generateAttributeParticleDifference(minuendAttributeParticles, null);
                            }
                            changedAType = true;
                        }

                        // Add simpleType to list
                        simpleTypes.add(newSimpleType);
                    }
                } else if (attributeNameAttributeParticlesMap.get(attributeName)[1] instanceof AttributeRef) {
                    AttributeRef subtrahendAttributeRef = (AttributeRef) attributeNameAttributeParticlesMap.get(attributeName)[1];

                    // If both attributes contain the same fixed values set result to empty
                    if ((minuendAttributeRef.getFixed() != null && minuendAttributeRef.getFixed().equals(subtrahendAttributeRef.getFixed())) || subtrahendAttributeRef.getUse() == AttributeUse.Prohibited) {
                        simpleTypes.add(null);
                    } else {

                        // Generate new simpleType
                        SimpleType newSimpleType = typeDifferenceGenerator.generateNewSimpleType(minuendAttributeRef.getAttribute().getSimpleType(), subtrahendAttributeRef.getAttribute().getSimpleType(), getTypeName(minuendAttributeRef.getAttribute().getSimpleType(), subtrahendAttributeRef.getAttribute().getSimpleType()));

                        // If new simpleType is not null set changedAType to true
                        if (newSimpleType != null) {

                            // If another simpleType was already changed return unchanged minuend attribute particles
                            if (changedAType == true) {
                                return generateAttributeParticleDifference(minuendAttributeParticles, null);
                            }
                            changedAType = true;
                        }

                        // Add simpleType to list
                        simpleTypes.add(newSimpleType);
                    }
                } else if (attributeNameAttributeParticlesMap.get(attributeName)[1] instanceof AnyAttribute) {

                    // If subtrahend attribute particle is an any attribute the type difference is empty
                    simpleTypes.add(null);

                } else if (attributeNameAttributeParticlesMap.get(attributeName)[1] == null) {

                    // If subtrahend attribute particle is an empty attribute minuend type is not changed
                    simpleTypes.add(minuendAttributeRef.getAttribute().getSimpleType());
                }
            } else if (attributeNameAttributeParticlesMap.get(attributeName)[0] instanceof AnyAttribute) {

                // If minuend attribute particle is an any attribute and subtrahend attribute particle is an attribute
                if (attributeNameAttributeParticlesMap.get(attributeName)[1] instanceof Attribute) {
                    Attribute subtrahendAttribute = (Attribute) attributeNameAttributeParticlesMap.get(attributeName)[1];

                    // Check if the subtrahend attribute particle contains an unrestricted anySimpleType
                    if (containsUnrestrictedAnySimpleType(subtrahendAttribute.getSimpleType())) {

                        // The result of the difference of an anyType and another anySimpleType is empty
                        simpleTypes.add(null);
                    } else {

                        // Any attribute has anySimpleType
                        simpleTypes.add(new SimpleType("{http://www.w3.org/2001/XMLSchema}anySimpleType", null));
                    }
                } else if (attributeNameAttributeParticlesMap.get(attributeName)[1] instanceof AttributeRef) {
                    AttributeRef subtrahendAttributeRef = (AttributeRef) attributeNameAttributeParticlesMap.get(attributeName)[1];

                    // Check if the subtrahend attribute particle contains an unrestricted anySimpleType
                    if (containsUnrestrictedAnySimpleType(subtrahendAttributeRef.getAttribute().getSimpleType())) {

                        // The result of the difference of an anyType and another anySimpleType is empty
                        simpleTypes.add(null);
                    } else {

                        // Any attribute has anySimpleType
                        simpleTypes.add(new SimpleType("{http://www.w3.org/2001/XMLSchema}anySimpleType", null));
                    }
                }
            } else if (attributeNameAttributeParticlesMap.get(attributeName)[1] == null) {

                // If minuend type is not present result is empty
                simpleTypes.add(null);
            }
        }

        // Get minuend and subtrahend counter
        int minuendCounter = getCounter(attributeNameAttributeParticlesMap, 0);
        int subtrahendCounter = getCounter(attributeNameAttributeParticlesMap, 1);

        // Check if any attributes allow difference
        if (generateNewAnyAttribute(anyAttributes[1], anyAttributes[0]) == null && generateNewAnyAttribute(anyAttributes[0], anyAttributes[1]) != null && subtrahendAttributeParticles != null) {
            return generateAttributeParticleDifference(minuendAttributeParticles, null);
        }

        // Check if counter allow difference
        if (minuendCounter - subtrahendCounter > minuendCounter / 2) {
            return generateAttributeParticleDifference(minuendAttributeParticles, null);
        }

        // Check if difference is empty
        if (isEmpty(simpleTypes) && isEmpty(attributeUses) && subtrahendAttributeParticles != null) {
            return null;
        }
        boolean changedAAnyAttribute = false;

        if (subtrahendAttributeParticles != null) {

            // Get set of removable attributes
            removableAttributes = new LinkedHashSet<String>();

            // Get position pointer
            int position = 0;

            for (Iterator<String> it = attributeNameAttributeParticlesMap.keySet().iterator(); it.hasNext();) {
                String attributeName = it.next();

                // Check if minuend attriute particle is attribute
                if (attributeNameAttributeParticlesMap.get(attributeName)[0] instanceof Attribute) {
                    Attribute minuendAttribute = (Attribute) attributeNameAttributeParticlesMap.get(attributeName)[0];

                    // Check if new simpleType is empty or a changed simpleType
                    if (simpleTypes.get(position) == null) {

                        // Check if new attribute use is present else take old one
                        if (attributeUses.get(position) == null) {

                            // Set subtrahend attribute particle to null
                            attributeNameAttributeParticlesMap.get(attributeName)[1] = null;
                        } else {

                            // Generate new attribute
                            attributeNameAttributeParticlesMap.get(attributeName)[0] = generateNewAttribute(minuendAttribute, attributeUses.get(position), null);
                        }
                    } else if (simpleTypes.get(position) != minuendAttribute.getSimpleType()) {

                        // Generate new attribute
                        attributeNameAttributeParticlesMap.get(attributeName)[0] = generateNewAttribute(minuendAttribute, null, simpleTypes.get(position));
                    } else if (simpleTypes.get(position) == minuendAttribute.getSimpleType()) {

                        // Set subtrahend attribute particle to null
                        attributeNameAttributeParticlesMap.get(attributeName)[1] = null;
                    }
                } else if (attributeNameAttributeParticlesMap.get(attributeName)[0] instanceof AttributeRef) {
                    AttributeRef minuendAttributeRef = (AttributeRef) attributeNameAttributeParticlesMap.get(attributeName)[0];

                    // Check if new simpleType is empty or a changed simpleType
                    if (simpleTypes.get(position) == null) {

                        // Check if new attribute use is present else take old one
                        if (attributeUses.get(position) == null) {

                            // Set subtrahend attribute particle to null
                            attributeNameAttributeParticlesMap.get(attributeName)[1] = null;
                        } else {

                            // Generate new attribute
                            attributeNameAttributeParticlesMap.get(attributeName)[0] = generateNewAttribute(minuendAttributeRef, attributeUses.get(position), null);
                        }
                    } else if (simpleTypes.get(position) != minuendAttributeRef.getAttribute().getSimpleType()) {

                        // Generate new attribute
                        attributeNameAttributeParticlesMap.get(attributeName)[0] = generateNewAttribute(minuendAttributeRef, null, simpleTypes.get(position));
                    } else if (simpleTypes.get(position) == minuendAttributeRef.getAttribute().getSimpleType()) {

                        // Set subtrahend attribute particle to null
                        attributeNameAttributeParticlesMap.get(attributeName)[1] = null;
                    }
                } else if (attributeNameAttributeParticlesMap.get(attributeName)[0] instanceof AnyAttribute && generateNewAnyAttribute(anyAttributes[0], anyAttributes[1]) == null) {

                    // Check if more than on attribute was computed with any attribute
                    if (changedAAnyAttribute) {
                        return generateAttributeParticleDifference(minuendAttributeParticles, null);
                    }

                    // Check if type difference is empty
                    if (simpleTypes.get(position) == null) {
                        changedAAnyAttribute = true;

                        // Get new base attribute
                        Attribute attribute = new Attribute(attributeName);

                        // Set form depending on name
                        if (attributeName.contains("{}")) {
                            attribute.setForm(null);
                        } else {
                            attribute.setForm(Qualification.qualified);
                        }

                        // Check if use difference is empty
                        if (attributeUses.get(position) == null) {

                            // Generate new attribute
                            attributeNameAttributeParticlesMap.get(attributeName)[0] = generateNewAttribute(attribute, AttributeUse.Optional, new SimpleType("{http://www.w3.org/2001/XMLSchema}anySimpleType", null));
                        } else {

                            // Generate new attribute
                            attributeNameAttributeParticlesMap.get(attributeName)[0] = generateNewAttribute(attribute, attributeUses.get(position), new SimpleType("{http://www.w3.org/2001/XMLSchema}anySimpleType", null));
                        }
                    } else {
                        removableAttributes.add(attributeName);
                    }
                }
                position = position + 1;
            }

            // Remove entries without minuend attribute particles
            for (Iterator<String> it = removableAttributes.iterator(); it.hasNext();) {
                String attributeName = it.next();
                attributeNameAttributeParticlesMap.remove(attributeName);
            }
        }

        // Sort the contained attribute groups according to their size
        Collections.sort(containedAttributeGroups, new Comparator<AttributeGroup>() {

            public int compare(AttributeGroup group1, AttributeGroup group2) {
                Integer group1Size = getAttributeParticles(group1, new LinkedHashMap<AttributeGroup, AttributeGroupRef>()).size();
                Integer group2Size = getAttributeParticles(group2, new LinkedHashMap<AttributeGroup, AttributeGroupRef>()).size();
                return -(group1Size.compareTo(group2Size));
            }
        });

        // New attribute particle list, which is the result of this method
        LinkedList<AttributeParticle> newAttributeParticles = new LinkedList<AttributeParticle>();

        // Get new attribute group list, which contains attribute groups, which were used
        LinkedList<AttributeGroup> alreadyUsedAttributeGroups = new LinkedList<AttributeGroup>();

        // Check for each attribute group if it has to be resolved
        for (Iterator<AttributeGroup> it = containedAttributeGroups.iterator(); it.hasNext();) {
            AttributeGroup attributeGroup = it.next();

            // Check if the current attribute group has already been seen
            if (!alreadyUsedAttributeGroups.contains(attributeGroup)) {

                // Use variables to store information about attribute group resolution
                boolean useAttributeGroup = true;

                // For all attribute particles contained in the current attribute group check if the attribute particle has to be changed
                for (Iterator<AttributeParticle> it2 = getAttributeParticles(attributeGroup, new LinkedHashMap<AttributeGroup, AttributeGroupRef>()).iterator(); it2.hasNext();) {
                    AttributeParticle attributeParticle = it2.next();

                    // Check if attribute is an attribute, an attribute reference or an anyAttribute
                    if (attributeParticle instanceof Attribute) {
                        Attribute attribute = (Attribute) attributeParticle;
                        String attributeName = "";

                        if ((attribute.getForm() == null && attributeOldSchemaMap.get(attribute).getElementFormDefault() == Qualification.qualified) || (attribute.getForm() != null && attribute.getForm().equals(Qualification.qualified))) {
                            attributeName = attribute.getName();
                        } else {
                            attributeName = "{}" + attribute.getLocalName();
                        }

                        // Check if attribute name is not contained in attributeNameAttributeParticlesMap or if attributeNameAttributeParticlesMap contains more than one attribute for the attribute name
                        // or an other attribute, if this is the case resolve attribute group
                        if (!attributeNameAttributeParticlesMap.containsKey(attributeName) || !containsOnlyOne(attributeNameAttributeParticlesMap.get(attributeName))) {
                            useAttributeGroup = false;
                        }
                    } else if (attributeParticle instanceof AttributeRef) {
                        AttributeRef attributeRef = (AttributeRef) attributeParticle;

                        String attributeName = "";

                        if (attributeRef.getAttribute().getForm().equals(Qualification.qualified)) {
                            attributeName = attributeRef.getAttribute().getName();
                        } else {
                            attributeName = "{}" + attributeRef.getAttribute().getLocalName();
                        }

                        // Check if referred attribute name is not contained in attributeNameAttributeParticlesMap or if attributeNameAttributeParticlesMap contains more than one attribute for the referred
                        // attribute name or an other attribute, if this is the case resolve attribute group
                        if (!attributeNameAttributeParticlesMap.containsKey(attributeName) || !containsOnlyOne(attributeNameAttributeParticlesMap.get(attributeName))) {
                            useAttributeGroup = false;
                        }
                    } else if (attributeParticle instanceof AnyAttribute) {
                        AnyAttribute anyAttribute = (AnyAttribute) attributeParticle;

                        // If contained attribute particle is an anyAttribute check if more than one anyAttribute exist and resolve attribute group if it does
                        if (!containsOnlyOne(anyAttributes) || anyAttribute.getProcessContentsInstruction().equals(ProcessContentsInstruction.Strict) || anyAttribute.getProcessContentsInstruction().equals(ProcessContentsInstruction.Lax)) {
                            useAttributeGroup = false;
                        }
                    }
                }

                // Check if current attribute can be used
                if (useAttributeGroup) {

                    // If anyAttribute is contained in the attribute group and the containedAnyAttributes list only contained one anyAttribute clear the list
                    anyAttributes = new AnyAttribute[2];

                    // Get attribute groups contained in the current attribute group with this new map
                    LinkedHashMap<AttributeGroup, AttributeGroupRef> usedAttributeGroupAttributeRefMap = new LinkedHashMap<AttributeGroup, AttributeGroupRef>();

                    // Remove each attribute particle contained in the attribute group from the attributeNameAttributeParticlesMap by removing the corresponding attribute particle name entries
                    for (Iterator<AttributeParticle> it2 = getAttributeParticles(attributeGroup, usedAttributeGroupAttributeRefMap).iterator(); it2.hasNext();) {
                        AttributeParticle attributeParticle = it2.next();

                        // Add contained attribute groups to list of already seen attribute groups
                        alreadyUsedAttributeGroups.addAll(usedAttributeGroupAttributeRefMap.keySet());

                        // Check if removed attribute particle is an attribute or an attribute reference
                        if (attributeParticle instanceof Attribute) {

                            String attributeName = "";

                            if (((Attribute) attributeParticle).getForm() != null && ((Attribute) attributeParticle).getForm().equals(Qualification.qualified)) {
                                attributeName = ((Attribute) attributeParticle).getName();
                            } else {
                                attributeName = "{}" + ((Attribute) attributeParticle).getLocalName();
                            }
                            attributeNameAttributeParticlesMap.remove(attributeName);
                        } else if (attributeParticle instanceof AttributeRef) {

                            String attributeName = "";

                            if ((((AttributeRef) attributeParticle).getAttribute()).getForm().equals(Qualification.qualified)) {
                                attributeName = (((AttributeRef) attributeParticle).getAttribute()).getName();
                            } else {
                                attributeName = "{}" + (((AttributeRef) attributeParticle).getAttribute()).getLocalName();
                            }
                            attributeNameAttributeParticlesMap.remove(attributeName);
                        }
                    }

                    // Generate new attribute group reference and add the reference to the resulting attribute particle list
                    AttributeGroupRef newAttributeGroupRef = generateNewAttributeGroupRef(containedAttributeGroupAttributeRefMap.get(attributeGroup));
                    newAttributeParticles.add(newAttributeGroupRef);
                }
            }
        }

        // For each entry in the attributeNameAttributeParticlesMap create a new attribute particle
        for (Iterator<String> it = attributeNameAttributeParticlesMap.keySet().iterator(); it.hasNext();) {
            String attributeName = it.next();

            // If only one attribute particle is contained in the current entry
            if (containsOnlyOne(attributeNameAttributeParticlesMap.get(attributeName))) {

                // Get attribute particle contained in the entry
                AttributeParticle oldAttributeParticle = attributeNameAttributeParticlesMap.get(attributeName)[0];

                // Check if the contained attribute particle is an attribute or attribute reference and construct a new attribute particle accordingly
                AttributeParticle newAttributeParticle = null;
                if (oldAttributeParticle instanceof Attribute) {

                    if (attributeOldSchemaMap.containsKey(oldAttributeParticle)) {
                        newAttributeParticle = generateNewAttribute((Attribute) oldAttributeParticle);
                    } else {
                        newAttributeParticle = oldAttributeParticle;
                    }
                } else if (oldAttributeParticle instanceof AttributeRef) {
                    newAttributeParticle = generateNewAttributeRef((AttributeRef) oldAttributeParticle);
                }
                // If the new attribute particle is not null add it to the resulting attribute particle list
                if (newAttributeParticle != null) {
                    newAttributeParticles.add(newAttributeParticle);
                }
            } else {

                // New Attribute particle was already build
                newAttributeParticles.add(attributeNameAttributeParticlesMap.get(attributeName)[0]);
            }
        }

        // Generate new any attributes
        if (generateNewAnyAttribute(anyAttributes[0], anyAttributes[1]) != null) {
            newAttributeParticles.add(generateNewAnyAttribute(anyAttributes[0], anyAttributes[1]));
        } else if (anyAttributes[0] != null) {
            newAttributeParticles.add(generateNewAnyAttribute(anyAttributes[0], null));
        }

        // Return the list of resulting attribute particles
        return newAttributeParticles;
    }

    /**
     * Generate new attribute particle for a given attribute particle. If use or
     * type are specified these replace the attributes of the old attribute
     * particle.
     * @param attributeParticle Attribute particle used to construct a new
     * attribute particle.
     * @param use AttributeUse which is use for the new attribute particle if
     * not null.
     * @param type Type which is use for the new attribute particle if not null.
     * @return New Attribute particle, which may be an attribute or attribute
     * reference.
     */
    private AttributeParticle generateNewAttribute(AttributeParticle attributeParticle, AttributeUse use, Type type) {

        // Check if all attribute particles are attribute references, which reference the same attribute and this attribute is not in conflict
        if (attributeParticle instanceof AttributeRef && type == null) {

            // Use the first attribute refernce to get the attribute SymbolTableRef
            AttributeRef oldAttributeRef = (AttributeRef) attributeParticle;

            // Check if attribute is contained in the output schema
            if (!oldAttributeRef.getAttribute().getNamespace().equals(outputSchema.getTargetNamespace())) {

                // Get the output schema in which the referenced attribute will be contained
                XSDSchema otherOutputSchema = namespaceOutputSchemaMap.get(oldAttributeRef.getAttribute().getNamespace());

                // Get SymbolTableRef in the other output schema
                SymbolTableRef<Type> symbolTableRef = otherOutputSchema.getTypeSymbolTable().getReference(oldAttributeRef.getAttribute().getName());

                // Add this SymbolTableRef to the output schema (This is correct even when the attribute is not present in the other output schema)
                outputSchema.getTypeSymbolTable().setReference(symbolTableRef.getKey(), symbolTableRef);
            }

            // Get correct reference stored in the new schema
            AttributeRef newAttributeRef = new AttributeRef(outputSchema.getAttributeSymbolTable().getReference(oldAttributeRef.getAttribute().getName()));

            // Set new id and annotation for new attribute reference
            newAttributeRef.setAnnotation(oldAttributeRef.getAnnotation());
            newAttributeRef.setId(getID(oldAttributeRef.getId()));

            // Set "default", "fixed" and "use" values for the new attribute refernce
            if (use == null || use == AttributeUse.Optional) {
                newAttributeRef.setDefault(oldAttributeRef.getDefault());
            }

            // Set new use value depending on if a value was specified or not
            if (use != null) {
                newAttributeRef.setUse(use);
            } else {
                newAttributeRef.setUse(oldAttributeRef.getUse());
            }
            newAttributeRef.setFixed(oldAttributeRef.getFixed());

            // Return new attribute reference
            return newAttributeRef;
        }

        // Get new simpleType for the new attribute
        Type newSimpleType = new SimpleType("{http://www.w3.org/2001/XMLSchema}anyType", null);

        // If attribute is not prohibited
        if (!(attributeParticle instanceof Attribute && ((Attribute) attributeParticle).getUse() != null && ((Attribute) attributeParticle).getUse().equals(AttributeUse.Prohibited)) && !(attributeParticle instanceof AttributeRef && ((AttributeRef) attributeParticle).getUse() != null && ((AttributeRef) attributeParticle).getUse().equals(AttributeUse.Prohibited))) {

            // Get new simpleType depending on if a simpleType was specified or not
            if (type == null) {
                newSimpleType = getSimpleType(attributeParticle);
            } else {
                newSimpleType = (SimpleType) type;
            }
        }

        // Get SymbolTable reference for the new simpleType
        SymbolTableRef<Type> newSymbolTableRef = outputSchema.getTypeSymbolTable().getReference(newSimpleType.getName());

        // Create new attribute
        Attribute newAttribute = new Attribute(getName(attributeParticle), newSymbolTableRef);

        // Set "ID" attribute and annotation
        newAttribute.setAnnotation(getAnnotation(attributeParticle));
        newAttribute.setId(getID(attributeParticle));

        // Set "form" and "use" attributes (not present for top-level attributes)
        if (getForm(attributeParticle) == Qualification.qualified) {
            newAttribute.setForm(Qualification.qualified);
        } else {
            newAttribute.setForm(null);
        }

        // Set new use value depending on if a value was specified or not
        if (use == null) {
            newAttribute.setUse(getUse(attributeParticle));
        } else {
            newAttribute.setUse(use);
        }

        // Set "default" attribute and type attribute for the new attribute
        newAttribute.setDefault(getDefault(attributeParticle, newAttribute.getUse() == AttributeUse.Optional));
        newAttribute.setTypeAttr(!newSimpleType.isAnonymous());

        // Get new "fixed" attribute, this can result in a new type
        newAttribute.setFixed(getFixed(attributeParticle));

        // Check if the attribute was qualified and if add new group to another schema
        if (newAttribute.getForm() != null && newAttribute.getForm() == XSDSchema.Qualification.qualified && !newAttribute.getNamespace().equals(outputSchema.getTargetNamespace())) {

            // Get other schema to store element group in
            XSDSchema otherSchema = new XSDSchema(newAttribute.getNamespace());
            otherSchema.getNamespaceList().addIdentifiedNamespace(new IdentifiedNamespace("xs", "http://www.w3.org/2001/XMLSchema"));
            otherSchema.setSchemaLocation(workingDirectory + "OtherSchema(" + newAttribute.getNamespace() + ").xsd");

            // Check if schema exist in schema groups
            if (namespaceOutputSchemaMap.get(newAttribute.getNamespace()) != null) {
                otherSchema = namespaceOutputSchemaMap.get(newAttribute.getNamespace());
            } else {

                // Check if other schema is present in other schema list
                for (Iterator<XSDSchema> it = otherSchemata.iterator(); it.hasNext();) {
                    XSDSchema currentOtherSchema = it.next();

                    // If other schema has same namespace as the element use this schema
                    if (otherSchema.getTargetNamespace().equals(newAttribute.getNamespace())) {
                        otherSchema = currentOtherSchema;
                    }
                }

                // Add other schema to schema set
                otherSchemata.add(otherSchema);
            }

            // Build new attribute group name and avoid conflicts
            String newAttributeGroupNameBase = "{" + otherSchema.getTargetNamespace() + "}" + "attribute.group." + newAttribute.getLocalName();
            String newAttributeGroupName = newAttributeGroupNameBase;
            int number = 1;

            // Check if group name is already taken in the other schema and add new number to the group name base until the name is available
            while (otherSchema.getGroupSymbolTable().hasReference(newAttributeGroupName)) {
                newAttributeGroupName = newAttributeGroupNameBase + "." + number;
                number++;
            }

            // Build new group and add it to the other schema
            AttributeGroup newAttributeGroup = new AttributeGroup(newAttributeGroupName);
            newAttributeGroup.addAttributeParticle(newAttribute);
            otherSchema.getAttributeGroupSymbolTable().updateOrCreateReference(newAttributeGroupName, newAttributeGroup);
            otherSchema.addAttributeGroup(otherSchema.getAttributeGroupSymbolTable().getReference(newAttributeGroupName));

            // Generate new import components in both schemata if necessary
            generateNewImport(outputSchema, otherSchema);
            generateNewImport(otherSchema, outputSchema);

            // Register group in output schema and return group reference
            outputSchema.getAttributeGroupSymbolTable().updateOrCreateReference(newAttributeGroupName, newAttributeGroup);
            return new AttributeGroupRef(outputSchema.getAttributeGroupSymbolTable().getReference(newAttributeGroupName));
        }

        // Return new attribute
        return newAttribute;
    }

    private AttributeParticle generateNewAnyAttribute(AnyAttribute minuendAnyAttribute, AnyAttribute subtrahendAnyAttribute) {

        // Check if minuend attribute is not null
        if (minuendAnyAttribute == null) {
            return null;
        }

        // Get new "namespace" attribute
        String newNamespace = getDifferenceNamespace(minuendAnyAttribute, subtrahendAnyAttribute);

        // If new "namespace" attribute is empty return null else create new any attribute
        if (newNamespace.equals("")) {
            return null;
        } else {

            // Create new any attribute
            AnyAttribute newAnyAttribute = new AnyAttribute(ProcessContentsInstruction.Skip, newNamespace);

            // Set new id and annotation for new any attribute
            newAnyAttribute.setAnnotation(generateNewAnnotation(minuendAnyAttribute.getAnnotation()));
            newAnyAttribute.setId(getID(minuendAnyAttribute.getId()));

            // Return new any pattern
            return newAnyAttribute;
        }
    }

    /**
     * Get the count of possible attribute combinations for a given attribute
     * list.
     * @param attributeNameAttributeParticlesMap Map mapping attribute names to
     * an array of attribute particles.
     * @param position Position in the array.
     * @return New integer representing the number of possible combinations.
     */
    private int getCounter(LinkedHashMap<String, AttributeParticle[]> attributeNameAttributeParticlesMap, int position) {

        // Initialize counter
        int counter = 1;

        // Check all entries of the attributeNameAttributeParticlesMap
        for (Iterator<String> it = attributeNameAttributeParticlesMap.keySet().iterator(); it.hasNext();) {
            String attributeName = it.next();

            // Check if current entry is an attribute
            if (attributeNameAttributeParticlesMap.get(attributeName)[position] instanceof Attribute) {
                Attribute attribute = (Attribute) attributeNameAttributeParticlesMap.get(attributeName)[position];

                // If use is optional double counter
                if (attribute.getUse() == AttributeUse.Optional) {
                    counter = 2 * counter;
                }
            }
        }
        return counter;
    }

    private boolean containsUnrestrictedAnySimpleType(SimpleType simpleType) {

        // Check if simpleType has no inheritance
        if (simpleType == null) {
            return true;
        } else if (simpleType.getInheritance() == null) {

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
                        simpleContentRestriction.getLength().getValue() != 1 ||
                        simpleContentRestriction.getMaxLength().getValue() < 1 ||
                        simpleContentRestriction.getMinLength().getValue() > 1 ||
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

    private LinkedList<AttributeUse> getAttributeUses(LinkedHashMap<String, AttributeParticle[]> attributeNameAttributeParticlesMap) {

        // Get list to store attribute uses
        LinkedList<AttributeUse> attributeUses = new LinkedList<AttributeUse>();

        // Check use attribute of all attributes
        for (Iterator<String> it = attributeNameAttributeParticlesMap.keySet().iterator(); it.hasNext();) {
            String attributeName = it.next();

            // Check if minuend attriute particle is attribute
            if (attributeNameAttributeParticlesMap.get(attributeName)[0] instanceof Attribute) {
                Attribute minuendAttribute = (Attribute) attributeNameAttributeParticlesMap.get(attributeName)[0];

                // Check if subtrahend attriute particle is attribute
                if (attributeNameAttributeParticlesMap.get(attributeName)[1] instanceof Attribute) {
                    Attribute subtrahendAttribute = (Attribute) attributeNameAttributeParticlesMap.get(attributeName)[1];

                    // Check for different combinations
                    if (minuendAttribute.getUse() == subtrahendAttribute.getUse()) {
                        attributeUses.add(null);
                    } else if ((minuendAttribute.getUse() == AttributeUse.Optional || minuendAttribute.getUse() == null) && subtrahendAttribute.getUse() == AttributeUse.Required) {
                        attributeUses.add(AttributeUse.Prohibited);
                    } else if ((minuendAttribute.getUse() == AttributeUse.Optional || minuendAttribute.getUse() == null) && subtrahendAttribute.getUse() == AttributeUse.Prohibited) {
                        attributeUses.add(AttributeUse.Required);
                    } else if (minuendAttribute.getUse() == AttributeUse.Required && (subtrahendAttribute.getUse() == AttributeUse.Optional || subtrahendAttribute.getUse() == null)) {
                        attributeUses.add(null);
                    } else if (minuendAttribute.getUse() == AttributeUse.Prohibited && (subtrahendAttribute.getUse() == AttributeUse.Optional || subtrahendAttribute.getUse() == null)) {
                        attributeUses.add(null);
                    }
                } else if (attributeNameAttributeParticlesMap.get(attributeName)[1] instanceof AttributeRef) {
                    AttributeRef subtrahendAttributeRef = (AttributeRef) attributeNameAttributeParticlesMap.get(attributeName)[1];

                    // Check for different combinations
                    if (minuendAttribute.getUse() == subtrahendAttributeRef.getUse()) {
                        attributeUses.add(null);
                    } else if ((minuendAttribute.getUse() == AttributeUse.Optional || minuendAttribute.getUse() == null) && subtrahendAttributeRef.getUse() == AttributeUse.Required) {
                        attributeUses.add(AttributeUse.Prohibited);
                    } else if ((minuendAttribute.getUse() == AttributeUse.Optional || minuendAttribute.getUse() == null) && subtrahendAttributeRef.getUse() == AttributeUse.Prohibited) {
                        attributeUses.add(AttributeUse.Required);
                    } else if (minuendAttribute.getUse() == AttributeUse.Required && (subtrahendAttributeRef.getUse() == AttributeUse.Optional || subtrahendAttributeRef.getUse() == null)) {
                        attributeUses.add(null);
                    } else if (minuendAttribute.getUse() == AttributeUse.Prohibited && (subtrahendAttributeRef.getUse() == AttributeUse.Optional || subtrahendAttributeRef.getUse() == null)) {
                        attributeUses.add(null);
                    }
                } else if (attributeNameAttributeParticlesMap.get(attributeName)[1] instanceof AnyAttribute) {

                    // If subtrahend attribute particle is an any attribute use is empty
                    attributeUses.add(null);

                } else if (attributeNameAttributeParticlesMap.get(attributeName)[1] == null) {

                    // An empty subtrahend particle is the same as an prohibited attribute
                    if (minuendAttribute.getUse() == AttributeUse.Prohibited) {
                        attributeUses.add(null);
                    } else if (minuendAttribute.getUse() == AttributeUse.Optional || minuendAttribute.getUse() == null || minuendAttribute.getUse() == AttributeUse.Required) {
                        attributeUses.add(AttributeUse.Required);
                    }
                }
            } else if (attributeNameAttributeParticlesMap.get(attributeName)[0] instanceof AttributeRef) {
                AttributeRef minuendAttributeRef = (AttributeRef) attributeNameAttributeParticlesMap.get(attributeName)[0];

                // Check if subtrahend attriute particle is attribute
                if (attributeNameAttributeParticlesMap.get(attributeName)[1] instanceof Attribute) {
                    Attribute subtrahendAttribute = (Attribute) attributeNameAttributeParticlesMap.get(attributeName)[1];

                    // Check for different combinations
                    if (minuendAttributeRef.getUse() == subtrahendAttribute.getUse()) {
                        attributeUses.add(null);
                    } else if ((minuendAttributeRef.getUse() == AttributeUse.Optional || minuendAttributeRef.getUse() == null) && subtrahendAttribute.getUse() == AttributeUse.Required) {
                        attributeUses.add(AttributeUse.Prohibited);
                    } else if ((minuendAttributeRef.getUse() == AttributeUse.Optional || minuendAttributeRef.getUse() == null) && subtrahendAttribute.getUse() == AttributeUse.Prohibited) {
                        attributeUses.add(AttributeUse.Required);
                    } else if (minuendAttributeRef.getUse() == AttributeUse.Required && (subtrahendAttribute.getUse() == AttributeUse.Optional || subtrahendAttribute.getUse() == null)) {
                        attributeUses.add(null);
                    } else if (minuendAttributeRef.getUse() == AttributeUse.Prohibited && (subtrahendAttribute.getUse() == AttributeUse.Optional || subtrahendAttribute.getUse() == null)) {
                        attributeUses.add(null);
                    }
                } else if (attributeNameAttributeParticlesMap.get(attributeName)[1] instanceof AttributeRef) {
                    AttributeRef subtrahendAttributeRef = (AttributeRef) attributeNameAttributeParticlesMap.get(attributeName)[1];

                    // Check for different combinations
                    if (minuendAttributeRef.getUse() == subtrahendAttributeRef.getUse()) {
                        attributeUses.add(null);
                    } else if ((minuendAttributeRef.getUse() == AttributeUse.Optional || minuendAttributeRef.getUse() == null) && subtrahendAttributeRef.getUse() == AttributeUse.Required) {
                        attributeUses.add(AttributeUse.Prohibited);
                    } else if ((minuendAttributeRef.getUse() == AttributeUse.Optional || minuendAttributeRef.getUse() == null) && subtrahendAttributeRef.getUse() == AttributeUse.Prohibited) {
                        attributeUses.add(AttributeUse.Required);
                    } else if (minuendAttributeRef.getUse() == AttributeUse.Required && (subtrahendAttributeRef.getUse() == AttributeUse.Optional || subtrahendAttributeRef.getUse() == null)) {
                        attributeUses.add(null);
                    } else if (minuendAttributeRef.getUse() == AttributeUse.Prohibited && (subtrahendAttributeRef.getUse() == AttributeUse.Optional || subtrahendAttributeRef.getUse() == null)) {
                        attributeUses.add(null);
                    }
                } else if (attributeNameAttributeParticlesMap.get(attributeName)[1] instanceof AnyAttribute) {

                    // If subtrahend attribute particle is an any attribute use is empty
                    attributeUses.add(null);

                } else if (attributeNameAttributeParticlesMap.get(attributeName)[1] == null) {

                    // An empty subtrahend particle is the same as an prohibited attribute
                    if (minuendAttributeRef.getUse() == AttributeUse.Prohibited) {
                        attributeUses.add(null);
                    } else if (minuendAttributeRef.getUse() == AttributeUse.Optional || minuendAttributeRef.getUse() == null || minuendAttributeRef.getUse() == AttributeUse.Required) {
                        attributeUses.add(AttributeUse.Required);
                    }
                }
            } else if (attributeNameAttributeParticlesMap.get(attributeName)[0] instanceof AnyAttribute) {

                // If the minuend attribute particle is an any pattern its the same as an optional attribute
                if (attributeNameAttributeParticlesMap.get(attributeName)[1] instanceof Attribute) {
                    Attribute subtrahendAttribute = (Attribute) attributeNameAttributeParticlesMap.get(attributeName)[1];

                    if (AttributeUse.Optional == subtrahendAttribute.getUse() || subtrahendAttribute.getUse() == null) {
                        attributeUses.add(null);
                    } else if (subtrahendAttribute.getUse() == AttributeUse.Required) {
                        attributeUses.add(AttributeUse.Prohibited);
                    } else if (subtrahendAttribute.getUse() == AttributeUse.Prohibited) {
                        attributeUses.add(AttributeUse.Required);
                    }
                } else if (attributeNameAttributeParticlesMap.get(attributeName)[1] instanceof AttributeRef) {
                    AttributeRef subtrahendAttributeRef = (AttributeRef) attributeNameAttributeParticlesMap.get(attributeName)[1];

                    if (AttributeUse.Optional == subtrahendAttributeRef.getUse() || subtrahendAttributeRef.getUse() == null) {
                        attributeUses.add(null);
                    } else if (subtrahendAttributeRef.getUse() == AttributeUse.Required) {
                        attributeUses.add(AttributeUse.Prohibited);
                    } else if (subtrahendAttributeRef.getUse() == AttributeUse.Prohibited) {
                        attributeUses.add(AttributeUse.Required);
                    }
                } else if (attributeNameAttributeParticlesMap.get(attributeName)[1] instanceof AnyAttribute) {

                    // The difference of two optional attributes uses is empty
                    attributeUses.add(null);

                } else if (attributeNameAttributeParticlesMap.get(attributeName)[1] == null) {
                    attributeUses.add(AttributeUse.Required);
                }
            } else if (attributeNameAttributeParticlesMap.get(attributeName)[0] == null) {

                // If minuend attribute is not present attribute use is empty
                attributeUses.add(null);
            }
        }

        // Return new list of attribute uses
        return attributeUses;
    }

    private String getTypeName(Type minuendType, Type subtrahendType) {

        // Build-in data types are not renamed
        if (subtrahendType == null && minuendType == null) {
            return "{http://www.w3.org/2001/XMLSchema}anySimpleType";
        }
        if (subtrahendType == null && minuendType.getNamespace().equals("http://www.w3.org/2001/XMLSchema")) {
            return minuendType.getName();
        }

        // Check if minuend name is present
        String minuendName = null;
        if (minuendType == null) {
            minuendName = "anySimpleType";
        } else {
            minuendName = minuendType.getLocalName();
        }

        // Check if subtrahend name is present
        String subtrahendName = null;
        if (subtrahendType == null) {
            subtrahendName = "anySimpleType";
        } else {
            subtrahendName = subtrahendType.getLocalName();
        }

        // Get new name
        String typeName = "{" + outputSchema.getTargetNamespace() + "}difference-type." + minuendName + "-" + subtrahendName;

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

    private String getDifferenceNamespace(AnyAttribute minunedAnyAttribute, AnyAttribute subtrahendAnyAttribute) {

        // Get minuend Namespace
        String minuendNamespace = null;

        // Check if namespace attribute is present or not for the minuend any attribute
        if (minunedAnyAttribute.getNamespace() == null) {
            minuendNamespace = "##any";
        } else {
            minuendNamespace = minunedAnyAttribute.getNamespace();
        }

        // Get subtrahend Namespace
        String subtrahendNamespace = null;

        // Check if subtrahend any attribute is present
        if (subtrahendAnyAttribute != null) {

            // Check if namespace attribute is present or not for the minuend any attribute
            if (subtrahendAnyAttribute.getNamespace() == null) {
                subtrahendNamespace = "##any";
            } else {
                subtrahendNamespace = subtrahendAnyAttribute.getNamespace();
            }
        } else {
            subtrahendNamespace = "";
        }

        // Check if minuend namespace contains any, other or a list of namespaces
        if (minuendNamespace.contains("##any")) {

            // Check if subtrahend namespace contains any, other or a list of namespaces
            if (subtrahendNamespace.contains("##any")) {

                // Return null if both namespaces are any
                return "";

            } else if (subtrahendNamespace.contains("##other")) {

                // New namespace will contain local
                String newNamespace = "##local";

                // Check if namespace of subtrahend any attribute is not local
                if (!anyAttributeOldSchemaMap.get(subtrahendAnyAttribute).getTargetNamespace().equals("")) {

                    // Check if namespace of subtrahend any attribute is current target namespace and add it to list else add namespace to list
                    if (anyAttributeOldSchemaMap.get(subtrahendAnyAttribute).getTargetNamespace().equals(outputSchema.getTargetNamespace())) {
                        newNamespace += " ##targetNamespace";
                    } else {
                        newNamespace += " " + anyAttributeOldSchemaMap.get(subtrahendAnyAttribute).getTargetNamespace();
                    }
                }
                return newNamespace;
            } else {

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
                if (anyAttributeOldSchemaMap.get(subtrahendAnyAttribute).getTargetNamespace().equals(anyAttributeOldSchemaMap.get(minunedAnyAttribute).getTargetNamespace())) {
                    return "";
                } else {

                    // Return namespace of subtrahend other
                    return anyAttributeOldSchemaMap.get(subtrahendAnyAttribute).getTargetNamespace();
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
                if (minuendNamespace.contains(anyAttributeOldSchemaMap.get(subtrahendAnyAttribute).getTargetNamespace()) || (minuendNamespace.contains("##targetNamespace") && anyAttributeOldSchemaMap.get(minuendNamespace).getTargetNamespace().equals(anyAttributeOldSchemaMap.get(subtrahendAnyAttribute).getTargetNamespace()))) {

                    // Check if namespace of subtrahend any attribute is not local
                    if (!anyAttributeOldSchemaMap.get(subtrahendAnyAttribute).getTargetNamespace().equals("")) {

                        // Add delimiter
                        if (!newNamespace.equals("")) {
                            newNamespace += " ";
                        }

                        // Check if namespace of subtrahend any attribute is current target namespace and add it to list else add namespace to list
                        if (anyAttributeOldSchemaMap.get(subtrahendAnyAttribute).getTargetNamespace().equals(outputSchema.getTargetNamespace())) {
                            newNamespace += "##targetNamespace";
                        } else {
                            newNamespace += anyAttributeOldSchemaMap.get(subtrahendAnyAttribute).getTargetNamespace();
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

                // Get set of contained namespaces for minuend attribute particle
                LinkedHashSet<String> minuendNamespaces = new LinkedHashSet<String>();

                // Split minuend namespace
                for (String currentNamespace : minuendNamespace.split(" ")) {

                    // Instead of target namespaces add namespace value
                    if (currentNamespace.equals("##targetNamespace")) {
                        minuendNamespaces.add(anyAttributeOldSchemaMap.get(minunedAnyAttribute).getTargetNamespace());
                    } else {
                        minuendNamespaces.add(currentNamespace);
                    }
                }

                // Get set of contained namespaces for subtrahend attribute particle
                LinkedHashSet<String> subtrahendNamespaces = new LinkedHashSet<String>();

                // Split minuend namespace
                for (String currentNamespace : subtrahendNamespace.split(" ")) {

                    // Instead of target namespaces add namespace value
                    if (currentNamespace.equals("##targetNamespace")) {
                        subtrahendNamespaces.add(anyAttributeOldSchemaMap.get(subtrahendAnyAttribute).getTargetNamespace());
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
     * Checks if a list contains an entry, which is not null.
     *
     * @param linkedList List which is checked.
     * @return <tt>true</tt> if no entry is contained in the list, which is not
     * null.
     */
    @SuppressWarnings("rawtypes")
	private boolean isEmpty(LinkedList linkedList) {

        // Check if an object is not null
        for (Object object: linkedList) {
            // Check if entry is null
            if (object != null) {
                return false;
            }
        }
        return true;
    }

    /**
     * This method generates a new simpleType from a specified attribute
     * particle. The new simpleType is registered in the output schema.
     *
     * @param attributeParticle Attribute particle, which is used to generate
     * the new simpleType
     * @return New simpleType, which can be used by the new attribute.
     */
    private Type getSimpleType(AttributeParticle attributeParticle) {

        // Use set to store all simpleTypes and a boolean variable to check if an anyType is contained
        LinkedList<SimpleType> simpleTypes = new LinkedList<SimpleType>();
        boolean containsAnyType = false;

        // If attribute particle is an attribute or an attribute reference
        if (attributeParticle instanceof Attribute) {
            Attribute attribute = ((Attribute) attributeParticle);

            // Check if contained simpleType is an anyType, an anySimpleType or not present, if one of these is true the boolean variable is set to true, else the simpleType is added to the set
            if (attribute.getSimpleType() != null && !attribute.getSimpleType().getName().equals("{http://www.w3.org/2001/XMLSchema}anyType") && !attribute.getSimpleType().getName().equals("{http://www.w3.org/2001/XMLSchema}anySimpleType")) {
                simpleTypes.add(attribute.getSimpleType());
            } else {
                containsAnyType = true;
            }
        } else if (attributeParticle instanceof AttributeRef) {
            AttributeRef attributeRef = ((AttributeRef) attributeParticle);

            // Check if contained simpleType is an anyType, an anySimpleType or not present, if one of these is true the boolean variable is set to true, else the simpleType is added to the set
            if (attributeRef.getAttribute().getSimpleType() != null && !attributeRef.getAttribute().getSimpleType().getName().equals("{http://www.w3.org/2001/XMLSchema}anyType") && !attributeRef.getAttribute().getSimpleType().getName().equals("{http://www.w3.org/2001/XMLSchema}anySimpleType")) {
                simpleTypes.add(attributeRef.getAttribute().getSimpleType());
            } else {
                containsAnyType = true;
            }
        }

        // Check if anyType was contained
        if (!containsAnyType) {

            // Get name of the new simpleType and return new simpleType
            String name = getTypeName(simpleTypes.getFirst(), null);
            return typeDifferenceGenerator.generateNewSimpleType(simpleTypes.getFirst(), name);
        } else {

            // Create new anySimpleType, update type SymbolTable and return new simpleType
            String name = "{http://www.w3.org/2001/XMLSchema}anySimpleType";

            // Check if anySimpleType already exist in the output schema
            if (!outputSchema.getTypeSymbolTable().hasReference(name)) {
                outputSchema.getTypeSymbolTable().updateOrCreateReference(name, new SimpleType(name, null));
            }
            return outputSchema.getTypeSymbolTable().getReference(name).getReference();
        }
    }

    /**
     * This mehtod generates the "use" attribute of a new attribute.
     *
     * @param attributeParticle Attribute particle which is used to generate the
     * new "use" attribute.
     * @return AttributeUse for the attribute generated for the new attribute
     * particle.
     */
    private AttributeUse getUse(AttributeParticle attributeParticle) {

        // Use set of AttributeUses and boolean variable to create the new "use" attribute
        LinkedHashSet<AttributeUse> useValues = new LinkedHashSet<AttributeUse>();
        boolean attributeUseOptional = false;

        // Check if attribute particle is attribute or attribute reference
        if (attributeParticle instanceof Attribute) {
            Attribute attribute = ((Attribute) attributeParticle);

            // Check if attribute is optional and update set and variable accordingly
            if (attribute.getUse() != null && !attribute.getUse().equals(AttributeUse.Optional)) {
                useValues.add(attribute.getUse());
            } else {
                attributeUseOptional = true;
            }
        } else if (attributeParticle instanceof AttributeRef) {
            AttributeRef attributeRef = ((AttributeRef) attributeParticle);

            // If attribute particle is attribute reference first check attribute reference and then referred attribute
            if (attributeRef.getUse() != null && !attributeRef.getUse().equals(AttributeUse.Optional)) {
                useValues.add(attributeRef.getUse());
            } else if (attributeRef.getAttribute().getUse() != null && !attributeRef.getAttribute().getUse().equals(AttributeUse.Optional)) {
                useValues.add(attributeRef.getAttribute().getUse());
            } else {
                attributeUseOptional = true;
            }
        }

        // If "optional" value was contained or if the size of the current attribute list is not the size of the attributeParticleList list return optional
        if (attributeUseOptional) {
            return AttributeUse.Optional;
        } else {

            // If both "required" and "prohibited" values are contained in the set return optional
            if (useValues.contains(AttributeUse.Required) && useValues.contains(AttributeUse.Prohibited)) {
                return AttributeUse.Optional;
            } else if (useValues.contains(AttributeUse.Required) && useValues.size() == 1) {

                // If only "required" value is contained in the set return required
                return AttributeUse.Required;
            } else if (useValues.contains(AttributeUse.Prohibited) && useValues.size() == 1) {

                // If only "prohibited" value is contained in the set return prohibited
                return AttributeUse.Prohibited;
            }
        }

        // Per default return optional
        return AttributeUse.Optional;
    }

    /**
     * This method creates a new annotation for a given attribute particle. Each
     * attribute particle may contain an annotation, these annotations are used
     * to contstruct the new annotation.
     *
     * @param attributeParticle Attribute particle, which is used to construct a
     * new attribute particle. This attribute particle will contain the new
     * annotation.
     * @return New Annotation, which contains the information stored in the
     * old annotation contained in the specified attribute particle.
     */
    private Annotation getAnnotation(AttributeParticle attributeParticle) {

        // Create new annotation
        Annotation newAnnotation = null;

        // Check if attribute particle contains an annotation
        if (attributeParticle.getAnnotation() != null) {
            Annotation oldAnnotation = attributeParticle.getAnnotation();

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
        return newAnnotation;
    }

    /**
     * Builds a new ID from the ID of the given attribute particle.
     *
     * @param attributeParticle Attribute particle, which may contain an ID.
     * @return New ID generated from the ID of specified attribute particle.
     */
    private String getID(AttributeParticle attributeParticle) {

        // Initialize new ID
        String newID = "";

        // If an ID is contained append ID to new ID
        if (attributeParticle.getId() != null) {
            newID += attributeParticle.getId();
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
     * This method generates a new "form" attribute from the "form" attribute
     * of given attribute particle.
     *
     * @param attributeParticle Attribute particle which is used to generate new
     * "form" attribute.
     * @return New "form" attribute build from the old "form" attribute.
     */
    private XSDSchema.Qualification getForm(AttributeParticle attributeParticle) {

        // Check if the attribute particle is an attribute
        if (attributeParticle instanceof Attribute) {
            Attribute attribute = ((Attribute) attributeParticle);

            // If current "form" attribute has a value, which is qualified, the new "form" attribute has the same value
            if (attribute.getForm() != null && attribute.getForm() == XSDSchema.Qualification.qualified) {
                return XSDSchema.Qualification.qualified;
            }
        } else if (attributeParticle instanceof AttributeRef) {
            return XSDSchema.Qualification.qualified;
        }

        // The default value for the "form" attribute is unqualified
        return null;
    }

    /**
     * This method returns the name which is contained in the specified
     * attribute particle. This is a simple method which is used with caution.
     *
     * @param attributeParticle Attribute particle, which has a direct or
     * indirect name.
     * @return Name of a given attribute particle.
     */
    private String getName(AttributeParticle attributeParticle) {

        // If attribute is qualified the namespace is important
        if ((getForm(attributeParticle) == null || getForm(attributeParticle) != XSDSchema.Qualification.qualified)) {

            // If the attribute particle is an attribute return the new name for an attribute
            if (attributeParticle instanceof Attribute) {
                return "{" + outputSchema.getTargetNamespace() + "}" + ((Attribute) attributeParticle).getLocalName();

            } else if (attributeParticle instanceof AttributeRef) {

                // If the attribute particle is an attribute reference return the new name for an attribute reference
                return "{" + outputSchema.getTargetNamespace() + "}" + ((AttributeRef) attributeParticle).getAttribute().getLocalName();
            } else {

                // This should not happen
                return null;
            }
        } else {

            // If attribute particle is an attribute return the new name for an attribute
            if (attributeParticle instanceof Attribute) {
                return ((Attribute) attributeParticle).getName();

            } else if (attributeParticle instanceof AttributeRef) {

                // If attribute particle is an attribute reference return the new name for an attribute reference
                return ((AttributeRef) attributeParticle).getAttribute().getName();
            } else {

                // This should not happen
                return null;
            }
        }
    }

    /**
     * This method computes a new "default" attribute for the given attribute
     * particle.
     *
     * @param attributeParticles Attribute particle which is used to generate
     * new "default" attribute
     * @param attributeUseOptional Boolean which is true when the "use"
     * attribute of the attribute for, which this "default" attribute is
     * generated, is optional.
     * @return New "default" attribute build from the given  "default"
     * attribute.
     */
    private String getDefault(AttributeParticle attributeParticle, boolean attributeUseOptional) {
        LinkedHashSet<String> defaultValues = new LinkedHashSet<String>();

        // Check if the attribute particle is an attribute or an attribute reference
        if (attributeParticle instanceof Attribute) {
            Attribute attribute = ((Attribute) attributeParticle);

            // If attribute contains a "default" attribute add the value of thies attribute to the value set
            if (attribute.getDefault() != null) {
                defaultValues.add(attribute.getDefault());
            }
        } else if (attributeParticle instanceof AttributeRef) {
            AttributeRef attributeRef = ((AttributeRef) attributeParticle);

            // If attribute reference contains a "default" attribute add the value of this attribute to the value set
            if (attributeRef.getDefault() != null) {
                defaultValues.add(attributeRef.getDefault());
            }

            // If referred attribute contains a "default" attribute add the value of this attribute to the value set
            if (attributeRef.getAttribute().getDefault() != null) {
                defaultValues.add(attributeRef.getAttribute().getDefault());
            }
        }

        // If only one "default" value exist in the set and the attribute is not optional return this value
        if (attributeUseOptional && defaultValues.size() == 1) {
            return defaultValues.iterator().next();
        } else {

            // If more than one "default" value exist in the set no unambiguous value exists
            return null;
        }
    }

    /**
     * This method generates a new "fixed" attribute for a given attribute
     * particle.
     *
     * @param attributeParticle Each attribute particle may contain a "fixed"
     * attribute.
     * @return If no "fixed" attribute was constructed a null pointer. If a
     * "fixed" attribute was constructed a String containing the value of the
     * "fixed" attribute.
     */
    private String getFixed(AttributeParticle attributeParticle) {

        // Check for attributes and attribute references contained "fixed" attributes
        if (attributeParticle instanceof Attribute) {
            Attribute attribute = ((Attribute) attributeParticle);

            // Check if attribute contains "fixed" attribute
            if (attribute.getFixed() != null) {
                return attribute.getFixed();
            }
        } else if (attributeParticle instanceof AttributeRef) {
            AttributeRef attributeRef = ((AttributeRef) attributeParticle);

            // Check if attribute reference contains "fixed" attribute or referenced attribute contains "fixed" attribute
            if (attributeRef.getFixed() != null) {
                return attributeRef.getFixed();
            } else if (attributeRef.getAttribute().getFixed() != null) {
                return attributeRef.getAttribute().getFixed();
            }
        }
        return null;
    }

    /**
     * Create new attribute as a copy of the specified old attribute. All
     * informations contained in the old attribute are transfered to the new
     * attribute. The new element contains a new type, a new "form" value and so
     * on.
     *
     * @param oldAttribute Attribute used to create the new attribute as an
     * exact copy of the old attribute in the new schema.
     * @return New attribute in the new schema created from the specified old
     * attribute or an attribute group containing the new attribute in another
     * schema.
     */
    private AttributeParticle generateNewAttribute(Attribute oldAttribute) {

        // Check if the old simpleType is present
        if (oldAttribute.getSimpleType() != null) {

            // Get new type name by changing the namespace
            String newTypeName = null;

            // Check if old simpleType is build-in datatype and name new type respectivly
            if (typeDifferenceGenerator.isBuiltInDatatype(oldAttribute.getSimpleType().getName())) {
                newTypeName = oldAttribute.getSimpleType().getName();
            } else {
                newTypeName = "{" + outputSchema.getTargetNamespace() + "}" + oldAttribute.getSimpleType().getLocalName();
            }

            // Create new simpleType via type difference generator
            typeDifferenceGenerator.generateNewSimpleType(oldAttribute.getSimpleType(), newTypeName);
        }

        // Initialize new element name
        Attribute newAttribute = null;

        // If element is qualified the namespace is important
        if ((oldAttribute.getForm() == null || oldAttribute.getForm() != XSDSchema.Qualification.qualified) && !oldAttribute.getNamespace().equals(outputSchema.getTargetNamespace())) {

            // Create new elment with same local name but different namespace as the old element but new type
            newAttribute = new Attribute("{" + outputSchema.getTargetNamespace() + "}" + oldAttribute.getLocalName());
        } else {

            // Create new elment with same name as the old element but new type
            newAttribute = new Attribute(oldAttribute.getName());
        }

        // Create new Attribute with new type reference
        if (oldAttribute.getSimpleType() != null) {

            if (typeDifferenceGenerator.isBuiltInDatatype(oldAttribute.getSimpleType().getName())) {
                newAttribute.setSimpleType(outputSchema.getTypeSymbolTable().getReference(oldAttribute.getSimpleType().getName()));
            } else {
                newAttribute.setSimpleType(outputSchema.getTypeSymbolTable().getReference("{" + outputSchema.getTargetNamespace() + "}" + oldAttribute.getSimpleType().getLocalName()));
            }
        }

        // Set "use" attribute for new attribute and check wether type is anonymous or not
        newAttribute.setTypeAttr(oldAttribute.getTypeAttr());
        newAttribute.setUse(oldAttribute.getUse());

        // Set id and annotation for the new attribute
        newAttribute.setAnnotation(generateNewAnnotation(oldAttribute.getAnnotation()));
        newAttribute.setId(getID(oldAttribute.getId()));

        // Set "fixed" and "default" values of the new attribute
        newAttribute.setDefault(oldAttribute.getDefault());
        newAttribute.setFixed(oldAttribute.getFixed());

        // Check if "form" attribute is present in old attribute
        if (oldAttribute.getForm() == null) {

            // Set "form" value to the value of the "attributeFormDefault" attribute
            if (attributeOldSchemaMap.get(oldAttribute).getAttributeFormDefault() == Qualification.qualified) {
                newAttribute.setForm(Qualification.qualified);
            } else {
                newAttribute.setForm(null);
            }
        } else {

            // "form" attribute is present add "form" value to new attribute
            if (oldAttribute.getForm() == Qualification.qualified) {
                newAttribute.setForm(Qualification.qualified);
            } else {
                newAttribute.setForm(null);
            }
        }

        // Check again if the element was qualified and if add new group to an other schema
        if (oldAttribute.getForm() != null && oldAttribute.getForm() == XSDSchema.Qualification.qualified && !oldAttribute.getNamespace().equals(outputSchema.getTargetNamespace())) {

            // Get other schema to store element group in
            XSDSchema otherSchema = new XSDSchema(oldAttribute.getNamespace());
            otherSchema.getNamespaceList().addIdentifiedNamespace(new IdentifiedNamespace("xs", "http://www.w3.org/2001/XMLSchema"));
            otherSchema.setSchemaLocation(workingDirectory + "OtherSchema(" + oldAttribute.getNamespace() + ").xsd");

            // Check if schema exist in schema groups
            if (namespaceOutputSchemaMap.get(oldAttribute.getNamespace()) != null) {
                otherSchema = namespaceOutputSchemaMap.get(oldAttribute.getNamespace());
            } else {

                // Check if other schema is present in other schema list
                for (Iterator<XSDSchema> it = otherSchemata.iterator(); it.hasNext();) {
                    XSDSchema currentOtherSchema = it.next();

                    // If other schema has same namespace as the element use this schema
                    if (otherSchema.getTargetNamespace().equals(oldAttribute.getNamespace())) {
                        otherSchema = currentOtherSchema;
                    }
                }

                // Add other schema to schema set
                otherSchemata.add(otherSchema);
            }

            // Build new attribute group name and avoid conflicts
            String newAttributeGroupNameBase = "{" + otherSchema.getTargetNamespace() + "}" + "attribute.group." + oldAttribute.getLocalName();
            String newAttributeGroupName = newAttributeGroupNameBase;
            int number = 1;

            // Check if group name is already taken in the other schema and add new number to the group name base until the name is available
            while (otherSchema.getGroupSymbolTable().hasReference(newAttributeGroupName)) {
                newAttributeGroupName = newAttributeGroupNameBase + "." + number;
                number++;
            }

            // Build new group and add it to the other schema
            AttributeGroup newAttributeGroup = new AttributeGroup(newAttributeGroupName);
            newAttributeGroup.addAttributeParticle(newAttribute);
            otherSchema.getAttributeGroupSymbolTable().updateOrCreateReference(newAttributeGroupName, newAttributeGroup);
            otherSchema.addAttributeGroup(otherSchema.getAttributeGroupSymbolTable().getReference(newAttributeGroupName));

            // Generate new import components in both schemata if necessary
            generateNewImport(outputSchema, otherSchema);
            generateNewImport(otherSchema, outputSchema);

            // Register group in output schema and return group reference
            outputSchema.getAttributeGroupSymbolTable().updateOrCreateReference(newAttributeGroupName, newAttributeGroup);
            return new AttributeGroupRef(outputSchema.getAttributeGroupSymbolTable().getReference(newAttributeGroupName));
        }

        // Return new attribute after copying all fields of the old attribute
        return newAttribute;
    }

    /**
     * This method generates a new attribute with top-level declaration for the
     * given old attribute.
     *
     * @param oldAttribute Attribute which is copied into the outputSchema.
     * @return Copy of the specified attribute, registered in the top-level
     * attribute list of the output schema.
     */
    public Attribute generateNewTopLevelAttribute(Attribute oldAttribute) {

        // Check if the old simpleType is present
        if (oldAttribute.getSimpleType() != null) {

            // Get new type name by changing the namespace
            String newTypeName = null;

            // Check if old simpleType is build-in datatype and name new type respectivly
            if (typeDifferenceGenerator.isBuiltInDatatype(oldAttribute.getSimpleType().getName())) {
                newTypeName = oldAttribute.getSimpleType().getName();
            } else {
                newTypeName = "{" + outputSchema.getTargetNamespace() + "}" + oldAttribute.getSimpleType().getLocalName();
            }

            // Create new simpleType via type difference generator
            typeDifferenceGenerator.generateNewSimpleType(oldAttribute.getSimpleType(), newTypeName);
        }

        // Initialize new element name
        Attribute newAttribute = new Attribute(oldAttribute.getName());

        // Create new Attribute with new type reference
        if (oldAttribute.getSimpleType() != null) {

            if (typeDifferenceGenerator.isBuiltInDatatype(oldAttribute.getSimpleType().getName())) {
                newAttribute.setSimpleType(outputSchema.getTypeSymbolTable().getReference(oldAttribute.getSimpleType().getName()));
            } else {
                newAttribute.setSimpleType(outputSchema.getTypeSymbolTable().getReference("{" + outputSchema.getTargetNamespace() + "}" + oldAttribute.getSimpleType().getLocalName()));
            }
        }

        // Set "use" attribute for new attribute and check wether type is anonymous or not
        newAttribute.setTypeAttr(oldAttribute.getTypeAttr());

        // Set id and annotation for the new attribute
        newAttribute.setAnnotation(oldAttribute.getAnnotation());
        newAttribute.setId(getID(oldAttribute.getId()));

        // Set "fixed" and "default" values of the new attribute
        newAttribute.setDefault(oldAttribute.getDefault());
        newAttribute.setFixed(oldAttribute.getFixed());

        // Set "use" and "form" attributes to default values (both are not available for top-level attributes)
        newAttribute.setUse(AttributeUse.Optional);
        newAttribute.setForm(null);

        // Add attribute to attribute SymbolTable
        outputSchema.getAttributeSymbolTable().updateOrCreateReference(newAttribute.getName(), newAttribute);

        // If no top-level attribute with same name exist add attribute to top-level attribute list
        if (!outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(newAttribute.getName()))) {
            outputSchema.addAttribute(outputSchema.getAttributeSymbolTable().getReference(newAttribute.getName()));
        }
        return newAttribute;
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

        // Check if imported schema is null
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
     * Create new attribute group reference for a given old attribute group
     * reference. The new attribute group reference is a complete copy of the
     * old attribute group reference. If the reference links to a attribute
     * group in another schema this element will be referred to by* the new
     * attribute group reference as well. If it links to an attribute group of
     * the old schema the new reference will link to the corresponding attribute
     * group in the current new schema.
     *
     * @param oldAttributeGroupRef Old attribute group reference which is used
     * to construct the new attribute group reference.
     * @return New attribute group reference linking to the correct attribute
     * group (either a new attribute group in the new schema or an attribute
     * group in a foreign schema).
     */
    private AttributeGroupRef generateNewAttributeGroupRef(AttributeGroupRef oldAttributeGroupRef) {

        // Generate new attribute group
        generateNewTopLevelAttributeGroup(oldAttributeGroupRef.getAttributeGroup());

        // Create new attribute group reference with correct reference stored in the new schema
        AttributeGroupRef newAttributeGroupRef = new AttributeGroupRef(outputSchema.getAttributeGroupSymbolTable().getReference("{" + outputSchema.getTargetNamespace() + "}" + oldAttributeGroupRef.getAttributeGroup().getLocalName()));

        // Set new id and annotation for new attribute group reference
        newAttributeGroupRef.setAnnotation(generateNewAnnotation(oldAttributeGroupRef.getAnnotation()));
        newAttributeGroupRef.setId(getID(oldAttributeGroupRef.getId()));

        // Return new attribute group reference
        return newAttributeGroupRef;
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

    /**
     * Creates new any attribute for a given any attribute. New and old any
     * attribute are identical but different objects. AnyAttributes do not
     * contain references to schema components.
     *
     * @param oldAnyAttribute Blueprint for the new any attribute.
     * @return New any attribute an exact copy of the specified old any
     * attribute.
     */
    private AttributeParticle generateNewAnyAttribute(AnyAttribute oldAnyAttribute) {

        // Create new any attribute
        AnyAttribute newAnyAttribute = new AnyAttribute(oldAnyAttribute.getProcessContentsInstruction(), oldAnyAttribute.getNamespace());

        // Set new id and annotation for new any attribute
        newAnyAttribute.setAnnotation(generateNewAnnotation(oldAnyAttribute.getAnnotation()));
        newAnyAttribute.setId(getID(oldAnyAttribute.getId()));

        // Return new any pattern
        return newAnyAttribute;
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
     * Create new attribute reference for a given old attribute reference. The
     * new attribute reference is a complete copy of the old attribute
     * reference. If the reference links to a attribute in another schema this
     * attribute will be referred to by the new attribute reference as well. If
     * it links to an attribute of the old schema the new refernce will link to
     * the corresponding attribute in the current new schema.  If the referred
     * attribute was changed due to difference a new attribute is returned
     * instead.
     *
     * @param oldAttributeRef Old element reference which is used to construct
     * the new attribute reference.
     * @return New attribute reference linking to the correct attribute (either
     * a new attribute in the new schema or an attribute in another output
     * schemaforeign). Else it is possible that instead of an attribute
     * reference an new attribute is returned.
     */
    private AttributeParticle generateNewAttributeRef(AttributeRef oldAttributeRef) {

        // Create new attribute
        AttributeParticle newAttributeParticle = generateNewAttribute(oldAttributeRef.getAttribute());

        if (newAttributeParticle instanceof AttributeGroupRef) {
            return newAttributeParticle;
        } else {
            Attribute newAttribute = (Attribute) newAttributeParticle;

            // Set new id and annotation for new attribute reference
            newAttribute.setAnnotation(generateNewAnnotation(oldAttributeRef.getAnnotation()));
            newAttribute.setId(getID(oldAttributeRef.getId()));

            // If "fixed" or "default" attribtues are present in attribute reference replace attributes in new attribute
            if (oldAttributeRef.getDefault() == null) {
                newAttribute.setDefault(oldAttributeRef.getDefault());
            }
            if (oldAttributeRef.getFixed() == null) {
                newAttribute.setDefault(oldAttributeRef.getFixed());
            }

            // Use is not present in top-level attribute
            newAttribute.setUse(oldAttributeRef.getUse());

            // Return new attribute
            return newAttribute;
        }
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
     * This method creates a new top-level attribute group. This attribute group
     * will be a copie of the specified old attribute group contained in the
     * given old schema. Furthermore the new attribute group will be registered
     * in the SymbolTable of the new schema and in the list of top-level
     * attribute groups.
     *
     * @param topLevelAttributeGroup Attribute group which is the blueprint for
     * the new attribute group.
     */
    private void generateNewTopLevelAttributeGroup(AttributeGroup topLevelAttributeGroup) {

        // Create new group with the name of the old group and a new container
        AttributeGroup newAttributeGroup = generateNewAttributeGroup(topLevelAttributeGroup);

        // Add new attribute group to the list of top-level attribute groups
        outputSchema.addAttributeGroup(outputSchema.getAttributeGroupSymbolTable().getReference(newAttributeGroup.getName()));
    }

    /**
     * This method creates a new attribute group. This attribute group will be a
     * copie of the specified old attribute group contained in the given old
     * schema. Furthermore the new attribute group will be registered in the
     * SymbolTable of the new schema, but not in the list of top-level attribute
     * groups (so the method can be used by a redefine-component).
     *
     * @param oldAttributeGroup Attribute group which is the blueprint for the
     * new attribute group.
     * @return The new attribute group contained in the new schema.
     */
    private AttributeGroup generateNewAttributeGroup(AttributeGroup oldAttributeGroup) {

        // Check if group exists already
        if (!outputSchema.getAttributeGroupSymbolTable().hasReference(oldAttributeGroup.getName())) {

            // Create new attribute group with the name of the old attribute group
            AttributeGroup newAttributeGroup = new AttributeGroup("{" + outputSchema.getTargetNamespace() + "}" + oldAttributeGroup.getLocalName());

            // Create new annotation and id and add them to the new attribute group
            newAttributeGroup.setAnnotation(generateNewAnnotation(oldAttributeGroup.getAnnotation()));
            newAttributeGroup.setId(getID(oldAttributeGroup.getId()));

            // Get list of all attribute particles contained in the old attribute group
            LinkedList<AttributeParticle> oldAttributeParticles = oldAttributeGroup.getAttributeParticles();

            // Create for each attribute particle a new attribute particle and add it to the new attribute group
            for (Iterator<AttributeParticle> it = oldAttributeParticles.iterator(); it.hasNext();) {
                AttributeParticle oldAttributeParticle = it.next();

                // Add new create attribute particle to the new attribute group
                newAttributeGroup.addAttributeParticle(generateNewAttributeParticle(oldAttributeParticle));
            }

            // Create new SymbolTableRef
            outputSchema.getAttributeGroupSymbolTable().updateOrCreateReference(newAttributeGroup.getName(), newAttributeGroup);

            // Return the new attribute group
            return newAttributeGroup;
        } else {
            return outputSchema.getAttributeGroupSymbolTable().getReference(oldAttributeGroup.getName()).getReference();
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
     * Create new attribute particle (AnyAttribute, Attribute,
     * AttributeGroupRef, AttributeRef) by copying the old attribute particle.
     * All information stored in the old attribute particle is present in the
     * new attribute particle.
     *
     * @param oldAttributeParticle Attribute particle which is used to crated
     * the new attribute particle, so that the new attribute particle is a copy
     * of the old attribute particle.
     * @return New attribute particle, copy the old attribute particle.
     */
    public AttributeParticle generateNewAttributeParticle(AttributeParticle oldAttributeParticle) {

        // Create new attribute particle
        AttributeParticle newAttributeParticle = null;

        // If old attribute particle is an AnyAttribute create new AnyAttribute.
        if (oldAttributeParticle instanceof AnyAttribute) {

            // Create new AnyAttribute with the createNewAnyAttribute method
            AnyAttribute oldAnyAttribute = (AnyAttribute) oldAttributeParticle;
            newAttributeParticle = generateNewAnyAttribute(oldAnyAttribute);

        // If old attribute particle is an Attribute create new Attribute.
        } else if (oldAttributeParticle instanceof Attribute) {

            // Create new Attribute with the createNewAttribute method
            Attribute oldAttribute = (Attribute) oldAttributeParticle;
            newAttributeParticle = generateNewAttribute(oldAttribute);

        // If old attribute particle is a AttributeGroupRef create new AttributeGroupRef.
        } else if (oldAttributeParticle instanceof AttributeGroupRef) {

            // Create new AttributeGroupRef with the createNewAttributeGroupRef method
            AttributeGroupRef oldAttributeGroupRef = (AttributeGroupRef) oldAttributeParticle;
            newAttributeParticle = generateNewAttributeGroupRef(oldAttributeGroupRef);

        // If old attribute particle is a AttributeRef create new AttributeRef.
        } else if (oldAttributeParticle instanceof AttributeRef) {

            // Create new AttributeRef with the createNewAttributeRef method
            AttributeRef oldAttributeRef = (AttributeRef) oldAttributeParticle;
            newAttributeParticle = generateNewAttributeRef(oldAttributeRef);
        }

        // Return new attribute particle after using other create-methods to create a copie of the old attribute particle
        return newAttributeParticle;
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
     * Checks if an attribute is included in the set of attributes represented
     * by an any attribute. If the attribute is included in the any attribute
     * <tt>true</tt> is returned otherwise <tt>false</tt>. For this check the
     * target namespace of the any attribute is needed because of its relevance
     * for the "namespace" attribute of the any attribute.
     *
     * @param attributeName Attribute name for which is checked if it is
     * contained in the specified any attribute.
     * @param anyAttribute Any attribute, which may contain the specified
     * attribute.
     * @return <tt>true</tt> if the attribute is included in the any attribute.
     */
    private boolean isIncluded(String attributeName, AnyAttribute anyAttribute) {

        // Get namespace of the element
        String namespaceAttribute = attributeName.substring(1, attributeName.lastIndexOf("}"));

        // Check if the namespace of the attribute is contained in the namespace space defined by the "namespace" attribute of the any attribute
        if (anyAttribute.getNamespace() == null || anyAttribute.getNamespace().contains("##any")) {
            return true;
        } else if (anyAttribute.getNamespace().contains("##other") && !anyAttributeOldSchemaMap.get(anyAttribute).getTargetNamespace().equals(namespaceAttribute)) {
            return true;
        } else if (anyAttribute.getNamespace().contains("##local") && namespaceAttribute.equals("")) {
            return true;
        } else if (anyAttribute.getNamespace().contains("##targetNamespace") && anyAttributeOldSchemaMap.get(anyAttribute).getTargetNamespace().equals(namespaceAttribute)) {
            return true;
        } else if (anyAttribute.getNamespace().contains(namespaceAttribute) && !namespaceAttribute.equals("")) {
            return true;
        }

        // If the namespace of the attribute is not included return false
        return false;
    }

    /**
     * Checks if the specified attribute array contains only one attribute.
     *
     * @param attributeParticleArray Attribute array, which may contain an
     * single attribute.
     * @return <tt>true</tt> if the attribute array contains only one attribute.
     */
    private boolean containsOnlyOne(AttributeParticle[] attributeParticleArray) {

        // Use variable to check if only one attribute particle is contained
        boolean containsOneAttribute = false;

        for (int i = 0; i < attributeParticleArray.length; i++) {
            AttributeParticle attributeParticle = attributeParticleArray[i];

            // If already another attribute particle was contained return false
            if (attributeParticle != null && containsOneAttribute) {
                return false;
            }

            // If this is the first contained attribute particle set variable to true
            if (attributeParticle != null && !containsOneAttribute) {
                containsOneAttribute = true;
            }
        }

        // Check if attribute particle was present
        if (containsOneAttribute) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Update the specified attributeNameAttributeParticlesMap with a new
     * entry for the given attribute particle or add the given attribute
     * particle to an existing entry. If the attribute particle is unqualified
     * the entry has no qualified attribute name.
     *
     * @param attributeNameAttributeParticlesMap Map, which contains for each
     * attribute name all corresponding attribute particles.
     * @param attributeParticle New particle, which is added to the map.
     * @param attributeParticleListsSize Size of the attribute list list.
     * @param attributeParticlePosition Current position in the attribute list
     * list.
     */
    private void updateAttributeNameAttributeParticlesMap(LinkedHashMap<String, AttributeParticle[]> attributeNameAttributeParticlesMap, AttributeParticle attributeParticle, int attributeParticleListsSize, int attributeParticlePosition) {

        // If attribute particle is attribute add attribute to map else if attribute particle is attribute reference add referred attribute to map
        if (attributeParticle instanceof Attribute) {
            Attribute attribute = (Attribute) attributeParticle;

            // If attribute is unqualified add attribute under local attribute name to map
            if (attribute.getForm() == null || attribute.getForm() == Qualification.unqualified) {

                // Update attributeNameAttributeParticlesMap with new attribute for the attribute local name
                if (!attributeNameAttributeParticlesMap.containsKey("{}" + attribute.getLocalName())) {
                    attributeNameAttributeParticlesMap.put("{}" + attribute.getLocalName(), new AttributeParticle[attributeParticleListsSize]);
                }
                AttributeParticle[] mappedAttributeParticles = attributeNameAttributeParticlesMap.get("{}" + attribute.getLocalName());

                if (mappedAttributeParticles[attributeParticlePosition] == null) {
                    mappedAttributeParticles[attributeParticlePosition] = attribute;
                }
                attributeNameAttributeParticlesMap.put("{}" + attribute.getLocalName(), mappedAttributeParticles);

            } else if (attribute.getForm() == Qualification.qualified) {

                // Update attributeNameAttributeParticlesMap with new attribute for the attribute qualified name
                if (!attributeNameAttributeParticlesMap.containsKey(attribute.getName())) {
                    attributeNameAttributeParticlesMap.put(attribute.getName(), new AttributeParticle[attributeParticleListsSize]);
                }
                AttributeParticle[] mappedAttributeParticles = attributeNameAttributeParticlesMap.get(attribute.getName());

                if (mappedAttributeParticles[attributeParticlePosition] == null) {
                    mappedAttributeParticles[attributeParticlePosition] = attribute;
                }
                attributeNameAttributeParticlesMap.put(attribute.getName(), mappedAttributeParticles);
            }
        } else if (attributeParticle instanceof AttributeRef) {
            AttributeRef attributeRef = (AttributeRef) attributeParticle;

            // Get referred attribute
            Attribute referredAttribute = attributeRef.getAttribute();

            // Update attributeNameAttributeParticlesMap with new attribute for the attribute qualified name, because top-level attributes can only be qualified
            if (!attributeNameAttributeParticlesMap.containsKey(referredAttribute.getName())) {
                attributeNameAttributeParticlesMap.put(referredAttribute.getName(), new AttributeParticle[attributeParticleListsSize]);
            }
            AttributeParticle[] mappedAttributeParticles = attributeNameAttributeParticlesMap.get(referredAttribute.getName());

            if (mappedAttributeParticles[attributeParticlePosition] == null) {
                mappedAttributeParticles[attributeParticlePosition] = attributeRef;
            }
            attributeNameAttributeParticlesMap.put(referredAttribute.getName(), mappedAttributeParticles);
        }
    }

    /**
     * Get elements contained in the specified any pattern, if the any pattern
     * is processed strictly.
     *
     * @param anyAttribute Any attribute which may contain attributes.
     * @return A set containing all elements contained in the specified any
     * pattern.
     */
    private LinkedHashSet<Attribute> getContainedAttributes(AnyAttribute anyAttribute) {

        // Check if the any pattern is processed strictly
        if (anyAttribute.getProcessContentsInstruction().equals(ProcessContentsInstruction.Strict) || anyAttribute.getProcessContentsInstruction().equals(ProcessContentsInstruction.Lax)) {

            // Initalize set to store all top-level elements
            LinkedHashSet<Attribute> topLevelAttributes = new LinkedHashSet<Attribute>();

            // If any pattern namespace attribute contains "##any"
            if (anyAttribute.getNamespace() == null || anyAttribute.getNamespace().contains("##any")) {

                // Add all elements contained in the schema to the set
                topLevelAttributes.addAll(anyAttributeOldSchemaMap.get(anyAttribute).getAttributes());

                // Add all elements contained in foreign schemata to the set
                for (Iterator<ForeignSchema> it = anyAttributeOldSchemaMap.get(anyAttribute).getForeignSchemas().iterator(); it.hasNext();) {
                    ForeignSchema foreignSchema = it.next();
                    topLevelAttributes.addAll(foreignSchema.getSchema().getAttributes());
                }
                for (Attribute attribute : topLevelAttributes) {
                    attribute.setForm(Qualification.qualified);
                }
                return topLevelAttributes;

            } else if (anyAttribute.getNamespace().contains("##other")) {

                // If any pattern namespace attribute contains "##other" only add elements contained in foreign schemata to the set
                for (Iterator<ForeignSchema> it = anyAttributeOldSchemaMap.get(anyAttribute).getForeignSchemas().iterator(); it.hasNext();) {
                    ForeignSchema foreignSchema = it.next();
                    topLevelAttributes.addAll(foreignSchema.getSchema().getAttributes());
                }
                for (Attribute attribute : topLevelAttributes) {
                    attribute.setForm(Qualification.qualified);
                }
                return topLevelAttributes;
            } else {

                // Check list of namespaces
                for (String currentNamespace : anyAttribute.getNamespace().split(" ")) {

                    // If any pattern namespace attribute contains "##local"
                    if (currentNamespace.contains("##local")) {

                        // Check if target namespace is empty
                        if (anyAttributeOldSchemaMap.get(anyAttribute).getTargetNamespace().equals("")) {

                            // Add all elements contained in the schema to the set
                            topLevelAttributes.addAll(anyAttributeOldSchemaMap.get(anyAttribute).getAttributes());
                        } else {

                            // If any pattern namespace attribute contains "##local" only add elements contained in foreign schemata to the set
                            for (Iterator<ForeignSchema> it = anyAttributeOldSchemaMap.get(anyAttribute).getForeignSchemas().iterator(); it.hasNext();) {
                                ForeignSchema foreignSchema = it.next();

                                // Check if the current namespace is the namespace of the foreign schema
                                if (foreignSchema.getSchema().getTargetNamespace().equals("")) {
                                    topLevelAttributes.addAll(foreignSchema.getSchema().getAttributes());
                                }
                            }
                            return topLevelAttributes;
                        }
                    } else if (currentNamespace.contains("##targetNamespace")) {

                        // Add all elements contained in the schema to the set
                        topLevelAttributes.addAll(anyAttributeOldSchemaMap.get(anyAttribute).getAttributes());

                    } else {

                        // Find foreign schema with the specified namespace
                        for (Iterator<ForeignSchema> it = anyAttributeOldSchemaMap.get(anyAttribute).getForeignSchemas().iterator(); it.hasNext();) {
                            ForeignSchema foreignSchema = it.next();

                            // Check if target namespace is empty
                            if (foreignSchema.getSchema().getTargetNamespace().equals("")) {

                                // Add all elements contained in the schema to the set
                                topLevelAttributes.addAll(foreignSchema.getSchema().getAttributes());
                            }
                            // Check if the current namespace is the namespace of the foreign schema
                            if (foreignSchema.getSchema().getTargetNamespace().equals(currentNamespace)) {
                                topLevelAttributes.addAll(foreignSchema.getSchema().getAttributes());
                            }
                        }
                    }
                }
                for (Attribute attribute : topLevelAttributes) {
                    attribute.setForm(Qualification.qualified);
                }
                return topLevelAttributes;
            }
        } else {

            // If the any pattern is processed "lax" or "skip" no elements are returned
            return new LinkedHashSet<Attribute>();
        }
    }

    /**
     * Set new type difference generator for the attribute particle generator.
     *
     * @param typeDifferenceGenerator Type difference generator, which is used
     * in the attribute particle generator.
     */
    public void setTypeDifferenceGenerator(TypeDifferenceGenerator typeDifferenceGenerator) {
        this.typeDifferenceGenerator = typeDifferenceGenerator;
    }

    /**
     * If no attribute particle in a list of attribute particles has
     * AttributeUse "required" the list of attribute particles is optional.
     *
     * @param attributeParticles List of attribute particles, for which is
     * checked, if all attribute particles are not required.
     * @return <tt>true</tt> if no attribute particle is required and
     * <tt>false</tt> otherwise.
     */
    public boolean isOptional(LinkedList<AttributeParticle> attributeParticles) {

        // Use stack to store all attribute particles
        Stack<AttributeParticle> attributeParticleStack = new Stack<AttributeParticle>();
        attributeParticleStack.addAll(attributeParticles);

        // As long as the stack is not empty check the next attribute particle
        while (!attributeParticleStack.isEmpty()) {
            AttributeParticle attributeParticle = attributeParticleStack.pop();

            // If attribute particle is an attribute check contained "use" attribute
            if (attributeParticle instanceof Attribute) {
                Attribute attribute = (Attribute) attributeParticle;

                // If attribute contains a "use" attribute with value "required" return false
                if (attribute.getUse() == AttributeUse.Required) {
                    return false;
                }
            } else if (attributeParticle instanceof AttributeRef) {
                AttributeRef attributeRef = (AttributeRef) attributeParticle;

                // For an attribute reference check if attribute contains a "use" attribute with value "required" and return false if it does
                if (attributeRef.getUse() == AttributeUse.Required) {
                    return false;
                }
            } else if (attributeParticle instanceof AttributeGroupRef) {
                AttributeGroupRef attributeGroupRef = (AttributeGroupRef) attributeParticle;

                // For an attribute group add all contained attribute particles to the stack
                for (Iterator<AttributeParticle> it = attributeGroupRef.getAttributeGroup().getAttributeParticles().iterator(); it.hasNext();) {
                    AttributeParticle groupAttributeParticle = it.next();
                    attributeParticleStack.add(groupAttributeParticle);
                }
            }
        }

        // If no contained attribute had AttributeUse "required" return true
        return true;
    }
}
