package eu.fox7.bonxai.dtd.writer;

import eu.fox7.bonxai.dtd.DocumentTypeDefinition;
import eu.fox7.bonxai.dtd.Element;
import eu.fox7.bonxai.dtd.common.DTDNameChecker;
import eu.fox7.bonxai.dtd.common.exceptions.IllegalNAMEStringException;
import eu.fox7.bonxai.dtd.writer.exceptions.DTDNoRootElementDefinedException;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Vector;

/**
 * Class for writing a DTD datastructure from package:
 * "eu.fox7.bonxai.dtd"
 * 
 * @author Lars Schmidt
 */
public class DTDWriter {

    private final DocumentTypeDefinition dtd;
    private boolean writeEntities;

    /**
     * Standard constructor of class DTDWriter
     * @param dtd
     */
    public DTDWriter(DocumentTypeDefinition dtd) {
        this.dtd = dtd;
        this.writeEntities = false;
    }

    /**
     * Constructor of class DTDWriter with boolean parameter to setup the
     * writing of DTD entities.
     * @param dtd
     * @param writeEntities
     */
    public DTDWriter(DocumentTypeDefinition dtd, boolean writeEntities) {
        this.dtd = dtd;
        this.writeEntities = writeEntities;
    }

    /**
     * If of the standard constructor was chosen, this method gives the opportunity
     * of setting up the writer to write DTD entities.
     * @param writeEntities
     */
    public void setWriteEntities(boolean writeEntities) {
        this.writeEntities = writeEntities;
    }

    /**
     * Getter for the complete xml holding the DTD block inline
     * @return
     * @throws Exception
     */
    public String getXMLWithFullDTDDeclarationString() throws Exception {
        DTDNameChecker nameChecker = new DTDNameChecker();
        String xmlString = "";

        Element rootElement = this.dtd.getRootElement();

        if (rootElement != null) {

            // Write xml header
            xmlString += "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";

            // Start the DocType block of the DTD
            xmlString += "<!DOCTYPE ";

            // Write the name of the root element
            if (!nameChecker.checkForXMLName(rootElement.getName())) {
                throw new IllegalNAMEStringException("Root element: " + rootElement.getName(), rootElement.getName());
            }
            xmlString += rootElement.getName() + " ";

            // Handle external identifier imports:
            // SYSTEM/PUBLIC + Identifier + location of included DTD?

            // Write the opening bracket of the DocType block
            xmlString += "[\n\n";

            xmlString += getExternalSubsetString();

            // Close the docType block of the DTD
            xmlString += "]>\n";

            // Write the root element of the XML Part of the file.
            // This makes the file a valid XML file.
            xmlString += "<" + rootElement.getName() + "/>";

        } else {
            throw new DTDNoRootElementDefinedException("");
        }

        return xmlString;
    }

    /**
     * Returns the fully generated DTD String without declarationblock
     * @return String
     * @throws Exception
     */
    public String getExternalSubsetString() throws Exception {
        String doctypeString = "";

        // Write entities
        doctypeString += getEntityDeclarationsString();

        // Write notations
        doctypeString += getNotationDeclarationsString();

        // Write elements with their Attributes
        doctypeString += getElementDeclarationsString();

        return doctypeString;
    }

    /**
     * Returns the all entity declarations defined in the DTD
     * @return String
     */
    private String getEntityDeclarationsString() throws Exception {
        String entityString = "";

        if (this.writeEntities) {
            DTDEntityWriter entityWriter = new DTDEntityWriter(this.dtd);

            if (!this.dtd.getInternalEntitySymbolTable().getAllReferencedObjects().isEmpty()) {
                entityString += entityWriter.getInternalEntitiesString();
                entityString += "\n";
            }
            if (!this.dtd.getExternalEntitySymbolTable().getAllReferencedObjects().isEmpty()) {
                entityString += entityWriter.getExternalEntitiesString();
                entityString += "\n";
            }
        }
        return entityString;
    }

    /**
     * Returns the all element declarations defined in the DTD
     * @return String
     */
    private String getElementDeclarationsString() throws Exception {
        String elementString = "";

        if (!this.dtd.getElementSymbolTable().getAllReferencedObjects().isEmpty()) {

            // Sort the list of internal entites List
            Vector<Element> elements = new Vector<Element>(this.dtd.getElementSymbolTable().getAllReferencedObjects());
            Collections.sort(elements, new Comparator<Element>() {

                @Override
                public int compare(Element element1, Element element2) {
                    if (element1.getName() == null || element1.getName().equals("") || element2.getName() == null || element2.getName().equals("")) {
                        return 1;
                    }
                    return element1.getName().compareTo(element2.getName());
                }
            });
            for (Iterator<Element> it = elements.iterator(); it.hasNext();) {
                Element currentElement = it.next();
                DTDElementWriter elementWriter = new DTDElementWriter(currentElement);
                elementString += elementWriter.getElementDeclarationString();
                elementString += elementWriter.getElementAttachedAttributesString();
                elementString += "\n";
            }
        }
        return elementString;
    }

    /**
     * Returns the all notation declarations defined in the DTD
     * @return String
     */
    private String getNotationDeclarationsString() throws Exception {
        String notationString = "";

        if (!this.dtd.getNotationSymbolTable().getAllReferencedObjects().isEmpty()) {
            DTDNotationWriter notationWriter = new DTDNotationWriter(this.dtd);
            notationString += notationWriter.getNotationsString();
            notationString += "\n";
        }
        return notationString;
    }
}
