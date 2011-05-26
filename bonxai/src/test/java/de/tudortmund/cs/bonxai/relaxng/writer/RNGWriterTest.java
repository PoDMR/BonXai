package de.tudortmund.cs.bonxai.relaxng.writer;

import de.tudortmund.cs.bonxai.relaxng.parser.RNGParser;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * Test of class RNGWriter
 * @author Lars Schmidt
 */
public class RNGWriterTest extends junit.framework.TestCase {

    /**
     * Test of getRNGString method, of class RNGWriter.
     * @throws IOException 
     * @throws FileNotFoundException
     * @throws SAXException
     */
    @Test
    public void testGetRNGString() throws IOException, FileNotFoundException, SAXException {

        String filePath = this.getClass().getResource("/tests/de/tudortmund/cs/bonxai/relaxng/parser/rngs/relaxng.rng").getFile();

        // RNGParser
        // Parameter 1: Path to file
        // Parameter 2: Debug
        RNGParser instance = new RNGParser(filePath, false);

        instance.getRNGSchema();

        RNGWriter rngWriter = new RNGWriter(instance.getRNGSchema());
        System.out.println(rngWriter.getRNGString());
    }

}