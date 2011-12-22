package eu.fox7.bonxai.relaxng.parser;

import eu.fox7.bonxai.relaxng.Grammar;
import eu.fox7.bonxai.relaxng.Interleave;
import eu.fox7.bonxai.relaxng.Pattern;
import eu.fox7.bonxai.relaxng.RelaxNGSchema;
import eu.fox7.bonxai.relaxng.parser.exceptions.EmptyPatternException;
import org.w3c.dom.Node;

/**
 * This processor handles the interleave elements of a Relax NG XSDSchema
 *
 * @author Lars Schmidt
 */
class InterleaveProcessor extends RNGPatternProcessorBase {

    private Interleave interleave;

    public InterleaveProcessor(RelaxNGSchema rngSchema, Grammar grammar) {
        super(rngSchema, grammar);
    }

    @Override
    protected Interleave processNode(Node node) throws Exception {

        /**
         * Attributes:
         * -----------
         */
        this.interleave = new Interleave();
        this.setPatternAttributes(interleave, node);
        visitChildren(node);

        if (this.interleave.getPatterns().size() < 1) {
            throw new EmptyPatternException("interleave has not enough pattern");
        }

        return this.interleave;
    }

	@Override
	protected void addPattern(Pattern pattern) {
		this.interleave.addPattern(pattern);
	}
}
