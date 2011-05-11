package de.tudortmund.cs.bonxai.relaxng;

import de.tudortmund.cs.bonxai.common.SymbolTable;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test of class IncludeContent
 * @author Lars Schmidt
 */
public class IncludeContentTest extends junit.framework.TestCase {

    /**
     * Test of getStartPatterns method, of class IncludeContent.
     */
    @Test
    public void testGetStartPatterns() {
        Pattern pattern = new ExternalRef("http://www.example.org/rng.rng");
        IncludeContent instance = new IncludeContent("http://www.example.org/rng.rng");
        instance.addStartPattern(pattern);
        assertEquals(pattern, instance.getStartPatterns().getFirst());
    }

    /**
     * Test of addStartPattern method, of class IncludeContent.
     */
    @Test
    public void testAddStartPattern() {
        Pattern pattern = new ExternalRef("http://www.example.org/rng.rng");
        IncludeContent instance = new IncludeContent("http://www.example.org/rng.rng");
        instance.addStartPattern(pattern);
        assertEquals(pattern, instance.getStartPatterns().getFirst());
    }

    /**
     * Test of getHref method, of class IncludeContent.
     */
    @Test
    public void testGetHref() {
        IncludeContent instance = new IncludeContent("http://www.myDomain.org/rng.rng");
        String expResult = "http://www.myDomain.org/rng.rng";
        String result = instance.getHref();
        assertEquals(expResult, result);
    }

    /**
     * Test of setHref method, of class IncludeContent.
     */
    @Test
    public void testSetHref() {
        IncludeContent instance = new IncludeContent("http://www.myDomain.org/rng.rng");
        instance.setHref("http://www.someOtherDomain.com/rng.rng");
        String expResult = "http://www.someOtherDomain.com/rng.rng";
        String result = instance.getHref();
        assertEquals(expResult, result);
    }

    /**
     * Test of getDefineLookUpTable method, of class IncludeContent.
     */
    @Test
    public void testGetDefineLookUpTable() {
        IncludeContent instance = new IncludeContent("http://www.myDomain.org/rng.rng");
        Define defineOne = new Define("defineOne");
        defineOne.addPattern(new Empty());
        Define defineTwo = new Define("defineTwo");
        defineTwo.addPattern(new Text());
        instance.addDefinePattern(defineOne);
        instance.addDefinePattern(defineTwo);
        SymbolTable<LinkedList<Define>> result = instance.getDefineLookUpTable();
        assertEquals(2, result.getAllReferencedObjects().size());
        assertEquals(2, result.getReferences().size());
    }

    /**
     * Test of addDefinePattern method, of class IncludeContent.
     */
    @Test
    public void testAddDefinePattern() {
        IncludeContent instance = new IncludeContent("http://www.myDomain.org/rng.rng");
        Define defineOne = new Define("defineOne");
        defineOne.addPattern(new Empty());
        Define defineTwo = new Define("defineTwo");
        defineTwo.addPattern(new Text());
        instance.addDefinePattern(defineOne);
        instance.addDefinePattern(defineTwo);
        SymbolTable<LinkedList<Define>> result = instance.getDefineLookUpTable();
        assertEquals(2, result.getAllReferencedObjects().size());
        assertEquals(2, result.getReferences().size());

        LinkedHashSet<String> defineNames = instance.getDefinedPatternNames();
        assertEquals(2, defineNames.size());
    }

    /**
     * Test of registerDefinePatternInLookUpTable method, of class IncludeContent.
     */
    @Test
    public void testRegisterDefinePatternInLookUpTable() {
        IncludeContent instance = new IncludeContent("http://www.myDomain.org/rng.rng");
        Define defineOne = new Define("defineOne");
        defineOne.addPattern(new Empty());
        Define defineTwo = new Define("defineTwo");
        defineTwo.addPattern(new Text());
        instance.registerDefinePatternInLookUpTable(defineOne);
        instance.registerDefinePatternInLookUpTable(defineTwo);
        SymbolTable<LinkedList<Define>> result = instance.getDefineLookUpTable();
        assertEquals(2, result.getAllReferencedObjects().size());
        assertEquals(2, result.getReferences().size());

        LinkedHashSet<String> defineNames = instance.getDefinedPatternNames();
        assertEquals(0, defineNames.size());
    }

    /**
     * Test of getDefinedPatternNames method, of class IncludeContent.
     */
    @Test
    public void testGetDefinedPatternNames() {
        IncludeContent instance = new IncludeContent("http://www.myDomain.org/rng.rng");
        Define defineOne = new Define("defineOne");
        defineOne.addPattern(new Empty());
        Define defineTwo = new Define("defineTwo");
        defineTwo.addPattern(new Text());
        instance.addDefinePattern(defineOne);
        instance.addDefinePattern(defineTwo);
        SymbolTable<LinkedList<Define>> result = instance.getDefineLookUpTable();
        assertEquals(2, result.getAllReferencedObjects().size());
        assertEquals(2, result.getReferences().size());

        LinkedHashSet<String> defineNames = instance.getDefinedPatternNames();
        assertEquals(2, defineNames.size());
        assertTrue(defineNames.contains("defineOne"));
        assertTrue(defineNames.contains("defineTwo"));
    }

    /**
     * Test of getDefinedPatternsFromLookUpTable method, of class IncludeContent.
     */
    @Test
    public void testGetDefinedPatternsFromLookUpTable() {
        IncludeContent instance = new IncludeContent("http://www.myDomain.org/rng.rng");
        Define defineOne = new Define("defineOne");
        defineOne.addPattern(new Empty());
        Define defineTwo = new Define("defineOne");
        defineTwo.addPattern(new Text());
        instance.registerDefinePatternInLookUpTable(defineOne);
        instance.registerDefinePatternInLookUpTable(defineTwo);
        LinkedList<Define> result = instance.getDefinedPatternsFromLookUpTable("defineOne");
        assertEquals(2, result.size());
        assertTrue(result.contains(defineOne));
        assertTrue(result.contains(defineTwo));
    }
}
