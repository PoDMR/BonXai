package de.tudortmund.cs.bonxai.relaxng.parser;

import de.tudortmund.cs.bonxai.relaxng.CombineMethod;
import de.tudortmund.cs.bonxai.relaxng.Define;
import de.tudortmund.cs.bonxai.relaxng.Empty;
import de.tudortmund.cs.bonxai.relaxng.ExternalRef;
import de.tudortmund.cs.bonxai.relaxng.Grammar;
import de.tudortmund.cs.bonxai.relaxng.IncludeContent;
import de.tudortmund.cs.bonxai.relaxng.NotAllowed;
import de.tudortmund.cs.bonxai.relaxng.ParentRef;
import de.tudortmund.cs.bonxai.relaxng.Pattern;
import de.tudortmund.cs.bonxai.relaxng.Ref;
import de.tudortmund.cs.bonxai.relaxng.RelaxNGSchema;
import de.tudortmund.cs.bonxai.relaxng.Text;
import de.tudortmund.cs.bonxai.relaxng.parser.exceptions.CombineMethodNotMatchingException;
import de.tudortmund.cs.bonxai.relaxng.parser.exceptions.GrammarIsNullException;
import de.tudortmund.cs.bonxai.relaxng.parser.exceptions.InvalidAnyUriException;
import de.tudortmund.cs.bonxai.relaxng.parser.exceptions.InvalidCombineMethodException;
import de.tudortmund.cs.bonxai.relaxng.parser.exceptions.InvalidNCNameException;
import de.tudortmund.cs.bonxai.relaxng.parser.exceptions.MissingHrefException;
import de.tudortmund.cs.bonxai.relaxng.parser.exceptions.MultiplePatternException;
import de.tudortmund.cs.bonxai.relaxng.parser.exceptions.ParentGrammarIsNullException;
import de.tudortmund.cs.bonxai.relaxng.parser.exceptions.ReturnObjectIsNullException;
import de.tudortmund.cs.bonxai.relaxng.parser.exceptions.UnsupportedContentException;
import de.tudortmund.cs.bonxai.xsd.tools.NameChecker;

import java.util.Iterator;
import java.util.LinkedList;
import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * This processor handles the currentDefine elements of a Relax NG XSDSchema
 *
 * @author Lars Schmidt
 */
class DefineProcessor extends RNGProcessorBase {

    private Define define;
    private IncludeContent includeContent;

    DefineProcessor(RelaxNGSchema rngSchema, Grammar grammar) {
        super(rngSchema, grammar);
    }

    DefineProcessor(RelaxNGSchema rngSchema, Grammar grammar, IncludeContent includeContent) {
        super(rngSchema, grammar);
        this.includeContent = includeContent;
    }

    @Override
    protected Object processNode(Node node) throws Exception {

        /**
         * Attributes:
         * -----------
         * name NCName
         * combine: method (choice|interleave)
         */
        String defineName = null;
        CombineMethod combineMethod = null;
        NamedNodeMap attributes = node.getAttributes();
        if (attributes != null) {
            if (attributes.getNamedItem("name") != null) {
                defineName = ((Attr) attributes.getNamedItem("name")).getValue();
                if (!NameChecker.isNCName(defineName)) {
                    throw new InvalidNCNameException(defineName, "define");
                }
            }
        }

        if (defineName != null) {
            this.define = new Define(defineName);
            visitChildren(node);
            if (this.includeContent != null) {
                this.includeContent.addDefinePattern(define);
            } else {
                this.grammar.addDefinePattern(define);
            }
        } else {
            throw new InvalidNCNameException(defineName, "define");
        }

        if (attributes != null) {
            if (attributes.getNamedItem("combine") != null) {
                if (((Attr) attributes.getNamedItem("combine")).getValue().equals("choice")) {
                    combineMethod = CombineMethod.choice;
                }
                if (((Attr) attributes.getNamedItem("combine")).getValue().equals("interleave")) {
                    combineMethod = CombineMethod.interleave;
                }
                if (combineMethod == null) {
                    throw new InvalidCombineMethodException("define");
                } else {

                    if (!this.grammar.getDefineLookUpTable().getAllReferencedObjects().isEmpty()) {
                        LinkedList<Define> defineList = this.grammar.getDefinedPatternsFromLookUpTable(defineName);
                        for (Iterator<Define> it = defineList.iterator(); it.hasNext();) {
                            Define currentDefine = it.next();
                            if (currentDefine.getCombineMethod() != null && !currentDefine.getCombineMethod().equals(combineMethod)) {
                                throw new CombineMethodNotMatchingException(defineName);
                            }
                        }
                    }


                    this.define.setCombineMethod(combineMethod);
                }
            }
        }

        if (this.define != null) {
            return this.define;
        } else {
            throw new ReturnObjectIsNullException("define");
        }

    }

    @Override
    protected void processChild(Node childNode) throws Exception {

        /**
         * Children:
         * ---------
         * Simple XML Syntax:
         *
         *      (
         *          element
         *      )
         *
         * Full XML Syntax:
         *
         * (
         *      element|
         *      attribute|
         *      group|
         *      interleave|
         *      choice|
         *      optional|
         *      zeroOrMore|
         *      oneOrMore|
         *      list|
         *      mixed|
         *      ref|
         *      parentRef|
         *      empty|
         *      text|
         *      value|
         *      data|
         *      notAllowed|
         *      externalRef|
         *      grammar
         * )+
         *
         */
        String nodeName = childNode.getNodeName();
        if (nodeName.contains(":")) {
            nodeName = nodeName.split(":")[1];
        }
        if (childNode != null && !"#text".equals(nodeName) && !"#comment".equals(nodeName) && !"#document".equals(nodeName) && !"#cdata-section".equals(nodeName)) {
            if (valueIsValid(nodeName)) {
                switch (CASE.valueOf(nodeName)) {
                    case element:
                        if (getDebug()) {
                            System.out.println("define :element");
                        }
                        ElementProcessor elementProcessor = new ElementProcessor(rngSchema, this.grammar);
                        this.define.addPattern(elementProcessor.processNode(childNode));
                        break;
                    case empty:
                        if (getDebug()) {
                            System.out.println("define (\"" + this.define.getName() + "\"):empty");
                        }
                        for (Pattern currentPattern : define.getPatterns()) {
                            if (currentPattern instanceof Empty) {
                                throw new MultiplePatternException("define with more than one <empty/>");
                            }
                        }
                        Empty empty = new Empty();
                        this.setPatternAttributes(empty, childNode);
                        this.define.addPattern(empty);
                        break;
                    case text:
                        if (getDebug()) {
                            System.out.println("define (\"" + this.define.getName() + "\"):text");
                        }
                        Text text = new Text();
                        this.setPatternAttributes(text, childNode);
                        this.define.addPattern(text);
                        break;
                    case data:
                        if (getDebug()) {
                            System.out.println("define (\"" + this.define.getName() + "\"):data");
                        }
                        DataProcessor dataProcessor = new DataProcessor(rngSchema, this.grammar);
                        this.define.addPattern(dataProcessor.processNode(childNode));
                        break;
                    case value:
                        if (getDebug()) {
                            System.out.println("define (\"" + this.define.getName() + "\"):value");
                        }
                        ValueProcessor valueProcessor = new ValueProcessor(rngSchema, this.grammar);
                        this.define.addPattern(valueProcessor.processNode(childNode));
                        break;
                    case list:
                        if (getDebug()) {
                            System.out.println("define (\"" + this.define.getName() + "\"):list");
                        }
                        ListProcessor listProcessor = new ListProcessor(rngSchema, grammar);
                        this.define.addPattern(listProcessor.processNode(childNode));
                        break;
                    case attribute:
                        if (getDebug()) {
                            System.out.println("define (\"" + this.define.getName() + "\"):attribute");
                        }
                        AttributeProcessor attributeProcessor = new AttributeProcessor(rngSchema, this.grammar);
                        this.define.addPattern(attributeProcessor.processNode(childNode));
                        break;
                    case optional:
                        if (getDebug()) {
                            System.out.println("define (\"" + this.define.getName() + "\"):optional");
                        }
                        OptionalProcessor optionalProcessor = new OptionalProcessor(rngSchema, this.grammar);
                        this.define.addPattern(optionalProcessor.processNode(childNode));
                        break;
                    case mixed:
                        if (getDebug()) {
                            System.out.println("define (\"" + this.define.getName() + "\"):mixed");
                        }
                        MixedProcessor mixedProcessor = new MixedProcessor(rngSchema, this.grammar);
                        this.define.addPattern(mixedProcessor.processNode(childNode));
                        break;
                    case zeroOrMore:
                        if (getDebug()) {
                            System.out.println("define (\"" + this.define.getName() + "\"):zeroOrMore");
                        }
                        ZeroOrMoreProcessor zeroOrMoreProcessor = new ZeroOrMoreProcessor(rngSchema, grammar);
                        this.define.addPattern(zeroOrMoreProcessor.processNode(childNode));
                        break;
                    case externalRef:
                        if (getDebug()) {
                            System.out.println("define (\"" + this.define.getName() + "\"):externalRef");
                        }
                        NamedNodeMap externalRefAttributes = childNode.getAttributes();
                        String externalRefName = null;
                        if (externalRefAttributes != null && externalRefAttributes.getNamedItem("href") != null) {
                            externalRefName = ((Attr) externalRefAttributes.getNamedItem("href")).getValue();
                            if (!NameChecker.isAnyUri(externalRefName)) {
                                throw new InvalidAnyUriException(externalRefName, "externalRef under define");
                            }
                        }
                        if (externalRefName == null) {
                            throw new MissingHrefException(nodeName);
                        }
                        ExternalRef externalRef = new ExternalRef(externalRefName);
                        this.setPatternAttributes(externalRef, childNode);
                        this.define.addPattern(externalRef);
                        break;
                    case notAllowed:
                        if (getDebug()) {
                            System.out.println("define (\"" + this.define.getName() + "\"):notAllowed");
                        }
                        NotAllowed notAllowed = new NotAllowed();
                        this.setPatternAttributes(notAllowed, childNode);
                        this.define.addPattern(notAllowed);
                        break;
                    case grammar:
                        if (getDebug()) {
                            System.out.println("define (\"" + this.define.getName() + "\"):grammar");
                        }
                        GrammarProcessor grammarProcessor = new GrammarProcessor(rngSchema, grammar);
                        this.define.addPattern(grammarProcessor.processNode(childNode));
                        break;
                    case ref:
                        if (getDebug()) {
                            System.out.print("define (\"" + this.define.getName() + "\"):ref");
                        }
                        NamedNodeMap attributes = childNode.getAttributes();
                        if (attributes != null && attributes.getNamedItem("name") != null) {
                            String refName = ((Attr) attributes.getNamedItem("name")).getValue();

                            if (!NameChecker.isNCName(refName)) {
                                throw new InvalidNCNameException(refName, "ref under define");
                            }

                            if (getDebug()) {
                                System.out.print(", name: " + refName + "\n");
                            }

                            if (this.grammar != null) {
                                this.grammar.registerDefinePatternInLookUpTable(new Define(refName));
                                Ref patternRef = new Ref(this.grammar.getDefineLookUpTable().getReference(refName), this.grammar);
                                patternRef.setRefName(refName);
                                this.setPatternAttributes(patternRef, childNode);
                                this.define.addPattern(patternRef);
                            } else {
                                throw new GrammarIsNullException(refName);
                            }
                        }
                        break;
                    case parentRef:
                        if (getDebug()) {
                            System.out.print("define (\"" + this.define.getName() + "\"):parentRef");
                        }
                        NamedNodeMap attributesParentRef = childNode.getAttributes();
                        if (attributesParentRef != null && attributesParentRef.getNamedItem("name") != null) {
                            String parentRefName = ((Attr) attributesParentRef.getNamedItem("name")).getValue();

                            if (!NameChecker.isNCName(parentRefName)) {
                                throw new InvalidNCNameException(parentRefName, "parentRef under define");
                            }

                            if (getDebug()) {
                                System.out.print(", name: " + parentRefName + "\n");
                            }

                            if (this.grammar.getParentGrammar() != null) {
                                this.grammar.getParentGrammar().registerDefinePatternInLookUpTable(new Define(parentRefName));
                            } else {
                                throw new ParentGrammarIsNullException("define (\"" + this.define.getName() + "\"):parentRef (\"" + parentRefName + "\")");
                            }

                            ParentRef parentRefPattern = new ParentRef(this.grammar.getParentGrammar().getDefineLookUpTable().getReference(parentRefName), this.grammar, this.grammar.getParentGrammar());
                            parentRefPattern.setRefName(parentRefName);
                            this.setPatternAttributes(parentRefPattern, childNode);
                            this.define.addPattern(parentRefPattern);
                        }
                        break;
                    case oneOrMore:
                        if (getDebug()) {
                            System.out.println("define (\"" + this.define.getName() + "\"):oneOrMore");
                        }
                        OneOrMoreProcessor oneOrMoreProcessor = new OneOrMoreProcessor(rngSchema, grammar);
                        this.define.addPattern(oneOrMoreProcessor.processNode(childNode));
                        break;
                    case choice:
                        if (getDebug()) {
                            System.out.println("define (\"" + this.define.getName() + "\"):choice");
                        }
                        // choice as pattern
                        ChoiceProcessor choiceProcessor = new ChoiceProcessor(rngSchema, this.grammar);
                        this.define.addPattern(choiceProcessor.processNode(childNode));
                        break;
                    case group:
                        if (getDebug()) {
                            System.out.println("define (\"" + this.define.getName() + "\"):group");
                        }
                        GroupProcessor groupProcessor = new GroupProcessor(rngSchema, grammar);
                        this.define.addPattern(groupProcessor.processNode(childNode));
                        break;
                    case interleave:
                        if (getDebug()) {
                            System.out.println("define (\"" + this.define.getName() + "\"):interleave");
                        }
                        InterleaveProcessor interleaveProcessor = new InterleaveProcessor(rngSchema, grammar);
                        this.define.addPattern(interleaveProcessor.processNode(childNode));
                        break;
                    default:
                        throw new UnsupportedContentException(nodeName, "define");
                }
            }
        }
    }
}
