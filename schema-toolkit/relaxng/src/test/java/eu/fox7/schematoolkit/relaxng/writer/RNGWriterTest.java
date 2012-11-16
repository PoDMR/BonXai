/**
 * Copyright 2009-2012 TU Dortmund
 *
 * This file is part of FoXLib.
 *
 * FoXLib is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FoXLib is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with BonXai.  If not, see <http://www.gnu.org/licenses/>.
 */

package eu.fox7.schematoolkit.relaxng.writer;

import eu.fox7.schematoolkit.relaxng.parser.RNGParser;
import eu.fox7.schematoolkit.relaxng.writer.RNGWriter;

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

        String filePath = this.getClass().getResource("/tests/eu/fox7/bonxai/relaxng/parser/rngs/relaxng.rng").getFile();

        // RNGParser
        // Parameter 1: Path to file
        // Parameter 2: Debug
        RNGParser instance = new RNGParser(filePath, false);

        instance.getRNGSchema();

        RNGWriter rngWriter = new RNGWriter(instance.getRNGSchema());
        System.out.println(rngWriter.getRNGString());
    }

}