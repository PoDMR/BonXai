package eu.fox7.bonxai.xsd.parser;

import eu.fox7.bonxai.tools.StatusLogger;
import eu.fox7.bonxai.xsd.*;

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

    public XSDSchema parse(String uriString) throws FileNotFoundException, SAXException, IOException, Exception {
        // Begin processing the XSD
        try {
            // Build the document tree from a file
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);

            this.schema = new XSDSchema();
            this.schemaProcessor = new SchemaProcessor(schema);
            doc = factory.newDocumentBuilder().parse(uriString);

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

            ForeignSchemaLoader foreignSchemaLoader = new ForeignSchemaLoader(schema, validateEDC);
            foreignSchemaLoader.findForeignSchemas();

            if (this.validateEDC) {
                StatusLogger.logLastInfoMessage("XSDParser", "Checking EDC-Constraint...");
                EDCChecker edcProcessor = new EDCChecker(this.schema);
                if (edcProcessor.isValid()) {
                    StatusLogger.logLastInfoMessage("XSDParser", "The parsed XML XSDSchema is valid with respect to the \"XSDSchema Component Constraint: Element Declarations Consistent\". :-)");
//                    System.out.println("The parsed XML XSDSchema is valid with regards to the \"XSDSchema Component Constraint: Element Declarations Consistent\". :-)\n");
                } else {
                    StatusLogger.logLastInfoMessage("XSDParser", "The parsed XML XSDSchema is NOT valid with respect to the \"XSDSchema Component Constraint: Element Declarations Consistent\"! :-(");
//                    System.out.println("The parsed XML XSDSchema is NOT valid with regards the \"XSDSchema Component Constraint: Element Declarations Consistent\"! :-(\n");
                }
            }

        } catch (Exception error) {
            StatusLogger.logError("XSDParser", error.getClass().getName() + ": " + error.getMessage());
//            error.printStackTrace();
            throw error;
        }
        return this.schema;
    }

    public XSDSchema parse(InputStream inputStream) throws FileNotFoundException, SAXException, IOException, Exception {
        // Begin processing the XSD
        try {
            // Build the document tree from a file
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);

            this.schema = new XSDSchema();
            this.schemaProcessor = new SchemaProcessor(schema);
            doc = factory.newDocumentBuilder().parse(inputStream);

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

            if (this.validateEDC) {
                StatusLogger.logLastInfoMessage("XSDParser", "Checking EDC-Constraint...");
                EDCChecker edcProcessor = new EDCChecker(this.schema);
                if (edcProcessor.isValid()) {
                    StatusLogger.logLastInfoMessage("XSDParser", "The parsed XML XSDSchema is valid with respect to the \"XSDSchema Component Constraint: Element Declarations Consistent\". :-)");
//                    System.out.println("The parsed XML XSDSchema is valid with regards to the \"XSDSchema Component Constraint: Element Declarations Consistent\". :-)\n");
                } else {
                    StatusLogger.logLastInfoMessage("XSDParser", "The parsed XML XSDSchema is NOT valid with respect to the \"XSDSchema Component Constraint: Element Declarations Consistent\"! :-(");
//                    System.out.println("The parsed XML XSDSchema is NOT valid with regards the \"XSDSchema Component Constraint: Element Declarations Consistent\"! :-(\n");
                }
            }

        } catch (Exception error) {
            StatusLogger.logError("XSDParser", error.getClass().getName() + ": " + error.getMessage());
//            error.printStackTrace();
            throw error;
        }
        return this.schema;
    }

    public XSDSchema parseForeignSchema(String uriString, String targetNamespace) throws FileNotFoundException, SAXException, IOException {
        // Begin processing the XSD
        try {
            // Build the document tree from a file
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);

            this.schema = new XSDSchema();
            this.schemaProcessor = new SchemaProcessor(schema);
            doc = factory.newDocumentBuilder().parse(uriString);

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

        } catch (Exception error) {
            error.printStackTrace();
        }
        return this.schema;
    }
}
