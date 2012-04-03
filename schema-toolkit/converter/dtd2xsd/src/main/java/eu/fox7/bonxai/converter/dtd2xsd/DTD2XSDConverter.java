package eu.fox7.bonxai.converter.dtd2xsd;

import eu.fox7.schematoolkit.dtd.om.DocumentTypeDefinition;
import eu.fox7.bonxai.tools.PreferencesManager;
import eu.fox7.bonxai.tools.StatusLogger;
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
     * Define the dummy namespace domainname for all DTD names (elements and
     * attributes) which are in the form of "abc:tempname".
     * In this case the "abc"-prefix in front of the colon will be handled as
     * namespace abbreviation.
     *
     * Example:
     * DUMMY_NAMESPACE_DOMAIN = "http://www.example.org";
     *
     * The resulting full qualified name of an element will be:
     * "{http://www.example.org/abc}tempname"
     *
     */
    public static String DUMMY_NAMESPACE_DOMAIN = "http://www.example.org";
    /**
     * Flag for the Namespace-generation Feature
     */
    private static boolean NAMESPACE_AWARE = true;
    /**
     * Variable for the target namespace of the resulting XML XSDSchema document
     */
    private static String TARGET_NAMESPACE = "";
    /**
     * Variable for the target namespace abbreviation of the resulting XML XSDSchema document
     */
    private static String TARGET_NAMESPACE_ABBREVIATION = "tns";
    /**
     * Variable for the resulting absolute filename
     */
    private String resultLocationFilename = "";

    /**
     * Constructor of class DTD2XSDConverter
     * @param dtd   DocumentTypeDefinition object holding the entire DTD structure
     */
    public DTD2XSDConverter(DocumentTypeDefinition dtd) {
        this.dtd = dtd;
        StatusLogger.logInfo("DTD2XSD", "Load preferences from file");
        this.loadPreferences();
        this.resultLocationFilename = "result.xsd";
    }

    /**
     * Constructor of class DTD2XSDConverter
     * @param dtd   DocumentTypeDefinition object holding the entire DTD structure
     */
    public DTD2XSDConverter(DocumentTypeDefinition dtd, String resultLocationFilename) {
        this.dtd = dtd;
        StatusLogger.logInfo("DTD2XSD", "Load preferences from file");
        this.loadPreferences();
        this.resultLocationFilename = resultLocationFilename;
    }

    /**
     * Method "convert" without any target namespace
     *
     * The empty namespace will be used as target namespace
     *
     * Start the conversion process and return the fully generated XML XSDSchema
     * @param namespaceAware
     * @return XSDSchema   XSD object holding the XSD structure
     * @throws ConversionFailedException
     */
    public XSDSchema convert(boolean namespaceAware) throws ConversionFailedException {
        return this.convert("", "", namespaceAware);
    }

    /**
     * Method "convert" with user-settings
     *
     * Start the conversion process and return the fully generated XML XSDSchema
     * @param namespaceAware
     * @return XSDSchema   XSD object holding the XSD structure
     * @throws ConversionFailedException
     */
    public XSDSchema convert() throws ConversionFailedException {

        boolean namespaceAware = false;
        if (DTD2XSDConverter.NAMESPACE_AWARE == true) {
            namespaceAware = true;
        }

        String targetNamespace = DTD2XSDConverter.TARGET_NAMESPACE;
        String abbreviation = TARGET_NAMESPACE_ABBREVIATION;

        if (DTD2XSDConverter.NAMESPACE_AWARE == true) {
            namespaceAware = true;
        }
        return this.convert(targetNamespace, abbreviation, namespaceAware);
    }

    /**
     * Method "convert" with the option to use the XML XSDSchema namespace as
     * target namespace
     *
     * The XML XSDSchema namespace will be used as target namespace, when the
     * parameter useXSDTargetNamespace is set to true.
     *
     * Start the conversion process and return the fully generated XML XSDSchema
     * @param namespaceAware
     * @param useXSDTargetNamespace
     * @return XSDSchema   XSD object holding the XSD structure
     * @throws ConversionFailedException
     */
    public XSDSchema convert(boolean namespaceAware, boolean useXSDTargetNamespace) throws ConversionFailedException {
        if (useXSDTargetNamespace) {
            return this.convert(DTD2XSDConverter.XMLSCHEMA_NAMESPACE, "xs", namespaceAware);
        } else {
            return this.convert("", "", namespaceAware);
        }
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
    public XSDSchema convert(String targetNamespace, String targetNamespaceAbbreviation, boolean namespaceAware) throws ConversionFailedException {
    	try {
    		IdentifiedNamespace targetIdentifiedNamespace = null;

    		StatusLogger.logInfo("DTD2XSD", "Start conversion progress");


    		// Generate a new XML XSDSchema object as the basis of the conversion progress
    		this.xmlSchema = new XSDSchema();

    		if (targetNamespace == null && targetNamespaceAbbreviation == null) {
    			targetNamespace = DTD2XSDConverter.XMLSCHEMA_NAMESPACE;
    		} else if (targetNamespace == null) {
    			targetNamespace = "";
    		}

    		this.xmlSchema.setTargetNamespace(targetNamespace);
    		targetIdentifiedNamespace = new IdentifiedNamespace(targetNamespaceAbbreviation, targetNamespace);

    		if (targetNamespaceAbbreviation != null && isNCName(targetNamespaceAbbreviation)) {
    			this.xmlSchema.getNamespaceList().addIdentifiedNamespace(targetIdentifiedNamespace);
    		}

    		if (this.xmlSchema.getNamespaceList().getNamespaceByUri(DTD2XSDConverter.XMLSCHEMA_NAMESPACE).getIdentifier() == null) {
    			IdentifiedNamespace xmlSchemaIdentifiedNamespace = new IdentifiedNamespace("xs", DTD2XSDConverter.XMLSCHEMA_NAMESPACE);
    			this.xmlSchema.getNamespaceList().addIdentifiedNamespace(xmlSchemaIdentifiedNamespace);
    		}

    		StatusLogger.logInfo("DTD2XSD", "The target namespace is generated as: \"" + targetIdentifiedNamespace.getUri() + "\"");

    		// Start the conversion
    		//        NotationConverter notationConverter = new NotationConverter(this.xmlSchema, targetIdentifiedNamespace, namespaceAware);

    		StatusLogger.logInfo("DTD2XSD", "Starting conversion progress with elements");
    		ElementConverter elementConverter = new ElementConverter(this.xmlSchema, targetIdentifiedNamespace, namespaceAware);
    		// The conversion of DTD attributes takes place within the ElementConverter class.

    		//        notationConverter.convert(this.dtd);
    		elementConverter.convert(this.dtd);

    		// Copy all Objects from the local symbolTable with the namespace of a foreign XSDSchema to the correct foreign XSDSchema
    		// setReference!
    		if (namespaceAware) {
    			StatusLogger.logInfo("DTD2XSD", "XML XSDSchema document will be generated as \"namespace aware\"");
    			StatusLogger.logInfo("DTD2XSD", "Generate external schemas for all different namespaces");

    			for (Iterator<SymbolTableRef<Attribute>> it = this.xmlSchema.getAttributeSymbolTable().getReferences().iterator(); it.hasNext();) {
    				SymbolTableRef<Attribute> symbolTableRefAttribute = it.next();
    				if (!symbolTableRefAttribute.getReference().getNamespace().equals(this.xmlSchema.getTargetNamespace())) {
    					ImportedSchema importedSchema = findImportedSchema(symbolTableRefAttribute.getReference().getNamespace());
    					if (importedSchema != null) {
    						SymbolTable<Attribute> attributeSymbolTable = (SymbolTable<Attribute>) importedSchema.getSchema().getAttributeSymbolTable();
    						attributeSymbolTable.setReference(symbolTableRefAttribute.getKey(), symbolTableRefAttribute);
    					}
    				}
    			}
    			for (Iterator<SymbolTableRef<Element>> it = this.xmlSchema.getElementSymbolTable().getReferences().iterator(); it.hasNext();) {
    				SymbolTableRef<Element> symbolTableRefElement = it.next();
    				if (!symbolTableRefElement.getReference().getNamespace().equals(this.xmlSchema.getTargetNamespace())) {
    					ImportedSchema importedSchema = findImportedSchema(symbolTableRefElement.getReference().getNamespace());
    					if (importedSchema != null) {
    						SymbolTable<Element> elementSymbolTable = (SymbolTable<Element>) importedSchema.getSchema().getElementSymbolTable();
    						elementSymbolTable.setReference(symbolTableRefElement.getKey(), symbolTableRefElement);
    					}
    				}
    			}
    			for (Iterator<SymbolTableRef<Type>> it = this.xmlSchema.getTypeSymbolTable().getReferences().iterator(); it.hasNext();) {
    				SymbolTableRef<Type> symbolTableRefType = it.next();
    				if (!symbolTableRefType.getReference().getNamespace().equals(this.xmlSchema.getTargetNamespace())) {
    					ImportedSchema importedSchema = findImportedSchema(symbolTableRefType.getReference().getNamespace());
    					if (importedSchema != null) {
    						SymbolTable<Type> typeSymbolTable = (SymbolTable<Type>) importedSchema.getSchema().getTypeSymbolTable();
    						typeSymbolTable.setReference(symbolTableRefType.getKey(), symbolTableRefType);
    					}
    				}
    				for (Iterator<ForeignSchema> it1 = this.xmlSchema.getForeignSchemas().iterator(); it1.hasNext();) {
    					ForeignSchema foreignSchema = (ImportedSchema) it1.next();
    					if (foreignSchema instanceof ImportedSchema) {
    						ImportedSchema importedSchema = (ImportedSchema) foreignSchema;
    						if (symbolTableRefType.getReference().getNamespace().equals(DTD2XSDConverter.XMLSCHEMA_NAMESPACE)) {
    							SymbolTable<Type> typeSymbolTable = (SymbolTable<Type>) importedSchema.getSchema().getTypeSymbolTable();
    							typeSymbolTable.setReference(symbolTableRefType.getKey(), symbolTableRefType);
    						}
    					}
    				}
    			}

    			if (!this.xmlSchema.getForeignSchemas().isEmpty()) {
    				StatusLogger.logInfo("DTD2XSD", this.xmlSchema.getForeignSchemas().size() + " external schemas generated");
    				for (Iterator<ForeignSchema> it1 = this.xmlSchema.getForeignSchemas().iterator(); it1.hasNext();) {
    					ForeignSchema foreignSchema = (ImportedSchema) it1.next();
    					if (foreignSchema instanceof ImportedSchema) {
    						ImportedSchema importedSchema = (ImportedSchema) foreignSchema;
    						if (!this.xmlSchema.getNamespaceList().getIdentifiedNamespaces().isEmpty()) {
    							for (Iterator<IdentifiedNamespace> it = this.xmlSchema.getNamespaceList().getIdentifiedNamespaces().iterator(); it.hasNext();) {
    								IdentifiedNamespace identifiedNamespace = it.next();
    								importedSchema.getSchema().getNamespaceList().addIdentifiedNamespace(identifiedNamespace);
    							}
    						}
    					}
    				}
    			}
    		}

    		this.xmlSchema.setSchemaLocation(this.resultLocationFilename);

    		StatusLogger.logLastInfoMessage("DTD2XSD", "Conversion completed: XML XSDSchema document structure created");
    		return this.xmlSchema;
    	} catch (Exception e) {
    		throw new ConversionFailedException(e);
    	}
    }

    /**
     * Find the correct imported schema object depending on a given namespace URI
     * @param namespace     Namespace URI
     * @return ImportedSchema   Correct ImportedSchema Object
     */
    protected ImportedSchema findImportedSchema(String namespace) {
        if (!this.xmlSchema.getForeignSchemas().isEmpty()) {
            for (Iterator<ForeignSchema> it1 = this.xmlSchema.getForeignSchemas().iterator(); it1.hasNext();) {
                ForeignSchema foreignSchema = (ImportedSchema) it1.next();
                if (foreignSchema instanceof ImportedSchema) {
                    ImportedSchema importedSchema = (ImportedSchema) foreignSchema;
                    if (importedSchema.getNamespace().equals(namespace)) {
                        return importedSchema;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Load application settings from config file
     */
    private void loadPreferences() {
        /***************************************************************************
         * Settings
         **************************************************************************/
        // Initialize PreferencesManager
        PreferencesManager preferencesManager = PreferencesManager.getPreferencesManager();
        /**
         * Define the dummy namespace domainname for all DTD names (elements and
         * attributes) which are in the form of "abc:tempname".
         * In this case the "abc"-prefix in front of the colon will be handled as
         * namespace abbreviation.
         *
         * Example:
         * DUMMY_NAMESPACE_DOMAIN = "http://www.example.org";
         *
         * The resulting full qualified name of an element will be:
         * "{http://www.example.org/abc}tempname"
         *
         */
        DUMMY_NAMESPACE_DOMAIN = preferencesManager.getSetting("DTD2XSD.dummy_namespace_domain", DUMMY_NAMESPACE_DOMAIN);

        NAMESPACE_AWARE = preferencesManager.getBooleanSetting("DTD2XSD.namespace_aware", NAMESPACE_AWARE);
        TARGET_NAMESPACE = preferencesManager.getSetting("DTD2XSD.target_namespace", TARGET_NAMESPACE);
        TARGET_NAMESPACE_ABBREVIATION = preferencesManager.getSetting("DTD2XSD.target_namespace_abbreviation", TARGET_NAMESPACE_ABBREVIATION);

    }
}
