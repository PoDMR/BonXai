package de.tudortmund.cs.bonxai.converter.bonxai2xsd;

import de.tudortmund.cs.bonxai.common.AllPattern;
import de.tudortmund.cs.bonxai.bonxai.Element;
import de.tudortmund.cs.bonxai.common.Group;
import de.tudortmund.cs.bonxai.common.GroupRef;
import de.tudortmund.cs.bonxai.common.Particle;
import de.tudortmund.cs.bonxai.common.SequencePattern;
import de.tudortmund.cs.bonxai.common.SymbolTable;
import de.tudortmund.cs.bonxai.bonxai.ElementGroupElement;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test of ParticleGroupRefReplacerTest
 */
public class ParticleGroupRefReplacerTest  extends junit.framework.TestCase {


    /**
     * Test of replaceGroupRefInParticle method, of class ParticleGroupRefReplacer.
     */
    @Test
    public void testReplaceGroupRefInParticle_elementParticle() {
        Particle particle = new Element("http://www.example.com", "elementName");
        SymbolTable<de.tudortmund.cs.bonxai.common.Group> symbolTable = new SymbolTable<Group>();

        GroupRef groupRef = new GroupRef(symbolTable.getReference("1"));
        GroupRef groupRefToGroup = new GroupRef(symbolTable.getReference("2"));

        ParticleGroupRefReplacer particleGroupRefReplacer = new ParticleGroupRefReplacer();
        particleGroupRefReplacer.replaceGroupRefInParticle(particle, groupRef, groupRefToGroup);

    }

    /**
     * Test of replaceGroupRefInParticle method, of class ParticleGroupRefReplacer.
     */
    @Test
    public void testReplaceGroupRefInParticle_GroupRefParticle() {
        SymbolTable<de.tudortmund.cs.bonxai.common.Group> symbolTable = new SymbolTable<Group>();

        SequencePattern sequencePattern = new SequencePattern();
        sequencePattern.addParticle(new Element("http://www.example.com", "elementName"));
        ElementGroupElement GroupElement1 = new ElementGroupElement("1", sequencePattern);
        ElementGroupElement GroupElement2 = new ElementGroupElement("2", sequencePattern);

        symbolTable.updateOrCreateReference("1", GroupElement1);
        symbolTable.updateOrCreateReference("2", GroupElement2);

        Particle particle = new GroupRef(symbolTable.getReference("1"));

        GroupRef groupRef = new GroupRef(symbolTable.getReference("1"));
        GroupRef groupRefToGroup = new GroupRef(symbolTable.getReference("2"));

        ParticleGroupRefReplacer particleGroupRefReplacer = new ParticleGroupRefReplacer();
        Particle newParticle = (particleGroupRefReplacer.replaceGroupRefInParticle(particle, groupRef, groupRefToGroup));

        assertEquals(GroupElement2, ((GroupRef) newParticle).getGroup());
    }

    /**
     * Test of replaceGroupRefInParticle method, of class ParticleGroupRefReplacer.
     */
    @Test
    public void testReplaceGroupRefInParticle_ParticleContainer() {
        SymbolTable<de.tudortmund.cs.bonxai.common.Group> symbolTable = new SymbolTable<Group>();

        SequencePattern sequencePattern = new SequencePattern();
        sequencePattern.addParticle(new Element("http://www.example.com", "elementName"));
        ElementGroupElement GroupElement1 = new ElementGroupElement("1", sequencePattern);
        ElementGroupElement GroupElement2 = new ElementGroupElement("2", sequencePattern);

        symbolTable.updateOrCreateReference("1", GroupElement1);
        symbolTable.updateOrCreateReference("2", GroupElement2);

        AllPattern particle = new AllPattern();
        particle.addParticle(new GroupRef(symbolTable.getReference("1")));
        particle.addParticle(new GroupRef(symbolTable.getReference("3")));

        GroupRef groupRef = new GroupRef(symbolTable.getReference("1"));
        GroupRef groupRefToGroup = new GroupRef(symbolTable.getReference("2"));

        ParticleGroupRefReplacer particleGroupRefReplacer = new ParticleGroupRefReplacer();
        Particle newParticle = (particleGroupRefReplacer.replaceGroupRefInParticle(particle, groupRef, groupRefToGroup));

        assertEquals(GroupElement2, ((GroupRef)((AllPattern) newParticle).getParticles().getFirst()).getGroup());
    }

}
