/*
 * Created on Jun 29, 2005
 * Modified on $Date: 2009-10-29 13:35:07 $
 */
package eu.fox7.flt.treegrammar.data;

import java.util.Properties;

import org.apache.commons.lang.math.RandomUtils;

/**
 * @author eu.fox7
 * @version $Revision: 1.1 $
 * 
 */
public class IntegerGenerator implements DataGenerator {

    protected int min = -100;
    protected int max = 100;

    public IntegerGenerator() {
        super();
    }

    public IntegerGenerator(Properties properties) {
        this();
        min = Integer.valueOf(properties.getProperty("min", "0")).intValue();
        max = Integer.valueOf(properties.getProperty("max", "10")).intValue();
    }

    /**
     * @return Returns the max.
     */
    public int getMax() {
        return max;
    }

    /**
     * @param max The max to set.
     */
    public void setMax(int max) {
        this.max = max;
    }

    /**
     * @return Returns the min.
     */
    public int getMin() {
        return min;
    }

    /**
     * @param min The min to set.
     */
    public void setMin(int min) {
        this.min = min;
    }

    public String getData() {
        return (new Integer(min + RandomUtils.nextInt(max - min+ 1))).toString();
    }


}
