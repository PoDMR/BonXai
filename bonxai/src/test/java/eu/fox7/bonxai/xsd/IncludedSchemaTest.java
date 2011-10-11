package eu.fox7.bonxai.xsd;

import org.junit.Test;

import eu.fox7.bonxai.xsd.IncludedSchema;



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