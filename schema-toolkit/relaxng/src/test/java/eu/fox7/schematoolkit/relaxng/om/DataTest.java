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

import eu.fox7.schematoolkit.relaxng.om.Data;
import eu.fox7.schematoolkit.relaxng.om.Empty;
import eu.fox7.schematoolkit.relaxng.om.ExternalRef;
import eu.fox7.schematoolkit.relaxng.om.Param;
import eu.fox7.schematoolkit.relaxng.om.Pattern;
import eu.fox7.schematoolkit.relaxng.om.Text;
import static org.junit.Assert.*;

/**
 * Test of class Data
 * @author Lars Schmidt
 */
public class DataTest extends junit.framework.TestCase {

    /**
     * Test of getType method, of class Data.
     */
    @Test
    public void testGetType() {
        Data instance = new Data("type");
        String expResult = "type";
        String result = instance.getType();
        assertEquals(expResult, result);
    }

    /**
     * Test of getParams method, of class Data.
     */
    @Test
    public void testGetParams() {
        Param param = new Param("name1");
        Param param2 = new Param("2name");
        Param param3 = new Param("na3me");
        Data instance = new Data("type");
        instance.addParam(param);
        instance.addParam(param2);
        instance.addParam(param3);
        assertEquals(param, instance.getParams().getFirst());
        assertEquals(param2, instance.getParams().get(1));
        assertEquals(param3, instance.getParams().getLast());
    }

    /**
     * Test of addParam method, of class Data.
     */
    @Test
    public void testAddParam() {
        Param param = new Param("name1");
        Param param2 = new Param("2name");
        Param param3 = new Param("na3me");
        Data instance = new Data("type");
        instance.addParam(param);
        instance.addParam(param2);
        instance.addParam(param3);
        assertEquals(param, instance.getParams().getFirst());
        assertEquals(param2, instance.getParams().get(1));
        assertEquals(param3, instance.getParams().getLast());
    }

    /**
     * Test of getExceptPatterns method, of class Data.
     */
    @Test
    public void testGetExceptPatterns() {
        Pattern pattern = new Empty();
        Pattern pattern2 = new ExternalRef("http://www.example.org/rng.rng");
        Pattern pattern3 = new Text();
        Data instance = new Data("type");
        instance.addExceptPattern(pattern);
        instance.addExceptPattern(pattern2);
        instance.addExceptPattern(pattern3);
        assertEquals(pattern, instance.getExceptPatterns().getFirst());
        assertEquals(pattern2, instance.getExceptPatterns().get(1));
        assertEquals(pattern3, instance.getExceptPatterns().getLast());
    }

    /**
     * Test of addExceptPattern method, of class Data.
     */
    @Test
    public void testAddExceptPattern() {
        Pattern pattern = new Empty();
        Pattern pattern2 = new ExternalRef("http://www.example.org/rng.rng");
        Pattern pattern3 = new Text();
        Data instance = new Data("type");
        instance.addExceptPattern(pattern);
        instance.addExceptPattern(pattern2);
        instance.addExceptPattern(pattern3);
        assertEquals(pattern, instance.getExceptPatterns().getFirst());
        assertEquals(pattern2, instance.getExceptPatterns().get(1));
        assertEquals(pattern3, instance.getExceptPatterns().getLast());
    }
}
