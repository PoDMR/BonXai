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

import org.junit.Test;

import eu.fox7.schematoolkit.relaxng.om.CombineMethod;
import eu.fox7.schematoolkit.relaxng.om.Define;
import eu.fox7.schematoolkit.relaxng.om.Empty;
import eu.fox7.schematoolkit.relaxng.om.ExternalRef;
import eu.fox7.schematoolkit.relaxng.om.Pattern;
import eu.fox7.schematoolkit.relaxng.om.Text;
import static org.junit.Assert.*;

/**
 * Test of class Define
 * @author lightsabre
 */
public class DefineTest extends junit.framework.TestCase {


    /**
     * Test of getName method, of class Define.
     */
    @Test
    public void testGetName() {
        Define instance = new Define("myDefineName");
        String expResult = "myDefineName";
        String result = instance.getName();
        assertEquals(expResult, result);
    }

    /**
     * Test of setName method, of class Define.
     */
    @Test
    public void testSetName() {
        Define instance = new Define("myDefineName");
        instance.setName("newName");
        String expResult = "newName";
        String result = instance.getName();
        assertEquals(expResult, result);
    }

    /**
     * Test of getCombineMethod method, of class Define.
     */
    @Test
    public void testGetCombineMethod() {
        Define instance = new Define("myDefineName");
        CombineMethod expResult = CombineMethod.choice;
        instance.setCombineMethod(expResult);
        CombineMethod result = instance.getCombineMethod();
        assertEquals(expResult, result);
        instance.setCombineMethod(CombineMethod.interleave);
        assertEquals(CombineMethod.interleave, instance.getCombineMethod());
    }

    /**
     * Test of setCombineMethod method, of class Define.
     */
    @Test
    public void testSetCombineMethod() {
        Define instance = new Define("myDefineName");
        CombineMethod expResult = CombineMethod.choice;
        instance.setCombineMethod(expResult);
        CombineMethod result = instance.getCombineMethod();
        assertEquals(expResult, result);
        instance.setCombineMethod(CombineMethod.interleave);
        assertEquals(CombineMethod.interleave, instance.getCombineMethod());
    }

    /**
     * Test of getPatterns method, of class Define.
     */
    @Test
    public void testGetPatterns() {
        Pattern pattern = new Empty();
        Pattern pattern2 = new ExternalRef("http://www.example.org/rng.rng");
        Pattern pattern3 = new Text();
        Define instance = new Define("2");
        instance.addPattern(pattern);
        instance.addPattern(pattern2);
        instance.addPattern(pattern3);
        assertEquals(pattern, instance.getPatterns().getFirst());
        assertEquals(pattern2, instance.getPatterns().get(1));
        assertEquals(pattern3, instance.getPatterns().getLast());
    }

    /**
     * Test of addPattern method, of class Define.
     */
    @Test
    public void testAddPattern() {
        Pattern pattern = new Empty();
        Pattern pattern2 = new ExternalRef("http://www.example.org/rng.rng");
        Pattern pattern3 = new Text();
        Define instance = new Define("2");
        instance.addPattern(pattern);
        instance.addPattern(pattern2);
        instance.addPattern(pattern3);
        assertEquals(pattern, instance.getPatterns().getFirst());
        assertEquals(pattern2, instance.getPatterns().get(1));
        assertEquals(pattern3, instance.getPatterns().getLast());
    }

}