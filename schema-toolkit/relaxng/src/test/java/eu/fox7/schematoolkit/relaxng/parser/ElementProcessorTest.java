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

package eu.fox7.schematoolkit.relaxng.parser;

import eu.fox7.schematoolkit.relaxng.om.*;
import eu.fox7.schematoolkit.relaxng.parser.RNGRootProcessor;
import eu.fox7.schematoolkit.relaxng.parser.exceptions.EmptyNameClassException;
import eu.fox7.schematoolkit.relaxng.parser.exceptions.EmptyPatternException;
import eu.fox7.schematoolkit.relaxng.parser.exceptions.GrammarIsNullException;
import eu.fox7.schematoolkit.relaxng.parser.exceptions.InvalidAnyUriException;
import eu.fox7.schematoolkit.relaxng.parser.exceptions.InvalidNCNameException;
import eu.fox7.schematoolkit.relaxng.parser.exceptions.InvalidQNameException;
import eu.fox7.schematoolkit.relaxng.parser.exceptions.MultipleNameClassException;
import eu.fox7.schematoolkit.relaxng.parser.exceptions.MultiplePatternException;
import eu.fox7.schematoolkit.relaxng.parser.exceptions.ParentGrammarIsNullException;
import eu.fox7.schematoolkit.relaxng.parser.exceptions.UnsupportedContentException;

import java.util.Iterator;
import java.util.LinkedList;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test of class ElementProcessor
 * @author Lars Schmidt
 */
public class ElementProcessorTest extends junit.framework.TestCase {

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
     * Test valid case of class ElementProcessor.
     * @throws Exception
     */
    @Test
    public void testValidCaseEmpty() throws Exception {
        try {
            rngRootProcessor.processNode(RNGUtilities.getSchemaNode("tests/eu/fox7/bonxai/relaxng/parser/rngs/elementTests/element_valid.rng"));
        } catch (Exception error) {
            fail("Exception was thrown: " + error);
        }
        assertTrue(rngSchema.getRootPattern() instanceof Grammar);
        Grammar grammar = (Grammar) rngSchema.getRootPattern();
        assertTrue(grammar.getStartPatterns().getFirst() instanceof Element);
        Element element = (Element) grammar.getStartPatterns().getFirst();
        assertEquals("foo", element.getNameAttribute());
        assertEquals("http://www.example.org", element.getAttributeNamespace());
        assertTrue(element.getPatterns().getFirst() instanceof Empty);
    }

    /**
     * Test valid case of class ElementProcessor.
     * @throws Exception
     */
    @Test
    public void testValidCaseNameAndNotallowed() throws Exception {
        try {
            rngRootProcessor.processNode(RNGUtilities.getSchemaNode("tests/eu/fox7/bonxai/relaxng/parser/rngs/elementTests/element_valid_name_notallowed.rng"));
        } catch (Exception error) {
            fail("Exception was thrown: " + error);
        }
        assertTrue(rngSchema.getRootPattern() instanceof Grammar);
        Grammar grammar = (Grammar) rngSchema.getRootPattern();
        assertTrue(grammar.getStartPatterns().getFirst() instanceof Element);
        Element element = (Element) grammar.getStartPatterns().getFirst();
        assertEquals(null, element.getNameAttribute());
        assertTrue(element.getPatterns().getFirst() instanceof NotAllowed);
        assertTrue(element.getNameClass() instanceof Name);
        Name name = (Name) element.getNameClass();
        assertEquals("temp", name.getContent());
    }

    /**
     * Test valid case of class ElementProcessor.
     * @throws Exception
     */
    @Test
    public void testValidCaseNsNameAndExternalRef() throws Exception {
        try {
            rngRootProcessor.processNode(RNGUtilities.getSchemaNode("tests/eu/fox7/bonxai/relaxng/parser/rngs/elementTests/element_valid_nsName_externalRef.rng"));
        } catch (Exception error) {
            fail("Exception was thrown: " + error);
        }
        assertTrue(rngSchema.getRootPattern() instanceof Grammar);
        Grammar grammar = (Grammar) rngSchema.getRootPattern();
        assertTrue(grammar.getStartPatterns().getFirst() instanceof Element);
        Element element = (Element) grammar.getStartPatterns().getFirst();
        assertEquals(null, element.getNameAttribute());
        assertTrue(element.getPatterns().getFirst() instanceof ExternalRef);
        assertTrue(element.getNameClass() instanceof NsName);
    }

    /**
     * Test valid case of class ElementProcessor.
     * @throws Exception
     */
    @Test
    public void testValidCaseAnyNameAndOptional() throws Exception {
        try {
            rngRootProcessor.processNode(RNGUtilities.getSchemaNode("tests/eu/fox7/bonxai/relaxng/parser/rngs/elementTests/element_valid_anyName_optional.rng"));
        } catch (Exception error) {
            fail("Exception was thrown: " + error);
        }
        assertTrue(rngSchema.getRootPattern() instanceof Grammar);
        Grammar grammar = (Grammar) rngSchema.getRootPattern();
        assertTrue(grammar.getStartPatterns().getFirst() instanceof Element);
        Element element = (Element) grammar.getStartPatterns().getFirst();
        assertTrue(element.getNameClass() instanceof AnyName);
        assertTrue(element.getPatterns().getFirst() instanceof Optional);
    }

    /**
     * Test valid case of class ElementProcessor.
     * @throws Exception
     */
    @Test
    public void testValidCaseChoiceNameAndMixed() throws Exception {
        try {
            rngRootProcessor.processNode(RNGUtilities.getSchemaNode("tests/eu/fox7/bonxai/relaxng/parser/rngs/elementTests/element_valid_choiceName_mixed.rng"));
        } catch (Exception error) {
            fail("Exception was thrown: " + error);
        }
        assertTrue(rngSchema.getRootPattern() instanceof Grammar);
        Grammar grammar = (Grammar) rngSchema.getRootPattern();
        assertTrue(grammar.getStartPatterns().getFirst() instanceof Element);
        Element element = (Element) grammar.getStartPatterns().getFirst();
        assertTrue(element.getNameClass() instanceof NameClassChoice);
        assertTrue(element.getPatterns().getFirst() instanceof Mixed);
    }

    /**
     * Test valid case of class ElementProcessor.
     * @throws Exception
     */
    @Test
    public void testValidCase() throws Exception {
        try {
            rngRootProcessor.processNode(RNGUtilities.getSchemaNode("tests/eu/fox7/bonxai/relaxng/parser/rngs/elementTests/element_valid_all_childs.rng"));
        } catch (Exception error) {
            fail("Exception was thrown: " + error);
        }
        assertTrue(rngSchema.getRootPattern() instanceof Grammar);
        Grammar outerGrammar = (Grammar) rngSchema.getRootPattern();
        assertTrue(outerGrammar.getStartPatterns().getFirst() instanceof Grammar);
        Grammar grammar = (Grammar) outerGrammar.getStartPatterns().getFirst();
        assertTrue(grammar.getDefineLookUpTable().getReference("define").getReference().getFirst().getPatterns().getFirst() instanceof Element);

        Element element = (Element) grammar.getDefineLookUpTable().getReference("define").getReference().getFirst().getPatterns().getFirst();
        LinkedList<Pattern> temp = element.getPatterns();

        Iterator<Pattern> it = element.getPatterns().iterator();

        assertTrue(it.next() instanceof Element);
        assertTrue(it.next() instanceof Empty);
        assertTrue(it.next() instanceof Text);
        assertTrue(it.next() instanceof Data);
        assertTrue(it.next() instanceof Value);
        assertTrue(it.next() instanceof List);
        assertTrue(it.next() instanceof Attribute);
        assertTrue(it.next() instanceof Optional);
        assertTrue(it.next() instanceof Mixed);
        assertTrue(it.next() instanceof ZeroOrMore);
        assertTrue(it.next() instanceof ExternalRef);
        assertTrue(it.next() instanceof NotAllowed);
        assertTrue(it.next() instanceof Grammar);
        assertTrue(it.next() instanceof Ref);
        assertTrue(it.next() instanceof ParentRef);
        assertTrue(it.next() instanceof OneOrMore);
        assertTrue(it.next() instanceof Choice);
        assertTrue(it.next() instanceof Group);
        assertTrue(it.next() instanceof Interleave);
    }

    /**
     * Test invalid case of class ElementProcessor, with UnsupportedContentException.
     * @throws Exception
     */
    @Test
    public void testInValidCaseWithUnsupportedContentException() throws Exception {
        try {
            rngRootProcessor.processNode(RNGUtilities.getSchemaNode("tests/eu/fox7/bonxai/relaxng/parser/rngs/elementTests/element_invalid_content.rng"));
        } catch (UnsupportedContentException error) {
            return;
        }
        fail("Unsupported content under element-tag was not detected.");
    }

    /**
     * Test invalid case of class ElementProcessor, with MultipleNameClassException.
     * @throws Exception
     */
    @Test
    public void testInValidCaseWithMultipleNameClassException() throws Exception {
        try {
            rngRootProcessor.processNode(RNGUtilities.getSchemaNode("tests/eu/fox7/bonxai/relaxng/parser/rngs/elementTests/element_invalid_multiNameClass.rng"));
        } catch (MultipleNameClassException error) {
            return;
        }
        fail("Multiple NameClass content under element-tag was not detected.");
    }

    /**
     * Test invalid case of class ElementProcessor, with MultipleName.
     * @throws Exception
     */
    @Test
    public void testInValidCaseWithMultipleName() throws Exception {
        try {
            rngRootProcessor.processNode(RNGUtilities.getSchemaNode("tests/eu/fox7/bonxai/relaxng/parser/rngs/elementTests/element_invalid_multiName.rng"));
        } catch (MultipleNameClassException error) {
            return;
        }
        fail("Multiple Names as attribute and NameClass content under element-tag was not detected.");
    }

    /**
     * Test invalid case of class ElementProcessor, with InvalidQNameException.
     * @throws Exception
     */
    @Test
    public void testInValidCaseWithInvalidQNameException() throws Exception {
        try {
            rngRootProcessor.processNode(RNGUtilities.getSchemaNode("tests/eu/fox7/bonxai/relaxng/parser/rngs/elementTests/element_invalid_QName.rng"));
        } catch (InvalidQNameException error) {
            return;
        }
        fail("There is an invalid QName in an element-tag, but this was not detected.");
    }

    /**
     * Test invalid case of class ElementProcessor, with EmptyNameClassException.
     * @throws Exception
     */
    @Test
    public void testInValidCaseWithEmptyNameClassException() throws Exception {
        try {
            rngRootProcessor.processNode(RNGUtilities.getSchemaNode("tests/eu/fox7/bonxai/relaxng/parser/rngs/elementTests/element_empty_name.rng"));
        } catch (EmptyNameClassException error) {
            return;
        }
        fail("There is no name in an element-tag, but this was not detected.");
    }

    /**
     * Test invalid case of class ElementProcessor, with EmptyPatternException.
     * @throws Exception
     */
    @Test
    public void testInValidCaseWithEmptyPatternException() throws Exception {
        try {
            rngRootProcessor.processNode(RNGUtilities.getSchemaNode("tests/eu/fox7/bonxai/relaxng/parser/rngs/elementTests/element_empty_pattern.rng"));
        } catch (EmptyPatternException error) {
            return;
        }
        fail("There is no pattern in an element-tag, but this was not detected.");
    }

    /**
     * Test invalid case of class ElementProcessor, with MultiplePatternException.
     * @throws Exception
     */
    @Test
    public void testInValidCaseWithMultiplePatternException() throws Exception {
        try {
            rngRootProcessor.processNode(RNGUtilities.getSchemaNode("tests/eu/fox7/bonxai/relaxng/parser/rngs/elementTests/element_empty_empty.rng"));
        } catch (MultiplePatternException error) {
            return;
        }
        fail("Multiple \"empty\" patterns under element-tag were not detected.");
    }

    /**
     * Test invalid case of class ElementProcessor, with InvalidAnyUriException externalRef.
     * @throws Exception
     */
    @Test
    public void testInValidCaseWithInvalidAnyUriException() throws Exception {
        try {
            rngRootProcessor.processNode(RNGUtilities.getSchemaNode("tests/eu/fox7/bonxai/relaxng/parser/rngs/elementTests/element_externalRef.rng"));
        } catch (InvalidAnyUriException error) {
            return;
        }
        fail("There is an invalid AnyURI in a externalRef under an element-tag, but this was not detected.");
    }

    /**
     * Test invalid case of class ElementProcessor, with InvalidNCNameException Ref.
     * @throws Exception
     */
    @Test
    public void testInValidCaseWithInvalidNCNameException() throws Exception {
        try {
            rngRootProcessor.processNode(RNGUtilities.getSchemaNode("tests/eu/fox7/bonxai/relaxng/parser/rngs/elementTests/element_ref_ncname.rng"));
        } catch (InvalidNCNameException error) {
            return;
        }
        fail("There is an invalid NCName in a Ref under an element-tag, but this was not detected.");
    }

    /**
     * Test invalid case of class ElementProcessor, with GrammarIsNullException Ref.
     * @throws Exception
     */
    @Test
    public void testInValidCaseWithGrammarIsNullException() throws Exception {
        try {
            rngRootProcessor.processNode(RNGUtilities.getSchemaNode("tests/eu/fox7/bonxai/relaxng/parser/rngs/elementTests/element_ref_grammar.rng"));
        } catch (GrammarIsNullException error) {
            return;
        }
        fail("There is no grammar for a Ref under an element-tag, but this was not detected.");
    }

    /**
     * Test invalid case of class ElementProcessor, with ParentGrammarIsNullException Ref.
     * @throws Exception
     */
    @Test
    public void testInValidCaseWithParentGrammarIsNullException() throws Exception {
        try {
            rngRootProcessor.processNode(RNGUtilities.getSchemaNode("tests/eu/fox7/bonxai/relaxng/parser/rngs/elementTests/element_parentRef_grammar.rng"));
        } catch (ParentGrammarIsNullException error) {
            return;
        }
        fail("There is no parent grammar for a parentRef under an element-tag, but this was not detected.");
    }
}
