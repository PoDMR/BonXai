package eu.fox7.bonxai.relaxng.parser;

import eu.fox7.bonxai.relaxng.Grammar;
import eu.fox7.bonxai.relaxng.Pattern;
import eu.fox7.bonxai.relaxng.RelaxNGSchema;
import eu.fox7.bonxai.relaxng.ZeroOrMore;
import eu.fox7.bonxai.relaxng.parser.exceptions.EmptyPatternException;
import org.w3c.dom.Node;

/**
 * This processor handles the zeroOrMore elements of a Relax NG XSDSchema
 *
 * @author Lars Schmidt
 */
class ZeroOrMoreProcessor extends RNGPatternProcessorBase {

    private ZeroOrMore zeroOrMore;

    public ZeroOrMoreProcessor(RelaxNGSchema rngSchema, Grammar grammar) {
        super(rngSchema, grammar);
    }

    @Override
    protected ZeroOrMore processNode(Node node) throws Exception {

        /**
         * Attributes:
         * -----------
         */
        this.zeroOrMore = new ZeroOrMore();
        this.setPatternAttributes(zeroOrMore, node);
        visitChildren(node);

        if (this.zeroOrMore.getPatterns().size() < 1) {
            throw new EmptyPatternException("\"zeroOrMore\" has not enough pattern");
        }

        return this.zeroOrMore;
    }

	@Override
	protected void addPattern(Pattern pattern) {
		this.zeroOrMore.addPattern(pattern);
	}
}