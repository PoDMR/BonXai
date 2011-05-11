package de.tudortmund.cs.bonxai.relaxng.parser;

import de.tudortmund.cs.bonxai.relaxng.*;
import de.tudortmund.cs.bonxai.relaxng.parser.exceptions.MultipleNameClassException;
import de.tudortmund.cs.bonxai.relaxng.parser.exceptions.MultiplePatternException;
import de.tudortmund.cs.bonxai.relaxng.parser.exceptions.UnsupportedContentException;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test of class AttributeProcessor
 * @author Lars Schmidt
 */
public class AttributeProcessorTest extends junit.framework.TestCase {

    // Relax NG Schema for this testcase
    RelaxNGSchema rngSchema;
    RNGRootProcessor rngRootProcessor;

    @Before
    @Override
    public void setUp() {
        rngSchema = new RelaxNGSchema();
        rngRootProcessor = new RNGRootProcessor(rngSchema);
    }

    /**
     * Test valid case of class AttributeProcessor.
     * @throws Exception
     */
    @Test
    public void testValidCaseEmpty() throws Exception {
        try {
            rngRootProcessor.processNode(RNGUtilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/relaxng/parser/rngs/attributeTests/attribute_valid.rng"));
        } catch (Exception error) {
            fail("Exception was thrown: " + error);
        }
        assertTrue(rngSchema.getRootPattern() instanceof Grammar);
        Grammar grammar = (Grammar) rngSchema.getRootPattern();
        assertTrue(grammar.getStartPatterns().getFirst() instanceof Element);
        Element element = (Element) grammar.getStartPatterns().getFirst();
        assertTrue(element.getPatterns().getFirst() instanceof Attribute);
        Attribute attribute = (Attribute) element.getPatterns().getFirst();
        assertEquals("temp", attribute.getNameAttribute());
        assertEquals("http://www.example.org", attribute.getAttributeNamespace());
        assertTrue(attribute.getPattern() instanceof Empty);
    }

    /**
     * Test valid case of class AttributeProcessor.
     * @throws Exception
     */
    @Test
    public void testValidCaseNameAndNotallowed() throws Exception {
        try {
            rngRootProcessor.processNode(RNGUtilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/relaxng/parser/rngs/attributeTests/attribute_valid_name_notallowed.rng"));
        } catch (Exception error) {
            fail("Exception was thrown: " + error);
        }
        assertTrue(rngSchema.getRootPattern() instanceof Grammar);
        Grammar grammar = (Grammar) rngSchema.getRootPattern();
        assertTrue(grammar.getStartPatterns().getFirst() instanceof Element);
        Element element = (Element) grammar.getStartPatterns().getFirst();
        assertTrue(element.getPatterns().getFirst() instanceof Attribute);
        Attribute attribute = (Attribute) element.getPatterns().getFirst();
        assertEquals(null, attribute.getNameAttribute());
        assertEquals("http://www.example.org", attribute.getAttributeNamespace());
        assertTrue(attribute.getPattern() instanceof NotAllowed);
        assertTrue(attribute.getNameClass() instanceof Name);
        Name name = (Name) attribute.getNameClass();
        assertEquals("temp", name.getContent());
    }

    /**
     * Test valid case of class AttributeProcessor.
     * @throws Exception
     */
    @Test
    public void testValidCaseNsNameAndExternalRef() throws Exception {
        try {
            rngRootProcessor.processNode(RNGUtilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/relaxng/parser/rngs/attributeTests/attribute_valid_nsName_externalRef.rng"));
        } catch (Exception error) {
            fail("Exception was thrown: " + error);
        }
        assertTrue(rngSchema.getRootPattern() instanceof Grammar);
        Grammar grammar = (Grammar) rngSchema.getRootPattern();
        assertTrue(grammar.getStartPatterns().getFirst() instanceof Element);
        Element element = (Element) grammar.getStartPatterns().getFirst();
        assertTrue(element.getPatterns().getFirst() instanceof Attribute);
        Attribute attribute = (Attribute) element.getPatterns().getFirst();
        assertEquals(null, attribute.getNameAttribute());
        assertEquals("http://www.example.org", attribute.getAttributeNamespace());
        assertTrue(attribute.getPattern() instanceof ExternalRef);
        assertTrue(attribute.getNameClass() instanceof NsName);
    }

    /**
     * Test valid case of class AttributeProcessor.
     * @throws Exception
     */
    @Test
    public void testValidCaseAnyNameAndOptional() throws Exception {
        try {
            rngRootProcessor.processNode(RNGUtilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/relaxng/parser/rngs/attributeTests/attribute_valid_anyName_optional.rng"));
        } catch (Exception error) {
            fail("Exception was thrown: " + error);
        }
        assertTrue(rngSchema.getRootPattern() instanceof Grammar);
        Grammar grammar = (Grammar) rngSchema.getRootPattern();
        assertTrue(grammar.getStartPatterns().getFirst() instanceof Element);
        Element element = (Element) grammar.getStartPatterns().getFirst();
        assertTrue(element.getPatterns().getFirst() instanceof OneOrMore);
        OneOrMore oneOrMore = (OneOrMore) element.getPatterns().getFirst();
        assertTrue(oneOrMore.getPatterns().getFirst() instanceof Attribute);
        Attribute attribute = (Attribute) oneOrMore.getPatterns().getFirst();
        assertTrue(attribute.getNameClass() instanceof AnyName);
        assertTrue(attribute.getPattern() instanceof Optional);
    }

    /**
     * Test valid case of class AttributeProcessor.
     * @throws Exception
     */
    @Test
    public void testValidCaseChoiceNameAndMixed() throws Exception {
        try {
            rngRootProcessor.processNode(RNGUtilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/relaxng/parser/rngs/attributeTests/attribute_valid_choiceName_mixed.rng"));
        } catch (Exception error) {
            fail("Exception was thrown: " + error);
        }
        assertTrue(rngSchema.getRootPattern() instanceof Grammar);
        Grammar grammar = (Grammar) rngSchema.getRootPattern();
        assertTrue(grammar.getStartPatterns().getFirst() instanceof Element);
        Element element = (Element) grammar.getStartPatterns().getFirst();
        assertTrue(element.getPatterns().getFirst() instanceof OneOrMore);
        OneOrMore oneOrMore = (OneOrMore) element.getPatterns().getFirst();
        assertTrue(oneOrMore.getPatterns().getFirst() instanceof Attribute);
        Attribute attribute = (Attribute) oneOrMore.getPatterns().getFirst();
        assertTrue(attribute.getNameClass() instanceof NameClassChoice);
        assertTrue(attribute.getPattern() instanceof Mixed);
    }

    /**
     * Test valid case of class AttributeProcessor.
     * @throws Exception
     */
    @Test
    public void testValidCaseNameAndText() throws Exception {
        try {
            rngRootProcessor.processNode(RNGUtilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/relaxng/parser/rngs/attributeTests/attribute_valid_name_text.rng"));
        } catch (Exception error) {
            fail("Exception was thrown: " + error);
        }
        assertTrue(rngSchema.getRootPattern() instanceof Grammar);
        Grammar grammar = (Grammar) rngSchema.getRootPattern();
        assertTrue(grammar.getStartPatterns().getFirst() instanceof Element);
        Element element = (Element) grammar.getStartPatterns().getFirst();
        assertTrue(element.getPatterns().getFirst() instanceof Attribute);
        Attribute attribute = (Attribute) element.getPatterns().getFirst();
        assertEquals(null, attribute.getNameAttribute());
        assertEquals("http://www.example.org", attribute.getAttributeNamespace());
        assertTrue(attribute.getPattern() instanceof Text);
        assertTrue(attribute.getNameClass() instanceof Name);
        Name name = (Name) attribute.getNameClass();
        assertEquals("temp", name.getContent());
    }

    /**
     * Test valid case of class AttributeProcessor.
     * @throws Exception
     */
    @Test
    public void testValidCaseNameAndData() throws Exception {
        try {
            rngRootProcessor.processNode(RNGUtilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/relaxng/parser/rngs/attributeTests/attribute_valid_name_data.rng"));
        } catch (Exception error) {
            fail("Exception was thrown: " + error);
        }
        assertTrue(rngSchema.getRootPattern() instanceof Grammar);
        Grammar grammar = (Grammar) rngSchema.getRootPattern();
        assertTrue(grammar.getStartPatterns().getFirst() instanceof Element);
        Element element = (Element) grammar.getStartPatterns().getFirst();
        assertTrue(element.getPatterns().getFirst() instanceof Attribute);
        Attribute attribute = (Attribute) element.getPatterns().getFirst();
        assertEquals(null, attribute.getNameAttribute());
        assertEquals("http://www.example.org", attribute.getAttributeNamespace());
        assertTrue(attribute.getPattern() instanceof Data);
        assertTrue(attribute.getNameClass() instanceof Name);
        Name name = (Name) attribute.getNameClass();
        assertEquals("temp", name.getContent());
    }

    /**
     * Test valid case of class AttributeProcessor.
     * @throws Exception
     */
    @Test
    public void testValidCaseNameAndValue() throws Exception {
        try {
            rngRootProcessor.processNode(RNGUtilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/relaxng/parser/rngs/attributeTests/attribute_valid_name_value.rng"));
        } catch (Exception error) {
            fail("Exception was thrown: " + error);
        }
        assertTrue(rngSchema.getRootPattern() instanceof Grammar);
        Grammar grammar = (Grammar) rngSchema.getRootPattern();
        assertTrue(grammar.getStartPatterns().getFirst() instanceof Element);
        Element element = (Element) grammar.getStartPatterns().getFirst();
        assertTrue(element.getPatterns().getFirst() instanceof Attribute);
        Attribute attribute = (Attribute) element.getPatterns().getFirst();
        assertEquals(null, attribute.getNameAttribute());
        assertEquals("http://www.example.org", attribute.getAttributeNamespace());
        assertTrue(attribute.getPattern() instanceof Value);
        assertTrue(attribute.getNameClass() instanceof Name);
        Name name = (Name) attribute.getNameClass();
        assertEquals("temp", name.getContent());
    }

    /**
     * Test valid case of class AttributeProcessor.
     * @throws Exception
     */
    @Test
    public void testValidCaseNameAndList() throws Exception {
        try {
            rngRootProcessor.processNode(RNGUtilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/relaxng/parser/rngs/attributeTests/attribute_valid_name_list.rng"));
        } catch (Exception error) {
            fail("Exception was thrown: " + error);
        }
        assertTrue(rngSchema.getRootPattern() instanceof Grammar);
        Grammar grammar = (Grammar) rngSchema.getRootPattern();
        assertTrue(grammar.getStartPatterns().getFirst() instanceof Element);
        Element element = (Element) grammar.getStartPatterns().getFirst();
        assertTrue(element.getPatterns().getFirst() instanceof Attribute);
        Attribute attribute = (Attribute) element.getPatterns().getFirst();
        assertEquals(null, attribute.getNameAttribute());
        assertEquals("http://www.example.org", attribute.getAttributeNamespace());
        assertTrue(attribute.getPattern() instanceof List);
        assertTrue(attribute.getNameClass() instanceof Name);
        Name name = (Name) attribute.getNameClass();
        assertEquals("temp", name.getContent());
    }

    /**
     * Test valid case of class AttributeProcessor.
     * @throws Exception
     */
    @Test
    public void testValidCaseNameAndRef() throws Exception {
        try {
            rngRootProcessor.processNode(RNGUtilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/relaxng/parser/rngs/attributeTests/attribute_valid_name_ref.rng"));
        } catch (Exception error) {
            fail("Exception was thrown: " + error);
        }
        assertTrue(rngSchema.getRootPattern() instanceof Grammar);
        Grammar grammar = (Grammar) rngSchema.getRootPattern();
        assertTrue(grammar.getStartPatterns().getFirst() instanceof Element);
        Element element = (Element) grammar.getStartPatterns().getFirst();
        assertTrue(element.getPatterns().getFirst() instanceof Attribute);
        Attribute attribute = (Attribute) element.getPatterns().getFirst();
        assertEquals(null, attribute.getNameAttribute());
        assertEquals("http://www.example.org", attribute.getAttributeNamespace());
        assertTrue(attribute.getPattern() instanceof Ref);
        assertTrue(attribute.getNameClass() instanceof Name);
        Name name = (Name) attribute.getNameClass();
        assertEquals("temp", name.getContent());
    }

    /**
     * Test valid case of class AttributeProcessor.
     * @throws Exception
     */
    @Test
    public void testValidCaseNameAndParentRef() throws Exception {
        try {
            rngRootProcessor.processNode(RNGUtilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/relaxng/parser/rngs/attributeTests/attribute_valid_name_parentRef.rng"));
        } catch (Exception error) {
            fail("Exception was thrown: " + error);
        }
        assertTrue(rngSchema.getRootPattern() instanceof Grammar);
        Grammar outerGrammar = (Grammar) rngSchema.getRootPattern();
        assertTrue(outerGrammar.getStartPatterns().getFirst() instanceof Grammar);
        Grammar grammar = (Grammar) outerGrammar.getStartPatterns().getFirst();
        assertTrue(grammar.getStartPatterns().getFirst() instanceof Element);
        Element element = (Element) grammar.getStartPatterns().getFirst();
        assertTrue(element.getPatterns().getFirst() instanceof Attribute);
        Attribute attribute = (Attribute) element.getPatterns().getFirst();
        assertEquals(null, attribute.getNameAttribute());
        assertEquals("http://www.example.org", attribute.getAttributeNamespace());
        assertTrue(attribute.getPattern() instanceof ParentRef);
        assertTrue(attribute.getNameClass() instanceof Name);
        Name name = (Name) attribute.getNameClass();
        assertEquals("temp", name.getContent());
    }

    /**
     * Test valid case of class AttributeProcessor.
     * @throws Exception
     */
    @Test
    public void testValidCaseNameAndChoice() throws Exception {
        try {
            rngRootProcessor.processNode(RNGUtilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/relaxng/parser/rngs/attributeTests/attribute_valid_name_choice.rng"));
        } catch (Exception error) {
            fail("Exception was thrown: " + error);
        }
        assertTrue(rngSchema.getRootPattern() instanceof Grammar);
        Grammar grammar = (Grammar) rngSchema.getRootPattern();
        assertTrue(grammar.getStartPatterns().getFirst() instanceof Element);
        Element element = (Element) grammar.getStartPatterns().getFirst();
        assertTrue(element.getPatterns().getFirst() instanceof Attribute);
        Attribute attribute = (Attribute) element.getPatterns().getFirst();
        assertEquals(null, attribute.getNameAttribute());
        assertEquals("http://www.example.org", attribute.getAttributeNamespace());
        assertTrue(attribute.getPattern() instanceof Choice);
        assertTrue(attribute.getNameClass() instanceof Name);
        Name name = (Name) attribute.getNameClass();
        assertEquals("temp", name.getContent());
    }

    /**
     * Test valid case of class AttributeProcessor.
     * @throws Exception
     */
    @Test
    public void testValidCaseNameAndInterleave() throws Exception {
        try {
            rngRootProcessor.processNode(RNGUtilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/relaxng/parser/rngs/attributeTests/attribute_valid_name_interleave.rng"));
        } catch (Exception error) {
            fail("Exception was thrown: " + error);
        }
        assertTrue(rngSchema.getRootPattern() instanceof Grammar);
        Grammar grammar = (Grammar) rngSchema.getRootPattern();
        assertTrue(grammar.getStartPatterns().getFirst() instanceof Element);
        Element element = (Element) grammar.getStartPatterns().getFirst();
        assertTrue(element.getPatterns().getFirst() instanceof Attribute);
        Attribute attribute = (Attribute) element.getPatterns().getFirst();
        assertEquals(null, attribute.getNameAttribute());
        assertEquals("http://www.example.org", attribute.getAttributeNamespace());
        assertTrue(attribute.getPattern() instanceof Interleave);
        assertTrue(attribute.getNameClass() instanceof Name);
        Name name = (Name) attribute.getNameClass();
        assertEquals("temp", name.getContent());
    }

    /**
     * Test valid case of class AttributeProcessor.
     * @throws Exception
     */
    @Test
    public void testValidCaseNameAndGroup() throws Exception {
        try {
            rngRootProcessor.processNode(RNGUtilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/relaxng/parser/rngs/attributeTests/attribute_valid_name_group.rng"));
        } catch (Exception error) {
            fail("Exception was thrown: " + error);
        }
        assertTrue(rngSchema.getRootPattern() instanceof Grammar);
        Grammar grammar = (Grammar) rngSchema.getRootPattern();
        assertTrue(grammar.getStartPatterns().getFirst() instanceof Element);
        Element element = (Element) grammar.getStartPatterns().getFirst();
        assertTrue(element.getPatterns().getFirst() instanceof Attribute);
        Attribute attribute = (Attribute) element.getPatterns().getFirst();
        assertEquals(null, attribute.getNameAttribute());
        assertEquals("http://www.example.org", attribute.getAttributeNamespace());
        assertTrue(attribute.getPattern() instanceof Group);
        assertTrue(attribute.getNameClass() instanceof Name);
        Name name = (Name) attribute.getNameClass();
        assertEquals("temp", name.getContent());
    }

    /**
     * Test valid case of class AttributeProcessor.
     * @throws Exception
     */
    @Test
    public void testValidCaseNameAndGrammar() throws Exception {
        try {
            rngRootProcessor.processNode(RNGUtilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/relaxng/parser/rngs/attributeTests/attribute_valid_name_grammar.rng"));
        } catch (Exception error) {
            fail("Exception was thrown: " + error);
        }
        assertTrue(rngSchema.getRootPattern() instanceof Grammar);
        Grammar grammar = (Grammar) rngSchema.getRootPattern();
        assertTrue(grammar.getStartPatterns().getFirst() instanceof Element);
        Element element = (Element) grammar.getStartPatterns().getFirst();
        assertTrue(element.getPatterns().getFirst() instanceof Attribute);
        Attribute attribute = (Attribute) element.getPatterns().getFirst();
        assertEquals(null, attribute.getNameAttribute());
        assertEquals("http://www.example.org", attribute.getAttributeNamespace());
        assertTrue(attribute.getPattern() instanceof Grammar);
        assertTrue(attribute.getNameClass() instanceof Name);
        Name name = (Name) attribute.getNameClass();
        assertEquals("temp", name.getContent());
    }

    /**
     * Test valid case of class AttributeProcessor.
     * @throws Exception
     */
    @Test
    public void testValidCaseNameAndOneOrMore() throws Exception {
        try {
            rngRootProcessor.processNode(RNGUtilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/relaxng/parser/rngs/attributeTests/attribute_valid_name_oneOrMore.rng"));
        } catch (Exception error) {
            fail("Exception was thrown: " + error);
        }
        assertTrue(rngSchema.getRootPattern() instanceof Grammar);
        Grammar grammar = (Grammar) rngSchema.getRootPattern();
        assertTrue(grammar.getStartPatterns().getFirst() instanceof Element);
        Element element = (Element) grammar.getStartPatterns().getFirst();
        assertTrue(element.getPatterns().getFirst() instanceof Attribute);
        Attribute attribute = (Attribute) element.getPatterns().getFirst();
        assertEquals(null, attribute.getNameAttribute());
        assertEquals("http://www.example.org", attribute.getAttributeNamespace());
        assertTrue(attribute.getPattern() instanceof OneOrMore);
        assertTrue(attribute.getNameClass() instanceof Name);
        Name name = (Name) attribute.getNameClass();
        assertEquals("temp", name.getContent());
    }

    /**
     * Test valid case of class AttributeProcessor.
     * @throws Exception
     */
    @Test
    public void testValidCaseNameAndZeroOrMore() throws Exception {
        try {
            rngRootProcessor.processNode(RNGUtilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/relaxng/parser/rngs/attributeTests/attribute_valid_name_zeroOrMore.rng"));
        } catch (Exception error) {
            fail("Exception was thrown: " + error);
        }
        assertTrue(rngSchema.getRootPattern() instanceof Grammar);
        Grammar grammar = (Grammar) rngSchema.getRootPattern();
        assertTrue(grammar.getStartPatterns().getFirst() instanceof Element);
        Element element = (Element) grammar.getStartPatterns().getFirst();
        assertTrue(element.getPatterns().getFirst() instanceof Attribute);
        Attribute attribute = (Attribute) element.getPatterns().getFirst();
        assertEquals(null, attribute.getNameAttribute());
        assertEquals("http://www.example.org", attribute.getAttributeNamespace());
        assertTrue(attribute.getPattern() instanceof ZeroOrMore);
        assertTrue(attribute.getNameClass() instanceof Name);
        Name name = (Name) attribute.getNameClass();
        assertEquals("temp", name.getContent());
    }

    /**
     * Test invalid case of class AttributeProcessor, with UnsupportedContentException.
     * @throws Exception
     */
    @Test
    public void testInValidCaseWithUnsupportedContentException() throws Exception {
        try {
            rngRootProcessor.processNode(RNGUtilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/relaxng/parser/rngs/attributeTests/attribute_invalid_content.rng"));
        } catch (UnsupportedContentException error) {
            return;
        }
        fail("Unsupported content under attribute-tag was not detected.");
    }

    /**
     * Test invalid case of class AttributeProcessor, with MultiplePatternException.
     * @throws Exception
     */
    @Test
    public void testInValidCaseWithMultiplePatternException() throws Exception {
        try {
            rngRootProcessor.processNode(RNGUtilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/relaxng/parser/rngs/attributeTests/attribute_invalid_multiPattern.rng"));
        } catch (MultiplePatternException error) {
            return;
        }
        fail("Multiple pattern content under attribute-tag was not detected.");
    }

    /**
     * Test invalid case of class AttributeProcessor, with MultipleNameClassException.
     * @throws Exception
     */
    @Test
    public void testInValidCaseWithMultipleNameClassException() throws Exception {
        try {
            rngRootProcessor.processNode(RNGUtilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/relaxng/parser/rngs/attributeTests/attribute_invalid_multiNameClass.rng"));
        } catch (MultipleNameClassException error) {
            return;
        }
        fail("Multiple NameClass content under attribute-tag was not detected.");
    }

    /**
     * Test invalid case of class AttributeProcessor, with MultipleName.
     * @throws Exception
     */
    @Test
    public void testInValidCaseWithMultipleName() throws Exception {
        try {
            rngRootProcessor.processNode(RNGUtilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/relaxng/parser/rngs/attributeTests/attribute_invalid_multiName.rng"));
        } catch (MultipleNameClassException error) {
            return;
        }
        fail("Multiple Names as attribute and NameClass content under attribute-tag was not detected.");
    }
}
