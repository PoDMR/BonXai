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

package eu.fox7.schematoolkit.converter.relaxng2xsd;

import eu.fox7.schematoolkit.converter.relaxng2xsd.PatternInformationCollector;
import eu.fox7.schematoolkit.relaxng.*;
//import eu.fox7.schematoolkit.relaxng.writer.RNGWriter;
import java.util.HashMap;
import java.util.HashSet;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test of class PatternInformationCollector
 * @author Lars Schmidt
 */
public class PatternInformationCollectorTest extends junit.framework.TestCase {

    /**
     * Test of collectData method, of class PatternInformationCollector.
     * @throws Exception
     */
    @Test
    public void testCollectData_element_attribute_text() throws Exception {

        Element element = new Element();
        element.setNameAttribute("root");

        Element childElement = new Element();
        childElement.setNameAttribute("child");
        element.addPattern(childElement);

        Text text = new Text();
        childElement.addPattern(text);

        Attribute attribute = new Attribute();
        attribute.setNameAttribute("childAttribute");
        childElement.addPattern(attribute);

        RelaxNGSchema relaxNGSchema = new RelaxNGSchema(element);

        // PatternInformationCollector
        PatternInformationCollector relaxNGPatternDataCollector = new PatternInformationCollector(relaxNGSchema);
        relaxNGPatternDataCollector.collectData();

        HashMap<Pattern, HashSet<String>> patternIntel = relaxNGPatternDataCollector.getPatternIntel();

        assertEquals(1, patternIntel.get(element).size());
        assertEquals(2, patternIntel.get(childElement).size());
        assertEquals(null, patternIntel.get(attribute));
        assertEquals(null, patternIntel.get(text));

        assertTrue(patternIntel.get(element).contains("element"));
        assertTrue(patternIntel.get(childElement).contains("attribute"));
        assertTrue(patternIntel.get(childElement).contains("text"));
    }

    /**
     * Test of collectData method, of class PatternInformationCollector.
     * @throws Exception
     */
    @Test
    public void testCollectData_data_value() throws Exception {

        Element element = new Element();
        element.setNameAttribute("root");

        Attribute attribute = new Attribute();
        attribute.setNameAttribute("childAttributeOne");
        element.addPattern(attribute);

        Attribute attribute2 = new Attribute();
        attribute2.setNameAttribute("childAttributeTwo");
        element.addPattern(attribute2);

        Data data = new Data("string");
        attribute.setPattern(data);

        Value value = new Value("myValue");
        attribute2.setPattern(value);

        RelaxNGSchema relaxNGSchema = new RelaxNGSchema(element);

        // PatternInformationCollector
        PatternInformationCollector relaxNGPatternDataCollector = new PatternInformationCollector(relaxNGSchema);
        relaxNGPatternDataCollector.collectData();

        HashMap<Pattern, HashSet<String>> patternIntel = relaxNGPatternDataCollector.getPatternIntel();

        assertEquals(1, patternIntel.get(element).size());
        assertEquals(1, patternIntel.get(attribute).size());
        assertEquals(1, patternIntel.get(attribute2).size());

        assertTrue(patternIntel.get(element).contains("attribute"));
        assertTrue(patternIntel.get(attribute).contains("data"));
        assertTrue(patternIntel.get(attribute2).contains("value"));
    }

    /**
     * Test of collectData method, of class PatternInformationCollector.
     * @throws Exception
     */
    @Test
    public void testCollectData_empty_notAllowed() throws Exception {

        Element element = new Element();
        element.setNameAttribute("root");

        Element childElement = new Element();
        childElement.setNameAttribute("child");
        element.addPattern(childElement);

        Empty empty = new Empty();
        childElement.addPattern(empty);

        Attribute attribute = new Attribute();
        attribute.setNameAttribute("childAttribute");
        childElement.addPattern(attribute);

        attribute.setPattern(new NotAllowed());

        RelaxNGSchema relaxNGSchema = new RelaxNGSchema(element);

        // PatternInformationCollector
        PatternInformationCollector relaxNGPatternDataCollector = new PatternInformationCollector(relaxNGSchema);
        relaxNGPatternDataCollector.collectData();

        HashMap<Pattern, HashSet<String>> patternIntel = relaxNGPatternDataCollector.getPatternIntel();

        assertEquals(1, patternIntel.get(element).size());
        assertEquals(2, patternIntel.get(childElement).size());
        assertEquals(1, patternIntel.get(attribute).size());

        assertTrue(patternIntel.get(element).contains("element"));
        assertTrue(patternIntel.get(childElement).contains("attribute"));
        assertTrue(patternIntel.get(childElement).contains("empty"));
        assertTrue(patternIntel.get(attribute).contains("notAllowed"));
    }

    /**
     * Test of collectData method, of class PatternInformationCollector.
     * @throws Exception
     */
    @Test
    public void testCollectData_mixed() throws Exception {

        Element element = new Element();
        element.setNameAttribute("root");

        Element childElement = new Element();
        childElement.setNameAttribute("child");

        Mixed mixed = new Mixed();
        mixed.addPattern(childElement);
        element.addPattern(mixed);

        Attribute attribute = new Attribute();
        attribute.setNameAttribute("childAttribute");
        childElement.addPattern(attribute);

        RelaxNGSchema relaxNGSchema = new RelaxNGSchema(element);

        // PatternInformationCollector
        PatternInformationCollector relaxNGPatternDataCollector = new PatternInformationCollector(relaxNGSchema);
        relaxNGPatternDataCollector.collectData();

        HashMap<Pattern, HashSet<String>> patternIntel = relaxNGPatternDataCollector.getPatternIntel();

        assertEquals(2, patternIntel.get(element).size());
        assertEquals(1, patternIntel.get(childElement).size());
        assertEquals(null, patternIntel.get(attribute));

        assertTrue(patternIntel.get(element).contains("element"));
        assertTrue(patternIntel.get(element).contains("mixed"));
        assertTrue(patternIntel.get(childElement).contains("attribute"));

    }

    /**
     * Test of collectData method, of class PatternInformationCollector.
     * @throws Exception
     */
    @Test
    public void testCollectData_optional() throws Exception {

        Element element = new Element();
        element.setNameAttribute("root");

        Element childElement = new Element();
        childElement.setNameAttribute("child");

        Optional optional = new Optional();
        optional.addPattern(childElement);
        element.addPattern(optional);

        Attribute attribute = new Attribute();
        attribute.setNameAttribute("childAttribute");
        childElement.addPattern(attribute);

        RelaxNGSchema relaxNGSchema = new RelaxNGSchema(element);

        // PatternInformationCollector
        PatternInformationCollector relaxNGPatternDataCollector = new PatternInformationCollector(relaxNGSchema);
        relaxNGPatternDataCollector.collectData();

        HashMap<Pattern, HashSet<String>> patternIntel = relaxNGPatternDataCollector.getPatternIntel();

        assertEquals(1, patternIntel.get(element).size());
        assertEquals(2, patternIntel.get(childElement).size());
        assertEquals(null, patternIntel.get(attribute));

        assertTrue(patternIntel.get(element).contains("element"));
        assertTrue(patternIntel.get(childElement).contains("optional"));
        assertTrue(patternIntel.get(childElement).contains("attribute"));

    }

    /**
     * Test of collectData method, of class PatternInformationCollector.
     * @throws Exception
     */
    @Test
    public void testCollectData_optional_zeroOrMoreAttributes() throws Exception {

        Element element = new Element();
        element.setNameAttribute("root");

        Element childElement = new Element();
        childElement.setNameAttribute("child");

        element.addPattern(childElement);

        Attribute attribute = new Attribute();
        attribute.setNameAttribute("childAttribute");

        ZeroOrMore zeroOrMore = new ZeroOrMore();
        zeroOrMore.addPattern(attribute);

        childElement.addPattern(zeroOrMore);

        RelaxNGSchema relaxNGSchema = new RelaxNGSchema(element);

        // PatternInformationCollector
        PatternInformationCollector relaxNGPatternDataCollector = new PatternInformationCollector(relaxNGSchema);
        relaxNGPatternDataCollector.collectData();

        HashMap<Pattern, HashSet<String>> patternIntel = relaxNGPatternDataCollector.getPatternIntel();

        assertEquals(1, patternIntel.get(element).size());
        assertEquals(1, patternIntel.get(childElement).size());
        assertEquals(1, patternIntel.get(attribute).size());

        assertTrue(patternIntel.get(element).contains("element"));
        assertTrue(patternIntel.get(childElement).contains("attribute"));
        assertTrue(patternIntel.get(attribute).contains("optional"));

    }

    /**
     * Test of collectData method, of class PatternInformationCollector.
     * @throws Exception
     */
    @Test
    public void testCollectData_optional_choiceWithElementAndAttribute() throws Exception {

        Element element = new Element();
        element.setNameAttribute("root");

        Element childElement = new Element();
        childElement.setNameAttribute("child");
        childElement.addPattern(new Empty());

        Attribute attribute = new Attribute();
        attribute.setNameAttribute("rootAttribute");

        Choice choice = new Choice();
        choice.addPattern(attribute);
        choice.addPattern(childElement);

        element.addPattern(choice);

        RelaxNGSchema relaxNGSchema = new RelaxNGSchema(element);

        // PatternInformationCollector
        PatternInformationCollector relaxNGPatternDataCollector = new PatternInformationCollector(relaxNGSchema);
        relaxNGPatternDataCollector.collectData();

        HashMap<Pattern, HashSet<String>> patternIntel = relaxNGPatternDataCollector.getPatternIntel();

        assertEquals(2, patternIntel.get(element).size());
        assertEquals(2, patternIntel.get(childElement).size());
        assertEquals(1, patternIntel.get(attribute).size());

        assertTrue(patternIntel.get(element).contains("element"));
        assertTrue(patternIntel.get(childElement).contains("empty"));
        assertTrue(patternIntel.get(childElement).contains("optional"));
        assertTrue(patternIntel.get(attribute).contains("optional"));
    }

    /**
     * Test of getDataForPattern method, of class PatternInformationCollector.
     */
    @Test
    public void testGetDataForPattern() {

        Element element = new Element();
        element.setNameAttribute("root");

        Element childElement = new Element();
        childElement.setNameAttribute("child1");
        childElement.addPattern(new Empty());
        element.addPattern(childElement);

        RelaxNGSchema relaxNGSchema = new RelaxNGSchema(element);

        // PatternInformationCollector
        PatternInformationCollector relaxNGPatternDataCollector = new PatternInformationCollector(relaxNGSchema);
        relaxNGPatternDataCollector.collectData();

//        RNGWriter rngWriter = new RNGWriter(relaxNGSchema);
//        System.out.println(rngWriter.getRNGString());

        Element childElement2 = new Element();
        childElement2.setNameAttribute("child2");
        childElement2.addPattern(new Empty());

        Attribute attribute = new Attribute();
        attribute.setNameAttribute("rootAttribute");
        attribute.setPattern(new Data("string"));

        element.addPattern(attribute);
        element.addPattern(childElement2);

        HashMap<Pattern, HashSet<String>> patternIntel = relaxNGPatternDataCollector.getPatternIntel();

//        System.out.println(rngWriter.getRNGString());

        assertEquals(1, patternIntel.get(element).size());
        assertEquals(1, patternIntel.get(childElement).size());
        assertEquals(null, patternIntel.get(attribute));

        assertTrue(patternIntel.get(element).contains("element"));
        assertTrue(patternIntel.get(childElement).contains("empty"));

        relaxNGPatternDataCollector.getDataForPattern(childElement2);
        relaxNGPatternDataCollector.getDataForPattern(attribute);

        patternIntel = relaxNGPatternDataCollector.getPatternIntel();

        assertEquals(1, patternIntel.get(childElement2).size());
        assertEquals(1, patternIntel.get(attribute).size());

        assertTrue(patternIntel.get(attribute).contains("data"));
        assertTrue(patternIntel.get(childElement2).contains("empty"));
    }
}
