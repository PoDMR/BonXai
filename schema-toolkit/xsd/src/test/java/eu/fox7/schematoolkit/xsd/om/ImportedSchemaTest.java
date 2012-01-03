package eu.fox7.schematoolkit.xsd.om;

import org.junit.Test;

import eu.fox7.schematoolkit.xsd.om.ImportedSchema;



public class ImportedSchemaTest extends junit.framework.TestCase {


    @Test
    public void testImportedSchema() {
        ImportedSchema imp;
        String namespace, location;

        namespace = "http://www.example.com/NS";
        location = "example.xsd";

        imp = new ImportedSchema(namespace, location);

        assertEquals("Namespace differs", namespace, imp.getNamespace());
        assertEquals("Location differs", location, imp.getSchemaLocation());
    }

}
