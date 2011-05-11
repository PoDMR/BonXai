package de.tudortmund.cs.bonxai.xsd.tools;

import de.tudortmund.cs.bonxai.common.*;
import de.tudortmund.cs.bonxai.xsd.*;
import de.tudortmund.cs.bonxai.xsd.Group;
import java.util.*;

/**
 * The SchemaComponentRenamer class can be used to rename components of a schema
 * or a schema set. Another use is to find components in different schemata which
 * chair the same name, such components can be trouble when constructing unions
 * and intersections and differences. So renaming these components is necessary.
 *
 * Not all schema components can be renamed only components whose name do not
 * effect the schema instance set. I.e. types, groups and constraints.
 *
 * @author Dominik Wolff
 */
public class SchemaComponentRenamer extends ResolverTool{

    /**
     * Method renames all conflicting type names. In order to do this all original
     * schemata and their child schemata are traversed and type names that appear
     * more than once are renamed. To ensure that no conflicts emerge after renaming
     * a type, a set containing all type names helps building the new type names.
     *
     * New names will be of the form "oldName.number." i.e. "typeA.2.". This
     * helps the schema to remain readable.
     *
     * @param originalSchemata Set of specified original Schemata. Among these
     * schemata may be Schemata containing types with same names. These types
     * will be found and renamed.
     */
    public void renameConflictingTypeNames(Collection<XSDSchema> originalSchemata) {

        // This set contains all names already used for types in the originalSchemata and its children
        LinkedHashSet<String> alreadyUsedTypeNames = getAlreadyUsedTypeNames(originalSchemata);

        // For each original schema a set of types with conflicting names is saved
        HashMap<XSDSchema, LinkedHashSet<SymbolTableRef<Type>>> schemaConflictingTypesMap = getConflictingTypes(originalSchemata);

        // Rename conflicting types in all original schemata
        for (Iterator<XSDSchema> it = originalSchemata.iterator(); it.hasNext();) {
            XSDSchema originalSchema = it.next();

            // Check if conflicting types exist for this original schema
            if (schemaConflictingTypesMap.containsKey(originalSchema)) {
                LinkedHashSet<SymbolTableRef<Type>> symbolTableRefs = schemaConflictingTypesMap.get(originalSchema);

                // Repair each symbolTableRef
                for (Iterator<SymbolTableRef<Type>> it2 = symbolTableRefs.iterator(); it2.hasNext();) {
                    SymbolTableRef<Type> symbolTableRef = it2.next();

                    // Rename a single type
                    renameSingleConflictingTypeName(originalSchema, symbolTableRef, alreadyUsedTypeNames);
                }
            }
        }
    }

    /**
     * This method renames a single type. The type is specified through the
     * given SymbolTableRef. Furthermore the set of already used type names helps
     * avoiding future conflcts. While the originalSchema specifies the schema
     * hierachy in which the type will be renamed.
     *
     * @param originalSchema Root of the schema tree in which the specified type
     * will be renamed.
     * @param symbolTableRef Reference to the type, which will be rename.
     * @param alreadyUsedTypeNames Set of already used names. New name must be
     * found outside this set.
     */
    public void renameSingleConflictingTypeName(XSDSchema originalSchema, SymbolTableRef<Type> symbolTableRef, LinkedHashSet<String> alreadyUsedTypeNames) {

        // Save old type name, for future use
        String oldTypeName = symbolTableRef.getReference().getName();

        // Generate new type name
        String newTypeName = null;
        int number = 0;

        while (newTypeName == null) {

            // New names will be of the form "oldName.number." i.e. "typeA.2."
            String possibleName = symbolTableRef.getReference().getName() + "." + number;

            // If the possibleName is not already used the type has a new name
            if (!alreadyUsedTypeNames.contains(possibleName)) {
                newTypeName = possibleName;
                alreadyUsedTypeNames.add(newTypeName);
            }
            number++;
        }

        // Rename the type itself
        symbolTableRef.getReference().setName(newTypeName);

        // Set new Key for the symbolTableRef
        symbolTableRef.setKey(newTypeName);

        // Check that HashMaps of SymbolTables are updated
        LinkedHashSet<XSDSchema> containedSchemata = getSchemata(originalSchema);
        for (Iterator<XSDSchema> it = containedSchemata.iterator(); it.hasNext();) {
            XSDSchema containedSchema = it.next();

            // If SymbolTable contains a reference to the old type name this reference is replaced by the new one
            if (containedSchema.getTypeSymbolTable().getReference(oldTypeName) == symbolTableRef) {
                containedSchema.getTypeSymbolTable().setReference(newTypeName, symbolTableRef);

                // Remove old reference and SymbolTable HashMap entry
                containedSchema.getTypeSymbolTable().removeReference(oldTypeName);
            }
        }
    }

    /**
     * This method returns a HashMap mapping specified original schemata to
     * type references. Each refernces represents a type which has to be renamed.
     * In order to create this map the original schemata and their child schemata
     * are traversed. Found types are compared with other types and conflicts
     * are tagged.
     *
     * @param originalSchemata Set of original schemata. Each schema structure
     * is searched for the conflicting types.
     * @return HashMap mapping original schemas to sets of conflicting types.
     */
    public HashMap<XSDSchema, LinkedHashSet<SymbolTableRef<Type>>> getConflictingTypes(Collection<XSDSchema> originalSchemata) {

        // This HashMap maps original Schemata to child schemata, like in a schema group
        HashMap<XSDSchema, LinkedHashSet<XSDSchema>> originalParentMap = new HashMap<XSDSchema, LinkedHashSet<XSDSchema>>();

        // For each type the schema containing this type is saved
        HashMap<Type, XSDSchema> typeSchemaMap = new HashMap<Type, XSDSchema>();

        // For each type name a set of types with this name is saved
        HashMap<String, LinkedHashSet<Type>> nameTypeMap = new HashMap<String, LinkedHashSet<Type>>();

        // Travers the list of given original schemata
        for (Iterator<XSDSchema> it = originalSchemata.iterator(); it.hasNext();) {
            XSDSchema originalSchema = it.next();

            // Each parent schema contains a list of included/redefined/imported schemata
            LinkedHashSet<XSDSchema> containedSchemata = getSchemata(originalSchema);

            // Check each of these schemata for types and add them to a HashMap
            for (Iterator<XSDSchema> it2 = containedSchemata.iterator(); it2.hasNext();) {
                XSDSchema containedSchema = it2.next();

                LinkedList<Type> types = containedSchema.getTypes();
                for (Iterator<Type> it3 = types.iterator(); it3.hasNext();) {
                    Type type = it3.next();

                    // Put type with corresponding schema into typeSchemaMap
                    typeSchemaMap.put(type, containedSchema);

                    // Update nameTypeMap to contain the new type
                    LinkedHashSet<Type> typeSet = null;

                    // If type name already exist in nameTypeMap add type to existing set
                    if (nameTypeMap.containsKey(type.getName())) {
                        typeSet = nameTypeMap.get(type.getName());
                    } else {
                        typeSet = new LinkedHashSet<Type>();
                    }
                    typeSet.add(type);
                    nameTypeMap.put(type.getName(), typeSet);
                }

                // Update originalParentMap to contain the new schema
                LinkedHashSet<XSDSchema> siblings = null;

                // If orginal parent already exist in map add schema to existing set else construct a new one
                if (originalParentMap.containsKey(originalSchema)) {
                    siblings = originalParentMap.get(originalSchema);
                } else {
                    siblings = new LinkedHashSet<XSDSchema>();
                }
                siblings.add(containedSchema);
                originalParentMap.put(originalSchema, siblings);
            }
        }

        // For each original schema a set of types with conflicting names is saved (These types are present in the schema hierachy of the original schema)
        HashMap<XSDSchema, LinkedHashSet<SymbolTableRef<Type>>> schemaConflictingTypesMap = new HashMap<XSDSchema, LinkedHashSet<SymbolTableRef<Type>>>();

        // Check for all type names if a name appears more than once
        for (Iterator<String> it = nameTypeMap.keySet().iterator(); it.hasNext();) {
            String typeName = it.next();
            LinkedHashSet<Type> typeSet = nameTypeMap.get(typeName);

            if (typeSet.size() > 1) {

                // If their are more than one type with the same name, these types are added to the schemaConflictingTypesMap
                for (Iterator<Type> it2 = typeSet.iterator(); it2.hasNext();) {
                    Type type = it2.next();

                    // Find original parent XSDSchema of the schema that contains this type
                    XSDSchema typeOriginalSchema = null;
                    for (Iterator<XSDSchema> it3 = originalParentMap.keySet().iterator(); it3.hasNext();) {
                        XSDSchema originalSchema = it3.next();

                        if (originalParentMap.get(originalSchema).contains(typeSchemaMap.get(type))) {
                            typeOriginalSchema = originalSchema;
                        }
                    }

                    // If no entry for this original schema exist initialize a new one
                    if (!schemaConflictingTypesMap.containsKey(typeOriginalSchema)) {
                        schemaConflictingTypesMap.put(typeOriginalSchema, new LinkedHashSet<SymbolTableRef<Type>>());
                    }

                    // Add new symbolTableRef to the set
                    LinkedHashSet<SymbolTableRef<Type>> symbolTableRefs = schemaConflictingTypesMap.get(typeOriginalSchema);
                    symbolTableRefs.add(typeSchemaMap.get(type).getTypeSymbolTable().getReference(type.getName()));
                    schemaConflictingTypesMap.put(typeOriginalSchema, symbolTableRefs);
                }
            }
        }
        return schemaConflictingTypesMap;
    }

    /**
     * Returns a set of all type names already used in the set of given original
     * schemata. Each name is only contained once, because this is a set.
     *
     * @param originalSchemata Set of original schemata. Each schema contains
     * types either direct or indirect through child schemata.
     * @return Set of all type names contained in the specified schema set.
     */
    public LinkedHashSet<String> getAlreadyUsedTypeNames(Collection<XSDSchema> originalSchemata) {

        // This set contains all names used for types in the hierachy of the specified original schemata
        LinkedHashSet<String> alreadyUsedTypeNames = new LinkedHashSet<String>();

        // Travers the list of given original schemata to find contained schemata
        for (Iterator<XSDSchema> it = originalSchemata.iterator(); it.hasNext();) {
            XSDSchema originalSchema = it.next();

            // For each original schema build a set of contained schemata (child schemata + parent)
            LinkedHashSet<XSDSchema> containedSchemata = getSchemata(originalSchema);
            for (Iterator<XSDSchema> it2 = containedSchemata.iterator(); it2.hasNext();) {
                XSDSchema containedSchema = it2.next();
                LinkedList<Type> types = containedSchema.getTypes();

                // Check each schemata for types and add their names to the set
                for (Iterator<Type> it3 = types.iterator(); it3.hasNext();) {
                    Type type = it3.next();
                    alreadyUsedTypeNames.add(type.getName());
                }
            }
        }
        return alreadyUsedTypeNames;
    }   

    /**
     * Method renames all conflicting group names. In order to do this all original
     * schemata and their child schemata are traversed and group names that appear
     * more than once are renamed. To ensure that no conflicts emerge after renaming
     * a group, a set containing all group names helps building the new group names.
     *
     * New names will be of the form "oldName.number." i.e. "groupA.2.". This
     * helps the schema to remain readable.
     *
     * @param originalSchemata Set of specified original Schemata. Among these
     * schemata may be Schemata containing groups with same names. These groups
     * will be found and renamed.
     */
    public void renameConflictingGroupNames(Collection<XSDSchema> originalSchemata) {

        // This set contains all names already used for groups in the originalSchemata and its children
        LinkedHashSet<String> alreadyUsedGroupNames = getAlreadyUsedGroupNames(originalSchemata);

        // For each original schema a set of groups with conflicting names is saved
        HashMap<XSDSchema, LinkedHashSet<SymbolTableRef<Group>>> schemaConflictingGroupsMap = getConflictingGroups(originalSchemata);

        // Rename conflicting groups in all original schemata
        for (Iterator<XSDSchema> it = originalSchemata.iterator(); it.hasNext();) {
            XSDSchema originalSchema = it.next();

            // Check if conflicting groups exist for this original schema
            if (schemaConflictingGroupsMap.containsKey(originalSchema)) {
                LinkedHashSet<SymbolTableRef<Group>> symbolTableRefs = schemaConflictingGroupsMap.get(originalSchema);

                // Repair each symbolTableRef
                for (Iterator<SymbolTableRef<Group>> it2 = symbolTableRefs.iterator(); it2.hasNext();) {
                    SymbolTableRef<Group> symbolTableRef = it2.next();

                    // Rename a single group
                    renameSingleConflictingGroupName(originalSchema, symbolTableRef, alreadyUsedGroupNames);
                }
            }
        }
    }

    /**
     * This method renames a single group. The group is specified through the
     * given SymbolTableRef. Furthermore the set of already used group names helps
     * avoiding future conflcts. While the originalSchema specifies the schema
     * hierachy in which the group will be renamed.
     *
     * @param originalSchema Root of the schema tree in which the specified group
     * will be renamed.
     * @param symbolTableRef Reference to the group, which will be rename.
     * @param alreadyUsedGroupNames Set of already used names. New name must be
     * found outside this set.
     */
    public void renameSingleConflictingGroupName(XSDSchema originalSchema, SymbolTableRef<Group> symbolTableRef, LinkedHashSet<String> alreadyUsedGroupNames) {

        // Save old group name, for future use
        String oldGroupName = symbolTableRef.getReference().getName();

        // Generate new group name
        String newGroupName = null;
        int number = 0;

        while (newGroupName == null) {

            // New names will be of the form "oldName.number." i.e. "groupA.2."
            String possibleName = symbolTableRef.getReference().getName() + "." + number;

            // If the possibleName is not already used the group has a new name
            if (!alreadyUsedGroupNames.contains(possibleName)) {
                newGroupName = possibleName;
                alreadyUsedGroupNames.add(newGroupName);
            }
            number++;
        }

        // Rename the group itself
        symbolTableRef.getReference().setName(newGroupName);

        // Set new Key for the symbolTableRef
        symbolTableRef.setKey(newGroupName);

        // Check that HashMaps of SymbolTables are updated
        LinkedHashSet<XSDSchema> containedSchemata = getSchemata(originalSchema);
        for (Iterator<XSDSchema> it = containedSchemata.iterator(); it.hasNext();) {
            XSDSchema containedSchema = it.next();

            // If SymbolTable contains a reference to the old group name this reference is replaced by the new one
            if (containedSchema.getGroupSymbolTable().getReference(oldGroupName) == symbolTableRef) {
                containedSchema.getGroupSymbolTable().setReference(newGroupName, symbolTableRef);

                // Remove old reference and SymbolTable HashMap entry
                containedSchema.getGroupSymbolTable().removeReference(oldGroupName);
            }
        }
    }

    /**
     * This method returns a HashMap mapping specified original schemata to
     * group references. Each refernces represents a group which has to be renamed.
     * In order to create this map the original schemata and their child schemata
     * are traversed. Found groups are compared with other groups and conflicts
     * are tagged.
     *
     * @param originalSchemata Set of original schemata. Each schema structure
     * is searched for the conflicting groups.
     * @return HashMap mapping original schemas to sets of conflicting groups.
     */
    public HashMap<XSDSchema, LinkedHashSet<SymbolTableRef<Group>>> getConflictingGroups(Collection<XSDSchema> originalSchemata) {

        // This HashMap maps original Schemata to child schemata, like in a schema group
        HashMap<XSDSchema, LinkedHashSet<XSDSchema>> originalParentMap = new HashMap<XSDSchema, LinkedHashSet<XSDSchema>>();

        // For each group the schema containing this group is saved
        HashMap<Group, XSDSchema> groupSchemaMap = new HashMap<Group, XSDSchema>();

        // For each group name a set of groups with this name is saved
        HashMap<String, LinkedHashSet<Group>> nameGroupMap = new HashMap<String, LinkedHashSet<Group>>();

        // Travers the list of given original schemata
        for (Iterator<XSDSchema> it = originalSchemata.iterator(); it.hasNext();) {
            XSDSchema originalSchema = it.next();

            // Each parent schema contains a list of included/redefined/imported schemata
            LinkedHashSet<XSDSchema> containedSchemata = getSchemata(originalSchema);

            // Check each of these schemata for groups and add them to a HashMap
            for (Iterator<XSDSchema> it2 = containedSchemata.iterator(); it2.hasNext();) {
                XSDSchema containedSchema = it2.next();

                LinkedList<Group> groups = containedSchema.getGroups();
                for (Iterator<Group> it3 = groups.iterator(); it3.hasNext();) {
                    Group group = it3.next();

                    // Put group with corresponding schema into groupSchemaMap
                    groupSchemaMap.put(group, containedSchema);

                    // Update nameGroupMap to contain the new group
                    LinkedHashSet<Group> groupSet = null;

                    // If group name already exist in nameGroupMap add group to existing set
                    if (nameGroupMap.containsKey(group.getName())) {
                        groupSet = nameGroupMap.get(group.getName());
                    } else {
                        groupSet = new LinkedHashSet<Group>();
                    }
                    groupSet.add(group);
                    nameGroupMap.put(group.getName(), groupSet);
                }

                // Update originalParentMap to contain the new schema
                LinkedHashSet<XSDSchema> siblings = null;

                // If orginal parent already exist in map add schema to existing set else construct a new one
                if (originalParentMap.containsKey(originalSchema)) {
                    siblings = originalParentMap.get(originalSchema);
                } else {
                    siblings = new LinkedHashSet<XSDSchema>();
                }
                siblings.add(containedSchema);
                originalParentMap.put(originalSchema, siblings);
            }
        }

        // For each original schema a set of groups with conflicting names is saved (These groups are present in the schema hierachy of the original schema)
        HashMap<XSDSchema, LinkedHashSet<SymbolTableRef<Group>>> schemaConflictingGroupsMap = new HashMap<XSDSchema, LinkedHashSet<SymbolTableRef<Group>>>();

        // Check for all group names if a name appears more than once
        for (Iterator<String> it = nameGroupMap.keySet().iterator(); it.hasNext();) {
            String groupName = it.next();
            LinkedHashSet<Group> groupSet = nameGroupMap.get(groupName);

            if (groupSet.size() > 1) {

                // If their are more than one group with the same name, these groups are added to the schemaConflictingGroupsMap
                for (Iterator<Group> it2 = groupSet.iterator(); it2.hasNext();) {
                    Group group = it2.next();

                    // Find original parent XSDSchema of the schema that contains this group
                    XSDSchema groupOriginalSchema = null;
                    for (Iterator<XSDSchema> it3 = originalParentMap.keySet().iterator(); it3.hasNext();) {
                        XSDSchema originalSchema = it3.next();

                        if (originalParentMap.get(originalSchema).contains(groupSchemaMap.get(group))) {
                            groupOriginalSchema = originalSchema;
                        }
                    }

                    // If no entry for this original schema exist initialize a new one
                    if (!schemaConflictingGroupsMap.containsKey(groupOriginalSchema)) {
                        schemaConflictingGroupsMap.put(groupOriginalSchema, new LinkedHashSet<SymbolTableRef<Group>>());
                    }

                    // Add new symbolTableRef to the set
                    LinkedHashSet<SymbolTableRef<Group>> symbolTableRefs = schemaConflictingGroupsMap.get(groupOriginalSchema);
                    symbolTableRefs.add(groupSchemaMap.get(group).getGroupSymbolTable().getReference(group.getName()));
                    schemaConflictingGroupsMap.put(groupOriginalSchema, symbolTableRefs);
                }
            }
        }
        return schemaConflictingGroupsMap;
    }

    /**
     * Returns a set of all group names already used in the set of given original
     * schemata. Each name is only contained once, because this is a set.
     *
     * @param originalSchemata Set of original schemata. Each schema contains
     * groups either direct or indirect through child schemata.
     * @return Set of all group names contained in the specified schema set.
     */
    public LinkedHashSet<String> getAlreadyUsedGroupNames(Collection<XSDSchema> originalSchemata) {

        // This set contains all names used for groups in the hierachy of the specified original schemata
        LinkedHashSet<String> alreadyUsedGroupNames = new LinkedHashSet<String>();

        // Travers the list of given original schemata to find contained schemata
        for (Iterator<XSDSchema> it = originalSchemata.iterator(); it.hasNext();) {
            XSDSchema originalSchema = it.next();

            // For each original schema build a set of contained schemata (child schemata + parent)
            LinkedHashSet<XSDSchema> containedSchemata = getSchemata(originalSchema);
            for (Iterator<XSDSchema> it2 = containedSchemata.iterator(); it2.hasNext();) {
                XSDSchema containedSchema = it2.next();
                LinkedList<Group> groups = containedSchema.getGroups();

                // Check each schemata for groups and add their names to the set
                for (Iterator<Group> it3 = groups.iterator(); it3.hasNext();) {
                    Group group = it3.next();
                    alreadyUsedGroupNames.add(group.getName());
                }
            }
        }
        return alreadyUsedGroupNames;
    }

    /**
     * Method renames all conflicting attributeGroup names. In order to do this 
     * all original* schemata and their child schemata are traversed and
     * attributeGroup names that appear* more than once are renamed. To ensure
     * that no conflicts emerge after renaming a attributeGroup, a set containing
     * all attributeGroup names helps building the new attributeGroup names.
     *
     * New names will be of the form "oldName.number." i.e. "attributeGroupA.2.".
     * This helps the schema to remain readable.
     *
     * @param originalSchemata Set of specified original Schemata. Among these
     * schemata may be Schemata containing attributeGroups with same names. These
     * attributeGroups will be found and renamed.
     */
    public void renameConflictingAttributeGroupNames(Collection<XSDSchema> originalSchemata) {

        // This set contains all names already used for attributeGroups in the originalSchemata and its children
        LinkedHashSet<String> alreadyUsedAttributeGroupNames = getAlreadyUsedAttributeGroupNames(originalSchemata);

        // For each original schema a set of attributeGroups with conflicting names is saved
        HashMap<XSDSchema, LinkedHashSet<SymbolTableRef<AttributeGroup>>> schemaConflictingAttributeGroupsMap = getConflictingAttributeGroups(originalSchemata);

        // Rename conflicting attributeGroups in all original schemata
        for (Iterator<XSDSchema> it = originalSchemata.iterator(); it.hasNext();) {
            XSDSchema originalSchema = it.next();

            // Check if conflicting attributeGroups exist for this original schema
            if (schemaConflictingAttributeGroupsMap.containsKey(originalSchema)) {
                LinkedHashSet<SymbolTableRef<AttributeGroup>> symbolTableRefs = schemaConflictingAttributeGroupsMap.get(originalSchema);

                // Repair each symbolTableRef
                for (Iterator<SymbolTableRef<AttributeGroup>> it2 = symbolTableRefs.iterator(); it2.hasNext();) {
                    SymbolTableRef<AttributeGroup> symbolTableRef = it2.next();

                    // Rename a single attributeGroup
                    renameSingleConflictingAttributeGroupName(originalSchema, symbolTableRef, alreadyUsedAttributeGroupNames);
                }
            }
        }
    }

    /**
     * This method renames a single attributeGroup. The attributeGroup is 
     * specified through the given SymbolTableRef. Furthermore the set of
     * already used attributeGroup names helps avoiding future conflcts. While
     * the originalSchema specifies the schema hierachy in which the
     * attributeGroup will be renamed.
     *
     * @param originalSchema Root of the schema tree in which the specified
     * attributeGroup will be renamed.
     * @param symbolTableRef Reference to the attributeGroup, which will be rename.
     * @param alreadyUsedAttributeGroupNames Set of already used names. New name 
     * must be found outside this set.
     */
    public void renameSingleConflictingAttributeGroupName(XSDSchema originalSchema, SymbolTableRef<AttributeGroup> symbolTableRef, LinkedHashSet<String> alreadyUsedAttributeGroupNames) {

        // Save old attributeGroup name, for future use
        String oldAttributeGroupName = symbolTableRef.getReference().getName();

        // Generate new attributeGroup name
        String newAttributeGroupName = null;
        int number = 0;

        while (newAttributeGroupName == null) {

            // New names will be of the form "oldName.number." i.e. "attributeGroupA.2."
            String possibleName = symbolTableRef.getReference().getName() + "." + number;

            // If the possibleName is not already used the attributeGroup has a new name
            if (!alreadyUsedAttributeGroupNames.contains(possibleName)) {
                newAttributeGroupName = possibleName;
                alreadyUsedAttributeGroupNames.add(newAttributeGroupName);
            }
            number++;
        }

        // Rename the attributeGroup itself
        symbolTableRef.getReference().setName(newAttributeGroupName);

        // Set new Key for the symbolTableRef
        symbolTableRef.setKey(newAttributeGroupName);

        // Check that HashMaps of SymbolTables are updated
        LinkedHashSet<XSDSchema> containedSchemata = getSchemata(originalSchema);
        for (Iterator<XSDSchema> it = containedSchemata.iterator(); it.hasNext();) {
            XSDSchema containedSchema = it.next();

            // If SymbolTable contains a reference to the old attributeGroup name this reference is replaced by the new one
            if (containedSchema.getAttributeGroupSymbolTable().getReference(oldAttributeGroupName) == symbolTableRef) {
                containedSchema.getAttributeGroupSymbolTable().setReference(newAttributeGroupName, symbolTableRef);

                // Remove old reference and SymbolTable HashMap entry
                containedSchema.getAttributeGroupSymbolTable().removeReference(oldAttributeGroupName);
            }
        }
    }

    /**
     * This method returns a HashMap mapping specified original schemata to
     * attributeGroups references. Each refernces represents an attributeGroup
     * which has to be renamed. In order to create this map the original schemata
     * and their child schemata are traversed. Found attributeGroups are compared
     * with other attributeGroups and conflicts are tagged.
     *
     * @param originalSchemata Set of original schemata. Each schema structure
     * is searched for the conflicting attributeGroups.
     * @return HashMap mapping original schemas to sets of conflicting
     * attributeGroups.
     */
    public HashMap<XSDSchema, LinkedHashSet<SymbolTableRef<AttributeGroup>>> getConflictingAttributeGroups(Collection<XSDSchema> originalSchemata) {

        // This HashMap maps original Schemata to child schemata, like in a schema group
        HashMap<XSDSchema, LinkedHashSet<XSDSchema>> originalParentMap = new HashMap<XSDSchema, LinkedHashSet<XSDSchema>>();

        // For each attributeGroup the schema containing this attributeGroup is saved
        HashMap<AttributeGroup, XSDSchema> attributeGroupSchemaMap = new HashMap<AttributeGroup, XSDSchema>();

        // For each attributeGroup name a set of attributeGroups with this name is saved
        HashMap<String, LinkedHashSet<AttributeGroup>> nameAttributeGroupMap = new HashMap<String, LinkedHashSet<AttributeGroup>>();

        // Travers the list of given original schemata
        for (Iterator<XSDSchema> it = originalSchemata.iterator(); it.hasNext();) {
            XSDSchema originalSchema = it.next();

            // Each parent schema contains a list of included/redefined/imported schemata
            LinkedHashSet<XSDSchema> containedSchemata = getSchemata(originalSchema);

            // Check each of these schemata for attributeGroups and add them to a HashMap
            for (Iterator<XSDSchema> it2 = containedSchemata.iterator(); it2.hasNext();) {
                XSDSchema containedSchema = it2.next();

                LinkedList<AttributeGroup> attributeGroups = containedSchema.getAttributeGroups();
                for (Iterator<AttributeGroup> it3 = attributeGroups.iterator(); it3.hasNext();) {
                    AttributeGroup attributeGroup = it3.next();

                    // Put attributeGroup with corresponding schema into attributeGroupSchemaMap
                    attributeGroupSchemaMap.put(attributeGroup, containedSchema);

                    // Update nameAttributeGroupMap to contain the new attributeGroup
                    LinkedHashSet<AttributeGroup> attributeGroupSet = null;

                    // If attributeGroup name already exist in nameAttributeGroupMap add attributeGroup to existing set
                    if (nameAttributeGroupMap.containsKey(attributeGroup.getName())) {
                        attributeGroupSet = nameAttributeGroupMap.get(attributeGroup.getName());
                    } else {
                        attributeGroupSet = new LinkedHashSet<AttributeGroup>();
                    }
                    attributeGroupSet.add(attributeGroup);
                    nameAttributeGroupMap.put(attributeGroup.getName(), attributeGroupSet);
                }

                // Update originalParentMap to contain the new schema
                LinkedHashSet<XSDSchema> siblings = null;

                // If orginal parent already exist in map add schema to existing set else construct a new one
                if (originalParentMap.containsKey(originalSchema)) {
                    siblings = originalParentMap.get(originalSchema);
                } else {
                    siblings = new LinkedHashSet<XSDSchema>();
                }
                siblings.add(containedSchema);
                originalParentMap.put(originalSchema, siblings);
            }
        }

        // For each original schema a set of attributeGroups with conflicting names is saved (These attributeGroups are present in the schema hierachy of the original schema)
        HashMap<XSDSchema, LinkedHashSet<SymbolTableRef<AttributeGroup>>> schemaConflictingAttributeGroupsMap = new HashMap<XSDSchema, LinkedHashSet<SymbolTableRef<AttributeGroup>>>();

        // Check for all attributeGroup names if a name appears more than once
        for (Iterator<String> it = nameAttributeGroupMap.keySet().iterator(); it.hasNext();) {
            String attributeGroupName = it.next();
            LinkedHashSet<AttributeGroup> attributeGroupSet = nameAttributeGroupMap.get(attributeGroupName);

            if (attributeGroupSet.size() > 1) {

                // If their are more than one attributeGroup with the same name, these attributeGroups are added to the schemaConflictingAttributeGroupsMap
                for (Iterator<AttributeGroup> it2 = attributeGroupSet.iterator(); it2.hasNext();) {
                    AttributeGroup attributeGroup = it2.next();

                    // Find original parent XSDSchema of the schema that contains this attributeGroup
                    XSDSchema attributeGroupOriginalSchema = null;
                    for (Iterator<XSDSchema> it3 = originalParentMap.keySet().iterator(); it3.hasNext();) {
                        XSDSchema originalSchema = it3.next();

                        if (originalParentMap.get(originalSchema).contains(attributeGroupSchemaMap.get(attributeGroup))) {
                            attributeGroupOriginalSchema = originalSchema;
                        }
                    }

                    // If no entry for this original schema exist initialize a new one
                    if (!schemaConflictingAttributeGroupsMap.containsKey(attributeGroupOriginalSchema)) {
                        schemaConflictingAttributeGroupsMap.put(attributeGroupOriginalSchema, new LinkedHashSet<SymbolTableRef<AttributeGroup>>());
                    }

                    // Add new symbolTableRef to the set
                    LinkedHashSet<SymbolTableRef<AttributeGroup>> symbolTableRefs = schemaConflictingAttributeGroupsMap.get(attributeGroupOriginalSchema);
                    symbolTableRefs.add(attributeGroupSchemaMap.get(attributeGroup).getAttributeGroupSymbolTable().getReference(attributeGroup.getName()));
                    schemaConflictingAttributeGroupsMap.put(attributeGroupOriginalSchema, symbolTableRefs);
                }
            }
        }
        return schemaConflictingAttributeGroupsMap;
    }

    /**
     * Returns a set of all attributeGroup names already used in the set of given
     * original schemata. Each name is only contained once, because this is a set.
     *
     * @param originalSchemata Set of original schemata. Each schema contains
     * attributeGroups either direct or indirect through child schemata.
     * @return Set of all attributeGroup names contained in the specified schema
     * set.
     */
    public LinkedHashSet<String> getAlreadyUsedAttributeGroupNames(Collection<XSDSchema> originalSchemata) {

        // This set contains all names used for attributeGroups in the hierachy of the specified original schemata
        LinkedHashSet<String> alreadyUsedAttributeGroupNames = new LinkedHashSet<String>();

        // Travers the list of given original schemata to find contained schemata
        for (Iterator<XSDSchema> it = originalSchemata.iterator(); it.hasNext();) {
            XSDSchema originalSchema = it.next();

            // For each original schema build a set of contained schemata (child schemata + parent)
            LinkedHashSet<XSDSchema> containedSchemata = getSchemata(originalSchema);
            for (Iterator<XSDSchema> it2 = containedSchemata.iterator(); it2.hasNext();) {
                XSDSchema containedSchema = it2.next();
                LinkedList<AttributeGroup> attributeGroups = containedSchema.getAttributeGroups();

                // Check each schemata for attributeGroups and add their names to the set
                for (Iterator<AttributeGroup> it3 = attributeGroups.iterator(); it3.hasNext();) {
                    AttributeGroup attributeGroup = it3.next();
                    alreadyUsedAttributeGroupNames.add(attributeGroup.getName());
                }
            }
        }
        return alreadyUsedAttributeGroupNames;
    }

    /**
     * Method renames all conflicting constraint names. In order to do this all
     * original schemata and their child schemata are traversed and constraint
     * names that appear more than once are renamed. To ensure that no conflicts
     * emerge after renaming a constraint, a set containing all constraint names
     * helps building the new constraint names.
     *
     * New names will be of the form "oldName.number." i.e. "constraintA.2.".
     * This helps the schema to remain readable.
     *
     * @param originalSchemata Set of specified original Schemata. Among these
     * schemata may be Schemata containing constraints with same names. These constraints
     * will be found and renamed.
     */
    public void renameConflictingConstraintNames(Collection<XSDSchema> originalSchemata) {

        // This set contains all names already used for constraints in the originalSchemata and its children
        LinkedHashSet<String> alreadyUsedConstraintNames = getAlreadyUsedConstraintNames(originalSchemata);

        // For each original schema a set of constraints with conflicting names is saved
        HashMap<XSDSchema, LinkedHashSet<SymbolTableRef<SimpleConstraint>>> schemaConflictingConstraintsMap = getConflictingConstraints(originalSchemata);

        // Rename conflicting constraints in all original schemata
        for (Iterator<XSDSchema> it = originalSchemata.iterator(); it.hasNext();) {
            XSDSchema originalSchema = it.next();

            // Check if conflicting constraints exist for this original schema
            if (schemaConflictingConstraintsMap.containsKey(originalSchema)) {
                LinkedHashSet<SymbolTableRef<SimpleConstraint>> symbolTableRefs = schemaConflictingConstraintsMap.get(originalSchema);

                // Repair each symbolTableRef
                for (Iterator<SymbolTableRef<SimpleConstraint>> it2 = symbolTableRefs.iterator(); it2.hasNext();) {
                    SymbolTableRef<SimpleConstraint> symbolTableRef = it2.next();

                    // Rename a single constraint
                    renameSingleConflictingConstraintName(originalSchema, symbolTableRef, alreadyUsedConstraintNames);
                }
            }
        }
    }

    /**
     * This method renames a single constraint. The constraint is specified 
     * through the given SymbolTableRef. Furthermore the set of already used
     * constraint names helps avoiding future conflcts. While the originalSchema
     * specifies the schema hierachy in which the constraint will be renamed.
     *
     * @param originalSchema Root of the schema tree in which the specified
     * constraint will be renamed.
     * @param symbolTableRef Reference to the constraint, which will be rename.
     * @param alreadyUsedConstraintNames Set of already used names. New name must
     * be found outside this set.
     */
    public void renameSingleConflictingConstraintName(XSDSchema originalSchema, SymbolTableRef<SimpleConstraint> symbolTableRef, LinkedHashSet<String> alreadyUsedConstraintNames) {

        // Save old constraint name, for future use
        String oldConstraintName = symbolTableRef.getReference().getName();

        // Generate new constraint name
        String newConstraintName = null;
        int number = 0;

        while (newConstraintName == null) {

            // New names will be of the form "oldName.number." i.e. "constraintA.2."
            String possibleName = symbolTableRef.getReference().getName() + "." + number;

            // If the possibleName is not already used the constraint has a new name
            if (!alreadyUsedConstraintNames.contains(possibleName)) {
                newConstraintName = possibleName;
                alreadyUsedConstraintNames.add(newConstraintName);
            }
            number++;
        }

        // Rename the constraint itself
        symbolTableRef.getReference().setName(newConstraintName);

        // Set new Key for the symbolTableRef
        symbolTableRef.setKey(newConstraintName);

        // Check that HashMaps of SymbolTables are updated
        LinkedHashSet<XSDSchema> containedSchemata = getSchemata(originalSchema);
        for (Iterator<XSDSchema> it = containedSchemata.iterator(); it.hasNext();) {
            XSDSchema containedSchema = it.next();

            // If SymbolTable contains a reference to the old constraint name this reference is replaced by the new one
            if (containedSchema.getKeyAndUniqueSymbolTable().getReference(oldConstraintName) == symbolTableRef) {
                containedSchema.getKeyAndUniqueSymbolTable().setReference(newConstraintName, symbolTableRef);

                // Remove old reference and SymbolTable HashMap entry
                containedSchema.getKeyAndUniqueSymbolTable().removeReference(oldConstraintName);
            }

            // Add new name to constraint name list in current schema and remove old name
            containedSchema.getConstraintNames().add(newConstraintName);
            containedSchema.getConstraintNames().remove(oldConstraintName);

        }
    }

    /**
     * This method returns a HashMap mapping specified original schemata to
     * constraint references. Each refernces represents a constraint which has
     * to be renamed. In order to create this map the original schemata and their
     * child schemata are traversed. Found constraints are compared with other
     * constraints and conflicts are tagged.
     *
     * @param originalSchemata Set of original schemata. Each schema structure
     * is searched for the conflicting constraints.
     * @return HashMap mapping original schemas to sets of conflicting
     * constraints.
     */
    public HashMap<XSDSchema, LinkedHashSet<SymbolTableRef<SimpleConstraint>>> getConflictingConstraints(Collection<XSDSchema> originalSchemata) {

        // This HashMap maps original Schemata to child schemata, like in a schema group
        HashMap<XSDSchema, LinkedHashSet<XSDSchema>> originalParentMap = new HashMap<XSDSchema, LinkedHashSet<XSDSchema>>();

        // For each constraint the schema containing this constraint is saved
        HashMap<SimpleConstraint, XSDSchema> constraintSchemaMap = new HashMap<SimpleConstraint, XSDSchema>();

        // For each constraint name a set of constraints with this name is saved
        HashMap<String, LinkedHashSet<SimpleConstraint>> nameConstraintMap = new HashMap<String, LinkedHashSet<SimpleConstraint>>();

        // Travers the list of given original schemata
        for (Iterator<XSDSchema> it = originalSchemata.iterator(); it.hasNext();) {
            XSDSchema originalSchema = it.next();

            // Each parent schema contains a list of included/redefined/imported schemata
            LinkedHashSet<XSDSchema> containedSchemata = getSchemata(originalSchema);

            // Check each of these schemata for constraints and add them to a HashMap
            for (Iterator<XSDSchema> it2 = containedSchemata.iterator(); it2.hasNext();) {
                XSDSchema containedSchema = it2.next();

                LinkedHashSet<SimpleConstraint> constraints = containedSchema.getKeyAndUniqueSymbolTable().getAllReferencedObjects();
                for (Iterator<SimpleConstraint> it3 = constraints.iterator(); it3.hasNext();) {
                    SimpleConstraint constraint = it3.next();

                    // Put constraint with corresponding schema into constraintSchemaMap
                    constraintSchemaMap.put(constraint, containedSchema);

                    // Update nameConstraintMap to contain the new constraint
                    LinkedHashSet<SimpleConstraint> constraintSet = null;

                    // If constraint name already exist in nameConstraintMap add constraint to existing set
                    if (nameConstraintMap.containsKey(constraint.getName())) {
                        constraintSet = nameConstraintMap.get(constraint.getName());
                    } else {
                        constraintSet = new LinkedHashSet<SimpleConstraint>();
                    }
                    constraintSet.add(constraint);
                    nameConstraintMap.put(constraint.getName(), constraintSet);
                }

                // Update originalParentMap to contain the new schema
                LinkedHashSet<XSDSchema> siblings = null;

                // If orginal parent already exist in map add schema to existing set else construct a new one
                if (originalParentMap.containsKey(originalSchema)) {
                    siblings = originalParentMap.get(originalSchema);
                } else {
                    siblings = new LinkedHashSet<XSDSchema>();
                }
                siblings.add(containedSchema);
                originalParentMap.put(originalSchema, siblings);
            }
        }

        // For each original schema a set of constraints with conflicting names is saved (These constraints are present in the schema hierachy of the original schema)
        HashMap<XSDSchema, LinkedHashSet<SymbolTableRef<SimpleConstraint>>> schemaConflictingConstraintsMap = new HashMap<XSDSchema, LinkedHashSet<SymbolTableRef<SimpleConstraint>>>();

        // Check for all constraint names if a name appears more than once
        for (Iterator<String> it = nameConstraintMap.keySet().iterator(); it.hasNext();) {
            String constraintName = it.next();
            LinkedHashSet<SimpleConstraint> constraintSet = nameConstraintMap.get(constraintName);

            if (constraintSet.size() > 1) {

                // If their are more than one constraint with the same name, these constraints are added to the schemaConflictingConstraintsMap
                for (Iterator<SimpleConstraint> it2 = constraintSet.iterator(); it2.hasNext();) {
                    SimpleConstraint constraint = it2.next();

                    // Find original parent XSDSchema of the schema that contains this constraint
                    XSDSchema constraintOriginalSchema = null;
                    for (Iterator<XSDSchema> it3 = originalParentMap.keySet().iterator(); it3.hasNext();) {
                        XSDSchema originalSchema = it3.next();

                        if (originalParentMap.get(originalSchema).contains(constraintSchemaMap.get(constraint))) {
                            constraintOriginalSchema = originalSchema;
                        }
                    }

                    // If no entry for this original schema exist initialize a new one
                    if (!schemaConflictingConstraintsMap.containsKey(constraintOriginalSchema)) {
                        schemaConflictingConstraintsMap.put(constraintOriginalSchema, new LinkedHashSet<SymbolTableRef<SimpleConstraint>>());
                    }

                    // Add new symbolTableRef to the set
                    LinkedHashSet<SymbolTableRef<SimpleConstraint>> symbolTableRefs = schemaConflictingConstraintsMap.get(constraintOriginalSchema);
                    symbolTableRefs.add(constraintSchemaMap.get(constraint).getKeyAndUniqueSymbolTable().getReference(constraint.getName()));
                    schemaConflictingConstraintsMap.put(constraintOriginalSchema, symbolTableRefs);
                }
            }
        }
        return schemaConflictingConstraintsMap;
    }

    /**
     * Returns a set of all constraint names already used in the set of given
     * original schemata. Each name is only contained once, because this is a set.
     *
     * @param originalSchemata Set of original schemata. Each schema contains
     * constraints either direct or indirect through child schemata.
     * @return Set of all constraint names contained in the specified schema set.
     */
    public LinkedHashSet<String> getAlreadyUsedConstraintNames(Collection<XSDSchema> originalSchemata) {

        // This set contains all names used for constraints in the hierachy of the specified original schemata
        LinkedHashSet<String> alreadyUsedConstraintNames = new LinkedHashSet<String>();

        // Travers the list of given original schemata to find contained schemata
        for (Iterator<XSDSchema> it = originalSchemata.iterator(); it.hasNext();) {
            XSDSchema originalSchema = it.next();

            // For each original schema build a set of contained schemata (child schemata + parent)
            LinkedHashSet<XSDSchema> containedSchemata = getSchemata(originalSchema);
            for (Iterator<XSDSchema> it2 = containedSchemata.iterator(); it2.hasNext();) {
                XSDSchema containedSchema = it2.next();
                LinkedHashSet<SimpleConstraint> constraints = containedSchema.getKeyAndUniqueSymbolTable().getAllReferencedObjects();

                // Check each schemata for constraints and add their names to the set
                for (Iterator<SimpleConstraint> it3 = constraints.iterator(); it3.hasNext();) {
                    SimpleConstraint constraint = it3.next();
                    alreadyUsedConstraintNames.add(constraint.getName());
                }
            }
        }
        return alreadyUsedConstraintNames;
    }
}
