package eu.fox7.schematoolkit.dtd.om;

import eu.fox7.schematoolkit.common.AnyPattern;
import eu.fox7.schematoolkit.common.ChoicePattern;
import eu.fox7.schematoolkit.common.Particle;
import eu.fox7.schematoolkit.common.ProcessContentsInstruction;
import eu.fox7.schematoolkit.dtd.common.ElementContentModelProcessor;
import eu.fox7.schematoolkit.dtd.om.Attribute;
import eu.fox7.schematoolkit.dtd.om.DocumentTypeDefinition;
import eu.fox7.schematoolkit.dtd.om.Element;

import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test of class Element
 * @author Lars Schmidt
 */
public class ElementTest extends junit.framework.TestCase {

    private DocumentTypeDefinition dtd;

    @Before
    @Override
    public void setUp() {
        this.dtd = new DocumentTypeDefinition();
    }

    /**
     * Test of getAttributes method, of class Element.
     */
    @Test
    public void testGetAttributes() {
        Attribute attribute = new Attribute("name", "#REQUIRED", "test");
        Element element = new Element("elementName");
        assertTrue(element.getAttributes().isEmpty());

        element.addAttribute(attribute);
        assertEquals(1, element.getAttributes().size());
        assertEquals(attribute, element.getAttributes().getFirst());
        assertEquals(attribute, element.getAttributes().getLast());
    }

    /**
     * Test of addAttribute method, of class Element.
     */
    @Test
    public void testAddAttribute() {
        Attribute attribute = new Attribute("name", "#REQUIRED", "test");
        Element element = new Element("elementName");
        assertTrue(element.getAttributes().isEmpty());

        element.addAttribute(attribute);
        assertEquals(1, element.getAttributes().size());
        assertEquals(attribute, element.getAttributes().getFirst());
        assertEquals(attribute, element.getAttributes().getLast());
    }

    /**
     * Test of isEmpty method, of class Element.
     */
    @Test
    public void testIsEmpty() {
        Element element = new Element("elementName");
        assertTrue(element.isEmpty());
        element.setParticle(new ChoicePattern());
        assertFalse(element.isEmpty());
    }

    /**
     * Test of hasAnyType method, of class Element.
     */
    @Test
    public void testHasAnyType() {
        Element element = new Element("elementName");
        assertFalse(element.hasAnyType());

        element.setParticle(new AnyPattern(ProcessContentsInstruction.Strict, null));
        assertTrue(element.hasAnyType());

        element.setParticle(null);
        try {
            element.setParticle(new ElementContentModelProcessor().convertRegExpStringToParticle("ANY"));
        } catch (Exception ex) {
            return;
        }
        assertTrue(element.hasAnyType());
    }

    /**
     * Test of setMixed method, of class Element.
     */
    @Test
    public void testSetMixed() {
        Element element = new Element("elementName");
        assertFalse(element.getMixed());
        element.setMixed(true);
        assertTrue(element.getMixed());
    }

    /**
     * Test of getMixed method, of class Element.
     */
    @Test
    public void testGetMixed() {
        Element element = new Element("elementName");
        assertFalse(element.getMixed());
        element.setMixed(true);
        assertTrue(element.getMixed());
        element.setMixed(false);
        assertFalse(element.getMixed());
        element.setMixedStar(true);
        assertTrue(element.getMixed());
    }

    /**
     * Test of setMixedStar method, of class Element.
     */
    @Test
    public void testSetMixedStar() {
        Element element = new Element("elementName");
        assertFalse(element.getMixed());
        element.setMixed(true);
        assertTrue(element.getMixed());
        element.setMixed(false);
        assertFalse(element.getMixed());
        element.setMixedStar(true);
        assertTrue(element.getMixed());
        assertTrue(element.getMixedStar());
    }

    /**
     * Test of getMixedStar method, of class Element.
     */
    @Test
    public void testGetMixedStar() {
        Element element = new Element("elementName");
        assertFalse(element.getMixed());
        element.setMixed(true);
        assertTrue(element.getMixed());
        element.setMixed(false);
        assertFalse(element.getMixed());
        element.setMixedStar(true);
        assertTrue(element.getMixed());
        assertTrue(element.getMixedStar());
    }

    /**
     * Test of setParticle method, of class Element.
     */
    @Test
    public void testSetParticle() {
        Element element = new Element("elementName");
        assertEquals(null, element.getParticle());
        AnyPattern anyPattern = new AnyPattern(ProcessContentsInstruction.Strict, null);
        element.setParticle(anyPattern);
        assertEquals(anyPattern, element.getParticle());

        element.setParticle(null);
        try {
            element.setParticle(new ElementContentModelProcessor().convertRegExpStringToParticle("EMPTY"));
        } catch (Exception ex) {
            return;
        }
        assertEquals(null, element.getParticle());
    }

    /**
     * Test of getParticle method, of class Element.
     */
    @Test
    public void testGetParticle() {
        Element element = new Element("elementName");
        assertEquals(null, element.getParticle());
        AnyPattern anyPattern = new AnyPattern(ProcessContentsInstruction.Strict, null);
        element.setParticle(anyPattern);
        assertEquals(anyPattern, element.getParticle());

        element.setParticle(null);
        try {
            element.setParticle(new ElementContentModelProcessor().convertRegExpStringToParticle("EMPTY"));
        } catch (Exception ex) {
            return;
        }
        assertEquals(null, element.getParticle());
    }
}
