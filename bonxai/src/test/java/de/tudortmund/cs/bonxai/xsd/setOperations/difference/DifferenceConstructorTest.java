package de.tudortmund.cs.bonxai.xsd.setOperations.difference;

import de.tudortmund.cs.bonxai.common.IdentifiedNamespace;
import de.tudortmund.cs.bonxai.xsd.*;
import de.tudortmund.cs.bonxai.xsd.setOperations.SchemaGroup;
import java.util.*;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test case of the <tt>IntersectionConstructor</tt> class, checks that every
 * method of this class performs properly.
 * @author Dominik Wolff
 */
public class DifferenceConstructorTest extends junit.framework.TestCase {

    public DifferenceConstructorTest() {
    }

    /**
     * Test of constructDifferenceSchemata method, of class DifferenceConstructor.
     */
    @Test
    public void testConstructDifferenceSchemata() throws Exception {
        XSDSchema schemaA1 = new XSDSchema("A");
        XSDSchema schemaA2 = new XSDSchema("A");
        XSDSchema schemaB1 = new XSDSchema("B");
        XSDSchema schemaB2 = new XSDSchema("B");
        XSDSchema schemaC1 = new XSDSchema("C");
        XSDSchema schemaC2 = new XSDSchema("C");
        SimpleType simpleType = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        schemaA1.getTypeSymbolTable().updateOrCreateReference(simpleType.getName(), simpleType);
        schemaB1.getTypeSymbolTable().updateOrCreateReference(simpleType.getName(), simpleType);
        schemaC1.getTypeSymbolTable().updateOrCreateReference(simpleType.getName(), simpleType);
        Element elementA1 = new Element("{A}element", schemaA1.getTypeSymbolTable().getReference(simpleType.getName()));
        Element elementB1 = new Element("{B}element", schemaB1.getTypeSymbolTable().getReference(simpleType.getName()));
        Element elementC1 = new Element("{C}element", schemaC1.getTypeSymbolTable().getReference(simpleType.getName()));
        schemaA1.getElementSymbolTable().updateOrCreateReference(elementA1.getName(), elementA1);
        schemaA1.addElement(schemaA1.getElementSymbolTable().getReference(elementA1.getName()));
        schemaB1.getElementSymbolTable().updateOrCreateReference(elementB1.getName(), elementB1);
        schemaB1.addElement(schemaB1.getElementSymbolTable().getReference(elementB1.getName()));
        schemaC1.getElementSymbolTable().updateOrCreateReference(elementC1.getName(), elementC1);
        schemaC1.addElement(schemaC1.getElementSymbolTable().getReference(elementC1.getName()));
        ImportedSchema importedSchema1 = new ImportedSchema("B", "C:/schemaB1.xsd");
        importedSchema1.setParentSchema(schemaA1);
        importedSchema1.setSchema(schemaB1);
        ImportedSchema importedSchema2 = new ImportedSchema("C", "C:/schemaC1.xsd");
        importedSchema2.setParentSchema(schemaA1);
        importedSchema2.setSchema(schemaC1);
        ImportedSchema importedSchema3 = new ImportedSchema("C", "C:/schemaC1.xsd");
        importedSchema3.setParentSchema(schemaB1);
        importedSchema3.setSchema(schemaC1);
        schemaA1.addForeignSchema(importedSchema1);
        schemaA1.addForeignSchema(importedSchema2);
        schemaB1.addForeignSchema(importedSchema3);
        LinkedHashSet<SchemaGroup> schemaGroups = new LinkedHashSet<SchemaGroup>();
        SchemaGroup schemaGroupA = new SchemaGroup("A");
        SchemaGroup schemaGroupB = new SchemaGroup("B");
        SchemaGroup schemaGroupC = new SchemaGroup("C");
        schemaGroupA.addMinuendSchema(schemaA1, null, true);
        schemaGroupA.addSubtrahendSchema(schemaA2, null);
        schemaGroupB.addMinuendSchema(schemaB1, null, false);
        schemaGroupB.addSubtrahendSchema(schemaB2, null);
        schemaGroupC.addMinuendSchema(schemaC1, null, false);
        schemaGroupC.addSubtrahendSchema(schemaC2, null);
        schemaGroups.add(schemaGroupA);
        schemaGroups.add(schemaGroupB);
        schemaGroups.add(schemaGroupC);
        String workingDirectory = "c:/";
        HashMap<SchemaGroup, String> schemaGroupSchemaNameMap = new HashMap<SchemaGroup, String>();
        schemaGroupSchemaNameMap.put(schemaGroupA, "Difference.newSchemaA");
        schemaGroupSchemaNameMap.put(schemaGroupB, "Difference.newSchemaB");
        schemaGroupSchemaNameMap.put(schemaGroupC, "Difference.newSchemaC");
        LinkedHashMap<String, String> namespaceAbbreviationMap = new LinkedHashMap<String, String>();
        namespaceAbbreviationMap.put("A", "a");
        namespaceAbbreviationMap.put("B", "ns1");
        LinkedHashSet<XSDSchema> oldSchemata = new LinkedHashSet<XSDSchema>();
        oldSchemata.add(schemaA1);
        oldSchemata.add(schemaA2);
        oldSchemata.add(schemaB1);
        oldSchemata.add(schemaB2);
        oldSchemata.add(schemaC1);
        DifferenceConstructor instance = new DifferenceConstructor(schemaGroups, workingDirectory, schemaGroupSchemaNameMap, namespaceAbbreviationMap, oldSchemata);

        XSDSchema resultingSchema = instance.constructDifferenceSchemata();

        LinkedHashSet<XSDSchema> result = new LinkedHashSet<XSDSchema>();
        result.add(resultingSchema);
        result.add(resultingSchema.getForeignSchemas().getFirst().getSchema());
        result.add(resultingSchema.getForeignSchemas().getLast().getSchema());

        assertEquals(3, result.size());
        assertEquals("A", ((XSDSchema) result.toArray()[0]).getTargetNamespace());
        assertEquals("B", ((XSDSchema) result.toArray()[1]).getTargetNamespace());
        assertEquals("C", ((XSDSchema) result.toArray()[2]).getTargetNamespace());
        assertEquals("c:/Difference.newSchemaA.xsd", ((XSDSchema) result.toArray()[0]).getSchemaLocation());
        assertEquals("c:/Difference.newSchemaB.xsd", ((XSDSchema) result.toArray()[1]).getSchemaLocation());
        assertEquals("c:/Difference.newSchemaC.xsd", ((XSDSchema) result.toArray()[2]).getSchemaLocation());
        assertEquals("A", ((XSDSchema) result.toArray()[0]).getNamespaceList().getDefaultNamespace().getUri());
        assertEquals(4, ((XSDSchema) result.toArray()[0]).getNamespaceList().getIdentifiedNamespaces().size());
        assertEquals(3, ((XSDSchema) result.toArray()[1]).getNamespaceList().getIdentifiedNamespaces().size());
        assertEquals(2, ((XSDSchema) result.toArray()[2]).getNamespaceList().getIdentifiedNamespaces().size());
        assertEquals("A", ((IdentifiedNamespace) ((XSDSchema) result.toArray()[0]).getNamespaceList().getIdentifiedNamespaces().toArray()[0]).getUri());
        assertEquals("a", ((IdentifiedNamespace) ((XSDSchema) result.toArray()[0]).getNamespaceList().getIdentifiedNamespaces().toArray()[0]).getIdentifier());
        assertEquals("B", ((IdentifiedNamespace) ((XSDSchema) result.toArray()[0]).getNamespaceList().getIdentifiedNamespaces().toArray()[1]).getUri());
        assertEquals("ns1", ((IdentifiedNamespace) ((XSDSchema) result.toArray()[0]).getNamespaceList().getIdentifiedNamespaces().toArray()[1]).getIdentifier());
        assertEquals("C", ((IdentifiedNamespace) ((XSDSchema) result.toArray()[0]).getNamespaceList().getIdentifiedNamespaces().toArray()[2]).getUri());
        assertEquals("ns2", ((IdentifiedNamespace) ((XSDSchema) result.toArray()[0]).getNamespaceList().getIdentifiedNamespaces().toArray()[2]).getIdentifier());
        assertEquals("http://www.w3.org/2001/XMLSchema", ((IdentifiedNamespace) ((XSDSchema) result.toArray()[0]).getNamespaceList().getIdentifiedNamespaces().toArray()[3]).getUri());
        assertEquals("xs", ((IdentifiedNamespace) ((XSDSchema) result.toArray()[0]).getNamespaceList().getIdentifiedNamespaces().toArray()[3]).getIdentifier());
        assertEquals("B", ((IdentifiedNamespace) ((XSDSchema) result.toArray()[1]).getNamespaceList().getIdentifiedNamespaces().toArray()[0]).getUri());
        assertEquals("ns1", ((IdentifiedNamespace) ((XSDSchema) result.toArray()[1]).getNamespaceList().getIdentifiedNamespaces().toArray()[0]).getIdentifier());
        assertEquals("C", ((IdentifiedNamespace) ((XSDSchema) result.toArray()[1]).getNamespaceList().getIdentifiedNamespaces().toArray()[1]).getUri());
        assertEquals("ns2", ((IdentifiedNamespace) ((XSDSchema) result.toArray()[1]).getNamespaceList().getIdentifiedNamespaces().toArray()[1]).getIdentifier());
        assertEquals("http://www.w3.org/2001/XMLSchema", ((IdentifiedNamespace) ((XSDSchema) result.toArray()[1]).getNamespaceList().getIdentifiedNamespaces().toArray()[2]).getUri());
        assertEquals("xs", ((IdentifiedNamespace) ((XSDSchema) result.toArray()[1]).getNamespaceList().getIdentifiedNamespaces().toArray()[2]).getIdentifier());
        assertEquals("C", ((IdentifiedNamespace) ((XSDSchema) result.toArray()[2]).getNamespaceList().getIdentifiedNamespaces().toArray()[0]).getUri());
        assertEquals("ns1", ((IdentifiedNamespace) ((XSDSchema) result.toArray()[2]).getNamespaceList().getIdentifiedNamespaces().toArray()[0]).getIdentifier());
        assertEquals("http://www.w3.org/2001/XMLSchema", ((IdentifiedNamespace) ((XSDSchema) result.toArray()[2]).getNamespaceList().getIdentifiedNamespaces().toArray()[1]).getUri());
        assertEquals("xs", ((IdentifiedNamespace) ((XSDSchema) result.toArray()[2]).getNamespaceList().getIdentifiedNamespaces().toArray()[1]).getIdentifier());
        assertEquals("a", ((XSDSchema) result.toArray()[0]).getNamespaceList().getNamespaceByUri("A").getIdentifier());
        assertEquals("ns1", ((XSDSchema) result.toArray()[0]).getNamespaceList().getNamespaceByUri("B").getIdentifier());
        assertEquals("ns2", ((XSDSchema) result.toArray()[0]).getNamespaceList().getNamespaceByUri("C").getIdentifier());
        assertEquals(null, ((XSDSchema) result.toArray()[1]).getNamespaceList().getNamespaceByUri("A").getIdentifier());
        assertEquals("ns1", ((XSDSchema) result.toArray()[1]).getNamespaceList().getNamespaceByUri("B").getIdentifier());
        assertEquals("ns2", ((XSDSchema) result.toArray()[1]).getNamespaceList().getNamespaceByUri("C").getIdentifier());
        assertEquals(null, ((XSDSchema) result.toArray()[2]).getNamespaceList().getNamespaceByUri("A").getIdentifier());
        assertEquals(null, ((XSDSchema) result.toArray()[2]).getNamespaceList().getNamespaceByUri("B").getIdentifier());
        assertEquals("ns1", ((XSDSchema) result.toArray()[2]).getNamespaceList().getNamespaceByUri("C").getIdentifier());
        assertEquals(2, ((XSDSchema) result.toArray()[0]).getForeignSchemas().size());
        assertEquals("Difference.newSchemaB.xsd", ((XSDSchema) result.toArray()[0]).getForeignSchemas().get(0).getSchemaLocation());
        assertEquals(((XSDSchema) result.toArray()[0]), ((XSDSchema) result.toArray()[0]).getForeignSchemas().get(0).getParentSchema());
        assertEquals(((XSDSchema) result.toArray()[1]), ((XSDSchema) result.toArray()[0]).getForeignSchemas().get(0).getSchema());
        assertEquals("Difference.newSchemaC.xsd", ((XSDSchema) result.toArray()[0]).getForeignSchemas().get(1).getSchemaLocation());
        assertEquals(((XSDSchema) result.toArray()[0]), ((XSDSchema) result.toArray()[0]).getForeignSchemas().get(1).getParentSchema());
        assertEquals(((XSDSchema) result.toArray()[2]), ((XSDSchema) result.toArray()[0]).getForeignSchemas().get(1).getSchema());
        assertEquals(1, ((XSDSchema) result.toArray()[1]).getForeignSchemas().size());
        assertEquals("Difference.newSchemaC.xsd", ((XSDSchema) result.toArray()[1]).getForeignSchemas().get(0).getSchemaLocation());
        assertEquals(((XSDSchema) result.toArray()[1]), ((XSDSchema) result.toArray()[1]).getForeignSchemas().get(0).getParentSchema());
        assertEquals(((XSDSchema) result.toArray()[2]), ((XSDSchema) result.toArray()[1]).getForeignSchemas().get(0).getSchema());
    }
}