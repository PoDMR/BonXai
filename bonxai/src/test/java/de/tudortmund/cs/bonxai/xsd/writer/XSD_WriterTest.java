package de.tudortmund.cs.bonxai.xsd.writer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.LinkedList;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.tudortmund.cs.bonxai.common.AllPattern;
import de.tudortmund.cs.bonxai.common.AnyPattern;
import de.tudortmund.cs.bonxai.common.ChoicePattern;
import de.tudortmund.cs.bonxai.common.CountingPattern;
import de.tudortmund.cs.bonxai.common.DefaultNamespace;
import de.tudortmund.cs.bonxai.common.ElementRef;
import de.tudortmund.cs.bonxai.common.GroupRef;
import de.tudortmund.cs.bonxai.common.IdentifiedNamespace;
import de.tudortmund.cs.bonxai.common.NamespaceList;
import de.tudortmund.cs.bonxai.common.ProcessContentsInstruction;
import de.tudortmund.cs.bonxai.common.SequencePattern;
import de.tudortmund.cs.bonxai.common.SymbolTableRef;
import de.tudortmund.cs.bonxai.xsd.*;



/**
 *
 */
public class XSD_WriterTest extends junit.framework.TestCase {

    private XSDWriter writer;

    @Before
    public void setUp()
    {
        DocumentBuilderFactory dbf;
        DocumentBuilder db;
        dbf = DocumentBuilderFactory.newInstance();
        XSDSchema s = new XSDSchema();
        writer = new XSDWriter(s);


        try
        {
            db = dbf.newDocumentBuilder();
            writer.xmldoc = db.newDocument();
        } catch (ParserConfigurationException e1)
        {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }

    @Test
    public void testIncludedSchema()
    {
        XSDSchema s = new XSDSchema();
        DefaultNamespace defaultNameSpace;

        defaultNameSpace = new DefaultNamespace("http://defNS");
        NamespaceList nslist = new NamespaceList(defaultNameSpace);
        s.setNamespaceList(nslist);

        IncludedSchema inc = new IncludedSchema("myLocation");
        s.addForeignSchema(inc);
        inc = new IncludedSchema("myLocation2");
        s.addForeignSchema(inc);

        boolean found1 = false, found2 = false;
        XSDWriter writer = new XSDWriter(s);

        try {
            Document doc = writer.getXSDDocument();
            NodeList list = doc.getElementsByTagName("include");
            if(list.getLength() > 0)
            {
                for(int i = 0; i < list.getLength(); i++)
                {
                    Node n = list.item(i);
                    if(n.hasAttributes())
                    {
                        n = n.getAttributes().getNamedItem("schemaLocation");
                        if(n.getNodeValue().equals("myLocation"))
                            found1 = true;
                        if(n.getNodeValue().equals("myLocation2"))
                            found2 = true;
                    }
                }
                assertTrue("one Location was not found!", found1 && found2);
            } else
            {
                fail("No include Nodes were found!");
            }
        } catch (Exception e) {
            fail("Exception thrown: \r\n" + e.getLocalizedMessage());
        }
    }

    @Test
    public void testImportedSchema()
    {
        DefaultNamespace defaultNameSpace;
        XSDSchema s = new XSDSchema();

        defaultNameSpace = new DefaultNamespace("http://defNS");
        NamespaceList nslist = new NamespaceList(defaultNameSpace);
        s.setNamespaceList(nslist);

        ImportedSchema imp = new ImportedSchema("myNamespace", "myLocation");
        s.addForeignSchema(imp);
        imp = new ImportedSchema("myNamespace2", "myLocation2");
        s.addForeignSchema(imp);
        Node n, attr1, attr2;
        boolean found1 = false, found2= false;

        XSDWriter writer = new XSDWriter(s);
        try {
            Document doc = writer.getXSDDocument();
            NodeList list = doc.getElementsByTagName("import");
            if(list.getLength() > 0)
            {
                for(int i = 0; i < list.getLength(); i++)
                {
                    n = list.item(i);
                    if(n.hasAttributes())
                    {
                        attr1 = n.getAttributes().getNamedItem("schemaLocation");
                        attr2 = n.getAttributes().getNamedItem("namespace");

                        if(attr1.getNodeValue().equals("myLocation")
                                && attr2.getNodeValue().equals("myNamespace"))
                        {
                            found1 = true;
                        }
                        if(attr1.getNodeValue().equals("myLocation2")
                                && attr2.getNodeValue().equals("myNamespace2"))
                        {
                            found2 = true;
                        }
                    }
                }
                assertTrue("a Location or a Namespace was not found!", found1 && found2);
            } else
            {
                fail("No include Nodes were found!");
            }
        } catch (Exception e) {
            fail("Exception thrown: \r\n" + e.getLocalizedMessage());
        }
    }

    @Test
    public void testRedefinedSchema()
    {
//        <redefine id = ID
//        schemaLocation = anyURI>
//        Content: (annotation | (simpleType | complexType | group | attributeGroup))*
//        </redefine>
        org.w3c.dom.Element tstEl1;//, tstEl2;
        XSDSchema s = new XSDSchema();
        DefaultNamespace defaultNameSpace;

        defaultNameSpace = new DefaultNamespace("www.sss.de");
        NamespaceList nslist = new NamespaceList(defaultNameSpace);
        nslist.addIdentifiedNamespace(new IdentifiedNamespace("xs", "http://www.w3.org/2001/XMLSchema"));
        s.setNamespaceList(nslist);

        RedefinedSchema redefSchema = new RedefinedSchema("http://myLocation");
        SimpleType sType;
        SymbolTableRef<Type> symRef;

        Group group;
        SequencePattern container;
        Element particle;
        SymbolTableRef<Group> symRefGroup;

        AttributeGroup attGroup;
        Attribute attr;
        SymbolTableRef<AttributeGroup> symRefAttrGroup;

        sType = new SimpleType("{}myType", null);
        symRef = new SymbolTableRef<Type>("myKey");
        symRef.setReference(sType);

        redefSchema.addType(symRef);

        container = new SequencePattern();
        particle = new Element("{}myElement");
        container.addParticle(particle);
        group = new Group("{}myGroup", container);

        symRefGroup = new SymbolTableRef<Group>("myGroup");
        symRefGroup.setReference(group);
        redefSchema.addGroup(symRefGroup);

        attGroup = new AttributeGroup("{}myAttrGroup");
        attr = new Attribute("{}myAttr");
        attGroup.addAttributeParticle(attr);
        symRefAttrGroup = new SymbolTableRef<AttributeGroup>("myAttrGroup");
        symRefAttrGroup.setReference(attGroup);
        redefSchema.addAttributeGroup(symRefAttrGroup);


        s.addForeignSchema(redefSchema);

        writer.schema = s;

        writer.writeSchemaHeader(s, new FoundElements());

        try
        {
            tstEl1 = (org.w3c.dom.Element)DOMHelper.findChildNode(writer.xmldoc, "redefine");
            assert(tstEl1 != null);
            assertEquals("http://myLocation", tstEl1.getAttribute("schemaLocation"));

            tstEl1 = (org.w3c.dom.Element)DOMHelper.findChildNode(writer.xmldoc, "simpleType");
            assert(tstEl1 != null);
            assertEquals("myType", tstEl1.getAttribute("name"));

            tstEl1 = (org.w3c.dom.Element)DOMHelper.findChildNode(writer.xmldoc, "group");
            assert(tstEl1 != null);
            assertEquals("myGroup", tstEl1.getAttribute("name"));
            tstEl1 = (org.w3c.dom.Element)DOMHelper.findChildNode(tstEl1, "sequence");
            assert(tstEl1 != null);
            tstEl1 = (org.w3c.dom.Element)DOMHelper.findChildNode(tstEl1, "element");
            assert(tstEl1 != null);
            assertEquals("myElement", tstEl1.getAttribute("name"));

            tstEl1 = (org.w3c.dom.Element)DOMHelper.findChildNode(writer.xmldoc, "attributeGroup");
            assert(tstEl1 != null);
            assertEquals("myAttrGroup", tstEl1.getAttribute("name"));
            tstEl1 = (org.w3c.dom.Element)DOMHelper.findChildNode(tstEl1, "attribute");
            assert(tstEl1 != null);
            assertEquals("myAttr", tstEl1.getAttribute("name"));
        }catch (Exception e)
        {
            fail("Exception was thrown");
        }
    }

    @Test
    public void testSetSchema() {
        XSDSchema s = new XSDSchema();
        IncludedSchema inc = new IncludedSchema("myLocation");
        DefaultNamespace defaultNameSpace;

        defaultNameSpace = new DefaultNamespace("http://defNS");
        NamespaceList nslist = new NamespaceList(defaultNameSpace);
        s.setNamespaceList(nslist);

        s.addForeignSchema(inc);
        XSDWriter w = new XSDWriter(s);
        try
        {
            s = new XSDSchema();

            inc = new IncludedSchema("myLoc");
            defaultNameSpace = new DefaultNamespace("http://defNS");
            nslist = new NamespaceList(defaultNameSpace);
            s.setNamespaceList(nslist);

            s.addForeignSchema(inc);

            w.setSchema(s);

            Document doc = w.getXSDDocument();
            NodeList list = doc.getElementsByTagName("include");
            if(list.getLength() > 0)
            {
                for(int i = 0; i < list.getLength(); i++)
                {
                    Node n = list.item(i);
                    if(n.hasAttributes())
                    {
                        n = n.getAttributes().getNamedItem("schemaLocation");
                        assertTrue(n.getNodeValue().equals("myLoc"));
                    }
                }
            } else
            {
                fail("No include Nodes were found!");
            }
        } catch (Exception e)
        {
            fail("exception thrown: " + e.getMessage() + "\r\n\r\n");
        }

    }

    @Test
    public void testGetXSDString() {
        XSDSchema s;
        XSDWriter w;
        String result;
        DefaultNamespace defaultNameSpace;

        defaultNameSpace = new DefaultNamespace("http://www.w3.org/2001/XMLSchema");
        NamespaceList nslist = new NamespaceList(defaultNameSpace);
        s = new XSDSchema();
        s.setNamespaceList(nslist);

        w = new XSDWriter(s);
        try
        {
            result = w.getXSDString();
            assertTrue(result.trim().startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>"));
            assertTrue(result.trim().endsWith("<schema xmlns=\"http://www.w3.org/2001/XMLSchema\" elementFormDefault=\"qualified\"/>"));
        } catch (Exception e)
        {
            fail("exception thrown: " + e.getMessage() + "\r\n\r\n");
        }
    }

    @Test
    public void testGetXSDDocument() {
        XSDSchema s;
        XSDWriter w;
        DefaultNamespace defaultNameSpace;
        s = new XSDSchema();

        defaultNameSpace = new DefaultNamespace("http://defNS");
        NamespaceList nslist = new NamespaceList(defaultNameSpace);
        s.setNamespaceList(nslist);

        IncludedSchema inc = new IncludedSchema("myLocation");
        s.addForeignSchema(inc);

        w = new XSDWriter(s);
        try {
            Document doc = w.getXSDDocument();
            assertTrue(doc.getChildNodes().item(0).getNodeName().equals("schema"));
            NodeList list = doc.getElementsByTagName("include");
            if(list.getLength() > 0)
            {
                for(int i = 0; i < list.getLength(); i++)
                {
                    Node n = list.item(i);
                    if(n.hasAttributes())
                    {
                        n = n.getAttributes().getNamedItem("schemaLocation");
                        assertTrue(n.getNodeValue().equals("myLocation"));
                    }
                }
            } else
            {
                fail("No include Nodes were found!");
            }
        } catch (Exception e) {
            fail("Exception thrown: \r\n" + e.getLocalizedMessage());
        }
    }

    @Test
    public void testWriteXSD() {
        XSDSchema s;
        XSDWriter w;
        File f;
        DefaultNamespace defaultNameSpace;

        s = new XSDSchema();
        defaultNameSpace = new DefaultNamespace("http://defNS");
        NamespaceList nslist = new NamespaceList(defaultNameSpace);
        s.setNamespaceList(nslist);
        IncludedSchema inc = new IncludedSchema("myLocation");
        s.addForeignSchema(inc);
        w = new XSDWriter(s);
        f = new File( "tst.xml" );

        try {
            w.writeXSD("tst.xml");

            DocumentBuilder db;
            DocumentBuilderFactory dbf;
            Document doc;

            // get the factory
            dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware( true );

            db = dbf.newDocumentBuilder();

            // parse using builder to get DOM representation of the XML file
            doc = db.parse( f );

            assertTrue(doc.getChildNodes().item(0).getNodeName().equals("schema"));
            NodeList list = doc.getElementsByTagName("include");
            if(list.getLength() > 0)
            {
                for(int i = 0; i < list.getLength(); i++)
                {
                    Node n = list.item(i);
                    if(n.hasAttributes())
                    {
                        n = n.getAttributes().getNamedItem("schemaLocation");
                        assertTrue(n.getNodeValue().equals("myLocation"));
                    }
                }
            } else
            {
                fail("No include Nodes were found!");
            }

        } catch (Exception e) {
            fail("exception thrown: " + e.getMessage());

        } finally
        {
            if(f != null && f.exists())
            {
                f.delete();
            }
        }

    }

    /**
     *
    <?xml version="1.0" encoding="ISO-8859-1" ?>
     <xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema">
     <xs:element name="shiporder">
       <xs:complexType>
         <xs:sequence>
           <xs:element name="orderperson" type="xs:string"/>
           <xs:element name="shipto">
             <xs:complexType>
               <xs:sequence>
                 <xs:element name="name" type="xs:string"/>
                 <xs:element name="address" type="xs:string"/>
                 <xs:element name="city" type="xs:string"/>
                 <xs:element name="country" type="xs:string"/>
               </xs:sequence>
             </xs:complexType>
           </xs:element>
           <xs:element name="item" maxOccurs="unbounded">
             <xs:complexType>
               <xs:sequence>
                 <xs:element name="title" type="xs:string"/>
                 <xs:element name="note" type="xs:string" minOccurs="0"/>
                 <xs:element name="quantity" type="xs:positiveInteger"/>
                 <xs:element name="price" type="xs:decimal"/>
               </xs:sequence>
             </xs:complexType>
           </xs:element>
         </xs:sequence>
         <xs:attribute name="orderid" type="xs:string" use="required"/>
       </xs:complexType>
     </xs:element>
     </xs:schema>
     **/
    @Test
    public void testShiporderSchema() {
        //schema + namespace stuff
        XSDSchema schema = new XSDSchema();
        DefaultNamespace defaultNameSpace = new DefaultNamespace("");
        NamespaceList namespaceList = new NamespaceList(defaultNameSpace);
        namespaceList.addIdentifiedNamespace(new IdentifiedNamespace("xs", "http://www.w3.org/2001/XMLSchema"));
        schema.setNamespaceList(namespaceList);


        //XML Schema types
        SimpleType stringType = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        schema.getTypeSymbolTable().updateOrCreateReference("{http://www.w3.org/2001/XMLSchema}string", stringType);
        SimpleType positiveIntegerType = new SimpleType("{http://www.w3.org/2001/XMLSchema}positiveInteger", null);
        schema.getTypeSymbolTable().updateOrCreateReference("{http://www.w3.org/2001/XMLSchema}positiveInteger", positiveIntegerType);
        SimpleType decimalType = new SimpleType("{http://www.w3.org/2001/XMLSchema}decimal", null);
        schema.getTypeSymbolTable().updateOrCreateReference("{http://www.w3.org/2001/XMLSchema}decimal", decimalType);

        //orderpersonElement
        Element orderPersonElement = new de.tudortmund.cs.bonxai.xsd.Element("{}orderperson", schema.getTypeSymbolTable().getReference("{http://www.w3.org/2001/XMLSchema}string"));

        //subelements of shipto
        Element nameElement = new de.tudortmund.cs.bonxai.xsd.Element("{}name", schema.getTypeSymbolTable().getReference("{http://www.w3.org/2001/XMLSchema}string"));
        Element addressElement = new de.tudortmund.cs.bonxai.xsd.Element("{}address", schema.getTypeSymbolTable().getReference("{http://www.w3.org/2001/XMLSchema}string"));
        Element cityElement = new de.tudortmund.cs.bonxai.xsd.Element("{}city", schema.getTypeSymbolTable().getReference("{http://www.w3.org/2001/XMLSchema}string"));
        Element countryElement = new de.tudortmund.cs.bonxai.xsd.Element("{}country", schema.getTypeSymbolTable().getReference("{http://www.w3.org/2001/XMLSchema}string"));

        //shipto type
        SequencePattern shiptoSequencePattern = new SequencePattern();
        shiptoSequencePattern.addParticle(nameElement);
        shiptoSequencePattern.addParticle(addressElement);
        shiptoSequencePattern.addParticle(cityElement);
        shiptoSequencePattern.addParticle(countryElement);

        ComplexContentType shiptoComplexContentType = new ComplexContentType();
        shiptoComplexContentType.setParticle(shiptoSequencePattern);
        ComplexType shiptoComplexType = new ComplexType("{}shiptoType", shiptoComplexContentType);
        schema.getTypeSymbolTable().updateOrCreateReference("{}shiptoType", shiptoComplexType);

        //shipto element
        Element shiptoElement = new de.tudortmund.cs.bonxai.xsd.Element("{}shipto", schema.getTypeSymbolTable().getReference("{}shiptoType"));

        // subelements of item type
        Element titleElement = new de.tudortmund.cs.bonxai.xsd.Element("{}title", schema.getTypeSymbolTable().getReference("{http://www.w3.org/2001/XMLSchema}string"));
        Element noteElement = new de.tudortmund.cs.bonxai.xsd.Element("{}note", schema.getTypeSymbolTable().getReference("{http://www.w3.org/2001/XMLSchema}string"));
        Element quantityElement = new de.tudortmund.cs.bonxai.xsd.Element("{}quantity", schema.getTypeSymbolTable().getReference("{http://www.w3.org/2001/XMLSchema}positiveInteger"));
        Element priceElement = new de.tudortmund.cs.bonxai.xsd.Element("{}price", schema.getTypeSymbolTable().getReference("{http://www.w3.org/2001/XMLSchema}decimal"));

        //item type
        SequencePattern itemSequencePattern = new SequencePattern();
        itemSequencePattern.addParticle(titleElement);
        itemSequencePattern.addParticle(noteElement);
        itemSequencePattern.addParticle(quantityElement);
        itemSequencePattern.addParticle(priceElement);

        ComplexContentType itemComplexContentType = new ComplexContentType();
        itemComplexContentType.setParticle(itemSequencePattern);
        ComplexType itemComplexType = new ComplexType("{}itemType", itemComplexContentType);
        schema.getTypeSymbolTable().updateOrCreateReference("{}itemType", itemComplexType);

        //item element
        Element itemElement = new de.tudortmund.cs.bonxai.xsd.Element("{}item", schema.getTypeSymbolTable().getReference("{}itemType"));
        CountingPattern itemCountingPattern = new CountingPattern(1, null);
        itemCountingPattern.addParticle(itemElement);

        //shiporderType
        SequencePattern shiporderSequencePattern = new SequencePattern();
        shiporderSequencePattern.addParticle(orderPersonElement);
        shiporderSequencePattern.addParticle(shiptoElement);
        shiporderSequencePattern.addParticle(itemCountingPattern);

        ComplexContentType shiporderComplexContentType = new ComplexContentType();
        shiporderComplexContentType.setParticle(shiporderSequencePattern);
        ComplexType shiporderComplexType = new ComplexType("{}shiporderType", shiporderComplexContentType);
        schema.getTypeSymbolTable().updateOrCreateReference("{}shiporderType", shiporderComplexType);

        Attribute attribute = new Attribute ("{}orderid", schema.getTypeSymbolTable().getReference("{http://www.w3.org/2001/XMLSchema}string"));
        attribute.setUse( AttributeUse.Required);
        shiporderComplexType.addAttribute(attribute);

        //shiporder element
        Element shiporderElement = new de.tudortmund.cs.bonxai.xsd.Element("{}shiporder", schema.getTypeSymbolTable().getReference("{}shiporderType"));
        schema.getElementSymbolTable().updateOrCreateReference("{}shiporder", shiporderElement);
        schema.addElement(schema.getElementSymbolTable().getReference("{}shiporder"));

        assertSchemaSame(schema, "shiporder");
    }

    /**
    <?xml version="1.0" encoding="UTF-8" ?>
    <xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:lib="http://www.example.org/library" xmlns:xml="http://www.w3.org/XML/1998/namespace"  xmlns="http://example.org/books">

     <!-- foreign schemas -->
     <xs:import namespace="http://www.w3.org/XML/1998/namespace" schemaLocation="myxml.xsd"/>
     <xs:include schemaLocation="character.xsd"/>
     <xs:redefine schemaLocation="character12.xsd">
      <xs:simpleType name="nameType">
        <xs:restriction base="xs:string">
          <xs:maxLength value="40"/>
        </xs:restriction>
      </xs:simpleType>
     </xs:redefine>

      <!-- definition of simple types -->
     <xs:simpleType name="isbnType">
        <xs:union>
          <xs:simpleType>
            <xs:restriction base="xs:string">
              <xs:pattern value="[0-9]{10}"/>
            </xs:restriction>
          </xs:simpleType>
          <xs:simpleType>
            <xs:restriction base="xs:NMTOKEN">
              <xs:enumeration value="TBD"/>
              <xs:enumeration value="NA"/>
            </xs:restriction>
          </xs:simpleType>
        </xs:union>
      </xs:simpleType>

      <!-- definition of complex types -->
        <xs:complexType name="descType" mixed="true">
        <xs:sequence>
          <xs:any namespace="http://www.w3.org/1999/xhtml" processContents="skip" minOccurs="0" maxOccurs="unbounded"/>
        </xs:sequence>
      </xs:complexType>

      <xs:complexType name="characterType" final="#all">
        <xs:sequence>
          <xs:group ref="nameTypes"/>
          <xs:element name="friend-of" type="lib:nameType" minOccurs="0" maxOccurs="unbounded"/>
          <xs:element name="since" type="lib:sinceType"/>
          <xs:element name="qualification" type="descType"/>
        </xs:sequence>
      </xs:complexType>

      <xs:complexType name="bookType">
        <xs:complexContent>
         <xs:extension base="lib:bookType">
              <xs:all>
                <xs:element name="title">
                  <xs:complexType>
                    <xs:simpleContent>
                      <xs:extension base="xs:string">
                        <xs:attribute ref="xml:lang"/>
                      </xs:extension>
                    </xs:simpleContent>
                  </xs:complexType>
                </xs:element>
                <xs:element name="author" type="xs:string"/>
                <xs:element name="character" type="characterType" minOccurs="0" maxOccurs="1"/>
              </xs:all>
            <xs:attributeGroup ref="bookAttributes"/>
          </xs:extension>
        </xs:complexContent>
      </xs:complexType>

      <!-- reference to "bookType" to define the "book" element -->
      <xs:element name="book" type="bookType">
        <xs:key name="charName">
          <xs:selector xpath="character"/>
          <xs:field xpath="name"/>
        </xs:key>
        <xs:keyref name="charNameRef" refer="charName">
          <xs:selector xpath="character"/>
          <xs:field xpath="friend-of"/>
        </xs:keyref>
      </xs:element>

      <!-- definition of an element group -->
      <xs:group name="nameTypes">
        <xs:choice>
          <xs:element name="name" type="xs:string"/>
          <xs:sequence>
            <xs:element name="firstName" type="xs:string"/>
            <xs:element name="middleName" type="xs:string" minOccurs="0"/>
            <xs:element name="lastName" type="xs:string"/>
          </xs:sequence>
        </xs:choice>
      </xs:group>

      <!-- definition of an attribute group -->
      <xs:attributeGroup name="bookAttributes">
        <xs:attribute name="isbn" type="isbnType" use="required"/>
        <xs:attribute name="available" type="xs:string"/>
      </xs:attributeGroup>
    </xs:schema>
     **/
    @Test
    public void testbookSchema() {
        //schema + namespace stuff
        XSDSchema schema = new XSDSchema();
        DefaultNamespace defaultNameSpace = new DefaultNamespace("http://www.example.org/books");
        NamespaceList namespaceList = new NamespaceList(defaultNameSpace);
        namespaceList.addIdentifiedNamespace(new IdentifiedNamespace("xs", "http://www.w3.org/2001/XMLSchema"));
        namespaceList.addIdentifiedNamespace(new IdentifiedNamespace("lib", "http://www.example.org/library"));
        namespaceList.addIdentifiedNamespace(new IdentifiedNamespace("xml", "http://www.w3.org/XML/1998/namespace"));
        schema.setNamespaceList(namespaceList);

        //foreign schemas
        schema.addForeignSchema(new ImportedSchema("http://www.w3.org/XML/1998/namespace", "myxml.xsd"));
        schema.addForeignSchema(new IncludedSchema("character.xsd"));
        RedefinedSchema redefinedSchema = new RedefinedSchema("character12.xsd");

        SimpleType stringType = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        SymbolTableRef<Type> stringTypeRef = new SymbolTableRef<Type>("{http://www.w3.org/2001/XMLSchema}string");
        stringTypeRef.setReference(stringType);

        SimpleContentRestriction simpleContentInheritance = new SimpleContentRestriction(stringTypeRef);
        simpleContentInheritance.setMaxLength(new SimpleContentFixableRestrictionProperty<Integer>(40, false));
        SimpleType nameType = new SimpleType("{}nameType", simpleContentInheritance);

        SymbolTableRef<Type> nameTypeRef = new SymbolTableRef<Type>("{}nameType");
        nameTypeRef.setReference(nameType);
        redefinedSchema.addType(nameTypeRef);

        schema.addForeignSchema(redefinedSchema);

        //definition of simple types

        //isbn type
        stringType = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        schema.getTypeSymbolTable().updateOrCreateReference("{http://www.w3.org/2001/XMLSchema}string", stringType);

        simpleContentInheritance = new SimpleContentRestriction(schema.getTypeSymbolTable().getReference("{http://www.w3.org/2001/XMLSchema}string"));
        simpleContentInheritance.setPattern(new SimpleContentFixableRestrictionProperty<String>("[0-9]{10}", false));
        SimpleType restrictedStringType = new SimpleType("{http://www.example.org/books}restrictedStringType", simpleContentInheritance);
        SymbolTableRef<Type> restrictedStringTypeRef = new SymbolTableRef<Type>("{http://example.org/books}restrictedStringType");
        restrictedStringTypeRef.setReference(restrictedStringType);

        SimpleType nmTokenType = new SimpleType("{http://www.w3.org/2001/XMLSchema}NMToken", null);
        schema.getTypeSymbolTable().updateOrCreateReference("{http://www.w3.org/2001/XMLSchema}NMToken", nmTokenType);

        simpleContentInheritance = new SimpleContentRestriction(schema.getTypeSymbolTable().getReference("{http://www.w3.org/2001/XMLSchema}NMToken"));
        LinkedList<String> enumeration = new LinkedList<String>();
        enumeration.add("TBD");
        enumeration.add("NA");
        simpleContentInheritance.addEnumeration(enumeration);
        SimpleType restrictedNMTokenType = new SimpleType("{http://www.example.org/books}restrictedNMTokenType", simpleContentInheritance);
        SymbolTableRef<Type> restrictedNMTokenTypeRef = new SymbolTableRef<Type>("{http://example.org/books}restrictedNMTokenType");
        restrictedNMTokenTypeRef.setReference(restrictedNMTokenType);

        LinkedList<SymbolTableRef<Type>> unionTypes = new LinkedList<SymbolTableRef<Type>>();
        unionTypes.add(restrictedStringTypeRef);
        unionTypes.add(restrictedNMTokenTypeRef);

        SimpleContentUnion simpleContentUnion = new SimpleContentUnion(unionTypes);

        SimpleType isbnType = new SimpleType("{http://www.example.org/books}isbn", simpleContentUnion);
        schema.getTypeSymbolTable().updateOrCreateReference("{http://www.example.org/books}isbn", isbnType);
        schema.addType(schema.getTypeSymbolTable().getReference("{http://www.example.org/books}isbn"));

        //definition of complex types

        //descType
        AnyPattern anyPattern = new AnyPattern(ProcessContentsInstruction.Skip, "http://www.w3.org/1999/xhtml");
        CountingPattern countingPattern = new CountingPattern(0,null);
        countingPattern.addParticle(anyPattern);

        SequencePattern sequencePattern = new SequencePattern();
        sequencePattern.addParticle(countingPattern);

        ComplexContentType complexContentType = new ComplexContentType();
        complexContentType.setParticle(sequencePattern);
        complexContentType.setMixed(true);
        ComplexType complexType = new ComplexType("{http://www.example.org/books}descType", complexContentType);
        schema.getTypeSymbolTable().updateOrCreateReference("{http://www.example.org/books}descType", complexType);
        schema.addType(schema.getTypeSymbolTable().getReference("{http://www.example.org/books}descType"));

        //characterType
        Group group = new Group("{http://www.example.org/books}nameTypes", null);
        schema.getGroupSymbolTable().updateOrCreateReference("{http://www.example.org/books}nameTypes", group);
        GroupRef groupRef = new GroupRef(schema.getGroupSymbolTable().getReference("{http://www.example.org/books}nameTypes"));
        sequencePattern = new SequencePattern();
        sequencePattern.addParticle(groupRef);

        SimpleType type = new SimpleType("{http://www.example.org/library}nameType", null);
        schema.getTypeSymbolTable().updateOrCreateReference("{http://www.example.org/library}friend-ofType", type);
        type = new SimpleType("{http://www.example.org/library}sinceType", null);
        schema.getTypeSymbolTable().updateOrCreateReference("{http://www.example.org/library}sinceType", type);

        Element element = new de.tudortmund.cs.bonxai.xsd.Element("{http://www.example.org/books}friend-of", schema.getTypeSymbolTable().getReference("{http://www.example.org/library}nameType"));
        countingPattern = new CountingPattern(0,null);
        countingPattern.addParticle(element);
        sequencePattern.addParticle(countingPattern);

        element = new de.tudortmund.cs.bonxai.xsd.Element("{http://www.example.org/books}since", schema.getTypeSymbolTable().getReference("{http://www.example.org/library}sinceType"));
        sequencePattern.addParticle(element);

        element = new de.tudortmund.cs.bonxai.xsd.Element("{http://www.example.org/books}qualification", schema.getTypeSymbolTable().getReference("{http://www.example.org/books}descType"));
        sequencePattern.addParticle(element);

        complexContentType = new ComplexContentType();
        complexContentType.setParticle(sequencePattern);
        complexType = new ComplexType("{http://www.example.org/books}characterType", complexContentType);
        complexType.addFinalModifier(ComplexTypeInheritanceModifier.Restriction);
        complexType.addFinalModifier(ComplexTypeInheritanceModifier.Extension);

        schema.getTypeSymbolTable().updateOrCreateReference("{http://www.example.org/books}characterType", complexType);
        schema.addType(schema.getTypeSymbolTable().getReference("{http://www.example.org/books}characterType"));

        //book type

        //inner title element
        SimpleContentExtension simpleContentExtension = new SimpleContentExtension(schema.getTypeSymbolTable().getReference("{http://www.w3.org/2001/XMLSchema}string"));
        SimpleContentType simpleContentType = new SimpleContentType();
        simpleContentType.setInheritance(simpleContentExtension);

        Attribute attribute = new Attribute("{http://www.w3.org/XML/1998/namespace}lang");
        schema.getAttributeSymbolTable().updateOrCreateReference("{http://www.w3.org/XML/1998/namespace}lang", attribute);
        AttributeRef attributeRef = new AttributeRef(schema.getAttributeSymbolTable().getReference("{http://www.w3.org/XML/1998/namespace}lang"));
        complexType = new ComplexType("{http://www.example.org/books}titleType", simpleContentType);
        complexType.addAttribute(attributeRef);
        schema.getTypeSymbolTable().updateOrCreateReference("{http://www.example.org/books}titleType", complexType);

        element = new de.tudortmund.cs.bonxai.xsd.Element("{http://www.example.org/books}title", schema.getTypeSymbolTable().getReference("{http://www.example.org/books}titleType"));
        AllPattern allPattern = new AllPattern();
        allPattern.addParticle(element);

        //inner author element
        element = new de.tudortmund.cs.bonxai.xsd.Element("{http://www.example.org/books}author", schema.getTypeSymbolTable().getReference("{http://www.w3.org/2001/XMLSchema}string"));
        allPattern.addParticle(element);

        //inner character element
        countingPattern = new CountingPattern(0,1);
        element = new de.tudortmund.cs.bonxai.xsd.Element("{http://www.example.org/books}character", schema.getTypeSymbolTable().getReference("{http://www.example.org/books}characterType"));
        countingPattern.addParticle(element);
        allPattern.addParticle(countingPattern);

        complexType = new ComplexType("{http://www.example.org/library}bookType", null);
        schema.getTypeSymbolTable().updateOrCreateReference("{http://www.example.org/library}bookType", complexType);
        ComplexContentExtension complexContentExtension = new ComplexContentExtension(schema.getTypeSymbolTable().getReference("{http://www.example.org/library}bookType"));
        complexContentType = new ComplexContentType();
        complexContentType.setInheritance(complexContentExtension);
        complexContentType.setParticle(allPattern);
        complexType = new ComplexType("{http://www.example.org/books}bookType", complexContentType);

        AttributeGroup attributeGroup = new AttributeGroup("{http://www.example.org/books}bookAttributes");
        schema.getAttributeGroupSymbolTable().updateOrCreateReference("{http://www.example.org/books}bookAttributes", attributeGroup);
        AttributeGroupRef attributeGroupRef = new AttributeGroupRef(schema.getAttributeGroupSymbolTable().getReference("{http://www.example.org/books}bookAttributes"));
        complexType.addAttribute(attributeGroupRef);

        schema.getTypeSymbolTable().updateOrCreateReference("{http://www.example.org/books}bookType", complexType);
        schema.addType(schema.getTypeSymbolTable().getReference("{http://www.example.org/books}bookType"));

        //book element
        element = new Element("{http://www.example.org/books}book", schema.getTypeSymbolTable().getReference("{http://www.example.org/books}bookType"));

        Key key = new Key("{http://www.example.org/books}charName", "character");
        key.addField("name");
        schema.getKeyAndUniqueSymbolTable().updateOrCreateReference("{http://www.example.org/books}charName", key);
        element.addConstraint(key);

        KeyRef keyRef = new KeyRef("charNameRef", "character", schema.getKeyAndUniqueSymbolTable().getReference("{http://www.example.org/books}charName"));
        keyRef.addField("friend-of");
        element.addConstraint(keyRef);

        schema.getElementSymbolTable().updateOrCreateReference("{http://www.example.org/books}book", element);
        schema.addElement(schema.getElementSymbolTable().getReference("{http://www.example.org/books}book"));

        //element group nameTypes
        ChoicePattern choicePattern = new ChoicePattern();
        element = new de.tudortmund.cs.bonxai.xsd.Element("{http://www.example.org/books}name", schema.getTypeSymbolTable().getReference("{http://www.w3.org/2001/XMLSchema}string"));
        choicePattern.addParticle(element);

        sequencePattern = new SequencePattern();
        element = new de.tudortmund.cs.bonxai.xsd.Element("{http://www.example.org/books}firstName", schema.getTypeSymbolTable().getReference("{http://www.w3.org/2001/XMLSchema}string"));
        sequencePattern.addParticle(element);

        countingPattern = new CountingPattern(0,1);
        element = new de.tudortmund.cs.bonxai.xsd.Element("{http://www.example.org/books}middleName", schema.getTypeSymbolTable().getReference("{http://www.w3.org/2001/XMLSchema}string"));
        countingPattern.addParticle(element);
        sequencePattern.addParticle(countingPattern);

        element = new de.tudortmund.cs.bonxai.xsd.Element("{http://www.example.org/books}lastName", schema.getTypeSymbolTable().getReference("{http://www.w3.org/2001/XMLSchema}string"));
        sequencePattern.addParticle(element);

        choicePattern.addParticle(sequencePattern);

        group = new Group("{http://www.example.org/books}nameTypes", choicePattern);
        schema.getGroupSymbolTable().updateOrCreateReference("{http://www.example.org/books}nameTypes", group);
        schema.addGroup(schema.getGroupSymbolTable().getReference("{http://www.example.org/books}nameTypes"));

        //attribute group bookAttributes
        attributeGroup = new AttributeGroup("{http://www.example.org/books}bookAttributes");
        attribute = new Attribute("{http://www.example.org/books}isbn", schema.getTypeSymbolTable().getReference("{http://www.example.org/books}isbn"));
        attribute.setUse(AttributeUse.Required);
        attributeGroup.addAttributeParticle(attribute);

        attribute = new Attribute("{http://www.example.org/books}available", schema.getTypeSymbolTable().getReference("{http://www.w3.org/2001/XMLSchema}string"));
        attributeGroup.addAttributeParticle(attribute);

        schema.getAttributeGroupSymbolTable().updateOrCreateReference("{http://www.example.org/books}bookAttributes", attributeGroup);
        schema.addAttributeGroup(schema.getAttributeGroupSymbolTable().getReference("{http://www.example.org/books}bookAttributes"));

        assertSchemaSame(schema, "library");
    }

    /**
     * Compare the given schema instance with the stored output of the given
     * name.
     */
    protected void assertSchemaSame(XSDSchema xsd, String name) {
        XSDWriter writer = new XSDWriter(xsd);

        String contents = "";
        String result   = "";
        boolean failed  = false;

        try {
            result   = writer.getXSDString();
            URL fileuri = getClass().getClassLoader().getResource("de/tudortmund/cs/bonxai/xsd/writer/data/" + name + ".xsd");
            String uriStr = fileuri.toString();
            uriStr = uriStr.replace('/', File.separatorChar);
            uriStr = uriStr.replace("file:\\", "file:/");
            URL url = new URL(uriStr);

            File file = new File(url.toURI());
            FileReader reader = new FileReader(file);
            char[] buffer     = new char[(int) file.length()];
            reader.read(buffer);
            contents = new String(buffer);

            failed = !contents.equals(result);
        } catch (Exception e) {
            failed = !contents.equals(result);
        }

        // Store output in temp file, if the test failed.
        if (failed) {
            try{
                FileWriter fstream = new FileWriter("temp/"+ name + ".xsd");
                BufferedWriter out = new BufferedWriter(fstream);
                out.write(result);
                out.close();
            } catch (IOException e) {
                System.out.println("Could not write test comparision file: " + e);
            }
        }
        result = result.replace("\r", "");
        assertTrue(contents.equals(result));
    }
}
