package eu.fox7.schematoolkit.converter.relaxng2xsd;

import eu.fox7.schematoolkit.common.DefaultNamespace;
import eu.fox7.schematoolkit.common.IdentifiedNamespace;
import eu.fox7.schematoolkit.exceptions.ConversionFailedException;
import eu.fox7.schematoolkit.relaxng.om.*;
import eu.fox7.schematoolkit.relaxng.tools.*;
import eu.fox7.schematoolkit.xsd.om.ForeignSchema;
import eu.fox7.schematoolkit.xsd.om.ImportedSchema;
import eu.fox7.schematoolkit.xsd.om.Type;
import eu.fox7.schematoolkit.xsd.om.XSDSchema;
import eu.fox7.schematoolkit.xsd.om.XSDSchema.Qualification;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Class RelaxNG2XSDConverter
 *
 * This class allows the conversion from the
 * RELAX NG object model (eu.fox7.schematoolkit.relaxng) to the
 * XSD object model (eu.fox7.schematoolkit.xsd.om).
 *
 * This is the main class, which initializes the conversion itself.
 *
 * The relaxng object holding the entire DTD structure is given to the constructor
 * and the convert-method starts the conversion process and returns the fully
 * generated XSD object holding the XSD structure.
 *
 * @author Lars Schmidt
 */
public class RelaxNG2XSDConverter extends ConverterBase {

    /**
     * The XML namespace.
     */
    public static final String XML_NAMESPACE = "http://www.w3.org/XML/1998/namespace";
    /**
     * The RELAX NG namespace.
     */
    public static final String RELAXNG_NAMESPACE = "http://relaxng.org/ns/structure/1.0";
    /**
     * Set all generated elements as toplevel elements
     * (pizza-slice structure using element/elementRefs like in Trang)
     *
     * This setting has no effect, if the RECURSION_MODE_COMPLEXTYPE setting
     * is set to true!
     */
    public static boolean RECURSION_MODE_ELEMENT_SET_ALL_ELEMENTS_TOPLEVEL = false;
    /**
     * Handling of the RELAX NG "interleave" pattern or combine method:
     *
     * By setting this option to true, the approximation of an interleave
     * pattern is turned off.
     * This results at first in an invalid XML XSDSchema, if there are XML XSDSchema
     * particles under an all-tag, which are not allowed in that place.
     *
     * A postprocessing should repair this invalidity by generating the
     * correct regular expression for the given interleaving by forming a new
     * particle structure in that place of the XML XSDSchema.
     */
    public static boolean INTERLEAVE_APPROXIMATION_OFF = true;
    /**
     * Generate XML XSDSchema groups on the base of the ref pattern structure:
     *
     * By setting this option to true, the generation of XML XSDSchema groups is
     * turned on.
     */
    public static boolean REF_TO_GROUP_CONVERSION = true;
    /**
     * Generate XML XSDSchema attributeGroups on the base of the ref pattern 
     * structure:
     *
     * By setting this option to true, the generation of XML XSDSchema groups is 
     * turned on.
     */
    public static boolean REF_TO_ATTRIBUTEGROUP_CONVERSION = true;
    /**
     * Switch for turning the EDC fix off.
     */
    private static boolean EDC_FIX_OFF = false;
    /**
     * Switch for turning the UPA fix off.
     */
    private static boolean UPA_FIX_OFF = false;
    /**
     * Filename for the resulting schema document
     */
    private String resultLocationFilename;
    /**
     * Absolute path of the resulting schema document
     */
    private String resultAbsolutePath;

    /**
     * Constructor of class RelaxNG2XSDConverter
     * @param relaxng       Source of the conversion
     */
    public RelaxNG2XSDConverter(RelaxNGSchema relaxng) {
        // Generate a new XML XSDSchema object as the target for the conversion progress
        super(new XSDSchema(), relaxng, new HashMap<Pattern, HashSet<String>>(), new HashSet<String>(), new HashSet<String>());
//        StatusLogger.logInfo("RNG2XSD", "Load preferences from file");
//        this.loadPreferences();
        this.resultLocationFilename = "result.xsd";
        this.resultAbsolutePath = getPathFromLocation(this.resultLocationFilename);
        this.xmlSchema.setSchemaLocation(this.resultLocationFilename);

    }

    /**
     * Constructor of class RelaxNG2XSDConverter
     * @param relaxng       Source of the conversion
     */
    public RelaxNG2XSDConverter(RelaxNGSchema relaxng, String resultLocationFilename) {
        // Generate a new XML XSDSchema object as the target for the conversion progress
        super(new XSDSchema(), relaxng, new HashMap<Pattern, HashSet<String>>(), new HashSet<String>(), new HashSet<String>());

//        StatusLogger.logInfo("RNG2XSD", "Load preferences from file");
//        this.loadPreferences();

        this.resultLocationFilename = resultLocationFilename;
        if (this.resultLocationFilename != null) {
            this.resultAbsolutePath = getPathFromLocation(this.resultLocationFilename);
            this.xmlSchema.setSchemaLocation(this.resultLocationFilename);
        }
    }

    /**
     * Method "convert"
     * @return XSDSchema       Target for the conversion
     * @throws Exception    Different exceptions may be thrown in submethods
     */
    public XSDSchema convert() throws ConversionFailedException {
    	try {
//    		StatusLogger.logInfo("RNG2XSD", "Start conversion progress");

    		/***********************************************************************
    		 * RELAX NG: Preprocessing on the given RELAX NG XSDSchema
    		 **********************************************************************/
    		// 1. External schema loader:
//    		StatusLogger.logInfo("RNG2XSD", "Load external RELAX NG schemas and resolve all \"externalRef\"s and grammar \"include\"s");
    		ExternalSchemaLoader relaxNGExternalSchemaLoader = new ExternalSchemaLoader(this.relaxng, true, false);
    		relaxNGExternalSchemaLoader.handleExternalSchemas();

    		// 2. XML attribute replenisher:
//    		StatusLogger.logInfo("RNG2XSD", "Replenish XML attributes into all patterns");
    		XMLAttributeReplenisher xmlAttributeReplenisher = new XMLAttributeReplenisher(this.relaxng);
    		xmlAttributeReplenisher.startReplenishment();
    		this.usedLocalNames = xmlAttributeReplenisher.getUsedNames();
//    		StatusLogger.logInfo("RNG2XSD", this.usedLocalNames.size() + " different names found");

    		// 3. Pattern information collector:
//    		StatusLogger.logInfo("RNG2XSD", "Collect information about the content and special attributes of all patterns (element, attribute, data, value, text, empty, notAllowed, mixed, optional)");
    		PatternInformationCollector relaxNGPatternDataCollector = new PatternInformationCollector(this.relaxng);
    		relaxNGPatternDataCollector.collectData();
    		this.patternInformation = relaxNGPatternDataCollector.getPatternIntel();

    		/***********************************************************************
    		 * XML XSDSchema:
    		 **********************************************************************/
    		// Generate XML XSDSchema declaration block with namespaces, ...
//    		StatusLogger.logInfo("RNG2XSD", "Generate XML XSDSchema declaration block with namespaces");
    		this.createIdentifiedNamespaces(xmlAttributeReplenisher.getIdentifiedNamespaces());

    		// Set the default/target namespace
//    		StatusLogger.logInfo("RNG2XSD", "Set the default/target namespace");
    		if (this.relaxng.getRootPattern().getAttributeNamespace() != null) {
    			this.xmlSchema.setTargetNamespace(new DefaultNamespace(this.relaxng.getRootPattern().getAttributeNamespace().getUri()));
    		}

    		// Set the default qualification for elements
//    		StatusLogger.logInfo("RNG2XSD", "Set the default qualification for elements");
    		this.xmlSchema.setElementFormDefault(XSDSchema.Qualification.qualified);

    		// Convert the RELAX NG XSDSchema to XML XSDSchema
    		// Elements from other namespaces have to be set to imported foreign schemas
//    		StatusLogger.logInfo("RNG2XSD", "Start the conversion with start elements of the RELAX NG schema document");
    		PatternElementConverter patternElementConverter = new PatternElementConverter(xmlSchema, relaxng, this.patternInformation, this.usedLocalNames, this.usedLocalTypeNames);
    		patternElementConverter.convert();


    		// Write all identified namespaces to the foreign schemas
//    		StatusLogger.logInfo("RNG2XSD", "Write all identified namespaces to external schemas");
    		if (!this.xmlSchema.getForeignSchemas().isEmpty()) {
    			for (Iterator<ForeignSchema> it1 = this.xmlSchema.getForeignSchemas().iterator(); it1.hasNext();) {
    				ForeignSchema foreignSchema = (ImportedSchema) it1.next();
    				if (!this.xmlSchema.getNamespaceList().getNamespaces().isEmpty()) {
    					for (IdentifiedNamespace identifiedNamespace: this.xmlSchema.getNamespaceList().getNamespaces()) {
    						foreignSchema.getSchema().getNamespaceList().addNamespace(identifiedNamespace);
    					}
    				}
    			}
    		}

    		// Case "Type":
    		for (Type type: this.xmlSchema.getTypes()) {
    			if (!type.getName().getNamespace().equals(this.xmlSchema.getDefaultNamespace())) {
    				ImportedSchema importedSchema = findImportedSchema(type.getName().getNamespace().getUri());
    				if (importedSchema != null) {
    					importedSchema.getSchema().addType(type);
    				}
    			}
    		}

    		if (RelaxNG2XSDConverter.INTERLEAVE_APPROXIMATION_OFF) {
//    			StatusLogger.logInfo("RNG2XSD", "Repair the schema regarding invalid all-elements");
    			// Only in case of INTERLEAVE_APPROXIMATION_OFF = true
//    			InterleaveHandler interleaveHandler = new InterleaveHandler(xmlSchema);
//    			interleaveHandler.repair(xmlSchema);
    		}

//    		if (!RelaxNG2XSDConverter.EDC_FIX_OFF) {
//    			StatusLogger.logInfo("RNG2XSD", "Fix the schema regarding an invalid EDC constraint");
//    			EDCFixer edcFixer = new EDCFixer();
//    			xmlSchema = edcFixer.fixEDC(xmlSchema, this.getPathFromLocation(resultLocationFilename) + "/");
//    		}

    		// return generated XML XSDSchema
//    		StatusLogger.logLastInfoMessage("RNG2XSD", "Conversion completed: XML XSDSchema document structure created");

    		this.xmlSchema.setElementFormDefault(Qualification.qualified);

    		return this.xmlSchema;
    	} catch (Exception e) {
    		throw new ConversionFailedException(e);
    	}
    }

    /**
     * Create identified namespaces for the values of a given HashMap
     * @param identifiedNamespaces
     */
    private void createIdentifiedNamespaces(HashMap<String, String> identifiedNamespaces) {
        this.xmlSchema.addIdentifiedNamespace(new IdentifiedNamespace("xs", XSDSchema.XMLSCHEMA_NAMESPACE));

        for (Iterator<String> it = identifiedNamespaces.keySet().iterator(); it.hasNext();) {
            String abbreviation = it.next();
            String uri = identifiedNamespaces.get(abbreviation);

            if (this.xmlSchema.getNamespaceByIdentifier(abbreviation).getUri() != null && !this.xmlSchema.getNamespaceList().getNamespaceByIdentifier(abbreviation).getUri().equals(uri)) {
                this.xmlSchema.addIdentifiedNamespace(new IdentifiedNamespace(abbreviation + "_" + this.xmlSchema.getNamespaceList().getNamespaces().size(), uri));
            } else {
                this.xmlSchema.addIdentifiedNamespace(new IdentifiedNamespace(abbreviation, uri));
            }
        }
    }

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

//    /**
//     * Load application settings from config file
//     */
//    private void loadPreferences() {
//        /***************************************************************************
//         * Settings
//         **************************************************************************/
//        // Initialize PreferencesManager
//        PreferencesManager preferencesManager = PreferencesManager.getPreferencesManager();
//        /***************************************************************************
//         * Settings                                                                *
//         ***************************************************************************
//         * Set the option for handling recursive elements
//         * (This is a boolean setting.)
//         * If the value is set to ...
//         * -------------------------------------------------------------------------
//         * true
//         *   --> the recursive structure of elements is build using complexTypes.
//         *       (RECURSION MODE: COMPLEXTYPE)
//         * - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
//         * false
//         *   --> the recursive structure of elements is build using
//         *       toplevel elements and their corresponding elementRefs
//         *       (RECURSION MODE: ELEMENT)
//         * - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
//         */
//        RECURSION_MODE_COMPLEXTYPE = preferencesManager.getBooleanSetting("RNG2XSD.recursion_mode_complexType", RECURSION_MODE_COMPLEXTYPE);
//        /**
//         * -------------------------------------------------------------------------
//         * Set all generated elements as toplevel elements
//         * (pizza-slice structure using element/elementRefs like in Trang)
//         *
//         * This setting has no effect, if the RECURSION_MODE_COMPLEXTYPE setting
//         * is set to true!
//         * - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
//         */
//        RECURSION_MODE_ELEMENT_SET_ALL_ELEMENTS_TOPLEVEL = preferencesManager.getBooleanSetting("RNG2XSD.recursion_mode_element_set_all_elements_toplevel", RECURSION_MODE_ELEMENT_SET_ALL_ELEMENTS_TOPLEVEL);
//        /**
//         * -------------------------------------------------------------------------
//         * Handling of the RELAX NG "interleave" pattern or combine method:
//         * - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
//         *
//         * By setting this option to true, the approximation of an interleave
//         * pattern is turned off.
//         * This results at first in an invalid XML XSDSchema, if there are XML XSDSchema
//         * particles under an all-tag, which are not allowed in that place.
//         *
//         * A postprocessing should repair this invalidity by generating the
//         * correct regular expression for the given interleaving by forming a new
//         * particle structure in that place of the XML XSDSchema.
//         * - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
//         */
//        INTERLEAVE_APPROXIMATION_OFF = preferencesManager.getBooleanSetting("RNG2XSD.interleave_approximation_off", INTERLEAVE_APPROXIMATION_OFF);
//        /**
//         * -------------------------------------------------------------------------
//         * Generate XML XSDSchema groups on the base of the ref pattern structure:
//         * - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
//         *
//         * By setting this option to true, the generation of XML XSDSchema groups is
//         * turned on.
//         * - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
//         */
//        REF_TO_GROUP_CONVERSION = preferencesManager.getBooleanSetting("RNG2XSD.ref_to_group_conversion", REF_TO_GROUP_CONVERSION);
//        /**
//         * -------------------------------------------------------------------------
//         * Generate XML XSDSchema attributeGroups on the base of the ref pattern
//         * structure:
//         * - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
//         *
//         * By setting this option to true, the generation of XML XSDSchema groups is
//         * turned on.
//         * - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
//         */
//        REF_TO_ATTRIBUTEGROUP_CONVERSION = preferencesManager.getBooleanSetting("RNG2XSD.ref_to_attributeGroup_conversion", REF_TO_ATTRIBUTEGROUP_CONVERSION);
//
//
//        /**
//         * Switch for turning the EDC fix off.
//         */
//        EDC_FIX_OFF = preferencesManager.getBooleanSetting("RNG2XSD.edc_fix_off", EDC_FIX_OFF);
//
//        /**
//         * Switch for turning the UPA fix off.
//         */
//        UPA_FIX_OFF = preferencesManager.getBooleanSetting("RNG2XSD.upa_fix_off", UPA_FIX_OFF);
//    }
}
