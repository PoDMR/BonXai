package eu.fox7.schematoolkit.relaxng.parser;

import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import eu.fox7.bonxai.xsd.tools.NameChecker;
import eu.fox7.schematoolkit.relaxng.om.Define;
import eu.fox7.schematoolkit.relaxng.om.Empty;
import eu.fox7.schematoolkit.relaxng.om.ExternalRef;
import eu.fox7.schematoolkit.relaxng.om.Grammar;
import eu.fox7.schematoolkit.relaxng.om.NotAllowed;
import eu.fox7.schematoolkit.relaxng.om.ParentRef;
import eu.fox7.schematoolkit.relaxng.om.Pattern;
import eu.fox7.schematoolkit.relaxng.om.Ref;
import eu.fox7.schematoolkit.relaxng.om.RelaxNGSchema;
import eu.fox7.schematoolkit.relaxng.om.Text;
import eu.fox7.schematoolkit.relaxng.parser.RNGProcessorBase.CASE;
import eu.fox7.schematoolkit.relaxng.parser.exceptions.GrammarIsNullException;
import eu.fox7.schematoolkit.relaxng.parser.exceptions.InvalidAnyUriException;
import eu.fox7.schematoolkit.relaxng.parser.exceptions.InvalidNCNameException;
import eu.fox7.schematoolkit.relaxng.parser.exceptions.MissingHrefException;
import eu.fox7.schematoolkit.relaxng.parser.exceptions.MultiplePatternException;
import eu.fox7.schematoolkit.relaxng.parser.exceptions.ParentGrammarIsNullException;
import eu.fox7.schematoolkit.relaxng.parser.exceptions.UnsupportedContentException;

public abstract class RNGPatternProcessorBase extends RNGProcessorBase {

	public RNGPatternProcessorBase(RelaxNGSchema rngSchema, Grammar grammar) {
		super(rngSchema, grammar);
	}

	public RNGPatternProcessorBase(RelaxNGSchema rngSchema) {
		super(rngSchema);
	}
	
    /**
     * Processes a single child node.
     * @param childNode     Child node which is processed for the parent node
     * @throws java.lang.Exception
     */
    protected void processChild(Node childNode) throws Exception {
        String nodeName = childNode.getNodeName();
        if (nodeName.contains(":")) {
            nodeName = nodeName.split(":")[1];
        }
        if (childNode != null && !"#text".equals(nodeName) && !"#comment".equals(nodeName) && !"#document".equals(nodeName) && !"#cdata-section".equals(nodeName)) {
            if (valueIsValid(nodeName)) {
                switch (CASE.valueOf(nodeName)) {
                    case empty:
                        Empty empty = new Empty();
                        this.setPatternAttributes(empty, childNode);
                        this.addPattern(empty);
                        break;
                    case text:
                        Text text = new Text();
                        this.setPatternAttributes(text, childNode);
                        this.addPattern(text);
                        break;
                    case data:
                        DataProcessor dataProcessor = new DataProcessor(rngSchema, this.grammar);
                        this.addPattern(dataProcessor.processNode(childNode));
                        break;
                    case value:
                        ValueProcessor valueProcessor = new ValueProcessor(rngSchema, this.grammar);
                        this.addPattern(valueProcessor.processNode(childNode));
                        break;
                    case list:
                        ListProcessor listProcessor = new ListProcessor(rngSchema, grammar);
                        this.addPattern(listProcessor.processNode(childNode));
                        break;
                    case attribute:
                        AttributeProcessor attributeProcessor = new AttributeProcessor(rngSchema, this.grammar);
                        this.addPattern(attributeProcessor.processNode(childNode));
                        break;
                    case element:
                        ElementProcessor elementProcessor = new ElementProcessor(rngSchema, this.grammar);
                        this.addPattern(elementProcessor.processNode(childNode));
                        break;
                    case optional:
                        OptionalProcessor optionalProcessor = new OptionalProcessor(rngSchema, this.grammar);
                        this.addPattern(optionalProcessor.processNode(childNode));
                        break;
                    case mixed:
                        MixedProcessor mixedProcessor = new MixedProcessor(rngSchema, this.grammar);
                        this.addPattern(mixedProcessor.processNode(childNode));
                        break;
                    case zeroOrMore:
                        ZeroOrMoreProcessor zeroOrMoreProcessor = new ZeroOrMoreProcessor(rngSchema, grammar);
                        this.addPattern(zeroOrMoreProcessor.processNode(childNode));
                        break;
                    case externalRef:
                        NamedNodeMap externalRefAttributes = childNode.getAttributes();
                        String externalRefName = null;
                        if (externalRefAttributes != null && externalRefAttributes.getNamedItem("href") != null) {
                            externalRefName = ((Attr) externalRefAttributes.getNamedItem("href")).getValue();
                            if (!NameChecker.isAnyUri(externalRefName)) {
                                throw new InvalidAnyUriException(externalRefName, "externalRef under optional");
                            }
                        }
                        if (externalRefName == null) {
                            throw new MissingHrefException(nodeName);
                        }
                        ExternalRef externalRef = new ExternalRef(externalRefName);
                        this.setPatternAttributes(externalRef, childNode);
                        this.addPattern(externalRef);
                        break;
                    case notAllowed:
                        NotAllowed notAllowed = new NotAllowed();
                        this.setPatternAttributes(notAllowed, childNode);
                        this.addPattern(notAllowed);
                        break;
                    case grammar:
                        GrammarProcessor grammarProcessor = new GrammarProcessor(rngSchema, grammar);
                        this.addPattern(grammarProcessor.processNode(childNode));
                        break;
                    case ref:
                        if (getDebug()) {
                            System.out.print("optional: ref");
                        }
                        NamedNodeMap attributes = childNode.getAttributes();
                        if (attributes != null && attributes.getNamedItem("name") != null) {
                            String refName = ((Attr) attributes.getNamedItem("name")).getValue();

                            if (!NameChecker.isNCName(refName)) {
                                throw new InvalidNCNameException(refName, "ref under optional");
                            }

                            Ref patternRef = new Ref(refName, this.grammar);
                            this.setPatternAttributes(patternRef, childNode);
                            this.addPattern(patternRef);
                        }
                        break;
                    case parentRef:
                        NamedNodeMap attributesParentRef = childNode.getAttributes();
                        if (attributesParentRef != null && attributesParentRef.getNamedItem("name") != null) {
                            String parentRefName = ((Attr) attributesParentRef.getNamedItem("name")).getValue();

                            if (!NameChecker.isNCName(parentRefName)) {
                                throw new InvalidNCNameException(parentRefName, "parentRef under optional");
                            }

                            if (this.grammar.getParentGrammar() == null)
                                throw new ParentGrammarIsNullException("optional: parentRef (\"" + parentRefName + "\")");

                            ParentRef parentRefPattern = new ParentRef(parentRefName, this.grammar);
                            this.setPatternAttributes(parentRefPattern, childNode);
                            this.addPattern(parentRefPattern);
                        }
                        break;
                    case oneOrMore:
                        OneOrMoreProcessor oneOrMoreProcessor = new OneOrMoreProcessor(rngSchema, grammar);
                        this.addPattern(oneOrMoreProcessor.processNode(childNode));
                        break;
                    case choice:
                        // choice as pattern
                        ChoiceProcessor choiceProcessor = new ChoiceProcessor(rngSchema, this.grammar);
                        this.addPattern(choiceProcessor.processNode(childNode));
                        break;
                    case group:
                        GroupProcessor groupProcessor = new GroupProcessor(rngSchema, grammar);
                        this.addPattern(groupProcessor.processNode(childNode));
                        break;
                    case interleave:
                        InterleaveProcessor interleaveProcessor = new InterleaveProcessor(rngSchema, grammar);
                        this.addPattern(interleaveProcessor.processNode(childNode));
                        break;
                    default:
                    throw new UnsupportedContentException(nodeName, "RNGProcessorBase");
                }
            }
        }
    }
    
    protected abstract void addPattern(Pattern pattern) throws MultiplePatternException;
}
