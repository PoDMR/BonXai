package eu.fox7.schematoolkit.xsd.parser;

//import eu.fox7.bonxai.tools.StatusLogger;
import eu.fox7.schematoolkit.SchemaToolkitException;
import eu.fox7.schematoolkit.xsd.om.*;
import eu.fox7.schematoolkit.xsd.parser.exceptions.XSDParseException;

import java.io.*;
import javax.xml.parsers.*;

import org.w3c.dom.*;
import org.xml.sax.*;

/**
 * This class parses a given XSD file into the XSD data structure.
 * @author Lars Schmidt, Dominik Wolff
 */
public class XSDParser {

    // Flag if the EDC should be validated by the parser.
    private boolean validateEDC = false;
    // Flag if the UPA should be validated by the parser.
    private boolean validateUPA = false;
    // Document created by the DOM parser
    private Document doc = null;
    // XSDSchema object build by this class
    private XSDSchema schema = null;
    // Processor for the XSDSchema
    private SchemaProcessor schemaProcessor;
    // True if invalid alls are allowed. Such alls can appeare as non top level components with particles other than elments.
    public static boolean allowInvalidAll = false;

    /**
     * Starts parsing the XSD document beginning with the XSDSchema
     * @param validateEDC
     * @param debug
     */
    public XSDParser(boolean validateEDC, boolean debug) {
        // Create a new schema object and it's processor
        this.validateEDC = validateEDC;
        Processor.setDebug(debug);
    }

    public XSDSchema parse(String uriString) throws FileNotFoundException, IOException, XSDParseException {
        // Begin processing the XSD
        // try {
            // Build the document tree from a file
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);

            this.schema = new XSDSchema();
            this.schemaProcessor = new SchemaProcessor(schema);
            try {
				doc = factory.newDocumentBuilder().parse(uriString);
			} catch (SAXException e) {
				throw new XSDParseException(e);
			} catch (ParserConfigurationException e) {
				throw new XSDParseException(e);
			}

            Node schemaNode = doc.getFirstChild();
            // We have to find the right starting node for the schemaProcessor
            // It is possible, that DTD information is written before the xsd
            // schema node. In DTD the Doctype root node can also be named
            // "schema" so we have to check the type of the found node.
            // Type == 1 means that it is the correct XSD node type
            while (schemaNode != null && !(schemaNode.getNodeType() == 1 && schemaNode.getNodeName().endsWith("schema"))) {
                schemaNode = schemaNode.getNextSibling();
            }


            schemaProcessor.processNode(schemaNode);

            schema.setSchemaLocation(doc.getBaseURI());

        return this.schema;
    }

    public XSDSchema parse(InputStream inputStream) throws FileNotFoundException, IOException, SchemaToolkitException {
        // Begin processing the XSD
    	// Build the document tree from a file
    	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    	factory.setNamespaceAware(true);

    	this.schema = new XSDSchema();
    	this.schemaProcessor = new SchemaProcessor(schema);
    	try {
			doc = factory.newDocumentBuilder().parse(inputStream);
		} catch (SAXException e) {
			throw new SchemaToolkitException(e);
		} catch (ParserConfigurationException e) {
			throw new SchemaToolkitException(e);
		}

    	Node schemaNode = doc.getFirstChild();
    	// We have to find the right starting node for the schemaProcessor
    	// It is possible, that DTD information is written before the xsd
    	// schema node. In DTD the Doctype root node can also be named
    	// "schema" so we have to check the type of the found node.
    	// Type == 1 means that it is the correct XSD node type
    	while (schemaNode != null && !(schemaNode.getNodeType() == 1 && schemaNode.getNodeName().endsWith("schema"))) {
    		schemaNode = schemaNode.getNextSibling();
    	}


    	schemaProcessor.processNode(schemaNode);

    	schema.setSchemaLocation(doc.getBaseURI());

    	//            ForeignSchemaLoader foreignSchemaLoader = new ForeignSchemaLoader(schema, validateEDC);
    	//            foreignSchemaLoader.findForeignSchemas();


    	return this.schema;
    }

    public XSDSchema parseForeignSchema(String uriString, String targetNamespace) throws FileNotFoundException, IOException, XSDParseException {
        // Begin processing the XSD
    	// Build the document tree from a file
    	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    	factory.setNamespaceAware(true);

    	this.schema = new XSDSchema();
    	this.schemaProcessor = new SchemaProcessor(schema);
    	try {
			doc = factory.newDocumentBuilder().parse(uriString);
		} catch (SAXException e) {
			throw new XSDParseException(e);
		} catch (ParserConfigurationException e) {
			throw new XSDParseException(e);
		}

    	Node schemaNode = doc.getFirstChild();
    	// We have to find the right starting node for the schemaProcessor
    	// It is possible, that DTD information is written before the xsd
    	// schema node. In DTD the Doctype root node can also be named
    	// "schema" so we have to check the type of the found node.
    	// Type == 1 means that it is the correct XSD node type
    	while (schemaNode != null && !(schemaNode.getNodeType() == 1 && schemaNode.getNodeName().endsWith("schema"))) {
    		schemaNode = schemaNode.getNextSibling();
    	}
    	schemaProcessor.setIncludeTargetNamespace(targetNamespace);
    	schemaProcessor.processNode(schemaNode);

    	schema.setSchemaLocation(doc.getBaseURI());

        return this.schema;
    }
}
