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

package eu.fox7.bonxai.converter.xsd2dtd;

import eu.fox7.bonxai.converter.xsd2dtd.ElementUnionBuilder;
import eu.fox7.bonxai.converter.xsd2dtd.ElementWrapper;
import eu.fox7.bonxai.converter.xsd2dtd.XSD2DTDConverter;
import eu.fox7.bonxai.dtd.DocumentTypeDefinition;
import eu.fox7.bonxai.dtd.Element;
import eu.fox7.bonxai.dtd.writer.DTDElementWriter;
import eu.fox7.schematoolkit.common.AnyPattern;
import eu.fox7.schematoolkit.common.ChoicePattern;
import eu.fox7.schematoolkit.common.CountingPattern;
import eu.fox7.schematoolkit.common.Particle;
import eu.fox7.schematoolkit.common.ProcessContentsInstruction;
import eu.fox7.schematoolkit.common.SequencePattern;
import eu.fox7.schematoolkit.xsd.om.XSDSchema;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test of class ElementUnionBuilder
 * @author Lars Schmidt
 */
public class ElementUnionBuilderTest extends junit.framework.TestCase {

    // Schema for this testcase
    XSDSchema schema;
    DocumentTypeDefinition dtd;
    boolean debug = false;

    @Before
    @Override
    public void setUp() throws Exception {
        schema = new XSDSchema(XSD2DTDConverter.XMLSCHEMA_NAMESPACE);
        dtd = new DocumentTypeDefinition();
    }

    public void printResult(Element result) throws Exception {
        if (this.debug) {
            DTDElementWriter elementWriter3 = new DTDElementWriter(result);
            System.out.println(elementWriter3.getElementDeclarationString() + "\n\n");
        }
    }

    public void printOut(Element element1, Element element2) throws Exception {
        if (this.debug) {
            DTDElementWriter elementWriter = new DTDElementWriter(element1);
            DTDElementWriter elementWriter2 = new DTDElementWriter(element2);
            System.out.println(elementWriter.getElementDeclarationString() + elementWriter2.getElementDeclarationString() + "\n wird zu \n");
        }
    }

    // EMPTY
    /**
     * Test of unifyElements method, of class ElementUnionBuilder.
     * @throws Exception
     */
    @Test
    public void testUnifyElements_EMPTY_EMPTY() throws Exception {

        Boolean element1mixed = false;
        Boolean element2mixed = false;
        Particle dtdElement1particle = null;
        Particle dtdElement2particle = null;

        //--------------------------------------------------------------------//

        Element dtdElement1 = new Element("element");
        dtdElement1.setMixed(element1mixed);
        dtdElement1.setParticle(dtdElement1particle);
        Element dtdElement2 = new Element("element");
        dtdElement2.setMixed(element2mixed);
        dtdElement2.setParticle(dtdElement2particle);
        ElementWrapper elementWrapper = new ElementWrapper(schema, "elementOne");
        elementWrapper.addDTDElement(dtdElement1);
        elementWrapper.addDTDElement(dtdElement2);
        ElementUnionBuilder instance = new ElementUnionBuilder(elementWrapper);

        printOut(dtdElement1, dtdElement2);

        instance.unifyElements();

        Element dtdElement = elementWrapper.getDTDElements().iterator().next();

        //--------------------------------------------------------------------//
        // Result:
        //--------------------------------------------------------------------//

        printResult(dtdElement);

        assertEquals("element", dtdElement.getName());
        assertFalse(dtdElement.getMixed());
        assertTrue(dtdElement.isEmpty());
        assertFalse(dtdElement.hasAnyType());
        assertTrue(dtdElement.getAttributes().isEmpty());
        assertTrue(dtdElement.getParticle() == null);

    }

    /**
     * Test of unifyElements method, of class ElementUnionBuilder.
     * @throws Exception
     */
    @Test
    public void testUnifyElements_EMPTY_ANY() throws Exception {

        Boolean element1mixed = false;
        Boolean element2mixed = false;
        Particle dtdElement1particle = null;
        Particle dtdElement2particle = new AnyPattern(ProcessContentsInstruction.STRICT, null);

        //--------------------------------------------------------------------//

        Element dtdElement1 = new Element("element");
        dtdElement1.setMixed(element1mixed);
        dtdElement1.setParticle(dtdElement1particle);
        Element dtdElement2 = new Element("element");
        dtdElement2.setMixed(element2mixed);
        dtdElement2.setParticle(dtdElement2particle);
        ElementWrapper elementWrapper = new ElementWrapper(schema, "elementOne");
        elementWrapper.addDTDElement(dtdElement1);
        elementWrapper.addDTDElement(dtdElement2);
        ElementUnionBuilder instance = new ElementUnionBuilder(elementWrapper);

        printOut(dtdElement1, dtdElement2);

        instance.unifyElements();

        Element dtdElement = elementWrapper.getDTDElements().iterator().next();

        //--------------------------------------------------------------------//
        // Result:
        //--------------------------------------------------------------------//

        printResult(dtdElement);

        assertEquals("element", dtdElement.getName());
        assertFalse(dtdElement.getMixed());
        assertFalse(dtdElement.isEmpty());
        assertTrue(dtdElement.hasAnyType());
        assertTrue(dtdElement.getAttributes().isEmpty());
        assertTrue(dtdElement.getParticle() instanceof AnyPattern);
    }

    /**
     * Test of unifyElements method, of class ElementUnionBuilder.
     * @throws Exception
     */
    @Test
    public void testUnifyElements_EMPTY_mixed() throws Exception {

        Boolean element1mixed = false;
        Boolean element2mixed = true;
        Particle dtdElement1particle = null;
        Particle dtdElement2particle = null;

        //--------------------------------------------------------------------//

        Element dtdElement1 = new Element("element");
        dtdElement1.setMixed(element1mixed);
        dtdElement1.setParticle(dtdElement1particle);
        Element dtdElement2 = new Element("element");
        dtdElement2.setMixed(element2mixed);
        dtdElement2.setParticle(dtdElement2particle);
        ElementWrapper elementWrapper = new ElementWrapper(schema, "elementOne");
        elementWrapper.addDTDElement(dtdElement1);
        elementWrapper.addDTDElement(dtdElement2);
        ElementUnionBuilder instance = new ElementUnionBuilder(elementWrapper);

        printOut(dtdElement1, dtdElement2);

        instance.unifyElements();

        Element dtdElement = elementWrapper.getDTDElements().iterator().next();

        //--------------------------------------------------------------------//
        // Result:
        //--------------------------------------------------------------------//

        printResult(dtdElement);

        assertEquals("element", dtdElement.getName());
        assertTrue(dtdElement.getMixed());
        assertTrue(dtdElement.isEmpty());
        assertFalse(dtdElement.hasAnyType());
        assertTrue(dtdElement.getAttributes().isEmpty());
        assertTrue(dtdElement.getParticle() == null);
    }

    /**
     * Test of unifyElements method, of class ElementUnionBuilder.
     * @throws Exception
     */
    @Test
    public void testUnifyElements_EMPTY_mixed_content() throws Exception {

        Boolean element1mixed = false;
        Boolean element2mixed = true;
        Particle dtdElement1particle = null;
        CountingPattern countingPattern = new CountingPattern(0, null);
        ChoicePattern choicePattern = new ChoicePattern();
        choicePattern.addParticle(new eu.fox7.schematoolkit.common.dtd.ElementRef(dtd.getElementSymbolTable().updateOrCreateReference("element", new Element("element"))));
        countingPattern.addParticle(choicePattern);
        Particle dtdElement2particle = countingPattern;

        //--------------------------------------------------------------------//

        Element dtdElement1 = new Element("element");
        dtdElement1.setMixed(element1mixed);
        dtdElement1.setParticle(dtdElement1particle);
        Element dtdElement2 = new Element("element");
        dtdElement2.setMixed(element2mixed);
        dtdElement2.setParticle(dtdElement2particle);
        ElementWrapper elementWrapper = new ElementWrapper(schema, "elementOne");
        elementWrapper.addDTDElement(dtdElement1);
        elementWrapper.addDTDElement(dtdElement2);
        ElementUnionBuilder instance = new ElementUnionBuilder(elementWrapper);

        printOut(dtdElement1, dtdElement2);

        instance.unifyElements();

        Element dtdElement = elementWrapper.getDTDElements().iterator().next();

        //--------------------------------------------------------------------//
        // Result:
        //--------------------------------------------------------------------//

        printResult(dtdElement);

        assertEquals("element", dtdElement.getName());
        assertTrue(dtdElement.getMixed());
        assertFalse(dtdElement.isEmpty());
        assertFalse(dtdElement.hasAnyType());
        assertTrue(dtdElement.getAttributes().isEmpty());
        assertTrue(dtdElement.getParticle() instanceof CountingPattern);

        CountingPattern countingPattern1 = (CountingPattern) dtdElement.getParticle();
        assertTrue(countingPattern1.getParticles().getFirst() instanceof ChoicePattern);
        ChoicePattern choicePattern1 = (ChoicePattern) countingPattern1.getParticles().getFirst();
        assertTrue(choicePattern1.getParticles().getFirst() instanceof eu.fox7.schematoolkit.common.dtd.ElementRef);

        Element element3 = ((eu.fox7.schematoolkit.common.dtd.ElementRef) choicePattern1.getParticles().getFirst()).getElement();
        assertEquals("element", element3.getName());
    }

    /**
     * Test of unifyElements method, of class ElementUnionBuilder.
     * @throws Exception
     */
    @Test
    public void testUnifyElements_EMPTY_counting_questionMark() throws Exception {

        Boolean element1mixed = false;
        Boolean element2mixed = false;
        Particle dtdElement1particle = null;
        CountingPattern countingPattern = new CountingPattern(0, 1);
        countingPattern.addParticle(new eu.fox7.schematoolkit.common.dtd.ElementRef(dtd.getElementSymbolTable().updateOrCreateReference("element", new Element("element"))));
        Particle dtdElement2particle = countingPattern;

        //--------------------------------------------------------------------//

        Element dtdElement1 = new Element("element");
        dtdElement1.setMixed(element1mixed);
        dtdElement1.setParticle(dtdElement1particle);
        Element dtdElement2 = new Element("element");
        dtdElement2.setMixed(element2mixed);
        dtdElement2.setParticle(dtdElement2particle);
        ElementWrapper elementWrapper = new ElementWrapper(schema, "elementOne");
        elementWrapper.addDTDElement(dtdElement1);
        elementWrapper.addDTDElement(dtdElement2);
        ElementUnionBuilder instance = new ElementUnionBuilder(elementWrapper);

        printOut(dtdElement1, dtdElement2);

        instance.unifyElements();

        Element dtdElement = elementWrapper.getDTDElements().iterator().next();

        //--------------------------------------------------------------------//
        // Result:
        //--------------------------------------------------------------------//

        printResult(dtdElement);

        assertEquals("element", dtdElement.getName());
        assertFalse(dtdElement.getMixed());
        assertFalse(dtdElement.isEmpty());
        assertFalse(dtdElement.hasAnyType());
        assertTrue(dtdElement.getAttributes().isEmpty());
        assertTrue(dtdElement.getParticle() instanceof CountingPattern);

        CountingPattern countingPattern1 = (CountingPattern) dtdElement.getParticle();
        assertEquals(0, countingPattern1.getMin().intValue());
        assertEquals(1, countingPattern1.getMax().intValue());
        assertTrue(countingPattern1.getParticles().getFirst() instanceof eu.fox7.schematoolkit.common.dtd.ElementRef);

        Element element3 = ((eu.fox7.schematoolkit.common.dtd.ElementRef) countingPattern1.getParticles().getFirst()).getElement();
        assertEquals("element", element3.getName());
    }

    /**
     * Test of unifyElements method, of class ElementUnionBuilder.
     * @throws Exception
     */
    @Test
    public void testUnifyElements_EMPTY_counting_star() throws Exception {

        Boolean element1mixed = false;
        Boolean element2mixed = false;
        Particle dtdElement1particle = null;
        CountingPattern countingPattern = new CountingPattern(0, null);
        countingPattern.addParticle(new eu.fox7.schematoolkit.common.dtd.ElementRef(dtd.getElementSymbolTable().updateOrCreateReference("element", new Element("element"))));
        Particle dtdElement2particle = countingPattern;

        //--------------------------------------------------------------------//

        Element dtdElement1 = new Element("element");
        dtdElement1.setMixed(element1mixed);
        dtdElement1.setParticle(dtdElement1particle);
        Element dtdElement2 = new Element("element");
        dtdElement2.setMixed(element2mixed);
        dtdElement2.setParticle(dtdElement2particle);
        ElementWrapper elementWrapper = new ElementWrapper(schema, "elementOne");
        elementWrapper.addDTDElement(dtdElement1);
        elementWrapper.addDTDElement(dtdElement2);
        ElementUnionBuilder instance = new ElementUnionBuilder(elementWrapper);

        printOut(dtdElement1, dtdElement2);

        instance.unifyElements();

        Element dtdElement = elementWrapper.getDTDElements().iterator().next();

        //--------------------------------------------------------------------//
        // Result:
        //--------------------------------------------------------------------//

        printResult(dtdElement);

        assertEquals("element", dtdElement.getName());
        assertFalse(dtdElement.getMixed());
        assertFalse(dtdElement.isEmpty());
        assertFalse(dtdElement.hasAnyType());
        assertTrue(dtdElement.getAttributes().isEmpty());
        assertTrue(dtdElement.getParticle() instanceof CountingPattern);

        CountingPattern countingPattern1 = (CountingPattern) dtdElement.getParticle();
        assertEquals(0, countingPattern1.getMin().intValue());
        assertEquals(null, countingPattern1.getMax());
        assertTrue(countingPattern1.getParticles().getFirst() instanceof eu.fox7.schematoolkit.common.dtd.ElementRef);

        Element element3 = ((eu.fox7.schematoolkit.common.dtd.ElementRef) countingPattern1.getParticles().getFirst()).getElement();
        assertEquals("element", element3.getName());
    }

    /**
     * Test of unifyElements method, of class ElementUnionBuilder.
     * @throws Exception
     */
    @Test
    public void testUnifyElements_EMPTY_counting_plus() throws Exception {

        Boolean element1mixed = false;
        Boolean element2mixed = false;
        Particle dtdElement1particle = null;
        CountingPattern countingPattern = new CountingPattern(1, null);
        countingPattern.addParticle(new eu.fox7.schematoolkit.common.dtd.ElementRef(dtd.getElementSymbolTable().updateOrCreateReference("element", new Element("element"))));
        Particle dtdElement2particle = countingPattern;

        //--------------------------------------------------------------------//

        Element dtdElement1 = new Element("element");
        dtdElement1.setMixed(element1mixed);
        dtdElement1.setParticle(dtdElement1particle);
        Element dtdElement2 = new Element("element");
        dtdElement2.setMixed(element2mixed);
        dtdElement2.setParticle(dtdElement2particle);
        ElementWrapper elementWrapper = new ElementWrapper(schema, "elementOne");
        elementWrapper.addDTDElement(dtdElement1);
        elementWrapper.addDTDElement(dtdElement2);
        ElementUnionBuilder instance = new ElementUnionBuilder(elementWrapper);

        printOut(dtdElement1, dtdElement2);

        instance.unifyElements();

        Element dtdElement = elementWrapper.getDTDElements().iterator().next();

        //--------------------------------------------------------------------//
        // Result:
        //--------------------------------------------------------------------//

        printResult(dtdElement);

        assertEquals("element", dtdElement.getName());
        assertFalse(dtdElement.getMixed());
        assertFalse(dtdElement.isEmpty());
        assertFalse(dtdElement.hasAnyType());
        assertTrue(dtdElement.getAttributes().isEmpty());
        assertTrue(dtdElement.getParticle() instanceof CountingPattern);

        CountingPattern countingPattern1 = (CountingPattern) dtdElement.getParticle();
        assertEquals(0, countingPattern1.getMin().intValue());
        assertEquals(null, countingPattern1.getMax());
        assertTrue(countingPattern1.getParticles().getFirst() instanceof eu.fox7.schematoolkit.common.dtd.ElementRef);

        Element element3 = ((eu.fox7.schematoolkit.common.dtd.ElementRef) countingPattern1.getParticles().getFirst()).getElement();
        assertEquals("element", element3.getName());
    }

    /**
     * Test of unifyElements method, of class ElementUnionBuilder.
     * @throws Exception
     */
    @Test
    public void testUnifyElements_EMPTY_complex_content() throws Exception {

        Boolean element1mixed = false;
        Boolean element2mixed = false;
        Particle dtdElement1particle = null;
        SequencePattern sequencePattern = new SequencePattern();
        sequencePattern.addParticle(new eu.fox7.schematoolkit.common.dtd.ElementRef(dtd.getElementSymbolTable().updateOrCreateReference("element", new Element("element"))));
        sequencePattern.addParticle(new eu.fox7.schematoolkit.common.dtd.ElementRef(dtd.getElementSymbolTable().updateOrCreateReference("element2", new Element("element2"))));
        Particle dtdElement2particle = sequencePattern;

        //--------------------------------------------------------------------//

        Element dtdElement1 = new Element("element");
        dtdElement1.setMixed(element1mixed);
        dtdElement1.setParticle(dtdElement1particle);
        Element dtdElement2 = new Element("element");
        dtdElement2.setMixed(element2mixed);
        dtdElement2.setParticle(dtdElement2particle);
        ElementWrapper elementWrapper = new ElementWrapper(schema, "elementOne");
        elementWrapper.addDTDElement(dtdElement1);
        elementWrapper.addDTDElement(dtdElement2);
        ElementUnionBuilder instance = new ElementUnionBuilder(elementWrapper);

        printOut(dtdElement1, dtdElement2);

        instance.unifyElements();

        Element dtdElement = elementWrapper.getDTDElements().iterator().next();

        //--------------------------------------------------------------------//
        // Result:
        //--------------------------------------------------------------------//

        printResult(dtdElement);

        assertEquals("element", dtdElement.getName());
        assertFalse(dtdElement.getMixed());
        assertFalse(dtdElement.isEmpty());
        assertFalse(dtdElement.hasAnyType());
        assertTrue(dtdElement.getAttributes().isEmpty());
        assertTrue(dtdElement.getParticle() instanceof CountingPattern);

        CountingPattern countingPattern1 = (CountingPattern) dtdElement.getParticle();
        assertEquals(0, countingPattern1.getMin().intValue());
        assertEquals(1, countingPattern1.getMax().intValue());

        assertTrue(countingPattern1.getParticles().getFirst() instanceof SequencePattern);
        SequencePattern sequencePattern1 = (SequencePattern) countingPattern1.getParticles().getFirst();

        assertTrue(sequencePattern1.getParticles().getFirst() instanceof eu.fox7.schematoolkit.common.dtd.ElementRef);

        Element element3 = ((eu.fox7.schematoolkit.common.dtd.ElementRef) sequencePattern1.getParticles().getFirst()).getElement();
        assertEquals("element", element3.getName());

        assertTrue(sequencePattern1.getParticles().getLast() instanceof eu.fox7.schematoolkit.common.dtd.ElementRef);

        Element element4 = ((eu.fox7.schematoolkit.common.dtd.ElementRef) sequencePattern1.getParticles().getLast()).getElement();
        assertEquals("element2", element4.getName());
    }

    // ANY
    /**
     * Test of unifyElements method, of class ElementUnionBuilder.
     * @throws Exception
     */
    @Test
    public void testUnifyElements_ANY_ANY() throws Exception {

        Boolean element1mixed = false;
        Boolean element2mixed = false;
        Particle dtdElement1particle = new AnyPattern(ProcessContentsInstruction.STRICT, null);
        Particle dtdElement2particle = new AnyPattern(ProcessContentsInstruction.STRICT, null);

        //--------------------------------------------------------------------//

        Element dtdElement1 = new Element("element");
        dtdElement1.setMixed(element1mixed);
        dtdElement1.setParticle(dtdElement1particle);
        Element dtdElement2 = new Element("element");
        dtdElement2.setMixed(element2mixed);
        dtdElement2.setParticle(dtdElement2particle);
        ElementWrapper elementWrapper = new ElementWrapper(schema, "elementOne");
        elementWrapper.addDTDElement(dtdElement1);
        elementWrapper.addDTDElement(dtdElement2);
        ElementUnionBuilder instance = new ElementUnionBuilder(elementWrapper);

        printOut(dtdElement1, dtdElement2);

        instance.unifyElements();

        Element dtdElement = elementWrapper.getDTDElements().iterator().next();

        //--------------------------------------------------------------------//
        // Result:
        //--------------------------------------------------------------------//

        printResult(dtdElement);

        assertEquals("element", dtdElement.getName());
        assertFalse(dtdElement.getMixed());
        assertFalse(dtdElement.isEmpty());
        assertTrue(dtdElement.hasAnyType());
        assertTrue(dtdElement.getAttributes().isEmpty());
        assertTrue(dtdElement.getParticle() instanceof AnyPattern);
    }

    /**
     * Test of unifyElements method, of class ElementUnionBuilder.
     * @throws Exception
     */
    @Test
    public void testUnifyElements_ANY_mixed() throws Exception {

        Boolean element1mixed = false;
        Boolean element2mixed = true;
        Particle dtdElement1particle = new AnyPattern(ProcessContentsInstruction.STRICT, null);
        Particle dtdElement2particle = null;

        //--------------------------------------------------------------------//

        Element dtdElement1 = new Element("element");
        dtdElement1.setMixed(element1mixed);
        dtdElement1.setParticle(dtdElement1particle);
        Element dtdElement2 = new Element("element");
        dtdElement2.setMixed(element2mixed);
        dtdElement2.setParticle(dtdElement2particle);
        ElementWrapper elementWrapper = new ElementWrapper(schema, "elementOne");
        elementWrapper.addDTDElement(dtdElement1);
        elementWrapper.addDTDElement(dtdElement2);
        ElementUnionBuilder instance = new ElementUnionBuilder(elementWrapper);

        printOut(dtdElement1, dtdElement2);

        instance.unifyElements();

        Element dtdElement = elementWrapper.getDTDElements().iterator().next();

        //--------------------------------------------------------------------//
        // Result:
        //--------------------------------------------------------------------//

        printResult(dtdElement);

        assertEquals("element", dtdElement.getName());
        assertFalse(dtdElement.getMixed());
        assertFalse(dtdElement.isEmpty());
        assertTrue(dtdElement.hasAnyType());
        assertTrue(dtdElement.getAttributes().isEmpty());
        assertTrue(dtdElement.getParticle() instanceof AnyPattern);
    }

    /**
     * Test of unifyElements method, of class ElementUnionBuilder.
     * @throws Exception
     */
    @Test
    public void testUnifyElements_ANY_mixed_content() throws Exception {

        Boolean element1mixed = false;
        Boolean element2mixed = true;
        Particle dtdElement1particle = new AnyPattern(ProcessContentsInstruction.STRICT, null);
        CountingPattern countingPattern = new CountingPattern(0, null);
        ChoicePattern choicePattern = new ChoicePattern();
        choicePattern.addParticle(new eu.fox7.schematoolkit.common.dtd.ElementRef(dtd.getElementSymbolTable().updateOrCreateReference("element", new Element("element"))));
        countingPattern.addParticle(choicePattern);
        Particle dtdElement2particle = countingPattern;

        //--------------------------------------------------------------------//

        Element dtdElement1 = new Element("element");
        dtdElement1.setMixed(element1mixed);
        dtdElement1.setParticle(dtdElement1particle);
        Element dtdElement2 = new Element("element");
        dtdElement2.setMixed(element2mixed);
        dtdElement2.setParticle(dtdElement2particle);
        ElementWrapper elementWrapper = new ElementWrapper(schema, "elementOne");
        elementWrapper.addDTDElement(dtdElement1);
        elementWrapper.addDTDElement(dtdElement2);
        ElementUnionBuilder instance = new ElementUnionBuilder(elementWrapper);

        printOut(dtdElement1, dtdElement2);

        instance.unifyElements();

        Element dtdElement = elementWrapper.getDTDElements().iterator().next();

        //--------------------------------------------------------------------//
        // Result:
        //--------------------------------------------------------------------//

        printResult(dtdElement);

        assertEquals("element", dtdElement.getName());
        assertFalse(dtdElement.getMixed());
        assertFalse(dtdElement.isEmpty());
        assertTrue(dtdElement.hasAnyType());
        assertTrue(dtdElement.getAttributes().isEmpty());
        assertTrue(dtdElement.getParticle() instanceof AnyPattern);
    }

    /**
     * Test of unifyElements method, of class ElementUnionBuilder.
     * @throws Exception
     */
    @Test
    public void testUnifyElements_ANY_counting_questionMark() throws Exception {

        Boolean element1mixed = false;
        Boolean element2mixed = false;
        Particle dtdElement1particle = new AnyPattern(ProcessContentsInstruction.STRICT, null);
        CountingPattern countingPattern = new CountingPattern(0, 1);
        countingPattern.addParticle(new eu.fox7.schematoolkit.common.dtd.ElementRef(dtd.getElementSymbolTable().updateOrCreateReference("element", new Element("element"))));
        Particle dtdElement2particle = countingPattern;

        //--------------------------------------------------------------------//

        Element dtdElement1 = new Element("element");
        dtdElement1.setMixed(element1mixed);
        dtdElement1.setParticle(dtdElement1particle);
        Element dtdElement2 = new Element("element");
        dtdElement2.setMixed(element2mixed);
        dtdElement2.setParticle(dtdElement2particle);
        ElementWrapper elementWrapper = new ElementWrapper(schema, "elementOne");
        elementWrapper.addDTDElement(dtdElement1);
        elementWrapper.addDTDElement(dtdElement2);
        ElementUnionBuilder instance = new ElementUnionBuilder(elementWrapper);

        printOut(dtdElement1, dtdElement2);

        instance.unifyElements();

        Element dtdElement = elementWrapper.getDTDElements().iterator().next();

        //--------------------------------------------------------------------//
        // Result:
        //--------------------------------------------------------------------//

        printResult(dtdElement);

        assertEquals("element", dtdElement.getName());
        assertFalse(dtdElement.getMixed());
        assertFalse(dtdElement.isEmpty());
        assertTrue(dtdElement.hasAnyType());
        assertTrue(dtdElement.getAttributes().isEmpty());
        assertTrue(dtdElement.getParticle() instanceof AnyPattern);
    }

    /**
     * Test of unifyElements method, of class ElementUnionBuilder.
     * @throws Exception
     */
    @Test
    public void testUnifyElements_ANY_counting_star() throws Exception {

        Boolean element1mixed = false;
        Boolean element2mixed = false;
        Particle dtdElement1particle = new AnyPattern(ProcessContentsInstruction.STRICT, null);
        CountingPattern countingPattern = new CountingPattern(0, null);
        countingPattern.addParticle(new eu.fox7.schematoolkit.common.dtd.ElementRef(dtd.getElementSymbolTable().updateOrCreateReference("element", new Element("element"))));
        Particle dtdElement2particle = countingPattern;

        //--------------------------------------------------------------------//

        Element dtdElement1 = new Element("element");
        dtdElement1.setMixed(element1mixed);
        dtdElement1.setParticle(dtdElement1particle);
        Element dtdElement2 = new Element("element");
        dtdElement2.setMixed(element2mixed);
        dtdElement2.setParticle(dtdElement2particle);
        ElementWrapper elementWrapper = new ElementWrapper(schema, "elementOne");
        elementWrapper.addDTDElement(dtdElement1);
        elementWrapper.addDTDElement(dtdElement2);
        ElementUnionBuilder instance = new ElementUnionBuilder(elementWrapper);

        printOut(dtdElement1, dtdElement2);

        instance.unifyElements();

        Element dtdElement = elementWrapper.getDTDElements().iterator().next();

        //--------------------------------------------------------------------//
        // Result:
        //--------------------------------------------------------------------//

        printResult(dtdElement);

        assertEquals("element", dtdElement.getName());
        assertFalse(dtdElement.getMixed());
        assertFalse(dtdElement.isEmpty());
        assertTrue(dtdElement.hasAnyType());
        assertTrue(dtdElement.getAttributes().isEmpty());
        assertTrue(dtdElement.getParticle() instanceof AnyPattern);
    }

    /**
     * Test of unifyElements method, of class ElementUnionBuilder.
     * @throws Exception
     */
    @Test
    public void testUnifyElements_ANY_counting_plus() throws Exception {

        Boolean element1mixed = false;
        Boolean element2mixed = false;
        Particle dtdElement1particle = new AnyPattern(ProcessContentsInstruction.STRICT, null);
        CountingPattern countingPattern = new CountingPattern(1, null);
        countingPattern.addParticle(new eu.fox7.schematoolkit.common.dtd.ElementRef(dtd.getElementSymbolTable().updateOrCreateReference("element", new Element("element"))));
        Particle dtdElement2particle = countingPattern;

        //--------------------------------------------------------------------//

        Element dtdElement1 = new Element("element");
        dtdElement1.setMixed(element1mixed);
        dtdElement1.setParticle(dtdElement1particle);
        Element dtdElement2 = new Element("element");
        dtdElement2.setMixed(element2mixed);
        dtdElement2.setParticle(dtdElement2particle);
        ElementWrapper elementWrapper = new ElementWrapper(schema, "elementOne");
        elementWrapper.addDTDElement(dtdElement1);
        elementWrapper.addDTDElement(dtdElement2);
        ElementUnionBuilder instance = new ElementUnionBuilder(elementWrapper);

        printOut(dtdElement1, dtdElement2);

        instance.unifyElements();

        Element dtdElement = elementWrapper.getDTDElements().iterator().next();

        //--------------------------------------------------------------------//
        // Result:
        //--------------------------------------------------------------------//

        printResult(dtdElement);

        assertEquals("element", dtdElement.getName());
        assertFalse(dtdElement.getMixed());
        assertFalse(dtdElement.isEmpty());
        assertTrue(dtdElement.hasAnyType());
        assertTrue(dtdElement.getAttributes().isEmpty());
        assertTrue(dtdElement.getParticle() instanceof AnyPattern);
    }

    /**
     * Test of unifyElements method, of class ElementUnionBuilder.
     * @throws Exception
     */
    @Test
    public void testUnifyElements_ANY_complex_content() throws Exception {

        Boolean element1mixed = false;
        Boolean element2mixed = false;
        Particle dtdElement1particle = new AnyPattern(ProcessContentsInstruction.STRICT, null);
        SequencePattern sequencePattern = new SequencePattern();
        sequencePattern.addParticle(new eu.fox7.schematoolkit.common.dtd.ElementRef(dtd.getElementSymbolTable().updateOrCreateReference("element", new Element("element"))));
        sequencePattern.addParticle(new eu.fox7.schematoolkit.common.dtd.ElementRef(dtd.getElementSymbolTable().updateOrCreateReference("element2", new Element("element2"))));
        Particle dtdElement2particle = sequencePattern;

        //--------------------------------------------------------------------//

        Element dtdElement1 = new Element("element");
        dtdElement1.setMixed(element1mixed);
        dtdElement1.setParticle(dtdElement1particle);
        Element dtdElement2 = new Element("element");
        dtdElement2.setMixed(element2mixed);
        dtdElement2.setParticle(dtdElement2particle);
        ElementWrapper elementWrapper = new ElementWrapper(schema, "elementOne");
        elementWrapper.addDTDElement(dtdElement1);
        elementWrapper.addDTDElement(dtdElement2);
        ElementUnionBuilder instance = new ElementUnionBuilder(elementWrapper);

        printOut(dtdElement1, dtdElement2);

        instance.unifyElements();

        Element dtdElement = elementWrapper.getDTDElements().iterator().next();

        //--------------------------------------------------------------------//
        // Result:
        //--------------------------------------------------------------------//

        printResult(dtdElement);

        assertEquals("element", dtdElement.getName());
        assertFalse(dtdElement.getMixed());
        assertFalse(dtdElement.isEmpty());
        assertTrue(dtdElement.hasAnyType());
        assertTrue(dtdElement.getAttributes().isEmpty());
        assertTrue(dtdElement.getParticle() instanceof AnyPattern);
    }

    // Mixed without content
    /**
     * Test of unifyElements method, of class ElementUnionBuilder.
     * @throws Exception
     */
    @Test
    public void testUnifyElements_mixed_mixed() throws Exception {

        Boolean element1mixed = true;
        Boolean element2mixed = true;
        Particle dtdElement1particle = null;
        Particle dtdElement2particle = null;

        //--------------------------------------------------------------------//

        Element dtdElement1 = new Element("element");
        dtdElement1.setMixed(element1mixed);
        dtdElement1.setParticle(dtdElement1particle);
        Element dtdElement2 = new Element("element");
        dtdElement2.setMixed(element2mixed);
        dtdElement2.setParticle(dtdElement2particle);
        ElementWrapper elementWrapper = new ElementWrapper(schema, "elementOne");
        elementWrapper.addDTDElement(dtdElement1);
        elementWrapper.addDTDElement(dtdElement2);
        ElementUnionBuilder instance = new ElementUnionBuilder(elementWrapper);

        printOut(dtdElement1, dtdElement2);

        instance.unifyElements();

        Element dtdElement = elementWrapper.getDTDElements().iterator().next();

        //--------------------------------------------------------------------//
        // Result:
        //--------------------------------------------------------------------//

        printResult(dtdElement);

        assertEquals("element", dtdElement.getName());
        assertTrue(dtdElement.getMixed());
        assertTrue(dtdElement.isEmpty());
        assertFalse(dtdElement.hasAnyType());
        assertTrue(dtdElement.getAttributes().isEmpty());
        assertTrue(dtdElement.getParticle() == null);
    }

    /**
     * Test of unifyElements method, of class ElementUnionBuilder.
     * @throws Exception
     */
    @Test
    public void testUnifyElements_mixed_mixed_content() throws Exception {

        Boolean element1mixed = true;
        Boolean element2mixed = true;
        Particle dtdElement1particle = null;
        CountingPattern countingPattern = new CountingPattern(0, null);
        ChoicePattern choicePattern = new ChoicePattern();
        choicePattern.addParticle(new eu.fox7.schematoolkit.common.dtd.ElementRef(dtd.getElementSymbolTable().updateOrCreateReference("element", new Element("element"))));
        countingPattern.addParticle(choicePattern);
        Particle dtdElement2particle = countingPattern;

        //--------------------------------------------------------------------//

        Element dtdElement1 = new Element("element");
        dtdElement1.setMixed(element1mixed);
        dtdElement1.setParticle(dtdElement1particle);
        Element dtdElement2 = new Element("element");
        dtdElement2.setMixed(element2mixed);
        dtdElement2.setParticle(dtdElement2particle);
        ElementWrapper elementWrapper = new ElementWrapper(schema, "elementOne");
        elementWrapper.addDTDElement(dtdElement1);
        elementWrapper.addDTDElement(dtdElement2);
        ElementUnionBuilder instance = new ElementUnionBuilder(elementWrapper);

        printOut(dtdElement1, dtdElement2);

        instance.unifyElements();

        Element dtdElement = elementWrapper.getDTDElements().iterator().next();

        //--------------------------------------------------------------------//
        // Result:
        //--------------------------------------------------------------------//

        printResult(dtdElement);

        assertEquals("element", dtdElement.getName());
        assertTrue(dtdElement.getMixed());
        assertFalse(dtdElement.isEmpty());
        assertFalse(dtdElement.hasAnyType());
        assertTrue(dtdElement.getAttributes().isEmpty());
        assertTrue(dtdElement.getParticle() instanceof CountingPattern);

        CountingPattern countingPattern1 = (CountingPattern) dtdElement.getParticle();
        assertEquals(0, countingPattern1.getMin().intValue());
        assertEquals(null, countingPattern1.getMax());
        assertTrue(countingPattern1.getParticles().getFirst() instanceof ChoicePattern);
        ChoicePattern choicePattern1 = (ChoicePattern) countingPattern1.getParticles().getFirst();
        assertTrue(choicePattern1.getParticles().getFirst() instanceof eu.fox7.schematoolkit.common.dtd.ElementRef);

        Element element3 = ((eu.fox7.schematoolkit.common.dtd.ElementRef) choicePattern1.getParticles().getFirst()).getElement();
        assertEquals("element", element3.getName());
    }

    /**
     * Test of unifyElements method, of class ElementUnionBuilder.
     * @throws Exception
     */
    @Test
    public void testUnifyElements_mixed_counting_questionMark() throws Exception {

        Boolean element1mixed = true;
        Boolean element2mixed = false;
        Particle dtdElement1particle = null;
        CountingPattern countingPattern = new CountingPattern(0, 1);
        countingPattern.addParticle(new eu.fox7.schematoolkit.common.dtd.ElementRef(dtd.getElementSymbolTable().updateOrCreateReference("element", new Element("element"))));
        Particle dtdElement2particle = countingPattern;

        //--------------------------------------------------------------------//

        Element dtdElement1 = new Element("element");
        dtdElement1.setMixed(element1mixed);
        dtdElement1.setParticle(dtdElement1particle);
        Element dtdElement2 = new Element("element");
        dtdElement2.setMixed(element2mixed);
        dtdElement2.setParticle(dtdElement2particle);
        ElementWrapper elementWrapper = new ElementWrapper(schema, "elementOne");
        elementWrapper.addDTDElement(dtdElement1);
        elementWrapper.addDTDElement(dtdElement2);
        ElementUnionBuilder instance = new ElementUnionBuilder(elementWrapper);

        printOut(dtdElement1, dtdElement2);

        instance.unifyElements();

        Element dtdElement = elementWrapper.getDTDElements().iterator().next();

        //--------------------------------------------------------------------//
        // Result:
        //--------------------------------------------------------------------//

        printResult(dtdElement);

        assertEquals("element", dtdElement.getName());
        assertTrue(dtdElement.getMixed());
        assertFalse(dtdElement.isEmpty());
        assertFalse(dtdElement.hasAnyType());
        assertTrue(dtdElement.getAttributes().isEmpty());
        assertTrue(dtdElement.getParticle() instanceof CountingPattern);

        CountingPattern countingPattern1 = (CountingPattern) dtdElement.getParticle();
        assertEquals(0, countingPattern1.getMin().intValue());
        assertEquals(null, countingPattern1.getMax());
        assertTrue(countingPattern1.getParticles().getFirst() instanceof ChoicePattern);
        ChoicePattern choicePattern1 = (ChoicePattern) countingPattern1.getParticles().getFirst();
        assertTrue(choicePattern1.getParticles().getFirst() instanceof eu.fox7.schematoolkit.common.dtd.ElementRef);

        Element element3 = ((eu.fox7.schematoolkit.common.dtd.ElementRef) choicePattern1.getParticles().getFirst()).getElement();
        assertEquals("element", element3.getName());
    }

    /**
     * Test of unifyElements method, of class ElementUnionBuilder.
     * @throws Exception
     */
    @Test
    public void testUnifyElements_mixed_counting_star() throws Exception {

        Boolean element1mixed = true;
        Boolean element2mixed = false;
        Particle dtdElement1particle = null;
        CountingPattern countingPattern = new CountingPattern(0, null);
        countingPattern.addParticle(new eu.fox7.schematoolkit.common.dtd.ElementRef(dtd.getElementSymbolTable().updateOrCreateReference("element", new Element("element"))));
        Particle dtdElement2particle = countingPattern;

        //--------------------------------------------------------------------//

        Element dtdElement1 = new Element("element");
        dtdElement1.setMixed(element1mixed);
        dtdElement1.setParticle(dtdElement1particle);
        Element dtdElement2 = new Element("element");
        dtdElement2.setMixed(element2mixed);
        dtdElement2.setParticle(dtdElement2particle);
        ElementWrapper elementWrapper = new ElementWrapper(schema, "elementOne");
        elementWrapper.addDTDElement(dtdElement1);
        elementWrapper.addDTDElement(dtdElement2);
        ElementUnionBuilder instance = new ElementUnionBuilder(elementWrapper);

        printOut(dtdElement1, dtdElement2);

        instance.unifyElements();

        Element dtdElement = elementWrapper.getDTDElements().iterator().next();

        //--------------------------------------------------------------------//
        // Result:
        //--------------------------------------------------------------------//

        printResult(dtdElement);

        assertEquals("element", dtdElement.getName());
        assertTrue(dtdElement.getMixed());
        assertFalse(dtdElement.isEmpty());
        assertFalse(dtdElement.hasAnyType());
        assertTrue(dtdElement.getAttributes().isEmpty());
        assertTrue(dtdElement.getParticle() instanceof CountingPattern);

        CountingPattern countingPattern1 = (CountingPattern) dtdElement.getParticle();
        assertEquals(0, countingPattern1.getMin().intValue());
        assertEquals(null, countingPattern1.getMax());
        assertTrue(countingPattern1.getParticles().getFirst() instanceof ChoicePattern);
        ChoicePattern choicePattern1 = (ChoicePattern) countingPattern1.getParticles().getFirst();
        assertTrue(choicePattern1.getParticles().getFirst() instanceof eu.fox7.schematoolkit.common.dtd.ElementRef);

        Element element3 = ((eu.fox7.schematoolkit.common.dtd.ElementRef) choicePattern1.getParticles().getFirst()).getElement();
        assertEquals("element", element3.getName());
    }

    /**
     * Test of unifyElements method, of class ElementUnionBuilder.
     * @throws Exception
     */
    @Test
    public void testUnifyElements_mixed_counting_plus() throws Exception {

        Boolean element1mixed = true;
        Boolean element2mixed = false;
        Particle dtdElement1particle = null;
        CountingPattern countingPattern = new CountingPattern(1, null);
        countingPattern.addParticle(new eu.fox7.schematoolkit.common.dtd.ElementRef(dtd.getElementSymbolTable().updateOrCreateReference("element", new Element("element"))));
        Particle dtdElement2particle = countingPattern;

        //--------------------------------------------------------------------//

        Element dtdElement1 = new Element("element");
        dtdElement1.setMixed(element1mixed);
        dtdElement1.setParticle(dtdElement1particle);
        Element dtdElement2 = new Element("element");
        dtdElement2.setMixed(element2mixed);
        dtdElement2.setParticle(dtdElement2particle);
        ElementWrapper elementWrapper = new ElementWrapper(schema, "elementOne");
        elementWrapper.addDTDElement(dtdElement1);
        elementWrapper.addDTDElement(dtdElement2);
        ElementUnionBuilder instance = new ElementUnionBuilder(elementWrapper);

        printOut(dtdElement1, dtdElement2);

        instance.unifyElements();

        Element dtdElement = elementWrapper.getDTDElements().iterator().next();

        //--------------------------------------------------------------------//
        // Result:
        //--------------------------------------------------------------------//

        printResult(dtdElement);

        assertEquals("element", dtdElement.getName());
        assertTrue(dtdElement.getMixed());
        assertFalse(dtdElement.isEmpty());
        assertFalse(dtdElement.hasAnyType());
        assertTrue(dtdElement.getAttributes().isEmpty());
        assertTrue(dtdElement.getParticle() instanceof CountingPattern);

        CountingPattern countingPattern1 = (CountingPattern) dtdElement.getParticle();
        assertEquals(0, countingPattern1.getMin().intValue());
        assertEquals(null, countingPattern1.getMax());
        assertTrue(countingPattern1.getParticles().getFirst() instanceof ChoicePattern);
        ChoicePattern choicePattern1 = (ChoicePattern) countingPattern1.getParticles().getFirst();
        assertTrue(choicePattern1.getParticles().getFirst() instanceof eu.fox7.schematoolkit.common.dtd.ElementRef);

        Element element3 = ((eu.fox7.schematoolkit.common.dtd.ElementRef) choicePattern1.getParticles().getFirst()).getElement();
        assertEquals("element", element3.getName());
    }

    /**
     * Test of unifyElements method, of class ElementUnionBuilder.
     * @throws Exception
     */
    @Test
    public void testUnifyElements_mixed_complex_content() throws Exception {

        Boolean element1mixed = true;
        Boolean element2mixed = false;
        Particle dtdElement1particle = null;
        SequencePattern sequencePattern = new SequencePattern();
        sequencePattern.addParticle(new eu.fox7.schematoolkit.common.dtd.ElementRef(dtd.getElementSymbolTable().updateOrCreateReference("element", new Element("element"))));
        sequencePattern.addParticle(new eu.fox7.schematoolkit.common.dtd.ElementRef(dtd.getElementSymbolTable().updateOrCreateReference("element2", new Element("element2"))));
        Particle dtdElement2particle = sequencePattern;

        //--------------------------------------------------------------------//

        Element dtdElement1 = new Element("element");
        dtdElement1.setMixed(element1mixed);
        dtdElement1.setParticle(dtdElement1particle);
        Element dtdElement2 = new Element("element");
        dtdElement2.setMixed(element2mixed);
        dtdElement2.setParticle(dtdElement2particle);
        ElementWrapper elementWrapper = new ElementWrapper(schema, "elementOne");
        elementWrapper.addDTDElement(dtdElement1);
        elementWrapper.addDTDElement(dtdElement2);
        ElementUnionBuilder instance = new ElementUnionBuilder(elementWrapper);

        printOut(dtdElement1, dtdElement2);

        instance.unifyElements();

        Element dtdElement = elementWrapper.getDTDElements().iterator().next();

        //--------------------------------------------------------------------//
        // Result:
        //--------------------------------------------------------------------//

        printResult(dtdElement);

        assertEquals("element", dtdElement.getName());
        assertTrue(dtdElement.getMixed());
        assertFalse(dtdElement.isEmpty());
        assertFalse(dtdElement.hasAnyType());
        assertTrue(dtdElement.getAttributes().isEmpty());
        assertTrue(dtdElement.getParticle() instanceof CountingPattern);

        CountingPattern countingPattern1 = (CountingPattern) dtdElement.getParticle();
        assertEquals(0, countingPattern1.getMin().intValue());
        assertEquals(null, countingPattern1.getMax());

        ChoicePattern choicePattern1 = (ChoicePattern) countingPattern1.getParticles().getFirst();
        assertTrue(choicePattern1.getParticles().getFirst() instanceof eu.fox7.schematoolkit.common.dtd.ElementRef);

        Element element3 = ((eu.fox7.schematoolkit.common.dtd.ElementRef) choicePattern1.getParticles().getFirst()).getElement();
        assertEquals("element", element3.getName());

        assertTrue(choicePattern1.getParticles().getLast() instanceof eu.fox7.schematoolkit.common.dtd.ElementRef);

        Element element4 = ((eu.fox7.schematoolkit.common.dtd.ElementRef) choicePattern1.getParticles().getLast()).getElement();
        assertEquals("element2", element4.getName());
    }

    // Mixed with content
    /**
     * Test of unifyElements method, of class ElementUnionBuilder.
     * @throws Exception
     */
    @Test
    public void testUnifyElements_mixed_content_mixed_content() throws Exception {

        Boolean element1mixed = true;

        CountingPattern countingPattern = new CountingPattern(0, null);
        ChoicePattern choicePattern = new ChoicePattern();
        choicePattern.addParticle(new eu.fox7.schematoolkit.common.dtd.ElementRef(dtd.getElementSymbolTable().updateOrCreateReference("element1", new Element("element1"))));
        countingPattern.addParticle(choicePattern);
        Particle dtdElement1particle = countingPattern;

        Boolean element2mixed = true;

        CountingPattern countingPattern2 = new CountingPattern(0, null);
        ChoicePattern choicePattern2 = new ChoicePattern();
        choicePattern2.addParticle(new eu.fox7.schematoolkit.common.dtd.ElementRef(dtd.getElementSymbolTable().updateOrCreateReference("element2", new Element("element2"))));
        countingPattern2.addParticle(choicePattern2);
        Particle dtdElement2particle = countingPattern2;

        //--------------------------------------------------------------------//

        Element dtdElement1 = new Element("element");
        dtdElement1.setMixed(element1mixed);
        dtdElement1.setParticle(dtdElement1particle);
        Element dtdElement2 = new Element("element");
        dtdElement2.setMixed(element2mixed);
        dtdElement2.setParticle(dtdElement2particle);
        ElementWrapper elementWrapper = new ElementWrapper(schema, "elementOne");
        elementWrapper.addDTDElement(dtdElement1);
        elementWrapper.addDTDElement(dtdElement2);
        ElementUnionBuilder instance = new ElementUnionBuilder(elementWrapper);

        printOut(dtdElement1, dtdElement2);

        instance.unifyElements();

        Element dtdElement = elementWrapper.getDTDElements().iterator().next();

        //--------------------------------------------------------------------//
        // Result:
        //--------------------------------------------------------------------//

        printResult(dtdElement);

        assertEquals("element", dtdElement.getName());
        assertTrue(dtdElement.getMixed());
        assertFalse(dtdElement.isEmpty());
        assertFalse(dtdElement.hasAnyType());
        assertTrue(dtdElement.getAttributes().isEmpty());
        assertTrue(dtdElement.getParticle() instanceof CountingPattern);

        CountingPattern countingPattern1 = (CountingPattern) dtdElement.getParticle();
        assertEquals(0, countingPattern1.getMin().intValue());
        assertEquals(null, countingPattern1.getMax());
        assertTrue(countingPattern1.getParticles().getFirst() instanceof ChoicePattern);
        ChoicePattern choicePattern1 = (ChoicePattern) countingPattern1.getParticles().getFirst();
        assertTrue(choicePattern1.getParticles().getFirst() instanceof eu.fox7.schematoolkit.common.dtd.ElementRef);

        Element element3 = ((eu.fox7.schematoolkit.common.dtd.ElementRef) choicePattern1.getParticles().getFirst()).getElement();
        assertEquals("element2", element3.getName());
    }

    /**
     * Test of unifyElements method, of class ElementUnionBuilder.
     * @throws Exception
     */
    @Test
    public void testUnifyElements_mixed_content_counting_questionMark() throws Exception {

        Boolean element1mixed = true;

        CountingPattern countingPattern = new CountingPattern(0, null);
        ChoicePattern choicePattern = new ChoicePattern();
        choicePattern.addParticle(new eu.fox7.schematoolkit.common.dtd.ElementRef(dtd.getElementSymbolTable().updateOrCreateReference("element1", new Element("element1"))));
        countingPattern.addParticle(choicePattern);
        Particle dtdElement1particle = countingPattern;

        Boolean element2mixed = false;

        CountingPattern countingPattern2 = new CountingPattern(0, 1);
        countingPattern2.addParticle(new eu.fox7.schematoolkit.common.dtd.ElementRef(dtd.getElementSymbolTable().updateOrCreateReference("element2", new Element("element2"))));
        Particle dtdElement2particle = countingPattern2;

        //--------------------------------------------------------------------//

        Element dtdElement1 = new Element("element");
        dtdElement1.setMixed(element1mixed);
        dtdElement1.setParticle(dtdElement1particle);
        Element dtdElement2 = new Element("element");
        dtdElement2.setMixed(element2mixed);
        dtdElement2.setParticle(dtdElement2particle);
        ElementWrapper elementWrapper = new ElementWrapper(schema, "elementOne");
        elementWrapper.addDTDElement(dtdElement1);
        elementWrapper.addDTDElement(dtdElement2);
        ElementUnionBuilder instance = new ElementUnionBuilder(elementWrapper);

        printOut(dtdElement1, dtdElement2);

        instance.unifyElements();

        Element dtdElement = elementWrapper.getDTDElements().iterator().next();

        //--------------------------------------------------------------------//
        // Result:
        //--------------------------------------------------------------------//

        printResult(dtdElement);

        assertEquals("element", dtdElement.getName());
        assertTrue(dtdElement.getMixed());
        assertFalse(dtdElement.isEmpty());
        assertFalse(dtdElement.hasAnyType());
        assertTrue(dtdElement.getAttributes().isEmpty());
        assertTrue(dtdElement.getParticle() instanceof CountingPattern);

        CountingPattern countingPattern1 = (CountingPattern) dtdElement.getParticle();
        assertEquals(0, countingPattern1.getMin().intValue());
        assertEquals(null, countingPattern1.getMax());
        assertTrue(countingPattern1.getParticles().getFirst() instanceof ChoicePattern);
        ChoicePattern choicePattern1 = (ChoicePattern) countingPattern1.getParticles().getFirst();
        assertTrue(choicePattern1.getParticles().getFirst() instanceof eu.fox7.schematoolkit.common.dtd.ElementRef);

        Element element3 = ((eu.fox7.schematoolkit.common.dtd.ElementRef) choicePattern1.getParticles().getFirst()).getElement();
        assertEquals("element1", element3.getName());
    }

    /**
     * Test of unifyElements method, of class ElementUnionBuilder.
     * @throws Exception
     */
    @Test
    public void testUnifyElements_mixed_content_counting_star() throws Exception {

        Boolean element1mixed = true;

        CountingPattern countingPattern = new CountingPattern(0, null);
        ChoicePattern choicePattern = new ChoicePattern();
        choicePattern.addParticle(new eu.fox7.schematoolkit.common.dtd.ElementRef(dtd.getElementSymbolTable().updateOrCreateReference("element1", new Element("element1"))));
        countingPattern.addParticle(choicePattern);
        Particle dtdElement1particle = countingPattern;

        Boolean element2mixed = false;

        CountingPattern countingPattern2 = new CountingPattern(0, null);
        countingPattern2.addParticle(new eu.fox7.schematoolkit.common.dtd.ElementRef(dtd.getElementSymbolTable().updateOrCreateReference("element2", new Element("element2"))));
        Particle dtdElement2particle = countingPattern2;

        //--------------------------------------------------------------------//

        Element dtdElement1 = new Element("element");
        dtdElement1.setMixed(element1mixed);
        dtdElement1.setParticle(dtdElement1particle);
        Element dtdElement2 = new Element("element");
        dtdElement2.setMixed(element2mixed);
        dtdElement2.setParticle(dtdElement2particle);
        ElementWrapper elementWrapper = new ElementWrapper(schema, "elementOne");
        elementWrapper.addDTDElement(dtdElement1);
        elementWrapper.addDTDElement(dtdElement2);
        ElementUnionBuilder instance = new ElementUnionBuilder(elementWrapper);

        printOut(dtdElement1, dtdElement2);

        instance.unifyElements();

        Element dtdElement = elementWrapper.getDTDElements().iterator().next();

        //--------------------------------------------------------------------//
        // Result:
        //--------------------------------------------------------------------//

        printResult(dtdElement);

        assertEquals("element", dtdElement.getName());
        assertTrue(dtdElement.getMixed());
        assertFalse(dtdElement.isEmpty());
        assertFalse(dtdElement.hasAnyType());
        assertTrue(dtdElement.getAttributes().isEmpty());
        assertTrue(dtdElement.getParticle() instanceof CountingPattern);

        CountingPattern countingPattern1 = (CountingPattern) dtdElement.getParticle();
        assertEquals(0, countingPattern1.getMin().intValue());
        assertEquals(null, countingPattern1.getMax());
        assertTrue(countingPattern1.getParticles().getFirst() instanceof ChoicePattern);
        ChoicePattern choicePattern1 = (ChoicePattern) countingPattern1.getParticles().getFirst();
        assertTrue(choicePattern1.getParticles().getFirst() instanceof eu.fox7.schematoolkit.common.dtd.ElementRef);

        Element element3 = ((eu.fox7.schematoolkit.common.dtd.ElementRef) choicePattern1.getParticles().getFirst()).getElement();
        assertEquals("element1", element3.getName());
    }

    /**
     * Test of unifyElements method, of class ElementUnionBuilder.
     * @throws Exception
     */
    @Test
    public void testUnifyElements_mixed_content_counting_plus() throws Exception {

        Boolean element1mixed = true;

        CountingPattern countingPattern = new CountingPattern(0, null);
        ChoicePattern choicePattern = new ChoicePattern();
        choicePattern.addParticle(new eu.fox7.schematoolkit.common.dtd.ElementRef(dtd.getElementSymbolTable().updateOrCreateReference("element1", new Element("element1"))));
        countingPattern.addParticle(choicePattern);
        Particle dtdElement1particle = countingPattern;

        Boolean element2mixed = false;

        CountingPattern countingPattern2 = new CountingPattern(1, null);
        countingPattern2.addParticle(new eu.fox7.schematoolkit.common.dtd.ElementRef(dtd.getElementSymbolTable().updateOrCreateReference("element2", new Element("element2"))));
        Particle dtdElement2particle = countingPattern2;

        //--------------------------------------------------------------------//

        Element dtdElement1 = new Element("element");
        dtdElement1.setMixed(element1mixed);
        dtdElement1.setParticle(dtdElement1particle);
        Element dtdElement2 = new Element("element");
        dtdElement2.setMixed(element2mixed);
        dtdElement2.setParticle(dtdElement2particle);
        ElementWrapper elementWrapper = new ElementWrapper(schema, "elementOne");
        elementWrapper.addDTDElement(dtdElement1);
        elementWrapper.addDTDElement(dtdElement2);
        ElementUnionBuilder instance = new ElementUnionBuilder(elementWrapper);

        printOut(dtdElement1, dtdElement2);

        instance.unifyElements();

        Element dtdElement = elementWrapper.getDTDElements().iterator().next();

        //--------------------------------------------------------------------//
        // Result:
        //--------------------------------------------------------------------//

        printResult(dtdElement);

        assertEquals("element", dtdElement.getName());
        assertTrue(dtdElement.getMixed());
        assertFalse(dtdElement.isEmpty());
        assertFalse(dtdElement.hasAnyType());
        assertTrue(dtdElement.getAttributes().isEmpty());
        assertTrue(dtdElement.getParticle() instanceof CountingPattern);

        CountingPattern countingPattern1 = (CountingPattern) dtdElement.getParticle();
        assertEquals(0, countingPattern1.getMin().intValue());
        assertEquals(null, countingPattern1.getMax());
        assertTrue(countingPattern1.getParticles().getFirst() instanceof ChoicePattern);
        ChoicePattern choicePattern1 = (ChoicePattern) countingPattern1.getParticles().getFirst();
        assertTrue(choicePattern1.getParticles().getFirst() instanceof eu.fox7.schematoolkit.common.dtd.ElementRef);

        Element element3 = ((eu.fox7.schematoolkit.common.dtd.ElementRef) choicePattern1.getParticles().getFirst()).getElement();
        assertEquals("element1", element3.getName());
    }

    /**
     * Test of unifyElements method, of class ElementUnionBuilder.
     * @throws Exception
     */
    @Test
    public void testUnifyElements_mixed_content_complex_content() throws Exception {

        Boolean element1mixed = true;

        CountingPattern countingPattern = new CountingPattern(0, null);
        ChoicePattern choicePattern = new ChoicePattern();
        choicePattern.addParticle(new eu.fox7.schematoolkit.common.dtd.ElementRef(dtd.getElementSymbolTable().updateOrCreateReference("element1", new Element("element1"))));
        countingPattern.addParticle(choicePattern);
        Particle dtdElement1particle = countingPattern;

        Boolean element2mixed = false;

        SequencePattern sequencePattern = new SequencePattern();
        sequencePattern.addParticle(new eu.fox7.schematoolkit.common.dtd.ElementRef(dtd.getElementSymbolTable().updateOrCreateReference("element1", new Element("element1"))));
        sequencePattern.addParticle(new eu.fox7.schematoolkit.common.dtd.ElementRef(dtd.getElementSymbolTable().updateOrCreateReference("element2", new Element("element2"))));
        Particle dtdElement2particle = sequencePattern;

        //--------------------------------------------------------------------//

        Element dtdElement1 = new Element("element");
        dtdElement1.setMixed(element1mixed);
        dtdElement1.setParticle(dtdElement1particle);
        Element dtdElement2 = new Element("element");
        dtdElement2.setMixed(element2mixed);
        dtdElement2.setParticle(dtdElement2particle);
        ElementWrapper elementWrapper = new ElementWrapper(schema, "elementOne");
        elementWrapper.addDTDElement(dtdElement1);
        elementWrapper.addDTDElement(dtdElement2);
        ElementUnionBuilder instance = new ElementUnionBuilder(elementWrapper);

        printOut(dtdElement1, dtdElement2);

        instance.unifyElements();

        Element dtdElement = elementWrapper.getDTDElements().iterator().next();

        //--------------------------------------------------------------------//
        // Result:
        //--------------------------------------------------------------------//

        printResult(dtdElement);

        assertEquals("element", dtdElement.getName());
        assertTrue(dtdElement.getMixed());
        assertFalse(dtdElement.isEmpty());
        assertFalse(dtdElement.hasAnyType());
        assertTrue(dtdElement.getAttributes().isEmpty());
        assertTrue(dtdElement.getParticle() instanceof CountingPattern);

        CountingPattern countingPattern1 = (CountingPattern) dtdElement.getParticle();
        assertEquals(0, countingPattern1.getMin().intValue());
        assertEquals(null, countingPattern1.getMax());

        ChoicePattern choicePattern1 = (ChoicePattern) countingPattern1.getParticles().getFirst();
        assertTrue(choicePattern1.getParticles().getFirst() instanceof eu.fox7.schematoolkit.common.dtd.ElementRef);

        Element element3 = ((eu.fox7.schematoolkit.common.dtd.ElementRef) choicePattern1.getParticles().getFirst()).getElement();
        assertEquals("element1", element3.getName());

        assertTrue(choicePattern1.getParticles().getLast() instanceof eu.fox7.schematoolkit.common.dtd.ElementRef);

        Element element4 = ((eu.fox7.schematoolkit.common.dtd.ElementRef) choicePattern1.getParticles().getLast()).getElement();
        assertEquals("element2", element4.getName());
    }

    // Complex content
    /**
     * Test of unifyElements method, of class ElementUnionBuilder.
     * @throws Exception
     */
    @Test
    public void testUnifyElements_complex_content_complex_content() throws Exception {

        Boolean element1mixed = false;

        CountingPattern countingPattern = new CountingPattern(0, 1);
        ChoicePattern choicePattern = new ChoicePattern();
        choicePattern.addParticle(new eu.fox7.schematoolkit.common.dtd.ElementRef(dtd.getElementSymbolTable().updateOrCreateReference("element1", new Element("element1"))));
        countingPattern.addParticle(choicePattern);
        Particle dtdElement1particle = countingPattern;

        Boolean element2mixed = false;

        SequencePattern sequencePattern = new SequencePattern();
        sequencePattern.addParticle(new eu.fox7.schematoolkit.common.dtd.ElementRef(dtd.getElementSymbolTable().updateOrCreateReference("element1", new Element("element1"))));
        sequencePattern.addParticle(new eu.fox7.schematoolkit.common.dtd.ElementRef(dtd.getElementSymbolTable().updateOrCreateReference("element2", new Element("element2"))));
        Particle dtdElement2particle = sequencePattern;

        //--------------------------------------------------------------------//

        Element dtdElement1 = new Element("element");
        dtdElement1.setMixed(element1mixed);
        dtdElement1.setParticle(dtdElement1particle);
        Element dtdElement2 = new Element("element");
        dtdElement2.setMixed(element2mixed);
        dtdElement2.setParticle(dtdElement2particle);
        ElementWrapper elementWrapper = new ElementWrapper(schema, "elementOne");
        elementWrapper.addDTDElement(dtdElement1);
        elementWrapper.addDTDElement(dtdElement2);
        ElementUnionBuilder instance = new ElementUnionBuilder(elementWrapper);

        printOut(dtdElement1, dtdElement2);

        instance.unifyElements();

        Element dtdElement = elementWrapper.getDTDElements().iterator().next();

        //--------------------------------------------------------------------//
        // Result:
        //--------------------------------------------------------------------//

        printResult(dtdElement);

        assertEquals("element", dtdElement.getName());
        assertFalse(dtdElement.getMixed());
        assertFalse(dtdElement.isEmpty());
        assertFalse(dtdElement.hasAnyType());
        assertTrue(dtdElement.getAttributes().isEmpty());
        assertTrue(dtdElement.getParticle() instanceof ChoicePattern);

        ChoicePattern choicePattern1 = (ChoicePattern) dtdElement.getParticle();

        assertTrue(choicePattern1.getParticles().getFirst() instanceof SequencePattern);

        SequencePattern sequencePattern1 = (SequencePattern) choicePattern1.getParticles().getFirst();

        assertTrue(sequencePattern1.getParticles().getFirst() instanceof eu.fox7.schematoolkit.common.dtd.ElementRef);

        Element element3 = ((eu.fox7.schematoolkit.common.dtd.ElementRef) sequencePattern1.getParticles().getFirst()).getElement();
        assertEquals("element1", element3.getName());

        assertTrue(sequencePattern1.getParticles().getLast() instanceof eu.fox7.schematoolkit.common.dtd.ElementRef);

        Element element4 = ((eu.fox7.schematoolkit.common.dtd.ElementRef) sequencePattern1.getParticles().getLast()).getElement();
        assertEquals("element2", element4.getName());

        assertTrue(choicePattern1.getParticles().getLast() instanceof CountingPattern);

        CountingPattern countingPattern1 = (CountingPattern) choicePattern1.getParticles().getLast();
        assertEquals(0, countingPattern1.getMin().intValue());
        assertEquals(1, countingPattern1.getMax().intValue());

        ChoicePattern choicePattern2 = (ChoicePattern) countingPattern1.getParticles().getFirst();
        assertTrue(choicePattern2.getParticles().getFirst() instanceof eu.fox7.schematoolkit.common.dtd.ElementRef);

        Element element5 = ((eu.fox7.schematoolkit.common.dtd.ElementRef) choicePattern2.getParticles().getFirst()).getElement();
        assertEquals("element1", element5.getName());

    }

            /**
     * Test of unifyElements method, of class ElementUnionBuilder.
     * @throws Exception
     */
    @Test
    public void testUnifyElements_complex_content_complex_content_with_one_choice() throws Exception {

        Boolean element1mixed = false;

        ChoicePattern choicePattern = new ChoicePattern();
        choicePattern.addParticle(new eu.fox7.schematoolkit.common.dtd.ElementRef(dtd.getElementSymbolTable().updateOrCreateReference("element", new Element("element"))));
        Particle dtdElement1particle = choicePattern;

        Boolean element2mixed = false;

        SequencePattern sequencePattern = new SequencePattern();
        sequencePattern.addParticle(new eu.fox7.schematoolkit.common.dtd.ElementRef(dtd.getElementSymbolTable().updateOrCreateReference("element", new Element("element"))));
        sequencePattern.addParticle(new eu.fox7.schematoolkit.common.dtd.ElementRef(dtd.getElementSymbolTable().updateOrCreateReference("element2", new Element("element2"))));
        Particle dtdElement2particle = sequencePattern;

        //--------------------------------------------------------------------//

        Element dtdElement1 = new Element("element");
        dtdElement1.setMixed(element1mixed);
        dtdElement1.setParticle(dtdElement1particle);
        Element dtdElement2 = new Element("element");
        dtdElement2.setMixed(element2mixed);
        dtdElement2.setParticle(dtdElement2particle);
        ElementWrapper elementWrapper = new ElementWrapper(schema, "elementOne");
        elementWrapper.addDTDElement(dtdElement1);
        elementWrapper.addDTDElement(dtdElement2);
        ElementUnionBuilder instance = new ElementUnionBuilder(elementWrapper);

        printOut(dtdElement1, dtdElement2);

        instance.unifyElements();

        Element dtdElement = elementWrapper.getDTDElements().iterator().next();

        //--------------------------------------------------------------------//
        // Result:
        //--------------------------------------------------------------------//

        printResult(dtdElement);

        assertEquals("element", dtdElement.getName());
        assertFalse(dtdElement.getMixed());
        assertFalse(dtdElement.isEmpty());
        assertFalse(dtdElement.hasAnyType());
        assertTrue(dtdElement.getAttributes().isEmpty());
        assertTrue(dtdElement.getParticle() instanceof ChoicePattern);

        ChoicePattern choicePattern1 = (ChoicePattern) dtdElement.getParticle();

        assertTrue(choicePattern1.getParticles().getFirst() instanceof eu.fox7.schematoolkit.common.dtd.ElementRef);

        Element element5 = ((eu.fox7.schematoolkit.common.dtd.ElementRef) choicePattern1.getParticles().getFirst()).getElement();
        assertEquals("element", element5.getName());

        SequencePattern sequencePattern1 = (SequencePattern) choicePattern1.getParticles().getLast();

        assertTrue(sequencePattern1.getParticles().getFirst() instanceof  eu.fox7.schematoolkit.common.dtd.ElementRef);

        Element element3 = ((eu.fox7.schematoolkit.common.dtd.ElementRef) sequencePattern1.getParticles().getFirst()).getElement();
        assertEquals("element", element3.getName());

        assertTrue(sequencePattern1.getParticles().getLast() instanceof eu.fox7.schematoolkit.common.dtd.ElementRef);

        Element element4 = ((eu.fox7.schematoolkit.common.dtd.ElementRef) sequencePattern1.getParticles().getLast()).getElement();
        assertEquals("element2", element4.getName());
    }

}
