package de.tudortmund.cs.bonxai.converter.dtd2xsd;

import de.tudortmund.cs.bonxai.common.IdentifiedNamespace;
import de.tudortmund.cs.bonxai.converter.dtd2xsd.exceptions.DTDNameIsEmptyException;
import de.tudortmund.cs.bonxai.converter.dtd2xsd.exceptions.DTDNameStartsWithUnsupportedSymbolException;
import de.tudortmund.cs.bonxai.converter.dtd2xsd.exceptions.IdentifiedNamespaceNotFoundException;
import de.tudortmund.cs.bonxai.xsd.ForeignSchema;
import de.tudortmund.cs.bonxai.xsd.ImportedSchema;
import de.tudortmund.cs.bonxai.xsd.XSDSchema;
import de.tudortmund.cs.bonxai.xsd.tools.NameChecker;

import java.util.Iterator;

/**
 * Base class for all DTD2XSD converter classes
 * @author Lars Schmidt
 */
public abstract class ConverterBase extends NameChecker {

    protected XSDSchema xmlSchema;
    protected IdentifiedNamespace targetNamespace;
    protected boolean namespaceAware;

    public ConverterBase(XSDSchema xmlSchema, IdentifiedNamespace targetNamespace, boolean namespaceAware) {
        this.xmlSchema = xmlSchema;
        this.targetNamespace = targetNamespace;
        this.namespaceAware = namespaceAware;
    }

    /**
     * Method generateXSDFQName is used for Element- and Attributenames
     * 
     * In DTD there are names like "xs:minLength" allowed for an element or an 
     * attibute. These are not a valid XML XSDSchema NCNames! 
     * It is also a problem, that there are no namespaces in DTD.
     * 
     * A possible solution for this problem is, to define IdentifiedNamespace 
     * Dummies and use their abbreviation for the full-qualified name in the XSD
     * object model.
     *
     * @param dtdNameString
     * @return String
     * @throws Exception 
     */
    protected String generateXSDFQName(String dtdNameString) throws Exception {
        String returnXSDFQName = "";

        if (dtdNameString == null || dtdNameString.equals("")) {
            throw new DTDNameIsEmptyException();
        }

        String targetUri = ((this.targetNamespace == null) ? "" : this.targetNamespace.getUri());

        if (this.namespaceAware) {
            if (isQName(dtdNameString) && dtdNameString.contains(":")) {
                String[] nameArray = dtdNameString.split(":");

                if (nameArray.length == 2) {
                    String resultNamespaceUri = "";

                    // Namespace handling
                    IdentifiedNamespace idNamespace = this.xmlSchema.getNamespaceList().getNamespaceByIdentifier(nameArray[0]);
                    if (idNamespace.getUri() != null) {
                        resultNamespaceUri = idNamespace.getUri();
                    } else {
                        String dummyNamespaceUri = DTD2XSDConverter.DUMMY_NAMESPACE_DOMAIN + "/" + nameArray[0];
                        IdentifiedNamespace identifiedNamespace = new IdentifiedNamespace(nameArray[0], dummyNamespaceUri);
                        this.xmlSchema.getNamespaceList().addIdentifiedNamespace(identifiedNamespace);
                        resultNamespaceUri = dummyNamespaceUri;
                    }
                    returnXSDFQName = "{" + resultNamespaceUri + "}" + nameArray[1].replaceAll("[^0-9a-zA-Z\\.\\-\\_]", "-");
                }
            } else {
                String newName = dtdNameString.replaceAll("[^0-9a-zA-Z\\.\\-\\_]", "-");
                if (newName.startsWith("-")) {
                    throw new DTDNameStartsWithUnsupportedSymbolException(dtdNameString);
                }
                returnXSDFQName = "{" + targetUri + "}" + newName;
            }
        } else {
            String newName = dtdNameString.replaceAll("[^0-9a-zA-Z\\.\\-\\_]", "-");
            if (newName.startsWith("-")) {
                throw new DTDNameStartsWithUnsupportedSymbolException(dtdNameString);
            }
            returnXSDFQName = "{" + targetUri + "}" + newName;
        }
        return returnXSDFQName;
    }

    /**
     * Method for generating or getting a schema for a given namespace.
     * If a schema with this namespace is already in the identifiedNamespaceList
     * this schema will be returned. If not, a new import will be generated
     * referencing a corresponding schema with the given namespace.
     * @param namespace     String namespace for check or generation
     * @return ImportedSchema   ForeignSchema object holding the schema with the given namespace
     * @throws Exception        IdentifiedNamespaceNotFoundException
     */
    protected ImportedSchema updateOrCreateImportedSchema(String namespace) throws Exception {
        // Check if there is already an imported schema for the current namespace and return it.
        for (Iterator<ForeignSchema> it1 = this.xmlSchema.getForeignSchemas().iterator(); it1.hasNext();) {
            ForeignSchema foreignSchema = (ImportedSchema) it1.next();
            if (foreignSchema instanceof ImportedSchema) {
                ImportedSchema importedSchema = (ImportedSchema) foreignSchema;
                if (importedSchema.getNamespace().equals(namespace)) {
                    return importedSchema;
                }
            }
        }

        // If there is no imported schema for the given namespace, create a new one
        if (this.xmlSchema.getNamespaceList().getNamespaceByUri(namespace).getIdentifier() == null) {
            throw new IdentifiedNamespaceNotFoundException(namespace);
        }

        ImportedSchema importedSchema = new ImportedSchema(namespace, "importedSchema_" + this.xmlSchema.getNamespaceList().getNamespaceByUri(namespace).getIdentifier() + ".xsd");
        importedSchema.setParentSchema(this.xmlSchema);
        XSDSchema newExternalSchema = new XSDSchema(namespace);
        newExternalSchema.setTargetNamespace(namespace);
        
        // Set the XML XSDSchema namespace to the identifiedNamespacelist of the new schema
        IdentifiedNamespace xmlSchemaIdentifiedNamespace = new IdentifiedNamespace("xs", DTD2XSDConverter.XMLSCHEMA_NAMESPACE);
        newExternalSchema.getNamespaceList().addIdentifiedNamespace(xmlSchemaIdentifiedNamespace);

        importedSchema.setSchema(newExternalSchema);
        this.xmlSchema.addForeignSchema(importedSchema);
        
        // return the new imported schema
        return importedSchema;

    }
}
