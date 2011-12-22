package eu.fox7.bonxai.xsd.parser;

import eu.fox7.bonxai.common.AttributeParticle;
import eu.fox7.bonxai.common.GroupRef;
import eu.fox7.bonxai.common.Particle;
import eu.fox7.bonxai.common.ParticleContainer;
import eu.fox7.bonxai.common.SymbolTable;
import eu.fox7.bonxai.common.SymbolTableRef;
import eu.fox7.bonxai.xsd.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.Stack;
import org.xml.sax.SAXException;

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
    // Global symbolTable for all found types in all found schemas to replace dummy types with the found correct definition of the type
    private SymbolTable<Type> typeSymbolTable;
    // Global symbolTable for all found attribute groups in all found schemas to replace dummy attribute groups with the found correct definition of the attribute group
    private SymbolTable<AttributeGroup> attributeGroupSymbolTable;
    // Global symbolTable for all found groups in all found schemas to replace dummy groups with the found correct definition of the group
    private SymbolTable<eu.fox7.bonxai.xsd.Group> groupSymbolTable;
    // Global symbolTable for all found attributes in all found schemas to replace dummy attributes with the found correct definition of the attribute
    private SymbolTable<Attribute> attributeSymbolTable;
    // Global symbolTable for all found elements in all found schemas to replace dummy elements with the found correct definition of the element
    private SymbolTable<Element> elementSymbolTable;
    // Global symbolTable for all found constraints in all found schemas to replace dummy constraints with the found correct definition of the constraint
    private SymbolTable<SimpleConstraint> keyAndUniqueSymbolTable;

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
        this.typeSymbolTable = new SymbolTable<Type>();
        this.attributeGroupSymbolTable = new SymbolTable<AttributeGroup>();
        this.groupSymbolTable = new SymbolTable<Group>();
        this.attributeSymbolTable = new SymbolTable<Attribute>();
        this.elementSymbolTable = new SymbolTable<Element>();
        this.keyAndUniqueSymbolTable = new SymbolTable<SimpleConstraint>();

        // Fill all global symboltables
        this.generateSymboltables(baseSchema);
    }

    /**
     * Main starting method for the loading progress of the ForeignSchemaLoader
     * class
     * @throws FileNotFoundException
     * @throws SAXException
     * @throws IOException
     * @throws URISyntaxException
     */
    public void findForeignSchemas() throws FileNotFoundException, SAXException, IOException, URISyntaxException {
        stack = new Stack<ForeignSchema>();

        // Put all ForeignSchemas of the base XSDSchema on top of the stack.
        if (baseSchema.getForeignSchemas() != null) {
            for (Iterator<ForeignSchema> it = baseSchema.getForeignSchemas().iterator(); it.hasNext();) {
                ForeignSchema foreignSchema = it.next();
                stack.add(foreignSchema);
            }
        }

        // As long as the stack contains another ForeignSchema, these schemas are parsed.
        while (!stack.isEmpty()) {
            parseForeignSchema(stack.pop());
        }

        for (Iterator<XSDSchema> it = seenSchemas.iterator(); it.hasNext();) {
            XSDSchema schema = it.next();
            setNewRedefinedReferences(schema);
        }


        // Traverse all seen schemata and handle the correction and replacement
        // of dummy-objects with the valid found definitions
        for (Iterator<XSDSchema> it = seenSchemas.iterator(); it.hasNext();) {
            XSDSchema schema = it.next();

            // Case Elements:
            for (Iterator<SymbolTableRef<Element>> it2 = this.elementSymbolTable.getReferences().iterator(); it2.hasNext();) {
                SymbolTableRef<Element> symbolTableRef = it2.next();
                if (schema.getElementSymbolTable().hasReference(symbolTableRef.getKey())) {
                    schema.getElementSymbolTable().updateOrCreateReference(symbolTableRef.getKey(), symbolTableRef.getReference());
                }
            }

            // Case Attribute:
            for (Iterator<SymbolTableRef<Attribute>> it2 = this.attributeSymbolTable.getReferences().iterator(); it2.hasNext();) {
                SymbolTableRef<Attribute> symbolTableRef = it2.next();
                if (schema.getAttributeSymbolTable().hasReference(symbolTableRef.getKey())) {
                    schema.getAttributeSymbolTable().updateOrCreateReference(symbolTableRef.getKey(), symbolTableRef.getReference());
                }
            }

            // Case AttributeGroup:
            for (Iterator<SymbolTableRef<AttributeGroup>> it2 = this.attributeGroupSymbolTable.getReferences().iterator(); it2.hasNext();) {
                SymbolTableRef<AttributeGroup> symbolTableRef = it2.next();
                if (schema.getAttributeGroupSymbolTable().hasReference(symbolTableRef.getKey())) {
                    schema.getAttributeGroupSymbolTable().updateOrCreateReference(symbolTableRef.getKey(), symbolTableRef.getReference());
                }
            }

            // Case Group:
            for (Iterator<SymbolTableRef<Group>> it2 = this.groupSymbolTable.getReferences().iterator(); it2.hasNext();) {
                SymbolTableRef<Group> symbolTableRef = it2.next();
                if (schema.getGroupSymbolTable().hasReference(symbolTableRef.getKey())) {
                    schema.getGroupSymbolTable().updateOrCreateReference(symbolTableRef.getKey(), symbolTableRef.getReference());
                }
            }

            // Case SimpleConstraint:
            for (Iterator<SymbolTableRef<SimpleConstraint>> it2 = this.keyAndUniqueSymbolTable.getReferences().iterator(); it2.hasNext();) {
                SymbolTableRef<SimpleConstraint> symbolTableRef = it2.next();
                if (schema.getKeyAndUniqueSymbolTable().hasReference(symbolTableRef.getKey())) {
                    schema.getKeyAndUniqueSymbolTable().updateOrCreateReference(symbolTableRef.getKey(), symbolTableRef.getReference());
                }
            }

            // Case Type:
            for (Iterator<SymbolTableRef<Type>> it2 = this.typeSymbolTable.getReferences().iterator(); it2.hasNext();) {
                SymbolTableRef<Type> symbolTableRef = it2.next();
                if (schema.getTypeSymbolTable().hasReference(symbolTableRef.getKey())) {
                    schema.getTypeSymbolTable().updateOrCreateReference(symbolTableRef.getKey(), symbolTableRef.getReference());
                }
            }
        }

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
    private void parseForeignSchema(ForeignSchema foreignSchema) throws FileNotFoundException, SAXException, IOException, URISyntaxException {

        if (foreignSchema.getSchemaLocation() != null && !foreignSchema.getSchemaLocation().equals("")) {

            // Initializes the new xsd parser
            XSDParser xsdParser = new XSDParser(this.checkEDC, false);

            // Get target namespace of the schema containing the current ForeignSchema or null if the ForeignSchema is an ImportedSchema
            String newTargetNamespaceForeignSchema = (foreignSchema instanceof ImportedSchema) ? null : foreignSchema.getParentSchema().getTargetNamespace();

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
            generateSymboltables(schema);

            // If current ForeignSchema schema contains itself foreign schemata
            if (schema.getForeignSchemas() != null) {
                for (Iterator<ForeignSchema> it = schema.getForeignSchemas().iterator(); it.hasNext();) {
                    ForeignSchema currentForeignSchema = it.next();

                    // Calculate the valid targetNamespace for the currentForeignSchema
                    String currentTargetNamespace = "";
                    if (currentForeignSchema instanceof IncludedSchema || currentForeignSchema instanceof RedefinedSchema) {
                        currentTargetNamespace = schema.getTargetNamespace();
                    } else if (currentForeignSchema instanceof ImportedSchema) {
                        currentTargetNamespace = ((ImportedSchema) currentForeignSchema).getNamespace();
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
                            for (Iterator<XSDSchema> it2 = seenSchemas.iterator(); it2.hasNext();) {
                                XSDSchema seenSchema = it2.next();
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
     * Check all object-references from the symbolTables of a given schema,
     * if they aren't already found or replace already found dummy-objects with
     * the new found correct definitions
     * @param schema     The source for the check mentioned above.
     */
    private void generateSymboltables(XSDSchema schema) {
        // Check Element-objects
        if (!schema.getElementSymbolTable().getReferences().isEmpty()) {
            LinkedList<SymbolTableRef<Element>> temp = schema.getElementSymbolTable().getReferences();
            for (Iterator<SymbolTableRef<Element>> it = temp.iterator(); it.hasNext();) {
                SymbolTableRef<Element> symbolTableRef = it.next();
                if (!this.elementSymbolTable.hasReference(symbolTableRef.getKey())) {
                    this.elementSymbolTable.setReference(symbolTableRef.getKey(), symbolTableRef);
                } else if ((this.elementSymbolTable.hasReference(symbolTableRef.getKey()) && this.elementSymbolTable.getReference(symbolTableRef.getKey()).getReference().isDummy())) {
                    this.elementSymbolTable.updateOrCreateReference(symbolTableRef.getKey(), symbolTableRef.getReference());
                }
            }
        }

        // Check Attribute-objects
        if (!schema.getAttributeSymbolTable().getReferences().isEmpty()) {
            LinkedList<SymbolTableRef<Attribute>> temp = schema.getAttributeSymbolTable().getReferences();
            for (Iterator<SymbolTableRef<Attribute>> it = temp.iterator(); it.hasNext();) {
                SymbolTableRef<Attribute> symbolTableRef = it.next();
                if (!this.attributeSymbolTable.hasReference(symbolTableRef.getKey())) {
                    this.attributeSymbolTable.setReference(symbolTableRef.getKey(), symbolTableRef);
                } else if ((this.attributeSymbolTable.hasReference(symbolTableRef.getKey()) && this.attributeSymbolTable.getReference(symbolTableRef.getKey()).getReference().isDummy())) {
                    this.attributeSymbolTable.updateOrCreateReference(symbolTableRef.getKey(), symbolTableRef.getReference());
                }
            }
        }

        // Check Type-objects
        if (!schema.getTypeSymbolTable().getReferences().isEmpty()) {
            LinkedList<SymbolTableRef<Type>> temp = schema.getTypeSymbolTable().getReferences();
            for (Iterator<SymbolTableRef<Type>> it = temp.iterator(); it.hasNext();) {
                SymbolTableRef<Type> symbolTableRef = it.next();
                if (!this.typeSymbolTable.hasReference(symbolTableRef.getKey())) {
                    this.typeSymbolTable.setReference(symbolTableRef.getKey(), symbolTableRef);
                } else if ((this.typeSymbolTable.hasReference(symbolTableRef.getKey()) && this.typeSymbolTable.getReference(symbolTableRef.getKey()).getReference().isDummy())) {
                    this.typeSymbolTable.updateOrCreateReference(symbolTableRef.getKey(), symbolTableRef.getReference());
                }
            }
        }

        // Check AttributeGroup-objects
        if (!schema.getAttributeGroupSymbolTable().getReferences().isEmpty()) {
            LinkedList<SymbolTableRef<AttributeGroup>> temp = schema.getAttributeGroupSymbolTable().getReferences();
            for (Iterator<SymbolTableRef<AttributeGroup>> it = temp.iterator(); it.hasNext();) {
                SymbolTableRef<AttributeGroup> symbolTableRef = it.next();
                if (!this.attributeGroupSymbolTable.hasReference(symbolTableRef.getKey())) {
                    this.attributeGroupSymbolTable.setReference(symbolTableRef.getKey(), symbolTableRef);
                } else if ((this.attributeGroupSymbolTable.hasReference(symbolTableRef.getKey()) && this.attributeGroupSymbolTable.getReference(symbolTableRef.getKey()).getReference().isDummy())) {
                    this.attributeGroupSymbolTable.updateOrCreateReference(symbolTableRef.getKey(), symbolTableRef.getReference());
                }
            }
        }

        // Check Group-objects
        if (!schema.getGroupSymbolTable().getReferences().isEmpty()) {
            LinkedList<SymbolTableRef<Group>> temp = schema.getGroupSymbolTable().getReferences();
            for (Iterator<SymbolTableRef<Group>> it = temp.iterator(); it.hasNext();) {
                SymbolTableRef<Group> symbolTableRef = it.next();
                if (!this.groupSymbolTable.hasReference(symbolTableRef.getKey())) {
                    this.groupSymbolTable.setReference(symbolTableRef.getKey(), symbolTableRef);
                } else if ((this.groupSymbolTable.hasReference(symbolTableRef.getKey()) && this.groupSymbolTable.getReference(symbolTableRef.getKey()).getReference().isDummy())) {
                    this.groupSymbolTable.updateOrCreateReference(symbolTableRef.getKey(), symbolTableRef.getReference());
                }
            }
        }

        // Check SimpleConstraint-objects
        if (!schema.getKeyAndUniqueSymbolTable().getReferences().isEmpty()) {
            LinkedList<SymbolTableRef<SimpleConstraint>> temp = schema.getKeyAndUniqueSymbolTable().getReferences();
            for (Iterator<SymbolTableRef<SimpleConstraint>> it = temp.iterator(); it.hasNext();) {
                SymbolTableRef<SimpleConstraint> symbolTableRef = it.next();
                if (!this.keyAndUniqueSymbolTable.hasReference(symbolTableRef.getKey())) {
                    this.keyAndUniqueSymbolTable.setReference(symbolTableRef.getKey(), symbolTableRef);
                } else if ((this.keyAndUniqueSymbolTable.hasReference(symbolTableRef.getKey()) && this.keyAndUniqueSymbolTable.getReference(symbolTableRef.getKey()).getReference().isDummy())) {
                    this.keyAndUniqueSymbolTable.updateOrCreateReference(symbolTableRef.getKey(), symbolTableRef.getReference());
                }
            }
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

    /**
     * Replace the self-references with the correct child-reference from an
     * external schema or a schema below.
     * @param schema    XSDSchema to start the reference_replacing from
     */
    private void setNewRedefinedReferences(XSDSchema schema) {

        LinkedList<ForeignSchema> foreignSchemas = schema.getForeignSchemas();

        // Traverse over direct linked external schemas in order to find a RedefinedSchema
        for (Iterator<ForeignSchema> it = foreignSchemas.iterator(); it.hasNext();) {
            ForeignSchema foreignSchema = it.next();

            if (foreignSchema instanceof RedefinedSchema) {
                RedefinedSchema redefinedSchema = (RedefinedSchema) foreignSchema;
                Set<Type> redefinedTypes = redefinedSchema.getTypes();

                // Traverse over all redefined types in the current RedefinedSchema and start the replacement of their base types
                for (Iterator<Type> it1 = redefinedTypes.iterator(); it1.hasNext();) {
                    Type redefinedType = it1.next();

                    if (redefinedType instanceof SimpleType) {

                        // Case "SimpleType":
                        SimpleType simpleType = (SimpleType) redefinedType;
                        if (simpleType.getInheritance() != null) {
                            if (simpleType.getInheritance() instanceof SimpleContentRestriction) {

                                // Subcase "SimpleContentRestriction":
                                SimpleContentRestriction simpleContentRestriction = (SimpleContentRestriction) simpleType.getInheritance();
                                SymbolTableRef<Type> symbolTableRef = findBaseType(redefinedType.getName(), redefinedSchema.getSchema(), new HashSet<XSDSchema>());
                                simpleContentRestriction.setBase(symbolTableRef);
                            }
                        }
                    } else if (redefinedType instanceof ComplexType) {

                        // Case "ComplexType":
                        ComplexType complexType = (ComplexType) redefinedType;
                        if (complexType.getContent() != null) {
                            if (complexType.getContent() instanceof ComplexContentType) {

                                // Subcase "ComplexContentType":
                                ComplexContentType complexContentType = (ComplexContentType) complexType.getContent();
                                if (complexContentType.getInheritance() != null) {
                                    if (complexContentType.getInheritance() instanceof ComplexContentRestriction) {

                                        // Subsubcase "ComplexContentRestriction":
                                        ComplexContentRestriction complexContentRestriction = (ComplexContentRestriction) complexContentType.getInheritance();
                                        SymbolTableRef<Type> symbolTableRef = findBaseType(redefinedType.getName(), redefinedSchema.getSchema(), new HashSet<XSDSchema>());
                                        complexContentRestriction.setBase(symbolTableRef);
                                    } else if (complexContentType.getInheritance() instanceof ComplexContentExtension) {

                                        // Subsubcase "ComplexContentExtension":
                                        ComplexContentExtension complexContentExtension = (ComplexContentExtension) complexContentType.getInheritance();
                                        SymbolTableRef<Type> symbolTableRef = findBaseType(redefinedType.getName(), redefinedSchema.getSchema(), new HashSet<XSDSchema>());
                                        complexContentExtension.setBase(symbolTableRef);
                                    }
                                }
                            } else if (complexType.getContent() instanceof SimpleContentType) {

                                // Subcase "SimpleContentType":
                                SimpleContentType simpleContentType = (SimpleContentType) complexType.getContent();
                                if (simpleContentType.getInheritance() instanceof SimpleContentRestriction) {

                                    // Subsubcase "SimpleContentRestriction":
                                    SimpleContentRestriction simpleContentRestriction = (SimpleContentRestriction) simpleContentType.getInheritance();
                                    SymbolTableRef<Type> symbolTableRef = findBaseType(redefinedType.getName(), redefinedSchema.getSchema(), new HashSet<XSDSchema>());
                                    simpleContentRestriction.setBase(symbolTableRef);
                                } else if (simpleContentType.getInheritance() instanceof SimpleContentExtension) {

                                    // Subsubcase "SimpleContentExtension":
                                    SimpleContentExtension simpleContentExtension = (SimpleContentExtension) simpleContentType.getInheritance();
                                    SymbolTableRef<Type> symbolTableRef = findBaseType(redefinedType.getName(), redefinedSchema.getSchema(), new HashSet<XSDSchema>());
                                    simpleContentExtension.setBase(symbolTableRef);
                                }
                            }
                        }
                    }
                }

                // Traverse over all redefined groups in the current RedefinedSchema and start the replacement of their self-references
                Set<Group> redefinedGroups = redefinedSchema.getGroups();
                for (Iterator<Group> it1 = redefinedGroups.iterator(); it1.hasNext();) {
                    Group redefinedGroup = it1.next();

                    // Search the external schema for the first appearance of the group definition and replace the old self-reference with a reference to this group definition
                    GroupRef oldGroupRef = findOldGroupRef(redefinedGroup.getName(), redefinedGroup.getParticleContainer());
                    SymbolTableRef<Group> strGroup = findBaseGroup(redefinedGroup.getName(), redefinedSchema.getSchema(), new LinkedHashSet<XSDSchema>());

                    oldGroupRef.setGroupRef(strGroup);
                }

                // Traverse over all redefined attribute groups in the current RedefinedSchema and start the replacement of their self-references
                Set<AttributeGroup> redefinedAttributeGroups = redefinedSchema.getAttributeGroups();
                for (Iterator<AttributeGroup> it1 = redefinedAttributeGroups.iterator(); it1.hasNext();) {
                    AttributeGroup redefinedAttributeGroup = it1.next();

                    // Search the external schema for the first appearance of the attribute group definition and replace the old self-reference with a reference to this attribute group definition
                    AttributeGroupRef oldAttributeGroupRef = findOldAttributeGroupRef(redefinedAttributeGroup.getName(), redefinedAttributeGroup.getAttributeParticles());
                    SymbolTableRef<AttributeGroup> strAttributeGroup = findBaseAttributeGroup(redefinedAttributeGroup.getName(), redefinedSchema.getSchema(), new LinkedHashSet<XSDSchema>());

                    oldAttributeGroupRef.setAttributeGroupRef(strAttributeGroup);
                }
            }
        }
    }

    /**
     * Find base type with given name from a schema or its child-schemas
     * recursivly.
     * @param name                      Type name to search for
     * @param schema                    Current schema
     * @param alreadySeenSchemas        Set of all seen schemas for prevention of endless loops
     * @return SymbolTableRef<Type>     Found type reference
     */
    private SymbolTableRef<Type> findBaseType(String name, XSDSchema schema, HashSet<XSDSchema> alreadySeenSchemas) {

        SymbolTableRef<Type> resultSymbolTableRefType = null;
        alreadySeenSchemas.add(schema);

        // Check if the requested type is defined in the current schema (type may be contained in a redefine component)
        if (schema.getTypeSymbolTable().hasReference(name)) {
            SymbolTableRef<Type> symbolTableRefType = schema.getTypeSymbolTable().getReference(name);
            if (!symbolTableRefType.getReference().isDummy()) {
                SymbolTableRef<Type> symbolTableRef = new SymbolTableRef<Type>(symbolTableRefType.getKey(), symbolTableRefType.getReference());
                resultSymbolTableRefType = symbolTableRef;
            }
        }

        // If the requested type wasn't found yet, search through external schemas defined below the current schema
        if (resultSymbolTableRefType == null) {
            LinkedList<ForeignSchema> foreignSchemas = schema.getForeignSchemas();
            for (Iterator<ForeignSchema> it = foreignSchemas.iterator(); it.hasNext();) {
                ForeignSchema foreignSchema = it.next();
                if (!alreadySeenSchemas.contains(foreignSchema.getSchema())) {
                    resultSymbolTableRefType = findBaseType(name, foreignSchema.getSchema(), alreadySeenSchemas);
                    if (resultSymbolTableRefType != null) {
                        break;
                    }
                }
            }
        }
        return resultSymbolTableRefType;
    }

    /**
     * Find group with given name from a schema or its child-schemas
     * recursivly.
     * @param name                      Group name to search for
     * @param schema                    Current schema
     * @param alreadySeenSchemas        Set of all seen schemas for prevention of endless loops
     * @return SymbolTableRef<Group>    Found group reference
     */
    private SymbolTableRef<Group> findBaseGroup(String name, XSDSchema schema, LinkedHashSet<XSDSchema> alreadySeenSchemas) {
        SymbolTableRef<Group> resultSymbolTableRefGroup = null;
        alreadySeenSchemas.add(schema);

        // Check if the requested group is defined in the current schema (group may be contained in a redefine component)
        if (schema.getGroupSymbolTable().hasReference(name)) {
            SymbolTableRef<Group> symbolTableRefGroup = schema.getGroupSymbolTable().getReference(name);
            if (!symbolTableRefGroup.getReference().isDummy()) {
                SymbolTableRef<Group> symbolTableRef = new SymbolTableRef<Group>(symbolTableRefGroup.getKey(), symbolTableRefGroup.getReference());
                resultSymbolTableRefGroup = symbolTableRef;
            }
        }

        // If the requested group wasn't found yet, search through external schemas defined below the current schema
        if (resultSymbolTableRefGroup == null) {
            LinkedList<ForeignSchema> foreignSchemas = schema.getForeignSchemas();
            for (Iterator<ForeignSchema> it = foreignSchemas.iterator(); it.hasNext();) {
                ForeignSchema foreignSchema = it.next();
                if (!alreadySeenSchemas.contains(foreignSchema.getSchema())) {
                    resultSymbolTableRefGroup = findBaseGroup(name, foreignSchema.getSchema(), alreadySeenSchemas);
                    if (resultSymbolTableRefGroup != null) {
                        break;
                    }
                }
            }
        }
        return resultSymbolTableRefGroup;
    }

    /**
     * Find attribute group with given name from a schema or its child-schemas
     * recursivly.
     * @param name                               Attribute group name to search for
     * @param schema                             Current schema
     * @param alreadySeenSchemas                 Set of all seen schemas for prevention of endless loops
     * @return SymbolTableRef<AttributeGroup>    Found attribute group reference
     */
    private SymbolTableRef<AttributeGroup> findBaseAttributeGroup(String name, XSDSchema schema, LinkedHashSet<XSDSchema> alreadySeenSchemas) {
        SymbolTableRef<AttributeGroup> resultSymbolTableRefAttributeGroup = null;
        alreadySeenSchemas.add(schema);

        // Check if the requested attribute group is defined in the current schema (attribute group may be contained in a redefine component)
        if (schema.getAttributeGroupSymbolTable().hasReference(name)) {
            SymbolTableRef<AttributeGroup> symbolTableRefAttributeGroup = schema.getAttributeGroupSymbolTable().getReference(name);
            if (!symbolTableRefAttributeGroup.getReference().isDummy()) {
                SymbolTableRef<AttributeGroup> symbolTableRef = new SymbolTableRef<AttributeGroup>(symbolTableRefAttributeGroup.getKey(), symbolTableRefAttributeGroup.getReference());
                resultSymbolTableRefAttributeGroup = symbolTableRef;
            }
        }

        // If the requested attribute group wasn't found yet, search through external schemas defined below the current schema
        if (resultSymbolTableRefAttributeGroup == null) {
            LinkedList<ForeignSchema> foreignSchemas = schema.getForeignSchemas();
            for (Iterator<ForeignSchema> it = foreignSchemas.iterator(); it.hasNext();) {
                ForeignSchema foreignSchema = it.next();
                if (!alreadySeenSchemas.contains(foreignSchema.getSchema())) {
                    resultSymbolTableRefAttributeGroup = findBaseAttributeGroup(name, foreignSchema.getSchema(), alreadySeenSchemas);
                    if (resultSymbolTableRefAttributeGroup != null) {
                        break;
                    }
                }
            }
        }
        return resultSymbolTableRefAttributeGroup;
    }

    /**
     * Search ParticleContainer for self-reference of a group with the given
     * name
     * @param name                  Group name to search for
     * @param particleContainer     Source for the search request
     * @return GroupRef             Found group-reference or null
     */
    private GroupRef findOldGroupRef(String name, ParticleContainer particleContainer) {
        for (Iterator<Particle> it = particleContainer.getParticles().iterator(); it.hasNext();) {
            Particle particle = it.next();
            if (particle instanceof ParticleContainer) {

                // Case "ParticleContainer":
                ParticleContainer innerParticleContainer = (ParticleContainer) particle;
                return this.findOldGroupRef(name, innerParticleContainer);
            } else if (particle instanceof GroupRef) {

                // Case "GroupRef":
                GroupRef groupRef = (GroupRef) particle;
                if (groupRef.getGroup().getName().equals(name)) {
                    return groupRef;
                }
            }
        }
        return null;
    }

    /**
     * Search ParticleContainer for self-reference of a attribute group with the
     * given name
     * @param name                  Attribute group name to search for
     * @param particleContainer     Source for the search request
     * @return AttributeGroupRef    Found attribute group-reference or null
     */
    private AttributeGroupRef findOldAttributeGroupRef(String name, LinkedList<AttributeParticle> attributeParticles) {
        for (Iterator<AttributeParticle> it = attributeParticles.iterator(); it.hasNext();) {
            AttributeParticle attributeParticle = it.next();
            if (attributeParticle instanceof AttributeGroupRef) {

                // Case "AttributeGroupRef":
                AttributeGroupRef attributeGroupRef = (AttributeGroupRef) attributeParticle;
                if (attributeGroupRef.getAttributeGroup().getName().equals(name)) {
                    return attributeGroupRef;
                }
            }
        }
        return null;
    }
}
