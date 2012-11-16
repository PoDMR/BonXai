/**
 * Copyright 2009-2012 TU Dortmund
 *
 * This file is part of FoXLib.
 *
 * FoXLib is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FoXLib is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with BonXai.  If not, see <http://www.gnu.org/licenses/>.
 */

package eu.fox7.schematoolkit.relaxng.tools;
import eu.fox7.schematoolkit.common.IdentifiedNamespace;
import eu.fox7.schematoolkit.relaxng.*;
import eu.fox7.schematoolkit.relaxng.parser.RNGParser;
import eu.fox7.schematoolkit.relaxng.tools.XMLAttributeReplenisher;

import java.util.Iterator;
import org.junit.Test;

/**
 * Test of class XMLAttributeReplenisher
 * @author Lars Schmidt
 */
public class XMLAttributeReplenisherTest extends junit.framework.TestCase {

    /**
     * Test of startReplenishment method with element, of class XMLAttributeReplenisher.
     * @throws Exception 
     */
    @Test
    public void testStartReplenishment_element() throws Exception {

        String filePath = new String("tests/eu/fox7/bonxai/relaxng/tools/XMLAttributeReplenisherTest/element.rng");
        filePath = this.getClass().getResource("/"+filePath).getFile();

        
        RNGParser instance = new RNGParser(filePath, false);

        RelaxNGSchema relaxNGSchema = instance.getRNGSchema();

        XMLAttributeReplenisher relaxNGSchemaReplenisher = new XMLAttributeReplenisher(relaxNGSchema);
        relaxNGSchemaReplenisher.startReplenishment();

        assertTrue(relaxNGSchema.getRootPattern() instanceof Element);
        Element rootElement = (Element) relaxNGSchema.getRootPattern();

        assertTrue(rootElement.getPatterns().getFirst() instanceof Element);
        Element element = (Element) rootElement.getPatterns().getFirst();

        assertEquals(rootElement.getAttributeNamespace(), element.getAttributeNamespace());
        assertEquals(rootElement.getAttributeDatatypeLibrary(), element.getAttributeDatatypeLibrary());

        assertEquals(rootElement.getNamespaceList().getIdentifiedNamespaces().size(), element.getNamespaceList().getIdentifiedNamespaces().size());

        for (Iterator<IdentifiedNamespace> it = rootElement.getNamespaceList().getIdentifiedNamespaces().iterator(); it.hasNext();) {
            IdentifiedNamespace identifiedNamespace = it.next();
            assertTrue(element.getNamespaceList().getNamespaceByIdentifier(identifiedNamespace.getIdentifier()).getUri() != null);
            assertTrue(element.getNamespaceList().getNamespaceByIdentifier(identifiedNamespace.getIdentifier()).getUri().equals(identifiedNamespace.getUri()));
        }
    }

    /**
     * Test of startReplenishment method with attribute, of class XMLAttributeReplenisher.
     * @throws Exception
     */
    @Test
    public void testStartReplenishment_attribute() throws Exception {

        String filePath = new String("tests/eu/fox7/bonxai/relaxng/tools/XMLAttributeReplenisherTest/attribute.rng");
        filePath = this.getClass().getResource("/"+filePath).getFile();

        RNGParser instance = new RNGParser(filePath, false);

        RelaxNGSchema relaxNGSchema = instance.getRNGSchema();

        XMLAttributeReplenisher relaxNGSchemaReplenisher = new XMLAttributeReplenisher(relaxNGSchema);
        relaxNGSchemaReplenisher.startReplenishment();

        assertTrue(relaxNGSchema.getRootPattern() instanceof Element);
        Element rootElement = (Element) relaxNGSchema.getRootPattern();

        assertTrue(rootElement.getPatterns().getFirst() instanceof Attribute);
        Attribute attribute = (Attribute) rootElement.getPatterns().getFirst();

        // Attributes do not inherit the namespace attribute from their parents
//        assertEquals(null, attribute.getAttributeNamespace());
        assertEquals(rootElement.getAttributeDatatypeLibrary(), attribute.getAttributeDatatypeLibrary());

        assertEquals(rootElement.getNamespaceList().getIdentifiedNamespaces().size(), attribute.getNamespaceList().getIdentifiedNamespaces().size());

        for (Iterator<IdentifiedNamespace> it = rootElement.getNamespaceList().getIdentifiedNamespaces().iterator(); it.hasNext();) {
            IdentifiedNamespace identifiedNamespace = it.next();
            assertTrue(attribute.getNamespaceList().getNamespaceByIdentifier(identifiedNamespace.getIdentifier()).getUri() != null);
            assertTrue(attribute.getNamespaceList().getNamespaceByIdentifier(identifiedNamespace.getIdentifier()).getUri().equals(identifiedNamespace.getUri()));
        }
    }

    /**
     * Test of startReplenishment method with group, of class XMLAttributeReplenisher.
     * @throws Exception
     */
    @Test
    public void testStartReplenishment_group() throws Exception {

        String filePath = new String("tests/eu/fox7/bonxai/relaxng/tools/XMLAttributeReplenisherTest/group.rng");
        filePath = this.getClass().getResource("/"+filePath).getFile();

        RNGParser instance = new RNGParser(filePath, false);

        RelaxNGSchema relaxNGSchema = instance.getRNGSchema();

        XMLAttributeReplenisher relaxNGSchemaReplenisher = new XMLAttributeReplenisher(relaxNGSchema);
        relaxNGSchemaReplenisher.startReplenishment();

        assertTrue(relaxNGSchema.getRootPattern() instanceof Element);
        Element rootElement = (Element) relaxNGSchema.getRootPattern();

        assertTrue(rootElement.getPatterns().getFirst() instanceof Group);
        Group group = (Group) rootElement.getPatterns().getFirst();

        assertEquals(rootElement.getAttributeNamespace(), group.getAttributeNamespace());
        assertEquals(rootElement.getAttributeDatatypeLibrary(), group.getAttributeDatatypeLibrary());

        assertEquals(rootElement.getNamespaceList().getIdentifiedNamespaces().size(), group.getNamespaceList().getIdentifiedNamespaces().size());

        for (Iterator<IdentifiedNamespace> it = rootElement.getNamespaceList().getIdentifiedNamespaces().iterator(); it.hasNext();) {
            IdentifiedNamespace identifiedNamespace = it.next();
            assertTrue(group.getNamespaceList().getNamespaceByIdentifier(identifiedNamespace.getIdentifier()).getUri() != null);
            assertTrue(group.getNamespaceList().getNamespaceByIdentifier(identifiedNamespace.getIdentifier()).getUri().equals(identifiedNamespace.getUri()));
        }
    }

    /**
     * Test of startReplenishment method with list, of class XMLAttributeReplenisher.
     * @throws Exception
     */
    @Test
    public void testStartReplenishment_interleave() throws Exception {

        String filePath = new String("tests/eu/fox7/bonxai/relaxng/tools/XMLAttributeReplenisherTest/interleave.rng");
        filePath = this.getClass().getResource("/"+filePath).getFile();

        RNGParser instance = new RNGParser(filePath, false);

        RelaxNGSchema relaxNGSchema = instance.getRNGSchema();

        XMLAttributeReplenisher relaxNGSchemaReplenisher = new XMLAttributeReplenisher(relaxNGSchema);
        relaxNGSchemaReplenisher.startReplenishment();

        assertTrue(relaxNGSchema.getRootPattern() instanceof Element);
        Element rootElement = (Element) relaxNGSchema.getRootPattern();

        assertTrue(rootElement.getPatterns().getFirst() instanceof Interleave);
        Interleave interleave = (Interleave) rootElement.getPatterns().getFirst();

        assertEquals(rootElement.getAttributeNamespace(), interleave.getAttributeNamespace());
        assertEquals(rootElement.getAttributeDatatypeLibrary(), interleave.getAttributeDatatypeLibrary());

        assertEquals(rootElement.getNamespaceList().getIdentifiedNamespaces().size(), interleave.getNamespaceList().getIdentifiedNamespaces().size());

        for (Iterator<IdentifiedNamespace> it = rootElement.getNamespaceList().getIdentifiedNamespaces().iterator(); it.hasNext();) {
            IdentifiedNamespace identifiedNamespace = it.next();
            assertTrue(interleave.getNamespaceList().getNamespaceByIdentifier(identifiedNamespace.getIdentifier()).getUri() != null);
            assertTrue(interleave.getNamespaceList().getNamespaceByIdentifier(identifiedNamespace.getIdentifier()).getUri().equals(identifiedNamespace.getUri()));
        }
    }

    /**
     * Test of startReplenishment method with list, of class XMLAttributeReplenisher.
     * @throws Exception
     */
    @Test
    public void testStartReplenishment_choice() throws Exception {

        String filePath = new String("tests/eu/fox7/bonxai/relaxng/tools/XMLAttributeReplenisherTest/choice.rng");
        filePath = this.getClass().getResource("/"+filePath).getFile();

        RNGParser instance = new RNGParser(filePath, false);

        RelaxNGSchema relaxNGSchema = instance.getRNGSchema();

        XMLAttributeReplenisher relaxNGSchemaReplenisher = new XMLAttributeReplenisher(relaxNGSchema);
        relaxNGSchemaReplenisher.startReplenishment();

        assertTrue(relaxNGSchema.getRootPattern() instanceof Element);
        Element rootElement = (Element) relaxNGSchema.getRootPattern();

        assertTrue(rootElement.getPatterns().getFirst() instanceof Choice);
        Choice choice = (Choice) rootElement.getPatterns().getFirst();

        assertEquals(rootElement.getAttributeNamespace(), choice.getAttributeNamespace());
        assertEquals(rootElement.getAttributeDatatypeLibrary(), choice.getAttributeDatatypeLibrary());

        assertEquals(rootElement.getNamespaceList().getIdentifiedNamespaces().size(), choice.getNamespaceList().getIdentifiedNamespaces().size());

        for (Iterator<IdentifiedNamespace> it = rootElement.getNamespaceList().getIdentifiedNamespaces().iterator(); it.hasNext();) {
            IdentifiedNamespace identifiedNamespace = it.next();
            assertTrue(choice.getNamespaceList().getNamespaceByIdentifier(identifiedNamespace.getIdentifier()).getUri() != null);
            assertTrue(choice.getNamespaceList().getNamespaceByIdentifier(identifiedNamespace.getIdentifier()).getUri().equals(identifiedNamespace.getUri()));
        }
    }

    /**
     * Test of startReplenishment method with list, of class XMLAttributeReplenisher.
     * @throws Exception
     */
    @Test
    public void testStartReplenishment_optional() throws Exception {

        String filePath = new String("tests/eu/fox7/bonxai/relaxng/tools/XMLAttributeReplenisherTest/optional.rng");
        filePath = this.getClass().getResource("/"+filePath).getFile();

        RNGParser instance = new RNGParser(filePath, false);

        RelaxNGSchema relaxNGSchema = instance.getRNGSchema();

        XMLAttributeReplenisher relaxNGSchemaReplenisher = new XMLAttributeReplenisher(relaxNGSchema);
        relaxNGSchemaReplenisher.startReplenishment();

        assertTrue(relaxNGSchema.getRootPattern() instanceof Element);
        Element rootElement = (Element) relaxNGSchema.getRootPattern();

        assertTrue(rootElement.getPatterns().getFirst() instanceof Optional);
        Optional optional = (Optional) rootElement.getPatterns().getFirst();

        assertEquals(rootElement.getAttributeNamespace(), optional.getAttributeNamespace());
        assertEquals(rootElement.getAttributeDatatypeLibrary(), optional.getAttributeDatatypeLibrary());

        assertEquals(rootElement.getNamespaceList().getIdentifiedNamespaces().size(), optional.getNamespaceList().getIdentifiedNamespaces().size());

        for (Iterator<IdentifiedNamespace> it = rootElement.getNamespaceList().getIdentifiedNamespaces().iterator(); it.hasNext();) {
            IdentifiedNamespace identifiedNamespace = it.next();
            assertTrue(optional.getNamespaceList().getNamespaceByIdentifier(identifiedNamespace.getIdentifier()).getUri() != null);
            assertTrue(optional.getNamespaceList().getNamespaceByIdentifier(identifiedNamespace.getIdentifier()).getUri().equals(identifiedNamespace.getUri()));
        }
    }

    /**
     * Test of startReplenishment method with list, of class XMLAttributeReplenisher.
     * @throws Exception
     */
    @Test
    public void testStartReplenishment_zeroOrMore() throws Exception {

        String filePath = new String("tests/eu/fox7/bonxai/relaxng/tools/XMLAttributeReplenisherTest/zeroOrMore.rng");
        filePath = this.getClass().getResource("/"+filePath).getFile();

        RNGParser instance = new RNGParser(filePath, false);

        RelaxNGSchema relaxNGSchema = instance.getRNGSchema();

        XMLAttributeReplenisher relaxNGSchemaReplenisher = new XMLAttributeReplenisher(relaxNGSchema);
        relaxNGSchemaReplenisher.startReplenishment();

        assertTrue(relaxNGSchema.getRootPattern() instanceof Element);
        Element rootElement = (Element) relaxNGSchema.getRootPattern();

        assertTrue(rootElement.getPatterns().getFirst() instanceof ZeroOrMore);
        ZeroOrMore zeroOrMore = (ZeroOrMore) rootElement.getPatterns().getFirst();

        assertEquals(rootElement.getAttributeNamespace(), zeroOrMore.getAttributeNamespace());
        assertEquals(rootElement.getAttributeDatatypeLibrary(), zeroOrMore.getAttributeDatatypeLibrary());

        assertEquals(rootElement.getNamespaceList().getIdentifiedNamespaces().size(), zeroOrMore.getNamespaceList().getIdentifiedNamespaces().size());

        for (Iterator<IdentifiedNamespace> it = rootElement.getNamespaceList().getIdentifiedNamespaces().iterator(); it.hasNext();) {
            IdentifiedNamespace identifiedNamespace = it.next();
            assertTrue(zeroOrMore.getNamespaceList().getNamespaceByIdentifier(identifiedNamespace.getIdentifier()).getUri() != null);
            assertTrue(zeroOrMore.getNamespaceList().getNamespaceByIdentifier(identifiedNamespace.getIdentifier()).getUri().equals(identifiedNamespace.getUri()));
        }
    }

    /**
     * Test of startReplenishment method with list, of class XMLAttributeReplenisher.
     * @throws Exception
     */
    @Test
    public void testStartReplenishment_oneOrMore() throws Exception {

        String filePath = new String("tests/eu/fox7/bonxai/relaxng/tools/XMLAttributeReplenisherTest/oneOrMore.rng");
        filePath = this.getClass().getResource("/"+filePath).getFile();

        RNGParser instance = new RNGParser(filePath, false);

        RelaxNGSchema relaxNGSchema = instance.getRNGSchema();

        XMLAttributeReplenisher relaxNGSchemaReplenisher = new XMLAttributeReplenisher(relaxNGSchema);
        relaxNGSchemaReplenisher.startReplenishment();

        assertTrue(relaxNGSchema.getRootPattern() instanceof Element);
        Element rootElement = (Element) relaxNGSchema.getRootPattern();

        assertTrue(rootElement.getPatterns().getFirst() instanceof OneOrMore);
        OneOrMore oneOrMore = (OneOrMore) rootElement.getPatterns().getFirst();

        assertEquals(rootElement.getAttributeNamespace(), oneOrMore.getAttributeNamespace());
        assertEquals(rootElement.getAttributeDatatypeLibrary(), oneOrMore.getAttributeDatatypeLibrary());

        assertEquals(rootElement.getNamespaceList().getIdentifiedNamespaces().size(), oneOrMore.getNamespaceList().getIdentifiedNamespaces().size());

        for (Iterator<IdentifiedNamespace> it = rootElement.getNamespaceList().getIdentifiedNamespaces().iterator(); it.hasNext();) {
            IdentifiedNamespace identifiedNamespace = it.next();
            assertTrue(oneOrMore.getNamespaceList().getNamespaceByIdentifier(identifiedNamespace.getIdentifier()).getUri() != null);
            assertTrue(oneOrMore.getNamespaceList().getNamespaceByIdentifier(identifiedNamespace.getIdentifier()).getUri().equals(identifiedNamespace.getUri()));
        }
    }

    /**
     * Test of startReplenishment method with list, of class XMLAttributeReplenisher.
     * @throws Exception
     */
    @Test
    public void testStartReplenishment_list() throws Exception {

        String filePath = new String("tests/eu/fox7/bonxai/relaxng/tools/XMLAttributeReplenisherTest/list.rng");
        filePath = this.getClass().getResource("/"+filePath).getFile();

        RNGParser instance = new RNGParser(filePath, false);

        RelaxNGSchema relaxNGSchema = instance.getRNGSchema();

        XMLAttributeReplenisher relaxNGSchemaReplenisher = new XMLAttributeReplenisher(relaxNGSchema);
        relaxNGSchemaReplenisher.startReplenishment();

        assertTrue(relaxNGSchema.getRootPattern() instanceof Element);
        Element rootElement = (Element) relaxNGSchema.getRootPattern();

        assertTrue(rootElement.getPatterns().getFirst() instanceof List);
        List list = (List) rootElement.getPatterns().getFirst();

        assertEquals(rootElement.getAttributeNamespace(), list.getAttributeNamespace());
        assertEquals(rootElement.getAttributeDatatypeLibrary(), list.getAttributeDatatypeLibrary());

        assertEquals(rootElement.getNamespaceList().getIdentifiedNamespaces().size(), list.getNamespaceList().getIdentifiedNamespaces().size());

        for (Iterator<IdentifiedNamespace> it = rootElement.getNamespaceList().getIdentifiedNamespaces().iterator(); it.hasNext();) {
            IdentifiedNamespace identifiedNamespace = it.next();
            assertTrue(list.getNamespaceList().getNamespaceByIdentifier(identifiedNamespace.getIdentifier()).getUri() != null);
            assertTrue(list.getNamespaceList().getNamespaceByIdentifier(identifiedNamespace.getIdentifier()).getUri().equals(identifiedNamespace.getUri()));
        }
    }

    /**
     * Test of startReplenishment method with mixed, of class XMLAttributeReplenisher.
     * @throws Exception
     */
    @Test
    public void testStartReplenishment_mixed() throws Exception {

        String filePath = new String("tests/eu/fox7/bonxai/relaxng/tools/XMLAttributeReplenisherTest/mixed.rng");
        filePath = this.getClass().getResource("/"+filePath).getFile();

        RNGParser instance = new RNGParser(filePath, false);

        RelaxNGSchema relaxNGSchema = instance.getRNGSchema();

        XMLAttributeReplenisher relaxNGSchemaReplenisher = new XMLAttributeReplenisher(relaxNGSchema);
        relaxNGSchemaReplenisher.startReplenishment();

        assertTrue(relaxNGSchema.getRootPattern() instanceof Element);
        Element rootElement = (Element) relaxNGSchema.getRootPattern();

        assertTrue(rootElement.getPatterns().getFirst() instanceof Mixed);
        Mixed mixed = (Mixed) rootElement.getPatterns().getFirst();

        assertEquals(rootElement.getAttributeNamespace(), mixed.getAttributeNamespace());
        assertEquals(rootElement.getAttributeDatatypeLibrary(), mixed.getAttributeDatatypeLibrary());

        assertEquals(rootElement.getNamespaceList().getIdentifiedNamespaces().size(), mixed.getNamespaceList().getIdentifiedNamespaces().size());

        for (Iterator<IdentifiedNamespace> it = rootElement.getNamespaceList().getIdentifiedNamespaces().iterator(); it.hasNext();) {
            IdentifiedNamespace identifiedNamespace = it.next();
            assertTrue(mixed.getNamespaceList().getNamespaceByIdentifier(identifiedNamespace.getIdentifier()).getUri() != null);
            assertTrue(mixed.getNamespaceList().getNamespaceByIdentifier(identifiedNamespace.getIdentifier()).getUri().equals(identifiedNamespace.getUri()));
        }
    }

    /**
     * Test of startReplenishment method with grammar, of class XMLAttributeReplenisher.
     * @throws Exception
     */
    @Test
    public void testStartReplenishment_grammar() throws Exception {

        String filePath = new String("tests/eu/fox7/bonxai/relaxng/tools/XMLAttributeReplenisherTest/grammar.rng");
        filePath = this.getClass().getResource("/"+filePath).getFile();

        RNGParser instance = new RNGParser(filePath, false);

        RelaxNGSchema relaxNGSchema = instance.getRNGSchema();

        XMLAttributeReplenisher relaxNGSchemaReplenisher = new XMLAttributeReplenisher(relaxNGSchema);
        relaxNGSchemaReplenisher.startReplenishment();

        assertTrue(relaxNGSchema.getRootPattern() instanceof Element);
        Element rootElement = (Element) relaxNGSchema.getRootPattern();

        assertTrue(rootElement.getPatterns().getFirst() instanceof Grammar);
        Grammar grammar = (Grammar) rootElement.getPatterns().getFirst();

        assertEquals(rootElement.getAttributeNamespace(), grammar.getAttributeNamespace());
        assertEquals(rootElement.getAttributeDatatypeLibrary(), grammar.getAttributeDatatypeLibrary());

        assertEquals(rootElement.getNamespaceList().getIdentifiedNamespaces().size(), grammar.getNamespaceList().getIdentifiedNamespaces().size());

        for (Iterator<IdentifiedNamespace> it = rootElement.getNamespaceList().getIdentifiedNamespaces().iterator(); it.hasNext();) {
            IdentifiedNamespace identifiedNamespace = it.next();
            assertTrue(grammar.getNamespaceList().getNamespaceByIdentifier(identifiedNamespace.getIdentifier()).getUri() != null);
            assertTrue(grammar.getNamespaceList().getNamespaceByIdentifier(identifiedNamespace.getIdentifier()).getUri().equals(identifiedNamespace.getUri()));
        }

        assertTrue(grammar.getStartPatterns().getFirst() instanceof Ref);
        Ref ref = (Ref) grammar.getStartPatterns().getFirst();

        assertEquals(rootElement.getAttributeNamespace(), ref.getAttributeNamespace());
        assertEquals(rootElement.getAttributeDatatypeLibrary(), ref.getAttributeDatatypeLibrary());

        assertEquals(rootElement.getNamespaceList().getIdentifiedNamespaces().size(), ref.getNamespaceList().getIdentifiedNamespaces().size());

        for (Iterator<IdentifiedNamespace> it = rootElement.getNamespaceList().getIdentifiedNamespaces().iterator(); it.hasNext();) {
            IdentifiedNamespace identifiedNamespace = it.next();
            assertTrue(ref.getNamespaceList().getNamespaceByIdentifier(identifiedNamespace.getIdentifier()).getUri() != null);
            assertTrue(ref.getNamespaceList().getNamespaceByIdentifier(identifiedNamespace.getIdentifier()).getUri().equals(identifiedNamespace.getUri()));
        }
    }


}
