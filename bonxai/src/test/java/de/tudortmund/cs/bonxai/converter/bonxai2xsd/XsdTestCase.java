package de.tudortmund.cs.bonxai.converter.bonxai2xsd;

import org.junit.Test;
import java.io.*;

import de.tudortmund.cs.bonxai.xsd.*;
import de.tudortmund.cs.bonxai.xsd.writer.*;

public class XsdTestCase extends junit.framework.TestCase {

    /**
     * Compare the given schema instance with the stored output of the given
     * name.
     */
    protected void assertSchemaSame(XSDSchema xsd, String name) {
        XSDWriter writer = new XSDWriter(xsd);

        boolean failed  = false;
        String contents = "";
        String result   = "";

        try {
            result   = writer.getXSDString();

            File file         = new File(getClass().getClassLoader().getResource("de/tudortmund/cs/bonxai/converter/bonxai2xsd/data/" + name + ".xsd").getPath());
            FileReader reader = new FileReader(file);
            char[] buffer     = new char[(int) file.length()];
            reader.read(buffer);
            contents = new String(buffer);

            failed = !contents.equals(result);
        } catch (Exception e) {
            failed = true;
        }

        // Store output in temp file, if the test failed.
        if (failed) {
            try{
                FileWriter fstream = new FileWriter("temp/" + name + ".xsd");
                BufferedWriter out = new BufferedWriter(fstream);
                out.write(result);
                out.close();
            } catch (IOException e) {
                System.out.println("Could not write test comparision file: " + e);
            }
        }

        assertTrue(contents.equals(result));
    }
}
