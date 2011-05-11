package de.tudortmund.cs.bonxai.converter.bonxai2xsd;

import de.tudortmund.cs.bonxai.common.*;
import de.tudortmund.cs.bonxai.bonxai.*;
import java.util.Iterator;
import org.junit.Test;

/**
 *
 */
public class ParticleExtractorTest extends junit.framework.TestCase {

    public ParticleExtractorTest() {
    }

    public de.tudortmund.cs.bonxai.bonxai.Expression generateTrivialExpression(Particle particle) {

        ElementPattern elementPattern = new ElementPattern(particle);
        ChildPattern childPattern = new ChildPattern(null, elementPattern);
        Expression expression = new Expression();
        expression.setChildPattern(childPattern);

        return expression;
    }

    /**
     * Test of createSubtreeFromParticle method, of class ParticleExtractor.
     */
    @Test
    public void testTrivialElementParticleCreateSubtree() {

        String name = "elementName";
        String namespace = "http://www.example.com/ns";
        de.tudortmund.cs.bonxai.bonxai.Element element = new de.tudortmund.cs.bonxai.bonxai.Element(namespace, name);

        Expression expression = this.generateTrivialExpression(element);
        SchemaTreeNode schemaTreeNode = new SchemaTreeNode();

        ParticleExtractor instance = new ParticleExtractor();
        instance.createSubtreeFromExpression(schemaTreeNode, expression);

        assertEquals(schemaTreeNode.getChildCount(), 1);

    }

    /**
     * Test of createSubtreeFromExpression method, of class ParticleExtractor.
     */
    @Test
    public void testTrivialGroupRefParticleCreateSubtree() {

        String name1 = "elementName1";
        String namespace1 = "http://www.example.com/ns/1";
        de.tudortmund.cs.bonxai.bonxai.Element element1 = new de.tudortmund.cs.bonxai.bonxai.Element(namespace1, name1);

        String name2 = "elementName2";
        String namespace2 = "http://www.example.com/ns/2";
        de.tudortmund.cs.bonxai.bonxai.Element element2 = new de.tudortmund.cs.bonxai.bonxai.Element(namespace2, name2);

        String groupName = "groupName";
        de.tudortmund.cs.bonxai.common.ParticleContainer partContainer = new AllPattern();
        partContainer.addParticle(element1);
        partContainer.addParticle(element2);



        de.tudortmund.cs.bonxai.bonxai.ElementGroupElement group = new ElementGroupElement(groupName, partContainer);

        de.tudortmund.cs.bonxai.common.SymbolTableRef<ElementGroupElement> symbolTableRef = new SymbolTableRef<ElementGroupElement>(groupName, group);

        de.tudortmund.cs.bonxai.common.GroupRef groupRef = new GroupRef(symbolTableRef);

        Expression expression = this.generateTrivialExpression(groupRef);

        ParticleExtractor instance = new ParticleExtractor();
        SchemaTreeNode schemaTreeNode = new SchemaTreeNode();
        instance.createSubtreeFromExpression(schemaTreeNode, expression);

        assertEquals(schemaTreeNode.getChildCount(), 1);

        Iterator<TreeNode> itr = schemaTreeNode.getChilds().iterator();

        GroupTreeNode currTreeNode = null;

        while
         (itr.hasNext()) {
            currTreeNode = (GroupTreeNode) itr.next();
        }

        assertEquals(currTreeNode.getChildCount(), 2);
    }

    /**
     * Test of getChildTree method, of class ParticleExtractor.
     */
    @Test
    public void testParticleContainerCreateSubtree() {

        String name = "elementName";
        String namespace = "http://www.example.com/ns";
        de.tudortmund.cs.bonxai.bonxai.Element element = new de.tudortmund.cs.bonxai.bonxai.Element(namespace, name);

        String name1 = "elementName1";
        String namespace1 = "http://www.example.com/ns/1";
        de.tudortmund.cs.bonxai.bonxai.Element element1 = new de.tudortmund.cs.bonxai.bonxai.Element(namespace1, name1);

        de.tudortmund.cs.bonxai.common.SequencePattern sequencePattern = new de.tudortmund.cs.bonxai.common.SequencePattern();
        sequencePattern.addParticle(element);
        sequencePattern.addParticle(element1);

        Expression expression = this.generateTrivialExpression(sequencePattern);

        ParticleExtractor instance = new ParticleExtractor();
        SchemaTreeNode schemaTreeNode = new SchemaTreeNode();
        instance.createSubtreeFromExpression(schemaTreeNode, expression);

        assertEquals(schemaTreeNode.getChildCount(), 2);
    }

    /**
     * Test of getChildTree method, of class ParticleExtractor.
     */
    @Test
    public void testAnyPatternCreateSubtree() {

        String namespace = "http://www.example.com/ns";
        AnyPattern any = new AnyPattern(ProcessContentsInstruction.Strict, namespace);

        Expression expression = this.generateTrivialExpression(any);

        ParticleExtractor instance = new ParticleExtractor();
        SchemaTreeNode schemaTreeNode = new SchemaTreeNode();
        instance.createSubtreeFromExpression(schemaTreeNode, expression);

        assertEquals(schemaTreeNode.getChildCount(), 0);
    }

    /**
     * Test of getChildTree method, of class ParticleExtractor.
     */
    @Test
    public void testRecursingChildPattern() {

        Expression expression_1 = new Expression();

        SingleSlashPrefixElement element_1_1 = new SingleSlashPrefixElement("http://example.com", "root");
        expression_1.setAncestorPattern(new AncestorPattern(element_1_1));

        de.tudortmund.cs.bonxai.bonxai.Element element_1_2 = new de.tudortmund.cs.bonxai.bonxai.Element("http://example.com", "path");
        ChildPattern childPattern_1 = new ChildPattern(null, new ElementPattern(element_1_2));
        expression_1.setChildPattern(childPattern_1);

        ParticleExtractor instance = new ParticleExtractor();
        SchemaTreeNode schemaTreeNode = new SchemaTreeNode();
        instance.createSubtreeFromExpression(schemaTreeNode, expression_1);

        assertEquals(1, schemaTreeNode.getChildCount());
    }
}
