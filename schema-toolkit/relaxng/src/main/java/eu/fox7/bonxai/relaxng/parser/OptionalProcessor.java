package eu.fox7.bonxai.relaxng.parser;

import eu.fox7.bonxai.relaxng.Grammar;
import eu.fox7.bonxai.relaxng.Optional;
import eu.fox7.bonxai.relaxng.Pattern;
import eu.fox7.bonxai.relaxng.RelaxNGSchema;
import eu.fox7.bonxai.relaxng.parser.exceptions.EmptyPatternException;
import org.w3c.dom.Node;

/**
 * This processor handles the optional elements of a Relax NG XSDSchema
 *
 * @author Lars Schmidt
 */
class OptionalProcessor extends RNGPatternProcessorBase {

    private Optional optional;

    public OptionalProcessor(RelaxNGSchema rngSchema, Grammar grammar) {
        super(rngSchema, grammar);
    }

    @Override
    protected Optional processNode(Node node) throws Exception {

        /**
         * Attributes:
         * -----------
         */
        this.optional = new Optional();
        this.setPatternAttributes(optional, node);
        visitChildren(node);

        if (this.optional.getPatterns().size() < 1) {
            throw new EmptyPatternException("\"optional\" has not enough pattern");
        }

        return this.optional;
    }

	@Override
	protected void addPattern(Pattern pattern) {
		this.optional.addPattern(pattern);
	}
    
}
