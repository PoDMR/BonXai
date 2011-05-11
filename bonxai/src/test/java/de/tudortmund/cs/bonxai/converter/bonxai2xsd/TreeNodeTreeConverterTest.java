package de.tudortmund.cs.bonxai.converter.bonxai2xsd;

import org.junit.Test;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Vector;

import de.tudortmund.cs.bonxai.common.*;
import de.tudortmund.cs.bonxai.bonxai.*;
import de.tudortmund.cs.bonxai.xsd.*;
import java.util.HashSet;

public class TreeNodeTreeConverterTest extends XsdTestCase {

    @Test
    public void testConvertEmptyElement() throws Exception {
        // Create a schema tree
        SchemaTreeNode tree = new SchemaTreeNode();

        // Create a single element
        // grammar { /element = { empty } }
        ElementTreeNode element = new ElementTreeNode("http://example.org", "element", new Expression());
        element.getExpression().setAncestorPattern(new AncestorPattern(new SingleSlashPrefixElement("http://example.org", "element")));
        element.setChildPattern(new ChildPattern(null, null));
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
        assertNull(xsdElement.getType());
        assertFalse(xsdElement.isNillable());
        assertNull(xsdElement.getDefault());
        assertNull(xsdElement.getFixed());
    }

    @Test
    public void testConvertSimpleTree() throws Exception {
        // Create a schema tree
        SchemaTreeNode tree = new SchemaTreeNode();

        // Create a single element
        // grammar { /element = { string } }
        ElementTreeNode element = new ElementTreeNode("http://example.org", "element", new Expression());
        element.getExpression().setAncestorPattern(new AncestorPattern(new SingleSlashPrefixElement("http://example.org", "element")));
        element.setChildPattern(new ChildPattern(null, new ElementPattern(new BonxaiType("", "string"))));
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
        assertTrue(((SymbolTable) xsd.getTypeSymbolTable()).hasReference("{}string"));

        de.tudortmund.cs.bonxai.xsd.Element xsdElement = ((SymbolTable<de.tudortmund.cs.bonxai.xsd.Element>) xsd.getElementSymbolTable()).getReference("{http://example.org}element").getReference();
        assertSame(xsdElement.getType(), ((SymbolTable<Type>) xsd.getTypeSymbolTable()).getReference("{}string").getReference());
        assertFalse(xsdElement.isNillable());
        assertNull(xsdElement.getDefault());
        assertNull(xsdElement.getFixed());
    }

    @Test
    public void testConvertNillableElement() throws Exception {
        // Create a schema tree
        SchemaTreeNode tree = new SchemaTreeNode();

        // Create a single element
        // grammar { /element = { missing string } }
        ElementTreeNode element = new ElementTreeNode("http://example.org", "element", new Expression());
        element.getExpression().setAncestorPattern(new AncestorPattern(new SingleSlashPrefixElement("http://example.org", "element")));
        element.setChildPattern(new ChildPattern(null, new ElementPattern(new BonxaiType("", "string"), true)));
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
        assertTrue(((SymbolTable) xsd.getTypeSymbolTable()).hasReference("{}string"));

        de.tudortmund.cs.bonxai.xsd.Element xsdElement = ((SymbolTable<de.tudortmund.cs.bonxai.xsd.Element>) xsd.getElementSymbolTable()).getReference("{http://example.org}element").getReference();
        assertSame(xsdElement.getType(), ((SymbolTable<Type>) xsd.getTypeSymbolTable()).getReference("{}string").getReference());
        assertTrue(xsdElement.isNillable());
    }

    @Test
    public void testConvertDefaultElement() throws Exception {
        // Create a schema tree
        SchemaTreeNode tree = new SchemaTreeNode();

        // Create a single element
        // grammar { /element = { string default "42" } }
        ElementTreeNode element = new ElementTreeNode("http://example.org", "element", new Expression());
        element.getExpression().setAncestorPattern(new AncestorPattern(new SingleSlashPrefixElement("http://example.org", "element")));
        ElementPattern elementPattern = new ElementPattern(new BonxaiType("", "string"), true);
        elementPattern.setDefault("42");
        element.setChildPattern(new ChildPattern(null, elementPattern));
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
        assertTrue(((SymbolTable) xsd.getTypeSymbolTable()).hasReference("{}string"));

        de.tudortmund.cs.bonxai.xsd.Element xsdElement = ((SymbolTable<de.tudortmund.cs.bonxai.xsd.Element>) xsd.getElementSymbolTable()).getReference("{http://example.org}element").getReference();
        assertSame(xsdElement.getType(), ((SymbolTable<Type>) xsd.getTypeSymbolTable()).getReference("{}string").getReference());
        assertEquals("42", xsdElement.getDefault());
    }

    @Test
    public void testConvertFixedElement() throws Exception {
        // Create a schema tree
        SchemaTreeNode tree = new SchemaTreeNode();

        // Create a single element
        // grammar { /element = { string fixed "42" } }
        ElementTreeNode element = new ElementTreeNode("http://example.org", "element", new Expression());
        element.getExpression().setAncestorPattern(new AncestorPattern(new SingleSlashPrefixElement("http://example.org", "element")));
        ElementPattern elementPattern = new ElementPattern(new BonxaiType("", "string"), true);
        elementPattern.setFixed("42");
        element.setChildPattern(new ChildPattern(null, elementPattern));
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
        assertTrue(((SymbolTable) xsd.getTypeSymbolTable()).hasReference("{}string"));

        de.tudortmund.cs.bonxai.xsd.Element xsdElement = ((SymbolTable<de.tudortmund.cs.bonxai.xsd.Element>) xsd.getElementSymbolTable()).getReference("{http://example.org}element").getReference();
        assertSame(xsdElement.getType(), ((SymbolTable<Type>) xsd.getTypeSymbolTable()).getReference("{}string").getReference());
        assertEquals("42", xsdElement.getFixed());
    }

    @Test
    public void testConvertSimpleComplexType() throws Exception {
        // Create a schema tree
        SchemaTreeNode tree = new SchemaTreeNode();

        // Create a single element
        // grammar { /element = { element child } }
        ElementTreeNode element = new ElementTreeNode("http://example.org", "element", new Expression());
        element.getExpression().setAncestorPattern(new AncestorPattern(new SingleSlashPrefixElement("http://example.org", "element")));

        SequencePattern particleContainer = new SequencePattern();
        particleContainer.addParticle(new de.tudortmund.cs.bonxai.bonxai.Element("http://example.org", "child"));
        ElementPattern elementPattern = new ElementPattern(particleContainer);

        element.setChildPattern(new ChildPattern(null, elementPattern));
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
        assertFalse(((SymbolTable) xsd.getElementSymbolTable()).hasReference("{http://example.org}child"));

        de.tudortmund.cs.bonxai.xsd.Element xsdElement = ((SymbolTable<de.tudortmund.cs.bonxai.xsd.Element>) xsd.getElementSymbolTable()).getReference("{http://example.org}element").getReference();

        ComplexType type = (ComplexType) xsdElement.getType();
        assertNotNull(type.getContent());
        ComplexContentType content = (ComplexContentType) type.getContent();
        assertTrue(content.getParticle() instanceof SequencePattern);
        assertEquals(1, ((SequencePattern) content.getParticle()).getParticles().size());
        assertTrue(((SequencePattern) content.getParticle()).getParticles().get(0) instanceof de.tudortmund.cs.bonxai.xsd.Element);
    }

    @Test
    public void testConvertSimpleMixedComplexType() throws Exception {
        // Create a schema tree
        SchemaTreeNode tree = new SchemaTreeNode();

        // Create a single element
        // grammar { /element = { mixed element child } }
        ElementTreeNode element = new ElementTreeNode("http://example.org", "element", new Expression());
        element.getExpression().setAncestorPattern(new AncestorPattern(new SingleSlashPrefixElement("http://example.org", "element")));

        SequencePattern particleContainer = new SequencePattern();
        particleContainer.addParticle(new de.tudortmund.cs.bonxai.bonxai.Element("http://example.org", "child"));
        ElementPattern elementPattern = new ElementPattern(particleContainer);
        elementPattern.setMixed(true);

        element.setChildPattern(new ChildPattern(null, elementPattern));
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
        assertFalse(((SymbolTable) xsd.getElementSymbolTable()).hasReference("{http://example.org}child"));

        de.tudortmund.cs.bonxai.xsd.Element xsdElement = ((SymbolTable<de.tudortmund.cs.bonxai.xsd.Element>) xsd.getElementSymbolTable()).getReference("{http://example.org}element").getReference();

        ComplexType type = (ComplexType) xsdElement.getType();
        assertNotNull(type.getContent());
        ComplexContentType content = (ComplexContentType) type.getContent();
        assertTrue(content.getMixed());
    }

    @Test
    public void testConvertInnerTypeDefinition() throws Exception {
        // Create a schema tree
        SchemaTreeNode tree = new SchemaTreeNode();

        // Create a single element
        // grammar { /element = { element child { string } } }
        ElementTreeNode element = new ElementTreeNode("http://example.org", "element", new Expression());
        element.getExpression().setAncestorPattern(new AncestorPattern(new SingleSlashPrefixElement("http://example.org", "element")));

        de.tudortmund.cs.bonxai.bonxai.Element innerElement = new de.tudortmund.cs.bonxai.bonxai.Element("http://example.org", "child", new BonxaiType("", "string"));

        SequencePattern particleContainer = new SequencePattern();
        particleContainer.addParticle(innerElement);

        ElementPattern elementPattern = new ElementPattern(particleContainer);
        element.setChildPattern(new ChildPattern(null, elementPattern));
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
        assertFalse(((SymbolTable) xsd.getElementSymbolTable()).hasReference("{http://example.org}child"));

        de.tudortmund.cs.bonxai.xsd.Element xsdElement = ((SymbolTable<de.tudortmund.cs.bonxai.xsd.Element>) xsd.getElementSymbolTable()).getReference("{http://example.org}element").getReference();

        ComplexType type = (ComplexType) xsdElement.getType();
        assertNotNull(type.getContent());

        ComplexContentType content = (ComplexContentType) type.getContent();
        assertTrue(content.getParticle() instanceof SequencePattern);
        assertEquals(1, ((SequencePattern) content.getParticle()).getParticles().size());
        assertTrue(((SequencePattern) content.getParticle()).getParticles().get(0) instanceof de.tudortmund.cs.bonxai.xsd.Element);

        de.tudortmund.cs.bonxai.xsd.Element innerXsdElement = (de.tudortmund.cs.bonxai.xsd.Element) ((SequencePattern) content.getParticle()).getParticles().get(0);

        assertTrue(((SymbolTable) xsd.getTypeSymbolTable()).hasReference("{}string"));
        assertSame(innerXsdElement.getType(), ((SymbolTable<Type>) xsd.getTypeSymbolTable()).getReference("{}string").getReference());
        assertFalse(xsdElement.isNillable());
        assertNull(xsdElement.getDefault());
        assertNull(xsdElement.getFixed());
    }

    @Test
    public void testConvertFixedInnerTypeDefinition() throws Exception {
        // Create a schema tree
        SchemaTreeNode tree = new SchemaTreeNode();

        // Create a single element
        // grammar { /element = { element child { string fixed "42" } } }
        ElementTreeNode element = new ElementTreeNode("http://example.org", "element", new Expression());
        element.getExpression().setAncestorPattern(new AncestorPattern(new SingleSlashPrefixElement("http://example.org", "element")));

        de.tudortmund.cs.bonxai.bonxai.Element innerElement = new de.tudortmund.cs.bonxai.bonxai.Element("http://example.org", "child", new BonxaiType("", "string"));
        innerElement.setFixed("42");

        SequencePattern particleContainer = new SequencePattern();
        particleContainer.addParticle(innerElement);

        ElementPattern elementPattern = new ElementPattern(particleContainer);
        element.setChildPattern(new ChildPattern(null, elementPattern));
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
        assertFalse(((SymbolTable) xsd.getElementSymbolTable()).hasReference("{http://example.org}child"));

        de.tudortmund.cs.bonxai.xsd.Element xsdElement = ((SymbolTable<de.tudortmund.cs.bonxai.xsd.Element>) xsd.getElementSymbolTable()).getReference("{http://example.org}element").getReference();

        ComplexType type = (ComplexType) xsdElement.getType();
        assertNotNull(type.getContent());

        ComplexContentType content = (ComplexContentType) type.getContent();
        assertTrue(content.getParticle() instanceof SequencePattern);
        assertEquals(1, ((SequencePattern) content.getParticle()).getParticles().size());
        assertTrue(((SequencePattern) content.getParticle()).getParticles().get(0) instanceof de.tudortmund.cs.bonxai.xsd.Element);

        de.tudortmund.cs.bonxai.xsd.Element innerXsdElement = (de.tudortmund.cs.bonxai.xsd.Element) ((SequencePattern) content.getParticle()).getParticles().get(0);

        assertTrue(((SymbolTable) xsd.getTypeSymbolTable()).hasReference("{}string"));
        assertSame(innerXsdElement.getType(), ((SymbolTable<Type>) xsd.getTypeSymbolTable()).getReference("{}string").getReference());
        assertEquals("42", innerXsdElement.getFixed());
    }

    @Test
    public void testConvertDefaultInnerTypeDefinition() throws Exception {
        // Create a schema tree
        SchemaTreeNode tree = new SchemaTreeNode();

        // Create a single element
        // grammar { /element = { element child { string default "42" } } }
        ElementTreeNode element = new ElementTreeNode("http://example.org", "element", new Expression());
        element.getExpression().setAncestorPattern(new AncestorPattern(new SingleSlashPrefixElement("http://example.org", "element")));

        de.tudortmund.cs.bonxai.bonxai.Element innerElement = new de.tudortmund.cs.bonxai.bonxai.Element("http://example.org", "child", new BonxaiType("", "string"));
        innerElement.setDefault("42");

        SequencePattern particleContainer = new SequencePattern();
        particleContainer.addParticle(innerElement);

        ElementPattern elementPattern = new ElementPattern(particleContainer);
        element.setChildPattern(new ChildPattern(null, elementPattern));
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
        assertFalse(((SymbolTable) xsd.getElementSymbolTable()).hasReference("{http://example.org}child"));

        de.tudortmund.cs.bonxai.xsd.Element xsdElement = ((SymbolTable<de.tudortmund.cs.bonxai.xsd.Element>) xsd.getElementSymbolTable()).getReference("{http://example.org}element").getReference();

        ComplexType type = (ComplexType) xsdElement.getType();
        assertNotNull(type.getContent());

        ComplexContentType content = (ComplexContentType) type.getContent();
        assertTrue(content.getParticle() instanceof SequencePattern);
        assertEquals(1, ((SequencePattern) content.getParticle()).getParticles().size());
        assertTrue(((SequencePattern) content.getParticle()).getParticles().get(0) instanceof de.tudortmund.cs.bonxai.xsd.Element);

        de.tudortmund.cs.bonxai.xsd.Element innerXsdElement = (de.tudortmund.cs.bonxai.xsd.Element) ((SequencePattern) content.getParticle()).getParticles().get(0);

        assertTrue(((SymbolTable) xsd.getTypeSymbolTable()).hasReference("{}string"));
        assertSame(innerXsdElement.getType(), ((SymbolTable<Type>) xsd.getTypeSymbolTable()).getReference("{}string").getReference());
        assertEquals("42", innerXsdElement.getDefault());
    }

    @Test
    public void testConvertNillableInnerTypeDefinition() throws Exception {
        // Create a schema tree
        SchemaTreeNode tree = new SchemaTreeNode();

        // Create a single element
        // grammar { /element = { element child { missing string } } }
        ElementTreeNode element = new ElementTreeNode("http://example.org", "element", new Expression());
        element.getExpression().setAncestorPattern(new AncestorPattern(new SingleSlashPrefixElement("http://example.org", "element")));

        de.tudortmund.cs.bonxai.bonxai.Element innerElement = new de.tudortmund.cs.bonxai.bonxai.Element("http://example.org", "child", new BonxaiType("", "string"), true);

        SequencePattern particleContainer = new SequencePattern();
        particleContainer.addParticle(innerElement);

        ElementPattern elementPattern = new ElementPattern(particleContainer);
        element.setChildPattern(new ChildPattern(null, elementPattern));
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
        assertFalse(((SymbolTable) xsd.getElementSymbolTable()).hasReference("{http://example.org}child"));

        de.tudortmund.cs.bonxai.xsd.Element xsdElement = ((SymbolTable<de.tudortmund.cs.bonxai.xsd.Element>) xsd.getElementSymbolTable()).getReference("{http://example.org}element").getReference();

        ComplexType type = (ComplexType) xsdElement.getType();
        assertNotNull(type.getContent());

        ComplexContentType content = (ComplexContentType) type.getContent();
        assertTrue(content.getParticle() instanceof SequencePattern);
        assertEquals(1, ((SequencePattern) content.getParticle()).getParticles().size());
        assertTrue(((SequencePattern) content.getParticle()).getParticles().get(0) instanceof de.tudortmund.cs.bonxai.xsd.Element);

        de.tudortmund.cs.bonxai.xsd.Element innerXsdElement = (de.tudortmund.cs.bonxai.xsd.Element) ((SequencePattern) content.getParticle()).getParticles().get(0);

        assertTrue(((SymbolTable) xsd.getTypeSymbolTable()).hasReference("{}string"));
        assertSame(innerXsdElement.getType(), ((SymbolTable<Type>) xsd.getTypeSymbolTable()).getReference("{}string").getReference());
        assertTrue(innerXsdElement.isNillable());
    }

    @Test
    public void testConversionOfComplexTypeInlining() throws Exception {
        // Create a schema tree
        SchemaTreeNode tree = new SchemaTreeNode();

        // Create TreeNodeTree for the following grammar:
        // grammar {
        //     /root              = { element child1, element alpha }
        //     /root/child1       = { element alpha }
        //     /root/alpha        = { element beta }
        //     /root/child1/alpha = { element beta }
        //
        //     # Type, with two references, but NO global type
        //     //alpha/beta       = { string }
        // }

        // Root
        ElementTreeNode root = new ElementTreeNode("http://example.org", "root", new Expression());
        root.getExpression().setAncestorPattern(new AncestorPattern(new SingleSlashPrefixElement("http://example.org", "root")));

        de.tudortmund.cs.bonxai.bonxai.Element root_child1 = new de.tudortmund.cs.bonxai.bonxai.Element("http://example.org", "child1");
        de.tudortmund.cs.bonxai.bonxai.Element root_alpha = new de.tudortmund.cs.bonxai.bonxai.Element("http://example.org", "alpha");

        SequencePattern root_particleContainer = new SequencePattern();
        root_particleContainer.addParticle(root_child1);
        root_particleContainer.addParticle(root_alpha);

        ElementPattern root_elementPattern = new ElementPattern(root_particleContainer);
        root.setChildPattern(new ChildPattern(null, root_elementPattern));
        tree.addChild(root);

        // Child1
        ElementTreeNode child1 = new ElementTreeNode("http://example.org", "child1", new Expression());

        Vector<AncestorPatternParticle> child1_ancestor = new Vector<AncestorPatternParticle>();
        child1_ancestor.add(new SingleSlashPrefixElement("http://example.org", "root"));
        child1_ancestor.add(new SingleSlashPrefixElement("http://example.org", "child1"));
        child1.getExpression().setAncestorPattern(new AncestorPattern(new SequenceExpression(child1_ancestor)));

        de.tudortmund.cs.bonxai.bonxai.Element child1_alpha = new de.tudortmund.cs.bonxai.bonxai.Element("http://example.org", "alpha");

        SequencePattern child1_particleContainer = new SequencePattern();
        child1_particleContainer.addParticle(child1_alpha);

        ElementPattern child1_elementPattern = new ElementPattern(child1_particleContainer);
        child1.setChildPattern(new ChildPattern(null, child1_elementPattern));
        root.addChild(child1);

        // Alpha
        ElementTreeNode alpha = new ElementTreeNode("http://example.org", "alpha", new Expression());

        Vector<AncestorPatternParticle> alpha_ancestor = new Vector<AncestorPatternParticle>();
        alpha_ancestor.add(new SingleSlashPrefixElement("http://example.org", "root"));
        alpha_ancestor.add(new SingleSlashPrefixElement("http://example.org", "alpha"));
        alpha.getExpression().setAncestorPattern(new AncestorPattern(new SequenceExpression(alpha_ancestor)));

        de.tudortmund.cs.bonxai.bonxai.Element alpha_beta = new de.tudortmund.cs.bonxai.bonxai.Element("http://example.org", "beta");

        SequencePattern alpha_particleContainer = new SequencePattern();
        alpha_particleContainer.addParticle(alpha_beta);

        ElementPattern alpha_elementPattern = new ElementPattern(alpha_particleContainer);
        alpha.setChildPattern(new ChildPattern(null, alpha_elementPattern));
        root.addChild(alpha);

        // Alpha 2
        ElementTreeNode alpha_2 = new ElementTreeNode("http://example.org", "alpha", new Expression());

        Vector<AncestorPatternParticle> alpha_2_ancestor = new Vector<AncestorPatternParticle>();
        alpha_2_ancestor.add(new SingleSlashPrefixElement("http://example.org", "root"));
        alpha_2_ancestor.add(new SingleSlashPrefixElement("http://example.org", "child1"));
        alpha_2_ancestor.add(new SingleSlashPrefixElement("http://example.org", "alpha"));
        alpha_2.getExpression().setAncestorPattern(new AncestorPattern(new SequenceExpression(alpha_2_ancestor)));

        de.tudortmund.cs.bonxai.bonxai.Element alpha_2_beta = new de.tudortmund.cs.bonxai.bonxai.Element("http://example.org", "beta");

        SequencePattern alpha_2_particleContainer = new SequencePattern();
        alpha_2_particleContainer.addParticle(alpha_2_beta);

        ElementPattern alpha_2_elementPattern = new ElementPattern(alpha_2_particleContainer);
        alpha_2.setChildPattern(new ChildPattern(null, alpha_2_elementPattern));
        child1.addChild(alpha_2);

        // Beta
        ElementTreeNode beta = new ElementTreeNode("http://example.org", "beta", new Expression());

        Vector<AncestorPatternParticle> beta_ancestor = new Vector<AncestorPatternParticle>();
        beta_ancestor.add(new DoubleSlashPrefixElement("http://example.org", "alpha"));
        beta_ancestor.add(new SingleSlashPrefixElement("http://example.org", "beta"));
        beta.getExpression().setAncestorPattern(new AncestorPattern(new SequenceExpression(beta_ancestor)));

        beta.setChildPattern(new ChildPattern(null, new ElementPattern(new BonxaiType("", "string"))));
        alpha.addChild(beta);
        alpha_2.addChild(beta);

        // DebugUtil debug = new DebugUtil();
        // debug.printTreeNodeToSystemOut(tree);

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
        xsd.setNamespaceList(new NamespaceList(new DefaultNamespace("http://example.org")));

        assertSchemaSame(xsd, "complex_type_inlining");
    }

    @Test
    public void testConversionOfComplexTypeReferencing() throws Exception {
        // Create a schema tree
        SchemaTreeNode tree = new SchemaTreeNode();

        // Create TreeNodeTree for the following grammar:
        // grammar {
        //     /root              = { element child1, element alpha }
        //     /root/child1       = { element alpha }
        //     /root/alpha        = { element beta }
        //     /root/child1/alpha = { element beta }
        //
        //     # Type, with two references, and global type
        //     //beta             = { string }
        // }

        // Root
        ElementTreeNode root = new ElementTreeNode("http://example.org", "root", new Expression());
        root.getExpression().setAncestorPattern(new AncestorPattern(new SingleSlashPrefixElement("http://example.org", "root")));

        de.tudortmund.cs.bonxai.bonxai.Element root_child1 = new de.tudortmund.cs.bonxai.bonxai.Element("http://example.org", "child1");
        de.tudortmund.cs.bonxai.bonxai.Element root_alpha = new de.tudortmund.cs.bonxai.bonxai.Element("http://example.org", "alpha");

        SequencePattern root_particleContainer = new SequencePattern();
        root_particleContainer.addParticle(root_child1);
        root_particleContainer.addParticle(root_alpha);

        ElementPattern root_elementPattern = new ElementPattern(root_particleContainer);
        root.setChildPattern(new ChildPattern(null, root_elementPattern));
        tree.addChild(root);

        // Child1
        ElementTreeNode child1 = new ElementTreeNode("http://example.org", "child1", new Expression());

        Vector<AncestorPatternParticle> child1_ancestor = new Vector<AncestorPatternParticle>();
        child1_ancestor.add(new SingleSlashPrefixElement("http://example.org", "root"));
        child1_ancestor.add(new SingleSlashPrefixElement("http://example.org", "child1"));
        child1.getExpression().setAncestorPattern(new AncestorPattern(new SequenceExpression(child1_ancestor)));

        de.tudortmund.cs.bonxai.bonxai.Element child1_alpha = new de.tudortmund.cs.bonxai.bonxai.Element("http://example.org", "alpha");

        SequencePattern child1_particleContainer = new SequencePattern();
        child1_particleContainer.addParticle(child1_alpha);

        ElementPattern child1_elementPattern = new ElementPattern(child1_particleContainer);
        child1.setChildPattern(new ChildPattern(null, child1_elementPattern));
        root.addChild(child1);

        // Alpha
        ElementTreeNode alpha = new ElementTreeNode("http://example.org", "alpha", new Expression());

        Vector<AncestorPatternParticle> alpha_ancestor = new Vector<AncestorPatternParticle>();
        alpha_ancestor.add(new SingleSlashPrefixElement("http://example.org", "root"));
        alpha_ancestor.add(new SingleSlashPrefixElement("http://example.org", "alpha"));
        alpha.getExpression().setAncestorPattern(new AncestorPattern(new SequenceExpression(alpha_ancestor)));

        de.tudortmund.cs.bonxai.bonxai.Element alpha_beta = new de.tudortmund.cs.bonxai.bonxai.Element("http://example.org", "beta");

        SequencePattern alpha_particleContainer = new SequencePattern();
        alpha_particleContainer.addParticle(alpha_beta);

        ElementPattern alpha_elementPattern = new ElementPattern(alpha_particleContainer);
        alpha.setChildPattern(new ChildPattern(null, alpha_elementPattern));
        root.addChild(alpha);

        // Alpha 2
        ElementTreeNode alpha_2 = new ElementTreeNode("http://example.org", "alpha", new Expression());

        Vector<AncestorPatternParticle> alpha_2_ancestor = new Vector<AncestorPatternParticle>();
        alpha_2_ancestor.add(new SingleSlashPrefixElement("http://example.org", "root"));
        alpha_2_ancestor.add(new SingleSlashPrefixElement("http://example.org", "child1"));
        alpha_2_ancestor.add(new SingleSlashPrefixElement("http://example.org", "alpha"));
        alpha_2.getExpression().setAncestorPattern(new AncestorPattern(new SequenceExpression(alpha_2_ancestor)));

        de.tudortmund.cs.bonxai.bonxai.Element alpha_2_beta = new de.tudortmund.cs.bonxai.bonxai.Element("http://example.org", "beta");

        SequencePattern alpha_2_particleContainer = new SequencePattern();
        alpha_2_particleContainer.addParticle(alpha_2_beta);

        ElementPattern alpha_2_elementPattern = new ElementPattern(alpha_2_particleContainer);
        alpha_2.setChildPattern(new ChildPattern(null, alpha_2_elementPattern));
        child1.addChild(alpha_2);

        // Beta
        ElementTreeNode beta = new ElementTreeNode("http://example.org", "beta", new Expression());

        Vector<AncestorPatternParticle> beta_ancestor = new Vector<AncestorPatternParticle>();
        beta_ancestor.add(new DoubleSlashPrefixElement("http://example.org", "beta"));
        beta.getExpression().setAncestorPattern(new AncestorPattern(new SequenceExpression(beta_ancestor)));

        beta.setChildPattern(new ChildPattern(null, new ElementPattern(new BonxaiType("", "string"))));
        alpha.addChild(beta);
        alpha_2.addChild(beta);
        tree.addChild(beta);

        // DebugUtil debug = new DebugUtil();
        // debug.printTreeNodeToSystemOut(tree);

        // List of root elements
        LinkedHashSet<TreeNode> rootNodes = new LinkedHashSet<TreeNode>();
        rootNodes.add(root);
        rootNodes.add(beta);

        // Convert tree to XSD
        HashMap<String, XSDSchema> schemas = new HashMap<String, XSDSchema>();
        TreeNodeTreeConverter converter = new TreeNodeTreeConverter(schemas);
        converter.convert(rootNodes, tree);

        // Assertions
        assertTrue(schemas.containsKey("http://example.org"));
        XSDSchema xsd = schemas.get("http://example.org");
        xsd.setNamespaceList(new NamespaceList(new DefaultNamespace("http://example.org")));

        assertSchemaSame(xsd, "complex_type_referencing");
    }

    @Test
    public void testConversionOnlyGlobalTypes() throws Exception {
        // Create a schema tree
        SchemaTreeNode tree = new SchemaTreeNode();

        // Create TreeNodeTree for the following grammar:
        // grammar {
        //     /root    = { element child1, element alpha }
        //     //child1 = { element alpha }
        //     //alpha  = { element beta }
        //     //beta   = { string }
        // }

        // Root
        ElementTreeNode root = new ElementTreeNode("http://example.org", "root", new Expression());
        root.getExpression().setAncestorPattern(new AncestorPattern(new SingleSlashPrefixElement("http://example.org", "root")));

        de.tudortmund.cs.bonxai.bonxai.Element root_child1 = new de.tudortmund.cs.bonxai.bonxai.Element("http://example.org", "child1");
        de.tudortmund.cs.bonxai.bonxai.Element root_alpha = new de.tudortmund.cs.bonxai.bonxai.Element("http://example.org", "alpha");

        SequencePattern root_particleContainer = new SequencePattern();
        root_particleContainer.addParticle(root_child1);
        root_particleContainer.addParticle(root_alpha);

        ElementPattern root_elementPattern = new ElementPattern(root_particleContainer);
        root.setChildPattern(new ChildPattern(null, root_elementPattern));
        tree.addChild(root);

        // Child1
        ElementTreeNode child1 = new ElementTreeNode("http://example.org", "child1", new Expression());

        Vector<AncestorPatternParticle> child1_ancestor = new Vector<AncestorPatternParticle>();
        child1_ancestor.add(new DoubleSlashPrefixElement("http://example.org", "child1"));
        child1.getExpression().setAncestorPattern(new AncestorPattern(new SequenceExpression(child1_ancestor)));

        de.tudortmund.cs.bonxai.bonxai.Element child1_alpha = new de.tudortmund.cs.bonxai.bonxai.Element("http://example.org", "alpha");

        SequencePattern child1_particleContainer = new SequencePattern();
        child1_particleContainer.addParticle(child1_alpha);

        ElementPattern child1_elementPattern = new ElementPattern(child1_particleContainer);
        child1.getExpression().setChildPattern(new ChildPattern(null, child1_elementPattern));
        root.addChild(child1);
        tree.addChild(child1);

        // Alpha
        ElementTreeNode alpha = new ElementTreeNode("http://example.org", "alpha", new Expression());

        Vector<AncestorPatternParticle> alpha_ancestor = new Vector<AncestorPatternParticle>();
        alpha_ancestor.add(new DoubleSlashPrefixElement("http://example.org", "alpha"));
        alpha.getExpression().setAncestorPattern(new AncestorPattern(new SequenceExpression(alpha_ancestor)));

        de.tudortmund.cs.bonxai.bonxai.Element alpha_beta = new de.tudortmund.cs.bonxai.bonxai.Element("http://example.org", "beta");

        SequencePattern alpha_particleContainer = new SequencePattern();
        alpha_particleContainer.addParticle(alpha_beta);

        ElementPattern alpha_elementPattern = new ElementPattern(alpha_particleContainer);
        alpha.setChildPattern(new ChildPattern(null, alpha_elementPattern));
        root.addChild(alpha);
        tree.addChild(alpha);

        // Beta
        ElementTreeNode beta = new ElementTreeNode("http://example.org", "beta", new Expression());

        Vector<AncestorPatternParticle> beta_ancestor = new Vector<AncestorPatternParticle>();
        beta_ancestor.add(new DoubleSlashPrefixElement("http://example.org", "beta"));
        beta.getExpression().setAncestorPattern(new AncestorPattern(new SequenceExpression(beta_ancestor)));

        beta.setChildPattern(new ChildPattern(null, new ElementPattern(new BonxaiType("", "string"))));
        alpha.addChild(beta);
        tree.addChild(beta);

        // DebugUtil debug = new DebugUtil();
        // debug.printTreeNodeToSystemOut(tree);

        // List of root elements
        LinkedHashSet<TreeNode> rootNodes = new LinkedHashSet<TreeNode>();
        rootNodes.add(root);
        rootNodes.add(child1);
        rootNodes.add(alpha);
        rootNodes.add(beta);

        // Convert tree to XSD
        HashMap<String, XSDSchema> schemas = new HashMap<String, XSDSchema>();
        TreeNodeTreeConverter converter = new TreeNodeTreeConverter(schemas);
        converter.convert(rootNodes, tree);

        // Assertions
        assertTrue(schemas.containsKey("http://example.org"));
        XSDSchema xsd = schemas.get("http://example.org");
        xsd.setNamespaceList(new NamespaceList(new DefaultNamespace("http://example.org")));

        assertSchemaSame(xsd, "complex_type_all_global");
    }

    @Test
    public void testConvertSimpleTreeWithKeyAndKeyRefConstraints() throws Exception {
        // Create a schema tree
        SchemaTreeNode tree = new SchemaTreeNode();

        // Create a single element
        // grammar { /element = { string } }
        ElementTreeNode element = new ElementTreeNode("http://example.org", "element", new Expression());
        element.getExpression().setAncestorPattern(new AncestorPattern(new SingleSlashPrefixElement("http://example.org", "element")));
        element.setChildPattern(new ChildPattern(null, new ElementPattern(new BonxaiType("", "string"))));
        tree.addChild(element);
        HashSet<String> fields = new HashSet<String>();
        fields.add("test");

        // Add Key and KeyRef Constraints to the element
        element.addConstraint(new KeyConstraint("{http://example.org}element", new AncestorPattern(new SingleSlashPrefixElement("http://example.org", "element")), "selector", fields));
        element.addConstraint(new KeyRefConstraint("{http://example.org}element", new AncestorPattern(new SingleSlashPrefixElement("http://example.org", "element")), "selector", fields));

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
        assertTrue(((SymbolTable) xsd.getTypeSymbolTable()).hasReference("{}string"));

        de.tudortmund.cs.bonxai.xsd.Element xsdElement = ((SymbolTable<de.tudortmund.cs.bonxai.xsd.Element>) xsd.getElementSymbolTable()).getReference("{http://example.org}element").getReference();

        assertSame(xsdElement.getType(), ((SymbolTable<Type>) xsd.getTypeSymbolTable()).getReference("{}string").getReference());
        assertFalse(xsdElement.isNillable());
        assertNull(xsdElement.getDefault());
        assertNull(xsdElement.getFixed());


        assertTrue(((KeyRef)xsdElement.getConstraints().getLast()).getFields().contains("test"));
        assertEquals("selector", ((KeyRef)xsdElement.getConstraints().getLast()).getSelector());
        assertSame(((Key)xsdElement.getConstraints().getFirst()), ((KeyRef)xsdElement.getConstraints().getLast()).getKeyOrUnique());

        assertTrue(xsd.getKeyAndUniqueSymbolTable().hasReference(((Key)xsdElement.getConstraints().getFirst()).getName()));
        assertSame(((Key)xsdElement.getConstraints().getFirst()), ((Key)xsd.getKeyAndUniqueSymbolTable().getReference(((Key)xsdElement.getConstraints().getFirst()).getName()).getReference()));
    }

    @Test
    public void testConvertSimpleTreeWithUniqueConstraint() throws Exception {
        // Create a schema tree
        SchemaTreeNode tree = new SchemaTreeNode();

        // Create a single element
        // grammar { /element = { string } }
        ElementTreeNode element = new ElementTreeNode("http://example.org", "element", new Expression());
        element.getExpression().setAncestorPattern(new AncestorPattern(new SingleSlashPrefixElement("http://example.org", "element")));
        element.setChildPattern(new ChildPattern(null, new ElementPattern(new BonxaiType("", "string"))));
        tree.addChild(element);
        HashSet<String> fields = new HashSet<String>();
        fields.add("test");

        // Add a UniqueConstraint to the element

        // These Constraints can not be part of the defined element above in a
        // real case. The second Constraint is fo renaming-test only.
        element.addConstraint(new UniqueConstraint(new AncestorPattern(new SingleSlashPrefixElement("http://example.org", "element")), "selector", fields));
        element.addConstraint(new UniqueConstraint(new AncestorPattern(new DoubleSlashPrefixElement("http://example.org", "element2")), "selector2", fields));

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
        assertTrue(((SymbolTable) xsd.getTypeSymbolTable()).hasReference("{}string"));

        de.tudortmund.cs.bonxai.xsd.Element xsdElement = ((SymbolTable<de.tudortmund.cs.bonxai.xsd.Element>) xsd.getElementSymbolTable()).getReference("{http://example.org}element").getReference();

        assertSame(xsdElement.getType(), ((SymbolTable<Type>) xsd.getTypeSymbolTable()).getReference("{}string").getReference());
        assertFalse(xsdElement.isNillable());
        assertNull(xsdElement.getDefault());
        assertNull(xsdElement.getFixed());

        assertTrue(((Unique)xsdElement.getConstraints().getFirst()).getFields().contains("test"));
        assertEquals("{http://example.org}unique_element", ((Unique)xsdElement.getConstraints().getFirst()).getName());
        assertEquals("{http://example.org}unique_element_1", ((Unique)xsdElement.getConstraints().getLast()).getName());
    }

    @Test
    public void testConvertElementsInDifferentNamespaces() throws Exception {
        // Create a schema tree
        SchemaTreeNode tree = new SchemaTreeNode();

        // Create a single element
        // grammar {
        //   /element = {
        //      element foo:child
        //   }
        //
        //   /root/foo:child = {
        //      element alpha
        //   }
        //
        //   /root/foo:child/alpha = { string }
        // }

        // Root
        ElementTreeNode root = new ElementTreeNode("http://example.org", "root", new Expression());
        root.getExpression().setAncestorPattern(new AncestorPattern(new SingleSlashPrefixElement("http://example.org", "root")));

        de.tudortmund.cs.bonxai.bonxai.Element root_child = new de.tudortmund.cs.bonxai.bonxai.Element("http://example.org", "foo:child");

        SequencePattern root_particleContainer = new SequencePattern();
        root_particleContainer.addParticle(root_child);

        ElementPattern root_elementPattern = new ElementPattern(root_particleContainer);
        root.setChildPattern(new ChildPattern(null, root_elementPattern));
        tree.addChild(root);

        // foo:Child
        ElementTreeNode child = new ElementTreeNode("http://example.org/foo", "child", new Expression());

        Vector<AncestorPatternParticle> child_ancestor = new Vector<AncestorPatternParticle>();
        child_ancestor.add(new SingleSlashPrefixElement("http://example.org", "root"));
        child_ancestor.add(new SingleSlashPrefixElement("http://example.org/foo", "child"));
        child.getExpression().setAncestorPattern(new AncestorPattern(new SequenceExpression(child_ancestor)));

        child.setChildPattern(new ChildPattern(null, new ElementPattern(new BonxaiType("", "string"))));
        root.addChild(child);

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

        // Assertions on second schema
        xsd = schemas.get("http://example.org/foo");

        assertTrue(((SymbolTable) xsd.getElementSymbolTable()).hasReference("{http://example.org/foo}child"));
        xsdElement = ((SymbolTable<de.tudortmund.cs.bonxai.xsd.Element>) xsd.getElementSymbolTable()).getReference("{http://example.org/foo}child").getReference();
        assertSame(xsdElement.getType(), ((SymbolTable<Type>) xsd.getTypeSymbolTable()).getReference("{}string").getReference());
    }

    @Test
    public void testConvertElementsDeepInDifferentNamespaces() throws Exception {
        // Create a schema tree
        SchemaTreeNode tree = new SchemaTreeNode();

        // Create a single element
        // grammar {
        //   /root = {
        //      element foo:child
        //   }
        //
        //   /root/foo:child = {
        //      element alpha
        //   }
        //
        //   /root/foo:child/alpha = { string }
        //   ; //alpha = { string }
        // }

        // Root
        ElementTreeNode root = new ElementTreeNode("http://example.org", "root", new Expression());
        root.getExpression().setAncestorPattern(new AncestorPattern(new SingleSlashPrefixElement("http://example.org", "root")));

        de.tudortmund.cs.bonxai.bonxai.Element root_child = new de.tudortmund.cs.bonxai.bonxai.Element("http://example.org", "foo:child");

        SequencePattern root_particleContainer = new SequencePattern();
        root_particleContainer.addParticle(root_child);

        ElementPattern root_elementPattern = new ElementPattern(root_particleContainer);
        root.setChildPattern(new ChildPattern(null, root_elementPattern));
        tree.addChild(root);

        // foo:Child
        ElementTreeNode child = new ElementTreeNode("http://example.org/foo", "child", new Expression());

        Vector<AncestorPatternParticle> child_ancestor = new Vector<AncestorPatternParticle>();
        child_ancestor.add(new SingleSlashPrefixElement("http://example.org", "root"));
        child_ancestor.add(new SingleSlashPrefixElement("http://example.org/foo", "child"));
        child.getExpression().setAncestorPattern(new AncestorPattern(new SequenceExpression(child_ancestor)));

        de.tudortmund.cs.bonxai.bonxai.Element child_alpha = new de.tudortmund.cs.bonxai.bonxai.Element("http://example.org", "alpha");

        SequencePattern child_particleContainer = new SequencePattern();
        child_particleContainer.addParticle(child_alpha);

        ElementPattern child_elementPattern = new ElementPattern(child_particleContainer);
        child.setChildPattern(new ChildPattern(null, child_elementPattern));
        root.addChild(child);

        // Alpha
        ElementTreeNode alpha = new ElementTreeNode("http://example.org", "alpha", new Expression());

        Vector<AncestorPatternParticle> alpha_ancestor = new Vector<AncestorPatternParticle>();
        alpha_ancestor.add(new SingleSlashPrefixElement("http://example.org", "root"));
        alpha_ancestor.add(new SingleSlashPrefixElement("http://example.org/foo", "child"));
        alpha_ancestor.add(new SingleSlashPrefixElement("http://example.org", "alpha"));
        alpha.getExpression().setAncestorPattern(new AncestorPattern(new SequenceExpression(alpha_ancestor)));

        alpha.setChildPattern(new ChildPattern(null, new ElementPattern(new BonxaiType("", "string"))));
        child.addChild(alpha);

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

        // Assertions on second schema
        xsd = schemas.get("http://example.org/foo");

        type = (ComplexType) xsdElement.getType();
        assertNotNull(type.getContent());

        // Assertions on first schema again
        xsd = schemas.get("http://example.org");

        assertTrue(((SymbolTable) xsd.getElementSymbolTable()).hasReference("{http://example.org}alpha"));
        xsdElement = ((SymbolTable<de.tudortmund.cs.bonxai.xsd.Element>) xsd.getElementSymbolTable()).getReference("{http://example.org}alpha").getReference();
        assertSame(xsdElement.getType(), ((SymbolTable<Type>) xsd.getTypeSymbolTable()).getReference("{}string").getReference());
    }
}
