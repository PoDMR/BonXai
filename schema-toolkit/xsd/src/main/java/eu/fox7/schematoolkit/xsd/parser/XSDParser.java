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

package eu.fox7.schematoolkit.xsd.parser;

//import eu.fox7.bonxai.tools.StatusLogger;
import eu.fox7.schematoolkit.SchemaToolkitException;
import eu.fox7.schematoolkit.xsd.om.*;
import eu.fox7.schematoolkit.xsd.parser.exceptions.XSDParseException;
import eu.fox7.schematoolkit.xsd.tools.ForeignSchemaLoader;

import java.io.*;
import java.net.URISyntaxException;

import javax.xml.parsers.*;

import org.w3c.dom.*;
import org.xml.sax.*;

/**
 * This class parses a given XSD file into the XSD data structure.
 * @author Lars Schmidt, Dominik Wolff
 */
public class XSDParser {
	
	private Locator locator;

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

	private boolean loadForeignSchemas;
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

    	if (loadForeignSchemas) {
    		ForeignSchemaLoader foreignSchemaLoader = new ForeignSchemaLoader(schema, validateEDC);
    		try {
    			foreignSchemaLoader.findForeignSchemas();
    		} catch (SAXException e) {
    			throw new XSDParseException(e);
    		} catch (URISyntaxException e) {
    			throw new XSDParseException(e);
    		}
    	}


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
