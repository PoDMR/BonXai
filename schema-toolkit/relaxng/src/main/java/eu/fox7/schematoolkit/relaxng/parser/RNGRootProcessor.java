package eu.fox7.schematoolkit.relaxng.parser;

import eu.fox7.schematoolkit.common.IdentifiedNamespace;
import eu.fox7.schematoolkit.relaxng.om.Empty;
import eu.fox7.schematoolkit.relaxng.om.ExternalRef;
import eu.fox7.schematoolkit.relaxng.om.NotAllowed;
import eu.fox7.schematoolkit.relaxng.om.Pattern;
import eu.fox7.schematoolkit.relaxng.om.RelaxNGSchema;
import eu.fox7.schematoolkit.relaxng.om.Text;
import eu.fox7.schematoolkit.relaxng.parser.exceptions.EmptyPatternException;
import eu.fox7.schematoolkit.relaxng.parser.exceptions.InvalidAnyUriException;
import eu.fox7.schematoolkit.relaxng.parser.exceptions.MissingHrefException;
import eu.fox7.schematoolkit.relaxng.parser.exceptions.NodeIsNullException;
import eu.fox7.schematoolkit.relaxng.parser.exceptions.UnsupportedContentException;
import eu.fox7.schematoolkit.xsd.tools.NameChecker;

import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * This processor handles the root elements of a Relax NG XSDSchema
 * There are only the following root elements allowed in the
 * Relax NG Full XML Syntax:
 *
 * element
 * group
 * interleave
 * choice
 * notAllowed
 * externalRef
 * grammar
 *
 * In the simple syntax there is only grammar allowed as root of the document
 *
 * @author Lars Schmidt
 */
class RNGRootProcessor extends RNGProcessorBase {

    private Pattern rootPattern;

    public RNGRootProcessor(RelaxNGSchema rngSchema) {
        super(rngSchema, null);
    }

    @Override
    protected Pattern processNode(Node node) throws Exception {
        /**
         * The following elements are allowed as root elements of a
         * Full XML Syntax Relax NG XSDSchema:
         *
         * element
         * group
         * interleave
         * choice
         * notAllowed
         * externalRef
         * grammar
         *
         */
        if (node != null) {
            NamedNodeMap attributes = node.getAttributes();
            if (attributes != null) {
                for (int i = 0; i < attributes.getLength(); i++) {
                    if ((attributes.item(i)).getNodeName().startsWith("xmlns")) {
                        Node currentNode = attributes.item(i);
                        if (!NameChecker.isAnyUri(currentNode.getNodeValue())) {
                            throw new InvalidAnyUriException(currentNode.getNodeValue(), "namespace of grammar element");
                        }
                        if (currentNode.getNodeName().equals("xmlns")) {
                            this.rngSchema.setDefaultNamespace(currentNode.getNodeValue());
                        } else {
                            this.rngSchema.getNamespaceList().addNamespace(new IdentifiedNamespace(currentNode.getLocalName(), currentNode.getNodeValue()));
                        }
                    }
                }
            }

            this.rngSchema.setRootNamespacePrefix(node.getPrefix());

            this.processChild(node);

            if (this.rootPattern != null) {
                this.rngSchema.setRootPattern(rootPattern);
                return rootPattern;
            } else {
                throw new EmptyPatternException("root element is invalid or null");
            }
        } else {
            throw new NodeIsNullException();
        }
    }

    @Override
    protected void processChild(Node childNode) throws Exception {
        String nodeName = childNode.getNodeName();
        if (nodeName.contains(":")) {
            nodeName = nodeName.split(":")[1];
        }
        if (childNode != null && !"#text".equals(nodeName) && !"#comment".equals(nodeName) && !"#document".equals(nodeName) && !"#cdata-section".equals(nodeName)) {
            if (valueIsValid(nodeName)) {
                switch (CASE.valueOf(nodeName)) {
                    case element:
                        if (getDebug()) {
                            System.out.println("root -> element");
                        }
                        ElementProcessor elementProcessor = new ElementProcessor(rngSchema, this.grammar);
                        this.rootPattern = elementProcessor.processNode(childNode);
                        break;
                    case externalRef:
                        if (getDebug()) {
                            System.out.println("root -> externalRef");
                        }
                        NamedNodeMap externalRefAttributes = childNode.getAttributes();
                        String externalRefName = null;
                        if (externalRefAttributes != null && externalRefAttributes.getNamedItem("href") != null) {
                            externalRefName = ((Attr) externalRefAttributes.getNamedItem("href")).getValue();
                            if (!NameChecker.isAnyUri(externalRefName)) {
                                throw new InvalidAnyUriException(externalRefName, "externalRef as root");
                            }
                        }
                        if (externalRefName == null) {
                            throw new MissingHrefException(nodeName);
                        }
                        ExternalRef externalRef = new ExternalRef(externalRefName);
                        this.setPatternAttributes(externalRef, childNode);
                        this.rootPattern = externalRef;
                        break;
                    case notAllowed:
                        if (getDebug()) {
                            System.out.println("root -> notAllowed");
                        }
                        NotAllowed notAllowed = new NotAllowed();
                        this.setPatternAttributes(notAllowed, childNode);
                        this.rootPattern = notAllowed;
                        break;
                    case grammar:
                        if (getDebug()) {
                            System.out.println("root -> grammar");
                        }
                        GrammarProcessor grammarProcessor = new GrammarProcessor(rngSchema, grammar);
                        this.rootPattern = grammarProcessor.processNode(childNode);
                        break;
                    case choice:
                        if (getDebug()) {
                            System.out.println("root -> choice");
                        }
                        // choice as pattern
                        ChoiceProcessor choiceProcessor = new ChoiceProcessor(rngSchema, this.grammar);
                        this.rootPattern = choiceProcessor.processNode(childNode);
                        break;
                    case group:
                        if (getDebug()) {
                            System.out.println("root -> group");
                        }
                        GroupProcessor groupProcessor = new GroupProcessor(rngSchema, grammar);
                        this.rootPattern = groupProcessor.processNode(childNode);
                        break;
                    case interleave:
                        if (getDebug()) {
                            System.out.println("root -> interleave");
                        }
                        InterleaveProcessor interleaveProcessor = new InterleaveProcessor(rngSchema, grammar);
                        this.rootPattern = interleaveProcessor.processNode(childNode);
                        break;
                    case attribute:
                        if (getDebug()) {
                            System.out.println("root -> attribute");
                        }
                        AttributeProcessor attributeProcessor = new AttributeProcessor(rngSchema, grammar);
                        this.rootPattern = attributeProcessor.processNode(childNode);
                        break;
                    case text:
                        if (getDebug()) {
                            System.out.println("root -> text");
                        }
                        Text text = new Text();
                        this.setPatternAttributes(text, childNode);
                        this.rootPattern = text;
                        break;
                    case zeroOrMore:
                        if (getDebug()) {
                            System.out.println("root -> zeroOrMore");
                        }
                        ZeroOrMoreProcessor zeroOrMoreProcessor = new ZeroOrMoreProcessor(rngSchema, grammar);
                        this.rootPattern = zeroOrMoreProcessor.processNode(childNode);
                        break;
                    case optional:
                        if (getDebug()) {
                            System.out.println("root -> optional");
                        }
                        OptionalProcessor optionalProcessor = new OptionalProcessor(rngSchema, grammar);
                        this.rootPattern = optionalProcessor.processNode(childNode);
                        break;
                    case mixed:
                        if (getDebug()) {
                            System.out.println("root -> mixed");
                        }
                        MixedProcessor mixedProcessor = new MixedProcessor(rngSchema, grammar);
                        this.rootPattern = mixedProcessor.processNode(childNode);
                        break;
                    case list:
                        if (getDebug()) {
                            System.out.println("root -> list");
                        }
                        ListProcessor listProcessor = new ListProcessor(rngSchema, grammar);
                        this.rootPattern = listProcessor.processNode(childNode);
                        break;
                    case value:
                        if (getDebug()) {
                            System.out.println("root -> value");
                        }
                        ValueProcessor valueProcessor = new ValueProcessor(rngSchema, grammar);
                        this.rootPattern = valueProcessor.processNode(childNode);
                        break;
                    case empty:
                        if (getDebug()) {
                            System.out.println("root -> empty");
                        }
                        Empty empty = new Empty();
                        this.setPatternAttributes(empty, childNode);
                        this.rootPattern = empty;
                        break;
                    case oneOrMore:
                        if (getDebug()) {
                            System.out.println("root -> oneOrMore");
                        }
                        OneOrMoreProcessor oneOrMoreProcessor = new OneOrMoreProcessor(rngSchema, grammar);
                        this.rootPattern = oneOrMoreProcessor.processNode(childNode);
                        break;
                    case data:
                        if (getDebug()) {
                            System.out.println("root -> data");
                        }
                        DataProcessor dataProcessor = new DataProcessor(rngSchema, grammar);
                        this.rootPattern = dataProcessor.processNode(childNode);
                        break;
                    default:
                        throw new UnsupportedContentException(nodeName, "Relax NG XSDSchema as root element.");
                }
            }
        }
    }
}
