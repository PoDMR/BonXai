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

package eu.fox7.schematoolkit.relaxng.parser;

import eu.fox7.schematoolkit.relaxng.om.RelaxNGSchema;

import java.io.*;
import java.util.Arrays;
import java.util.HashSet;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.*;

/**
 * This class parses a given RNG file into the XSD data structure with extensions for Relax NG.
 * @author Lars Schmidt
 */
public class RNGParser {

    // Document created by the DOM parser
    private Document doc = null;
    // XSDSchema object build by this class
    private RelaxNGSchema rngSchema = null;
    // Processor for the XSDSchema
    private RNGRootProcessor rngRootProcessor;
    private final HashSet<String> startElements = new HashSet<String>(
            Arrays.asList(
            "element",
            "group",
            "interleave",
            "choice",
            "notAllowed",
            "externalRef",
            "grammar"));


    public RNGParser(String URI, boolean debug) throws FileNotFoundException, SAXException, IOException {
    	this(new FileInputStream(URI), debug);
    }
    
    /**
     * Starts parsing the XSD document beginning with the XSDSchema
     * @param uriString
     * @param debug
     * @throws FileNotFoundException
     * @throws SAXException
     * @throws IOException
     */
    public RNGParser(InputStream stream, boolean debug) throws FileNotFoundException, SAXException, IOException {
        // Create a new schema object and it's processor
        rngSchema = new RelaxNGSchema();
        rngRootProcessor = new RNGRootProcessor(rngSchema);

        RNGProcessorBase.setDebug(debug);

        // Begin processing the Relax NG
        try {

            // Build the document tree from a file
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            
            doc = factory.newDocumentBuilder().parse(stream);
            rngSchema.setAbsoluteUri(doc.getBaseURI());
            
            Node schemaNode = doc.getFirstChild();

            while (schemaNode != null
                    && !(schemaNode.getNodeType() == 1
                    && (this.startElements.contains(schemaNode.getLocalName())))) {
                schemaNode = schemaNode.getNextSibling();
            }
            if (schemaNode != null) {
                rngRootProcessor.processNode(schemaNode);
            }

        } catch (Exception error) {
            error.printStackTrace();
        }
    }

    /**
     * Getter for the attribute schema holding the parsed Relax NG-XSDSchema
     * @return RelaxNGSchema
     */
    public RelaxNGSchema getRNGSchema() {
        return rngSchema;
    }
}
