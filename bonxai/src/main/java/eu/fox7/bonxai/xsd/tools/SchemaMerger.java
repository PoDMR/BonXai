package eu.fox7.bonxai.xsd.tools;

import eu.fox7.bonxai.common.AllPattern;
import eu.fox7.bonxai.common.Annotation;
import eu.fox7.bonxai.common.AnyAttribute;
import eu.fox7.bonxai.common.AnyPattern;
import eu.fox7.bonxai.common.ChoicePattern;
import eu.fox7.bonxai.common.CountingPattern;
import eu.fox7.bonxai.common.GroupRef;
import eu.fox7.bonxai.common.IdentifiedNamespace;
import eu.fox7.bonxai.common.Particle;
import eu.fox7.bonxai.common.ParticleContainer;
import eu.fox7.bonxai.common.SequencePattern;
import eu.fox7.bonxai.common.SymbolTableRef;
import eu.fox7.bonxai.xsd.*;
import eu.fox7.bonxai.xsd.Element.*;
import eu.fox7.bonxai.xsd.XSDSchema.*;

import java.util.*;

/**
 * The SchemaMerger class is used to merge different schemata. The result is a
 * new schema containing all components of given schemata. In order to merge
 * schemata these schemata must not contain conflicting components. For schemata
 * appear below the same parent schema this is true because every schema
 * component may be referenced by an unique name.
 *
 * @author Dominik Wolff
 */
public class SchemaMerger {

    // New schema which should contain the result of the merging process.
    XSDSchema newSchema;

    /**
     * This method computes a single output schema for a given set of schemata.
     * In order to merge schemata the schema-components have to be disjunct.
     * If this is not the case the schema merger will not work properly.
     * XSDSchema default values will not be resolved in the schemata of the schema
     * set, so further use of these schemata is possible.
     *
     * @param mergingSchemata Set of schemata. All whom are used to compute a
     * schema validating all XML instances which are valid for one of these
     * schemata.
     * @param targetNamespace New target namespace of the new schema.
     * @return New schema which is the result of merging all specified schemata.
     */
    public XSDSchema mergeSchemata(LinkedHashSet<XSDSchema> mergingSchemata, String targetNamespace) {

        // Generate new schema with given targetNamespace.
        newSchema = new XSDSchema(targetNamespace);

        // Add all schema components of each merging schema to the new schema
        for (Iterator<XSDSchema> it = mergingSchemata.iterator(); it.hasNext();) {
            XSDSchema oldSchema = it.next();

            // Add namespaces from old schema to the new schema
            addNamespaces(newSchema, oldSchema);

            // Add ForeignSchemata (Import/Include/Redefine) of the old schema to the new schema
            addForeignSchemas(newSchema, oldSchema, mergingSchemata);

            // Get a set of all top-level attribute groups contained in the old schema (top-level because these attribute groups are not contained in a redefine-component)
            LinkedHashSet<AttributeGroup> topLevelAttributeGroups = new LinkedHashSet<AttributeGroup>(oldSchema.getAttributeGroups());

            // Generate a new attribute group in the new schema for each top-level attribute group contained in the old schema.
            for (Iterator<AttributeGroup> it2 = topLevelAttributeGroups.iterator(); it2.hasNext();) {
                AttributeGroup topLevelAttributeGroup = it2.next();

                // Generate new attribute group in new schema equivalent to the current top-level attribute group
                createNewTopLevelAttributeGroup(topLevelAttributeGroup, oldSchema);
            }

            // Get a set of all top-level attributes contained in the old schema
            LinkedHashSet<Attribute> topLevelAttributes = new LinkedHashSet<Attribute>(oldSchema.getAttributes());

            // Generate a new attribute in the new schema for each top-level attribute contained in the old schema.
            for (Iterator<Attribute> it2 = topLevelAttributes.iterator(); it2.hasNext();) {
                Attribute topLevelAttribute = it2.next();

                // Generate new attribute in new schema equivalent to the current top-level attribute
                createNewTopLevelAttribute(topLevelAttribute, oldSchema);
            }

            // Get a set of all top-level elements contained in the old schema
            LinkedHashSet<Element> topLevelElements = new LinkedHashSet<Element>(oldSchema.getElements());

            // Generate a new element in the new schema for each top-level element contained in the old schema.
            for (Iterator<Element> it2 = topLevelElements.iterator(); it2.hasNext();) {
                Element topLevelElement = it2.next();

                // Generate new element in new schema equivalent to the current top-level element
                createNewTopLevelElement(topLevelElement, oldSchema);
            }

            // Get a set of all top-level groups contained in the old schema
            LinkedHashSet<Group> topLevelGroups = new LinkedHashSet<Group>(oldSchema.getGroups());

            // Generate a new group in the new schema for each  top-level group contained in the old schema.
            for (Iterator<Group> it2 = topLevelGroups.iterator(); it2.hasNext();) {
                Group topLevelGroup = it2.next();

                // Generate new group in new schema equivalent to the current top-level group
                createNewTopLevelGroup(topLevelGroup, oldSchema);
            }

            // Get a set of all top-level types contained in the old schema
            LinkedHashSet<Type> topLevelTypes = new LinkedHashSet<Type>(oldSchema.getTypes());

            // Generate a new type in the new schema for each top-level type contained in the old schema.
            for (Iterator<Type> it2 = topLevelTypes.iterator(); it2.hasNext();) {
                Type topLevelType = it2.next();

                // Generate new type in new schema equivalent to the current top-level type
                createNewTopLevelType(topLevelType, oldSchema);
            }
        }

        // After creating all top-level components the new schema is complete.
        return newSchema;
    }

    /**
     * This method adds all namespaces and abbreviations from the old schema to
     * the new schema. This is necessary to reference schema components with
     * foreign namespaces.
     *
     * @param newSchema XSDSchema to which the namespaces and abbreviations stored
     * in IdentifiedNamespaces are added.
     * @param oldSchema XSDSchema from which the namespaces and
     * abbreviations in the form of identifiedNamespaces are taken.
     */
    public void addNamespaces(XSDSchema newSchema, XSDSchema oldSchema) {

        // Get a set of all namespaces and abbreviations used in the current schema
        LinkedHashSet<IdentifiedNamespace> namespaces = oldSchema.getNamespaceList().getIdentifiedNamespaces();

        // Add all namespaces and abbreviations to the new schema
        for (Iterator<IdentifiedNamespace> it2 = namespaces.iterator(); it2.hasNext();) {
            IdentifiedNamespace namespace = it2.next();

            // If no abbreviation for current namespace exists add IdentifiedNamespace to the new schema (An IdentifiedNamespace contains a namespaces and an abbreviation for this namespace).
            if (newSchema.getNamespaceList().getNamespaceByUri(namespace.getUri()).getIdentifier() == null) {

                // Create new IdentifiedNamespace and add it to the namespace set
                IdentifiedNamespace identifiedNamespace = new IdentifiedNamespace(namespace.getIdentifier(), namespace.getUri());
                newSchema.getNamespaceList().addIdentifiedNamespace(identifiedNamespace);
            }
        }
    }

    /**
     * This method adds the ForeignSchemata of the old schema to the new schema.
     * In order for the the new schema to reference schema components which are
     * referenced in the old schema these ForeignSchemata have to be present in
     * the new schema.
     *
     * @param newSchema XSDSchema to which the foreign schemata of the
     * old schema are added.
     * @param oldSchema XSDSchema providing the foreign schemata added to the new
     * schema.
     * @param mergingSchemata A set of all schemata merged to create the new
     * schema.
     */
    public void addForeignSchemas(XSDSchema newSchema, XSDSchema oldSchema, LinkedHashSet<XSDSchema> mergingSchemata) {

        // Get lists of contained ForeignSchemata for new schema and old schema.
        LinkedList<ForeignSchema> foreignSchemataNewSchema = newSchema.getForeignSchemas();
        LinkedList<ForeignSchema> foreignSchemataOldSchema = oldSchema.getForeignSchemas();

        // Generate set containing all schema locations
        HashSet<String> schemaLocations = new HashSet<String>();

        // Add for each schema a location to the set
        for (Iterator<XSDSchema> it2 = mergingSchemata.iterator(); it2.hasNext();) {
            XSDSchema mergingSchema = it2.next();
            schemaLocations.add(mergingSchema.getSchemaLocation());
        }

        // Add each ForeignSchema contained in the list of the old schema to the list of the new schema.
        for (Iterator<ForeignSchema> it = foreignSchemataOldSchema.iterator(); it.hasNext();) {
            ForeignSchema foreignSchemaOldSchema = it.next();

            // Check wether the ForeignSchema references to one of the specified schemata (If this is the case the ForeignSchema is not added to the new schema)
            if (!(foreignSchemaOldSchema.getSchema() != null && schemaLocations.contains(foreignSchemaOldSchema.getSchema().getSchemaLocation()))) {

                //Create new ForeignSchema
                ForeignSchema foreignSchema = null;

                if (foreignSchemaOldSchema instanceof IncludedSchema) {

                    // If the ForeignSchema of the old schema is an IncludedSchema create new IncludedSchema
                    foreignSchema = new IncludedSchema(foreignSchemaOldSchema.getSchemaLocation());

                } else if (foreignSchemaOldSchema instanceof RedefinedSchema) {

                    // If the ForeignSchema of the old schema is an RedefinedSchema create new RedefinedSchema
                    RedefinedSchema oldRedefinedSchema = (RedefinedSchema) foreignSchemaOldSchema;
                    RedefinedSchema newRedefinedSchema = new RedefinedSchema(oldRedefinedSchema.getSchemaLocation());

                    // Get attribute groups stored in the old redefine component
                    LinkedHashSet<AttributeGroup> oldAttributeGroups = (LinkedHashSet<AttributeGroup>) oldRedefinedSchema.getAttributeGroups();

                    // For each redefined attribute group of the old redefine component a new redefined attribute group is added to the new RedefinedSchema
                    for (Iterator<AttributeGroup> it2 = oldAttributeGroups.iterator(); it2.hasNext();) {
                        AttributeGroup oldAttributeGroup = it2.next();

                        // Create new attribute group from the old attribute group and add it to the RedefinedSchema
                        createNewAttributeGroup(oldAttributeGroup, oldSchema);
                        newRedefinedSchema.addAttributeGroup(newSchema.getAttributeGroupSymbolTable().getReference(oldAttributeGroup.getName()));
                    }

                    // Store groups of the old RedefinedSchema in a variable
                    LinkedHashSet<Group> oldGroups = (LinkedHashSet<Group>) oldRedefinedSchema.getGroups();

                    // Create for each group of the old RedefinedSchema a new group in the new RedefinedSchema
                    for (Iterator<Group> it2 = oldGroups.iterator(); it2.hasNext();) {
                        Group oldGroup = it2.next();

                        // Add new group to the new RedefinedSchema
                        createNewGroup(oldGroup, oldSchema);
                        newRedefinedSchema.addGroup(newSchema.getGroupSymbolTable().getReference(oldGroup.getName()));
                    }

                    // Get types stored in the old redefine component
                    LinkedHashSet<Type> oldTypes = (LinkedHashSet<Type>) oldRedefinedSchema.getTypes();

                    // Create for each type of the old RedefinedSchema a new type in the new RedefinedSchema
                    for (Iterator<Type> it2 = oldTypes.iterator(); it2.hasNext();) {
                        Type oldType = it2.next();

                        // Add new type to the new RedefinedSchema
                        createNewType(oldType, oldSchema);
                        newRedefinedSchema.addType(newSchema.getTypeSymbolTable().getReference(oldType.getName()));
                    }

                    foreignSchema = newRedefinedSchema;

                } else if (foreignSchemaOldSchema instanceof ImportedSchema) {

                    // If the ForeignSchema of the old schema is an ImportedSchema create new ImportedSchema
                    foreignSchema = new ImportedSchema(((ImportedSchema) foreignSchemaOldSchema).getNamespace(), foreignSchemaOldSchema.getSchemaLocation());
                }

                // Set annotation and id of the new ForeignSchema
                foreignSchema.setAnnotation(createNewAnnotation(foreignSchemaOldSchema.getAnnotation()));
                foreignSchema.setId(foreignSchemaOldSchema.getId());

                // Add ForeignSchema to the list of foreign schemata of the new schema and change parent schema to new schema get schema from the old ForeignSchema
                foreignSchemataNewSchema.add(foreignSchema);
                foreignSchema.setParentSchema(newSchema);
                foreignSchema.setSchema(foreignSchemaOldSchema.getSchema());
            }
        }

        // Set new list of foreign schemata of the new schema in the new schema
        newSchema.setForeignSchemas(foreignSchemataNewSchema);
    }

    /**
     * Creates a new annotation with new appInfos and new documentations by
     * copying the given old annotation.
     *
     * @param oldAnnotation Blueprint for the new annotation.
     * @return New Annotation matching the old annotation.
     */
    private Annotation createNewAnnotation(Annotation oldAnnotation) {

        // Create new annotation
        Annotation newAnnotation = new Annotation();

        // Add id of the old annotation to the new annotation
        newAnnotation.setId(oldAnnotation.getId());

        // Get all old appInfos
        LinkedList<AppInfo> oldAppInfos = oldAnnotation.getAppInfos();

        // For each old appInfo create a new appInfo and add it to the annotation
        for (Iterator<AppInfo> it = oldAppInfos.iterator(); it.hasNext();) {
            AppInfo oldAppInfo = it.next();

            // Create new appInfo and add it to the appInfo list of the new annotation
            newAnnotation.addAppInfos(createNewAppInfo(oldAppInfo));
        }

        // Get all old documentations
        LinkedList<Documentation> oldDocumentations = oldAnnotation.getDocumentations();

        // For each old documentation create a new documentation and add it to the annotation
        for (Iterator<Documentation> it = oldDocumentations.iterator(); it.hasNext();) {
            Documentation oldDocumentation = it.next();

            // Create new documentation and add it to the documentation list of the new annotation
            newAnnotation.addDocumentations(createNewDocumentation(oldDocumentation));
        }

        // Retrun new created annotation
        return newAnnotation;
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
    private AttributeParticle createNewAnyAttribute(AnyAttribute oldAnyAttribute) {

        // Create new any attribute
        AnyAttribute newAnyAttribute = new AnyAttribute(oldAnyAttribute.getProcessContentsInstruction(), oldAnyAttribute.getNamespace());

        // Set new id and annotation for new any attribute
        newAnyAttribute.setAnnotation(createNewAnnotation(oldAnyAttribute.getAnnotation()));
        newAnyAttribute.setId(oldAnyAttribute.getId());

        // Return new any pattern
        return newAnyAttribute;
    }

    /**
     * Creates new any pattern for a given any pattern. New and old any pattern
     * are identical but different objects. AnyPatterns do not contain
     * references to schema components.
     *
     * @param oldAnyPattern Blueprint for the new any pattern.
     * @return New any pattern an exact copy of the specified old any pattern.
     */
    private AnyPattern createNewAnyPattern(AnyPattern oldAnyPattern) {

        // Create new any pattern
        AnyPattern newAnyPattern = new AnyPattern(oldAnyPattern.getProcessContentsInstruction(), oldAnyPattern.getNamespace());

        // Set new id and annotation for new any pattern
        newAnyPattern.setAnnotation(createNewAnnotation(oldAnyPattern.getAnnotation()));
        newAnyPattern.setId(oldAnyPattern.getId());

        // Return new any pattern
        return newAnyPattern;
    }

    /**
     * Create new appInfo after the model of a specified old appInfo.
     *
     * @param oldAppInfo Old appInfo used to create the new appInfo.
     * @return New appInfo containing all information of the specified old
     * appInfo.
     */
    private AppInfo createNewAppInfo(AppInfo oldAppInfo) {

        // Create new appInfo
        AppInfo newAppInfo = new AppInfo();

        // Set new node list for the new appInfo (node list is taken directly from the old appInfo because no operation changes this DOM structure)
        newAppInfo.setNodeList(oldAppInfo.getNodeList());

        // Set new source for the new appInfo
        newAppInfo.setSource(oldAppInfo.getSource());

        return newAppInfo;
    }

    /**
     * Create new attribute as a copy of the specified old attribute. All
     * informations contained in the old attribute are transfered to the new
     * attribute. The new element contains a new type, a new "form" value and so
     * on.
     *
     * @param oldAttribute Attribute used to create the new attribute as an
     * exact copy of the old attribute in the new schema.
     * @param oldSchema XSDSchema containing the old attribute and its type
     * reference.
     * @return New attribute in the new schema created from the specified old
     * attribute.
     */
    private Attribute createNewAttribute(Attribute oldAttribute, XSDSchema oldSchema) {

        // Get SymbolTableRef of the old type
        SymbolTableRef<Type> symbolTableRef = oldSchema.getTypeSymbolTable().getReference(oldAttribute.getSimpleType().getName());

        // If the SymbolTable for types contains no entry for the given simpleType name or contains an entry linking to null a new entry is generated
        // or updated linking to the old simpleType
        if (!newSchema.getTypeSymbolTable().hasReference(symbolTableRef.getKey()) || newSchema.getTypeSymbolTable().getReference(symbolTableRef.getKey()) != null) {
            newSchema.getTypeSymbolTable().updateOrCreateReference(symbolTableRef.getKey(), symbolTableRef.getReference());
        }

        // If the simpleType is anonymous (the simpleType is locally defined and is not referenced elsewhere) a new simpleType is created
        if (oldAttribute.getSimpleType().isAnonymous()) {
            createNewType(oldAttribute.getSimpleType(), oldSchema);
        }

        // Create new Attribute with new type reference
        Attribute newAttribute = new Attribute(oldAttribute.getName());
        newAttribute.setSimpleType(newSchema.getTypeSymbolTable().getReference(oldAttribute.getSimpleType().getName()));

        // Set "use" attribute for new attribute and check wether type is anonymous or not
        newAttribute.setTypeAttr(oldAttribute.getTypeAttr());
        newAttribute.setUse(oldAttribute.getUse());

        // Set id and annotation for the new attribute
        newAttribute.setAnnotation(createNewAnnotation(oldAttribute.getAnnotation()));
        newAttribute.setId(oldAttribute.getId());

        // Set "fixed" and "default" values of the new attribute
        newAttribute.setDefault(oldAttribute.getDefault());
        newAttribute.setFixed(oldAttribute.getFixed());

        // Check if "form" attribute is present in old attribute
        if (oldAttribute.getForm() == null) {

            // Set "form" value to the value of the "attributeFormDefault" attribute
            newAttribute.setForm(oldSchema.getAttributeFormDefault());
        } else {

            // "form" attribute is present add "form" value to new attribute
            newAttribute.setForm(oldAttribute.getForm());
        }

        // Return new attribute after copying all fields of the old attribute
        return newAttribute;
    }

    /**
     * Create new attribute group reference for a given old attribute group
     * reference. The new attribute group  reference is a complete copy of the
     * old attribute group reference. If the reference links to a attribute
     * group in another schema this element will be referred to by* the new
     * attribute group reference as well. If it links to an attribute group of
     * the old schema the new refernce will link to the corresponding attribute
     * group in the current new schema.
     *
     * @param oldAttributeGroupRef Old attribute group reference which is used
     * to construct the new attribute group reference.
     * @param oldSchema XSDSchema containing the specified old attribute group
     * reference and the SymbolTableRef contained in the attribute group
     * reference.
     * @return New attribute group reference linking to the correct attribute
     * group (either a new attribute group in the new schema or an old attribute
     * group in a foreign schema).
     */
    private AttributeGroupRef createNewAttributeGroupRef(AttributeGroupRef oldAttributeGroupRef, XSDSchema oldSchema) {

        // Get SymbolTableRef of the old attribute group reference
        SymbolTableRef<AttributeGroup> symbolTableRef = oldSchema.getAttributeGroupSymbolTable().getReference(oldAttributeGroupRef.getAttributeGroup().getName());

        // If the SymbolTable for attribute group contains no entry for the given attribute group name or contains an entry linking to null a new entry is generated
        // or updated linking to the attribute group specified in the old attribute group reference.
        if (!newSchema.getAttributeGroupSymbolTable().hasReference(symbolTableRef.getKey()) || newSchema.getAttributeGroupSymbolTable().getReference(symbolTableRef.getKey()) != null) {
            newSchema.getAttributeGroupSymbolTable().updateOrCreateReference(symbolTableRef.getKey(), symbolTableRef.getReference());
        }

        // Create new attribute group reference with correct reference stored in the new schema
        AttributeGroupRef newAttributeGroupRef = new AttributeGroupRef(newSchema.getAttributeGroupSymbolTable().getReference(oldAttributeGroupRef.getAttributeGroup().getName()));

        // Set new id and annotation for new attribute group reference
        newAttributeGroupRef.setAnnotation(createNewAnnotation(oldAttributeGroupRef.getAnnotation()));
        newAttributeGroupRef.setId(oldAttributeGroupRef.getId());

        // Return new attribute group reference
        return newAttributeGroupRef;
    }

    /**
     * Create new attribute reference for a given old attribute reference. The
     * new attribute reference is a complete copy of the old attribute
     * reference. If the reference links to a attribute in another schema this
     * attribute will be referred to by the new attribute reference as well. If
     * it links to an attribute of the old schema the new refernce will link to
     * the corresponding attribute in the current new schema.
     *
     * @param oldAttributeRef Old element reference which is used to construct
     * the new attribute reference.
     * @param oldSchema XSDSchema containing the specified old attribute reference
     * and the SymbolTableRef contained in the attribute reference.
     * @return New attribute reference linking to the correct attribute (either
     * a new attribute in the new schema or an old attribute in a foreign
     * schema).
     */
    private AttributeRef createNewAttributeRef(AttributeRef oldAttributeRef, XSDSchema oldSchema) {

        // Get SymbolTableRef of the old attribute reference
        SymbolTableRef<Attribute> symbolTableRef = oldSchema.getAttributeSymbolTable().getReference(oldAttributeRef.getAttribute().getName());

        // If the SymbolTable for attributes contains no entry for the given attribute name or contains an entry linking to null a new entry is generated
        // or updated linking to the attribute specified in the old attribute reference.
        if (!newSchema.getAttributeSymbolTable().hasReference(symbolTableRef.getKey()) || newSchema.getAttributeSymbolTable().getReference(symbolTableRef.getKey()) != null) {
            newSchema.getAttributeSymbolTable().updateOrCreateReference(symbolTableRef.getKey(), symbolTableRef.getReference());
        }

        // Get correct reference stored in the new schema
        AttributeRef newAttributeRef = new AttributeRef(newSchema.getAttributeSymbolTable().getReference(oldAttributeRef.getAttribute().getName()));

        // Set new id and annotation for new attribute reference
        newAttributeRef.setAnnotation(createNewAnnotation(oldAttributeRef.getAnnotation()));
        newAttributeRef.setId(oldAttributeRef.getId());

        // Set "default", "fixed" and "use" values for the new attribute refernce
        newAttributeRef.setDefault(oldAttributeRef.getDefault());
        newAttributeRef.setFixed(oldAttributeRef.getFixed());
        newAttributeRef.setUse(oldAttributeRef.getUse());

        // Return new attribute reference
        return newAttributeRef;
    }

    /**
     * Create new ComplexContent extension by copying the specified old
     * ComplexContent extension. The new ComplexContent extension will contain a
     * new anonymous base type or a reference to a type contained in another
     * schema, this is achieved by copying the SymbolTableRef from the old
     * schema.
     *
     * @param oldComplexContentExtension Old ComplexContent extension used to
     * create the new extension.
     * @param oldSchema XSDSchema containing the old extension.
     * @return New ComplexContent extension which is a copy of the old
     * extension.
     */
    private ComplexContentInheritance createNewComplexContentExtension(ComplexContentExtension oldComplexContentExtension, XSDSchema oldSchema) {

        // Get SymbolTableRef of the old base type
        SymbolTableRef<Type> symbolTableRef = oldSchema.getTypeSymbolTable().getReference(oldComplexContentExtension.getBase().getName());

        // If the SymbolTable for types contains no entry for the given base type name or contains an entry linking to null a new entry is generated
        // or updated linking to the old base type
        if (!newSchema.getTypeSymbolTable().hasReference(symbolTableRef.getKey()) || newSchema.getTypeSymbolTable().getReference(symbolTableRef.getKey()) != null) {
            newSchema.getTypeSymbolTable().updateOrCreateReference(symbolTableRef.getKey(), symbolTableRef.getReference());
        }

        // If the base type is anonymous (the type is locally defined and is not referenced elsewhere) a new type is created
        if (oldComplexContentExtension.getBase().isAnonymous()) {
            createNewType(oldComplexContentExtension.getBase(), oldSchema);
        }

        // Create new ComplexContent extension with new base
        ComplexContentExtension newComplexContentExtension = new ComplexContentExtension(newSchema.getTypeSymbolTable().getReference(oldComplexContentExtension.getBase().getName()));


        // Set new id and annotation in new extension
        newComplexContentExtension.setAnnotation(createNewAnnotation(oldComplexContentExtension.getAnnotation()));
        newComplexContentExtension.setId(oldComplexContentExtension.getId());

        // Get list of all attribute particles contained in the old extension
        LinkedList<AttributeParticle> oldAttributeParticles = oldComplexContentExtension.getAttributes();

        // Create for each attribute particle a new attribute particle and add it to the new extension
        for (Iterator<AttributeParticle> it = oldAttributeParticles.iterator(); it.hasNext();) {
            AttributeParticle oldAttributeParticle = it.next();

            // Add new create attribute particle to the new extension
            newComplexContentExtension.addAttribute(createNewAttributeParticle(oldAttributeParticle, oldSchema));
        }

        // Return new ComplexContent extension
        return newComplexContentExtension;
    }

    /**
     * Create new complexContent inheritance (extension, restriction) from a
     * specified complexContent inheritance by copying the old complexContent
     * inheritance.
     *
     * @param oldComplexContentInheritance Old complexContent inheritance which
     * is copied to create the new complexContent inheritance.
     * @param oldSchema XSDSchema containing the old complexContent inheritance.
     * @return New complexContent inheritance created by copying the old
     * complexContent inheritance.
     */
    private ComplexContentInheritance createNewComplexContentInheritance(ComplexContentInheritance oldComplexContentInheritance, XSDSchema oldSchema) {

        // Create new complexContent inheritance
        ComplexContentInheritance newComplexContentInheritance = null;

        // Check if the old inheritance is an extension
        if (oldComplexContentInheritance instanceof ComplexContentExtension) {

            // Create new complexContent extension
            ComplexContentExtension oldComplexContentExtension = (ComplexContentExtension) oldComplexContentInheritance;
            newComplexContentInheritance = createNewComplexContentExtension(oldComplexContentExtension, oldSchema);

        // Check if the old inheritance is a restriction
        } else if (oldComplexContentInheritance instanceof ComplexContentRestriction) {

            // Create new complexContent restriction
            ComplexContentRestriction oldComplexContentRestriction = (ComplexContentRestriction) oldComplexContentInheritance;
            newComplexContentInheritance = createNewComplexContentRestriction(oldComplexContentRestriction, oldSchema);
        }

        // Return new complexContent inheritance
        return newComplexContentInheritance;
    }

    /**
     * Create new ComplexContent restriction by copying the specified old
     * ComplexContent restriction. The new ComplexContent restriction will
     * contain a new anonymous base type or a reference to a type contained in
     * another schema, this is achieved by copying the SymbolTableRef from the
     * old schema.
     *
     * @param oldComplexContentRestriction Old ComplexContent restriction used
     * to create the new restriction.
     * @param oldSchema XSDSchema containing the old restriction.
     * @return New ComplexContent restriction which is a copy of the old
     * restriction.
     */
    private ComplexContentInheritance createNewComplexContentRestriction(ComplexContentRestriction oldComplexContentRestriction, XSDSchema oldSchema) {

        // Get SymbolTableRef of the old base type
        SymbolTableRef<Type> symbolTableRef = oldSchema.getTypeSymbolTable().getReference(oldComplexContentRestriction.getBase().getName());

        // If the SymbolTable for types contains no entry for the given base type name or contains an entry linking to null a new entry is generated
        // or updated linking to the old base type
        if (!newSchema.getTypeSymbolTable().hasReference(symbolTableRef.getKey()) || newSchema.getTypeSymbolTable().getReference(symbolTableRef.getKey()) != null) {
            newSchema.getTypeSymbolTable().updateOrCreateReference(symbolTableRef.getKey(), symbolTableRef.getReference());
        }

        // If the base type is anonymous (the type is locally defined and is not referenced elsewhere) a new type is created
        if (oldComplexContentRestriction.getBase().isAnonymous()) {
            createNewType(oldComplexContentRestriction.getBase(), oldSchema);
        }

        // Create new ComplexContent restriction with new base
        ComplexContentRestriction newComplexContentRestriction = new ComplexContentRestriction(newSchema.getTypeSymbolTable().getReference(oldComplexContentRestriction.getBase().getName()));


        // Set new id and annotation in new restriction
        newComplexContentRestriction.setAnnotation(createNewAnnotation(oldComplexContentRestriction.getAnnotation()));
        newComplexContentRestriction.setId(oldComplexContentRestriction.getId());

        // Get list of all attribute particles contained in the old restriction
        LinkedList<AttributeParticle> oldAttributeParticles = oldComplexContentRestriction.getAttributes();

        // Create for each attribute particle a new attribute particle and add it to the new restriction
        for (Iterator<AttributeParticle> it = oldAttributeParticles.iterator(); it.hasNext();) {
            AttributeParticle oldAttributeParticle = it.next();

            // Add new create attribute particle to the new restriction
            newComplexContentRestriction.addAttribute(createNewAttributeParticle(oldAttributeParticle, oldSchema));
        }

        // Return new ComplexContent restriction
        return newComplexContentRestriction;
    }

    /**
     * Create new ComplexContentType for a given old ComplexContentType. The new
     * ComplexContentType is an exact copy of the old ComplexContentType,
     * containing all information stored in the old ComplexContentType.
     *
     * @param oldComplexContentType ComplexContentType which is used to create 
     * the new ComplexContentType.
     * @param oldSchema XSDSchema containing the old ComplexContentType.
     * @return New ComplexContentType which is a copy of the specified old
     * ComplexContentType.
     */
    private ComplexContentType createNewComplexContentType(ComplexContentType oldComplexContentType, XSDSchema oldSchema) {

        // Create new ComplexContentType with new particle and new ComplexContentInheritance
        ComplexContentType newComplexContentType = new ComplexContentType(createNewParticle(oldComplexContentType.getParticle(), oldSchema), createNewComplexContentInheritance(oldComplexContentType.getInheritance(), oldSchema));

        // Set new fiels for the new ComplexContentType
        newComplexContentType.setAnnotation(createNewAnnotation(oldComplexContentType.getAnnotation()));
        newComplexContentType.setId(oldComplexContentType.getId());
        newComplexContentType.setMixed(oldComplexContentType.getMixed());

        // Return new ComplexContentType
        return newComplexContentType;
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
    private Type createNewComplexType(ComplexType oldComplexType, XSDSchema oldSchema) {

        // Create new complexType with new content
        ComplexType newComplexType = new ComplexType(oldComplexType.getName(), createNewContent(oldComplexType.getContent(), oldSchema));

        // Set id and annotation for the new element
        newComplexType.setAnnotation(createNewAnnotation(oldComplexType.getAnnotation()));
        newComplexType.setId(oldComplexType.getId());

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
            newComplexType.addAttribute(createNewAttributeParticle(oldAttributeParticle, oldSchema));
        }

        // Check if "block" attribute is present in old element
        if (oldComplexType.getBlockModifiers() == null) {

            // Add "blockDefault" attribute values to new element (resolving "blockDefault" indirectly)
            if (oldSchema.getBlockDefaults().contains(BlockDefault.extension)) {
                newComplexType.addBlockModifier(ComplexTypeInheritanceModifier.Extension);
            }
            if (oldSchema.getBlockDefaults().contains(BlockDefault.restriction)) {
                newComplexType.addBlockModifier(ComplexTypeInheritanceModifier.Restriction);
            }
        } else {

            // "block" attribute is present add "block" value to new element
            newComplexType.setBlockModifiers(oldComplexType.getBlockModifiers());
        }

        // Check if "final" attribute is present in old element
        if (oldComplexType.getFinalModifiers() == null) {

            // Add "finalDefault" attribute values to new element (resolving "finalDefault" indirectly)
            if (oldSchema.getFinalDefaults().contains(FinalDefault.extension)) {
                newComplexType.addFinalModifier(ComplexTypeInheritanceModifier.Extension);
            }
            if (oldSchema.getFinalDefaults().contains(FinalDefault.restriction)) {
                newComplexType.addFinalModifier(ComplexTypeInheritanceModifier.Restriction);
            }
        } else {

            // "final" attribute is present add "final" value to new element
            newComplexType.setFinalModifiers(oldComplexType.getFinalModifiers());
        }

        // Return new complexType
        return newComplexType;
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
    private Constraint createNewConstraint(Constraint oldConstraint, XSDSchema oldSchema) {

        // Create new constraint
        Constraint newConstraint = null;

        // If old constraint is an Key create new Key.
        if (oldConstraint instanceof Key) {

            // Create new Key with the createNewKey method
            Key oldKey = (Key) oldConstraint;
            newConstraint = createNewKey(oldKey);

        // If old constraint is an KeyRef create new KeyRef.
        } else if (oldConstraint instanceof KeyRef) {

            // Create new KeyRef with the createNewKeyRef method
            KeyRef oldKeyRef = (KeyRef) oldConstraint;
            newConstraint = createNewKeyRef(oldKeyRef, oldSchema);

        // If old constraint is a Unique create new Unique.
        } else if (oldConstraint instanceof Unique) {

            // Create new Unique with the createNewUnique method
            Unique oldUnique = (Unique) oldConstraint;
            newConstraint = createNewUnique(oldUnique);
        }

        // Return new constraint after using other create-methods to create a copie of the old constraint
        return newConstraint;
    }

    /**
     * Create new content (ComplexContentType, SimpleContentType) by copying the
     * old content. All information stored in the old content is present in the
     * new content.
     *
     * @param oldContent Content which is used to created the new content, so
     * that the new content is a copy of the old content.
     * @param oldSchema XSDSchema containing the specified old content.
     * @return New content, copy the old content.
     */
    private Content createNewContent(Content oldContent, XSDSchema oldSchema) {

        // Create new content
        Content newContent = null;

        // If old content is an ComplexContentType create new ComplexContentType.
        if (oldContent instanceof ComplexContentType) {

            // Create new ComplexContentType with the createNewComplexContentType method
            ComplexContentType oldComplexContentType = (ComplexContentType) oldContent;
            newContent = createNewComplexContentType(oldComplexContentType, oldSchema);

        // If old content is an SimpleContentType create new SimpleContentType.
        } else if (oldContent instanceof SimpleContentType) {

            // Create new SimpleContentType with the createNewSimpleContentType method
            SimpleContentType oldSimpleContentType = (SimpleContentType) oldContent;
            newContent = createNewSimpleContentType(oldSimpleContentType, oldSchema);
        }

        // Return new content.
        return newContent;
    }

    /**
     * Create new documentation after the model of a specified old documentation.
     *
     * @param oldAppInfo Old documentation used to create the new documentation.
     * @return New documentation containing all information of the specified old
     * documentation.
     */
    private Documentation createNewDocumentation(Documentation oldDocumentation) {

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
     * Create new element as a copy of the specified old element. All
     * informations contained in the old element are transfered to the new
     * element. The new element contains a new type, new constraints and so on.
     *
     * @param oldElement Element used to create the new element as an exact copy
     * of the old element in the new schema.
     * @param oldSchema XSDSchema containing the old element and its type
     * reference.
     * @return New element in the new schema created from the specified old
     * element.
     */
    private Element createNewElement(Element oldElement, XSDSchema oldSchema) {

        // Get SymbolTableRef of the old type
        SymbolTableRef<Type> symbolTableRef = oldSchema.getTypeSymbolTable().getReference(oldElement.getType().getName());

        // If the SymbolTable for types contains no entry for the given type name or contains an entry linking to null a new entry is generated
        // or updated linking to the old type
        if (!newSchema.getTypeSymbolTable().hasReference(symbolTableRef.getKey()) || newSchema.getTypeSymbolTable().getReference(symbolTableRef.getKey()) != null) {
            newSchema.getTypeSymbolTable().updateOrCreateReference(symbolTableRef.getKey(), symbolTableRef.getReference());
        }

        // If the type is anonymous (the type is locally defined and is not referenced elsewhere) a new type is created
        if (oldElement.getType().isAnonymous()) {
            createNewType(oldElement.getType(), oldSchema);
        }

        // Create new elment with same name as the old element
        Element newElement = new Element(oldElement.getName());

        // Set new type to the type found in the type SymbolTable of the schema and check if "type" attribute is needed
        newElement.setType(newSchema.getTypeSymbolTable().getReference(oldElement.getType().getName()));
        newElement.setTypeAttr(oldElement.getTypeAttr());

        // Set "abstract" and "nillable" values of the new element
        newElement.setAbstract(oldElement.getAbstract());
        if (oldElement.getNillable()) {
            newElement.setNillable();
        }

        // Set id and annotation for the new element
        newElement.setAnnotation(createNewAnnotation(oldElement.getAnnotation()));
        newElement.setId(oldElement.getId());

        // Set "fixed" and "default" values of the new element
        newElement.setDefault(oldElement.getDefault());
        newElement.setFixed(oldElement.getFixed());

        // Check if "block" attribute is present in old element
        if (oldElement.getBlockModifiers() == null) {

            // Add "blockDefault" attribute values to new element (resolving "blockDefault" indirectly)
            if (oldSchema.getBlockDefaults().contains(BlockDefault.extension)) {
                newElement.addBlockModifier(Block.extension);
            }
            if (oldSchema.getBlockDefaults().contains(BlockDefault.restriction)) {
                newElement.addBlockModifier(Block.restriction);
            }
            if (oldSchema.getBlockDefaults().contains(BlockDefault.substitution)) {
                newElement.addBlockModifier(Block.substitution);
            }
        } else {

            // "block" attribute is present add "block" value to new element
            newElement.setBlockModifiers(oldElement.getBlockModifiers());
        }

        // Check if "final" attribute is present in old element
        if (oldElement.getFinalModifiers() == null) {

            // Add "finalDefault" attribute values to new element (resolving "finalDefault" indirectly)
            if (oldSchema.getFinalDefaults().contains(FinalDefault.extension)) {
                newElement.addFinalModifier(Final.extension);
            }
            if (oldSchema.getFinalDefaults().contains(FinalDefault.restriction)) {
                newElement.addFinalModifier(Final.restriction);
            }
        } else {

            // "final" attribute is present add "final" value to new element
            newElement.setFinalModifiers(oldElement.getFinalModifiers());
        }

        // Check if "form" attribute is present in old element
        if (oldElement.getForm() == null) {

            // Set "form" value to the value of the "elementFormDefault" attribute
            newElement.setForm(oldSchema.getElementFormDefault());
        } else {

            // "form" attribute is present add "form" value to new element
            newElement.setForm(oldElement.getForm());
        }

        // Add for each constraint of the old element a new constraint
        for (Iterator<Constraint> it = oldElement.getConstraints().iterator(); it.hasNext();) {
            Constraint oldConstraint = it.next();

            // Add new constraint to the new element
            newElement.addConstraint(createNewConstraint(oldConstraint, oldSchema));
        }

        // Get reference to the head element
        SymbolTableRef<Element> headElementReference = newElement.getSubstitutionGroup();

        // If the SymbolTable for elements contains no entry for the given head element name or contains an entry linking to null a new entry is generated
        // or updated linking to the old head element (Can be contained in a foreign schema)
        if (!newSchema.getElementSymbolTable().hasReference(headElementReference.getKey()) || newSchema.getElementSymbolTable().getReference(headElementReference.getKey()) != null) {
            newSchema.getElementSymbolTable().updateOrCreateReference(headElementReference.getKey(), headElementReference.getReference());
        }

        // Set new "substitutionGroup" value to the element contained in the SymbolTable of the new schema.
        newElement.setSubstitutionGroup(newSchema.getElementSymbolTable().getReference(headElementReference.getKey()));

        // Return new complete element
        return newElement;
    }

    /**
     * Create new element reference for a given old element reference. The new
     * element reference is a complete copy of the old element reference. If the
     * reference links to a element in another schema this element will be
     * referred to by the new element reference as well. If it links to an
     * element of the old schema the new refernce will link to the corresponding
     * element in the current new schema.
     *
     * @param oldElementRef Old element reference which is used to construct the
     * new element reference.
     * @param oldSchema XSDSchema containing the specified old element reference
     * and the SymbolTableRef contained in the element reference.
     * @return New element reference linking to the correct element (either
     * a new element in the new schema or an old element in a foreign schema).
     */
    private ElementRef createNewElementRef(ElementRef oldElementRef, XSDSchema oldSchema) {

        // Get SymbolTableRef of the old element reference
        SymbolTableRef<Element> symbolTableRef = oldSchema.getElementSymbolTable().getReference(oldElementRef.getElement().getName());

        // If the SymbolTable for elements contains no entry for the given element name or contains an entry linking to null a new entry is generated
        // or updated linking to the element specified in the old element reference. (Only top-level elements can be referred to, if the referred to top-level
        // element exist in the current schema this SymbolTableRef will be updated again to point to the new element of the old referred to element)
        if (!newSchema.getElementSymbolTable().hasReference(symbolTableRef.getKey()) || newSchema.getElementSymbolTable().getReference(symbolTableRef.getKey()) != null) {
            newSchema.getElementSymbolTable().updateOrCreateReference(symbolTableRef.getKey(), symbolTableRef.getReference());
        }

        // Get correct reference stored in the new schema
        ElementRef newElementRef = new ElementRef(newSchema.getElementSymbolTable().getReference(oldElementRef.getElement().getName()));

        // Set new id and annotation for new element reference
        newElementRef.setAnnotation(createNewAnnotation(oldElementRef.getAnnotation()));
        newElementRef.setId(oldElementRef.getId());

        // Return new element reference
        return newElementRef;
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
     * @param oldSchema XSDSchema containing the specified old group reference
     * and the SymbolTableRef contained in the group reference.
     * @return New group reference linking to the correct group (either a new
     * group in the new schema or an old group in a foreign schema).
     */
    private GroupRef createNewGroupRef(GroupRef oldGroupRef, XSDSchema oldSchema) {

        // Get SymbolTableRef of the old group reference
        SymbolTableRef<Group> symbolTableRef = oldSchema.getGroupSymbolTable().getReference(oldGroupRef.getGroup().getName());

        // If the SymbolTable for group contains no entry for the given group name or contains an entry linking to null a new entry is generated
        // or updated linking to the group specified in the old group reference.
        if (!newSchema.getGroupSymbolTable().hasReference(symbolTableRef.getKey()) || newSchema.getGroupSymbolTable().getReference(symbolTableRef.getKey()) != null) {
            newSchema.getGroupSymbolTable().updateOrCreateReference(symbolTableRef.getKey(), symbolTableRef.getReference());
        }

        // Create new group reference with correct reference stored in the new schema
        GroupRef newGroupRef = new GroupRef(newSchema.getGroupSymbolTable().getReference(oldGroupRef.getGroup().getName()));

        // Set new id and annotation for new group reference
        newGroupRef.setAnnotation(createNewAnnotation(oldGroupRef.getAnnotation()));
        newGroupRef.setId(oldGroupRef.getId());

        // Return new group reference
        return newGroupRef;
    }

    /**
     * Create new key by copying the specified old key. New key contains all
     * information of the old key, i.e. all fields of the old key are still
     * present. New key is registered in the constraint SymbolTable.
     *
     * @param oldKey Old key used to construct the new key.
     * @return New key, copy of the old key.
     */
    private Key createNewKey(Key oldKey) {

        // Create new key with new name and selector
        Key newKey = new Key(oldKey);

        // Add new key to SymbolTabel and new key name to list of constraint names
        newSchema.getKeyAndUniqueSymbolTable().updateOrCreateReference(newKey.getName(), newKey);
        newSchema.addConstraintName(newKey.getName());

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
    private KeyRef createNewKeyRef(KeyRef oldKeyRef, XSDSchema oldSchema) {

        // Get SymbolTableRef of the old key reference
        SymbolTableRef<SimpleConstraint> symbolTableRef = oldSchema.getKeyAndUniqueSymbolTable().getReference(oldKeyRef.getKeyOrUnique().getName());

        // If the SymbolTable for keys and uniques contains no entry for the given key/unique name or contains an entry linking to null a new entry is generated
        // or updated linking to the key/unique specified in the old key reference.
        if (!newSchema.getKeyAndUniqueSymbolTable().hasReference(symbolTableRef.getKey()) || newSchema.getKeyAndUniqueSymbolTable().getReference(symbolTableRef.getKey()) != null) {
            newSchema.getKeyAndUniqueSymbolTable().updateOrCreateReference(symbolTableRef.getKey(), symbolTableRef.getReference());
        }

        // Create new key with new name, selector and reference
        KeyRef newKeyRef = new KeyRef(oldKeyRef.getName(), oldKeyRef.getSelector(), newSchema.getKeyAndUniqueSymbolTable().getReference(oldKeyRef.getKeyOrUnique().getName()));

        // Add for each field of the old key reference a new field to the new key reference
        for (Iterator<String> it = oldKeyRef.getFields().iterator(); it.hasNext();) {
            String oldField = it.next();

            // Add new field to the new key
            newKeyRef.addField(oldField);
        }

        // Set id and annotation for new key reference
        newKeyRef.setAnnotation(createNewAnnotation(oldKeyRef.getAnnotation()));
        newKeyRef.setId(oldKeyRef.getId());

        // Add new key reference name to list of constraint names
        newSchema.addConstraintName(newKeyRef.getName());

        // Return new key reference
        return newKeyRef;
    }

    /**
     * Create new particle (AnyPattern, Element, ElementRef, GroupRef,
     * ParticleContainer) by copying the old particle. All information stored
     * in the old particle is present in the new particle.
     *
     * @param oldParticle Particle which is used to created the new particle, so
     * that the new particle is a copy of the old particle.
     * @param oldSchema XSDSchema containing the specified old particle.
     * @return New particle, copy the old particle.
     */
    private Particle createNewParticle(Particle oldParticle, XSDSchema oldSchema) {

        // Create new particle
        Particle newParticle = null;

        // If old particle is an AnyPattern create new AnyPattern.
        if (oldParticle instanceof AnyPattern) {

            // Create new AnyPattern with the createNewAnyPattern method
            AnyPattern oldAnyPattern = (AnyPattern) oldParticle;
            newParticle = createNewAnyPattern(oldAnyPattern);

        // If old particle is an Element create new Element.
        } else if (oldParticle instanceof Element) {

            // Create new Element with the createNewElement method
            Element oldElement = (Element) oldParticle;
            newParticle = createNewElement(oldElement, oldSchema);

        // If old particle is a ElementRef create new ElementRef.
        } else if (oldParticle instanceof ElementRef) {

            // Create new ElementRef with the createNewElementRef method
            ElementRef oldElementRef = (ElementRef) oldParticle;
            newParticle = createNewElementRef(oldElementRef, oldSchema);

        // If old particle is a GroupRef create new GroupRef.
        } else if (oldParticle instanceof GroupRef) {

            // Create new GroupRef with the createNewGroupRef method
            GroupRef oldGroupRef = (GroupRef) oldParticle;
            newParticle = createNewGroupRef(oldGroupRef, oldSchema);

        // If old particle is a ParticleContainer create new ParticleContainer.
        } else if (oldParticle instanceof ParticleContainer) {

            // Create new ParticleContainer with the createNewParticleContainer method
            ParticleContainer oldParticleContainer = (ParticleContainer) oldParticle;
            newParticle = createNewParticleContainer(oldParticleContainer, oldSchema);
        }

        // Return new particle after using other create-methods to create a copie of the old particle
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
    private ParticleContainer createNewParticleContainer(ParticleContainer oldParticleContainer, XSDSchema oldSchema) {

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
        newParticleContainer.setAnnotation(createNewAnnotation(oldParticleContainer.getAnnotation()));
        newParticleContainer.setId(oldParticleContainer.getId());

        // Get list of all old particles containes in the old particle container
        LinkedList<Particle> oldParticles = oldParticleContainer.getParticles();

        // For each old particle create a new particle and add it to new particle container
        for (Iterator<Particle> it = oldParticles.iterator(); it.hasNext();) {
            Particle oldParticle = it.next();

            // New create particle is added to new particle container
            newParticleContainer.addParticle(createNewParticle(oldParticle, oldSchema));
        }

        // Return new created particle container after it is complete
        return newParticleContainer;
    }

    /**
     * Create new SimpleContent extension by copying the specified old
     * SimpleContent extension. The new SimpleContent extension will contain a
     * new anonymous base type or a reference to a type contained in another
     * schema, this is achieved by copying the SymbolTableRef from the old
     * schema.
     *
     * @param oldSimpleContentExtension Old SimpleContent extension used to create the new
     * extension.
     * @param oldSchema XSDSchema containing the old extension.
     * @return New SimpleContent extension which is a copy of the old extension.
     */
    private SimpleContentInheritance createNewSimpleContentExtension(SimpleContentExtension oldSimpleContentExtension, XSDSchema oldSchema) {

        // Get SymbolTableRef of the old base type
        SymbolTableRef<Type> symbolTableRef = oldSchema.getTypeSymbolTable().getReference(oldSimpleContentExtension.getBase().getName());

        // If the SymbolTable for types contains no entry for the given base type name or contains an entry linking to null a new entry is generated
        // or updated linking to the old base type
        if (!newSchema.getTypeSymbolTable().hasReference(symbolTableRef.getKey()) || newSchema.getTypeSymbolTable().getReference(symbolTableRef.getKey()) != null) {
            newSchema.getTypeSymbolTable().updateOrCreateReference(symbolTableRef.getKey(), symbolTableRef.getReference());
        }

        // If the base type is anonymous (the type is locally defined and is not referenced elsewhere) a new type is created
        if (oldSimpleContentExtension.getBase().isAnonymous()) {
            createNewType(oldSimpleContentExtension.getBase(), oldSchema);
        }

        // Create new SimpleContent extension with new base
        SimpleContentExtension newSimpleContentExtension = new SimpleContentExtension(newSchema.getTypeSymbolTable().getReference(oldSimpleContentExtension.getBase().getName()));

        // Set new id and annotation in new extension
        newSimpleContentExtension.setAnnotation(createNewAnnotation(oldSimpleContentExtension.getAnnotation()));
        newSimpleContentExtension.setId(oldSimpleContentExtension.getId());

        // Get list of all attribute particles contained in the old extension
        LinkedList<AttributeParticle> oldAttributeParticles = oldSimpleContentExtension.getAttributes();

        // Create for each attribute particle a new attribute particle and add it to the new extension
        for (Iterator<AttributeParticle> it = oldAttributeParticles.iterator(); it.hasNext();) {
            AttributeParticle oldAttributeParticle = it.next();

            // Add new create attribute particle to the new extension
            newSimpleContentExtension.addAttribute(createNewAttributeParticle(oldAttributeParticle, oldSchema));
        }

        // Return new SimpleContent extension
        return newSimpleContentExtension;
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
    private SimpleContentInheritance createNewSimpleContentInheritance(SimpleContentInheritance oldSimpleContentInheritance, XSDSchema oldSchema) {

        // Create new simpleContent inheritance
        SimpleContentInheritance newSimpleContentInheritance = null;

        // Check if the old inheritance is an extension
        if (oldSimpleContentInheritance instanceof SimpleContentExtension) {

            // Create new simpleContent extension
            SimpleContentExtension oldSimpleContentExtension = (SimpleContentExtension) oldSimpleContentInheritance;
            newSimpleContentInheritance = createNewSimpleContentExtension(oldSimpleContentExtension, oldSchema);

        // Check if the old inheritance is a restriction
        } else if (oldSimpleContentInheritance instanceof SimpleContentRestriction) {

            // Create new simpleContent restriction
            SimpleContentRestriction oldSimpleContentRestriction = (SimpleContentRestriction) oldSimpleContentInheritance;
            newSimpleContentInheritance = createNewSimpleContentRestriction(oldSimpleContentRestriction, oldSchema);
        }

        // Return new simpleContent inheritance
        return newSimpleContentInheritance;
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
    private SimpleContentList createNewSimpleContentList(SimpleContentList oldSimpleContentList, XSDSchema oldSchema) {

        // Get SymbolTableRef of the old base type
        SymbolTableRef<Type> symbolTableRef = oldSchema.getTypeSymbolTable().getReference(oldSimpleContentList.getBase().getName());

        // If the SymbolTable for types contains no entry for the given base type name or contains an entry linking to null a new entry is generated
        // or updated linking to the old base type
        if (!newSchema.getTypeSymbolTable().hasReference(symbolTableRef.getKey()) || newSchema.getTypeSymbolTable().getReference(symbolTableRef.getKey()) != null) {
            newSchema.getTypeSymbolTable().updateOrCreateReference(symbolTableRef.getKey(), symbolTableRef.getReference());
        }

        // If the base type is anonymous (the type is locally defined and is not referenced elsewhere) a new type is created
        if (oldSimpleContentList.getBase().isAnonymous()) {
            createNewType(oldSimpleContentList.getBase(), oldSchema);
        }

        // Create new list with new base type
        SimpleContentList newSimpleContentList = new SimpleContentList(newSchema.getTypeSymbolTable().getReference(oldSimpleContentList.getBase().getName()));

        // Set new id and annotation in new list
        newSimpleContentList.setAnnotation(createNewAnnotation(oldSimpleContentList.getAnnotation()));
        newSimpleContentList.setId(oldSimpleContentList.getId());

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
    private SimpleContentRestriction createNewSimpleContentRestriction(SimpleContentRestriction oldSimpleContentRestriction, XSDSchema oldSchema) {

        // Get SymbolTableRef of the old base type
        SymbolTableRef<Type> symbolTableRef = oldSchema.getTypeSymbolTable().getReference(oldSimpleContentRestriction.getBase().getName());

        // If the SymbolTable for types contains no entry for the given base type name or contains an entry linking to null a new entry is generated
        // or updated linking to the old base type
        if (!newSchema.getTypeSymbolTable().hasReference(symbolTableRef.getKey()) || newSchema.getTypeSymbolTable().getReference(symbolTableRef.getKey()) != null) {
            newSchema.getTypeSymbolTable().updateOrCreateReference(symbolTableRef.getKey(), symbolTableRef.getReference());
        }

        // If the base type is anonymous (the type is locally defined and is not referenced elsewhere) a new type is created
        if (oldSimpleContentRestriction.getBase().isAnonymous()) {
            createNewType(oldSimpleContentRestriction.getBase(), oldSchema);
        }

        // Create new anonymous simpleType if old anonymous simpleType is present
        SimpleType newAnonymousSimpleType = null;
        if (oldSimpleContentRestriction.getAnonymousSimpleType() != null) {

            // Store new anonymous simpleType for later usage
            newAnonymousSimpleType = (SimpleType) createNewType(oldSimpleContentRestriction.getAnonymousSimpleType(), oldSchema);
        }

        // Create new restriction with new base and new anonymous type
        SimpleContentRestriction newSimpleContentRestriction = new SimpleContentRestriction(newSchema.getTypeSymbolTable().getReference(oldSimpleContentRestriction.getBase().getName()), newAnonymousSimpleType);

        // Set new id and annotation in new restiction
        newSimpleContentRestriction.setAnnotation(createNewAnnotation(oldSimpleContentRestriction.getAnnotation()));
        newSimpleContentRestriction.setId(oldSimpleContentRestriction.getId());

        // Get list of all attribute particles contained in the old restriction
        LinkedList<AttributeParticle> oldAttributeParticles = oldSimpleContentRestriction.getAttributes();

        // Create for each attribute particle a new attribute particle and add it to the new restriction
        for (Iterator<AttributeParticle> it = oldAttributeParticles.iterator(); it.hasNext();) {
            AttributeParticle oldAttributeParticle = it.next();

            // Add new create attribute particle to the new restriction
            newSimpleContentRestriction.addAttribute(createNewAttributeParticle(oldAttributeParticle, oldSchema));
        }

        // Set "facets" of the new restriction
        newSimpleContentRestriction.setEnumeration(new LinkedList<String>(oldSimpleContentRestriction.getEnumeration()));

        SimpleContentFixableRestrictionProperty<Integer> newFractionDigits = new SimpleContentFixableRestrictionProperty<Integer>(oldSimpleContentRestriction.getFractionDigits().getValue(), oldSimpleContentRestriction.getFractionDigits().getFixed());
        newSimpleContentRestriction.setFractionDigits(newFractionDigits);

        SimpleContentFixableRestrictionProperty<Integer> newLenght = new SimpleContentFixableRestrictionProperty<Integer>(oldSimpleContentRestriction.getLength().getValue(), oldSimpleContentRestriction.getLength().getFixed());
        newSimpleContentRestriction.setLength(newLenght);

        SimpleContentFixableRestrictionProperty<Integer> newMaxLength = new SimpleContentFixableRestrictionProperty<Integer>(oldSimpleContentRestriction.getMaxLength().getValue(), oldSimpleContentRestriction.getMaxLength().getFixed());
        newSimpleContentRestriction.setMaxLength(newMaxLength);

        SimpleContentFixableRestrictionProperty<Integer> newMinLength = new SimpleContentFixableRestrictionProperty<Integer>(oldSimpleContentRestriction.getMinLength().getValue(), oldSimpleContentRestriction.getMinLength().getFixed());
        newSimpleContentRestriction.setMinLength(newMinLength);

        SimpleContentFixableRestrictionProperty<Integer> newTotalDigits = new SimpleContentFixableRestrictionProperty<Integer>(oldSimpleContentRestriction.getTotalDigits().getValue(), oldSimpleContentRestriction.getTotalDigits().getFixed());
        newSimpleContentRestriction.setTotalDigits(newTotalDigits);

        SimpleContentFixableRestrictionProperty<String> newMaxExclusive = new SimpleContentFixableRestrictionProperty<String>(oldSimpleContentRestriction.getMaxExclusive().getValue(), oldSimpleContentRestriction.getMaxExclusive().getFixed());
        newSimpleContentRestriction.setMaxExclusive(newMaxExclusive);

        SimpleContentFixableRestrictionProperty<String> newMaxInclusive = new SimpleContentFixableRestrictionProperty<String>(oldSimpleContentRestriction.getMaxInclusive().getValue(), oldSimpleContentRestriction.getMaxInclusive().getFixed());
        newSimpleContentRestriction.setMaxInclusive(newMaxInclusive);

        SimpleContentFixableRestrictionProperty<String> newMinExclusive = new SimpleContentFixableRestrictionProperty<String>(oldSimpleContentRestriction.getMinExclusive().getValue(), oldSimpleContentRestriction.getMinExclusive().getFixed());
        newSimpleContentRestriction.setMinExclusive(newMinExclusive);

        SimpleContentFixableRestrictionProperty<String> newMinInclusive = new SimpleContentFixableRestrictionProperty<String>(oldSimpleContentRestriction.getMinInclusive().getValue(), oldSimpleContentRestriction.getMinInclusive().getFixed());
        newSimpleContentRestriction.setMinInclusive(newMinInclusive);

        SimpleContentRestrictionProperty<String> newPattern = new SimpleContentRestrictionProperty<String>(oldSimpleContentRestriction.getPattern().getValue());
        newSimpleContentRestriction.setPattern(newPattern);

        SimpleContentFixableRestrictionProperty<SimpleContentPropertyWhitespace> newWhitespace = new SimpleContentFixableRestrictionProperty<SimpleContentPropertyWhitespace>(oldSimpleContentRestriction.getWhitespace().getValue(), oldSimpleContentRestriction.getWhitespace().getFixed());
        newSimpleContentRestriction.setWhitespace(newWhitespace);

        // Return new restriction
        return newSimpleContentRestriction;
    }

    /**
     * Create new SimpleContentType for a given old SimpleContentType. The new
     * SimpleContentType is an exact copy of the old SimpleContentType,
     * containing all information stored in the old SimpleContentType.
     *
     * @param oldSimpleContentType SimpleContentType which is used to create the
     * new SimpleContentType.
     * @param oldSchema XSDSchema containing the old SimpleContentType.
     * @return New SimpleContentType which is a copy of the specified old
     * SimpleContentType.
     */
    private SimpleContentType createNewSimpleContentType(SimpleContentType oldSimpleContentType, XSDSchema oldSchema) {

        // Create new SimpleContentType
        SimpleContentType newSimpleContentType = new SimpleContentType();

        // Set id and annotation for the new SimpleContentType
        newSimpleContentType.setAnnotation(createNewAnnotation(oldSimpleContentType.getAnnotation()));
        newSimpleContentType.setId(oldSimpleContentType.getId());

        // Set new created inheritance
        newSimpleContentType.setInheritance(createNewSimpleContentInheritance(oldSimpleContentType.getInheritance(), oldSchema));

        // Retrun new SimpleContentType with new inheritance
        return newSimpleContentType;
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
    private SimpleTypeInheritance createNewSimpleContentUnion(SimpleContentUnion oldSimpleContentUnion, XSDSchema oldSchema) {

        // Create new list for all new member types
        LinkedList<SymbolTableRef<Type>> newMemberTypes = new LinkedList<SymbolTableRef<Type>>();

        // Check for each old member type the new type SymbolTable
        for (Iterator<SymbolTableRef<Type>> it = oldSimpleContentUnion.getAllMemberTypes().iterator(); it.hasNext();) {
            SymbolTableRef<Type> symbolTableRef = it.next();

            // If the SymbolTable for types contains no entry for the given member type name or contains an entry linking to null a new entry is generated
            // or updated linking to the old member type
            if (!newSchema.getTypeSymbolTable().hasReference(symbolTableRef.getKey()) || newSchema.getTypeSymbolTable().getReference(symbolTableRef.getKey()) != null) {
                newSchema.getTypeSymbolTable().updateOrCreateReference(symbolTableRef.getKey(), symbolTableRef.getReference());
            }

            // If the member type is anonymous (the type is locally defined and is not referenced elsewhere) a new type is created
            if (symbolTableRef.getReference().isAnonymous()) {
                createNewType(symbolTableRef.getReference(), oldSchema);
            }

            // Get new member type from the type SymbolTable of the new schema
            newMemberTypes.add(newSchema.getTypeSymbolTable().getReference(symbolTableRef.getKey()));
        }

        // Create new union with new member types
        SimpleContentUnion newSimpleContentUnion = new SimpleContentUnion(newMemberTypes);

        // Set new id and annotation in new union
        newSimpleContentUnion.setAnnotation(createNewAnnotation(oldSimpleContentUnion.getAnnotation()));
        newSimpleContentUnion.setId(oldSimpleContentUnion.getId());

        // Return new union
        return newSimpleContentUnion;
    }

    /**
     * Method creates a new simpleType copying a specified old simpleType. Old
     * and new simpleType are mostly identical, but the new simpleType is
     * registered in the new schema where the old simpleType has to be
     * registered in the old schema.
     *
     * @param oldSimpleType SimpleType for which a copy is generated.
     * @param oldSchema  XSDSchema containing the old simpleType either directly
     * or indirectly.
     * @return New simpleType which is a copy of the specified old simpleType.
     */
    private SimpleType createNewSimpleType(SimpleType oldSimpleType, XSDSchema oldSchema) {

        // Create new simpleType with new inheritance
        SimpleType newSimpleType = new SimpleType(oldSimpleType.getName(), createNewSimpleTypeInheritance(oldSimpleType.getInheritance(), oldSchema));

        // Check if "final" attribute is present in old simpleType
        if (oldSimpleType.getFinalModifiers() == null) {

            // Add "finalDefault" attribute values to new simpleType (resolving "finalDefault" indirectly)
            if (oldSchema.getFinalDefaults().contains(FinalDefault.restriction)) {
                newSimpleType.addFinalModifier(SimpleTypeInheritanceModifier.Restriction);
            }
            if (oldSchema.getFinalDefaults().contains(FinalDefault.list)) {
                newSimpleType.addFinalModifier(SimpleTypeInheritanceModifier.List);
            }
            if (oldSchema.getFinalDefaults().contains(FinalDefault.union)) {
                newSimpleType.addFinalModifier(SimpleTypeInheritanceModifier.Union);
            }
        } else {

            // "final" attribute is present add "final" value to new simpleType
            newSimpleType.setFinalModifiers(oldSimpleType.getFinalModifiers());
        }

        // Check wether the new simpleType has to be anonymous
        newSimpleType.setIsAnonymous(oldSimpleType.isAnonymous());

        // Set id and annotation for the new simple type
        newSimpleType.setAnnotation(createNewAnnotation(oldSimpleType.getAnnotation()));
        newSimpleType.setId(oldSimpleType.getId());

        // Update type SymbolTable and return new simpleType
        newSchema.getTypeSymbolTable().updateOrCreateReference(newSimpleType.getName(), newSimpleType);
        return newSimpleType;
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
    private SimpleTypeInheritance createNewSimpleTypeInheritance(SimpleTypeInheritance oldSimpleTypeInheritance, XSDSchema oldSchema) {

        // Create new simpleType inheritance
        SimpleTypeInheritance newSimpleTypeInheritance = null;


        // Check if the old inheritance is a list
        if (oldSimpleTypeInheritance instanceof SimpleContentList) {

            // Create new simpleType list
            SimpleContentList oldSimpleContentList = (SimpleContentList) oldSimpleTypeInheritance;
            newSimpleTypeInheritance = createNewSimpleContentList(oldSimpleContentList, oldSchema);

        // Check if the old inheritance is a restriction
        } else if (oldSimpleTypeInheritance instanceof SimpleContentRestriction) {

            // Create new simpleType restriction
            SimpleContentRestriction oldSimpleContentRestriction = (SimpleContentRestriction) oldSimpleTypeInheritance;
            newSimpleTypeInheritance = createNewSimpleContentRestriction(oldSimpleContentRestriction, oldSchema);

        // Check if the old inheritance is a union
        } else if (oldSimpleTypeInheritance instanceof SimpleContentUnion) {

            // Create new simpleType union
            SimpleContentUnion oldSimpleContentUnion = (SimpleContentUnion) oldSimpleTypeInheritance;
            newSimpleTypeInheritance = createNewSimpleContentUnion(oldSimpleContentUnion, oldSchema);
        }

        // Return new simpleType inheritance
        return newSimpleTypeInheritance;
    }

    /**
     * This method creates a new top-level group. This group will be a copie of
     * the specified old group contained in the given old schema. Furthermore
     * the new group will be registered in the SymbolTable of the new schema and
     * in the list of top-level groups.
     *
     * @param topLevelGroup Group which is the blueprint for the new group.
     * @param oldSchema XSDSchema containing the old group.
     * @return A SymbolTableRef to the new group contained in the new schema.
     */
    private void createNewTopLevelGroup(Group topLevelGroup, XSDSchema oldSchema) {

        // Create new group
        createNewGroup(topLevelGroup, oldSchema);

        // Add new group to the list of top-level groups
        newSchema.addGroup(newSchema.getGroupSymbolTable().getReference(topLevelGroup.getName()));
    }

    /**
     * This method creates a new group. This group will be a copie of the 
     * specified old group contained in the given old schema. Furthermore the 
     * new group will be registered in the SymbolTable of the new schema, but 
     * not in the list of top-level groups (so the method can be used by a 
     * redefine-component).
     *
     * @param oldGroup Group which is the blueprint for the new group.
     * @param oldSchema XSDSchema containing the old group.
     * @return The new attribute group contained in the new schema.
     */
    private Group createNewGroup(Group oldGroup, XSDSchema oldSchema) {

        // Create new particle container for the new group by copying the old particle container
        ParticleContainer newContainer = createNewParticleContainer(oldGroup.getParticleContainer(), oldSchema);

        // Create new group with the name of the old group and a new container
        Group newGroup = new Group(oldGroup.getName(), newContainer);

        // Create new annotation and id and add them to the new group
        newGroup.setAnnotation(createNewAnnotation(oldGroup.getAnnotation()));
        newGroup.setId(oldGroup.getId());

        // Create new SymbolTableRef
        newSchema.getGroupSymbolTable().updateOrCreateReference(newGroup.getName(), newGroup);

        // Return the new group
        return newGroup;
    }

    /**
     * This method creates a new top-level attribute group. This attribute group 
     * will be a copie of the specified old attribute group contained in the
     * given old schema. Furthermore the new attribute group will be registered
     * in the SymbolTable of the new schema and in the list of top-level
     * attribute groups.
     *
     * @param oldAttributeGroup Attribute group which is the blueprint for the
     * new attribute group.
     * @param oldSchema XSDSchema containing the old attribute group.
     * @return A SymbolTableRef to the new attribute group contained in the new
     * schema.
     */
    private void createNewTopLevelAttributeGroup(AttributeGroup topLevelAttributeGroup, XSDSchema oldSchema) {

        // Create new attribute group
        createNewAttributeGroup(topLevelAttributeGroup, oldSchema);

        // Add new attribute group to the list of top-level attribute groups
        newSchema.addAttributeGroup(newSchema.getAttributeGroupSymbolTable().getReference(topLevelAttributeGroup.getName()));
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
     * @param oldSchema XSDSchema containing the old attribute group.
     * @return The new attribute group contained in the new schema.
     */
    private AttributeGroup createNewAttributeGroup(AttributeGroup oldAttributeGroup, XSDSchema oldSchema) {

        // Create new attribute group with the name of the old attribute group
        AttributeGroup newAttributeGroup = new AttributeGroup(oldAttributeGroup.getName());

        // Create new annotation and id and add them to the new attribute group
        newAttributeGroup.setAnnotation(createNewAnnotation(oldAttributeGroup.getAnnotation()));
        newAttributeGroup.setId(oldAttributeGroup.getId());

        // Get list of all attribute particles contained in the old attribute group
        LinkedList<AttributeParticle> oldAttributeParticles = oldAttributeGroup.getAttributeParticles();

        // Create for each attribute particle a new attribute particle and add it to the new attribute group
        for (Iterator<AttributeParticle> it = oldAttributeParticles.iterator(); it.hasNext();) {
            AttributeParticle oldAttributeParticle = it.next();

            // Add new create attribute particle to the new attribute group
            newAttributeGroup.addAttributeParticle(createNewAttributeParticle(oldAttributeParticle, oldSchema));
        }

        // Create new SymbolTableRef
        newSchema.getAttributeGroupSymbolTable().updateOrCreateReference(newAttributeGroup.getName(), newAttributeGroup);

        // Return the new attribute group
        return newAttributeGroup;
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
     * @param oldSchema XSDSchema containing the specified old attribute particle.
     * @return New attribute particle, copy the old attribute particle.
     */
    private AttributeParticle createNewAttributeParticle(AttributeParticle oldAttributeParticle, XSDSchema oldSchema) {

        // Create new attribute particle
        AttributeParticle newAttributeParticle = null;

        // If old attribute particle is an AnyAttribute create new AnyAttribute.
        if (oldAttributeParticle instanceof AnyAttribute) {

            // Create new AnyAttribute with the createNewAnyAttribute method
            AnyAttribute oldAnyAttribute = (AnyAttribute) oldAttributeParticle;
            newAttributeParticle = createNewAnyAttribute(oldAnyAttribute);

        // If old attribute particle is an Attribute create new Attribute.
        } else if (oldAttributeParticle instanceof Attribute) {

            // Create new Attribute with the createNewAttribute method
            Attribute oldAttribute = (Attribute) oldAttributeParticle;
            newAttributeParticle = createNewAttribute(oldAttribute, oldSchema);

        // If old attribute particle is a AttributeGroupRef create new AttributeGroupRef.
        } else if (oldAttributeParticle instanceof AttributeGroupRef) {

            // Create new AttributeGroupRef with the createNewAttributeGroupRef method
            AttributeGroupRef oldAttributeGroupRef = (AttributeGroupRef) oldAttributeParticle;
            newAttributeParticle = createNewAttributeGroupRef(oldAttributeGroupRef, oldSchema);

        // If old attribute particle is a AttributeRef create new AttributeRef.
        } else if (oldAttributeParticle instanceof AttributeRef) {

            // Create new AttributeRef with the createNewAttributeRef method
            AttributeRef oldAttributeRef = (AttributeRef) oldAttributeParticle;
            newAttributeParticle = createNewAttributeRef(oldAttributeRef, oldSchema);
        }

        // Return new attribute particle after using other create-methods to create a copie of the old attribute particle
        return newAttributeParticle;
    }

    /**
     * This method creates a new top-level attribute. This attribute will be a
     * copie of the specified old attribute contained in the given old schema.
     * Furthermore the new attribute will be registered in the SymbolTable of
     * the new schema and in the list of top-level attributes.
     *
     * @param topLevelAttribute Attribute which is the blueprint for the new
     * attribute.
     * @param oldSchema XSDSchema containing the old attribute.
     * @return A SymbolTableRef to the new attribute contained in the new
     * schema.
     */
    private void createNewTopLevelAttribute(Attribute topLevelAttribute, XSDSchema oldSchema) {

        // Create new attribute
        createNewAttribute(topLevelAttribute, oldSchema);

        // Add new attribute to the list of top-level attributes
        newSchema.addAttribute(newSchema.getAttributeSymbolTable().getReference(topLevelAttribute.getName()));
    }

    /**
     * This method creates a new top-level element. This element will be a copie
     * of the specified old element contained in the given old schema.
     * Furthermore the new element will be registered in the SymbolTable of the
     * new schema and in the list of top-level elements.
     *
     * @param topLevelElement Element which is the blueprint for the new
     * element.
     * @param oldSchema XSDSchema containing the old element.
     * @return A SymbolTableRef to the new element contained in the new schema.
     */
    private void createNewTopLevelElement(Element topLevelElement, XSDSchema oldSchema) {

        // Create new element
        createNewElement(topLevelElement, oldSchema);

        // Add new element to the list of top-level elements
        newSchema.addElement(newSchema.getElementSymbolTable().getReference(topLevelElement.getName()));
    }

    /**
     * This method creates a new top-level type. This type will be a copie of
     * the specified old type contained in the given old schema. Furthermore the
     * new type will be registered in the SymbolTable of the new schema and in
     * the list of top-level types.
     *
     * @param topLevelType Type which is the blueprint for the new type.
     * @param oldSchema XSDSchema containing the old type.
     * @return A SymbolTableRef to the new type contained in the new schema.
     */
    private void createNewTopLevelType(Type topLevelType, XSDSchema oldSchema) {

        // Create new type
        createNewType(topLevelType, oldSchema);

        // Add new type to the list of top-level types
        newSchema.addType(newSchema.getTypeSymbolTable().getReference(topLevelType.getName()));
    }

    /**
     * Create new type copying the old type. New type has not to be a top-level
     * type it can be anonymous and locally defined. Further more this type can
     * be a simpleType or complexType.
     *
     * @param oldType Type which is the model for the new type.
     * @param oldSchema XSDSchema which contains the old type directly or
     * indirectly.
     * @return New type matching the old type.
     */
    private Type createNewType(Type oldType, XSDSchema oldSchema) {

        // Create new type for the old type
        Type newType = null;

        // Check if old type is a simpleType
        if (oldType instanceof SimpleType) {

            // Create new simpleType
            SimpleType oldSimpleType = (SimpleType) oldType;
            newType = createNewSimpleType(oldSimpleType, oldSchema);

        // Check if old type is a complexType
        } else if (oldType instanceof ComplexType) {

            // Create new complexType
            ComplexType oldComplexType = (ComplexType) oldType;
            newType = createNewComplexType(oldComplexType, oldSchema);
        }

        // Return new type
        return newType;
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
    private Unique createNewUnique(Unique oldUnique) {

        // Create new unique with new name and selector
        Unique newUnique = new Unique(oldUnique);

        // Add new unique to SymbolTabel and new unique name to list of constraint names
        newSchema.getKeyAndUniqueSymbolTable().updateOrCreateReference(newUnique.getName(), newUnique);
        newSchema.addConstraintName(newUnique.getName());

        // Return new unique
        return newUnique;
    }
}
