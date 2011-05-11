package de.tudortmund.cs.bonxai.converter.xsd2relaxng;

import de.tudortmund.cs.bonxai.common.SymbolTableRef;
import de.tudortmund.cs.bonxai.converter.xsd2relaxng.exceptions.NoStartElementException;
import de.tudortmund.cs.bonxai.relaxng.*;
import de.tudortmund.cs.bonxai.tools.StatusLogger;
import de.tudortmund.cs.bonxai.xsd.AttributeGroup;
import de.tudortmund.cs.bonxai.xsd.ForeignSchema;
import de.tudortmund.cs.bonxai.xsd.XSDSchema;
import de.tudortmund.cs.bonxai.xsd.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;

/**
 * Class StartElementConverter
 *
 * The conversion of the content of a given XML XSDSchema document and all of its
 * external schemas starts with the top-level elements from these schemas.
 * In XML XSDSchema all top-level element definitions can be used to form
 * start-elements for possible instances
 *
 * @author Lars Schmidt
 */
class StartElementConverter extends ConverterBase {

    private HashSet<XSDSchema> alreadyHandledSchemas;
    // The elementInheritanceInformation and typeInheritanceInformation
    // will be passed through to the converter objects which will work with them
    private LinkedHashMap<de.tudortmund.cs.bonxai.xsd.Element, LinkedHashSet<de.tudortmund.cs.bonxai.xsd.Element>> elementInheritanceInformation;
    private LinkedHashMap<Type, LinkedHashSet<Type>> typeInheritanceInformation;

    /**
     * Constructor of class StartElementConverter
     * @param xmlSchema     The source XML XSDSchema object for the conversion
     *                      process
     * @param relaxng       The target RELAX NG object for the conversion
     *                      process
     *
     * @param xsdAttributeDefineRefMap          Look-up table for holding the mapping between an XML XSDSchema attribute and the corresponding conversion result RELAX NG pattern
     * @param xsdAttributeGroupDefineRefMap     Look-up table for holding the mapping between an XML XSDSchema attributeGroup and the corresponding conversion result RELAX NG pattern
     * @param xsdElementDefineRefMap            Look-up table for holding the mapping between an XML XSDSchema element and the corresponding conversion result RELAX NG pattern
     * @param xsdGroupDefineRefMap              Look-up table for holding the mapping between an XML XSDSchema group and the corresponding conversion result RELAX NG pattern
     * @param xsdTypeDefineRefMap               Look-up table for holding the mapping between an XML XSDSchema type and the corresponding conversion result RELAX NG pattern
     *
     * @param elementInheritanceInformation     Inheritance mapping of possible element substitutions for an element
     * @param typeInheritanceInformation        Inheritance mapping of possible element substitutions for an element
     */
    public StartElementConverter(XSDSchema xmlSchema, RelaxNGSchema relaxng, HashMap<de.tudortmund.cs.bonxai.xsd.Attribute, SymbolTableRef<LinkedList<Define>>> xsdAttributeDefineRefMap, HashMap<AttributeGroup, SymbolTableRef<LinkedList<Define>>> xsdAttributeGroupDefineRefMap, HashMap<de.tudortmund.cs.bonxai.xsd.Element, SymbolTableRef<LinkedList<Define>>> xsdElementDefineRefMap, HashMap<de.tudortmund.cs.bonxai.xsd.Group, SymbolTableRef<LinkedList<Define>>> xsdGroupDefineRefMap, HashMap<Type, SymbolTableRef<LinkedList<Define>>> xsdTypeDefineRefMap, LinkedHashMap<de.tudortmund.cs.bonxai.xsd.Element, LinkedHashSet<de.tudortmund.cs.bonxai.xsd.Element>> elementInheritanceInformation, LinkedHashMap<Type, LinkedHashSet<Type>> typeInheritanceInformation) {
        super(xmlSchema, relaxng, xsdAttributeDefineRefMap, xsdAttributeGroupDefineRefMap, xsdElementDefineRefMap, xsdGroupDefineRefMap, xsdTypeDefineRefMap);
        this.alreadyHandledSchemas = new HashSet<XSDSchema>();
        this.elementInheritanceInformation = elementInheritanceInformation;
        this.typeInheritanceInformation = typeInheritanceInformation;
    }

    /**
     * Start the conversion from XML XSDSchema to RELAX NG with the handling of the
     * all top-level elements from the given schema.
     */
    public void startConversionWithToplevelElements() throws NoStartElementException {
        Choice choice = new Choice();

        LinkedList<de.tudortmund.cs.bonxai.xsd.Element> startElements = new LinkedList<de.tudortmund.cs.bonxai.xsd.Element>();

        // Find all top-level elements recursivly from within all external and local schemas
        findStartElements(xmlSchema, startElements);

        StatusLogger.logInfo("XSD2RNG", startElements.size() + " start elements has been found");
        // Initialize the elementConverter for conversion of ONE XML XSDSchema element
        ElementConverter elementConverter = new ElementConverter(xmlSchema, relaxng, xsdAttributeDefineRefMap, xsdAttributeGroupDefineRefMap, xsdElementDefineRefMap, xsdGroupDefineRefMap, xsdTypeDefineRefMap, elementInheritanceInformation, typeInheritanceInformation);

        for (Iterator<de.tudortmund.cs.bonxai.xsd.Element> it = startElements.iterator(); it.hasNext();) {
            de.tudortmund.cs.bonxai.xsd.Element xsdElement = it.next();

            // Convert a single XML XSDSchema element to the corresponding RELAX NG element definition
            de.tudortmund.cs.bonxai.relaxng.Pattern pattern = elementConverter.convertElement(xsdElement, true);

            if (pattern != null) {
                choice.addPattern(pattern);
            }
        }

        Grammar grammar = (Grammar) this.relaxng.getRootPattern();
        if (choice.getPatterns().size() > 1) {
            grammar.addStartPattern(choice);
        } else {
            if (choice.getPatterns().isEmpty()) {
                // When there was no start element in the given XML schema document, throw exception
                throw new NoStartElementException();
            } else {
                grammar.addStartPattern(choice.getPatterns().getFirst());
            }
        }
    }

    /**
     * Find all start elements from the current XML XSDSchema object structure
     * representing a local schema and all of its foreign or external schemas
     * recursivly.
     * @param currentXSDSchema      The current XML schema document object
     * @param startElements         List of all found elements, that can be used as start elements for valid XML instances
     */
    private void findStartElements(XSDSchema currentXSDSchema, LinkedList<de.tudortmund.cs.bonxai.xsd.Element> startElements) {
        this.alreadyHandledSchemas.add(currentXSDSchema);

        startElements.addAll(currentXSDSchema.getElements());

        // Walk trough all foreignSchemas.
        if (currentXSDSchema.getForeignSchemas() != null && !currentXSDSchema.getForeignSchemas().isEmpty()) {
            for (Iterator<ForeignSchema> it = currentXSDSchema.getForeignSchemas().iterator(); it.hasNext();) {
                ForeignSchema foreignSchema = it.next();
                if (!this.alreadyHandledSchemas.contains(foreignSchema.getSchema())) {
                    findStartElements(foreignSchema.getSchema(), startElements);
                }
            }
        }
    }
}
