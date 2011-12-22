package eu.fox7.bonxai.relaxng.parser;

import eu.fox7.bonxai.relaxng.CombineMethod;
import eu.fox7.bonxai.relaxng.Define;
import eu.fox7.bonxai.relaxng.Empty;
import eu.fox7.bonxai.relaxng.ExternalRef;
import eu.fox7.bonxai.relaxng.Grammar;
import eu.fox7.bonxai.relaxng.IncludeContent;
import eu.fox7.bonxai.relaxng.NotAllowed;
import eu.fox7.bonxai.relaxng.ParentRef;
import eu.fox7.bonxai.relaxng.Pattern;
import eu.fox7.bonxai.relaxng.Ref;
import eu.fox7.bonxai.relaxng.RelaxNGSchema;
import eu.fox7.bonxai.relaxng.Text;
import eu.fox7.bonxai.relaxng.parser.exceptions.CombineMethodNotMatchingException;
import eu.fox7.bonxai.relaxng.parser.exceptions.GrammarIsNullException;
import eu.fox7.bonxai.relaxng.parser.exceptions.InvalidAnyUriException;
import eu.fox7.bonxai.relaxng.parser.exceptions.InvalidCombineMethodException;
import eu.fox7.bonxai.relaxng.parser.exceptions.InvalidNCNameException;
import eu.fox7.bonxai.relaxng.parser.exceptions.MissingHrefException;
import eu.fox7.bonxai.relaxng.parser.exceptions.MultiplePatternException;
import eu.fox7.bonxai.relaxng.parser.exceptions.ParentGrammarIsNullException;
import eu.fox7.bonxai.relaxng.parser.exceptions.ReturnObjectIsNullException;
import eu.fox7.bonxai.relaxng.parser.exceptions.UnsupportedContentException;
import eu.fox7.bonxai.xsd.tools.NameChecker;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * This processor handles the currentDefine elements of a Relax NG XSDSchema
 *
 * @author Lars Schmidt
 */
class DefineProcessor extends RNGPatternProcessorBase {

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
                	List<Define> defineList = this.grammar.getDefinedPattern(defineName);
                	for (Iterator<Define> it = defineList.iterator(); it.hasNext();) {
                		Define currentDefine = it.next();
                		if (currentDefine.getCombineMethod() != null && !currentDefine.getCombineMethod().equals(combineMethod)) {
                			throw new CombineMethodNotMatchingException(defineName);
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
	protected void addPattern(Pattern pattern) {
		this.define.addPattern(pattern);
	}
}
