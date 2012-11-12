package eu.fox7.schematoolkit.xsd.tools;

import eu.fox7.schematoolkit.common.IdentifiedNamespace;
import eu.fox7.schematoolkit.xsd.om.*;

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
public class IncludedSchemaResolver {

    /**
     * This method resolves the included schema, if it is included in the
     * including schema. Afterwards top-level components of the included schema
     * are present in the including schema and the included schema is not
     * needed anymore. Global default values of the included schema are resolved
     * before adding schema components to the including schema. So all "block",
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
            // TODO: hope that the included schema has the same defaults as the including one
//            removeAttributeFormDefault(includedSchema.getSchema());
//            removeElementFormDefault(includedSchema.getSchema());
//            removeBlockDefault(includedSchema.getSchema());
//            removeFinalDefault(includedSchema.getSchema());

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
            List<IdentifiedNamespace> namespaces = includedSchema.getSchema().getNamespaces();

            // Add all namespaces and abbreviations to the including schema
            for (IdentifiedNamespace namespace: namespaces) {
                // If no abbreviation for current namespace exists add IdentifiedNamespace to the including schema (An IdentifiedNamespace contains a namespaces and an abbreviation for this namespace).
                if (includingSchema.getNamespaceByURI(namespace.getUri()) == null)
                    includingSchema.addIdentifiedNamespace(namespace);
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
     * including schema. 
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
            Collection<Element> topLevelElementsIncludedSchema = includedSchema.getSchema().getElements();

            // Add each top-level element of the included schema to the including schema
            for (Element topLevelElementIncludedSchema: topLevelElementsIncludedSchema) {
                includingSchema.addElement(topLevelElementIncludedSchema);
            }
        }
    }

    /**
     * This method adds all attributes defined top-level in the included schema
     * to the including schema. Top-level attributes of the included schema can
     * be used in the including schema as if they were attributes of the
     * including schema. Because of this it is valid to move these attributes to
     * the including schema, non top-level attributes can not be referenced and
     * are moved only with types holding them. 
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
            Collection<Attribute> topLevelAttributesIncludedSchema = includedSchema.getSchema().getAttributes();

            // Add each top-level attribute of the included schema to the including schema
            for (Attribute topLevelAttributeIncludedSchema: topLevelAttributesIncludedSchema) {
            	includingSchema.addAttribute(topLevelAttributeIncludedSchema);
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
            Collection<Type> topLevelTypesIncludedSchema = includedSchema.getSchema().getTypes();

            // Add each top-level type of the included schema to the including schema.
            for (Type topLevelTypeIncludedSchema: topLevelTypesIncludedSchema) {
            	includingSchema.addType(topLevelTypeIncludedSchema);
            }
        }
    }

    /**
     * This method adds all groups of the included schema to the including
     * schema. Groups can only be defined top-level in a global scope. Moving 
     * them from the inlcuded schema to the including schema has no changes to
     * valid XML instances of the including schema. 
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
            Collection<Group> groupsIncludedSchema = includedSchema.getSchema().getGroups();

            // Add each group of the included schema to the including schema.
            for (Group groupIncludedSchema: groupsIncludedSchema) {
            	includingSchema.addGroup(groupIncludedSchema);
            }
        }
    }

    /**
     * This method adds all attribute groups contained in the included schema to
     * the including schema. Attribute groups can only be defined top-level in a
     * global scope. Moving them from the inlcuded schema to the including
     * schema does no change valid XML instances of the including schema.
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
            Collection<AttributeGroup> attributeGroupsIncludedSchema = includedSchema.getSchema().getAttributeGroups();

            // Add each attribute group of the included schema to the including schema.
            for (AttributeGroup attributeGroupIncludedSchema: attributeGroupsIncludedSchema) {
                includingSchema.addAttributeGroup(attributeGroupIncludedSchema);
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
//        // Check if the IncludedSchema contains a schema, else this method does nothing.
//        if (includedSchema.getSchema() != null) {
//
//            // Get list of key and unique references stored in the included schema.
//            LinkedList<SymbolTableRef<SimpleConstraint>> keyAndUniqueReferencesIncludedSchema = includedSchema.getSchema();
//
//            // Add each reference to the including schema.
//            for (Iterator<SymbolTableRef<SimpleConstraint>> it = keyAndUniqueReferencesIncludedSchema.iterator(); it.hasNext();) {
//                SymbolTableRef<SimpleConstraint> keyAndUniqueReferenceIncludedSchema = it.next();
//
//                // Copies the key/unique references from the included schema into the including schema via the SymbolTable.
//                includingSchema.getKeyAndUniqueSymbolTable().setReference(keyAndUniqueReferenceIncludedSchema.getReference().getName(), keyAndUniqueReferenceIncludedSchema);
//            }
//
//            // To prevent name collisions the list of key/keyRef/unique names of the included schema is added to the list of the including schema.
//            includingSchema.getConstraintNames().addAll(includedSchema.getSchema().getConstraintNames());
//        }
    }
}
