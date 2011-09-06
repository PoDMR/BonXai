/**
 * Created on Oct 26, 2009
 * Modified on $Date: 2009-10-27 09:30:13 $
 */
package eu.fox7.flt.regex.infer.crx;

import eu.fox7.flt.regex.infer.crx.impl.AbstractCRXInferrer;
import eu.fox7.flt.regex.infer.crx.impl.SmallSampleElement;

/**
 * @author lucg5005
 * @version $Revision: 1.3 $
 *
 */
public class SmallSampleCRXInferrer extends AbstractCRXInferrer<SmallSampleElement> {

	public SmallSampleCRXInferrer() {
		element = new SmallSampleElement();
	}

	@Override
    public String infer() {
	    return element.getRegex();
    }

}
