package de.tudortmund.cs.bonxai.dtd.writer;

import de.tudortmund.cs.bonxai.dtd.DocumentTypeDefinition;
import de.tudortmund.cs.bonxai.dtd.Notation;
import de.tudortmund.cs.bonxai.dtd.PublicNotation;
import de.tudortmund.cs.bonxai.dtd.SystemNotation;
import de.tudortmund.cs.bonxai.dtd.common.DTDNameChecker;
import de.tudortmund.cs.bonxai.dtd.common.exceptions.IllegalNAMEStringException;
import de.tudortmund.cs.bonxai.dtd.writer.exceptions.DTDNotationEmptyNameException;
import de.tudortmund.cs.bonxai.dtd.writer.exceptions.DTDNotationIdentifierNullException;
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
        if (!this.dtd.getNotationSymbolTable().getAllReferencedObjects().isEmpty()) {


            // Sort the list of internal entites List
            Vector<Notation> notations = new Vector<Notation>(this.dtd.getNotationSymbolTable().getAllReferencedObjects());
            Collections.sort(notations, new Comparator<Notation>() {

                @Override
                public int compare(Notation notation1, Notation notation2) {
                    if (notation1.getName() == null || notation1.getName().equals("") || notation2.getName() == null || notation2.getName().equals("")) {
                        return 1;
                    }
                    return notation1.getName().compareTo(notation2.getName());
                }
            });

            // Iterate over all notations in the DTD
            for (Iterator<Notation> it = notations.iterator(); it.hasNext();) {
                Notation notation = it.next();
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
        }
        return notationString;
    }
}
