package de.tudortmund.cs.bonxai.converter.xsd2relaxng;

import de.tudortmund.cs.bonxai.relaxng.Ref;
import de.tudortmund.cs.bonxai.relaxng.Choice;
import de.tudortmund.cs.bonxai.relaxng.Pattern;
import de.tudortmund.cs.bonxai.relaxng.Grammar;
import de.tudortmund.cs.bonxai.common.SymbolTableRef;
import de.tudortmund.cs.bonxai.converter.xsd2relaxng.exceptions.NoStartElementException;
import de.tudortmund.cs.bonxai.relaxng.Define;
import de.tudortmund.cs.bonxai.relaxng.RelaxNGSchema;
import de.tudortmund.cs.bonxai.xsd.Attribute;
import de.tudortmund.cs.bonxai.xsd.AttributeGroup;
import de.tudortmund.cs.bonxai.xsd.Group;
import de.tudortmund.cs.bonxai.xsd.XSDSchema;
import de.tudortmund.cs.bonxai.xsd.Type;
import de.tudortmund.cs.bonxai.xsd.parser.XSDParser;
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
        String uri = "tests/de/tudortmund/cs/bonxai/converter/xsd2relaxng/startElementConverterTests/startElements.xsd";

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
                new HashMap<de.tudortmund.cs.bonxai.xsd.Element, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<Group, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<Type, SymbolTableRef<LinkedList<Define>>>(),
                new LinkedHashMap<de.tudortmund.cs.bonxai.xsd.Element, LinkedHashSet<de.tudortmund.cs.bonxai.xsd.Element>>(),
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
        String uri = "tests/de/tudortmund/cs/bonxai/converter/xsd2relaxng/startElementConverterTests/oneStartElement_ref.xsd";

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
                new HashMap<de.tudortmund.cs.bonxai.xsd.Element, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<Group, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<Type, SymbolTableRef<LinkedList<Define>>>(),
                new LinkedHashMap<de.tudortmund.cs.bonxai.xsd.Element, LinkedHashSet<de.tudortmund.cs.bonxai.xsd.Element>>(),
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
                new HashMap<de.tudortmund.cs.bonxai.xsd.Element, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<Group, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<Type, SymbolTableRef<LinkedList<Define>>>(),
                new LinkedHashMap<de.tudortmund.cs.bonxai.xsd.Element, LinkedHashSet<de.tudortmund.cs.bonxai.xsd.Element>>(),
                new LinkedHashMap<Type, LinkedHashSet<Type>>());
        try {
            instance.startConversionWithToplevelElements();
        } catch (NoStartElementException error) {
            return;
        }
        fail("There is no start element in the given schema, but it wasn't detected.");
    }
}
