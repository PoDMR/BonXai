package eu.fox7.bonxai.relaxng.parser;

import eu.fox7.bonxai.relaxng.Define;
import eu.fox7.bonxai.relaxng.Element;
import eu.fox7.bonxai.relaxng.Empty;
import eu.fox7.bonxai.relaxng.ExternalRef;
import eu.fox7.bonxai.relaxng.Grammar;
import eu.fox7.bonxai.relaxng.NotAllowed;
import eu.fox7.bonxai.relaxng.ParentRef;
import eu.fox7.bonxai.relaxng.Pattern;
import eu.fox7.bonxai.relaxng.Ref;
import eu.fox7.bonxai.relaxng.RelaxNGSchema;
import eu.fox7.bonxai.relaxng.Text;
import eu.fox7.bonxai.relaxng.parser.exceptions.EmptyNameClassException;
import eu.fox7.bonxai.relaxng.parser.exceptions.EmptyPatternException;
import eu.fox7.bonxai.relaxng.parser.exceptions.GrammarIsNullException;
import eu.fox7.bonxai.relaxng.parser.exceptions.InvalidAnyUriException;
import eu.fox7.bonxai.relaxng.parser.exceptions.InvalidNCNameException;
import eu.fox7.bonxai.relaxng.parser.exceptions.InvalidQNameException;
import eu.fox7.bonxai.relaxng.parser.exceptions.MissingHrefException;
import eu.fox7.bonxai.relaxng.parser.exceptions.MultipleNameClassException;
import eu.fox7.bonxai.relaxng.parser.exceptions.MultiplePatternException;
import eu.fox7.bonxai.relaxng.parser.exceptions.ParentGrammarIsNullException;
import eu.fox7.bonxai.relaxng.parser.exceptions.UnsupportedContentException;
import eu.fox7.bonxai.xsd.tools.NameChecker;

import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * This processor handles the element elements of a Relax NG XSDSchema
 *
 * @author Lars Schmidt
 */
class ElementProcessor extends RNGProcessorBase {

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
                    case empty:
                        if (getDebug()) {
                            System.out.println("element: empty");
                        }
                        for (Pattern currentPattern : element.getPatterns()) {
                            if (currentPattern instanceof Empty) {
                                throw new MultiplePatternException("element with more than one <empty/>");
                            }
                        }
                        Empty empty = new Empty();
                        this.setPatternAttributes(empty, childNode);
                        this.element.addPattern(empty);
                        break;
                    case text:
                        if (getDebug()) {
                            System.out.println("element: text");
                        }
                        Text text = new Text();
                        this.setPatternAttributes(text, childNode);
                        this.element.addPattern(text);
                        break;
                    case data:
                        if (getDebug()) {
                            System.out.println("element: data");
                        }
                        DataProcessor dataProcessor = new DataProcessor(rngSchema, this.grammar);
                        this.element.addPattern(dataProcessor.processNode(childNode));
                        break;
                    case value:
                        if (getDebug()) {
                            System.out.println("element: value");
                        }
                        ValueProcessor valueProcessor = new ValueProcessor(rngSchema, this.grammar);
                        this.element.addPattern(valueProcessor.processNode(childNode));
                        break;
                    case list:
                        if (getDebug()) {
                            System.out.println("element: list");
                        }
                        ListProcessor listProcessor = new ListProcessor(rngSchema, grammar);
                        this.element.addPattern(listProcessor.processNode(childNode));
                        break;
                    case attribute:
                        if (getDebug()) {
                            System.out.println("element: attribute");
                        }
                        AttributeProcessor attributeProcessor = new AttributeProcessor(rngSchema, this.grammar);
                        this.element.addPattern(attributeProcessor.processNode(childNode));
                        break;
                    case element:
                        if (getDebug()) {
                            System.out.println("element: element");
                        }
                        ElementProcessor elementProcessor = new ElementProcessor(rngSchema, this.grammar);
                        this.element.addPattern(elementProcessor.processNode(childNode));
                        break;
                    case mixed:
                        if (getDebug()) {
                            System.out.println("element: mixed");
                        }
                        MixedProcessor mixedProcessor = new MixedProcessor(rngSchema, this.grammar);
                        this.element.addPattern(mixedProcessor.processNode(childNode));
                        break;
                    case optional:
                        if (getDebug()) {
                            System.out.println("element: optional");
                        }
                        OptionalProcessor optionalProcessor = new OptionalProcessor(rngSchema, this.grammar);
                        this.element.addPattern(optionalProcessor.processNode(childNode));
                        break;
                    case zeroOrMore:
                        if (getDebug()) {
                            System.out.println("element: zeroOrMore");
                        }
                        ZeroOrMoreProcessor zeroOrMoreProcessor = new ZeroOrMoreProcessor(rngSchema, grammar);
                        this.element.addPattern(zeroOrMoreProcessor.processNode(childNode));
                        break;
                    case externalRef:
                        if (getDebug()) {
                            System.out.println("element: externalRef");
                        }
                        NamedNodeMap externalRefAttributes = childNode.getAttributes();
                        String externalRefName = null;
                        if (externalRefAttributes != null && externalRefAttributes.getNamedItem("href") != null) {
                            externalRefName = ((Attr) externalRefAttributes.getNamedItem("href")).getValue();
                            if (!NameChecker.isAnyUri(externalRefName)) {
                                throw new InvalidAnyUriException(externalRefName, "externalRef under element");
                            }
                        }
                        if (externalRefName == null) {
                            throw new MissingHrefException(nodeName);
                        }
                        ExternalRef externalRef = new ExternalRef(externalRefName);
                        this.setPatternAttributes(externalRef, childNode);
                        this.element.addPattern(externalRef);
                        break;
                    case notAllowed:
                        if (getDebug()) {
                            System.out.println("element: notAllowed");
                        }
                        NotAllowed notAllowed = new NotAllowed();
                        this.setPatternAttributes(notAllowed, childNode);
                        this.element.addPattern(notAllowed);
                        break;
                    case grammar:
                        if (getDebug()) {
                            System.out.println("element: grammar");
                        }
                        GrammarProcessor grammarProcessor = new GrammarProcessor(rngSchema, grammar);
                        this.element.addPattern(grammarProcessor.processNode(childNode));
                        break;
                    case ref:
                        if (getDebug()) {
                            System.out.print("element: ref");
                        }
                        NamedNodeMap attributes = childNode.getAttributes();
                        if (attributes != null && attributes.getNamedItem("name") != null) {
                            String refName = ((Attr) attributes.getNamedItem("name")).getValue();
                            if (!NameChecker.isNCName(refName)) {
                                throw new InvalidNCNameException(refName, "ref under element");
                            }
                            if (getDebug()) {
                                System.out.print(", name: " + refName + "\n");
                            }
                            if (this.grammar != null) {
                                this.grammar.registerDefinePatternInLookUpTable(new Define(refName));
                                Ref patternRef = new Ref(this.grammar.getDefineLookUpTable().getReference(refName), this.grammar);
                                patternRef.setRefName(refName);
                                this.setPatternAttributes(patternRef, childNode);
                                this.element.addPattern(patternRef);
                            } else {
                                throw new GrammarIsNullException(refName);
                            }
                        }
                        break;
                    case parentRef:
                        if (getDebug()) {
                            System.out.print("element: parentRef");
                        }
                        NamedNodeMap attributesParentRef = childNode.getAttributes();
                        if (attributesParentRef != null && attributesParentRef.getNamedItem("name") != null) {
                            String parentRefName = ((Attr) attributesParentRef.getNamedItem("name")).getValue();
                            if (!NameChecker.isNCName(parentRefName)) {
                                throw new InvalidNCNameException(parentRefName, "parentRef under element");
                            }
                            if (getDebug()) {
                                System.out.print(", name: " + parentRefName + "\n");
                            }
                            if (this.grammar.getParentGrammar() != null) {
                                this.grammar.getParentGrammar().registerDefinePatternInLookUpTable(new Define(parentRefName));
                            } else {
                                throw new ParentGrammarIsNullException("element: parentRef (\"" + parentRefName + "\")");
                            }
                            ParentRef parentRefPattern = new ParentRef(this.grammar.getParentGrammar().getDefineLookUpTable().getReference(parentRefName), this.grammar, this.grammar.getParentGrammar());
                            parentRefPattern.setRefName(parentRefName);
                            this.setPatternAttributes(parentRefPattern, childNode);
                            this.element.addPattern(parentRefPattern);
                        }
                        break;
                    case oneOrMore:
                        if (getDebug()) {
                            System.out.println("element: oneOrMore");
                        }
                        OneOrMoreProcessor oneOrMoreProcessor = new OneOrMoreProcessor(rngSchema, grammar);
                        this.element.addPattern(oneOrMoreProcessor.processNode(childNode));
                        break;
                    case choice:
                        if (getDebug()) {
                            System.out.println("element: choice");
                        }
                        if (this.element.getNameAttribute() == null && this.element.getNameClass() == null) {
                            ChoiceNameProcessor choiceNameProcessor = new ChoiceNameProcessor(rngSchema);
                            this.element.setNameClass(choiceNameProcessor.processNode(childNode));
                        } else {
                            ChoiceProcessor choiceProcessor = new ChoiceProcessor(rngSchema, this.grammar);
                            this.element.addPattern(choiceProcessor.processNode(childNode));
                        }
                        break;
                    case group:
                        if (getDebug()) {
                            System.out.println("element: group");
                        }
                        GroupProcessor groupProcessor = new GroupProcessor(rngSchema, grammar);
                        this.element.addPattern(groupProcessor.processNode(childNode));
                        break;
                    case interleave:
                        if (getDebug()) {
                            System.out.println("element: interleave");
                        }
                        InterleaveProcessor interleaveProcessor = new InterleaveProcessor(rngSchema, grammar);
                        this.element.addPattern(interleaveProcessor.processNode(childNode));
                        break;
                    default:
                        throw new UnsupportedContentException(nodeName, "element");
                }
            }
        }
    }
}
