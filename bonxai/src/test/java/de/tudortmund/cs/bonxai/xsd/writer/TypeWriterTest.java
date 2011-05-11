package de.tudortmund.cs.bonxai.xsd.writer;


import java.util.HashSet;
import java.util.LinkedList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import de.tudortmund.cs.bonxai.common.*;
import de.tudortmund.cs.bonxai.xsd.*;

public class TypeWriterTest extends junit.framework.TestCase
{
    private Document xmlDoc;
    private Element testElement;
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
            // XMLOutputter out;
            xmlDoc = db.newDocument();
        } catch(Exception e)
        {
        }

        testElement = xmlDoc.createElement("testElement");
        foundElements = new FoundElements();
    }

    @Test
    public void testWriteType()
    {

    }

    @Test
    public void testWriteFoundType()
    {
        SimpleType sType;
        SymbolTableRef<Type> symRef;
        de.tudortmund.cs.bonxai.xsd.Element element;
        Element tmpEl;//, tmpEl2;
        DefaultNamespace defaultNameSpace;
        IdentifiedNamespace identifiedNamespace;

        defaultNameSpace = new DefaultNamespace("http://defNS");
        NamespaceList nslist = new NamespaceList(defaultNameSpace);
        identifiedNamespace = new IdentifiedNamespace("ns", "myNamespace");
        nslist.addIdentifiedNamespace(identifiedNamespace);
        foundElements.setNamespaceList(nslist);

        sType = new SimpleType("{myNamespace}myType", null);
        symRef = new SymbolTableRef<Type>("myKey");
        symRef.setReference(sType);

        element = new de.tudortmund.cs.bonxai.xsd.Element("{}myElement");
        element.setType(symRef);
        ParticleWriter.writeElement(testElement, element, foundElements);

        element = new de.tudortmund.cs.bonxai.xsd.Element("{}myElement");
        element.setType(symRef);
        ParticleWriter.writeElement(testElement, element, foundElements);

        tmpEl = (Element)DOMHelper.findChildNode(testElement, "element");
        assert(tmpEl != null);

        /*
         * ElementWriter now creates a element with a type attribute if there
         * is no inheritance defined
        tmpEl2 = (Element)DOMHelper.findChildNode(tmpEl, "simpleType");
        assert(tmpEl2 != null);
        assertEquals("ns:myType", tmpEl2.getAttribute("name"));
        */

        tmpEl = (Element)tmpEl.getNextSibling();
        assert(tmpEl != null);
        assertEquals("element", tmpEl.getNodeName());
        assertEquals("ns:myType", tmpEl.getAttribute("type"));
    }

    @Test
    public void testWriteSimpleType()
    {
//        <simpleType id = ID
//        final = (#all | (list | union | restriction))
//        name = NCName>
//        Content: ( annotation ?, ( restriction | list | union )) </simpleType>

        SimpleType sType, baseType;
        SimpleContentList scList, scListBase;
        SymbolTableRef<Type> symRef;


        scListBase = new SimpleContentList(null);
        baseType = new SimpleType("{}baseType", scListBase);

        symRef = new SymbolTableRef<Type>("baseKey");
        symRef.setReference(baseType);
        scList = new SimpleContentList(symRef);

        sType = new SimpleType("{}myType", scList);

        TypeWriter.writeSimpleType(testElement, sType, foundElements, true);

        testElement = (Element)testElement.getFirstChild();
        assertEquals("simpleType", testElement.getNodeName());
        assertEquals("myType", testElement.getAttribute("name"));
        testElement = (Element)testElement.getFirstChild();
        assertEquals("list", testElement.getNodeName());
        assertEquals("baseType", testElement.getAttribute("itemType"));

    }

    @Test
    public void testWriteComplexType()
    {
        ComplexType cType;
        SimpleContentType sCont;
        ComplexContentType cCont;

        sCont = new SimpleContentType();

        DefaultNamespace defaultNameSpace;
        IdentifiedNamespace identifiedNamespace;

        defaultNameSpace = new DefaultNamespace("http://defNS");
        NamespaceList nslist = new NamespaceList(defaultNameSpace);
        identifiedNamespace = new IdentifiedNamespace("ns", "http://MyNamespace");
        nslist.addIdentifiedNamespace(identifiedNamespace);
        foundElements.setNamespaceList(nslist);

        cType = new ComplexType("{http://MyNamespace}myType", sCont);

        TypeWriter.writeComplexType(testElement, cType, foundElements, true);

        testElement = (Element)testElement.getFirstChild();
        assert(testElement != null);
        assertEquals("complexType", testElement.getNodeName());
        assertEquals("ns:myType", testElement.getAttribute("name"));


        testElement = (Element)testElement.getFirstChild();
        assert(testElement != null);
        assertEquals("simpleContent", testElement.getNodeName());

        cCont = new ComplexContentType();
        cType = new ComplexType("{http://MyNamespace}myType", cCont);
        TypeWriter.writeComplexType(testElement, cType, foundElements, true);
        testElement = (Element)testElement.getFirstChild();
        assert(testElement != null);
        assertEquals("complexType", testElement.getNodeName());
        assertEquals("ns:myType", testElement.getAttribute("name"));


        testElement = (Element)testElement.getFirstChild();
        assert(testElement != null);
        // Should be null, because no Inheritance has been defined
        // assertEquals("complexContent", testElement.getNodeName());
        assertNull(testElement);
    }

    @Test
    public void testWriteSimpleTypeInheritance()
    {
        SimpleType sTypeNode,myBaseType, myType1;
        SimpleContentUnion union;
        SymbolTableRef<Type> symRefBase, symRef1;

        myBaseType = new SimpleType("{}myBaseType", null);
        symRefBase = new SymbolTableRef<Type>("myBaseType");
        symRefBase.setReference(myBaseType);

        LinkedList<SymbolTableRef<Type>> baseTypes = new LinkedList<SymbolTableRef<Type>>();
        baseTypes.add(symRefBase);

        union = new SimpleContentUnion(baseTypes);
        symRef1 = new SymbolTableRef<Type>("type1");
        myType1 = new SimpleType("{}type1", null);
        symRef1.setReference(myType1);

        union.addMemberType(symRef1);
        sTypeNode = new SimpleType("{}myType", union);

        TypeWriter.writeSimpleTypeInheritance(testElement, sTypeNode, foundElements);

        testElement = (Element)testElement.getFirstChild();

        assertEquals("union", testElement.getNodeName());
    }

    @Test
    public void testWriteSimpContList()
    {
//        <list
//          id = ID
//          itemType = QName
//          {any attributes with non-schema Namespace}...>
//            Content: (annotation?, (simpleType?))
//        </list>

        Element sNode;
        SimpleContentList contentList;
        SimpleType myType;
        SymbolTableRef<Type> symRef;

        Element tmpNode;

        myType = new SimpleType("{}myBaseType", null);
        symRef = new SymbolTableRef<Type>("myBaseType");
        symRef.setReference(myType);
        contentList = new SimpleContentList( symRef);

        sNode = xmlDoc.createElement("testElement");

        TypeWriter.writeSimpContList(sNode, contentList,foundElements);

        assert(sNode.getElementsByTagName("testElement").getLength() == 1);

        tmpNode = (Element)sNode.getFirstChild();
        assertEquals(tmpNode.getAttribute("itemType"), "myBaseType");
    }

    @Test
    public void testWriteSimpContUnion()
    {
//        <union
//          id = ID
//          memberTypes = List of QNames
//          {any attributes with non-schema Namespace}...>
//        Content: (annotation?, (simpleType*))
//        </union>

        SimpleContentUnion contentUnion;
        SimpleType myBaseType, myType1, myType2;
        SymbolTableRef<Type> symRefBase, symRef1, symRef2;
        Element tmpNode;

        myBaseType = new SimpleType("{}myBaseType", null);
        symRefBase = new SymbolTableRef<Type>("myBaseType");
        symRefBase.setReference(myBaseType);

        LinkedList<SymbolTableRef<Type>> baseTypes = new LinkedList<SymbolTableRef<Type>>();
        baseTypes.add(symRefBase);

        contentUnion = new SimpleContentUnion(baseTypes);

        symRef1 = new SymbolTableRef<Type>("{}type1");
        symRef2 = new SymbolTableRef<Type>("{}type2");
        myType1 = new SimpleType("{}type1", null);
        myType2 = new SimpleType("{}type2", null);
        symRef1.setReference(myType1);
        symRef2.setReference(myType2);

        contentUnion.addMemberType(symRef1);
        contentUnion.addMemberType(symRef2);

        TypeWriter.writeSimpContUnion(testElement, contentUnion, foundElements);

        tmpNode = (Element)testElement.getFirstChild();
        assertEquals(tmpNode.getAttribute("memberTypes"), "myBaseType type1 type2");

        tmpNode = (Element)DOMHelper.findByAttribute(testElement.getFirstChild(), "name", "myBaseType");
        assert(tmpNode != null);
        tmpNode = (Element)DOMHelper.findByAttribute(testElement.getFirstChild(), "name", "type1");
        assert(tmpNode != null);
        tmpNode = (Element)DOMHelper.findByAttribute(testElement.getFirstChild(), "name", "type2");
        assert(tmpNode != null);
    }

    @Test
    public void testWriteSimpleContentRestriction()
    {
//        <restriction
//          base = QName
//          id = ID
//          {any attributes with non-schema Namespace}...>
//        Content: (annotation?, (simpleType?, (minExclusive | minInclusive |
//        maxExclusive | maxInclusive | totalDigits |fractionDigits | length |
//        minLength | maxLength | enumeration | whiteSpace | pattern)*)?,
//        ((attribute | attributeGroup)*, anyAttribute?))
//        </restriction>

        SimpleContentRestriction contentRestr;
        SimpleContentFixableRestrictionProperty<String> minEx, maxEx, minInc, maxInc;
        SimpleContentFixableRestrictionProperty<Integer> totDig, fracDig, len, minLen, maxLen;
        SimpleContentRestrictionProperty<String> pattern;
        SimpleContentFixableRestrictionProperty<SimpleContentPropertyWhitespace> whitespace;
        SimpleContentPropertyWhitespace whiteProp;
        LinkedList<String> enumeration;

        SimpleType myBaseType;
        SymbolTableRef<Type> symRefBase;
        Element tmpNode;
        NodeList tmpList;

        myBaseType = new SimpleType("{}myBaseType", null);
        symRefBase = new SymbolTableRef<Type>("myBaseType");
        symRefBase.setReference(myBaseType);
        contentRestr = new SimpleContentRestriction( symRefBase);

        minEx = new SimpleContentFixableRestrictionProperty<String>("minEx",true);
        contentRestr.setMinExclusive(minEx);

        maxEx = new SimpleContentFixableRestrictionProperty<String>("maxEx", false);
        contentRestr.setMaxExclusive(maxEx);

        minInc = new SimpleContentFixableRestrictionProperty<String>("minInc",true);
        contentRestr.setMinInclusive(minInc);

        maxInc = new SimpleContentFixableRestrictionProperty<String>("maxInc", false);
        contentRestr.setMaxInclusive(maxInc);

        pattern = new SimpleContentRestrictionProperty<String>("mypattern");
        contentRestr.setPattern(pattern);

        totDig = new SimpleContentFixableRestrictionProperty<Integer>(2, false);
        contentRestr.setTotalDigits(totDig);

        fracDig = new SimpleContentFixableRestrictionProperty<Integer>(3, true);
        contentRestr.setFractionDigits(fracDig);

        minLen = new SimpleContentFixableRestrictionProperty<Integer>(4, false);
        contentRestr.setMinLength(minLen);

        maxLen = new SimpleContentFixableRestrictionProperty<Integer>(5, true);
        contentRestr.setMaxLength(maxLen);

        len = new SimpleContentFixableRestrictionProperty<Integer>(6, false);
        contentRestr.setLength(len);

        whiteProp = SimpleContentPropertyWhitespace.Collapse;
        whitespace = new SimpleContentFixableRestrictionProperty<SimpleContentPropertyWhitespace>(whiteProp, false);
        contentRestr.setWhitespace(whitespace);

        enumeration = new LinkedList<String>();
        for(int i = 1; i < 4; i++)
        {
            enumeration.add("enum" + i);
        }
        contentRestr.addEnumeration(enumeration);

        TypeWriter.writeSimpleContentRestriction(testElement, contentRestr, foundElements);

        testElement = (Element)DOMHelper.findChildNode(testElement, "restriction");
        assert(testElement != null);
        assertEquals("myBaseType",testElement.getAttribute("base"));

        tmpNode = (Element)DOMHelper.findChildNode(testElement, "minExclusive");
        assert(tmpNode != null);
        assertEquals("true", tmpNode.getAttribute("fixed"));
        assertEquals("minEx", tmpNode.getAttribute("value"));

        tmpNode = (Element)DOMHelper.findChildNode(testElement, "maxExclusive");
        assert(tmpNode != null);
        assertEquals("false", tmpNode.getAttribute("fixed"));
        assertEquals("maxEx", tmpNode.getAttribute("value"));

        tmpNode = (Element)DOMHelper.findChildNode(testElement, "minInclusive");
        assert(tmpNode != null);
        assertEquals("true", tmpNode.getAttribute("fixed"));
        assertEquals("minInc", tmpNode.getAttribute("value"));

        tmpNode = (Element)DOMHelper.findChildNode(testElement, "maxInclusive");
        assert(tmpNode != null);
        assertEquals("false", tmpNode.getAttribute("fixed"));
        assertEquals("maxInc", tmpNode.getAttribute("value"));

        tmpNode = (Element)DOMHelper.findChildNode(testElement, "totalDigits");
        assert(tmpNode != null);
        assertEquals("false", tmpNode.getAttribute("fixed"));
        assertEquals("2", tmpNode.getAttribute("value"));

        tmpNode = (Element)DOMHelper.findChildNode(testElement, "fractionDigits");
        assert(tmpNode != null);
        assertEquals("true", tmpNode.getAttribute("fixed"));
        assertEquals("3", tmpNode.getAttribute("value"));

        tmpNode = (Element)DOMHelper.findChildNode(testElement, "minLength");
        assert(tmpNode != null);
        assertEquals("false", tmpNode.getAttribute("fixed"));
        assertEquals("4", tmpNode.getAttribute("value"));

        tmpNode = (Element)DOMHelper.findChildNode(testElement, "maxLength");
        assert(tmpNode != null);
        assertEquals("true", tmpNode.getAttribute("fixed"));
        assertEquals("5", tmpNode.getAttribute("value"));

        tmpNode = (Element)DOMHelper.findChildNode(testElement, "whiteSpace");
        assert(tmpNode != null);
        assertEquals("false", tmpNode.getAttribute("fixed"));
        assertEquals("collapse", tmpNode.getAttribute("value"));

        tmpNode = (Element)DOMHelper.findChildNode(testElement, "pattern");
        assert(tmpNode != null);
        assertEquals("mypattern", tmpNode.getAttribute("value"));

        tmpList = testElement.getElementsByTagName("enumeration");
        assert(tmpList.getLength() == 3);
        for(int i = 0; i < tmpList.getLength(); i++)
        {
            tmpNode = (Element)tmpList.item(i);
            assertEquals("enum" + (i + 1), tmpNode.getAttribute("value"));
        }

        tmpNode = (Element)DOMHelper.findChildNode(testElement, "length");
        assert(tmpNode != null);
        assertEquals("false", tmpNode.getAttribute("fixed"));
        assertEquals("6", tmpNode.getAttribute("value"));
    }

    @Test
    public void testWriteComplexContentMod()
    {
        HashSet<ComplexTypeInheritanceModifier> modifiers;
        String modType;

        modifiers = new HashSet<ComplexTypeInheritanceModifier>();
        modifiers.add(ComplexTypeInheritanceModifier.Extension);
        modifiers.add(ComplexTypeInheritanceModifier.Restriction);
        modType = "final";

        TypeWriter.writeComplexContentMod(testElement, modifiers, modType);
        assertEquals("#all", testElement.getAttribute("final"));


        setUp();
        modifiers = new HashSet<ComplexTypeInheritanceModifier>();
        modifiers.add(ComplexTypeInheritanceModifier.Extension);
        modType = "final";

        TypeWriter.writeComplexContentMod(testElement, modifiers, modType);
        assertEquals("extension", testElement.getAttribute("final"));

        setUp();
        modifiers = new HashSet<ComplexTypeInheritanceModifier>();
        modifiers.add(ComplexTypeInheritanceModifier.Restriction);
        modType = "final";

        TypeWriter.writeComplexContentMod(testElement, modifiers, modType);
        assertEquals("restriction", testElement.getAttribute("final"));

        setUp();
        modifiers = new HashSet<ComplexTypeInheritanceModifier>();
        modType = "final";

        TypeWriter.writeComplexContentMod(testElement, modifiers, modType);
        assertEquals("", testElement.getAttribute("final"));




        setUp();
        modifiers = new HashSet<ComplexTypeInheritanceModifier>();
        modifiers.add(ComplexTypeInheritanceModifier.Extension);
        modifiers.add(ComplexTypeInheritanceModifier.Restriction);
        modType = "block";

        TypeWriter.writeComplexContentMod(testElement, modifiers, modType);
        assertEquals("#all", testElement.getAttribute("block"));


        setUp();
        modifiers = new HashSet<ComplexTypeInheritanceModifier>();
        modifiers.add(ComplexTypeInheritanceModifier.Extension);
        modType = "block";

        TypeWriter.writeComplexContentMod(testElement, modifiers, modType);
        assertEquals("extension", testElement.getAttribute("block"));

        setUp();
        modifiers = new HashSet<ComplexTypeInheritanceModifier>();
        modifiers.add(ComplexTypeInheritanceModifier.Restriction);
        modType = "block";

        TypeWriter.writeComplexContentMod(testElement, modifiers, modType);
        assertEquals("restriction", testElement.getAttribute("block"));

        setUp();
        modifiers = new HashSet<ComplexTypeInheritanceModifier>();
        modType = "block";

        TypeWriter.writeComplexContentMod(testElement, modifiers, modType);
        assertEquals("", testElement.getAttribute("block"));
    }

    @Test
    public void testWriteContent()
    {
        ComplexType cType;
        SimpleContentType sCont;
        ComplexContentType cCont;


        sCont = new SimpleContentType();
        cType = new ComplexType("{}myType", sCont);

        TypeWriter.writeContent(testElement, cType, foundElements);

        testElement = (Element)testElement.getFirstChild();
        assert(testElement != null);
        assertEquals("simpleContent", testElement.getNodeName());

        setUp();
        cCont = new ComplexContentType();
        cType = new ComplexType("{}myType", cCont);

        TypeWriter.writeContent(testElement, cType, foundElements);
        testElement = (Element)testElement.getFirstChild();
        // Should be null because no inheritance has been defined
        // assert(testElement != null);
        // assertEquals("complexContent", testElement.getNodeName());
        assertNull(testElement);

    }

    @Test
    public void testWriteComplexContent()
    {
//        <complexContent id = ID
//        mixed = boolean>
//        Content: (annotation?, (restriction | extension)) </complexContent>

        ComplexContentType cCont;
        ComplexContentRestriction restriction;
        SimpleType myBaseType;
        SymbolTableRef<Type> symRef;

        myBaseType = new SimpleType("{}myBaseType", null);
        symRef = new SymbolTableRef<Type>("myBaseType");
        symRef.setReference(myBaseType);
        restriction = new ComplexContentRestriction(symRef);
        cCont = new ComplexContentType();
        cCont.setInheritance(restriction);

        cCont.setMixed(false);

        TypeWriter.writeComplexContent(testElement, cCont, foundElements);
        testElement = (Element)testElement.getFirstChild();
        assert(testElement != null);
        assertEquals("complexContent", testElement.getNodeName());
        //standard value no longer written
        //assertEquals("false", testElement.getAttribute("mixed"));
        testElement = (Element)testElement.getFirstChild();
        assert(testElement != null);
        assertEquals("restriction", testElement.getNodeName());
    }

    @Test
    public void testWriteComplexContentRestriction()
    {
//        <restriction id = ID
//        base = QName>
//        Content: (annotation?, (group | all | choice | sequence)?,
//        ((attribute | attributeGroup)*, anyAttribute?)) </restriction>
        ComplexContentType cContent;
        ComplexContentRestriction ccRest;
        SymbolTableRef<Type> symRef;
        Type myBaseType;
        Element tmpNode;
        SequencePattern sequence;
        de.tudortmund.cs.bonxai.xsd.Element seqEl1, seqEl2;
        SimpleType type1, type2;
        SymbolTableRef<Type> symRef1, symRef2;

        myBaseType = new SimpleType("{}myBaseType", null);
        symRef = new SymbolTableRef<Type>("myBaseType");
        symRef.setReference(myBaseType);
        ccRest = new ComplexContentRestriction(symRef);
        cContent = new ComplexContentType();
        cContent.setInheritance(ccRest);

        seqEl1 = new de.tudortmund.cs.bonxai.xsd.Element("{}el1");
        type1 = new SimpleType("{}seqElType1", null);
        symRef1 = new SymbolTableRef<Type>("ref1");
        symRef1.setReference(type1);
        seqEl1.setType(symRef1);
        seqEl2 = new de.tudortmund.cs.bonxai.xsd.Element("{}el2");
        type2 = new SimpleType("{}seqElType2", null);
        symRef2 = new SymbolTableRef<Type>("ref2");
        symRef2.setReference(type2);
        seqEl2.setType(symRef2);

        sequence = new SequencePattern();
        sequence.addParticle(seqEl1);
        sequence.addParticle(seqEl2);
        cContent.setParticle(sequence);

        TypeWriter.writeComplexContentRestriction(testElement, cContent, foundElements);
        tmpNode = (Element)DOMHelper.findChildNode(testElement, "restriction");
        assert(tmpNode != null);
        assertEquals("myBaseType", tmpNode.getAttribute("base"));
        tmpNode = (Element)DOMHelper.findChildNode(tmpNode, "sequence");
        assert(tmpNode != null);
    }

    @Test
    public void testWriteComplexContentExtension()
    {

        ComplexContentType cContent;
        ComplexContentExtension ccExt;
        SymbolTableRef<Type> symRef;
        Type myBaseType;
        Element tmpNode;
        SequencePattern sequence;
        de.tudortmund.cs.bonxai.xsd.Element seqEl1, seqEl2;
        SimpleType type1, type2;
        SymbolTableRef<Type> symRef1, symRef2;

        myBaseType = new SimpleType("{}myBaseType", null);
        symRef = new SymbolTableRef<Type>("myBaseType");
        symRef.setReference(myBaseType);
        ccExt = new ComplexContentExtension(symRef);
        cContent = new ComplexContentType();
        cContent.setInheritance(ccExt);

        seqEl1 = new de.tudortmund.cs.bonxai.xsd.Element("{}el1");
        type1 = new SimpleType("{}seqElType1", null);
        symRef1 = new SymbolTableRef<Type>("ref1");
        symRef1.setReference(type1);
        seqEl1.setType(symRef1);
        seqEl2 = new de.tudortmund.cs.bonxai.xsd.Element("{}el2");
        type2 = new SimpleType("{}seqElType2", null);
        symRef2 = new SymbolTableRef<Type>("ref2");
        symRef2.setReference(type2);
        seqEl2.setType(symRef2);

        sequence = new SequencePattern();
        sequence.addParticle(seqEl1);
        sequence.addParticle(seqEl2);
        cContent.setParticle(sequence);

        TypeWriter.writeComplexContentExtension(testElement, cContent, foundElements);
        tmpNode = (Element)DOMHelper.findChildNode(testElement, "extension");
        assert(tmpNode != null);
        assertEquals("myBaseType", tmpNode.getAttribute("base"));
        tmpNode = (Element)DOMHelper.findChildNode(tmpNode, "sequence");
        assert(tmpNode != null);
    }

    @Test
    public void testWriteSimpleContent()
    {
        // <simpleContent
        // id = ID
        // {any attributes with non-schema Namespace}...>
        // Content: (annotation?, (restriction | extension))
        // </simpleContent>
        SimpleType sType;
        ComplexType cType;
        SimpleContentType sCont;
        SimpleContentInheritance inheritance;
        SymbolTableRef<Type> symRefExt;

        sType = new SimpleType("{}myBaseType", null);
        symRefExt = new SymbolTableRef<Type>("extension");
        symRefExt.setReference(sType);

        inheritance = new SimpleContentExtension(symRefExt);

        sCont = new SimpleContentType();
        sCont.setInheritance(inheritance);
        cType = new ComplexType("{}myType", sCont);

        TypeWriter.writeSimpleContent(testElement, cType, foundElements);

        testElement = (Element)testElement.getFirstChild();
        assertEquals("simpleContent", testElement.getNodeName());
        testElement = (Element)testElement.getFirstChild();
        assertEquals("extension", testElement.getNodeName());
        assertEquals("myBaseType", testElement.getAttribute("base"));
    }

}
