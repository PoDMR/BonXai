package eu.fox7.bonxai.relaxng.parser;

import eu.fox7.bonxai.relaxng.Grammar;
import eu.fox7.bonxai.relaxng.Group;
import eu.fox7.bonxai.relaxng.Pattern;
import eu.fox7.bonxai.relaxng.RelaxNGSchema;
import eu.fox7.bonxai.relaxng.parser.exceptions.EmptyPatternException;
import org.w3c.dom.Node;

/**
 * This processor handles the group elements of a Relax NG XSDSchema
 *
 * @author Lars Schmidt
 */
class GroupProcessor extends RNGPatternProcessorBase {

    Group group;

    GroupProcessor(RelaxNGSchema rngSchema, Grammar grammar) {
        super(rngSchema, grammar);
    }

    @Override
    protected Group processNode(Node node) throws Exception {

        /**
         * Attributes:
         * -----------
         */
        this.group = new Group();
        this.setPatternAttributes(group, node);
        visitChildren(node);

        if (this.group.getPatterns().size() < 1) {
            throw new EmptyPatternException("group has not enough pattern");
        }
        return this.group;
    }

	@Override
	protected void addPattern(Pattern pattern) {
		this.group.addPattern(pattern);
	}
}
