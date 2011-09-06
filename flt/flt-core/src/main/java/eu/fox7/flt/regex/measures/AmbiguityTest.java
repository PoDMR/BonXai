/**
 * Created on Mar 26, 2009
 * Modified on $Date: 2010-02-02 16:00:28 $
 */
package eu.fox7.flt.regex.measures;

import eu.fox7.flt.automata.NFAException;
import eu.fox7.flt.automata.factories.sparse.GlushkovFactory;
import eu.fox7.flt.automata.impl.sparse.StateNFA;
import eu.fox7.flt.regex.RegexException;
import eu.fox7.util.tree.SExpressionParseException;

/**
 * @author lucg5005
 * @version $Revision: 1.2 $
 *
 */
public class AmbiguityTest implements LanguageTest {

	protected GlushkovFactory glushkov = new GlushkovFactory();
    @SuppressWarnings("unchecked")
    protected eu.fox7.flt.automata.measures.LanguageTest test = new eu.fox7.flt.automata.measures.AmbiguityTest();

    public AmbiguityTest() {
    	super();
    }

    public AmbiguityTest(GlushkovFactory glushkov) {
    	this();
    	this.glushkov = glushkov;
    }

    public boolean test(String regexStr)
	        throws SExpressionParseException, RegexException, NFAException {
		StateNFA nfa = glushkov.create(regexStr);
		return test.test(nfa);
	}

}
