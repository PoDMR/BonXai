/**
 * Copyright 2009-2012 TU Dortmund
 *
 * This file is part of FoXLib.
 *
 * FoXLib is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FoXLib is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with BonXai.  If not, see <http://www.gnu.org/licenses/>.
 */

package eu.fox7.schematoolkit.xsd.tools;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedHashSet;
import java.util.Stack;

import org.xml.sax.SAXException;

import eu.fox7.schematoolkit.xsd.om.ForeignSchema;
import eu.fox7.schematoolkit.xsd.om.ImportedSchema;
import eu.fox7.schematoolkit.xsd.om.IncludedSchema;
import eu.fox7.schematoolkit.xsd.om.XSDSchema;
import eu.fox7.schematoolkit.xsd.parser.XSDParser;
import eu.fox7.schematoolkit.xsd.parser.exceptions.XSDParseException;



/**
 * Class for loading external (foreign) schemas
 * 
 * @author Lars Schmidt, Dominik Wolff
 */
public class ForeignSchemaLoader {

    // Given base schema for the computed schema structure
    private XSDSchema baseSchema;
    // Set for all absolute paths of already seeen schemas
    private LinkedHashSet<String> alreadySeenSchemas;
    // Set of all already seen schema objects
    private LinkedHashSet<XSDSchema> seenSchemas;
    // Setting for checking the edc constraint during the loading progress
    private boolean checkEDC;
    // Stack for all not processed schemata
    private Stack<ForeignSchema> stack;
//    // Global symbolTable for all found types in all found schemas to replace dummy types with the found correct definition of the type
//    private SymbolTable<Type> typeSymbolTable;
//    // Global symbolTable for all found attribute groups in all found schemas to replace dummy attribute groups with the found correct definition of the attribute group
//    private SymbolTable<AttributeGroup> attributeGroupSymbolTable;
//    // Global symbolTable for all found groups in all found schemas to replace dummy groups with the found correct definition of the group
//    private SymbolTable<de.tudortmund.cs.bonxai.xsd.Group> groupSymbolTable;
//    // Global symbolTable for all found attributes in all found schemas to replace dummy attributes with the found correct definition of the attribute
//    private SymbolTable<Attribute> attributeSymbolTable;
//    // Global symbolTable for all found elements in all found schemas to replace dummy elements with the found correct definition of the element
//    private SymbolTable<Element> elementSymbolTable;
//    // Global symbolTable for all found constraints in all found schemas to replace dummy constraints with the found correct definition of the constraint
//    private SymbolTable<SimpleConstraint> keyAndUniqueSymbolTable;

    /**
     * Load all external schemas, parse them and fill all dummy-references with
     * the correct definitions of the found objects.
     * @param baseSchema    Start schema for the loading progress
     * @param checkEDC      Setting for checking the edc constraint during the
     *                      loading progress on each schema
     */
    public ForeignSchemaLoader(XSDSchema baseSchema, boolean checkEDC) {

        // Initialization of all class fields
        this.baseSchema = baseSchema;
        this.alreadySeenSchemas = new LinkedHashSet<String>();
        this.alreadySeenSchemas.add("{" + baseSchema.getTargetNamespace() + "}" + getCanonicalPath(baseSchema.getSchemaLocation()));
        this.seenSchemas = new LinkedHashSet<XSDSchema>();
        this.seenSchemas.add(baseSchema);
        this.checkEDC = checkEDC;

        // Fill all global symboltables
//        this.generateSymboltables(baseSchema);
    }

    /**
     * Main starting method for the loading progress of the ForeignSchemaLoader
     * class
     * @throws FileNotFoundException
     * @throws SAXException
     * @throws IOException
     * @throws URISyntaxException
     * @throws XSDParseException 
     */
    public void findForeignSchemas() throws FileNotFoundException, SAXException, IOException, URISyntaxException, XSDParseException {
        stack = new Stack<ForeignSchema>();

        // Put all ForeignSchemas of the base XSDSchema on top of the stack.
        if (baseSchema.getForeignSchemas() != null) {
            for (ForeignSchema foreignSchema: baseSchema.getForeignSchemas()) {
                stack.add(foreignSchema);
            }
        }

        // As long as the stack contains another ForeignSchema, these schemas are parsed.
        while (!stack.isEmpty()) {
            parseForeignSchema(stack.pop());
        }

//        for (XSDSchema schema: seenSchemas) {
//            setNewRedefinedReferences(schema);
//        }
    }

    /**
     * Parses a single foreignSchema and attaches the generated schema-object to
     * the foreignSchema-object. Foreign schemas contained in the new
     * schema-object will be added to the stack. For each contained schema a new
     * entry in the set of already seen schematas is added. This entry consists
     * of a schmeaLocation string, which is an absolute URI and a
     * target namespace part.
     *
     * @param foreignSchema     foreignSchema holding the schemaLocation of the
     *                          file, that will be parsed
     * @throws FileNotFoundException
     * @throws SAXException
     * @throws IOException
     * @throws URISyntaxException
     */
    private void parseForeignSchema(ForeignSchema foreignSchema) throws XSDParseException, FileNotFoundException, IOException, URISyntaxException {

        if (foreignSchema.getSchemaLocation() != null && !foreignSchema.getSchemaLocation().equals("")) {

            // Initializes the new xsd parser
            XSDParser xsdParser = new XSDParser(this.checkEDC, false);

            // Get target namespace of the schema containing the current ForeignSchema or null if the ForeignSchema is an ImportedSchema
            String newTargetNamespaceForeignSchema = (foreignSchema instanceof ImportedSchema) ? null : foreignSchema.getParentSchema().getTargetNamespace().getUri();

            // Initialize new schema object for ForeignSchema and absulute path
            XSDSchema schema = null;
            String absolutePath = "";

            // Check if schema location of the current ForeignSchema is absolute if not fix absolute path
            if (!new URI(foreignSchema.getSchemaLocation()).isAbsolute()) {
                absolutePath = foreignSchema.getParentSchema().getSchemaLocation().substring(0, (foreignSchema.getParentSchema().getSchemaLocation().lastIndexOf("/") + 1)) + foreignSchema.getSchemaLocation();
                schema = xsdParser.parseForeignSchema(this.getCanonicalPath(absolutePath), newTargetNamespaceForeignSchema);
            } else {
                absolutePath = foreignSchema.getSchemaLocation();
                schema = xsdParser.parseForeignSchema(this.getCanonicalPath(absolutePath), newTargetNamespaceForeignSchema);
            }

            // Set new schema as schema for the current ForeignSchema
            foreignSchema.setSchema(schema);

            // Store namespace and location of every seen schema and update global SymbolTables
            alreadySeenSchemas.add("{" + schema.getTargetNamespace() + "}" + getCanonicalPath(schema.getSchemaLocation()));
            seenSchemas.add(schema);
//            generateSymboltables(schema);

            // If current ForeignSchema schema contains itself foreign schemata
            if (schema.getForeignSchemas() != null) {
                for (ForeignSchema currentForeignSchema: schema.getForeignSchemas()) {
                    // Calculate the valid targetNamespace for the currentForeignSchema
                    String currentTargetNamespace = "";
                    if (currentForeignSchema instanceof IncludedSchema) {
                        currentTargetNamespace = schema.getTargetNamespace().getUri();
                    } else if (currentForeignSchema instanceof ImportedSchema) {
                        currentTargetNamespace = ((ImportedSchema) currentForeignSchema).getNamespace().getUri();
                    }

                    // If a schemaLocation is given, calculate the absolute path to this schema and put the currentForeignSchema onto the stack
                    if (currentForeignSchema.getSchemaLocation() != null) {
                        URI currentForeignSchemaLocation = new URI(currentForeignSchema.getSchemaLocation());
                        String absolutePathForCurrentForeignSchema = "";

                        if (!currentForeignSchemaLocation.isAbsolute()) {
                            absolutePathForCurrentForeignSchema = currentForeignSchema.getParentSchema().getSchemaLocation().substring(0, (currentForeignSchema.getParentSchema().getSchemaLocation().lastIndexOf("/") + 1)) + currentForeignSchema.getSchemaLocation();
                        } else {
                            absolutePathForCurrentForeignSchema = currentForeignSchemaLocation.toString();
                        }

                        // If the absolutePathForCurrentForeignSchema was not already parsed in the case of the currentTargetNamespace, put the currentForeignSchema onto the stack
                        if (!this.alreadySeenSchemas.contains("{" + currentTargetNamespace + "}" + getCanonicalPath(absolutePathForCurrentForeignSchema))) {
                            stack.add(currentForeignSchema);
                        } else {

                            // The absolutePathForCurrentForeignSchema was already handled with the currentTargetNamespace.
                            // Walk over all already handled schemas and find the correct one that matches the path and the namespace
                            for (XSDSchema seenSchema: seenSchemas) {
                                if (seenSchema.getTargetNamespace().equals(currentTargetNamespace) && seenSchema.getSchemaLocation().equals(getCanonicalPath(absolutePathForCurrentForeignSchema))) {
                                    currentForeignSchema.setSchema(seenSchema);
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // Import may not contain a schemaLocation. If this is the case ...
            // ...nothing has to be done here.
        }
    }



    /**
     * Remove all occurrences of "../" by removing the last folder, which is a
     * prefix of string-occurrence, and the occurrence itself, from the given path.
     * The result should be a canonic uri expression.
     * Occurrences of the string "./" are replaced, too.
     *
     * @param path      path, that will be cleaned
     * @return String   resulting string after cleaning of "../" and "./" appearances
     */
    private String getCanonicalPath(String path) {

        while (path.contains("/./")) {
            path = path.replaceFirst("[/][.][/]", "/");
        }
        
        while (path.contains("../")) {
            int pos = path.indexOf("../");
            String pathBefore = path.substring(0, pos - 1);
            String pathAfter = path.substring(pos + 2, path.length());
            int posLastSlash = pathBefore.lastIndexOf("/");
            pathBefore = pathBefore.substring(0, posLastSlash);
            path = pathBefore + pathAfter;
        }
        
        return path;
    }
}
