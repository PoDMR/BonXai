/*
 * Created on Jun 26, 2005
 * Modified on $Date: 2009-10-29 13:35:07 $
 */
package gjb.flt.treegrammar.data;

import java.util.Properties;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.math.RandomUtils;

/**
 * @author gjb
 * @version $Revision: 1.1 $
 * 
 */
public class StringGenerator implements DataGenerator {

    protected int minLength = 0;
    protected int maxLength = 10;

    public StringGenerator() {
        super();
    }

    public StringGenerator(Properties properties) {
        this();
        minLength = Integer.valueOf(properties.getProperty("min", "0")).intValue();
        maxLength = Integer.valueOf(properties.getProperty("max", "10")).intValue();
    }

    /**
     * @return Returns the maxLength.
     */
    public int getMaxLength() {
        return maxLength;
    }

    /**
     * @param maxLength The maxLength to set.
     */
    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    /**
     * @return Returns the minLength.
     */
    public int getMinLength() {
        return minLength;
    }

    /**
     * @param minLength The minLength to set.
     */
    public void setMinLength(int minLength) {
        this.minLength = minLength;
    }

    public String getData() {
        int length = minLength + RandomUtils.nextInt(maxLength - minLength + 1);
        return RandomStringUtils.randomAlphanumeric(length);
    }

}
