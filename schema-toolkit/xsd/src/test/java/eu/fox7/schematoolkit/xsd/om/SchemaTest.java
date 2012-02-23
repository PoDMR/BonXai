package eu.fox7.schematoolkit.xsd.om;

import java.util.Collection;
import java.util.LinkedList;

import org.junit.Test;

import eu.fox7.schematoolkit.common.*;
import eu.fox7.schematoolkit.xsd.om.Attribute;
import eu.fox7.schematoolkit.xsd.om.AttributeGroup;
import eu.fox7.schematoolkit.xsd.om.ComplexType;
import eu.fox7.schematoolkit.xsd.om.Content;
import eu.fox7.schematoolkit.xsd.om.Element;
import eu.fox7.schematoolkit.xsd.om.ForeignSchema;
import eu.fox7.schematoolkit.xsd.om.Group;
import eu.fox7.schematoolkit.xsd.om.ImportedSchema;
import eu.fox7.schematoolkit.xsd.om.IncludedSchema;
import eu.fox7.schematoolkit.xsd.om.SimpleContentType;
import eu.fox7.schematoolkit.xsd.om.Type;
import eu.fox7.schematoolkit.xsd.om.XSDSchema;

public class SchemaTest extends junit.framework.TestCase {

    private XSDSchema schema;
    private DefaultNamespace defaultNamespace;

    public void setUp() {
        this.schema = new XSDSchema();
    }


    public void testClearForeignSchemaList() {
        IncludedSchema included = new IncludedSchema("included");
        defaultNamespace = new DefaultNamespace("http://test.com/xyz");
        this.schema.setDefaultNamespace(defaultNamespace);
        this.schema.setTargetNamespace(defaultNamespace);
        this.schema.addForeignSchema(included);
        this.schema.clearForeignSchemas();
        assertEquals(0, this.schema.getForeignSchemas().size());
    }

    public void testAddAttributeGroup() {
        QualifiedName gr1, gr2;
        AttributeGroup ag1, ag2;

        Collection<AttributeGroup> list;
        int tmpSize;

        gr1 = new QualifiedName(defaultNamespace,"group1");
        gr2 = new QualifiedName(defaultNamespace,"group2");

        ag1 = new AttributeGroup(gr1);
        schema.addAttributeGroup(ag1);

        ag2 = new AttributeGroup(gr2);
        schema.addAttributeGroup(ag2);

        list = schema.getAttributeGroups();
        tmpSize = list.size();

        if (tmpSize > 0) {
            list.clear();
            assertTrue("AttributeGroup-List can be manipulated from outside the schema",
                    schema.getAttributeGroups().size() == tmpSize);
        }

        assertTrue("An expected attributeGroup was not foung in the Set.",
                schema.getAttributeGroups().contains(ag1));
        assertTrue("An expected attributeGroup was not foung in the Set.",
                schema.getAttributeGroups().contains(ag2));
    }

    public void testAddAttribute() {
        QualifiedName key1, key2;
        Attribute a1, a2;

        Collection<Attribute> list;
        int tmpSize;

        key1 = new QualifiedName(defaultNamespace,"key1");
        key2 = new QualifiedName(defaultNamespace,"key2");

        a1 = new Attribute("{}name1");
        schema.addAttribute(symref);

        a2 = new Attribute("{}name2");
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
        Element e1, e2;
        QualifiedName key1, key2;

        Collection<Element> list;
        int tmpSize;

        key1 = new QualifiedName(defaultNamespace,"key1");
        key2 = new QualifiedName(defaultNamespace,"key2");

        e1 = new Element(new QualifiedName(defaultNamespace,"element1"));
        schema.addElement(e1);

        e2 = new Element(new QualifiedName(defaultNamespace,"element2"));
        schema.addElement(e2);

        list = schema.getElements();
        tmpSize = list.size();

        if (tmpSize > 0) {
            list.clear();
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
        Group g1, g2;
        QualifiedName name1, name2;
        Particle pc1, pc2;

        Collection<eu.fox7.schematoolkit.xsd.om.Group> list;
        int tmpSize;

        name1 = new QualifiedName(defaultNamespace,"g1n1");
        name2 = new QualifiedName(defaultNamespace,"g2n2");

        pc1 = new ChoicePattern();

        g1 = new Group(name1, pc1);
        schema.addGroup(g1);

        pc2 = new ChoicePattern();
        g2 = new Group(name2, pc2);
        schema.addGroup(g2);

        list = schema.getGroups();
        tmpSize = list.size();

        if (tmpSize > 0) {
            list.clear();
            assertTrue("AttributeGroup-List can be manipulated from outside the schema",
                    schema.getGroups().size() == tmpSize);
        }

        assertTrue("An expected Group was not foung in the Set.", schema.getGroups().contains(g1));
        assertTrue("An expected Group was not foung in the Set.", schema.getGroups().contains(g2));

    }

    public void testAddType() {
        String key1, key2;
        ComplexType ct1, ct2;
        String name1, name2;
        Content c1, c2;

        Collection<Type> list;
        int tmpSize;

        key1 = "key1";
        key2 = "key2";
        name1 = "{}g1n1";
        name2 = "{}g2n2";

        c1 = new SimpleContentType();
        ct1 = new ComplexType(name1, c1);
        schema.addType(symref);

        c2 = new SimpleContentType();
        ct2 = new ComplexType(name2, c2);
        schema.addType(symref);

        list = schema.getTypes();
        tmpSize = list.size();

        if (tmpSize > 0) {
            list.clear();
            assertTrue("Type-List can be manipulated from outside the schema",
                    schema.getTypes().size() == tmpSize);
        }

        assertTrue("An expected Type was not foung in the schema.",
                schema.getTypes().contains(ct1));
        assertTrue("An expected Type was not foung in the schema.",
                schema.getTypes().contains(ct2));

    }

    public final void testTargetNamespaceEmptyOnConstruction() {
        assertEquals("", this.schema.getDefaultNamespace());
    }

    public final void testSetTargetNamespace() {
        this.schema.setTargetNamespace(new DefaultNamespace("foobar"));
        assertEquals(new DefaultNamespace("foobar"), this.schema.getDefaultNamespace());
    }
}
