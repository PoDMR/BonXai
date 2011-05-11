package de.tudortmund.cs.bonxai.converter.bonxai2xsd;

import de.tudortmund.cs.bonxai.common.DefaultNamespace;
import de.tudortmund.cs.bonxai.common.NamespaceList;
import de.tudortmund.cs.bonxai.bonxai.DataType;
import de.tudortmund.cs.bonxai.bonxai.DataTypeList;
import de.tudortmund.cs.bonxai.bonxai.Declaration;
import de.tudortmund.cs.bonxai.bonxai.Import;
import de.tudortmund.cs.bonxai.bonxai.ImportList;
import de.tudortmund.cs.bonxai.bonxai.Bonxai;
import de.tudortmund.cs.bonxai.xsd.ImportedSchema;
import de.tudortmund.cs.bonxai.xsd.XSDSchema;

import java.util.HashMap;
import org.junit.Test;

/**
 * Test for DeclarationConverter class.
 *
 */
public class DeclarationConverterTest extends junit.framework.TestCase {

    /**
     * Test of convert method for a Bonxai schema with existing ImportList
     */
    @Test
    public void testConvertWithAllListsEmpty() {
        Bonxai bonxai = new Bonxai();

        HashMap<String, XSDSchema> schemas = new HashMap<String, XSDSchema>();
        XSDSchema xsd = new XSDSchema();
        schemas.put("xsd1", xsd);
        DeclarationConverter declarationConverter = new DeclarationConverter(schemas);

        declarationConverter.convert(bonxai);

        assertNotNull(xsd.getNamespaceList());
        assertEquals(0, xsd.getNamespaceList().getIdentifiedNamespaces().size());
        assertTrue(xsd.getForeignSchemas().isEmpty());
    }

    /**
     * Test of convert method for a Bonxai schema with existing NamespaceList
     */
    @Test
    public void testConvertWithNamespaceList() {
        Bonxai bonxai = new Bonxai();

        HashMap<String, XSDSchema> schemas = new HashMap<String, XSDSchema>();
        XSDSchema xsd = new XSDSchema();
        schemas.put("xsd1", xsd);
        DeclarationConverter declarationConverter = new DeclarationConverter(schemas);

        DefaultNamespace defaultNamespace = new DefaultNamespace("http://example.com/namespace");
        NamespaceList namespaceList = new NamespaceList(defaultNamespace);
        bonxai.setDeclaration(new Declaration(null,null,namespaceList));

        declarationConverter.convert(bonxai);

        assertEquals("http://example.com/namespace", ((DefaultNamespace) xsd.getNamespaceList().getDefaultNamespace()).getUri());

    }

    /**
     * Test of convert method for a Bonxai schema with existing DataTypeList
     */
    @Test
    public void testConvertWithDataTypeList() {
        Bonxai bonxai = new Bonxai();

        HashMap<String, XSDSchema> schemas = new HashMap<String, XSDSchema>();
        XSDSchema xsd = new XSDSchema();
        schemas.put("xsd1", xsd);
        DeclarationConverter declarationConverter = new DeclarationConverter(schemas);

        DataType dataType = new DataType("myDataType", "http://example.com/datatypes");
        DataTypeList dataTypeList = new DataTypeList();
        dataTypeList.addDataType(dataType);
        bonxai.setDeclaration(new Declaration(null,dataTypeList, null));

        declarationConverter.convert(bonxai);

        assertEquals("http://example.com/datatypes", ((ImportedSchema) xsd.getForeignSchemas().getFirst()).getSchemaLocation());
    }

    /**
     * Test of convert method for a Bonxai schema with existing ImportList
     */
    @Test
    public void testConvertWithImportList() {
        Bonxai bonxai = new Bonxai();

        HashMap<String, XSDSchema> schemas = new HashMap<String, XSDSchema>();
        XSDSchema xsd = new XSDSchema();
        schemas.put("xsd1", xsd);
        DeclarationConverter declarationConverter = new DeclarationConverter(schemas);

        Import imporT = new Import("http://example.com/bla/blub/import");
        ImportList importList = new ImportList();
        importList.addImport(imporT);

        bonxai.setDeclaration(new Declaration(importList, null, null));

        declarationConverter.convert(bonxai);

        assertEquals("http://example.com/bla/blub/import", ((ImportedSchema) xsd.getForeignSchemas().getFirst()).getSchemaLocation());
    }
}