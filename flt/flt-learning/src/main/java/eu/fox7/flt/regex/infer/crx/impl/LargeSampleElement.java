/*
 * Created on May 15, 2005
 * Modified on $Date: 2009-10-27 09:30:13 $
 */
package eu.fox7.flt.regex.infer.crx.impl;


import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * <p>LargeSampleElement extends StructureElement by adding support for deriving
 * the multiplicity of factors in the partial order.  It serves as a base class
 * for SmallSampleElement that derives more precise element definitions at the cost
 * of more runtime memory.</p>  
 * @author eu.fox7
 * @version $Revision: 1.1 $
 */
public class LargeSampleElement extends StructureElement
        implements ParsingElement {

    /**
     * maps a child name to its multiplicity, i.e. the minimum and
     * maximum number of times the child name occurs in the content of
     * the element
     */
    protected Map<String,Multiplicity> contentMultiplicityMap = new HashMap<String,Multiplicity>();

    /**
     * Method that adds an Example for the element, it updates the alphabet and the
     * pre-order relation as well as the attribute information.
     * @param example
     *            Example to add to the element
     */
    public void addExample(Example example) {
        super.addExample(example);
        Map<String,Integer> occurrenceMap = example.getOccurrenceMap();
        for (String symbol : alphabet) {
            int occurrence = 0;
            if (occurrenceMap.containsKey(symbol))
                occurrence = occurrenceMap.get(symbol).intValue();
            if (!contentMultiplicityMap.containsKey(symbol)) {
                Multiplicity m = new Multiplicity();
                contentMultiplicityMap.put(symbol, m);
                /* if this element occurs for the first time in example n > 1, then
                 * the multiplicity should be updated with 0 first to establish that
                 * it can occur 0 times.
                 */
                if (count > 1) m.update(0);
            }
            contentMultiplicityMap.get(symbol).update(occurrence);
        }
    }

    public void setMultiplicities(Map<String,Multiplicity> multiplicityMap) {
        for (String symbol : alphabet) {
            contentMultiplicityMap.put(symbol, multiplicityMap.get(symbol));
        }
    }
    
    /**
     * Method that computes the multiplicity of an equivalence class
     * @param eClass
     *            Set equivalence class to determine the multiplicity for
     * @return String multiplicity character
     */
    protected String computeMultiplicity(Set<String> eClass) {
        boolean isOptional = true;
        boolean moreThanOne = false;
        for (String childName : eClass) {
            Multiplicity m = contentMultiplicityMap.get(childName);
            if (m == null)
                System.err.println("no multiplicity for " + childName);
            isOptional = isOptional && m.getMin() == 0;
            moreThanOne = moreThanOne || m.getMax() > 1;
        }
        Set<Set<String>> equivalenceClasses = getRelation().getEquivalenceClasses();
        for (Set<String> preOrderClass : equivalenceClasses)
            if (eClass.size() > 1 && eClass.equals(preOrderClass))
                moreThanOne = true;
        if (isOptional && !moreThanOne) {
            return "?";
        } else if (!isOptional && !moreThanOne) {
            return "";
        } else if (isOptional && moreThanOne) {
            return "*";
        } else if (!isOptional && moreThanOne) {
            return "+";
        }
        return null;
    }

    /**
     * Method for debugging purposes
     */
    public String toString() {
        StringBuffer str = new StringBuffer(super.toString());
        str.append("\n\tmultiplicities:\n");
        for (String childName : contentMultiplicityMap.keySet()) {
            str.append("\t\t").append(childName);
            str.append(contentMultiplicityMap.get(childName)).append("\n");
        }
        return str.toString();
    }

    /**
     * Convenience method that updates counters in a Map
     * @param map Map of counters to update
     * @param key Key to update in the Map
     */
    protected static void increment(Map<String,Integer> map, String key) {
        map.put(key, map.get(key) + 1);
    }

}
