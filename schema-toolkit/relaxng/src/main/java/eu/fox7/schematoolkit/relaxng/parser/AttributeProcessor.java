package eu.fox7.schematoolkit.relaxng.parser;

import eu.fox7.schematoolkit.relaxng.om.Attribute;
import eu.fox7.schematoolkit.relaxng.om.Grammar;
import eu.fox7.schematoolkit.relaxng.om.Pattern;
import eu.fox7.schematoolkit.relaxng.om.RelaxNGSchema;
import eu.fox7.schematoolkit.relaxng.parser.exceptions.EmptyNameClassException;
import eu.fox7.schematoolkit.relaxng.parser.exceptions.InvalidQNameException;
import eu.fox7.schematoolkit.relaxng.parser.exceptions.MultipleNameClassException;
import eu.fox7.schematoolkit.relaxng.parser.exceptions.MultiplePatternException;
import eu.fox7.schematoolkit.xsd.tools.NameChecker;

import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * This processor handles the attribute elements of a Relax NG XSDSchema
 *
 * @author Lars Schmidt
 */
class AttributeProcessor extends RNGPatternProcessorBase {

    private Attribute attribute;

    public AttributeProcessor(RelaxNGSchema rngSchema, Grammar grammar) {
        super(rngSchema, grammar);
    }

    @Override
    protected Attribute processNode(Node node) throws Exception {

        /**
         * Attributes:
         * -----------
         */
        this.attribute = new Attribute();

        String attributeName = null;
        NamedNodeMap attributes = node.getAttributes();
        if (attributes != null && attributes.getNamedItem("name") != null) {
            attributeName = ((Attr) attributes.getNamedItem("name")).getValue();
            if (!NameChecker.isQName(attributeName)) {
                throw new InvalidQNameException(attributeName, "element");
            }
            this.attribute.setNameAttribute(attributeName);
        }
        this.setPatternAttributes(attribute, node);
        visitChildren(node);

        if (this.attribute.getNameClass() == null && (this.attribute.getNameAttribute() == null || this.attribute.getNameAttribute().equals(""))) {
            throw new EmptyNameClassException("attribute");
        }

        // The pattern is optional in the Full XML Syntax of Relax NG

        return this.attribute;

    }

    @Override
    protected void processChild(Node childNode) throws Exception {

        /**
         * Children:
         * ---------
         * (
         *      (
         *          name|
         *          anyName|            
         *          nsName|
         *          choice -> nameClass
         *      ),
         *      (
         *          element|
         *          attribute|
         *          group|
         *          interleave|
         *          choice|
         *          optional|
         *          zeroOrMore|
         *          oneOrMore|
         *          list|
         *          mixed|
         *          ref|
         *          parentRef|
         *          empty|
         *          text|
         *          value|
         *          data|
         *          notAllowed|
         *          externalRef|
         *          grammar
         *      )?
         * )
         *
         *
         */
        String nodeName = childNode.getNodeName();
        if (nodeName.contains(":")) {
            nodeName = nodeName.split(":")[1];
        }
        if (childNode != null && !"#text".equals(nodeName) && !"#comment".equals(nodeName) && !"#document".equals(nodeName) && !"#cdata-section".equals(nodeName)) {
            if (valueIsValid(nodeName)) {
                switch (CASE.valueOf(nodeName)) {
                    case name:
                        if (getDebug()) {
                            System.out.println("attribute: name");
                        }
                        if (this.attribute.getNameClass() != null || this.attribute.getNameAttribute() != null) {
                            throw new MultipleNameClassException("attribute: name");
                        }
                        NameProcessor nameProcessor = new NameProcessor(rngSchema);
                        this.attribute.setNameClass(nameProcessor.processNode(childNode));
                        break;
                    case anyName:
                        if (getDebug()) {
                            System.out.println("attribute: anyName");
                        }
                        if (this.attribute.getNameClass() != null || this.attribute.getNameAttribute() != null) {
                            throw new MultipleNameClassException("attribute: anyName");
                        }
                        AnyNameProcessor anyNameProcessor = new AnyNameProcessor(rngSchema);
                        this.attribute.setNameClass(anyNameProcessor.processNode(childNode));
                        break;
                    case nsName:
                        if (getDebug()) {
                            System.out.println("attribute: nsName");
                        }
                        if (this.attribute.getNameClass() != null || this.attribute.getNameAttribute() != null) {
                            throw new MultipleNameClassException("attribute: nsName");
                        }
                        NsNameProcessor nsNameProcessor = new NsNameProcessor(rngSchema);
                        this.attribute.setNameClass(nsNameProcessor.processNode(childNode));
                        break;
                    case choice:
                        if (getDebug()) {
                            System.out.println("attribute: choice");
                        }
                        if (this.attribute.getNameAttribute() == null && this.attribute.getNameClass() == null) {
                            ChoiceNameProcessor choiceNameProcessor = new ChoiceNameProcessor(rngSchema);
                            this.attribute.setNameClass(choiceNameProcessor.processNode(childNode));
                        } else {
                            // choice as pattern
                            ChoiceProcessor choiceProcessor = new ChoiceProcessor(rngSchema, this.grammar);
                            this.addPattern(choiceProcessor.processNode(childNode));
                        }
                        break;
                    default:
                        super.processChild(childNode);
                }
            }
        }
    }

	@Override
	protected void addPattern(Pattern pattern) throws MultiplePatternException {
		if (this.attribute.getPattern() == null)
			this.attribute.setPattern(pattern);
		else
			throw new MultiplePatternException("attribute");
	}
}
