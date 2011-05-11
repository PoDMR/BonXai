package de.tudortmund.cs.bonxai.converter.bonxai2xsd;

import de.tudortmund.cs.bonxai.bonxai.*;
import java.util.LinkedHashSet;
import java.util.Vector;
import org.junit.Test;

/**
 * SchemaTreeNodeTest
 */
public class SchemaTreeNodeTest extends junit.framework.TestCase {

    public SchemaTreeNodeTest() {
    }

    /**
     * Test of getChilds method, of class SchemaTreeNode.
     */
    @Test
    public void testGetChilds() {
        SchemaTreeNode instance = new SchemaTreeNode();
        SchemaTreeNode instance_child = new SchemaTreeNode();
        instance.addChild(instance_child);
        LinkedHashSet<SchemaTreeNode> expHashSet = new LinkedHashSet<SchemaTreeNode>();
        expHashSet.add(instance_child);
        assertEquals(expHashSet, instance.getChilds());
    }

    /**
     * Test of addChild method, of class SchemaTreeNode.
     */
    @Test
    public void testAddChild() {
        SchemaTreeNode instance = new SchemaTreeNode();
        SchemaTreeNode instance_child = new SchemaTreeNode();

        instance.addChild(instance_child);

        LinkedHashSet<SchemaTreeNode> expHashSet = new LinkedHashSet<SchemaTreeNode>();
        expHashSet.add(instance_child);
        assertEquals(expHashSet, instance.getChilds());
    }

    /**
     * Test of generateId method, of class SchemaTreeNode.
     */
    @Test
    public void testGenerateId() {
        SchemaTreeNode schema = new SchemaTreeNode();
        assertEquals("Schema()", schema.generateId());
    }

    /**
     * Test of generateId method, of class SchemaTreeNode.
     */
    @Test
    public void testRecursiveGenerateId() {
        SchemaTreeNode schema = new SchemaTreeNode();

        GroupTreeNode group = new GroupTreeNode("group", new Expression(), null);
        schema.addChild(group);

        ElementTreeNode element1 = new ElementTreeNode("http://example.com/ns", "foo", new de.tudortmund.cs.bonxai.bonxai.Expression());
        group.addChild(element1);
        ElementTreeNode element2 = new ElementTreeNode("http://example.com/ns", "bar", new de.tudortmund.cs.bonxai.bonxai.Expression());
        group.addChild(element2);

        assertEquals("Schema([group]({http://example.com/ns}foo(), {http://example.com/ns}bar()))", schema.generateId());
    }

    /**
     * Test of getId method, of class SchemaTreeNode.
     */
    @Test
    public void testGetId() {
        SchemaTreeNode instance = new SchemaTreeNode();
        String expResult = "test";
        instance.setId(expResult);

        String result = instance.getId();

        assertEquals(expResult, result);
    }

    /**
     * Test of getParents method, of class SchemaTreeNode.
     */
    @Test
    public void testGetParent() {
        SchemaTreeNode instance = new SchemaTreeNode();
        SchemaTreeNode instance_parent = new SchemaTreeNode();
        instance.addParent(instance_parent);

        assertTrue(instance.getParents().contains(instance_parent));
    }

    /**
     * Test of setParent method, of class SchemaTreeNode.
     */
    @Test
    public void testSetParent() {
        SchemaTreeNode instance = new SchemaTreeNode();
        SchemaTreeNode instance_parent = new SchemaTreeNode();

        instance.addParent(instance_parent);
        assertTrue(instance.getParents().contains(instance_parent));
    }

    /**
     * Test of getAncestorPath method, of class SchemaTreeNode.
     */
    @Test
    public void testGetAncestorPath() {
        // Setup
        de.tudortmund.cs.bonxai.bonxai.Expression expression = new de.tudortmund.cs.bonxai.bonxai.Expression();
        de.tudortmund.cs.bonxai.bonxai.Expression expression1 = new de.tudortmund.cs.bonxai.bonxai.Expression();
        SchemaTreeNode schemaTreeNode = new SchemaTreeNode();
        ElementTreeNode instance1 = new ElementTreeNode("http://example.com/ns", "test", expression);
        ElementTreeNode instance2 = new ElementTreeNode("http://example.com/ns", "test2", expression1);

        schemaTreeNode.addChild(instance1);
        instance1.addChild(instance2);

        // Assertions
        Vector<TreeNode> expResult = new Vector<TreeNode>();
        expResult.add(schemaTreeNode);
        expResult.add(instance1);
        expResult.add(instance2);

        /* All the TreeNodes defined above have to be in the AncestorPath of
         * instance2
         */
        //System.out.println(instance2.getAncestorPath());
        assertTrue(instance2.getAncestorPath().containsAll(expResult));

        /* The AncestorPath Vector have to be the same like the Vector expResult
         */
        assertTrue(instance2.getAncestorPath().equals(expResult));

        /* The Elements in the AncestorPath Vector have to be sorted in the
         * same way like in the given order by adding them as parents to
         * the objects
         */
        assertEquals(instance2.getAncestorPath().indexOf(schemaTreeNode), 0);
        assertEquals(instance2.getAncestorPath().indexOf(instance1), 1);
    }

    /**
     * Test of removeChild method, of class SchemaTreeNode.
     */
    @Test
    public void testRemoveChild() {
        // adding a child
        SchemaTreeNode instance = new SchemaTreeNode();
        SchemaTreeNode instance_child = new SchemaTreeNode();

        instance.addChild(instance_child);

        LinkedHashSet<SchemaTreeNode> expHashSet = new LinkedHashSet<SchemaTreeNode>();
        expHashSet.add(instance_child);
        assertEquals(expHashSet, instance.getChilds());
        assertEquals(1, instance.getChildCount());

        // removing a child
        instance.removeChild(instance_child);
        assertEquals(0, instance.getChildCount());
    }

    /**
     * Test of setId method, of class SchemaTreeNode.
     */
    @Test
    public void testSetId() {
        SchemaTreeNode instance = new SchemaTreeNode();
        String expResult = "test1";

        instance.setId(expResult);

        String result = instance.getId();
        assertEquals(expResult, result);
    }

    /**
     * Test of isLeaf method, of class SchemaTreeNode.
     */
    @Test
    public void testIsLeaf() {
        SchemaTreeNode instance = new SchemaTreeNode();
        boolean expResult = true;
        boolean result = instance.isLeaf();
        assertEquals(expResult, result);

        SchemaTreeNode instance_child = new SchemaTreeNode();
        instance.addChild(instance_child);
        boolean expResult_child = false;
        boolean result_child = instance.isLeaf();
        assertEquals(expResult_child, result_child);
    }

    /**
     * Test of getChildCount method, of class SchemaTreeNode.
     */
    @Test
    public void testGetChildCount() {
        SchemaTreeNode instance = new SchemaTreeNode();
        int expResult_0 = 0;

        assertEquals(expResult_0, instance.getChildCount());

        SchemaTreeNode instance_child = new SchemaTreeNode();
        instance.addChild(instance_child);
        int expResult_1 = 1;

        int result = instance.getChildCount();

        assertEquals(expResult_1, result);
    }
}
