package eu.fox7.bonxai.relaxng.parser;

import eu.fox7.bonxai.relaxng.Grammar;
import eu.fox7.bonxai.relaxng.OneOrMore;
import eu.fox7.bonxai.relaxng.Pattern;
import eu.fox7.bonxai.relaxng.RelaxNGSchema;
import eu.fox7.bonxai.relaxng.parser.exceptions.EmptyPatternException;
import org.w3c.dom.Node;

/**
 * This processor handles the oneOrMore elements of a Relax NG XSDSchema
 *
 * @author Lars Schmidt
 */
class OneOrMoreProcessor extends RNGPatternProcessorBase {

    private OneOrMore oneOrMore;

    public OneOrMoreProcessor(RelaxNGSchema rngSchema, Grammar grammar) {
        super(rngSchema, grammar);
    }

    @Override
    protected OneOrMore processNode(Node node) throws Exception {

        /**
         * Attributes:
         * -----------
         */
        this.oneOrMore = new OneOrMore();
        this.setPatternAttributes(oneOrMore, node);
        visitChildren(node);

        if (this.oneOrMore.getPatterns().size() < 1) {
            throw new EmptyPatternException("oneOrMore has not enough pattern");
        }

        return this.oneOrMore;

    }

	@Override
	protected void addPattern(Pattern pattern) {
		this.oneOrMore.addPattern(pattern);
	}
}
