package eu.fox7.bonxai.converter.xsd2dtd;

import eu.fox7.bonxai.converter.xsd2dtd.ElementWrapper;
import eu.fox7.bonxai.dtd.Attribute;
import eu.fox7.bonxai.dtd.DocumentTypeDefinition;
import eu.fox7.bonxai.dtd.Element;
import eu.fox7.bonxai.dtd.ElementRef;
import eu.fox7.schematoolkit.common.SymbolTableRef;
import eu.fox7.schematoolkit.xsd.om.Key;
import eu.fox7.schematoolkit.xsd.om.KeyRef;
import eu.fox7.schematoolkit.xsd.om.SimpleConstraint;
import eu.fox7.schematoolkit.xsd.om.XSDSchema;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test of class ElementWrapper
 * @author Lars Schmidt
 */
public class ElementWrapperTest extends junit.framework.TestCase {

    // Schema for this testcase
    XSDSchema schema;
    DocumentTypeDefinition dtd;

    @Before
    @Override
    public void setUp() throws Exception {
        schema = new XSDSchema();
        this.dtd = new DocumentTypeDefinition();
    }

    /**
     * Test of getDTDElements method, of class ElementWrapper.
     */
    @Test
    public void testGetDTDElements() {
        ElementWrapper instance = new ElementWrapper(schema, "dtdElementName");
        Element element1 = new Element("dtdElementName_1");
        Element element2 = new Element("dtdElementName_2");
        instance.addDTDElement(element1);
        instance.addDTDElement(element2);
        assertEquals(2, instance.getDTDElements().size());
        LinkedList<Element> linkedList = new LinkedList<Element>(instance.getDTDElements());
        assertEquals("dtdElementName_1", linkedList.getFirst().getName());
        assertEquals("dtdElementName_2", linkedList.getLast().getName());
    }

    /**
     * Test of addDTDElement method, of class ElementWrapper.
     */
    @Test
    public void testAddDTDElement() {
        ElementWrapper instance = new ElementWrapper(schema, "dtdElementName");
        Element element1 = new Element("dtdElementName_1");
        Element element2 = new Element("dtdElementName_2");
        instance.addDTDElement(element1);
        instance.addDTDElement(element2);
        assertEquals(2, instance.getDTDElements().size());
        LinkedList<Element> linkedList = new LinkedList<Element>(instance.getDTDElements());
        assertEquals("dtdElementName_1", linkedList.getFirst().getName());
        assertEquals("dtdElementName_2", linkedList.getLast().getName());
    }

    /**
     * Test of addXSDConstraint method, of class ElementWrapper.
     */
    @Test
    public void testAddXSDConstraint() {
        ElementWrapper instance = new ElementWrapper(schema, "dtdElementName");
        Key key = new Key("{}name", ".//");
        SymbolTableRef<SimpleConstraint> symTabRef = schema.getKeyAndUniqueSymbolTable().updateOrCreateReference("name", key);
        KeyRef keyRef = new KeyRef("{}name2", ".//", symTabRef);
        instance.addXSDConstraint(key);
        instance.addXSDConstraint(keyRef);
        assertEquals(2, instance.getXsdContraints().size());
        LinkedList<SimpleConstraint> linkedList = new LinkedList<SimpleConstraint>(instance.getXsdContraints());
        assertEquals("{}name", linkedList.getFirst().getName());
        assertEquals("{}name2", linkedList.getLast().getName());
    }

    /**
     * Test of getXsdContraints method, of class ElementWrapper.
     */
    @Test
    public void testGetXsdContraints() {
        ElementWrapper instance = new ElementWrapper(schema, "dtdElementName");
        Key key = new Key("{}name", ".//");
        SymbolTableRef<SimpleConstraint> symTabRef = schema.getKeyAndUniqueSymbolTable().updateOrCreateReference("name", key);
        KeyRef keyRef = new KeyRef("{}name2", ".//", symTabRef);
        instance.addXSDConstraint(key);
        instance.addXSDConstraint(keyRef);
        assertEquals(2, instance.getXsdContraints().size());
        LinkedList<SimpleConstraint> linkedList = new LinkedList<SimpleConstraint>(instance.getXsdContraints());
        assertEquals("{}name", linkedList.getFirst().getName());
        assertEquals("{}name2", linkedList.getLast().getName());
    }

    /**
     * Test of addDTDElementRef method, of class ElementWrapper.
     */
    @Test
    public void testAddDTDElementRef() {
        ElementWrapper instance = new ElementWrapper(schema, "dtdElementName");
        Element element1 = new Element("dtdElementName_1");
        Element element2 = new Element("dtdElementName_2");
        SymbolTableRef<Element> symTabRef1 = dtd.getElementSymbolTable().updateOrCreateReference("element1", element1);
        SymbolTableRef<Element> symTabRef2 = dtd.getElementSymbolTable().updateOrCreateReference("element2", element2);
        ElementRef elementRef1 = new ElementRef(symTabRef1);
        ElementRef elementRef2 = new ElementRef(symTabRef2);
        instance.addDTDElementRef(elementRef1);
        instance.addDTDElementRef(elementRef2);
        assertEquals(2, instance.getDtdElementRefs().size());
        LinkedList<ElementRef> linkedList = new LinkedList<ElementRef>(instance.getDtdElementRefs());
        assertEquals("dtdElementName_1", linkedList.getFirst().getElement().getName());
        assertEquals("dtdElementName_2", linkedList.getLast().getElement().getName());
    }

    /**
     * Test of getDtdElementRefs method, of class ElementWrapper.
     */
    @Test
    public void testGetDtdElementRefs() {
        ElementWrapper instance = new ElementWrapper(schema, "dtdElementName");
        Element element1 = new Element("dtdElementName_1");
        Element element2 = new Element("dtdElementName_2");
        SymbolTableRef<Element> symTabRef1 = dtd.getElementSymbolTable().updateOrCreateReference("element1", element1);
        SymbolTableRef<Element> symTabRef2 = dtd.getElementSymbolTable().updateOrCreateReference("element2", element2);
        ElementRef elementRef1 = new ElementRef(symTabRef1);
        ElementRef elementRef2 = new ElementRef(symTabRef2);
        instance.addDTDElementRef(elementRef1);
        instance.addDTDElementRef(elementRef2);
        assertEquals(2, instance.getDtdElementRefs().size());
        LinkedList<ElementRef> linkedList = new LinkedList<ElementRef>(instance.getDtdElementRefs());
        assertEquals("dtdElementName_1", linkedList.getFirst().getElement().getName());
        assertEquals("dtdElementName_2", linkedList.getLast().getElement().getName());
    }

    /**
     * Test of getDTDAttributeMap method, of class ElementWrapper.
     */
    @Test
    public void testGetDTDAttributeMap() {
        ElementWrapper instance = new ElementWrapper(schema, "dtdElementName");
        Attribute attribute1 = new Attribute("dtdAttribute1", "");
        Attribute attribute2 = new Attribute("dtdAttribute2", "");
        instance.addDTDAttribute(attribute1);
        instance.addDTDAttribute(attribute2);
        assertEquals(2, instance.getDTDAttributeMap().size());

        assertEquals(1, instance.getDTDAttributeMap().get("dtdAttribute1").size());
        assertEquals(1, instance.getDTDAttributeMap().get("dtdAttribute2").size());
        assertEquals("dtdAttribute1", instance.getDTDAttributeMap().get("dtdAttribute1").iterator().next().getName());
        assertEquals("dtdAttribute2", instance.getDTDAttributeMap().get("dtdAttribute2").iterator().next().getName());
    }

    /**
     * Test of addDTDAttribute method, of class ElementWrapper.
     */
    @Test
    public void testAddDTDAttribute() {
        ElementWrapper instance = new ElementWrapper(schema, "dtdElementName");
        Attribute attribute1 = new Attribute("dtdAttribute1", "");
        Attribute attribute2 = new Attribute("dtdAttribute2", "");
        instance.addDTDAttribute(attribute1);
        instance.addDTDAttribute(attribute2);
        assertEquals(2, instance.getDTDAttributeMap().size());

        assertEquals(1, instance.getDTDAttributeMap().get("dtdAttribute1").size());
        assertEquals(1, instance.getDTDAttributeMap().get("dtdAttribute2").size());
        assertEquals("dtdAttribute1", instance.getDTDAttributeMap().get("dtdAttribute1").iterator().next().getName());
        assertEquals("dtdAttribute2", instance.getDTDAttributeMap().get("dtdAttribute2").iterator().next().getName());
    }

    /**
     * Test of getDTDElementName method, of class ElementWrapper.
     */
    @Test
    public void testGetDTDElementName() {
        ElementWrapper instance = new ElementWrapper(schema, "dtdElementName");
        String expResult = "dtdElementName";
        String result = instance.getDTDElementName();
        assertEquals(expResult, result);
    }

    /**
     * Test of setDtdElements method, of class ElementWrapper.
     */
    @Test
    public void testSetDtdElements() {
        ElementWrapper instance = new ElementWrapper(schema, "dtdElementName");
        LinkedHashSet<Element> dtdElements = new LinkedHashSet<Element>();
        Element element1 = new Element("dtdElementName_1");
        Element element2 = new Element("dtdElementName_2");
        dtdElements.add(element1);
        dtdElements.add(element2);
        instance.setDtdElements(dtdElements);
        assertEquals(2, instance.getDTDElements().size());
        LinkedList<Element> linkedList = new LinkedList<Element>(instance.getDTDElements());
        assertEquals("dtdElementName_1", linkedList.getFirst().getName());
        assertEquals("dtdElementName_2", linkedList.getLast().getName());
    }

    /**
     * Test of getXmlSchema method, of class ElementWrapper.
     */
    @Test
    public void testGetXmlSchema() {
        ElementWrapper instance = new ElementWrapper(schema, "dtdElementName");
        XSDSchema expResult = this.schema;
        XSDSchema result = instance.getXmlSchema();
        assertEquals(expResult, result);
    }

    /**
     * Test of setDtdAttributeMap method, of class ElementWrapper.
     */
    @Test
    public void testSetDtdAttributeMap() {
        ElementWrapper instance = new ElementWrapper(schema, "dtdElementName");
        Attribute attribute1 = new Attribute("dtdAttribute1", "");
        Attribute attribute2 = new Attribute("dtdAttribute2", "");

        LinkedHashMap<String, LinkedHashSet<Attribute>> newAttributeMap = new LinkedHashMap<String, LinkedHashSet<Attribute>>();

        LinkedHashSet<Attribute> tempSet1 = new LinkedHashSet<Attribute>();
        tempSet1.add(attribute1);
        LinkedHashSet<Attribute> tempSet2 = new LinkedHashSet<Attribute>();
        tempSet2.add(attribute2);
        newAttributeMap.put("dtdAttribute1", tempSet1);
        newAttributeMap.put("dtdAttribute2", tempSet2);

        instance.setDtdAttributeMap(newAttributeMap);

        assertEquals(2, instance.getDTDAttributeMap().size());
        assertEquals(1, instance.getDTDAttributeMap().get("dtdAttribute1").size());
        assertEquals(1, instance.getDTDAttributeMap().get("dtdAttribute2").size());
        assertEquals("dtdAttribute1", instance.getDTDAttributeMap().get("dtdAttribute1").iterator().next().getName());
        assertEquals("dtdAttribute2", instance.getDTDAttributeMap().get("dtdAttribute2").iterator().next().getName());
    }
}
