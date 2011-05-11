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

public class TreeNodeTreeAttributeGroupConverterTest extends junit.framework.TestCase {

    @Test
    public void testConvertGroupIntoOneSchema() throws Exception {
        // Create a schema tree
        SchemaTreeNode tree = new SchemaTreeNode();

        // A single group reference
        // groups {
        //  attribute-group alpha = {
        //      attribute attr { string }
        //  }
        // }
        //
        // grammar {
        //  /root = {
        //      attribute-group alpha, empty
        //  }
        // }

        // Create group
        AttributePattern groupParticleContainer = new AttributePattern();
        groupParticleContainer.addAttribute(new de.tudortmund.cs.bonxai.bonxai.Attribute("http://example.org", "attr", new BonxaiType("", "string")));

        SymbolTable<AttributeGroupElement> groups = new SymbolTable<AttributeGroupElement>();
        groups.createReference("alpha", new AttributeGroupElement("alpha", groupParticleContainer));

        // Create root element
        ElementTreeNode root = new ElementTreeNode("http://example.org", "root", new Expression());
        root.getExpression().setAncestorPattern(new AncestorPattern(new SingleSlashPrefixElement("http://example.org", "root")));

        AttributePattern rootPattern       = new AttributePattern();
        rootPattern.addAttribute(new AttributeGroupReference(groups.getReference("alpha")));

        root.setChildPattern(new ChildPattern(rootPattern, null));
        tree.addChild(root);

        // List of root elements
        LinkedHashSet<TreeNode> rootNodes = new LinkedHashSet<TreeNode>();
        rootNodes.add(root);

        // Convert tree to XSD
        HashMap<String, XSDSchema> schemas = new HashMap<String, XSDSchema>();
        TreeNodeTreeConverter converter = new TreeNodeTreeConverter(schemas);
        converter.convert(rootNodes, tree);

        // Assertions
        assertTrue(schemas.containsKey("http://example.org"));
        XSDSchema xsd = schemas.get("http://example.org");

        assertTrue(((SymbolTable) xsd.getElementSymbolTable()).hasReference("{http://example.org}root"));
        de.tudortmund.cs.bonxai.xsd.Element xsdElement = ((SymbolTable<de.tudortmund.cs.bonxai.xsd.Element>) xsd.getElementSymbolTable()).getReference("{http://example.org}root").getReference();

        ComplexType type = (ComplexType) xsdElement.getType();
        assertEquals(1, type.getAttributes().size());
        assertTrue(type.getAttributes().get(0) instanceof AttributeGroupRef);

        // Assert group has been created
        assertNotNull(xsd.getAttributeGroupSymbolTable().getReference("{http://example.org}alpha"));
        assertNotNull(xsd.getAttributeGroupSymbolTable().getReference("{http://example.org}alpha").getReference());
        assertNotNull(xsd.getAttributeGroupSymbolTable().getReference("{http://example.org}alpha").getReference() instanceof AttributeGroup);
        assertNotNull(xsd.getAttributeGroupSymbolTable().getReference("{http://example.org}alpha").getReference().getAttributeParticles());
        assertEquals(1, xsd.getAttributeGroupSymbolTable().getReference("{http://example.org}alpha").getReference().getAttributeParticles().size());
    }

    @Test
    public void testConvertGroupIntoMultipleSchemas() throws Exception {
        // Create a schema tree
        SchemaTreeNode tree = new SchemaTreeNode();

        // A single group reference
        // groups {
        //  attribute-group alpha = {
        //      attribute attr { string }
        //  }
        // }
        //
        // grammar {
        //  /root = {
        //      attribute-group alpha, empty
        //  }
        //
        //  /foo:root = {
        //      attribute-group alpha, empty
        //  }
        // }

        // Create group
        AttributePattern groupParticleContainer = new AttributePattern();
        groupParticleContainer.addAttribute(new de.tudortmund.cs.bonxai.bonxai.Attribute("http://example.org", "attr", new BonxaiType("", "string")));

        SymbolTable<AttributeGroupElement> groups = new SymbolTable<AttributeGroupElement>();
        groups.createReference("alpha", new AttributeGroupElement("alpha", groupParticleContainer));

        // Create root element
        ElementTreeNode root = new ElementTreeNode("http://example.org", "root", new Expression());
        root.getExpression().setAncestorPattern(new AncestorPattern(new SingleSlashPrefixElement("http://example.org", "root")));

        AttributePattern rootPattern       = new AttributePattern();
        rootPattern.addAttribute(new AttributeGroupReference(groups.getReference("alpha")));

        root.setChildPattern(new ChildPattern(rootPattern, null));
        tree.addChild(root);

        // Create foo:root element
        ElementTreeNode fooRoot = new ElementTreeNode("http://example.org/foo", "root", new Expression());
        fooRoot.getExpression().setAncestorPattern(new AncestorPattern(new SingleSlashPrefixElement("http://example.org/foo", "root")));

        AttributePattern fooRootPattern       = new AttributePattern();
        fooRootPattern.addAttribute(new AttributeGroupReference(groups.getReference("alpha")));

        fooRoot.setChildPattern(new ChildPattern(fooRootPattern, null));
        tree.addChild(fooRoot);

        // List of root elements
        LinkedHashSet<TreeNode> rootNodes = new LinkedHashSet<TreeNode>();
        rootNodes.add(root);
        rootNodes.add(fooRoot);

        // Convert tree to XSD
        HashMap<String, XSDSchema> schemas = new HashMap<String, XSDSchema>();
        TreeNodeTreeConverter converter = new TreeNodeTreeConverter(schemas);
        converter.convert(rootNodes, tree);

        // Assertions
        assertTrue(schemas.containsKey("http://example.org"));
        XSDSchema xsd = schemas.get("http://example.org");

        assertTrue(((SymbolTable) xsd.getElementSymbolTable()).hasReference("{http://example.org}root"));
        de.tudortmund.cs.bonxai.xsd.Element xsdElement = ((SymbolTable<de.tudortmund.cs.bonxai.xsd.Element>) xsd.getElementSymbolTable()).getReference("{http://example.org}root").getReference();

        ComplexType type = (ComplexType) xsdElement.getType();
        assertEquals(1, type.getAttributes().size());
        assertTrue(type.getAttributes().get(0) instanceof AttributeGroupRef);

        // Assert group has been created
        assertNotNull(xsd.getAttributeGroupSymbolTable().getReference("{http://example.org}alpha"));
        assertNotNull(xsd.getAttributeGroupSymbolTable().getReference("{http://example.org}alpha").getReference());
        assertNotNull(xsd.getAttributeGroupSymbolTable().getReference("{http://example.org}alpha").getReference() instanceof AttributeGroup);
        assertNotNull(xsd.getAttributeGroupSymbolTable().getReference("{http://example.org}alpha").getReference().getAttributeParticles());
        assertEquals(1, xsd.getAttributeGroupSymbolTable().getReference("{http://example.org}alpha").getReference().getAttributeParticles().size());

        // Assertions
        assertTrue(schemas.containsKey("http://example.org/foo"));
        xsd = schemas.get("http://example.org/foo");

        assertTrue(((SymbolTable) xsd.getElementSymbolTable()).hasReference("{http://example.org/foo}root"));
        xsdElement = ((SymbolTable<de.tudortmund.cs.bonxai.xsd.Element>) xsd.getElementSymbolTable()).getReference("{http://example.org/foo}root").getReference();

        type = (ComplexType) xsdElement.getType();
        assertEquals(1, type.getAttributes().size());
        assertTrue(type.getAttributes().get(0) instanceof AttributeGroupRef);

        // Assert group has been created
        assertNotNull(xsd.getAttributeGroupSymbolTable().getReference("{http://example.org/foo}alpha"));
        assertNotNull(xsd.getAttributeGroupSymbolTable().getReference("{http://example.org/foo}alpha").getReference());
        assertNotNull(xsd.getAttributeGroupSymbolTable().getReference("{http://example.org/foo}alpha").getReference() instanceof AttributeGroup);
        assertNotNull(xsd.getAttributeGroupSymbolTable().getReference("{http://example.org/foo}alpha").getReference().getAttributeParticles());
        assertEquals(1, xsd.getAttributeGroupSymbolTable().getReference("{http://example.org/foo}alpha").getReference().getAttributeParticles().size());
    }

    @Test
    public void testConvertRecursiveGroupIntoOneSchema() throws Exception {
        // Create a schema tree
        SchemaTreeNode tree = new SchemaTreeNode();

        // A single group reference
        // groups {
        //  attribute-group alpha = {
        //      attribute-group beta
        //  }
        //
        //  attribute-group beta = {
        //      attribute attr { string }
        //  }
        // }
        //
        // grammar {
        //  /root = {
        //      attribute-group alpha, empty
        //  }
        // }

        SymbolTable<AttributeGroupElement> groups = new SymbolTable<AttributeGroupElement>();

        // Create beta group
        AttributePattern betaGroupParticleContainer = new AttributePattern();
        betaGroupParticleContainer.addAttribute(new de.tudortmund.cs.bonxai.bonxai.Attribute("http://example.org", "attr", new BonxaiType("", "string")));

        groups.createReference("beta", new AttributeGroupElement("beta", betaGroupParticleContainer));

        // Create alpha group
        AttributePattern groupParticleContainer = new AttributePattern();
        groupParticleContainer.addAttribute(new AttributeGroupReference(groups.getReference("beta")));

        groups.createReference("alpha", new AttributeGroupElement("alpha", groupParticleContainer));

        // Create root element
        ElementTreeNode root = new ElementTreeNode("http://example.org", "root", new Expression());
        root.getExpression().setAncestorPattern(new AncestorPattern(new SingleSlashPrefixElement("http://example.org", "root")));

        AttributePattern rootPattern       = new AttributePattern();
        rootPattern.addAttribute(new AttributeGroupReference(groups.getReference("alpha")));

        root.setChildPattern(new ChildPattern(rootPattern, null));
        tree.addChild(root);

        // List of root elements
        LinkedHashSet<TreeNode> rootNodes = new LinkedHashSet<TreeNode>();
        rootNodes.add(root);

        // Convert tree to XSD
        HashMap<String, XSDSchema> schemas = new HashMap<String, XSDSchema>();
        TreeNodeTreeConverter converter = new TreeNodeTreeConverter(schemas);
        converter.convert(rootNodes, tree);

        // Assertions
        assertTrue(schemas.containsKey("http://example.org"));
        XSDSchema xsd = schemas.get("http://example.org");

        assertTrue(((SymbolTable) xsd.getElementSymbolTable()).hasReference("{http://example.org}root"));
        de.tudortmund.cs.bonxai.xsd.Element xsdElement = ((SymbolTable<de.tudortmund.cs.bonxai.xsd.Element>) xsd.getElementSymbolTable()).getReference("{http://example.org}root").getReference();

        ComplexType type = (ComplexType) xsdElement.getType();
        assertEquals(1, type.getAttributes().size());
        assertTrue(type.getAttributes().get(0) instanceof AttributeGroupRef);

        // Assert group has been created
        assertNotNull(xsd.getAttributeGroupSymbolTable().getReference("{http://example.org}alpha"));
        assertNotNull(xsd.getAttributeGroupSymbolTable().getReference("{http://example.org}alpha").getReference());
        assertNotNull(xsd.getAttributeGroupSymbolTable().getReference("{http://example.org}alpha").getReference() instanceof AttributeGroup);
        assertNotNull(xsd.getAttributeGroupSymbolTable().getReference("{http://example.org}alpha").getReference().getAttributeParticles());
        assertEquals(1, xsd.getAttributeGroupSymbolTable().getReference("{http://example.org}alpha").getReference().getAttributeParticles().size());

        // Assert second group has been created
        assertNotNull(xsd.getAttributeGroupSymbolTable().getReference("{http://example.org}beta"));
        assertNotNull(xsd.getAttributeGroupSymbolTable().getReference("{http://example.org}beta").getReference());
        assertNotNull(xsd.getAttributeGroupSymbolTable().getReference("{http://example.org}beta").getReference() instanceof AttributeGroup);
        assertNotNull(xsd.getAttributeGroupSymbolTable().getReference("{http://example.org}beta").getReference().getAttributeParticles());
        assertEquals(1, xsd.getAttributeGroupSymbolTable().getReference("{http://example.org}beta").getReference().getAttributeParticles().size());
    }
}
