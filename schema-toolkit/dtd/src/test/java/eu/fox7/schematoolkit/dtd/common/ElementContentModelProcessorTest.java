package eu.fox7.schematoolkit.dtd.common;

import eu.fox7.schematoolkit.common.AllPattern;
import eu.fox7.schematoolkit.common.ElementRef;
import eu.fox7.schematoolkit.common.ChoicePattern;
import eu.fox7.schematoolkit.common.CountingPattern;
import eu.fox7.schematoolkit.common.Namespace;
import eu.fox7.schematoolkit.common.Particle;
import eu.fox7.schematoolkit.common.QualifiedName;
import eu.fox7.schematoolkit.common.SequencePattern;
import eu.fox7.schematoolkit.dtd.common.ElementContentModelProcessor;
import eu.fox7.schematoolkit.dtd.common.exceptions.ContentModelCountingPatternNotAllowedDTDValueException;
import eu.fox7.schematoolkit.dtd.common.exceptions.ContentModelEmptyChildParticleListException;
import eu.fox7.schematoolkit.dtd.common.exceptions.ContentModelIllegalMixedDuplicateElementException;
import eu.fox7.schematoolkit.dtd.common.exceptions.ContentModelIllegalMixedParticleException;
import eu.fox7.schematoolkit.dtd.common.exceptions.ContentModelIllegalParticleException;
import eu.fox7.schematoolkit.dtd.common.exceptions.ContentModelIllegalStringForMixedContentException;
import eu.fox7.schematoolkit.dtd.common.exceptions.ContentModelNullParticleException;
import eu.fox7.schematoolkit.dtd.common.exceptions.ContentModelStringEmptyException;
import eu.fox7.schematoolkit.dtd.common.exceptions.ContentModelStringTokenizerIllegalStateException;
import eu.fox7.schematoolkit.dtd.om.Element;

import org.junit.Test;

/**
 * Test of class ElementContentModelProcessor
 * @author Lars Schmidt
 */
public class ElementContentModelProcessorTest extends junit.framework.TestCase {

	private QualifiedName elementRefName = new QualifiedName(Namespace.EMPTY_NAMESPACE,"myElementRef");
	private QualifiedName elementRefName2 = new QualifiedName(Namespace.EMPTY_NAMESPACE,"myElementRef2");
	private QualifiedName elementName = new QualifiedName(Namespace.EMPTY_NAMESPACE,"myElement");

	/**
     * Test of isMixed method, of class ElementContentModelProcessor.
     */
    @Test
    public void testIsMixed() {

        ElementContentModelProcessor instance = new ElementContentModelProcessor();
        try {

            instance.convertRegExpStringToParticle("(test)");
            assertFalse(instance.isMixed());

            instance.convertRegExpStringToParticle("(test)*");
            assertFalse(instance.isMixed());

            instance.convertRegExpStringToParticle("(test|temp|foo)");
            assertFalse(instance.isMixed());

            instance.convertRegExpStringToParticle("(test, temp, foo)*");
            assertFalse(instance.isMixed());

            instance.convertRegExpStringToParticle("(#PCDATA)");
            assertTrue(instance.isMixed());

            instance.convertRegExpStringToParticle("(PCDATA)");
            assertFalse(instance.isMixed());

            instance.convertRegExpStringToParticle("(#PCDATA)*");
            assertTrue(instance.isMixed());

            instance.convertRegExpStringToParticle("(PCDATA|test)*");
            assertFalse(instance.isMixed());

            instance.convertRegExpStringToParticle("(test|PCDATA)*");
            assertFalse(instance.isMixed());

            instance.convertRegExpStringToParticle("(#PCDATA|test)*");
            assertTrue(instance.isMixed());

        } catch (Exception ex) {
            fail("Exception was thrown: " + ex.getMessage());
        }
    }

    /**
     * Test of isMixedStar method, of class ElementContentModelProcessor.
     */
    @Test
    public void testIsMixedStar() {
        ElementContentModelProcessor instance = new ElementContentModelProcessor();
        try {

            instance.convertRegExpStringToParticle("(test)");
            assertFalse(instance.isMixedStar());

            instance.convertRegExpStringToParticle("(test)*");
            assertFalse(instance.isMixedStar());

            instance.convertRegExpStringToParticle("(test|temp|foo)");
            assertFalse(instance.isMixedStar());

            instance.convertRegExpStringToParticle("(test, temp, foo)*");
            assertFalse(instance.isMixedStar());

            instance.convertRegExpStringToParticle("(#PCDATA)");
            assertFalse(instance.isMixedStar());

            instance.convertRegExpStringToParticle("(PCDATA)");
            assertFalse(instance.isMixedStar());

            instance.convertRegExpStringToParticle("(#PCDATA)*");
            assertTrue(instance.isMixedStar());

            instance.convertRegExpStringToParticle("(PCDATA|test)");
            assertFalse(instance.isMixedStar());

            instance.convertRegExpStringToParticle("(test|PCDATA)");
            assertFalse(instance.isMixedStar());

            instance.convertRegExpStringToParticle("(#PCDATA|test)*");
            assertTrue(instance.isMixedStar());

        } catch (Exception ex) {
            fail("Exception was thrown: " + ex.getMessage());
        }
    }


    /**
     * Test of isWhitespaceChar method, of class ElementContentModelProcessor.
     */
    @Test
    public void testIsWhitespaceChar() {
        ElementContentModelProcessor instance = new ElementContentModelProcessor();

        assertTrue(instance.isWhitespaceChar(' '));
        assertTrue(instance.isWhitespaceChar('\n'));
        assertTrue(instance.isWhitespaceChar('\t'));
        assertTrue(instance.isWhitespaceChar('\r'));
        assertFalse(instance.isWhitespaceChar('0'));
        assertFalse(instance.isWhitespaceChar('u'));
    }

    @Test
    public void testContentModelIllegalParticleException() throws Exception {
        try {
            ElementContentModelProcessor instance = new ElementContentModelProcessor();
            Element element = new Element(elementName);
            CountingPattern countingPattern = new CountingPattern(new AllPattern(), 0, 1);
            element.setParticle(countingPattern);

            instance.convertParticleToRegExpString(element);

        } catch (ContentModelIllegalParticleException error) {
            return;
        }
        fail("The contentModel has an illegal internal particle, but this was not detected.");
    }

    @Test
    public void testContentModelCountingPatternNotAllowedDTDValueException() throws Exception {
        try {
            ElementContentModelProcessor instance = new ElementContentModelProcessor();
            Element element = new Element(elementName);
            ElementRef elementRef2 = new ElementRef(elementRefName2);
            CountingPattern countingPattern = new CountingPattern(elementRef2, 5, 1);

            element.setParticle(countingPattern);

            instance.convertParticleToRegExpString(element);

        } catch (ContentModelCountingPatternNotAllowedDTDValueException error) {
            return;
        }
        fail("The contentModel of Type CountingPattern has an illegal value for use in a DTD Schema, but this was not detected.");
    }

    @Test
    public void testContentModelEmptyChildParticleListException() throws Exception {
        try {
            ElementContentModelProcessor instance = new ElementContentModelProcessor();
            Element element = new Element(elementName);
            ChoicePattern choicePattern = new ChoicePattern();
            element.setParticle(choicePattern);

            instance.convertParticleToRegExpString(element);

        } catch (ContentModelEmptyChildParticleListException error) {
            return;
        }
        fail("The contentModel has an empty childlist (choice), but this was not detected.");
    }

    @Test
    public void testContentModelIllegalMixedParticleExceptionWithoutCountingPattern() throws Exception {
        try {
            ElementContentModelProcessor instance = new ElementContentModelProcessor();
            Element element = new Element(elementName);
            element.setMixed(true);
            ChoicePattern choicePattern = new ChoicePattern();
            element.setParticle(choicePattern);

            instance.convertParticleToRegExpString(element);

        } catch (ContentModelIllegalMixedParticleException error) {
            return;
        }
        fail("The contentModel of a mixed element has no CountingPattern, but this was not detected.");
    }

    @Test
    public void testContentModelIllegalMixedParticleExceptionWithCountingPatternWithInvalidValues() throws Exception {
        try {
            ElementContentModelProcessor instance = new ElementContentModelProcessor();
            Element element = new Element(elementName);
            element.setMixed(true);
            ElementRef elementRef2 = new ElementRef(elementRefName2);
            CountingPattern countingPattern = new CountingPattern(elementRef2, 0, 3);

            element.setParticle(countingPattern);

            instance.convertParticleToRegExpString(element);

        } catch (ContentModelIllegalMixedParticleException error) {
            return;
        }
        fail("The contentModel of a mixed element has a CountingPattern with invalid min/max values, but this was not detected.");
    }


    @Test
    public void testContentModelIllegalMixedParticleExceptionWithChoicePatternEmpty() throws Exception {
        try {
            ElementContentModelProcessor instance = new ElementContentModelProcessor();
            Element element = new Element(elementName);
            element.setMixed(true);
            ChoicePattern choicePattern = new ChoicePattern();
            CountingPattern countingPattern = new CountingPattern(choicePattern, 0, null);

            element.setParticle(countingPattern);

            instance.convertParticleToRegExpString(element);

        } catch (ContentModelIllegalMixedParticleException error) {
            return;
        }
        fail("The contentModel of a mixed element has an empty choice in a CountingPattern, but this was not detected.");
    }

    @Test
    public void testContentModelIllegalMixedParticleExceptionWithChoicePatternWrongContent() throws Exception {
        try {
            ElementContentModelProcessor instance = new ElementContentModelProcessor();
            Element element = new Element(elementName);
            element.setMixed(true);
            ChoicePattern choicePattern = new ChoicePattern();
            choicePattern.addParticle(new AllPattern());
            CountingPattern countingPattern = new CountingPattern(choicePattern, 0, null);
            element.setParticle(countingPattern);

            instance.convertParticleToRegExpString(element);

        } catch (ContentModelIllegalMixedParticleException error) {
            return;
        }
        fail("The contentModel of a mixed element has a choice in a CountingPattern with a wrong content, but this was not detected.");
    }

    @Test
    public void testContentModelIllegalMixedParticleExceptionWithElementRefNull() throws Exception {
        try {
            ElementContentModelProcessor instance = new ElementContentModelProcessor();
            Element element = new Element(elementName);
            element.setMixed(true);

            ElementRef elementRef = new ElementRef(null);
            CountingPattern countingPattern = new CountingPattern(elementRef, 0, null);


            element.setParticle(countingPattern);

            instance.convertParticleToRegExpString(element);

        } catch (ContentModelNullParticleException error) {
            return;
        }
        fail("The contentModel of a mixed element has an ElementRef with null content in a CountingPattern, but this was not detected.");
    }

    @Test
    public void testContentModelIllegalStringForMixedContentExceptionMixed() throws Exception {
        try {
            ElementContentModelProcessor instance = new ElementContentModelProcessor();
            instance.convertRegExpStringToParticle("(#PCDATA| ");

        } catch (ContentModelIllegalStringForMixedContentException error) {
            return;
        }
        fail("The regExpString has not the correct format as a mixed content, but this was not detected.");
    }

    @Test
    public void testContentModelStringEmptyException() throws Exception {
        try {
            ElementContentModelProcessor instance = new ElementContentModelProcessor();
            instance.convertRegExpStringToParticle("");

        } catch (ContentModelStringEmptyException error) {
            return;
        }
        fail("The regExpString was empty, but this was not detected.");
    }

    @Test
    public void testContentModelStringTokenizerIllegalStateException() throws Exception {
        try {
            ElementContentModelProcessor instance = new ElementContentModelProcessor();
            instance.convertRegExpStringToParticle(" ");

        } catch (ContentModelStringTokenizerIllegalStateException error) {
            return;
        }
        fail("The regExpString leads to an illegal state within the tokenizer with an illegal character, but this was not detected.");
    }

}