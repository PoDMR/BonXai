package eu.fox7.schematoolkit.relaxng.parser;

import eu.fox7.schematoolkit.relaxng.om.Grammar;
import eu.fox7.schematoolkit.relaxng.om.List;
import eu.fox7.schematoolkit.relaxng.om.Pattern;
import eu.fox7.schematoolkit.relaxng.om.RelaxNGSchema;
import eu.fox7.schematoolkit.relaxng.parser.exceptions.EmptyPatternException;

import org.w3c.dom.Node;

/**
 * This processor handles the list elements of a Relax NG XSDSchema
 *
 * @author Lars Schmidt
 */
class ListProcessor extends RNGPatternProcessorBase {

    private List list;

    public ListProcessor(RelaxNGSchema rngSchema, Grammar grammar) {
        super(rngSchema, grammar);
    }

    @Override
    protected List processNode(Node node) throws Exception {

        /**
         * Attributes:
         * -----------
         */
        this.list = new List();
        this.setPatternAttributes(list, node);
        visitChildren(node);

        if (this.list.getPatterns().size() < 1) {
            throw new EmptyPatternException("list has not enough pattern");
        }
        return this.list;
    }

	@Override
	protected void addPattern(Pattern pattern) {
		this.list.addPattern(pattern);
	}

}
