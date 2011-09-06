package eu.fox7.bonxai.xsd.setOperations.intersection;

import eu.fox7.bonxai.common.Annotation;
import eu.fox7.bonxai.common.AnyAttribute;
import eu.fox7.bonxai.common.IdentifiedNamespace;
import eu.fox7.bonxai.common.ProcessContentsInstruction;
import eu.fox7.bonxai.common.SymbolTableRef;
import eu.fox7.bonxai.xsd.*;
import eu.fox7.bonxai.xsd.XSDSchema.Qualification;

import java.util.*;

/**
 * This class is responsible for creating new AttributeParticles in the resulting
 * schema of an intersection operation. So it contains methods to intersect various
 * AttributeParticle parameters as well as Attributes, AttributeRefs, AnyAttributes
 * and so on. Furthermore a distinction is drawn between local and global
 * AttributeParticles, where local Attributes are not registered in the resulting
 * schema.
 * @author Dominik Wolff
 */
public class AttributeParticleIntersectionGenerator {

    // HashMap mapping namespaces to output schemata (Can be used if an element with a foreign namespace is referenced)
    private LinkedHashMap<String, XSDSchema> namespaceOutputSchemaMap;

    // XSDSchema which will contain the intersection of schemata contained in the schema group
    private XSDSchema outputSchema;

    // Map mapping each any attribute to the old schema, that contains the any attribute
    private LinkedHashMap<AnyAttribute, XSDSchema> anyAttributeOldSchemaMap;

    // Set of schemata not contained in a schema group
    private LinkedHashSet<XSDSchema> otherSchemata = new LinkedHashSet<XSDSchema>();

    // NamespaceAbbreviationMap maps namespaces to abbreviations
    private LinkedHashMap<String, String> namespaceAbbreviationMap;

    // Directory where the new output schemata will be stored (Default is ""c:\")
    private String workingDirectory = "c:/";

    // Map mapping top-level attribute names to top-level attributes.
    private LinkedHashMap<String, LinkedHashSet<Attribute>> nameTopLevelAttributesMap;

    // Type intersection generator of the attribute particle intersection generator class
    private TypeIntersectionGenerator typeIntersectionGenerator;

    /**
     * Construct new attribute particle intersection generator with new
     * specified fiels.
     *
     * @param outputSchema XSDSchema which will contain the intersection of schemata
     * contained in the schema group.
     * @param namespaceOutputSchemaMap HashMap mapping namespaces to output
     * schemata.
     * @param anyAttributeOldSchemaMap Map mapping each any attribute to the old
     * schema, that contains the any attribute.
     * @param otherSchemata Set of schemata not contained in a schema group.
     * @param namespaceAbbreviationMap NamespaceAbbreviationMap maps namespaces
     * to abbreviations.
     * @param workingDirectory Directory where the new output schemata will be
     * stored.
     */
    public AttributeParticleIntersectionGenerator(XSDSchema outputSchema,
            LinkedHashMap<String, XSDSchema> namespaceOutputSchemaMap,
            LinkedHashMap<AnyAttribute, XSDSchema> anyAttributeOldSchemaMap,
            LinkedHashSet<XSDSchema> otherSchemata,
            LinkedHashMap<String, String> namespaceAbbreviationMap,
            String workingDirectory) {

        //Initialize class fields
        this.outputSchema = outputSchema;
        this.namespaceOutputSchemaMap = namespaceOutputSchemaMap;
        this.anyAttributeOldSchemaMap = anyAttributeOldSchemaMap;
        this.otherSchemata = otherSchemata;
        this.namespaceAbbreviationMap = namespaceAbbreviationMap;
        this.workingDirectory = workingDirectory;
    }

    /**
     * This method creates for an array of attribute particles a new atttribute
     * or attribute reference as result of an intersection. This new attribute
     * particle is declared local. Attribute groups and attribute group
     * references amongst the attribute particles are ignored. Furthermore there
     * should be no null entry in the array.
     *
     * @param attributeName Name of the new Attribute, this should be
     * the name shared by all attributes in the array, attribute references
     * refer to attributes with the same name. The name can be a local name
     * if no attribute references are contained in the array and only
     * unqualified attributes are contained.
     * @param attributeParticleArray Array of attribute particels with no null
     * entries.
     * @return Attribute if any attribute is amongst the attribute particles or
     * if the referred attributes of attribute references no longer exist.
     * Attribute referene if no attribute is present and the referred attributes
     * were intersected.
     */
    private AttributeParticle generateNewLocalAttributeOrAttributeRef(String attributeName, AttributeParticle[] attributeParticleArray) {

        // Initialize sets to partition the attribute particles, contained in the attributeParticleArray, into
        LinkedHashSet<Attribute> attributes = new LinkedHashSet<Attribute>();
        LinkedHashSet<AttributeRef> attributeRefs = new LinkedHashSet<AttributeRef>();
        LinkedHashSet<AnyAttribute> anyAttributes = new LinkedHashSet<AnyAttribute>();
        LinkedHashSet<AttributeParticle> attributesAndAttributeRefs = new LinkedHashSet<AttributeParticle>();

        // Get set to store contained simpleTypes
        LinkedHashSet<SimpleType> simpleTypes = new LinkedHashSet<SimpleType>();

        // Partition attribute particle array
        for (int i = 0; i < attributeParticleArray.length; i++) {
            AttributeParticle attributeParticle = attributeParticleArray[i];

            // If attribute particle is attribut, attribute reference or any attribute add it to the respective set
            if (attributeParticle instanceof Attribute) {

                // Add attribute to attributesAndAttributeRefs and attributes sets and contained simpleType to the simpleType set
                attributes.add((Attribute) attributeParticle);
                attributesAndAttributeRefs.add(attributeParticle);

                if (((Attribute) attributeParticle).getSimpleType() == null) {
                    simpleTypes.add(new SimpleType("{http://www.w3.org/2001/XMLSchema}anySimpleType", null));
                } else {
                    simpleTypes.add(((Attribute) attributeParticle).getSimpleType());
                }
            } else if (attributeParticle instanceof AttributeRef) {

                // Add attribute to attributesAndAttributeRefs and attributeRefs sets and contained simpleType to the simpleType set
                attributeRefs.add((AttributeRef) attributeParticle);
                attributesAndAttributeRefs.add(attributeParticle);

                if (((AttributeRef) attributeParticle).getAttribute().getSimpleType() == null) {
                    simpleTypes.add(new SimpleType("{http://www.w3.org/2001/XMLSchema}anySimpleType", null));
                } else {
                    simpleTypes.add(((AttributeRef) attributeParticle).getAttribute().getSimpleType());
                }
            } else if (attributeParticle instanceof AnyAttribute) {

                // Add any attribute to anyAttributes set
                anyAttributes.add((AnyAttribute) attributeParticle);
            }
        }

        // Use boolean varibale to store the qualification information
        Qualification newForm = getForm(attributesAndAttributeRefs);

        if (getUse(attributesAndAttributeRefs) != AttributeUse.Prohibited) {

            // If AnyAttributes are present each Attribute must be included in all of them, this includes Attributes referenced through AttributeRefs
            if (!anyAttributes.isEmpty()) {

                // For each any attribute check if it contains an attribute for this
                for (Iterator<AnyAttribute> it = anyAttributes.iterator(); it.hasNext();) {
                    AnyAttribute anyAttribute = it.next();

                    if (!isIncluded(attributeName, anyAttribute)) {

                        // If the any attribute does not contain the attribute namespace and has no "strict" "use" value return null
                        return null;
                    }
                }
            }

            // If attribute set is not empty create new attribute
            if (!attributes.isEmpty()) {

                // Build new simpleType for the new attribute
                SimpleType simpleType = typeIntersectionGenerator.generateNewSimpleType(simpleTypes);

                // Check if simpltType was generated
                if (simpleType == null) {
                    return null;
                }
                SymbolTableRef<Type> newSimpleTypeRef = outputSchema.getTypeSymbolTable().getReference(simpleType.getName());

                // Generate new "use" attribute
                AttributeUse newUse = getUse(attributesAndAttributeRefs);

                // If "use" attribute, "fixed" attribute or type are null return null
                if (newUse == null || newSimpleTypeRef == null || hasFixed(attributesAndAttributeRefs) && getFixed(attributesAndAttributeRefs) == null) {
                    return null;
                }

                // Generate new "default" attribute
                String newDefault = getDefault(attributesAndAttributeRefs, newUse == AttributeUse.Optional);

                // Create new attribute with new attributes and return the new attribute
                Attribute newAttribute = new Attribute(attributeName, newSimpleTypeRef, newDefault, getFixed(attributesAndAttributeRefs), newUse, newSimpleTypeRef.getReference().isAnonymous(), newForm, getAnnotation(attributesAndAttributeRefs));
                newAttribute.setId(getID(attributesAndAttributeRefs));

                // Check if the attribute was qualified and if add new group to another schema
                if (newForm != null && newForm == XSDSchema.Qualification.qualified && !newAttribute.getNamespace().equals(outputSchema.getTargetNamespace())) {

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
                return newAttribute;

            } else if (!attributeRefs.isEmpty() && attributes.isEmpty()) {

                // If only attribute references are  contained use new attribute set to store referred attributes
                LinkedHashSet<Attribute> referredAttributes = new LinkedHashSet<Attribute>();

                // Add all referred attributes to the referred attribute set
                for (Iterator<AttributeRef> it = attributeRefs.iterator(); it.hasNext();) {
                    AttributeRef attributeRef = it.next();
                    referredAttributes.add(attributeRef.getAttribute());
                }

                // Get entry of the nameTopLevelAttributesMap
                HashSet<Attribute> intersectingAttributes = nameTopLevelAttributesMap.get(referredAttributes.iterator().next().getName());

                // Get new "use" and "fixed" attributes
                AttributeUse newUse = getUse(attributesAndAttributeRefs);
                String newFixed = getFixed(attributesAndAttributeRefs);

                // If "use" or "fixed" attribute are invalid return null
                if (newUse == null || hasFixed(attributesAndAttributeRefs) && getFixed(attributesAndAttributeRefs) == null) {
                    return null;
                }

                // Get new "ID" and "default" attributes and new annotation
                String newID = getID(attributesAndAttributeRefs);
                Annotation newAnnotation = getAnnotation(attributesAndAttributeRefs);
                String newDefault = getDefault(attributesAndAttributeRefs, newUse == AttributeUse.Optional);

                if (namespaceOutputSchemaMap.get(referredAttributes.iterator().next().getNamespace()) != null && namespaceOutputSchemaMap.get(referredAttributes.iterator().next().getNamespace()).getAttributeSymbolTable().hasReference(attributeName) &&
                        intersectingAttributes.containsAll(referredAttributes) && referredAttributes.containsAll(intersectingAttributes)) {

                    // Get attribute SymbolTabeleRef
                    SymbolTableRef<Attribute> attributeRef = namespaceOutputSchemaMap.get(referredAttributes.iterator().next().getNamespace()).getAttributeSymbolTable().getReference(attributeName);

                    // Create new attribute reference with generate attributes and return the reference
                    AttributeRef newAttributeRef = new AttributeRef(attributeRef, newDefault, newFixed, newUse, newAnnotation);
                    newAttributeRef.setId(newID);
                    return newAttributeRef;
                } else {

                    // Build new simpleType for the new attribute
                    SimpleType simpleType = typeIntersectionGenerator.generateNewSimpleType(simpleTypes);
                    SymbolTableRef<Type> newSimpleTypeRef = outputSchema.getTypeSymbolTable().getReference(simpleType.getName());

                    // If simpleType is invalid return null
                    if (newSimpleTypeRef == null) {
                        return null;
                    }

                    // Create new attribute with new attributes and return the new attribute
                    Attribute newAttribute = new Attribute(attributeName, newSimpleTypeRef, newDefault, newFixed, newUse, newSimpleTypeRef.getReference().isAnonymous(), Qualification.qualified, newAnnotation);
                    newAttribute.setId(newID);

                    // Check if the attribute was qualified and if add new group to another schema
                    if (newForm != null && newForm == XSDSchema.Qualification.qualified && !newAttribute.getNamespace().equals(outputSchema.getTargetNamespace())) {

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
                    return newAttribute;
                }
            }
        } else {

            // Generate new attribute
            Attribute newAttribute = new Attribute(attributeName);
            newAttribute.setUse(getUse(attributesAndAttributeRefs));
            newAttribute.setId(getID(attributesAndAttributeRefs));
            newAttribute.setAnnotation(getAnnotation(attributesAndAttributeRefs));
            newAttribute.setForm(newForm);

            // Check if the attribute was qualified and if add new group to another schema
            if (newForm != null && newForm == XSDSchema.Qualification.qualified && !newAttribute.getNamespace().equals(outputSchema.getTargetNamespace())) {

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
            return newAttribute;
        }
        return null;
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
     * Checks if one of the attribute particles contained in the given attribute
     * particle set contains a "fixed" value. If this is the case a new "fixed"
     * value can be computed.
     *
     * @param attributeParticles Set of attribute particles, each attribute
     * particle may contain a "fixed" attribute.
     * @return <tt>true</tt> if an attribute particle contains a "fixed" value,
     * else <tt>false</tt>.
     */
    private boolean hasFixed(LinkedHashSet<AttributeParticle> attributeParticles) {

        // For each attribute particle check if "fixed" value is contained
        for (Iterator<AttributeParticle> it = attributeParticles.iterator(); it.hasNext();) {
            AttributeParticle attributeParticle = it.next();

            // If attribute particle is attribute check "fixed" attribute of the attribute else check "fixed" attribute of the attribute reference or the "fixed" attribute of the referred attribute
            if (attributeParticle instanceof Attribute) {
                Attribute attribute = (Attribute) attributeParticle;

                // Check if attribute contains "fixed" attribute
                if (attribute.getFixed() != null) {
                    return true;
                }
            } else if (attributeParticle instanceof AttributeRef) {
                AttributeRef attributeRef = (AttributeRef) attributeParticle;

                // Check if attribute reference or attribute has "fixed" attribute
                if (attributeRef.getFixed() != null || attributeRef.getAttribute().getFixed() != null) {
                    return true;
                }
            }
        }

        // Return false if no element contains a "fixed" attribute
        return false;
    }

    /**
     * Set new type intersection generator for the attribute particle generator.
     *
     * @param typeIntersectionGenerator Type intersection generator, which is
     * used in the attribute particle generator.
     */
    public void setTypeIntersectionGenerator(TypeIntersectionGenerator typeIntersectionGenerator) {
        this.typeIntersectionGenerator = typeIntersectionGenerator;
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
     * Get attributes contained in the specified any attributes, if the any
     * attributes is processed strictly.
     *
     * @param anyAttribute Any attributes which specifies the set of attributes.
     * @return A set containing all attributes contained in the specified any
     * attribute.
     */
    private LinkedHashSet<Attribute> getContainedAttributes(AnyAttribute anyAttribute) {

        // Check if the any attribute is processed strictly
        if (anyAttribute.getProcessContentsInstruction().equals(ProcessContentsInstruction.Strict) || anyAttribute.getProcessContentsInstruction().equals(ProcessContentsInstruction.Lax)) {

            // Initalize set to store all top-level attributes
            LinkedHashSet<Attribute> topLevelAttributes = new LinkedHashSet<Attribute>();

            // If any attribute namespace attribute contains "##any"
            if (anyAttribute.getNamespace() == null || anyAttribute.getNamespace().contains("##any")) {

                // Add all attributes contained in the schema to the set
                topLevelAttributes.addAll(anyAttributeOldSchemaMap.get(anyAttribute).getAttributes());

                // Add all attributes contained in foreign schemata to the set
                for (Iterator<ForeignSchema> it = anyAttributeOldSchemaMap.get(anyAttribute).getForeignSchemas().iterator(); it.hasNext();) {
                    ForeignSchema foreignSchema = it.next();
                    topLevelAttributes.addAll(foreignSchema.getSchema().getAttributes());
                }
                for (Attribute attribute : topLevelAttributes) {
                    attribute.setForm(Qualification.qualified);
                }
                return topLevelAttributes;

            } else if (anyAttribute.getNamespace().contains("##other")) {

                // If any attribute namespace attribute contains "##other" only add attributes contained in foreign schemata to the set
                for (Iterator<ForeignSchema> it = anyAttributeOldSchemaMap.get(anyAttribute).getForeignSchemas().iterator(); it.hasNext();) {
                    ForeignSchema foreignSchema = it.next();
                    topLevelAttributes.addAll(foreignSchema.getSchema().getAttributes());
                }

                // Set attributes qualified
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

                            // Add all attributes contained in the schema to the set
                            topLevelAttributes.addAll(anyAttributeOldSchemaMap.get(anyAttribute).getAttributes());
                        } else {

                            // If any attribute namespace attribute contains "##local" only add attributes contained in foreign schemata to the set
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

                        // Add all attributes contained in the schema to the set
                        topLevelAttributes.addAll(anyAttributeOldSchemaMap.get(anyAttribute).getAttributes());

                    } else {

                        // Find foreign schema with the specified namespace
                        for (Iterator<ForeignSchema> it = anyAttributeOldSchemaMap.get(anyAttribute).getForeignSchemas().iterator(); it.hasNext();) {
                            ForeignSchema foreignSchema = it.next();

                            // Check if target namespace is empty
                            if (foreignSchema.getSchema().getTargetNamespace().equals("")) {

                                // Add all attributes contained in the schema to the set
                                topLevelAttributes.addAll(foreignSchema.getSchema().getAttributes());
                            }
                            // Check if the current namespace is the namespace of the foreign schema
                            if (foreignSchema.getSchema().getTargetNamespace().equals(currentNamespace)) {
                                topLevelAttributes.addAll(foreignSchema.getSchema().getAttributes());
                            }
                        }
                    }
                }

                // Set attributes qualified
                for (Attribute attribute : topLevelAttributes) {
                    attribute.setForm(Qualification.qualified);
                }
                return topLevelAttributes;
            }
        } else {

            // If the any attribute is processed "lax" or "skip" no attributes are returned
            return new LinkedHashSet<Attribute>();
        }
    }

    /**
     * When building a new complexType it is necessary to intersect the
     * contained attributes. This method serves this purpose. An intersection
     * for the given list of attribute lists is constructed by comparing
     * attributes, attribute references and any attributes and building
     * intersections for each, if they match other attribute particles. If i.e.
     * an attribute has no match in another attribute list, it is not in the
     * intersection and if it is required, which can be specified via the "use"
     * attribute, the whole intersection is empty.
     *
     * @param attributeParticleLists List of attribute particle lists. Each
     * complexType contains a LinkedList of AttributeParticles these list are
     * combined in one list, which is then used by this method to build a new
     * list of attribute particle.
     * @return Null if no intersection was possible or intersection is empty,
     * otherwise a list of attribute particles.
     */
    public LinkedList<AttributeParticle> generateAttributeParticleIntersection(LinkedList<LinkedList<AttributeParticle>> attributeParticleLists) {

        // Create new attributeParticleListsSize variable with new value
        int attributeParticleListsSize = attributeParticleLists.size();

        // Create map which maps attribute names to arrays of attributes, which can be used to generate new attributes
        LinkedHashMap<String, AttributeParticle[]> attributeNameAttributeParticlesMap = new LinkedHashMap<String, AttributeParticle[]>();

        // Create array of any attributes
        AnyAttribute[] anyAttributes = new AnyAttribute[attributeParticleListsSize];

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

                    // Check all attribute particles contained in the attribute group
                    for (Iterator<AttributeParticle> it3 = getContainedAttributeParticles(attributeGroup).iterator(); it3.hasNext();) {
                        AttributeParticle groupAttributeParticle = it3.next();

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

        // If in some complexTypes no attribute with corresponding name is present but an any attribute this any attribute is placed in the attributeNameAttributeParticlesMap
        for (int i = 0; i < anyAttributes.length; i++) {
            AnyAttribute anyAttribute = anyAttributes[i];

            // For attribute name entry in the attributeNameAttributeParticlesMap check if the current array position is null and if  place any attribute in this position
            for (Iterator<String> it = attributeNameAttributeParticlesMap.keySet().iterator(); it.hasNext();) {
                String attributeName = it.next();

                // If the a current array position is null place any attribute in this position (can be null as well)
                if (attributeNameAttributeParticlesMap.get(attributeName)[i] == null) {
                    attributeNameAttributeParticlesMap.get(attributeName)[i] = anyAttribute;
                }
            }
        }

        // Remove AttributeParticles with empty Intersection, this is the case when an array entry is null for an Attribute name.
        LinkedList<String> attributesWithEmptyIntersection = new LinkedList<String>();

        // For each entry of the attributeNameAttributeParticlesMap check if an array contains a null value
        for (Iterator<String> it = attributeNameAttributeParticlesMap.keySet().iterator(); it.hasNext();) {
            String attributeName = it.next();

            // For each array entry check if entry attribute is prohibited
            boolean isProhibited = false;

            for (int i = 0; i < attributeNameAttributeParticlesMap.get(attributeName).length; i++) {
                LinkedHashSet<AttributeParticle> attributeParticles = new LinkedHashSet<AttributeParticle>();
                attributeParticles.add(attributeNameAttributeParticlesMap.get(attributeName)[i]);

                if (getUse(attributeParticles) == AttributeUse.Prohibited) {
                    isProhibited = true;
                }
            }

            // For each array entry check if entry is empty
            for (int i = 0; i < attributeNameAttributeParticlesMap.get(attributeName).length; i++) {
                AttributeParticle attributeParticle = attributeNameAttributeParticlesMap.get(attributeName)[i];

                // If an empty array entry is present add attribute name to list of attributes names without intersection
                if (attributeParticle == null && !isProhibited) {
                    attributesWithEmptyIntersection.add(attributeName);
                }
            }
        }

        // If an attribute particle should be removed and one of the attribute particle in its intersection has "use" attribute value "required" the whole intersection is empty
        for (Iterator<String> it = attributesWithEmptyIntersection.iterator(); it.hasNext();) {
            String attributeName = it.next();

            // For each attribute particle for the current attribute name, which should be removed, check "use" attribute
            for (int i = 0; i < attributeNameAttributeParticlesMap.get(attributeName).length; i++) {
                AttributeParticle attributeParticle = attributeNameAttributeParticlesMap.get(attributeName)[i];

                // If attribute particle is attribute check attribute for "use" attribute else if attribute particle is attribute reference check attribute reference and referred attribute for "use" attribute
                if (attributeParticle instanceof Attribute) {
                    Attribute attribute = (Attribute) attributeParticle;

                    // If "use" attribute is "required" return null
                    if (attribute != null && attribute.getUse() != null && attribute.getUse() == AttributeUse.Required) {
                        return null;
                    }
                } else if (attributeParticle instanceof AttributeRef) {
                    AttributeRef attributeRef = (AttributeRef) attributeParticle;

                    // If attribute reference or referred attribute contains "use" attribute with value "required" return null
                    if (attributeRef != null && (attributeRef.getAttribute().getUse() != null && attributeRef.getAttribute().getUse() == AttributeUse.Required || attributeRef.getUse() != null && attributeRef.getUse() == AttributeUse.Required)) {
                        return null;
                    }
                }
            }
            // If no required attribute is contained remove attributeNameAttributeParticlesMap entry
            attributeNameAttributeParticlesMap.remove(attributeName);
        }

        // Build new LinkedList of AttributeParticles from attributeMap for the new complexType
        LinkedList<AttributeParticle> newAttributeParticles = new LinkedList<AttributeParticle>();

        // For each attribute name contained in the attributeNameAttributeParticlesMap generate new attribute
        for (Iterator<String> it = attributeNameAttributeParticlesMap.keySet().iterator(); it.hasNext();) {
            String attributeName = it.next();

            // Generate new attribute particle
            AttributeParticle attributeParticle = generateNewLocalAttributeOrAttributeRef(attributeName, attributeNameAttributeParticlesMap.get(attributeName));

            // If the generated attribute particle is null check if whole attribute list is null else add attribute particle to the list
            if (attributeParticle == null) {

                // For each attribute particle check if it is required
                for (int i = 0; i < attributeNameAttributeParticlesMap.get(attributeName).length; i++) {
                    AttributeParticle mappedAttributeParticle = attributeNameAttributeParticlesMap.get(attributeName)[i];

                    // If attribute particle is attribute check attribute for "use" attribute else if attribute particle is attribute reference check attribute reference and referred attribute for "use" attribute
                    if (mappedAttributeParticle instanceof Attribute) {
                        Attribute attribute = (Attribute) mappedAttributeParticle;

                        // If "use" attribute is "required" return null
                        if (attribute.getUse() == AttributeUse.Required) {
                            return null;
                        }
                    } else if (mappedAttributeParticle instanceof AttributeRef) {
                        AttributeRef attributeRef = (AttributeRef) mappedAttributeParticle;

                        // If attribute reference or referred attribute contains "use" attribute with value "required" return null
                        if (attributeRef.getAttribute().getUse() == AttributeUse.Required || attributeRef.getUse() == AttributeUse.Required) {
                            return null;
                        }
                    }
                }
            } else {
                newAttributeParticles.add(attributeParticle);
            }
        }

        // If possible build a new any attribute but only if all attribute lists contained one
        AnyAttribute newAnyAttribute = generateNewAnyAttribute(anyAttributes);

        // If the new any attribute is not null add it to attribute particle list, which is then returned
        if (newAnyAttribute != null) {
            newAttributeParticles.add(newAnyAttribute);
        }
        return newAttributeParticles;
    }

    /**
     * This method generates all top-level attributes of the output schema. To
     * do this the method gets a set of attribute lists, these list are checked
     * for intersecting attributes and new attributes are build and registered
     * in the output schema. Because top-level attributes are used to be
     * referenced by attribute references and any attributes, attributes, which
     * appear only in one schema but not in all, are not registered in the
     * output schema.
     *
     * @param topLevelAttributeListSet Set of lists of attributes, these lists
     * are normaly contained in schemata, to reference top-level attributes.
     * @return HashMap mapping attribute names to attributes used to generate
     * them.
     */
    public LinkedHashMap<String, LinkedHashSet<Attribute>> generateNewTopLevelAttributes(LinkedHashSet<LinkedList<Attribute>> topLevelAttributeListSet) {

        // Initialize HashMap mapping top-level attribute names to top-level attributes used to generate them
        nameTopLevelAttributesMap = new LinkedHashMap<String, LinkedHashSet<Attribute>>();

        // Generate nameTopLevelAttributesMap, which maps each attribute name to a set of attributes is was generated from, if it was.
        for (Iterator<LinkedList<Attribute>> it = topLevelAttributeListSet.iterator(); it.hasNext();) {
            LinkedList<Attribute> topLevelAttributes = it.next();

            // For each top-level attribute list check all contained attributes
            for (Iterator<Attribute> it2 = topLevelAttributes.iterator(); it2.hasNext();) {
                Attribute topLevelAttribute = it2.next();

                // Check if the namespace of the top-level attribute is the same as the target namespace of the output schema, if nothing is done with the attribute
                if (topLevelAttribute.getNamespace().equals(outputSchema.getTargetNamespace())) {

                    // Update nameTopLevelAttributesMap with new attribute, create new entry for the name of the attribute or add attribute to an existing one
                    if (!nameTopLevelAttributesMap.containsKey(topLevelAttribute.getName())) {
                        nameTopLevelAttributesMap.put(topLevelAttribute.getName(), new LinkedHashSet<Attribute>());
                    }
                    LinkedHashSet<Attribute> intersectionAttributes = nameTopLevelAttributesMap.get(topLevelAttribute.getName());
                    intersectionAttributes.add(topLevelAttribute);
                    nameTopLevelAttributesMap.put(topLevelAttribute.getName(), intersectionAttributes);
                }
            }
        }

        // An attribute name in the nameTopLevelAttributesMap responds to a set of attributes if this set is of the same size as the number of
        // intersecting schemata, meaning each schema contains an attribute with same name, this entry is left in the HashMap all other are removed.
        for (Iterator<String> it = nameTopLevelAttributesMap.keySet().iterator(); it.hasNext();) {
            String attributeName = it.next();

            // Compare size of nameTopLevelAttributesMap entry and the specified set of attribute lists
            if (nameTopLevelAttributesMap.get(attributeName).size() != topLevelAttributeListSet.size()) {
                nameTopLevelAttributesMap.remove(attributeName);
            }
        }

        // For each entry in the nameTopLevelAttributesMap a new top-level Attribute is constructed and registered in the output schema.
        // But it is also possible that the intersection is empty and then no attribute is constructed or registered in the output schema.
        for (Iterator<String> it = nameTopLevelAttributesMap.keySet().iterator(); it.hasNext();) {
            String attributeName = it.next();

            // Create new top-level attribute for the attribute name entry
            generateNewTopLevelAttribute(nameTopLevelAttributesMap.get(attributeName));
        }

        // Return the complete and updated nameTopLevelAttributesMap
        return nameTopLevelAttributesMap;
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

        // Get namespace of the attribute
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
     * Generates a new "namespace" attribute for a new any attribute. The
     * attribute is constructed as intersection of the given any attribute
     * array. To generate an accurate intersection the target namespaces of the
     * any attributes are considered too. This is important for values like
     * "##other", which selects all namespaces different from the target
     * namespace of the current any attribute.
     *
     * As it is not possible to exclude certain namespaces from the namespace
     * list some intersections like the intersection of two "##other" values
     * from different namespaces are approximated, in this case through a
     * "##other" value, which does not exlude the target namespace of the other
     * "##other" value.
     *
     * @param anyAttributes Array of any attributes each may contain a
     * "namespace" attribute.
     * @return New list of namespaces. The list is represented as String and
     * an intersection of the "namespace" values of specified any attributes.
     */
    private String getNamespace(AnyAttribute[] anyAttributes) {

        // Use bollean variabel to check if each any attribute has "namespace" value "##any"
        boolean any = true;

        // Get a set containing all namespaces contained in the any attributes
        LinkedHashSet<String> namespaces = new LinkedHashSet<String>();

        // For each any attribute check the contained "namespace" attribute
        for (int i = 0; i < anyAttributes.length; i++) {
            AnyAttribute anyAttribute = anyAttributes[i];

            if (anyAttribute != null) {

                // If any attribute contains "namespace" attribute check contained values else add "##any" to the namespace
                if (anyAttribute.getNamespace() != null) {

                    // Get current "namespace" attribute
                    String namespace = anyAttribute.getNamespace();

                    // If "namespace" attribute equals "##any" or "##other" or "##local" add namespace to namespace set
                    if (namespace.equals("##any") || namespace.equals("##other") || namespace.equals("##local")) {
                        namespaces.add(namespace);
                    } else if (namespace.equals("##targetNamespace")) {

                        // If "namespace" attribute equals "##targetNamespace" get the target namespace of the containing schema and add it to the namespace set
                        namespaces.add(anyAttributeOldSchemaMap.get(anyAttribute).getTargetNamespace());
                    } else {

                        // If the "namespace" attribute contains a list of namespaces split the string and add namespaces to the namespace set
                        for (String currentNamespace : namespace.split(" ")) {

                            // If current namespace equals "##local" or an URI add the namespace to the set
                            if (currentNamespace.equals("##local") || !currentNamespace.equals("##targetNamespace")) {
                                namespaces.add(currentNamespace);

                            } else if (currentNamespace.equals("##targetNamespace")) {

                                // For a current namespace with value "##targetNamespace" get the target namespace of the containing schema and add it to the namespace set
                                namespaces.add(anyAttributeOldSchemaMap.get(anyAttribute).getTargetNamespace());
                            }
                        }
                    }
                } else {

                    // If any attribute has no "namespace" attribute the default values is "##any"
                    namespaces.add("##any");
                }

                // If the current any attribute contains a "namespace" attribute which contains no "##any" the any attribute has no "##any" value
                if (anyAttribute.getNamespace() != null && !anyAttribute.getNamespace().contains("##any")) {
                    any = false;
                }
            }
        }

        // If each any attribute contained an "##any" value return "##any" as value of the any attribute
        if (any) {
            return "##any";
        }

        // Check for each "namespace" if each any attribute contains it
        for (Iterator<String> it = namespaces.iterator(); it.hasNext();) {
            String namespace = it.next();
            for (int i = 0; i < anyAttributes.length; i++) {
                AnyAttribute anyAttribute = anyAttributes[i];

                if (anyAttribute != null && namespaces.contains(namespace)) {

                    // Set namespace of the any attribute to the default value
                    if (anyAttribute.getNamespace() == null) {
                        anyAttribute.setNamespace("##any");
                    }

                    // If current "namespace" is "##any" it can be removed (as it was checked beforehand)
                    if (namespace.equals("##any")) {
                        it.remove();
                    } else if (namespace.equals("##other")) {

                        // If current "namespace" is "##other" and if current any attribute contains no "namespace" with "##any" or "##other" remove it
                        if (anyAttribute.getNamespace() != null && !anyAttribute.getNamespace().contains("##any") && !anyAttribute.getNamespace().contains("##other")) {
                            it.remove();
                        }
                    } else if (namespace.equals("##local")) {

                        // If current "namespace" is "##local" and if current any attribute contains no "namespace" with "##any" or "##local", remove it
                        if (anyAttribute.getNamespace() != null && !anyAttribute.getNamespace().contains("##any") && !anyAttribute.getNamespace().contains("##local")) {
                            it.remove();
                        }
                    } else {

                        // If current "namespace" has any other value and if current any attribute contains no "namespace" with "##any"or the "namespace" value itself or "other", when the target namespace is not the specified namespace, remove it
                        if (anyAttribute.getNamespace() != null && !anyAttribute.getNamespace().contains("##any") && !anyAttribute.getNamespace().contains(namespace) && !(anyAttribute.getNamespace().contains("##targetNamespace") && anyAttributeOldSchemaMap.get(anyAttribute).getTargetNamespace().equals(namespace)) && !((anyAttribute.getNamespace().contains("##other") && !anyAttributeOldSchemaMap.get(anyAttribute).getTargetNamespace().equals(namespace)))) {
                            it.remove();
                        }
                    }
                }
            }
        }

        // After removing all "namespace" values not contained in each any attribute check if namespace set contains "##other"
        if (namespaces.contains("##other")) {

            // For each any attribute check if the new "namespace" attribute will have value "##other"
            for (int i = 0; i < anyAttributes.length; i++) {
                AnyAttribute anyAttribute = anyAttributes[i];

                // If an "##other" valued "namepace" with same target namespace exist return "##other"
                if (anyAttribute.getNamespace() != null && anyAttribute.getNamespace().contains("##other") && anyAttributeOldSchemaMap.get(anyAttribute).getTargetNamespace().equals(outputSchema.getTargetNamespace())) {
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
     * This method creates a new annotation for a given list of attribute
     * particles. Each attribute particle may contain an annotation, these
     * annotations are used to contstruct the new annotation, which contains the
     * app infos and documentations of the old annotations.
     *
     * @param attributeParticles Set of attribute particles, which is used to
     * construct a new attribute particle. This attribute particle will contain
     * the new annotation.
     * @return New Annotation, which contains the information stored in the
     * old annotations contained in the specified attribute particles.
     */
    private Annotation getAnnotation(LinkedHashSet<AttributeParticle> attributeParticles) {

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
     * This method generates an new any attribute as result of the intersection
     * of specified any attributes. If the intersection is empty a null pointer
     * is returned, this is the case when the "namespace" attributes of 
     * different any attributes have no intersection or if an entry of the 
     * array is null.
     *
     * @param anyAttributes Array of any attributes, which are intersected to 
     * generate the new any attribute.
     * @return New any attribute, if an intersection is possible and if not a 
     * null pointer otherwise.
     */
    private AnyAttribute generateNewAnyAttribute(AnyAttribute[] anyAttributes) {

        // Use any attribute set, which contains no null refernces for other methods 
        LinkedHashSet<AttributeParticle> anyAttributesSet = new LinkedHashSet<AttributeParticle>();

        // For each any attribute contained in the array check if the attribute is null
        for (int i = 0; i < anyAttributes.length; i++) {
            AnyAttribute anyAttribute = anyAttributes[i];

            // If array contains null return null else add array entry to any attribute set
            if (anyAttribute == null) {
                return null;
            } else {
                anyAttributesSet.add(anyAttribute);
            }
        }

        // Generate new "namespace" attribute
        String newNamespace = getNamespace(anyAttributes);

        // If new "namespace" attribute is empty return null else create new any attribute
        if (newNamespace.equals("")) {
            return null;
        } else {

            // Create new any attribute with new "processContents" and "ID" attributes and with new annotation
            AnyAttribute newAnyAttribute = new AnyAttribute(ProcessContentsInstruction.Skip, newNamespace);
            newAnyAttribute.setAnnotation(getAnnotation(anyAttributesSet));
            newAnyAttribute.setId(getID(anyAttributesSet));
            return newAnyAttribute;
        }
    }

    /**
     * This method generates a new ProcessContentsInstruction from a specified
     * array of any attributes. If an any attribute has "strict" as value of its
     * "processContents" attribute a validator must validate this any attribute.
     * So if a single any attribute of the specified attributes has
     * "processContents" value "strict" the new ProcessContentsInstruction is 
     * "strict" as well. Analog for "lax" and "skip", which is returned if no
     * any attribute has ProcessContentsInstruction "strict" or "lax".
     *
     * @param anyAttributes Array of any attributes, each any attribute has a
     * ProcessContentsInstruction.
     * @return New ProcessContentsInstruction if any any attribute has
     * ProcessContentsInstruction "strict", "strict" is returned, then "lax" and 
     * then "skip".
     */
    private ProcessContentsInstruction getNewProcessContentsInstruction(AnyAttribute[] anyAttributes) {

        // Use variable to check if "processContents" attribute with value "lax" exists 
        boolean lax = false;

        // For each any attribute 
        for (int i = 0; i < anyAttributes.length; i++) {
            AnyAttribute anyAttribute = anyAttributes[i];

            // If the "processContents" attribute is present in the any attribute and "strict" or if the "processContents" attribute is not present (Default: "strict") return "strict"
            if (anyAttribute.getProcessContentsInstruction() == ProcessContentsInstruction.Strict || anyAttribute.getProcessContentsInstruction() == null) {
                return ProcessContentsInstruction.Strict;
            } else if (anyAttribute.getProcessContentsInstruction() == ProcessContentsInstruction.Lax) {

                // If the existing "processContents" attribute is "lax" set variable to true
                lax = true;
            }
        }

        // If any "processContents" attribute is "lax" and non is "strict" return lax else if no "strict" and "lax" values are present return "skip"
        if (lax) {
            return ProcessContentsInstruction.Lax;
        } else {
            return ProcessContentsInstruction.Skip;
        }
    }

    /**
     * The generateNewTopLevelAttribute method creates a new top-level
     * attribute. This attribute is the result of an intersection of the
     * specified attribute set. If the intersection is empty a null pointer is
     * returned, this would be the case if not all attributes in the attribute
     * set would have the same names or if different attributes would have
     * different "fixed" values. As with all top-level defined attributes the
     * new attribute is registered in a SymbolTable, in this case the attribute
     * SymbolTable of the output schema.
     *
     * @param oldAttributes Set of all attributes involved in the intersection.
     * @return Attribute if intersection of attributes is not empty, a null
     * pointer otherwise.
     */
    private Attribute generateNewTopLevelAttribute(LinkedHashSet<Attribute> oldAttributes) {

        // Check if new top-level attribute can be computed (This is the case if all top-level attributes contain the same name and no different "fixed" attributes are contained in different top-level attributes)
        if (areIntersectable(oldAttributes)) {

            // Initialize set of simpleTypes contained by the attributes of the attribute set
            LinkedHashSet<SimpleType> containedSimpleTypes = new LinkedHashSet<SimpleType>();

            // Get simpleType of each attribute contained in the set and add it to the simpleType set
            for (Iterator<Attribute> it = oldAttributes.iterator(); it.hasNext();) {
                Attribute attribute = it.next();
                containedSimpleTypes.add(attribute.getSimpleType());
            }

            // Generate new type intersection generator and build new simpleType
            SimpleType simpleType = typeIntersectionGenerator.generateNewSimpleType(containedSimpleTypes);

            // Get SymbolTableRef, which links to the new type (if the type could be constructed)
            SymbolTableRef<Type> newSimpleTypeRef = outputSchema.getTypeSymbolTable().getReference(simpleType.getName());

            // Check if type was constructed, if not return null
            if (newSimpleTypeRef.getReference() != null) {

                // Compute different Attribute fields
                String newName = oldAttributes.iterator().next().getName();
                String newDefault = getDefault(new LinkedHashSet<AttributeParticle>(oldAttributes), true);
                String newFixed = getFixed(new LinkedHashSet<AttributeParticle>(oldAttributes));
                String newID = getID(new LinkedHashSet<AttributeParticle>(oldAttributes));
                boolean newTypeAttribute = newSimpleTypeRef.getReference().isAnonymous();
                Annotation newAnnotation = getAnnotation(new LinkedHashSet<AttributeParticle>(oldAttributes));

                // Create new top-level attribute with the fields constructed beforehand
                Attribute newTopLevelAttribute = new Attribute(newName, newSimpleTypeRef, newDefault, newFixed, AttributeUse.Optional, newTypeAttribute, null, newAnnotation);
                newTopLevelAttribute.setId(newID);

                // Update attribute SymbolTable of the output schema with the new top-level attribute
                outputSchema.getAttributeSymbolTable().updateOrCreateReference(newTopLevelAttribute.getName(), newTopLevelAttribute);

                // Check if output schema contains attribute already and if not add top-level attribute to the list of top-level attributes
                if (!outputSchema.getAttributes().contains(newTopLevelAttribute)) {
                    outputSchema.addAttribute(outputSchema.getAttributeSymbolTable().getReference(newTopLevelAttribute.getName()));
                }

                // Return complete top-level attribute
                return newTopLevelAttribute;
            } else {

                // No new top-level attribute was constructed
                return null;
            }
        } else {

            // No new top-level attribute was constructed
            return null;
        }
    }

    /**
     * If no attribute particle in a list of attribute particles has
     * AttributeUse "required" the list of attribute particles is optional,
     * meaning null is a valid result of the intersection of only optional
     * attribute particle lists.
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

    /**
     * The method returns the intersection of different "use" values. These
     * values are contained in the specified attribute particles. Because no
     * attribute particle can have use "required" and "prohibited" the
     * intersection of these two leads to no attribute.
     *
     * This method ignores attribute groups, attribute group references and any
     * attributes and should only be used for attributes and attribute
     * references.
     *
     * @param attributeParticles Set of attributes and attribute references.
     * These attribute particles contain "use" values, which are used to compute
     * the new "use" value.
     * @return An AttributeUse value, depending on what "use" values are
     * contained in the attribute particle set. If both "required" and
     * "prohibited" are present a null pointer is returned, signaling an empty
     * intersection.
     */
    private AttributeUse getUse(LinkedHashSet<AttributeParticle> attributeParticles) {

        // Use boolean variable to check if both "required" and "prohibited" values are used for "use" attributes
        boolean required = false;
        boolean prohibited = false;

        // Check for each specified attribute particle if it contains "use" value and store the value
        for (Iterator<AttributeParticle> it = attributeParticles.iterator(); it.hasNext();) {
            AttributeParticle attributeParticle = it.next();

            // If attribute particle is attribute or attribute reference
            if (attributeParticle instanceof Attribute) {
                Attribute attribute = (Attribute) attributeParticle;

                // If attribute contains "use" value "required" set corresponding variable to true
                if (attribute.getUse() == AttributeUse.Required) {
                    required = true;
                }
                // If attribute contains "use" value "prohibited" set corresponding variable to true
                if (attribute.getUse() == AttributeUse.Prohibited) {
                    prohibited = true;
                }
            } else if (attributeParticle instanceof AttributeRef) {
                AttributeRef attributeRef = (AttributeRef) attributeParticle;

                // If attribute reference contains "use" value "required" set corresponding variable to true
                if (attributeRef.getUse() == AttributeUse.Required) {
                    required = true;
                }
                // If attribute reference contains "use" value "prohibited" set corresponding variable to true
                if (attributeRef.getUse() == AttributeUse.Prohibited) {
                    prohibited = true;
                }
            }
        }

        // If "use" attributes contained both "prohibited" and "required" values return null
        if (prohibited && required) {
            return null;
        } else if (prohibited) {

            // If only "prohibited" was used as value of the "use" attribute return "prohibited"
            return AttributeUse.Prohibited;
        } else if (required) {

            // If only "required" was used as value of the "use" attribute return "required"
            return AttributeUse.Required;
        }

        // Return per default "optional"
        return AttributeUse.Optional;
    }

    /**
     * Constructs an intersection of "default" values. If all attribute
     * particles contain the same "default" value and attribute use is optional,
     * for the attribute particle resulting from the intersection, a string with
     * this value is returned, otherwise a null pointer is returned.
     *
     * This method ignores attribute groups, attribute group references and any
     * attributes and should only be used for attributes and attribute
     * references. Attribute references in specific are allowed to have own
     * "default" values different from the referred attribute.
     *
     * @param attributeParticles Set of attribute particles which contains
     * "default" values.
     * @param optional Boolean signaling if the resulting attribute particle can
     * be used optionally. Is this not the case a "default" value is not
     * necessary because the "default" value is used when the attribute is not
     * present.
     * @return String representing the new "default" value or null pointer if
     * intersection is empty.
     */
    private String getDefault(LinkedHashSet<AttributeParticle> attributeParticles, boolean optional) {

        // Initialize new "default" attribute, which is null per default
        String newDefault = null;

        // For each given attribute particle check if it contains a "default" attribute
        for (Iterator<AttributeParticle> it = attributeParticles.iterator(); it.hasNext();) {
            AttributeParticle attributeParticle = it.next();

            // If attribute particle is attribute or attribute reference check if attribute, attribute reference or referred attribute contains "default" attribute
            if (attributeParticle instanceof Attribute) {
                Attribute attribute = (Attribute) attributeParticle;

                // Return null if the new attribute is not optional or if the current attribute contains no "default" attribute or if the current "default" value is different from the stored "default" value
                if (!optional || attribute.getDefault() == null || newDefault != null && !newDefault.equals(attribute.getDefault())) {
                    return null;
                } else {
                    newDefault = attribute.getDefault();
                }
            } else if (attributeParticle instanceof AttributeRef) {
                AttributeRef attributeRef = (AttributeRef) attributeParticle;

                // Return null if the new attribute is not optional or if the current attribute and attribute reference contains no "default" attribute or if the current "default" value is different from the stored "default" value
                if (!optional || attributeRef.getDefault() == null && attributeRef.getAttribute().getDefault() == null || newDefault != null && !newDefault.equals(attributeRef.getDefault()) && !newDefault.equals(attributeRef.getAttribute().getDefault())) {
                    return null;
                } else {

                    // Set new "default" value to the value contained in the attribute reference or the attribute
                    if (attributeRef.getDefault() != null) {
                        newDefault = attributeRef.getDefault();
                    } else if (attributeRef.getAttribute().getDefault() != null) {
                        newDefault = attributeRef.getAttribute().getDefault();
                    }

                }
            }
        }
        return newDefault;
    }

    /**
     * This method constructs the intersection of "fixed" values. If in an
     * attribute the "fixed" value is not present this attribute can assume any
     * value predefined by its type. When there are attributes containing
     * different "fixed" values each attribute is fixed to its own "fixed" value
     * an intersection of these would be empty. So a null pointer is returned in
     * this case.
     *
     * This method ignores attribute groups, attribute group references and any
     * attributes and should only be used for attributes and attribute
     * references.
     *
     * @param attributeParticles Set of attribute particles, each attribute or
     * attribute reference carries a "fixed" value, which is used to compute the
     * new "fixed" value.
     * @return Null if all attribute particles do not contain "fixed" values or
     * if the intersection is empty. A String if all attribute particles contain
     * the same value.
     */
    private String getFixed(LinkedHashSet<AttributeParticle> attributeParticles) {

        // Initialize new "fixed" attribute, which is null per default
        String newFixed = null;

        // Check for each attribute particle if a "fixed" attribute is contained
        for (Iterator<AttributeParticle> it = attributeParticles.iterator(); it.hasNext();) {
            AttributeParticle attributeParticle = it.next();

            // If attribute particle is attribute check "fixed" attribute of the attribute else check "fixed" attribute of the attribute reference or the "fixed" attribute of the referred attribute
            if (attributeParticle instanceof Attribute) {
                Attribute attribute = (Attribute) attributeParticle;

                // Check if attribute contains "fixed" attribute
                if (attribute.getFixed() != null) {

                    // If current "fixed" value is different from the stored "fixed" value return null else store the new "fixed" value
                    if (newFixed != null && !newFixed.equals(attribute.getFixed())) {
                        return null;
                    } else {
                        newFixed = attribute.getFixed();
                    }
                }
            } else if (attributeParticle instanceof AttributeRef) {
                AttributeRef attributeRef = (AttributeRef) attributeParticle;

                // Check if attribute reference or attribute has "fixed" attribute
                if (attributeRef.getFixed() != null || attributeRef.getAttribute().getFixed() != null) {

                    // If current "fixed" value in the attribute reference or the referred attribute is different from the stored "fixed" value return null
                    if (newFixed != null && !newFixed.equals(attributeRef.getFixed()) && !newFixed.equals(attributeRef.getAttribute().getFixed())) {
                        return null;
                    } else {

                        // Set new "fixed" value to the value contained in the attribute reference or the attribute
                        if (attributeRef.getFixed() != null) {
                            newFixed = attributeRef.getFixed();
                        } else if (attributeRef.getAttribute().getFixed() != null) {
                            newFixed = attributeRef.getAttribute().getFixed();
                        }
                    }
                }
            }
        }
        return newFixed;
    }

    /**
     * Checks if a set of top-level attributes is intersectable. This is the
     * case if all attributes share the same name and contain the same "fixed"
     * value or non. For attributes with different names an intersection would
     * be empty and thus unnecessary. If the "fixed" values would differ, each
     * attribute would only be able to assume its own value an intersection of
     * these attributes would be empty.
     *
     * @param topLevelAttributes Set of attributes for which is checked if an
     * intersection is necessary.
     * @return <tt>true</tt> if the attribute set can be intersected.
     */
    private boolean areIntersectable(LinkedHashSet<Attribute> topLevelAttributes) {

        // Uses strings to store attribute names and "fixed" attribute of previous top-level attribute
        String attributeName = null;
        String fixedValue = null;

        // Check for each top-level attribute if the name and "fixed" value equals the last top-level element
        for (Iterator<Attribute> it = topLevelAttributes.iterator(); it.hasNext();) {
            Attribute attribute = it.next();

            // Check if the top-level attribute contains an equal name as the last top-level attribute or if it is the first top-level attribute
            if (attributeName != null && !attribute.getName().equals(attributeName)) {

                // Return false if top-level attributes with different names exist
                return false;
            }

            // Set attribute name for the next top-level attribute
            attributeName = attribute.getName();

            // Check if current top-level attribute contains "fixed" value
            if (attribute.getFixed() != null) {

                // Check if the top-level attribute contains the same "fixed" value as a previous top-level attribute or if it is the first top-level attribute with an "fixed" attribute
                if (fixedValue != null && !fixedValue.equals(attribute.getFixed())) {
                    return false;
                }

                // Set "fixed" attribute for the following top-level attribute
                fixedValue = attribute.getFixed();
            }
        }
        return true;
    }

    /**
     * Returns all attribute particles, except attribute group references, which
     * are contained in the specified attribute group. So the list contains
     * attributes, attribute references and possible an any attribute, only one
     * is allowed per attribute content.
     *
     * @param attributeGroup Is the attribute group, which is searched for
     * attribute particles.
     * @return List of contained attribute particles, with the exception of
     * attribute group references and attribute groups, which are not allowed to
     * be contained in attribute groups.
     */
    private LinkedList<AttributeParticle> getContainedAttributeParticles(AttributeGroup attributeGroup) {

        // Initialize list of attribute particles contained in the attribute group
        LinkedList<AttributeParticle> containedAttributeParticles = new LinkedList<AttributeParticle>();

        // Check all attribute particles contained in the current attribute group
        for (Iterator<AttributeParticle> it = attributeGroup.getAttributeParticles().iterator(); it.hasNext();) {
            AttributeParticle attributeParticle = it.next();

            // If attribute particle is attribute group reference check the referred group for attribute particles else add attribute particle to the list
            if (attributeParticle instanceof AttributeGroupRef) {
                AttributeGroup currentAttributeGroup = ((AttributeGroupRef) attributeParticle).getAttributeGroup();

                // Check if the referred attribute group contains attribute particles, which then are added to the list
                containedAttributeParticles.addAll(getContainedAttributeParticles(currentAttributeGroup));
            } else {
                containedAttributeParticles.add(attributeParticle);
            }
        }
        return containedAttributeParticles;
    }

    /**
     * The getID method generates an intersection of specified "IDs." The result
     * is a new "ID" which is used by the attribute particle resulting from the
     * intersection of given attribute particles. If two attribute particles
     * have different "ID" values the intersection is empty and a null pointer
     * is returned.
     *
     * This method ignores attribute groups, attribute group references and
     * any attributes and should only be used for attributes and attribute
     * references. Attribute references in specific are allowed to have own
     * "ID"s different from the referred attribute.
     *
     * @param attributeParticles Set of AttributeParticles from which the new
     * "ID" is calculated.
     * @return Null if the intersection is empty or a String representing the
     * "ID" attribute if all attribute particles carry the same "ID" value.
     */
    private String getID(LinkedHashSet<AttributeParticle> attributeParticles) {

        // Initialize new "ID" attribute
        String newID = null;

        // Check for each given attribute particle if an "ID" attribute is contained
        for (Iterator<AttributeParticle> it = attributeParticles.iterator(); it.hasNext();) {
            AttributeParticle attributeParticle = it.next();

            // Check if attribute is attribute reference or attribute
            if (attributeParticle instanceof Attribute || attributeParticle instanceof AttributeRef) {

                // If attribute particle contains no "ID" attribte or if contained "ID" attribute is different from the new "ID" attribute return null else store the contained "ID" attribute in the new "ID" attribute
                if (attributeParticle.getId() == null || attributeParticle.getId().equals("") || !newID.equals(attributeParticle.getId()) && !newID.equals("")) {
                    return null;
                } else {
                    newID = attributeParticle.getId();
                }
            }
        }
        return newID;
    }

    /**
     * Computes the a new "form" value from different attribute particles. Each
     * attribute or attribute reference contains directly or indirectly a "form"
     * value, which is used to generate the new form value.
     *
     * This method ignores attribute groups, attribute group references and
     * any attributes and should only be used for attributes and attribute
     * references.
     *
     * @param attributeParticles Set of AttributeParticles. Both attributes and
     * attribute references are used to compute the new from value. Attributes
     * directly contain form attributes where attribute referencess refer to
     * top-level defined attributes which are generally qualified.
     * @return Null if no intersection was possible i.e. intersection of 
     * qualified and unqualified. Qualification if all attribute particles
     * contain the same "form" value.
     */
    private Qualification getForm(LinkedHashSet<AttributeParticle> attributeParticles) {

        // Initialize new "form" attribute, which is null per default
        Qualification newForm = null;

        // Check for each given attribute particle if a "form" attribute is contained
        for (Iterator<AttributeParticle> it = attributeParticles.iterator(); it.hasNext();) {
            AttributeParticle attributeParticle = it.next();

            // Check if attribute particle is attribute or attribute reference
            if (attributeParticle instanceof Attribute) {
                Attribute attribute = (Attribute) attributeParticle;

                // If attribute has no "form" attribute or if current "form" attribute is not equivalent to the new "form" attribute return null else store current "form" attribute in new "form" attribute
                if (attribute.getForm() == null || newForm != null && newForm != attribute.getForm()) {
                    return null;
                } else {
                    newForm = attribute.getForm();
                }
            } else if (attributeParticle instanceof AttributeRef) {

                // If attribute particle is attribute reference and if current "form" attribute is not equivalent to the new "form" attribute return null else store current "form" attribute in new "form" attribute
                if (newForm != null && newForm != Qualification.qualified) {
                    return null;
                } else {
                    newForm = Qualification.qualified;
                }
            }
        }
        return newForm;
    }
}
