package eu.fox7.bonxai.relaxng.parser;

import eu.fox7.bonxai.relaxng.Grammar;
import eu.fox7.bonxai.relaxng.Pattern;
import eu.fox7.bonxai.relaxng.RelaxNGSchema;
import eu.fox7.bonxai.relaxng.parser.exceptions.EmptyPatternException;
import java.util.LinkedList;
import org.w3c.dom.Node;

/**
 * This processor handles the except elements of a Relax NG XSDSchema
 *
 * @author Lars Schmidt
 */
class ExceptProcessor extends RNGPatternProcessorBase {

    private LinkedList<Pattern> patterns = new LinkedList<Pattern>();

    public ExceptProcessor(RelaxNGSchema rngSchema, Grammar grammar) {
        super(rngSchema, grammar);
    }

    @Override
    protected LinkedList<Pattern> processNode(Node node) throws Exception {

        /**
         * Attributes:
         * -----------
         */
        visitChildren(node);

        if (!this.patterns.isEmpty()) {
            return this.patterns;
        } else {
            throw new EmptyPatternException("except");
        }

    }

	@Override
	protected void addPattern(Pattern pattern) {
		this.patterns.add(pattern);
	}
}
