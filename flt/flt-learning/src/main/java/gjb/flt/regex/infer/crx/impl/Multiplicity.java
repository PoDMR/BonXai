/*
 * Created on May 31, 2005
 * Modified on $Date: 2009-10-27 09:30:13 $
 */
package gjb.flt.regex.infer.crx.impl;


/**
 * <p>Convenience class to store and update the multiplicity of child elements
 * in the content model.</p>
 * @author gjb
 * @version $Revision: 1.1 $
 */
public class Multiplicity {

    /**
     * minimum number of times an item occurs in the content model
     */
    protected int min = Integer.MAX_VALUE;
    /**
     * maximum number of times an item occurs in the content model
     */
    protected int max = Integer.MIN_VALUE;
    /**
     * counts the number of times an update is performed, to be compared with
     * the total number of examples, if less, min should be set to zero
     */
    protected int count = 0;

    public Multiplicity() {
        super();
    }

    public Multiplicity(int min, int max) {
        this();
        this.min = min;
        this.max = max;
    }

    /**
     * Accessor method for the minimum field
     * @return int minimum number
     */
    public int getMin() {
        return this.min;
    }

    /**
     * Accessor method for the maximum field
     * @return int maximum number
     */
    public int getMax() {
        return this.max;
    }

    /**
     * Method to update the minimum and maximum according to the specified
     * number of occurrences in the current example.
     * @param occurrence int number of times a content item occurs in a specific
     *        example
     */
    public void update(int occurrence) {
        count++;
        if (occurrence > this.max) {
            this.max = occurrence;
        }
        if (occurrence < this.min) {
            this.min = occurrence;
        }
    }

    /**
     * Method to update the minimum occurences, to be called when all examples
     * have been added
     * @param total number of examples for the content model
     */
    public void updateFinalMin(int total) {
        if (total > count)
            this.min = 0;
    }

    /**
     * Method for debugging purposes.
     */
    public String toString() {
        return "{" + getMin() + "," + getMax() + "}";
    }

}