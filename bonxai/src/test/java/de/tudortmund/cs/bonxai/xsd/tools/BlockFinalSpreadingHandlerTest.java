package de.tudortmund.cs.bonxai.xsd.tools;

import de.tudortmund.cs.bonxai.xsd.Element;
import de.tudortmund.cs.bonxai.common.ChoicePattern;
import de.tudortmund.cs.bonxai.common.CountingPattern;
import de.tudortmund.cs.bonxai.common.SequencePattern;
import de.tudortmund.cs.bonxai.xsd.ComplexContentType;
import de.tudortmund.cs.bonxai.xsd.ComplexType;
import de.tudortmund.cs.bonxai.xsd.ComplexTypeInheritanceModifier;
import de.tudortmund.cs.bonxai.xsd.Element.Block;
import de.tudortmund.cs.bonxai.xsd.Element.Final;
import de.tudortmund.cs.bonxai.xsd.Group;
import de.tudortmund.cs.bonxai.xsd.parser.XSDParser;
import de.tudortmund.cs.bonxai.xsd.XSDSchema;
import org.junit.*;

/**
 * Test of class BlockFinalSpreadingHandler
 * @author Lars Schmidt
 */
public class BlockFinalSpreadingHandlerTest extends junit.framework.TestCase {

    /**
     * Test of spread method, of class BlockFinalSpreadingHandler.
     */
    @Test
    public void testSpread() throws Exception {
        String uri = "/tests/de/tudortmund/cs/bonxai/xsd/tools/blockFinalSpreadingHandlerTests/blockFinal.xsd";
        uri = this.getClass().getResource(uri).getFile();
        
        XSDParser xsdParser = new XSDParser(false, false);
        XSDSchema xmlSchema = xsdParser.parse(uri);

        BlockFinalSpreadingHandler instance = new BlockFinalSpreadingHandler();
        instance.spread(xmlSchema);

        assertTrue(xmlSchema.getElements().getFirst().getBlockModifiers().contains(Block.extension));
        assertFalse(xmlSchema.getElements().getFirst().getBlockModifiers().contains(Block.restriction));
        assertFalse(xmlSchema.getElements().getFirst().getBlockModifiers().contains(Block.substitution));

        assertTrue(xmlSchema.getElements().getFirst().getFinalModifiers().contains(Final.extension));
        assertTrue(xmlSchema.getElements().getFirst().getFinalModifiers().contains(Final.restriction));

        assertTrue(((ComplexType) xmlSchema.getTypeSymbolTable().getReference("{}ordertype").getReference()).getBlockModifiers().contains(ComplexTypeInheritanceModifier.Extension));
        assertFalse(((ComplexType) xmlSchema.getTypeSymbolTable().getReference("{}ordertype").getReference()).getBlockModifiers().contains(ComplexTypeInheritanceModifier.Restriction));

        assertTrue(((ComplexType) xmlSchema.getTypeSymbolTable().getReference("{}ordertype").getReference()).getFinalModifiers().contains(ComplexTypeInheritanceModifier.Extension));
        assertTrue(((ComplexType) xmlSchema.getTypeSymbolTable().getReference("{}ordertype").getReference()).getFinalModifiers().contains(ComplexTypeInheritanceModifier.Restriction));

        ComplexType complexType = ((ComplexType) xmlSchema.getTypeSymbolTable().getReference("{}ordertype").getReference());
        assertTrue(complexType.getContent() instanceof ComplexContentType);
        ComplexContentType complexContentType = (ComplexContentType) complexType.getContent();
        assertTrue(complexContentType.getParticle() instanceof ChoicePattern);
        ChoicePattern choicePattern = (ChoicePattern) complexContentType.getParticle();
        assertTrue(choicePattern.getParticles().getFirst() instanceof Element);
        Element element = (Element) choicePattern.getParticles().getFirst();

        assertTrue(element.getBlockModifiers().contains(Block.extension));
        assertFalse(element.getBlockModifiers().contains(Block.restriction));
        assertFalse(element.getBlockModifiers().contains(Block.substitution));

        Group group = xmlSchema.getGroups().getFirst();
        assertTrue(group.getParticleContainer() instanceof SequencePattern);

        SequencePattern sequencePattern = (SequencePattern) group.getParticleContainer();
        assertTrue(sequencePattern.getParticles().getFirst() instanceof Element);
        Element element2 = (Element) sequencePattern.getParticles().getFirst();

        assertFalse(element2.getBlockModifiers().contains(Block.extension));
        assertFalse(element2.getBlockModifiers().contains(Block.restriction));
        assertTrue(element2.getBlockModifiers().contains(Block.substitution));

        ChoicePattern choicePattern2 = (ChoicePattern) sequencePattern.getParticles().get(1);
        assertTrue(choicePattern2.getParticles().getFirst() instanceof CountingPattern);
        CountingPattern countingPattern = (CountingPattern) choicePattern2.getParticles().getFirst();
        Element element3 = (Element) countingPattern.getParticles().getFirst();

        assertTrue(element3.getBlockModifiers().contains(Block.extension));
        assertFalse(element3.getBlockModifiers().contains(Block.restriction));
        assertFalse(element3.getBlockModifiers().contains(Block.substitution));

    }
}
