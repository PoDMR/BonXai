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

package eu.fox7.bonxai.converter.xsd2relaxng;

import eu.fox7.bonxai.converter.xsd2relaxng.StartElementConverter;
import eu.fox7.bonxai.converter.xsd2relaxng.XSD2RelaxNGConverter;
import eu.fox7.bonxai.converter.xsd2relaxng.exceptions.NoStartElementException;
import eu.fox7.schematoolkit.common.SymbolTableRef;
import eu.fox7.schematoolkit.relaxng.Choice;
import eu.fox7.schematoolkit.relaxng.Define;
import eu.fox7.schematoolkit.relaxng.Grammar;
import eu.fox7.schematoolkit.relaxng.Pattern;
import eu.fox7.schematoolkit.relaxng.Ref;
import eu.fox7.schematoolkit.relaxng.RelaxNGSchema;
import eu.fox7.schematoolkit.xsd.om.Attribute;
import eu.fox7.schematoolkit.xsd.om.AttributeGroup;
import eu.fox7.schematoolkit.xsd.om.Group;
import eu.fox7.schematoolkit.xsd.om.Type;
import eu.fox7.schematoolkit.xsd.om.XSDSchema;
import eu.fox7.schematoolkit.xsd.parser.XSDParser;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test of class StartElementConverter
 * @author Lars Schmidt
 */
public class StartElementConverterTest extends junit.framework.TestCase {

    /**
     * Test of startConversionWithToplevelElements method, of class StartElementConverter.
     */
    @Test
    public void testStartConversionWithToplevelElements() throws Exception {
        String uri = "/tests/eu/fox7/bonxai/converter/xsd2relaxng/startElementConverterTests/startElements.xsd";
        uri = this.getClass().getResource(uri).getFile();
        
        XSDParser xsdParser = new XSDParser(false, false);
        XSDSchema xmlSchema = xsdParser.parse(uri);
        RelaxNGSchema relaxNGSchema = new RelaxNGSchema();

        // Define a grammar as root pattern of the new RELAX NG schema
        Grammar grammar = new Grammar(XSD2RelaxNGConverter.RELAXNG_XMLSCHEMA_DATATYPE_NAMESPACE, "", XSD2RelaxNGConverter.RELAXNG_NAMESPACE);
        relaxNGSchema.setRootPattern(grammar);

        StartElementConverter instance = new StartElementConverter(
                xmlSchema,
                relaxNGSchema,
                new HashMap<Attribute, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<AttributeGroup, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<eu.fox7.schematoolkit.xsd.om.Element, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<Group, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<Type, SymbolTableRef<LinkedList<Define>>>(),
                new LinkedHashMap<eu.fox7.schematoolkit.xsd.om.Element, LinkedHashSet<eu.fox7.schematoolkit.xsd.om.Element>>(),
                new LinkedHashMap<Type, LinkedHashSet<Type>>());

        instance.startConversionWithToplevelElements();

        assertTrue(relaxNGSchema.getRootPattern() instanceof Grammar);
        Grammar grammar2 = (Grammar) relaxNGSchema.getRootPattern();

        assertTrue(grammar2.getStartPatterns().size() > 0);

        LinkedList<Pattern> startPatterList = grammar2.getStartPatterns();
        assertEquals(1, startPatterList.size());
        assertTrue(startPatterList.getFirst() instanceof Choice);
        assertEquals(4, ((Choice) startPatterList.getFirst()).getPatterns().size());
    }

    /**
     * Test of startConversionWithToplevelElements method, of class StartElementConverter.
     */
    @Test
    public void testStartConversionWithToplevelElements_one() throws Exception {
        String uri = "/tests/eu/fox7/bonxai/converter/xsd2relaxng/startElementConverterTests/oneStartElement_ref.xsd";
        uri = this.getClass().getResource(uri).getFile();
        
        XSDParser xsdParser = new XSDParser(false, false);
        XSDSchema xmlSchema = xsdParser.parse(uri);
        RelaxNGSchema relaxNGSchema = new RelaxNGSchema();

        // Define a grammar as root pattern of the new RELAX NG schema
        Grammar grammar = new Grammar(XSD2RelaxNGConverter.RELAXNG_XMLSCHEMA_DATATYPE_NAMESPACE, "", XSD2RelaxNGConverter.RELAXNG_NAMESPACE);
        relaxNGSchema.setRootPattern(grammar);

        StartElementConverter instance = new StartElementConverter(
                xmlSchema,
                relaxNGSchema,
                new HashMap<Attribute, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<AttributeGroup, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<eu.fox7.schematoolkit.xsd.om.Element, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<Group, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<Type, SymbolTableRef<LinkedList<Define>>>(),
                new LinkedHashMap<eu.fox7.schematoolkit.xsd.om.Element, LinkedHashSet<eu.fox7.schematoolkit.xsd.om.Element>>(),
                new LinkedHashMap<Type, LinkedHashSet<Type>>());

        instance.startConversionWithToplevelElements();

        assertTrue(relaxNGSchema.getRootPattern() instanceof Grammar);
        Grammar grammar2 = (Grammar) relaxNGSchema.getRootPattern();

        assertTrue(grammar2.getStartPatterns().size() > 0);

        LinkedList<Pattern> startPatterList = grammar2.getStartPatterns();
        assertEquals(1, startPatterList.size());
        assertTrue(startPatterList.getFirst() instanceof Ref);
    }


    /**
     * Test of startConversionWithToplevelElements method, of class StartElementConverter.
     */
    @Test
    public void testStartConversionWithToplevelElements_no_startelement() throws Exception {
        XSDSchema xmlSchema = new XSDSchema();
        RelaxNGSchema relaxNGSchema = new RelaxNGSchema();

        // Define a grammar as root pattern of the new RELAX NG schema
        Grammar grammar = new Grammar(XSD2RelaxNGConverter.RELAXNG_XMLSCHEMA_DATATYPE_NAMESPACE, "", XSD2RelaxNGConverter.RELAXNG_NAMESPACE);
        relaxNGSchema.setRootPattern(grammar);

        StartElementConverter instance = new StartElementConverter(
                xmlSchema,
                relaxNGSchema,
                new HashMap<Attribute, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<AttributeGroup, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<eu.fox7.schematoolkit.xsd.om.Element, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<Group, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<Type, SymbolTableRef<LinkedList<Define>>>(),
                new LinkedHashMap<eu.fox7.schematoolkit.xsd.om.Element, LinkedHashSet<eu.fox7.schematoolkit.xsd.om.Element>>(),
                new LinkedHashMap<Type, LinkedHashSet<Type>>());
        try {
            instance.startConversionWithToplevelElements();
        } catch (NoStartElementException error) {
            return;
        }
        fail("There is no start element in the given schema, but it wasn't detected.");
    }
}
