package de.tudortmund.cs.bonxai.xsd.tools;

import de.tudortmund.cs.bonxai.xsd.*;
import java.util.*;

/**
 * This class contains basic methods most resolver classes are in need of. Each
 * resolver class extends this abstract class. 
 *
 * @author Dominik Wolff
 */
public abstract class ResolverTool {

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
}
