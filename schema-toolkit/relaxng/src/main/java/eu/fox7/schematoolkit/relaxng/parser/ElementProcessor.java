package eu.fox7.schematoolkit.relaxng.parser;

import eu.fox7.schematoolkit.relaxng.om.Element;
import eu.fox7.schematoolkit.relaxng.om.Grammar;
import eu.fox7.schematoolkit.relaxng.om.Pattern;
import eu.fox7.schematoolkit.relaxng.om.RelaxNGSchema;
import eu.fox7.schematoolkit.relaxng.parser.exceptions.EmptyNameClassException;
import eu.fox7.schematoolkit.relaxng.parser.exceptions.EmptyPatternException;
import eu.fox7.schematoolkit.relaxng.parser.exceptions.InvalidQNameException;
import eu.fox7.schematoolkit.relaxng.parser.exceptions.MultipleNameClassException;
import eu.fox7.schematoolkit.xsd.tools.NameChecker;

import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * This processor handles the element elements of a Relax NG XSDSchema
 *
 * @author Lars Schmidt
 */
class ElementProcessor extends RNGPatternProcessorBase {

    private Element element;

    public ElementProcessor(RelaxNGSchema rngSchema, Grammar grammar) {
        super(rngSchema, grammar);
    }

    @Override
    protected Element processNode(Node node) throws Exception {

        /**
         * Attributes:
         * -----------
         * name     QName, or a nameClass content -> only one is allowed!
         */
        this.element = new Element();

        String elementNameAttributeString = null;
        NamedNodeMap attributes = node.getAttributes();
        if (attributes != null && attributes.getNamedItem("name") != null) {
            elementNameAttributeString = ((Attr) attributes.getNamedItem("name")).getValue();
            if (!NameChecker.isQName(elementNameAttributeString)) {
                throw new InvalidQNameException(elementNameAttributeString, "element");
            }
            this.element.setNameAttribute(elementNameAttributeString);
        }
        this.setPatternAttributes(element, node);
        visitChildren(node);

        if (this.element.getNameClass() == null && (this.element.getNameAttribute() == null || this.element.getNameAttribute().equals(""))) {
            throw new EmptyNameClassException("element");
        }

        if (this.element.getPatterns().size() < 1) {
            throw new EmptyPatternException("element");
        }
        return this.element;
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
         *      ),      if there is a name attribute the above is not allowed
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
         *      )+
         * )
         * 
         */
        String nodeName = childNode.getNodeName();
        if (nodeName.contains(":")) {
            nodeName = nodeName.split(":")[1];
        }
        if (childNode != null
                && !"#text".equals(nodeName)
                && !"#comment".equals(nodeName)
                && !"#document".equals(nodeName)
                && !"#cdata-section".equals(nodeName)) {
            if (valueIsValid(nodeName)) {
                switch (CASE.valueOf(nodeName)) {
                    case name:
                        if (getDebug()) {
                            System.out.println("element: name");
                        }
                        if (this.element.getNameClass() != null || this.element.getNameAttribute() != null) {
                            throw new MultipleNameClassException("element: name");
                        }
                        NameProcessor nameProcessor = new NameProcessor(rngSchema);
                        this.element.setNameClass(nameProcessor.processNode(childNode));
                        break;
                    case anyName:
                        if (getDebug()) {
                            System.out.println("element: anyName");
                        }
                        if (this.element.getNameClass() != null || this.element.getNameAttribute() != null) {
                            throw new MultipleNameClassException("element: anyName");
                        }
                        AnyNameProcessor anyNameProcessor = new AnyNameProcessor(rngSchema);
                        this.element.setNameClass(anyNameProcessor.processNode(childNode));
                        break;
                    case nsName:
                        if (getDebug()) {
                            System.out.println("element: nsName");
                        }
                        if (this.element.getNameClass() != null || this.element.getNameAttribute() != null) {
                            throw new MultipleNameClassException("element: nsName");
                        }
                        NsNameProcessor nsNameProcessor = new NsNameProcessor(rngSchema);
                        this.element.setNameClass(nsNameProcessor.processNode(childNode));
                        break;
                    case choice:                              
                    	if (this.element.getNameAttribute() == null && this.element.getNameClass() == null) {
                    		ChoiceNameProcessor choiceNameProcessor = new ChoiceNameProcessor(rngSchema);
                    		this.element.setNameClass(choiceNameProcessor.processNode(childNode));
                    	} else {
                    		ChoiceProcessor choiceProcessor = new ChoiceProcessor(rngSchema, this.grammar);
                    		this.element.addPattern(choiceProcessor.processNode(childNode));
                    	}
                    	break;
                    default:
                        super.processChild(childNode);
                }
            }
        }
    }

	@Override
	protected void addPattern(Pattern pattern) {
		this.element.addPattern(pattern);
	}
}
