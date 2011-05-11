package de.tudortmund.cs.bonxai.converter.bonxai2xsd;

import org.junit.Test;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Vector;
import java.io.*;

import de.tudortmund.cs.bonxai.common.*;
import de.tudortmund.cs.bonxai.bonxai.*;
import de.tudortmund.cs.bonxai.xsd.*;
import de.tudortmund.cs.bonxai.xsd.writer.*;

public class TreeNodeTreeAttributeConverterTest extends junit.framework.TestCase {

    @Test
    public void testConvertSimpleAttribute() throws Exception {
        // Create a schema tree
        SchemaTreeNode tree = new SchemaTreeNode();

        // Create a single element
        // grammar {
        //  /element = {
        //      attribute attr { string },
        //      empty
        //  }
        // }
        ElementTreeNode element = new ElementTreeNode("http://example.org", "element", new Expression());
        element.getExpression().setAncestorPattern(new AncestorPattern(new SingleSlashPrefixElement("http://example.org", "element")));

        AttributePattern attributePattern       = new AttributePattern();
        Vector<AttributeListElement> attributes = new Vector<AttributeListElement>();
        attributes.add(new de.tudortmund.cs.bonxai.bonxai.Attribute("http://example.org", "attr", new BonxaiType("", "string")));
        attributePattern.setAttributeList(attributes);

        element.setChildPattern(new ChildPattern(attributePattern, null));
        tree.addChild(element);

        // List of root elements
        LinkedHashSet<TreeNode> rootNodes = new LinkedHashSet<TreeNode>();
        rootNodes.add(element);

        // Convert tree to XSD
        HashMap<String, XSDSchema> schemas = new HashMap<String, XSDSchema>();
        TreeNodeTreeConverter converter = new TreeNodeTreeConverter(schemas);
        converter.convert(rootNodes, tree);

        // Assertions
        assertTrue(schemas.containsKey("http://example.org"));
        XSDSchema xsd = schemas.get("http://example.org");

        assertTrue(((SymbolTable) xsd.getElementSymbolTable()).hasReference("{http://example.org}element"));

        de.tudortmund.cs.bonxai.xsd.Element xsdElement = ((SymbolTable<de.tudortmund.cs.bonxai.xsd.Element>) xsd.getElementSymbolTable()).getReference("{http://example.org}element").getReference();

        ComplexType type = (ComplexType) xsdElement.getType();
        type.getAttributes();
        assertEquals(1, type.getAttributes().size());
        assertTrue(type.getAttributes().get(0) instanceof de.tudortmund.cs.bonxai.xsd.Attribute);
    }

    @Test
    public void testConvertSimpleAttributeWithComplexType() throws Exception {
        // Create a schema tree
        SchemaTreeNode tree = new SchemaTreeNode();

        // Create a single element
        // grammar {
        //  /element = {
        //      attribute attr { string },
        //      element child
        //  }
        // }
        ElementTreeNode element = new ElementTreeNode("http://example.org", "element", new Expression());
        element.getExpression().setAncestorPattern(new AncestorPattern(new SingleSlashPrefixElement("http://example.org", "element")));

        AttributePattern attributePattern       = new AttributePattern();
        Vector<AttributeListElement> attributes = new Vector<AttributeListElement>();
        attributes.add(new de.tudortmund.cs.bonxai.bonxai.Attribute("http://example.org", "attr", new BonxaiType("", "string")));
        attributePattern.setAttributeList(attributes);

        SequencePattern particleContainer = new SequencePattern();
        particleContainer.addParticle(new de.tudortmund.cs.bonxai.bonxai.Element("http://example.org", "child"));
        ElementPattern elementPattern = new ElementPattern(particleContainer);

        element.setChildPattern(new ChildPattern(attributePattern, elementPattern));
        tree.addChild(element);

        // List of root elements
        LinkedHashSet<TreeNode> rootNodes = new LinkedHashSet<TreeNode>();
        rootNodes.add(element);

        // Convert tree to XSD
        HashMap<String, XSDSchema> schemas = new HashMap<String, XSDSchema>();
        TreeNodeTreeConverter converter = new TreeNodeTreeConverter(schemas);
        converter.convert(rootNodes, tree);

        // Assertions
        assertTrue(schemas.containsKey("http://example.org"));
        XSDSchema xsd = schemas.get("http://example.org");

        assertTrue(((SymbolTable) xsd.getElementSymbolTable()).hasReference("{http://example.org}element"));

        de.tudortmund.cs.bonxai.xsd.Element xsdElement = ((SymbolTable<de.tudortmund.cs.bonxai.xsd.Element>) xsd.getElementSymbolTable()).getReference("{http://example.org}element").getReference();

        ComplexType type = (ComplexType) xsdElement.getType();
        assertEquals(1, type.getAttributes().size());
        assertTrue(type.getAttributes().get(0) instanceof de.tudortmund.cs.bonxai.xsd.Attribute);
    }

    @Test
    public void testConvertSimpleAttributeWithSimpleType() throws Exception {
        // Create a schema tree
        SchemaTreeNode tree = new SchemaTreeNode();

        // Create a single element
        // grammar {
        //  /element = {
        //      attribute attr { string },
        //      string
        //  }
        // }
        ElementTreeNode element = new ElementTreeNode("http://example.org", "element", new Expression());
        element.getExpression().setAncestorPattern(new AncestorPattern(new SingleSlashPrefixElement("http://example.org", "element")));

        AttributePattern attributePattern       = new AttributePattern();
        Vector<AttributeListElement> attributes = new Vector<AttributeListElement>();
        attributes.add(new de.tudortmund.cs.bonxai.bonxai.Attribute("http://example.org", "attr", new BonxaiType("", "string")));
        attributePattern.setAttributeList(attributes);

        SequencePattern particleContainer = new SequencePattern();
        particleContainer.addParticle(new de.tudortmund.cs.bonxai.bonxai.Element("http://example.org", "child"));
        ElementPattern elementPattern = new ElementPattern(particleContainer);

        element.setChildPattern(new ChildPattern(attributePattern, new ElementPattern(new BonxaiType("", "string"))));
        tree.addChild(element);

        // List of root elements
        LinkedHashSet<TreeNode> rootNodes = new LinkedHashSet<TreeNode>();
        rootNodes.add(element);

        // Convert tree to XSD
        HashMap<String, XSDSchema> schemas = new HashMap<String, XSDSchema>();
        TreeNodeTreeConverter converter = new TreeNodeTreeConverter(schemas);
        converter.convert(rootNodes, tree);

        // Assertions
        assertTrue(schemas.containsKey("http://example.org"));
        XSDSchema xsd = schemas.get("http://example.org");

        assertTrue(((SymbolTable) xsd.getElementSymbolTable()).hasReference("{http://example.org}element"));

        de.tudortmund.cs.bonxai.xsd.Element xsdElement = ((SymbolTable<de.tudortmund.cs.bonxai.xsd.Element>) xsd.getElementSymbolTable()).getReference("{http://example.org}element").getReference();

        ComplexType type = (ComplexType) xsdElement.getType();
        assertEquals(1, type.getAttributes().size());
        assertTrue(type.getAttributes().get(0) instanceof de.tudortmund.cs.bonxai.xsd.Attribute);

        assertNotNull(type.getContent());
        assertTrue(type.getContent() instanceof SimpleContentType);
        SimpleContentType content = (SimpleContentType) type.getContent();

        assertNotNull(content.getInheritance());
        assertTrue(content.getInheritance() instanceof SimpleContentExtension);
        SimpleContentExtension extension = (SimpleContentExtension) content.getInheritance();

        assertNotNull(extension.getBase());
        assertEquals("{}string", extension.getBase().getName());
    }
}
