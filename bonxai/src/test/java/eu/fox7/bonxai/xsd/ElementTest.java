package eu.fox7.bonxai.xsd;

import java.util.LinkedList;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import eu.fox7.bonxai.common.SymbolTable;
import eu.fox7.bonxai.common.SymbolTableRef;
import eu.fox7.bonxai.xsd.Constraint;
import eu.fox7.bonxai.xsd.Element;
import eu.fox7.bonxai.xsd.SimpleContentList;
import eu.fox7.bonxai.xsd.SimpleType;
import eu.fox7.bonxai.xsd.Type;

/**
 *
 */
public class ElementTest extends junit.framework.TestCase {

    public ElementTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testInvalidName() {
        try {
            Element element = new Element("fooElement");
            fail("Expected RuntimeException.");
        } catch (RuntimeException e)
        { /* Expected */ }
    }

    @Test
    public void testCtorFullyQualifiedName() {
        Element element = new Element("{http://example.com/fooNamespace}fooElement");

        assertEquals("fooElement", element.getLocalName());
        assertEquals("http://example.com/fooNamespace", element.getNamespace());
        assertEquals("{http://example.com/fooNamespace}fooElement", element.getName());
        assertNull(element.getType());
        assertNull(element.getDefault());
        assertNull(element.getFixed());
        assertFalse(element.nillable);
    }

    @Test
    public void testCtorNamespaceAndName() {
        Element element = new Element("http://example.com/fooNamespace", "fooElement");

        assertEquals("fooElement", element.getLocalName());
        assertEquals("http://example.com/fooNamespace", element.getNamespace());
        assertEquals("{http://example.com/fooNamespace}fooElement", element.getName());
        assertNull(element.getType());
        assertNull(element.getDefault());
        assertNull(element.getFixed());
        assertFalse(element.nillable);
    }

    @Test
    public void testCtorNameAndType() {
        SymbolTableRef<Type> typeRef = new SymbolTableRef<Type>("fooType");
        Element element = new Element("{http://example.com/fooNamespace}fooElement", typeRef);

        assertEquals("fooElement", element.getLocalName());
        assertEquals("http://example.com/fooNamespace", element.getNamespace());
        assertEquals("{http://example.com/fooNamespace}fooElement", element.getName());
        assertSame(typeRef.getReference(), element.getType());
        assertNull(element.getDefault());
        assertNull(element.getFixed());
        assertFalse(element.nillable);
    }

    @Test
    public void testCtorNamespaceNameAndType() {
        SymbolTableRef<Type> typeRef = new SymbolTableRef<Type>("fooType");
        Element element = new Element("http://example.com/fooNamespace", "fooElement", typeRef);

        assertEquals("fooElement", element.getLocalName());
        assertEquals("http://example.com/fooNamespace", element.getNamespace());
        assertEquals("{http://example.com/fooNamespace}fooElement", element.getName());
        assertSame(typeRef.getReference(), element.getType());
        assertNull(element.getDefault());
        assertNull(element.getFixed());
        assertFalse(element.nillable);
    }

    @Test
    public void testSetGetType() {
        Element element = new Element("http://example.com/anotherNamespace", "anotherElement");
        String typeName = "{}typeName";
        SymbolTableRef<Type> test = new SymbolTableRef<Type>("NameRef");
        SimpleContentList typeInheritance = new SimpleContentList(test);
        SimpleType type = new SimpleType(typeName, typeInheritance);
        SymbolTableRef<Type> reftype = new SymbolTableRef<Type>("SomeTypeRef",
                type);
        element.setType(reftype);
        assertEquals(type, element.getType());
    }

    @Test
    public void testSetGetDefault() {
        Element element = new Element("http://example.com/someNamespace", "someElement");
        assertNull(element.getDefault());
        element.setDefault("default");
        assertEquals("default", element.getDefault());
        element.setDefault(null);
        assertNull(element.getDefault());
    }

    @Test
    public void testSetGetFixed() {
        Element element = new Element("http://example.com/someNamespace", "someElement");
        assertNull(element.getFixed());
        element.setFixed("fixed");
        assertEquals("fixed", element.getFixed());
        element.setFixed(null);
        assertNull(element.getFixed());
    }

    @Test
    public void testNillableFlag()
    {
        Element element = new Element("http://example.com/someNamespace", "someElement");
        assertFalse(element.isNillable());
        element.setNillable();
        assertTrue(element.isNillable());
        element.setNotNillable();
        assertFalse(element.isNillable());
    }

    @Test
    public void testAddConstraint() {
        Element element = new Element("http://example.com/someNamespace", "someElement");
        Constraint c1, c2;

        LinkedList<Constraint> list;
        int tmpSize;

        c1 = new Constraint();
        element.addConstraint(c1);

        c2 = new Constraint();
        element.addConstraint(c2);

        list = element.getConstraints();
        tmpSize = list.size();

        if(tmpSize > 0)
        {
            list.remove();
            assertTrue("AttributeGroup-List can be manipulated from outside the element",
                    element.getConstraints().size() == tmpSize);
        }

        assertTrue("An expected attributeGroup was not foung in the Set.",
                element.getConstraints().contains(c1));
        assertTrue("An expected attributeGroup was not foung in the Set.",
                element.getConstraints().contains(c2));
    }
}
