package eu.fox7.schematoolkit.dtd.writer;

import eu.fox7.schematoolkit.dtd.common.DTDNameChecker;
import eu.fox7.schematoolkit.dtd.common.exceptions.IllegalNAMEStringException;
import eu.fox7.schematoolkit.dtd.om.DocumentTypeDefinition;
import eu.fox7.schematoolkit.dtd.om.InternalEntity;
import eu.fox7.schematoolkit.dtd.writer.exceptions.DTDInternalEntityEmptyNameException;
import eu.fox7.schematoolkit.dtd.writer.exceptions.DTDInternalEntityValueNullException;

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
        for (InternalEntity internalEntity: this.dtd.getInternalEntitys()) {
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
