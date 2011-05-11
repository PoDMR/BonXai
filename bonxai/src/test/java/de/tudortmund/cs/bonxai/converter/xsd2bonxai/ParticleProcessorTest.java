package de.tudortmund.cs.bonxai.converter.xsd2bonxai;

import org.junit.Test;
import org.junit.Assert.*;

import de.tudortmund.cs.bonxai.common.*;
import de.tudortmund.cs.bonxai.xsd.*;
import de.tudortmund.cs.bonxai.bonxai.*;

public class ParticleProcessorTest extends ConverterTest {
    @Test
    public void testConvertElementSimpleType() {
        de.tudortmund.cs.bonxai.xsd.SimpleType xsdSimpleType = new de.tudortmund.cs.bonxai.xsd.SimpleType(
            "{}fooSimpleType",
            null
        );
        SymbolTableRef<Type> xsdSymTabRef = new SymbolTableRef<Type>("fooSimpleType", xsdSimpleType);
        de.tudortmund.cs.bonxai.xsd.Element fixture = new de.tudortmund.cs.bonxai.xsd.Element(
            "http://example.com/foo",
            "fooElement",
            xsdSymTabRef
        );

        SymbolTable<ElementGroupElement> bonxaiSymTab = new SymbolTable<ElementGroupElement>();
        ParticleProcessor processor = new ParticleProcessor(bonxaiSymTab);

        de.tudortmund.cs.bonxai.bonxai.Element actual = processor.convertElement(fixture);

        assertTrue(actual instanceof de.tudortmund.cs.bonxai.bonxai.Element);

        assertEquals("http://example.com/foo", actual.getNamespace());
        assertEquals("fooElement", actual.getName());
        assertNull(actual.getDefault());
        assertNull(actual.getFixed());

        assertEquals("fooSimpleType", actual.getType().getType());
        assertFalse(actual.isMissing());
    }

    @Test
    public void testConvertElementComplexType() {
        de.tudortmund.cs.bonxai.xsd.ComplexType xsdComplexType = new de.tudortmund.cs.bonxai.xsd.ComplexType(
            "{}complex",
            null
        );
        SymbolTableRef<Type> xsdSymTabRef = new SymbolTableRef<Type>("fooComplexType", xsdComplexType);
        de.tudortmund.cs.bonxai.xsd.Element fixture = new de.tudortmund.cs.bonxai.xsd.Element(
            "http://example.com/foo",
            "fooElement",
            xsdSymTabRef
        );

        SymbolTable<ElementGroupElement> bonxaiSymTab = new SymbolTable<ElementGroupElement>();
        ParticleProcessor processor = new ParticleProcessor(bonxaiSymTab);

        de.tudortmund.cs.bonxai.bonxai.Element actual = processor.convertElement(fixture);

        assertTrue(actual instanceof de.tudortmund.cs.bonxai.bonxai.Element);

        assertEquals("http://example.com/foo", actual.getNamespace());
        assertEquals("fooElement", actual.getName());
        assertNull(actual.getDefault());
        assertNull(actual.getFixed());

        assertNull(actual.getType());
        assertFalse(actual.isMissing());
    }

    @Test
    public void testConvertElementDefaultValue() {
        de.tudortmund.cs.bonxai.xsd.ComplexType xsdComplexType = new de.tudortmund.cs.bonxai.xsd.ComplexType(
            "{}fooComplexType",
            null
        );
        SymbolTableRef<Type> xsdSymTabRef = new SymbolTableRef<Type>("fooComplexType", xsdComplexType);
        de.tudortmund.cs.bonxai.xsd.Element fixture = new de.tudortmund.cs.bonxai.xsd.Element(
            "http://example.com/foo",
            "fooElement",
            xsdSymTabRef
        );
        fixture.setDefault("default");

        SymbolTable<ElementGroupElement> bonxaiSymTab = new SymbolTable<ElementGroupElement>();
        ParticleProcessor processor = new ParticleProcessor(bonxaiSymTab);

        de.tudortmund.cs.bonxai.bonxai.Element actual = processor.convertElement(fixture);

        assertTrue(actual instanceof de.tudortmund.cs.bonxai.bonxai.Element);

        assertEquals("http://example.com/foo", actual.getNamespace());
        assertEquals("fooElement", actual.getName());
        assertEquals("default", actual.getDefault());
        assertNull(actual.getFixed());

        assertNull(actual.getType());
        assertFalse(actual.isMissing());
    }

    @Test
    public void testConvertElementFixedValue() {
        de.tudortmund.cs.bonxai.xsd.ComplexType xsdComplexType = new de.tudortmund.cs.bonxai.xsd.ComplexType(
            "{}fooComplexType",
            null
        );
        SymbolTableRef<Type> xsdSymTabRef = new SymbolTableRef<Type>("fooComplexType", xsdComplexType);
        de.tudortmund.cs.bonxai.xsd.Element fixture = new de.tudortmund.cs.bonxai.xsd.Element(
            "http://example.com/foo",
            "fooElement",
            xsdSymTabRef
        );
        fixture.setFixed("fixed");

        SymbolTable<ElementGroupElement> bonxaiSymTab = new SymbolTable<ElementGroupElement>();
        ParticleProcessor processor = new ParticleProcessor(bonxaiSymTab);

        de.tudortmund.cs.bonxai.bonxai.Element actual = processor.convertElement(fixture);

        assertTrue(actual instanceof de.tudortmund.cs.bonxai.bonxai.Element);

        assertEquals("http://example.com/foo", actual.getNamespace());
        assertEquals("fooElement", actual.getName());
        assertNull(actual.getDefault());
        assertEquals("fixed", actual.getFixed());

        assertNull(actual.getType());
        assertFalse(actual.isMissing());
    }

    @Test
    public void testConvertElementNillable() {
        de.tudortmund.cs.bonxai.xsd.SimpleType xsdSimpleType = new de.tudortmund.cs.bonxai.xsd.SimpleType(
            "{}fooSimpleType",
            null
        );
        SymbolTableRef<Type> xsdSymTabRef = new SymbolTableRef<Type>("fooSimpleType", xsdSimpleType);
        de.tudortmund.cs.bonxai.xsd.Element fixture = new de.tudortmund.cs.bonxai.xsd.Element(
            "http://example.com/foo",
            "fooElement",
            xsdSymTabRef
        );
        fixture.setNillable();

        SymbolTable<ElementGroupElement> bonxaiSymTab = new SymbolTable<ElementGroupElement>();
        ParticleProcessor processor = new ParticleProcessor(bonxaiSymTab);

        de.tudortmund.cs.bonxai.bonxai.Element actual = processor.convertElement(fixture);

        assertTrue(actual instanceof de.tudortmund.cs.bonxai.bonxai.Element);

        assertEquals("http://example.com/foo", actual.getNamespace());
        assertEquals("fooElement", actual.getName());
        assertNull(actual.getDefault());
        assertNull(actual.getFixed());

        assertNotNull(actual.getType());
        assertEquals("fooSimpleType", actual.getType().getType());
        assertEquals("", actual.getType().getNamespace());
        assertTrue(actual.isMissing());
    }

    public void testConvertElementRef() throws SymbolAlreadyRegisteredException {
        de.tudortmund.cs.bonxai.xsd.ComplexType xsdComplexType = new de.tudortmund.cs.bonxai.xsd.ComplexType(
            "{}fooSimpleType",
            null
        );
        SymbolTableRef<Type> xsdSymTabRef = new SymbolTableRef<Type>("fooComplexType", xsdComplexType);
        de.tudortmund.cs.bonxai.xsd.Element element = new de.tudortmund.cs.bonxai.xsd.Element(
            "http://example.com/foo",
            "fooElement",
            xsdSymTabRef
        );
        element.setFixed("fixed");

        SymbolTable<de.tudortmund.cs.bonxai.xsd.Element> xsdSymbTab = new SymbolTable<de.tudortmund.cs.bonxai.xsd.Element>();
        xsdSymbTab.createReference("fooElement", element);

        ElementRef fixture = new ElementRef(xsdSymbTab.getReference("fooElement"));

        SymbolTable<ElementGroupElement> bonxaiSymTab = new SymbolTable<ElementGroupElement>();
        ParticleProcessor processor = new ParticleProcessor(bonxaiSymTab);

        de.tudortmund.cs.bonxai.bonxai.Element actual = processor.convertElementRef(fixture);

        assertTrue(actual instanceof de.tudortmund.cs.bonxai.bonxai.Element);

        assertEquals("http://example.com/foo", actual.getNamespace());
        assertEquals("fooElement", actual.getName());
        assertNull(actual.getDefault());
        assertEquals("fixed", actual.getFixed());

        assertNull(actual.getType());
    }

    public void testConvertGroupRef() throws SymbolAlreadyRegisteredException {
        de.tudortmund.cs.bonxai.xsd.Group xsdGroup = new de.tudortmund.cs.bonxai.xsd.Group("{}fooGroup", new SequencePattern());
        SymbolTableRef<de.tudortmund.cs.bonxai.xsd.Group> groupSymTabRef = new SymbolTableRef<de.tudortmund.cs.bonxai.xsd.Group>(
            "{}fooGroup",
            xsdGroup
        );

        GroupRef fixture = new GroupRef(groupSymTabRef);

        SymbolTable<ElementGroupElement> bonxaiSymTab = new SymbolTable<ElementGroupElement>();
        ParticleProcessor processor = new ParticleProcessor(bonxaiSymTab);

        de.tudortmund.cs.bonxai.common.GroupRef actual = processor.convertGroupRef(fixture);

        assertTrue(actual instanceof de.tudortmund.cs.bonxai.common.GroupRef);

        assertTrue(bonxaiSymTab.hasReference("fooGroup"));
        assertSame(bonxaiSymTab.getReference("fooGroup").getReference(), actual.getGroup());
    }

    public void testConvertAnyPattern() {
        AnyPattern anyPattern = new AnyPattern(null, "http://example.com");

        SymbolTable<ElementGroupElement> bonxaiSymTab = new SymbolTable<ElementGroupElement>();
        ParticleProcessor processor = new ParticleProcessor(bonxaiSymTab);

        AnyPattern actual = processor.convertAnyPattern(anyPattern);

        assertTrue(actual instanceof AnyPattern);

        assertSame(anyPattern, actual);
    }

    public void testConvertParticleContainer() {
        AllPattern all = new AllPattern();

        ChoicePattern choice = new ChoicePattern();
        choice.addParticle(all);

        SequencePattern sequence = new SequencePattern();
        sequence.addParticle(choice);
        sequence.addParticle(
            new de.tudortmund.cs.bonxai.xsd.Element(
                "http://example.com", "fooElement",
                null
            )
        );

        SymbolTable<ElementGroupElement> bonxaiSymTab = new SymbolTable<ElementGroupElement>();
        ParticleProcessor processor = new ParticleProcessor(bonxaiSymTab);

        ParticleContainer actual = processor.convertParticleContainer(sequence);

        assertTrue(actual instanceof SequencePattern);
        assertEquals(2, actual.getParticles().size());

        assertTrue(actual.getParticles().get(0) instanceof ChoicePattern);

        ChoicePattern actualChoice = (ChoicePattern) actual.getParticles().get(0);
        assertEquals(1, actualChoice.getParticles().size());

        assertTrue(actualChoice.getParticles().get(0) instanceof AllPattern);
        AllPattern actualAll = (AllPattern) actualChoice.getParticles().get(0);

        assertEquals(0, actualAll.getParticles().size());

        assertTrue(actual.getParticles().get(1) instanceof de.tudortmund.cs.bonxai.bonxai.Element);

        de.tudortmund.cs.bonxai.bonxai.Element actualElement = (de.tudortmund.cs.bonxai.bonxai.Element) actual.getParticles().get(1);

        assertEquals("fooElement", actualElement.getName());
        assertEquals("http://example.com", actualElement.getNamespace());
    }

    public void testParticleProcessListening() {
        AllPattern all = new AllPattern();

        ChoicePattern choice = new ChoicePattern();
        choice.addParticle(all);

        de.tudortmund.cs.bonxai.xsd.Element element = new de.tudortmund.cs.bonxai.xsd.Element(
            "http://example.com", "fooElement",
            null
        );

        SequencePattern sequence = new SequencePattern();
        sequence.addParticle(choice);
        sequence.addParticle(element);

        SymbolTable<ElementGroupElement> bonxaiSymTab = new SymbolTable<ElementGroupElement>();
        ParticleProcessor processor = new ParticleProcessor(bonxaiSymTab);

        ParticleProcessListenerMock listener = new ParticleProcessListenerMock();
        processor.attachListener(listener);

        ParticleContainer actual = processor.convertParticleContainer(sequence);

        assertEquals(4, listener.notifyCount);

        assertTrue(listener.notifiedParticles.elementAt(0) instanceof SequencePattern);
        assertSame(sequence, (SequencePattern) listener.notifiedParticles.elementAt(0));

        assertTrue(listener.notifiedParticles.elementAt(1) instanceof ChoicePattern);
        assertSame(choice, (ChoicePattern) listener.notifiedParticles.elementAt(1));

        assertTrue(listener.notifiedParticles.elementAt(2) instanceof AllPattern);
        assertSame(all, (AllPattern) listener.notifiedParticles.elementAt(2));

        assertTrue(listener.notifiedParticles.elementAt(3) instanceof de.tudortmund.cs.bonxai.xsd.Element);
        assertSame(element, (de.tudortmund.cs.bonxai.xsd.Element) listener.notifiedParticles.elementAt(3));
    }
}
