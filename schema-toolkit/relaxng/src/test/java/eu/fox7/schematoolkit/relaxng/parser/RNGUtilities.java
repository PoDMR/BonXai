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

import java.io.*;
import java.net.URL;

import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.*;

/**
 * @author Lars Schmidt
 */
public class RNGUtilities {

    /**
     * Method to get the schemaNode of the Dom tree
     * @param filename      Name of the XSD which should be parsed
     * @return      SchemaNode of the Dom tree
     * @throws javax.xml.parsers.ParserConfigurationException
     * @throws org.xml.sax.SAXException
     * @throws java.io.IOException
     */
    protected static Node getSchemaNode(String filename) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        URL url = filename.getClass().getResource("/"+filename);
        File file = new File(url.getFile());
        Document doc = factory.newDocumentBuilder().parse(file);
        Node schemaNode = doc.getFirstChild();
        while (schemaNode != null
                && !(schemaNode.getNodeType() == 1
                )) {
            schemaNode = schemaNode.getNextSibling();
        }

        return schemaNode;
    }
}
