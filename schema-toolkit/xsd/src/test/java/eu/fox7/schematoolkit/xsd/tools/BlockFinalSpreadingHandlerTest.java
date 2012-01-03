package eu.fox7.schematoolkit.xsd.tools;

import eu.fox7.schematoolkit.common.ChoicePattern;
import eu.fox7.schematoolkit.common.CountingPattern;
import eu.fox7.schematoolkit.common.SequencePattern;
import eu.fox7.schematoolkit.xsd.om.ComplexContentType;
import eu.fox7.schematoolkit.xsd.om.ComplexType;
import eu.fox7.schematoolkit.xsd.om.ComplexTypeInheritanceModifier;
import eu.fox7.schematoolkit.xsd.om.Element;
import eu.fox7.schematoolkit.xsd.om.Group;
import eu.fox7.schematoolkit.xsd.om.XSDSchema;
import eu.fox7.schematoolkit.xsd.om.Element.Block;
import eu.fox7.schematoolkit.xsd.om.Element.Final;
import eu.fox7.schematoolkit.xsd.parser.XSDParser;
import eu.fox7.schematoolkit.xsd.tools.BlockFinalSpreadingHandler;

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
        String uri = "/tests/eu/fox7/bonxai/xsd/tools/blockFinalSpreadingHandlerTests/blockFinal.xsd";
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
