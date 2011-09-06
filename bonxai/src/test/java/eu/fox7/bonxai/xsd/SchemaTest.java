package eu.fox7.bonxai.xsd;

import java.util.LinkedList;

import org.junit.Test;

import eu.fox7.bonxai.common.*;
import eu.fox7.bonxai.xsd.Attribute;
import eu.fox7.bonxai.xsd.AttributeGroup;
import eu.fox7.bonxai.xsd.ComplexType;
import eu.fox7.bonxai.xsd.Content;
import eu.fox7.bonxai.xsd.Element;
import eu.fox7.bonxai.xsd.ForeignSchema;
import eu.fox7.bonxai.xsd.Group;
import eu.fox7.bonxai.xsd.ImportedSchema;
import eu.fox7.bonxai.xsd.IncludedSchema;
import eu.fox7.bonxai.xsd.SimpleContentType;
import eu.fox7.bonxai.xsd.Type;
import eu.fox7.bonxai.xsd.XSDSchema;

public class SchemaTest extends junit.framework.TestCase {

    private XSDSchema schema;

    public void setUp() {
        this.schema = new XSDSchema();
    }

    public void testTypeSymbolTableConstruction() {
        //@TODO: Does anyone know how to check for generic type?
        assertTrue(this.schema.getTypeSymbolTable() instanceof SymbolTable<?>);
    }

    public void testAttributeGroupSymbolTableConstruction() {
        //@TODO: Does anyone know how to check for generic type?
        assertTrue(this.schema.getAttributeGroupSymbolTable() instanceof SymbolTable<?>);
    }

    public void testGroupSymbolTableConstruction() {
        //@TODO: Does anyone know how to check for generic type?
        assertTrue(this.schema.getGroupSymbolTable() instanceof SymbolTable<?>);
    }

    public void testGlobalAttributeSymbolTableConstruction() {
        //@TODO: Does anyone know how to check for generic type?
        assertTrue(this.schema.getAttributeSymbolTable() instanceof SymbolTable<?>);
    }

    public void testGlobalElementSymbolTableConstruction() {
        //@TODO: Does anyone know how to check for generic type?
        assertTrue(this.schema.getElementSymbolTable() instanceof SymbolTable<?>);
    }

    public void testKeySymbolTableConstruction() {
        //@TODO: Does anyone know how to check for generic type?
        assertTrue(this.schema.getKeyAndUniqueSymbolTable() instanceof SymbolTable<?>);
    }

    public void testClearForeignSchemaList() {
        IncludedSchema included = new IncludedSchema("included");
        this.schema.addForeignSchema(included);
        this.schema.clearForeignSchemas();
        assertEquals(0, this.schema.getForeignSchemas().size());
    }

    public void testAddAttributeGroup() {
        SymbolTableRef<AttributeGroup> symref;
        String key1, key2, gr1, gr2;
        AttributeGroup ag1, ag2;

        LinkedList<AttributeGroup> list;
        int tmpSize;

        key1 = "key1";
        key2 = "key2";
        gr1 = "{}group1";
        gr2 = "{}group2";

        ag1 = new AttributeGroup(gr1);
        symref = new SymbolTableRef<AttributeGroup>(key1, ag1);
        schema.addAttributeGroup(symref);

        ag2 = new AttributeGroup(gr2);
        symref = new SymbolTableRef<AttributeGroup>(key2, ag2);
        schema.addAttributeGroup(symref);

        list = schema.getAttributeGroups();
        tmpSize = list.size();

        if (tmpSize > 0) {
            list.remove();
            assertTrue("AttributeGroup-List can be manipulated from outside the schema",
                    schema.getAttributeGroups().size() == tmpSize);
        }

        assertTrue("An expected attributeGroup was not foung in the Set.",
                schema.getAttributeGroups().contains(ag1));
        assertTrue("An expected attributeGroup was not foung in the Set.",
                schema.getAttributeGroups().contains(ag2));
    }

    public void testAddAttribute() {
        SymbolTableRef<Attribute> symref;
        String key1, key2;
        Attribute a1, a2;

        LinkedList<Attribute> list;
        int tmpSize;

        key1 = "key1";
        key2 = "key2";

        a1 = new Attribute("{}name1");
        symref = new SymbolTableRef<Attribute>(key1, a1);
        schema.addAttribute(symref);

        a2 = new Attribute("{}name2");
        symref = new SymbolTableRef<Attribute>(key2, a2);
        schema.addAttribute(symref);

        list = schema.getAttributes();
        tmpSize = list.size();

        if (tmpSize > 0) {
            list.remove();
            assertTrue("AttributeGroup-List can be manipulated from outside the schema",
                    schema.getAttributes().size() == tmpSize);
        }

        assertTrue("An expected attributeGroup was not foung in the Set.",
                schema.getAttributes().contains(a1));
        assertTrue("An expected attributeGroup was not foung in the Set.",
                schema.getAttributes().contains(a2));
    }

    public void testAddElement() {
        SymbolTableRef<Element> symref;
        Element e1, e2;
        String key1, key2;

        LinkedList<Element> list;
        int tmpSize;

        key1 = "key1";
        e1 = new Element("{}element1");
        symref = new SymbolTableRef<Element>(key1, e1);
        schema.addElement(symref);

        key2 = "key2";
        e2 = new Element("{}element2");
        symref = new SymbolTableRef<Element>(key2, e2);
        schema.addElement(symref);

        list = schema.getElements();
        tmpSize = list.size();

        if (tmpSize > 0) {
            list.remove();
            assertTrue("AttributeGroup-List can be manipulated from outside the schema",
                    schema.getElements().size() == tmpSize);
        }


        assertTrue("An expected attributeGroup was not foung in the Set.",
                schema.getElements().contains(e1));
        assertTrue("An expected attributeGroup was not foung in the Set.",
                schema.getElements().contains(e2));
    }

    public void testAddForeignSchema() {
        ForeignSchema f1, f2;
        String ns1, ns2, loc1, loc2;

        LinkedList<ForeignSchema> list;
        int tmpSize;

        ns1 = "http://www.example.com/NS1";
        loc1 = "example1.xsd";

        ns2 = "http://www.example.com/NS2";
        loc2 = "example2.xsd";

        f1 = new ImportedSchema(ns1, loc1);
        f2 = new ImportedSchema(ns2, loc2);

        schema.addForeignSchema(f1);

        schema.addForeignSchema(f2);

        list = schema.getForeignSchemas();
        tmpSize = list.size();

        if (tmpSize > 0) {
            list.remove();
            assertTrue("AttributeGroup-List can be manipulated from outside the schema",
                    schema.getForeignSchemas().size() == tmpSize);
        }

        assertTrue("An expected ForreignSchema was not found in the Set.",
                schema.getForeignSchemas().contains(f1));
        assertTrue("An expected attributeGroup was not found in the Set.",
                schema.getForeignSchemas().contains(f2));
    }

    public void testAddGroup() {
        SymbolTableRef<eu.fox7.bonxai.xsd.Group> symref;
        String key1, key2;
        Group g1, g2;
        String name1, name2;
        ParticleContainer pc1, pc2;

        LinkedList<eu.fox7.bonxai.xsd.Group> list;
        int tmpSize;

        key1 = "key1";
        key2 = "key2";
        name1 = "{}g1n1";
        name2 = "{}g2n2";

        pc1 = new ChoicePattern();

        g1 = new Group(name1, pc1);
        symref = new SymbolTableRef<eu.fox7.bonxai.xsd.Group>(key1, g1);
        schema.addGroup(symref);

        pc2 = new ChoicePattern();
        g2 = new Group(name2, pc2);
        symref = new SymbolTableRef<eu.fox7.bonxai.xsd.Group>(key2, g2);
        schema.addGroup(symref);

        list = schema.getGroups();
        tmpSize = list.size();

        if (tmpSize > 0) {
            list.remove();
            assertTrue("AttributeGroup-List can be manipulated from outside the schema",
                    schema.getGroups().size() == tmpSize);
        }

        assertTrue("An expected Group was not foung in the Set.", schema.getGroups().contains(g1));
        assertTrue("An expected Group was not foung in the Set.", schema.getGroups().contains(g2));

    }

    public void testAddType() {
        SymbolTableRef<Type> symref;
        String key1, key2;
        ComplexType ct1, ct2;
        String name1, name2;
        Content c1, c2;

        LinkedList<Type> list;
        int tmpSize;

        key1 = "key1";
        key2 = "key2";
        name1 = "{}g1n1";
        name2 = "{}g2n2";

        c1 = new SimpleContentType();
        ct1 = new ComplexType(name1, c1);
        symref = new SymbolTableRef<Type>(key1, ct1);
        schema.addType(symref);

        c2 = new SimpleContentType();
        ct2 = new ComplexType(name2, c2);
        symref = new SymbolTableRef<Type>(key2, ct2);
        schema.addType(symref);

        list = schema.getTypes();
        tmpSize = list.size();

        if (tmpSize > 0) {
            list.remove();
            assertTrue("AttributeGroup-List can be manipulated from outside the schema",
                    schema.getTypes().size() == tmpSize);
        }

        assertTrue("An expected Type was not foung in the schema.",
                schema.getTypes().contains(ct1));
        assertTrue("An expected Type was not foung in the schema.",
                schema.getTypes().contains(ct2));

    }

    @Test
    public void testSetNamespaceList() {
        NamespaceList namespaceList = new NamespaceList(new DefaultNamespace("http://example.com/foo"));
        XSDSchema schema = new XSDSchema();
        schema.setNamespaceList(namespaceList);
        assertEquals(schema.getNamespaceList(), namespaceList);
    }

    public final void testTargetNamespaceEmptyOnConstruction() {
        assertEquals("", this.schema.getTargetNamespace());
    }

    public final void testSetTargetNamespace() {
        this.schema.setTargetNamespace("foobar");
        assertEquals("foobar", this.schema.getTargetNamespace());
    }
}
