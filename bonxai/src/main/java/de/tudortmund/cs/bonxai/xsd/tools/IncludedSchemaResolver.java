package de.tudortmund.cs.bonxai.xsd.tools;

import de.tudortmund.cs.bonxai.common.IdentifiedNamespace;
import de.tudortmund.cs.bonxai.common.SymbolTableRef;
import de.tudortmund.cs.bonxai.xsd.*;
import java.util.*;

/**
 * This class resolves an IncludedSchema, so that all of its components are 
 * transferred to the including schema, which contains the IncludedSchema.
 * Because an included schema can be added to an including schema without
 * changing the set of valid XML instances the IncludedSchema is not needed
 * anymore. So this method can be used to generate from a set of schemata a
 * single schema. 
 *
 * @author Dominik Wolff
 */
public class IncludedSchemaResolver extends ForeignSchemaResolver {

    /**
     * This method resolves the included schema, if it is included in the
     * including schema. Afterwards top-level components of the included schema
     * are present in the including schema and the included schema is not
     * needed anymore. Global default values of the included schema are resolved
     * befor adding schema components to the including schema. So all "block",
     * "final" and "form" values are still valid.
     *
     * @param includingSchema XSDSchema which includes the specified included
     * schema and which will contain all top-level components of the included
     * schema.
     * @param includedSchema XSDSchema is included in the specified including
     * schema and is now removed from the including schema.
     */
    public void resolveInclude(XSDSchema includingSchema, IncludedSchema includedSchema) {

        // Check wether the IncludedSchema contains a schema and if the including schema includes the included schema.
        if (includedSchema.getSchema() != null && includingSchema.getForeignSchemas().contains(includedSchema)) {

            // Remove IncludedSchema from the including schema.
            removeIncludedSchema(includingSchema, includedSchema);

            // Remove global default values from the included schema.
            removeAttributeFormDefault(includedSchema.getSchema());
            removeElementFormDefault(includedSchema.getSchema());
            removeBlockDefault(includedSchema.getSchema());
            removeFinalDefault(includedSchema.getSchema());

            // Add namespaces and abbreviations contained in the included schema to the including schema.
            addNamespaces(includingSchema, includedSchema);

            // Add ForeignSchemata (Include/Redefine/Import components) contained in the included schema to the including schema.
            addForeignSchemas(includingSchema, includedSchema);

            // Add top-level schema components of the included schema to the including schema.
            addElements(includingSchema, includedSchema);
            addAttributes(includingSchema, includedSchema);
            addTypes(includingSchema, includedSchema);
            addGroups(includingSchema, includedSchema);
            addAttributeGroups(includingSchema, includedSchema);

            // Add constraint references of the included schema to the including schema.
            addKeyAndUnique(includingSchema, includedSchema);

            // Update element inheritance map of the including schema.
            addSubstitutionElements(includingSchema, includedSchema);
        }
    }

    /**
     * This method removes the included schema from the ForeignSchema list of the
     * including schema, if the included schema is present in the ForeignSchema 
     * list.
     *
     * @param includingSchema XSDSchema from which the specified included schema
     * is removed.
     * @param includedSchema The foreign schema which is removed from the
     * foreingn schema list of the specified including schema.
     */
    public void removeIncludedSchema(XSDSchema includingSchema, IncludedSchema includedSchema) {

        // Get foreign schema set, remove included schema and set new list in including schema
        LinkedList<ForeignSchema> foreignSchemas = includingSchema.getForeignSchemas();
        foreignSchemas.remove(includedSchema);
        includingSchema.setForeignSchemas(foreignSchemas);
    }

    /**
     * This method adds all namespaces and abbreviations from the included
     * schema to the including schema. This is necessary to reference schema
     * components with foreign namespaces.
     *
     * @param includingSchema XSDSchema to which the namespaces and abbreviations
     * stored in IdentifiedNamespaces are added.
     * @param includedSchema ForeignSchema from which the namespaces and
     * abbreviations in the form of identifiedNamespaces are taken.
     */
    public void addNamespaces(XSDSchema includingSchema, IncludedSchema includedSchema) {

        // Check if the IncludedSchema contains a schema, else this method does nothing
        if (includedSchema.getSchema() != null) {

            // Get a set of all namespaces and abbreviations used in the included schema
            LinkedHashSet<IdentifiedNamespace> namespaces = includedSchema.getSchema().getNamespaceList().getIdentifiedNamespaces();

            // Add all namespaces and abbreviations to the including schema
            for (Iterator<IdentifiedNamespace> it = namespaces.iterator(); it.hasNext();) {
                IdentifiedNamespace namespace = it.next();

                // If no abbreviation for current namespace exists add IdentifiedNamespace to the including schema (An IdentifiedNamespace contains a namespaces and an abbreviation for this namespace).
                if (includingSchema.getNamespaceList().getNamespaceByUri(namespace.getUri()).getIdentifier() == null) {
                    includingSchema.getNamespaceList().addIdentifiedNamespace(namespace);
                }
            }
        }
    }

    /**
     * This method adds the ForeignSchemata of the included schema to the
     * including schema. In order for the the including schema to reference
     * schema components which are referenced in the included schema these
     * ForeignSchemata have to be present in the including schema.
     *
     * @param includingSchema XSDSchema to which the foreign schemata of the
     * included schema are added.
     * @param includedSchema XSDSchema providing the foreign schemata added to the
     * including schema.
     */
    public void addForeignSchemas(XSDSchema includingSchema, IncludedSchema includedSchema) {

        // Check if the IncludedSchema contains a schema, else this method does nothing.
        if (includedSchema.getSchema() != null) {

            // Get lists of contained ForeignSchemata for including schema and included schema.
            LinkedList<ForeignSchema> foreignSchemataIncludingSchema = includingSchema.getForeignSchemas();
            LinkedList<ForeignSchema> foreignSchemataIncludedSchema = includedSchema.getSchema().getForeignSchemas();

            // Add each ForeignSchema contained in the list of the included schema to the list of the including schema.
            for (Iterator<ForeignSchema> it = foreignSchemataIncludedSchema.iterator(); it.hasNext();) {
                ForeignSchema foreignSchemaIncludedSchema = it.next();

                // If the ForeignSchema does not refer to the including schema it is added to the list of foreign schemata of the including schema
                if (!(foreignSchemaIncludedSchema.getSchema() != null && foreignSchemaIncludedSchema.getSchema().getSchemaLocation().equals(includingSchema.getSchemaLocation()))) {

                    // Add foreign schema to the list of foreign schemata of the including schema and change parent schema to including schema
                    foreignSchemataIncludingSchema.add(foreignSchemaIncludedSchema);
                    foreignSchemaIncludedSchema.setParentSchema(includingSchema);
                }
            }

            // Set new list of foreign schemata of the including schema in the including schema
            includingSchema.setForeignSchemas(foreignSchemataIncludingSchema);
        }
    }

    /**
     * This method adds all elements defined top-level in the included schema 
     * to the including schema. Because top-level elements of included schemata
     * can be used as root elements for valid XML instances of the including
     * schema it is valid to move these elements from the included schema to the
     * including schema. Furthermore SymbolTableRefs to elements contained in
     * ForeignSchemata of the included schema are added to the SymbolTable of
     * the including schema.
     *
     * @param includingSchema XSDSchema to which all top-level elements of the
     * specified included schema are moved.
     * @param includedSchema This schema contains the top-level elements which
     * are added to the specified including schema.
     */
    public void addElements(XSDSchema includingSchema, IncludedSchema includedSchema) {

        // Check if the IncludedSchema contains a schema, else this method does nothing.
        if (includedSchema.getSchema() != null) {

            // Get list of all top-level elements of the included schema
            LinkedList<Element> topLevelElementsIncludedSchema = includedSchema.getSchema().getElements();

            // Add each top-level element of the included schema to the including schema
            for (Iterator<Element> it = topLevelElementsIncludedSchema.iterator(); it.hasNext();) {
                Element topLevelElementIncludedSchema = it.next();

                // To add an element to the including schema, the SymbolTableRef of this element is copied to the including schema
                SymbolTableRef<Element> symbolTableRef = includedSchema.getSchema().getElementSymbolTable().getReference(topLevelElementIncludedSchema.getName());
                includingSchema.getElementSymbolTable().setReference(topLevelElementIncludedSchema.getName(), symbolTableRef);

                // If the element is already contained in the list of top-level elements of the including schema, it is not added to the list again. This should not happen for a valid including schema.
                if (!includingSchema.getElements().contains(symbolTableRef.getReference())) {
                    includingSchema.addElement(symbolTableRef);
                }
            }

            // Get list of all element references of the included schema
            LinkedList<SymbolTableRef<Element>> elementReferencesIncludedSchema = includedSchema.getSchema().getElementSymbolTable().getReferences();

            // Add each element references of the included schema to the including schema
            for (Iterator<SymbolTableRef<Element>> it = elementReferencesIncludedSchema.iterator(); it.hasNext();) {
                SymbolTableRef<Element> elementReferenceIncludedSchema = it.next();

                // Check if the element reference already exist in the including schema and if it is a dummy. If the check is true add the reference to the schema.
                if (!includingSchema.getElementSymbolTable().hasReference(elementReferenceIncludedSchema.getKey()) || (includingSchema.getElementSymbolTable().getReference(elementReferenceIncludedSchema.getKey()).getReference() != null && includingSchema.getElementSymbolTable().getReference(elementReferenceIncludedSchema.getKey()).getReference().isDummy())) {
                    includingSchema.getElementSymbolTable().setReference(elementReferenceIncludedSchema.getKey(), elementReferenceIncludedSchema);
                }
            }
        }
    }

    /**
     * This method adds all attributes defined top-level in the included schema
     * to the including schema. Top-level attributes of the included schema can
     * be used in the including schema as if they were attributes of the
     * including schema. Because of this it is valid to move these attributes to
     * the including schema, non top-level attributes can not be referenced and
     * are moved only with types holding them. Furthermore SymbolTableRefs to
     * attributes contained in ForeignSchemata of the included schema are added
     * to the SymbolTable of the including schema.
     *
     * @param includingSchema XSDSchema to which all top-level attributes of the
     * specified included schema are moved.
     * @param includedSchema This schema contains the top-level attributes which
     * are added to the specified including schema.
     */
    public void addAttributes(XSDSchema includingSchema, IncludedSchema includedSchema) {

        // Check if the IncludedSchema contains a schema, else this method does nothing.
        if (includedSchema.getSchema() != null) {

            // Get list of all top-level attributes of the included schema
            LinkedList<Attribute> topLevelAttributesIncludedSchema = includedSchema.getSchema().getAttributes();

            // Add each top-level attribute of the included schema to the including schema
            for (Iterator<Attribute> it = topLevelAttributesIncludedSchema.iterator(); it.hasNext();) {
                Attribute topLevelAttributeIncludedSchema = it.next();

                // To add an attribute to the including schema, the SymbolTableRef of this attribute is copied to the including schema
                SymbolTableRef<Attribute> symbolTableRef = includedSchema.getSchema().getAttributeSymbolTable().getReference(topLevelAttributeIncludedSchema.getName());
                includingSchema.getAttributeSymbolTable().setReference(topLevelAttributeIncludedSchema.getName(), symbolTableRef);

                // If the attribute is already contained in the list of top-level attributes of the including schema, it is not added to the list again. This should not happen for a valid including schema.
                if (!includingSchema.getAttributes().contains(symbolTableRef.getReference())) {
                    includingSchema.addAttribute(symbolTableRef);
                }
            }

            // Get list of all attribute references of the included schema
            LinkedList<SymbolTableRef<Attribute>> attributeReferencesIncludedSchema = includedSchema.getSchema().getAttributeSymbolTable().getReferences();

            // Add each attribute references of the included schema to the including schema
            for (Iterator<SymbolTableRef<Attribute>> it = attributeReferencesIncludedSchema.iterator(); it.hasNext();) {
                SymbolTableRef<Attribute> attributeReferenceIncludedSchema = it.next();

                // Check if the attribute reference already exist in the including schema and if it is a dummy. If the check is true add the reference to the schema.
                if (!includingSchema.getAttributeSymbolTable().hasReference(attributeReferenceIncludedSchema.getKey()) || (includingSchema.getAttributeSymbolTable().getReference(attributeReferenceIncludedSchema.getKey()).getReference() != null && includingSchema.getAttributeSymbolTable().getReference(attributeReferenceIncludedSchema.getKey()).getReference().isDummy())) {
                    includingSchema.getAttributeSymbolTable().setReference(attributeReferenceIncludedSchema.getKey(), attributeReferenceIncludedSchema);
                }
            }
        }
    }

    /**
     * This method adds all types defined top-level in the included schema
     * to the including schema. Top-level types of the included schema can
     * be used in the including schema as if they were types of the
     * including schema. Because of this it is valid to move these types to
     * the including schema, non top-level types can not be referenced (because
     * they have no names) and are moved only with elements holding them.
     * Furthermore SymbolTableRefs to types contained in ForeignSchemata of the
     * included schema are added to the SymbolTable of the including schema.
     *
     * @param includingSchema XSDSchema to which all top-level types of the
     * specified included schema are moved.
     * @param includedSchema This schema contains the top-level types which
     * are added to the specified including schema.
     */
    public void addTypes(XSDSchema includingSchema, IncludedSchema includedSchema) {

        // Check if the IncludedSchema contains a schema, else this method does nothing.
        if (includedSchema.getSchema() != null) {

            // Get list of all top-level types of the included schema.
            LinkedList<Type> topLevelTypesIncludedSchema = includedSchema.getSchema().getTypes();

            // Add each top-level type of the included schema to the including schema.
            for (Iterator<Type> it = topLevelTypesIncludedSchema.iterator(); it.hasNext();) {
                Type topLevelTypeIncludedSchema = it.next();

                // To add a type to the including schema, the SymbolTableRef of this type is copied to the including schema.
                SymbolTableRef<Type> symbolTableRef = includedSchema.getSchema().getTypeSymbolTable().getReference(topLevelTypeIncludedSchema.getName());
                includingSchema.getTypeSymbolTable().setReference(topLevelTypeIncludedSchema.getName(), symbolTableRef);

                // If the type is already contained in the list of top-level types of the including schema, it is not added to the list again. This should not happen for a valid including schema.
                if (!includingSchema.getTypes().contains(symbolTableRef.getReference())) {
                    includingSchema.addType(symbolTableRef);
                }
            }

            // Get list of all type references of the included schema
            LinkedList<SymbolTableRef<Type>> typeReferencesIncludedSchema = includedSchema.getSchema().getTypeSymbolTable().getReferences();

            // Add each type references of the included schema to the including schema
            for (Iterator<SymbolTableRef<Type>> it = typeReferencesIncludedSchema.iterator(); it.hasNext();) {
                SymbolTableRef<Type> typeReferenceIncludedSchema = it.next();

                // Check if the type reference already exist in the including schema and if it is a dummy. If the check is true add the reference to the schema.
                if (!includingSchema.getTypeSymbolTable().hasReference(typeReferenceIncludedSchema.getKey()) || (includingSchema.getTypeSymbolTable().getReference(typeReferenceIncludedSchema.getKey()).getReference() !=null && includingSchema.getTypeSymbolTable().getReference(typeReferenceIncludedSchema.getKey()).getReference().isDummy())) {
                    includingSchema.getTypeSymbolTable().setReference(typeReferenceIncludedSchema.getKey(), typeReferenceIncludedSchema);
                }
            }
        }
    }

    /**
     * This method adds all groups of the included schema to the including
     * schema. Groups can only be defined top-level in a global scope. Moving 
     * them from the inlcuded schema to the including schema has no changes to
     * valid XML instances of the including schema. Furthermore SymbolTableRefs
     * to groups contained in ForeignSchemata of the included schema are added
     * to the SymbolTable of the including schema.
     *
     * @param includingSchema XSDSchema to which all groups of the specified
     * included schema are moved.
     * @param includedSchema XSDSchema holding all groups which are added to
     * the specified including schema.
     */
    public void addGroups(XSDSchema includingSchema, IncludedSchema includedSchema) {

        // Check if the IncludedSchema contains a schema, else this method does nothing.
        if (includedSchema.getSchema() != null) {

            // Get list of all groups contained in the included schema.
            LinkedList<Group> groupsIncludedSchema = includedSchema.getSchema().getGroups();

            // Add each group of the included schema to the including schema.
            for (Iterator<Group> it = groupsIncludedSchema.iterator(); it.hasNext();) {
                Group groupIncludedSchema = it.next();

                // To add a group to the including schema, the SymbolTableRef of this group is copied to the including schema.
                SymbolTableRef<Group> symbolTableRef = includedSchema.getSchema().getGroupSymbolTable().getReference(groupIncludedSchema.getName());
                includingSchema.getGroupSymbolTable().setReference(groupIncludedSchema.getName(), symbolTableRef);

                // If the group is already contained in the list of groups of the including schema, it is not added to the list again. This should not happen for a valid including schema.
                if (!includingSchema.getGroups().contains(symbolTableRef.getReference())) {
                    includingSchema.addGroup(symbolTableRef);
                }
            }

            // Get list of all group references of the included schema
            LinkedList<SymbolTableRef<Group>> groupReferencesIncludedSchema = includedSchema.getSchema().getGroupSymbolTable().getReferences();

            // Add each group references of the included schema to the including schema
            for (Iterator<SymbolTableRef<Group>> it = groupReferencesIncludedSchema.iterator(); it.hasNext();) {
                SymbolTableRef<Group> groupReferenceIncludedSchema = it.next();

                // Check if the group reference already exist in the including schema and if it is a dummy. If the check is true add the reference to the schema.
                if (!includingSchema.getGroupSymbolTable().hasReference(groupReferenceIncludedSchema.getKey()) || (includingSchema.getGroupSymbolTable().getReference(groupReferenceIncludedSchema.getKey()).getReference() != null && includingSchema.getGroupSymbolTable().getReference(groupReferenceIncludedSchema.getKey()).getReference().isDummy())) {
                    includingSchema.getGroupSymbolTable().setReference(groupReferenceIncludedSchema.getKey(), groupReferenceIncludedSchema);
                }
            }
        }
    }

    /**
     * This method adds all attribute groups contained in the included schema to
     * the including schema. Attribute groups can only be defined top-level in a
     * global scope. Moving them from the inlcuded schema to the including
     * schema does no change valid XML instances of the including schema.
     * Furthermore SymbolTableRefs to attribute groups contained in
     * ForeignSchemata of the included schema are added to the SymbolTable of
     * the including schema.
     *
     * @param includingSchema XSDSchema to which all groups of the specified
     * included schema are moved.
     * @param includedSchema XSDSchema holding all groups which are added to
     * the specified including schema.
     */
    public void addAttributeGroups(XSDSchema includingSchema, IncludedSchema includedSchema) {

        // Check if the IncludedSchema contains a schema, else this method does nothing.
        if (includedSchema.getSchema() != null) {

            // Get list of all attribute groups contained in the included schema.
            LinkedList<AttributeGroup> attributeGroupsIncludedSchema = includedSchema.getSchema().getAttributeGroups();

            // Add each attribute group of the included schema to the including schema.
            for (Iterator<AttributeGroup> it = attributeGroupsIncludedSchema.iterator(); it.hasNext();) {
                AttributeGroup attributeGroupIncludedSchema = it.next();

                // To add an attribute group to the including schema, the SymbolTableRef of this attribute group is copied to the including schema.
                SymbolTableRef<AttributeGroup> symbolTableRef = includedSchema.getSchema().getAttributeGroupSymbolTable().getReference(attributeGroupIncludedSchema.getName());
                includingSchema.getAttributeGroupSymbolTable().setReference(attributeGroupIncludedSchema.getName(), symbolTableRef);

                // If the attribute group is already contained in the list of attribute groups of the including schema, it is not added to the list again. This should not happen for a valid including schema.
                if (!includingSchema.getAttributeGroups().contains(symbolTableRef.getReference())) {
                    includingSchema.addAttributeGroup(symbolTableRef);
                }
            }

            // Get list of all attribute group references of the included schema
            LinkedList<SymbolTableRef<AttributeGroup>> attributeGroupReferencesIncludedSchema = includedSchema.getSchema().getAttributeGroupSymbolTable().getReferences();

            // Add each attribute group references of the included schema to the including schema
            for (Iterator<SymbolTableRef<AttributeGroup>> it = attributeGroupReferencesIncludedSchema.iterator(); it.hasNext();) {
                SymbolTableRef<AttributeGroup> attributeGroupReferenceIncludedSchema = it.next();

                // Check if the attribute group reference already exist in the including schema and if it is a dummy. If the check is true add the reference to the schema.
                if (!includingSchema.getAttributeGroupSymbolTable().hasReference(attributeGroupReferenceIncludedSchema.getKey()) || (includingSchema.getAttributeGroupSymbolTable().getReference(attributeGroupReferenceIncludedSchema.getKey()).getReference() != null && includingSchema.getAttributeGroupSymbolTable().getReference(attributeGroupReferenceIncludedSchema.getKey()).getReference().isDummy())) {
                    includingSchema.getAttributeGroupSymbolTable().setReference(attributeGroupReferenceIncludedSchema.getKey(), attributeGroupReferenceIncludedSchema);
                }
            }
        }
    }

    /**
     * This method adds key and unique references from the included schema to
     * the including schema. This is necessary so that keys and uniques can be
     * referenced in the including schema like in the included schema. Key
     * and Uniques themself are held by elements so in order to move them the
     * all elements have to be moved. Also the constraint name set of the 
     * including schema is updated to contain all new constraint names.
     *
     * @param includingSchema XSDSchema which contains the included schema and
     * to which the key/unque references of the included schema are added.
     * @param includedSchema XSDSchema containing the key and unique references
     * which are added to the given including schema.
     */
    public void addKeyAndUnique(XSDSchema includingSchema, IncludedSchema includedSchema) {

        // Check if the IncludedSchema contains a schema, else this method does nothing.
        if (includedSchema.getSchema() != null) {

            // Get list of key and unique references stored in the included schema.
            LinkedList<SymbolTableRef<SimpleConstraint>> keyAndUniqueReferencesIncludedSchema = includedSchema.getSchema().getKeyAndUniqueSymbolTable().getReferences();

            // Add each reference to the including schema.
            for (Iterator<SymbolTableRef<SimpleConstraint>> it = keyAndUniqueReferencesIncludedSchema.iterator(); it.hasNext();) {
                SymbolTableRef<SimpleConstraint> keyAndUniqueReferenceIncludedSchema = it.next();

                // Copies the key/unique references from the included schema into the including schema via the SymbolTable.
                includingSchema.getKeyAndUniqueSymbolTable().setReference(keyAndUniqueReferenceIncludedSchema.getReference().getName(), keyAndUniqueReferenceIncludedSchema);
            }

            // To prevent name collisions the list of key/keyRef/unique names of the included schema is added to the list of the including schema.
            includingSchema.getConstraintNames().addAll(includedSchema.getSchema().getConstraintNames());
        }
    }

    /**
     * This method adds substitution group information of the included schema to
     * the including schema. The "substitutionGroup" attribute of an element
     * enables a user to use element inheritance. In the substitutionElements
     * map of a schema information about head elements and sustitutionable 
     * elements are stored. These information are moved from the included schema
     * to the including schema so that element inheritance still works in the 
     * including schema for elements of the included schema.
     *
     * @param includingSchema XSDSchema to which the new substitution group 
     * information of the specified included schema are added.
     * @param includedSchema XSDSchema containing the substitution group
     * information which are added to the specified including schema.
     */
    public void addSubstitutionElements(XSDSchema includingSchema, IncludedSchema includedSchema) {

        // Check if the IncludedSchema contains a schema, else this method does nothing.
        if (includedSchema.getSchema() != null) {

            // Get maps mapping all head elements to sustitutionable elements contained in the including schema and the included schema.
            HashMap<SymbolTableRef<Element>, HashSet<SymbolTableRef<Element>>> substitutionElementsIncludingSchema = includingSchema.getSubstitutionElements();
            HashMap<SymbolTableRef<Element>, HashSet<SymbolTableRef<Element>>> substitutionElementsIncludedSchema = includedSchema.getSchema().getSubstitutionElements();

            // Add each enty of the substitutionElementsIncludedSchema map to the substitutionElementsIncludingSchema map
            for (Iterator<SymbolTableRef<Element>> it = substitutionElementsIncludedSchema.keySet().iterator(); it.hasNext();) {
                SymbolTableRef<Element> substitutionElementIncludedSchema = it.next();

                // If a substitutionGroup for an head element already exists new sustitutionable elements are added, else a new entry is made
                HashSet<SymbolTableRef<Element>> symbolTableRefs = new HashSet<SymbolTableRef<Element>>();
                if (substitutionElementsIncludingSchema.containsKey(substitutionElementIncludedSchema)) {

                    // Element was a head element and an entry exists.
                    symbolTableRefs = substitutionElementsIncludingSchema.get(substitutionElementIncludedSchema);
                }

                // Add all sustitutionable elements for the head element contained in the included schema to the set of sustitutionable elements for the head element contained in the including schema.
                symbolTableRefs.addAll(substitutionElementsIncludedSchema.get(substitutionElementIncludedSchema));
                substitutionElementsIncludingSchema.put(substitutionElementIncludedSchema, symbolTableRefs);
            }
        }
    }
}
