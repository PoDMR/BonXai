package de.tudortmund.cs.bonxai.converter.bonxai2xsd;

import de.tudortmund.cs.bonxai.common.*;
import de.tudortmund.cs.bonxai.bonxai.*;
import de.tudortmund.cs.bonxai.xsd.*;

import java.util.HashMap;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test of the main Bonxai-2-XSD conversion Class called "converter".
 */
public class ConverterTest extends XsdTestCase {

    /**
     * Test of convert method, of class Converter.
     * bonxai = null
     */
    @Test
    public void testConvertNull() {
        Bonxai bonxai = null;
        Bonxai2XSDConverter bonxai2xsdConverter = new Bonxai2XSDConverter();
        HashMap<String, XSDSchema> xsdSchemas = bonxai2xsdConverter.convert(bonxai);

        assertTrue(xsdSchemas.isEmpty());
    }

    /**
     * Test of convert method, of class Converter.
     *
     */
    @Test
    public void testConvertMinimalBonxai() {
        // Bonxai Schema
        Bonxai bonxai = new Bonxai();
        bonxai.setDeclaration(new Declaration(
                new ImportList(),
                new DataTypeList(),
                new NamespaceList(new DefaultNamespace("http://example.com/ns"))));

        Bonxai2XSDConverter bonxai2xsdConverter = new Bonxai2XSDConverter();

        // GrammarList in Bonxai Schema
        GrammarList grammar = new GrammarList();
        bonxai.setGrammarList(grammar);


        // Grammar:

        // grammar {
        //    /element = { string }
        // }

        // Expression
        Expression expression = new Expression();
        grammar.addExpression(expression);

        // AncestorPattern for expression1
        SingleSlashPrefixElement element = new SingleSlashPrefixElement("http://example.com/ns", "element");
        expression.setAncestorPattern(new AncestorPattern(element));

        // ChildPattern for expression
        de.tudortmund.cs.bonxai.common.GroupRef groupRefTo_a1 = new GroupRef(bonxai.getGroupSymbolTable().getReference("a"));

        de.tudortmund.cs.bonxai.common.SequencePattern groupRootSequence1 = new SequencePattern();
        groupRootSequence1.addParticle(groupRefTo_a1);

        ChildPattern elementChildPattern = new ChildPattern(null, new ElementPattern(new BonxaiType("", "string")));
        expression.setChildPattern(elementChildPattern);

        HashMap<String, XSDSchema> xsdSchemas = bonxai2xsdConverter.convert(bonxai);

        assertFalse(xsdSchemas.isEmpty());
        assertEquals("{http://example.com/ns}element", xsdSchemas.get("http://example.com/ns").getElements().getFirst().getName());
        assertEquals("http://example.com/ns", xsdSchemas.get("http://example.com/ns").getNamespaceList().getDefaultNamespace().getUri());

        assertSchemaSame(xsdSchemas.get("http://example.com/ns"), "simple");
    }

}
