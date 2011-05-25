package de.tudortmund.cs.bonxai.xsd.tools;

import de.tudortmund.cs.bonxai.common.AnyAttribute;
import de.tudortmund.cs.bonxai.common.ChoicePattern;
import de.tudortmund.cs.bonxai.common.SequencePattern;
import de.tudortmund.cs.bonxai.xsd.Attribute;
import de.tudortmund.cs.bonxai.xsd.ComplexContentType;
import de.tudortmund.cs.bonxai.xsd.ComplexType;
import de.tudortmund.cs.bonxai.xsd.Element;
import de.tudortmund.cs.bonxai.xsd.XSDSchema;
import de.tudortmund.cs.bonxai.xsd.parser.XSDParser;
import de.tudortmund.cs.bonxai.xsd.writer.XSDWriter;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test of class GroupReplacer
 * @author Lars Schmidt
 */
public class GroupReplacerTest extends junit.framework.TestCase {

    /**
     * Test of replace method, of class GroupReplacer.
     * @throws Exception 
     */
    @Test
    public void testReplace() throws Exception {
        String uri = new String("/tests/de/tudortmund/cs/bonxai/xsd/tools/groupReplacerTests/group.xsd");
        uri = this.getClass().getResource(uri).getFile();
        
        XSDParser instance = new XSDParser(false, false);
        XSDSchema xsdSchema = instance.parse(uri);

        GroupReplacer groupReplacer = new GroupReplacer(xsdSchema);
        groupReplacer.replace();

        ComplexType complexType1 = (ComplexType) xsdSchema.getTypeSymbolTable().getReference("{}ordertype").getReference();
        assertEquals(3, complexType1.getAttributes().size());
        Attribute attribute1 = (Attribute) complexType1.getAttributes().get(0);
        assertEquals("{}firstname", attribute1.getName());
        Attribute attribute2 = (Attribute) complexType1.getAttributes().get(1);
        assertEquals("{}lastname", attribute2.getName());
        assertTrue(complexType1.getAttributes().get(2) instanceof AnyAttribute);

        ComplexContentType complexContent1 = (ComplexContentType) complexType1.getContent();
        assertTrue(complexContent1.getParticle() instanceof ChoicePattern);

        ChoicePattern choicePattern = (ChoicePattern) complexContent1.getParticle();
        assertTrue(choicePattern.getParticles().get(0) instanceof Element);
        assertTrue(choicePattern.getParticles().get(1) instanceof SequencePattern);

        SequencePattern sequencePattern2 = (SequencePattern) choicePattern.getParticles().get(1);
        assertEquals(2, sequencePattern2.getParticles().size());
        assertTrue(sequencePattern2.getParticles().get(0) instanceof Element);
        assertTrue(sequencePattern2.getParticles().get(1) instanceof SequencePattern);

        SequencePattern sequencePattern3 = (SequencePattern) sequencePattern2.getParticles().get(1);
        assertEquals(3, sequencePattern3.getParticles().size());
        assertTrue(sequencePattern3.getParticles().get(0) instanceof Element);
        assertTrue(sequencePattern3.getParticles().get(1) instanceof Element);
        assertTrue(sequencePattern3.getParticles().get(1) instanceof Element);

    }
}
