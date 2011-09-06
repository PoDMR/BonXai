package eu.fox7.bonxai.xsd.writer;

import org.junit.Test;
import org.junit.Before;

import eu.fox7.bonxai.common.*;
import eu.fox7.bonxai.xsd.*;
import eu.fox7.bonxai.xsd.Group;
import eu.fox7.bonxai.xsd.writer.FoundElements;
import eu.fox7.bonxai.xsd.writer.GroupWriter;
import eu.fox7.bonxai.xsd.writer.XSDWriter;

public class GroupWriterTest extends junit.framework.TestCase {

    XSDWriter writer;
    FoundElements foundElements;

    @Before
    public void setUp() {
        DefaultNamespace defaultNameSpace;

        XSDSchema s = new XSDSchema();
        foundElements = new FoundElements();

        defaultNameSpace = new DefaultNamespace("http://example.com/xyz");
        NamespaceList nslist = new NamespaceList(defaultNameSpace);
        nslist.addIdentifiedNamespace(new IdentifiedNamespace("xs", "http://www.w3.org/2001/XMLSchema"));
        nslist.addIdentifiedNamespace(new IdentifiedNamespace("bar", "http://example.com/bar"));
        foundElements.setNamespaceList(nslist);

        s.setNamespaceList(nslist);

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
        Group group = new Group("{}someGroup", sequencePattern);
        GroupWriter.writeGroup(writer.root, group, foundElements);
        assertTrue(writer.root.getFirstChild().getLocalName().equals("group"));
        assertTrue(writer.root.getFirstChild().getFirstChild().getLocalName().equals("sequence"));
    }

}
