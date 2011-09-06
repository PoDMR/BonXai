package eu.fox7.bonxai.xsd.setOperations;

import eu.fox7.bonxai.xsd.*;
import eu.fox7.bonxai.xsd.tools.SchemaMerger;

import java.util.*;

/**
 * This class reprensents a group of schemata with same targetNamespace.
 * @author Dominik Wolff
 */
public class SchemaGroup {

    // TargetNamespace of all contained schemata.
    private String targetNamespace;

    // Set containing all minuend schemata of this group.
    // If the performed set-operation is not difference this is the only schema set used.
    private LinkedHashSet<XSDSchema> minuendSchemata;

    // Set containing all subtrahend schemata of this group.
    private LinkedHashSet<XSDSchema> subtrahendSchemata;

    // If the SchemaGroup contains an original schema the field is true.
    private boolean original;

    // Set of imported namespaces contained in minuendSchemata.
    private LinkedHashSet<String> namespaces;

    // This HashMap maps original Schemata to child schemata
    private HashMap<XSDSchema, LinkedHashSet<XSDSchema>> originalParentMap;

    /**
     * This is the constructor of the SchemaGroup class. To create a new SchemaGroup
     * only a targetNamespace is needed. The minuend and subtrahend schema sets
     * and all other informations are added afterwards.
     *
     * @param targetNamespace of all schemata contained in this group.
     */
    public SchemaGroup(String targetNamespace) {
        this.targetNamespace = targetNamespace;

        // Initialize fields, which are not present when the SchemaGroup is constructed
        minuendSchemata = new LinkedHashSet<XSDSchema>();
        subtrahendSchemata = new LinkedHashSet<XSDSchema>();
        namespaces = new LinkedHashSet<String>();
        original = false;
        originalParentMap = new HashMap<XSDSchema, LinkedHashSet<XSDSchema>>();
    }

    /**
     * This method returns the targetNamespace of all schemata contained in this
     * group.
     *
     * @return TargetNamespace of all schemata contained in this group.
     */
    public String getTargetNamespace() {
        return targetNamespace;
    }

    /**
     * This method returns the set of minuend schemata contained in this SchemaGroup.
     *
     * This is only a copie of the original set, so changes made to the set
     * will result in no changes for the original.
     *
     * @return Copie of the minuend schemata set.
     */
    public LinkedHashSet<XSDSchema> getMinuendSchemata() {
        return new LinkedHashSet<XSDSchema>(minuendSchemata);
    }

    /**
     * This method returns the set of subtrahend schemata contained in this SchemaGroup.
     *
     * This is only a copie of the original set, so changes made to the set
     * will result in no changes for the original.
     *
     * @return Copie of the subtrahend schemata set.
     */
    public LinkedHashSet<XSDSchema> getSubtrahendSchemata() {
        return new LinkedHashSet<XSDSchema>(subtrahendSchemata);
    }

    /**
     * This method returns a set of imported namespaces contained by the minuend
     * schemata. The schema constructed from this SchemaGroup will import these
     * namespaces.
     *
     * @return Set of imported namespaces contained by the minuend schemata.
     */
    public LinkedHashSet<String> getNamespaces() {
        return namespaces;
    }

    /**
     * This method returns <tt>true</tt> when the SchemaGroup contains an original
     * schema. If this is the case for some set-operations import-components are
     * set differently.
     *
     * @return <tt>true</tt> if original schema is contained.
     */
    public boolean isOriginal() {
        return original;
    }

    /**
     * Adds given schema to the minuend schema set and updates namespace and
     * originalParentMap fields accordingly.
     *
     * @param minuendSchema XSDSchema added to the minuend schema set.
     * @param originalParentSchema Original schema and parent of the minuendSchema.
     * @param original <tt>true</tt> if the minuendSchema is an original schema.
     */
    public void addMinuendSchema(XSDSchema minuendSchema, XSDSchema originalParentSchema, boolean original) {

        // Add schema to minuendSchemata set
        minuendSchemata.add(minuendSchema);

        // Update imported namespace set, with new namespaces from added schema
        for (Iterator<ForeignSchema> it = minuendSchema.getForeignSchemas().iterator(); it.hasNext();) {
            ForeignSchema foreignSchema = it.next();

            if (foreignSchema instanceof ImportedSchema) {
                ImportedSchema importedSchema = (ImportedSchema) foreignSchema;
                namespaces.add(importedSchema.getNamespace());
            }
        }

        // If added schema is an original schema the original field is set to true
        if (original) {
            this.original = true;
        }

        // Updated originalParentMap so that added schema and original parent schema are present
        LinkedHashSet<XSDSchema> siblings = new LinkedHashSet<XSDSchema>();

        if (originalParentMap.containsKey(originalParentSchema)) {
            siblings = originalParentMap.get(originalParentSchema);
        }
        siblings.add(minuendSchema);
        originalParentMap.put(originalParentSchema, siblings);
    }

    /**
     * Adds given schema to the subtrahend schema set and updates originalParentMap 
     * field accordingly.
     * 
     * @param subtrahendSchema XSDSchema which is added to the subtrahendSchemata
     * field.
     * @param originalParentSchema Direct or non direct parent of the added schema
     * and an original schema.
     */
    public void addSubtrahendSchema(XSDSchema subtrahendSchema, XSDSchema originalParentSchema) {

        // Add schema to subtrahendSchemata set
        subtrahendSchemata.add(subtrahendSchema);

        // Updated originalParentMap so that added schema and original parent schema are present
        LinkedHashSet<XSDSchema> siblings = new LinkedHashSet<XSDSchema>();

        if (originalParentMap.containsKey(originalParentSchema)) {
            siblings = originalParentMap.get(originalParentSchema);
        }
        siblings.add(subtrahendSchema);
        originalParentMap.put(originalParentSchema, siblings);
    }

    /**
     * A set of given minuend schemata is merged in the schema group. In order
     * to support intersection and difference construction schemata with same
     * originalParentSchema can be merged.
     *
     * @param schemata Set of schemata. Each schema has to be a child of the
     * specified original schema.
     * @param originalParentSchema Parent of specified schemata.
     */
    public void mergeMinuendSchema(LinkedHashSet<XSDSchema> schemata, XSDSchema originalParentSchema) {

        if (originalParentMap.get(originalParentSchema).contains(schemata)) {

            // Initialize SchemaMerger to merge minuend schemata
            SchemaMerger schemaMerger = new SchemaMerger();

            // Merge minuend schemata into a new merged schema
            XSDSchema mergedSchema = schemaMerger.mergeSchemata(schemata, targetNamespace);
            minuendSchemata.add(mergedSchema);

            // Remove old siblings and add new merged schema to originalParentMap from the original parent schema
            LinkedHashSet<XSDSchema> siblings = new LinkedHashSet<XSDSchema>();
            siblings.add(mergedSchema);
            originalParentMap.put(originalParentSchema, siblings);

            // Check if a schema of the schema set has no parent anymore
            for (Iterator<XSDSchema> it = schemata.iterator(); it.hasNext();) {
                XSDSchema schema = it.next();

                // Check if schema is contained in a set of the originalParentMap
                boolean contained = false;
                for (Iterator<XSDSchema> it2 = originalParentMap.keySet().iterator(); it2.hasNext();) {
                    XSDSchema currentOriginalParentSchema = it2.next();

                    if (originalParentMap.get(currentOriginalParentSchema).contains(schema)) {
                        contained = true;
                    }
                }

                // If the schema is not contained in the originalParentMap it is removed from the schema group
                if (!contained) {
                    minuendSchemata.remove(schema);
                }
            }
        }
    }

    /**
     * A set of given subtrahend schemata is merged in the schema group. In order
     * to support difference construction schemata with same originalParentSchema
     * can be merged.
     *
     * This method will only be used for difference construction. Removing a
     * schema set for a parent schema from the originalParentMap is valid because
     * it is impossible for an original parent schema to be parent of both a
     * minuend schema and a subrahend schema.
     *
     * @param schemata Set of schemata. Each schema has to be a child of the
     * specified original schema.
     * @param originalParentSchema Parent of specified schemata.
     */
    public void mergeSubtrahendSchema(LinkedHashSet<XSDSchema> schemata, XSDSchema originalParentSchema) {

        if (originalParentMap.get(originalParentSchema).contains(schemata)) {

            // Initialize SchemaMerger to merge subtrahend schemata
            SchemaMerger schemaMerger = new SchemaMerger();

            // Merge subtrahend schemata into a new merged schema
            XSDSchema mergedSchema = schemaMerger.mergeSchemata(schemata, targetNamespace);
            subtrahendSchemata.add(mergedSchema);

            // Remove old siblings and add new merged schema to originalParentMap from the original parent schema
            LinkedHashSet<XSDSchema> siblings = new LinkedHashSet<XSDSchema>();
            siblings.add(mergedSchema);
            originalParentMap.put(originalParentSchema, siblings);

            // Check if a schema of the schema set has no parent anymore
            for (Iterator<XSDSchema> it = schemata.iterator(); it.hasNext();) {
                XSDSchema schema = it.next();

                // Check if schema is contained in a set of the originalParentMap
                boolean contained = false;
                for (Iterator<XSDSchema> it2 = originalParentMap.keySet().iterator(); it2.hasNext();) {
                    XSDSchema currentOriginalParentSchema = it2.next();

                    if (originalParentMap.get(currentOriginalParentSchema).contains(schema)) {
                        contained = true;
                    }
                }

                // If the schema is not contained in the originalParentMap it is removed from the schema group
                if (!contained) {
                    subtrahendSchemata.remove(schema);
                }
            }
        }
    }

    /**
     * This method removes a schema from both schema sets. This is necessary for
     * difference construction when a schema is present in the minuend and the
     * subtrahend schema sets, because such a schema has no difference.
     * 
     * @param schemaLocation Location of the schema file. This filepath should be
     * an absolute URI.
     */
    public void removeSchema(String schemaLocation) {

        // Find minuend schema with specified schema location
        for (Iterator<XSDSchema> it = getMinuendSchemata().iterator(); it.hasNext();) {
            XSDSchema minuendSchema = it.next();

            if (schemaLocation.equals(minuendSchema.getSchemaLocation())) {

                // Remove minuend schema from minuend schema set
                minuendSchemata.remove(minuendSchema);

                // Check if schema group still contains original schema and update namespaces
                original = false;
                namespaces.clear();
                for (Iterator<XSDSchema> it2 = minuendSchemata.iterator(); it2.hasNext();) {
                    XSDSchema currentMinuendSchema = it2.next();

                    // OriginalParentMap contains a set of original schemata possibly contained in minuend schema set
                    if (originalParentMap.keySet().contains(currentMinuendSchema)) {
                        original = true;
                    }

                    // Update imported namespace set. Namespaces contained in the removed schema should be removed.
                    for (Iterator<ForeignSchema> it3 = currentMinuendSchema.getForeignSchemas().iterator(); it3.hasNext();) {
                        ForeignSchema foreignSchema = it3.next();

                        if (foreignSchema instanceof ImportedSchema) {
                            ImportedSchema importedSchema = (ImportedSchema) foreignSchema;
                            namespaces.add(importedSchema.getNamespace());
                        }
                    }
                }

                // Update originalParentMap so that minuend schema is no longer present
                for (Iterator<XSDSchema> it2 = originalParentMap.keySet().iterator(); it2.hasNext();) {
                    XSDSchema originalParentSchema = it2.next();
                    LinkedHashSet<XSDSchema> siblings = originalParentMap.get(originalParentSchema);

                    // If minuend schema is in the set of siblings remove it
                    if (siblings.contains(minuendSchema)) {
                        siblings.remove(minuendSchema);
                        originalParentMap.put(originalParentSchema, siblings);
                    }
                }
            }
        }

        // Find subtrahend schema with specified schema location
        for (Iterator<XSDSchema> it = getSubtrahendSchemata().iterator(); it.hasNext();) {
            XSDSchema subtrahendSchema = it.next();

            if (schemaLocation.equals(subtrahendSchema.getSchemaLocation())) {

                // Remove subrahend schema from minuend schema set
                minuendSchemata.remove(subtrahendSchema);

                // Update originalParentMap so that subtrahend schema is no longer present
                for (Iterator<XSDSchema> it2 = originalParentMap.keySet().iterator(); it2.hasNext();) {
                    XSDSchema originalParentSchema = it2.next();
                    LinkedHashSet<XSDSchema> siblings = originalParentMap.get(originalParentSchema);

                    // If subtrahend schema is in the set of siblings remove it
                    if (siblings.contains(subtrahendSchema)) {
                        siblings.remove(subtrahendSchema);
                        originalParentMap.put(originalParentSchema, siblings);
                    }
                }
            }
        }
    }

    /**
     * This method checks if a specified schema is contained in this set of
     * minuend schemata of the schema group.
     *
     * @param schema , which may be contained in this minuend schema set.
     * @return <tt>true</tt> if the set of minuend schemata of schema group
     * contains the specified schema.
     */
    public boolean containsMinuendSchema(XSDSchema schema) {
        for (Iterator<XSDSchema> it = minuendSchemata.iterator(); it.hasNext();) {
            XSDSchema minuendSchema = it.next();

            // Check if targetnamespace and schema location of any schema in the minuendSchemata set are equal to the given schema
            if (schema.getTargetNamespace().equals(minuendSchema.getTargetNamespace()) && schema.getSchemaLocation().equals(minuendSchema.getSchemaLocation())) {
                return true;
            }
        }
        return false;
    }

    /**
     * This method returns a minuend schema with specified schema location, if 
     * the minuend schema exists in the minuend schema set.
     * 
     * @param schemaLocation Location of the minuend schema.
     * @return Minuend schema contained in the minuend schema set with the 
     * specified schema location. 
     */
    public XSDSchema getMinuendSchema(String schemaLocation) {
        for (Iterator<XSDSchema> it = minuendSchemata.iterator(); it.hasNext();) {
            XSDSchema minuendSchema = it.next();

            // Check if schema location of any schema in the minuendSchemata set is equal to the given schema location
            if (schemaLocation.equals(minuendSchema.getSchemaLocation())) {
                return minuendSchema;
            }
        }
        return null;
    }

    /**
     * This method checks if a specified schema is contained in this set of
     * subtrahend schemata of the schema group.
     *
     * @param schema , which may be contained in this subtrahend schema set.
     * @return <tt>true</tt> if the set of subtrahend schemata of schema group
     * contains the specified schema.
     */
    public boolean containsSubtrahendSchema(XSDSchema schema) {
        for (Iterator<XSDSchema> it = subtrahendSchemata.iterator(); it.hasNext();) {
            XSDSchema subtrahendSchema = it.next();

            // Check if targetnamespace and schema location of any schema in the subtrahendSchemata set are equal to the given schema
            if (schema.getTargetNamespace().equals(subtrahendSchema.getTargetNamespace()) && schema.getSchemaLocation().equals(subtrahendSchema.getSchemaLocation())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns a set of original schemata. Each original schema is the parent of 
     * a schemata contained in the schema group. 
     * 
     * @return Set of original Schemata. Each parent of a schema of the schema group.
     */
    public LinkedHashSet<XSDSchema> getOriginalParents() {
        return new LinkedHashSet<XSDSchema>(originalParentMap.keySet());
    }

    /**
     * Returnd a set of schemata. Each schema is contained in the minuend or
     * subtrahend schema sets and child of the given original schema.
     *
     * @param originalSchema which is the parent of the returned schemata.
     * @return Set of schemata, all children of the specified schema and
     * contained in the schema group.
     */
    public LinkedHashSet<XSDSchema> getChildSchemata(XSDSchema originalSchema) {
        return new LinkedHashSet<XSDSchema>(originalParentMap.get(originalSchema));
    }

    /**
     * This method returns a set of minuend schema names. Thes names can be used
     * to create a new schema name for the schema resulting from this schema
     * group.
     *
     * @return Set of minuend schema names. If a two or more schemata have the
     * same names numbers are added to these names.
     */
    public LinkedHashSet<String> getMinuendSchemaNames() {

        // Set containing the names of all minuend schemata
        LinkedHashSet<String> minuendSchemaNames = new LinkedHashSet<String>();

        for (Iterator<XSDSchema> it = minuendSchemata.iterator(); it.hasNext();) {
            XSDSchema minuendSchema = it.next();

            // Name of the schema
            String minuendSchemaName = null;

            // Check if new name can be taken from schema location or hash
            if (minuendSchema.getSchemaLocation() != null) {

                // Minuend schema name is the name of the schema (In order to get this schema name the absolute path and the .xsd extension have to be removed from the schema location variable)
                minuendSchemaName = minuendSchema.getSchemaLocation().substring(minuendSchema.getSchemaLocation().lastIndexOf("/") + 1, minuendSchema.getSchemaLocation().lastIndexOf("."));
            } else {
                minuendSchemaName = minuendSchema.toString();
            }

            // If a schema name appears more than once a number is added to its name
            if (minuendSchemaNames.contains(minuendSchemaName)) {
                int number = 2;
                while (minuendSchemaNames.contains(minuendSchemaName)) {
                    minuendSchemaName = minuendSchemaName + "(" + number + ")";
                    number++;
                }
            }

            // Add new name to set of names
            minuendSchemaNames.add(minuendSchemaName);
        }
        return minuendSchemaNames;
    }

    /**
     * This method returns a set of subtrahend schema names. Thes names can be
     * used to create a new schema name for the schema resulting from this schema
     * group.
     *
     * @return Set of subtrahend schema names. If a two or more schemata have the
     * same names numbers are added to these names.
     */
    public LinkedHashSet<String> getSubtrahendSchemaNames() {

        // Set containing the names of all subtrahend schemata
        LinkedHashSet<String> subtrahendSchemaNames = new LinkedHashSet<String>();

        for (Iterator<XSDSchema> it = subtrahendSchemata.iterator(); it.hasNext();) {
            XSDSchema subtrahendSchema = it.next();

            // Subtrahend schema name is the name of the schema (In order to get this schema name the absolute path and the .xsd extension have to be removed from the schema location variable)
            String subtrahendSchemaName = subtrahendSchema.getSchemaLocation().substring(subtrahendSchema.getSchemaLocation().lastIndexOf("/") + 1, subtrahendSchema.getSchemaLocation().lastIndexOf("."));

            // If a schema name appears more than once a number is added to its name
            if (subtrahendSchemaNames.contains(subtrahendSchemaName)) {
                int number = 2;
                while (subtrahendSchemaNames.contains(subtrahendSchemaName)) {
                    subtrahendSchemaName = subtrahendSchemaName + "(" + number + ")";
                    number++;
                }
            }

            // Add new name to set of names
            subtrahendSchemaNames.add(subtrahendSchemaName);
        }
        return subtrahendSchemaNames;
    }
}
