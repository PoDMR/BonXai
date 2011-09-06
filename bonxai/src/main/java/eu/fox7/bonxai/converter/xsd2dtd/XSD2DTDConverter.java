package eu.fox7.bonxai.converter.xsd2dtd;

import eu.fox7.bonxai.common.IdentifiedNamespace;
import eu.fox7.bonxai.common.NamespaceList;
import eu.fox7.bonxai.common.SymbolTableRef;
import eu.fox7.bonxai.converter.ConversionFailedException;
import eu.fox7.bonxai.dtd.Attribute;
import eu.fox7.bonxai.dtd.DocumentTypeDefinition;
import eu.fox7.bonxai.dtd.Element;
import eu.fox7.bonxai.dtd.ElementRef;
import eu.fox7.bonxai.dtd.InternalEntity;
import eu.fox7.bonxai.tools.PreferencesManager;
import eu.fox7.bonxai.tools.StatusLogger;
import eu.fox7.bonxai.xsd.XSDSchema;
import eu.fox7.bonxai.xsd.parser.ForeignSchemaLoader;
import eu.fox7.bonxai.xsd.tools.ComplexTypeInheritanceResolver;
import eu.fox7.bonxai.xsd.tools.ElementInheritanceResolver;
import eu.fox7.bonxai.xsd.tools.GroupReplacer;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

/**
 * XSD2DTDConverter
 *
 * This class allows the conversion from the
 * XSD object model (eu.fox7.bonxai.xsd) to the
 * DTD object model (eu.fox7.bonxai.dtd).
 *
 * This is the main class, which initializes the conversion itself.
 *
 * The XML XSDSchema object holding the entire XSD structure is given to the
 * constructor and the convertElement-method starts the conversion process and returns
 * the fully generated DTD object holding the DTD structure.
 *
 * @author Lars Schmidt
 */
public class XSD2DTDConverter {

    /**
     * The object for holding the XML XSDSchema schema (source of the conversion).
     */
    private XSDSchema xmlSchema;
    /**
     * The object for holding the Document Type Definition schema (source of the conversion).
     */
    private DocumentTypeDefinition dtd;
    /**
     * The XML XSDSchema namespace.
     */
    public static final String XMLSCHEMA_NAMESPACE = "http://www.w3.org/2001/XMLSchema";
    /***************************************************************************
     * Settings
     **************************************************************************/
    /**
     * Activate debug mode, in which debug information are written to the
     * console
     */
    public static boolean debug = false;
    /**
     * all: Upper bound for writing out all permutations of the content model
     * (number of elements)
     * result: example: ( (1, 2, 3) | (1, 3, 2) | (2, 1, 3) | (2, 3, 1) | (3, 1, 2) | (3, 2, 1) )
     */
    public static int ALL_UPPER_BOUND_PERMUTATION = 3;
    /**
     * all: Upper bound for writing out a sequence of choices of contained
     * elements (number of elements)
     * result: one choice: ( elementA | elementB | elementC )*
     */
    public static int ALL_UPPER_BOUND_CHOICE = 3;
    /**
     * all: Upper bound for writing out ANY (number of elements)
     * result: ANY
     */
    public static int ALL_UPPER_BOUND_ANY = 3;
    /**
     * Use namespace abbreviations as DTD name prefix for non-topLevel elements
     */
    public static boolean NAMESPACE_PREFIX_FEATURE = true;
    /**
     * Consider the setting of qualification for each non-topLevel element for
     * prefixing it with the namespace abbreviation
     */
    public static boolean NAMESPACE_PREFIX_CONSIDER_QUALIFICATION_ATTRIBUTE = true;
    /**
     * Force namespace abbreviations as DTD name prefix for all elements, when
     * the namespace is given
     */
    public static boolean NAMESPACE_PREFIX_FORCE_USAGE = true;
    /**
     * Countingpattern upper bound: multiplicity of nested countingPatterns
     * result: deeper countingPatterns of the form "(element)[6, 10]" are 
     * converted to "(...)*"
     */
    public static int COUNTINGPATTERN_UPPER_BOUND_MULTIPLICITY = 20;

    /**
     * Constructor of class XSD2DTDConverter
     * @param xmlSchema     The XML XSDSchema as source of the conversion progress.
     * @param debug 
     */
    public XSD2DTDConverter(XSDSchema xmlSchema, boolean debug) {
        this.xmlSchema = xmlSchema;
        StatusLogger.logInfo("XSD2DTD", "Load preferences from file");
        this.loadPreferences();
        XSD2DTDConverter.setDebug(debug);
    }

    /**
     * Activates or deactivates debug mode
     * @param debug     If true console output is enabled else it is disabled
     */
    public static void setDebug(boolean debug) {
        XSD2DTDConverter.debug = debug;
    }

    /**
     * Return true when debug mode is active else false.
     * @return current debug flag
     */
    public static boolean getDebug() {
        return XSD2DTDConverter.debug;
    }

    /**
     * Method "convertElement"
     *
     * The empty namespace will be used as target namespace
     *
     * Start the conversion process and return the fully generated DTD
     * @return XSDSchema   XSD object holding the XSD structure
     * @throws Exception
     */
    public DocumentTypeDefinition convert() throws ConversionFailedException {
    	try {
    		this.dtd = new DocumentTypeDefinition();

    		StatusLogger.logInfo("XSD2DTD", "Start conversion progress");

    		/**
    		 * 0.
    		 * Handle all external schemas and link the references to the correct 
    		 * declarations
    		 */
    		if (this.xmlSchema.getSchemaLocation()!=null) {
    			StatusLogger.logInfo("XSD2DTD", "Start loading of external schemas");
    			ForeignSchemaLoader foreignSchemaLoader = new ForeignSchemaLoader(this.xmlSchema, false);
    			foreignSchemaLoader.findForeignSchemas();
    		}

    		/**
    		 * 1. a
    		 * Resolve the element substitutionGroups
    		 */
    		StatusLogger.logInfo("XSD2DTD", "Resolve all element substitutions");
    		ElementInheritanceResolver esgr = new ElementInheritanceResolver(this.xmlSchema);
    		esgr.resolveSubstitutionGroups();

    		/**
    		 * 1. b
    		 * Resolve the inheritance structure of all types in the local
    		 * xmlSchema and in attached foreign schemas.
    		 */
    		StatusLogger.logInfo("XSD2DTD", "Resolve all type inheritance");
    		ComplexTypeInheritanceResolver ctir = new ComplexTypeInheritanceResolver(this.xmlSchema);
    		ctir.resolveComplexTypeInheritance();

    		/**
    		 * 2.
    		 * Replace all "groupRef" and "attributeGroupRef"s with the content of
    		 * the corresponding "group" and attributeGroup.
    		 */
    		StatusLogger.logInfo("XSD2DTD", "Remove all group definitions and replace references with the content");
    		GroupReplacer groupReplacer = new GroupReplacer(this.xmlSchema);
    		groupReplacer.replace();

    		/**
    		 * 3. a
    		 * Convert all XSD elements to their DTD counterparts
    		 */
    		StatusLogger.logInfo("XSD2DTD", "Start conversion with toplevel elements");
    		ElementConverter elementConverter = new ElementConverter(this.xmlSchema, this.dtd);
    		LinkedHashMap<String, ElementWrapper> elementMap = elementConverter.convert();

    		/**
    		 * 4. a
    		 * Build content model union over all elementMap entries
    		 * and the union of attributes with the same name under an element
    		 */
    		StatusLogger.logInfo("XSD2DTD", "Build the union of content models for elements with the same name");
    		StatusLogger.logInfo("XSD2DTD", "Build the union of attributes with the same name under each element");

    		for (Iterator<String> it = elementMap.keySet().iterator(); it.hasNext();) {
    			String mapKey = it.next();
    			ElementWrapper elementWrapper = elementMap.get(mapKey);

    			// Build the union of elements with the same name
    			ElementUnionBuilder elementContentModelUnionBuilder = new ElementUnionBuilder(elementWrapper);
    			elementContentModelUnionBuilder.unifyElements();

    			// Build the union of attributes with the same name
    			AttributeUnionBuilder attributeUnionBuilder = new AttributeUnionBuilder(elementWrapper);
    			attributeUnionBuilder.unifyAttributes();

    			// Handle XSD Key and KeyRef Constraints to build the correct ID and IDREF attributes in DTD
    			ConstraintHandler constraintHandler = new ConstraintHandler(elementWrapper);
    			constraintHandler.manageConstraints(elementConverter.getGlobalConstraints());
    			constraintHandler.manageElementConstraints();
    		}

    		StatusLogger.logInfo("XSD2DTD", "Convert allowed constraints to their ID/IDREF/IDREF type attribute counterparts");

    		/**
    		 * 4. c
    		 * Write the results to the target DTD object structure.
    		 */
    		StatusLogger.logInfo("XSD2DTD", "Write the results to the target DTD object structure.");

    		boolean first = true;
    		for (Iterator<String> it = elementMap.keySet().iterator(); it.hasNext();) {
    			String mapKey = it.next();
    			ElementWrapper elementWrapper = elementMap.get(mapKey);

    			if (elementWrapper != null) {
    				if (elementWrapper.getDTDElements().size() == 1) {
    					Element currentElement = elementWrapper.getDTDElements().iterator().next();

    					if (first) {
    						this.dtd.setRootElementRef(new ElementRef(this.dtd.getElementSymbolTable().getReference(mapKey)));
    						first = false;
    					}

    					for (Iterator<String> it1 = elementWrapper.getDTDAttributeMap().keySet().iterator(); it1.hasNext();) {
    						String attributeMapKey = it1.next();
    						LinkedHashSet<Attribute> attributeHashSet = elementWrapper.getDTDAttributeMap().get(attributeMapKey);
    						if (attributeHashSet.size() == 1) {
    							currentElement.addAttribute(attributeHashSet.iterator().next());
    						}
    					}

    					SymbolTableRef<Element> finalElementSymbolTableRef = this.dtd.getElementSymbolTable().updateOrCreateReference(elementWrapper.getDTDElementName(), elementWrapper.getDTDElements().iterator().next());
    					for (Iterator<ElementRef> it1 = elementWrapper.getDtdElementRefs().iterator(); it1.hasNext();) {
    						ElementRef currentElementRef = it1.next();
    						currentElementRef.setReference(finalElementSymbolTableRef);
    					}
    				}
    			}
    		}

    		StatusLogger.logInfo("XSD2DTD", "Write all namespaces as entities to the resulting DTD");
    		// Write all namespaces as entities to the resulting DTD
    		if (this.xmlSchema.getNamespaceList() != null) {
    			NamespaceList namespaceList = this.xmlSchema.getNamespaceList();
    			if (namespaceList.getDefaultNamespace() != null && namespaceList.getDefaultNamespace().getUri() != null && !namespaceList.getDefaultNamespace().getUri().equals("")) {
    				this.dtd.addInternalEntity(new InternalEntity("xmlns", namespaceList.getDefaultNamespace().getUri()));
    			}

    			if (!namespaceList.getIdentifiedNamespaces().isEmpty()) {
    				for (Iterator<IdentifiedNamespace> it = namespaceList.getIdentifiedNamespaces().iterator(); it.hasNext();) {
    					IdentifiedNamespace identifiedNamespace = it.next();
    					if (identifiedNamespace.getIdentifier() != null && !identifiedNamespace.getIdentifier().equals("")) {
    						this.dtd.addInternalEntity(new InternalEntity(identifiedNamespace.getIdentifier(), identifiedNamespace.getUri()));
    					}
    				}
    			}
    		}

    		StatusLogger.logInfo("XSD2DTD", this.dtd.getElementSymbolTable().getAllReferencedObjects().size() + " elements converted");

    		StatusLogger.logLastInfoMessage("XSD2DTD", "Conversion completed: Document Type Definition schema created");
    		// return the generated DTD
    		return this.dtd;
    	} catch (Exception e) {
    		throw new ConversionFailedException(e);
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
        debug = preferencesManager.getBooleanSetting("debug.XSD2DTD", debug);
        /**
         * all: Upper bound for writing out all permutations of the content model
         * (number of elements)
         * result: example: ( (1, 2, 3) | (1, 3, 2) | (2, 1, 3) | (2, 3, 1) | (3, 1, 2) | (3, 2, 1) )
         */
        ALL_UPPER_BOUND_PERMUTATION = preferencesManager.getIntegerSetting("XSD2DTD.all_upper_bound_permutation", ALL_UPPER_BOUND_PERMUTATION);
        /**
         * all: Upper bound for writing out a sequence of choices of contained
         * elements (number of elements)
         * result: one choice: ( elementA | elementB | elementC )*
         */
        ALL_UPPER_BOUND_CHOICE = preferencesManager.getIntegerSetting("XSD2DTD.all_upper_bound_choice", ALL_UPPER_BOUND_CHOICE);
        /**
         * all: Upper bound for writing out ANY (number of elements)
         * result: ANY
         */
        ALL_UPPER_BOUND_ANY = preferencesManager.getIntegerSetting("XSD2DTD.all_upper_bound_any", ALL_UPPER_BOUND_ANY);
        /**
         * Use namespace abbreviations as DTD name prefix for non-topLevel elements
         */
        NAMESPACE_PREFIX_FEATURE = preferencesManager.getBooleanSetting("XSD2DTD.namespace_prefix_feature", NAMESPACE_PREFIX_FEATURE);
        /**
         * Consider the setting of qualification for each non-topLevel element for
         * prefixing it with the namespace abbreviation
         */
        NAMESPACE_PREFIX_CONSIDER_QUALIFICATION_ATTRIBUTE = preferencesManager.getBooleanSetting("XSD2DTD.namespace_prefix_consider_qualification_attribute", NAMESPACE_PREFIX_CONSIDER_QUALIFICATION_ATTRIBUTE);
        /**
         * Force namespace abbreviations as DTD name prefix for all elements, when
         * the namespace is given
         */
        NAMESPACE_PREFIX_FORCE_USAGE = preferencesManager.getBooleanSetting("XSD2DTD.namespace_prefix_force_usage", NAMESPACE_PREFIX_FORCE_USAGE);
        /**
         * Countingpattern upper bound: multiplicity of nested countingPatterns
         * result: deeper countingPatterns of the form "(element)[6, 10]" are
         * converted to "(...)*"
         */
        COUNTINGPATTERN_UPPER_BOUND_MULTIPLICITY = preferencesManager.getIntegerSetting("XSD2DTD.countingPattern_upper_bound_multiplicity", COUNTINGPATTERN_UPPER_BOUND_MULTIPLICITY);
    }
}
