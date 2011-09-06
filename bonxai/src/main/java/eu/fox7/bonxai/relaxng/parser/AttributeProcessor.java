package eu.fox7.bonxai.relaxng.parser;

import eu.fox7.bonxai.relaxng.Attribute;
import eu.fox7.bonxai.relaxng.Define;
import eu.fox7.bonxai.relaxng.Empty;
import eu.fox7.bonxai.relaxng.ExternalRef;
import eu.fox7.bonxai.relaxng.Grammar;
import eu.fox7.bonxai.relaxng.NotAllowed;
import eu.fox7.bonxai.relaxng.ParentRef;
import eu.fox7.bonxai.relaxng.Ref;
import eu.fox7.bonxai.relaxng.RelaxNGSchema;
import eu.fox7.bonxai.relaxng.Text;
import eu.fox7.bonxai.relaxng.parser.exceptions.EmptyNameClassException;
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
 * This processor handles the attribute elements of a Relax NG XSDSchema
 *
 * @author Lars Schmidt
 */
class AttributeProcessor extends RNGProcessorBase {

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
                    case empty:
                        if (getDebug()) {
                            System.out.println("attribute: empty");
                        }
                        if (this.attribute.getPattern() != null) {
                            throw new MultiplePatternException("attribute: empty");
                        }
                        Empty empty = new Empty();
                        this.setPatternAttributes(empty, childNode);
                        this.attribute.setPattern(empty);
                        break;
                    case notAllowed:
                        if (getDebug()) {
                            System.out.println("attribute: notAllowed");
                        }
                        if (this.attribute.getPattern() != null) {
                            throw new MultiplePatternException("attribute: notAllowed");
                        }
                        NotAllowed notAllowed = new NotAllowed();
                        this.setPatternAttributes(notAllowed, childNode);
                        this.attribute.setPattern(notAllowed);
                        break;
                    case externalRef:
                        if (getDebug()) {
                            System.out.println("attribute: externalRef");
                        }
                        if (this.attribute.getPattern() != null) {
                            throw new MultiplePatternException("attribute: externalRef");
                        }

                        NamedNodeMap externalRefAttributes = childNode.getAttributes();
                        String externalRefName = null;
                        if (externalRefAttributes != null && externalRefAttributes.getNamedItem("href") != null) {
                            externalRefName = ((Attr) externalRefAttributes.getNamedItem("href")).getValue();
                            if (!NameChecker.isAnyUri(externalRefName)) {
                                throw new InvalidAnyUriException(externalRefName, "externalRef under attribute");
                            }
                        }
                        if (externalRefName == null) {
                            throw new MissingHrefException(nodeName);
                        }
                        ExternalRef externalRef = new ExternalRef(externalRefName);
                        this.setPatternAttributes(externalRef, childNode);
                        this.attribute.setPattern(externalRef);
                        break;
                    case optional:
                        if (getDebug()) {
                            System.out.println("attribute: optional");
                        }
                        if (this.attribute.getPattern() != null) {
                            throw new MultiplePatternException("attribute: optional");
                        }
                        OptionalProcessor optionalProcessor = new OptionalProcessor(rngSchema, this.grammar);
                        this.attribute.setPattern(optionalProcessor.processNode(childNode));
                        break;
                    case mixed:
                        if (getDebug()) {
                            System.out.println("attribute: mixed");
                        }
                        if (this.attribute.getPattern() != null) {
                            throw new MultiplePatternException("attribute: mixed");
                        }
                        MixedProcessor mixedProcessor = new MixedProcessor(rngSchema, this.grammar);
                        this.attribute.setPattern(mixedProcessor.processNode(childNode));
                        break;
                    case text:
                        if (getDebug()) {
                            System.out.println("attribute: text");
                        }
                        if (this.attribute.getPattern() != null) {
                            throw new MultiplePatternException("attribute: text");
                        }
                        Text text = new Text();
                        this.setPatternAttributes(text, childNode);
                        this.attribute.setPattern(text);
                        break;
                    case data:
                        if (getDebug()) {
                            System.out.println("attribute: data");
                        }
                        if (this.attribute.getPattern() != null) {
                            throw new MultiplePatternException("attribute: data");
                        }
                        DataProcessor dataProcessor = new DataProcessor(rngSchema, this.grammar);
                        this.attribute.setPattern(dataProcessor.processNode(childNode));
                        break;
                    case value:
                        if (getDebug()) {
                            System.out.println("attribute: value");
                        }
                        if (this.attribute.getPattern() != null) {
                            throw new MultiplePatternException("attribute: value");
                        }
                        ValueProcessor valueProcessor = new ValueProcessor(rngSchema, this.grammar);
                        this.attribute.setPattern(valueProcessor.processNode(childNode));
                        break;
                    case list:
                        if (getDebug()) {
                            System.out.println("attribute: list");
                        }
                        if (this.attribute.getPattern() != null) {
                            throw new MultiplePatternException("attribute: list");
                        }
                        ListProcessor listProcessor = new ListProcessor(rngSchema, grammar);
                        this.attribute.setPattern(listProcessor.processNode(childNode));
                        break;
                    case ref:
                        if (getDebug()) {
                            System.out.print("attribute: ref");
                        }
                        if (this.attribute.getPattern() != null) {
                            throw new MultiplePatternException("attribute: ref");
                        }

                        NamedNodeMap refAttributes = childNode.getAttributes();
                        if (refAttributes != null && refAttributes.getNamedItem("name") != null) {
                            String refName = ((Attr) refAttributes.getNamedItem("name")).getValue();

                            if (!NameChecker.isNCName(refName)) {
                                throw new InvalidNCNameException(refName, "ref under attribute");
                            }

                            if (getDebug()) {
                                System.out.print(", name: " + refName + "\n");
                            }

                            if (this.grammar != null) {
                                this.grammar.registerDefinePatternInLookUpTable(new Define(refName));
                                Ref patternRef = new Ref(this.grammar.getDefineLookUpTable().getReference(refName), this.grammar);
                                patternRef.setRefName(refName);
                                this.setPatternAttributes(patternRef, childNode);
                                this.attribute.setPattern(patternRef);
                            } else {
                                throw new GrammarIsNullException(refName);
                            }
                        }
                        break;
                    case parentRef:
                        if (getDebug()) {
                            System.out.print("attribute: parentRef");
                        }
                        if (this.attribute.getPattern() != null) {
                            throw new MultiplePatternException("attribute: parentRef");
                        }
                        NamedNodeMap attributesParentRef = childNode.getAttributes();
                        if (attributesParentRef != null && attributesParentRef.getNamedItem("name") != null) {
                            String parentRefName = ((Attr) attributesParentRef.getNamedItem("name")).getValue();

                            if (!NameChecker.isNCName(parentRefName)) {
                                throw new InvalidNCNameException(parentRefName, "parentRef under attribute");
                            }

                            if (getDebug()) {
                                System.out.print(", name: " + parentRefName + "\n");
                            }

                            if (this.grammar.getParentGrammar() != null) {
                                this.grammar.getParentGrammar().registerDefinePatternInLookUpTable(new Define(parentRefName));
                            } else {
                                throw new ParentGrammarIsNullException("attribute: parentRef (\"" + parentRefName + "\")");
                            }

                            ParentRef parentRefPattern = new ParentRef(this.grammar.getParentGrammar().getDefineLookUpTable().getReference(parentRefName), this.grammar, this.grammar.getParentGrammar());
                            parentRefPattern.setRefName(parentRefName);
                            this.setPatternAttributes(parentRefPattern, childNode);
                            this.attribute.setPattern(parentRefPattern);
                        }
                        break;
                    case oneOrMore:
                        if (getDebug()) {
                            System.out.println("attribute: oneOrMore");
                        }
                        if (this.attribute.getPattern() != null) {
                            throw new MultiplePatternException("attribute: oneOrMore");
                        }
                        OneOrMoreProcessor oneOrMoreProcessor = new OneOrMoreProcessor(rngSchema, grammar);
                        this.attribute.setPattern(oneOrMoreProcessor.processNode(childNode));
                        break;
                    case zeroOrMore:
                        if (getDebug()) {
                            System.out.println("attribute: zeroOrMore");
                        }
                        if (this.attribute.getPattern() != null) {
                            throw new MultiplePatternException("attribute: zeroOrMore");
                        }
                        ZeroOrMoreProcessor zeroOrMoreProcessor = new ZeroOrMoreProcessor(rngSchema, grammar);
                        this.attribute.setPattern(zeroOrMoreProcessor.processNode(childNode));
                        break;
                    case grammar:
                        if (getDebug()) {
                            System.out.println("attribute: grammar");
                        }
                        if (this.attribute.getPattern() != null) {
                            throw new MultiplePatternException("attribute: grammar");
                        }
                        GrammarProcessor grammarProcessor = new GrammarProcessor(rngSchema, grammar);
                        this.attribute.setPattern(grammarProcessor.processNode(childNode));
                        break;
                    case choice:
                        if (getDebug()) {
                            System.out.println("attribute: choice");
                        }
                        if (this.attribute.getNameAttribute() == null && this.attribute.getNameClass() == null) {
                            ChoiceNameProcessor choiceNameProcessor = new ChoiceNameProcessor(rngSchema);
                            this.attribute.setNameClass(choiceNameProcessor.processNode(childNode));
                        } else {
                            if (this.attribute.getPattern() != null) {
                                throw new MultiplePatternException("attribute: choice");
                            }
                            // choice as pattern
                            ChoiceProcessor choiceProcessor = new ChoiceProcessor(rngSchema, this.grammar);
                            this.attribute.setPattern(choiceProcessor.processNode(childNode));
                        }
                        break;
                    case group:
                        if (getDebug()) {
                            System.out.println("attribute: group");
                        }
                        if (this.attribute.getPattern() != null) {
                            throw new MultiplePatternException("attribute: group");
                        }
                        GroupProcessor groupProcessor = new GroupProcessor(rngSchema, this.grammar);
                        this.attribute.setPattern(groupProcessor.processNode(childNode));
                        break;
                    case interleave:
                        if (getDebug()) {
                            System.out.println("attribute: interleave");
                        }
                        if (this.attribute.getPattern() != null) {
                            throw new MultiplePatternException("attribute: interleave");
                        }
                        InterleaveProcessor interleaveProcessor = new InterleaveProcessor(rngSchema, grammar);
                        this.attribute.setPattern(interleaveProcessor.processNode(childNode));
                        break;
                    default:
                        throw new UnsupportedContentException(nodeName, "attribute");
                }
            }
        }
    }
}
