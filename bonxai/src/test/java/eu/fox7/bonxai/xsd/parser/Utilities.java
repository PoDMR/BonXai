package eu.fox7.bonxai.xsd.parser;

import java.io.*;
import java.net.URL;

import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.*;

/**
 * @author Lars Schmidt, Dominik Wolff
 */
public class Utilities {

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
        while (!(schemaNode.getNodeType() == 1 && schemaNode.getNodeName().endsWith("schema"))) {
            schemaNode = schemaNode.getNextSibling();
        }
        return schemaNode;
    }

}
