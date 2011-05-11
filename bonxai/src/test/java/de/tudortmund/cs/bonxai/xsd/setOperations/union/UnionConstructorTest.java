package de.tudortmund.cs.bonxai.xsd.setOperations.union;

import de.tudortmund.cs.bonxai.common.IdentifiedNamespace;
import de.tudortmund.cs.bonxai.xsd.ImportedSchema;
import de.tudortmund.cs.bonxai.xsd.XSDSchema;
import de.tudortmund.cs.bonxai.xsd.setOperations.SchemaGroup;
import java.util.*;
import org.junit.*;
import static org.junit.Assert.*;

/**
 * Test case of the <tt>UnionConstructor</tt> class, checks that every
 * method of this class performs properly.
 * @author Dominik Wolff
 */
public class UnionConstructorTest extends junit.framework.TestCase {

    public UnionConstructorTest() {
    }

    /**
     * Test of constructUnionSchemata method, of class UnionConstructor.
     */
    @Test
    public void testConstructUnionSchemata() throws Exception {
        XSDSchema schemaA1 = new XSDSchema("A");
        XSDSchema schemaA2 = new XSDSchema("A");
        XSDSchema schemaA3 = new XSDSchema("A");
        XSDSchema schemaB1 = new XSDSchema("B");
        XSDSchema schemaB2 = new XSDSchema("B");
        XSDSchema schemaC1 = new XSDSchema("C");
        ImportedSchema importedSchema1 = new ImportedSchema("B", "C:/schemaB1.xsd");
        importedSchema1.setParentSchema(schemaA1);
        importedSchema1.setSchema(schemaB1);
        ImportedSchema importedSchema2 = new ImportedSchema("A", "C:/schemaA2.xsd");
        importedSchema2.setParentSchema(schemaB1);
        importedSchema2.setSchema(schemaA2);
        ImportedSchema importedSchema3 = new ImportedSchema("A", "C:/schemaA2.xsd");
        importedSchema3.setParentSchema(schemaB2);
        importedSchema3.setSchema(schemaA2);
        ImportedSchema importedSchema4 = new ImportedSchema("C", "C:/schemaC1.xsd");
        importedSchema4.setParentSchema(schemaB2);
        importedSchema4.setSchema(schemaC1);
        schemaA1.addForeignSchema(importedSchema1);
        schemaB1.addForeignSchema(importedSchema2);
        schemaB2.addForeignSchema(importedSchema3);
        schemaB2.addForeignSchema(importedSchema4);
        LinkedHashSet<SchemaGroup> schemaGroups = new LinkedHashSet<SchemaGroup>();
        SchemaGroup schemaGroupA = new SchemaGroup("A");
        SchemaGroup schemaGroupB = new SchemaGroup("B");
        SchemaGroup schemaGroupC = new SchemaGroup("C");
        schemaGroupA.addMinuendSchema(schemaA1, null, true);
        schemaGroupA.addMinuendSchema(schemaA2, null, true);
        schemaGroupA.addMinuendSchema(schemaA3, null, true);
        schemaGroupB.addMinuendSchema(schemaB1, null, false);
        schemaGroupB.addMinuendSchema(schemaB2, null, true);
        schemaGroupC.addMinuendSchema(schemaC1, null, false);
        schemaGroups.add(schemaGroupA);
        schemaGroups.add(schemaGroupB);
        schemaGroups.add(schemaGroupC);
        String workingDirectory = "c:/";
        HashMap<SchemaGroup, String> schemaGroupSchemaNameMap = new HashMap<SchemaGroup, String>();
        schemaGroupSchemaNameMap.put(schemaGroupA, "newSchemaA");
        schemaGroupSchemaNameMap.put(schemaGroupB, "newSchemaB");
        schemaGroupSchemaNameMap.put(schemaGroupC, "newSchemaC");
        LinkedHashMap<String, String> namespaceAbbreviationMap = new LinkedHashMap<String, String>();
        namespaceAbbreviationMap.put("A", "a");
        namespaceAbbreviationMap.put("B", "ns1");
        LinkedHashSet<XSDSchema> oldSchemata = new LinkedHashSet<XSDSchema>();
        oldSchemata.add(schemaA1);
        oldSchemata.add(schemaA2);
        oldSchemata.add(schemaA3);
        oldSchemata.add(schemaB1);
        oldSchemata.add(schemaB2);
        oldSchemata.add(schemaC1);
        UnionConstructor instance = new UnionConstructor(schemaGroups, workingDirectory, schemaGroupSchemaNameMap, namespaceAbbreviationMap, oldSchemata);

        LinkedHashSet<XSDSchema> result = instance.constructUnionSchemata();

        assertEquals(3, result.size());
        assertEquals("A", ((XSDSchema) result.toArray()[0]).getTargetNamespace());
        assertEquals("B", ((XSDSchema) result.toArray()[1]).getTargetNamespace());
        assertEquals("C", ((XSDSchema) result.toArray()[2]).getTargetNamespace());
        assertEquals("c:/newSchemaA.xsd", ((XSDSchema) result.toArray()[0]).getSchemaLocation());
        assertEquals("c:/newSchemaB.xsd", ((XSDSchema) result.toArray()[1]).getSchemaLocation());
        assertEquals("c:/newSchemaC.xsd", ((XSDSchema) result.toArray()[2]).getSchemaLocation());
        assertEquals("A", ((XSDSchema) result.toArray()[0]).getNamespaceList().getDefaultNamespace().getUri());
        assertEquals(4, ((XSDSchema) result.toArray()[0]).getNamespaceList().getIdentifiedNamespaces().size());
        assertEquals(4, ((XSDSchema) result.toArray()[1]).getNamespaceList().getIdentifiedNamespaces().size());
        assertEquals(2, ((XSDSchema) result.toArray()[2]).getNamespaceList().getIdentifiedNamespaces().size());
        assertEquals("A", ((IdentifiedNamespace) ((XSDSchema) result.toArray()[0]).getNamespaceList().getIdentifiedNamespaces().toArray()[0]).getUri());
        assertEquals("a", ((IdentifiedNamespace) ((XSDSchema) result.toArray()[0]).getNamespaceList().getIdentifiedNamespaces().toArray()[0]).getIdentifier());
        assertEquals("B", ((IdentifiedNamespace) ((XSDSchema) result.toArray()[0]).getNamespaceList().getIdentifiedNamespaces().toArray()[1]).getUri());
        assertEquals("ns1", ((IdentifiedNamespace) ((XSDSchema) result.toArray()[0]).getNamespaceList().getIdentifiedNamespaces().toArray()[1]).getIdentifier());
        assertEquals("C", ((IdentifiedNamespace) ((XSDSchema) result.toArray()[0]).getNamespaceList().getIdentifiedNamespaces().toArray()[2]).getUri());
        assertEquals("ns2", ((IdentifiedNamespace) ((XSDSchema) result.toArray()[0]).getNamespaceList().getIdentifiedNamespaces().toArray()[2]).getIdentifier());
        assertEquals("http://www.w3.org/2001/XMLSchema", ((IdentifiedNamespace) ((XSDSchema) result.toArray()[0]).getNamespaceList().getIdentifiedNamespaces().toArray()[3]).getUri());
        assertEquals("xs", ((IdentifiedNamespace) ((XSDSchema) result.toArray()[0]).getNamespaceList().getIdentifiedNamespaces().toArray()[3]).getIdentifier());
        assertEquals("A", ((IdentifiedNamespace) ((XSDSchema) result.toArray()[1]).getNamespaceList().getIdentifiedNamespaces().toArray()[0]).getUri());
        assertEquals("a", ((IdentifiedNamespace) ((XSDSchema) result.toArray()[1]).getNamespaceList().getIdentifiedNamespaces().toArray()[0]).getIdentifier());
        assertEquals("B", ((IdentifiedNamespace) ((XSDSchema) result.toArray()[1]).getNamespaceList().getIdentifiedNamespaces().toArray()[1]).getUri());
        assertEquals("ns1", ((IdentifiedNamespace) ((XSDSchema) result.toArray()[1]).getNamespaceList().getIdentifiedNamespaces().toArray()[1]).getIdentifier());
        assertEquals("C", ((IdentifiedNamespace) ((XSDSchema) result.toArray()[1]).getNamespaceList().getIdentifiedNamespaces().toArray()[2]).getUri());
        assertEquals("ns2", ((IdentifiedNamespace) ((XSDSchema) result.toArray()[1]).getNamespaceList().getIdentifiedNamespaces().toArray()[2]).getIdentifier());
        assertEquals("http://www.w3.org/2001/XMLSchema", ((IdentifiedNamespace) ((XSDSchema) result.toArray()[1]).getNamespaceList().getIdentifiedNamespaces().toArray()[3]).getUri());
        assertEquals("xs", ((IdentifiedNamespace) ((XSDSchema) result.toArray()[1]).getNamespaceList().getIdentifiedNamespaces().toArray()[3]).getIdentifier());
        assertEquals("C", ((IdentifiedNamespace) ((XSDSchema) result.toArray()[2]).getNamespaceList().getIdentifiedNamespaces().toArray()[0]).getUri());
        assertEquals("ns1", ((IdentifiedNamespace) ((XSDSchema) result.toArray()[2]).getNamespaceList().getIdentifiedNamespaces().toArray()[0]).getIdentifier());
        assertEquals("http://www.w3.org/2001/XMLSchema", ((IdentifiedNamespace) ((XSDSchema) result.toArray()[2]).getNamespaceList().getIdentifiedNamespaces().toArray()[1]).getUri());
        assertEquals("xs", ((IdentifiedNamespace) ((XSDSchema) result.toArray()[2]).getNamespaceList().getIdentifiedNamespaces().toArray()[1]).getIdentifier());
        assertEquals("a", ((XSDSchema) result.toArray()[0]).getNamespaceList().getNamespaceByUri("A").getIdentifier());
        assertEquals("ns1", ((XSDSchema) result.toArray()[0]).getNamespaceList().getNamespaceByUri("B").getIdentifier());
        assertEquals("ns2", ((XSDSchema) result.toArray()[0]).getNamespaceList().getNamespaceByUri("C").getIdentifier());
        assertEquals("a", ((XSDSchema) result.toArray()[1]).getNamespaceList().getNamespaceByUri("A").getIdentifier());
        assertEquals("ns1", ((XSDSchema) result.toArray()[1]).getNamespaceList().getNamespaceByUri("B").getIdentifier());
        assertEquals("ns2", ((XSDSchema) result.toArray()[1]).getNamespaceList().getNamespaceByUri("C").getIdentifier());
        assertEquals(null, ((XSDSchema) result.toArray()[2]).getNamespaceList().getNamespaceByUri("A").getIdentifier());
        assertEquals(null, ((XSDSchema) result.toArray()[2]).getNamespaceList().getNamespaceByUri("B").getIdentifier());
        assertEquals("ns1", ((XSDSchema) result.toArray()[2]).getNamespaceList().getNamespaceByUri("C").getIdentifier());
        assertEquals(2, ((XSDSchema) result.toArray()[0]).getForeignSchemas().size());
        assertEquals("newSchemaB.xsd", ((XSDSchema) result.toArray()[0]).getForeignSchemas().get(0).getSchemaLocation());
        assertEquals(((XSDSchema) result.toArray()[0]), ((XSDSchema) result.toArray()[0]).getForeignSchemas().get(0).getParentSchema());
        assertEquals(((XSDSchema) result.toArray()[1]), ((XSDSchema) result.toArray()[0]).getForeignSchemas().get(0).getSchema());
        assertEquals("newSchemaC.xsd", ((XSDSchema) result.toArray()[0]).getForeignSchemas().get(1).getSchemaLocation());
        assertEquals(((XSDSchema) result.toArray()[0]), ((XSDSchema) result.toArray()[0]).getForeignSchemas().get(1).getParentSchema());
        assertEquals(((XSDSchema) result.toArray()[2]), ((XSDSchema) result.toArray()[0]).getForeignSchemas().get(1).getSchema());
        assertEquals(2, ((XSDSchema) result.toArray()[1]).getForeignSchemas().size());
        assertEquals("newSchemaA.xsd", ((XSDSchema) result.toArray()[1]).getForeignSchemas().get(0).getSchemaLocation());
        assertEquals(((XSDSchema) result.toArray()[1]), ((XSDSchema) result.toArray()[1]).getForeignSchemas().get(0).getParentSchema());
        assertEquals(((XSDSchema) result.toArray()[0]), ((XSDSchema) result.toArray()[1]).getForeignSchemas().get(0).getSchema());
        assertEquals("newSchemaC.xsd", ((XSDSchema) result.toArray()[1]).getForeignSchemas().get(1).getSchemaLocation());
        assertEquals(((XSDSchema) result.toArray()[1]), ((XSDSchema) result.toArray()[1]).getForeignSchemas().get(1).getParentSchema());
        assertEquals(((XSDSchema) result.toArray()[2]), ((XSDSchema) result.toArray()[1]).getForeignSchemas().get(1).getSchema());
        assertEquals(0, ((XSDSchema) result.toArray()[2]).getForeignSchemas().size());
    }
}