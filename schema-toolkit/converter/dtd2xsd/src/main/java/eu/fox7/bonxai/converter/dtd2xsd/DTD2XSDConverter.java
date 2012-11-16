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

package eu.fox7.bonxai.converter.dtd2xsd;

import eu.fox7.schematoolkit.dtd.om.DocumentTypeDefinition;
import eu.fox7.schematoolkit.exceptions.ConversionFailedException;
import eu.fox7.schematoolkit.common.DefaultNamespace;
import eu.fox7.schematoolkit.common.IdentifiedNamespace;
import eu.fox7.schematoolkit.xsd.om.Attribute;
import eu.fox7.schematoolkit.xsd.om.Element;
import eu.fox7.schematoolkit.xsd.om.ForeignSchema;
import eu.fox7.schematoolkit.xsd.om.ImportedSchema;
import eu.fox7.schematoolkit.xsd.om.Type;
import eu.fox7.schematoolkit.xsd.om.XSDSchema;
import eu.fox7.schematoolkit.xsd.tools.NameChecker;

import java.util.Iterator;

/**
 * DTD2XSDConverter
 *
 * This class allows the conversion from the
 * DTD object model (eu.fox7.bonxai.dtd) to the
 * XSD object model (eu.fox7.schematoolkit.xsd.om).
 *
 * This is the main class, which initializes the conversion itself.
 * 
 * The DTD object holding the entire DTD structure is given to the constructor
 * and the convert-method starts the conversion process and returns the fully
 * generated XSD object holding the XSD structure.
 *
 * @author Lars Schmidt
 */
public class DTD2XSDConverter extends NameChecker {

    /**
     * The object for holding the Document Type Definition schema (source of the conversion).
     */
    private DocumentTypeDefinition dtd;
    /**
     * The object for holding the XML XSDSchema schema (target of the conversion).
     */
    private XSDSchema xmlSchema;
    /**
     * The XML XSDSchema namespace.
     */
    public static final String XMLSCHEMA_NAMESPACE = "http://www.w3.org/2001/XMLSchema";
    /**
     * Variable for the target namespace of the resulting XML XSDSchema document
     */
    private static String TARGET_NAMESPACE = "";

    /**
     * Constructor of class DTD2XSDConverter
     * @param dtd   DocumentTypeDefinition object holding the entire DTD structure
     */
    public DTD2XSDConverter(DocumentTypeDefinition dtd) {
        this.dtd = dtd;
//        StatusLogger.logInfo("DTD2XSD", "Load preferences from file");
//        this.loadPreferences();
    }

    /**
     * Method "convert" without any target namespace
     *
     * The empty namespace will be used as target namespace
     *
     * Start the conversion process and return the fully generated XML XSDSchema
     * @return XSDSchema   XSD object holding the XSD structure
     * @throws ConversionFailedException
     */
    public XSDSchema convert() throws ConversionFailedException {
        String targetNamespace = DTD2XSDConverter.TARGET_NAMESPACE;

        return this.convert(targetNamespace);
    }

    /**
     * Method "convert"
     *
     * This method allows different options for the handling of the target
     * namespace
     *
     * Start the conversion process and return the fully generated XML XSDSchema
     * @param targetNamespace 
     * @param targetNamespaceAbbreviation 
     * @param namespaceAware
     * @return XSDSchema   XSD object holding the XSD structure
     * @throws ConversionFailedException 
     */
    public XSDSchema convert(String targetNamespaceURI) throws ConversionFailedException {
    	// Generate a new XML XSDSchema object as the basis of the conversion progress
    	this.xmlSchema = new XSDSchema();

    	if (targetNamespaceURI == null) {
    		targetNamespaceURI = "";
    	}

    	DefaultNamespace targetNamespace = new DefaultNamespace(targetNamespaceURI);
    	this.xmlSchema.setTargetNamespace(targetNamespace);
    	this.xmlSchema.setDefaultNamespace(targetNamespace);
    	this.xmlSchema.addIdentifiedNamespace(new IdentifiedNamespace("xs",XSDSchema.XMLSCHEMA_NAMESPACE));

    	// Start the conversion
    	//        NotationConverter notationConverter = new NotationConverter(this.xmlSchema, targetIdentifiedNamespace, namespaceAware);

    	//    		StatusLogger.logInfo("DTD2XSD", "Starting conversion progress with elements");
    	ElementConverter elementConverter = new ElementConverter(this.xmlSchema, targetNamespace);
    	// The conversion of DTD attributes takes place within the ElementConverter class.

    	//        notationConverter.convert(this.dtd);
    	elementConverter.convert(this.dtd);

    	return this.xmlSchema;
    }
}
