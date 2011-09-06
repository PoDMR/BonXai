/*
 * Created on May 15, 2005
 * Modified on $Date: 2009-10-27 09:30:13 $
 */
package eu.fox7.flt.regex.infer.crx.impl;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * <p>SmallSampleElement extends LargeSampleElement by keeping track of the set of
 * examples IDs for the element.  Note that this consumes more memory, but yields
 * better precision in return.</p>
 * @author eu.fox7
 * @version $Revision: 1.1 $
 */
public class SmallSampleElement extends LargeSampleElement
        implements ParsingElement {

    /**
     * Map that associates the child name with the Set of example number it occurs
     * in
     */
    protected Map<String,Set<Integer>> exampleSetsMap = new HashMap<String,Set<Integer>>();

   /**
     * Method that adds an Example for the element, it updates the alphabet and the
     * pre-order relation as well as the attribute information.
     * @param example
     *            Example to add to the element
     */
    public void addExample(Example example) {
        super.addExample(example);
        for (Iterator<String> it = example.getContentIterator(); it.hasNext();) {
            String contentItem = it.next();
            if (!exampleSetsMap.containsKey(contentItem)) {
                exampleSetsMap.put(contentItem, new HashSet<Integer>());
            }
            exampleSetsMap.get(contentItem).add(count);
        }
    }
    
    /**
     * Method that computes the multiplicity of an equivalence class
     * @param eClass
     *            Set equivalence class to determine the multiplicity for
     * @return String multiplicity character
     */
    protected String computeMultiplicity(Set<String> eClass) {
        Set<Integer> exampleSet = new HashSet<Integer>();
        int totalOccurrences = 0;
        boolean moreThanOne = false;
        for (String childName : eClass) {
            Multiplicity m = super.contentMultiplicityMap.get(childName);
            totalOccurrences += exampleSetsMap.get(childName).size();
            exampleSet.addAll(exampleSetsMap.get(childName));
            moreThanOne = moreThanOne || m.getMax() > 1;
        }
        int totalExamples = exampleSet.size(); 
        if (totalExamples < super.count &&
                totalOccurrences == totalExamples && !moreThanOne) {
            return "?";
        } else if (totalExamples == super.count &&
                totalOccurrences == totalExamples && !moreThanOne) {
            return "";
        } else if (totalExamples < super.count &&
                (totalOccurrences > totalExamples || moreThanOne)) {
            return "*";
        } else if (totalExamples == super.count &&
                (totalOccurrences > totalExamples || moreThanOne)) {
            return "+";
        }
        return null;
    }

}
