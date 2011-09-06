/*
 * Created on Jul 3, 2008
 * Modified on $Date: 2009-10-26 18:37:40 $
 */
package eu.fox7.util.cli;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * @author eu.fox7
 * @version $Revision: 1.1 $
 * 
 */
public class Parameters implements Iterable<Parameters.Parameter> {

    protected List<Parameter> parameters = new LinkedList<Parameter>();

    /**
     * method to add a command line parameter
     * @param flag String the command line flag
     * @param longFlag String the long version of the command line flag
     * @param hasValue boolean indicates whether the command line parameter
     *                 takes a value
     * @param isRequired boolean indicates whether the command line parameter
     *                   is required
     * @param comment String comment used to generate the help message
     */
    void add(String flag, String longFlag, boolean hasValue,
                    boolean isRequired, String comment) {
        parameters.add(new Parameter(flag, longFlag, hasValue, isRequired,
                                     comment));
    }

    public Iterator<Parameter> iterator() {
        return parameters.iterator();
    }

    class Parameter {

        protected String flag, longFlag, comment;
        protected boolean hasValue, isRequired;

        public Parameter(String flag, String longFlag, boolean hasValue,
                         boolean isRequired, String comment) {
            super();
            this.flag = flag;
            this.longFlag = longFlag;
            this.hasValue = hasValue;
            this.isRequired = isRequired;
            this.comment = comment;
        }

        public String flag() {
            return flag;
        }

        public String longFlag() {
            return longFlag;
        }

        public String comment() {
            return comment;
        }

        public boolean hasValue() {
            return hasValue;
        }

        public boolean isRequired() {
            return isRequired;
        }

    }

}
