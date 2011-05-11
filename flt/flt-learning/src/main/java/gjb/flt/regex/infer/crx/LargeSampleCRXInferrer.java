/**
 * Created on Oct 26, 2009
 * Modified on $Date: 2009-10-27 09:30:13 $
 */
package gjb.flt.regex.infer.crx;

import gjb.flt.regex.infer.crx.impl.AbstractCRXInferrer;
import gjb.flt.regex.infer.crx.impl.LargeSampleElement;
import gjb.flt.regex.infer.crx.impl.Multiplicity;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lucg5005
 * @version $Revision: 1.3 $
 *
 */
public class LargeSampleCRXInferrer extends AbstractCRXInferrer<LargeSampleElement> {

    protected Map<String,Multiplicity> multiplicityMap;

    public LargeSampleCRXInferrer() {
		element = new LargeSampleElement();
		multiplicityMap = new HashMap<String,Multiplicity>();
    }

    @Override
    public void addExample(String[] example) {
    	super.addExample(example);
    	computeMultiplicity(example);
    }

    protected void computeMultiplicity(String[] example) {
		Map<String,Integer> counter = new HashMap<String,Integer>();
		for (int i = 0; i < example.length; i++) {
			if (!counter.containsKey(example[i]))
				counter.put(example[i], 0);
			counter.put(example[i], counter.get(example[i]) + 1);
		}
		for (String symbol : counter.keySet()) {
			if (!multiplicityMap.containsKey(symbol))
				multiplicityMap.put(symbol, new Multiplicity());
			Multiplicity multiplicity = multiplicityMap.get(symbol);
			multiplicity.update(counter.get(symbol));
			multiplicity.updateFinalMin(numberOfExamples);
		}
	}

    @Override
    public String infer() {
    	element.setMultiplicities(multiplicityMap);
    	return element.getRegex();
    }

}
