/**
 * Created on Oct 27, 2009
 * Modified on $Date: 2009-10-27 09:30:13 $
 */
package eu.fox7.flt.regex.infer.rwr;

import java.util.Properties;

import eu.fox7.flt.automata.factories.sparse.SOAFactory;
import eu.fox7.flt.automata.impl.sparse.SparseNFA;
import eu.fox7.flt.regex.Regex;
import eu.fox7.flt.regex.UnknownOperatorException;
import eu.fox7.flt.regex.infer.Inferrer;
import eu.fox7.flt.regex.infer.rwr.impl.Automaton;
import eu.fox7.flt.regex.infer.rwr.impl.GraphAutomatonFactory;
import eu.fox7.util.tree.SExpressionParseException;


/**
 * @author lucg5005
 * @version $Revision: 1.1 $
 *
 */
public class RWRInferrer implements Inferrer {

	protected SOAFactory soaFactory;
	protected RewriteEngine engine;
	protected GraphAutomatonFactory gaFactory;
	protected int numberOfExamples = 0;
	protected Properties properties;

	public RWRInferrer(RewriteEngine engine) {
		super();
		this.engine = engine;
		this.soaFactory = new SOAFactory();
		this.gaFactory = new GraphAutomatonFactory();
	}

	public RWRInferrer(RewriteEngine engine, Properties properties) {
		this(engine);
		this.properties = properties;
	}

	@Override
	public void addExample(String[] example) {
		soaFactory.add(example);
		numberOfExamples++;
	}

	@Override
	public int getNumberOfExamples() {
		return numberOfExamples;
	}

	@Override
	public String infer() throws NoOpportunityFoundException {
		SparseNFA soa = soaFactory.getAutomaton();
		Automaton automaton = gaFactory.create(soa);
		return engine.rewriteToRegex(automaton);
	}

	@Override
	public Regex inferRegex() throws NoOpportunityFoundException {
		try {
	        return new Regex(infer(), this.properties);
        } catch (SExpressionParseException e) {
        	throw new RuntimeException(e);
        } catch (UnknownOperatorException e) {
        	throw new RuntimeException(e);
        }
	}

}
