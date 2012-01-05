package eu.fox7.schematoolkit.relaxng.parser;

import eu.fox7.schematoolkit.relaxng.om.Choice;
import eu.fox7.schematoolkit.relaxng.om.Define;
import eu.fox7.schematoolkit.relaxng.om.Empty;
import eu.fox7.schematoolkit.relaxng.om.ExternalRef;
import eu.fox7.schematoolkit.relaxng.om.Grammar;
import eu.fox7.schematoolkit.relaxng.om.NotAllowed;
import eu.fox7.schematoolkit.relaxng.om.ParentRef;
import eu.fox7.schematoolkit.relaxng.om.Pattern;
import eu.fox7.schematoolkit.relaxng.om.Ref;
import eu.fox7.schematoolkit.relaxng.om.RelaxNGSchema;
import eu.fox7.schematoolkit.relaxng.om.Text;
import eu.fox7.schematoolkit.relaxng.parser.exceptions.EmptyPatternException;
import eu.fox7.schematoolkit.relaxng.parser.exceptions.GrammarIsNullException;
import eu.fox7.schematoolkit.relaxng.parser.exceptions.InvalidAnyUriException;
import eu.fox7.schematoolkit.relaxng.parser.exceptions.InvalidNCNameException;
import eu.fox7.schematoolkit.relaxng.parser.exceptions.MissingHrefException;
import eu.fox7.schematoolkit.relaxng.parser.exceptions.MultiplePatternException;
import eu.fox7.schematoolkit.relaxng.parser.exceptions.ParentGrammarIsNullException;
import eu.fox7.schematoolkit.relaxng.parser.exceptions.UnsupportedContentException;
import eu.fox7.schematoolkit.xsd.tools.NameChecker;

import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * This processor handles the choice elements of a Relax NG XSDSchema
 *
 * @author Lars Schmidt
 */
class ChoiceProcessor extends RNGPatternProcessorBase {

    private Choice choice;

    public ChoiceProcessor(RelaxNGSchema rngSchema, Grammar grammar) {
        super(rngSchema, grammar);
    }

    @Override
    protected Choice processNode(Node node) throws Exception {

        /**
         * Attributes:
         * -----------
         */
        this.choice = new Choice();
        this.setPatternAttributes(choice, node);
        visitChildren(node);

        if (this.choice.getPatterns().size() < 1) {
            throw new EmptyPatternException("\"choice\" has not enough pattern");
        }

        return this.choice;
    }

	@Override
	protected void addPattern(Pattern pattern) {
		this.choice.addPattern(pattern);
	}

    
}
