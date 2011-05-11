package de.tudortmund.cs.bonxai.converter.xsd2bonxai;

import de.tudortmund.cs.bonxai.xsd.*;
import org.junit.*;
import static org.junit.Assert.*;

public class AncestorTreeTransitionTest extends junit.framework.TestCase {

    AncestorTreeNode source;
    AncestorTreeNode destination;
    String elementName;
    AncestorTreeTransition instance;

    @Before
    @Override
    public void setUp() {
        SimpleContentType content = new SimpleContentType();
        ComplexType type = new ComplexType("{http://example.com/mynamespace}complexType", content);
        TypeAutomatonState state = new TypeAutomatonState(type);
        source = new AncestorTreeNode(state);
        destination = new AncestorTreeNode(state);
        elementName = "{http://example.com/mynamespace}elementName";
        instance = new AncestorTreeTransition(source, destination, elementName);
    }

    /**
     * Test of getSource method, of class AncestorTreeTransition.
     */
    @Test
    public void testGetSource() {
        AncestorTreeNode expResult = source;
        AncestorTreeNode result = instance.getSource();
        assertEquals(expResult, result);
    }

    /**
     * Test of getDestination method, of class AncestorTreeTransition.
     */
    @Test
    public void testGetDestination() {
        AncestorTreeNode expResult = destination;
        AncestorTreeNode result = instance.getDestination();
        assertEquals(expResult, result);
    }

    /**
     * Test of getElementName method, of class AncestorTreeTransition.
     */
    @Test
    public void testGetElementName() {
        String expResult = "{http://example.com/mynamespace}elementName";
        String result = instance.getElementName();
        assertEquals(expResult, result);
    }
}