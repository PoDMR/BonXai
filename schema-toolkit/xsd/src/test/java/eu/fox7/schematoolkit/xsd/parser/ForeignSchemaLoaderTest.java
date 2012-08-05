package eu.fox7.schematoolkit.xsd.parser;

import eu.fox7.schematoolkit.common.SymbolTable;
import eu.fox7.schematoolkit.common.SymbolTableFoundation;
import eu.fox7.schematoolkit.common.SymbolTableRef;
import eu.fox7.schematoolkit.xsd.om.Attribute;
import eu.fox7.schematoolkit.xsd.om.AttributeGroup;
import eu.fox7.schematoolkit.xsd.om.ComplexContentExtension;
import eu.fox7.schematoolkit.xsd.om.ComplexContentType;
import eu.fox7.schematoolkit.xsd.om.ComplexType;
import eu.fox7.schematoolkit.xsd.om.Element;
import eu.fox7.schematoolkit.xsd.om.ForeignSchema;
import eu.fox7.schematoolkit.xsd.om.Group;
import eu.fox7.schematoolkit.xsd.om.ImportedSchema;
import eu.fox7.schematoolkit.xsd.om.IncludedSchema;
import eu.fox7.schematoolkit.xsd.om.RedefinedSchema;
import eu.fox7.schematoolkit.xsd.om.SimpleConstraint;
import eu.fox7.schematoolkit.xsd.om.SimpleContentRestriction;
import eu.fox7.schematoolkit.xsd.om.SimpleContentType;
import eu.fox7.schematoolkit.xsd.om.SimpleType;
import eu.fox7.schematoolkit.xsd.om.Type;
import eu.fox7.schematoolkit.xsd.om.XSDSchema;
import eu.fox7.schematoolkit.xsd.parser.XSDParser;

import java.util.Iterator;
import java.util.LinkedList;
import org.junit.Test;

/**
 * Test of class ForeignSchemaLoader
 * @author Lars Schmidt, Dominik Wolff
 */
public class ForeignSchemaLoaderTest extends junit.framework.TestCase {

    /**
     * Test of findForeignSchema method, of class ForeignSchemaLoader.
     * @throws Exception 
     */
//    @Test
//    public void testFindForeignSchema_presentation() throws Exception {
////        String uri = new String("http://www.w3.org/2001/XMLSchema.xsd");
////        String uri = new String("tests/eu/fox7/bonxai/xsd/parser/xsds/foreignSchemaLoaderTests/base.xsd");
////        String uri = new String("tests/eu/fox7/bonxai/xsd/parser/xsds/foreignSchemaLoaderTests/redefineBase.xsd");
////        String uri = new String("tests/eu/fox7/bonxai/xsd/union/xsds/redefinedSchemaResolverTest/removeRedefinedSchema_before.xsd");
//
////        String uri = "tests/eu/fox7/bonxai/xsd/parser/xsds/foreignSchemaLoaderTests/importBase.xsd";
//        String uri = "tests/eu/fox7/bonxai/converter/xsd2relaxng/preparationTests/xhtml/redefineBase.xsd";
//        XSDParserHandler instance = new XSDParserHandler(true, false);
//        Schema xmlSchema = instance.parse(uri);
//
//        ForeignSchemaLoader foreignSchemaLoader = new ForeignSchemaLoader(xmlSchema, false);
//
//        foreignSchemaLoader.findForeignSchemas();
//        XSD_Writer xsd_Writer = new XSD_Writer(xmlSchema);
//
//        System.out.println(xsd_Writer.getXSDString());
//
//        if (!xmlSchema.getForeignSchemas().isEmpty()) {
//            for (Iterator<ForeignSchema> it = xmlSchema.getForeignSchemas().iterator(); it.hasNext();) {
//                ForeignSchema foreignSchema = it.next();
//                if (foreignSchema.getSchema() != null) {
//                    System.out.println("\n--------------------------------------------------------------------------\n" + foreignSchema.getClass().getName() + " - " + foreignSchema.getSchemaLocation() + "\n--------------------------------------------------------------------------\n");
//                    xsd_Writer = new XSD_Writer(foreignSchema.getSchema());
//                    System.out.println(xsd_Writer.getXSDString());
//
//                    if (!foreignSchema.getSchema().getForeignSchemas().isEmpty()) {
//                        for (Iterator<ForeignSchema> it2 = foreignSchema.getSchema().getForeignSchemas().iterator(); it2.hasNext();) {
//                            ForeignSchema foreignSchema2 = it2.next();
//                            if (foreignSchema2.getSchema() != null) {
//                                System.out.println("\n--------------------------------------------------------------------------\n" + foreignSchema2.getClass().getName() + " - " + foreignSchema2.getSchemaLocation() + "\n--------------------------------------------------------------------------\n");
//                                xsd_Writer = new XSD_Writer(foreignSchema2.getSchema());
//                                System.out.println(xsd_Writer.getXSDString());
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
    
    /**
     * Test of findForeignSchema method, of class ForeignSchemaLoader.
     * Checks redefine hierarchy and type replacement for a simpleContent/complexContent ComplexType
     * @throws Exception
     */
    @Test
    public void testFindForeignSchema_findBaseType() throws Exception {
        String uri = "tests/eu/fox7/bonxai/xsd/parser/xsds/foreignSchemaLoaderTests/findBaseType_root.xsd";
        uri = this.getClass().getResource("/"+uri).getFile();
        XSDParser instance = new XSDParser(false, false);
        XSDSchema xmlSchema = instance.parse(uri);

        assertTrue(xmlSchema.getTypeSymbolTable().hasReference("{}type"));

        SymbolTableRef<Type> strComplexType = xmlSchema.getTypeSymbolTable().getReference("{}type");

        assertTrue(strComplexType.getReference() instanceof ComplexType);
        assertFalse(strComplexType.getReference().isDummy());
        assertFalse(strComplexType.getReference().isAnonymous());

        ComplexType complexType = (ComplexType) strComplexType.getReference();

        assertTrue(complexType.getContent() instanceof SimpleContentType);
        SimpleContentType simpleContentType = (SimpleContentType) complexType.getContent();
        assertTrue(simpleContentType.getInheritance() instanceof SimpleContentRestriction);
        SimpleContentRestriction simpleContentRestriction = (SimpleContentRestriction) simpleContentType.getInheritance();

        // Anonymous SimpleType from redefined ComplexType "type"
        assertTrue(simpleContentRestriction.getAnonymousSimpleType() instanceof SimpleType);
        SimpleType simpleTypeAnonymous = (SimpleType) simpleContentRestriction.getAnonymousSimpleType();
        assertTrue(simpleTypeAnonymous.getInheritance() instanceof SimpleContentRestriction);
        SimpleContentRestriction simpleContentRestriction2 = (SimpleContentRestriction) simpleTypeAnonymous.getInheritance();
        assertTrue(simpleContentRestriction2.getAnonymousSimpleType() == null);
        assertTrue(simpleContentRestriction2.getBase().getName().equals("{http://www.w3.org/2001/XMLSchema}string"));

        // Get base type from redefined ComplexType "type"
        assertTrue(simpleContentRestriction.getBase() instanceof ComplexType);
        ComplexType complexTypeBase = (ComplexType) simpleContentRestriction.getBase();

        assertFalse(complexTypeBase == complexType);

        assertTrue(xmlSchema.getForeignSchemas().getFirst() instanceof RedefinedSchema);
        RedefinedSchema redefinedSchema = (RedefinedSchema) xmlSchema.getForeignSchemas().getFirst();

        XSDSchema schemaBranchOne = redefinedSchema.getSchema();

        SymbolTableRef<Type> strComplexTypeBranchOne = schemaBranchOne.getTypeSymbolTable().getReference("{}type");

        assertTrue(strComplexTypeBranchOne.getReference() instanceof ComplexType);
        assertFalse(strComplexTypeBranchOne.getReference().isDummy());
        assertFalse(strComplexTypeBranchOne.getReference().isAnonymous());

        ComplexType complexTypeBranchOne = (ComplexType) strComplexTypeBranchOne.getReference();

        assertTrue(complexType == complexTypeBranchOne);

        assertTrue(schemaBranchOne.getForeignSchemas().getFirst() instanceof RedefinedSchema);
        RedefinedSchema redefinedSchemaTwo = (RedefinedSchema) schemaBranchOne.getForeignSchemas().getFirst();

        XSDSchema schemaBranchTwo = redefinedSchemaTwo.getSchema();

        SymbolTableRef<Type> strComplexTypeBranchTwo = schemaBranchTwo.getTypeSymbolTable().getReference("{}type");

        assertTrue(strComplexTypeBranchTwo.getReference() instanceof ComplexType);
        assertFalse(strComplexTypeBranchTwo.getReference().isDummy());
        assertFalse(strComplexTypeBranchTwo.getReference().isAnonymous());

        ComplexType complexTypeBranchTwo = (ComplexType) strComplexTypeBranchTwo.getReference();

        assertTrue(complexType == complexTypeBranchTwo);

        // Check hierarchy of complexTypeBase
        assertTrue(complexTypeBase.getContent() instanceof ComplexContentType);
        ComplexContentType complexContentTypeBase = (ComplexContentType) complexTypeBase.getContent();
        assertTrue(complexContentTypeBase.getInheritance() instanceof ComplexContentExtension);
        ComplexContentExtension complexContentExtension = (ComplexContentExtension) complexContentTypeBase.getInheritance();

        assertTrue(complexContentExtension.getBase() instanceof ComplexType);
        ComplexType complexTypeBaseBase = (ComplexType) complexContentExtension.getBase();

        assertFalse(complexTypeBaseBase == complexType);
        assertTrue(complexTypeBaseBase.getMixed());
        assertTrue(complexTypeBaseBase.getContent() == null);
    }

    /**
     * Test of findForeignSchema method, of class ForeignSchemaLoader.
     * Checks redefine hierarchy and type replacement for a simpleContent/complexContent ComplexType
     * @throws Exception
     */
    @Test
    public void testFindForeignSchema_namespaceSharing() throws Exception {
        String uri = "tests/eu/fox7/bonxai/xsd/parser/xsds/foreignSchemaLoaderTests/namespaceSharing_root.xsd";
        uri = this.getClass().getResource("/"+uri).getFile();
        XSDParser instance = new XSDParser(false, false);
        XSDSchema xmlSchema = instance.parse(uri);

        assertEquals(1, xmlSchema.getElements().size());
        Element element = xmlSchema.getElements().getFirst();

        assertEquals("{www.A.org}elementRoot", element.getName());

        // included schema
        assertTrue(xmlSchema.getForeignSchemas().getFirst() instanceof IncludedSchema);
        IncludedSchema includedSchema = (IncludedSchema) xmlSchema.getForeignSchemas().getFirst();
        XSDSchema schemaBranchOne = includedSchema.getSchema();

        assertEquals(1, schemaBranchOne.getElements().size());
        Element elementOne = schemaBranchOne.getElements().getFirst();

        assertEquals("{www.A.org}elementOne", elementOne.getName());

        // redefined schema
        assertTrue(schemaBranchOne.getForeignSchemas().getFirst() instanceof RedefinedSchema);
        RedefinedSchema redefinedSchema = (RedefinedSchema) schemaBranchOne.getForeignSchemas().getFirst();
        XSDSchema schemaBranchTwo = redefinedSchema.getSchema();

        assertEquals(1, schemaBranchTwo.getElements().size());
        Element elementTwo = schemaBranchTwo.getElements().getFirst();

        assertEquals("{www.A.org}elementTwo", elementTwo.getName());

        // imported schema
        assertTrue(xmlSchema.getForeignSchemas().getLast() instanceof ImportedSchema);
        ImportedSchema importedSchema = (ImportedSchema) xmlSchema.getForeignSchemas().getLast();
        XSDSchema schemaBranchOneTwo = importedSchema.getSchema();

        assertEquals(1, schemaBranchOneTwo.getElements().size());
        Element elementThree = schemaBranchOneTwo.getElements().getFirst();

        assertEquals("{www.B.org}elementThree", elementThree.getName());
    }
    /**
     * Test of findForeignSchema method, of class ForeignSchemaLoader.
     * Checks redefine hierarchy and type replacement for a simpleContent/complexContent ComplexType
     * @throws Exception
     */
    @Test
    public void testFindForeignSchema_absolutPath() throws Exception {
        String uri = "tests/eu/fox7/bonxai/xsd/parser/xsds/foreignSchemaLoaderTests/absolutPath_root.xsd";
        uri = this.getClass().getResource("/"+uri).getFile();
        XSDParser instance = new XSDParser(false, false);
        XSDSchema xmlSchema = instance.parse(uri);

        LinkedList<ForeignSchema> foreignSchemas = xmlSchema.getForeignSchemas();
        for (Iterator<ForeignSchema> it = foreignSchemas.iterator(); it.hasNext();) {
            ForeignSchema foreignSchema = it.next();
            if (foreignSchema instanceof ImportedSchema) {
                assertTrue(foreignSchema.getSchema() == null);
            } else {
                assertFalse(foreignSchema.getSchema() == null);

                LinkedList<ForeignSchema> currentForeignSchemaforeignSchemas = foreignSchema.getSchema().getForeignSchemas();
                for (Iterator<ForeignSchema> it2 = currentForeignSchemaforeignSchemas.iterator(); it2.hasNext();) {
                    ForeignSchema currentForeignSchemaforeignSchema = it2.next();
                    if (currentForeignSchemaforeignSchema instanceof ImportedSchema) {
                        assertTrue(currentForeignSchemaforeignSchema.getSchema() == null);
                    } else {
                        assertFalse(currentForeignSchemaforeignSchema.getSchema() == null);
                    }
                }
            }
        }
    }
    /**
     * Test of findForeignSchema method, of class ForeignSchemaLoader.
     * Checks redefine hierarchy and type replacement for a simpleContent/complexContent ComplexType
     * @throws Exception
     */
    @Test
    public void testFindForeignSchema_twoSameSchema() throws Exception {
        String uri = "tests/eu/fox7/bonxai/xsd/parser/xsds/foreignSchemaLoaderTests/twoSameSchema_root.xsd";
        uri = this.getClass().getResource("/"+uri).getFile();
        XSDParser instance = new XSDParser(false, false);
        XSDSchema xmlSchema = instance.parse(uri);

        assertTrue(xmlSchema.getElementSymbolTable().hasReference("{www.A.org}element"));
        SymbolTableRef<Element> strElementA = xmlSchema.getElementSymbolTable().getReference("{www.A.org}element");

        assertTrue(xmlSchema.getElementSymbolTable().hasReference("{www.B.org}element"));
        SymbolTableRef<Element> strElementB = xmlSchema.getElementSymbolTable().getReference("{www.B.org}element");

        assertFalse(strElementA.getReference() == strElementB.getReference());
    }

    /**
     * Test of findForeignSchema method, of class ForeignSchemaLoader.
     * Checks redefine hierarchy and type replacement for a simpleContent/complexContent ComplexType
     * @throws Exception
     */
    @Test
    public void testFindForeignSchema_allComponents() throws Exception {
        String uri = "tests/eu/fox7/bonxai/xsd/parser/xsds/foreignSchemaLoaderTests/allComponents_root.xsd";
        uri = this.getClass().getResource("/"+uri).getFile();
        XSDParser instance = new XSDParser(false, false);
        XSDSchema xmlSchema = instance.parse(uri);

        SymbolTable<AttributeGroup> attributeGroupSymbolTable = (SymbolTable<AttributeGroup>) xmlSchema.getAttributeGroupSymbolTable();
        SymbolTableRef<AttributeGroup> strAttributeGroup = attributeGroupSymbolTable.getReference("{}attributeGroup");
        assertFalse(strAttributeGroup.getReference().isDummy());

        assertTrue(xmlSchema.getForeignSchemas().getFirst() instanceof IncludedSchema);
        IncludedSchema includedSchema = (IncludedSchema) xmlSchema.getForeignSchemas().getFirst();
        assertTrue(includedSchema.getSchema().getAttributeGroups().contains(strAttributeGroup.getReference()));

        SymbolTable<Attribute> attributeSymbolTable = (SymbolTable<Attribute>) xmlSchema.getAttributeSymbolTable();
        SymbolTableRef<Attribute> strAttribute = attributeSymbolTable.getReference("{}attributeOne");
        assertFalse(strAttribute.getReference().isDummy());
        assertTrue(includedSchema.getSchema().getAttributes().contains(strAttribute.getReference()));

        SymbolTable<Type> typeSymbolTable = (SymbolTable<Type>) xmlSchema.getTypeSymbolTable();
        SymbolTableRef<Type> strType = typeSymbolTable.getReference("{}complexType");
        assertFalse(strType.getReference().isDummy());
        assertTrue(includedSchema.getSchema().getTypes().contains(strType.getReference()));

        SymbolTable<Group> groupSymbolTable = (SymbolTable<Group>) xmlSchema.getGroupSymbolTable();
        SymbolTableRef<Group> strGroup = groupSymbolTable.getReference("{}group");
        assertFalse(strGroup.getReference().isDummy());
        assertTrue(includedSchema.getSchema().getGroups().contains(strGroup.getReference()));

        SymbolTable<Element> elementSymbolTable = (SymbolTable<Element>) xmlSchema.getElementSymbolTable();
        SymbolTableRef<Element> strElement = elementSymbolTable.getReference("{}elementOne");
        assertFalse(strElement.getReference().isDummy());
        assertTrue(includedSchema.getSchema().getElements().contains(strElement.getReference()));

        SymbolTable<SimpleConstraint> keyAndUniqueSymbolTable = (SymbolTable<SimpleConstraint>) xmlSchema.getKeyAndUniqueSymbolTable();
        SymbolTableRef<SimpleConstraint> strKeyAndUnique = keyAndUniqueSymbolTable.getReference("{}key");
        assertFalse(strKeyAndUnique.getReference().isDummy());
        assertTrue(xmlSchema.getConstraintNames().contains("{}keyrefTwo"));

        assertTrue(strElement.getReference().getConstraints().contains(strKeyAndUnique.getReference()));
    }
}
