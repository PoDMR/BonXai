/**
 * Copyright 2009-2012 TU Dortmund
 *
 * This file is part of FoXLib.
 *
 * FoXLib is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FoXLib is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with BonXai.  If not, see <http://www.gnu.org/licenses/>.
 */

package eu.fox7.schematoolkit.dtd.om;

import eu.fox7.schematoolkit.common.AnyPattern;
import eu.fox7.schematoolkit.common.ChoicePattern;
import eu.fox7.schematoolkit.common.Namespace;
import eu.fox7.schematoolkit.common.ProcessContentsInstruction;
import eu.fox7.schematoolkit.common.QualifiedName;
import eu.fox7.schematoolkit.dtd.common.ElementContentModelProcessor;
import eu.fox7.schematoolkit.dtd.om.Attribute;
import eu.fox7.schematoolkit.dtd.om.Element;

import org.junit.Test;

/**
 * Test of class Element
 * @author Lars Schmidt
 */
public class ElementTest extends junit.framework.TestCase {
	private QualifiedName elementName = new QualifiedName(Namespace.EMPTY_NAMESPACE, "elementName");
	
    /**
     * Test of getAttributes method, of class Element.
     */
    @Test
    public void testGetAttributes() {
        Attribute attribute = new Attribute(new QualifiedName("","name"), "#REQUIRED", "test");
        Element element = new Element(elementName);
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
        Attribute attribute = new Attribute(new QualifiedName("","name"), "#REQUIRED", "test");
        Element element = new Element(elementName);
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
        Element element = new Element(elementName);
        assertTrue(element.isEmpty());
        element.setParticle(new ChoicePattern());
        assertFalse(element.isEmpty());
    }

    /**
     * Test of hasAnyType method, of class Element.
     */
    @Test
    public void testHasAnyType() {
        Element element = new Element(elementName);
        assertFalse(element.hasAnyType());

        AnyPattern anyPattern = new AnyPattern(ProcessContentsInstruction.STRICT, Namespace.EMPTY_NAMESPACE);
        element.setParticle(anyPattern);
        assertTrue(element.hasAnyType());

        element.setParticle(null);
        try {
            element.setParticle(new AnyPattern());
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
        Element element = new Element(elementName);
        assertFalse(element.getMixed());
        element.setMixed(true);
        assertTrue(element.getMixed());
    }

    /**
     * Test of getMixed method, of class Element.
     */
    @Test
    public void testGetMixed() {
        Element element = new Element(elementName);
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
        Element element = new Element(elementName);
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
        Element element = new Element(elementName);
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
        Element element = new Element(elementName);
        assertEquals(null, element.getParticle());
        AnyPattern anyPattern = new AnyPattern(ProcessContentsInstruction.STRICT, Namespace.EMPTY_NAMESPACE);
        element.setParticle(anyPattern);
        assertEquals(anyPattern, element.getParticle());
    }
}
