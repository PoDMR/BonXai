package eu.fox7.bonxai.xsd;

import org.junit.*;

import eu.fox7.bonxai.common.ChoicePattern;
import eu.fox7.bonxai.common.ParticleContainer;
import eu.fox7.bonxai.common.SymbolTableRef;
import eu.fox7.bonxai.xsd.AttributeGroup;
import eu.fox7.bonxai.xsd.ComplexType;
import eu.fox7.bonxai.xsd.Content;
import eu.fox7.bonxai.xsd.Group;
import eu.fox7.bonxai.xsd.RedefinedSchema;
import eu.fox7.bonxai.xsd.SimpleContentType;
import eu.fox7.bonxai.xsd.Type;


public class RedefinedSchemaTest extends junit.framework.TestCase {

    static RedefinedSchema schema;
    static String location;

    @Before
    public void setUp()
    {
        location = "http://www.example.com/NS";
        schema = new RedefinedSchema(location);
    }

    public void testRedefinedSchema() {
        assertEquals("location is different", location, schema.getSchemaLocation());
    }

    public void testAddAttributeGroup() {
        SymbolTableRef<AttributeGroup> symref;
        String key1, key2, gr1, gr2;
        AttributeGroup ag1, ag2;

        key1 = "key1";
        key2 = "key2";
        gr1 = "{}group1";
        gr2 = "{}group2";

        ag1 = new AttributeGroup(gr1);
        symref = new SymbolTableRef<AttributeGroup>(key1,ag1);
        schema.addAttributeGroup(symref);

        ag2 = new AttributeGroup(gr2);
        symref = new SymbolTableRef<AttributeGroup>(key2, ag2);
        schema.addAttributeGroup(symref);

        assertTrue("An expected attributeGroup was not foung in the Set.", schema.getAttributeGroups().contains(ag1));
        assertTrue("An expected attributeGroup was not foung in the Set.", schema.getAttributeGroups().contains(ag2));
    }

    public void testAddGroup() {
        SymbolTableRef<eu.fox7.bonxai.xsd.Group> symref;
        String key1, key2;
        Group g1, g2;
        String name1, name2;
        ParticleContainer pc1, pc2;

        key1 = "key1";
        key2 = "key2";
        name1 = "{}g1n1";
        name2 = "{}g2n2";

        pc1 = new ChoicePattern();

        g1 = new Group(name1, pc1);
        symref = new SymbolTableRef<eu.fox7.bonxai.xsd.Group>(key1,g1);
        schema.addGroup(symref);

        pc2 = new ChoicePattern();
        g2 = new Group(name2, pc2);
        symref = new SymbolTableRef<eu.fox7.bonxai.xsd.Group>(key2, g2);
        schema.addGroup(symref);

        assertTrue("An expected Group was not foung in the Set.", schema.getGroups().contains(g1));
        assertTrue("An expected Group was not foung in the Set.", schema.getGroups().contains(g2));
    }

    public void testGetSchemaLocation() {
        assertEquals("Location ist different", schema.getSchemaLocation(), location);
    }

    public void testAddType() {
        SymbolTableRef<Type> symref;
        String key1, key2;
        ComplexType ct1, ct2;
        String name1, name2;
        Content c1, c2;

        key1 = "key1";
        key2 = "key2";
        name1 = "{}g1n1";
        name2 = "{}g2n2";

        c1 = new SimpleContentType();
        ct1 = new ComplexType(name1, c1);
        symref = new SymbolTableRef<Type>(key1,ct1);
        schema.addType(symref);

        c2 = new SimpleContentType();
        ct2 = new ComplexType(name2, c2);
        symref = new SymbolTableRef<Type>(key2,ct2);
        schema.addType(symref);

        assertTrue("An expected Type was not foung in the Set.", schema.getTypes().contains(ct1));
        assertTrue("An expected Type was not foung in the Set.", schema.getTypes().contains(ct2));
    }

}
