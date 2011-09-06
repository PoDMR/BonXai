package eu.fox7.bonxai.dtd.common;

import eu.fox7.bonxai.common.AllPattern;
import eu.fox7.bonxai.common.ChoicePattern;
import eu.fox7.bonxai.common.CountingPattern;
import eu.fox7.bonxai.common.Particle;
import eu.fox7.bonxai.common.SequencePattern;
import eu.fox7.bonxai.dtd.DocumentTypeDefinition;
import eu.fox7.bonxai.dtd.Element;
import eu.fox7.bonxai.dtd.ElementRef;
import eu.fox7.bonxai.dtd.common.ElementContentModelProcessor;
import eu.fox7.bonxai.dtd.common.exceptions.ContentModelCountingPatternIllegalMinValueException;
import eu.fox7.bonxai.dtd.common.exceptions.ContentModelCountingPatternNotAllowedDTDValueException;
import eu.fox7.bonxai.dtd.common.exceptions.ContentModelEmptyChildParticleListException;
import eu.fox7.bonxai.dtd.common.exceptions.ContentModelIllegalMixedDuplicateElementException;
import eu.fox7.bonxai.dtd.common.exceptions.ContentModelIllegalMixedParticleException;
import eu.fox7.bonxai.dtd.common.exceptions.ContentModelIllegalParticleException;
import eu.fox7.bonxai.dtd.common.exceptions.ContentModelIllegalStringForMixedContentException;
import eu.fox7.bonxai.dtd.common.exceptions.ContentModelNullParticleException;
import eu.fox7.bonxai.dtd.common.exceptions.ContentModelStringEmptyException;
import eu.fox7.bonxai.dtd.common.exceptions.ContentModelStringTokenizerIllegalStateException;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test of class ElementContentModelProcessor
 * @author Lars Schmidt
 */
public class ElementContentModelProcessorTest extends junit.framework.TestCase {

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
     * Test of convertRegExpStringToParticle method, of class ElementContentModelProcessor.
     */
    @Test
    public void testConvertRegExpStringToParticle() throws Exception {
        ElementContentModelProcessor instance = new ElementContentModelProcessor();
        Particle particle = null;
        try {

            particle = instance.convertRegExpStringToParticle("(test)");
            assertTrue(particle instanceof ElementRef);
            assertEquals("test", ((ElementRef) particle).getElement().getName());

            particle = instance.convertRegExpStringToParticle("(test)*");
            assertTrue(particle instanceof CountingPattern);
            CountingPattern countingPattern = ((CountingPattern) particle);
            assertEquals(0, countingPattern.getMin().intValue());
            assertTrue(countingPattern.getMax() == null);
            assertEquals(1, countingPattern.getParticles().size());
            assertEquals("test", ((ElementRef) countingPattern.getParticles().getFirst()).getElement().getName());

            particle = instance.convertRegExpStringToParticle("(test|temp|foo)");
            assertTrue(particle instanceof ChoicePattern);
            ChoicePattern ChoicePattern = ((ChoicePattern) particle);
            assertEquals(3, ChoicePattern.getParticles().size());
            assertEquals("temp", ((ElementRef) ChoicePattern.getParticles().get(1)).getElement().getName());


            particle = instance.convertRegExpStringToParticle("(test, temp, foo)+");
            assertTrue(particle instanceof CountingPattern);
            CountingPattern countingPatternSequence = ((CountingPattern) particle);
            assertEquals(1, countingPatternSequence.getMin().intValue());
            assertTrue(countingPatternSequence.getMax() == null);
            assertEquals(1, countingPatternSequence.getParticles().size());
            SequencePattern sequencePattern = ((SequencePattern) countingPatternSequence.getParticles().getFirst());
            assertEquals(3, sequencePattern.getParticles().size());
            assertEquals("foo", ((ElementRef) sequencePattern.getParticles().get(2)).getElement().getName());

            particle = instance.convertRegExpStringToParticle("(#PCDATA)");
            assertTrue(particle == null);
            assertTrue(instance.isMixed());
            assertFalse(instance.isMixedStar());

            particle = instance.convertRegExpStringToParticle("(#PCDATA)*");
            assertTrue(particle == null);
            assertTrue(instance.isMixed());
            assertTrue(instance.isMixedStar());

            particle = instance.convertRegExpStringToParticle("(#PCDATA|test)*");
            assertTrue(particle instanceof CountingPattern);
            CountingPattern countingPatternMixed = (CountingPattern) particle;
            assertEquals(0, countingPatternMixed.getMin().intValue());
            assertTrue(countingPatternMixed.getMax() == null);
            assertEquals("test", ((ElementRef) countingPatternMixed.getParticles().getFirst()).getElement().getName());

            particle = instance.convertRegExpStringToParticle("(#PCDATA|test|foobar)*");
            assertTrue(particle instanceof CountingPattern);
            CountingPattern countingPatternMixed2 = (CountingPattern) particle;
            assertEquals(0, countingPatternMixed2.getMin().intValue());
            assertTrue(countingPatternMixed2.getMax() == null);
            ChoicePattern choicePatternMixed = (ChoicePattern) countingPatternMixed2.getParticles().getLast();
            assertEquals("foobar", ((ElementRef) choicePatternMixed.getParticles().getLast()).getElement().getName());

        } catch (Exception ex) {
            fail("Exception was thrown: " + ex.getMessage());
        }
    }

    /**
     * Test of convertParticleToRegExpString method, of class ElementContentModelProcessor.
     */
    @Test
    public void testConvertParticleToRegExpString() throws Exception {
        ElementContentModelProcessor instance = new ElementContentModelProcessor();

        DocumentTypeDefinition dtd = new DocumentTypeDefinition();


        Element elementRefElement = new Element("myRefElement");
        dtd.getElementSymbolTable().updateOrCreateReference("myRefElement", elementRefElement);
        ElementRef elementRef = new ElementRef(dtd.getElementSymbolTable().getReference("myRefElement"));
        Particle particle = elementRef;
        Element element = new Element("myElement");
        element.setParticle(particle);

        try {
            assertEquals("(myRefElement)", instance.convertParticleToRegExpString(element));

        } catch (Exception ex) {
            fail("Exception was thrown: " + ex.getMessage());
        }

        CountingPattern countingPattern = new CountingPattern(0, 1);
        countingPattern.addParticle(elementRef);
        element.setParticle(countingPattern);

        try {
            assertEquals("(myRefElement)?", instance.convertParticleToRegExpString(element));
        } catch (Exception ex) {
            fail("Exception was thrown: " + ex.getMessage());
        }

        ChoicePattern choicePattern = new ChoicePattern();
        choicePattern.addParticle(elementRef);
        element.setParticle(choicePattern);

        try {
            assertEquals("(myRefElement)", instance.convertParticleToRegExpString(element));
        } catch (Exception ex) {
            fail("Exception was thrown: " + ex.getMessage());
        }

        Element elementRefElement2 = new Element("myRefElement2");
        dtd.getElementSymbolTable().updateOrCreateReference("myRefElement2", elementRefElement2);
        ElementRef elementRef2 = new ElementRef(dtd.getElementSymbolTable().getReference("myRefElement2"));

        choicePattern.addParticle(elementRef2);
        element.setParticle(choicePattern);

        try {
            assertEquals("(myRefElement|myRefElement2)", instance.convertParticleToRegExpString(element));
        } catch (Exception ex) {
            fail("Exception was thrown: " + ex.getMessage());
        }


        Element elementMixed = new Element("myMixedElement");
        elementMixed.setMixed(true);

        try {
            assertEquals("(#PCDATA)", instance.convertParticleToRegExpString(elementMixed));
        } catch (Exception ex) {
            fail("Exception was thrown: " + ex.getMessage());
        }

        Element elementMixed2 = new Element("myMixedElement");
        elementMixed2.setMixedStar(true);

        try {
            assertEquals("(#PCDATA)*", instance.convertParticleToRegExpString(elementMixed2));
        } catch (Exception ex) {
            fail("Exception was thrown: " + ex.getMessage());
        }

        Element elementMixed3 = new Element("myMixedElement");
        CountingPattern countingPattern1 = new CountingPattern(0, null);
        countingPattern1.addParticle(elementRef);
        elementMixed3.setParticle(countingPattern1);
        elementMixed3.setMixed(true);
        elementMixed3.setMixedStar(true);

        try {
            assertEquals("(#PCDATA|myRefElement)*", instance.convertParticleToRegExpString(elementMixed3));
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
    public void testContentModelNullParticleException() throws Exception {
        try {
            ElementContentModelProcessor instance = new ElementContentModelProcessor();
            Element element = new Element("myElement");
            CountingPattern countingPattern = new CountingPattern(0, 1);
            countingPattern.addParticle(null);
            element.setParticle(countingPattern);

            instance.convertParticleToRegExpString(element);

        } catch (ContentModelNullParticleException error) {
            return;
        }
        fail("The contentModel has an internal null particle, but this was not detected.");
    }

    @Test
    public void testContentModelIllegalParticleException() throws Exception {
        try {
            ElementContentModelProcessor instance = new ElementContentModelProcessor();
            Element element = new Element("myElement");
            CountingPattern countingPattern = new CountingPattern(0, 1);
            countingPattern.addParticle(new AllPattern());
            element.setParticle(countingPattern);

            instance.convertParticleToRegExpString(element);

        } catch (ContentModelIllegalParticleException error) {
            return;
        }
        fail("The contentModel has an illegal internal particle, but this was not detected.");
    }

    @Test
    public void testContentModelCountingPatternIllegalMinValueException() throws Exception {
        try {
            DocumentTypeDefinition dtd = new DocumentTypeDefinition();
            ElementContentModelProcessor instance = new ElementContentModelProcessor();
            Element element = new Element("myElement");
            CountingPattern countingPattern = new CountingPattern(null, 1);

            Element elementRefElement2 = new Element("myRefElement2");
            dtd.getElementSymbolTable().updateOrCreateReference("myRefElement2", elementRefElement2);
            ElementRef elementRef2 = new ElementRef(dtd.getElementSymbolTable().getReference("myRefElement2"));

            countingPattern.addParticle(elementRef2);
            element.setParticle(countingPattern);

            instance.convertParticleToRegExpString(element);

        } catch (ContentModelCountingPatternIllegalMinValueException error) {
            return;
        }
        fail("The contentModel of Type CountingPattern has an illegal MinValue, but this was not detected.");
    }

    @Test
    public void testContentModelCountingPatternNotAllowedDTDValueException() throws Exception {
        try {
            DocumentTypeDefinition dtd = new DocumentTypeDefinition();
            ElementContentModelProcessor instance = new ElementContentModelProcessor();
            Element element = new Element("myElement");
            CountingPattern countingPattern = new CountingPattern(5, 1);

            Element elementRefElement2 = new Element("myRefElement2");
            dtd.getElementSymbolTable().updateOrCreateReference("myRefElement2", elementRefElement2);
            ElementRef elementRef2 = new ElementRef(dtd.getElementSymbolTable().getReference("myRefElement2"));

            countingPattern.addParticle(elementRef2);
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
            DocumentTypeDefinition dtd = new DocumentTypeDefinition();
            ElementContentModelProcessor instance = new ElementContentModelProcessor();
            Element element = new Element("myElement");
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
            DocumentTypeDefinition dtd = new DocumentTypeDefinition();
            ElementContentModelProcessor instance = new ElementContentModelProcessor();
            Element element = new Element("myElement");
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
    public void testContentModelIllegalMixedParticleExceptionWithCountingPatternWithTwoElementRefs() throws Exception {
        try {
            DocumentTypeDefinition dtd = new DocumentTypeDefinition();
            ElementContentModelProcessor instance = new ElementContentModelProcessor();
            Element element = new Element("myElement");
            element.setMixed(true);
            CountingPattern countingPattern = new CountingPattern(0, null);

            Element elementRefElement2 = new Element("myRefElement2");
            dtd.getElementSymbolTable().updateOrCreateReference("myRefElement2", elementRefElement2);
            ElementRef elementRef2 = new ElementRef(dtd.getElementSymbolTable().getReference("myRefElement2"));

            countingPattern.addParticle(elementRef2);
            countingPattern.addParticle(elementRef2);
            element.setParticle(countingPattern);

            instance.convertParticleToRegExpString(element);

        } catch (ContentModelIllegalMixedParticleException error) {
            return;
        }
        fail("The contentModel of a mixed element has more than one Particle in a CountingPattern, but this was not detected.");
    }

    @Test
    public void testContentModelIllegalMixedParticleExceptionWithCountingPatternWithInvalidValues() throws Exception {
        try {
            DocumentTypeDefinition dtd = new DocumentTypeDefinition();
            ElementContentModelProcessor instance = new ElementContentModelProcessor();
            Element element = new Element("myElement");
            element.setMixed(true);
            CountingPattern countingPattern = new CountingPattern(0, 3);

            Element elementRefElement2 = new Element("myRefElement2");
            dtd.getElementSymbolTable().updateOrCreateReference("myRefElement2", elementRefElement2);
            ElementRef elementRef2 = new ElementRef(dtd.getElementSymbolTable().getReference("myRefElement2"));

            countingPattern.addParticle(elementRef2);
            element.setParticle(countingPattern);

            instance.convertParticleToRegExpString(element);

        } catch (ContentModelIllegalMixedParticleException error) {
            return;
        }
        fail("The contentModel of a mixed element has a CountingPattern with invalid min/max values, but this was not detected.");
    }

    @Test
    public void testContentModelIllegalMixedParticleExceptionWithInvalidParticleInCP() throws Exception {
        try {
            DocumentTypeDefinition dtd = new DocumentTypeDefinition();
            ElementContentModelProcessor instance = new ElementContentModelProcessor();
            Element element = new Element("myElement");
            element.setMixed(true);
            CountingPattern countingPattern = new CountingPattern(0, null);

            countingPattern.addParticle(countingPattern);
            element.setParticle(countingPattern);

            instance.convertParticleToRegExpString(element);

        } catch (ContentModelIllegalMixedParticleException error) {
            return;
        }
        fail("The contentModel of a mixed element has a CountingPattern with an invalid particle, but this was not detected.");
    }

    @Test
    public void testContentModelIllegalMixedParticleExceptionWithChoicePatternEmpty() throws Exception {
        try {
            DocumentTypeDefinition dtd = new DocumentTypeDefinition();
            ElementContentModelProcessor instance = new ElementContentModelProcessor();
            Element element = new Element("myElement");
            element.setMixed(true);
            CountingPattern countingPattern = new CountingPattern(0, null);
            ChoicePattern choicePattern = new ChoicePattern();

            countingPattern.addParticle(choicePattern);
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
            DocumentTypeDefinition dtd = new DocumentTypeDefinition();
            ElementContentModelProcessor instance = new ElementContentModelProcessor();
            Element element = new Element("myElement");
            element.setMixed(true);
            CountingPattern countingPattern = new CountingPattern(0, null);
            ChoicePattern choicePattern = new ChoicePattern();
            choicePattern.addParticle(new AllPattern());
            countingPattern.addParticle(choicePattern);
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
            DocumentTypeDefinition dtd = new DocumentTypeDefinition();
            ElementContentModelProcessor instance = new ElementContentModelProcessor();
            Element element = new Element("myElement");
            element.setMixed(true);
            CountingPattern countingPattern = new CountingPattern(0, null);

            ElementRef elementRef = new ElementRef(null);

            countingPattern.addParticle(elementRef);
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

    @Test
    public void testContentModelIllegalMixedDuplicateElementExceptionString() throws Exception {
        try {
            ElementContentModelProcessor instance = new ElementContentModelProcessor();
            instance.convertRegExpStringToParticle("(#PCDATA|test|air|test)*");

        } catch (ContentModelIllegalMixedDuplicateElementException error) {
            return;
        }
        fail("The regExpString has duplicate elements in a mixed content, but this was not detected.");
    }

    @Test
    public void testContentModelIllegalMixedDuplicateElementExceptionParticle() throws Exception {
        try {
            ElementContentModelProcessor instance = new ElementContentModelProcessor();

            DocumentTypeDefinition dtd = new DocumentTypeDefinition();

            Element elementRefElement = new Element("myRefElement");
            dtd.getElementSymbolTable().updateOrCreateReference("myRefElement", elementRefElement);
            ElementRef elementRef = new ElementRef(dtd.getElementSymbolTable().getReference("myRefElement"));

            Element elementRefElement2 = new Element("myRefElement");
            dtd.getElementSymbolTable().updateOrCreateReference("myRefElement2", elementRefElement2);
            ElementRef elementRef2 = new ElementRef(dtd.getElementSymbolTable().getReference("myRefElement2"));

            Element elementMixed3 = new Element("myMixedElement");
            CountingPattern countingPattern1 = new CountingPattern(0, null);

            ChoicePattern choicePattern = new ChoicePattern();
            choicePattern.addParticle(elementRef);
            choicePattern.addParticle(elementRef2);

            countingPattern1.addParticle(choicePattern);

            elementMixed3.setParticle(countingPattern1);
            elementMixed3.setMixed(true);
            elementMixed3.setMixedStar(true);
            instance.convertParticleToRegExpString(elementMixed3);
            
        } catch (ContentModelIllegalMixedDuplicateElementException error) {
            return;
        }
        fail("The ChoicePattern has duplicate elements in a mixed content, but this was not detected.");
    }
}