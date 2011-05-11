/**
 *
 */
package de.tudortmund.cs.bonxai.xsd.writer;

import java.util.LinkedList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.tudortmund.cs.bonxai.common.AnyAttribute;
import de.tudortmund.cs.bonxai.common.DefaultNamespace;
import de.tudortmund.cs.bonxai.common.IdentifiedNamespace;
import de.tudortmund.cs.bonxai.common.NamespaceList;
import de.tudortmund.cs.bonxai.common.ProcessContentsInstruction;
import de.tudortmund.cs.bonxai.common.SymbolTableRef;
import de.tudortmund.cs.bonxai.xsd.Attribute;
import de.tudortmund.cs.bonxai.xsd.AttributeGroup;
import de.tudortmund.cs.bonxai.xsd.AttributeGroupRef;
import de.tudortmund.cs.bonxai.xsd.AttributeParticle;
import de.tudortmund.cs.bonxai.xsd.AttributeRef;
import de.tudortmund.cs.bonxai.xsd.AttributeUse;
import de.tudortmund.cs.bonxai.xsd.SimpleContentList;
import de.tudortmund.cs.bonxai.xsd.SimpleType;
import de.tudortmund.cs.bonxai.xsd.Type;

/**
 * @author do
 *
 */
public class AttributeWriterTest extends junit.framework.TestCase
{
    private Document xmlDoc;
    private Element testElement, tmpEl;
    private FoundElements foundElements;

    @Before
    public void setUp()
    {
        DocumentBuilder db;
        DocumentBuilderFactory dbf;

        // get the factory
        dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);

        try
        {
            db = dbf.newDocumentBuilder();
            xmlDoc = db.newDocument();
        } catch (ParserConfigurationException e)
        {
            e.printStackTrace();
        }

        testElement = xmlDoc.createElement("testElement");
        foundElements = new FoundElements();
    }

    /**
     * Test method for {@link de.tudortmund.cs.bonxai.xsd.writer.AttributeWriter#writeAttributeParticles(org.w3c.dom.Node, java.util.LinkedList, de.tudortmund.cs.bonxai.xsd.writer.FoundElements)}.
     */
    @Test
    public void testWriteAttributeParticles()
    {
        LinkedList<AttributeParticle> attributes;
        AttributeParticle attrPart;
        SymbolTableRef<AttributeGroup> attributeGroupRef;
        AttributeGroup attrGroup;
        SymbolTableRef<Attribute> reference;
        Attribute attr;

        DefaultNamespace defaultNameSpace;
        IdentifiedNamespace idNs;
        NamespaceList nsList;

        defaultNameSpace = new DefaultNamespace("http://myNs");
        idNs = new IdentifiedNamespace("foo", "http://myIdNs");
        nsList = new NamespaceList(defaultNameSpace);
        nsList.addIdentifiedNamespace(idNs);
        foundElements.setNamespaceList(nsList);

        attributes =  new LinkedList<AttributeParticle>();

        attrPart = new Attribute("{http://myIdNs}myAttribute");
        attributes.add(attrPart);

        attrPart = new AnyAttribute("http://myIdNs");
        attributes.add(attrPart);

        attrGroup = new AttributeGroup("{http://myIdNs}myGroup");
        attrPart = new Attribute("{http://myIdNs}myGroupAttribute");
        attrGroup.addAttributeParticle(attrPart);
        attributeGroupRef = new SymbolTableRef<AttributeGroup>("myKey");
        attributeGroupRef.setReference(attrGroup);
        attrPart = new AttributeGroupRef(attributeGroupRef);
        attributes.add(attrPart);

        attr = new Attribute("{}myRefAttr");
        reference = new SymbolTableRef<Attribute>("myKey");
        reference.setReference(attr);
        attrPart = new AttributeRef(reference);
        attributes.add(attrPart);

        AttributeWriter.writeAttributeParticles(testElement, attributes, foundElements);

        tmpEl = (Element)DOMHelper.findByAttribute(testElement, "name", "foo:myAttribute");
        assert(tmpEl != null);
        assertEquals("attribute", tmpEl.getNodeName());

        tmpEl = (Element)DOMHelper.findChildNode(testElement, "anyAttribute");
        assert(tmpEl != null);
        assertEquals("http://myIdNs", tmpEl.getAttribute("namespace"));

        tmpEl = (Element)DOMHelper.findChildNode(testElement, "attributeGroup");
        assert(tmpEl != null);
        assertEquals("foo:myGroup", tmpEl.getAttribute("ref"));

        tmpEl = (Element)DOMHelper.findByAttribute(testElement, "ref", "myRefAttr");
        assert(tmpEl != null);
        assertEquals("attribute", tmpEl.getNodeName());
    }

    /**
     * Test method for {@link de.tudortmund.cs.bonxai.xsd.writer.AttributeWriter#writeAttributeList(org.w3c.dom.Node, java.util.LinkedList, de.tudortmund.cs.bonxai.xsd.writer.FoundElements)}.
     */
    @Test
    public void testWriteAttributeList()
    {
        LinkedList<Attribute> attributes;
        Attribute attr;

        attributes = new LinkedList<Attribute>();
        attr = new Attribute("{}myAttr1");
        attributes.add(attr);
        attr = new Attribute("{}myAttr2");
        attributes.add(attr);

        AttributeWriter.writeAttributeList(testElement, attributes, foundElements);

        assert( DOMHelper.findByAttribute(testElement, "name", "{}myAttr1") != null);
        assert( DOMHelper.findByAttribute(testElement, "name", "{}myAttr2") != null);
    }

    /**
     * Test method for {@link de.tudortmund.cs.bonxai.xsd.writer.AttributeWriter#writeAttribute(org.w3c.dom.Node, de.tudortmund.cs.bonxai.xsd.Attribute, de.tudortmund.cs.bonxai.xsd.writer.FoundElements)}.
     */
    /**
     *
     */
    @Test
    public void testWriteAttribute()
    {
//        <attribute id = ID
//        default = string
//        fixed = string
//        form = (qualified | unqualified)
//        name = NCName
//        ref = QName
//        type = QName
//        use = (optional | prohibited | required) : optional >
//        Content: (annotation?, (simpleType?)) </attribute>

        Attribute attribute;
        NamespaceList nslist;
        DefaultNamespace defaultns;
        IdentifiedNamespace namespace;
        SimpleType sType, baseType;

        SimpleContentList scList, scListBase;
        SymbolTableRef<Type> symRef, typRef;


        scListBase = new SimpleContentList(null);
        baseType = new SimpleType("{}baseType", scListBase);

        symRef = new SymbolTableRef<Type>("baseKey");
        symRef.setReference(baseType);
        scList = new SimpleContentList(symRef);

        attribute = new Attribute("{}myAttr");
        sType = new SimpleType("{}myType", scList);
        typRef = new SymbolTableRef<Type>("{}myRef");
        typRef.setReference(sType);
        attribute.setSimpleType(typRef);

        AttributeWriter.writeAttribute(testElement, attribute, foundElements);

        tmpEl = (Element)testElement.getFirstChild();
        assertEquals("attribute", tmpEl.getNodeName());
        assertEquals("myAttr", tmpEl.getAttribute("name"));
        assertEquals("optional", tmpEl.getAttribute("use"));
        tmpEl = (Element)DOMHelper.findChildNode(tmpEl, "simpleType");
        assert(tmpEl != null);
        assertEquals("myType", tmpEl.getAttribute("name"));

        // #################################################
        setUp();
        defaultns = new DefaultNamespace("http://example.com/default");
        namespace = new IdentifiedNamespace("foo", "http://example.com/foo");
        nslist = new NamespaceList(defaultns);
        nslist.addIdentifiedNamespace(namespace);
        foundElements.setNamespaceList(nslist);

        attribute = new Attribute("{http://example.com/foo}myAttr");
        attribute.setDefault("defVal");
        attribute.setFixed("fixedVal");
        attribute.setUse(AttributeUse.Optional);
        attribute.setSimpleType(typRef);
        foundElements.addType(typRef.getReference());

        AttributeWriter.writeAttribute(testElement, attribute, foundElements);
        tmpEl = (Element)testElement.getFirstChild();
        assertEquals("attribute", tmpEl.getNodeName());
        assertEquals("foo:myAttr", tmpEl.getAttribute("name"));
        assertEquals("defVal", tmpEl.getAttribute("default"));
        assertEquals("fixedVal", tmpEl.getAttribute("fixed"));
        assertEquals("optional", tmpEl.getAttribute("use"));
        assertEquals("myType", tmpEl.getAttribute("type"));

        // #################################################
        setUp();
        attribute.setUse(AttributeUse.Prohibited);
        AttributeWriter.writeAttribute(testElement, attribute, foundElements);
        tmpEl = (Element)testElement.getFirstChild();
        assertEquals("prohibited", tmpEl.getAttribute("use"));

        // #################################################
        setUp();
        attribute.setUse(AttributeUse.Required);
        AttributeWriter.writeAttribute(testElement, attribute, foundElements);
        tmpEl = (Element)testElement.getFirstChild();
        assertEquals("required", tmpEl.getAttribute("use"));
    }

    /**
     * Test method for {@link de.tudortmund.cs.bonxai.xsd.writer.AttributeWriter#writeAnyAttribute(org.w3c.dom.Node, de.tudortmund.cs.bonxai.common.AnyAttribute)}.
     */
    @Test
    public void testWriteAnyAttribute()
    {
//        <anyAttribute id = ID
//        namespace = ((##any | ##other) | List of (anyURI |
//        (##targetNamespace | ##local)) ) : ##any
//        processContents = (lax | skip | strict) : strict >
//        Content: (annotation?)</anyAttribute>
        AnyAttribute anyAttr;
        FoundElements foundElements = new FoundElements();
        anyAttr = new AnyAttribute();

        AttributeWriter.writeAnyAttribute(testElement, anyAttr, foundElements);
        tmpEl = (Element)testElement.getFirstChild();
        assertEquals("anyAttribute", tmpEl.getNodeName());
        assertEquals("##any", tmpEl.getAttribute("namespace"));
        assertEquals("strict", tmpEl.getAttribute("processContents"));

        setUp();
        anyAttr = new AnyAttribute(ProcessContentsInstruction.Lax);

        AttributeWriter.writeAnyAttribute(testElement, anyAttr, foundElements);
        tmpEl = (Element)testElement.getFirstChild();
        assertEquals("anyAttribute", tmpEl.getNodeName());
        assertEquals("##any", tmpEl.getAttribute("namespace"));
        assertEquals("lax", tmpEl.getAttribute("processContents"));

        setUp();
        anyAttr = new AnyAttribute(ProcessContentsInstruction.Skip);

        AttributeWriter.writeAnyAttribute(testElement, anyAttr, foundElements);
        tmpEl = (Element)testElement.getFirstChild();
        assertEquals("anyAttribute", tmpEl.getNodeName());
        assertEquals("##any", tmpEl.getAttribute("namespace"));
        assertEquals("skip", tmpEl.getAttribute("processContents"));

        setUp();
        anyAttr = new AnyAttribute(ProcessContentsInstruction.Lax, "ns");

        AttributeWriter.writeAnyAttribute(testElement, anyAttr, foundElements);
        tmpEl = (Element)testElement.getFirstChild();
        assertEquals("anyAttribute", tmpEl.getNodeName());
        assertEquals("ns", tmpEl.getAttribute("namespace"));
        assertEquals("lax", tmpEl.getAttribute("processContents"));
    }

    /**
     * Test method for {@link de.tudortmund.cs.bonxai.xsd.writer.AttributeWriter#writeAttributeRef(org.w3c.dom.Node, de.tudortmund.cs.bonxai.xsd.AttributeRef)}.
     */
    @Test
    public void testWriteAttributeRef()
    {
        Attribute a;
        AttributeRef attr;
        SymbolTableRef<Attribute> reference;

        a = new Attribute("{}myAttribute");
        reference = new SymbolTableRef<Attribute>("myKey");
        reference.setReference(a);

        attr = new AttributeRef(reference);

        AttributeWriter.writeAttributeRef(testElement, attr, foundElements);
        tmpEl = (Element)DOMHelper.findChildNode(testElement, "attribute");
        assert(tmpEl != null);

        assertEquals("myAttribute", tmpEl.getAttribute("ref"));
    }

    /**
     * Test method for {@link de.tudortmund.cs.bonxai.xsd.writer.AttributeWriter#writeAttributeGroupRef(org.w3c.dom.Node, de.tudortmund.cs.bonxai.xsd.AttributeGroupRef)}.
     */
    @Test
    public void testWriteAttributeGroupRef()
    {
//        <attributeGroup id = ID
//        name = NCName
//        ref = QName >
//        Content: (annotation?,
//        ((attribute | attributeGroup)*, anyAttribute?)) </attributeGroup>
        AttributeGroupRef ag;
        AttributeGroup group;
        SymbolTableRef<AttributeGroup> reference;

        group = new AttributeGroup("{}myAttrGroup");
        reference = new SymbolTableRef<AttributeGroup>("myKey");
        reference.setReference(group);
        ag = new AttributeGroupRef(reference);

        AttributeWriter.writeAttributeGroupRef(testElement, ag, foundElements);

        tmpEl = (Element)DOMHelper.findChildNode(testElement, "attributeGroup");
        assert(tmpEl != null);

        assertEquals("myAttrGroup", tmpEl.getAttribute("ref"));
    }

    /**
     * Test method for {@link de.tudortmund.cs.bonxai.xsd.writer.AttributeWriter#writeAttributeGroup(org.w3c.dom.Node, de.tudortmund.cs.bonxai.xsd.AttributeGroup, de.tudortmund.cs.bonxai.xsd.writer.FoundElements)}.
     */
    @Test
    public void testWriteAttributeGroup()
    {
//        <attributeGroup id = ID
//        name = NCName
//        ref = QName >
//        Content: (annotation?,
//        ((attribute | attributeGroup)*, anyAttribute?)) </attributeGroup>

        AttributeGroup attrGroup;
        Attribute attr1, attr2;

        attr1 = new Attribute("{}myAttr1");
        attr2 = new Attribute("{}myAttr2");
        attrGroup = new AttributeGroup("{}myGroup");

        attrGroup.addAttributeParticle(attr1);
        attrGroup.addAttributeParticle(attr2);
        AttributeWriter.writeAttributeGroup(testElement, attrGroup, foundElements);

        tmpEl = (Element)testElement.getFirstChild();
        assert(tmpEl != null);
        assertEquals("attributeGroup", tmpEl.getNodeName());

        tmpEl = (Element)tmpEl.getFirstChild();
        assertEquals("attribute", tmpEl.getNodeName());
        tmpEl = (Element)tmpEl.getNextSibling();
        assert(tmpEl != null);
        assertEquals("attribute", tmpEl.getNodeName());

        tmpEl = (Element)testElement.getFirstChild();
        assert(DOMHelper.findByAttribute(tmpEl, "name", "myAttr1") != null);
        assert(DOMHelper.findByAttribute(tmpEl, "name", "myAttr2") != null);
    }

}
