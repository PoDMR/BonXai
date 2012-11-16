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

package eu.fox7.bonxai.converter.xsd2dtd;

import eu.fox7.flt.automata.impl.sparse.State;
import eu.fox7.schematoolkit.dtd.om.Attribute;
import eu.fox7.schematoolkit.dtd.om.DocumentTypeDefinition;
import eu.fox7.schematoolkit.dtd.om.Element;
import eu.fox7.schematoolkit.dtd.om.InternalEntity;
import eu.fox7.schematoolkit.exceptions.ConversionFailedException;
import eu.fox7.schematoolkit.common.IdentifiedNamespace;
import eu.fox7.schematoolkit.common.NamespaceList;
import eu.fox7.schematoolkit.typeautomaton.TypeAutomaton;
import eu.fox7.schematoolkit.typeautomaton.factories.XSDTypeAutomatonFactory;
import eu.fox7.schematoolkit.xsd.om.Type;
import eu.fox7.schematoolkit.xsd.om.XSDSchema;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

/**
 * XSD2DTDConverter
 *
 * This class allows the conversion from the
 * XSD object model (eu.fox7.schematoolkit.xsd.om) to the
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

    public static final int COUNTINGPATTERN_UPPER_BOUND = 10;
	/**
     * The object for holding the XML XSDSchema schema (source of the conversion).
     */
    private XSDSchema xmlSchema;
    /**
     * The object for holding the Document Type Definition schema (source of the conversion).
     */
    private DocumentTypeDefinition dtd;
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
     * Constructor of class XSD2DTDConverter
     * @param xmlSchema     The XML XSDSchema as source of the conversion progress.
     * @param debug 
     */
    public XSD2DTDConverter(XSDSchema xmlSchema, boolean debug) {
        this.xmlSchema = xmlSchema;
//        StatusLogger.logInfo("XSD2DTD", "Load preferences from file");
//        this.loadPreferences();
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
    	this.dtd = new DocumentTypeDefinition();
    	ElementConverter elementConverter = new ElementConverter(xmlSchema, dtd);
    	XSDTypeAutomatonFactory factory = new XSDTypeAutomatonFactory();
    	TypeAutomaton typeAutomaton = factory.createTypeAutomaton(xmlSchema);
    	
    	TypeAutomatonDTDizer typeAutomatonDeterminizer = new TypeAutomatonDTDizer();
    	typeAutomatonDeterminizer.simplify(typeAutomaton);
    	
    	Collection<State> states = typeAutomaton.getStates();
    	for (State state: states) {
    		Type type = typeAutomaton.getType(state);
    		Element element = elementConverter.convertElement(type.getName(), type);
    		this.dtd.addElement(element);
    	}
    	

    	/**
    	 * 0.
    	 * Handle all external schemas and link the references to the correct 
    	 * declarations
    	 */
    	//    		if (this.xmlSchema.getSchemaLocation()!=null) {
    	//    			StatusLogger.logInfo("XSD2DTD", "Start loading of external schemas");
    	//    			ForeignSchemaLoader foreignSchemaLoader = new ForeignSchemaLoader(this.xmlSchema, false);
    	//    			foreignSchemaLoader.findForeignSchemas();
    	//    		}

    	/**
    	 * 1. a
    	 * Resolve the element substitutionGroups
    	 */
    	//    		StatusLogger.logInfo("XSD2DTD", "Resolve all element substitutions");
    	//    		ElementInheritanceResolver esgr = new ElementInheritanceResolver(this.xmlSchema);
    	//    		esgr.resolveSubstitutionGroups();

    	/**
    	 * 1. b
    	 * Resolve the inheritance structure of all types in the local
    	 * xmlSchema and in attached foreign schemas.
    	 */
    	//    		StatusLogger.logInfo("XSD2DTD", "Resolve all type inheritance");
    	//    		ComplexTypeInheritanceResolver ctir = new ComplexTypeInheritanceResolver(this.xmlSchema);
    	//    		ctir.resolveComplexTypeInheritance();

    	/**
    	 * 2.
    	 * replace all "groupRef" and "attributeGroupRef"s with the content of
    	 * the corresponding "group" and attributeGroup.
    	 */
    	//    		StatusLogger.logInfo("XSD2DTD", "Remove all group definitions and replace references with the content");
    	//    		GroupReplacer groupReplacer = new GroupReplacer(this.xmlSchema);
    	//    		groupReplacer.replace();


    	//    		StatusLogger.logInfo("XSD2DTD", "Write all namespaces as entities to the resulting DTD");
    	// Write all namespaces as entities to the resulting DTD
    	if (xmlSchema.getDefaultNamespace() != null && xmlSchema.getDefaultNamespace().getUri() != null && !xmlSchema.getDefaultNamespace().getUri().equals("")) {
    		this.dtd.addInternalEntity(new InternalEntity("xmlns", xmlSchema.getDefaultNamespace().getUri()));
    	}

    	if (!xmlSchema.getNamespaces().isEmpty()) {
    		for (IdentifiedNamespace  identifiedNamespace: xmlSchema.getNamespaces()) {
    			if (identifiedNamespace.getIdentifier() != null && !identifiedNamespace.getIdentifier().equals("")) {
    				this.dtd.addInternalEntity(new InternalEntity(identifiedNamespace.getIdentifier(), identifiedNamespace.getUri()));
    			}
    		}
    	}

    	//    		StatusLogger.logInfo("XSD2DTD", this.dtd.getElementSymbolTable().getAllReferencedObjects().size() + " elements converted");

    	//    		StatusLogger.logLastInfoMessage("XSD2DTD", "Conversion completed: Document Type Definition schema created");
    	// return the generated DTD
    	return this.dtd;
    }

    /**
     * Load application settings from config file
     */
//    private void loadPreferences() {
//        /***************************************************************************
//         * Settings
//         **************************************************************************/
//        // Initialize PreferencesManager
//        PreferencesManager preferencesManager = PreferencesManager.getPreferencesManager();
//        /**
//         * Activate debug mode, in which debug information are written to the
//         * console
//         */
//        debug = preferencesManager.getBooleanSetting("debug.XSD2DTD", debug);
//        /**
//         * all: Upper bound for writing out all permutations of the content model
//         * (number of elements)
//         * result: example: ( (1, 2, 3) | (1, 3, 2) | (2, 1, 3) | (2, 3, 1) | (3, 1, 2) | (3, 2, 1) )
//         */
//        ALL_UPPER_BOUND_PERMUTATION = preferencesManager.getIntegerSetting("XSD2DTD.all_upper_bound_permutation", ALL_UPPER_BOUND_PERMUTATION);
//        /**
//         * all: Upper bound for writing out a sequence of choices of contained
//         * elements (number of elements)
//         * result: one choice: ( elementA | elementB | elementC )*
//         */
//        ALL_UPPER_BOUND_CHOICE = preferencesManager.getIntegerSetting("XSD2DTD.all_upper_bound_choice", ALL_UPPER_BOUND_CHOICE);
//        /**
//         * all: Upper bound for writing out ANY (number of elements)
//         * result: ANY
//         */
//        ALL_UPPER_BOUND_ANY = preferencesManager.getIntegerSetting("XSD2DTD.all_upper_bound_any", ALL_UPPER_BOUND_ANY);
//        /**
//         * Use namespace abbreviations as DTD name prefix for non-topLevel elements
//         */
//        NAMESPACE_PREFIX_FEATURE = preferencesManager.getBooleanSetting("XSD2DTD.namespace_prefix_feature", NAMESPACE_PREFIX_FEATURE);
//        /**
//         * Consider the setting of qualification for each non-topLevel element for
//         * prefixing it with the namespace abbreviation
//         */
//        NAMESPACE_PREFIX_CONSIDER_QUALIFICATION_ATTRIBUTE = preferencesManager.getBooleanSetting("XSD2DTD.namespace_prefix_consider_qualification_attribute", NAMESPACE_PREFIX_CONSIDER_QUALIFICATION_ATTRIBUTE);
//        /**
//         * Force namespace abbreviations as DTD name prefix for all elements, when
//         * the namespace is given
//         */
//        NAMESPACE_PREFIX_FORCE_USAGE = preferencesManager.getBooleanSetting("XSD2DTD.namespace_prefix_force_usage", NAMESPACE_PREFIX_FORCE_USAGE);
//        /**
//         * Countingpattern upper bound: multiplicity of nested countingPatterns
//         * result: deeper countingPatterns of the form "(element)[6, 10]" are
//         * converted to "(...)*"
//         */
//        COUNTINGPATTERN_UPPER_BOUND_MULTIPLICITY = preferencesManager.getIntegerSetting("XSD2DTD.countingPattern_upper_bound_multiplicity", COUNTINGPATTERN_UPPER_BOUND_MULTIPLICITY);
//    }
}
