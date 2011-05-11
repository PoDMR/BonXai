package de.tudortmund.cs.bonxai.xsd;

import org.junit.Test;

import de.tudortmund.cs.bonxai.xsd.IncludedSchema;



public class IncludedSchemaTest extends junit.framework.TestCase {

    @Test
    public void testIncludedSchema() {
        String location;
        IncludedSchema inc;

        location = "//testlocation";
        inc = new IncludedSchema(location);
        assertEquals("Locations differ", location, inc.getSchemaLocation());
    }
}
