package eu.fox7.schematoolkit.xsd.writer;

import org.junit.Test;
import org.junit.Before;

import eu.fox7.schematoolkit.common.*;
import eu.fox7.schematoolkit.xsd.om.*;
import eu.fox7.schematoolkit.xsd.om.Group;
import eu.fox7.schematoolkit.xsd.writer.GroupWriter;
import eu.fox7.schematoolkit.xsd.writer.XSDWriter;

public class GroupWriterTest extends junit.framework.TestCase {

    XSDWriter writer;
    XSDSchema s;
    DefaultNamespace defaultNameSpace;
	private IdentifiedNamespace xsdNamespace;
	private IdentifiedNamespace barNamespace;
    
    @Before
    public void setUp() {
        s = new XSDSchema();

        defaultNameSpace = new DefaultNamespace("http://example.com/xyz");
        xsdNamespace = new IdentifiedNamespace("xs", "http://www.w3.org/2001/XMLSchema");
        barNamespace = new IdentifiedNamespace("bar", "http://example.com/bar");
        s.addIdentifiedNamespace(xsdNamespace);
        s.addIdentifiedNamespace(barNamespace);

        writer = new XSDWriter(s);
        try {
            writer.createXSD();
        } catch (Exception e) {
            fail("Exception thrown: \r\n" + e.getLocalizedMessage());
        }
    }

    @Test
    public void testWriteParticleContainerWithStandardOccurrence() {
        SequencePattern sequencePattern = new SequencePattern();
        Group group = new Group(new QualifiedName(barNamespace,"someGroup"), sequencePattern);
        GroupWriter.writeGroup(writer.root, group, s);
        assertTrue(writer.root.getFirstChild().getLocalName().equals("group"));
        assertTrue(writer.root.getFirstChild().getFirstChild().getLocalName().equals("sequence"));
    }

}
