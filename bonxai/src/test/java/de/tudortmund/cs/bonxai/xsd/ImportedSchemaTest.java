package de.tudortmund.cs.bonxai.xsd;

import org.junit.Test;

import de.tudortmund.cs.bonxai.xsd.ImportedSchema;



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
