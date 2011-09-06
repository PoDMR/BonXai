package eu.fox7.bonxai.converter.xsd2relaxng;

import eu.fox7.bonxai.common.IdentifiedNamespace;
import eu.fox7.bonxai.common.NamespaceList;
import eu.fox7.bonxai.converter.ConversionFailedException;
import eu.fox7.bonxai.relaxng.*;
import eu.fox7.bonxai.tools.PreferencesManager;
import eu.fox7.bonxai.tools.StatusLogger;
import eu.fox7.bonxai.xsd.AttributeGroup;
import eu.fox7.bonxai.xsd.ComplexType;
import eu.fox7.bonxai.xsd.ForeignSchema;
import eu.fox7.bonxai.xsd.SimpleType;
import eu.fox7.bonxai.xsd.Type;
import eu.fox7.bonxai.xsd.XSDSchema;
import eu.fox7.bonxai.xsd.parser.ForeignSchemaLoader;
import eu.fox7.bonxai.xsd.tools.BlockFinalSpreadingHandler;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

/**
 * XSD2RelaxNGConverter
 *
 * This class allows the conversion from the
 * XSD object model (eu.fox7.bonxai.xsd) to the
 * RELAX NG object model (eu.fox7.bonxai.dtd).
 *
 * This is the main class, which initializes the conversion itself.
 *
 * The XML XSDSchema object holding the entire XSD structure is given to the
 * constructor and the convert-method starts the conversion process and returns
 * the fully generated rng object holding the RELAX NG structure.
 *
 * @author Lars Schmidt
 */
public class XSD2RelaxNGConverter extends ConverterBase {

    /**
     * The XML XSDSchema namespace.
     */
    public static final String XMLSCHEMA_NAMESPACE = "http://www.w3.org/2001/XMLSchema";
    /**
     * The RELAX NG namespace.
     */
    public static final String RELAXNG_NAMESPACE = "http://relaxng.org/ns/structure/1.0";
    /**
     * The XML XSDSchema datatype namespace for RELAX NG usage.
     */
    public static final String RELAXNG_XMLSCHEMA_DATATYPE_NAMESPACE = "http://www.w3.org/2001/XMLSchema-datatypes";
    /***************************************************************************
     * Settings
     **************************************************************************/
    /**
     * Activate debug mode, in which debug information are written to the
     * console
     */
    public static boolean debug = false;
    /**
     * Countingpattern upper bound: multiplicity of nested countingPatterns
     * result: deeper countingPatterns of the form "(element)[6, 10]" are 
     * converted to "(...)*" in the case of a value between 1 and 9.
     */
    public static int COUNTINGPATTERN_UPPER_BOUND_MULTIPLICITY = 100;
    /**
     * Prefix for a define that represents a XML XSDSchema type
     */
    public static String PREFIX_TYPE_DEFINE = "";
    /**
     * Prefix for a define that represents a XML XSDSchema element
     */
    public static String PREFIX_ELEMENT_DEFINE = "";
    /**
     * Prefix for a define that represents a XML XSDSchema attribute
     */
    public static String PREFIX_ATTRIBUTE_DEFINE = "";
    /**
     *  Prefix for a define that represents a XML XSDSchema attributeGroup
     */
    public static String PREFIX_ATTRIBUTEGROUP_DEFINE = "";
    /**
     * Prefix for a define that represents a XML XSDSchema group
     */
    public static String PREFIX_GROUP_DEFINE = "";
    /**
     * Setting to force the writing of not used schema components
     */
    public static boolean WRITE_NOT_USED_COMPONENTS = true;
    /**
     * Setting to prohibit the calculation and generation of possible
     * substitutions for elements and types.
     */
    public static boolean HANDLE_SUBSTITUTIONS = true;

    /**
     * Constructor of class XSD2DTDConverter
     * @param xmlSchema     The XML XSDSchema as source of the conversion progress.
     * @param debug 
     */
    public XSD2RelaxNGConverter(XSDSchema xmlSchema, boolean debug) {
        super(xmlSchema, new RelaxNGSchema());
        StatusLogger.logInfo("XSD2RNG", "Load preferences from file");
        this.loadPreferences();
        XSD2RelaxNGConverter.setDebug(debug);
    }

    /**
     * Activates or deactivates debug mode
     * @param debug     If true console output is enabled else it is disabled
     */
    public static void setDebug(boolean debug) {
        XSD2RelaxNGConverter.debug = debug;
    }

    /**
     * Returns true when debug mode is active else false.
     * @return current debug flag
     */
    public static boolean getDebug() {
        return XSD2RelaxNGConverter.debug;
    }

    /**
     * Method "convert"
     *
     * The empty namespace will be used as target namespace
     *
     * Start the conversion process and return the fully generated DTD
     * @return XSDSchema   XSD object holding the XSD structure
     * @throws Exception
     */
    public RelaxNGSchema convert() throws ConversionFailedException {
    	try {
    		StatusLogger.logInfo("XSD2RNG", "Start conversion progress");
    		/**
    		 * 0.
    		 * Handle all external schemas and link the references to the correct 
    		 * declarations
    		 */
    		StatusLogger.logInfo("XSD2RNG", "Start loading of external schemas");
    		if (this.xmlSchema.getSchemaLocation()!=null) {
    			ForeignSchemaLoader foreignSchemaLoader = new ForeignSchemaLoader(this.xmlSchema, false);
    			foreignSchemaLoader.findForeignSchemas();
    		}

    		/**
    		 * 1.
    		 * Handle the default values for block and final
    		 */
    		if (HANDLE_SUBSTITUTIONS) {
    			StatusLogger.logInfo("XSD2RNG", "Spread block-/finalDefault values to all components");
    			BlockFinalSpreadingHandler blockFinalSpreadingHandler = new BlockFinalSpreadingHandler();
    			blockFinalSpreadingHandler.spread(this.xmlSchema);
    		}

    		/**
    		 * 2.
    		 * Type preprocessing: find all derived Types of a base type, regarding 
    		 * to the block/blockDefault attributes. This is necessary for the
    		 * correct handling of the XML XSDSchema inheritance feature
    		 * (restriction, extension) in case of the conversion to RELAX NG.
    		 * In an instance it is possible to choose the type via the
    		 * xsi:type attribute.
    		 * Types declared in external schemas have to be checked in this manner, too.
    		 */
    		LinkedHashMap<Type, LinkedHashSet<Type>> typeInheritanceInformation = null;
    		if (HANDLE_SUBSTITUTIONS) {
    			StatusLogger.logInfo("XSD2RNG", "Collecting information about type inheritance");
    			InheritanceInformationCollector inheritanceInformationCollector = new InheritanceInformationCollector();
    			inheritanceInformationCollector.collectInformation(this.xmlSchema);
    			typeInheritanceInformation = inheritanceInformationCollector.getInheritanceInformation();
    			StatusLogger.logInfo("XSD2RNG", "Possible substitutions has been found for " + typeInheritanceInformation.size() + " types");
    		} else {
    			typeInheritanceInformation = new LinkedHashMap<Type, LinkedHashSet<Type>>();
    		}
    		/**
    		 * 3.
    		 * Element preprocessing: find all substitutionGroups and calculate
    		 * valid substitutions for an instance regarding to the
    		 * block/blockDefault attributes.
    		 * Elements declared in external schemas have to be checked in this manner, too.
    		 */
    		LinkedHashMap<eu.fox7.bonxai.xsd.Element, LinkedHashSet<eu.fox7.bonxai.xsd.Element>> elementInheritanceInformation = null;
    		if (HANDLE_SUBSTITUTIONS) {
    			StatusLogger.logInfo("XSD2RNG", "Collecting information about element inheritance (substitution groups)");
    			SubstitutionGroupInformationCollector substitutionGroupInformationCollector = new SubstitutionGroupInformationCollector();
    			substitutionGroupInformationCollector.collectInformation(this.xmlSchema);
    			elementInheritanceInformation = substitutionGroupInformationCollector.getSubstitutionGroupInformation();
    			StatusLogger.logInfo("XSD2RNG", "Possible substitutions has been found for " + typeInheritanceInformation.size() + " elements");
    		} else {
    			elementInheritanceInformation = new LinkedHashMap<eu.fox7.bonxai.xsd.Element, LinkedHashSet<eu.fox7.bonxai.xsd.Element>>();
    		}
    		/**
    		 * 4.
    		 * Generate a valid root pattern
    		 */
    		String defaultNamespace = null;
    		if (xmlSchema.getNamespaceList() != null
    				&& xmlSchema.getNamespaceList().getDefaultNamespace() != null
    				&& xmlSchema.getNamespaceList().getDefaultNamespace().getUri() != null) {
    			defaultNamespace = xmlSchema.getNamespaceList().getDefaultNamespace().getUri();
    			// In XML XSDSchema a not given namespace has the same meaning as the empty namespace ("").
    			// In RELAX NG the empty namespace ("") can be used to overwrite the namespace of the parent element.
    			// It is the normal case, that namespaces are be derived by childPatterns.
    		}
    		StatusLogger.logInfo("XSD2RNG", "The targetNamespace for the resulting RELAX NG schema is: \"" + defaultNamespace + "\"");

    		StatusLogger.logInfo("XSD2RNG", "Create a valid root pattern");
    		// Define a grammar as root pattern of the new RELAX NG schema
    		Grammar grammar = new Grammar(RELAXNG_XMLSCHEMA_DATATYPE_NAMESPACE, defaultNamespace, RELAXNG_NAMESPACE);
    		this.relaxng.setRootPattern(grammar);

    		/**
    		 * 5.
    		 * Start the conversion with all XSD toplevel elements
    		 */
    		StatusLogger.logInfo("XSD2RNG", "Starting conversion progress with toplevel elements ...");
    		StartElementConverter startElementConverter = new StartElementConverter(this.xmlSchema, this.relaxng, xsdAttributeDefineRefMap, xsdAttributeGroupDefineRefMap, xsdElementDefineRefMap, xsdGroupDefineRefMap, xsdTypeDefineRefMap, elementInheritanceInformation, typeInheritanceInformation);
    		startElementConverter.startConversionWithToplevelElements();

    		/***********************************************************************
    		 * Conversion of XML XSDSchema constraints?
    		 * -------------------------------------
    		 *
    		 * There is no such feature in RELAX NG equal to the possible 
    		 * constraints in XML XSDSchema (key, keyRef, unique).
    		 *
    		 * One could convert some of these constraints to the XML attribute 
    		 * types ID/IDREF/IDREFS like in the conversion progress from XML XSDSchema
    		 * to DTD, but this handling would remove all other type-information
    		 * from an attribute!
    		 *
    		 * This would lead to a very inaccurate upper approximation.
    		 * In DTD this is not a problem, because the conversion itself is
    		 * already an upper approximation and the content of an attribute can
    		 * not be defined in such a detailed way like in RELAX NG.
    		 *
    		 * The conversion of XML XSDSchema constraints is not possible without
    		 * loosing too much information about the content of an attribute!
    		 **********************************************************************/

    		/**
    		 * 6.  
    		 * Convert remaining components, which are not used within the schema, 
    		 * but anyhow defined.
    		 * 
    		 * Types, Attributes (top-level), AttributeGroups, Groups
    		 */
    		if (XSD2RelaxNGConverter.WRITE_NOT_USED_COMPONENTS) {
    			StatusLogger.logInfo("XSD2RNG", "Search for not used components in the XML schema document");
    			this.generateDefinesForNotUsedComponents(typeInheritanceInformation, elementInheritanceInformation);
    		}

    		/**
    		 * 7.
    		 * Convert all namespaces of the XML schema document to the RELAX NG root pattern (grammar)
    		 */
    		if (this.xmlSchema.getNamespaceList() != null) {
    			StatusLogger.logInfo("XSD2RNG", "Writing namespace definitions to the root pattern ...");
    			NamespaceList namespaceList = this.xmlSchema.getNamespaceList();

    			if (!namespaceList.getIdentifiedNamespaces().isEmpty()) {
    				for (Iterator<IdentifiedNamespace> it = namespaceList.getIdentifiedNamespaces().iterator(); it.hasNext();) {
    					IdentifiedNamespace identifiedNamespace = it.next();
    					if (identifiedNamespace.getIdentifier() != null && !identifiedNamespace.getIdentifier().equals("")) {
    						grammar.getNamespaceList().addIdentifiedNamespace(new IdentifiedNamespace(identifiedNamespace.getIdentifier(), identifiedNamespace.getUri()));
    					}
    				}
    			}
    		}

    		StatusLogger.logInfo("XSD2RNG", grammar.getDefinedPatternNames().size() + " different define-patterns has been written");

    		/**
    		 * 8.
    		 * Return the generated RELAX NG schema
    		 */
    		StatusLogger.logLastInfoMessage("XSD2RNG", "Conversion completed: RELAX NG schema created");
    		return this.relaxng;
    	} catch (Exception e) {
    		throw new ConversionFailedException(e);
    	}
    }

    /**
     * Find not used schema components to provide a mechanism to write them into
     * the RELAX NG result anyhow
     * @param xmlSchema             The source for the conversion progress
     * @param alreadySeenSchemas    A set of already seen schemas to avoid duplicate handling
     * @param foundTypes            A set of all found types that are not used within the schema
     * @param foundAttributes       A set of all found attributes that are not used within the schema
     * @param foundAttributeGroups  A set of all found attributeGroups that are not used within the schema
     * @param foundGroups           A set of all found groups that are not used within the schema
     */
    private void findNotUsedComponents(XSDSchema xmlSchema, LinkedHashSet<XSDSchema> alreadySeenSchemas, LinkedHashSet<Type> foundTypes, LinkedHashSet<eu.fox7.bonxai.xsd.Attribute> foundAttributes, LinkedHashSet<eu.fox7.bonxai.xsd.AttributeGroup> foundAttributeGroups, LinkedHashSet<eu.fox7.bonxai.xsd.Group> foundGroups) {

        if (!alreadySeenSchemas.contains(xmlSchema)) {

            // Process types
            for (Iterator<Type> it = xmlSchema.getTypes().iterator(); it.hasNext();) {
                Type type = it.next();
                if (!this.xsdTypeDefineRefMap.containsKey(type)) {
                    foundTypes.add(type);
                }
            }

            // Process attributes
            for (Iterator<eu.fox7.bonxai.xsd.Attribute> it = xmlSchema.getAttributes().iterator(); it.hasNext();) {
                eu.fox7.bonxai.xsd.Attribute attribute = it.next();
                if (!this.xsdAttributeDefineRefMap.containsKey(attribute)) {
                    foundAttributes.add(attribute);
                }
            }

            // Process attributeGroups
            for (Iterator<eu.fox7.bonxai.xsd.AttributeGroup> it = xmlSchema.getAttributeGroups().iterator(); it.hasNext();) {
                eu.fox7.bonxai.xsd.AttributeGroup attributeGroup = it.next();
                if (!this.xsdAttributeGroupDefineRefMap.containsKey(attributeGroup)) {
                    foundAttributeGroups.add(attributeGroup);
                }
            }

            // Process Groups
            for (Iterator<eu.fox7.bonxai.xsd.Group> it = xmlSchema.getGroups().iterator(); it.hasNext();) {
                eu.fox7.bonxai.xsd.Group group = it.next();
                if (!this.xsdGroupDefineRefMap.containsKey(group)) {
                    foundGroups.add(group);
                }
            }

            // Walk over all external schemas recursivly
            if (xmlSchema.getForeignSchemas() != null && !xmlSchema.getForeignSchemas().isEmpty()) {
                for (Iterator<ForeignSchema> it = xmlSchema.getForeignSchemas().iterator(); it.hasNext();) {
                    ForeignSchema foreignSchema = it.next();
                    if (foreignSchema.getSchema() != null) {
                        findNotUsedComponents(foreignSchema.getSchema(), alreadySeenSchemas, foundTypes, foundAttributes, foundAttributeGroups, foundGroups);
                    }
                }
            }
        }
    }

    /**
     * Generate defines for all found and not-used schema components.
     * @param typeInheritanceInformation        Information about the inheritance structure of types
     * @param elementInheritanceInformation     Information about the inheritance structure of elements
     */
    private void generateDefinesForNotUsedComponents(LinkedHashMap<Type, LinkedHashSet<Type>> typeInheritanceInformation, LinkedHashMap<eu.fox7.bonxai.xsd.Element, LinkedHashSet<eu.fox7.bonxai.xsd.Element>> elementInheritanceInformation) {

        // Provide sets for each type of a possible unused schema component
        LinkedHashSet<Type> foundTypes = new LinkedHashSet<Type>();
        LinkedHashSet<eu.fox7.bonxai.xsd.Attribute> foundAttributes = new LinkedHashSet<eu.fox7.bonxai.xsd.Attribute>();
        LinkedHashSet<eu.fox7.bonxai.xsd.AttributeGroup> foundAttributeGroups = new LinkedHashSet<AttributeGroup>();
        LinkedHashSet<eu.fox7.bonxai.xsd.Group> foundGroups = new LinkedHashSet<eu.fox7.bonxai.xsd.Group>();

        // Fill the sets of unused components with the found objects
        findNotUsedComponents(xmlSchema, new LinkedHashSet<XSDSchema>(), foundTypes, foundAttributes, foundAttributeGroups, foundGroups);

        // Initialize an ElementConverter to provide the possibility of converting an attribute
        ElementConverter elementConverter = new ElementConverter(xmlSchema, relaxng, xsdAttributeDefineRefMap, xsdAttributeGroupDefineRefMap, xsdElementDefineRefMap, xsdGroupDefineRefMap, xsdTypeDefineRefMap, elementInheritanceInformation, typeInheritanceInformation);

        // Handle each type of found unused component and generate a define within the resulting RELAX NG schema object.

        if (!foundAttributes.isEmpty()) {
            StatusLogger.logInfo("XSD2RNG", "Found " + foundAttributes.size() + " attributes, which are defined but not used in the XML XSDSchema document");
            // Handle found attributes
            for (Iterator<eu.fox7.bonxai.xsd.Attribute> it = foundAttributes.iterator(); it.hasNext();) {
                eu.fox7.bonxai.xsd.Attribute attribute = it.next();
                Pattern attributePattern = elementConverter.getComplexTypeConverter().getAttributeParticleConverter().convert(attribute);
                if (attributePattern != null) {
                    this.setPatternToDefine(XSD2RelaxNGConverter.PREFIX_ATTRIBUTE_DEFINE + attribute.getLocalName(), attributePattern);
                }
            }
        }

        if (!foundAttributeGroups.isEmpty()) {
            StatusLogger.logInfo("XSD2RNG", "Found " + foundAttributeGroups.size() + " attribute groups, which are defined but not used in the XML XSDSchema document");
            // Handle found attributeGroups
            for (Iterator<eu.fox7.bonxai.xsd.AttributeGroup> it = foundAttributeGroups.iterator(); it.hasNext();) {
                eu.fox7.bonxai.xsd.AttributeGroup attributeGroup = it.next();
                elementConverter.getComplexTypeConverter().getAttributeParticleConverter().convert(attributeGroup);
                // AttributeGroups will be set to the global define list automatically
            }
        }

        if (!foundGroups.isEmpty()) {
            StatusLogger.logInfo("XSD2RNG", "Found " + foundGroups.size() + " groups, which are defined but not used in the XML XSDSchema document");
            // Handle found groups
            for (Iterator<eu.fox7.bonxai.xsd.Group> it = foundGroups.iterator(); it.hasNext();) {
                eu.fox7.bonxai.xsd.Group group = it.next();
                elementConverter.getComplexTypeConverter().convertGroupToPattern(group);
                // Groups will be set to the global define list automatically
            }
        }

        if (!foundTypes.isEmpty()) {
            StatusLogger.logInfo("XSD2RNG", "Found " + foundTypes.size() + " types, which are defined but not used in the XML XSDSchema document");
            // Handle found types
            for (Iterator<eu.fox7.bonxai.xsd.Type> it = foundTypes.iterator(); it.hasNext();) {
                eu.fox7.bonxai.xsd.Type type = it.next();
                if (type instanceof SimpleType) {
                    elementConverter.getSimpleTypeConverter().convert((SimpleType) type, true);
                } else if (type instanceof ComplexType) {
                    elementConverter.getComplexTypeConverter().convert((ComplexType) type);
                }
                // Types will be set to the global define list automatically
            }
        }
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
         * Activate debug mode, in which debug information are written to the
         * console
         */
        debug = preferencesManager.getBooleanSetting("debug.XSD2RNG", debug);
        /**
         * Countingpattern upper bound: multiplicity of nested countingPatterns
         * result: deeper countingPatterns of the form "(element)[6, 10]" are
         * converted to "(...)*" in the case of a value between 1 and 9.
         */
        COUNTINGPATTERN_UPPER_BOUND_MULTIPLICITY = preferencesManager.getIntegerSetting("XSD2RNG.countingPattern_upper_bound_multiplicity", COUNTINGPATTERN_UPPER_BOUND_MULTIPLICITY);
        /**
         * Prefix for a define that represents a XML XSDSchema type
         */
        PREFIX_TYPE_DEFINE = preferencesManager.getSetting("XSD2RNG.prefix_for_type_define", PREFIX_TYPE_DEFINE);
        /**
         * Prefix for a define that represents a XML XSDSchema element
         */
        PREFIX_ELEMENT_DEFINE = preferencesManager.getSetting("XSD2RNG.prefix_for_element_define", PREFIX_ELEMENT_DEFINE);
        /**
         * Prefix for a define that represents a XML XSDSchema attribute
         */
        PREFIX_ATTRIBUTE_DEFINE = preferencesManager.getSetting("XSD2RNG.prefix_for_attribute_define", PREFIX_ATTRIBUTE_DEFINE);
        /**
         *  Prefix for a define that represents a XML XSDSchema attributeGroup
         */
        PREFIX_ATTRIBUTEGROUP_DEFINE = preferencesManager.getSetting("XSD2RNG.prefix_for_attributeGroup_define", PREFIX_ATTRIBUTEGROUP_DEFINE);
        /**
         * Prefix for a define that represents a XML XSDSchema group
         */
        PREFIX_GROUP_DEFINE = preferencesManager.getSetting("XSD2RNG.prefix_for_group_define", PREFIX_GROUP_DEFINE);
        /**
         * Setting to force the writing of not used schema components
         */
        WRITE_NOT_USED_COMPONENTS = preferencesManager.getBooleanSetting("XSD2RNG.write_not_used_components", WRITE_NOT_USED_COMPONENTS);
        /**
         * Setting to prohibit the calculation and generation of possible
         * substitutions for elements and types.
         */
        HANDLE_SUBSTITUTIONS = preferencesManager.getBooleanSetting("XSD2RNG.provide_type_and_element_substitutions", HANDLE_SUBSTITUTIONS);
    }
}
