package eu.fox7.schematoolkit.dtd.writer;

import eu.fox7.schematoolkit.dtd.common.DTDNameChecker;
import eu.fox7.schematoolkit.dtd.common.exceptions.IllegalNAMEStringException;
import eu.fox7.schematoolkit.dtd.om.DocumentTypeDefinition;
import eu.fox7.schematoolkit.dtd.om.Notation;
import eu.fox7.schematoolkit.dtd.om.PublicNotation;
import eu.fox7.schematoolkit.dtd.om.SystemNotation;
import eu.fox7.schematoolkit.dtd.writer.exceptions.DTDNotationEmptyNameException;
import eu.fox7.schematoolkit.dtd.writer.exceptions.DTDNotationIdentifierNullException;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Vector;

/**
 * Class DTDNotationWriter
 * This class generates a DTD notation declaration String containing all
 * notations of the given DTD.
 * 
 * @author Lars Schmidt
 */
public class DTDNotationWriter {

    private final DocumentTypeDefinition dtd;

    /**
     * Constructor of class DTDNotationWriter
     * @param dtd
     */
    public DTDNotationWriter(DocumentTypeDefinition dtd) {
        this.dtd = dtd;
    }

    /**
     * Get a String containing all defined notations from the given DTD
     * @return String
     * @throws Exception 
     */
    public String getNotationsString() throws Exception {
        DTDNameChecker nameChecker = new DTDNameChecker();
        String notationString = "";
        // Iterate over all notations in the DTD
        for (Notation notation: this.dtd.getNotations()) {
        	if (notation.getName() == null || notation.getName().equals("")) {
        		throw new DTDNotationEmptyNameException("");
        	}
        	if (!nameChecker.checkForXMLName(notation.getName())) {
        		throw new IllegalNAMEStringException("NOTATION: " + notation.getName(), notation.getName());
        	}
        	notationString += "<!NOTATION" + " " + notation.getName() + " ";
        	if (notation instanceof SystemNotation) {
        		if (notation.getIdentifier() != null) {
        			notationString += "SYSTEM" + " \"" + notation.getIdentifier() + "\"";
        		} else {
        			throw new DTDNotationIdentifierNullException(notation.getName());
        		}
        	} else if (notation instanceof PublicNotation) {
        		if (notation.getIdentifier() != null) {
        			notationString += "PUBLIC" + " \"" + notation.getIdentifier() + "\"";
        		} else {
        			throw new DTDNotationIdentifierNullException(notation.getName());
        		}
        	}
        	notationString += ">\n";
        }
        return notationString;
    }
}
