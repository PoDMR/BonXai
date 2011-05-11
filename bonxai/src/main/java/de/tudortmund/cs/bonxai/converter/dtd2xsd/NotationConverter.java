package de.tudortmund.cs.bonxai.converter.dtd2xsd;

import de.tudortmund.cs.bonxai.common.IdentifiedNamespace;
import de.tudortmund.cs.bonxai.dtd.DocumentTypeDefinition;
import de.tudortmund.cs.bonxai.xsd.XSDSchema;

/**
 * Class for the conversion of a DTD NOTATION to XML XSDSchema.
 *
 * This is not used at the moment. The DTD NOTATION type is currently handled
 * the same way like the enumeration type.
 *
 * It is planned to support this type in the correct way, if there is time left
 * in the period of this diploma-thesis.
 *
 * (Precondition: A representation for the XML XSDSchema "notation"s has to be
 * build in the XML XSDSchema object structure of this project)
 *
 * @author Lars Schmidt
 */
public class NotationConverter extends ConverterBase {

    NotationConverter(XSDSchema xmlSchema, IdentifiedNamespace targetNamespace, boolean namespaceAware) {
        super(xmlSchema, targetNamespace, namespaceAware);
    }

    void convert(DocumentTypeDefinition dtd) {

    }

}
