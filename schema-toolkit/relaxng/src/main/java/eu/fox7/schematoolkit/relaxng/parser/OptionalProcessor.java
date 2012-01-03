package eu.fox7.schematoolkit.relaxng.parser;

import eu.fox7.schematoolkit.relaxng.om.Grammar;
import eu.fox7.schematoolkit.relaxng.om.Optional;
import eu.fox7.schematoolkit.relaxng.om.Pattern;
import eu.fox7.schematoolkit.relaxng.om.RelaxNGSchema;
import eu.fox7.schematoolkit.relaxng.parser.exceptions.EmptyPatternException;

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
