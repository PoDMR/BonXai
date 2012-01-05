package eu.fox7.schematoolkit.relaxng.parser;

import eu.fox7.schematoolkit.relaxng.om.Grammar;
import eu.fox7.schematoolkit.relaxng.om.Param;
import eu.fox7.schematoolkit.relaxng.om.RelaxNGSchema;
import eu.fox7.schematoolkit.relaxng.parser.exceptions.InvalidNCNameException;
import eu.fox7.schematoolkit.relaxng.parser.exceptions.MissingNameException;
import eu.fox7.schematoolkit.xsd.tools.NameChecker;

import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * This processor handles the param elements of a Relax NG XSDSchema
 *
 * @author Lars Schmidt
 */
class ParamProcessor extends RNGProcessorBase {

    public ParamProcessor(RelaxNGSchema rngSchema, Grammar grammar) {
        super(rngSchema, grammar);
    }

    @Override
    protected Param processNode(Node node) throws Exception {

        /**
         * Attributes:
         * -----------
         * name NCName
         */
        String content = node.getTextContent();

        if (getDebug()) {
            System.out.println("Param: \"" + content + "\"");
        }
        String name = null;
        NamedNodeMap attributes = node.getAttributes();
        if (attributes != null && attributes.getNamedItem("name") != null) {
            name = ((Attr) attributes.getNamedItem("name")).getValue();
            if (name == null || !NameChecker.isNCName(name)) {
                throw new InvalidNCNameException(name, "name of param");
            }
        }
        if (name == null) {
            throw new MissingNameException("Param");
        }
        Param param = new Param(name);
        param.setContent(content);

        return param;
    }

    @Override
    protected void processChild(Node childNode) throws Exception {

        /**
         * Children:
         * ---------
         * (#PCDATA) String content
         *  This is already handled in the method "processNode" above.
         */
    }
}
