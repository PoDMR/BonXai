package de.tudortmund.cs.bonxai.dtd.writer;

import de.tudortmund.cs.bonxai.dtd.DocumentTypeDefinition;
import de.tudortmund.cs.bonxai.dtd.InternalEntity;
import de.tudortmund.cs.bonxai.dtd.common.DTDNameChecker;
import de.tudortmund.cs.bonxai.dtd.common.exceptions.IllegalNAMEStringException;
import de.tudortmund.cs.bonxai.dtd.writer.exceptions.DTDInternalEntityEmptyNameException;
import de.tudortmund.cs.bonxai.dtd.writer.exceptions.DTDInternalEntityValueNullException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Vector;

/**
 * Class DTDEntityWriter
 * This class generates a DTD entity declaration String containing all
 * entities of the given DTD.
 *
 * @author Lars Schmidt
 */
public class DTDEntityWriter {

    private final DocumentTypeDefinition dtd;

    /**
     * Constructor of class DTDEntityWriter
     * @param dtd
     */
    public DTDEntityWriter(DocumentTypeDefinition dtd) {
        this.dtd = dtd;
    }

    /**
     * Get a String containing all defined internal entities from the given DTD
     * @return String
     * @throws Exception 
     */
    public String getInternalEntitiesString() throws Exception {
        /**
         * <!ENTITY [%] name "value" >
         */
        DTDNameChecker nameChecker = new DTDNameChecker();
        String internalEntityString = "";
        if (!this.dtd.getInternalEntitySymbolTable().getAllReferencedObjects().isEmpty()) {

            // Sort the list of internal entites List
            Vector<InternalEntity> internalEntities = new Vector<InternalEntity>(this.dtd.getInternalEntitySymbolTable().getAllReferencedObjects());
            Collections.sort(internalEntities, new Comparator<InternalEntity>() {

                @Override
                public int compare(InternalEntity internalEntity1, InternalEntity internalEntity2) {
                    if (internalEntity1.getName() == null || internalEntity1.getName().equals("") || internalEntity2.getName() == null || internalEntity2.getName().equals("")) {
                        return 1;
                    }
                    return internalEntity1.getName().compareTo(internalEntity2.getName());
                }
            });
            for (Iterator<InternalEntity> it = internalEntities.iterator(); it.hasNext();) {
                InternalEntity internalEntity = it.next();
                if (internalEntity.getName() == null || internalEntity.getName().equals("")) {
                    throw new DTDInternalEntityEmptyNameException("");
                }
                if (internalEntity.getValue() == null) {
                    throw new DTDInternalEntityValueNullException(internalEntity.getName());
                }

                String stringDelimiter = "\"";
                if (internalEntity.getValue().contains("\"")) {
                    stringDelimiter = "'";
                }
                if (internalEntity.getValue().equals(" ")) {
                    internalEntity.setValue("&#160;");
                }
                String internalEntityName = "";
                if (internalEntity.getName().startsWith("%"))   {
                    internalEntityName = internalEntity.getName().substring(1);
                } else {
                    internalEntityName = internalEntity.getName();
                }
                if (!nameChecker.checkForXMLName(internalEntityName)) {
                    throw new IllegalNAMEStringException("InternalEntity: " + internalEntityName, internalEntityName);
                }

                internalEntityString += "<!ENTITY" + " " + ((internalEntity.getName().startsWith("%")) ? "% " + internalEntityName : internalEntityName) + " " + stringDelimiter + internalEntity.getValue() + stringDelimiter;
                internalEntityString += ">\n";
            }
        }
        return internalEntityString;
    }

    /**
     * Get a String containing all defined external entities from the given DTD
     *
     * !This is not used at the moment.!
     *
     * @return String
     */
    public String getExternalEntitiesString() {
        /**
         * Important:
         * This is not necessary and therefore not supported in the current
         * implementation. All external entities are fetched by the SAXParser itself
         * and their content will be placed within the generated dtd structure.
         */
        return "";
    }
}
