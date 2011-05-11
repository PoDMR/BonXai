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

public class TreeNodeTreeGroupConverterTest extends junit.framework.TestCase {

    @Test
    public void testConvertGroupIntoOneSchema() throws Exception {
        // Create a schema tree
        SchemaTreeNode tree = new SchemaTreeNode();

        // A single group reference
        // groups {
        //  group alpha = {
        //      element child
        //  }
        // }
        //
        // grammar {
        //  /root = {
        //      group alpha
        //  }
        // }

        // Create group
        SequencePattern groupParticleContainer = new SequencePattern();
        groupParticleContainer.addParticle(new de.tudortmund.cs.bonxai.bonxai.Element("http://example.org", "child"));

        SymbolTable<ElementGroupElement> groups = new SymbolTable<ElementGroupElement>();
        groups.createReference("alpha", new ElementGroupElement("alpha", groupParticleContainer));

        // Create root element
        ElementTreeNode root = new ElementTreeNode("http://example.org", "root", new Expression());
        root.getExpression().setAncestorPattern(new AncestorPattern(new SingleSlashPrefixElement("http://example.org", "root")));

        SequencePattern particleContainer = new SequencePattern();
        particleContainer.addParticle(new GroupRef(groups.getReference("alpha")));
        ElementPattern rootPattern = new ElementPattern(particleContainer);

        root.setChildPattern(new ChildPattern(null, rootPattern));
        tree.addChild(root);

        // Create GroupTreeNode
        GroupTreeNode alpha = new GroupTreeNode("alpha", new Expression(), new GroupRef(groups.getReference("alpha")));
        LinkedHashSet<String> namespaces = new LinkedHashSet<String>();
        namespaces.add("http://example.org");
        alpha.addNamespaces(namespaces);

        alpha.getExpression().setChildPattern(new ChildPattern(null, new ElementPattern(groupParticleContainer)));
        root.addChild(alpha);

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
        assertNotNull(type.getContent());
        ComplexContentType content = (ComplexContentType) type.getContent();
        assertTrue(content.getParticle() instanceof SequencePattern);
        assertEquals(1, ((SequencePattern) content.getParticle()).getParticles().size());
        assertTrue(((SequencePattern) content.getParticle()).getParticles().get(0) instanceof GroupRef);

        // Assert group has been created
        assertNotNull(xsd.getGroupSymbolTable().getReference("{http://example.org}alpha"));
        assertNotNull(xsd.getGroupSymbolTable().getReference("{http://example.org}alpha").getReference());
        assertNotNull(xsd.getGroupSymbolTable().getReference("{http://example.org}alpha").getReference().getParticleContainer());
        assertTrue(xsd.getGroupSymbolTable().getReference("{http://example.org}alpha").getReference().getParticleContainer() instanceof SequencePattern);
    }

    @Test
    public void testConvertGroupIntoMultipleSchemas() throws Exception {
        // Create a schema tree
        SchemaTreeNode tree = new SchemaTreeNode();

        // A single group reference
        // defaul namespace http://example.org
        // namespace foo http://example.org/foo
        //
        // groups {
        //  group alpha = {
        //      element child
        //  }
        // }
        //
        // grammar {
        //  /root = {
        //      group alpha
        //  }
        //
        //  /foo:root = {
        //      group alpha
        //  }
        // }

        // Create group
        SequencePattern groupParticleContainer = new SequencePattern();
        groupParticleContainer.addParticle(new de.tudortmund.cs.bonxai.bonxai.Element("http://example.org", "child"));

        SymbolTable<ElementGroupElement> groups = new SymbolTable<ElementGroupElement>();
        groups.createReference("alpha", new ElementGroupElement("alpha", groupParticleContainer));

        // Create root element
        ElementTreeNode root = new ElementTreeNode("http://example.org", "root", new Expression());
        root.getExpression().setAncestorPattern(new AncestorPattern(new SingleSlashPrefixElement("http://example.org", "root")));

        SequencePattern particleContainer = new SequencePattern();
        particleContainer.addParticle(new GroupRef(groups.getReference("alpha")));
        ElementPattern rootPattern = new ElementPattern(particleContainer);

        root.setChildPattern(new ChildPattern(null, rootPattern));
        tree.addChild(root);

        // Create foo:root element
        ElementTreeNode fooRoot = new ElementTreeNode("http://example.org/foo", "root", new Expression());
        fooRoot.getExpression().setAncestorPattern(new AncestorPattern(new SingleSlashPrefixElement("http://example.org/foo", "root")));

        SequencePattern fooParticleContainer = new SequencePattern();
        fooParticleContainer.addParticle(new GroupRef(groups.getReference("alpha")));
        ElementPattern fooRootPattern = new ElementPattern(fooParticleContainer);

        fooRoot.setChildPattern(new ChildPattern(null, fooRootPattern));
        tree.addChild(fooRoot);

        // Create GroupTreeNode
        GroupTreeNode alpha = new GroupTreeNode("alpha", new Expression(), new GroupRef(groups.getReference("alpha")));
        LinkedHashSet<String> namespaces = new LinkedHashSet<String>();
        namespaces.add("http://example.org");
        namespaces.add("http://example.org/foo");
        alpha.addNamespaces(namespaces);

        alpha.getExpression().setChildPattern(new ChildPattern(null, new ElementPattern(groupParticleContainer)));
        root.addChild(alpha);

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
        assertNotNull(type.getContent());
        ComplexContentType content = (ComplexContentType) type.getContent();
        assertTrue(content.getParticle() instanceof SequencePattern);
        assertEquals(1, ((SequencePattern) content.getParticle()).getParticles().size());
        assertTrue(((SequencePattern) content.getParticle()).getParticles().get(0) instanceof GroupRef);

        // Assert group has been created
        assertNotNull(xsd.getGroupSymbolTable().getReference("{http://example.org}alpha"));
        assertNotNull(xsd.getGroupSymbolTable().getReference("{http://example.org}alpha").getReference());
        assertNotNull(xsd.getGroupSymbolTable().getReference("{http://example.org}alpha").getReference().getParticleContainer());
        assertTrue(xsd.getGroupSymbolTable().getReference("{http://example.org}alpha").getReference().getParticleContainer() instanceof SequencePattern);

        // Assertions on second schema
        assertTrue(schemas.containsKey("http://example.org/foo"));
        XSDSchema fooXsd = schemas.get("http://example.org/foo");

        assertTrue(((SymbolTable) fooXsd.getElementSymbolTable()).hasReference("{http://example.org/foo}root"));

        de.tudortmund.cs.bonxai.xsd.Element fooXsdElement = ((SymbolTable<de.tudortmund.cs.bonxai.xsd.Element>) fooXsd.getElementSymbolTable()).getReference("{http://example.org/foo}root").getReference();

        ComplexType fooType = (ComplexType) fooXsdElement.getType();
        assertNotNull(fooType.getContent());
        ComplexContentType fooContent = (ComplexContentType) fooType.getContent();
        assertTrue(fooContent.getParticle() instanceof SequencePattern);
        assertEquals(1, ((SequencePattern) fooContent.getParticle()).getParticles().size());
        assertTrue(((SequencePattern) fooContent.getParticle()).getParticles().get(0) instanceof GroupRef);

        // Assert group has been created
        assertNotNull(fooXsd.getGroupSymbolTable().getReference("{http://example.org/foo}alpha"));
        assertNotNull(fooXsd.getGroupSymbolTable().getReference("{http://example.org/foo}alpha").getReference());
        assertNotNull(fooXsd.getGroupSymbolTable().getReference("{http://example.org/foo}alpha").getReference().getParticleContainer());
        assertTrue(fooXsd.getGroupSymbolTable().getReference("{http://example.org/foo}alpha").getReference().getParticleContainer() instanceof SequencePattern);
    }

    @Test
    public void testConvertMultipleGroupsIntoMultipleSchemas() throws Exception {
        // Create a schema tree
        SchemaTreeNode tree = new SchemaTreeNode();

        // A single group reference
        // defaul namespace http://example.org
        // namespace foo http://example.org/foo
        //
        // groups {
        //  group alpha = {
        //      element child
        //  }
        //
        //  group beta = {
        //      element child
        //  }
        // }
        //
        // grammar {
        //  /root = {
        //      group alpha
        //  }
        //
        //  /foo:root = {
        //      group beta
        //  }
        // }

        // Create group
        SequencePattern groupParticleContainer = new SequencePattern();
        groupParticleContainer.addParticle(new de.tudortmund.cs.bonxai.bonxai.Element("http://example.org", "child"));

        SymbolTable<ElementGroupElement> groups = new SymbolTable<ElementGroupElement>();
        groups.createReference("alpha", new ElementGroupElement("alpha", groupParticleContainer));
        groups.createReference("beta",  new ElementGroupElement("beta",  groupParticleContainer));

        // Create root element
        ElementTreeNode root = new ElementTreeNode("http://example.org", "root", new Expression());
        root.getExpression().setAncestorPattern(new AncestorPattern(new SingleSlashPrefixElement("http://example.org", "root")));

        SequencePattern particleContainer = new SequencePattern();
        particleContainer.addParticle(new GroupRef(groups.getReference("alpha")));
        ElementPattern rootPattern = new ElementPattern(particleContainer);

        root.setChildPattern(new ChildPattern(null, rootPattern));
        tree.addChild(root);

        // Create foo:root element
        ElementTreeNode fooRoot = new ElementTreeNode("http://example.org/foo", "root", new Expression());
        fooRoot.getExpression().setAncestorPattern(new AncestorPattern(new SingleSlashPrefixElement("http://example.org/foo", "root")));

        SequencePattern fooParticleContainer = new SequencePattern();
        fooParticleContainer.addParticle(new GroupRef(groups.getReference("beta")));
        ElementPattern fooRootPattern = new ElementPattern(fooParticleContainer);

        fooRoot.setChildPattern(new ChildPattern(null, fooRootPattern));
        tree.addChild(fooRoot);

        // Create GroupTreeNode
        GroupTreeNode alpha = new GroupTreeNode("alpha", new Expression(), new GroupRef(groups.getReference("alpha")));
        LinkedHashSet<String> alphaNamespaces = new LinkedHashSet<String>();
        alphaNamespaces.add("http://example.org");
        alpha.addNamespaces(alphaNamespaces);

        alpha.getExpression().setChildPattern(new ChildPattern(null, new ElementPattern(groupParticleContainer)));
        root.addChild(alpha);

        // Create GroupTreeNode
        GroupTreeNode beta = new GroupTreeNode("beta", new Expression(), new GroupRef(groups.getReference("beta")));
        LinkedHashSet<String> betaNamespaces = new LinkedHashSet<String>();
        betaNamespaces.add("http://example.org/foo");
        beta.addNamespaces(betaNamespaces);

        beta.getExpression().setChildPattern(new ChildPattern(null, new ElementPattern(groupParticleContainer)));
        fooRoot.addChild(beta);

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
        assertNotNull(type.getContent());
        ComplexContentType content = (ComplexContentType) type.getContent();
        assertTrue(content.getParticle() instanceof SequencePattern);
        assertEquals(1, ((SequencePattern) content.getParticle()).getParticles().size());
        assertTrue(((SequencePattern) content.getParticle()).getParticles().get(0) instanceof GroupRef);

        // Assert group has been created
        assertNotNull(xsd.getGroupSymbolTable().getReference("{http://example.org}alpha"));
        assertNotNull(xsd.getGroupSymbolTable().getReference("{http://example.org}alpha").getReference());
        assertNotNull(xsd.getGroupSymbolTable().getReference("{http://example.org}alpha").getReference().getParticleContainer());
        assertTrue(xsd.getGroupSymbolTable().getReference("{http://example.org}alpha").getReference().getParticleContainer() instanceof SequencePattern);

        // Assertions on second schema
        assertTrue(schemas.containsKey("http://example.org/foo"));
        XSDSchema fooXsd = schemas.get("http://example.org/foo");

        assertTrue(((SymbolTable) fooXsd.getElementSymbolTable()).hasReference("{http://example.org/foo}root"));

        de.tudortmund.cs.bonxai.xsd.Element fooXsdElement = ((SymbolTable<de.tudortmund.cs.bonxai.xsd.Element>) fooXsd.getElementSymbolTable()).getReference("{http://example.org/foo}root").getReference();

        ComplexType fooType = (ComplexType) fooXsdElement.getType();
        assertNotNull(fooType.getContent());
        ComplexContentType fooContent = (ComplexContentType) fooType.getContent();
        assertTrue(fooContent.getParticle() instanceof SequencePattern);
        assertEquals(1, ((SequencePattern) fooContent.getParticle()).getParticles().size());
        assertTrue(((SequencePattern) fooContent.getParticle()).getParticles().get(0) instanceof GroupRef);

        // Assert group has been created
        assertNotNull(fooXsd.getGroupSymbolTable().getReference("{http://example.org/foo}beta"));
        assertNotNull(fooXsd.getGroupSymbolTable().getReference("{http://example.org/foo}beta").getReference());
        assertNotNull(fooXsd.getGroupSymbolTable().getReference("{http://example.org/foo}beta").getReference().getParticleContainer());
        assertTrue(fooXsd.getGroupSymbolTable().getReference("{http://example.org/foo}beta").getReference().getParticleContainer() instanceof SequencePattern);
    }

    @Test
    public void testConvertMultipleGroupsMultipleTimesIntoMultipleSchemas() throws Exception {
        // Create a schema tree
        SchemaTreeNode tree = new SchemaTreeNode();

        // A single group reference
        // defaul namespace http://example.org
        // namespace foo http://example.org/foo
        //
        // groups {
        //  group alpha = {
        //      element child
        //  }
        //
        //  group beta = {
        //      element child
        //  }
        // }
        //
        // grammar {
        //  /root = {
        //      group alpha,
        //      group beta
        //  }
        //
        //  /foo:root = {
        //      group alpha,
        //      group beta
        //  }
        // }

        // Create group
        SequencePattern groupParticleContainer = new SequencePattern();
        groupParticleContainer.addParticle(new de.tudortmund.cs.bonxai.bonxai.Element("http://example.org", "child"));

        SymbolTable<ElementGroupElement> groups = new SymbolTable<ElementGroupElement>();
        groups.createReference("alpha", new ElementGroupElement("alpha", groupParticleContainer));
        groups.createReference("beta",  new ElementGroupElement("beta",  groupParticleContainer));

        // Create root element
        ElementTreeNode root = new ElementTreeNode("http://example.org", "root", new Expression());
        root.getExpression().setAncestorPattern(new AncestorPattern(new SingleSlashPrefixElement("http://example.org", "root")));

        SequencePattern particleContainer = new SequencePattern();
        particleContainer.addParticle(new GroupRef(groups.getReference("alpha")));
        particleContainer.addParticle(new GroupRef(groups.getReference("beta")));
        ElementPattern rootPattern = new ElementPattern(particleContainer);

        root.setChildPattern(new ChildPattern(null, rootPattern));
        tree.addChild(root);

        // Create foo:root element
        ElementTreeNode fooRoot = new ElementTreeNode("http://example.org/foo", "root", new Expression());
        fooRoot.getExpression().setAncestorPattern(new AncestorPattern(new SingleSlashPrefixElement("http://example.org/foo", "root")));

        SequencePattern fooParticleContainer = new SequencePattern();
        fooParticleContainer.addParticle(new GroupRef(groups.getReference("alpha")));
        fooParticleContainer.addParticle(new GroupRef(groups.getReference("beta")));
        ElementPattern fooRootPattern = new ElementPattern(fooParticleContainer);

        fooRoot.setChildPattern(new ChildPattern(null, fooRootPattern));
        tree.addChild(fooRoot);

        // Create GroupTreeNode
        GroupTreeNode alpha = new GroupTreeNode("alpha", new Expression(), new GroupRef(groups.getReference("alpha")));
        LinkedHashSet<String> alphaNamespaces = new LinkedHashSet<String>();
        alphaNamespaces.add("http://example.org");
        alphaNamespaces.add("http://example.org/foo");
        alpha.addNamespaces(alphaNamespaces);

        alpha.getExpression().setChildPattern(new ChildPattern(null, new ElementPattern(groupParticleContainer)));

        // Create GroupTreeNode
        GroupTreeNode beta = new GroupTreeNode("beta", new Expression(), new GroupRef(groups.getReference("beta")));
        LinkedHashSet<String> betaNamespaces = new LinkedHashSet<String>();
        betaNamespaces.add("http://example.org");
        betaNamespaces.add("http://example.org/foo");
        beta.addNamespaces(betaNamespaces);

        beta.getExpression().setChildPattern(new ChildPattern(null, new ElementPattern(groupParticleContainer)));

        root.addChild(alpha);
        root.addChild(beta);
        fooRoot.addChild(alpha);
        fooRoot.addChild(beta);

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
        assertNotNull(type.getContent());
        ComplexContentType content = (ComplexContentType) type.getContent();
        assertTrue(content.getParticle() instanceof SequencePattern);
        assertEquals(2, ((SequencePattern) content.getParticle()).getParticles().size());
        assertTrue(((SequencePattern) content.getParticle()).getParticles().get(0) instanceof GroupRef);
        assertTrue(((SequencePattern) content.getParticle()).getParticles().get(1) instanceof GroupRef);

        // Assert group has been created
        assertNotNull(xsd.getGroupSymbolTable().getReference("{http://example.org}alpha"));
        assertNotNull(xsd.getGroupSymbolTable().getReference("{http://example.org}alpha").getReference());
        assertNotNull(xsd.getGroupSymbolTable().getReference("{http://example.org}alpha").getReference().getParticleContainer());
        assertTrue(xsd.getGroupSymbolTable().getReference("{http://example.org}alpha").getReference().getParticleContainer() instanceof SequencePattern);

        assertNotNull(xsd.getGroupSymbolTable().getReference("{http://example.org}beta"));
        assertNotNull(xsd.getGroupSymbolTable().getReference("{http://example.org}beta").getReference());
        assertNotNull(xsd.getGroupSymbolTable().getReference("{http://example.org}beta").getReference().getParticleContainer());
        assertTrue(xsd.getGroupSymbolTable().getReference("{http://example.org}beta").getReference().getParticleContainer() instanceof SequencePattern);

        // Assertions on second schema
        assertTrue(schemas.containsKey("http://example.org/foo"));
        XSDSchema fooXsd = schemas.get("http://example.org/foo");

        assertTrue(((SymbolTable) fooXsd.getElementSymbolTable()).hasReference("{http://example.org/foo}root"));

        de.tudortmund.cs.bonxai.xsd.Element fooXsdElement = ((SymbolTable<de.tudortmund.cs.bonxai.xsd.Element>) fooXsd.getElementSymbolTable()).getReference("{http://example.org/foo}root").getReference();

        ComplexType fooType = (ComplexType) fooXsdElement.getType();
        assertNotNull(fooType.getContent());
        ComplexContentType fooContent = (ComplexContentType) fooType.getContent();
        assertTrue(fooContent.getParticle() instanceof SequencePattern);
        assertEquals(2, ((SequencePattern) fooContent.getParticle()).getParticles().size());
        assertTrue(((SequencePattern) fooContent.getParticle()).getParticles().get(0) instanceof GroupRef);
        assertTrue(((SequencePattern) fooContent.getParticle()).getParticles().get(1) instanceof GroupRef);

        // Assert group has been created
        assertNotNull(fooXsd.getGroupSymbolTable().getReference("{http://example.org/foo}alpha"));
        assertNotNull(fooXsd.getGroupSymbolTable().getReference("{http://example.org/foo}alpha").getReference());
        assertNotNull(fooXsd.getGroupSymbolTable().getReference("{http://example.org/foo}alpha").getReference().getParticleContainer());
        assertTrue(fooXsd.getGroupSymbolTable().getReference("{http://example.org/foo}alpha").getReference().getParticleContainer() instanceof SequencePattern);

        assertNotNull(fooXsd.getGroupSymbolTable().getReference("{http://example.org/foo}beta"));
        assertNotNull(fooXsd.getGroupSymbolTable().getReference("{http://example.org/foo}beta").getReference());
        assertNotNull(fooXsd.getGroupSymbolTable().getReference("{http://example.org/foo}beta").getReference().getParticleContainer());
        assertTrue(fooXsd.getGroupSymbolTable().getReference("{http://example.org/foo}beta").getReference().getParticleContainer() instanceof SequencePattern);
    }
}
