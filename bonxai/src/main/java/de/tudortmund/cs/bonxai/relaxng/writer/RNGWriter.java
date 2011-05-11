package de.tudortmund.cs.bonxai.relaxng.writer;

import de.tudortmund.cs.bonxai.relaxng.*;
import de.tudortmund.cs.bonxai.relaxng.writer.exceptions.EmptyRelaxNGSchemaException;
import java.io.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/*******************************************************************************
 * Relax NG Writer                                                             *
 *******************************************************************************
 * Supported is the corresponding Relax NG object model from package
 * de.tudortmund.cs.bonxai.relaxng.*
 *
 * Currently this is a XML writer for the Simple XML Syntax of Relax NG.
 *
 * Relax NG specification: http://relaxng.org/spec-20011203.html
 *
 * The Simple XML Syntax is defined at:
 * http://relaxng.org/spec-20011203.html#simple-syntax
 *
 * @author Lars Schmidt
 */
public class RNGWriter {

    /**
     * Private variable holding the rngSchema
     */
    private RelaxNGSchema rngSchema;
    /**
     * Private variable holding the XML document root node.
     */
    private org.w3c.dom.Document rngDoc;

    /**
     * Constructor of class RNGWriter
     * @param rngSchema
     */
    public RNGWriter(RelaxNGSchema rngSchema) {
        this.rngSchema = rngSchema;
    }

    /**
     * Method for writing the given RNGSchema into a XML String
     * @return String   The generated XML Relax NG document
     */
    public String getRNGString() {

        // Prepare all necessary Variables for writing the XML structure of the
        // Relax NG language.
        TransformerFactory transformerFactory;
        Transformer transformer;

        DOMSource domSource;

        StringWriter stringWriter;
        String rngString = "";

        DocumentBuilder documentBuilder;
        DocumentBuilderFactory documentBuilderFactory;

        try {

            documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
            org.w3c.dom.DOMImplementation domImplementation = documentBuilder.getDOMImplementation();

            // create a new document root node
            this.rngDoc = domImplementation.createDocument(null, null, null);

            // In RelaxNG there are two possible root nodes: grammar or element
            // The complete document is built under this root node.
            this.rngDoc.appendChild(this.createRootNodeFromRNGSchema(rngDoc));

            rngDoc.setXmlStandalone(true);

            // Transform the Document into a String
            domSource = new DOMSource(this.rngDoc);

            // Initialize the TransformerFactory
            transformerFactory = TransformerFactory.newInstance();

            // Setup the indentation number.
            transformerFactory.setAttribute("indent-number", 2);

            // Setup the transformer with the right settings.
            transformer = transformerFactory.newTransformer();
            //transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");

            // Initialize the StringWriter for holding the generated XML String
            stringWriter = new StringWriter();

            StreamResult streamResult = new StreamResult(stringWriter);

            // Start the transformation
            transformer.transform(domSource, streamResult);

            rngString = stringWriter.getBuffer().toString();

        } catch (Exception e) {
            // Catch all exceptions and print the stack trace
            e.printStackTrace();
        }
        // If no exceptions were thrown return the generated string.
        return rngString;
    }

    private org.w3c.dom.Element createRootNodeFromRNGSchema(org.w3c.dom.Document rngDocument) throws Exception {
        org.w3c.dom.Element rootElement = null;

        if (this.rngSchema != null && this.rngSchema.getRootPattern() != null) {
            Pattern rootPattern = this.rngSchema.getRootPattern();
            PatternWriter patternWriter = new PatternWriter(rngDocument, this.rngSchema.getNamespaceList());
            rootElement = patternWriter.createNodeForPattern(rootPattern);
        } else {
            throw new EmptyRelaxNGSchemaException();
        }

        return rootElement;
    }
}
