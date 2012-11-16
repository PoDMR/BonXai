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

package eu.fox7.schematoolkit.relaxng.om;

import eu.fox7.schematoolkit.relaxng.om.Define;
import eu.fox7.schematoolkit.relaxng.om.Empty;
import eu.fox7.schematoolkit.relaxng.om.ExternalRef;
import eu.fox7.schematoolkit.relaxng.om.IncludeContent;
import eu.fox7.schematoolkit.relaxng.om.Pattern;
import eu.fox7.schematoolkit.relaxng.om.Text;

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
