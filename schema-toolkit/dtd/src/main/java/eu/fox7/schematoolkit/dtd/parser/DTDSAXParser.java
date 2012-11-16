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

package eu.fox7.schematoolkit.dtd.parser;

import eu.fox7.schematoolkit.common.Namespace;
import eu.fox7.schematoolkit.common.QualifiedName;
import eu.fox7.schematoolkit.dtd.common.DTDNameChecker;
import eu.fox7.schematoolkit.dtd.common.exceptions.IllegalNAMEStringException;
import eu.fox7.schematoolkit.dtd.om.DocumentTypeDefinition;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

import org.xml.sax.XMLReader;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.Locator;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.XMLReaderFactory;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Class DTDSAXParser. This is a SAX event parser for the DTD Content of a XML file.
 * @author Lars Schmidt
 */
public class DTDSAXParser extends DefaultHandler {

    // Locator
    private Locator locator;
    private boolean debug;
    private DTDEventHandler myDTDHandler;
    private String tempFileName = null;

    public DocumentTypeDefinition parseXML(String xmlURI)
    throws Exception {
    	InputSource inputSource = new InputSource(new FileInputStream(new File(xmlURI)));
    	return parseXML(inputSource, xmlURI);
    }
    
    /**
     * Method parse. This is the "main" method of this class for parsing the DTD 
     * content out of a XML file given by its path.
     * @param xmlURI
     * @return DocumentTypeDefinition
     * @throws Exception
     */
    public DocumentTypeDefinition parseXML(InputSource inputSource, String xmlURI)
            throws Exception {

        // Initialize the xmlReader
        XMLReader xr = XMLReaderFactory.createXMLReader("com.sun.org.apache.xerces.internal.parsers.SAXParser");

        // Define this class as the handler for the used SAXParser
        DTDSAXParser handler = this;

        
        myDTDHandler = new DTDEventHandler();
        myDTDHandler.setDebug(this.debug);
        xr.setContentHandler(handler);
        xr.setErrorHandler(handler);

        // For DTD events lexical and a declaration handlers are important

        /***********************************************************************
         * The DTDHandler manages
         ***********************************************************************
         * notationDecl:       Receive notification of a notation declaration event.
         * unparsedEntityDecl: Receive notification of an unparsed entity declaration event.
         **********************************************************************/
        xr.setDTDHandler(myDTDHandler);

        /***********************************************************************
         * The lexical-handler manages
         ***********************************************************************
         * comment:     Report an XML comment anywhere in the document.
         * endCDATA:    Report the end of a CDATA section.
         * endDTD:      Report the end of DTD declarations.
         * endEntity:   Report the end of an entity.
         * startCDATA:  Report the start of a CDATA section.
         * startDTD:    Report the start of DTD declarations, if any.
         * startEntity: Report the beginning of some internal and external XML entities.
         **********************************************************************/
        xr.setProperty("http://xml.org/sax/properties/lexical-handler", myDTDHandler);

        /***********************************************************************
         * The declaration-handler manages
         ***********************************************************************
         * attributeDecl:       Report an attribute type declaration.
         * elementDecl:         Report an element type declaration.
         * externalEntityDecl:  Report a parsed external entity declaration.
         * internalEntityDecl:  Report an internal entity declaration.
         **********************************************************************/
        xr.setProperty("http://xml.org/sax/properties/declaration-handler", myDTDHandler);

        // The following settings are necessary for resolving the correct content for each type of entities
        //xr.setEntityResolver(handler);
        xr.setFeature("http://xml.org/sax/features/external-parameter-entities", true);
        xr.setFeature("http://xml.org/sax/features/external-general-entities", true);
        xr.setFeature("http://xml.org/sax/features/lexical-handler/parameter-entities", true);

        // It is necessary to use InputSources to define a SystemId. This
        // handles the setting of the correct workingdirectory!
        // 
        inputSource.setSystemId(xmlURI);

        /***********************************************************************
         * Call the 'parse'-method of the XMLReader to start the main parsing
         * progress
         **********************************************************************/
        xr.parse(inputSource);

        // return the generated DTD Structure
        return myDTDHandler.getDTD();
    }
    
    

    /**
     * Method parse. This is the "main" method of this class for parsing the DTD
     * content directly from a DTD File. The filename is used as name of the
     * root-element
     * @param dtdURI
     * @return DocumentTypeDefinition
     * @throws Exception
     */

    /**
     * Method parse. This is the "main" method of this class for parsing the DTD
     * content directly from a DTD File.
     * @param dtdURI
     * @return DocumentTypeDefinition
     * @throws SAXException 
     * @throws IOException 
     */
    public DocumentTypeDefinition parseDTDOnly(InputStream inputStream) throws SAXException, IOException {

    	// Initialize the xmlReader
        XMLReader xr = XMLReaderFactory.createXMLReader();

        // Define this class as the handler for the used SAXParser
        DTDSAXParser handler = new DTDSAXParser();

        myDTDHandler = new DTDEventHandler();
        myDTDHandler.setDebug(this.debug);
        xr.setContentHandler(handler);
        xr.setErrorHandler(handler);

        // For DTD events lexical and a declaration handlers are important

        /***********************************************************************
         * The DTDHandler manages
         ***********************************************************************
         * notationDecl:       Receive notification of a notation declaration event.
         * unparsedEntityDecl: Receive notification of an unparsed entity declaration event.
         **********************************************************************/
        xr.setDTDHandler(myDTDHandler);

        /***********************************************************************
         * The lexical-handler manages
         ***********************************************************************
         * comment:     Report an XML comment anywhere in the document.
         * endCDATA:    Report the end of a CDATA section.
         * endDTD:      Report the end of DTD declarations.
         * endEntity:   Report the end of an entity.
         * startCDATA:  Report the start of a CDATA section.
         * startDTD:    Report the start of DTD declarations, if any.
         * startEntity: Report the beginning of some internal and external XML entities.
         **********************************************************************/
        xr.setProperty("http://xml.org/sax/properties/lexical-handler", myDTDHandler);

        /***********************************************************************
         * The declaration-handler manages
         ***********************************************************************
         * attributeDecl:       Report an attribute type declaration.
         * elementDecl:         Report an element type declaration.
         * externalEntityDecl:  Report a parsed external entity declaration.
         * internalEntityDecl:  Report an internal entity declaration.
         **********************************************************************/
        xr.setProperty("http://xml.org/sax/properties/declaration-handler", myDTDHandler);

        // The following settings are necessary for resolving the correct content for each type of entities
        //xr.setEntityResolver(handler);
        xr.setFeature("http://xml.org/sax/features/external-parameter-entities", true);
        xr.setFeature("http://xml.org/sax/features/external-general-entities", true);
        xr.setFeature("http://xml.org/sax/features/lexical-handler/parameter-entities", true);

        String uniqueRandID = java.util.UUID.randomUUID().toString();

        InputSource inputSource = new InputSource(inputStream);
//        inputSource.setSystemId(dtdURI);

        /***********************************************************************
         * Call the 'parse'-method of the XMLReader to start the main parsing
         * progress
         **********************************************************************/
        xr.parse(inputSource);

        // return the generated DTD Structure
        return myDTDHandler.getDTD();
    }

    @Override
    public void setDocumentLocator(Locator locator) {
        this.locator = locator;
    }

    /**
     * Constructor of this class DTDSAXParser (debug is set to false)
     */
    public DTDSAXParser() {
        super();
        this.debug = false;
    }

    /**
     * Constructor of this class DTDSAXParser (with boolean parameter: debug)
     * @param debug 
     */
    public DTDSAXParser(boolean debug) {
        super();
        this.debug = debug;
    }

    // DTD entity resolving
    @Override
    public InputSource resolveEntity(String publicID, String systemID) throws SAXException {
        if (this.debug) {
            System.out.println("resolve DTD Entity:" + publicID + " " + systemID);
            System.out.println("Locator:" + locator.getPublicId() + " " + locator.getSystemId()
                    + " " + locator.getLineNumber() + " " + locator.getColumnNumber());
        }
        return null;
    }

    @Override
    public void warning(SAXParseException e) throws SAXException {
        System.out.println("Warning: " + e.getMessage() + " (line: " + locator.getLineNumber() + ", column: " + locator.getColumnNumber() + ")");
    }

    @Override
    public void error(SAXParseException e) throws SAXException {
        throw new SAXException(e.getMessage() + " (line: " + locator.getLineNumber() + ", column: " + locator.getColumnNumber() + ")");
    }

    @Override
    public void fatalError(SAXParseException e) throws SAXException {
        throw new SAXException(e.getMessage() + " (line: " + locator.getLineNumber() + ", column: " + locator.getColumnNumber() + ")");
    }
}
