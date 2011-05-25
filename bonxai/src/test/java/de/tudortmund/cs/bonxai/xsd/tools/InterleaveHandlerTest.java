package de.tudortmund.cs.bonxai.xsd.tools;

import de.tudortmund.cs.bonxai.xsd.Element;
import de.tudortmund.cs.bonxai.common.AllPattern;
import de.tudortmund.cs.bonxai.xsd.ElementRef;
import de.tudortmund.cs.bonxai.common.GroupRef;
import de.tudortmund.cs.bonxai.common.Particle;
import de.tudortmund.cs.bonxai.common.ParticleContainer;
import de.tudortmund.cs.bonxai.xsd.ComplexContentType;
import de.tudortmund.cs.bonxai.xsd.ComplexType;
import de.tudortmund.cs.bonxai.xsd.ForeignSchema;
import de.tudortmund.cs.bonxai.xsd.Group;
import de.tudortmund.cs.bonxai.xsd.XSDSchema;
import de.tudortmund.cs.bonxai.xsd.parser.XSDParser;
import de.tudortmund.cs.bonxai.xsd.writer.XSDWriter;
import java.util.Iterator;
import java.util.LinkedHashSet;
import org.junit.Test;

/**
 * Test of class InterleaveHandler
 * @author Dominik Wolff, Lars Schmidt
 */
public class InterleaveHandlerTest extends junit.framework.TestCase {

    private LinkedHashSet<XSDSchema> alreadySeenSchemas = new LinkedHashSet<XSDSchema>();
    private Element allowedElement;
    private Element allowedReferencedElement;
    private XSDSchema schema;
    private LinkedHashSet<Group> allowedGroups;
    private LinkedHashSet<String> allowedElementNamespaces;
    private LinkedHashSet<String> allowedElementRefNamespaces;
    private LinkedHashSet<String> allowedGroupRefNamespaces;
    private LinkedHashSet<XSDSchema> alreadySeenSchemasForGroups = new LinkedHashSet<XSDSchema>();

 
    private void getSchemas(XSDSchema schema) {

        this.alreadySeenSchemas.add(schema);

        System.out.println("\n--------------------------------------------------------------------------\n" + schema.getSchemaLocation() + "\n--------------------------------------------------------------------------\n");

        XSDWriter xSD_Writer = new XSDWriter(schema);
        xSD_Writer = new XSDWriter(schema);
        System.out.println(xSD_Writer.getXSDString());

        // For each foreignSchema defined in the current schema call the method
        // findContentModels recursively
        if (schema.getForeignSchemas() != null && !schema.getForeignSchemas().isEmpty()) {
            for (Iterator<ForeignSchema> it = schema.getForeignSchemas().iterator(); it.hasNext();) {
                ForeignSchema foreignSchema = it.next();
                if (!this.alreadySeenSchemas.contains(foreignSchema.getSchema())) {
                    this.getSchemas(foreignSchema.getSchema());
                }
            }
        }
    }

    /**
     * Test of repair method, of class InterleaveHandler.
     */
    @Test
    public void testInterleaveGroup() throws Exception {

        String uri = "/tests/de/tudortmund/cs/bonxai/xsd/tools/interleaveHandlerTests/interleaveGroup_root.xsd";
        uri = this.getClass().getResource(uri).getFile();
        
        XSDParser instance = new XSDParser(false, false);

        XSDParser.allowInvalidAll = true;

        XSDSchema xsdSchema = instance.parse(uri);

        ComplexType complexType = (ComplexType) xsdSchema.getElements().element().getType();
        ComplexContentType complexContent = (ComplexContentType) complexType.getContent();

        AllPattern allPattern = (AllPattern) complexContent.getParticle();
        allPattern.getParticles().getFirst();
        allowedElement = (Element) allPattern.getParticles().getFirst();
        allowedReferencedElement = xsdSchema.getForeignSchemas().getFirst().getSchema().getElements().getFirst();

        InterleaveHandler interleaveHandler = new InterleaveHandler(xsdSchema);
        interleaveHandler.repair(xsdSchema);
        this.allowedGroups = new LinkedHashSet<Group>();
        boolean isIncluded = false;
        for (Iterator<Group> it = xsdSchema.getForeignSchemas().getFirst().getSchema().getGroups().iterator(); it.hasNext();) {
            Group group = it.next();

            if (group.getName().equals("{B}group.elementOne2.1")) {
                isIncluded = true;
                allowedGroups.add(group);
            }
        }
        assertTrue(isIncluded);
        assertNotNull(allowedGroups);
        assertEquals(1, allowedGroups.size());

        allowedElementNamespaces = new LinkedHashSet<String>();
        allowedElementNamespaces.add("");

        allowedElementRefNamespaces = new LinkedHashSet<String>();
        allowedElementRefNamespaces.add("B");

        allowedGroupRefNamespaces = new LinkedHashSet<String>();
        allowedGroupRefNamespaces.add("B");

        this.schema = xsdSchema;

//        getSchemas(xsdSchema);
        
        ComplexType complexType2 = (ComplexType) xsdSchema.getElements().element().getType();
        ComplexContentType complexContent2 = (ComplexContentType) complexType2.getContent();

        traverseParticle(complexContent2.getParticle());
    }

    private void traverseParticle(Particle particle) {

        if (particle instanceof ParticleContainer) {

            // Check particle container and its children
            ParticleContainer particleContainer = (ParticleContainer) particle;
            for (Iterator<Particle> it = particleContainer.getParticles().iterator(); it.hasNext();) {
                Particle containedParticle = it.next();
                traverseParticle(containedParticle);
            }
        } else if (particle instanceof GroupRef) {

            // Check group reference
            checkGroupRef((GroupRef) particle);
        } else if (particle instanceof ElementRef) {

            // Check element reference
            checkElementRef((ElementRef) particle);
        } else if (particle instanceof Element) {

            // Check element
            checkElement((Element) particle);
        }
    }

    private void checkGroupRef(GroupRef groupRef) {

        // Check namespace
        checkNamespace(groupRef);

        // Check no local elements with other name
        assertTrue(allowedGroups.contains((Group) groupRef.getGroup()));
    }

    private void checkElementRef(ElementRef elementRef) {

        // Check namespace
        checkNamespace(elementRef);

        // Check element reference for referred element
        assertTrue(elementRef.getElement() == allowedReferencedElement);
    }

    private void checkElement(Element element) {

        // Check namespace
        checkNamespace(element);

////        // Check no local elements with other name
        assertTrue(element == allowedElement);
    }

    private void checkNamespace(Particle particle) {

        if (particle instanceof GroupRef) {
            GroupRef groupRef = (GroupRef) particle;

            assertTrue(allowedGroupRefNamespaces.contains(((Group) groupRef.getGroup()).getNamespace()));
            assertTrue(schema.getNamespaceList().getNamespaceByUri(((Group) groupRef.getGroup()).getNamespace()).getIdentifier() != null);

        } else if (particle instanceof Element) {
            Element element = (Element) particle;

            assertTrue(allowedElementNamespaces.contains(element.getNamespace()));
            assertTrue(schema.getNamespaceList().getNamespaceByUri(element.getNamespace()).getIdentifier() == null);


        } else if (particle instanceof ElementRef) {
            ElementRef elementRef = (ElementRef) particle;

            assertTrue(allowedElementRefNamespaces.contains(elementRef.getElement().getNamespace()));
            assertTrue(schema.getNamespaceList().getNamespaceByUri(elementRef.getElement().getNamespace()).getIdentifier() != null);
        }
    }

    /**
     * Test of repair method, of class InterleaveHandler.
     */
    @Test
    public void testNewIdentifier() throws Exception {

        String uri = "/tests/de/tudortmund/cs/bonxai/xsd/tools/interleaveHandlerTests/interleaveNewIdentifier_root.xsd";
        uri = this.getClass().getResource(uri).getFile();
        
        XSDParser instance = new XSDParser(false, false);

        XSDParser.allowInvalidAll = true;

        XSDSchema xsdSchema = instance.parse(uri);

        InterleaveHandler interleaveHandler = new InterleaveHandler(xsdSchema);
        interleaveHandler.repair(xsdSchema);
//        getSchemas(xsdSchema);
        allowedGroups = new LinkedHashSet<Group>();
        getAllowedGroups(xsdSchema);
        assertEquals(9, allowedGroups.size());

        allowedElementNamespaces = new LinkedHashSet<String>();
        allowedElementNamespaces.add("");

        allowedElementRefNamespaces = new LinkedHashSet<String>();
        
        allowedGroupRefNamespaces = new LinkedHashSet<String>();
        allowedGroupRefNamespaces.add("B");
        allowedGroupRefNamespaces.add("C");

        this.schema = xsdSchema;
        assertTrue(xsdSchema.getNamespaceList().getNamespaceByUri("C").getIdentifier() != null);
        assertTrue(xsdSchema.getNamespaceList().getNamespaceByUri("C").getIdentifier().equals("ns2"));



        ComplexType complexType2 = (ComplexType) xsdSchema.getElements().element().getType();
        ComplexContentType complexContent2 = (ComplexContentType) complexType2.getContent();

        traverseParticle(complexContent2.getParticle());
    }

    private void getAllowedGroups(XSDSchema schema) {

        this.alreadySeenSchemasForGroups.add(schema);

        for (Iterator<Group> it = schema.getGroups().iterator(); it.hasNext();) {
            Group group = it.next();
            this.allowedGroups.add(group);
        }

        // For each foreignSchema defined in the current schema call the method
        // findContentModels recursively
        if (schema.getForeignSchemas() != null && !schema.getForeignSchemas().isEmpty()) {
            for (Iterator<ForeignSchema> it = schema.getForeignSchemas().iterator(); it.hasNext();) {
                ForeignSchema foreignSchema = it.next();
                if (!this.alreadySeenSchemasForGroups.contains(foreignSchema.getSchema())) {
                    this.getAllowedGroups(foreignSchema.getSchema());
                }
            }
        }
    }
}
