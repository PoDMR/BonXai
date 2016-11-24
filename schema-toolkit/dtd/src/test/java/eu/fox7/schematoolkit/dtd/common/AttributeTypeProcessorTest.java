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

package eu.fox7.schematoolkit.dtd.common;

import eu.fox7.schematoolkit.common.QualifiedName;
import eu.fox7.schematoolkit.dtd.common.AttributeTypeProcessor;
import eu.fox7.schematoolkit.dtd.common.exceptions.AttributeTypeIllegalValueException;
import eu.fox7.schematoolkit.dtd.common.exceptions.NotationNotDeclaredException;
import eu.fox7.schematoolkit.dtd.om.Attribute;
import eu.fox7.schematoolkit.dtd.om.AttributeType;
import eu.fox7.schematoolkit.dtd.om.DocumentTypeDefinition;
import eu.fox7.schematoolkit.dtd.om.PublicNotation;
import eu.fox7.schematoolkit.dtd.om.SystemNotation;

import java.util.LinkedHashSet;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test of class AttributeTypeProcessor
 * @author Lars Schmidt
 */
public class AttributeTypeProcessorTest extends junit.framework.TestCase {

    private DocumentTypeDefinition dtd;

    @Before
    @Override
    public void setUp() {
        this.dtd = new DocumentTypeDefinition();
    }

    /**
     * Test of setTypeToAttribute method, of class AttributeTypeProcessor.
     * @throws Exception 
     */
    @Test
    public void testSetTypeToAttribute() throws Exception {
        Attribute attribute = new Attribute(new QualifiedName("","name"), "mode", "value");
        AttributeTypeProcessor instance = new AttributeTypeProcessor(dtd, attribute);

        instance.setTypeToAttribute("ID");
        assertEquals(AttributeType.ID, attribute.getType());
        assertTrue(attribute.getEnumerationOrNotationTokens().isEmpty());

        instance.setTypeToAttribute("IDREF");
        assertEquals(AttributeType.IDREF, attribute.getType());
        assertTrue(attribute.getEnumerationOrNotationTokens().isEmpty());

        instance.setTypeToAttribute("IDREFS");
        assertEquals(AttributeType.IDREFS, attribute.getType());
        assertTrue(attribute.getEnumerationOrNotationTokens().isEmpty());

        instance.setTypeToAttribute("CDATA");
        assertEquals(AttributeType.CDATA, attribute.getType());
        assertTrue(attribute.getEnumerationOrNotationTokens().isEmpty());

        instance.setTypeToAttribute("ENTITIES");
        assertEquals(AttributeType.ENTITIES, attribute.getType());
        assertTrue(attribute.getEnumerationOrNotationTokens().isEmpty());

        instance.setTypeToAttribute("ENTITY");
        assertEquals(AttributeType.ENTITY, attribute.getType());
        assertTrue(attribute.getEnumerationOrNotationTokens().isEmpty());

        instance.setTypeToAttribute("(bla|blub)");
        assertEquals(AttributeType.ENUMERATION, attribute.getType());
        assertEquals(2, attribute.getEnumerationOrNotationTokens().size());

        instance.setTypeToAttribute("NMTOKEN");
        assertEquals(AttributeType.NMTOKEN, attribute.getType());
        attribute.setEnumerationOrNotationTokens(new LinkedHashSet<String>());
        assertTrue(attribute.getEnumerationOrNotationTokens().isEmpty());

        instance.setTypeToAttribute("NMTOKENS");
        assertEquals(AttributeType.NMTOKENS, attribute.getType());
        assertTrue(attribute.getEnumerationOrNotationTokens().isEmpty());

        dtd.addNotation(new PublicNotation("test", "test"));
        dtd.addNotation(new SystemNotation("temp", "temp"));
        dtd.addNotation(new PublicNotation("three", "three"));

        instance.setTypeToAttribute("NOTATION (test|temp|three)");
        assertEquals(AttributeType.NOTATION, attribute.getType());
        assertEquals(3, attribute.getEnumerationOrNotationTokens().size());
    }

    /*
     * NotationNotDeclaredException
     */
    @Test
    public void testAttributeTypeIllegalValueExceptionWithENUMERATION() throws Exception {
        try {
            /**
             * The attribute type ENUMERATION will implicit be set, when a
             * choice of possible strings is given.
             * Form "(test1|part2|house)"
             */
            Attribute attribute = new Attribute(new QualifiedName("","name"), "mode", "value");
            AttributeTypeProcessor instance = new AttributeTypeProcessor(dtd, attribute);
            instance.setTypeToAttribute("ENUMERATION");
        } catch (AttributeTypeIllegalValueException error) {
            return;
        }
        fail("String value for setting an attribute type is illegal (ENUMERATION), but this wasn't detected.");
    }

    @Test
    public void testAttributeTypeIllegalValueException() throws Exception {
        try {
            /**
             * Testing a random string, which has nothing to do with a possible
             * name of a type in DTD
             */
            Attribute attribute = new Attribute(new QualifiedName("","name"), "mode", "value");
            AttributeTypeProcessor instance = new AttributeTypeProcessor(dtd, attribute);
            instance.setTypeToAttribute("someOtherString");
        } catch (AttributeTypeIllegalValueException error) {
            return;
        }
        fail("String value for setting an attribute type is illegal, but this wasn't detected.");
    }
}
