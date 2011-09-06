package eu.fox7.bonxai.xsd.tools;

import eu.fox7.bonxai.common.IdentifiedNamespace;
import eu.fox7.bonxai.common.SymbolTableRef;
import eu.fox7.bonxai.xsd.*;

import java.util.*;

/**
 * This class resolves an RedefinedSchema, so that all of its components are 
 * transferred to an redefining schema, which contains the RedefinedSchema.
 * Because a redefind schema can be added to an redefininf schema without
 * changing the set of valid XML instances the RedefinedSchema is not needed
 * anymore. With this method a single schema can be generate from a set of
 * schemata .
 *
 * @author Dominik Wolff
 */
public class RedefinedSchemaResolver extends ForeignSchemaResolver {

    /**
     * This method resolves the redefined schema, if it is redefined in the
     * redefining schema. Afterwards top-level components of the redefined
     * schema are present in the redefining schema and the redefined schema is
     * no longer needed. Global default values of the redefined schema are
     * resolved befor adding schema components to the redefinig schema. So all
     * "block", "final" and "form" values are still valid.
     *
     * @param redefiningSchema XSDSchema which redefines the specified redefined
     * schema and which will contain all top-level components of the redefined
     * schema.
     * @param redefinedSchema XSDSchema is redefined in the specified redefining
     * schema and is now removed from the redefining schema.
     */
    public void resolveRedefine(XSDSchema redefiningSchema, RedefinedSchema redefinedSchema) {

        // Check wether the RedefinedSchema contains a schema and if the redefining schema redefines the redefined schema.
        if (redefinedSchema.getSchema() != null && redefiningSchema.getForeignSchemas().contains(redefinedSchema)) {

            // Remove RedefinedSchema from the redefining schema (This will also add the components redefined in the RedefinedSchema to the redefining schema).
            removeRedefinedSchema(redefiningSchema, redefinedSchema);

            // Remove global default values from the redefined schema.
            removeAttributeFormDefault(redefinedSchema.getSchema());
            removeElementFormDefault(redefinedSchema.getSchema());
            removeBlockDefault(redefinedSchema.getSchema());
            removeFinalDefault(redefinedSchema.getSchema());

            // Add namespaces and abbreviations contained in the redefined schema to the redefining schema.
            addNamespaces(redefiningSchema, redefinedSchema);

            // Add ForeignSchemata (Include/Redefine/Import components) contained in the redefined schema to the redefining schema.
            addForeignSchemas(redefiningSchema, redefinedSchema);

            // Add top-level schema components of the redefined schema to the redefining schema.
            addElements(redefiningSchema, redefinedSchema);
            addAttributes(redefiningSchema, redefinedSchema);
            addTypes(redefiningSchema, redefinedSchema);
            addGroups(redefiningSchema, redefinedSchema);
            addAttributeGroups(redefiningSchema, redefinedSchema);

            // Add constraint references of the redefined schema to the redefining schema.
            addKeyAndUnique(redefiningSchema, redefinedSchema);

            // Update element inheritance map of the redefining schema.
            addSubstitutionElements(redefiningSchema, redefinedSchema);
        }
    }

    /**
     * This method removes the redefined schema from the ForeignSchema list of
     * the redefining schema, if the redefined schema is present in the
     * ForeignSchema list. Redefined types, groups and attribute groups are
     * added to the redefining schema itself. The base types of redefined types
     * with inheritance are renamed and added to the redefining schema.
     *
     * @param redefiningSchema XSDSchema from which the specified redefined schema
     * is removed.
     * @param redefinedSchema The foreign schema which is removed from the
     * foreingn schema list of the specified redefining schema.
     */
    public void removeRedefinedSchema(XSDSchema redefiningSchema, RedefinedSchema redefinedSchema) {

        // Redefined attribute group is already present in the SymbolTable of the redefining schema so setting it top-level is the only thing left.
        for (Iterator<AttributeGroup> it = redefinedSchema.getAttributeGroups().iterator(); it.hasNext();) {
            AttributeGroup redefinedAttributeGroup = it.next();

            // Get SymbolTableRef of the redefined attribute group contained in the SymbolTable of the redefining schema
            SymbolTableRef<AttributeGroup> symbolTableRef = redefiningSchema.getAttributeGroupSymbolTable().getReference(redefinedAttributeGroup.getName());

            // Set redefined attribute group as top-level attribute group of the redefining schema
            redefiningSchema.addAttributeGroup(symbolTableRef);
        }

        // Redefined group is already present in the SymbolTable of the redefining schema so setting it top-level is the only thing left.
        for (Iterator<Group> it = redefinedSchema.getGroups().iterator(); it.hasNext();) {
            Group redefinedGroup = it.next();

            // Get SymbolTableRef of the redefined group contained in the SymbolTable of the redefining schema
            SymbolTableRef<Group> symbolTableRef = redefiningSchema.getGroupSymbolTable().getReference(redefinedGroup.getName());

            // Set redefined group as top-level group of the redefining schema
            redefiningSchema.addGroup(symbolTableRef);
        }

        // Redefined type is already present in the SymbolTable of the redefining schema so setting it top-level is the only thing left.
        for (Iterator<Type> it = redefinedSchema.getTypes().iterator(); it.hasNext();) {
            Type redefinedType = it.next();

            // Get SymbolTableRef of the redefined type contained in the SymbolTable of the redefining schema
            SymbolTableRef<Type> symbolTableRef = redefiningSchema.getTypeSymbolTable().getReference(redefinedType.getName());

            // Check if redefined type is a simpleType or ...
            if (redefinedType instanceof SimpleType) {
                SimpleType redefinedSimpleType = (SimpleType) redefinedType;

                // Check if redefined simpleType contains inheritance and if that inheritance  is a restriction.
                if (redefinedSimpleType.getInheritance() != null && redefinedSimpleType.getInheritance() instanceof SimpleContentRestriction) {
                    SimpleContentRestriction simpleContentRestriction = (SimpleContentRestriction) redefinedSimpleType.getInheritance();

                    // Add base type to redefining schema and set base type in redefined type
                    simpleContentRestriction.setBase(addBaseTypeToSchema(simpleContentRestriction.getBase(), redefiningSchema));
                }

            // Check if redefined type is a complexType.
            } else if (redefinedType instanceof ComplexType) {
                ComplexType redefinedComplexType = (ComplexType) redefinedType;

                // Check if the redefined complexType has a content.
                if (redefinedComplexType.getContent() != null) {

                    // Check if the content of the redefined type is complex or ...
                    if (redefinedComplexType.getContent() instanceof ComplexContentType) {
                        ComplexContentType complexContentType = (ComplexContentType) redefinedComplexType.getContent();

                        // Check if the redefined complexType contains inheritance and if itis a restriction or ...
                        if (complexContentType.getInheritance() != null && complexContentType.getInheritance() instanceof ComplexContentRestriction) {
                            ComplexContentRestriction complexContentRestriction = (ComplexContentRestriction) complexContentType.getInheritance();

                            // Add base type to redefining schema and set base type in redefined type
                            complexContentRestriction.setBase(addBaseTypeToSchema(complexContentRestriction.getBase(), redefiningSchema));

                        // Check if the contained inheritance is an extension.
                        } else if (complexContentType.getInheritance() != null && complexContentType.getInheritance() instanceof ComplexContentExtension) {
                            ComplexContentExtension complexContentExtension = (ComplexContentExtension) complexContentType.getInheritance();

                            // Add base type to redefining schema and set base type in redefined type
                            complexContentExtension.setBase(addBaseTypeToSchema(complexContentExtension.getBase(), redefiningSchema));
                        }

                    // Check if the content of the redefined type is simple
                    } else if (redefinedComplexType.getContent() instanceof SimpleContentType) {
                        SimpleContentType simpleContentType = (SimpleContentType) redefinedComplexType.getContent();

                        // Check if the redefined complexType contains inheritance and if itis a restriction or ...
                        if (simpleContentType.getInheritance() != null && simpleContentType.getInheritance() instanceof SimpleContentRestriction) {
                            SimpleContentRestriction simpleContentRestriction = (SimpleContentRestriction) simpleContentType.getInheritance();

                            // Add base type to redefining schema and set base type in redefined type
                            simpleContentRestriction.setBase(addBaseTypeToSchema(simpleContentRestriction.getBase(), redefiningSchema));

                        // Check if the contained inheritance is an extension.
                        } else if (simpleContentType.getInheritance() != null && simpleContentType.getInheritance() instanceof SimpleContentExtension) {
                            SimpleContentExtension simpleContentExtension = (SimpleContentExtension) simpleContentType.getInheritance();

                            // Add base type to redefining schema and set base type in redefined type
                            simpleContentExtension.setBase(addBaseTypeToSchema(simpleContentExtension.getBase(), redefiningSchema));
                        }
                    }
                }
            }

            // Set redefined type as top-level type of the redefining schema
            redefiningSchema.addType(symbolTableRef);
        }

        // Get foreign schema set, remove redefined schema and set new list in redefining schema
        LinkedList<ForeignSchema> foreignSchemas = redefiningSchema.getForeignSchemas();
        foreignSchemas.remove(redefinedSchema);
        redefiningSchema.setForeignSchemas(foreignSchemas);
    }

    /**
     * This method adds all namespaces and abbreviations from the redefined
     * schema to the redefining schema. This is necessary to reference schema
     * components with foreign namespaces.
     *
     * @param redefiningSchema XSDSchema to which the namespaces and abbreviations
     * stored in IdentifiedNamespaces are added.
     * @param redefinedSchema ForeignSchema from which the namespaces and
     * abbreviations in the form of identifiedNamespaces are taken.
     */
    public void addNamespaces(XSDSchema redefiningSchema, RedefinedSchema redefinedSchema) {

        // Check if the RedefinedSchema contains a schema, else this method does nothing
        if (redefinedSchema.getSchema() != null) {

            // Get a set of all namespaces and abbreviations used in the redefined schema
            LinkedHashSet<IdentifiedNamespace> namespaces = redefinedSchema.getSchema().getNamespaceList().getIdentifiedNamespaces();

            // Add all namespaces and abbreviations to the redefininf schema
            for (Iterator<IdentifiedNamespace> it = namespaces.iterator(); it.hasNext();) {
                IdentifiedNamespace namespace = it.next();

                // If no abbreviation for current namespace exists add IdentifiedNamespace to the redefining schema (An IdentifiedNamespace contains a namespaces and an abbreviation for this namespace).
                if (redefiningSchema.getNamespaceList().getNamespaceByUri(namespace.getUri()).getIdentifier() == null) {
                    redefiningSchema.getNamespaceList().addIdentifiedNamespace(namespace);
                }
            }
        }
    }

    /**
     * This method adds the ForeignSchemata of the redefined schema to the
     * redefining schema. In order for the the redefining schema to reference
     * schema components which are referenced in the redefined schema these
     * ForeignSchemata have to be present in the redefining schema.
     *
     * @param redefiningSchema XSDSchema to which the foreign schemata of the
     * redefined schema are added.
     * @param redefinedSchema XSDSchema providing the foreign schemata added to the
     * redefining schema.
     */
    public void addForeignSchemas(XSDSchema redefiningSchema, RedefinedSchema redefinedSchema) {

        // Check if the RedefinedSchema contains a schema, else this method does nothing.
        if (redefinedSchema.getSchema() != null) {

            // Get lists of contained ForeignSchemata for redefining schema and redefined schema.
            LinkedList<ForeignSchema> foreignSchemasRedefiningSchema = redefiningSchema.getForeignSchemas();
            LinkedList<ForeignSchema> foreignSchemasRedefinedSchema = redefinedSchema.getSchema().getForeignSchemas();

            // Add each ForeignSchema contained in the list of the redefined schema to the list of the redefining schema.
            for (Iterator<ForeignSchema> it = foreignSchemasRedefinedSchema.iterator(); it.hasNext();) {
                ForeignSchema foreignSchemaRedefinedSchema = it.next();

                // If the ForeignSchema does not refer to the redefining schema it is added to the list of foreign schemas of the redefining schema
                if (!(foreignSchemaRedefinedSchema.getSchema() != null && foreignSchemaRedefinedSchema.getSchema().getSchemaLocation().equals(redefiningSchema.getSchemaLocation()))) {

                    // Add foreign schema to the list of foreign schemata of the redefining schema and change parent schema to redefining schema
                    foreignSchemasRedefiningSchema.add(foreignSchemaRedefinedSchema);
                    foreignSchemaRedefinedSchema.setParentSchema(redefiningSchema);
                }
            }

            // Set new list of foreign schemata of the redefining schema in the redefining schema
            redefiningSchema.setForeignSchemas(foreignSchemasRedefiningSchema);
        }
    }

    /**
     * This method adds all elements defined top-level in the redefined schema
     * to the redefining schema. Beacuse top-level elements of redefined
     * schemata can be used as root elements for valid XML instances of the
     * redefining schema it is valid to move these elements from the redefined
     * schema to the redefining schema. Furthermore SymbolTableRefs to elements
     * contained in ForeignSchemata of the redefined schema are added to the
     * SymbolTable of the redefining schema.
     *
     * @param redefiningSchema XSDSchema to which all top-level elements of the
     * specified redefined schema are moved.
     * @param redefinedSchema This schema contains the top-level elements which
     * are added to the specified redefining schema.
     */
    public void addElements(XSDSchema redefiningSchema, RedefinedSchema redefinedSchema) {

        // Check if the RedefinedSchema contains a schema, else this method does nothing.
        if (redefinedSchema.getSchema() != null) {

            // Get list of all top-level elements of the redefined schema
            LinkedList<Element> topLevelElementsRedefinedSchema = redefinedSchema.getSchema().getElements();

            // Add each top-level element of the redefined schema to the redefining schema
            for (Iterator<Element> it = topLevelElementsRedefinedSchema.iterator(); it.hasNext();) {
                Element topLevelElementRedefinedSchema = it.next();

                // To add an element to the redefining schema, the SymbolTableRef of this element is copied to the redefining schema
                SymbolTableRef<Element> symbolTableRef = redefinedSchema.getSchema().getElementSymbolTable().getReference(topLevelElementRedefinedSchema.getName());
                redefiningSchema.getElementSymbolTable().setReference(topLevelElementRedefinedSchema.getName(), symbolTableRef);

                // If the element is already contained in the list of top-level elements of the redefining schema, it is not added to the list again. This should not happen for a valid redefining schema.
                if (!redefiningSchema.getElements().contains(symbolTableRef.getReference())) {
                    redefiningSchema.addElement(symbolTableRef);
                }
            }

            // Get list of all element references of the redefined schema
            LinkedList<SymbolTableRef<Element>> elementReferencesRedefinedSchema = redefinedSchema.getSchema().getElementSymbolTable().getReferences();

            // Add each element references of the redefined schema to the redefining schema
            for (Iterator<SymbolTableRef<Element>> it = elementReferencesRedefinedSchema.iterator(); it.hasNext();) {
                SymbolTableRef<Element> elementReferenceRedefinedSchema = it.next();

                // Check if the element reference already exist in the redefining schema and if it is a dummy. If the check is true add the reference to the schema.
                if (!redefiningSchema.getElementSymbolTable().hasReference(elementReferenceRedefinedSchema.getKey()) || (redefiningSchema.getElementSymbolTable().getReference(elementReferenceRedefinedSchema.getKey()).getReference() != null && redefiningSchema.getElementSymbolTable().getReference(elementReferenceRedefinedSchema.getKey()).getReference().isDummy())) {
                    redefiningSchema.getElementSymbolTable().setReference(elementReferenceRedefinedSchema.getKey(), elementReferenceRedefinedSchema);
                }
            }
        }
    }

    /**
     * This method adds all attributes defined top-level in the redefined schema
     * to the redefining schema. Top-level attributes of the redefined schema
     * can be used in the redefining schema as if they were attributes of the
     * redefining schema. Because of this it is valid to move these attributes
     * to the redefining schema, non top-level attributes can not be referenced
     * and are moved only with types holding them. Furthermore SymbolTableRefs
     * to attributes contained in ForeignSchemata of the redefined schema are
     * added to the SymbolTable of the redefining schema.
     *
     * @param redefiningSchema XSDSchema to which all top-level attributes of the
     * specified redefined schema are moved.
     * @param redefinedSchema This schema contains the top-level attributes which
     * are added to the specified redefining schema.
     */
    public void addAttributes(XSDSchema redefiningSchema, RedefinedSchema redefinedSchema) {

        // Check if the RedefinedSchema contains a schema, else this method does nothing.
        if (redefinedSchema.getSchema() != null) {

            // Get list of all top-level attributes of the redefined schema
            LinkedList<Attribute> topLevelAttributesRedefinedSchema = redefinedSchema.getSchema().getAttributes();

            // Add each top-level attribute of the redefined schema to the redefining schema
            for (Iterator<Attribute> it = topLevelAttributesRedefinedSchema.iterator(); it.hasNext();) {
                Attribute topLevelAttributeRedefinedSchema = it.next();

                // To add the attribute to the redefining schema, the symbolTableRef of this attribute is copied to the redefining schema
                SymbolTableRef<Attribute> symbolTableRef = redefinedSchema.getSchema().getAttributeSymbolTable().getReference(topLevelAttributeRedefinedSchema.getName());
                redefiningSchema.getAttributeSymbolTable().setReference(topLevelAttributeRedefinedSchema.getName(), symbolTableRef);

                // If the attribute is already contained in the list of top-level attributes of the redefining schema, it is not added to the list again. This should not happen for a valid redefining schema.
                if (!redefiningSchema.getAttributes().contains(symbolTableRef.getReference())) {
                    redefiningSchema.addAttribute(symbolTableRef);
                }
            }

            // Get list of all attribute references of the redefined schema
            LinkedList<SymbolTableRef<Attribute>> attributeReferencesRedefinedSchema = redefinedSchema.getSchema().getAttributeSymbolTable().getReferences();

            // Add each attribute references of the redefined schema to the redefining schema
            for (Iterator<SymbolTableRef<Attribute>> it = attributeReferencesRedefinedSchema.iterator(); it.hasNext();) {
                SymbolTableRef<Attribute> attributeReferenceRedefinedSchema = it.next();

                // Check if the attribute reference already exist in the redefining schema and if it is a dummy. If the check is true add the reference to the schema.
                if (!redefiningSchema.getAttributeSymbolTable().hasReference(attributeReferenceRedefinedSchema.getKey()) || (redefiningSchema.getAttributeSymbolTable().getReference(attributeReferenceRedefinedSchema.getKey()).getReference() != null && redefiningSchema.getAttributeSymbolTable().getReference(attributeReferenceRedefinedSchema.getKey()).getReference().isDummy())) {
                    redefiningSchema.getAttributeSymbolTable().setReference(attributeReferenceRedefinedSchema.getKey(), attributeReferenceRedefinedSchema);
                }
            }
        }
    }

    /**
     * This method adds all types defined top-level in the redefined schema
     * to the redefining schema. Top-level types of the redefined schema can
     * be used in the redefining schema as if they were types of the
     * redefining schema. Because of this it is valid to move these types to
     * the redefining schema, non top-level types can not be referenced (because
     * they have no names) and are moved only with elements holding them.
     * Furthermore SymbolTableRefs to types contained in ForeignSchemata of the
     * redefined schema are added to the SymbolTable of the redefining schema.
     *
     * @param redefiningSchema XSDSchema to which all top-level types of the
     * specified redefined schema are moved.
     * @param redefinedSchema This schema contains the top-level types which
     * are added to the specified redefining schema.
     */
    public void addTypes(XSDSchema redefiningSchema, RedefinedSchema redefinedSchema) {

        // Check if the RedefinedSchema contains a schema, else this method does nothing.
        if (redefinedSchema.getSchema() != null) {

            // Get list of all top-level types of the redefined schema.
            LinkedList<Type> topLevelTypesRedefinedSchema = redefinedSchema.getSchema().getTypes();

            // Add each top-level type of the redefined schema to the redefining schema.
            for (Iterator<Type> it = topLevelTypesRedefinedSchema.iterator(); it.hasNext();) {
                Type topLevelTypeRedefinedSchema = it.next();

                // Check if the type reference already exist in the redefining schema, this is possible because  if the type is redefined the redefined group may already be contained.
                SymbolTableRef<Type> symbolTableRef = null;
                if (redefiningSchema.getTypeSymbolTable().getReference(topLevelTypeRedefinedSchema.getName()) == null || redefiningSchema.getTypeSymbolTable().getReference(topLevelTypeRedefinedSchema.getName()).getReference().isDummy()) {

                    // To add a type to the redefining schema, the SymbolTableRef of this type is copied to the redefining schema.
                    symbolTableRef = redefinedSchema.getSchema().getTypeSymbolTable().getReference(topLevelTypeRedefinedSchema.getName());
                    redefiningSchema.getTypeSymbolTable().setReference(topLevelTypeRedefinedSchema.getName(), symbolTableRef);
                }

                // If the type is already contained in the list of top-level types of the redefining schema, it is not added to the list again. This should not happen for a valid redefining schema.
                if (symbolTableRef != null && !redefiningSchema.getTypes().contains(symbolTableRef.getReference())) {
                    redefiningSchema.addType(symbolTableRef);
                }
            }

            // Get list of all type references of the redefined schema
            LinkedList<SymbolTableRef<Type>> typeReferencesRedfinedSchema = redefinedSchema.getSchema().getTypeSymbolTable().getReferences();

            // Add each type references of the redefined schema to the redefining schema
            for (Iterator<SymbolTableRef<Type>> it = typeReferencesRedfinedSchema.iterator(); it.hasNext();) {
                SymbolTableRef<Type> typeReferenceRedfinedSchema = it.next();

                // Check if the type reference already exist in the redefining schema and if it is a dummy. If the check is true add the reference to the schema.
                if (!redefiningSchema.getTypeSymbolTable().hasReference(typeReferenceRedfinedSchema.getKey()) || (redefiningSchema.getTypeSymbolTable().getReference(typeReferenceRedfinedSchema.getKey()).getReference() != null && redefiningSchema.getTypeSymbolTable().getReference(typeReferenceRedfinedSchema.getKey()).getReference().isDummy())) {
                    redefiningSchema.getTypeSymbolTable().setReference(typeReferenceRedfinedSchema.getKey(), typeReferenceRedfinedSchema);
                }
            }
        }
    }

    /**
     * This method adds all groups of the redefined schema to the redefining
     * schema. Groups can only be defined top-level in a global scope. Moving
     * them from the redefined schema to the redefining schema has no changes to
     * valid XML instances of the redefining schema. Furthermore SymbolTableRefs
     * to groups contained in ForeignSchemata of the redefined schema are added
     * to the SymbolTable of the redefining schema.
     *
     * @param redefiningSchema XSDSchema to which all groups of the specified
     * redefined schema are moved.
     * @param redefinedSchema XSDSchema holding all groups which are added to
     * the specified redefining schema.
     */
    public void addGroups(XSDSchema redefiningSchema, RedefinedSchema redefinedSchema) {

        // Check if the RedefinedSchema contains a schema, else this method does nothing.
        if (redefinedSchema.getSchema() != null) {

            // Get list of all groups contained in the redefined schema.
            LinkedList<Group> groupsRedefinedSchema = redefinedSchema.getSchema().getGroups();

            // Add each group of the redefined schema to the redefining schema.
            for (Iterator<Group> it = groupsRedefinedSchema.iterator(); it.hasNext();) {
                Group groupRedefinedSchema = it.next();

                // Check if the group reference already exist in the redefining schema, this is possible because if the group is redefined the redefined group may already be contained.
                SymbolTableRef<Group> symbolTableRef = null;
                if (redefiningSchema.getGroupSymbolTable().getReference(groupRedefinedSchema.getName()) == null || redefiningSchema.getGroupSymbolTable().getReference(groupRedefinedSchema.getName()).getReference().isDummy()) {

                    // To add a group to the redefining schema, the SymbolTableRef of this group is copied to the redefining schema.
                    symbolTableRef = redefinedSchema.getSchema().getGroupSymbolTable().getReference(groupRedefinedSchema.getName());
                    redefiningSchema.getGroupSymbolTable().setReference(groupRedefinedSchema.getName(), symbolTableRef);
                }

                // If the group is already contained in the list of groups of the redefining schema, it is not added to the list again. This should not happen for a valid redefining schema.
                if (symbolTableRef != null && !redefiningSchema.getGroups().contains(symbolTableRef.getReference())) {
                    redefiningSchema.addGroup(symbolTableRef);
                }
            }

            // Get list of all group references of the redefined schema
            LinkedList<SymbolTableRef<Group>> groupReferencesRedefinedSchema = redefinedSchema.getSchema().getGroupSymbolTable().getReferences();

            // Add each group references of the redefined schema to the redefining schema
            for (Iterator<SymbolTableRef<Group>> it = groupReferencesRedefinedSchema.iterator(); it.hasNext();) {
                SymbolTableRef<Group> groupReferenceRedefinedSchema = it.next();

                // Check if the group reference already exist in the redefining schema and if it is a dummy. If the check is true add the reference to the schema.
                if (!redefiningSchema.getGroupSymbolTable().hasReference(groupReferenceRedefinedSchema.getKey()) || (redefiningSchema.getGroupSymbolTable().getReference(groupReferenceRedefinedSchema.getKey()).getReference() != null && redefiningSchema.getGroupSymbolTable().getReference(groupReferenceRedefinedSchema.getKey()).getReference().isDummy())) {
                    redefiningSchema.getGroupSymbolTable().setReference(groupReferenceRedefinedSchema.getKey(), groupReferenceRedefinedSchema);
                }
            }
        }
    }

    /**
     * This method adds all attribute groups contained in the redefined schema to
     * the redefining schema. Attribute groups can only be defined top-level in a
     * global scope. Moving them from the redefined schema to the redefining
     * schema does no change valid XML instances of the redefining schema.
     * Furthermore SymbolTableRefs to attribute groups contained in
     * ForeignSchemata of the redefined schema are added to the SymbolTable of
     * the redefinig schema.
     *
     * @param redefiningSchema XSDSchema to which all groups of the specified
     * redefined schema are moved.
     * @param redefinedSchema XSDSchema holding all groups which are added to
     * the specified redefinig schema.
     */
    public void addAttributeGroups(XSDSchema redefiningSchema, RedefinedSchema redefinedSchema) {

        // Check if the RedefinedSchema contains a schema, else this method does nothing.
        if (redefinedSchema.getSchema() != null) {

            // Get list of all attribute groups contained in the redefined schema.
            LinkedList<AttributeGroup> attributeGroupsRedefinedSchema = redefinedSchema.getSchema().getAttributeGroups();

            // Add each attribute group of the redefined schema to the redefining schema.
            for (Iterator<AttributeGroup> it = attributeGroupsRedefinedSchema.iterator(); it.hasNext();) {
                AttributeGroup attributeGroupRedefinedSchema = it.next();

                // Check if the attribute group reference already exist in the redefining schema, this is possible because if the attribute group is redefined the redefined attribute group may already be contained.
                SymbolTableRef<AttributeGroup> symbolTableRef = null;
                if (redefiningSchema.getAttributeGroupSymbolTable().getReference(attributeGroupRedefinedSchema.getName()) == null || redefiningSchema.getAttributeGroupSymbolTable().getReference(attributeGroupRedefinedSchema.getName()).getReference().isDummy()) {

                    // To add an attribute group to the redefining schema, the SymbolTableRef of this attribute group is copied to the redefining schema.
                    symbolTableRef = redefinedSchema.getSchema().getAttributeGroupSymbolTable().getReference(attributeGroupRedefinedSchema.getName());
                    redefiningSchema.getAttributeGroupSymbolTable().setReference(attributeGroupRedefinedSchema.getName(), symbolTableRef);
                }

                // If the attribute group is already contained in the list of attribute groups of the redefining schema, it is not added to the list again. This should not happen for a valid redefining schema.
                if (symbolTableRef != null && !redefiningSchema.getAttributeGroups().contains(symbolTableRef.getReference())) {
                    redefiningSchema.addAttributeGroup(symbolTableRef);
                }
            }

            // Get list of all attribute group references of the redefined schema
            LinkedList<SymbolTableRef<AttributeGroup>> attributeGroupReferencesRedefinedSchema = redefinedSchema.getSchema().getAttributeGroupSymbolTable().getReferences();

            // Add each attribute group references of the redefined schema to the redefining schema
            for (Iterator<SymbolTableRef<AttributeGroup>> it = attributeGroupReferencesRedefinedSchema.iterator(); it.hasNext();) {
                SymbolTableRef<AttributeGroup> attributeGroupReferenceRedefinedSchema = it.next();

                // Check if the attribute group reference already exist in the redefining schema and if it is a dummy. If the check is true add the reference to the schema.
                if (!redefiningSchema.getAttributeGroupSymbolTable().hasReference(attributeGroupReferenceRedefinedSchema.getKey()) || (redefiningSchema.getAttributeGroupSymbolTable().getReference(attributeGroupReferenceRedefinedSchema.getKey()).getReference() != null && redefiningSchema.getAttributeGroupSymbolTable().getReference(attributeGroupReferenceRedefinedSchema.getKey()).getReference().isDummy())) {
                    redefiningSchema.getAttributeGroupSymbolTable().setReference(attributeGroupReferenceRedefinedSchema.getKey(), attributeGroupReferenceRedefinedSchema);
                }
            }
        }
    }

    /**
     * This method adds key and unique references from the redefined schema to
     * the redefining schema. This is necessary so that keys and uniques can be
     * referenced in the redefining schema like in the redefined schema. Key
     * and Uniques themself are held by elements so in order to move them the
     * all elements have to be moved. Also the constraint name set of the
     * redefining schema is updated to contain all new constraint names.
     *
     * @param redefiningSchema XSDSchema which contains the redefined schema and
     * to which the key/unque references of the redefined schema are added.
     * @param redefinedSchema XSDSchema containing the key and unique references
     * which are added to the given redefining schema.
     */
    public void addKeyAndUnique(XSDSchema redefiningSchema, RedefinedSchema redefinedSchema) {

        // Check if the RedefinedSchema contains a schema, else this method does nothing.
        if (redefinedSchema.getSchema() != null) {

            // Get list of key and unique references stored in the redefined schema.
            LinkedList<SymbolTableRef<SimpleConstraint>> keyAndUniqueReferencesRedefinedSchema = redefinedSchema.getSchema().getKeyAndUniqueSymbolTable().getReferences();

            // Add each reference to the redefining schema.
            for (Iterator<SymbolTableRef<SimpleConstraint>> it = keyAndUniqueReferencesRedefinedSchema.iterator(); it.hasNext();) {
                SymbolTableRef<SimpleConstraint> keyAndUniqueReferenceRedefinedSchema = it.next();

                // Copies the key/unique references from the redefined schema into the redefining schema via the SymbolTable.
                redefiningSchema.getKeyAndUniqueSymbolTable().setReference(keyAndUniqueReferenceRedefinedSchema.getReference().getName(), keyAndUniqueReferenceRedefinedSchema);
            }

            // To prevent name collisions the list of key/keyRef/unique names of the redefined schema is added to the list of the redefining schema.
            redefiningSchema.getConstraintNames().addAll(redefinedSchema.getSchema().getConstraintNames());
        }
    }

    /**
     * This method adds substitution group information of the redefined schema
     * to the redefining schema. The "substitutionGroup" attribute of an element
     * enables a user to use element inheritance. In the substitutionElements
     * map of a schema information about head elements and sustitutionable
     * elements are stored. These information are moved from the redefined
     * schema to the redefining schema so that element inheritance still works
     * in the redefining schema for elements of the redefined schema.
     *
     * @param redefiningSchema XSDSchema to which the new substitution group
     * information of the specified redefined schema are added.
     * @param redefinedSchema XSDSchema containing the substitution group
     * information which are added to the specified redefining schema.
     */
    public void addSubstitutionElements(XSDSchema redefiningSchema, RedefinedSchema redefinedSchema) {

        // Check if the RedefinedSchema contains a schema, else this method does nothing.
        if (redefinedSchema.getSchema() != null) {

            // Get maps mapping all head elements to sustitutionable elements contained in the redefining schema and the redefined schema.
            HashMap<SymbolTableRef<Element>, HashSet<SymbolTableRef<Element>>> substitutionElementsRedefiningSchema = redefiningSchema.getSubstitutionElements();
            HashMap<SymbolTableRef<Element>, HashSet<SymbolTableRef<Element>>> substitutionElementsRedefinedSchema = redefinedSchema.getSchema().getSubstitutionElements();

            // Add each enty of the substitutionElementsRedefinedSchema map to the substitutionElementsRedefiningSchema map
            for (Iterator<SymbolTableRef<Element>> it = substitutionElementsRedefinedSchema.keySet().iterator(); it.hasNext();) {
                SymbolTableRef<Element> substitutionElementRedefinedSchema = it.next();

                // If a substitutionGroup for an head element already exists new sustitutionable elements are added, else a new entry is made
                HashSet<SymbolTableRef<Element>> symbolTableRefs = new HashSet<SymbolTableRef<Element>>();
                if (substitutionElementsRedefiningSchema.containsKey(substitutionElementRedefinedSchema)) {

                    // Element was a head element and an entry exists.
                    symbolTableRefs = substitutionElementsRedefiningSchema.get(substitutionElementRedefinedSchema);
                }

                // Add all sustitutionable elements for the head element contained in the redefined schema to the set of sustitutionable elements for the head element contained in the redefining schema.
                symbolTableRefs.addAll(substitutionElementsRedefinedSchema.get(substitutionElementRedefinedSchema));
                substitutionElementsRedefiningSchema.put(substitutionElementRedefinedSchema, symbolTableRefs);
            }
        }
    }

    /**
     * Method adds the base type of a redefined type to the redefing schema.
     * Because the base type of the redefined type may be a type with the same
     * name as the redefined type (i.e the type "typeA" in the redefining schema
     * may redefine the type "typeA" of the redefined schema) this name can not
     * be used by the base type. A new name is generate by this method as
     * follows: "former-type_oldname_ID.randomUUID". After that the base type
     * is set as top-level type in the redefined schema so that the redefined
     * type can reference it.
     *
     * @param baseType Base of the redefined type, which is set as top-level
     * type of the redefining schema with new name.
     * @param redefiningSchema XSDSchema which should contain the the base type
     * afterwards.
     * @return SymbolTableRef referencing the base type contained in the type
     * SymbolTable of the redefining schema.
     */
    private SymbolTableRef<Type> addBaseTypeToSchema(Type baseType, XSDSchema redefiningSchema) {

        // Rename old base type. (Old name is taken by the redefined type. New name is of the form "former-type_oldname_ID.randomUUID" i.e. "former-type_string_ID.067e6162-3b6f-4ae2-a171-2470b63dff00")
        // New type should not be used in future schemata so the UUID variant should be a valid option for the type name.
        baseType.setName("{" + baseType.getNamespace() + "}former-type_" + baseType.getLocalName() + "ID." + UUID.randomUUID());

        // Create new SymbolTableRef in the type SymbolTable of the redefining schema
        redefiningSchema.getTypeSymbolTable().updateOrCreateReference(baseType.getName(), baseType);

        // Get new type reference from the type SymbolTable and add type to top-level type list
        SymbolTableRef<Type> typeReference = redefiningSchema.getTypeSymbolTable().getReference(baseType.getName());
        redefiningSchema.addType(typeReference);

        // Return type refernce
        return typeReference;
    }
}
