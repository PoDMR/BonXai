package eu.fox7.bonxai.xsd.setOperations.union;

import eu.fox7.bonxai.common.Annotation;
import eu.fox7.bonxai.common.AnyAttribute;
import eu.fox7.bonxai.common.IdentifiedNamespace;
import eu.fox7.bonxai.common.ProcessContentsInstruction;
import eu.fox7.bonxai.common.SymbolTableRef;
import eu.fox7.bonxai.xsd.*;
import eu.fox7.bonxai.xsd.XSDSchema.Qualification;

import java.util.*;

/**
 * This class generates the new attribute particles for the union of schemata.
 * To generate a new sttribute particle either a single attribute particle is
 * specified, this is the case when the attribute particle does not intersect
 * with another attribute particle in the union, or a list of attribute
 * particles is given, when the attribute particles intersect.
 * 
 * @author Dominik Wolff
 */
public class AttributeParticleUnionGenerator {

    // XSDSchema which will contain the union of schemata contained in the schema group
    private XSDSchema outputSchema;

    // HashMap mapping target namespaces to output schemata, this is necessary to reference components in other schemata
    private LinkedHashMap<String, XSDSchema> namespaceOutputSchemaMap;

    // HashMap mapping attributes to old schemata used to construct the new output schema
    private LinkedHashMap<Attribute, XSDSchema> attributeOldSchemaMap;
    // HashMap mapping each any attribute to the old schema, that contains the any attribute
    private LinkedHashMap<AnyAttribute, XSDSchema> anyAttributeOldSchemaMap;

    // HashMap mapping namespaces to top-level attributes which are present in more than one schema of the schema group with the corresponding namespace
    private LinkedHashMap<String, LinkedHashSet<Attribute>> namespaceConflictingAttributeMap;

    // Set containing all IDs used in all new output schemata
    private LinkedHashSet<String> usedIDs;

    // Type union generator of the attribute particle union generator class
    private TypeUnionGenerator typeUnionGenerator;

    // Size of the current attribute particle lists list
    private int attributeParticleListsSize;

    // Set of schemata not contained in a schema group
    private LinkedHashSet<XSDSchema> otherSchemata = new LinkedHashSet<XSDSchema>();

    // NamespaceAbbreviationMap maps namespaces to abbreviations
    private LinkedHashMap<String, String> namespaceAbbreviationMap;

    // Directory where the new output schemata will be stored (Default is ""c:\")
    private String workingDirectory = "c:/";

    /**
     * This is the constructor of the AttributeParticleUnionGenerator class,
     * which initializes most of the fields contained in the class.
     * 
     * @param outputSchema  XSDSchema which will contain the new attribute
     * particles.
     * @param namespaceOutputSchemaMap Map mapping namespaces to output
     * schemata.
     * @param attributeOldSchemaMap Map mapping attributes to old schemata of
     * the output schema.
     * @param anyAttributeOldSchemaMap Map mapping each any attribute to the old
     * schema, that contains the any attribute.
     * @param namespaceConflictingAttributeMap Map maps namespaces to
     * conflicting attributes within that namespace.
     * @param usedIDs Set containg all IDs used in each output schema.
     * @param otherSchemata List of schemas not contained in schema groups.
     * @param namespaceAbbreviationMap Map that maps namespace to namespace
     * abbreviations.
     * @param workingDirectory Directory, which will contain the new output
     * schema.
     */
    public AttributeParticleUnionGenerator(XSDSchema outputSchema, LinkedHashMap<String, XSDSchema> namespaceOutputSchemaMap, LinkedHashMap<Attribute, XSDSchema> attributeOldSchemaMap, LinkedHashMap<AnyAttribute, XSDSchema> anyAttributeOldSchemaMap, LinkedHashMap<String, LinkedHashSet<Attribute>> namespaceConflictingAttributeMap, LinkedHashSet<String> usedIDs, LinkedHashSet<XSDSchema> otherSchemata, LinkedHashMap<String, String> namespaceAbbreviationMap, String workingDirectory) {

        //Initialize class fields
        this.outputSchema = outputSchema;
        this.namespaceOutputSchemaMap = namespaceOutputSchemaMap;
        this.attributeOldSchemaMap = attributeOldSchemaMap;
        this.anyAttributeOldSchemaMap = anyAttributeOldSchemaMap;
        this.namespaceConflictingAttributeMap = namespaceConflictingAttributeMap;
        this.usedIDs = usedIDs;
        this.otherSchemata = otherSchemata;
        this.namespaceAbbreviationMap = namespaceAbbreviationMap;
        this.workingDirectory = workingDirectory;
    }

    /**
     * Set new type union generator for the attribute particle generator.
     * 
     * @param typeUnionGenerator Type union generator, which is used in the
     * attribute particle generator.
     */
    public void setTypeUnionGenerator(TypeUnionGenerator typeUnionGenerator) {
        this.typeUnionGenerator = typeUnionGenerator;
    }

    /**
     * Get elements contained in the specified any attribute, if the any
     * attribute is processed strictly.
     *
     * @param anyAttribute Any attribute which specifies the set of elements.
     * @return A set containing all elements contained in the specified any
     * attribute.
     */
    private LinkedHashSet<Attribute> getContainedAttributes(AnyAttribute anyAttribute) {

        // Check if the any attribute is processed strictly
        if (anyAttribute.getProcessContentsInstruction().equals(ProcessContentsInstruction.Strict) || anyAttribute.getProcessContentsInstruction().equals(ProcessContentsInstruction.Lax)) {

            // Initalize set to store all top-level elements
            LinkedHashSet<Attribute> topLevelAttributes = new LinkedHashSet<Attribute>();

            // If any attribute namespace attribute contains "##any"
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

                // If any attribute namespace attribute contains "##other" only add elements contained in foreign schemata to the set
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

                    // If any attribute namespace attribute contains "##local"
                    if (currentNamespace.contains("##local")) {

                        // Check if target namespace is empty
                        if (anyAttributeOldSchemaMap.get(anyAttribute).getTargetNamespace().equals("")) {

                            // Add all elements contained in the schema to the set
                            topLevelAttributes.addAll(anyAttributeOldSchemaMap.get(anyAttribute).getAttributes());
                        } else {

                            // If any attribute namespace attribute contains "##local" only add elements contained in foreign schemata to the set
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

            // If the any attribute is processed "lax" or "skip" no elements are returned
            return new LinkedHashSet<Attribute>();
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
     * This method is used to unite the lists of attribute lists contained by
     * different types, which are united. A new attribute list is returned, which
     * fits all attribute lists.
     *
     * @param attributeParticleLists list of attribute lists contained by various
     * types
     * @return list of AttributeParticles, which is the union of specified
     * attribute lists
     */
    public LinkedList<AttributeParticle> generateAttributeParticleUnion(LinkedList<LinkedList<AttributeParticle>> attributeParticleLists) {

        // Create new attributeParticleListsSize variable with new value
        attributeParticleListsSize = attributeParticleLists.size();

        // Create map which maps attribute names to arrays of attributes, which can be used to generate new attributes
        LinkedHashMap<String, AttributeParticle[]> attributeNameAttributeParticlesMap = new LinkedHashMap<String, AttributeParticle[]>();

        // Create array of any attributes
        AnyAttribute[] anyAttributes = new AnyAttribute[attributeParticleListsSize];

        // Get map mapping attribute groups to attribute group references
        LinkedHashMap<AttributeGroup, AttributeGroupRef> containedAttributeGroupAttributeRefMap = new LinkedHashMap<AttributeGroup, AttributeGroupRef>();
        LinkedList<AttributeGroup> containedAttributeGroups = new LinkedList<AttributeGroup>();

        // Build attributeNameAttributeParticlesMap, which contains for each attribute name all corresponding attribute particles
        for (ListIterator<LinkedList<AttributeParticle>> it = attributeParticleLists.listIterator(); it.hasNext();) {
            LinkedList<AttributeParticle> attributeParticles = it.next();

            // Check each attribute particle contained in an attribute list of the attribute list list
            for (Iterator<AttributeParticle> it2 = attributeParticles.iterator(); it2.hasNext();) {
                AttributeParticle attributeParticle = it2.next();

                // Check if attribute particle is an attribute
                if (attributeParticle instanceof Attribute || attributeParticle instanceof AttributeRef) {

                    // Update attributeNameAttributeParticlesMap
                    updateAttributeNameAttributeParticlesMap(attributeNameAttributeParticlesMap, attributeParticle, attributeParticleListsSize, it.previousIndex());
                } else if (attributeParticle instanceof AnyAttribute) {
                    AnyAttribute anyAttribute = (AnyAttribute) attributeParticle;

                    // If attribute particle is an any attribute add any attribute to any attribute list
                    if (anyAttribute.getProcessContentsInstruction().equals(ProcessContentsInstruction.Skip) || anyAttribute.getProcessContentsInstruction().equals(ProcessContentsInstruction.Lax)) {
                        anyAttributes[it.previousIndex()] = anyAttribute;
                    }

                    // Add attributes contained in any attributes
                    for (Iterator<Attribute> it3 = getContainedAttributes(anyAttribute).iterator(); it3.hasNext();) {
                        Attribute attribute = it3.next();
                        updateAttributeNameAttributeParticlesMap(attributeNameAttributeParticlesMap, attribute, attributeParticleListsSize, it.previousIndex());
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
                            updateAttributeNameAttributeParticlesMap(attributeNameAttributeParticlesMap, groupAttributeParticle, attributeParticleListsSize, it.previousIndex());
                        } else if (groupAttributeParticle instanceof AnyAttribute) {
                            AnyAttribute anyAttribute = (AnyAttribute) groupAttributeParticle;

                            // If attribute particle is an any attribute add any attribute to any attribute list
                            if (anyAttribute.getProcessContentsInstruction().equals(ProcessContentsInstruction.Skip) || anyAttribute.getProcessContentsInstruction().equals(ProcessContentsInstruction.Lax)) {
                                anyAttributes[it.previousIndex()] = anyAttribute;
                            }

                            // Add attributes contained in any attributes
                            for (Iterator<Attribute> it4 = getContainedAttributes(anyAttribute).iterator(); it4.hasNext();) {
                                Attribute attribute = it4.next();
                                updateAttributeNameAttributeParticlesMap(attributeNameAttributeParticlesMap, attribute, attributeParticleListsSize, it.previousIndex());
                            }
                        }
                    }
                }
            }
        }
        LinkedHashSet<String> attributeNames = new LinkedHashSet<String>();

        // If in some complexTypes no attribute with corresponding name is present but an any attribute this any attribute is placed in the attributeNameAttributeParticlesMap
        for (int i = 0; i < anyAttributes.length; i++) {
            AnyAttribute anyAttribute = anyAttributes[i];

            // For attribute name entry in the attributeNameAttributeParticlesMap check if the current array position is null and if  place any attribute in this position
            for (Iterator<String> it = attributeNameAttributeParticlesMap.keySet().iterator(); it.hasNext();) {
                String attributeName = it.next();

                // If the a current array position is null place any attribute in this position (can be null as well)
                if (attributeNameAttributeParticlesMap.get(attributeName)[i] == null && anyAttribute != null && isIncluded(attributeName, anyAttribute)) {
                    attributeNames.add(attributeName);
                }
            }
        }

        // Get contained attribute names
        for (Iterator<String> it = attributeNames.iterator(); it.hasNext();) {
            String attributeName = it.next();
            attributeNameAttributeParticlesMap.remove(attributeName);
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
                    anyAttributes = new AnyAttribute[attributeParticleListsSize];

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

            LinkedList<AttributeParticle> attributeParticles = new LinkedList<AttributeParticle>();

            for (AttributeParticle attributeParticle : attributeNameAttributeParticlesMap.get(attributeName)) {

                if (attributeParticle != null) {
                    attributeParticles.add(attributeParticle);
                }
            }

            // If only one attribute particle is contained in the current entry
            if (containsOnlyOne(attributeNameAttributeParticlesMap.get(attributeName))) {

                // Get attribute particle contained in the entry
                AttributeParticle oldAttributeParticle = attributeParticles.getFirst();

                // Check if the contained attribute particle is an attribute or attribute reference and construct a new attribute particle accordingly
                AttributeParticle newAttributeParticle = null;
                if (oldAttributeParticle instanceof Attribute) {
                    newAttributeParticle = generateNewAttribute((Attribute) oldAttributeParticle);

                } else if (oldAttributeParticle instanceof AttributeRef) {
                    newAttributeParticle = generateNewAttributeRef((AttributeRef) oldAttributeParticle);
                }
                // If the new attribute particle is not null add it to the resulting attribute particle list
                if (newAttributeParticle != null) {
                    newAttributeParticles.add(newAttributeParticle);
                }
            } else {

                // If more than one attribute particle is contained in the current entry create a new attribute particle
                AttributeParticle newAttributeParticle = generateNewAttribute(attributeParticles);

                // If the new attribute particle is not null add it to the resulting attribute particle list
                if (newAttributeParticle != null) {
                    newAttributeParticles.add(newAttributeParticle);
                }
            }
        }

        // Get new any attributes.
        LinkedList<AnyAttribute> anyAttributesList = new LinkedList<AnyAttribute>();

        for (AnyAttribute anyAttribute : anyAttributes) {

            // Only add any attributes to list
            if (anyAttribute != null) {
                anyAttributesList.add(anyAttribute);
            }
        }

        // Check if any attributes where contained
        if (anyAttributesList.size() > 0) {
            newAttributeParticles.add(generateNewAnyAttribute(anyAttributesList));
        }

        // Reset attributeParticleListsSize value so that other methods are no longer affected
        attributeParticleListsSize = 0;

        // Return the list of resulting attribute particles
        return newAttributeParticles;
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
        String namespaceElement = attributeName.substring(1, attributeName.lastIndexOf("}"));

        // Check if the namespace of the attribute is contained in the namespace space defined by the "namespace" attribute of the any attribute
        if (anyAttribute.getNamespace() == null || anyAttribute.getNamespace().contains("##any")) {
            return true;
        } else if (anyAttribute.getNamespace().contains("##other") && !anyAttributeOldSchemaMap.get(anyAttribute).getTargetNamespace().equals(namespaceElement)) {
            return true;
        } else if (anyAttribute.getNamespace().contains("##local") && namespaceElement.equals("")) {
            return true;
        } else if (anyAttribute.getNamespace().contains("##targetNamespace") && anyAttributeOldSchemaMap.get(anyAttribute).getTargetNamespace().equals(namespaceElement)) {
            return true;
        } else if (anyAttribute.getNamespace().contains(namespaceElement) && !namespaceElement.equals("")) {
            return true;
        }

        // If the namespace of the attribute is not included return false
        return false;
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
     * This mehtod creates a new any attribute from a list of any attributes.
     * Because the new any attribute generates the union of "namespace"
     * attributes from given any attribute the output schema, which contain the
     * new any attribute is needed, because of its target namespace.
     *
     * @param anyAttributes List of different any attribute, used to construct
     * the new any attribute.
     * @return New AnyAttribute, which is registered in the output schema.
     */
    private AnyAttribute generateNewAnyAttribute(LinkedList<AnyAttribute> anyAttributes) {

        // Create new any attribute with new ProcessContentsInstruction and "namespace" attribute
        AnyAttribute anyAttribute = new AnyAttribute(ProcessContentsInstruction.Skip, getNamespace(anyAttributes));

        // Set new annotaton and "ID" attribute for the new any attribute
        anyAttribute.setAnnotation(getAnnotation(new LinkedList<AttributeParticle>(anyAttributes)));
        anyAttribute.setId(getID(new LinkedList<AttributeParticle>(anyAttributes)));

        // Return the new any attribtue
        return anyAttribute;
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
     * This method generates a new attribute particle from a list of attribute
     * particle. The list may contain attributes and attribute references. If
     * all attribute particles are attribute references an attribute references
     * is returned else a new attribute.
     *
     * @param attributeParticles Various attributes and attribute references,
     * all with same name or reference to an attribute with same name.
     * @return New attribute particle representing the union of all specified
     * attribute particles.
     */
    @SuppressWarnings("unchecked")
	private AttributeParticle generateNewAttribute(LinkedList<AttributeParticle> attributeParticles) {

        // Check if all attribute particles are attribute references, which reference the same attribute and this attribute is not in conflict
        if (isAttributeRef(attributeParticles) && !namespaceConflictingAttributeMap.containsKey(((AttributeRef) attributeParticles.getFirst()).getAttribute().getName())) {

            // Use the first attribute refernce to get the attribute SymbolTableRef
            AttributeRef oldAttributeRef = (AttributeRef) attributeParticles.getFirst();

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
            newAttributeRef.setAnnotation(getAnnotation(attributeParticles));
            newAttributeRef.setId(getID(attributeParticles));

            // Set "default", "fixed" and "use" values for the new attribute refernce
            newAttributeRef.setDefault(getDefault(attributeParticles, getUse(attributeParticles) == AttributeUse.Optional));
            newAttributeRef.setFixed(oldAttributeRef.getFixed());
            newAttributeRef.setUse(getUse(attributeParticles));

            // Check if "fixed" attribute is not present or only present once
            Object object = getFixed(attributeParticles, new SymbolTableRef<Type>("{}type", new SimpleType("{}type", null)));
            if (object instanceof String || object == null) {
                newAttributeRef.setFixed((String) object);
                return newAttributeRef;
            }
        }

        // Get attributes which are not proghibited
        LinkedList<AttributeParticle> notProhibitedAttributes = new LinkedList<AttributeParticle>();

        for (Iterator<AttributeParticle> it = attributeParticles.iterator(); it.hasNext();) {
            AttributeParticle attributeParticle = it.next();

            // Check if the referenced attribute or given attribute is prohibited
            if (!(attributeParticle instanceof Attribute && ((Attribute) attributeParticle).getUse() != null && ((Attribute) attributeParticle).getUse().equals(AttributeUse.Prohibited))
                    || !(attributeParticle instanceof AttributeRef && ((AttributeRef) attributeParticle).getUse() != null && ((AttributeRef) attributeParticle).getUse().equals(AttributeUse.Prohibited))){
                notProhibitedAttributes.add(attributeParticle);
            }
        }

        // Get new simpleType for the new attribute
        Type newSimpleType = new SimpleType("{http://www.w3.org/2001/XMLSchema}anyType", null);

        if (!notProhibitedAttributes.isEmpty()) {
            newSimpleType = getSimpleType(notProhibitedAttributes);
        }

        // Get SymbolTable reference for the new simpleType
        SymbolTableRef<Type> newSymbolTableRef = outputSchema.getTypeSymbolTable().getReference(newSimpleType.getName());

        // Create new attribute
        Attribute newAttribute = new Attribute(getName(attributeParticles), newSymbolTableRef);

        // Set "ID" attribute and annotation
        newAttribute.setAnnotation(getAnnotation(attributeParticles));
        newAttribute.setId(getID(attributeParticles));

        // Set "form" and "use" attributes (not present for top-level attributes)
        newAttribute.setForm(getForm(attributeParticles));
        newAttribute.setUse(getUse(attributeParticles));

        // Set "default" attribute and type attribute for the new attribute
        newAttribute.setDefault(getDefault(notProhibitedAttributes, getUse(notProhibitedAttributes) == AttributeUse.Optional));
        newAttribute.setTypeAttr(!newSimpleType.isAnonymous());

        // Get new "fixed" attribute, this can result in a new type
        Object object = getFixed(notProhibitedAttributes, newSymbolTableRef);
        if (object instanceof String) {

            // If result is a String, set new "fixed" value
            newAttribute.setFixed((String) object);
        } else if (object instanceof SymbolTableRef) {

            // If result is a reference to a type, set new anonymous type
            newAttribute.setSimpleType((SymbolTableRef<Type>) object);
            newAttribute.setTypeAttr(false);
        }

        // Check if the attribute was qualified and if add new group to another schema
        if (getForm(attributeParticles) != null && getForm(attributeParticles) == XSDSchema.Qualification.qualified && !newAttribute.getNamespace().equals(outputSchema.getTargetNamespace())) {

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

        // If the simpleType is anonymous (the simpleType is locally defined and is not referenced elsewhere) a new simpleType is created
        if (oldAttribute.getSimpleType() != null) {
            if (oldAttribute.getSimpleType().isAnonymous()) {
                typeUnionGenerator.generateNewType(oldAttribute.getSimpleType());
            } else {
                typeUnionGenerator.generateNewTopLevelType(oldAttribute.getSimpleType());
            }
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

            if (typeUnionGenerator.isBuiltInDatatype(oldAttribute.getSimpleType().getName())) {
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
            newAttribute.setForm(attributeOldSchemaMap.get(oldAttribute).getAttributeFormDefault());
        } else {

            // "form" attribute is present add "form" value to new attribute
            newAttribute.setForm(oldAttribute.getForm());
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
     * Create new attribute reference for a given old attribute reference. The
     * new attribute reference is a complete copy of the old attribute
     * reference. If the reference links to a attribute in another schema this
     * attribute will be referred to by the new attribute reference as well. If
     * it links to an attribute of the old schema the new refernce will link to
     * the corresponding attribute in the current new schema.  If the referred
     * attribute was changed due to intersection a new attribute is returned
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

        // Check if the attribute reference refers to an attribute which is in conflict
        if (namespaceConflictingAttributeMap.get(oldAttributeRef.getAttribute().getNamespace()).contains(oldAttributeRef.getAttribute().getName())) {

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
        } else {

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
            newAttributeRef.setAnnotation(generateNewAnnotation(oldAttributeRef.getAnnotation()));
            newAttributeRef.setId(getID(oldAttributeRef.getId()));

            // Set "default", "fixed" and "use" values for the new attribute refernce
            newAttributeRef.setDefault(oldAttributeRef.getDefault());
            newAttributeRef.setFixed(oldAttributeRef.getFixed());
            newAttributeRef.setUse(oldAttributeRef.getUse());

            // Return new attribute reference
            return newAttributeRef;
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
     * This method generates a new attribute with top-level declaration for the
     * given old attribute.
     *
     * @param oldAttribute Attribute which is copied into the outputSchema.
     * @return Copy of the specified attribute, registered in the top-level
     * attribute list of the output schema.
     */
    public Attribute generateNewTopLevelAttribute(Attribute oldAttribute) {

        // Generate new local attribute
        Attribute newAttribute = (Attribute) generateNewAttribute(oldAttribute);

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
     * This method generates a new top-level attribute from all specified
     * attribute particles. These attribute particles should be attributes
     * because only attributes can be declared top-level.
     *
     * @param attributeParticles List of attributes used to construct the new
     * top-level attribute.
     * @return New top-level Attribute registered in the output schema.
     */
    @SuppressWarnings("unchecked")
	public Attribute generateNewTopLevelAttribute(LinkedList<AttributeParticle> attributeParticles) {

        // Get new simpleType for the new top-level attribute
        Type newSimpleType = getSimpleType(attributeParticles);

        // Get SymbolTable reference for the new simpleType
        SymbolTableRef<Type> newSymbolTableRef = outputSchema.getTypeSymbolTable().getReference(newSimpleType.getName());

        // Create new top-level attribute
        Attribute newAttribute = new Attribute(getName(attributeParticles), newSymbolTableRef);

        // Set "ID" attribute and annotation
        newAttribute.setAnnotation(getAnnotation(attributeParticles));
        newAttribute.setId(getID(attributeParticles));

        // Set "form" and "use" attributes (not present for top-level attributes)
        newAttribute.setForm(null);
        newAttribute.setUse(null);

        // Set "default" attribute and type attribute for the new attribute
        newAttribute.setDefault(getDefault(attributeParticles, getUse(attributeParticles) == AttributeUse.Optional));
        newAttribute.setTypeAttr(!newSimpleType.isAnonymous());

        // Get new "fixed" attribute, this can result in a new type
        Object object = getFixed(attributeParticles, newSymbolTableRef);
        if (object instanceof String) {

            // If result is a String, set new "fixed" value
            newAttribute.setFixed((String) object);
        } else if (object instanceof SymbolTableRef) {

            // If result is a reference to a type, set new anonymous type
            newAttribute.setSimpleType((SymbolTableRef<Type>) object);
            newAttribute.setTypeAttr(false);
        }

        // Add new attribute to attribute SymbolTable and to top-level attribute list
        outputSchema.getAttributeSymbolTable().updateOrCreateReference(newAttribute.getName(), newAttribute);

        if (!outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(newAttribute.getName()))) {
            outputSchema.addAttribute(outputSchema.getAttributeSymbolTable().getReference(newAttribute.getName()));
        }
        return newAttribute;
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
     * This method creates a new annotation for a given list of attribute
     * particles. Each attribute particle may contain an annotation, these
     * annotations are used to contstruct the new annotation, which contains the
     * app infos and documentations of the old annotations.
     *
     * @param attributeParticles List of attribute particles, which is used to
     * construct a new attribute particle. This attribute particle will contain
     * the new annotation.
     * @return New Annotation, which contains the information stored in the
     * old annotations contained in the specified attribute particles.
     */
    private Annotation getAnnotation(LinkedList<AttributeParticle> attributeParticles) {

        // Create new annotation
        Annotation newAnnotation = null;

        // Check each attribute particle for a contained annotation
        for (AttributeParticle attributeParticle : attributeParticles) {

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
        }
        return newAnnotation;
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
     * This method computes a new "default" attribute for the given list of
     * attributes. A new "default" attribute only exists if only one attribute
     * in the list contains a "default" attribute. If more than one different
     * "default" attribute exists no attribute can be constructed.
     *
     * @param attributeParticles Attribute particle list which is used to
     * generate new "default" attribute
     * @param attributeUseOptional Boolean which is true when the "use"
     * attribute of the attribute for, which this "default" attribute is
     * generated, is optional.
     * @return New "default" attribute build from various "default" attributes.
     */
    private String getDefault(LinkedList<AttributeParticle> attributeParticles, boolean attributeUseOptional) {
        LinkedHashSet<String> defaultValues = new LinkedHashSet<String>();

        // Check each attribute particle for a "default" attribute
        for (Iterator<AttributeParticle> it = attributeParticles.iterator(); it.hasNext();) {
            AttributeParticle attributeParticle = it.next();

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
     * This method generates a new "fixed" attribute for a given set of
     * attribute particles. If not all attribute particles contain the same
     * "fixed" attribute either null or a new type are returned. The type used
     * enumerations fo the different "fixed" values.
     *
     * @param attributeParticles List of attribute particles, each attribute
     * particle may contain a "fixed" attribute.
     * @param newTypeRef Type reference of the type contained in the new
     * attribute particle.
     * @return If no "fixed" attribute was constructed a null pointer. If a
     * "fixed" attribute was constructed a String containing the value of the
     * "fixed" attribute. If no unambiguous "fixed" attribute could be
     * constructed a new type, which used enumerations for an equivalent
     * behaviour.
     */
    private Object getFixed(LinkedList<AttributeParticle> attributeParticles, SymbolTableRef<Type> newTypeRef) {

        // Use set of strings to store "fixed" values and boolean variable to check if all attribute particles are fixed
        LinkedHashSet<String> fixed = new LinkedHashSet<String>();
        boolean allFixed = true;

        // Check the specified attribute particles for "fixed" attribute
        for (AttributeParticle attributeParticle : attributeParticles) {

            // Check for attributes and attribute references contained "fixed" attributes
            if (attributeParticle instanceof Attribute) {
                Attribute attribute = ((Attribute) attributeParticle);

                // Check if attribute contains "fixed" attribute and if each attribute particle until now contained an "fixed" attribute
                if (allFixed && attribute.getFixed() != null) {
                    fixed.add(attribute.getFixed());
                } else {

                    // If one attribute is not fixed set allFixed variable to false
                    allFixed = false;
                }
            } else if (attributeParticle instanceof AttributeRef) {
                AttributeRef attributeRef = ((AttributeRef) attributeParticle);

                // Check if attribute reference contains "fixed" attribute or referenced attribute contains "fixed" attribute and if each attribute particle until now contained an "fixed" attribute
                if (allFixed && attributeRef.getFixed() != null) {
                    fixed.add(attributeRef.getFixed());
                } else if (allFixed && attributeRef.getAttribute().getFixed() != null) {
                    fixed.add(attributeRef.getAttribute().getFixed());
                } else {

                    // If one attribute reference is not fixed set allFixed variable to false
                    allFixed = false;
                }
            }
        }

        // Check if all attribute particles are fixed
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

                // If more than one "fixed" value exist and if the specified type reference of the new attribute particle referrers to a simpleType create new simple content restriction
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

            // If not all attribute particles are fixed return null
            return null;
        }
    }

    /**
     * This method generates a new "form" attribute from the "form" attributes
     * of given AttributeParticles.
     *
     * @param attributeParticles Attribute particle list which is used to
     * generate new "form" attribute
     * @return New "form" attribute build from various "form" attributes
     */
    private XSDSchema.Qualification getForm(LinkedList<AttributeParticle> attributeParticles) {

        // Check all attributes contained in the attribute particle list (only attributes can contain "form" attributes)
        for (Iterator<AttributeParticle> it = attributeParticles.iterator(); it.hasNext();) {
            AttributeParticle attributeParticle = it.next();

            // Check if the attribute particle is an attribute
            if (attributeParticle instanceof Attribute) {
                Attribute attribute = ((Attribute) attributeParticle);

                // If current "form" attribute has a value, which is qualified, the new "form" attribute has the same value
                if (attribute.getForm() != null && attribute.getForm() == XSDSchema.Qualification.qualified) {
                    return XSDSchema.Qualification.qualified;
                }
            }
        }

        // The default value for the "form" attribute is unqualified
        return null;
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
     * Builds a new ID from the IDs of given attribute particles.
     *
     * @param attributeParticles List of attribute particles, each may contain
     * an ID.
     * @return New ID generated from the IDs of specified attribute particles.
     */
    private String getID(LinkedList<AttributeParticle> attributeParticles) {

        // Initialize new ID
        String newID = "";

        // Check for each attribute particles if an ID is contained
        for (Iterator<AttributeParticle> it = attributeParticles.iterator(); it.hasNext();) {
            AttributeParticle attributeParticle = it.next();

            // If an ID is contained append ID to new ID
            if (attributeParticle.getId() != null) {
                newID += attributeParticle.getId();
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
     * This method returns the name which is contained in all specified
     * attribute particle. This is a simple method which is used with caution.
     *
     * @param attributeParticles List of attribute particle, which have all the
     * same name.
     * @return Name of all given attribute particles.
     */
    private String getName(LinkedList<AttributeParticle> attributeParticles) {

        // If element is qualified the namespace is important
        if ((getForm(attributeParticles) == null || getForm(attributeParticles) != XSDSchema.Qualification.qualified)) {


            // If the first attribute in the list of attribute particles is an attribute return the new name for an attribute
            if (attributeParticles.getFirst() instanceof Attribute) {
                return "{" + outputSchema.getTargetNamespace() + "}" + ((Attribute) attributeParticles.getFirst()).getLocalName();

            } else if (attributeParticles.getFirst() instanceof AttributeRef) {

                // If the first attribute in the list of attribute particle is an attribute reference return the new name for an attribute reference
                return "{" + outputSchema.getTargetNamespace() + "}" + ((AttributeRef) attributeParticles.getFirst()).getAttribute().getLocalName();
            } else {

                // This should not happen
                return null;
            }
        } else {

            // If the first attribute in the list of attribute particles is an attribute return the new name for an attribute
            if (attributeParticles.getFirst() instanceof Attribute) {
                return ((Attribute) attributeParticles.getFirst()).getName();

            } else if (attributeParticles.getFirst() instanceof AttributeRef) {

                // If the first attribute in the list of attribute particle is an attribute reference return the new name for an attribute reference
                return ((AttributeRef) attributeParticles.getFirst()).getAttribute().getName();
            } else {

                // This should not happen
                return null;
            }
        }
    }

    /**
     * This method constructs the "namespace" attribute of an any attribute.
     *
     * @param anyAttributes List of any attributes. Their "namespace" attributes
     * are used to create the new "namespace" attribute.
     * @return String representing the new "namespace" attribute or null.
     */
    private String getNamespace(LinkedList<AnyAttribute> anyAttributes) {

        // Initialize namespace set, which will contain found namespaces
        LinkedHashSet<String> namespaces = new LinkedHashSet<String>();

        // Check for each any attribute the contained "namespace" atttibute
        for (Iterator<AnyAttribute> it = anyAttributes.iterator(); it.hasNext();) {
            AnyAttribute currentAnyAttribute = it.next();
            String namespaceValue = currentAnyAttribute.getNamespace();

            // Check if the "namespace" attribute is present and if the resulting "namespace" attribute is already known
            if (namespaceValue != null && !(namespaces.contains("##any") || (namespaces.contains("##other") && (namespaces.contains("##local") || namespaces.contains("##targetNamespace"))))) {

                // Check if "##any" value is contained and add it to the set
                if (namespaceValue.contains("##any")) {
                    namespaces.add("##any");
                }
                // Check if "##other" value is contained in an any pattern with same namespace as the current ouput schema and add "##other" to the set
                if (namespaceValue.contains("##other") && anyAttributeOldSchemaMap.get(currentAnyAttribute).getTargetNamespace().equals(outputSchema.getTargetNamespace())) {
                    namespaces.add("##other");
                }
                // Check if "##other" value is contained in an any pattern with a different namespace as the current ouput schema and add "##any" to the set
                if (namespaceValue.contains("##other") && !anyAttributeOldSchemaMap.get(currentAnyAttribute).getTargetNamespace().equals(outputSchema.getTargetNamespace())) {
                    namespaces.add("##any");
                }
                // Check if "##local" value is contained and add it to the set
                if (namespaceValue.contains("##local")) {
                    namespaces.add("##local");
                }
                // Check if "##targetNamespace" value is contained in an any pattern with same namespace as the current ouput schema or a namespace equal to the target namespace and add it to the set
                if (namespaceValue.contains("##targetNamespace") && anyAttributeOldSchemaMap.get(currentAnyAttribute).getTargetNamespace().equals(outputSchema.getTargetNamespace()) || (namespaceValue.contains(anyAttributeOldSchemaMap.get(currentAnyAttribute).getTargetNamespace()) && !anyAttributeOldSchemaMap.get(currentAnyAttribute).getTargetNamespace().equals(""))) {
                    namespaces.add("##targetNamespace");
                }
                // Check if "##targetNamespace" value is contained in an any pattern with a different namespace as the current ouput schema
                if (namespaceValue.contains("##targetNamespace") && !anyAttributeOldSchemaMap.get(currentAnyAttribute).getTargetNamespace().equals(outputSchema.getTargetNamespace())) {

                    // For the empty namespace add "##local"
                    if (anyAttributeOldSchemaMap.get(currentAnyAttribute).getTargetNamespace().equals("")) {
                        namespaces.add("##local");
                    } else {
                        namespaces.add(anyAttributeOldSchemaMap.get(currentAnyAttribute).getTargetNamespace());
                    }
                }
                // If "namespace" attribute contains no "##any" or "##other" value a list of namespaces could be contained
                if (!namespaceValue.contains("##any") && !namespaceValue.contains("##other")) {
                    String[] namespaceValueArray = namespaceValue.split(" ");

                    // Check for each namespace if it is not the target namespace or any of the already checked namespaces and add it to the set
                    for (String namespace : namespaceValueArray) {
                        if (!namespace.equals(anyAttributeOldSchemaMap.get(currentAnyAttribute).getTargetNamespace()) && !namespaces.contains(namespace) && !namespace.equals("##local") && !namespace.equals("##targetNamespace")) {
                            namespaces.add(namespace);
                        }
                    }
                }
            } else {

                // If no namespace is present the default value is "##any"
                namespaces.add("##any");
            }
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
     * This method computes a new ProcessContentsInstruction for a new any
     * attribute.
     *
     * @param anyAttributes List of any attribute, which is needed to generate
     * the new ProcessContentsInstruction
     * @return ProcessContentsInstruction, which describes how to process the
     * containing any attribute.
     */
    private ProcessContentsInstruction getProcessContentsInstruction(LinkedList<AnyAttribute> anyAttributes) {

        // Initilize list of ProcessContentsInstructions, which is used to find the new ProcessContentsInstruction
        LinkedHashSet<ProcessContentsInstruction> processContentsInstructions = new LinkedHashSet<ProcessContentsInstruction>();

        // Check for each any attribute if it contains a ProcessContentsInstruction
        for (Iterator<AnyAttribute> it = anyAttributes.iterator(); it.hasNext();) {
            AnyAttribute anyAttribute = it.next();

            // Check if any attribute contains a ProcessContentsInstruction if it does add the ProcessContentsInstruction to the list
            if (anyAttribute.getProcessContentsInstruction() != null) {
                processContentsInstructions.add(anyAttribute.getProcessContentsInstruction());
            }
        }

        // If the list contains the value "skip" return skip, if no "skip" value is contained but a "lax" value return lax, else return strict
        if (processContentsInstructions.contains(ProcessContentsInstruction.Skip)) {
            return ProcessContentsInstruction.Skip;
        } else if (processContentsInstructions.contains(ProcessContentsInstruction.Lax)) {
            return ProcessContentsInstruction.Lax;
        } else {
            return ProcessContentsInstruction.Strict;
        }
    }

    /**
     * This method generates a new simpleType from a list of specified attribute
     * particles. The new simpleType is registered in the output schema.
     *
     * @param attributeParticles List of attribute particles, which is used to
     * generate the new simpleType
     * @return New simpleType, which can be used by the attribute created from
     * the list of attribute particles
     */
    private Type getSimpleType(LinkedList<AttributeParticle> attributeParticles) {

        // Use set to store all simpleTypes and a boolean variable to check if an anyType is contained
        LinkedList<SimpleType> simpleTypes = new LinkedList<SimpleType>();
        boolean containsAnyType = false;

        // For each attribute particle check if a typ is contained
        for (Iterator<AttributeParticle> it = attributeParticles.iterator(); it.hasNext();) {
            AttributeParticle attributeParticle = it.next();

            // Check if no anyType was contained until now
            if (!containsAnyType) {

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
            }
        }

        // Check if anyType was contained
        if (!containsAnyType) {

            // Get name of the new simpleType and return new simpleType
            String name = getSimpleTypeName(simpleTypes);
            return typeUnionGenerator.generateNewSimpleType(simpleTypes, name);
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
     * Create new name for a new simpleType. The name is constructed by using
     * the specified list of simpleTypes.
     *
     * @param memberTypes List of simpleTypes. The names of these simpleTypes
     * are contained in the new simpltType name.
     * @return String representing the name of the new simpleType.
     */
    private String getSimpleTypeName(LinkedList<SimpleType> simpleTypes) {

        // If only one simpleType is the new simpleType no new name is created
        if (simpleTypes.size() == 1) {

            if (typeUnionGenerator.isBuiltInDatatype(simpleTypes.getFirst().getName())) {
                return simpleTypes.getFirst().getName();
            } else {
                return "{" + outputSchema.getTargetNamespace() + "}" + simpleTypes.getFirst().getLocalName();
            }
        } else {

            // Generate new name for the simpleType
            String typeName = "{" + outputSchema.getTargetNamespace() + "}union-type.";

            for (Iterator<SimpleType> it = simpleTypes.iterator(); it.hasNext();) {
                String name = it.next().getLocalName();

                // If anySimpleType is contained return the anySimpleType name
                if (name.equals("{http://www.w3.org/2001/XMLSchema}anySimpleType")) {
                    return "{http://www.w3.org/2001/XMLSchema}anySimpleType";
                }

                // Add each type name to the new name
                typeName += name;

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
    }

    /**
     * This mehtod generates the "use" attribute of a new attribute form a list
     * of attribute particles.
     *
     * @param attributeParticles List of attribute particles which is used to
     * generate the new "use" attribute.
     * @return AttributeUse for the attribute generated from the union of those
     * attribute particles.
     */
    private AttributeUse getUse(LinkedList<AttributeParticle> attributeParticles) {

        // Use set of AttributeUses and boolean variable to create the new "use" attribute
        LinkedHashSet<AttributeUse> useValues = new LinkedHashSet<AttributeUse>();
        boolean attributeUseOptional = false;

        // Check for all attribute particles if "use" attributes are contained
        for (Iterator<AttributeParticle> it = attributeParticles.iterator(); it.hasNext();) {
            AttributeParticle attributeParticle = it.next();

            // If no "use" attribute particle until now had value "optional"
            if (!attributeUseOptional) {

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
            }
        }

        // If "optional" value was contained or if the size of the current attribute list is not the size of the attributeParticleList list return optional
        if (attributeUseOptional || attributeParticleListsSize != attributeParticles.size()) {
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
     * Returns true when all specified attribute particles are attribute
     * references.
     *
     * @param attributeParticles List of attribute particles used for the check.
     * @return <tt>true</tt> if the list of attribute particles contains only
     * attribute references.
     */
    private boolean isAttributeRef(LinkedList<AttributeParticle> attributeParticles) {

        // For each attribute parrticle check if the particle is an attribute
        for (Iterator<AttributeParticle> it = attributeParticles.iterator(); it.hasNext();) {
            AttributeParticle attributeParticle = it.next();

            // If particle is an attribute return false
            if (attributeParticle instanceof Attribute) {
                return false;
            }
        }

        // If not attributes are contained in the attribute particle list return true
        return true;
    }
}
