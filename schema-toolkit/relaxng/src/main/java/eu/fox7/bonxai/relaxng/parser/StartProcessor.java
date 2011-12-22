package eu.fox7.bonxai.relaxng.parser;

import eu.fox7.bonxai.relaxng.CombineMethod;
import eu.fox7.bonxai.relaxng.Grammar;
import eu.fox7.bonxai.relaxng.Pattern;
import eu.fox7.bonxai.relaxng.RelaxNGSchema;
import eu.fox7.bonxai.relaxng.parser.exceptions.CombineMethodNotMatchingException;
import eu.fox7.bonxai.relaxng.parser.exceptions.GrammarIsNullException;
import eu.fox7.bonxai.relaxng.parser.exceptions.InvalidCombineMethodException;
import eu.fox7.bonxai.relaxng.parser.exceptions.MultiplePatternException;
import eu.fox7.bonxai.relaxng.parser.exceptions.PatternNotInitializedException;
import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * This processor handles the start elements of a Relax NG XSDSchema
 *
 * @author Lars Schmidt
 */
class StartProcessor extends RNGPatternProcessorBase {

    Pattern pattern;

    StartProcessor(RelaxNGSchema rngSchema, Grammar grammar) {
        super(rngSchema, grammar);
    }

    @Override
    protected Pattern processNode(Node node) throws Exception {
        /**
         * Attributes:
         * -----------
         */
        if (this.grammar == null) {
            throw new GrammarIsNullException("start");
        }

        CombineMethod combineMethod = null;
        NamedNodeMap attributes = node.getAttributes();
        if (attributes != null) {
            if (attributes.getNamedItem("combine") != null) {
                if (((Attr) attributes.getNamedItem("combine")).getValue().equals("choice")) {
                    combineMethod = CombineMethod.choice;
                }
                if (((Attr) attributes.getNamedItem("combine")).getValue().equals("interleave")) {
                    combineMethod = CombineMethod.interleave;
                }
                if (combineMethod == null) {
                    throw new InvalidCombineMethodException("start");
                } else {
                    if (this.grammar.getStartCombineMethod() != null) {
                        if (!this.grammar.getStartCombineMethod().equals(combineMethod)) {
                            throw new CombineMethodNotMatchingException("start: " + this.grammar.getStartCombineMethod().name() + " <-> " + combineMethod.name());
                        }
                    }
                    this.grammar.setStartCombineMethod(combineMethod);
                }
            }
        }

        visitChildren(node);
        if (this.pattern != null) {
            return this.pattern;
        } else {
            throw new PatternNotInitializedException("pattern", "start");
        }
    }

	@Override
	protected void addPattern(Pattern pattern) throws MultiplePatternException {
		if (this.pattern == null)
			this.pattern = pattern;
		else
			throw new MultiplePatternException("StartProcessor");
	}


}
