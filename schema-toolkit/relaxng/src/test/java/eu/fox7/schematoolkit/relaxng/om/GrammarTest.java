package eu.fox7.schematoolkit.relaxng.om;

import eu.fox7.schematoolkit.common.NamespaceList;
import eu.fox7.schematoolkit.relaxng.om.CombineMethod;
import eu.fox7.schematoolkit.relaxng.om.Define;
import eu.fox7.schematoolkit.relaxng.om.Empty;
import eu.fox7.schematoolkit.relaxng.om.ExternalRef;
import eu.fox7.schematoolkit.relaxng.om.Grammar;
import eu.fox7.schematoolkit.relaxng.om.IncludeContent;
import eu.fox7.schematoolkit.relaxng.om.Pattern;
import eu.fox7.schematoolkit.relaxng.om.RelaxNGSchema;
import eu.fox7.schematoolkit.relaxng.om.Text;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test of class Grammar
 * @author Lars Schmidt
 */
public class GrammarTest extends junit.framework.TestCase {

    /**
     * Test of getStartPatterns method, of class Grammar.
     */
    @Test
    public void testGetStartPatterns() {
        Pattern pattern = new ExternalRef("http://www.example.org/rng.rng");
        Grammar instance = new Grammar("defaultNamespace");
        instance.addStartPattern(pattern);
        assertEquals(pattern, instance.getStartPatterns().getFirst());
    }

    /**
     * Test of addStartPattern method, of class Grammar.
     */
    @Test
    public void testAddStartPattern() {
        Pattern pattern = new ExternalRef("http://www.example.org/rng.rng");
        Grammar instance = new Grammar("defaultNamespace");
        instance.addStartPattern(pattern);
        assertEquals(pattern, instance.getStartPatterns().getFirst());
    }

    /**
     * Test of getDefaultNamespace method, of class Grammar.
     */
    @Test
    public void testGetDefaultNamespace() {
        Grammar instance = new Grammar("http://www.example.org/");
        String expResult = "http://www.example.org/";
        String result = instance.getDefaultNamespace();
        assertEquals(expResult, result);
    }

    /**
     * Test of setDefaultNamespace method, of class Grammar.
     */
    @Test
    public void testSetDefaultNamespace() {
        Grammar instance = new Grammar("http://www.example.org/");
        instance.setDefaultNamespace("http://www.myDomain.org/");
        String expResult = "http://www.myDomain.org/";
        String result = instance.getDefaultNamespace();
        assertEquals(expResult, result);
    }

    /**
     * Test of getDefineLookUpTable method, of class Grammar.
     */
    @Test
    public void testLookUpTable() {
        Grammar instance = new Grammar();
        Define defineOne = new Define("defineOne");
        defineOne.addPattern(new Empty());
        Define defineTwo = new Define("defineTwo");
        defineTwo.addPattern(new Text());
        instance.addDefinePattern(defineOne);
        instance.addDefinePattern(defineTwo);
        java.util.List<Define> result = instance.getDefinedPattern("defineOne");
        assertEquals(defineOne, result);
        result = instance.getDefinedPattern("defineTwo");
        assertEquals(defineTwo, result);
    }

    /**
     * Test of getNamespaceList method, of class Grammar.
     */
    @Test
    public void testGetNamespaceList() {
        Grammar instance = new Grammar();
        NamespaceList result = instance.getNamespaceList();
        assertNotNull(result);
        assertEquals(RelaxNGSchema.RELAXNG_NAMESPACE, result.getDefaultNamespace().getUri());
        assertEquals(0, result.getNamespaces().size());
    }

    /**
     * Test of getDefinedPatternNames method, of class Grammar.
     */
    @Test
    public void testGetDefinedPatternNames() {
        Grammar instance = new Grammar();
        Define defineOne = new Define("defineOne");
        defineOne.addPattern(new Empty());
        Define defineTwo = new Define("defineTwo");
        defineTwo.addPattern(new Text());
        instance.addDefinePattern(defineOne);
        instance.addDefinePattern(defineTwo);

        LinkedHashSet<String> defineNames = instance.getDefinedPatternNames();
        assertEquals(2, defineNames.size());
        assertTrue(defineNames.contains("defineOne"));
        assertTrue(defineNames.contains("defineTwo"));
    }

    /**
     * Test of getStartCombineMethod method, of class Grammar.
     */
    @Test
    public void testGetStartCombineMethod() {
        Grammar instance = new Grammar();
        CombineMethod expResult = CombineMethod.choice;
        instance.setStartCombineMethod(expResult);
        CombineMethod result = instance.getStartCombineMethod();
        assertEquals(expResult, result);
        instance.setStartCombineMethod(CombineMethod.interleave);
        assertEquals(CombineMethod.interleave, instance.getStartCombineMethod());
    }

    /**
     * Test of setStartCombineMethod method, of class Grammar.
     */
    @Test
    public void testSetStartCombineMethod() {
        Grammar instance = new Grammar();
        CombineMethod expResult = CombineMethod.choice;
        instance.setStartCombineMethod(expResult);
        CombineMethod result = instance.getStartCombineMethod();
        assertEquals(expResult, result);
        instance.setStartCombineMethod(CombineMethod.interleave);
        assertEquals(CombineMethod.interleave, instance.getStartCombineMethod());
    }

    /**
     * Test of getDefinedPatternsFromLookUpTable method, of class Grammar.
     */
    @Test
    public void testGetDefinedPatternsFromLookUpTable() {
        Grammar instance = new Grammar();
        Define defineOne = new Define("defineOne");
        defineOne.addPattern(new Empty());
        Define defineTwo = new Define("defineOne");
        defineTwo.addPattern(new Text());
        instance.addDefinePattern(defineOne);
        instance.addDefinePattern(defineTwo);
        java.util.List<Define> result = instance.getDefinedPattern("defineOne");
        assertEquals(2, result.size());
        assertTrue(result.contains(defineOne));
        assertTrue(result.contains(defineTwo));
    }

    /**
     * Test of getParentGrammar method, of class Grammar.
     */
    @Test
    public void testGetParentGrammar() {
        Grammar instance = new Grammar();
        Grammar result = instance.getParentGrammar();
        assertNull(result);
        Grammar parentGrammar = new Grammar(RelaxNGSchema.RELAXNG_NAMESPACE);
        instance.setParentGrammar(parentGrammar);
        assertEquals(parentGrammar, instance.getParentGrammar());
    }

    /**
     * Test of setParentGrammar method, of class Grammar.
     */
    @Test
    public void testSetParentGrammar() {
        Grammar instance = new Grammar();
        Grammar result = instance.getParentGrammar();
        assertNull(result);
        Grammar parentGrammar = new Grammar(RelaxNGSchema.RELAXNG_NAMESPACE);
        instance.setParentGrammar(parentGrammar);
        assertEquals(parentGrammar, instance.getParentGrammar());
    }

    /**
     * Test of getIncludeContents method, of class Grammar.
     */
    @Test
    public void testGetIncludeContents() {
        Grammar instance = new Grammar();
        instance.addIncludeContent(new IncludeContent("hrefString"));
        LinkedList<IncludeContent> result = instance.getIncludeContents();
        assertEquals("hrefString", (result.getFirst()).getHref());
    }

    /**
     * Test of addIncludeContent method, of class Grammar.
     */
    @Test
    public void testAddIncludeContent() {
        Grammar instance = new Grammar();
        instance.addIncludeContent(new IncludeContent("hrefString"));
        LinkedList<IncludeContent> result = instance.getIncludeContents();
        assertEquals("hrefString", (result.getFirst()).getHref());
    }
}
