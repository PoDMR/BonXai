/**
 * Created on Oct 26, 2009
 * Modified on $Date: 2009-10-27 09:30:13 $
 */
package eu.fox7.flt.regex.infer.crx.impl;

import java.util.Properties;

import eu.fox7.flt.regex.Regex;
import eu.fox7.flt.regex.UnknownOperatorException;
import eu.fox7.flt.regex.infer.Inferrer;
import eu.fox7.util.tree.SExpressionParseException;


/**
 * @author lucg5005
 * @version $Revision: 1.1 $
 *
 */
public abstract class AbstractCRXInferrer<ElemT extends StructureElement> implements Inferrer {

    protected ElemT element;
    protected int numberOfExamples = 0;
    protected Properties properties;

    public AbstractCRXInferrer() {
    	this(null);
    }

    public AbstractCRXInferrer(Properties properties) {
    	this.properties = properties;
    }

    abstract public String infer();

    @Override
    public void addExample(String[] contentItems) {
		Example example = new Example();
		for (String contentItem : contentItems)
			example.addContentItem(contentItem);
		element.addExample(example);
    }

    @Override
	public int getNumberOfExamples() {
    	return numberOfExamples;
    }

    @Override
	public Regex inferRegex() {
		try {
	        return new Regex(infer(), this.properties);
        } catch (SExpressionParseException e) {
        	throw new RuntimeException(e);
        } catch (UnknownOperatorException e) {
        	throw new RuntimeException(e);
        }
	}

}
