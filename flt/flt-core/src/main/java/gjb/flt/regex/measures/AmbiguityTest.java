/**
 * Created on Mar 26, 2009
 * Modified on $Date: 2010-02-02 16:00:28 $
 */
package gjb.flt.regex.measures;

import gjb.flt.automata.NFAException;
import gjb.flt.automata.factories.sparse.GlushkovFactory;
import gjb.flt.automata.impl.sparse.StateNFA;
import gjb.flt.regex.RegexException;
import gjb.util.tree.SExpressionParseException;

/**
 * @author lucg5005
 * @version $Revision: 1.2 $
 *
 */
public class AmbiguityTest implements LanguageTest {

	protected GlushkovFactory glushkov = new GlushkovFactory();
    @SuppressWarnings("unchecked")
    protected gjb.flt.automata.measures.LanguageTest test = new gjb.flt.automata.measures.AmbiguityTest();

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
