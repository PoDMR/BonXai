package eu.fox7.bonxai.dtd.common;

import eu.fox7.bonxai.dtd.Attribute;
import eu.fox7.bonxai.dtd.AttributeType;
import eu.fox7.bonxai.dtd.DocumentTypeDefinition;
import eu.fox7.bonxai.dtd.PublicNotation;
import eu.fox7.bonxai.dtd.SystemNotation;
import eu.fox7.bonxai.dtd.common.AttributeTypeProcessor;
import eu.fox7.bonxai.dtd.common.exceptions.AttributeTypeIllegalValueException;
import eu.fox7.bonxai.dtd.common.exceptions.NotationNotDeclaredException;

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
        Attribute attribute = new Attribute("name", "mode", "value");
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
            Attribute attribute = new Attribute("name", "mode", "value");
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
            Attribute attribute = new Attribute("name", "mode", "value");
            AttributeTypeProcessor instance = new AttributeTypeProcessor(dtd, attribute);
            instance.setTypeToAttribute("someOtherString");
        } catch (AttributeTypeIllegalValueException error) {
            return;
        }
        fail("String value for setting an attribute type is illegal, but this wasn't detected.");
    }

    @Test
    public void testNotationNotDeclaredException() throws Exception {
        try {
            Attribute attribute = new Attribute("name", "mode", "value");
            AttributeTypeProcessor instance = new AttributeTypeProcessor(dtd, attribute);
            instance.setTypeToAttribute("NOTATION (gobbler|chicken|turkey)");
        } catch (NotationNotDeclaredException error) {
            return;
        }
        fail("A value of type NOTATION has not been declared in the current DTD Schema, but this wasn't detected.");
    }
}
