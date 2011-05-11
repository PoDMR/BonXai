package de.tudortmund.cs.bonxai.relaxng.parser;

import de.tudortmund.cs.bonxai.relaxng.Grammar;
import de.tudortmund.cs.bonxai.relaxng.RelaxNGSchema;
import de.tudortmund.cs.bonxai.relaxng.Value;
import de.tudortmund.cs.bonxai.relaxng.parser.exceptions.InvalidNCNameException;
import de.tudortmund.cs.bonxai.xsd.tools.NameChecker;

import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * This processor handles the value elements of a Relax NG XSDSchema
 *
 * @author Lars Schmidt
 */
class ValueProcessor extends RNGProcessorBase {

    private Value value;

    public ValueProcessor(RelaxNGSchema rngSchema, Grammar grammar) {
        super(rngSchema, grammar);
    }

    @Override
    protected Value processNode(Node node) throws Exception {

        /**
         * Attributes:
         * -----------
         * type NCName
         * datatypeLibrary anyURI
         * ns String
         */
        String type = null;
        NamedNodeMap attributes = node.getAttributes();
        if (attributes != null) {
            if (attributes.getNamedItem("type") != null) {
                type = ((Attr) attributes.getNamedItem("type")).getValue();
                if (!NameChecker.isNCName(type)) {
                    throw new InvalidNCNameException(type, "data: type attribute");
                }
            }
        }

        String content = node.getTextContent();

        this.value = new Value(content);

        // type is optional in Full XML Syntax
        if (type != null) {
            this.value.setType(type);
        }
        this.setPatternAttributes(value, node);
        return this.value;
    }

    @Override
    protected void processChild(Node childNode) throws Exception {
        /**
         * Children:
         * ---------
         * (#PCDATA) String content
         *
         */
    }
}
