package eu.fox7.bonxai.xsd.setOperations;

import eu.fox7.bonxai.common.IdentifiedNamespace;
import eu.fox7.bonxai.common.NamespaceList;
import eu.fox7.bonxai.xsd.ForeignSchema;
import eu.fox7.bonxai.xsd.ImportedSchema;
import eu.fox7.bonxai.xsd.IncludedSchema;
import eu.fox7.bonxai.xsd.RedefinedSchema;
import eu.fox7.bonxai.xsd.XSDSchema;
import eu.fox7.bonxai.xsd.automaton.ParticleAutomatons.exceptions.*;
import eu.fox7.bonxai.xsd.automaton.TypeAutomatons.exceptions.*;
import eu.fox7.bonxai.xsd.automaton.exceptions.*;
import eu.fox7.bonxai.xsd.setOperations.difference.DifferenceConstructor;
import eu.fox7.bonxai.xsd.setOperations.intersection.IntersectionConstructor;
import eu.fox7.bonxai.xsd.setOperations.union.UnionConstructor;
import eu.fox7.bonxai.xsd.tools.ComplexTypeInheritanceResolver;
import eu.fox7.bonxai.xsd.tools.EDCFixer;
import eu.fox7.bonxai.xsd.tools.ElementInheritanceResolver;
import eu.fox7.bonxai.xsd.tools.ForeignSchemaResolver;
import eu.fox7.bonxai.xsd.tools.IncludedSchemaResolver;
import eu.fox7.bonxai.xsd.tools.RedefinedSchemaResolver;
import eu.fox7.bonxai.xsd.tools.SchemaComponentRenamer;

import java.util.*;

/**
 * This class is the main class of the set operations library. Methods to
 * generate schema unions, intersections and differences are provided.
 *
 * Before calling the calling the corresponding constructor classes a set of
 * schema groups is build. Because each set operation has special needs the
 * construction of these schema groups vary from operation to operation. But in
 * order to reused functions needed by every schema construction these groups
 * are constructed here.
 *
 * @author Dominik Wolff
 */
public class SetOperationsMain {

    /**
     * Computes the union of a given schema set. The schemata are specified
     * trough their file paths. Constructed union schemata are written to
     * the given working directory.
     *
     * @param originalSchemas schemas, which should be united.
     * @param workingDirectory Directory where the new union schema files are
     * written to.
     * @throws InvalidTypeStateContentException Exception that is thrown if a
     * type automaton state contains invalid content.
     * @throws EmptyProductTypeStateFieldException Exception which is thrown
     * if a product type state contains no type states.
     * @throws NonDeterministicFiniteAutomataException Exception which is thrown
     * if a type automaton is a no deterministic finite automaton.
     * @throws Exception Exception which is thrown if the parser has a
     * complication.
     */
    public Collection<XSDSchema> union(Collection<XSDSchema> originalSchemas, String workingDirectory) throws InvalidTypeStateContentException, EmptyProductTypeStateFieldException, NonDeterministicFiniteAutomataException, Exception {
    	originalSchemas = postProcessOriginalSchemas(originalSchemas, workingDirectory);
    	
        // Get namespace to namespace abbreviation map
        LinkedHashMap<String, String> namespaceAbbreviationMap = getNamespaceAbbreviationMap(originalSchemas);

        // Build schema groups with flattened original and imported schemata
        LinkedHashSet<SchemaGroup> schemaGroups = buildSchemaGroups(originalSchemas);

        // Get a list of all old schemata (contains imported schemata and original schemata as well)
        LinkedHashSet<XSDSchema> oldSchemata = getOldSchemata(originalSchemas);

        // HashMap containing a schema name for each schema group
        HashMap<SchemaGroup, String> schemaGroupSchemaNameMap = new HashMap<SchemaGroup, String>();

        // Create schema names for all schema groups
        for (Iterator<SchemaGroup> it = schemaGroups.iterator(); it.hasNext();) {
            SchemaGroup schemaGroup = it.next();

            // Set containing the names of all minuend schemata in sorted order
            TreeSet<String> sortedMinuendSchemaNames = new TreeSet<String>(schemaGroup.getMinuendSchemaNames());

            // New schema name. The name has the form: "Union(SchemaA.SchemaB.SchemaC)"
            String schemaGroupSchemaName = "Union(";

            // Generate new name from sorted minuend schema names
            for (Iterator<String> it2 = sortedMinuendSchemaNames.iterator(); it2.hasNext();) {
                String minuendSchemaName = it2.next();
                schemaGroupSchemaName += minuendSchemaName;

                if (it2.hasNext()) {
                    schemaGroupSchemaName += ".";
                } else {
                    schemaGroupSchemaName += ")";
                }
            }

            // If the new schema name already exist construct a new name by adding (number) to the end of the original name
            int number = 1;
            String originalName = schemaGroupSchemaName;

            // Check if the schema name is already in use
            while (schemaGroupSchemaNameMap.containsValue(schemaGroupSchemaName)) {

                schemaGroupSchemaName += originalName + "(" + number + ")";
                number++;
            }

            // Add new schema name and schema group to map
            schemaGroupSchemaNameMap.put(schemaGroup, schemaGroupSchemaName);
        }

        // Construct new union schemata from schema groups via the UnionConstructor
        UnionConstructor unionConstructor = new UnionConstructor(schemaGroups, workingDirectory, schemaGroupSchemaNameMap, namespaceAbbreviationMap, oldSchemata);
        LinkedHashSet<XSDSchema> unionSchemata = unionConstructor.constructUnionSchemata();
        
        return unionSchemata;
    }
    
    /**
     * Computes the intersection of a given schema set. The schemata are specified
     * trough their file paths. Constructed intersection schemata are written to
     * the given working directory.
     * 
     * @param originalSchemaFilePaths File paths to the schemata, which should
     * be intersected.
     * @param workingDirectory Directory where the new intersection schema files
     * are written to.
     * @throws InvalidTypeStateContentException Exception that is thrown if a
     * type automaton state contains invalid content.
     * @throws NonDeterministicFiniteAutomataException Exception which is thrown
     * if a type automaton is a no deterministic finite automaton.
     * @throws EmptyProductTypeStateFieldException Exception which is thrown
     * if a product type state contains no type states.
     * @throws EmptySubsetParticleStateFieldException Exception which is thrown
     * if a subset state contains no particle states.
     * @throws EmptyProductParticleStateFieldException Exception which is thrown
     * if a product state contains no particle states.
     * @throws NotSupportedParticleAutomatonException Exception which is thrown
     * if particle automaton is not supported for a given operation.
     * @throws Exception Exception which is thrown if the parser has a
     * complication.
     */
    public Collection<XSDSchema> intersection(Collection<XSDSchema> originalSchemas, String workingDirectory) throws InvalidTypeStateContentException, NonDeterministicFiniteAutomataException, EmptyProductTypeStateFieldException, EmptySubsetParticleStateFieldException, EmptyProductParticleStateFieldException, NotSupportedParticleAutomatonException, Exception {
        // resolve inheritance and conflicting component names
    	originalSchemas = postProcessOriginalSchemas(originalSchemas, workingDirectory);

        // Get namespace to namespace abbreviation map
        LinkedHashMap<String, String> namespaceAbbreviationMap = getNamespaceAbbreviationMap(originalSchemas);

        // Build schema groups with flattened original and imported schemata
        LinkedHashSet<SchemaGroup> schemaGroups = buildSchemaGroups(originalSchemas);

        // Get a list of all old schemata (contains imported schemata and original schemata as well)
        LinkedHashSet<XSDSchema> oldSchemata = getOldSchemata(originalSchemas);

        // Check for all schema groups if they are empty
        for (Iterator<SchemaGroup> it = new LinkedHashSet<SchemaGroup>(schemaGroups).iterator(); it.hasNext();) {
            SchemaGroup schemaGroup = it.next();

            // Check if the set of original parent schemata of the schema group are equivalent to the set of original schemata, if not remove schema group because intersection is empty
            if (!(schemaGroup.getOriginalParents().containsAll(originalSchemas) && originalSchemas.containsAll(schemaGroup.getOriginalParents()))) {
                schemaGroups.remove(schemaGroup);
            }
        }

        // HashMap containing a schema name for each schema group
        HashMap<SchemaGroup, String> schemaGroupSchemaNameMap = new HashMap<SchemaGroup, String>();

        // Create schema names for all schema groups
        for (Iterator<SchemaGroup> it = schemaGroups.iterator(); it.hasNext();) {
            SchemaGroup schemaGroup = it.next();

            // Set containing the names of all minuend schemata in sorted order
            TreeSet<String> sortedMinuendSchemaNames = new TreeSet<String>(schemaGroup.getMinuendSchemaNames());

            // New schema name. The name has the form: "Intersection(SchemaA.SchemaB.SchemaC)"
            String schemaGroupSchemaName = "Intersection(";

            // Generate new name from sorted minuend schema names
            for (Iterator<String> it2 = sortedMinuendSchemaNames.iterator(); it2.hasNext();) {
                String minuendSchemaName = it2.next();
                schemaGroupSchemaName += minuendSchemaName;

                if (it2.hasNext()) {
                    schemaGroupSchemaName += ".";
                } else {
                    schemaGroupSchemaName += ")";
                }
            }

            // If the new schema name already exist construct a new name by adding (number) to the end of the original name
            int number = 1;
            String originalName = schemaGroupSchemaName;

            // Check if the schema name is already in use
            while (schemaGroupSchemaNameMap.containsValue(schemaGroupSchemaName)) {

                schemaGroupSchemaName += originalName + "(" + number + ")";
                number++;
            }

            // Add new schema name and schema group to map
            schemaGroupSchemaNameMap.put(schemaGroup, schemaGroupSchemaName);
        }

        // For each schema group merge schemata with same parent. Because for schemata with same parent no intersection is needed.
        for (Iterator<SchemaGroup> it = schemaGroups.iterator(); it.hasNext();) {
            SchemaGroup schemaGroup = it.next();

            // For all parents check if schemata with same parent schema exist
            for (Iterator<XSDSchema> it2 = schemaGroup.getOriginalParents().iterator(); it2.hasNext();) {
                XSDSchema originalSchema = it2.next();
                LinkedHashSet<XSDSchema> schemata = schemaGroup.getChildSchemata(originalSchema);

                // If an original schema has more than one child in the schema group merge schemata
                if (schemata.size() > 1) {
                    schemaGroup.mergeMinuendSchema(schemata, originalSchema);
                }
            }
        }

        // Construct new intersection schemata from schema groups via the IntersectionConstructor
        IntersectionConstructor intersectionConstructor = new IntersectionConstructor(schemaGroups, workingDirectory, schemaGroupSchemaNameMap, namespaceAbbreviationMap, oldSchemata);
        LinkedHashSet<XSDSchema> intersectionSchemata = intersectionConstructor.constructIntersectionSchemata();

        return intersectionSchemata;
    }
    
    /**
     * Computes the difference of a given schema set. The schemata are specified
     * trough their file paths. Constructed difference schemata are written to
     * the given working directory.
     *
     * Because the difference construction is more different than other the
     * difference method is more complex than the union or intersection method.
     *
     * @param XSDSchema originalMinuendSchema minuend Schema
     * @param XSDSchema originalSubtrahendSchema subtrahend Schema
     * @param workingDirectory Directory where the new difference schema files
     * are written to.
     * @throws InvalidTypeStateContentException Exception that is thrown if a
     * type automaton state contains invalid content.
     * @throws EmptyProductTypeStateFieldException Exception which is thrown
     * if a product type state contains no type states.
     * @throws NonDeterministicFiniteAutomataException Exception which is thrown
     * if a type automaton is a no deterministic finite automaton.
     * @throws EmptySubsetParticleStateFieldException Exception which is thrown
     * if a subset state contains no particle states.
     * @throws UniqueParticleAttributionViolationException Exception which is thrown
     * if the UPA-constraint is violated.
     * @throws EmptyProductParticleStateFieldException Exception which is thrown
     * if a product state contains no particle states.
     * @throws NoUniqueStateNumbersException Exception which is thrown if two
     * states with the same state number exist in the specified automaton.
     * @throws NoDestinationStateFoundException Exception which is thrown
     * if the no destination state for a given transition was found.
     * @throws NotSupportedParticleAutomatonException Exception which is thrown
     * if particle automaton is not supported for a given operation.
     * @throws Exception Exception which is thrown if the parser has a
     * complication.
     */
    public Collection<XSDSchema> difference(XSDSchema originalMinuendSchema, XSDSchema originalSubtrahendSchema, String workingDirectory) throws InvalidTypeStateContentException, EmptyProductTypeStateFieldException, NonDeterministicFiniteAutomataException, EmptySubsetParticleStateFieldException, UniqueParticleAttributionViolationException, EmptyProductParticleStateFieldException, NoUniqueStateNumbersException, NoDestinationStateFoundException, NotSupportedParticleAutomatonException, Exception {
        // Set of schemata to store changed schemata
        LinkedHashSet<XSDSchema> changedSchemata = new LinkedHashSet<XSDSchema>();

        // For original minuend and subtrahend schemata resolve substitutionGroups
        ElementInheritanceResolver elementInheritanceResolver = new ElementInheritanceResolver(originalMinuendSchema);
        changedSchemata.addAll(elementInheritanceResolver.resolveSubstitutionGroups());
        elementInheritanceResolver = new ElementInheritanceResolver(originalSubtrahendSchema);
        changedSchemata.addAll(elementInheritanceResolver.resolveSubstitutionGroups());

        // For original minuend and subtrahend schemata resolve type inheritance
        ComplexTypeInheritanceResolver complexTypeInheritanceResolver = new ComplexTypeInheritanceResolver(originalMinuendSchema);
        originalMinuendSchema = complexTypeInheritanceResolver.getSchemaWithoutComplexTypeInheritance(workingDirectory);
        complexTypeInheritanceResolver = new ComplexTypeInheritanceResolver(originalSubtrahendSchema);
        originalSubtrahendSchema = complexTypeInheritanceResolver.getSchemaWithoutComplexTypeInheritance(workingDirectory);

        // Get namespace to namespace abbreviation map
        LinkedHashSet<XSDSchema> originalSchemata = new LinkedHashSet<XSDSchema>();
        originalSchemata.add(originalMinuendSchema);
        originalSchemata.add(originalSubtrahendSchema);
        LinkedHashMap<String, String> namespaceAbbreviationMap = getNamespaceAbbreviationMap(originalSchemata);

        // For each targetNamespace a new schemaGroup is build while originalMinuendSchema and originalSubtrahendSchema are placed in corresponding groups
        LinkedHashSet<SchemaGroup> schemaGroups = new LinkedHashSet<SchemaGroup>();

        // Check if schema group with original minuend schema targetnamespace already exists or get new one
        String targetNamespace = originalMinuendSchema.getTargetNamespace();
        SchemaGroup currentSchemaGroup = getSchemaGroup(schemaGroups, targetNamespace);

        // Add original minuend schema to current schema group
        currentSchemaGroup.addMinuendSchema(originalMinuendSchema, originalMinuendSchema, true);
        schemaGroups.add(currentSchemaGroup);

        // Check if schema group with original subrtahend schema targetnamespace already exists or get new one
        targetNamespace = originalSubtrahendSchema.getTargetNamespace();
        currentSchemaGroup = getSchemaGroup(schemaGroups, targetNamespace);

        // Add original subtrahend schema to current schema group
        currentSchemaGroup.addSubtrahendSchema(originalSubtrahendSchema, originalMinuendSchema);
        schemaGroups.add(currentSchemaGroup);

        // For each of the two original schemata, all imported schemata found in their schema hierarchy are added to corresponding schema groups
        for (Iterator<ImportedSchema> it2 = getImportedForeignSchemata(originalMinuendSchema).iterator(); it2.hasNext();) {
            ImportedSchema importedForeignSchema = it2.next();

            // If the importedForeignSchema contains a schema this is added to a schema group with the same namespace
            if (importedForeignSchema.getSchema() != null) {

                // The imported XSDSchema is not an original XSDSchema
                XSDSchema importedSchema = importedForeignSchema.getSchema();
                targetNamespace = importedSchema.getTargetNamespace();
                currentSchemaGroup = getSchemaGroup(schemaGroups, targetNamespace);

                // Add imported minuend schema to current schema group
                currentSchemaGroup.addMinuendSchema(importedSchema, originalMinuendSchema, false);
                schemaGroups.add(currentSchemaGroup);
            }
        }

        for (Iterator<ImportedSchema> it2 = getImportedForeignSchemata(originalSubtrahendSchema).iterator(); it2.hasNext();) {
            ImportedSchema importedForeignSchema = it2.next();

            // If the importedForeignSchema contains a schema this is added to a schema group with the same namespace
            if (importedForeignSchema.getSchema() != null) {

                // The imported XSDSchema is not an original XSDSchema
                XSDSchema importedSchema = importedForeignSchema.getSchema();
                targetNamespace = importedSchema.getTargetNamespace();
                currentSchemaGroup = getSchemaGroup(schemaGroups, targetNamespace);

                // Add imported subtrahend schema to current schema group
                currentSchemaGroup.addSubtrahendSchema(importedSchema, originalMinuendSchema);
                schemaGroups.add(currentSchemaGroup);
            }
        }

        // For all schemata contained in schemaGroups resolve includes and redefines
        for (Iterator<SchemaGroup> it = schemaGroups.iterator(); it.hasNext();) {
            SchemaGroup schemaGroup = it.next();

            for (Iterator<XSDSchema> it2 = schemaGroup.getMinuendSchemata().iterator(); it2.hasNext();) {
                XSDSchema fileGroupSchema = it2.next();

                // FlattenSchema resolves includes and redefines until the schema is "flat" (Only imports remain as direct children of the schema in schema hierachy)
                flattenSchema(fileGroupSchema);
            }

            for (Iterator<XSDSchema> it2 = schemaGroup.getSubtrahendSchemata().iterator(); it2.hasNext();) {
                XSDSchema fileGroupSchema = it2.next();

                // FlattenSchema resolves includes and redefines until the schema is "flat" (Only imports remain as direct children of the schema in schema hierachy)
                flattenSchema(fileGroupSchema);
            }
        }

        // Get a list of all old schemata (contains imported schemata and original schemata as well)
        LinkedHashSet<XSDSchema> oldSchemata = getOldSchemata(originalSchemata);

        // For the set of all schema groups remove unnecessary schemata and unnecessary schema groups
        for (Iterator<SchemaGroup> it = new LinkedHashSet<SchemaGroup>(schemaGroups).iterator(); it.hasNext();) {
            SchemaGroup schemaGroup = it.next();

            // Remove minuend schemata which are also subtrahend schemata
            for (Iterator<XSDSchema> it2 = schemaGroup.getSubtrahendSchemata().iterator(); it2.hasNext();) {
                XSDSchema subtrahendSchema = it2.next();

                // Check that the schemata have the same schema location and where not changed when the inheritance was removed.
                if (schemaGroup.containsMinuendSchema(subtrahendSchema) && !changedSchemata.contains(subtrahendSchema) && !changedSchemata.contains(schemaGroup.getMinuendSchema(subtrahendSchema.getSchemaLocation()))) {
                    schemaGroup.removeSchema(subtrahendSchema.getSchemaLocation());
                }
            }

            // Remove schema group which contains no minuend schema
            if (schemaGroup.getMinuendSchemata().size() == 0) {
                schemaGroups.remove(schemaGroup);
            }
        }

        // HashMap containing a schema name for each schema group
        HashMap<SchemaGroup, String> schemaGroupSchemaNameMap = new HashMap<SchemaGroup, String>();

        // Create schema names for all schema groups
        for (Iterator<SchemaGroup> it = schemaGroups.iterator(); it.hasNext();) {
            SchemaGroup schemaGroup = it.next();

            // Set containing the names of all minuend schemata in sorted order
            TreeSet<String> sortedMinuendSchemaNames = new TreeSet<String>(schemaGroup.getMinuendSchemaNames());

            // Set containing the names of all subtrahend schemata in sorted order
            TreeSet<String> sortedSubtrahendSchemaNames = new TreeSet<String>(schemaGroup.getSubtrahendSchemaNames());

            // New schema name. The name has the form: "Difference(SchemaA.SchemaB-SchemaC)"
            String schemaGroupSchemaName = "Difference(";

            // Generate name part from sorted minuend schema names
            for (Iterator<String> it2 = sortedMinuendSchemaNames.iterator(); it2.hasNext();) {
                String minuendSchemaName = it2.next();
                schemaGroupSchemaName += minuendSchemaName;

                if (it2.hasNext()) {
                    schemaGroupSchemaName += ".";
                } else {
                    schemaGroupSchemaName += "-";
                }
            }

            // Generate name part from sorted subtrahend schema names
            for (Iterator<String> it2 = sortedSubtrahendSchemaNames.iterator(); it2.hasNext();) {
                String subtrahendSchemaName = it2.next();
                schemaGroupSchemaName += subtrahendSchemaName;

                if (it2.hasNext()) {
                    schemaGroupSchemaName += ".";
                } else {
                    schemaGroupSchemaName += ")";
                }
            }

            if (schemaGroupSchemaName.endsWith("-")) {
                schemaGroupSchemaName += "null)";
            }

            // If the new schema name already exist construct a new name by adding (number) to the end of the original name
            int number = 1;
            String originalName = schemaGroupSchemaName;

            // Check if the schema name is already in use
            while (schemaGroupSchemaNameMap.containsValue(schemaGroupSchemaName)) {

                schemaGroupSchemaName += originalName + "(" + number + ")";
                number++;
            }

            // Add new schema name and schema group to map
            schemaGroupSchemaNameMap.put(schemaGroup, schemaGroupSchemaName);
        }

        // For each schema group merge minuend schemata and merge subtrahend schemata if more than one schema exists on any side.
        for (Iterator<SchemaGroup> it = schemaGroups.iterator(); it.hasNext();) {
            SchemaGroup schemaGroup = it.next();

            // Merge minuend schemata if more than one minuend schema exist
            if (schemaGroup.getMinuendSchemata().size() > 1) {
                schemaGroup.mergeMinuendSchema(schemaGroup.getMinuendSchemata(), originalMinuendSchema);
            }
            // Merge subtrahend schemata if more than one subtrahend schema exist
            if (schemaGroup.getSubtrahendSchemata().size() > 1) {
                schemaGroup.mergeSubtrahendSchema(schemaGroup.getSubtrahendSchemata(), originalMinuendSchema);
            }
        }

        // Construct new difference schemata from schema groups via the DifferenceConstructor
        DifferenceConstructor differenceConstructor = new DifferenceConstructor(schemaGroups, workingDirectory, schemaGroupSchemaNameMap, namespaceAbbreviationMap, oldSchemata);
        XSDSchema differenceSchema = differenceConstructor.constructDifferenceSchemata();

        // Fix EDC-rule
        EDCFixer edcFixer = new EDCFixer();
        differenceSchema = edcFixer.fixEDCWithoutInheritance(differenceSchema, workingDirectory);
        LinkedHashSet<XSDSchema> differenceSchemata = getSchemata(differenceSchema);
        
        return differenceSchemata;
    }

    /**
     * This method retuns a set of schemata. This set consists of all schemata
     * in the schema hierachy of the parent schema, including the parent schema.
     *
     * @param parentSchema Parent schema which contains other schemata. These
     * schemata and the parent schema together build the returned schema set.
     * Contained schemta are not necessarily direct children of the parent schema.
     * @return Set of schemata including the parent schema and its children.
     */
    public LinkedHashSet<XSDSchema> getSchemata(XSDSchema parentSchema) {

        // Initialize stack and schema set
        LinkedHashSet<XSDSchema> schemata = new LinkedHashSet<XSDSchema>();
        Stack<XSDSchema> stack = new Stack<XSDSchema>();

        // Add parent schema to stack and schema set
        schemata.add(parentSchema);
        stack.add(parentSchema);

        // As long as the stack contains a schema check next schema for foreign schemata
        while (!stack.isEmpty()) {
            XSDSchema currentSchema = stack.pop();
            LinkedList<ForeignSchema> foreignSchemata = currentSchema.getForeignSchemas();

            // For each contained foreign schema add schema to schema set and stack
            for (Iterator<ForeignSchema> it = foreignSchemata.iterator(); it.hasNext();) {
                ForeignSchema foreignSchema = it.next();

                // Check if schema is not null and not already contained in the schema set
                if (foreignSchema.getSchema() != null && !schemata.contains(foreignSchema.getSchema())) {
                    schemata.add(foreignSchema.getSchema());
                    stack.add(foreignSchema.getSchema());
                }
            }
        }
        return schemata;
    }

    /**
     * Inheritance is resolved and conflicting component names are renamed.
     *
     * @param originalSchemas original schemas.
     * @param workingDirectory Directory where the new union schema files are
     * written to.
     * @return A set of original schemata. For each schema type and element
     * inheritance is resolved.
     * @throws Exception Exception which is thrown if the parser has a
     * complication.
     */
    Collection<XSDSchema> postProcessOriginalSchemas(Collection<XSDSchema> originalSchemas, String workingDirectory) throws InvalidTypeStateContentException, EmptyProductTypeStateFieldException, NonDeterministicFiniteAutomataException, EmptySubsetTypeStateFieldException {
        // For each schema of the original schemata resolve substitutionGroups
        for (Iterator<XSDSchema> it = originalSchemas.iterator(); it.hasNext();) {
            XSDSchema originalSchema = it.next();

            ElementInheritanceResolver elementInheritanceResolver = new ElementInheritanceResolver(originalSchema);
            elementInheritanceResolver.resolveSubstitutionGroups();
        }

        // New original schemata set
        LinkedHashSet<XSDSchema> newOriginalSchemata = new LinkedHashSet<XSDSchema>();

        // For each schema of the original schemata resolve type inheritance
        for (Iterator<XSDSchema> it = originalSchemas.iterator(); it.hasNext();) {
            XSDSchema originalSchema = it.next();

            ComplexTypeInheritanceResolver complexTypeInheritanceResolver = new ComplexTypeInheritanceResolver(originalSchema);
            newOriginalSchemata.add(complexTypeInheritanceResolver.getSchemaWithoutComplexTypeInheritance(workingDirectory));
        }
        originalSchemas = newOriginalSchemata;

        // Rename intersecting Types, Groups, AttributeGroups and SimpleConstraints
        SchemaComponentRenamer schemaComponentRenamer = new SchemaComponentRenamer();

        schemaComponentRenamer.renameConflictingTypeNames(originalSchemas);
        schemaComponentRenamer.renameConflictingGroupNames(originalSchemas);
        schemaComponentRenamer.renameConflictingAttributeGroupNames(originalSchemas);
        schemaComponentRenamer.renameConflictingConstraintNames(originalSchemas);

        // Return the new updated set of original schemata
        return originalSchemas;
    }

    /**
     * Constructs schema groups for original schemata. Each original schema and
     * the schemata imported by these schemata are added to corresponding schema
     * groups. Further more all schemata contained in schema groups are flattened.
     *
     * @param originalSchemata Set of original schemata. These are schemata used
     * to generate a set operation.
     * @return Set of schema groups, filled with schemata found in the schema
     * structure of the original schemata. Included and imported schemata are
     * merged with their parents.
     */
    private LinkedHashSet<SchemaGroup> buildSchemaGroups(Collection<XSDSchema> originalSchemata) {

        // For each targetNamespace a new schemaGroup is build while schemata are placed in corresponding groups
        LinkedHashSet<SchemaGroup> schemaGroups = new LinkedHashSet<SchemaGroup>();

        for (Iterator<XSDSchema> it = originalSchemata.iterator(); it.hasNext();) {
            XSDSchema originalSchema = it.next();
            String targetNamespace = originalSchema.getTargetNamespace();

            // Check if  schema group already exists or get new one
            SchemaGroup currentSchemaGroup = getSchemaGroup(schemaGroups, targetNamespace);

            // Add original XSDSchema to current schema group
            currentSchemaGroup.addMinuendSchema(originalSchema, originalSchema, true);
            schemaGroups.add(currentSchemaGroup);
        }

        // For each original schema, all imported schemata found in its schema hierarchy are added to corresponding schema groups
        for (Iterator<XSDSchema> it = originalSchemata.iterator(); it.hasNext();) {
            XSDSchema originalSchema = it.next();

            for (Iterator<ImportedSchema> it2 = getImportedForeignSchemata(originalSchema).iterator(); it2.hasNext();) {
                ImportedSchema importedForeignSchema = it2.next();

                // If the importedForeignSchema contains a schema this is added to a schema group with the same namespace
                if (importedForeignSchema.getSchema() != null) {

                    // The imported XSDSchema is not an original XSDSchema
                    XSDSchema importedSchema = importedForeignSchema.getSchema();
                    String targetNamespace = importedSchema.getTargetNamespace();

                    // Check if  schema group already exists or get new one
                    SchemaGroup currentSchemaGroup = getSchemaGroup(schemaGroups, targetNamespace);

                    // Add imported XSDSchema to current schema group if not already present
                    currentSchemaGroup.addMinuendSchema(importedSchema, originalSchema, false);
                    schemaGroups.add(currentSchemaGroup);
                }
            }
        }

        // For all schemata contained in schemaGroups resolve includes and redefines
        for (Iterator<SchemaGroup> it = schemaGroups.iterator(); it.hasNext();) {
            SchemaGroup schemaGroup = it.next();

            for (Iterator<XSDSchema> it2 = schemaGroup.getMinuendSchemata().iterator(); it2.hasNext();) {
                XSDSchema fileGroupSchema = it2.next();

                // FlattenSchema resolves includes and redefines until the schema is "flat" (Only imports remain as direct children of the schema in schema hierachy)
                flattenSchema(fileGroupSchema);
            }
        }

        // Return schemaGroups with flattened original and imported schemata
        return schemaGroups;
    }

    /**
     * Constructs a set of old schemata. Each original schema and the schemata
     * imported by these schemata are added to schema set. Further more all
     * schemata contained are flattened.
     *
     * @param originalSchemata Set of original schemata. These are schemata used
     * to generate a set operation.
     * @return Set of schemata, filled with schemata found in the schema
     * structure of the original schemata. Included and imported schemata are
     * merged with their parents.
     */
    private LinkedHashSet<XSDSchema> getOldSchemata(Collection<XSDSchema> originalSchemata) {

        // Original schemata are contained in the old schema set
        LinkedHashSet<XSDSchema> oldSchemata = new LinkedHashSet<XSDSchema>();
        oldSchemata.addAll(originalSchemata);

        // For each original schema, all imported schemata found in its schema hierarchy are added to the old schemata set
        for (Iterator<XSDSchema> it = originalSchemata.iterator(); it.hasNext();) {
            XSDSchema originalSchema = it.next();

            for (Iterator<ImportedSchema> it2 = getAllImportedForeignSchemata(originalSchema).iterator(); it2.hasNext();) {
                ImportedSchema importedForeignSchema = it2.next();

                // If the importedForeignSchema contains a schema this is added to the old schemata set
                if (importedForeignSchema.getSchema() != null) {
                    oldSchemata.add(importedForeignSchema.getSchema());
                }
            }
        }

        for (Iterator<XSDSchema> it = oldSchemata.iterator(); it.hasNext();) {
            XSDSchema schema = it.next();

            // Check if attributes in schema are per default qualified
            if (schema.getAttributeFormDefault() != null && schema.getAttributeFormDefault() == XSDSchema.Qualification.qualified) {
                ForeignSchemaResolver foreignSchemaResolver = new ForeignSchemaResolver();
                foreignSchemaResolver.removeAttributeFormDefault(schema);
            }

            // Check if attributes in schema are per default qualified
            if (schema.getElementFormDefault() != null && schema.getElementFormDefault() == XSDSchema.Qualification.qualified) {
                ForeignSchemaResolver foreignSchemaResolver = new ForeignSchemaResolver();
                foreignSchemaResolver.removeElementFormDefault(schema);
            }
        }

        // For all old schemata resolve includes and redefines
        for (Iterator<XSDSchema> it2 = oldSchemata.iterator(); it2.hasNext();) {
            XSDSchema oldSchema = it2.next();

            // FlattenSchema resolves includes and redefines until the schema is "flat" (Only imports remain as direct children of the schema in schema hierachy)
            flattenSchema(oldSchema);
        }

        // Return schemaGroups with flattened original and imported schemata
        return oldSchemata;
    }

    /**
     * Checks whether a schema group with specified target namespace exists and
     * if it exists returns the schema group. If the schema group does not exist
     * a new schema group is constructed with given target namespace.
     *
     * @param schemaGroups Set of schema groups for which the inclusion is tested.
     * @param targetNamespace Namespace of the sought-after schema group.
     * @return Found schema group or a new schema group
     */
    private SchemaGroup getSchemaGroup(LinkedHashSet<SchemaGroup> schemaGroups, String targetNamespace) {

        // Check if schema group with same targetnamespace already exists
        SchemaGroup currentSchemaGroup = null;

        for (Iterator<SchemaGroup> it2 = schemaGroups.iterator(); it2.hasNext();) {
            SchemaGroup schemaGroup = it2.next();

            if (schemaGroup.getTargetNamespace().equals(targetNamespace)) {
                currentSchemaGroup = schemaGroup;
            }
        }
        // If no schema group for the targetnamespace of the original schema exists, generate a new one
        if (currentSchemaGroup == null) {
            currentSchemaGroup = new SchemaGroup(targetNamespace);
            schemaGroups.add(currentSchemaGroup);
        }
        return currentSchemaGroup;
    }

    /**
     * Method returns all imported schemata present in the given schema
     * structure. The specified schema is the root of the schema structure. If
     * an schema, reached by using includes and redefines from the root, in this
     * structure contains an import-component this is added to the set,
     * which is returned after traversing all schemata.
     *
     * @param schema Root of the schema structure.
     * @return Set containing all import-components found in the specified schema
     * structure.
     */
    private LinkedHashSet<ImportedSchema> getImportedForeignSchemata(XSDSchema schema) {

        // Initialize set to save imported schemata
        LinkedHashSet<ImportedSchema> importedSchemata = new LinkedHashSet<ImportedSchema>();

        // Use stack and set of already seen ForeignSchemata to traveser schema hierarchy
        LinkedHashSet<ForeignSchema> alreadySeenForeignSchemas = new LinkedHashSet<ForeignSchema>();
        Stack<ForeignSchema> stack = new Stack<ForeignSchema>();
        stack.addAll(schema.getForeignSchemas());

        // As long as the stack is not empty check current schema children
        while (!stack.isEmpty()) {

            // If the current schema is an ImportedSchema it is add to the importedSchemata set
            ForeignSchema currentForeignSchema = stack.pop();

            if (currentForeignSchema instanceof ImportedSchema) {
                ImportedSchema currentImportedSchema = (ImportedSchema) currentForeignSchema;
                importedSchemata.add(currentImportedSchema);
            } else {

                // Get ForeinSchemata of the current ForeignSchema and add them to the stack if not already visited
                LinkedList<ForeignSchema> currentForeignSchemaForeignSchemata = currentForeignSchema.getSchema().getForeignSchemas();
                for (Iterator<ForeignSchema> it = currentForeignSchemaForeignSchemata.iterator(); it.hasNext();) {
                    ForeignSchema currentForeignSchemaForeignSchema = it.next();

                    if (!alreadySeenForeignSchemas.contains(currentForeignSchemaForeignSchema)) {
                        stack.add(currentForeignSchemaForeignSchema);
                    }
                }
            }

            // Add current ForeignSchema to the set of already seen ForeignSchemata
            alreadySeenForeignSchemas.add(currentForeignSchema);
        }
        return importedSchemata;
    }

    /**
     * Method returns all imported schemata present in the given schema structure.
     * The specified schema is the root of the schema structure. If a schema
     * in this structure contains an import-component this is added to the set,
     * which is returned after traversing all schemata.
     *
     * @param schema Root of the schema structure.
     * @return Set containing all import-components found in the specified schema
     * structure.
     */
    private LinkedHashSet<ImportedSchema> getAllImportedForeignSchemata(XSDSchema schema) {

        // Initialize set to save imported schemata
        LinkedHashSet<ImportedSchema> importedSchemata = new LinkedHashSet<ImportedSchema>();

        // Use stack and set of already seen ForeignSchemata to traveser schema hierarchy
        LinkedHashSet<ForeignSchema> alreadySeenForeignSchemas = new LinkedHashSet<ForeignSchema>();
        Stack<ForeignSchema> stack = new Stack<ForeignSchema>();
        stack.addAll(schema.getForeignSchemas());

        // As long as the stack is not empty check current schema children
        while (!stack.isEmpty()) {

            // If the current schema is an ImportedSchema it is add to the importedSchemata set
            ForeignSchema currentForeignSchema = stack.pop();

            if (currentForeignSchema instanceof ImportedSchema) {
                ImportedSchema currentImportedSchema = (ImportedSchema) currentForeignSchema;
                importedSchemata.add(currentImportedSchema);
            }

            // Get ForeinSchemata of the current ForeignSchema and add them to the stack if not already visited
            LinkedList<ForeignSchema> currentForeignSchemaForeignSchemata = currentForeignSchema.getSchema().getForeignSchemas();
            for (Iterator<ForeignSchema> it = currentForeignSchemaForeignSchemata.iterator(); it.hasNext();) {
                ForeignSchema currentForeignSchemaForeignSchema = it.next();

                if (!alreadySeenForeignSchemas.contains(currentForeignSchemaForeignSchema)) {
                    stack.add(currentForeignSchemaForeignSchema);
                }
            }

            // Add current ForeignSchema to the set of already seen ForeignSchemata
            alreadySeenForeignSchemas.add(currentForeignSchema);
        }
        return importedSchemata;
    }

    /**
     * This method "flattens" a schema. In other words all included and redefined
     * schemata are integrated into the base schema. Because there is no
     * intersection between components of included/redefined schemata and all
     * components are in the same namespace as the base schema these schemata and
     * the base schema can easily be integrated.
     *
     * @param schema XSDSchema which should be flattened.
     */
    private void flattenSchema(XSDSchema schema) {

        // Initialize ForeignSchemaResolver
        IncludedSchemaResolver includedSchemaResolver = new IncludedSchemaResolver();
        RedefinedSchemaResolver redefinedSchemaResolver = new RedefinedSchemaResolver();

        // As long as the schema is flat ForeignSchemata will be resolved
        boolean flat = false;
        while (!flat) {

            // If no include or redefine elements are present in the schema, it is flat
            flat = true;

            // Resolve all include and redefine elements in the schema
            LinkedList<ForeignSchema> foreignSchemas = schema.getForeignSchemas();
            for (Iterator<ForeignSchema> it = foreignSchemas.iterator(); it.hasNext();) {
                ForeignSchema foreignSchema = it.next();

                if (foreignSchema instanceof RedefinedSchema) {
                    redefinedSchemaResolver.resolveRedefine(foreignSchema.getParentSchema(), (RedefinedSchema) foreignSchema);
                    flat = false;
                }
                if (foreignSchema instanceof IncludedSchema) {
                    includedSchemaResolver.resolveInclude(foreignSchema.getParentSchema(), (IncludedSchema) foreignSchema);
                    flat = false;
                }
            }
        }
    }

    /**
     * This method checks each schema in the schema structure of the original
     * schemata for namespaces and corresponding abbreviations. A map mapping
     * namespaces to abbreviations is returned. It can be used to generate
     * new IdentifiedNamespaces if needed.
     * 
     * @param originalSchemata Set of original schemata. Each schema structure
     * may contain namespace abbreviations found by this method.
     * @return HashMap mapping namespaces to abbreviations. Not every namespace
     * has a unique abbreviation.
     */
    private LinkedHashMap<String, String> getNamespaceAbbreviationMap(Collection<XSDSchema> originalSchemata) {

        // HashMap containing for each namespace a unique abbreviation
        LinkedHashMap<String, String> namespaceAbbreviationMap = new LinkedHashMap<String, String>();

        // List of already used abbreviations
        LinkedList<String> alreadyUsedAbbreviations = new LinkedList<String>();

        // Check find all import-components in original schema set and search for abbreviations in importing schemata
        for (Iterator<XSDSchema> it = originalSchemata.iterator(); it.hasNext();) {
            XSDSchema originalSchema = it.next();

            // Initialize stack and schema set
            LinkedHashSet<XSDSchema> schemata = new LinkedHashSet<XSDSchema>();
            Stack<XSDSchema> stack = new Stack<XSDSchema>();

            // Add parent schema to stack and schema set
            schemata.add(originalSchema);
            stack.add(originalSchema);

            // As long as the stack contains a schema check next schema for foreign schemata and check for namespace abbreviations
            while (!stack.isEmpty()) {
                XSDSchema currentSchema = stack.pop();

                // Get current schema namespace list
                NamespaceList namespaceList = currentSchema.getNamespaceList();
                LinkedHashSet<IdentifiedNamespace> identifiedNamespaces = namespaceList.getIdentifiedNamespaces();

                // Check each identified namespace
                for (Iterator<IdentifiedNamespace> it3 = identifiedNamespaces.iterator(); it3.hasNext();) {
                    IdentifiedNamespace identifiedNamespace = it3.next();

                    // If the namespaceAbbreviationMap does not contain the identified namespace namespace and the abbreviation is not contained in the list of already used abbreviations,
                    // namespace and abbreviation can be added to map
                    if (!namespaceAbbreviationMap.containsKey(identifiedNamespace.getUri()) && !alreadyUsedAbbreviations.contains(identifiedNamespace.getIdentifier())) {

                        namespaceAbbreviationMap.put(identifiedNamespace.getUri(), identifiedNamespace.getIdentifier());
                        alreadyUsedAbbreviations.add(identifiedNamespace.getIdentifier());
                    }
                }
                LinkedList<ForeignSchema> foreignSchemata = currentSchema.getForeignSchemas();

                // For each contained foreign schema add schema to schema set and stack
                for (Iterator<ForeignSchema> it2 = foreignSchemata.iterator(); it2.hasNext();) {
                    ForeignSchema foreignSchema = it2.next();

                    // Check if schema is not null and not already contained in the schema set
                    if (foreignSchema.getSchema() != null && !schemata.contains(foreignSchema.getSchema())) {
                        schemata.add(foreignSchema.getSchema());
                        stack.add(foreignSchema.getSchema());
                    }
                }
            }
        }
        return namespaceAbbreviationMap;
    }
}
