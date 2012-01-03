package eu.fox7.schematoolkit.relaxng.parser;

import eu.fox7.bonxai.xsd.tools.NameChecker;
import eu.fox7.schematoolkit.relaxng.om.Name;
import eu.fox7.schematoolkit.relaxng.om.RelaxNGSchema;
import eu.fox7.schematoolkit.relaxng.parser.exceptions.InvalidQNameException;

import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * This processor handles the name elements of a Relax NG XSDSchema
 *
 * @author Lars Schmidt
 */
class NameProcessor extends RNGProcessorBase {

    public NameProcessor(RelaxNGSchema rngSchema) {
        super(rngSchema, null);
    }

    @Override
    protected Name processNode(Node node) throws Exception {

        /**
         * Attributes:
         * -----------
         * ns String
         */
        String nameContent = node.getTextContent();
        if (nameContent == null || !NameChecker.isQName(nameContent)) {
            throw new InvalidQNameException(nameContent, "content of name");
        }

        if (getDebug()) {
            System.out.println("name: \"" + nameContent + "\"");
        }

        Name name = new Name(nameContent);

        NamedNodeMap attributes = node.getAttributes();
        if (attributes != null && attributes.getNamedItem("ns") != null) {
            String nsName = ((Attr) attributes.getNamedItem("ns")).getValue();
            name.setAttributeNamespace(nsName);
        }
        return name;
    }

    @Override
    protected void processChild(Node childNode) throws Exception {
        /**
         * Children:
         * ---------
         * (#PCDATA) NCName as content
         * 
         */
    }
}
