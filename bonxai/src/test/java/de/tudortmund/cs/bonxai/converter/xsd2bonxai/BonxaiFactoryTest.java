package de.tudortmund.cs.bonxai.converter.xsd2bonxai;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;

import de.tudortmund.cs.bonxai.xsd.XSDSchema;
import de.tudortmund.cs.bonxai.bonxai.Bonxai;

public class BonxaiFactoryTest extends junit.framework.TestCase {

    private XSDSchema schema;

    private TypeAutomaton typeAutomaton;

    private Bonxai bonxai;

    @Before
    public void setUp() {
        schema = new XSDSchema();
        typeAutomaton = new TypeAutomaton(null);
        bonxai = new Bonxai();
    }

    @After
    public void tearDown() {
        schema = null;
        typeAutomaton = null;
        bonxai = null;
    }

    @Test
    public void testConvertNoConverter() {
        BonxaiFactory factory = new BonxaiFactory();

        Bonxai actual = factory.convert(schema, typeAutomaton);

        assertBonxaiEmpty(actual);
    }

    private void assertBonxaiEmpty(Bonxai bonxai) {
        assertNotNull(bonxai);

        assertNotNull(bonxai.getAttributeGroupElementSymbolTable());
        assertEquals(0, bonxai.getAttributeGroupElementSymbolTable().getAllReferencedObjects().size());

        assertNotNull(bonxai.getGroupSymbolTable());
        assertEquals(0, bonxai.getGroupSymbolTable().getAllReferencedObjects().size());

        assertNull(bonxai.getConstraintList());
        assertNull(bonxai.getDeclaration());
        assertNull(bonxai.getGrammarList());
        assertNull(bonxai.getGroupList());
    }

    @Test
    public void testConvertSingleConverter() {
        ConverterMock firstConverter = new ConverterMock();

        BonxaiFactory factory = new BonxaiFactory();
        factory.addConverter(firstConverter);

        Bonxai actual = factory.convert(schema, typeAutomaton);

        assertBonxaiEmpty(actual);

        assertEquals(1,(int) firstConverter.convertCount);
        assertSame(schema, firstConverter.schemas.elementAt(0));
        assertSame(typeAutomaton, firstConverter.automatons.elementAt(0));
        assertSame(actual, firstConverter.bonxais.elementAt(0));
    }

    @Test
    public void testConvertMultipleConverters() {
        ConverterMock firstConverter = new ConverterMock();
        ConverterMock secondConverter = new ConverterMock();

        BonxaiFactory factory = new BonxaiFactory();
        factory.addConverter(firstConverter);
        factory.addConverter(secondConverter);

        Bonxai actual = factory.convert(schema, typeAutomaton);

        assertBonxaiEmpty(bonxai);

        assertEquals(1, (int) firstConverter.convertCount);
        assertSame(schema, firstConverter.schemas.elementAt(0));
        assertSame(typeAutomaton, firstConverter.automatons.elementAt(0));
        assertSame(actual, firstConverter.bonxais.elementAt(0));

        assertEquals(1, (int) secondConverter.convertCount);
        assertSame(schema, secondConverter.schemas.elementAt(0));
        assertSame(typeAutomaton, secondConverter.automatons.elementAt(0));
        assertSame(actual, secondConverter.bonxais.elementAt(0));
    }
}
