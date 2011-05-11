package de.tudortmund.cs.bonxai.relaxng.writer;

import com.sun.org.apache.xerces.internal.dom.DocumentImpl;
import de.tudortmund.cs.bonxai.common.DefaultNamespace;
import de.tudortmund.cs.bonxai.common.NamespaceList;
import de.tudortmund.cs.bonxai.relaxng.*;
import de.tudortmund.cs.bonxai.relaxng.writer.exceptions.EmptyPatternException;
import de.tudortmund.cs.bonxai.relaxng.writer.exceptions.InvalidAnyUriException;
import de.tudortmund.cs.bonxai.relaxng.writer.exceptions.InvalidHrefException;
import de.tudortmund.cs.bonxai.relaxng.writer.exceptions.InvalidNCNameException;
import de.tudortmund.cs.bonxai.relaxng.writer.exceptions.InvalidQNameException;
import de.tudortmund.cs.bonxai.relaxng.writer.exceptions.MissingHrefException;
import de.tudortmund.cs.bonxai.relaxng.writer.exceptions.MissingNameException;
import de.tudortmund.cs.bonxai.relaxng.writer.exceptions.MissingPatternException;
import de.tudortmund.cs.bonxai.relaxng.writer.exceptions.MultipleNameException;
import de.tudortmund.cs.bonxai.relaxng.writer.exceptions.PatternReferencedButNotDefinedException;
import de.tudortmund.cs.bonxai.relaxng.writer.exceptions.UnknownPatternException;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Element;

/**
 * Test of class PatternWriter
 * @author Lars Schmidt
 */
public class PatternWriterTest extends junit.framework.TestCase {

    // DOM Document for this testcase
    private org.w3c.dom.Document rngDocument;
    private NamespaceList rootElementNamespaceList;

    @Before
    @Override
    public void setUp() {
        rngDocument = new DocumentImpl();
        rootElementNamespaceList = new NamespaceList(new DefaultNamespace(RelaxNGSchema.RELAXNG_NAMESPACE));

    }

    /**
     * Test of valid case of createNodeForPattern method with Ref, of class PatternWriter.
     * @throws Exception 
     */
    @Test
    public void testValidCaseCreateNodeForPatternWithRef() throws Exception {
        Grammar grammar = new Grammar();
        Define define = new Define("test");
        define.addPattern(new Text());
        grammar.registerDefinePatternInLookUpTable(define);
        Ref pattern = new Ref(grammar.getDefineLookUpTable().getReference("test"), grammar);
        pattern.setAttributeDatatypeLibrary("datatypeLib");
        pattern.setAttributeNamespace("namespace");
        pattern.setDefaultNamespace("xmlnsDefault");

        PatternWriter instance = new PatternWriter(rngDocument, rootElementNamespaceList);
        Element result = instance.createNodeForPattern(pattern);

        assertEquals("ref", result.getLocalName());
        assertEquals("test", result.getAttribute("name"));
        assertEquals("datatypeLib", result.getAttribute("datatypeLibrary"));
        assertEquals("namespace", result.getAttribute("ns"));
        assertEquals("xmlnsDefault", result.getAttribute("xmlns"));
    }

    /**
     * Test of invalid case of createNodeForPattern method with Ref and PatternReferencedButNotDefinedException, of class PatternWriter.
     * @throws Exception
     */
//    @Test
//    public void testInvalidCaseCreateNodeForPatternWithRefAndPatternReferencedButNotDefinedException() throws Exception {
//        try {
//            Grammar grammar = new Grammar();
//            Define define = new Define("test");
//            grammar.registerDefinePatternInLookUpTable(define);
//            Ref pattern = new Ref(grammar.getDefineLookUpTable().getReference("test"));
//            PatternWriter instance = new PatternWriter(rngDocument, rootElementNamespaceList);
//
//            instance.createNodeForPattern(pattern);
//
//        } catch (PatternReferencedButNotDefinedException e) {
//            return;
//        }
//        fail("There is no content in the define-element, so the reference has been registered but not defined in the given grammar. This was not detected.");
//    }

    /**
     * Test of invalid case of createNodeForPattern method with Ref and InvalidNCNameException, of class PatternWriter.
     * @throws Exception
     */
    @Test
    public void testInvalidCaseCreateNodeForPatternWithRefAndInvalidNCNameException() throws Exception {
        try {
            Grammar grammar = new Grammar();
            Define define = new Define("te\\st");
            define.addPattern(new Text());
            grammar.registerDefinePatternInLookUpTable(define);
            Ref pattern = new Ref(grammar.getDefineLookUpTable().getReference("te\\st"), grammar);
            PatternWriter instance = new PatternWriter(rngDocument, rootElementNamespaceList);

            instance.createNodeForPattern(pattern);

        } catch (InvalidNCNameException e) {
            return;
        }
        fail("There is an invalid NCName in the Ref, but this was not detected.");
    }

    /**
     * Test of valid case of createNodeForPattern method with Attribute, of class PatternWriter.
     * @throws Exception
     */
    @Test
    public void testValidCaseCreateNodeForPatternWithAttribute() throws Exception {
        Attribute pattern = new Attribute();
        pattern.setAttributeDatatypeLibrary("datatypeLib");
        pattern.setAttributeNamespace("namespace");
        pattern.setDefaultNamespace("xmlnsDefault");
        pattern.setNameAttribute("test");
        pattern.setPattern(new Empty());

        PatternWriter instance = new PatternWriter(rngDocument, rootElementNamespaceList);
        Element result = instance.createNodeForPattern(pattern);

        assertEquals("attribute", result.getLocalName());
        assertEquals("test", result.getAttribute("name"));
        assertEquals("datatypeLib", result.getAttribute("datatypeLibrary"));
        assertEquals("namespace", result.getAttribute("ns"));
        assertEquals("xmlnsDefault", result.getAttribute("xmlns"));
        assertEquals(1, result.getChildNodes().getLength());
        Element child = (Element) result.getChildNodes().item(0);
        assertEquals("empty", child.getLocalName());

        pattern.setNameAttribute(null);
        pattern.setNameClass(new Name("myName"));

        Element result2 = instance.createNodeForPattern(pattern);
        assertEquals(2, result2.getChildNodes().getLength());
        Element childNameClass = (Element) result2.getChildNodes().item(0);
        assertEquals("name", childNameClass.getLocalName());
        assertEquals("myName", childNameClass.getTextContent());
    }

    /**
     * Test of invalid case of createNodeForPattern method with Attribute and InvalidQNameException, of class PatternWriter.
     * @throws Exception
     */
    @Test
    public void testInvalidCaseCreateNodeForPatternWithAttributeAndInvalidQNameException() throws Exception {
        try {
            Attribute pattern = new Attribute();
            pattern.setNameAttribute("te{{st");
            PatternWriter instance = new PatternWriter(rngDocument, rootElementNamespaceList);

            instance.createNodeForPattern(pattern);

        } catch (InvalidQNameException e) {
            return;
        }
        fail("There is an invalid QName in the attribute, but this was not detected.");
    }

    /**
     * Test of invalid case of createNodeForPattern method with Attribute and MultipleNameException, of class PatternWriter.
     * @throws Exception
     */
    @Test
    public void testInvalidCaseCreateNodeForPatternWithAttributeAndMultipleNameException() throws Exception {
        try {
            Attribute pattern = new Attribute();
            pattern.setNameAttribute("hoihioh");
            pattern.setNameClass(new Name("myName"));
            PatternWriter instance = new PatternWriter(rngDocument, rootElementNamespaceList);

            instance.createNodeForPattern(pattern);

        } catch (MultipleNameException e) {
            return;
        }
        fail("There are multiple names for an attribute (name-attribute and NameClassContent), but this was not detected.");
    }

    /**
     * Test of invalid case of createNodeForPattern method with Attribute and MissingNameException, of class PatternWriter.
     * @throws Exception
     */
    @Test
    public void testInvalidCaseCreateNodeForPatternWithAttributeAndMissingNameException() throws Exception {
        try {
            Attribute pattern = new Attribute();
            PatternWriter instance = new PatternWriter(rngDocument, rootElementNamespaceList);

            instance.createNodeForPattern(pattern);

        } catch (MissingNameException e) {
            return;
        }
        fail("There is no name in the given attribute, but this was not detected.");
    }

    /**
     * Test of valid case of createNodeForPattern method with Choice, of class PatternWriter.
     * @throws Exception
     */
    @Test
    public void testValidCaseCreateNodeForPatternWithChoice() throws Exception {
        Choice pattern = new Choice();
        pattern.setAttributeDatatypeLibrary("datatypeLib");
        pattern.setAttributeNamespace("namespace");
        pattern.setDefaultNamespace("xmlnsDefault");
        pattern.addPattern(new Empty());
        pattern.addPattern(new Text());

        PatternWriter instance = new PatternWriter(rngDocument, rootElementNamespaceList);
        Element result = instance.createNodeForPattern(pattern);

        assertEquals("choice", result.getLocalName());
        assertEquals("datatypeLib", result.getAttribute("datatypeLibrary"));
        assertEquals("namespace", result.getAttribute("ns"));
        assertEquals("xmlnsDefault", result.getAttribute("xmlns"));
        assertEquals(2, result.getChildNodes().getLength());
        Element child = (Element) result.getChildNodes().item(0);
        assertEquals("empty", child.getLocalName());
        Element child2 = (Element) result.getChildNodes().item(1);
        assertEquals("text", child2.getLocalName());
    }

    /**
     * Test of invalid case of createNodeForPattern method with Choice and MissingPatternException, of class PatternWriter.
     * @throws Exception
     */
    @Test
    public void testInvalidCaseCreateNodeForPatternWithChoiceAndMissingPatternException() throws Exception {
        try {
            Choice pattern = new Choice();

            PatternWriter instance = new PatternWriter(rngDocument, rootElementNamespaceList);
            instance.createNodeForPattern(pattern);

        } catch (MissingPatternException e) {
            return;
        }
        fail("There is no pattern under a choice element, but this was not detected.");
    }

    /**
     * Test of valid case of createNodeForPattern method with Interleave, of class PatternWriter.
     * @throws Exception
     */
    @Test
    public void testValidCaseCreateNodeForPatternWithInterleave() throws Exception {
        Interleave pattern = new Interleave();
        pattern.setAttributeDatatypeLibrary("datatypeLib");
        pattern.setAttributeNamespace("namespace");
        pattern.setDefaultNamespace("xmlnsDefault");
        pattern.addPattern(new Empty());
        pattern.addPattern(new Text());

        PatternWriter instance = new PatternWriter(rngDocument, rootElementNamespaceList);
        Element result = instance.createNodeForPattern(pattern);

        assertEquals("interleave", result.getLocalName());
        assertEquals("datatypeLib", result.getAttribute("datatypeLibrary"));
        assertEquals("namespace", result.getAttribute("ns"));
        assertEquals("xmlnsDefault", result.getAttribute("xmlns"));
        assertEquals(2, result.getChildNodes().getLength());
        Element child = (Element) result.getChildNodes().item(0);
        assertEquals("empty", child.getLocalName());
        Element child2 = (Element) result.getChildNodes().item(1);
        assertEquals("text", child2.getLocalName());
    }

    /**
     * Test of invalid case of createNodeForPattern method with Interleave and MissingPatternException, of class PatternWriter.
     * @throws Exception
     */
    @Test
    public void testInvalidCaseCreateNodeForPatternWithInterleaveAndMissingPatternException() throws Exception {
        try {
            Interleave pattern = new Interleave();

            PatternWriter instance = new PatternWriter(rngDocument, rootElementNamespaceList);
            instance.createNodeForPattern(pattern);

        } catch (MissingPatternException e) {
            return;
        }
        fail("There is no pattern under a interleave element, but this was not detected.");
    }

    /**
     * Test of valid case of createNodeForPattern method with OneOrMore, of class PatternWriter.
     * @throws Exception
     */
    @Test
    public void testValidCaseCreateNodeForPatternWithOneOrMore() throws Exception {
        OneOrMore pattern = new OneOrMore();
        pattern.setAttributeDatatypeLibrary("datatypeLib");
        pattern.setAttributeNamespace("namespace");
        pattern.setDefaultNamespace("xmlnsDefault");
        pattern.addPattern(new Empty());
        pattern.addPattern(new Text());

        PatternWriter instance = new PatternWriter(rngDocument, rootElementNamespaceList);
        Element result = instance.createNodeForPattern(pattern);

        assertEquals("oneOrMore", result.getLocalName());
        assertEquals("datatypeLib", result.getAttribute("datatypeLibrary"));
        assertEquals("namespace", result.getAttribute("ns"));
        assertEquals("xmlnsDefault", result.getAttribute("xmlns"));
        assertEquals(2, result.getChildNodes().getLength());
        Element child = (Element) result.getChildNodes().item(0);
        assertEquals("empty", child.getLocalName());
        Element child2 = (Element) result.getChildNodes().item(1);
        assertEquals("text", child2.getLocalName());
    }

    /**
     * Test of invalid case of createNodeForPattern method with OneOrMore and MissingPatternException, of class PatternWriter.
     * @throws Exception
     */
    @Test
    public void testInvalidCaseCreateNodeForPatternWithOneOrMoreAndMissingPatternException() throws Exception {
        try {
            OneOrMore pattern = new OneOrMore();

            PatternWriter instance = new PatternWriter(rngDocument, rootElementNamespaceList);
            instance.createNodeForPattern(pattern);

        } catch (MissingPatternException e) {
            return;
        }
        fail("There is no pattern under a oneormore element, but this was not detected.");
    }

    /**
     * Test of valid case of createNodeForPattern method with ZeroOrMore, of class PatternWriter.
     * @throws Exception
     */
    @Test
    public void testValidCaseCreateNodeForPatternWithZeroOrMore() throws Exception {
        ZeroOrMore pattern = new ZeroOrMore();
        pattern.setAttributeDatatypeLibrary("datatypeLib");
        pattern.setAttributeNamespace("namespace");
        pattern.setDefaultNamespace("xmlnsDefault");
        pattern.addPattern(new Empty());
        pattern.addPattern(new Text());

        PatternWriter instance = new PatternWriter(rngDocument, rootElementNamespaceList);
        Element result = instance.createNodeForPattern(pattern);

        assertEquals("zeroOrMore", result.getLocalName());
        assertEquals("datatypeLib", result.getAttribute("datatypeLibrary"));
        assertEquals("namespace", result.getAttribute("ns"));
        assertEquals("xmlnsDefault", result.getAttribute("xmlns"));
        assertEquals(2, result.getChildNodes().getLength());
        Element child = (Element) result.getChildNodes().item(0);
        assertEquals("empty", child.getLocalName());
        Element child2 = (Element) result.getChildNodes().item(1);
        assertEquals("text", child2.getLocalName());
    }

    /**
     * Test of invalid case of createNodeForPattern method with ZeroOrMore and MissingPatternException, of class PatternWriter.
     * @throws Exception
     */
    @Test
    public void testInvalidCaseCreateNodeForPatternWithZeroOrMoreAndMissingPatternException() throws Exception {
        try {
            ZeroOrMore pattern = new ZeroOrMore();

            PatternWriter instance = new PatternWriter(rngDocument, rootElementNamespaceList);
            instance.createNodeForPattern(pattern);

        } catch (MissingPatternException e) {
            return;
        }
        fail("There is no pattern under a zeroormore element, but this was not detected.");
    }

    /**
     * Test of valid case of createNodeForPattern method with Optional, of class PatternWriter.
     * @throws Exception
     */
    @Test
    public void testValidCaseCreateNodeForPatternWithOptional() throws Exception {
        Optional pattern = new Optional();
        pattern.setAttributeDatatypeLibrary("datatypeLib");
        pattern.setAttributeNamespace("namespace");
        pattern.setDefaultNamespace("xmlnsDefault");
        pattern.addPattern(new Empty());
        pattern.addPattern(new Text());

        PatternWriter instance = new PatternWriter(rngDocument, rootElementNamespaceList);
        Element result = instance.createNodeForPattern(pattern);

        assertEquals("optional", result.getLocalName());
        assertEquals("datatypeLib", result.getAttribute("datatypeLibrary"));
        assertEquals("namespace", result.getAttribute("ns"));
        assertEquals("xmlnsDefault", result.getAttribute("xmlns"));
        assertEquals(2, result.getChildNodes().getLength());
        Element child = (Element) result.getChildNodes().item(0);
        assertEquals("empty", child.getLocalName());
        Element child2 = (Element) result.getChildNodes().item(1);
        assertEquals("text", child2.getLocalName());
    }

    /**
     * Test of invalid case of createNodeForPattern method with Optional and MissingPatternException, of class PatternWriter.
     * @throws Exception
     */
    @Test
    public void testInvalidCaseCreateNodeForPatternWithOptionalAndMissingPatternException() throws Exception {
        try {
            Optional pattern = new Optional();

            PatternWriter instance = new PatternWriter(rngDocument, rootElementNamespaceList);
            instance.createNodeForPattern(pattern);

        } catch (MissingPatternException e) {
            return;
        }
        fail("There is no pattern under a optional element, but this was not detected.");
    }

    /**
     * Test of valid case of createNodeForPattern method with Mixed, of class PatternWriter.
     * @throws Exception
     */
    @Test
    public void testValidCaseCreateNodeForPatternWithMixed() throws Exception {
        Mixed pattern = new Mixed();
        pattern.setAttributeDatatypeLibrary("datatypeLib");
        pattern.setAttributeNamespace("namespace");
        pattern.setDefaultNamespace("xmlnsDefault");
        pattern.addPattern(new Empty());
        pattern.addPattern(new Text());

        PatternWriter instance = new PatternWriter(rngDocument, rootElementNamespaceList);
        Element result = instance.createNodeForPattern(pattern);

        assertEquals("mixed", result.getLocalName());
        assertEquals("datatypeLib", result.getAttribute("datatypeLibrary"));
        assertEquals("namespace", result.getAttribute("ns"));
        assertEquals("xmlnsDefault", result.getAttribute("xmlns"));
        assertEquals(2, result.getChildNodes().getLength());
        Element child = (Element) result.getChildNodes().item(0);
        assertEquals("empty", child.getLocalName());
        Element child2 = (Element) result.getChildNodes().item(1);
        assertEquals("text", child2.getLocalName());
    }

    /**
     * Test of invalid case of createNodeForPattern method with Mixed and MissingPatternException, of class PatternWriter.
     * @throws Exception
     */
    @Test
    public void testInvalidCaseCreateNodeForPatternWithMixedAndMissingPatternException() throws Exception {
        try {
            Mixed pattern = new Mixed();

            PatternWriter instance = new PatternWriter(rngDocument, rootElementNamespaceList);
            instance.createNodeForPattern(pattern);

        } catch (MissingPatternException e) {
            return;
        }
        fail("There is no pattern under a mixed element, but this was not detected.");
    }

    /**
     * Test of valid case of createNodeForPattern method with List, of class PatternWriter.
     * @throws Exception
     */
    @Test
    public void testValidCaseCreateNodeForPatternWithList() throws Exception {
        List pattern = new List();
        pattern.setAttributeDatatypeLibrary("datatypeLib");
        pattern.setAttributeNamespace("namespace");
        pattern.setDefaultNamespace("xmlnsDefault");
        pattern.addPattern(new Empty());
        pattern.addPattern(new Text());

        PatternWriter instance = new PatternWriter(rngDocument, rootElementNamespaceList);
        Element result = instance.createNodeForPattern(pattern);

        assertEquals("list", result.getLocalName());
        assertEquals("datatypeLib", result.getAttribute("datatypeLibrary"));
        assertEquals("namespace", result.getAttribute("ns"));
        assertEquals("xmlnsDefault", result.getAttribute("xmlns"));
        assertEquals(2, result.getChildNodes().getLength());
        Element child = (Element) result.getChildNodes().item(0);
        assertEquals("empty", child.getLocalName());
        Element child2 = (Element) result.getChildNodes().item(1);
        assertEquals("text", child2.getLocalName());
    }

    /**
     * Test of invalid case of createNodeForPattern method with List and MissingPatternException, of class PatternWriter.
     * @throws Exception
     */
    @Test
    public void testInvalidCaseCreateNodeForPatternWithListAndMissingPatternException() throws Exception {
        try {
            List pattern = new List();

            PatternWriter instance = new PatternWriter(rngDocument, rootElementNamespaceList);
            instance.createNodeForPattern(pattern);

        } catch (MissingPatternException e) {
            return;
        }
        fail("There is no pattern under a list element, but this was not detected.");
    }

    /**
     * Test of valid case of createNodeForPattern method with Group, of class PatternWriter.
     * @throws Exception
     */
    @Test
    public void testValidCaseCreateNodeForPatternWithGroup() throws Exception {
        Group pattern = new Group();
        pattern.setAttributeDatatypeLibrary("datatypeLib");
        pattern.setAttributeNamespace("namespace");
        pattern.setDefaultNamespace("xmlnsDefault");
        pattern.addPattern(new Empty());
        pattern.addPattern(new Text());

        PatternWriter instance = new PatternWriter(rngDocument, rootElementNamespaceList);
        Element result = instance.createNodeForPattern(pattern);

        assertEquals("group", result.getLocalName());
        assertEquals("datatypeLib", result.getAttribute("datatypeLibrary"));
        assertEquals("namespace", result.getAttribute("ns"));
        assertEquals("xmlnsDefault", result.getAttribute("xmlns"));
        assertEquals(2, result.getChildNodes().getLength());
        Element child = (Element) result.getChildNodes().item(0);
        assertEquals("empty", child.getLocalName());
        Element child2 = (Element) result.getChildNodes().item(1);
        assertEquals("text", child2.getLocalName());
    }

    /**
     * Test of invalid case of createNodeForPattern method with Group and MissingPatternException, of class PatternWriter.
     * @throws Exception
     */
    @Test
    public void testInvalidCaseCreateNodeForPatternWithGroupAndMissingPatternException() throws Exception {
        try {
            Group pattern = new Group();

            PatternWriter instance = new PatternWriter(rngDocument, rootElementNamespaceList);
            instance.createNodeForPattern(pattern);

        } catch (MissingPatternException e) {
            return;
        }
        fail("There is no pattern under a group element, but this was not detected.");
    }

    /**
     * Test of valid case of createNodeForPattern method with Empty, of class PatternWriter.
     * @throws Exception
     */
    @Test
    public void testValidCaseCreateNodeForPatternWithEmpty() throws Exception {
        Empty pattern = new Empty();
        pattern.setAttributeDatatypeLibrary("datatypeLib");
        pattern.setAttributeNamespace("namespace");
        pattern.setDefaultNamespace("xmlnsDefault");

        PatternWriter instance = new PatternWriter(rngDocument, rootElementNamespaceList);
        Element result = instance.createNodeForPattern(pattern);

        assertEquals("empty", result.getLocalName());
        assertEquals("datatypeLib", result.getAttribute("datatypeLibrary"));
        assertEquals("namespace", result.getAttribute("ns"));
        assertEquals("xmlnsDefault", result.getAttribute("xmlns"));
    }

    /**
     * Test of valid case of createNodeForPattern method with NotAllowed, of class PatternWriter.
     * @throws Exception
     */
    @Test
    public void testValidCaseCreateNodeForPatternWithNotAllowed() throws Exception {
        NotAllowed pattern = new NotAllowed();
        pattern.setAttributeDatatypeLibrary("datatypeLib");
        pattern.setAttributeNamespace("namespace");
        pattern.setDefaultNamespace("xmlnsDefault");

        PatternWriter instance = new PatternWriter(rngDocument, rootElementNamespaceList);
        Element result = instance.createNodeForPattern(pattern);

        assertEquals("notAllowed", result.getLocalName());
        assertEquals("datatypeLib", result.getAttribute("datatypeLibrary"));
        assertEquals("namespace", result.getAttribute("ns"));
        assertEquals("xmlnsDefault", result.getAttribute("xmlns"));
    }

    /**
     * Test of valid case of createNodeForPattern method with Text, of class PatternWriter.
     * @throws Exception
     */
    @Test
    public void testValidCaseCreateNodeForPatternWithText() throws Exception {
        Text pattern = new Text();
        pattern.setAttributeDatatypeLibrary("datatypeLib");
        pattern.setAttributeNamespace("namespace");
        pattern.setDefaultNamespace("xmlnsDefault");

        PatternWriter instance = new PatternWriter(rngDocument, rootElementNamespaceList);
        Element result = instance.createNodeForPattern(pattern);

        assertEquals("text", result.getLocalName());
        assertEquals("datatypeLib", result.getAttribute("datatypeLibrary"));
        assertEquals("namespace", result.getAttribute("ns"));
        assertEquals("xmlnsDefault", result.getAttribute("xmlns"));
    }

    /**
     * Test of valid case of createNodeForPattern method with ParentRef, of class PatternWriter.
     * @throws Exception
     */
    @Test
    public void testValidCaseCreateNodeForPatternWithParentRef() throws Exception {
        Grammar grammar = new Grammar();
        Define define = new Define("test");
        define.addPattern(new Text());
        grammar.registerDefinePatternInLookUpTable(define);

        ParentRef pattern = new ParentRef(grammar.getDefineLookUpTable().getReference("test"), grammar, new Grammar());

        pattern.setAttributeDatatypeLibrary("datatypeLib");
        pattern.setAttributeNamespace("namespace");
        pattern.setDefaultNamespace("xmlnsDefault");

        PatternWriter instance = new PatternWriter(rngDocument, rootElementNamespaceList);
        Element result = instance.createNodeForPattern(pattern);

        assertEquals("parentRef", result.getLocalName());
        assertEquals("test", result.getAttribute("name"));
        assertEquals("datatypeLib", result.getAttribute("datatypeLibrary"));
        assertEquals("namespace", result.getAttribute("ns"));
        assertEquals("xmlnsDefault", result.getAttribute("xmlns"));
    }

    /**
     * Test of invalid case of createNodeForPattern method with Ref and PatternReferencedButNotDefinedException, of class PatternWriter.
     * @throws Exception
     */
//    @Test
//    public void testInvalidCaseCreateNodeForPatternWithParentRefAndPatternReferencedButNotDefinedException() throws Exception {
//        try {
//            Grammar grammar = new Grammar();
//            Define define = new Define("test");
//            grammar.registerDefinePatternInLookUpTable(define);
//            ParentRef pattern = new ParentRef(grammar.getDefineLookUpTable().getReference("test"));
//            PatternWriter instance = new PatternWriter(rngDocument, rootElementNamespaceList);
//
//            instance.createNodeForPattern(pattern);
//
//        } catch (PatternReferencedButNotDefinedException e) {
//            return;
//        }
//        fail("There is no content in the define-element, so the ParentRef has been registered but not defined in the given grammar. This was not detected.");
//    }

    /**
     * Test of invalid case of createNodeForPattern method with ParentRef and InvalidNCNameException, of class PatternWriter.
     * @throws Exception
     */
    @Test
    public void testInvalidCaseCreateNodeForPatternWithParentRefAndInvalidNCNameException() throws Exception {
        try {
            Grammar grammar = new Grammar();
            Define define = new Define("he\\llo");
            define.addPattern(new Text());
            grammar.registerDefinePatternInLookUpTable(define);
            ParentRef pattern = new ParentRef(grammar.getDefineLookUpTable().getReference("he\\llo"), grammar, new Grammar());
            PatternWriter instance = new PatternWriter(rngDocument, rootElementNamespaceList);

            instance.createNodeForPattern(pattern);

        } catch (InvalidNCNameException e) {
            return;
        }
        fail("There is an invalid NCName in the ParentRef, but this was not detected.");
    }

    /**
     * Test of valid case of createNodeForPattern method with ExternalRef, of class PatternWriter.
     * @throws Exception
     */
    @Test
    public void testValidCaseCreateNodeForPatternWithExternalRef() throws Exception {

        ExternalRef pattern = new ExternalRef("http://www.relaxng.org/example/rng.rng");

        pattern.setAttributeDatatypeLibrary("datatypeLib");
        pattern.setAttributeNamespace("namespace");
        pattern.setDefaultNamespace("xmlnsDefault");

        PatternWriter instance = new PatternWriter(rngDocument, rootElementNamespaceList);
        Element result = instance.createNodeForPattern(pattern);

        assertEquals("externalRef", result.getLocalName());
        assertEquals("http://www.relaxng.org/example/rng.rng", result.getAttribute("href"));
        assertEquals("datatypeLib", result.getAttribute("datatypeLibrary"));
        assertEquals("namespace", result.getAttribute("ns"));
        assertEquals("xmlnsDefault", result.getAttribute("xmlns"));
    }

    /**
     * Test of invalid case of createNodeForPattern method with ExternalRef and InvalidAnyUriException, of class PatternWriter.
     * @throws Exception
     */
    @Test
    public void testInvalidCaseCreateNodeForPatternWithExternalRefAndInvalidAnyUriException() throws Exception {
        try {
            ExternalRef pattern = new ExternalRef("http://www.relax\\@üng.org/example/rng.rng");

            PatternWriter instance = new PatternWriter(rngDocument, rootElementNamespaceList);

            instance.createNodeForPattern(pattern);

        } catch (InvalidAnyUriException e) {
            return;
        }
        fail("There is an invalid AnyUri in the ExternalRef, but this was not detected.");
    }

    /**
     * Test of invalid case of createNodeForPattern method with ExternalRef and MissingHrefException, of class PatternWriter.
     * @throws Exception
     */
    @Test
    public void testInvalidCaseCreateNodeForPatternWithExternalRefAndMissingHrefException() throws Exception {
        try {
            ExternalRef pattern = new ExternalRef(null);

            PatternWriter instance = new PatternWriter(rngDocument, rootElementNamespaceList);

            instance.createNodeForPattern(pattern);

        } catch (MissingHrefException e) {
            return;
        }
        fail("There is an invalid AnyUri in the ExternalRef, but this was not detected.");
    }

    /**
     * Test of valid case of createNodeForPattern method with Data, of class PatternWriter.
     * @throws Exception
     */
    @Test
    public void testValidCaseCreateNodeForPatternWithData() throws Exception {

        Data pattern = new Data("string");

        pattern.setAttributeDatatypeLibrary("datatypeLib");
        pattern.setAttributeNamespace("namespace");
        pattern.setDefaultNamespace("xmlnsDefault");
        pattern.addParam(new Param("paramName"));
        pattern.addParam(new Param("paramName2"));
        pattern.addExceptPattern(new Text());

        PatternWriter instance = new PatternWriter(rngDocument, rootElementNamespaceList);
        Element result = instance.createNodeForPattern(pattern);

        assertEquals("data", result.getLocalName());
        assertEquals("string", result.getAttribute("type"));
        assertEquals("datatypeLib", result.getAttribute("datatypeLibrary"));
        assertEquals("namespace", result.getAttribute("ns"));
        assertEquals("xmlnsDefault", result.getAttribute("xmlns"));

        assertEquals(3, result.getChildNodes().getLength());
        Element child = (Element) result.getChildNodes().item(0);
        assertEquals("param", child.getLocalName());

        assertEquals("paramName", child.getAttribute("name"));

        Element child2 = (Element) result.getChildNodes().item(1);
        assertEquals("param", child2.getLocalName());

        assertEquals("paramName2", child2.getAttribute("name"));

        Element child3 = (Element) result.getChildNodes().item(2);
        assertEquals("except", child3.getLocalName());

        Element grandChild3 = (Element) child3.getChildNodes().item(0);
        assertEquals("text", grandChild3.getLocalName());
    }

    /**
     * Test of invalid case of createNodeForPattern method with Data and InvalidNCNameException, of class PatternWriter.
     * @throws Exception
     */
    @Test
    public void testInvalidCaseCreateNodeForPatternWithDataAndInvalidNCNameException() throws Exception {
        try {
            Data pattern = new Data("st\\ring");

            PatternWriter instance = new PatternWriter(rngDocument, rootElementNamespaceList);

            instance.createNodeForPattern(pattern);

        } catch (InvalidNCNameException e) {
            return;
        }
        fail("There is an invalid NCName as type of a data-tag, but this was not detected.");
    }

    /**
     * Test of invalid case of createNodeForPattern method with Data and InvalidNCNameException for Param, of class PatternWriter.
     * @throws Exception
     */
    @Test
    public void testInvalidCaseCreateNodeForPatternWithDataAndInvalidNCNameExceptionForParam() throws Exception {
        try {
            Data pattern = new Data("string");
            pattern.addParam(new Param("para\\mName"));
            PatternWriter instance = new PatternWriter(rngDocument, rootElementNamespaceList);

            instance.createNodeForPattern(pattern);

        } catch (InvalidNCNameException e) {
            return;
        }
        fail("There is an invalid NCName for a param under a data-tag, but this was not detected.");
    }

    /**
     * Test of invalid case of createNodeForPattern method with Data and MissingPatternException, of class PatternWriter.
     * @throws Exception
     */
    @Test
    public void testInvalidCaseCreateNodeForPatternWithDataAndMissingPatternException() throws Exception {
        try {
            Data pattern = new Data("string");
            pattern.addParam(new Param("paramName"));
            pattern.addExceptPattern(null);
            PatternWriter instance = new PatternWriter(rngDocument, rootElementNamespaceList);

            instance.createNodeForPattern(pattern);

        } catch (MissingPatternException e) {
            return;
        }
        fail("There is a missing pattern as except content under a data-tag, but this was not detected.");
    }

    /**
     * Test of valid case of createNodeForPattern method with Value, of class PatternWriter.
     * @throws Exception
     */
    @Test
    public void testValidCaseCreateNodeForPatternWithValue() throws Exception {

        Value pattern = new Value("content");

        pattern.setType("integer");
        pattern.setAttributeDatatypeLibrary("datatypeLib");
        pattern.setAttributeNamespace("namespace");
        pattern.setDefaultNamespace("xmlnsDefault");


        PatternWriter instance = new PatternWriter(rngDocument, rootElementNamespaceList);
        Element result = instance.createNodeForPattern(pattern);

        assertEquals("value", result.getLocalName());
        assertEquals("integer", result.getAttribute("type"));
        assertEquals("datatypeLib", result.getAttribute("datatypeLibrary"));
        assertEquals("namespace", result.getAttribute("ns"));
        assertEquals("xmlnsDefault", result.getAttribute("xmlns"));
        assertEquals("content", result.getTextContent());

        pattern.setContent("myContent");
        Element result2 = instance.createNodeForPattern(pattern);
        assertEquals("myContent", result2.getTextContent());

    }

    /**
     * Test of invalid case of createNodeForPattern method with Value and InvalidNCNameException, of class PatternWriter.
     * @throws Exception
     */
    @Test
    public void testInvalidCaseCreateNodeForPatternWithValueAndInvalidNCNameException() throws Exception {
        try {
            Value pattern = new Value("string");
            pattern.setType("inte\\ger");
            PatternWriter instance = new PatternWriter(rngDocument, rootElementNamespaceList);

            instance.createNodeForPattern(pattern);

        } catch (InvalidNCNameException e) {
            return;
        }
        fail("There is an invalid NCName as type of a value-tag, but this was not detected.");
    }

    ////----------////
    /**
     * Test of valid case of createNodeForPattern method with Element, of class PatternWriter.
     * @throws Exception
     */
    @Test
    public void testValidCaseCreateNodeForPatternWithElement() throws Exception {
        de.tudortmund.cs.bonxai.relaxng.Element pattern = new de.tudortmund.cs.bonxai.relaxng.Element();
        pattern.setAttributeDatatypeLibrary("datatypeLib");
        pattern.setAttributeNamespace("namespace");
        pattern.setDefaultNamespace("xmlnsDefault");
        pattern.setNameAttribute("test");
        pattern.addPattern(new Empty());

        PatternWriter instance = new PatternWriter(rngDocument, rootElementNamespaceList);
        Element result = instance.createNodeForPattern(pattern);

        assertEquals("element", result.getLocalName());
        assertEquals("test", result.getAttribute("name"));
        assertEquals("datatypeLib", result.getAttribute("datatypeLibrary"));
        assertEquals("namespace", result.getAttribute("ns"));
        assertEquals("xmlnsDefault", result.getAttribute("xmlns"));
        assertEquals(1, result.getChildNodes().getLength());
        Element child = (Element) result.getChildNodes().item(0);
        assertEquals("empty", child.getLocalName());

        pattern.setNameAttribute(null);
        pattern.setNameClass(new Name("myName"));

        Element result2 = instance.createNodeForPattern(pattern);
        assertEquals(2, result2.getChildNodes().getLength());
        Element childNameClass = (Element) result2.getChildNodes().item(0);
        assertEquals("name", childNameClass.getLocalName());
        assertEquals("myName", childNameClass.getTextContent());
    }

    /**
     * Test of invalid case of createNodeForPattern method with Element and InvalidQNameException, of class PatternWriter.
     * @throws Exception
     */
    @Test
    public void testInvalidCaseCreateNodeForPatternWithElementAndInvalidQNameException() throws Exception {
        try {
            de.tudortmund.cs.bonxai.relaxng.Element pattern = new de.tudortmund.cs.bonxai.relaxng.Element();
            pattern.setNameAttribute("te{{st");
            PatternWriter instance = new PatternWriter(rngDocument, rootElementNamespaceList);

            instance.createNodeForPattern(pattern);

        } catch (InvalidQNameException e) {
            return;
        }
        fail("There is an invalid QName in the element, but this was not detected.");
    }

    /**
     * Test of invalid case of createNodeForPattern method with Element and MultipleNameException, of class PatternWriter.
     * @throws Exception
     */
    @Test
    public void testInvalidCaseCreateNodeForPatternWithElementAndMultipleNameException() throws Exception {
        try {
            de.tudortmund.cs.bonxai.relaxng.Element pattern = new de.tudortmund.cs.bonxai.relaxng.Element();
            pattern.addPattern(new Empty());
            pattern.setNameAttribute("hoihioh");
            pattern.setNameClass(new Name("myName"));
            PatternWriter instance = new PatternWriter(rngDocument, rootElementNamespaceList);

            instance.createNodeForPattern(pattern);

        } catch (MultipleNameException e) {
            return;
        }
        fail("There are multiple names for an element (name-element and NameClassContent), but this was not detected.");
    }

    /**
     * Test of invalid case of createNodeForPattern method with Element and MissingNameException, of class PatternWriter.
     * @throws Exception
     */
    @Test
    public void testInvalidCaseCreateNodeForPatternWithElementAndMissingNameException() throws Exception {
        try {
            de.tudortmund.cs.bonxai.relaxng.Element pattern = new de.tudortmund.cs.bonxai.relaxng.Element();
            pattern.addPattern(new Empty());
            PatternWriter instance = new PatternWriter(rngDocument, rootElementNamespaceList);

            instance.createNodeForPattern(pattern);

        } catch (MissingNameException e) {
            return;
        }
        fail("There is no name in the given element, but this was not detected.");
    }

    /**
     * Test of invalid case of createNodeForPattern method with Element and MissingPatternException, of class PatternWriter.
     * @throws Exception
     */
    @Test
    public void testInvalidCaseCreateNodeForPatternWithElementAndMissingPatternException() throws Exception {
        try {
            de.tudortmund.cs.bonxai.relaxng.Element pattern = new de.tudortmund.cs.bonxai.relaxng.Element();
            pattern.setNameAttribute("test");
            PatternWriter instance = new PatternWriter(rngDocument, rootElementNamespaceList);

            instance.createNodeForPattern(pattern);

        } catch (MissingPatternException e) {
            return;
        }
        fail("There is no pattern under the given element, but this was not detected.");
    }

    /**
     * Test of valid case of createNodeForPattern method with Grammar, of class PatternWriter.
     * @throws Exception
     */
    @Test
    public void testValidCaseCreateNodeForPatternWithGrammar() throws Exception {
        Grammar pattern = new Grammar();
        Define define = new Define("test");
        define.setCombineMethod(CombineMethod.choice);
        define.addPattern(new Text());

        pattern.setStartCombineMethod(CombineMethod.interleave);

        pattern.setAttributeDatatypeLibrary("datatypeLib");
        pattern.setAttributeNamespace("namespace");
        pattern.setDefaultNamespace("xmlnsDefault");

        pattern.addDefinePattern(define);

        de.tudortmund.cs.bonxai.relaxng.Element element = new de.tudortmund.cs.bonxai.relaxng.Element();
        element.setNameAttribute("elementName");
        element.addPattern(new Empty());
        pattern.addStartPattern(element);

        IncludeContent includeContent = new IncludeContent("myHref");
        includeContent.addDefinePattern(define);
        includeContent.addStartPattern(element);
        pattern.addIncludeContent(includeContent);

        PatternWriter instance = new PatternWriter(rngDocument, rootElementNamespaceList);
        Element result = instance.createNodeForPattern(pattern);

        assertEquals("grammar", result.getLocalName());
        assertEquals("datatypeLib", result.getAttribute("datatypeLibrary"));
        assertEquals("namespace", result.getAttribute("ns"));
        assertEquals("xmlnsDefault", result.getAttribute("xmlns"));

        assertEquals(3, result.getChildNodes().getLength());

        Element child = (Element) result.getChildNodes().item(0);
        assertEquals("include", child.getLocalName());
        assertEquals("myHref", child.getAttribute("href"));

        Element childInclude = (Element) child.getChildNodes().item(0);
        assertEquals("start", childInclude.getLocalName());
        assertEquals("interleave", childInclude.getAttribute("combine"));

        Element childIncludeStart = (Element) childInclude.getChildNodes().item(0);
        assertEquals("element", childIncludeStart.getLocalName());
        assertEquals("elementName", childIncludeStart.getAttribute("name"));

        Element childInclude2 = (Element) child.getChildNodes().item(1);
        assertEquals("define", childInclude2.getLocalName());
        assertEquals("test", childInclude2.getAttribute("name"));
        assertEquals("choice", childInclude2.getAttribute("combine"));

        Element childIncludeDefine = (Element) childInclude2.getChildNodes().item(0);
        assertEquals("text", childIncludeDefine.getLocalName());

        Element child2 = (Element) result.getChildNodes().item(1);
        assertEquals("start", child2.getLocalName());

        Element childStart = (Element) child2.getChildNodes().item(0);
        assertEquals("element", childStart.getLocalName());
        assertEquals("elementName", childStart.getAttribute("name"));

        Element child3 = (Element) result.getChildNodes().item(2);
        assertEquals("define", child3.getLocalName());
        assertEquals("test", child3.getAttribute("name"));
        assertEquals("choice", child3.getAttribute("combine"));

        Element childIncludeDefine2 = (Element) child3.getChildNodes().item(0);
        assertEquals("text", childIncludeDefine2.getLocalName());
    }

    /**
     * Test of invalid case of createNodeForPattern method with Grammar and InvalidHrefException, of class PatternWriter.
     * @throws Exception
     */
    @Test
    public void testInvalidCaseCreateNodeForPatternWithGrammarAndInvalidHrefException() throws Exception {
        try {
            Grammar pattern = new Grammar();

            IncludeContent includeContent = new IncludeContent("my\\Href");
            pattern.addIncludeContent(includeContent);

            PatternWriter instance = new PatternWriter(rngDocument, rootElementNamespaceList);
            instance.createNodeForPattern(pattern);

        } catch (InvalidHrefException e) {
            return;
        }
        fail("There is an invalid href in an include-tag under grammar, but this was not detected.");
    }

    /**
     * Test of invalid case of createNodeForPattern method with Grammar and EmptyPatternException, of class PatternWriter.
     * @throws Exception
     */
    @Test
    public void testInvalidCaseCreateNodeForPatternWithGrammarAndEmptyPatternException() throws Exception {
        try {
            Grammar pattern = new Grammar();

            PatternWriter instance = new PatternWriter(rngDocument, rootElementNamespaceList);
            instance.createNodeForPattern(pattern);

        } catch (EmptyPatternException e) {
            return;
        }
        fail("There is no content under a grammar, but this was not detected.");
    }

    /**
     * Test of invalid case of createNodeForPattern method with UnknownPatternException, of class PatternWriter.
     * @throws Exception
     */
    @Test
    public void testInvalidCaseCreateNodeForPatternWithUnknownPatternException() throws Exception {
        try {
            Pattern pattern = new Pattern() {};

            PatternWriter instance = new PatternWriter(rngDocument, rootElementNamespaceList);
            instance.createNodeForPattern(pattern);

        } catch (UnknownPatternException e) {
            return;
        }
        fail("There is an unknown pattern, but this was not detected.");
    }
}
