package eu.fox7.bonxai.relaxng.parser;

import eu.fox7.bonxai.relaxng.Grammar;
import eu.fox7.bonxai.relaxng.Mixed;
import eu.fox7.bonxai.relaxng.Pattern;
import eu.fox7.bonxai.relaxng.RelaxNGSchema;
import eu.fox7.bonxai.relaxng.parser.exceptions.EmptyPatternException;
import org.w3c.dom.Node;

/**
 * This processor handles the mixed elements of a Relax NG XSDSchema
 *
 * @author Lars Schmidt
 */
class MixedProcessor extends RNGPatternProcessorBase {

    private Mixed mixed;

    public MixedProcessor(RelaxNGSchema rngSchema, Grammar grammar) {
        super(rngSchema, grammar);
    }

    @Override
    protected Mixed processNode(Node node) throws Exception {

        /**
         * Attributes:
         * -----------
         */
        this.mixed = new Mixed();
        this.setPatternAttributes(mixed, node);
        visitChildren(node);

        if (this.mixed.getPatterns().size() < 1) {
            throw new EmptyPatternException("\"mixed\" has not enough pattern");
        }

        return this.mixed;
    }

	@Override
	protected void addPattern(Pattern pattern) {
		this.mixed.addPattern(pattern);
	}

}
